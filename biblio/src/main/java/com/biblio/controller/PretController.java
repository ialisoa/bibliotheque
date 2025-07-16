package com.biblio.controller;

import com.biblio.model.Pret;
import com.biblio.model.Adherent;
import com.biblio.model.Exemplaire;
import com.biblio.model.Statut;
import com.biblio.repository.PretRepository;
import com.biblio.repository.AdherentRepository;
import com.biblio.repository.ExemplaireRepository;
import com.biblio.repository.StatutRepository;
import com.biblio.repository.LivreRepository;
import com.biblio.repository.PenaliteRepository;
import com.biblio.model.Penalite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import java.beans.PropertyEditorSupport;

import java.util.List;

@Controller
@RequestMapping("/prets")
public class PretController {
    private static final Logger logger = LoggerFactory.getLogger(PretController.class);

    @Autowired
    private PretRepository pretRepository;

    @Autowired
    private AdherentRepository adherentRepository;

    @Autowired
    private ExemplaireRepository exemplaireRepository;

    @Autowired
    private StatutRepository statutRepository;

    @Autowired
    private LivreRepository livreRepository;

    @Autowired
    private PenaliteRepository penaliteRepository;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Exemplaire.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null || text.isEmpty()) {
                    setValue(null);
                } else {
                    setValue(exemplaireRepository.findById(Long.valueOf(text)).orElse(null));
                }
            }
        });
    }

    @GetMapping
    public String listePrets(Model model, 
                           @RequestParam(required = false) String adherent,
                           @RequestParam(required = false) String livre,
                           @RequestParam(required = false) String statut) {
        logger.info("Accès à la liste des prêts avec filtres - Adhérent: {}, Livre: {}, Statut: {}", adherent, livre, statut);
        try {
            List<Pret> prets;
            
            // Appliquer les filtres si fournis
            if (adherent != null && !adherent.trim().isEmpty()) {
                prets = pretRepository.findByAdherentNomContainingIgnoreCase(adherent.trim());
                logger.info("Filtrage par adhérent '{}' - {} prêts trouvés", adherent, prets.size());
            } else if (livre != null && !livre.trim().isEmpty()) {
                prets = pretRepository.findByExemplaireLivreTitreContainingIgnoreCase(livre.trim());
                logger.info("Filtrage par livre '{}' - {} prêts trouvés", livre, prets.size());
            } else {
                prets = pretRepository.findAll();
                logger.info("Aucun filtre - {} prêts trouvés", prets.size());
            }
            
            // Filtrer par statut si spécifié
            if (statut != null && !statut.trim().isEmpty()) {
                prets = prets.stream()
                    .filter(pret -> {
                        if ("retourne".equals(statut)) {
                            return pret.getDateRenduReelle() != null;
                        } else if ("en_cours".equals(statut)) {
                            return pret.getDateRenduReelle() == null && 
                                   !pret.getDateRenduPrevue().isBefore(java.time.LocalDate.now());
                        } else if ("en_retard".equals(statut)) {
                            return pret.getDateRenduReelle() == null && 
                                   pret.getDateRenduPrevue().isBefore(java.time.LocalDate.now());
                        }
                        return true;
                    })
                    .collect(java.util.stream.Collectors.toList());
                logger.info("Filtrage par statut '{}' - {} prêts restants", statut, prets.size());
            }
            
            // Log des détails de chaque prêt pour débogage
            for (Pret pret : prets) {
                logger.info("Prêt ID: {}, Adhérent: {}, Exemplaire: {}, Statut: {}", 
                    pret.getIdPret(),
                    pret.getAdherent() != null ? pret.getAdherent().getNom() : "NULL",
                    pret.getExemplaire() != null ? pret.getExemplaire().getIdExemplaire() : "NULL",
                    pret.getStatut() != null ? pret.getStatut().getNom() : "NULL"
                );
            }
            
            model.addAttribute("prets", prets);
            return "prets/liste";
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des prêts : {}", e.getMessage(), e);
            model.addAttribute("error", "Erreur lors du chargement des prêts : " + e.getMessage());
            model.addAttribute("prets", List.of());
            return "prets/liste";
        }
    }

    @GetMapping("/ajouter")
    public String formAjouterPret(Model model) {
        logger.info("Accès au formulaire d'ajout de prêt");
        
        try {
            // Récupérer tous les adhérents actifs
            List<Adherent> adherents = adherentRepository.findByStatut("actif");
            logger.info("Nombre d'adhérents actifs trouvés : {}", adherents.size());
            
            // Récupérer seulement les exemplaires disponibles
            List<Exemplaire> exemplaires = exemplaireRepository.findByStatut("disponible");
            logger.info("Nombre d'exemplaires disponibles trouvés : {}", exemplaires.size());
            
            // Si aucun exemplaire disponible, créer des exemplaires pour les livres existants
            if (exemplaires.isEmpty()) {
                logger.info("Aucun exemplaire disponible trouvé, création d'exemplaires automatique...");
                List<com.biblio.model.Livre> livres = livreRepository.findAll();
                for (com.biblio.model.Livre livre : livres) {
                    // Créer 2 exemplaires par livre
                    for (int i = 1; i <= 2; i++) {
                        Exemplaire exemplaire = new Exemplaire();
                        exemplaire.setNomExemplaire("Exemplaire " + i + " - " + livre.getTitre());
                        exemplaire.setLivre(livre);
                        exemplaire.setEtat("bon");
                        exemplaire.setStatut("disponible");
                        exemplaireRepository.save(exemplaire);
                        logger.info("Exemplaire créé pour le livre: {} - Exemplaire: {}", livre.getTitre(), exemplaire.getNomExemplaire());
                    }
                }
                // Récupérer à nouveau les exemplaires disponibles
                exemplaires = exemplaireRepository.findByStatut("disponible");
                logger.info("Nombre d'exemplaires disponibles après création : {}", exemplaires.size());
            }
            
            // Récupérer tous les statuts
            List<Statut> statuts = statutRepository.findAll();
            logger.info("Nombre de statuts trouvés : {}", statuts.size());
            
            // Si aucun statut trouvé, créer les statuts de base
            if (statuts.isEmpty()) {
                logger.info("Aucun statut trouvé, création des statuts de base...");
                Statut statut1 = new Statut();
                statut1.setNom("en_cours");
                statutRepository.save(statut1);
                
                Statut statut2 = new Statut();
                statut2.setNom("valide");
                statutRepository.save(statut2);
                
                Statut statut3 = new Statut();
                statut3.setNom("refuse");
                statutRepository.save(statut3);
                
                statuts = statutRepository.findAll();
                logger.info("Nombre de statuts après création : {}", statuts.size());
            }
            
            model.addAttribute("pret", new Pret());
            model.addAttribute("adherents", adherents);
            model.addAttribute("exemplaires", exemplaires);
            model.addAttribute("statuts", statuts);
            
            return "prets/ajouter";
        } catch (Exception e) {
            logger.error("Erreur lors du chargement du formulaire d'ajout : {}", e.getMessage(), e);
            model.addAttribute("error", "Erreur lors du chargement du formulaire : " + e.getMessage());
            return "prets/ajouter";
        }
    }

    @PostMapping("/ajouter")
    public String ajouterPret(@ModelAttribute Pret pret) {
        logger.info("Tentative d'ajout du prêt pour l'adhérent ID: {}", 
            pret.getAdherent() != null ? pret.getAdherent().getIdAdherent() : "NULL");
        try {
            // Mettre à jour l'état de l'exemplaire à "emprunté"
            if (pret.getExemplaire() != null) {
                Exemplaire exemplaire = exemplaireRepository.findById(pret.getExemplaire().getIdExemplaire()).orElseThrow();
                exemplaire.setEtat("emprunte");
                exemplaireRepository.save(exemplaire);
                logger.info("État de l'exemplaire {} mis à jour à 'emprunté'", exemplaire.getIdExemplaire());
            }
            
            pretRepository.save(pret);
            logger.info("Prêt ajouté avec succès ID: {}", pret.getIdPret());
            return "redirect:/prets?success=true";
        } catch (Exception e) {
            logger.error("Erreur lors de l'ajout du prêt: {}", e.getMessage(), e);
            return "redirect:/prets/ajouter?error=true";
        }
    }

    @GetMapping("/{id}")
    public String detailPret(@PathVariable Long id, Model model) {
        logger.info("Accès au détail du prêt ID: {}", id);
        return pretRepository.findById(id)
            .map(pret -> {
                model.addAttribute("pret", pret);
                return "prets/detail";
            })
            .orElse("redirect:/prets?error=notfound");
    }

    @GetMapping("/{id}/modifier")
    public String formModifierPret(@PathVariable Long id, Model model) {
        logger.info("Accès au formulaire de modification du prêt ID: {}", id);
        try {
            Pret pret = pretRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prêt non trouvé avec l'ID: " + id));
            
            // Récupérer tous les adhérents actifs
            List<Adherent> adherents = adherentRepository.findByStatut("actif");
            logger.info("Nombre d'adhérents actifs trouvés : {}", adherents.size());
            
            // Récupérer tous les exemplaires (disponibles et empruntés)
            List<Exemplaire> exemplaires = exemplaireRepository.findAll();
            logger.info("Nombre d'exemplaires trouvés : {}", exemplaires.size());
            
            // Récupérer tous les statuts
            List<Statut> statuts = statutRepository.findAll();
            logger.info("Nombre de statuts trouvés : {}", statuts.size());
            
            model.addAttribute("pret", pret);
            model.addAttribute("adherents", adherents);
            model.addAttribute("exemplaires", exemplaires);
            model.addAttribute("statuts", statuts);
            
            return "prets/modifier";
        } catch (Exception e) {
            logger.error("Erreur lors du chargement du formulaire de modification : {}", e.getMessage(), e);
            return "redirect:/prets?error=load";
        }
    }

    @PostMapping("/{id}/modifier")
    public String modifierPret(@PathVariable Long id, 
                              @RequestParam(required = false) Long adherentId,
                              @RequestParam(required = false) Long exemplaireId,
                              @RequestParam(required = false) Long statutId,
                              @ModelAttribute Pret pret) {
        logger.info("Tentative de modification du prêt ID: {}", id);
        try {
            Pret pretExistant = pretRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prêt non trouvé avec l'ID: " + id));
            
            // Mettre à jour l'adhérent si fourni
            if (adherentId != null) {
                Adherent adherent = adherentRepository.findById(adherentId)
                    .orElseThrow(() -> new RuntimeException("Adhérent non trouvé avec l'ID: " + adherentId));
                pretExistant.setAdherent(adherent);
            }
            
            // Mettre à jour l'exemplaire si fourni
            if (exemplaireId != null) {
                Exemplaire exemplaire = exemplaireRepository.findById(exemplaireId)
                    .orElseThrow(() -> new RuntimeException("Exemplaire non trouvé avec l'ID: " + exemplaireId));
                pretExistant.setExemplaire(exemplaire);
            }
            
            // Mettre à jour le statut si fourni
            if (statutId != null) {
                Statut statut = statutRepository.findById(statutId)
                    .orElseThrow(() -> new RuntimeException("Statut non trouvé avec l'ID: " + statutId));
                pretExistant.setStatut(statut);
            }
            
            // Mettre à jour les autres champs
            pretExistant.setDatePret(pret.getDatePret());
            pretExistant.setDateRenduPrevue(pret.getDateRenduPrevue());
            pretExistant.setDateRenduReelle(pret.getDateRenduReelle());
            pretExistant.setType(pret.getType());
            
            pretRepository.save(pretExistant);
            logger.info("Prêt modifié avec succès ID: {}", id);
            return "redirect:/prets?success=modified";
        } catch (Exception e) {
            logger.error("Erreur lors de la modification du prêt: {}", e.getMessage(), e);
            return "redirect:/prets?error=modify";
        }
    }

    @PostMapping("/{id}/retourner")
    public String retournerPret(@PathVariable Long id, @RequestParam(value = "dateRenduReelle", required = false) String dateRenduReelleStr) {
        logger.info("Tentative de retour du prêt ID: {}", id);
        try {
            Pret pret = pretRepository.findById(id).orElseThrow();
            java.time.LocalDate dateRenduReelle = dateRenduReelleStr != null && !dateRenduReelleStr.isEmpty()
                ? java.time.LocalDate.parse(dateRenduReelleStr)
                : java.time.LocalDate.now();
            pret.setDateRenduReelle(dateRenduReelle);
            pretRepository.save(pret);
            // Mettre à jour l'état de l'exemplaire à "disponible"
            if (pret.getExemplaire() != null) {
                Exemplaire exemplaire = exemplaireRepository.findById(pret.getExemplaire().getIdExemplaire()).orElseThrow();
                exemplaire.setStatut("disponible");
                exemplaireRepository.save(exemplaire);
                logger.info("État de l'exemplaire {} mis à jour à 'disponible'", exemplaire.getIdExemplaire());
            }
            // Création de la pénalité si retard
            if (pret.getDateRenduPrevue() != null && dateRenduReelle.isAfter(pret.getDateRenduPrevue())) {
                Adherent adherent = pret.getAdherent();
                int nbJoursPenalite = 0;
                String type = adherent.getType() != null ? adherent.getType().toLowerCase() : "";
                switch (type) {
                    case "etudiant": nbJoursPenalite = 10; break;
                    case "enseignant": nbJoursPenalite = 9; break;
                    case "anonyme": nbJoursPenalite = 8; break;
                    default: nbJoursPenalite = 7; // valeur par défaut
                }
                Penalite penalite = new Penalite();
                penalite.setPret(pret);
                penalite.setAdherent(adherent);
                penalite.setDateDebut(dateRenduReelle);
                penalite.setDateFin(dateRenduReelle.plusDays(nbJoursPenalite));
                penalite.setMotif("Retard de retour de livre");
                penalite.setMontant(java.math.BigDecimal.ZERO); // ou autre logique
                penaliteRepository.save(penalite);
                logger.info("Pénalité créée pour l'adhérent {} ({} jours)", adherent.getNom(), nbJoursPenalite);
            }
            logger.info("Prêt retourné avec succès ID: {}", id);
            return "redirect:/prets?success=returned";
        } catch (Exception e) {
            logger.error("Erreur lors du retour du prêt: {}", e.getMessage(), e);
            return "redirect:/prets?error=return";
        }
    }

    @PostMapping("/{id}/prolonger")
    public String prolongerPret(@PathVariable Long id, @RequestParam("nouvelleDateRendu") String nouvelleDateRenduStr) {
        logger.info("Tentative de prolongation du prêt ID: {}", id);
        try {
            Pret pret = pretRepository.findById(id).orElseThrow();
            java.time.LocalDate dateFinPretBase = pret.getDateRenduPrevue();
            java.time.LocalDate nouvelleDateRendu = java.time.LocalDate.parse(nouvelleDateRenduStr);
            // Créer un nouveau prêt pour le prolongement
            Pret prolongement = new Pret();
            prolongement.setAdherent(pret.getAdherent());
            prolongement.setExemplaire(pret.getExemplaire());
            prolongement.setType("prolongation");
            prolongement.setStatut(pret.getStatut());
            prolongement.setDatePret(dateFinPretBase);
            prolongement.setDateRenduPrevue(nouvelleDateRendu);
            pretRepository.save(prolongement);
            logger.info("Prolongement créé pour le prêt ID: {} (nouvelle date: {})", id, nouvelleDateRendu);
            return "redirect:/prets?success=extended";
        } catch (Exception e) {
            logger.error("Erreur lors de la prolongation du prêt: {}", e.getMessage(), e);
            return "redirect:/prets?error=extend";
        }
    }

    @PostMapping("/{id}/supprimer")
    public String supprimerPret(@PathVariable Long id) {
        logger.info("Tentative de suppression du prêt ID: {}", id);
        try {
            pretRepository.deleteById(id);
            logger.info("Prêt supprimé avec succès ID: {}", id);
            return "redirect:/prets?success=deleted";
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression du prêt: {}", e.getMessage(), e);
            return "redirect:/prets?error=delete";
        }
    }

    @PostMapping("/{id}/valider")
    public String validerPret(@PathVariable Long id) {
        Pret pret = pretRepository.findById(id).orElse(null);
        if (pret != null && pret.getStatut() != null && "en_attente".equals(pret.getStatut().getNom())) {
            Statut valide = statutRepository.findByNom("valide");
            pret.setStatut(valide);
            pretRepository.save(pret);
        }
        return "redirect:/prets";
    }

    @PostMapping("/{id}/refuser")
    public String refuserPret(@PathVariable Long id) {
        Pret pret = pretRepository.findById(id).orElse(null);
        if (pret != null && pret.getStatut() != null && "en_attente".equals(pret.getStatut().getNom())) {
            Statut refuse = statutRepository.findByNom("refuse");
            pret.setStatut(refuse);
            pretRepository.save(pret);
        }
        return "redirect:/prets";
    }
}
