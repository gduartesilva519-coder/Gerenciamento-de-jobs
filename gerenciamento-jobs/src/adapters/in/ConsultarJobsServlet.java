package adapters.in;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Job;

import application.services.ConsultarJobsService;
import ports.in.ConsultarJobsUseCase;
import ports.out.JobRepository;
import adapters.out.JobRepositoryPostgres;

public class ConsultarJobsServlet extends HttpServlet{

    @Override 
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            JobRepository repository = 
                new JobRepositoryPostgres();

            ConsultarJobsUseCase useCase =
                new ConsultarJobsService(repository);
            
            List<Job> jobs =
                useCase.consultar();

            request.setAttribute(
                "jobs",
                jobs
            );

            request.getRequestDispatcher("jobs.jsp")
                   .forward(request, response);

        } catch (Exception e) {

            request.setAttribute(
                "erro",
                e.getMessage()
            );

            request.getRequestDispatcher("jobs.jsp")
                   .forward(request, response);
        }
    }    
}