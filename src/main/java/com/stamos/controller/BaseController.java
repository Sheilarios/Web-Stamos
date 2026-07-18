package com.stamos.controller;

import com.stamos.dto.MenuItem;
import com.stamos.model.Usuario;
import com.stamos.service.NotificacionService;
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

    @Autowired
    private NotificacionService notificacionService;

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
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
                return "ANONIMO";
            }
            try {
                Usuario usuario = usuarioService.findByEmail(auth.getName());
                if (usuario != null && usuario.getNombreRol() != null) {
                    return usuario.getNombreRol().toUpperCase().replace(" ", "_");
                }
                return "ANONIMO";
            } catch (Exception e) {
                return "ANONIMO";
            }
        } catch (Exception e) {
            return "ANONIMO";
        }
    }

    @ModelAttribute("menuItems")
    public List<MenuItem> getMenuItems() {
        try {
            String rol = getRolUsuario();
            List<MenuItem> items = new ArrayList<>();

            // Dashboard solo para roles internos (no cliente)
            if (!"CLIENTE".equals(rol) && !"ANONIMO".equals(rol)) {
                items.add(new MenuItem("Dashboard", "/admin/dashboard", "bi bi-layout-dashboard"));
            }

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
                items.add(new MenuItem("Configuracion", "/admin/configuracion", "bi bi-gear"));
                items.add(new MenuItem("Usuarios", "/admin/usuarios", "bi bi-person-gear"));
                items.add(new MenuItem("Reportes", "/admin/reportes", "bi bi-file-pdf"));
            }

            if (!"ADMINISTRADOR".equals(rol)) {
                items.add(new MenuItem("Notificaciones", "/notificaciones", "bi bi-bell"));
            }

            return items;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @ModelAttribute("currentPath")
    public String getCurrentPath(HttpServletRequest request) {
        try {
            return request.getRequestURI();
        } catch (Exception e) {
            return "/";
        }
    }

    @ModelAttribute("notificacionesNoLeidas")
    public Long getNotificacionesNoLeidas() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
                return 0L;
            }
            try {
                Usuario usuario = usuarioService.findByEmail(auth.getName());
                if (usuario != null) {
                    return notificacionService.countNoLeidasByUsuario(usuario.getIdUsuario());
                }
                return 0L;
            } catch (Exception e) {
                return 0L;
            }
        } catch (Exception e) {
            return 0L;
        }
    }

    @ModelAttribute("username")
    public String getUsername() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
                return "Invitado";
            }
            try {
                Usuario usuario = usuarioService.findByEmail(auth.getName());
                if (usuario != null && usuario.getNombreCompleto() != null) {
                    return usuario.getNombreCompleto();
                }
                return auth.getName();
            } catch (Exception e) {
                return auth.getName();
            }
        } catch (Exception e) {
            return "Invitado";
        }
    }

    @ModelAttribute("rol")
    public String getRol() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
                return "Publico";
            }
            try {
                Usuario usuario = usuarioService.findByEmail(auth.getName());
                if (usuario != null && usuario.getNombreRol() != null) {
                    return usuario.getNombreRol();
                }
                return "Usuario";
            } catch (Exception e) {
                return "Usuario";
            }
        } catch (Exception e) {
            return "Publico";
        }
    }
}