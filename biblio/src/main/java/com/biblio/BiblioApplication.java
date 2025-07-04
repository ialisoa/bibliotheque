package com.biblio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import com.biblio.repository.AdministrateurRepository;

@SpringBootApplication
public class BiblioApplication {
    public static void main(String[] args) {
        SpringApplication.run(BiblioApplication.class, args);
    }

    @Bean
    CommandLineRunner testDatabase(AdministrateurRepository administrateurRepository) {
        return args -> {
            System.out.println("=== Test de la base de données ===");
            System.out.println("Nombre d'administrateurs: " + administrateurRepository.count());
            administrateurRepository.findAll().forEach(admin -> {
                System.out.println("Admin trouvé: " + admin.getLogin() + " - Mot de passe: " + admin.getMotDePasse());
            });
            System.out.println("=== Fin du test ===");
        };
    }
} 