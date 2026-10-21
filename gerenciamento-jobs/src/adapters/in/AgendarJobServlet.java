package adapters.in;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Job;

import application.services.AgendarJobService;
import ports.in.AgendarJobUseCase;
import ports.out.AgendamentoAirflowPorta;
import ports.out.JobRepository;
import adapters.out.JobRepositoryPostgres;
import adapters.out.AgendamentoAirflowAdapter;

public class AgendarJobServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String produto = request.getParameter("produto");
            String data = request.getParameter("data");
            String horario = request.getParameter("horario");

            if (produto == null || data == null || horario == null) {
                throw new IllegalArgumentException(
                    "Campos ausentes. produto=" + produto
                    + ", data=" + data + ", horario=" + horario);
            }

            // Aceita HH:mm ou HH:mm:ss
            if (horario.length() == 5) {
                horario = horario + ":00";
            }

            String dataHora = data + " " + horario;

            Job job = new Job(produto, "AGENDADO", dataHora);

            JobRepository repository = new JobRepositoryPostgres();

            AgendamentoAirflowPorta agendamentoAirflow =
                new AgendamentoAirflowAdapter(
                    "http://airflow-apiserver:8080",
                    "airflow",
                    "airflow"
                );

            AgendarJobUseCase useCase =
                new AgendarJobService(repository, agendamentoAirflow);

            useCase.agendar(job);

            request.setAttribute("produto", job.getProduto());
            request.setAttribute("dataHoraExecucao", job.getDataHoraExecucao());

        } catch (Exception e) {
            e.printStackTrace(); // vai para o log do Tomcat

            Throwable causa = e;
            while (causa.getCause() != null) {
                causa = causa.getCause();
            }

            request.setAttribute("erro", e.getMessage() + " | Causa: " + causa);
        }

        request.getRequestDispatcher("resultado.jsp").forward(request, response);
    }
}