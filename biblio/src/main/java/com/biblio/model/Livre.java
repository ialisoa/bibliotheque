package com.biblio.model;

import jakarta.persistence.*;

@Entity
@Table(name = "livre")
public class Livre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLivre;

    private String titre;
    private String isbn;
    private String edition;
    private String auteur;
    private Integer anneePublication;
    private Integer nombreExemplaires;
    private String langue;
    private Integer nombrePages;
    @Column(columnDefinition = "TEXT")
    private String cv;
    private Integer ageMinimum;
    
    // Nouveaux champs
    private String editeur;
    private String categorie;
    private String statut;
    private Double prix;
    @Column(columnDefinition = "TEXT")
    private String resume;

    // Getters et setters
    public Long getIdLivre() { return idLivre; }
    public void setIdLivre(Long idLivre) { this.idLivre = idLivre; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getEdition() { return edition; }
    public void setEdition(String edition) { this.edition = edition; }
    public String getAuteur() { return auteur; }
    public void setAuteur(String auteur) { this.auteur = auteur; }
    public Integer getAnneePublication() { return anneePublication; }
    public void setAnneePublication(Integer anneePublication) { this.anneePublication = anneePublication; }
    public Integer getNombreExemplaires() { return nombreExemplaires; }
    public void setNombreExemplaires(Integer nombreExemplaires) { this.nombreExemplaires = nombreExemplaires; }
    public String getLangue() { return langue; }
    public void setLangue(String langue) { this.langue = langue; }
    public Integer getNombrePages() { return nombrePages; }
    public void setNombrePages(Integer nombrePages) { this.nombrePages = nombrePages; }
    public String getCv() { return cv; }
    public void setCv(String cv) { this.cv = cv; }
    public Integer getAgeMinimum() { return ageMinimum; }
    public void setAgeMinimum(Integer ageMinimum) { this.ageMinimum = ageMinimum; }
    
    // Nouveaux getters et setters
    public String getEditeur() { return editeur; }
    public void setEditeur(String editeur) { this.editeur = editeur; }
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public Double getPrix() { return prix; }
    public void setPrix(Double prix) { this.prix = prix; }
    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }
} 