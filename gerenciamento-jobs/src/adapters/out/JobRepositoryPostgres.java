package adapters.out;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.Job;
import ports.out.JobRepository;

public class JobRepositoryPostgres implements JobRepository {

    private final String url =
        "jdbc:postgresql://postgres:5432/gerenciamento_jobs";

    private  final String usuario = "airflow";

    private final String senha = "airflow";

    @Override
    public void salvar(Job job) {

        String sql = "INSERT INTO jobs_produto "
                   + "(produto, data_hora_execucao) "
                   + "VALUES (?, ?) RETURNING id";

        try {
            Class.forName("org.postgresql.Driver");

            Timestamp timestamp = Timestamp.valueOf(
                job.getDataHoraExecucao()
            );

            try (
                Connection conexao =
                    java.sql.DriverManager.getConnection(
                        url, usuario, senha
                    );
                PreparedStatement comando =
                    conexao.prepareStatement(sql)
            ) {
                comando.setString(1, job.getProduto());
                comando.setTimestamp(2, timestamp);

                try (ResultSet resultado = comando.executeQuery()) {
                    if (resultado.next()) {
                        job.setId(resultado.getInt("id"));
                    } else {
                        throw new RuntimeException(
                            "O PostgreSQL não retornou o ID do job."
                        );
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(
                "Erro ao salvar Job no PostgreSQL",
                e
            );
        }
    }

    @Override
    public List<Job> listarTodos() {

        java.util.List<Job> jobs = new ArrayList<>();

        String sql =
            "SELECT id, produto, status, data_hora_execucao " +
            "FROM jobs_produto " +
            "ORDER BY id DESC";

        try {
            Class.forName("org.postgresql.Driver");

            Connection conexao = java.sql.DriverManager.getConnection(
                url,
                usuario,
                senha
            );
            
            PreparedStatement comando = 
                conexao.prepareStatement(sql);

            ResultSet resultado = 
            comando.executeQuery();

            while (resultado.next()) {

                Job job = new Job();

                job.setId(
                    resultado.getInt("id")
                );

                job.setProduto(
                    resultado.getString("produto")
                );

                job.setStatus(
                    resultado.getString("status")
                );

                job.setDataHoraExecucao(
                    resultado
                        .getTimestamp("data_hora_execucao")
                        .toString()  
                );

                jobs.add(job);  
            }
            resultado.close();
            comando.close();
            conexao.close();

            return jobs;

        } catch (Exception e) {
            throw new RuntimeException(
                "Erro ao consultar Jobs no PostgreSQL",
                e
            );
        }
    }
}