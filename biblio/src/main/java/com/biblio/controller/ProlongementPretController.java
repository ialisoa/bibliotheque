package com.biblio.controller;

import com.biblio.model.ProlongementPret;
import com.biblio.model.Pret;
import com.biblio.model.Adherent;
import com.biblio.model.Parametre;
import com.biblio.repository.ProlongementPretRepository;
import com.biblio.repository.PretRepository;
import com.biblio.repository.ParametreRepository;
import com.biblio.repository.AdherentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/prolongements")
public class ProlongementPretController {
    @Autowired
    private ProlongementPretRepository prolongementPretRepository;
    @Autowired
    private PretRepository pretRepository;
    @Autowired
    private ParametreRepository parametreRepository;
    @Autowired
    private AdherentRepository adherentRepository;

    @GetMapping
    public String listeDemandes(Model model) {
        List<ProlongementPret> demandes = prolongementPretRepository.findAll();
        
        // Vérifier et renouveler les quotas mensuellement
        for (ProlongementPret demande : demandes) {
            Adherent adherent = demande.getPret().getAdherent();
            if (adherent != null && adherent.getDateRenouvellementQuota() != null) {
                if (ChronoUnit.DAYS.between(adherent.getDateRenouvellementQuota(), LocalDate.now()) >= 30) {
                    // Renouveler le quota
                    adherent.setDemandesProlongementUtilisees(0);
                    adherent.setDateRenouvellementQuota(LocalDate.now());
                    adherentRepository.save(adherent);
                }
            }
        }
        
        model.addAttribute("demandes", demandes);
        return "prets/prolongements";
    }

    @PostMapping("/{id}/valider")
    public String validerDemande(@PathVariable Long id, 
                                @RequestParam(required = false) Integer nbJours,
                                @RequestParam(required = false) String mode) {
        ProlongementPret demande = prolongementPretRepository.findById(id).orElseThrow();
        Pret pretOriginal = demande.getPret();
        Adherent adherent = pretOriginal.getAdherent();
        
        // Récupérer les paramètres selon le type d'adhérent
        Parametre parametre = parametreRepository.findByTypeAdherent(adherent.getType());
        if (parametre == null) {
            return "redirect:/prolongements?error=parametre_manquant";
        }
        
        Integer quotaProlongement = parametre.getQuotaProlongement();
        
        // Vérifier le quota (0 = illimité)
        if (quotaProlongement > 0 && adherent.getDemandesProlongementUtilisees() >= quotaProlongement) {
            demande.setEtat("refuse");
            demande.setNouvelleDateRendu(pretOriginal.getDateRenduPrevue());
            prolongementPretRepository.save(demande);
            return "redirect:/prolongements?error=quota_depasse";
        }
        
        // Déterminer le nombre de jours selon le mode
        int joursProlongation;
        if ("automatique".equals(mode)) {
            // Mode automatique : utiliser la durée paramétrable
            joursProlongation = parametre.getDureePret() != null ? parametre.getDureePret() : 14;
        } else {
            // Mode manuel : utiliser la valeur fournie
            joursProlongation = nbJours != null ? nbJours : 7;
        }
        
        // Calculer les dates pour le nouveau prêt
        LocalDate finPretOriginal = pretOriginal.getDateRenduPrevue();
        LocalDate debutNouveauPret = finPretOriginal; // Le nouveau prêt commence à la fin de l'ancien
        LocalDate finNouveauPret = debutNouveauPret.plusDays(joursProlongation);
        
        // Terminer le prêt original (le marquer comme rendu)
        pretOriginal.setDateRenduReelle(finPretOriginal);
        pretRepository.save(pretOriginal);
        
        // Créer un nouveau prêt pour la prolongation
        Pret nouveauPret = new Pret();
        nouveauPret.setAdherent(adherent);
        nouveauPret.setExemplaire(pretOriginal.getExemplaire());
        nouveauPret.setDatePret(debutNouveauPret);
        nouveauPret.setDateRenduPrevue(finNouveauPret);
        nouveauPret.setType("prolongation");
        nouveauPret.setStatut(pretOriginal.getStatut()); // Garder le même statut
        pretRepository.save(nouveauPret);
        
        // Mettre à jour la demande de prolongement
        demande.setEtat("valide");
        demande.setNouvelleDateRendu(finNouveauPret);
        prolongementPretRepository.save(demande);
        
        // Incrémenter le quota utilisé seulement si pas illimité
        if (quotaProlongement > 0) {
            adherent.setDemandesProlongementUtilisees(adherent.getDemandesProlongementUtilisees() + 1);
            adherentRepository.save(adherent);
        }
        
        return "redirect:/prolongements?success=valide&jours=" + joursProlongation + "&nouvelleDate=" + finNouveauPret + "&debutPret=" + debutNouveauPret;
    }

    @PostMapping("/{id}/refuser")
    public String refuserDemande(@PathVariable Long id) {
        ProlongementPret demande = prolongementPretRepository.findById(id).orElseThrow();
        demande.setEtat("refuse");
        prolongementPretRepository.save(demande);
        return "redirect:/prolongements?success=refuse";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimerDemande(@PathVariable Long id) {
        try {
            ProlongementPret demande = prolongementPretRepository.findById(id).orElseThrow();
            prolongementPretRepository.delete(demande);
            return "redirect:/prolongements?success=supprime";
        } catch (Exception e) {
            return "redirect:/prolongements?error=suppression_echoue";
        }
    }
} 