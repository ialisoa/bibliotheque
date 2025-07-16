package com.biblio.controller;

import com.biblio.model.Reservation;
import com.biblio.model.Adherent;
import com.biblio.model.Livre;
import com.biblio.repository.ReservationRepository;
import com.biblio.repository.AdherentRepository;
import com.biblio.repository.LivreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import com.biblio.model.Exemplaire;
import com.biblio.model.Pret;
import com.biblio.model.Statut;
import com.biblio.repository.ExemplaireRepository;
import com.biblio.repository.PretRepository;
import com.biblio.repository.StatutRepository;

@Controller
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private AdherentRepository adherentRepository;

    @Autowired
    private LivreRepository livreRepository;

    @Autowired
    private ExemplaireRepository exemplaireRepository;

    @Autowired
    private StatutRepository statutRepository;

    @Autowired
    private PretRepository pretRepository;

    // Liste des réservations
    @GetMapping
    public String listeReservations(Model model, 
                                   @RequestParam(required = false) String adherent,
                                   @RequestParam(required = false) String livre,
                                   @RequestParam(required = false) String etat) {
        
        List<Reservation> reservations;
        
        if (adherent != null && !adherent.isEmpty() || livre != null && !livre.isEmpty() || etat != null && !etat.isEmpty()) {
            if (adherent != null && !adherent.isEmpty() && livre != null && !livre.isEmpty()) {
                reservations = reservationRepository.findByAdherentNomContainingIgnoreCaseAndLivreTitreContainingIgnoreCase(adherent, livre);
            } else if (adherent != null && !adherent.isEmpty()) {
                reservations = reservationRepository.findByAdherentNomContainingIgnoreCase(adherent);
            } else if (livre != null && !livre.isEmpty()) {
                reservations = reservationRepository.findByLivreTitreContainingIgnoreCase(livre);
            } else {
                reservations = reservationRepository.findByEtat(etat);
            }
        } else {
            reservations = reservationRepository.findAll();
        }
        
        model.addAttribute("reservations", reservations);
        return "reservations/liste";
    }

    // Formulaire d'ajout
    @GetMapping("/ajouter")
    public String formulaireAjout(Model model) {
        model.addAttribute("reservation", new Reservation());
        List<Adherent> adherents = adherentRepository.findAll();
        List<Livre> livres = livreRepository.findAll();
        model.addAttribute("adherents", adherents);
        model.addAttribute("livres", livres);
        return "reservations/ajouter";
    }

    // Traitement de l'ajout
    @PostMapping("/ajouter")
    public String ajouterReservation(@ModelAttribute Reservation reservation, 
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        
        try {
            // Définir la date de réservation
            reservation.setDateReservation(LocalDate.now());
            
            // Définir l'état par défaut
            if (reservation.getEtat() == null) {
                reservation.setEtat("en_attente");
            }

            reservationRepository.save(reservation);
            redirectAttributes.addFlashAttribute("success", "Réservation ajoutée avec succès !");
            return "redirect:/reservations?success=true";
            
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de l'ajout de la réservation : " + e.getMessage());
            List<Adherent> adherents = adherentRepository.findAll();
            List<Livre> livres = livreRepository.findAll();
            model.addAttribute("adherents", adherents);
            model.addAttribute("livres", livres);
            return "reservations/ajouter";
        }
    }

    // Détails d'une réservation
    @GetMapping("/{id}")
    public String detailsReservation(@PathVariable Long id, Model model) {
        Optional<Reservation> reservation = reservationRepository.findById(id);
        
        if (reservation.isPresent()) {
            model.addAttribute("reservation", reservation.get());
            return "reservations/detail";
        } else {
            return "redirect:/reservations?error=notfound";
        }
    }

    // Formulaire de modification
    @GetMapping("/{id}/modifier")
    public String formulaireModification(@PathVariable Long id, Model model) {
        Optional<Reservation> reservation = reservationRepository.findById(id);
        
        if (reservation.isPresent()) {
            model.addAttribute("reservation", reservation.get());
            List<Adherent> adherents = adherentRepository.findAll();
            List<Livre> livres = livreRepository.findAll();
            model.addAttribute("adherents", adherents);
            model.addAttribute("livres", livres);
            return "reservations/modifier";
        } else {
            return "redirect:/reservations?error=notfound";
        }
    }

    // Traitement de la modification
    @PostMapping("/{id}/modifier")
    public String modifierReservation(@PathVariable Long id, 
                                     @ModelAttribute Reservation reservation, 
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        
        try {
            reservation.setIdReservation(id);
            reservationRepository.save(reservation);
            redirectAttributes.addFlashAttribute("success", "Réservation modifiée avec succès !");
            return "redirect:/reservations?success=modified";
            
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la modification : " + e.getMessage());
            List<Adherent> adherents = adherentRepository.findAll();
            List<Livre> livres = livreRepository.findAll();
            model.addAttribute("adherents", adherents);
            model.addAttribute("livres", livres);
            return "reservations/modifier";
        }
    }

    // Suppression d'une réservation
    @PostMapping("/{id}/supprimer")
    public String supprimerReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservationRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Réservation supprimée avec succès !");
            return "redirect:/reservations?success=deleted";
        } catch (Exception e) {
            return "redirect:/reservations?error=delete";
        }
    }

    @PostMapping("/{id}/valider")
    public String validerReservation(@PathVariable Long id) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation != null && "en_attente".equals(reservation.getEtat())) {
            // Chercher un exemplaire disponible du livre réservé
            List<Exemplaire> exemplairesDispo = exemplaireRepository.findByLivreIdLivre(reservation.getLivre().getIdLivre())
                .stream().filter(ex -> "disponible".equalsIgnoreCase(ex.getStatut())).toList();
            if (!exemplairesDispo.isEmpty()) {
                Exemplaire exemplaire = exemplairesDispo.get(0);
                // Créer le prêt
                Pret pret = new Pret();
                pret.setAdherent(reservation.getAdherent());
                pret.setExemplaire(exemplaire);
                pret.setDatePret(reservation.getDateReservation());
                pret.setDateRenduPrevue(reservation.getDateFinReservation());
                pret.setType("domicile");
                Statut statutPret = statutRepository.findByNom("en_cours");
                pret.setStatut(statutPret);
                pretRepository.save(pret);
                // Mettre à jour l'exemplaire comme emprunté
                exemplaire.setStatut("emprunte");
                exemplaireRepository.save(exemplaire);
                // Mettre à jour la réservation
                reservation.setEtat("valide");
            } else {
                // Pas d'exemplaire disponible, la réservation reste en attente ou passe à 'impossible'
                reservation.setEtat("impossible");
            }
            reservationRepository.save(reservation);
        }
        return "redirect:/reservations";
    }

    @PostMapping("/{id}/refuser")
    public String refuserReservation(@PathVariable Long id) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation != null && "en_attente".equals(reservation.getEtat())) {
            reservation.setEtat("annulee");
            reservationRepository.save(reservation);
        }
        return "redirect:/reservations";
    }
} 