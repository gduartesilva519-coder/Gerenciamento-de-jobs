package adapters.in;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Job;

import application.services.AgendarJobService;
import ports.in.AgendarJobUseCase;
import ports.out.JobRepository;
import adapters.out.JobRepositoryPostgres;

public class AgendarJobServlet extends HttpServlet {

    @Override 
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String produto = request.getParameter("produto");
        String data = request.getParameter("data");
        String horario = request.getParameter("horario");

        String dataHora = data + " " + horario + ":00";

        Job job = new Job(
            produto,
            "AGENDADO",
            dataHora
        );

        try {

            JobRepository repository = new JobRepositoryPostgres();

            AgendarJobUseCase useCase =
                new AgendarJobService(repository);

            useCase.agendar(job);
            
            request.setAttribute(
                "produto",
                job.getProduto()
            );

            request.setAttribute(
                "dataHoraExecucao",
                job.getDataHoraExecucao()
            );

            request.getRequestDispatcher("resultado.jsp")
                   .forward(request, response);

        } catch (Exception e) {

            request.setAttribute(
                "erro",
                e.getMessage()
            );

            request.getRequestDispatcher("resultado.jsp")
                   .forward(request, response);
        }
    }
}