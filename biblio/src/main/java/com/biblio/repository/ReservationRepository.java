package com.biblio.repository;

import com.biblio.model.Reservation;
import com.biblio.model.Adherent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByAdherentIdAdherent(Long idAdherent);
    List<Reservation> findByLivreIdLivre(Long idLivre);
    List<Reservation> findByEtat(String etat);
    long countByEtat(String etat);
    List<Reservation> findByAdherentNomContainingIgnoreCase(String nom);
    List<Reservation> findByLivreTitreContainingIgnoreCase(String titre);
    List<Reservation> findByAdherentNomContainingIgnoreCaseAndLivreTitreContainingIgnoreCase(String nom, String titre);
    
    // Méthode pour l'activité récente
    @Query("SELECT r FROM Reservation r ORDER BY r.dateReservation DESC LIMIT 5")
    List<Reservation> findTop5ByOrderByDateReservationDesc();

    List<Reservation> findByAdherentAndEtatNot(Adherent adherent, String etat);
    List<Reservation> findByAdherentOrderByDateReservationDesc(Adherent adherent);
} 