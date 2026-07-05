package com.stamos.controller;

import com.stamos.dto.MenuItem;
import com.stamos.model.Usuario;
import com.stamos.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseController {

    @Autowired
    private UsuarioService usuarioService;

    protected String formatearFecha(LocalDate fecha) {
        if (fecha == null) return "-";
        return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    protected String formatearFechaHora(LocalDateTime fecha) {
        if (fecha == null) return "-";
        return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    protected String formatearMoneda(BigDecimal monto) {
        if (monto == null) return "S/ 0.00";
        return "S/ " + monto.setScale(2, RoundingMode.HALF_UP);
    }

    protected String truncarTexto(String texto, int maxLength) {
        if (texto == null) return "";
        if (texto.length() <= maxLength) return texto;
        return texto.substring(0, maxLength) + "...";
    }

    protected String getEstadoBadgeClass(String estado) {
        if (estado == null) return "status-pendiente";
        return switch (estado) {
            case "Activo" -> "status-validado";
            case "Inactivo" -> "status-rechazado";
            case "Aprobado" -> "status-aprobado";
            case "En Diseño" -> "status-diseno";
            case "En Producción" -> "status-produccion";
            case "Instalación/Montaje" -> "status-instalacion";
            case "Entregado" -> "status-entregado";
            case "Anulado" -> "status-anulado";
            default -> "status-pendiente";
        };
    }

    protected String getEstadoColor(String estado) {
        if (estado == null) return "#6B7280";
        return switch (estado) {
            case "Activo", "Validado" -> "#10B981";
            case "Inactivo", "Rechazado" -> "#EF4444";
            case "Aprobado" -> "#1A56DB";
            case "En Diseño" -> "#F59E0B";
            case "En Producción" -> "#3B82F6";
            case "Instalación/Montaje" -> "#8B5CF6";
            case "Entregado" -> "#10B981";
            case "Anulado" -> "#EF4444";
            default -> "#6B7280";
        };
    }

    protected void addCommonAttributes(Model model) {
        model.addAttribute("fechaActual", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        model.addAttribute("anioActual", LocalDateTime.now().getYear());
    }

    @ModelAttribute("rolUsuario")
    public String getRolUsuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return "ANONIMO";
        }
        try {
            Usuario usuario = usuarioService.findByEmail(auth.getName());
            if (usuario != null && usuario.getNombreRol() != null) {
                String rol = usuario.getNombreRol().toUpperCase().replace(" ", "_");
                return rol;
            }
            return "ANONIMO";
        } catch (Exception e) {
            return "ANONIMO";
        }
    }

    @ModelAttribute("menuItems")
    public List<MenuItem> getMenuItems() {
        String rol = getRolUsuario();
        List<MenuItem> items = new ArrayList<>();

        items.add(new MenuItem("Dashboard", "/admin/dashboard", "bi bi-layout-dashboard"));

        if ("ADMINISTRADOR".equals(rol) || "VENDEDOR".equals(rol)) {
            items.add(new MenuItem("Clientes", "/admin/clientes", "bi bi-people"));
            items.add(new MenuItem("Cotizaciones", "/admin/cotizaciones", "bi bi-file-earmark-text"));
            items.add(new MenuItem("Pagos", "/admin/pagos", "bi bi-credit-card"));
        }

        if (!"CLIENTE".equals(rol) && !"ANONIMO".equals(rol)) {
            items.add(new MenuItem("Proyectos", "/admin/proyectos", "bi bi-folder-open"));
        }

        if ("ADMINISTRADOR".equals(rol) || "PRODUCCION".equals(rol) || "SOPORTE_TECNICO".equals(rol)) {
            items.add(new MenuItem("Inventario", "/admin/inventario", "bi bi-box"));
        }

        if ("ADMINISTRADOR".equals(rol)) {
            items.add(new MenuItem("Configuración", "/admin/configuracion", "bi bi-gear"));
            items.add(new MenuItem("Usuarios", "/admin/usuarios", "bi bi-person-gear"));
            items.add(new MenuItem("Reportes", "/admin/reportes", "bi bi-file-pdf"));
        }

        return items;
    }

    @ModelAttribute("currentPath")
    public String getCurrentPath(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
