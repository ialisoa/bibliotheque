package com.biblio.repository;

import com.biblio.model.ProlongementPret;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProlongementPretRepository extends JpaRepository<ProlongementPret, Long> {
    ProlongementPret findTopByPretOrderByDateDemandeDesc(com.biblio.model.Pret pret);
} 