package com.biblio.controller;

import com.biblio.model.Adherent;
import com.biblio.model.Penalite;
import com.biblio.model.Pret;
import com.biblio.model.Reservation;
import com.biblio.model.Parametre;
import com.biblio.repository.AdherentRepository;
import com.biblio.repository.PenaliteRepository;
import com.biblio.repository.PretRepository;
import com.biblio.repository.ReservationRepository;
import com.biblio.repository.CommentaireRepository;
import com.biblio.repository.LivreRepository;
import com.biblio.repository.ParametreRepository;
import com.biblio.model.Livre;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import com.biblio.model.ProlongementPret;
import com.biblio.repository.ProlongementPretRepository;

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
    
    @Autowired
    private CommentaireRepository commentaireRepository;
    
    @Autowired
    private LivreRepository livreRepository;
    
    @Autowired
    private ProlongementPretRepository prolongementPretRepository;
    
    @Autowired
    private ParametreRepository parametreRepository;
    
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
        
        // Statistiques dynamiques du jour (pour l'adhérent)
        LocalDate today = LocalDate.now();
        long pretsAujourdHui = pretsActifs.stream().filter(p -> p.getDatePret() != null && p.getDatePret().isEqual(today)).count();
        long retoursAujourdHui = pretsTermines.stream().filter(p -> p.getDateRenduReelle() != null && p.getDateRenduReelle().isEqual(today)).count();
        long reservationsAujourdHui = reservations.stream().filter(r -> r.getDateReservation() != null && r.getDateReservation().isEqual(today)).count();
        long penalitesAujourdHui = penalites.stream().filter(p -> p.getDateDebut() != null && p.getDateDebut().isEqual(today)).count();
        model.addAttribute("pretsAujourdHui", pretsAujourdHui);
        model.addAttribute("retoursAujourdHui", retoursAujourdHui);
        model.addAttribute("reservationsAujourdHui", reservationsAujourdHui);
        model.addAttribute("penalitesAujourdHui", penalitesAujourdHui);
        
        // Moyenne des notes par livre (pour tous les livres)
        List<Livre> livres = livreRepository.findAll();
        Map<Long, Double> moyennesLivres = new HashMap<>();
        for (Livre livre : livres) {
            Double moyenne = commentaireRepository.findAverageNoteByLivre(livre);
            moyennesLivres.put(livre.getIdLivre(), moyenne != null ? moyenne : 0.0);
        }
        model.addAttribute("moyennesLivres", moyennesLivres);
        model.addAttribute("livres", livres);
        
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
        
        // Calcul de la différence en jours entre aujourd'hui et la date d'expiration
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate expiration = adherent.getDateExpiration();
        long daysBetween = 0;
        if (expiration != null) {
            daysBetween = java.time.temporal.ChronoUnit.DAYS.between(today, expiration);
        }
        model.addAttribute("daysBetween", daysBetween);
        
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

    @PostMapping("/prets/{id}/demander-prolongement")
    public String demanderProlongement(@PathVariable Long id, Model model) {
        try {
            Pret pret = pretRepository.findById(id).orElseThrow();
            Adherent adherent = pret.getAdherent();
            
            // Récupérer les paramètres selon le type d'adhérent
            Parametre parametre = parametreRepository.findByTypeAdherent(adherent.getType());
            if (parametre == null) {
                return "redirect:/user/prets?error=parametre_manquant";
            }
            
            Integer quotaProlongement = parametre.getQuotaProlongement();
            
            // Vérifier le quota (0 = illimité)
            if (quotaProlongement > 0 && adherent.getDemandesProlongementUtilisees() >= quotaProlongement) {
                model.addAttribute("error", "Quota de prolongement dépassé. Votre quota sera renouvelé le mois prochain.");
                return "redirect:/user/prets?error=quota_depasse";
            }
            
            // Créer la demande de prolongement
            ProlongementPret demande = new ProlongementPret();
            demande.setPret(pret);
            demande.setDateDemande(LocalDate.now());
            demande.setNouvelleDateRendu(pret.getDateRenduPrevue().plusDays(14)); // 14 jours par défaut
            demande.setEtat("en_attente");
            prolongementPretRepository.save(demande);
            
            return "redirect:/user/prets?success=prolongement_demande";
        } catch (Exception e) {
            return "redirect:/user/prets?error=demande_echoue";
        }
    }
} 