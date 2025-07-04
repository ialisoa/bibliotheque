package com.biblio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.biblio.repository.LivreRepository;
import com.biblio.repository.AdherentRepository;
import com.biblio.repository.PretRepository;
import com.biblio.repository.ReservationRepository;
import com.biblio.repository.ExemplaireRepository;
import com.biblio.repository.PenaliteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
public class PagesController {
    private static final Logger logger = LoggerFactory.getLogger(PagesController.class);
    
    @Autowired private LivreRepository livreRepository;
    @Autowired private AdherentRepository adherentRepository;
    @Autowired private PretRepository pretRepository;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private ExemplaireRepository exemplaireRepository;
    @Autowired private PenaliteRepository penaliteRepository;

    @GetMapping("/recherche")
    public String recherche(@RequestParam(value = "q", required = false) String q,
                           @RequestParam(value = "type", required = false) String type,
                           Model model) {
        logger.info("Accès à la page recherche");
        if (q != null && !q.trim().isEmpty()) {
            if (type == null || type.isEmpty() || type.equals("all")) {
                model.addAttribute("livres", livreRepository.findByTitreContainingOrAuteurContainingOrLangue(q, q, q));
                model.addAttribute("adherents", adherentRepository.findByNomContainingIgnoreCase(q));
                model.addAttribute("prets", pretRepository.findByAdherentNomContainingIgnoreCase(q));
                model.addAttribute("reservations", reservationRepository.findByAdherentNomContainingIgnoreCase(q));
                model.addAttribute("exemplaires", exemplaireRepository.findByLivreTitreContainingIgnoreCase(q));
                model.addAttribute("penalites", penaliteRepository.findByAdherentNomContainingIgnoreCase(q));
            } else {
                switch (type) {
                    case "livre":
                        model.addAttribute("livres", livreRepository.findByTitreContainingOrAuteurContainingOrLangue(q, q, q));
                        break;
                    case "adherent":
                        model.addAttribute("adherents", adherentRepository.findByNomContainingIgnoreCase(q));
                        break;
                    case "pret":
                        model.addAttribute("prets", pretRepository.findByAdherentNomContainingIgnoreCase(q));
                        break;
                    case "reservation":
                        model.addAttribute("reservations", reservationRepository.findByAdherentNomContainingIgnoreCase(q));
                        break;
                    case "exemplaire":
                        model.addAttribute("exemplaires", exemplaireRepository.findByLivreTitreContainingIgnoreCase(q));
                        break;
                    case "penalite":
                        model.addAttribute("penalites", penaliteRepository.findByAdherentNomContainingIgnoreCase(q));
                        break;
                }
            }
            model.addAttribute("q", q);
            model.addAttribute("type", type);
        }
        return "pages/recherche";
    }
} 