package adapters.out;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

import model.Job;
import ports.out.JobRepository;

public class JobRepositoryPostgres implements JobRepository {

    private final String url =
        "jdbc:postgresql://postgres:5432/gerenciamento_jobs";

    private  final String usuario = "airflow";

    private final String senha = "airflow";

    @Override
    public void salvar(Job job) {

        String sql = "INSERT INTO jobs_produto"
                   + "(produto, data_hora_execucao) "
                   + "VALUES (?, ?)";
        
        try {
            Class.forName("org.postgresql.Driver");

            Connection conexao = java.sql.DriverManager.getConnection(
                url,
                usuario,
                senha
            );

            Timestamp timestamp = Timestamp.valueOf(
                job.getDataHoraExecucao()
            );

            PreparedStatement comando =
                conexao.prepareStatement(sql);

            comando.setString(1, job.getProduto());

            comando.setTimestamp(2, timestamp);

            comando.executeUpdate();

            comando.close();
            conexao.close();

        } catch (Exception e) {
            throw new RuntimeException(
                "Erro ao salvar Job no PostgreSQL",
                e
            );
        }
    }
}