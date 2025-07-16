package com.biblio.controller;

import com.biblio.model.Adherent;
import com.biblio.repository.AdherentRepository;
import com.biblio.repository.PenaliteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/adherents")
public class AdherentController {

    @Autowired
    private AdherentRepository adherentRepository;

    @Autowired
    private PenaliteRepository penaliteRepository;

    // Liste des adhérents
    @GetMapping
    public String listeAdherents(Model model, 
                                @RequestParam(required = false) String nom,
                                @RequestParam(required = false) String prenom,
                                @RequestParam(required = false) String statut) {
        
        List<Adherent> adherents;
        
        if (nom != null && !nom.isEmpty() || prenom != null && !prenom.isEmpty() || statut != null && !statut.isEmpty()) {
            // Recherche avec filtres
            if (nom != null && !nom.isEmpty() && prenom != null && !prenom.isEmpty()) {
                adherents = adherentRepository.findByNomContainingIgnoreCaseAndPrenomContainingIgnoreCase(nom, prenom);
            } else if (nom != null && !nom.isEmpty()) {
                adherents = adherentRepository.findByNomContainingIgnoreCase(nom);
            } else if (prenom != null && !prenom.isEmpty()) {
                adherents = adherentRepository.findByPrenomContainingIgnoreCase(prenom);
            } else {
                adherents = adherentRepository.findByStatut(statut);
            }
        } else {
            adherents = adherentRepository.findAll();
        }
        
        model.addAttribute("adherents", adherents);
        return "adherents/liste";
    }

    // Formulaire d'ajout
    @GetMapping("/ajouter")
    public String formulaireAjout(Model model) {
        model.addAttribute("adherent", new Adherent());
        return "adherents/ajouter";
    }

    // Traitement de l'ajout
    @PostMapping("/ajouter")
    public String ajouterAdherent(@ModelAttribute Adherent adherent, 
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        
        try {
            // Définir la date d'inscription
            adherent.setDateInscription(LocalDate.now());
            
            // Définir la date d'expiration (1 an par défaut)
            adherent.setDateExpiration(LocalDate.now().plusYears(1));
            
            // Définir le statut par défaut
            if (adherent.getStatut() == null) {
                adherent.setStatut("actif");
            }

            // S'assurer que les champs de connexion restent vides pour permettre à l'adhérent de créer son compte
            adherent.setLogin(null);
            adherent.setMotDePasse(null);

            adherentRepository.save(adherent);
            redirectAttributes.addFlashAttribute("success", "Adhérent ajouté avec succès !");
            return "redirect:/adherents?success=true";
            
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de l'ajout de l'adhérent : " + e.getMessage());
            return "adherents/ajouter";
        }
    }

    // Détails d'un adhérent
    @GetMapping("/{id}")
    public String detailsAdherent(@PathVariable Long id, Model model) {
        Optional<Adherent> adherent = adherentRepository.findById(id);
        
        if (adherent.isPresent()) {
            model.addAttribute("adherent", adherent.get());
            return "adherents/detail";
        } else {
            return "redirect:/adherents?error=notfound";
        }
    }

    // Formulaire de modification
    @GetMapping("/{id}/modifier")
    public String formulaireModification(@PathVariable Long id, Model model) {
        Optional<Adherent> adherent = adherentRepository.findById(id);
        
        if (adherent.isPresent()) {
            model.addAttribute("adherent", adherent.get());
            return "adherents/modifier";
        } else {
            return "redirect:/adherents?error=notfound";
        }
    }

    // Traitement de la modification
    @PostMapping("/{id}/modifier")
    public String modifierAdherent(@PathVariable Long id, 
                                  @ModelAttribute Adherent adherent, 
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        
        try {
            adherent.setIdAdherent(id);
            adherentRepository.save(adherent);
            redirectAttributes.addFlashAttribute("success", "Adhérent modifié avec succès !");
            return "redirect:/adherents?success=modified";
            
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la modification : " + e.getMessage());
            return "adherents/modifier";
        }
    }

    // Suppression d'un adhérent
    @PostMapping("/{id}/supprimer")
    public String supprimerAdherent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adherentRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Adhérent supprimé avec succès !");
            return "redirect:/adherents?success=deleted";
        } catch (Exception e) {
            return "redirect:/adherents?error=delete";
        }
    }

    // Endpoint API JSON pour les détails d'un adhérent
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> getAdherentDetailsJson(@PathVariable Long id) {
        return adherentRepository.findById(id)
            .map(adherent -> {
                Map<String, Object> result = new HashMap<>();
                result.put("id", adherent.getIdAdherent());
                result.put("nom", adherent.getNom());
                result.put("prenom", adherent.getPrenom());
                result.put("email", adherent.getEmail());
                result.put("statut", adherent.getStatut());
                result.put("quota_prolongement", adherent.getQuotaProlongement());
                result.put("demandes_prolongement_utilisees", adherent.getDemandesProlongementUtilisees());
                result.put("date_expiration", adherent.getDateExpiration());
                // Pénalités de l'adhérent
                result.put("penalites", penaliteRepository.findByAdherentIdAdherent(adherent.getIdAdherent()));
                return ResponseEntity.ok(result);
            })
            .orElse(ResponseEntity.notFound().build());
    }
} 