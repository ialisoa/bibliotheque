package com.biblio.repository;

import com.biblio.model.Adherent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AdherentRepository extends JpaRepository<Adherent, Long> {
    Optional<Adherent> findByLogin(String login);
    List<Adherent> findByStatut(String statut);
    List<Adherent> findByNomContainingIgnoreCase(String nom);
    List<Adherent> findByPrenomContainingIgnoreCase(String prenom);
    List<Adherent> findByNomContainingIgnoreCaseAndPrenomContainingIgnoreCase(String nom, String prenom);
    long countByStatut(String statut);
    List<Adherent> findByEmailContainingIgnoreCase(String email);
    List<Adherent> findByNomContainingIgnoreCaseAndEmailContainingIgnoreCase(String nom, String email);
    Adherent findByNomAndPrenomAndEmail(String nom, String prenom, String email);
    
    // Méthodes pour le renouvellement d'abonnement
    List<Adherent> findByDateExpirationBetween(java.time.LocalDate dateDebut, java.time.LocalDate dateFin);
    List<Adherent> findByDateExpirationBeforeAndStatutEquals(java.time.LocalDate date, String statut);
}
