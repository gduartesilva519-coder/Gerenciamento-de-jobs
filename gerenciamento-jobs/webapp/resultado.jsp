<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html lang="pt-BR">

<head>

    <meta charset="UTF-8">

    <title>Resultado do Agendamento</title>

    <style>

        body {
            font-family: Arial, sans-serif;
            background-color: #f4f6f8;
            text-align: center;
            padding-top: 100px;
        }

        .card {
            background: white;
            width: 400px;
            margin: auto;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }

        h1 {
            color: #222;
        }

        p {
            color: #555;
            font-size: 16px;
        }

        a {
            display: block;
            margin-top: 25px;
            padding: 12px;
            background: #333;
            color: white;
            text-decoration: none;
            border-radius: 6px;
        }

    </style>

</head>

<body>

    <div class="card">

        <% if (request.getAttribute("erro") != null) { %>

            <h1>Erro ao agendar o Job</h1>

            <p>
                <%= request.getAttribute("erro") %>
            </p>

        <% } else { %>

            <h1>Job agendado com sucesso!</h1>

            <p>
                <strong>Produto:</strong>
                <%= request.getAttribute("produto") %>
            </p>

            <p>
                <strong>Data e horário:</strong>
                <%= request.getAttribute("dataHoraExecucao") %>
            </p>

        <% } %>

        <a href="http://localhost:9090/manager/html">
            Voltar para o Manager
        </a>

    </div>

</body>

</html>