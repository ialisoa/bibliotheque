package com.biblio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class PagesController {
    private static final Logger logger = LoggerFactory.getLogger(PagesController.class);
    
    @GetMapping("/recherche")
    public String recherche() {
        logger.info("Accès à la page recherche");
        return "pages/recherche";
    }
} 