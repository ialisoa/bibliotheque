package com.biblio.service;

import com.biblio.model.Livre;
import com.biblio.repository.LivreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LivreService {
    @Autowired
    private LivreRepository livreRepository;

    public Livre ajouterLivre(Livre livre) {
        return livreRepository.save(livre);
    }

    public List<Livre> rechercherLivres(String titre, String auteur, String langue) {
        return livreRepository.findByTitreContainingOrAuteurContainingOrLangue(titre, auteur, langue);
    }

    public List<Livre> getAllLivres() {
        return livreRepository.findAll();
    }

    public Optional<Livre> getLivreById(Long id) {
        return livreRepository.findById(id);
    }

    public void supprimerLivre(Long id) {
        livreRepository.deleteById(id);
    }
} 