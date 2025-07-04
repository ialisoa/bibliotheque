package com.biblio.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        for (GrantedAuthority auth : authentication.getAuthorities()) {
            if ("ROLE_ADMIN".equals(auth.getAuthority())) {
                response.sendRedirect("/accueil"); // ou "/admin/dashboard" selon ton mapping
                return;
            } else if ("ROLE_ADHERENT".equals(auth.getAuthority())) {
                response.sendRedirect("/user/dashboard");
                return;
            }
        }
        // Par défaut, si aucun rôle ne correspond
        response.sendRedirect("/login?error");
    }
} 