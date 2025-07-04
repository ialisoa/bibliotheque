package com.biblio.repository;

import com.biblio.model.Administrateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdministrateurRepository extends JpaRepository<Administrateur, Long> {
    Optional<Administrateur> findByLogin(String login);
} 