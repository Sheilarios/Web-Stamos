package com.stamos.controller.publics;

import com.stamos.model.Cliente;
import com.stamos.model.Usuario;
import com.stamos.service.ClienteService;
import com.stamos.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthPublicController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return "redirect:/cliente/dashboard";
        }

        if (!model.containsAttribute("cliente")) {
            model.addAttribute("cliente", new Cliente());
        }
        model.addAttribute("titulo", "Registro de Cliente");
        return "public/registro";
    }

    @PostMapping("/registro")
    public String registrarCliente(@Valid @ModelAttribute("cliente") Cliente cliente,
                                   BindingResult result,
                                   @RequestParam("password") String password,
                                   @RequestParam("confirmPassword") String confirmPassword,
                                   RedirectAttributes redirectAttributes) {
        try {
            if (password == null || password.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", 
                    "La contrasena es obligatoria.");
                redirectAttributes.addFlashAttribute("cliente", cliente);
                return "redirect:/auth/registro";
            }

            if (password.length() < 6) {
                redirectAttributes.addFlashAttribute("error", 
                    "La contrasena debe tener al menos 6 caracteres.");
                redirectAttributes.addFlashAttribute("cliente", cliente);
                return "redirect:/auth/registro";
            }

            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", 
                    "Las contrasenas no coinciden.");
                redirectAttributes.addFlashAttribute("cliente", cliente);
                return "redirect:/auth/registro";
            }

            if (cliente.getNombreRazonSocial() == null || cliente.getNombreRazonSocial().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", 
                    "El nombre o razon social es obligatorio.");
                redirectAttributes.addFlashAttribute("cliente", cliente);
                return "redirect:/auth/registro";
            }

            if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", 
                    "El correo electronico es obligatorio.");
                redirectAttributes.addFlashAttribute("cliente", cliente);
                return "redirect:/auth/registro";
            }

            if (cliente.getTelefono() == null || cliente.getTelefono().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", 
                    "El telefono es obligatorio.");
                redirectAttributes.addFlashAttribute("cliente", cliente);
                return "redirect:/auth/registro";
            }

            Usuario usuarioExistente = clienteService.findUsuarioByEmail(cliente.getEmail().trim());
            if (usuarioExistente != null) {
                redirectAttributes.addFlashAttribute("error", 
                    "El correo electronico ya esta registrado.");
                redirectAttributes.addFlashAttribute("cliente", cliente);
                return "redirect:/auth/registro";
            }

            if (cliente.getRuc() != null && !cliente.getRuc().trim().isEmpty()) {
                Cliente clienteRucExistente = clienteService.findByRuc(cliente.getRuc().trim());
                if (clienteRucExistente != null) {
                    redirectAttributes.addFlashAttribute("error", 
                        "El RUC ya esta registrado.");
                    redirectAttributes.addFlashAttribute("cliente", cliente);
                    return "redirect:/auth/registro";
                }
            }

            cliente.setTipoPersona("Natural");
            cliente.setEstado("Activo");
            clienteService.registrar(cliente, password);

            redirectAttributes.addFlashAttribute("mensaje", 
                "Registro exitoso. Ahora puedes iniciar sesion con tus credenciales.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al registrar el cliente: " + e.getMessage());
            redirectAttributes.addFlashAttribute("cliente", cliente);
            return "redirect:/auth/registro";
        }
    }

    @GetMapping("/verify")
    public String verificarCuenta(@RequestParam("token") String token,
                                  RedirectAttributes redirectAttributes) {
        try {
            if (token == null || token.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Token no valido.");
                redirectAttributes.addFlashAttribute("errorClass", "alert-danger");
                return "redirect:/login";
            }

            boolean tokenValido = emailService.validarTokenVerificacion(token.trim());
            
            if (!tokenValido) {
                redirectAttributes.addFlashAttribute("error", 
                    "El enlace de verificacion no es valido o ha expirado.");
                redirectAttributes.addFlashAttribute("errorClass", "alert-danger");
                return "redirect:/login";
            }

            String email = emailService.obtenerEmailPorTokenVerificacion(token.trim());
            if (email == null || email.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", 
                    "El enlace de verificacion no es valido.");
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

            if (!usuario.isActivo()) {
                usuario.setActivo(true);
                clienteService.actualizarUsuario(usuario);
            }

            emailService.invalidarTokenVerificacion(token.trim());
            
            redirectAttributes.addFlashAttribute("mensaje", 
                "Cuenta verificada exitosamente. Ahora puedes iniciar sesion.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al verificar la cuenta: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorClass", "alert-danger");
            return "redirect:/login";
        }
    }

    @PostMapping("/resend-verification")
    public String reenviarVerificacion(@RequestParam("email") String email,
                                       RedirectAttributes redirectAttributes,
                                       HttpServletRequest request) {
        try {
            if (email == null || email.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", 
                    "Debes ingresar tu correo electronico.");
                redirectAttributes.addFlashAttribute("errorClass", "alert-danger");
                return "redirect:/login";
            }

            Usuario usuario = clienteService.findUsuarioByEmail(email.trim());
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", 
                    "No existe un usuario con ese correo electronico.");
                redirectAttributes.addFlashAttribute("errorClass", "alert-danger");
                return "redirect:/login";
            }

            if (usuario.isActivo()) {
                redirectAttributes.addFlashAttribute("mensaje", 
                    "Tu cuenta ya esta verificada. Puedes iniciar sesion.");
                redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
                return "redirect:/login";
            }

            String token = emailService.generarTokenVerificacion(email.trim());
            String verifyUrl = getBaseUrl(request) + "/auth/verify?token=" + token;
            emailService.enviarCorreoVerificacion(email.trim(), verifyUrl);
            
            redirectAttributes.addFlashAttribute("mensaje", 
                "Se ha enviado un nuevo enlace de verificacion a tu correo electronico.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al reenviar la verificacion: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorClass", "alert-danger");
            return "redirect:/login";
        }
    }

    private String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        
        String baseUrl = scheme + "://" + serverName;
        if ((serverPort != 80) && (serverPort != 443)) {
            baseUrl += ":" + serverPort;
        }
        baseUrl += contextPath;
        
        return baseUrl;
    }

    public String getTipoPersonaLegible(String tipoPersona) {
        if (tipoPersona == null) return "No especificado";
        
        switch (tipoPersona) {
            case "Natural":
                return "Persona Natural";
            case "Juridica":
                return "Persona Juridica";
            default:
                return tipoPersona;
        }
    }

    public String formatearTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return "No registrado";
        }
        return telefono;
    }
}