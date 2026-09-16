from airflow.sdk import DAG
from airflow.providers.standard.operators.python import PythonOperator
from airflow.providers.postgres.hooks.postgres import PostgresHook
from airflow.providers.standard.operators.trigger_dagrun import TriggerDagRunOperator

from datetime import datetime


def verificar_jobs():
    postgres = PostgresHook(postgres_conn_id="postgres_jobs")

    conexao = postgres.get_conn()
    cursor = conexao.cursor()

    cursor.execute("""
        SELECT id, produto, status, data_hora_execucao
        FROM jobs_produto
        WHERE status = 'AGENDADO'
          AND data_hora_execucao <= NOW()
        ORDER BY id;
    """)

    jobs = cursor.fetchall()

    for job in jobs:
        print(job)

    cursor.close()
    conexao.close()

    return [
        {
            "job_id": job[0],
            "produto": job[1]
        }
        for job in jobs
    ]


with DAG(
    dag_id="verificar_jobs",
    start_date=datetime(2026, 1, 1),
    schedule=None,
    catchup=False
) as dag:

    tarefa_verificar = PythonOperator(
        task_id="verificar_jobs_postgres",
        python_callable=verificar_jobs
    )

    disparar_job = TriggerDagRunOperator.partial(
        task_id="disparar_job",
        trigger_dag_id="executar_job_produto",
    ).expand(
        conf=tarefa_verificar.output
    )

    tarefa_verificar >> disparar_job