<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Liste des Livres</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body>
<div class="container mt-4">
    <h1>Catalogue des Livres</h1>
    <a href="/livres/ajouter" class="btn btn-primary mb-3">Ajouter un livre</a>
    <form action="/livres/recherche" method="get" class="row g-3 mb-3">
        <div class="col-auto">
            <input type="text" name="titre" class="form-control" placeholder="Titre">
        </div>
        <div class="col-auto">
            <input type="text" name="auteur" class="form-control" placeholder="Auteur">
        </div>
        <div class="col-auto">
            <input type="text" name="langue" class="form-control" placeholder="Langue">
        </div>
        <div class="col-auto">
            <button type="submit" class="btn btn-secondary">Rechercher</button>
        </div>
    </form>
    <table class="table table-striped">
        <thead>
        <tr>
            <th>ID</th>
            <th>Titre</th>
            <th>Auteur</th>
            <th>ISBN</th>
            <th>Edition</th>
            <th>Année</th>
            <th>Langue</th>
            <th>Exemplaires</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="livre" items="${livres}">
            <tr>
                <td>${livre.idLivre}</td>
                <td>${livre.titre}</td>
                <td>${livre.auteur}</td>
                <td>${livre.isbn}</td>
                <td>${livre.edition}</td>
                <td>${livre.anneePublication}</td>
                <td>${livre.langue}</td>
                <td>${livre.nombreExemplaires}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html> 