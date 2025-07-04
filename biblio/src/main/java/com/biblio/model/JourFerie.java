package com.biblio.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class JourFerie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idJourFerie;

    private String nomJourFerie;
    private LocalDate dateFerie;
    private Boolean recurrent;

    // Getters et setters
    public Long getIdJourFerie() { return idJourFerie; }
    public void setIdJourFerie(Long idJourFerie) { this.idJourFerie = idJourFerie; }
    public String getNomJourFerie() { return nomJourFerie; }
    public void setNomJourFerie(String nomJourFerie) { this.nomJourFerie = nomJourFerie; }
    public LocalDate getDateFerie() { return dateFerie; }
    public void setDateFerie(LocalDate dateFerie) { this.dateFerie = dateFerie; }
    public Boolean getRecurrent() { return recurrent; }
    public void setRecurrent(Boolean recurrent) { this.recurrent = recurrent; }
} 