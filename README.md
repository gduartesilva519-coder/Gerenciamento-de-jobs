1. Objetivo do projeto:
Um sistema quede produto faz agendamento de Jobs de produtos, criando uma tela onde registra os Jobs para cadastrar um produto, nessa tela precisa ter um tempo em que o Job vai ser acionado.
Usar o PostgreSQL no Docker para armazenar informações, através do PostgreSQL, o Airflow precisa acessar e reconhecer esse Job e disparar uma DAG.

2. Funcionamento do sistema: O Tomcat agenda os Jobs para armazenar no banco de dados, nos códigos do Tomcat existem o AgendarJobServlet.java que é responsável por controlar a visualização e o modelo do projeto,
o index.html faz a parte de mostrar a tela para o usuário e o resultado.jsp demonstra o resultado do agendamento, se foi agendado com sucesso ou se teve erro, o Job.java são os dados que são armazenados,
O JobDAO.java faz a conexão com o PostgreSQL.

3. Fluxo de execução: 
