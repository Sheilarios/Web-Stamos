package com.stamos.config;

import com.stamos.payment.ManualPaymentGateway;
import com.stamos.payment.PaymentGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuración del módulo de pagos.
 * Permite seleccionar la implementación de PaymentGateway mediante properties.
 * Por defecto se usa ManualPaymentGateway.
 * Para cambiar a una pasarela real, solo se modifica esta clase o se añade una nueva implementación.
 */
@Configuration
public class PaymentGatewayConfig {

    @Value("${stamos.payment.gateway.implementation:manual}")
    private String gatewayImpl;

    /**
     * Define el bean de PaymentGateway según la propiedad stamos.payment.gateway.implementation.
     * Valores posibles: "manual" (por defecto), "culqi", "izipay", etc.
     *
     * @param manualGateway Inyección de la implementación manual
     * @return PaymentGateway seleccionado
     */
    @Bean
    @Primary
    public PaymentGateway paymentGateway(ManualPaymentGateway manualGateway) {
        // Aquí se pueden agregar condiciones para otros gateways en el futuro
        if ("manual".equalsIgnoreCase(gatewayImpl)) {
            return manualGateway;
        }
        // Ejemplo: if ("culqi".equalsIgnoreCase(gatewayImpl)) return new CulqiPaymentGateway();
        // Por defecto, retorna manual
        return manualGateway;
    }
}