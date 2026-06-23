package com.stamos.controller.admin;

import com.stamos.model.*;
import com.stamos.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'DISEÑADOR', 'PRODUCCION', 'SOPORTE_TECNICO', 'ADMINISTRADOR')")
public class DashboardController {

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private CotizacionService cotizacionService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PagoService pagoService;

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private UsuarioService usuarioService;



    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        System.out.println("=== Dashboard Admin GET ===");
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            System.out.println("Usuario no autenticado, redirigiendo a login");
            return "redirect:/login";
        }

        try {
            String email = auth.getName();
            System.out.println("Usuario autenticado: " + email);
            
            Usuario usuarioActual = usuarioService.findByEmail(email);
            if (usuarioActual == null) {
                System.out.println("Usuario no encontrado: " + email);
                return "redirect:/login";
            }
            
            String rol = usuarioActual.getNombreRol();
            if (rol == null) {
                rol = "Administrador";
            }
            System.out.println("Rol del usuario: " + rol);
            
            model.addAttribute("usuario", usuarioActual);
            model.addAttribute("rol", rol);
            model.addAttribute("nombreUsuario", usuarioActual.getNombreCompleto() != null ? 
                              usuarioActual.getNombreCompleto() : usuarioActual.getEmail());
            model.addAttribute("email", usuarioActual.getEmail());
            model.addAttribute("fechaActual", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            model.addAttribute("titulo", "Dashboard - " + rol);

            long totalClientes = 0;
            long totalProyectos = 0;
            long proyectosActivos = 0;
            long cotizacionesPendientes = 0;
            long totalEquipos = 0;
            long pagosPendientesValidacion = 0;

            try {
                totalClientes = clienteService.count();
            } catch (Exception e) {
                System.err.println("Error al contar clientes: " + e.getMessage());
            }

            try {
                totalProyectos = proyectoService.count();
            } catch (Exception e) {
                System.err.println("Error al contar proyectos: " + e.getMessage());
            }

            try {
                proyectosActivos = proyectoService.countByEstadoNotIn(Arrays.asList("Entregado", "Anulado"));
            } catch (Exception e) {
                System.err.println("Error al contar proyectos activos: " + e.getMessage());
                proyectosActivos = totalProyectos;
            }

            try {
                cotizacionesPendientes = cotizacionService.countByEstado("Pendiente");
            } catch (Exception e) {
                System.err.println("Error al contar cotizaciones pendientes: " + e.getMessage());
            }

            try {
                totalEquipos = inventarioService.count();
            } catch (Exception e) {
                System.err.println("Error al contar inventario: " + e.getMessage());
            }

            try {
                pagosPendientesValidacion = pagoService.countByEstado("Pendiente");
            } catch (Exception e) {
                System.err.println("Error al contar pagos pendientes: " + e.getMessage());
            }

            model.addAttribute("username", usuarioActual.getNombreCompleto() != null ? 
                              usuarioActual.getNombreCompleto() : usuarioActual.getEmail());
            model.addAttribute("totalClientes", totalClientes);
            model.addAttribute("totalProyectos", totalProyectos);
            model.addAttribute("proyectosActivos", proyectosActivos);
            model.addAttribute("cotizacionesPendientes", cotizacionesPendientes);
            model.addAttribute("totalEquipos", totalEquipos);
            model.addAttribute("pagosPendientesValidacion", pagosPendientesValidacion);
            model.addAttribute("ultimasCotizaciones", new ArrayList<>());
            model.addAttribute("ultimosProyectos", new ArrayList<>());

            return "admin/dashboard";

        } catch (Exception e) {
            System.err.println("Error en dashboard admin: " + e.getMessage());
            model.addAttribute("error", "Error al cargar el dashboard: " + e.getMessage());
            model.addAttribute("username", "Administrador");
            model.addAttribute("totalClientes", 0);
            model.addAttribute("totalProyectos", 0);
            model.addAttribute("proyectosActivos", 0);
            model.addAttribute("cotizacionesPendientes", 0);
            model.addAttribute("totalEquipos", 0);
            model.addAttribute("pagosPendientesValidacion", 0);
            model.addAttribute("ultimasCotizaciones", new ArrayList<>());
            model.addAttribute("ultimosProyectos", new ArrayList<>());
            return "admin/dashboard";
        }
    }

    @GetMapping({"", "/"})
    public String redirectToDashboard() {
        return "redirect:/admin/dashboard";
    }





    private Map<String, Long> getProyectosPorMes(int meses) {
        Map<String, Long> resultado = new LinkedHashMap<>();
        LocalDate hoy = LocalDate.now();
        
        for (int i = meses - 1; i >= 0; i--) {
            LocalDate fecha = hoy.minusMonths(i);
            String mes = fecha.getMonth().toString().substring(0, 3) + " " + fecha.getYear();
            long cantidad = 0;
            try {
                cantidad = proyectoService.countByFechaInicioMonthAndYear(
                    fecha.getMonthValue(), fecha.getYear()
                );
            } catch (Exception e) {
                System.err.println("Error al contar proyectos por mes: " + e.getMessage());
                cantidad = 0;
            }
            resultado.put(mes, cantidad);
        }
        
        return resultado;
    }


    public String getEstadoLegible(String estado) {
        if (estado == null) return "Sin estado";
        return switch (estado) {
            case "Aprobado" -> "Aprobado";
            case "En Diseño" -> "En Diseño";
            case "En Producción" -> "En Producción";
            case "Instalación/Montaje" -> "Instalación/Montaje";
            case "Entregado" -> "Entregado";
            case "Anulado" -> "Anulado";
            default -> estado;
        };
    }

    public String getEstadoBadgeClass(String estado) {
        if (estado == null) return "status-pendiente";
        return switch (estado) {
            case "Aprobado" -> "status-aprobado";
            case "En Diseño" -> "status-diseno";
            case "En Producción" -> "status-produccion";
            case "Instalación/Montaje" -> "status-instalacion";
            case "Entregado" -> "status-entregado";
            case "Anulado" -> "status-anulado";
            default -> "status-pendiente";
        };
    }

    public String formatearMoneda(BigDecimal monto) {
        if (monto == null) return "S/ 0.00";
        return "S/ " + monto.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public String getColorByEstado(String estado) {
        if (estado == null) return "#6B7280";
        return switch (estado) {
            case "En Diseño" -> "#F59E0B";
            case "En Producción" -> "#3B82F6";
            case "Instalación/Montaje" -> "#8B5CF6";
            case "Entregado" -> "#10B981";
            case "Anulado" -> "#EF4444";
            default -> "#6B7280";
        };
    }
}