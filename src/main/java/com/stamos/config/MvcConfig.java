package com.stamos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("public/index");
        registry.addViewController("/home").setViewName("public/index");
        registry.addViewController("/servicios").setViewName("public/servicios");
        registry.addViewController("/galeria").setViewName("public/galeria");
        registry.addViewController("/contacto").setViewName("public/contacto");
        
        registry.addViewController("/public/login").setViewName("public/login");
        registry.addViewController("/login").setViewName("public/login");
        registry.addViewController("/olvide-contrasena").setViewName("public/forgot-password");
        registry.addViewController("/resetear-contrasena").setViewName("public/reset-password");
        
        registry.addViewController("/error/403").setViewName("error/403");
        registry.addViewController("/error/404").setViewName("error/404");
        registry.addViewController("/error/500").setViewName("error/500");
    }
}