package com.biblio.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "adherent")
public class Adherent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAdherent;

    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private LocalDate dateNaissance;
    private String sexe;
    private String adresse;
    private String codePostal;
    private String ville;
    private String pays;
    private String profession;
    private String statut;
    private LocalDate dateInscription;
    private LocalDate dateExpiration;
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    // Champs existants
    private String type;
    @Column(unique = true)
    private String login;
    @Column(name = "mot_de_passe")
    private String motDePasse;

    // Nouveaux champs
    @Column(name = "quota_prolongement")
    private Integer quotaProlongement = 3; // Valeur par défaut

    @Column(name = "date_renouvellement_quota")
    private LocalDate dateRenouvellementQuota;

    @Column(name = "demandes_prolongement_utilisees")
    private Integer demandesProlongementUtilisees = 0;

    // Getters et setters
    public Long getIdAdherent() { return idAdherent; }
    public void setIdAdherent(Long idAdherent) { this.idAdherent = idAdherent; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    
    // Nouveaux getters et setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getCodePostal() { return codePostal; }
    public void setCodePostal(String codePostal) { this.codePostal = codePostal; }
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }
    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public LocalDate getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDate dateInscription) { this.dateInscription = dateInscription; }
    public LocalDate getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDate dateExpiration) { this.dateExpiration = dateExpiration; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    // Getters et setters pour le quota de prolongement
    public Integer getQuotaProlongement() { return quotaProlongement; }
    public void setQuotaProlongement(Integer quotaProlongement) { this.quotaProlongement = quotaProlongement; }

    public LocalDate getDateRenouvellementQuota() { return dateRenouvellementQuota; }
    public void setDateRenouvellementQuota(LocalDate dateRenouvellementQuota) { this.dateRenouvellementQuota = dateRenouvellementQuota; }

    public Integer getDemandesProlongementUtilisees() { return demandesProlongementUtilisees; }
    public void setDemandesProlongementUtilisees(Integer demandesProlongementUtilisees) { this.demandesProlongementUtilisees = demandesProlongementUtilisees; }
} 