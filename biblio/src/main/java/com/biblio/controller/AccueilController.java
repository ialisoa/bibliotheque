package com.biblio.controller;

import com.biblio.repository.LivreRepository;
import com.biblio.repository.PretRepository;
import com.biblio.repository.AdherentRepository;
import com.biblio.repository.ExemplaireRepository;
import com.biblio.repository.ReservationRepository;
import com.biblio.repository.PenaliteRepository;
import com.biblio.model.Pret;
import com.biblio.model.Reservation;
import com.biblio.model.Penalite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AccueilController {
    private static final Logger logger = LoggerFactory.getLogger(AccueilController.class);
    
    @Autowired
    private LivreRepository livreRepository;
    
    @Autowired
    private PretRepository pretRepository;
    
    @Autowired
    private AdherentRepository adherentRepository;
    
    @Autowired
    private ExemplaireRepository exemplaireRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private PenaliteRepository penaliteRepository;
    
    @GetMapping({"/", "/accueil"})
    public String accueil(Model model) {
        logger.info("Accès à la page d'accueil");
        
        try {
            // Statistiques de base (méthodes sûres)
            long totalLivres = livreRepository.count();
            long totalPrets = pretRepository.count();
            long totalAdherents = adherentRepository.count();
            long totalReservations = reservationRepository.count();
            long totalPenalites = penaliteRepository.count();
            
            // Statistiques plus complexes (avec gestion d'erreur)
            long exemplairesDisponibles = 0;
            long exemplairesEmpruntes = 0;
            long pretsActifs = 0;
            long pretsEnRetard = 0;
            long adherentsActifs = 0;
            long reservationsEnAttente = 0;
            long penalitesActives = 0;
            
            try {
                exemplairesDisponibles = exemplaireRepository.countByEtat("disponible");
            } catch (Exception e) {
                logger.warn("Impossible de compter les exemplaires disponibles: {}", e.getMessage());
            }
            
            try {
                exemplairesEmpruntes = exemplaireRepository.countByEtat("emprunte");
            } catch (Exception e) {
                logger.warn("Impossible de compter les exemplaires empruntés: {}", e.getMessage());
            }
            
            try {
                pretsActifs = pretRepository.countByDateRenduReelleIsNull();
            } catch (Exception e) {
                logger.warn("Impossible de compter les prêts actifs: {}", e.getMessage());
            }
            
            try {
                pretsEnRetard = pretRepository.countByDateRenduReelleIsNullAndDateRenduPrevueBefore(LocalDate.now());
            } catch (Exception e) {
                logger.warn("Impossible de compter les prêts en retard: {}", e.getMessage());
            }
            
            try {
                adherentsActifs = adherentRepository.countByStatut("actif");
            } catch (Exception e) {
                logger.warn("Impossible de compter les adhérents actifs: {}", e.getMessage());
            }
            
            try {
                reservationsEnAttente = reservationRepository.countByEtat("en_attente");
            } catch (Exception e) {
                logger.warn("Impossible de compter les réservations en attente: {}", e.getMessage());
            }
            
            try {
                penalitesActives = penaliteRepository.countByDateFinIsNull();
            } catch (Exception e) {
                logger.warn("Impossible de compter les pénalités actives: {}", e.getMessage());
            }
            
            // Récupérer l'activité récente (derniers prêts, retours, réservations)
            List<Object> activiteRecente = new ArrayList<>();
            
            try {
                // Derniers prêts actifs
                List<Pret> derniersPrets = pretRepository.findTop5ByOrderByDatePretDesc();
                for (Pret pret : derniersPrets) {
                    if (pret.getAdherent() != null && pret.getExemplaire() != null && pret.getExemplaire().getLivre() != null) {
                        Map<String, Object> activite = new HashMap<>();
                        activite.put("date", pret.getDatePret());
                        activite.put("action", "Prêt");
                        activite.put("utilisateur", pret.getAdherent().getNom() + " " + pret.getAdherent().getPrenom());
                        activite.put("details", "Livre \"" + pret.getExemplaire().getLivre().getTitre() + "\" prêté");
                        activite.put("type", "pret");
                        activiteRecente.add(activite);
                    }
                }
                
                // Derniers retours
                List<Pret> derniersRetours = pretRepository.findTop5ByDateRenduReelleIsNotNullOrderByDateRenduReelleDesc();
                for (Pret pret : derniersRetours) {
                    if (pret.getAdherent() != null && pret.getExemplaire() != null && pret.getExemplaire().getLivre() != null) {
                        Map<String, Object> activite = new HashMap<>();
                        activite.put("date", pret.getDateRenduReelle());
                        activite.put("action", "Retour");
                        activite.put("utilisateur", pret.getAdherent().getNom() + " " + pret.getAdherent().getPrenom());
                        activite.put("details", "Livre \"" + pret.getExemplaire().getLivre().getTitre() + "\" retourné");
                        activite.put("type", "retour");
                        activiteRecente.add(activite);
                    }
                }
                
                // Dernières réservations
                List<Reservation> dernieresReservations = reservationRepository.findTop5ByOrderByDateReservationDesc();
                for (Reservation reservation : dernieresReservations) {
                    if (reservation.getAdherent() != null && reservation.getLivre() != null) {
                        Map<String, Object> activite = new HashMap<>();
                        activite.put("date", reservation.getDateReservation());
                        activite.put("action", "Réservation");
                        activite.put("utilisateur", reservation.getAdherent().getNom() + " " + reservation.getAdherent().getPrenom());
                        activite.put("details", "Réservation \"" + reservation.getLivre().getTitre() + "\"");
                        activite.put("type", "reservation");
                        activiteRecente.add(activite);
                    }
                }
                
                // Dernières pénalités
                List<Penalite> dernieresPenalites = penaliteRepository.findTop5ByOrderByDateDebutDesc();
                for (Penalite penalite : dernieresPenalites) {
                    if (penalite.getAdherent() != null) {
                        Map<String, Object> activite = new HashMap<>();
                        activite.put("date", penalite.getDateDebut());
                        activite.put("action", "Pénalité");
                        activite.put("utilisateur", penalite.getAdherent().getNom() + " " + penalite.getAdherent().getPrenom());
                        activite.put("details", penalite.getMotif() + " - " + penalite.getMontant() + "€");
                        activite.put("type", "penalite");
                        activiteRecente.add(activite);
                    }
                }
                
                // Trier par date (plus récent en premier) et prendre les 10 premiers
                activiteRecente.sort((a, b) -> {
                    LocalDate dateA = (LocalDate) ((Map<String, Object>) a).get("date");
                    LocalDate dateB = (LocalDate) ((Map<String, Object>) b).get("date");
                    return dateB.compareTo(dateA);
                });
                
                if (activiteRecente.size() > 10) {
                    activiteRecente = activiteRecente.subList(0, 10);
                }
                
            } catch (Exception e) {
                logger.warn("Impossible de récupérer l'activité récente: {}", e.getMessage());
            }
            
            // Ajouter les statistiques au modèle
            model.addAttribute("totalLivres", totalLivres);
            model.addAttribute("exemplairesDisponibles", exemplairesDisponibles);
            model.addAttribute("exemplairesEmpruntes", exemplairesEmpruntes);
            model.addAttribute("totalPrets", totalPrets);
            model.addAttribute("pretsActifs", pretsActifs);
            model.addAttribute("pretsEnRetard", pretsEnRetard);
            model.addAttribute("totalAdherents", totalAdherents);
            model.addAttribute("adherentsActifs", adherentsActifs);
            model.addAttribute("totalReservations", totalReservations);
            model.addAttribute("reservationsEnAttente", reservationsEnAttente);
            model.addAttribute("totalPenalites", totalPenalites);
            model.addAttribute("penalitesActives", penalitesActives);
            model.addAttribute("activiteRecente", activiteRecente);
            
            logger.info("Statistiques calculées - Livres: {}, Prêts actifs: {}, Adhérents: {}, Retards: {}, Pénalités: {}", 
                totalLivres, pretsActifs, totalAdherents, pretsEnRetard, totalPenalites);
                
        } catch (Exception e) {
            logger.error("Erreur lors du calcul des statistiques : {}", e.getMessage(), e);
            // Valeurs par défaut en cas d'erreur
            model.addAttribute("totalLivres", 0);
            model.addAttribute("exemplairesDisponibles", 0);
            model.addAttribute("exemplairesEmpruntes", 0);
            model.addAttribute("totalPrets", 0);
            model.addAttribute("pretsActifs", 0);
            model.addAttribute("pretsEnRetard", 0);
            model.addAttribute("totalAdherents", 0);
            model.addAttribute("adherentsActifs", 0);
            model.addAttribute("totalReservations", 0);
            model.addAttribute("reservationsEnAttente", 0);
            model.addAttribute("totalPenalites", 0);
            model.addAttribute("penalitesActives", 0);
            model.addAttribute("activiteRecente", new ArrayList<>());
        }
        
        return "accueil"; // Thymeleaf: accueil.html
    }
}
