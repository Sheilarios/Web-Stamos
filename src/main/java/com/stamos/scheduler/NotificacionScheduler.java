package com.stamos.scheduler;

import com.stamos.model.Inventario;
import com.stamos.model.Proyecto;
import com.stamos.model.Usuario;
import com.stamos.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class NotificacionScheduler {

    private static final Logger log = LoggerFactory.getLogger(NotificacionScheduler.class);

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private PagoService pagoService;

    @Scheduled(cron = "0 0 6 * * ?")
    public void enviarRecordatoriosPago() {
        log.info("Iniciando envío de recordatorios de pago...");
        
        try {
            List<Proyecto> proyectos = proyectoService.findProyectosConPagosVencidos();
            LocalDate hoy = LocalDate.now();
            LocalDate fechaLimite = hoy.plusDays(5);
            
            int recordatoriosEnviados = 0;
            
            for (Proyecto proyecto : proyectos) {
                if (proyecto.getFechaVencimiento() != null) {
                    LocalDate fechaVencimiento = proyecto.getFechaVencimiento();
                    
                    if (fechaVencimiento.equals(fechaLimite) && 
                        proyecto.getSaldoPendiente().compareTo(BigDecimal.ZERO) > 0) {
                        
                        try {
                            notificacionService.notificarPagoVencido(
                                proyecto.getIdCliente(),
                                proyecto.getNombreProyecto(),
                                proyecto.getSaldoPendienteFormateado(),
                                proyecto.getIdProyecto()
                            );
                            recordatoriosEnviados++;
                            log.info("Recordatorio de pago enviado para proyecto: {}", 
                                proyecto.getNombreProyecto());
                        } catch (Exception e) {
                            log.error("Error al enviar recordatorio de pago para proyecto {}: {}", 
                                proyecto.getNombreProyecto(), e.getMessage());
                        }
                    }
                }
            }
            log.info("Recordatorios de pago enviados: {}", recordatoriosEnviados);
        } catch (Exception e) {
            log.error("Error al ejecutar enviarRecordatoriosPago: {}", e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 8 * * 1")
    public void verificarStockBajo() {
        log.info("Verificando inventario con stock bajo...");
        
        try {
            List<Inventario> inventarioBajo = inventarioService.findInventarioBajo();
            
            if (!inventarioBajo.isEmpty()) {
                List<Usuario> administradores = usuarioService.findByNombreRol("Administrador");
                StringBuilder mensaje = new StringBuilder();
                mensaje.append(" ALERTA DE STOCK BAJO\n");
                mensaje.append("========================================\n\n");
                mensaje.append("Los siguientes productos tienen stock bajo:\n\n");
                
                for (Inventario item : inventarioBajo) {
                    mensaje.append("  📦 ")
                           .append(item.getNombreProducto() != null ? item.getNombreProducto() : "Producto ID: " + item.getIdProducto())
                           .append("\n")
                           .append("     Serial: ").append(item.getCodigoSerial())
                           .append("\n")
                           .append("     Ubicación: ").append(item.getUbicacion() != null ? item.getUbicacion() : "No especificada")
                           .append("\n")
                           .append("     Estado: ").append(item.getEstadoFisico())
                           .append("\n");
                    
                    if (item.getObservaciones() != null && !item.getObservaciones().isEmpty()) {
                        mensaje.append("     Observaciones: ").append(item.getObservaciones()).append("\n");
                    }
                    mensaje.append("\n");
                }
                
                mensaje.append("========================================\n");
                mensaje.append("Por favor, revise el inventario y tome las acciones necesarias.\n");
                
                int notificacionesEnviadas = 0;
                for (Usuario admin : administradores) {
                    try {
                        notificacionService.notificarStockBajo(
                            admin.getIdUsuario(),
                            mensaje.toString()
                        );
                        notificacionesEnviadas++;
                    } catch (Exception e) {
                        log.error("Error al enviar notificación a admin {}: {}", 
                            admin.getEmail(), e.getMessage());
                    }
                }
                log.info("Notificaciones de stock bajo enviadas a {} administradores ({} productos en riesgo)", 
                    notificacionesEnviadas, inventarioBajo.size());
            } else {
                log.info("No hay productos con stock bajo.");
            }
        } catch (Exception e) {
            log.error("Error al verificar stock bajo: {}", e.getMessage());
        }
    }


    @Scheduled(cron = "0 0 7 * * ?")
    public void verificarProyectosSinAvance() {
        log.info("Verificando proyectos sin avance...");
        
        try {
            List<Proyecto> proyectosActivos = proyectoService.findActivos();
            LocalDate hoy = LocalDate.now();
            LocalDate hace7Dias = hoy.minusDays(7);
            
            int notificacionesEnviadas = 0;
            
            for (Proyecto proyecto : proyectosActivos) {
                if (proyecto.getFechaActualizacion() != null) {
                    LocalDate fechaActualizacion = proyecto.getFechaActualizacion().toLocalDate();
                    
                    if (fechaActualizacion.isBefore(hace7Dias) && 
                        proyecto.getSaldoPendiente().compareTo(BigDecimal.ZERO) > 0) {
                        
                        try {
                            notificacionService.notificarProyectoSinAvance(
                                proyecto.getIdCliente(),
                                proyecto.getNombreProyecto(),
                                7,
                                proyecto.getIdProyecto()
                            );
                            notificacionesEnviadas++;
                            log.info("Notificación de sin avance enviada para proyecto: {}", 
                                proyecto.getNombreProyecto());
                        } catch (Exception e) {
                            log.error("Error al notificar sin avance para proyecto {}: {}", 
                                proyecto.getNombreProyecto(), e.getMessage());
                        }
                    }
                }
            }
            log.info("Notificaciones de proyectos sin avance enviadas: {}", notificacionesEnviadas);
        } catch (Exception e) {
            log.error("Error al verificar proyectos sin avance: {}", e.getMessage());
        }
    }


    @Scheduled(cron = "0 0 9 * * 1")
    public void enviarResumenSemanal() {
        log.info("Generando resumen semanal...");
        
        try {
            List<Usuario> administradores = usuarioService.findByNombreRol("Administrador");
            LocalDate hoy = LocalDate.now();
            LocalDate inicioSemana = hoy.minusDays(7);
            
            long totalProyectos = proyectoService.count();
            long proyectosActivos = proyectoService.countByEstadoNotIn(List.of("Entregado", "Anulado"));
            long proyectosNuevos = proyectoService.countByFechaInicioMonthAndYear(
                hoy.getMonthValue(), hoy.getYear()
            );
            long cotizacionesNuevas = 0; 
            BigDecimal ingresos = pagoService.getTotalPagadoGeneral();
            
            StringBuilder resumen = new StringBuilder();
            resumen.append("RESUMEN SEMANAL STAMOS S.L.\n");
            resumen.append("========================================\n\n");
            resumen.append("Período: ").append(inicioSemana.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                   .append(" - ").append(hoy.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n\n");
            resumen.append("ESTADÍSTICAS GENERALES:\n");
            resumen.append("────────────────────────────────────────\n");
            resumen.append("  • Total Proyectos: ").append(totalProyectos).append("\n");
            resumen.append("  • Proyectos Activos: ").append(proyectosActivos).append("\n");
            resumen.append("  • Proyectos Nuevos (mes): ").append(proyectosNuevos).append("\n");
            resumen.append("  • Cotizaciones Nuevas (mes): ").append(cotizacionesNuevas).append("\n");
            resumen.append("  • Ingresos Totales: S/ ").append(ingresos.setScale(2, BigDecimal.ROUND_HALF_UP)).append("\n\n");
            
            resumen.append("PROYECTOS POR ESTADO:\n");
            resumen.append("────────────────────────────────────────\n");
            resumen.append("  • Aprobados: ").append(proyectoService.countByEstado("Aprobado")).append("\n");
            resumen.append("  • En Diseño: ").append(proyectoService.countByEstado("En Diseño")).append("\n");
            resumen.append("  • En Producción: ").append(proyectoService.countByEstado("En Producción")).append("\n");
            resumen.append("  • Instalación/Montaje: ").append(proyectoService.countByEstado("Instalación/Montaje")).append("\n");
            resumen.append("  • Entregados: ").append(proyectoService.countByEstado("Entregado")).append("\n");
            resumen.append("  • Anulados: ").append(proyectoService.countByEstado("Anulado")).append("\n\n");
            
            resumen.append("========================================\n");
            resumen.append("Este resumen es generado automáticamente.\n");
            resumen.append("Para más detalles, ingrese al sistema.\n");
            
            int notificacionesEnviadas = 0;
            for (Usuario admin : administradores) {
                try {
                    notificacionService.enviarResumenSemanal(
                        admin.getIdUsuario(),
                        resumen.toString()
                    );
                    notificacionesEnviadas++;
                } catch (Exception e) {
                    log.error("Error al enviar resumen a admin {}: {}", 
                        admin.getEmail(), e.getMessage());
                }
            }
            log.info("Resumen semanal enviado a {} administradores", notificacionesEnviadas);
            
        } catch (Exception e) {
            log.error("Error al enviar resumen semanal: {}", e.getMessage());
        }
    }
}