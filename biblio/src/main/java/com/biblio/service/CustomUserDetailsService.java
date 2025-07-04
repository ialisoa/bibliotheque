package com.biblio.service;

import com.biblio.model.Administrateur;
import com.biblio.repository.AdministrateurRepository;
import com.biblio.model.Adherent;
import com.biblio.repository.AdherentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private AdministrateurRepository administrateurRepository;
    @Autowired
    private AdherentRepository adherentRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Recherche d'abord un administrateur
        Administrateur admin = administrateurRepository.findByLogin(username).orElse(null);
        if (admin != null) {
            return new User(
                admin.getLogin(),
                admin.getMotDePasse(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + admin.getRole().toUpperCase()))
            );
        }
        // Recherche un adhérent
        Adherent adherent = adherentRepository.findByLogin(username).orElse(null);
        if (adherent != null) {
            return new User(
                adherent.getLogin(),
                adherent.getMotDePasse(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADHERENT"))
            );
        }
        throw new UsernameNotFoundException("Utilisateur non trouvé");
    }
} 