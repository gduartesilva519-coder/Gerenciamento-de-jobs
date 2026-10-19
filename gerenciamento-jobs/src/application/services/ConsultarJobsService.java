package application.services;

import java.util.List;

import model.Job;
import ports.in.ConsultarJobsUseCase;
import ports.out.JobRepository;

public class ConsultarJobsService implements ConsultarJobsUseCase {

    private final JobRepository jobRepository;

    public ConsultarJobsService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public List<Job> consultar() {
        return jobRepository.listarTodos();
    }
}