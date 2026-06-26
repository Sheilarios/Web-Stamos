package com.stamos.controller.admin;

import com.stamos.controller.BaseController;
import com.stamos.dto.CotizacionDTO;
import com.stamos.dto.ProyectoDTO;
import com.stamos.model.Usuario;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'DISEÑADOR', 'PRODUCCION', 'SOPORTE_TECNICO', 'ADMINISTRADOR')")
public class DashboardController extends BaseController {

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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        try {
            String email = auth.getName();
            Usuario usuarioActual = usuarioService.findByEmail(email);
            if (usuarioActual == null) {
                return "redirect:/login";
            }
            
            String rol = usuarioActual.getNombreRol();
            if (rol == null) {
                rol = "Administrador";
            }
            
            model.addAttribute("usuario", usuarioActual);
            model.addAttribute("rol", rol);
            model.addAttribute("nombreUsuario", usuarioActual.getNombreCompleto() != null ? 
                              usuarioActual.getNombreCompleto() : usuarioActual.getEmail());
            model.addAttribute("email", usuarioActual.getEmail());
            model.addAttribute("fechaActual", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            model.addAttribute("titulo", "Dashboard - " + rol);

            long totalClientes = clienteService.count();
            long totalProyectos = proyectoService.count();
            long proyectosActivos = proyectoService.countByEstadoNotIn(Arrays.asList("Entregado", "Anulado"));
            long cotizacionesPendientes = cotizacionService.countByEstado("Pendiente");
            long totalEquipos = inventarioService.count();
            long pagosPendientesValidacion = pagoService.countByEstado("Pendiente");

            List<CotizacionDTO> ultimasCotizaciones = new ArrayList<>();
            List<com.stamos.model.Cotizacion> cotizaciones = cotizacionService.findTopN(5);
            for (com.stamos.model.Cotizacion cotizacion : cotizaciones) {
                CotizacionDTO dto = new CotizacionDTO();
                dto.setIdCotizacion(cotizacion.getIdCotizacion());
                dto.setNombreCliente(cotizacion.getNombreCliente());
                dto.setTotal(cotizacion.getTotal());
                dto.setEstado(cotizacion.getEstado());
                dto.setFechaEmision(cotizacion.getFechaEmision());
                ultimasCotizaciones.add(dto);
            }

            List<ProyectoDTO> ultimosProyectos = new ArrayList<>();
            List<com.stamos.model.Proyecto> proyectos = proyectoService.findTop10ByOrderByFechaInicioDesc();
            for (com.stamos.model.Proyecto proyecto : proyectos) {
                ProyectoDTO dto = new ProyectoDTO();
                dto.setIdProyecto(proyecto.getIdProyecto());
                dto.setNombreProyecto(proyecto.getNombreProyecto());
                dto.setNombreCliente(proyecto.getNombreCliente());
                dto.setEstado(proyecto.getEstado());
                dto.setMontoTotal(proyecto.getMontoTotal());
                dto.setFechaInicio(proyecto.getFechaInicio());
                ultimosProyectos.add(dto);
            }

            model.addAttribute("username", usuarioActual.getNombreCompleto() != null ? 
                              usuarioActual.getNombreCompleto() : usuarioActual.getEmail());
            model.addAttribute("totalClientes", totalClientes);
            model.addAttribute("totalProyectos", totalProyectos);
            model.addAttribute("proyectosActivos", proyectosActivos);
            model.addAttribute("cotizacionesPendientes", cotizacionesPendientes);
            model.addAttribute("totalEquipos", totalEquipos);
            model.addAttribute("pagosPendientesValidacion", pagosPendientesValidacion);
            model.addAttribute("ultimasCotizaciones", ultimasCotizaciones);
            model.addAttribute("ultimosProyectos", ultimosProyectos);
            addCommonAttributes(model);

            return "admin/dashboard";

        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el dashboard: " + e.getMessage());
            model.addAttribute("username", "Administrador");
            model.addAttribute("totalClientes", 0L);
            model.addAttribute("totalProyectos", 0L);
            model.addAttribute("proyectosActivos", 0L);
            model.addAttribute("cotizacionesPendientes", 0L);
            model.addAttribute("totalEquipos", 0L);
            model.addAttribute("pagosPendientesValidacion", 0L);
            model.addAttribute("ultimasCotizaciones", new ArrayList<>());
            model.addAttribute("ultimosProyectos", new ArrayList<>());
            addCommonAttributes(model);
            return "admin/dashboard";
        }
    }

    @GetMapping({"", "/"})
    public String redirectToDashboard() {
        return "redirect:/admin/dashboard";
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

    @Override
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

    @Override
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