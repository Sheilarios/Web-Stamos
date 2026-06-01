package com.stamos.service;

import com.stamos.entity.Proyecto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@EnableScheduling
public class AlertaVencimientoService {

    @Autowired
    private ProyectoService proyectoService;
    @Autowired
    private PagoService pagoService;
    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "0 0 8 * * *") // cada día a las 8 AM
    public void verificarPagosVencidos() {
        List<Proyecto> proyectos = proyectoService.findAll();
        StringBuilder mensaje = new StringBuilder("Proyectos con pagos pendientes y vencidos:\n");
        boolean hayVencidos = false;
        for (Proyecto p : proyectos) {
            if (!"ENTREGADO".equals(p.getEstado()) && !"ANULADO".equals(p.getEstado())) {
                BigDecimal saldo = pagoService.getSaldoPendiente(p);
                if (saldo.compareTo(BigDecimal.ZERO) > 0) {
                    LocalDate fechaEntrega = p.getFechaEntregaEstimada();
                    if (fechaEntrega != null && fechaEntrega.isBefore(LocalDate.now())) {
                        hayVencidos = true;
                        mensaje.append("- Proyecto ID: ").append(p.getId())
                               .append(", Cliente: ").append(p.getCliente().getNombreRazonSocial())
                               .append(", Saldo: S/ ").append(saldo)
                               .append(", Fecha entrega estimada: ").append(fechaEntrega)
                               .append("\n");
                    }
                }
            }
        }
        if (hayVencidos) {
            emailService.sendEmail("admin@stamos.com", "Alertas de pagos vencidos", mensaje.toString());
        }
    }
}