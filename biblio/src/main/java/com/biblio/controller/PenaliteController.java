package com.biblio.controller;

import com.biblio.model.Penalite;
import com.biblio.model.Adherent;
import com.biblio.model.Pret;
import com.biblio.repository.PenaliteRepository;
import com.biblio.repository.AdherentRepository;
import com.biblio.repository.PretRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/penalites")
public class PenaliteController {

    @Autowired
    private PenaliteRepository penaliteRepository;

    @Autowired
    private AdherentRepository adherentRepository;

    @Autowired
    private PretRepository pretRepository;

    // Liste des pénalités
    @GetMapping
    public String listePenalites(Model model, 
                                @RequestParam(required = false) String adherent,
                                @RequestParam(required = false) String motif) {
        
        List<Penalite> penalites;
        
        if (adherent != null && !adherent.isEmpty() || motif != null && !motif.isEmpty()) {
            if (adherent != null && !adherent.isEmpty() && motif != null && !motif.isEmpty()) {
                penalites = penaliteRepository.findByAdherentNomContainingIgnoreCaseAndMotifContainingIgnoreCase(adherent, motif);
            } else if (adherent != null && !adherent.isEmpty()) {
                penalites = penaliteRepository.findByAdherentNomContainingIgnoreCase(adherent);
            } else {
                penalites = penaliteRepository.findByMotifContainingIgnoreCase(motif);
            }
        } else {
            penalites = penaliteRepository.findAll();
        }
        
        model.addAttribute("penalites", penalites);
        return "penalites/liste";
    }

    // Formulaire d'ajout
    @GetMapping("/ajouter")
    public String formulaireAjout(Model model) {
        model.addAttribute("penalite", new Penalite());
        List<Adherent> adherents = adherentRepository.findAll();
        List<Pret> prets = pretRepository.findAll();
        model.addAttribute("adherents", adherents);
        model.addAttribute("prets", prets);
        return "penalites/ajouter";
    }

    // Traitement de l'ajout
    @PostMapping("/ajouter")
    public String ajouterPenalite(@ModelAttribute Penalite penalite, 
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        
        try {
            // Définir la date de début si non définie
            if (penalite.getDateDebut() == null) {
                penalite.setDateDebut(LocalDate.now());
            }

            penaliteRepository.save(penalite);
            redirectAttributes.addFlashAttribute("success", "Pénalité ajoutée avec succès !");
            return "redirect:/penalites?success=true";
            
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de l'ajout de la pénalité : " + e.getMessage());
            List<Adherent> adherents = adherentRepository.findAll();
            List<Pret> prets = pretRepository.findAll();
            model.addAttribute("adherents", adherents);
            model.addAttribute("prets", prets);
            return "penalites/ajouter";
        }
    }

    // Détails d'une pénalité
    @GetMapping("/{id}")
    public String detailsPenalite(@PathVariable Long id, Model model) {
        Optional<Penalite> penalite = penaliteRepository.findById(id);
        
        if (penalite.isPresent()) {
            model.addAttribute("penalite", penalite.get());
            return "penalites/detail";
        } else {
            return "redirect:/penalites?error=notfound";
        }
    }

    // Formulaire de modification
    @GetMapping("/{id}/modifier")
    public String formulaireModification(@PathVariable Long id, Model model) {
        Optional<Penalite> penalite = penaliteRepository.findById(id);
        
        if (penalite.isPresent()) {
            model.addAttribute("penalite", penalite.get());
            List<Adherent> adherents = adherentRepository.findAll();
            List<Pret> prets = pretRepository.findAll();
            model.addAttribute("adherents", adherents);
            model.addAttribute("prets", prets);
            return "penalites/modifier";
        } else {
            return "redirect:/penalites?error=notfound";
        }
    }

    // Traitement de la modification
    @PostMapping("/{id}/modifier")
    public String modifierPenalite(@PathVariable Long id, 
                                  @ModelAttribute Penalite penalite, 
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        
        try {
            penalite.setIdPenalite(id);
            penaliteRepository.save(penalite);
            redirectAttributes.addFlashAttribute("success", "Pénalité modifiée avec succès !");
            return "redirect:/penalites?success=modified";
            
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la modification : " + e.getMessage());
            List<Adherent> adherents = adherentRepository.findAll();
            List<Pret> prets = pretRepository.findAll();
            model.addAttribute("adherents", adherents);
            model.addAttribute("prets", prets);
            return "penalites/modifier";
        }
    }

    // Suppression d'une pénalité
    @PostMapping("/{id}/supprimer")
    public String supprimerPenalite(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            penaliteRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Pénalité supprimée avec succès !");
            return "redirect:/penalites?success=deleted";
        } catch (Exception e) {
            return "redirect:/penalites?error=delete";
        }
    }
} 