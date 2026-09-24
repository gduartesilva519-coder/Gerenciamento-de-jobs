import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import org.postgresql.Driver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AgendarJobServlet extends HttpServlet {

    @Override 
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String produto = request.getParameter("produto");
        String data = request.getParameter("data");
        String horario = request.getParameter("horario");

        String url = "jdbc:postgresql://postgres:5432/gerenciamento_jobs";
        String usuario = "airflow";
        String senha = "airflow";

        String sql = "INSERT INTO jobs_produto "
                    + "(produto, data_hora_execucao) "
                    + "VALUES (?,?)";

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        try {

            Class.forName("org.postgresql.Driver");

            Connection conexao = java.sql.DriverManager.getConnection(
                url, 
                usuario,
                senha
            );

            String dataHora = data + " " + horario + ":00";

            Timestamp timestamp = Timestamp.valueOf(dataHora);

            PreparedStatement comando = conexao.prepareStatement(sql);

            comando.setString(1, produto);
            comando.setTimestamp(2, timestamp);

            comando.executeUpdate();

            comando.close();
            conexao.close();
        
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Job Agendado</title>");
            out.println("</head>");
            out.println("<body>");

            out.println("<h1>Job agendado com sucesso!</h1>");

            out.println("<p>Produto: " + produto + "</p>");
            out.println("<p> " + data + "</p>");
            out.println("<p> " + horario + "</p>");

            out.println("<br>");
            out.println("<a href=\"http://localhost:9090/manager/html\">");
            out.println("<button>Voltar para o Manager</button>");
            out.println("</a>");

            out.println("</body>");
            out.println("</html>");

        } catch (Exception e) {

            out.println("<html>");
            out.println("<body>");

            out.println("<h1>Erro ao agendar o Job</h1>");

            out.println("<p>" + e.getMessage() + "</p>");

            out.println("</body>");
            out.println("</html>");
        }
    
    }
}