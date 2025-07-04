package com.biblio.repository;

import com.biblio.model.Statut;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatutRepository extends JpaRepository<Statut, Long> {
    Statut findByNom(String nom);
} 