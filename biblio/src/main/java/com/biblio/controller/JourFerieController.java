package com.biblio.controller;

import com.biblio.model.JourFerie;
import com.biblio.repository.JourFerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/joursferies")
public class JourFerieController {
    @Autowired
    private JourFerieRepository jourFerieRepository;

    @GetMapping
    public String listeJoursFeries(Model model) {
        List<JourFerie> joursFeries = jourFerieRepository.findAll();
        model.addAttribute("joursFeries", joursFeries);
        return "joursferies/liste";
    }

    @GetMapping("/ajouter")
    public String formAjouterJourFerie(Model model) {
        model.addAttribute("jourFerie", new JourFerie());
        return "joursferies/ajouter";
    }

    @PostMapping("/ajouter")
    public String ajouterJourFerie(@ModelAttribute JourFerie jourFerie) {
        jourFerieRepository.save(jourFerie);
        return "redirect:/joursferies?success=true";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimerJourFerie(@PathVariable Long id) {
        jourFerieRepository.deleteById(id);
        return "redirect:/joursferies?deleted=true";
    }
} 