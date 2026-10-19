package ports.out;

import java.util.List;

import model.Job;

public interface JobRepository {

    void salvar(Job job);

    List<Job> listarTodos();

}