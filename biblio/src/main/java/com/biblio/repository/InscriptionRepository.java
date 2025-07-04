package com.biblio.repository;

import com.biblio.model.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InscriptionRepository extends JpaRepository<Inscription, Long> {
} 