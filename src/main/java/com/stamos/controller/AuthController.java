package com.stamos.controller;

import com.stamos.entity.Usuario;
import com.stamos.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.security.Principal;

/**
 * Controlador de autenticación para el sistema STAMOS.
 * Maneja login, logout y cambio de contraseña tanto para empleados (sistema interno)
 * como para clientes (portal público).
 * 
 * Nota: El proceso de login es delegado a Spring Security, pero este controlador
 * proporciona las vistas y el cambio de contraseña.
 */
@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Muestra el formulario de login.
     * Se puede especificar un parámetro "type" para diferenciar entre
     * login de empleados (internal) y clientes (public), aunque ambos
     * utilizan el mismo endpoint de autenticación de Spring Security.
     * 
     * @param error   Indica si hubo error de autenticación
     * @param logout  Indica si se ha cerrado sesión exitosamente
     * @param model   Modelo para thymeleaf
     * @return        Nombre de la vista de login
     */
    @GetMapping("/login")
    public String loginForm(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", "Correo electrónico o contraseña incorrectos.");
        }
        if (logout != null) {
            model.addAttribute("message", "Has cerrado sesión correctamente.");
        }
        return "public/login";  // Vista única de login (empleados y clientes)
    }

    /**
     * Redirección post-login según el rol del usuario autenticado.
     * Este método es llamado por CustomAuthenticationSuccessHandler.
     * No es un endpoint público, sino un helper para el handler.
     * 
     * @param authentication Objeto de autenticación de Spring Security
     * @return               Ruta del dashboard correspondiente
     */
    public String determineTargetUrl(Authentication authentication) {
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        return switch (role) {
            case "ROLE_ADMINISTRADOR" -> "redirect:/internal/dashboard/admin";
            case "ROLE_COMERCIAL" -> "redirect:/internal/dashboard/comercial";
            case "ROLE_DISEÑADOR" -> "redirect:/internal/dashboard/disenador";
            case "ROLE_PRODUCCION" -> "redirect:/internal/dashboard/produccion";
            case "ROLE_SOPORTE_TECNICO" -> "redirect:/internal/dashboard/soporte";
            case "ROLE_CLIENTE" -> "redirect:/public/cliente/dashboard";
            default -> "redirect:/login?error=true";
        };
    }

    /**
     * Muestra el formulario de cambio de contraseña.Accesible solo para usuarios autenticados (empleados o clientes).
     * 
     * @param model Modelo para mostrar el email del usuario
     * @param principal
     * @return      Vista de cambio de contraseña
     */
    @GetMapping("/cambiar-contraseña")
    public String showChangePasswordForm(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String email = principal.getName();
        model.addAttribute("email", email);
        model.addAttribute("passwordForm", new Object()); // objeto vacío para binding
        return "cambiar-contrasena"; // Vista genérica (se adapta con fragmentos)
    }

    /**
     * Procesa el cambio de contraseña para el usuario autenticado.
     * Valida que la contraseña actual sea correcta y que la nueva cumpla
     * los requisitos mínimos (longitud 8 caracteres).
     * 
     * @param currentPassword Contraseña actual
     * @param newPassword     Nueva contraseña
     * @param confirmPassword Confirmación de la nueva contraseña
     * @param principal       Usuario autenticado
     * @param redirectAttrs   Para mensajes flash
     * @return                Redirección según el rol
     */
    @PostMapping("/cambiar-contraseña")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Principal principal,
                                 RedirectAttributes redirectAttrs) {
        if (principal == null) {
            return "redirect:/login";
        }
        String email = principal.getName();
        Usuario usuario = usuarioService.findByEmail(email);
        if (usuario == null) {
            redirectAttrs.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/login";
        }

        // Validar contraseña actual
        if (!passwordEncoder.matches(currentPassword, usuario.getPassword())) {
            redirectAttrs.addFlashAttribute("error", "La contraseña actual es incorrecta.");
            return "redirect:/cambiar-contraseña";
        }

        // Validar que nueva contraseña y confirmación coincidan
        if (!newPassword.equals(confirmPassword)) {
            redirectAttrs.addFlashAttribute("error", "La nueva contraseña y su confirmación no coinciden.");
            return "redirect:/cambiar-contraseña";
        }

        // Validar longitud mínima (8 caracteres)
        if (newPassword.length() < 8) {
            redirectAttrs.addFlashAttribute("error", "La nueva contraseña debe tener al menos 8 caracteres.");
            return "redirect:/cambiar-contraseña";
        }

        // Actualizar contraseña
        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioService.save(usuario);

        redirectAttrs.addFlashAttribute("success", "Contraseña actualizada correctamente. Por favor, inicia sesión nuevamente.");
        
        // Forzar cierre de sesión para que el usuario use la nueva contraseña
        SecurityContextHolder.clearContext();
        return "redirect:/login?logout";
    }

    /**
     * Muestra el formulario para solicitar restablecimiento de contraseña (opcional).
     * 
     * @return Vista de "olvidé mi contraseña"
     */
    @GetMapping("/olvide-contraseña")
    public String showForgotPasswordForm() {
        return "public/forgot-password";
    }

    /**
     * Procesa el envío de correo para restablecer contraseña (opcional).
     * Envía un enlace temporal al correo del usuario.
     * 
     * @param email          Correo del usuario
     * @param redirectAttrs  Mensajes flash
     * @return               Redirección a login
     */
    @PostMapping("/olvide-contraseña")
    public String processForgotPassword(@RequestParam("email") String email,
                                        RedirectAttributes redirectAttrs) {
        Usuario usuario = usuarioService.findByEmail(email);
        if (usuario == null) {
            redirectAttrs.addFlashAttribute("error", "No existe una cuenta con ese correo electrónico.");
            return "redirect:/olvide-contraseña";
        }
        // Generar token y enviar correo (implementar en UsuarioService)
        usuarioService.sendPasswordResetToken(usuario);
        redirectAttrs.addFlashAttribute("success", 
            "Se ha enviado un enlace para restablecer tu contraseña a " + email);
        return "redirect:/login";
    }

    /**
     * Muestra el formulario para establecer nueva contraseña mediante token.
     * 
     * @param token  Token de restablecimiento
     * @param model  Modelo
     * @return       Vista para nueva contraseña
     */
    @GetMapping("/resetear-contraseña")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        if (!usuarioService.isValidPasswordResetToken(token)) {
            model.addAttribute("error", "El enlace es inválido o ha expirado.");
            return "public/reset-password-error";
        }
        model.addAttribute("token", token);
        return "public/reset-password";
    }

    /**
     * Procesa el restablecimiento de contraseña mediante token.
     * 
     * @param token          Token de restablecimiento
     * @param newPassword    Nueva contraseña
     * @param redirectAttrs  Mensajes flash
     * @return               Redirección a login
     */
    @PostMapping("/resetear-contraseña")
    public String resetPassword(@RequestParam("token") String token,
                                @RequestParam("newPassword") String newPassword,
                                RedirectAttributes redirectAttrs) {
        if (newPassword.length() < 8) {
            redirectAttrs.addFlashAttribute("error", "La contraseña debe tener al menos 8 caracteres.");
            return "redirect:/resetear-contraseña?token=" + token;
        }
        boolean success = usuarioService.resetPassword(token, passwordEncoder.encode(newPassword));
        if (!success) {
            redirectAttrs.addFlashAttribute("error", "El enlace es inválido o ha expirado.");
            return "redirect:/olvide-contraseña";
        }
        redirectAttrs.addFlashAttribute("success", "Contraseña restablecida correctamente. Inicia sesión.");
        return "redirect:/login";
    }

    /**
     * Cierre de sesión personalizado (Spring Security ya maneja /logout,
     * pero podemos agregar un mensaje).
     * 
     * @param session Sesión HTTP
     * @return        Redirección a login con mensaje
     */
    @GetMapping("/logout-success")
    public String logoutSuccess(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }
}