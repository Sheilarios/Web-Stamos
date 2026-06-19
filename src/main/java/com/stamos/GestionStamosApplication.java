package com.stamos;

import com.stamos.model.Cliente;
import com.stamos.model.Cotizacion;
import com.stamos.service.CotizacionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootApplication
public class GestionStamosApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestionStamosApplication.class, args);
    }
}