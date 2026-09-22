from airflow.sdk import DAG
from airflow.providers.standard.operators.python import PythonOperator
from airflow.providers.postgres.hooks.postgres import PostgresHook

from datetime import datetime


def executar_job(**context):

    conf = context["dag_run"].conf or {}

    print("==========================")
    print("DADOS RECEBIDOS:")
    print("==========================")

    print("CONF:", conf)

    job_id = conf.get("job_id")
    produto = conf.get("produto")

    print("Job ID:", job_id)
    print("Produto:", produto)

    if not job_id:
        print("Nenhum Job recebido.")
        return

    postgres = PostgresHook(postgres_conn_id="postgres_jobs")

    conexao = postgres.get_conn()
    cursor = conexao.cursor()

    cursor.execute("""
        UPDATE public.jobs_produto
        SET status = 'CONCLUIDO'
        WHERE id = %s;
    """, (job_id,))

    conexao.commit()

    print("=======================")
    print("JOB CONCLUIDO:")
    print("=======================")
    print("Job_ID:", job_id)
    print("Produto:", produto)

    cursor.close()
    conexao.close()

with DAG(
    dag_id="executar_job_produto",
    start_date=datetime(2026, 1, 1),
    schedule=None,
    catchup=False
) as dag:

    tarefa_executar = PythonOperator(
        task_id="executar_job",
        python_callable=executar_job
    )