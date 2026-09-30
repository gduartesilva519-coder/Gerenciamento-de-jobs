package model;

public class Job {

    private int id;
    private String produto;
    private String status; 
    private String data_hora_execucao;

    public Job() {
    }

    public Job(String produto, String status, String dataHoraExecucao) {
        this.produto = produto;
        this.status = status;
        this.data_hora_execucao = dataHoraExecucao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDataHoraExecucao() {
        return data_hora_execucao;
    }

    public void setDataHoraExecucao(String dataHoraExecucao) {
        this.data_hora_execucao = dataHoraExecucao;
    }
}