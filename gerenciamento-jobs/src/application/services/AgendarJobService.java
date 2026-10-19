package application.services;

import model.Job;
import ports.in.AgendarJobUseCase;
import ports.out.JobRepository;
import ports.out.AgendamentoAirflowPorta;

public class AgendarJobService implements AgendarJobUseCase{

    private final JobRepository jobRepository;
    private final AgendamentoAirflowPorta agendamentoAirflow;

    public AgendarJobService(
        JobRepository jobRepository,
        AgendamentoAirflowPorta agendamentoAirflow) {
        this.jobRepository = jobRepository;
        this.agendamentoAirflow = agendamentoAirflow;
    }

    @Override
    public void agendar(Job job) {

        jobRepository.salvar(job);

        agendamentoAirflow.agendar(job);
    }
}