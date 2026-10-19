import pendulum

from airflow.sdk import DAG, task
from airflow.providers.postgres.hooks.postgres import PostgresHook
from airflow.providers.standard.operators.trigger_dagrun import (
    TriggerDagRunOperator,
)


@task
def buscar_e_reservar_jobs():
    postgres = PostgresHook(postgres_conn_id="postgres_jobs")
    conexao = postgres.get_conn()
    cursor = conexao.cursor()

    try:
        cursor.execute(
            """
            WITH jobs_prontos AS (
                SELECT id
                FROM public.jobs_produto
                WHERE status = 'AGENDADO'
                  AND data_hora_execucao <=
                      (CURRENT_TIMESTAMP AT TIME ZONE 'America/Sao_Paulo')
                ORDER BY data_hora_execucao, id
                FOR UPDATE SKIP LOCKED
        )
        UPDATE public.jobs_produto AS j
        SET status = 'DISPARADO'
        FROM jobs_prontos AS p
        WHERE j.id = p.id
          AND j.status = 'AGENDADO'
        RETURNING j.id, j.produto;
        """
        )

        jobs = cursor.fetchall()
        conexao.commit()

        resultado = [
            {
                "conf": {
                    "job_id": job_id,
                    "produto": produto,
                },
                "trigger_run_id": f"execucao_job_{job_id}",
            }
            for job_id, produto in jobs
        ]

        print(f"Jobs reservados para execução: {len(resultado)}")
        for item in resultado:
            print(item["conf"])

        return resultado

    except Exception:
        conexao.rollback()
        raise

    finally:
        cursor.close()
        conexao.close()

with DAG(
    dag_id="agendar_job_individual",
    start_date=pendulum.datetime(
        2026, 1, 1, tz="America/Sao_Paulo"
    ),
    schedule="* * * * *",
    catchup=False,
    max_active_runs=1,
    tags=["jobs", "agendamento"],
) as dag:

    jobs = buscar_e_reservar_jobs()

    disparar_execucoes = TriggerDagRunOperator.partial(
        task_id="disparar_execucao",
        trigger_dag_id="executar_job_produto",
        wait_for_completion=False,
    ).expand_kwargs(jobs)

    jobs >> disparar_execucoes