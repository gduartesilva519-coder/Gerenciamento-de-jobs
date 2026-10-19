package ports.in;

import java.util.List;
import model.Job;

public interface ConsultarJobsUseCase {

    List<Job> consultar();
}