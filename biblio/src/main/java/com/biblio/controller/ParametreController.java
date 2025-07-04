package com.biblio.controller;

import com.biblio.model.Parametre;
import com.biblio.repository.ParametreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/parametres")
public class ParametreController {

    @Autowired
    private ParametreRepository parametreRepository;

    // Liste des paramètres
    @GetMapping
    public String listeParametres(Model model) {
        List<Parametre> parametres = parametreRepository.findAll();
        model.addAttribute("parametres", parametres);
        return "parametres/liste";
    }

    // Formulaire d'ajout
    @GetMapping("/ajouter")
    public String formulaireAjout(Model model) {
        model.addAttribute("parametre", new Parametre());
        return "parametres/ajouter";
    }

    // Traitement de l'ajout
    @PostMapping("/ajouter")
    public String ajouterParametre(@ModelAttribute Parametre parametre, 
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        
        try {
            parametreRepository.save(parametre);
            redirectAttributes.addFlashAttribute("success", "Paramètre ajouté avec succès !");
            return "redirect:/parametres?success=true";
            
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de l'ajout du paramètre : " + e.getMessage());
            return "parametres/ajouter";
        }
    }

    // Détails d'un paramètre
    @GetMapping("/{id}")
    public String detailsParametre(@PathVariable Long id, Model model) {
        Optional<Parametre> parametre = parametreRepository.findById(id);
        
        if (parametre.isPresent()) {
            model.addAttribute("parametre", parametre.get());
            return "parametres/detail";
        } else {
            return "redirect:/parametres?error=notfound";
        }
    }

    // Formulaire de modification
    @GetMapping("/{id}/modifier")
    public String formulaireModification(@PathVariable Long id, Model model) {
        Optional<Parametre> parametre = parametreRepository.findById(id);
        
        if (parametre.isPresent()) {
            model.addAttribute("parametre", parametre.get());
            return "parametres/modifier";
        } else {
            return "redirect:/parametres?error=notfound";
        }
    }

    // Traitement de la modification
    @PostMapping("/{id}/modifier")
    public String modifierParametre(@PathVariable Long id, 
                                   @ModelAttribute Parametre parametre, 
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        
        try {
            parametre.setIdParametre(id);
            parametreRepository.save(parametre);
            redirectAttributes.addFlashAttribute("success", "Paramètre modifié avec succès !");
            return "redirect:/parametres?success=modified";
            
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la modification : " + e.getMessage());
            return "parametres/modifier";
        }
    }

    // Suppression d'un paramètre
    @PostMapping("/{id}/supprimer")
    public String supprimerParametre(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            parametreRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Paramètre supprimé avec succès !");
            return "redirect:/parametres?success=deleted";
        } catch (Exception e) {
            return "redirect:/parametres?error=delete";
        }
    }
} 