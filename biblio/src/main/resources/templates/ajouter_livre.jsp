<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Ajouter un Livre</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body>
<div class="container mt-4">
    <h1>Ajouter un Livre</h1>
    <form action="/livres/ajouter" method="post">
        <div class="mb-3">
            <label for="titre" class="form-label">Titre</label>
            <input type="text" class="form-control" id="titre" name="titre" required>
        </div>
        <div class="mb-3">
            <label for="auteur" class="form-label">Auteur</label>
            <input type="text" class="form-control" id="auteur" name="auteur">
        </div>
        <div class="mb-3">
            <label for="isbn" class="form-label">ISBN</label>
            <input type="text" class="form-control" id="isbn" name="isbn">
        </div>
        <div class="mb-3">
            <label for="edition" class="form-label">Édition</label>
            <input type="text" class="form-control" id="edition" name="edition">
        </div>
        <div class="mb-3">
            <label for="anneePublication" class="form-label">Année de publication</label>
            <input type="number" class="form-control" id="anneePublication" name="anneePublication">
        </div>
        <div class="mb-3">
            <label for="nombreExemplaires" class="form-label">Nombre d'exemplaires</label>
            <input type="number" class="form-control" id="nombreExemplaires" name="nombreExemplaires" required>
        </div>
        <div class="mb-3">
            <label for="langue" class="form-label">Langue</label>
            <input type="text" class="form-control" id="langue" name="langue">
        </div>
        <div class="mb-3">
            <label for="nombrePages" class="form-label">Nombre de pages</label>
            <input type="number" class="form-control" id="nombrePages" name="nombrePages">
        </div>
        <div class="mb-3">
            <label for="cv" class="form-label">Résumé</label>
            <textarea class="form-control" id="cv" name="cv"></textarea>
        </div>
        <div class="mb-3">
            <label for="ageMinimum" class="form-label">Âge minimum</label>
            <input type="number" class="form-control" id="ageMinimum" name="ageMinimum">
        </div>
        <button type="submit" class="btn btn-success">Ajouter</button>
        <a href="/livres" class="btn btn-secondary">Annuler</a>
    </form>
</div>
</body>
</html> 