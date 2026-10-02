package application.services;

import model.Job;
import ports.in.AgendarJobUseCase;
import ports.out.JobRepository;

public class AgendarJobService implements AgendarJobUseCase{

    private final JobRepository jobRepository;

    public AgendarJobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public void agendar(Job job) {
        jobRepository.salvar(job);
    }
}