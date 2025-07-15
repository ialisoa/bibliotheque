package com.biblio.model;

import jakarta.persistence.*;

@Entity
@Table(name = "exemplaire")
public class Exemplaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idExemplaire;

    @Column(name = "nom_exemplaire")
    private String nomExemplaire;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_livre")
    private Livre livre;

    private String etat;
    private String statut;

    // Getters et setters
    public Long getIdExemplaire() { return idExemplaire; }
    public void setIdExemplaire(Long idExemplaire) { this.idExemplaire = idExemplaire; }
    public String getNomExemplaire() { return nomExemplaire; }
    public void setNomExemplaire(String nomExemplaire) { this.nomExemplaire = nomExemplaire; }
    public Livre getLivre() { return livre; }
    public void setLivre(Livre livre) { this.livre = livre; }
    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exemplaire that = (Exemplaire) o;
        return idExemplaire != null && idExemplaire.equals(that.idExemplaire);
    }

    @Override
    public int hashCode() {
        return idExemplaire != null ? idExemplaire.hashCode() : 0;
    }
} 