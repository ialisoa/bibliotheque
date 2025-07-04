package com.biblio.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class Parametre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idParametre;

    private String typeAdherent;
    private Integer dureePret;
    private Integer quotaMax;
    private Integer ageMinimum;
    private BigDecimal penaliteParJour;

    // Getters et setters
    public Long getIdParametre() { return idParametre; }
    public void setIdParametre(Long idParametre) { this.idParametre = idParametre; }
    public String getTypeAdherent() { return typeAdherent; }
    public void setTypeAdherent(String typeAdherent) { this.typeAdherent = typeAdherent; }
    public Integer getDureePret() { return dureePret; }
    public void setDureePret(Integer dureePret) { this.dureePret = dureePret; }
    public Integer getQuotaMax() { return quotaMax; }
    public void setQuotaMax(Integer quotaMax) { this.quotaMax = quotaMax; }
    public Integer getAgeMinimum() { return ageMinimum; }
    public void setAgeMinimum(Integer ageMinimum) { this.ageMinimum = ageMinimum; }
    public BigDecimal getPenaliteParJour() { return penaliteParJour; }
    public void setPenaliteParJour(BigDecimal penaliteParJour) { this.penaliteParJour = penaliteParJour; }
}
