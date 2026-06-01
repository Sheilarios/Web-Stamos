package com.stamos;

import com.stamos.entity.Cliente;
import com.stamos.entity.Cotizacion;
import com.stamos.entity.DetalleCotizacion;
import com.stamos.service.ClienteService;
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