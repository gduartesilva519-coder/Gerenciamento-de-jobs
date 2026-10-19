from datetime import datetime
import os
import smtplib
from email.message import EmailMessage

from airflow.sdk import DAG
from airflow.providers.postgres.hooks.postgres import PostgresHook
from airflow.providers.standard.operators.python import PythonOperator


def enviar_email(assunto, corpo):
    remetente = os.getenv("SMTP_USER")
    senha = os.getenv("SMTP_PASSWORD")
    destinatario = os.getenv("SMTP_DESTINATARIO")

    if not remetente or not senha or not destinatario:
        raise ValueError(
            "Configure SMTP_USER, SMTP_PASSWORD e SMTP_DESTINATARIO."
        )

    mensagem = EmailMessage()
    mensagem["Subject"] = assunto
    mensagem["From"] = remetente
    mensagem["To"] = destinatario
    mensagem.set_content(corpo)

    with smtplib.SMTP(
        os.getenv("SMTP_HOST", "smtp.gmail.com"),
        int(os.getenv("SMTP_PORT", "587"))
    ) as servidor:
        servidor.starttls()
        servidor.login(remetente, senha)
        servidor.send_message(mensagem)


def executar_job(**context):

    conf = context["dag_run"].conf or {}

    job_id = conf.get("job_id")
    produto = conf.get("produto")

    print("=============================")
    print("DADOS RECEBIDOS")
    print("=============================")
    print("Job ID:", job_id)
    print("Produto:", produto)

    conexao = None
    cursor = None

    try:

        if not job_id:
            raise ValueError(
                "job_id não foi recebido pelo executar_job_produto."
            )

        postgres = PostgresHook(
                postgres_conn_id="postgres_jobs"
            )
        
        conexao = postgres.get_conn()
        cursor = conexao.cursor()

        cursor.execute(
            """
            UPDATE public.jobs_produto
            SET status = 'PROCESSANDO'
            WHERE id = %s
              AND status = 'DISPARADO'
            RETURNING id, produto;
            """,
            (job_id,)
        )

        job = cursor.fetchone()

        if not job:
            raise ValueError(
                f"O job {job_id} não existe ou não está mais AGENDADO."
            )

        conexao.commit()

        print("============================")
        print("JOB EM PROCESSAMENTO")
        print("============================")
        print("Job ID:", job[0])
        print("Produto:", job[1])

        cursor.execute(
            """
            UPDATE public.jobs_produto
            SET status = 'CONCLUIDO',
                concluido_em = NOW()
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

        try:
            enviar_email(
                f"Job {job_id} concluído",
                f"O job {job_id} do produto {produto} foi marcado como CONCLUIDO no banco de dados."
            )
            print("E-mail de conclusão enviado.")

        except Exception as erro_email:
            print("Falha ao enviar e-mail de conclusão:", erro_email)

    except Exception as erro:
        if conexao is not None:
            conexao.rollback()

        print("===========================")
        print("ERRO NA EXECUÇÃO")
        print("===========================")
        print("Job ID:", job_id)
        print("Produto:", produto)
        print("Erro:", erro)

        try:
            enviar_email(
                f"Falha no Job {job_id}",
                f"O job {job_id} do produto {produto} falhou durante a execução. \n\nErro: {erro}"
            )
            print("E-mail de falha enviado.")

        except Exception as erro_email:
            print("Falha ao enviar e-mail de erro:", erro_email)

        raise

    finally:

        if cursor is not None:
            cursor.close()

        if conexao is not None:
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
        pool="jobs_sequenciais"
    )