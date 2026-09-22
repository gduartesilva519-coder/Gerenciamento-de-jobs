from airflow.sdk import DAG
from airflow.providers.standard.operators.python import PythonOperator, BranchPythonOperator
from airflow.providers.standard.operators.trigger_dagrun import TriggerDagRunOperator
from airflow.providers.postgres.hooks.postgres import PostgresHook

from datetime import datetime


def verificar_jobs():

    
    postgres = PostgresHook(postgres_conn_id="postgres_jobs")

    conexao = postgres.get_conn()
    cursor =  conexao.cursor()

    cursor.execute("""
        SELECT id, status, produto, data_hora_execucao
        FROM public.jobs_produto
        WHERE status = 'AGENDADO'
            AND data_hora_execucao <= NOW()
        ORDER BY id
        LIMIT 1;
    """)

    jobs = cursor.fetchall()

    print("=========================")
    print("JOBS ENCONTRADOS:")
    print("=========================")

    for job in jobs:
        print(job)

    if jobs:
        job_id = jobs[0][0]

        cursor.execute("""
            UPDATE public.jobs_produto
            SET status = 'PROCESSANDO'
            WHERE id = %s;
        """, (job_id,))

        conexao.commit()

        print("=========================")
        print("JOB MARCADO COMO PROCESSANDO:")
        print("=========================")
        print("Job ID:", job_id)

    cursor.close()
    conexao.close()

    dados_jobs = []

    for job in jobs:
        dados_jobs.append({
            "job_id": job [0],
            "produto": job [2]
        })

    print("=========================")
    print("DADOS PARA EXECUÇÃO:")
    print("=========================")
    print(dados_jobs)

    return dados_jobs[0] if dados_jobs else {}

def decidir_proximo_passo(**context):

    dados = context["ti"].xcom_pull(
        task_ids="verificar_jobs_postgres"
    )

    if dados:
        return "disparar_job"

    return "fim"

with DAG(
    dag_id="verificar_jobs",
    start_date=datetime(2026, 1, 1),
    schedule="*/1 * * * *",
    catchup=False
) as dag:

    tarefa_verificar = PythonOperator(
        task_id="verificar_jobs_postgres",
        python_callable=verificar_jobs
    )

    decidir = BranchPythonOperator(
        task_id="decidir",
        python_callable=decidir_proximo_passo
    )

    disparar_job = TriggerDagRunOperator(
        task_id="disparar_job",
        trigger_dag_id="executar_job_produto",
        conf=tarefa_verificar.output
    )

    fim = PythonOperator(
        task_id="fim",
        python_callable=lambda: print("Nenhum Job para executar.")
    )

    tarefa_verificar >> decidir

    decidir >> disparar_job
    decidir >> fim