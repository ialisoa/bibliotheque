package com.biblio.repository;

import com.biblio.model.Penalite;
import com.biblio.model.Adherent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface PenaliteRepository extends JpaRepository<Penalite, Long> {
    List<Penalite> findByAdherentIdAdherent(Long idAdherent);
    List<Penalite> findByPretIdPret(Long idPret);
    List<Penalite> findByDateFinIsNull();
    long countByDateFinIsNull();
    List<Penalite> findByAdherentNomContainingIgnoreCase(String nom);
    List<Penalite> findByMotifContainingIgnoreCase(String motif);
    List<Penalite> findByAdherentNomContainingIgnoreCaseAndMotifContainingIgnoreCase(String nom, String motif);
    
    // Méthode pour l'activité récente
    @Query("SELECT p FROM Penalite p ORDER BY p.dateDebut DESC LIMIT 5")
    List<Penalite> findTop5ByOrderByDateDebutDesc();

    List<Penalite> findByAdherentAndDateFinIsNull(Adherent adherent);
    List<Penalite> findByAdherentOrderByDateDebutDesc(Adherent adherent);

    // Nombre de pénalités créées à une date donnée
    long countByDateDebut(java.time.LocalDate dateDebut);
} 