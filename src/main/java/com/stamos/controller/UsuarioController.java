package com.stamos.controller;

import com.stamos.entity.Usuario;
import com.stamos.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/internal/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/lista")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String listUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.findAllEmployees(); // Solo empleados (no clientes)
        model.addAttribute("usuarios", usuarios);
        return "internal/usuarios/lista";
    }

    @GetMapping("/crear")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String showCreateForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", getRolesList());
        return "internal/usuarios/crear-empleado";
    }

    @PostMapping("/guardar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String saveUsuario(@Valid @ModelAttribute("usuario") Usuario usuario,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            return "internal/usuarios/crear-empleado";
        }
        if (usuarioService.existsByEmail(usuario.getEmail())) {
            bindingResult.rejectValue("email", "error.usuario", "Ya existe un usuario con ese correo electrónico");
            return "internal/usuarios/crear-empleado";
        }
        String tempPassword = generateTemporaryPassword();
        usuario.setPassword(passwordEncoder.encode(tempPassword));
        usuario.setActivo(true);
        usuario.setEsEmpleado(true); 
        usuarioService.save(usuario);

        redirectAttrs.addFlashAttribute("success", 
            "Empleado creado exitosamente. Se ha enviado un correo con las credenciales temporales.");
        return "redirect:/internal/usuarios/lista";
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        Usuario usuario = usuarioService.findById(id);
        if (usuario == null || !usuario.isEsEmpleado()) {
            redirectAttrs.addFlashAttribute("error", "Empleado no encontrado.");
            return "redirect:/internal/usuarios/lista";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", getRolesList());
        return "internal/usuarios/editar-empleado";
    }

    @PostMapping("/actualizar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String updateUsuario(@PathVariable Long id,
                                @Valid @ModelAttribute("usuario") Usuario usuario,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttrs) {
        Usuario existing = usuarioService.findById(id);
        if (existing == null || !existing.isEsEmpleado()) {
            redirectAttrs.addFlashAttribute("error", "Empleado no encontrado.");
            return "redirect:/internal/usuarios/lista";
        }
        if (bindingResult.hasErrors()) {
            return "internal/usuarios/editar-empleado";
        }
        if (!existing.getEmail().equals(usuario.getEmail()) && 
            usuarioService.existsByEmail(usuario.getEmail())) {
            bindingResult.rejectValue("email", "error.usuario", "Ya existe otro usuario con ese correo");
            return "internal/usuarios/editar-empleado";
        }
        existing.setEmail(usuario.getEmail());
        existing.setNombre(usuario.getNombre());
        existing.setRol(usuario.getRol());
        usuarioService.save(existing);
        redirectAttrs.addFlashAttribute("success", "Empleado actualizado correctamente.");
        return "redirect:/internal/usuarios/lista";
    }

    @PostMapping("/deshabilitar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String disableUsuario(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        Usuario usuario = usuarioService.findById(id);
        if (usuario == null || !usuario.isEsEmpleado()) {
            redirectAttrs.addFlashAttribute("error", "Empleado no encontrado.");
            return "redirect:/internal/usuarios/lista";
        }
        if (usuario.getId().equals(getCurrentUserId())) {
            redirectAttrs.addFlashAttribute("error", "No puedes deshabilitar tu propia cuenta.");
            return "redirect:/internal/usuarios/lista";
        }
        usuario.setActivo(false);
        usuarioService.save(usuario);
        redirectAttrs.addFlashAttribute("success", "Empleado deshabilitado. No podrá iniciar sesión.");
        return "redirect:/internal/usuarios/lista";
    }

    @PostMapping("/habilitar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String enableUsuario(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        Usuario usuario = usuarioService.findById(id);
        if (usuario == null || !usuario.isEsEmpleado()) {
            redirectAttrs.addFlashAttribute("error", "Empleado no encontrado.");
            return "redirect:/internal/usuarios/lista";
        }
        usuario.setActivo(true);
        usuarioService.save(usuario);
        redirectAttrs.addFlashAttribute("success", "Empleado habilitado correctamente.");
        return "redirect:/internal/usuarios/lista";
    }

    @PostMapping("/reset-password/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String resetPassword(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        Usuario usuario = usuarioService.findById(id);
        if (usuario == null || !usuario.isEsEmpleado()) {
            redirectAttrs.addFlashAttribute("error", "Empleado no encontrado.");
            return "redirect:/internal/usuarios/lista";
        }
        String token = UUID.randomUUID().toString();
        usuarioService.savePasswordResetToken(usuario, token);
        redirectAttrs.addFlashAttribute("success", 
            "Se ha enviado un correo al empleado para restablecer su contraseña.");
        return "redirect:/internal/usuarios/lista";
    }

    @GetMapping("/perfil")
    public String showProfile(Model model, Authentication auth) {
        Usuario usuario = usuarioService.findByEmail(auth.getName());
        model.addAttribute("usuario", usuario);
        return "internal/usuarios/perfil";
    }

    @GetMapping("/perfil/cambiar-contrasena")
    public String redirectChangePassword() {
        return "redirect:/cambiar-contraseña";
    }


    private List<String> getRolesList() {
        return List.of("ADMINISTRADOR", "COMERCIAL", "DISEÑADOR", "PRODUCCION", "SOPORTE_TECNICO");
    }

    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private Long getCurrentUserId() {
        return usuarioService.getCurrentUserId();
    }
}