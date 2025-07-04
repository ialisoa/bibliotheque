package com.biblio.repository;

import com.biblio.model.Parametre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParametreRepository extends JpaRepository<Parametre, Long> {
    Parametre findByTypeAdherent(String typeAdherent);
} 