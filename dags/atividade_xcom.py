from airflow.sdk import DAG
from airflow.providers.standard.operators.python import PythonOperator
from airflow.providers.standard.operators.bash import BashOperator

from datetime import datetime

def gerar_numero():
    return 10

def calcular(ti):
    numero = ti.xcom_pull(task_ids="gerar_numero")
    resultado = numero * 2
    return resultado

def verificar(ti):
    resultado = ti.xcom_pull(task_ids="calcular")

    if resultado == 20:
        print("O resultado está correto!")
    else:
        print("O resultado está incorreto!")

with DAG(
    dag_id="atividade_com",
    start_date=datetime(2026,9,8),
    schedule=None,
    catchup=False,
)as dag:

    task_gerar_numero = PythonOperator(
        task_id="gerar_numero",
        python_callable=gerar_numero
    )

    task_calcular = PythonOperator(
        task_id="calcular",
        python_callable=calcular
    )

    task_verificar = PythonOperator(
        task_id="verificar",
        python_callable=verificar
    )

    task_bash_final = BashOperator(
        task_id="bash_final",
        bash_command="echo 'Atividade concluída com sucesso!'"
    )

    task_gerar_numero >> task_calcular
    task_calcular >> task_verificar
    task_verificar >> task_bash_final