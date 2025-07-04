package com.biblio.repository;

import com.biblio.model.Pret;
import com.biblio.model.Adherent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface PretRepository extends JpaRepository<Pret, Long> {
    List<Pret> findByAdherentIdAdherent(Long idAdherent);
    List<Pret> findByExemplaireIdExemplaire(Long idExemplaire);
    List<Pret> findByDateRenduReelleIsNull();
    long countByDateRenduReelleIsNull();
    List<Pret> findByDateRenduReelleIsNullAndDateRenduPrevueBefore(LocalDate date);
    long countByDateRenduReelleIsNullAndDateRenduPrevueBefore(LocalDate date);
    List<Pret> findByAdherentNomContainingIgnoreCase(String nom);
    List<Pret> findByExemplaireLivreTitreContainingIgnoreCase(String titre);
    
    // Méthodes pour l'activité récente
    @Query("SELECT p FROM Pret p ORDER BY p.datePret DESC LIMIT 5")
    List<Pret> findTop5ByOrderByDatePretDesc();
    
    @Query("SELECT p FROM Pret p WHERE p.dateRenduReelle IS NOT NULL ORDER BY p.dateRenduReelle DESC LIMIT 5")
    List<Pret> findTop5ByDateRenduReelleIsNotNullOrderByDateRenduReelleDesc();

    List<Pret> findByAdherentAndDateRenduReelleIsNull(Adherent adherent);
    List<Pret> findByAdherentAndDateRenduReelleIsNotNullOrderByDateRenduReelleDesc(Adherent adherent);
    List<Pret> findByAdherentOrderByDatePretDesc(Adherent adherent);
    long countByAdherent(Adherent adherent);

    // Nombre de prêts à une date donnée
    long countByDatePret(java.time.LocalDate datePret);

    // Nombre de retours à une date donnée
    long countByDateRenduReelle(java.time.LocalDate dateRenduReelle);
} 