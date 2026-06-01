package com.stamos.security;

import com.stamos.security.CustomAuthenticationSuccessHandler;
import com.stamos.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;

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
                // === RUTAS PÚBLICAS (sin autenticación) ===
                .requestMatchers("/", "/home").permitAll()  // ← IMPORTANTE: permitir raíz
                .requestMatchers("/servicios", "/galeria", "/contacto").permitAll()
                .requestMatchers("/login", "/logout-success").permitAll()
                .requestMatchers("/olvide-contrasena", "/resetear-contrasena").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                .requestMatchers("/uploads/**").permitAll()
                .requestMatchers("/error/**").permitAll()
                
                // === RUTAS PROTEGIDAS ===
                .requestMatchers("/public/cliente/**").hasRole("CLIENTE")
                .requestMatchers("/internal/**").hasAnyRole("ADMINISTRADOR", "COMERCIAL", "DISENADOR", "PRODUCCION", "SOPORTE_TECNICO")
                
                .anyRequest().authenticated()
            )
            
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(successHandler)
                .permitAll()
            )
            
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/error/403")
            );
        
        return http.build();
    }
}