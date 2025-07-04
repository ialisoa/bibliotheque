package com.biblio.repository;

import com.biblio.model.Livre;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LivreRepository extends JpaRepository<Livre, Long> {
    List<Livre> findByTitreContainingOrAuteurContainingOrLangue(String titre, String auteur, String langue);
    List<Livre> findByStatut(String statut);
} 