package com.biblio.controller;

import com.biblio.model.Adherent;
import com.biblio.repository.AdherentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/compte")
public class CompteController {

    @Autowired
    private AdherentRepository adherentRepository;

    @GetMapping("/creer")
    public String formulaireCreationCompte(Model model) {
        model.addAttribute("adherent", new Adherent());
        return "compte/creer";
    }

    @PostMapping("/creer")
    public String creerCompte(@ModelAttribute Adherent adherent, 
                             @RequestParam String nom,
                             @RequestParam String prenom,
                             @RequestParam String email,
                             @RequestParam String login,
                             @RequestParam String motDePasse,
                             @RequestParam String confirmerMotDePasse,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        
        try {
            // Vérifier que les mots de passe correspondent
            if (!motDePasse.equals(confirmerMotDePasse)) {
                model.addAttribute("error", "Les mots de passe ne correspondent pas.");
                model.addAttribute("adherent", adherent);
                return "compte/creer";
            }

            // Vérifier que le login n'existe pas déjà
            Optional<Adherent> adherentExistant = adherentRepository.findByLogin(login);
            if (adherentExistant.isPresent()) {
                model.addAttribute("error", "Ce nom d'utilisateur est déjà utilisé.");
                model.addAttribute("adherent", adherent);
                return "compte/creer";
            }

            // Rechercher l'adhérent par nom, prénom et email
            Adherent adherentTrouve = adherentRepository.findByNomAndPrenomAndEmail(nom, prenom, email);
            if (adherentTrouve == null) {
                model.addAttribute("error", "Aucun adhérent trouvé avec ces informations. Vérifiez vos données ou contactez l'administrateur.");
                model.addAttribute("adherent", adherent);
                return "compte/creer";
            }

            // Vérifier que l'adhérent n'a pas déjà un compte
            if (adherentTrouve.getLogin() != null && !adherentTrouve.getLogin().isEmpty()) {
                model.addAttribute("error", "Un compte existe déjà pour cet adhérent.");
                model.addAttribute("adherent", adherent);
                return "compte/creer";
            }

            // Mettre à jour l'adhérent avec le login choisi et le mot de passe
            adherentTrouve.setLogin(login);
            adherentTrouve.setMotDePasse(motDePasse); // En production, il faudrait hasher le mot de passe
            adherentRepository.save(adherentTrouve);

            redirectAttributes.addFlashAttribute("success", "Compte créé avec succès ! Vous pouvez maintenant vous connecter.");
            return "redirect:/login?success=compte_creer";

        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la création du compte : " + e.getMessage());
            model.addAttribute("adherent", adherent);
            return "compte/creer";
        }
    }
} 