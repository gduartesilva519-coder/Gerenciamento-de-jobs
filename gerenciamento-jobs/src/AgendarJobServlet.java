import java.io.IOException;
import dao.JobDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Job;

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

            JobDAO dao = new JobDAO();
            
            dao.salvar(job);
            
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