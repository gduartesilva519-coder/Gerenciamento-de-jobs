import pendulum

from airflow.sdk import DAG
from airflow.providers.postgres.hooks.postgres import PostgresHook
from airflow.providers.standard.operators.python import PythonOperator

def limpar_jobs_antigos():

    postgres = PostgresHook(
        postgres_conn_id="postgres_jobs"
    )

    conexao = postgres.get_conn()
    cursor = conexao.cursor()

    try:
        cursor.execute(
            """
            DELETE FROM public.jobs_produto
            WHERE (
                status = 'CONCLUIDO'
                AND concluido_em IS NOT NULL
                AND concluido_em <= NOW() - INTERVAL '24 hours'
            )
            OR (
                status = 'AGENDADO'
                AND data_hora_execucao <= NOW() - INTERVAL '24 hours'
            )
            RETURNING id, produto, status;
            """
        )

        jobs_removidos = cursor.fetchall()
        conexao.commit()

        print("============================")
        print("LIMPEZA DE JOBS ANTIGOS")
        print("============================")
        print("Quantidade removida:", len(jobs_removidos))

        for job_id, produto, status in jobs_removidos:
            print(
                f"Job ID: {job_id} | "
                f"Produto: {produto} | "
                f"Status: {status}"
            )

    except Exception:
        conexao.rollback()
        raise

    finally:
        cursor.close()
        conexao.close()

with DAG(
    dag_id="limpeza_jobs_antigos",
    start_date=pendulum.datetime(
        2026, 1, 1,
        tz="America/Sao_Paulo"
    ),
    schedule="0 0 * * *",
    catchup=False
) as dag:

    limpar = PythonOperator(
        task_id="limpar_jobs_concluidos",
        python_callable=limpar_jobs_antigos,
    )