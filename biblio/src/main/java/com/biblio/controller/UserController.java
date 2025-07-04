package com.biblio.controller;

import com.biblio.model.Adherent;
import com.biblio.model.Penalite;
import com.biblio.model.Pret;
import com.biblio.model.Reservation;
import com.biblio.repository.AdherentRepository;
import com.biblio.repository.PenaliteRepository;
import com.biblio.repository.PretRepository;
import com.biblio.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private AdherentRepository adherentRepository;
    
    @Autowired
    private PretRepository pretRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private PenaliteRepository penaliteRepository;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        logger.info("Accès au dashboard utilisateur pour: {}", username);
        
        // Récupérer l'adhérent connecté
        Adherent adherent = adherentRepository.findByLogin(username)
            .orElseThrow(() -> new RuntimeException("Adhérent non trouvé"));
        
        // Récupérer les prêts actifs
        List<Pret> pretsActifs = pretRepository.findByAdherentAndDateRenduReelleIsNull(adherent);
        
        // Récupérer les prêts terminés (5 plus récents)
        List<Pret> pretsTermines = pretRepository.findByAdherentAndDateRenduReelleIsNotNullOrderByDateRenduReelleDesc(adherent);
        if (pretsTermines.size() > 5) {
            pretsTermines = pretsTermines.subList(0, 5);
        }
        
        // Récupérer les réservations actives
        List<Reservation> reservations = reservationRepository.findByAdherentAndEtatNot(adherent, "annulee");
        
        // Récupérer les pénalités actives
        List<Penalite> penalites = penaliteRepository.findByAdherentAndDateFinIsNull(adherent);
        
        // Calculer les statistiques
        long totalPrets = pretRepository.countByAdherent(adherent);
        long pretsEnCours = pretsActifs.size();
        long reservationsEnCours = reservations.size();
        long penalitesActives = penalites.size();
        
        // Ajouter les données au modèle
        model.addAttribute("adherent", adherent);
        model.addAttribute("pretsActifs", pretsActifs);
        model.addAttribute("pretsTermines", pretsTermines);
        model.addAttribute("reservations", reservations);
        model.addAttribute("penalites", penalites);
        model.addAttribute("totalPrets", totalPrets);
        model.addAttribute("pretsEnCours", pretsEnCours);
        model.addAttribute("reservationsEnCours", reservationsEnCours);
        model.addAttribute("penalitesActives", penalitesActives);
        
        logger.info("Dashboard chargé - Prêts actifs: {}, Réservations: {}, Pénalités: {}", 
                   pretsEnCours, reservationsEnCours, penalitesActives);
        
        return "user/dashboard";
    }
    
    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        logger.info("Accès au profil utilisateur pour: {}", username);
        
        Adherent adherent = adherentRepository.findByLogin(username)
            .orElseThrow(() -> new RuntimeException("Adhérent non trouvé"));
        
        model.addAttribute("adherent", adherent);
        
        return "user/profile";
    }
    
    @GetMapping("/prets")
    public String mesPrets(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        logger.info("Accès aux prêts de l'utilisateur: {}", username);
        
        Adherent adherent = adherentRepository.findByLogin(username)
            .orElseThrow(() -> new RuntimeException("Adhérent non trouvé"));
        
        List<Pret> prets = pretRepository.findByAdherentOrderByDatePretDesc(adherent);
        
        model.addAttribute("adherent", adherent);
        model.addAttribute("prets", prets);
        
        return "user/prets";
    }
    
    @GetMapping("/reservations")
    public String mesReservations(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        logger.info("Accès aux réservations de l'utilisateur: {}", username);
        
        Adherent adherent = adherentRepository.findByLogin(username)
            .orElseThrow(() -> new RuntimeException("Adhérent non trouvé"));
        
        List<Reservation> reservations = reservationRepository.findByAdherentOrderByDateReservationDesc(adherent);
        
        model.addAttribute("adherent", adherent);
        model.addAttribute("reservations", reservations);
        
        return "user/reservations";
    }
    
    @GetMapping("/penalites")
    public String mesPenalites(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        logger.info("Accès aux pénalités de l'utilisateur: {}", username);
        
        Adherent adherent = adherentRepository.findByLogin(username)
            .orElseThrow(() -> new RuntimeException("Adhérent non trouvé"));
        
        List<Penalite> penalites = penaliteRepository.findByAdherentOrderByDateDebutDesc(adherent);
        
        model.addAttribute("adherent", adherent);
        model.addAttribute("penalites", penalites);
        
        return "user/penalites";
    }
} 