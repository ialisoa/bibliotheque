package com.biblio.repository;

import com.biblio.model.Commentaire;
import com.biblio.model.Livre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {
    @Query("SELECT AVG(c.note) FROM Commentaire c WHERE c.livre = :livre")
    Double findAverageNoteByLivre(Livre livre);
} 