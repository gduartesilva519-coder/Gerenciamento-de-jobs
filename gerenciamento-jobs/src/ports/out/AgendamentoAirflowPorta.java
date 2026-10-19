package ports.out;

import model.Job;

public interface AgendamentoAirflowPorta {

    void agendar(Job job);
}