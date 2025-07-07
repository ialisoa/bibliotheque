package com.biblio.controller;

import com.biblio.model.Adherent;
import com.biblio.service.RenouvellementAbonnementService;
import com.biblio.repository.AdherentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/renouvellements")
public class RenouvellementAbonnementController {
    
    @Autowired
    private RenouvellementAbonnementService renouvellementService;
    
    @Autowired
    private AdherentRepository adherentRepository;
    
    @GetMapping
    public String listeRenouvellements(Model model) {
        // Exécuter la vérification automatique
        renouvellementService.verifierEtRenouvelerAbonnements();
        renouvellementService.desactiverAdherentsExpires();
        
        // Récupérer les listes
        List<Adherent> adherentsExpirantBientot = renouvellementService.getAdherentsExpirantBientot();
        List<Adherent> adherentsExpires = renouvellementService.getAdherentsExpires();
        
        // Calculer les jours restants pour chaque adhérent
        Map<Long, Long> joursRestants = new HashMap<>();
        LocalDate aujourdhui = LocalDate.now();
        
        for (Adherent adherent : adherentsExpirantBientot) {
            if (adherent.getDateExpiration() != null) {
                long jours = ChronoUnit.DAYS.between(aujourdhui, adherent.getDateExpiration());
                joursRestants.put(adherent.getIdAdherent(), jours);
            }
        }
        
        model.addAttribute("adherentsExpirantBientot", adherentsExpirantBientot);
        model.addAttribute("adherentsExpires", adherentsExpires);
        model.addAttribute("joursRestants", joursRestants);
        
        return "renouvellements/liste";
    }
    
    @PostMapping("/{id}/renouveler")
    public String renouvelerAbonnement(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Adherent adherent = adherentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Adhérent non trouvé"));
            
            renouvellementService.renouvelerAbonnement(adherent);
            
            redirectAttributes.addFlashAttribute("success", 
                "Abonnement de " + adherent.getNom() + " " + adherent.getPrenom() + " renouvelé avec succès !");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors du renouvellement : " + e.getMessage());
        }
        
        return "redirect:/renouvellements";
    }
    
    @PostMapping("/{id}/desactiver")
    public String desactiverAdherent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Adherent adherent = adherentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Adhérent non trouvé"));
            
            adherent.setStatut("inactif");
            adherentRepository.save(adherent);
            
            redirectAttributes.addFlashAttribute("success", 
                "Adhérent " + adherent.getNom() + " " + adherent.getPrenom() + " désactivé.");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors de la désactivation : " + e.getMessage());
        }
        
        return "redirect:/renouvellements";
    }
    
    @PostMapping("/renouveler-tous")
    public String renouvelerTousLesAbonnements(RedirectAttributes redirectAttributes) {
        try {
            List<Adherent> adherentsExpirantBientot = renouvellementService.getAdherentsExpirantBientot();
            int count = 0;
            
            for (Adherent adherent : adherentsExpirantBientot) {
                if (adherent.getStatut().equals("actif")) {
                    renouvellementService.renouvelerAbonnement(adherent);
                    count++;
                }
            }
            
            redirectAttributes.addFlashAttribute("success", 
                count + " abonnements renouvelés automatiquement !");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors du renouvellement en masse : " + e.getMessage());
        }
        
        return "redirect:/renouvellements";
    }
} 