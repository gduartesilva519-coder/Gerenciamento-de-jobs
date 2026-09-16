from airflow.sdk import DAG
from airflow.providers.standard.operators.python import PythonOperator

from datetime import datetime


def executar_job(**context):
    conf = context["dag_run"].conf

    job_id = conf["job_id"]
    produto = conf["produto"]

    print("Executando Job no produto...")
    print("Job ID:", job_id)
    print("Produto:", produto)
    print("CONF RECEBIDO:", conf)

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

