package com.biblio.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReservation;

    @ManyToOne
    @JoinColumn(name = "id_adherent")
    private Adherent adherent;

    @ManyToOne
    @JoinColumn(name = "id_livre")
    private Livre livre;

    private LocalDate dateReservation;
    private LocalDate dateFinReservation;
    private String etat;

    // Getters et setters
    public Long getIdReservation() { return idReservation; }
    public void setIdReservation(Long idReservation) { this.idReservation = idReservation; }
    public Adherent getAdherent() { return adherent; }
    public void setAdherent(Adherent adherent) { this.adherent = adherent; }
    public Livre getLivre() { return livre; }
    public void setLivre(Livre livre) { this.livre = livre; }
    public LocalDate getDateReservation() { return dateReservation; }
    public void setDateReservation(LocalDate dateReservation) { this.dateReservation = dateReservation; }
    public LocalDate getDateFinReservation() { return dateFinReservation; }
    public void setDateFinReservation(LocalDate dateFinReservation) { this.dateFinReservation = dateFinReservation; }
    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }
} 