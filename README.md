1. Objetivo do projeto:
Um sistema quede produto faz agendamento de Jobs de produtos, criando uma tela onde registra os Jobs para cadastrar um produto, nessa tela precisa ter um tempo em que o Job vai ser acionado. Usar o PostgreSQL no Docker para armazenar informações, através do PostgreSQL, o Airflow precisa acessar e reconhecer esse Job e disparar uma DAG.


3. Funcionamento do sistema:
O Docker faz os containers se interagirem, usando um sistema de redes virtuais, cada containers sendo isolados do exterior e entre cada um por padrão. O Visual Studio Code, é a plataforma que possui a programação dos códigos de cada container, o Tomcat sendo utilizado a linguagem Java para criar o agendamento dos Jobs e o Airflow usa a linguagem Python para a criação de DAGs e o registro dos resultados com os dados, cada um tendo pastas diferentes para dividir as responsabilidades entre si e organizando a estrutura do sistema.


4. Fluxo de execução:
O Tomcat está no localhost:9090, um servidor web onde fica o gerenciamento-jobs.war do projeto, agendado os Jobs colocando o produto, horário e a data que foram registrados, esses Jobs serão armazenados no banco de dados PostgreSQL tendo o server que depois de criar a tabela jobs_produto terá as colunas id, status, produto e data_hora_execucao, apertando o query tool, faz o comando do SELECT dos dados, o FROM de onde está os dados armazenados no public.jobs_produto e ORDER BY id que ordena os dados que foram guardados, assim executando o script e vendo a tabela dos Jobs que foram agendados. Após isso o Airflow terá duas DAGs, o verificar_jobs e executar_job_produto, quando acionar o verificar_jobs será mostrado o resultado se foi bem sucedido ou falhado, se for bem sucedido o executar_job_produto será acionado automaticamente, observando no logs os dados que foram coletados, assim sendo registrado no PostgreSQL que foi concluído, já se tiver falhado, o Job não será concluído. Para a interação do Tomcat até o PostgreSQL terá adaptadores e portas com o núcleo, o AgendarJobService.java, os adaptadores implementam a comunicação, assim as portas fazem interação com o mundo exterior, no caso, o Tomcat e o Postgres.


5. Arquiteturas:

MVC = Tomcat > gerenciamento_jobs > MVC: Model, Controller, View > DAO > PostgreSQL > Airflow

Hexagonal = Tomcat > Adaptador de entrada > Porta de entrada > Núcleo > Porta de saída > Adaptador de saída > Postgres > Airflow > Postgres


5. Conhecimento adquiridos:
