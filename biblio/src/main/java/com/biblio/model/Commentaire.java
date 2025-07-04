package com.biblio.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Commentaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int note; // de 1 à 5

    @Column(length = 1000)
    private String texte;

    @ManyToOne
    @JoinColumn(name = "livre_id")
    private Livre livre;

    @ManyToOne
    @JoinColumn(name = "adherent_id")
    private Adherent adherent;

    private LocalDate date;

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getNote() { return note; }
    public void setNote(int note) { this.note = note; }

    public String getTexte() { return texte; }
    public void setTexte(String texte) { this.texte = texte; }

    public Livre getLivre() { return livre; }
    public void setLivre(Livre livre) { this.livre = livre; }

    public Adherent getAdherent() { return adherent; }
    public void setAdherent(Adherent adherent) { this.adherent = adherent; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
} 