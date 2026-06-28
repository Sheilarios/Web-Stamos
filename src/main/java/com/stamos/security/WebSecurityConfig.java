package com.stamos.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
    securedEnabled = true,
    jsr250Enabled = true,
    prePostEnabled = true
)
public class WebSecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DataSource dataSource;

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            System.out.println("=== AUTENTICACION EXITOSA ===");
            System.out.println("Usuario: " + authentication.getName());
            System.out.println("Authorities: " + authentication.getAuthorities());
            response.sendRedirect("/login-success");
        };
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            System.out.println("=== AUTENTICACION FALLIDA ===");
            System.out.println("Error: " + exception.getMessage());
            String errorMessage = "Usuario o contrasena incorrectos";
            if (exception.getMessage() != null && exception.getMessage().contains("deshabilitado")) {
                errorMessage = "Tu cuenta esta deshabilitada. Contacta con soporte.";
            }
            request.getSession().setAttribute("error", errorMessage);
            response.sendRedirect("/login?error=true");
        };
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            System.out.println("=== CERRANDO SESION ===");
            System.out.println("Usuario: " + (authentication != null ? authentication.getName() : "desconocido"));
            response.sendRedirect("/login?logout=true");
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        // 🔧 CORREGIDO: Removido el método .and() que estaba deprecated
        AuthenticationManagerBuilder authManagerBuilder = 
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authManagerBuilder.userDetailsService(customUserDetailsService)
                          .passwordEncoder(passwordEncoder);
        return authManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                    "/",
                    "/index",
                    "/login",
                    "/login-error",
                    "/logout",
                    "/access-denied",
                    "/error",
                    "/forgot-password",
                    "/reset-password",
                    "/auth/**",
                    "/contacto",
                    "/servicios",
                    "/galeria",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/fonts/**",
                    "/webjars/**",
                    "/favicon.ico",
                    "/manifest.json",
                    "/img/**"
                ).permitAll()
                
                .requestMatchers("/productos/imagen/**").permitAll()
                .requestMatchers("/documentos/public/**").permitAll()
                
                .requestMatchers("/cliente/**").hasRole("CLIENTE")
                
                .requestMatchers("/admin/**").hasAnyRole(
                    "ADMINISTRADOR", 
                    "VENDEDOR", 
                    "DISEÑADOR", 
                    "PRODUCCION", 
                    "SOPORTE_TECNICO"
                )
                
                .requestMatchers("/admin/configuracion/**").hasRole("ADMINISTRADOR")
                
                .requestMatchers("/admin/usuarios/**").hasRole("ADMINISTRADOR")
                
                .requestMatchers("/admin/reportes/**").hasAnyRole("ADMINISTRADOR", "VENDEDOR")
                
                .anyRequest().authenticated()
            )
            .formLogin(formLogin -> formLogin
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/login-success", true)
                .failureUrl("/login?error=true")
                .failureHandler(authenticationFailureHandler())
                .successHandler(authenticationSuccessHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout=true")
                .logoutSuccessHandler(logoutSuccessHandler())
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .clearAuthentication(true)
                .permitAll()
            )
            .rememberMe(rememberMe -> rememberMe
                .key("stamosSecretKey")
                .tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(7 * 24 * 60 * 60)
                .rememberMeParameter("remember-me")
                .alwaysRemember(false)
                .userDetailsService(customUserDetailsService)
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .expiredUrl("/login?expired=true")
                .maxSessionsPreventsLogin(false)
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/access-denied")
            )
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.deny())
                .xssProtection(xss -> xss.disable())
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives(
                        "default-src 'self'; " +
                        "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdn.jsdelivr.net https://code.jquery.com; " +
                        "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com https://cdn.jsdelivr.net; " +
                        "font-src 'self' https://fonts.gstatic.com https://cdn.jsdelivr.net; " +
                        "img-src 'self' data: https://stamosl.com; " +
                        "frame-src 'none'"
                    )
                )
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                    "/api/**",
                    "/webhook/**"
                )
            );

        return http.build();
    }

    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService)
            .passwordEncoder(passwordEncoder);
    }
}