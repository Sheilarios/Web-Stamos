package com.stamos.config;

import com.stamos.dto.MenuItem;
import com.stamos.model.Usuario;
import com.stamos.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ControllerAdvice
public class WebControllerAdvice {

    @Autowired
    private UsuarioService usuarioService;

    private static final Set<String> ROLES_INTERNOS = Set.of(
        "ADMINISTRADOR", "VENDEDOR", "DISEÑADOR", "PRODUCCION", "SOPORTE_TECNICO"
    );

    private String normalizarRol(String rol) {
        if (rol == null) return "";
        String normalized = Normalizer.normalize(rol, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return normalized.toUpperCase().replace(" ", "_");
    }

    @ModelAttribute("menuItems")
    public List<MenuItem> getMenuItems() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String rol = "ANONIMO";
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Usuario usuario = usuarioService.findByEmail(auth.getName());
            if (usuario != null && usuario.getNombreRol() != null) {
                rol = normalizarRol(usuario.getNombreRol());
            }
        }
        return construirMenu(rol);
    }

    @ModelAttribute("username")
    public String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Usuario usuario = usuarioService.findByEmail(auth.getName());
            return usuario != null ? usuario.getNombreCompleto() : auth.getName();
        }
        return "Invitado";
    }

    @ModelAttribute("rol")
    public String getRol() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Usuario usuario = usuarioService.findByEmail(auth.getName());
            if (usuario != null) {
                return usuario.getNombreRol();
            }
        }
        return "Público";
    }

    @ModelAttribute("currentPath")
    public String getCurrentPath(HttpServletRequest request) {
        return request.getRequestURI();
    }

    private List<MenuItem> construirMenu(String rol) {
        List<MenuItem> items = new ArrayList<>();
        items.add(new MenuItem("Dashboard", "/admin/dashboard", "ti ti-layout-dashboard"));

        if ("ADMINISTRADOR".equals(rol) || "VENDEDOR".equals(rol)) {
            items.add(new MenuItem("Clientes", "/admin/clientes", "ti ti-users"));
            items.add(new MenuItem("Cotizaciones", "/admin/cotizaciones", "ti ti-file-invoice"));
            items.add(new MenuItem("Pagos", "/admin/pagos", "ti ti-credit-card"));
        }

        if ("ADMINISTRADOR".equals(rol) || "PRODUCCION".equals(rol) || "SOPORTE_TECNICO".equals(rol)) {
            items.add(new MenuItem("Inventario", "/admin/inventario", "ti ti-box"));
        }

        if (ROLES_INTERNOS.contains(rol)) {
            items.add(new MenuItem("Proyectos", "/admin/proyectos", "ti ti-folder-open"));
        }

        if ("ADMINISTRADOR".equals(rol)) {
            items.add(new MenuItem("Productos", "/admin/productos", "ti ti-package"));
            items.add(new MenuItem("Configuración", "/admin/configuracion", "ti ti-settings"));
            items.add(new MenuItem("Usuarios", "/admin/usuarios", "ti ti-user-cog"));
            items.add(new MenuItem("Reportes", "/admin/reportes", "ti ti-file-pdf"));
        }
        return items;
    }
}
