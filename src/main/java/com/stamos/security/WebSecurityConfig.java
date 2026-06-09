package com.stamos.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/home", "/public/**").permitAll()
                .requestMatchers("/servicios", "/galeria", "/contacto").permitAll()
                .requestMatchers("/public/login", "/login", "/logout-success").permitAll()
                .requestMatchers("/olvide-contrasena", "/resetear-contrasena").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/static/**", "/assets/**").permitAll()
                .requestMatchers("/uploads/**", "/files/**").permitAll()
                .requestMatchers("/error/**", "/error").permitAll()
                .requestMatchers("/favicon.ico").permitAll()
                .requestMatchers("/public/cliente/**").hasRole("CLIENTE")
                .requestMatchers("/internal/**").hasAnyRole("ADMINISTRADOR", "COMERCIAL", "DISENADOR", "PRODUCCION", "SOPORTE_TECNICO")
                .requestMatchers("/api/**").authenticated()
                
                .anyRequest().authenticated()
            )
            
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(successHandler())
                .failureUrl("/login?error=true")
                .permitAll()
            )
            
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/public/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            
            .rememberMe(remember -> remember
                .key("stamosSecretKey")
                .tokenValiditySeconds(7 * 24 * 60 * 60) // 7 días
                .rememberMeParameter("remember-me")
                .userDetailsService(userDetailsService)
            )
            
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/error/403")
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendRedirect("/public/login");
                })
            )
            
            .sessionManagement(session -> session
                .maximumSessions(1)
                .expiredUrl("/public/login?expired=true")
            );
        
        return http.build();
    }
    
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            String redirectUrl = "/dashboard";
            
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(granted -> granted.getAuthority().equals("ROLE_ADMINISTRADOR"));
            
            boolean isComercial = authentication.getAuthorities().stream()
                    .anyMatch(granted -> granted.getAuthority().equals("ROLE_COMERCIAL"));
            
            boolean isDisenador = authentication.getAuthorities().stream()
                    .anyMatch(granted -> granted.getAuthority().equals("ROLE_DISENADOR"));
            
            boolean isSoporte = authentication.getAuthorities().stream()
                    .anyMatch(granted -> granted.getAuthority().equals("ROLE_SOPORTE_TECNICO"));
            
            boolean isProduccion = authentication.getAuthorities().stream()
                    .anyMatch(granted -> granted.getAuthority().equals("ROLE_PRODUCCION"));
            
            boolean isClient = authentication.getAuthorities().stream()
                    .anyMatch(granted -> granted.getAuthority().equals("ROLE_CLIENTE"));
            
            if (isAdmin || isComercial || isDisenador || isSoporte || isProduccion) {
                redirectUrl = "/dashboard";
            } else if (isClient) {
                redirectUrl = "/public/cliente/dashboard";
            }
            String targetUrl = redirectUrl;
            response.sendRedirect(targetUrl);
        };
    }
}