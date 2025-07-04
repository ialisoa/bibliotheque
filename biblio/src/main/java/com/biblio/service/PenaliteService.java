package com.biblio.service;

import com.biblio.model.*;
import com.biblio.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PenaliteService {
    @Autowired
    private PretRepository pretRepository;
    @Autowired
    private JourFerieRepository jourFerieRepository;
    @Autowired
    private PenaliteRepository penaliteRepository;
    @Autowired
    private ParametreRepository parametreRepository;

    public void verifierPenalites() {
        List<Pret> pretsEnRetard = pretRepository.findByDateRenduReelleIsNullAndDateRenduPrevueBefore(LocalDate.now());
        for (Pret pret : pretsEnRetard) {
            long joursRetard = ChronoUnit.DAYS.between(pret.getDateRenduPrevue(), LocalDate.now());
            List<JourFerie> joursFeries = jourFerieRepository.findAll();
            joursRetard -= joursFeries.stream()
                    .filter(j -> j.getDateFerie().isAfter(pret.getDateRenduPrevue()) && j.getDateFerie().isBefore(LocalDate.now()))
                    .count();
            if (joursRetard > 0) {
                Parametre parametre = parametreRepository.findByTypeAdherent(pret.getAdherent().getType());
                Penalite penalite = new Penalite();
                penalite.setPret(pret);
                penalite.setAdherent(pret.getAdherent());
                penalite.setDateDebut(LocalDate.now());
                penalite.setDateFin(LocalDate.now().plusDays(joursRetard));
                penalite.setMontant(parametre.getPenaliteParJour().multiply(new java.math.BigDecimal(joursRetard)));
                penalite.setMotif("Retard de rendu");
                penaliteRepository.save(penalite);
            }
        }
    }
} 