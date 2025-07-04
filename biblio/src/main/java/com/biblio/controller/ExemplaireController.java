package com.biblio.controller;

import com.biblio.model.Exemplaire;
import com.biblio.model.Livre;
import com.biblio.repository.ExemplaireRepository;
import com.biblio.repository.LivreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/exemplaires")
public class ExemplaireController {

    @Autowired
    private ExemplaireRepository exemplaireRepository;

    @Autowired
    private LivreRepository livreRepository;

    // Liste des exemplaires
    @GetMapping
    public String listeExemplaires(Model model, 
                                  @RequestParam(required = false) String livre,
                                  @RequestParam(required = false) String statut) {
        
        List<Exemplaire> exemplaires;
        
        if (livre != null && !livre.isEmpty() || statut != null && !statut.isEmpty()) {
            if (livre != null && !livre.isEmpty() && statut != null && !statut.isEmpty()) {
                exemplaires = exemplaireRepository.findByLivreTitreContainingIgnoreCaseAndStatut(livre, statut);
            } else if (livre != null && !livre.isEmpty()) {
                exemplaires = exemplaireRepository.findByLivreTitreContainingIgnoreCase(livre);
            } else {
                exemplaires = exemplaireRepository.findByStatut(statut);
            }
        } else {
            exemplaires = exemplaireRepository.findAll();
        }
        
        model.addAttribute("exemplaires", exemplaires);
        return "exemplaires/liste";
    }

    // Formulaire d'ajout
    @GetMapping("/ajouter")
    public String formulaireAjout(Model model) {
        model.addAttribute("exemplaire", new Exemplaire());
        List<Livre> livres = livreRepository.findAll();
        model.addAttribute("livres", livres);
        return "exemplaires/ajouter";
    }

    // Traitement de l'ajout
    @PostMapping("/ajouter")
    public String ajouterExemplaire(@ModelAttribute Exemplaire exemplaire, 
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        
        try {
            // Définir le statut par défaut
            if (exemplaire.getStatut() == null) {
                exemplaire.setStatut("disponible");
            }
            
            // Définir l'état par défaut
            if (exemplaire.getEtat() == null) {
                exemplaire.setEtat("bon");
            }

            exemplaireRepository.save(exemplaire);
            redirectAttributes.addFlashAttribute("success", "Exemplaire ajouté avec succès !");
            return "redirect:/exemplaires?success=true";
            
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de l'ajout de l'exemplaire : " + e.getMessage());
            List<Livre> livres = livreRepository.findAll();
            model.addAttribute("livres", livres);
            return "exemplaires/ajouter";
        }
    }

    // Détails d'un exemplaire
    @GetMapping("/{id}")
    public String detailsExemplaire(@PathVariable Long id, Model model) {
        try {
            System.out.println("Recherche de l'exemplaire avec l'ID: " + id);
            Optional<Exemplaire> exemplaireOpt = exemplaireRepository.findById(id);
            
            if (exemplaireOpt.isPresent()) {
                Exemplaire exemplaire = exemplaireOpt.get();
                System.out.println("Exemplaire trouvé: " + exemplaire.getNomExemplaire());
                
                // Vérifier si le livre est associé
                if (exemplaire.getLivre() == null) {
                    System.out.println("ATTENTION: L'exemplaire n'a pas de livre associé!");
                    model.addAttribute("error", "Cet exemplaire n'a pas de livre associé");
                    return "exemplaires/detail";
                }
                
                model.addAttribute("exemplaire", exemplaire);
                return "exemplaires/detail";
            } else {
                System.out.println("Aucun exemplaire trouvé avec l'ID: " + id);
                return "redirect:/exemplaires?error=notfound";
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération de l'exemplaire: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/exemplaires?error=server";
        }
    }

    // Formulaire de modification
    @GetMapping("/{id}/modifier")
    public String formulaireModification(@PathVariable Long id, Model model) {
        Optional<Exemplaire> exemplaire = exemplaireRepository.findById(id);
        
        if (exemplaire.isPresent()) {
            model.addAttribute("exemplaire", exemplaire.get());
            List<Livre> livres = livreRepository.findAll();
            model.addAttribute("livres", livres);
            return "exemplaires/modifier";
        } else {
            return "redirect:/exemplaires?error=notfound";
        }
    }

    // Traitement de la modification
    @PostMapping("/{id}/modifier")
    public String modifierExemplaire(@PathVariable Long id, 
                                    @ModelAttribute Exemplaire exemplaire, 
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        
        try {
            exemplaire.setIdExemplaire(id);
            exemplaireRepository.save(exemplaire);
            redirectAttributes.addFlashAttribute("success", "Exemplaire modifié avec succès !");
            return "redirect:/exemplaires?success=modified";
            
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la modification : " + e.getMessage());
            List<Livre> livres = livreRepository.findAll();
            model.addAttribute("livres", livres);
            return "exemplaires/modifier";
        }
    }

    // Suppression d'un exemplaire
    @PostMapping("/{id}/supprimer")
    public String supprimerExemplaire(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            exemplaireRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Exemplaire supprimé avec succès !");
            return "redirect:/exemplaires?success=deleted";
        } catch (Exception e) {
            return "redirect:/exemplaires?error=delete";
        }
    }
} 