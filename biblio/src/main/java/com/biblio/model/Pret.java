package com.biblio.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "pret")
public class Pret {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPret;

    private String type;
    
    @Column(name = "date_pret")
    private LocalDate datePret;
    
    @Column(name = "date_rendu_prevue")
    private LocalDate dateRenduPrevue;
    
    @Column(name = "date_rendu_reelle")
    private LocalDate dateRenduReelle;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_adherent")
    private Adherent adherent;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_exemplaire")
    private Exemplaire exemplaire;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "statut")
    private Statut statut;

    // Getters et setters
    public Long getIdPret() { return idPret; }
    public void setIdPret(Long idPret) { this.idPret = idPret; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public LocalDate getDatePret() { return datePret; }
    public void setDatePret(LocalDate datePret) { this.datePret = datePret; }
    
    public LocalDate getDateRenduPrevue() { return dateRenduPrevue; }
    public void setDateRenduPrevue(LocalDate dateRenduPrevue) { this.dateRenduPrevue = dateRenduPrevue; }
    
    public LocalDate getDateRenduReelle() { return dateRenduReelle; }
    public void setDateRenduReelle(LocalDate dateRenduReelle) { this.dateRenduReelle = dateRenduReelle; }
    
    public Adherent getAdherent() { return adherent; }
    public void setAdherent(Adherent adherent) { this.adherent = adherent; }
    
    public Exemplaire getExemplaire() { return exemplaire; }
    public void setExemplaire(Exemplaire exemplaire) { this.exemplaire = exemplaire; }
    
    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }
    
    // Méthodes utilitaires pour le template
    public LocalDate getDateEmprunt() { return datePret; }
    public void setDateEmprunt(LocalDate dateEmprunt) { this.datePret = dateEmprunt; }
} 