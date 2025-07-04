package com.biblio.model;

import jakarta.persistence.*;

@Entity
public class Statut {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idStatut;

    private String nom;

    // Getters et setters
    public Long getIdStatut() { return idStatut; }
    public void setIdStatut(Long idStatut) { this.idStatut = idStatut; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
} 