package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

import model.Job;

public class JobDAO {

    private final String url =
    "jdbc:postgresql://postgres:5432/gerenciamento_jobs";

    private final String usuario = "airflow";

    private final String senha = "airflow";


    public void salvar(Job job) throws Exception {

        String sql = "INSERT INTO jobs_produto"
                   + "(produto, data_hora_execucao) "
                   + "VALUES (?, ?)";

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
    }
}