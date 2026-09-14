from airflow.sdk import DAG
from airflow.providers.standard.operators.bash import BashOperator
from datetime import datetime


dag = DAG(
    dag_id="meu_segundo_dag",
    start_date=datetime(2026, 1, 1),
    schedule=None,
    catchup=False,
)


task_bash = BashOperator(
    task_id="task_bash",
    bash_command='echo "Olá, Airflow!"',
    dag=dag,
)