package com.biblio.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class ProlongementPret {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProlongement;

    @ManyToOne
    @JoinColumn(name = "id_pret")
    private Pret pret;

    private LocalDate dateDemande;
    private LocalDate nouvelleDateRendu;
    private String etat;

    // Getters et setters
    public Long getIdProlongement() { return idProlongement; }
    public void setIdProlongement(Long idProlongement) { this.idProlongement = idProlongement; }
    public Pret getPret() { return pret; }
    public void setPret(Pret pret) { this.pret = pret; }
    public LocalDate getDateDemande() { return dateDemande; }
    public void setDateDemande(LocalDate dateDemande) { this.dateDemande = dateDemande; }
    public LocalDate getNouvelleDateRendu() { return nouvelleDateRendu; }
    public void setNouvelleDateRendu(LocalDate nouvelleDateRendu) { this.nouvelleDateRendu = nouvelleDateRendu; }
    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }
} 