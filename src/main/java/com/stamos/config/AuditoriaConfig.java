package com.stamos.config;

import com.stamos.entity.Usuario;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditoriaConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            try {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                
                if (auth == null || !auth.isAuthenticated()) {
                    return Optional.of("SISTEMA");
                }
                
                Object principal = auth.getPrincipal();
                if ("anonymousUser".equals(principal)) {
                    return Optional.of("SISTEMA");
                }
                
                if (principal instanceof Usuario usuario) {
                    String email = usuario.getEmail();
                    return Optional.of(email != null ? email : "SISTEMA");
                }
                
                String name = auth.getName();
                return Optional.of(name != null ? name : "SISTEMA");
                
            } catch (Exception e) {
                return Optional.of("SISTEMA");
            }
        };
    }
}