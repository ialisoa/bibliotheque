package com.biblio.service;

import com.biblio.model.Commentaire;
import com.biblio.repository.CommentaireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentaireService {
    @Autowired
    private CommentaireRepository commentaireRepository;

    public Commentaire ajouterCommentaire(Commentaire commentaire) {
        return commentaireRepository.save(commentaire);
    }
} 