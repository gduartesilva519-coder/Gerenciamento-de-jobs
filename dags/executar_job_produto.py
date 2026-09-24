from datetime import datetime

from airflow.sdk import DAG
from airflow.providers.postgres.hooks.postgres import PostgresHook
from airflow.providers.standard.operators.python import PythonOperator


def executar_job(**context):

    conf = context["dag_run"].conf or {}

    job_id = conf.get("job_id")
    produto = conf.get("produto")

    print("=============================")
    print("DADOS RECEBIDOS")
    print("=============================")
    print("Job ID:", job_id)
    print("Produto:", produto)

    if not job_id:
        raise ValueError(
            "job_id não foi recebido pelo executar_job_produto."
        )

    postgres = PostgresHook(
        postgres_conn_id="postgres_jobs"
    )

    conexao = postgres.get_conn()
    cursor = conexao.cursor()

    try:


        cursor.execute(
            """
            UPDATE public.jobs_produto
            SET status = 'PROCESSANDO'
            WHERE id = %s
              AND status = 'AGENDADO'
            RETURNING id, produto;
            """,
            (job_id,)
        )

        job = cursor.fetchone()

        if not job:
            conexao.rollback()

            print(
                f"job {job_id} não está mais AGENDADO."
            )

            return

        conexao.commit()

        print("============================")
        print("JOB EM PROCESSAMENTO")
        print("============================")
        print("Job ID:", job[0])
        print("Produto:", job[1])

        cursor.execute(
            """
            UPDATE public.jobs_produto
            SET status = 'CONCLUIDO'
            WHERE id = %s
              AND status = 'PROCESSANDO';
            """,
            (job_id,)
        )

        if cursor.rowcount != 1:
            raise RuntimeError(
                f"Não foi possível concluir o job {job_id}."
            )
        
        conexao.commit()

        print("===========================")
        print("JOB CONCLUIDO")
        print("===========================")
        print("Job ID:", job_id)
        print("Produto:", produto)

    except Exception:

        conexao.rollback()

        print("===========================")
        print("ERRO NA EXECUÇÃO")
        print("===========================")
        print("Job ID:", job_id)
        print("Produto:", produto)

        raise

    finally:

        cursor.close()
        conexao.close()


with DAG(
    dag_id="executar_job_produto",
    start_date=datetime(2026, 1, 1),
    schedule= None,
    catchup=False,
) as dag:

    executar = PythonOperator(
        task_id="executar_job",
        python_callable=executar_job,
    )