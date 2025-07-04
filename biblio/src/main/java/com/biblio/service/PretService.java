package com.biblio.service;

import com.biblio.model.*;
import com.biblio.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class PretService {
    @Autowired
    private PretRepository pretRepository;
    @Autowired
    private ExemplaireRepository exemplaireRepository;
    @Autowired
    private ParametreRepository parametreRepository;
    @Autowired
    private AdherentRepository adherentRepository;
    @Autowired
    private StatutRepository statutRepository;

    public Pret creerPret(Long idAdherent, Long idExemplaire) {
        Exemplaire exemplaire = exemplaireRepository.findById(idExemplaire)
                .orElseThrow(() -> new RuntimeException("Exemplaire non trouvé"));
        if (!"disponible".equals(exemplaire.getEtat())) {
            throw new RuntimeException("Exemplaire indisponible");
        }
        Adherent adherent = adherentRepository.findById(idAdherent)
                .orElseThrow(() -> new RuntimeException("Adhérent non trouvé"));
        Parametre parametre = parametreRepository.findByTypeAdherent(adherent.getType());
        Pret pret = new Pret();
        pret.setAdherent(adherent);
        pret.setExemplaire(exemplaire);
        pret.setDatePret(LocalDate.now());
        pret.setDateRenduPrevue(LocalDate.now().plusDays(parametre.getDureePret()));
        pret.setStatut(statutRepository.findById(1L).orElse(null)); // 1 = en_cours
        pret.setType("domicile");
        exemplaire.setEtat("emprunte");
        exemplaireRepository.save(exemplaire);
        return pretRepository.save(pret);
    }
} 