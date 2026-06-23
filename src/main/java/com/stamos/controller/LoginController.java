package com.stamos.controller;

import com.stamos.model.Usuario;
import com.stamos.service.ClienteService;
import com.stamos.service.EmailService;
import com.stamos.service.LogAccesoClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Collection;

@Controller
public class LoginController {
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private LogAccesoClienteService logAccesoClienteService;

    @GetMapping("/login")
    public String mostrarLogin(@RequestParam(value = "error", required = false) String error,
                               @RequestParam(value = "logout", required = false) String logout,
                               @RequestParam(value = "expired", required = false) String expired,
                               Model model) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return redirectByRole(auth);
        }

        if (error != null) {
            model.addAttribute("error", "Usuario o contrasena incorrectos. Por favor, intente nuevamente.");
            model.addAttribute("errorClass", "alert-danger");
        }
        
        if (logout != null) {
            model.addAttribute("mensaje", "Has cerrado sesion exitosamente.");
            model.addAttribute("mensajeClass", "alert-success");
        }
        
        if (expired != null) {
            model.addAttribute("error", "Tu sesion ha expirado. Por favor, inicia sesion nuevamente.");
            model.addAttribute("errorClass", "alert-warning");
        }

        return "public/login";
    }

    private String redirectByRole(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        
        boolean isAdmin = authorities.stream().anyMatch(a -> 
            a.getAuthority().equals("ROLE_ADMIN") ||
            a.getAuthority().equals("ROLE_ADMINISTRADOR") ||
            a.getAuthority().equals("ROLE_Administrador") ||
            a.getAuthority().equals("Administrador") ||
            a.getAuthority().toUpperCase().contains("ADMIN"));
        
        boolean isVendedor = authorities.stream().anyMatch(a -> 
            a.getAuthority().equals("ROLE_VENDEDOR") ||
            a.getAuthority().equals("ROLE_Vendedor") ||
            a.getAuthority().equals("Vendedor") ||
            a.getAuthority().toUpperCase().contains("VENDEDOR"));
        
        boolean isDisenador = authorities.stream().anyMatch(a -> 
            a.getAuthority().equals("ROLE_DISEÑADOR") ||
            a.getAuthority().equals("ROLE_Diseñador") ||
            a.getAuthority().equals("Diseñador") ||
            a.getAuthority().toUpperCase().contains("DISEÑADOR"));
        
        boolean isProduccion = authorities.stream().anyMatch(a -> 
            a.getAuthority().equals("ROLE_PRODUCCION") ||
            a.getAuthority().equals("ROLE_Produccion") ||
            a.getAuthority().equals("Produccion") ||
            a.getAuthority().toUpperCase().contains("PRODUCCION"));
        
        boolean isSoporte = authorities.stream().anyMatch(a -> 
            a.getAuthority().equals("ROLE_SOPORTE_TECNICO") ||
            a.getAuthority().equals("ROLE_Soporte_Tecnico") ||
            a.getAuthority().equals("SoporteTecnico") ||
            a.getAuthority().toUpperCase().contains("SOPORTE"));
        
        boolean isCliente = authorities.stream().anyMatch(a -> 
            a.getAuthority().equals("ROLE_CLIENTE") ||
            a.getAuthority().equals("ROLE_Cliente") ||
            a.getAuthority().equals("Cliente") ||
            a.getAuthority().toUpperCase().contains("CLIENTE"));

        if (isAdmin || isVendedor || isDisenador || isProduccion || isSoporte) {
            return "redirect:/admin/dashboard";
        } else if (isCliente) {
            return "redirect:/cliente/dashboard";
        }

        SecurityContextHolder.clearContext();
        return "redirect:/login?error=true";
    }

    @GetMapping("/login-success")
    public String loginSuccess(Authentication auth, HttpServletRequest request) {
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            String email = auth.getName();
            try {
                Usuario usuario = clienteService.findUsuarioByEmail(email);
                if (usuario != null) {
                    usuario.setUltimoAcceso(LocalDateTime.now());
                    clienteService.actualizarUsuario(usuario);
                }
            } catch (Exception e) {
                System.err.println("Error al actualizar ultimo acceso: " + e.getMessage());
            }
        }
        return redirectByRole(auth);
    }

    @GetMapping("/forgot-password")
    public String mostrarForgotPassword(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return redirectByRole(auth);
        }
        return "public/forgot-p";
    }

    @PostMapping("/forgot-password")
    public String procesarForgotPassword(@RequestParam("email") String email,
                                         RedirectAttributes redirectAttributes) {
        try {
            if (email == null || email.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Debes ingresar tu correo electronico.");
                redirectAttributes.addFlashAttribute("errorClass", "alert-danger");
                return "redirect:/forgot-password";
            }

            Usuario usuario = clienteService.findUsuarioByEmail(email.trim());
            
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("mensaje", 
                    "Si el correo esta registrado, recibiras un enlace para recuperar tu contrasena.");
                redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
                return "redirect:/login";
            }

            if (!usuario.isActivo()) {
                redirectAttributes.addFlashAttribute("error", 
                    "Tu cuenta esta deshabilitada. Contacta con soporte.");
                redirectAttributes.addFlashAttribute("errorClass", "alert-danger");
                return "redirect:/forgot-password";
            }

            String token = emailService.generarTokenRecuperacion(email.trim());
            emailService.enviarCorreoRecuperacion(email.trim(), token);
            
            redirectAttributes.addFlashAttribute("mensaje", 
                "Se ha enviado un enlace de recuperacion a tu correo electronico.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al procesar la solicitud: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorClass", "alert-danger");
        }
        
        return "redirect:/login";
    }

    @GetMapping("/reset-password")
    public String mostrarResetPassword(@RequestParam("token") String token, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return redirectByRole(auth);
        }

        try {
            if (token == null || token.trim().isEmpty()) {
                model.addAttribute("error", "Token de recuperacion no valido.");
                return "public/reset-password-error";
            }

            boolean tokenValido = emailService.validarTokenRecuperacion(token.trim());
            
            if (!tokenValido) {
                model.addAttribute("error", "El enlace de recuperacion no es valido o ha expirado.");
                return "public/reset-password-error";
            }
            
            model.addAttribute("token", token.trim());
            return "public/reset-password";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al procesar el enlace de recuperacion.");
            return "public/reset-password-error";
        }
    }

    @PostMapping("/reset-password")
    public String procesarResetPassword(@RequestParam("token") String token,
                                        @RequestParam("password") String password,
                                        @RequestParam("confirmPassword") String confirmPassword,
                                        RedirectAttributes redirectAttributes) {
        try {
            if (token == null || token.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Token no valido.");
                redirectAttributes.addFlashAttribute("errorClass", "alert-danger");
                return "redirect:/login";
            }

            boolean tokenValido = emailService.validarTokenRecuperacion(token.trim());
            
            if (!tokenValido) {
                redirectAttributes.addFlashAttribute("error", 
                    "El enlace de recuperacion no es valido o ha expirado.");
                redirectAttributes.addFlashAttribute("errorClass", "alert-danger");
                return "redirect:/login";
            }

            if (password == null || password.length() < 6) {
                redirectAttributes.addFlashAttribute("error", 
                    "La contrasena debe tener al menos 6 caracteres.");
                redirectAttributes.addFlashAttribute("errorClass", "alert-danger");
                return "redirect:/reset-password?token=" + token.trim();
            }

            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", 
                    "Las contrasenas no coinciden.");
                redirectAttributes.addFlashAttribute("errorClass", "alert-danger");
                return "redirect:/reset-password?token=" + token.trim();
            }

            String email = emailService.obtenerEmailPorToken(token.trim());
            
            if (email == null || email.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", 
                    "Token no valido.");
                redirectAttributes.addFlashAttribute("errorClass", "alert-danger");
                return "redirect:/login";
            }

            Usuario usuario = clienteService.findUsuarioByEmail(email);
            
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", 
                    "Usuario no encontrado.");
                redirectAttributes.addFlashAttribute("errorClass", "alert-danger");
                return "redirect:/login";
            }

            clienteService.cambiarPassword(usuario.getIdUsuario(), password);
            emailService.invalidarToken(token.trim());
            
            redirectAttributes.addFlashAttribute("mensaje", 
                "Contrasena actualizada exitosamente. Ahora puedes iniciar sesion.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al restablecer la contrasena: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorClass", "alert-danger");
        }
        
        return "redirect:/login";
    }

    @GetMapping("/access-denied")
    public String mostrarAccessDenied(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            model.addAttribute("usuario", auth.getName());
            model.addAttribute("roles", auth.getAuthorities().toString());
        }
        return "public/access-denied";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return "redirect:/login?logout=true";
    }

    @GetMapping("/error")
    public String mostrarError(Model model) {
        model.addAttribute("mensaje", "Ha ocurrido un error inesperado.");
        model.addAttribute("errorClass", "alert-danger");
        return "public/error";
    }
}