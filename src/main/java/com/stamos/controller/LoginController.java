package com.stamos.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    
    @GetMapping("/dashboard")
    public String redirectAfterLogin(Authentication auth) {
        if (auth == null) {
            return "redirect:/login";
        }
        
        boolean isAdmin = auth.getAuthorities().stream()
            .anyMatch(granted -> granted.getAuthority().equals("ROLE_ADMINISTRADOR"));
        
        boolean isComercial = auth.getAuthorities().stream()
            .anyMatch(granted -> granted.getAuthority().equals("ROLE_COMERCIAL"));
        
        boolean isDisenador = auth.getAuthorities().stream()
            .anyMatch(granted -> granted.getAuthority().equals("ROLE_DISENADOR"));
        
        boolean isProduccion = auth.getAuthorities().stream()
            .anyMatch(granted -> granted.getAuthority().equals("ROLE_PRODUCCION"));
        
        boolean isSoporte = auth.getAuthorities().stream()
            .anyMatch(granted -> granted.getAuthority().equals("ROLE_SOPORTE_TECNICO"));
        
        boolean isClient = auth.getAuthorities().stream()
            .anyMatch(granted -> granted.getAuthority().equals("ROLE_CLIENTE"));
        
        if (isAdmin || isComercial || isDisenador || isProduccion || isSoporte) {
            return "redirect:/internal/dashboard";
        } else if (isClient) {
            return "redirect:/public/cliente/dashboard";
        }
        
        return "redirect:/login?error=true";
    }
}