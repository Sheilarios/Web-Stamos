package com.stamos.controller;

import com.stamos.entity.Usuario;
import com.stamos.service.UsuarioService;
import com.stamos.service.EmailService;
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

/**
 * Controlador para la gestión de usuarios empleados del sistema interno.
 * Solo el rol ADMINISTRADOR puede crear, modificar o eliminar empleados.
 * Los clientes son gestionados a través de ClienteController.
 * 
 * Reglas de negocio:
 * - Solo ADMINISTRADOR puede listar, crear, editar, habilitar/deshabilitar empleados.
 * - Los roles válidos: ADMINISTRADOR, COMERCIAL, DISEÑADOR, PRODUCCION, SOPORTE_TECNICO.
 * - Un empleado no puede eliminarse físicamente si tiene proyectos asociados.
 * - Las contraseñas se almacenan cifradas con BCrypt.
 * - Se puede resetear la contraseña de un empleado (envío de correo con token).
 */
@Controller
@RequestMapping("/internal/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ==================== LISTAR EMPLEADOS ====================

    /**
     * Lista todos los empleados del sistema (solo ADMIN).
     *
     * @param model Modelo
     * @return Vista lista de empleados
     */
    @GetMapping("/lista")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String listUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.findAllEmployees(); // Solo empleados (no clientes)
        model.addAttribute("usuarios", usuarios);
        return "internal/usuarios/lista";
    }

    // ==================== CREAR EMPLEADO ====================

    /**
     * Muestra el formulario para crear un nuevo empleado.
     *
     * @param model Modelo
     * @return Vista formulario de creación
     */
    @GetMapping("/crear")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String showCreateForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", getRolesList());
        return "internal/usuarios/crear-empleado";
    }

    /**
     * Guarda un nuevo empleado. Asigna una contraseña temporal y envía correo.
     *
     * @param usuario Datos del nuevo empleado
     * @param bindingResult Validación
     * @param redirectAttrs Mensajes flash
     * @return Redirección a lista o formulario con errores
     */
    @PostMapping("/guardar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String saveUsuario(@Valid @ModelAttribute("usuario") Usuario usuario,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            return "internal/usuarios/crear-empleado";
        }
        // Validar email único
        if (usuarioService.existsByEmail(usuario.getEmail())) {
            bindingResult.rejectValue("email", "error.usuario", "Ya existe un usuario con ese correo electrónico");
            return "internal/usuarios/crear-empleado";
        }
        // Generar contraseña temporal aleatoria
        String tempPassword = generateTemporaryPassword();
        usuario.setPassword(passwordEncoder.encode(tempPassword));
        usuario.setActivo(true);
        usuario.setEsEmpleado(true); // Marcar como empleado (no cliente)
        usuarioService.save(usuario);

        // Enviar correo con credenciales temporales
        emailService.enviarCredencialesEmpleado(usuario, tempPassword);

        redirectAttrs.addFlashAttribute("success", 
            "Empleado creado exitosamente. Se ha enviado un correo con las credenciales temporales.");
        return "redirect:/internal/usuarios/lista";
    }

    // ==================== EDITAR EMPLEADO ====================

    /**
     * Muestra el formulario para editar un empleado existente.
     *
     * @param id ID del empleado
     * @param model Modelo
     * @param redirectAttrs
     * @return Vista formulario de edición
     */
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

    /**
     * Actualiza los datos de un empleado (excepto contraseña y estado activo).
     *
     * @param id ID del empleado
     * @param usuario Datos actualizados
     * @param bindingResult Validación
     * @param redirectAttrs Mensajes flash
     * @return Redirección a lista
     */
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
        // Validar email único excluyendo al propio usuario
        if (!existing.getEmail().equals(usuario.getEmail()) && 
            usuarioService.existsByEmail(usuario.getEmail())) {
            bindingResult.rejectValue("email", "error.usuario", "Ya existe otro usuario con ese correo");
            return "internal/usuarios/editar-empleado";
        }
        existing.setEmail(usuario.getEmail());
        existing.setNombre(usuario.getNombre());
        existing.setRol(usuario.getRol());
        // No se modifica la contraseña aquí
        usuarioService.save(existing);
        redirectAttrs.addFlashAttribute("success", "Empleado actualizado correctamente.");
        return "redirect:/internal/usuarios/lista";
    }

    // ==================== CAMBIAR ESTADO (HABILITAR/DESHABILITAR) ====================

    /**
     * Deshabilita un empleado (no puede iniciar sesión).
     *
     * @param id ID del empleado
     * @param redirectAttrs Mensajes flash
     * @return Redirección a lista
     */
    @PostMapping("/deshabilitar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String disableUsuario(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        Usuario usuario = usuarioService.findById(id);
        if (usuario == null || !usuario.isEsEmpleado()) {
            redirectAttrs.addFlashAttribute("error", "Empleado no encontrado.");
            return "redirect:/internal/usuarios/lista";
        }
        // No permitir deshabilitar al propio administrador (seguridad)
        if (usuario.getId().equals(getCurrentUserId())) {
            redirectAttrs.addFlashAttribute("error", "No puedes deshabilitar tu propia cuenta.");
            return "redirect:/internal/usuarios/lista";
        }
        usuario.setActivo(false);
        usuarioService.save(usuario);
        redirectAttrs.addFlashAttribute("success", "Empleado deshabilitado. No podrá iniciar sesión.");
        return "redirect:/internal/usuarios/lista";
    }

    /**
     * Habilita un empleado previamente deshabilitado.
     *
     * @param id ID del empleado
     * @param redirectAttrs Mensajes flash
     * @return Redirección a lista
     */
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

    // ==================== RESETEO DE CONTRASEÑA (ADMIN) ====================

    /**
     * Envía un correo con un enlace para restablecer la contraseña de un empleado.
     *
     * @param id ID del empleado
     * @param redirectAttrs Mensajes flash
     * @return Redirección a lista
     */
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
        emailService.enviarResetPasswordEmpleado(usuario, token);
        redirectAttrs.addFlashAttribute("success", 
            "Se ha enviado un correo al empleado para restablecer su contraseña.");
        return "redirect:/internal/usuarios/lista";
    }

    // ==================== PERFIL DEL USUARIO AUTENTICADO ====================

    /**
     * Muestra el perfil del usuario autenticado (empleado o cliente).
     * No requiere rol específico, solo autenticación.
     *
     * @param model Modelo
     * @param auth Usuario autenticado
     * @return Vista de perfil
     */
    @GetMapping("/perfil")
    public String showProfile(Model model, Authentication auth) {
        Usuario usuario = usuarioService.findByEmail(auth.getName());
        model.addAttribute("usuario", usuario);
        return "internal/usuarios/perfil";
    }

    /**
     * Formulario para cambiar la propia contraseña (empleado o cliente).
     * La lógica de cambio está en AuthController, pero aquí mostramos el formulario.
     *
     * @return Redirección a la ruta común de cambio de contraseña
     */
    @GetMapping("/perfil/cambiar-contrasena")
    public String redirectChangePassword() {
        return "redirect:/cambiar-contraseña";
    }

    // ==================== MÉTODOS AUXILIARES PRIVADOS ====================

    private List<String> getRolesList() {
        return List.of("ADMINISTRADOR", "COMERCIAL", "DISEÑADOR", "PRODUCCION", "SOPORTE_TECNICO");
    }

    private String generateTemporaryPassword() {
        // Genera una contraseña temporal de 8 caracteres alfanuméricos
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private Long getCurrentUserId() {
        // Obtener ID del usuario autenticado (implementar según la lógica de seguridad)
        // Se puede usar SecurityContextHolder o un helper
        return usuarioService.getCurrentUserId();
    }
}