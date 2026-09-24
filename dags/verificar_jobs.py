from datetime import datetime

from airflow.sdk import DAG, task
from airflow.providers.postgres.hooks.postgres import PostgresHook
from airflow.providers.standard.operators.trigger_dagrun import (
    TriggerDagRunOperator
)

@task
def buscar_jobs():

    postgres = PostgresHook(
        postgres_conn_id="postgres_jobs"
    )

    conexao = postgres.get_conn()
    cursor = conexao.cursor()

    try:

        cursor.execute(
            """
            SELECT
            id,
            produto
        FROM public.jobs_produto
        WHERE status = 'AGENDADO'
          AND data_hora_execucao <= NOW()
        ORDER BY data_hora_execucao, id;
        """
        )

        jobs = cursor.fetchall()

        resultado = []

        for job_id, produto in jobs:

            resultado.append(
            {
                "conf": {
                    "job_id": job_id,
                    "produto": produto
                }
            }
        )

        print("==============================")
        print("JOBS ENCONTRADOS")
        print("==============================")
        print("Quantidade:", len(resultado))

        for item in resultado:

            job = item["conf"]

            print(
            f"Job ID: {job['job_id']} | "
            f"Produto: {job['produto']}"
            )

        return resultado

    finally: 

        cursor.close()
        conexao.close()

with DAG(
    dag_id="verificar_jobs",
    start_date=datetime(2026, 1, 1),
    schedule=None,
    catchup=False,
) as dag:

    jobs = buscar_jobs()

    disparar_jobs = TriggerDagRunOperator.partial(
        task_id="disparar_job",
        trigger_dag_id="executar_job_produto",
        wait_for_completion=False,
    ).expand_kwargs(jobs)

    jobs >> disparar_jobs