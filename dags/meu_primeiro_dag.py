from airflow.sdk import DAG
from airflow.providers.standard.operators.python import PythonOperator
from datetime import datetime


def tarefa_1():
    print("Essa é a tarefa 1")

def tarefa_2():
    print("Essa é a tarefa 2")

def tarefa_3():
    print("Essa é a tarefa 3")

dag = DAG(
    dag_id="meu_primeiro_dag",
    start_date=datetime(2026, 1, 1),
    schedule=None,
    catchup=False,
)

task_1 = PythonOperator(
    task_id="task_1",
    python_callable=tarefa_1,
    dag=dag,
)

task_2 = PythonOperator(
    task_id="task_2",
    python_callable=tarefa_2,
    dag=dag,
)

task_3 = PythonOperator(
    task_id="task_3",
    python_callable=tarefa_3,
    dag=dag,
)

task_1 >> task_2 >> task_3