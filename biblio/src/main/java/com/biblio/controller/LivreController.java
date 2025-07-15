package com.biblio.controller;

import com.biblio.model.Livre;
import com.biblio.service.LivreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.biblio.model.Commentaire;
import com.biblio.repository.CommentaireRepository;
import com.biblio.service.CommentaireService;
import java.time.LocalDate;
import java.util.Optional;

import java.util.List;
import com.biblio.repository.ExemplaireRepository;
import com.biblio.model.Exemplaire;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/livres")
public class LivreController {
    private static final Logger logger = LoggerFactory.getLogger(LivreController.class);

    @Autowired
    private LivreService livreService;
    @Autowired
    private CommentaireService commentaireService;
    @Autowired
    private CommentaireRepository commentaireRepository;
    @Autowired
    private ExemplaireRepository exemplaireRepository;

    @GetMapping
    public String listeLivres(Model model) {
        logger.info("Accès à la liste des livres");
        List<Livre> livres = livreService.getAllLivres();
        model.addAttribute("livres", livres);
        return "livres/liste";
    }

    @GetMapping("/ajouter")
    public String formAjouterLivre(Model model) {
        logger.info("Accès au formulaire d'ajout de livre");
        model.addAttribute("livre", new Livre());
        return "livres/ajouter";
    }

    @PostMapping("/ajouter")
    public String ajouterLivre(@ModelAttribute Livre livre) {
        logger.info("Tentative d'ajout du livre: {}", livre.getTitre());
        try {
            // Validation et nettoyage des données
            if (livre.getTitre() == null || livre.getTitre().trim().isEmpty()) {
                logger.error("Titre du livre manquant");
                return "redirect:/livres/ajouter?error=missing_title";
            }
            
            if (livre.getAuteur() == null || livre.getAuteur().trim().isEmpty()) {
                logger.error("Auteur du livre manquant");
                return "redirect:/livres/ajouter?error=missing_author";
            }
            
            // Nettoyer les champs
            livre.setTitre(livre.getTitre().trim());
            livre.setAuteur(livre.getAuteur().trim());
            
            // Définir des valeurs par défaut si nécessaire
            if (livre.getStatut() == null || livre.getStatut().isEmpty()) {
                livre.setStatut("disponible");
            }
            
            logger.info("Données du livre validées, sauvegarde en cours...");
            livreService.ajouterLivre(livre);
            logger.info("Livre ajouté avec succès: {}", livre.getTitre());
            return "redirect:/livres?success=true";
        } catch (Exception e) {
            logger.error("Erreur lors de l'ajout du livre: {}", e.getMessage(), e);
            return "redirect:/livres/ajouter?error=save_failed";
        }
    }

    @GetMapping("/{id}")
    public String detailLivre(@PathVariable Long id, Model model) {
        logger.info("Accès au détail du livre ID: {}", id);
        Optional<Livre> optLivre = livreService.getLivreById(id);
        if (optLivre.isPresent()) {
            Livre livre = optLivre.get();
            model.addAttribute("livre", livre);
            // Ajout des exemplaires du livre
            List<Exemplaire> exemplaires = exemplaireRepository.findByLivreIdLivre(livre.getIdLivre());
            model.addAttribute("exemplaires", exemplaires);
            // Afficher les commentaires et la moyenne
            Double moyenne = commentaireRepository.findAverageNoteByLivre(livre);
            model.addAttribute("moyenneNote", moyenne != null ? moyenne : 0.0);
            model.addAttribute("commentaires", commentaireRepository.findAll().stream().filter(c -> c.getLivre().getIdLivre().equals(id)).toList());
            model.addAttribute("nouveauCommentaire", new Commentaire());
            return "livres/detail";
        } else {
            return "redirect:/livres?error=notfound";
        }
    }

    @GetMapping("/{id}/modifier")
    public String formModifierLivre(@PathVariable Long id, Model model) {
        logger.info("Accès au formulaire de modification du livre ID: {}", id);
        return livreService.getLivreById(id)
            .map(livre -> {
                model.addAttribute("livre", livre);
                return "livres/modifier";
            })
            .orElse("redirect:/livres?error=notfound");
    }

    @PostMapping("/{id}/modifier")
    public String modifierLivre(@PathVariable Long id, @ModelAttribute Livre livre,
                                @RequestParam(value = "nomExemplaire", required = false) String nomExemplaire,
                                @RequestParam(value = "etatExemplaire", required = false) String etatExemplaire,
                                @RequestParam(value = "statutExemplaire", required = false) String statutExemplaire) {
        logger.info("Tentative de modification du livre ID: {}", id);
        try {
            livre.setIdLivre(id);
            livreService.ajouterLivre(livre);
            logger.info("Livre modifié avec succès: {}", livre.getTitre());
            // Recharger le livre depuis la base pour garantir la persistance
            Livre livreFromDb = livreService.getLivreById(id).orElse(null);
            // Ajout optionnel d'un exemplaire
            if (nomExemplaire != null && !nomExemplaire.trim().isEmpty() && livreFromDb != null) {
                Exemplaire exemplaire = new Exemplaire();
                exemplaire.setLivre(livreFromDb);
                exemplaire.setNomExemplaire(nomExemplaire.trim());
                if (etatExemplaire != null && !etatExemplaire.isEmpty()) {
                    exemplaire.setEtat(etatExemplaire);
                } else {
                    exemplaire.setEtat("bon");
                }
                if (statutExemplaire != null && !statutExemplaire.isEmpty()) {
                    exemplaire.setStatut(statutExemplaire);
                } else {
                    exemplaire.setStatut("disponible");
                }
                exemplaireRepository.save(exemplaire);
                logger.info("Exemplaire ajouté lors de la modification du livre: {}", nomExemplaire);
            }
            return "redirect:/livres?success=modified";
        } catch (Exception e) {
            logger.error("Erreur lors de la modification du livre: {}", e.getMessage());
            return "redirect:/livres/" + id + "/modifier?error=true";
        }
    }

    @PostMapping("/{id}/supprimer")
    public String supprimerLivre(@PathVariable Long id) {
        logger.info("Tentative de suppression du livre ID: {}", id);
        try {
            livreService.supprimerLivre(id);
            logger.info("Livre supprimé avec succès ID: {}", id);
            return "redirect:/livres?success=deleted";
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression du livre: {}", e.getMessage());
            return "redirect:/livres?error=delete";
        }
    }

    @GetMapping("/recherche")
    public String rechercheLivres(@RequestParam(required = false) String titre,
                                  @RequestParam(required = false) String auteur,
                                  @RequestParam(required = false) String langue,
                                  Model model) {
        List<Livre> livres = livreService.rechercherLivres(titre, auteur, langue);
        model.addAttribute("livres", livres);
        return "livres";
    }

    @PostMapping("/{id}/noter")
    public String noterLivre(@PathVariable Long id, @ModelAttribute Commentaire nouveauCommentaire) {
        Optional<Livre> optLivre = livreService.getLivreById(id);
        if (optLivre.isPresent()) {
            Livre livre = optLivre.get();
            nouveauCommentaire.setLivre(livre);
            nouveauCommentaire.setDate(LocalDate.now());
            // Pour l'admin, pas d'adherent associé
            nouveauCommentaire.setAdherent(null);
            commentaireService.ajouterCommentaire(nouveauCommentaire);
        }
        return "redirect:/livres/" + id;
    }

    @PostMapping("/{id}/ajouter-exemplaire")
    public String ajouterExemplaire(@PathVariable Long id,
                                    @RequestParam String nomExemplaire,
                                    @RequestParam String etat,
                                    @RequestParam String statut) {
        try {
            Optional<Livre> optLivre = livreService.getLivreById(id);
            if (optLivre.isPresent()) {
                Livre livre = optLivre.get();
                Exemplaire exemplaire = new Exemplaire();
                exemplaire.setLivre(livre);
                exemplaire.setNomExemplaire(nomExemplaire);
                exemplaire.setEtat(etat);
                exemplaire.setStatut(statut);
                exemplaireRepository.save(exemplaire);
            }
            return "redirect:/livres/" + id + "/modifier";
        } catch (Exception e) {
            logger.error("Erreur lors de l'ajout de l'exemplaire: {}", e.getMessage(), e);
            return "redirect:/livres/" + id + "/modifier?error=exemplaire";
        }
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> getLivreDetailsJson(@PathVariable Long id) {
        Optional<Livre> optLivre = livreService.getLivreById(id);
        if (optLivre.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Livre livre = optLivre.get();
        List<Exemplaire> exemplaires = exemplaireRepository.findByLivreIdLivre(livre.getIdLivre());
        Double moyenne = commentaireRepository.findAverageNoteByLivre(livre);
        List<Commentaire> commentaires = commentaireRepository.findAll().stream()
            .filter(c -> c.getLivre().getIdLivre().equals(id)).toList();
        Map<String, Object> result = new HashMap<>();
        result.put("idLivre", livre.getIdLivre());
        result.put("titre", livre.getTitre());
        result.put("auteur", livre.getAuteur());
        result.put("isbn", livre.getIsbn());
        result.put("edition", livre.getEdition());
        result.put("anneePublication", livre.getAnneePublication());
        result.put("nombreExemplaires", livre.getNombreExemplaires());
        result.put("langue", livre.getLangue());
        result.put("nombrePages", livre.getNombrePages());
        result.put("cv", livre.getCv());
        result.put("ageMinimum", livre.getAgeMinimum());
        result.put("editeur", livre.getEditeur());
        result.put("categorie", livre.getCategorie());
        result.put("statut", livre.getStatut());
        result.put("prix", livre.getPrix());
        result.put("resume", livre.getResume());
        result.put("genre", livre.getGenre());
        result.put("moyenneNote", moyenne != null ? moyenne : 0.0);
        result.put("exemplaires", exemplaires.stream().map(e -> Map.of(
            "idExemplaire", e.getIdExemplaire(),
            "nomExemplaire", e.getNomExemplaire(),
            "etat", e.getEtat(),
            "statut", e.getStatut()
        )).toList());
        result.put("commentaires", commentaires.stream().map(c -> Map.of(
            "note", c.getNote(),
            "texte", c.getTexte(),
            "date", c.getDate()
        )).toList());
        return ResponseEntity.ok(result);
    }
}
