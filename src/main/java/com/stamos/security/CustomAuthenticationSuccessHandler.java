package com.stamos.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String redirectUrl = determineTargetUrl(authentication);
        response.sendRedirect(redirectUrl);
    }

    private String determineTargetUrl(Authentication authentication) {
        // Roles de empleados (sistema interno)
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))) {
            return "/internal/dashboard/admin";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_COMERCIAL"))) {
            return "/internal/dashboard/comercial";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_DISENADOR"))) {
            return "/internal/dashboard/disenador";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PRODUCCION"))) {
            return "/internal/dashboard/produccion";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SOPORTE_TECNICO"))) {
            return "/internal/dashboard/soporte";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CLIENTE"))) {
            return "/public/cliente/dashboard";
        }
        return "/login?error=true";
    }
}