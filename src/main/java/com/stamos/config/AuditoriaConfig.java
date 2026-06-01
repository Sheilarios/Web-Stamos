package com.stamos.config;

import com.stamos.entity.Usuario;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Configuración de auditoría JPA.
 * Habilita el auditor automático para registrar fechas de creación/modificación
 * y el usuario que realizó la acción.
 * 
 * Nota: Esta configuración es complementaria a AuditoriaService, que registra
 * logs detallados de acciones críticas (valores anteriores/nuevos).
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditoriaConfig {

    /**
     * Proveedor del auditor actual (usuario autenticado).
     * Spring Data JPA llamará a este bean automáticamente al guardar entidades
     * que usen anotaciones como @CreatedBy, @LastModifiedBy, etc.
     *
     * @return AuditorAware con el email del usuario actual o "SISTEMA"
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return Optional.of("SISTEMA");
            }
            // Si el principal es un objeto Usuario (de nuestra entidad), extraemos su email
            Object principal = auth.getPrincipal();
            if (principal instanceof Usuario) {
                return Optional.of(((Usuario) principal).getEmail());
            }
            // Si es un String (email) típico de UserDetails
            return Optional.of(auth.getName());
        };
    }
}