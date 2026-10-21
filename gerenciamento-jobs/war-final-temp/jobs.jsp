<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Job" %>

<!DOCTYPE html>
<html lang="pt-BR">

<head>
    <meta charset="UTF-8">
    <title>Jobs Agendados</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f6f8;
            padding: 40px;
        }

        .container {
            width: 90%;
            max-width: 1000px;
            margin: auto;
            background: white;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }

        h1 {
            text-align: center;
            color: #222;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 25px;
        }

        th,td {
            padding: 12px;
            border-bottom: 1px solid #ddd;
            text-align: center;
        }

        th {
            background-color: #333;
            color: white;
        }

        tr:hover {
            background-color: #f1f1f1;
        }

        .voltar {
            display: inline-block;
            margin-top: 25px;
            padding: 10px 20px;
            background-color: #333;
            color: white;
            text-decoration: none;
            border-radius: 6px;
        }
    </style>
</head>

<body>

<div class="container">

    <h1>Jobs Agendados</h1>

    <%
        List<Job> jobs = (List<Job>) request.getAttribute("jobs");
    %>

    <% if (jobs == null || jobs.isEmpty()) { %>

        <p style="text-align: center;">
            Nenhum job encontrado.
        </p>

    <% } else { %>
    
        <table>

            <thead>
                <tr>
                    <th>ID</th>
                    <th>Produto</th>
                    <th>Status</th>
                    <th>Data e Hora de Execução</th>
                </tr>
            </thead>

            <tbody>

                <% for (Job job : jobs) { %>
                
                    <tr>
                        <td><%= job.getId() %></td>
                        <td><%= job.getProduto() %></td>
                        <td><%= job.getStatus() %></td>
                        <td><%= job.getDataHoraExecucao() %></td>
                    </tr>
                
                <% } %>

            </tbody>

        </table>

    <% } %>

    <a class="voltar" href="index.html">
        Voltar
    </a>

</div>

</body>
</html>