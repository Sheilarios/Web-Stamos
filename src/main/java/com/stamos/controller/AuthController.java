package com.stamos.controller;

import com.stamos.entity.Usuario;
import com.stamos.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.security.Principal;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginForm(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            @RequestParam(value = "expired", required = false) String expired,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", "Correo electrónico o contraseña incorrectos.");
        }
        if (logout != null) {
            model.addAttribute("success", "Has cerrado sesión correctamente.");
        }
        if (expired != null) {
            model.addAttribute("error", "Tu sesión ha expirado. Por favor, inicia sesión nuevamente.");
        }
        return "public/login";
    }

    @GetMapping("/cambiar-contraseña")
    public String showChangePasswordForm(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String email = principal.getName();
        model.addAttribute("email", email);
        return "cambiar-contrasena";
    }

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

        if (!passwordEncoder.matches(currentPassword, usuario.getPassword())) {
            redirectAttrs.addFlashAttribute("error", "La contraseña actual es incorrecta.");
            return "redirect:/cambiar-contraseña";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttrs.addFlashAttribute("error", "La nueva contraseña y su confirmación no coinciden.");
            return "redirect:/cambiar-contraseña";
        }

        if (newPassword.length() < 8) {
            redirectAttrs.addFlashAttribute("error", "La nueva contraseña debe tener al menos 8 caracteres.");
            return "redirect:/cambiar-contraseña";
        }

        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioService.save(usuario);

        redirectAttrs.addFlashAttribute("success", "Contraseña actualizada correctamente. Por favor, inicia sesión nuevamente.");
        
        SecurityContextHolder.clearContext();
        return "redirect:/login?logout=true";
    }

    @GetMapping("/olvide-contrasena")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/olvide-contrasena")
    public String processForgotPassword(@RequestParam("email") String email,
                                        RedirectAttributes redirectAttrs) {
        Usuario usuario = usuarioService.findByEmail(email);
        if (usuario == null) {
            redirectAttrs.addFlashAttribute("error", "No existe una cuenta con ese correo electrónico.");
            return "redirect:/olvide-contrasena";
        }
        
        usuarioService.sendPasswordResetToken(usuario);
        redirectAttrs.addFlashAttribute("success", 
            "Se ha enviado un enlace para restablecer tu contraseña a " + email);
        return "redirect:/login";
    }

    @GetMapping("/resetear-contrasena")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        if (!usuarioService.isValidPasswordResetToken(token)) {
            model.addAttribute("error", "El enlace es inválido o ha expirado.");
            return "reset-password-error";
        }
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/resetear-contrasena")
    public String resetPassword(@RequestParam("token") String token,
                                @RequestParam("newPassword") String newPassword,
                                RedirectAttributes redirectAttrs) {
        if (newPassword.length() < 8) {
            redirectAttrs.addFlashAttribute("error", "La contraseña debe tener al menos 8 caracteres.");
            return "redirect:/resetear-contrasena?token=" + token;
        }
        
        boolean success = usuarioService.resetPassword(token, passwordEncoder.encode(newPassword));
        if (!success) {
            redirectAttrs.addFlashAttribute("error", "El enlace es inválido o ha expirado.");
            return "redirect:/olvide-contrasena";
        }
        
        redirectAttrs.addFlashAttribute("success", "Contraseña restablecida correctamente. Inicia sesión.");
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        SecurityContextHolder.clearContext();
        return "redirect:/login?logout=true";
    }
    
    @GetMapping("/perfil")
    public String perfil() {
        return "redirect:/usuarios/perfil";
    }
}