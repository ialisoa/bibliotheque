package com.biblio.repository;

import com.biblio.model.Exemplaire;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExemplaireRepository extends JpaRepository<Exemplaire, Long> {
    List<Exemplaire> findByLivreIdLivre(Long idLivre);
    List<Exemplaire> findByEtat(String etat);
    long countByEtat(String etat);
    List<Exemplaire> findByStatut(String statut);
    List<Exemplaire> findByLivreTitreContainingIgnoreCase(String titre);
    List<Exemplaire> findByLivreTitreContainingIgnoreCaseAndStatut(String titre, String statut);
} 