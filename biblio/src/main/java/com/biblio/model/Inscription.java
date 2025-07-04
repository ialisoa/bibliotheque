package com.biblio.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Inscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInscription;

    @ManyToOne
    @JoinColumn(name = "id_adherent")
    private Adherent adherent;

    private LocalDate dateInscription;
    private String statut;

    // Getters et setters
    public Long getIdInscription() { return idInscription; }
    public void setIdInscription(Long idInscription) { this.idInscription = idInscription; }
    public Adherent getAdherent() { return adherent; }
    public void setAdherent(Adherent adherent) { this.adherent = adherent; }
    public LocalDate getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDate dateInscription) { this.dateInscription = dateInscription; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
} 