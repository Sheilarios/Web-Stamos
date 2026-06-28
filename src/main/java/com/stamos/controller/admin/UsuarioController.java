package com.stamos.controller.admin;

import com.stamos.controller.BaseController;
import com.stamos.model.Usuario;
import com.stamos.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/admin/usuarios")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class UsuarioController extends BaseController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listarUsuarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            Model model) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("nombreCompleto").ascending());
            Page<Usuario> usuariosPage;

            if (search != null && !search.isEmpty()) {
                usuariosPage = usuarioService.search(search, pageable);
            } else {
                usuariosPage = usuarioService.findAll(pageable);
            }

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null ? auth.getName() : "Administrador";
            String rol = "Administrador";

            model.addAttribute("usuarios", usuariosPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", usuariosPage.getTotalPages());
            model.addAttribute("totalItems", usuariosPage.getTotalElements());
            model.addAttribute("totalUsuarios", usuarioService.count());
            model.addAttribute("search", search);
            model.addAttribute("size", size);
            model.addAttribute("username", username);
            model.addAttribute("rol", rol);
            model.addAttribute("fechaActual", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            model.addAttribute("titulo", "Gestión de Usuarios");
            
            addCommonAttributes(model);

            return "admin/usuarios";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar los usuarios: " + e.getMessage());
            model.addAttribute("username", "Administrador");
            model.addAttribute("rol", "Administrador");
            model.addAttribute("fechaActual", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            addCommonAttributes(model);
            return "admin/usuarios";
        }
    }
}