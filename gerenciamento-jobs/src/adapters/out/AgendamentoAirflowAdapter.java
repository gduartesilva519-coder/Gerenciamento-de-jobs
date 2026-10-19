package adapters.out;

import model.Job;
import ports.out.AgendamentoAirflowPorta;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class AgendamentoAirflowAdapter implements AgendamentoAirflowPorta{

    private final String airflowUrl;
    private final String usuario;
    private final String senha;

    public AgendamentoAirflowAdapter(
            String airflowUrl,
            String usuario,
            String senha) {

        this.airflowUrl = airflowUrl;
        this.usuario = usuario;
        this.senha = senha;
    }

    @Override
    public void agendar(Job job) {
        String token = obterToken();

        String json = "{"
                + "\"dag_run_id\":\"job_" + job.getId() + "\","
                + "\"logical_date\":\"" + Instant.now().toString() + "\","
                + "\"conf\":{"
                + "\"job_id\":" + job.getId() + ","
                + "\"produto\":\"" + escaparJson(job.getProduto()) + "\","
                + "\"data_hora_execucao\":\""
                + escaparJson(job.getDataHoraExecucao()) + "\""
                + "}"
                + "}";

        HttpURLConnection conexao = null;

        try {
            URL url = new  URL(
                    airflowUrl
                    + "/api/v2/dags/agendar_job_individual/dagRuns"
            );

            conexao = (HttpURLConnection) url.openConnection();
            conexao.setRequestMethod("POST");
            conexao.setConnectTimeout(10000);
            conexao.setReadTimeout(10000);
            conexao.setRequestProperty(
                "Authorization", "Bearer " + token
            );
            conexao.setRequestProperty(
                "Content-Type", "application/json"
            );
            conexao.setDoOutput(true);

            byte[] dados = json.getBytes(StandardCharsets.UTF_8);

            try (OutputStream saida = conexao.getOutputStream()) {
                saida.write(dados);
            }

            int status = conexao.getResponseCode();

            if (status < 200 || status >= 300) {
                InputStream erro = conexao.getErrorStream();
                String resposta = erro == null ? "" : lerResposta(erro);

                throw new RuntimeException(
                        "Erro ao agendar no Airflow. HTTP "
                        + status + ": " + resposta
                );
            }

        } catch (Exception erro) {
            throw new  RuntimeException(
                    "Falha ao enviar agendamento ao Airflow.",
                    erro
            );
        } finally {
            if (conexao != null) {
                conexao.disconnect();
            }
        }
    }

    private String obterToken() {
        HttpURLConnection conexao = null;

        try {
            URL url = new URL(airflowUrl + "/auth/token");
            conexao = (HttpURLConnection) url.openConnection();
            conexao.setRequestMethod("POST");
            conexao.setConnectTimeout(10000);
            conexao.setReadTimeout(10000);
            conexao.setRequestProperty(
                "Content-Type", "application/json"
            );
            conexao.setDoOutput(true);

            String credenciais = "{"
                    + "\"username\":\"" + escaparJson(usuario) + "\","
                    + "\"password\":\"" + escaparJson(senha) + "\""
                    + "}";

            try (OutputStream saida = conexao.getOutputStream()) {
                saida.write(
                    credenciais.getBytes(StandardCharsets.UTF_8)
                );
            }

            int status = conexao.getResponseCode();
            InputStream resposta = status >= 200 && status < 300
                    ? conexao.getInputStream()
                    : conexao.getErrorStream();

            String corpo = resposta == null ? "" : lerResposta(resposta);

            if (status < 200 || status >= 300) {
                throw new RuntimeException(
                        "Falha na autenticação do Airflow. HTTP "
                        + status + ": " + corpo
                );
            }

            String marcador = "\"access_token\":\"";
            int inicio = corpo.indexOf(marcador);

            if (inicio < 0) {
                throw new RuntimeException(
                    "A resposta de autenticação não contém access_token."
                );
            }

            inicio += marcador.length();
            int fim = corpo.indexOf("\"", inicio);

            if (fim < 0) {
                throw new RuntimeException(
                        "Não foi possível interpretar o token do Airflow."
                );
            }

            return  corpo.substring(inicio, fim);

        } catch (Exception erro) {
            throw new RuntimeException(
                    "Não foi possível autenticar na API do Airflow.",
                    erro
            );
        } finally {
            if (conexao != null) {
                conexao.disconnect();
            }
        }
    }

    private String lerResposta(InputStream entrada) throws Exception {
        try (InputStream stream = entrada;
            ByteArrayOutputStream saida =
                    new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int quantidade;

            while ((quantidade = stream.read(buffer)) != -1) {
                saida.write(buffer, 0, quantidade);
            }

            return new String(
                    saida.toByteArray(),
                    StandardCharsets.UTF_8
            );
        }
    }

    private String escaparJson(String valor) {
        if (valor == null) {
            return "";
        }

        return valor
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}