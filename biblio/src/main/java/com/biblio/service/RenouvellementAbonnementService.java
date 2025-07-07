package com.biblio.service;

import com.biblio.model.Adherent;
import com.biblio.repository.AdherentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class RenouvellementAbonnementService {
    
    @Autowired
    private AdherentRepository adherentRepository;
    
    /**
     * Vérifie et renouvelle automatiquement les abonnements expirés ou expirant bientôt
     */
    public void verifierEtRenouvelerAbonnements() {
        LocalDate aujourdhui = LocalDate.now();
        LocalDate dans30Jours = aujourdhui.plusDays(30);
        
        // Trouver les adhérents dont l'abonnement expire dans les 30 prochains jours
        List<Adherent> adherentsExpirant = adherentRepository.findByDateExpirationBetween(aujourdhui, dans30Jours);
        
        for (Adherent adherent : adherentsExpirant) {
            long joursRestants = ChronoUnit.DAYS.between(aujourdhui, adherent.getDateExpiration());
            
            // Si l'abonnement expire dans moins de 7 jours, renouveler automatiquement
            if (joursRestants <= 7 && adherent.getStatut().equals("actif")) {
                renouvelerAbonnement(adherent);
            }
        }
    }
    
    /**
     * Renouvelle l'abonnement d'un adhérent (1 an supplémentaire)
     */
    public void renouvelerAbonnement(Adherent adherent) {
        LocalDate nouvelleDateExpiration = adherent.getDateExpiration().plusYears(1);
        adherent.setDateExpiration(nouvelleDateExpiration);
        adherent.setStatut("actif"); // S'assurer que le statut est actif
        adherentRepository.save(adherent);
    }
    
    /**
     * Trouve les adhérents dont l'abonnement expire bientôt (pour notifications)
     */
    public List<Adherent> getAdherentsExpirantBientot() {
        LocalDate aujourdhui = LocalDate.now();
        LocalDate dans30Jours = aujourdhui.plusDays(30);
        return adherentRepository.findByDateExpirationBetween(aujourdhui, dans30Jours);
    }
    
    /**
     * Trouve les adhérents dont l'abonnement a expiré
     */
    public List<Adherent> getAdherentsExpires() {
        LocalDate aujourdhui = LocalDate.now();
        return adherentRepository.findByDateExpirationBeforeAndStatutEquals(aujourdhui, "actif");
    }
    
    /**
     * Désactive les adhérents dont l'abonnement a expiré
     */
    public void desactiverAdherentsExpires() {
        List<Adherent> adherentsExpires = getAdherentsExpires();
        for (Adherent adherent : adherentsExpires) {
            adherent.setStatut("inactif");
            adherentRepository.save(adherent);
        }
    }
} 