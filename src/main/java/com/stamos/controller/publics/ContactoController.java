package com.stamos.controller.publics;

import com.stamos.controller.BaseController;
import com.stamos.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Controller
@RequestMapping("/contacto")
public class ContactoController extends BaseController {

    @Autowired
    private EmailService emailService;

    @GetMapping
    public String mostrarContacto(Model model) {
        model.addAttribute("empresa", "STAMOS LINARES S.R.L.");
        model.addAttribute("ruc", "20549500715");
        model.addAttribute("direccion", "Jr. Antisuyo 438, Urbanización Zárate, San Juan de Lurigancho, Lima, Perú");
        model.addAttribute("telefono", "01 7046345");
        model.addAttribute("celular", "987819456");
        model.addAttribute("celular2", "987181168");
        model.addAttribute("email", "info@stamos.com");
        model.addAttribute("horario", "Lunes a Viernes: 9:00 AM - 6:00 PM");
        model.addAttribute("horarioSabado", "Sábado: 9:00 AM - 1:00 PM");
        model.addAttribute("mapaLatitud", "-12.0453");
        model.addAttribute("mapaLongitud", "-77.0312");
        addCommonAttributes(model);
        return "public/contacto";
    }

    @PostMapping("/enviar")
    public String enviarMensaje(@RequestParam("nombre") @NotBlank(message = "El nombre es obligatorio") String nombre,
                                @RequestParam("email") @NotBlank(message = "El email es obligatorio") @Email(message = "Email inválido") String email,
                                @RequestParam(value = "telefono", required = false) String telefono,
                                @RequestParam("asunto") @NotBlank(message = "El asunto es obligatorio") @Size(max = 100, message = "El asunto no puede exceder los 100 caracteres") String asunto,
                                @RequestParam("mensaje") @NotBlank(message = "El mensaje es obligatorio") @Size(max = 1000, message = "El mensaje no puede exceder los 1000 caracteres") String mensaje,
                                RedirectAttributes redirectAttributes) {
        try {
            if (nombre == null || nombre.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Por favor, ingresa tu nombre.");
                return "redirect:/contacto";
            }

            if (email == null || email.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Por favor, ingresa tu correo electrónico.");
                return "redirect:/contacto";
            }

            if (asunto == null || asunto.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Por favor, ingresa un asunto.");
                return "redirect:/contacto";
            }

            if (mensaje == null || mensaje.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Por favor, ingresa tu mensaje.");
                return "redirect:/contacto";
            }

            String cuerpoMensaje = construirMensajeContacto(nombre, email, telefono, asunto, mensaje);
            String asuntoCorreo = "Nuevo mensaje de contacto - " + asunto;
            
            emailService.enviarCorreoContacto("info@stamos.com", asuntoCorreo, cuerpoMensaje);
            emailService.enviarCorreoContacto(email, "Confirmación - STAMOS S.L.", 
                construirMensajeConfirmacion(nombre, asunto));

            redirectAttributes.addFlashAttribute("mensaje", 
                "¡Gracias por contactarnos! Hemos recibido tu mensaje y te responderemos a la brevedad.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/contacto#formulario";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al enviar el mensaje: " + e.getMessage());
            return "redirect:/contacto";
        }
    }

    private String construirMensajeContacto(String nombre, String email, String telefono, String asunto, String mensaje) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("  NUEVO MENSAJE DE CONTACTO - STAMOS S.L.\n");
        sb.append("========================================\n\n");
        
        sb.append(" DATOS DEL REMITENTE:\n");
        sb.append("────────────────────────────────────────\n");
        sb.append("Nombre:      ").append(nombre).append("\n");
        sb.append("Email:       ").append(email).append("\n");
        if (telefono != null && !telefono.trim().isEmpty()) {
            sb.append("Teléfono:    ").append(telefono).append("\n");
        }
        sb.append("\n");
        
        sb.append(" DETALLES DEL MENSAJE:\n");
        sb.append("────────────────────────────────────────\n");
        sb.append("Asunto:      ").append(asunto).append("\n");
        sb.append("Fecha:       ").append(java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\n");
        sb.append("\n");
        
        sb.append(" MENSAJE:\n");
        sb.append("────────────────────────────────────────\n");
        sb.append(mensaje).append("\n\n");
        
        sb.append("========================================\n");
        sb.append("  Este mensaje fue enviado desde el formulario de contacto de STAMOS S.L.\n");
        sb.append("========================================\n");
        
        return sb.toString();
    }

    private String construirMensajeConfirmacion(String nombre, String asunto) {
        StringBuilder sb = new StringBuilder();
        sb.append("Estimado(a) ").append(nombre).append(",\n\n");
        sb.append("Hemos recibido tu mensaje con el asunto: \"").append(asunto).append("\".\n\n");
        sb.append("Agradecemos tu interés en STAMOS S.L. Nuestro equipo revisará tu consulta y te responderá a la brevedad.\n\n");
        sb.append(" Información de contacto:\n");
        sb.append("────────────────────────────────────────\n");
        sb.append("Teléfono:   01 7046345\n");
        sb.append("Celular:    987819456 / 987181168\n");
        sb.append("Dirección:  Jr. Antisuyo 438, Urbanización Zárate\n");
        sb.append("            San Juan de Lurigancho, Lima, Perú\n\n");
        sb.append("Horario de atención:\n");
        sb.append("Lunes a Viernes: 9:00 AM - 6:00 PM\n");
        sb.append("Sábado: 9:00 AM - 1:00 PM\n\n");
        sb.append("Saludos cordiales,\n");
        sb.append("Equipo STAMOS S.L.\n");
        sb.append("----------------------------------------\n");
        sb.append("Este es un mensaje automático, por favor no responder a este correo.");
        
        return sb.toString();
    }

    @GetMapping("/info")
    public String getInfoEmpresa(Model model) {
        model.addAttribute("empresa", "STAMOS LINARES S.R.L.");
        model.addAttribute("ruc", "20549500715");
        model.addAttribute("direccion", "Jr. Antisuyo 438, Urbanización Zárate, San Juan de Lurigancho, Lima, Perú");
        model.addAttribute("telefono", "01 7046345");
        model.addAttribute("celular", "987819456");
        model.addAttribute("celular2", "987181168");
        model.addAttribute("email", "info@stamos.com");
        model.addAttribute("horario", "Lunes a Viernes: 9:00 AM - 6:00 PM");
        model.addAttribute("horarioSabado", "Sábado: 9:00 AM - 1:00 PM");
        addCommonAttributes(model);
        return "public/contacto :: info";
    }

    public String getHorarios() {
        return "Lunes a Viernes: 9:00 AM - 6:00 PM\nSábado: 9:00 AM - 1:00 PM";
    }

    public String getTelefonoPrincipal() {
        return "01 7046345";
    }

    public String getCelulares() {
        return "987819456 / 987181168";
    }

    public String getDireccion() {
        return "Jr. Antisuyo 438, Urbanización Zárate, San Juan de Lurigancho, Lima, Perú";
    }

    public String getEmailContacto() {
        return "info@stamos.com";
    }

    public static class MensajeContacto {
        
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
        private String nombre;
        
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Email inválido")
        private String email;
        
        @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres")
        private String telefono;
        
        @NotBlank(message = "El asunto es obligatorio")
        @Size(max = 100, message = "El asunto no puede exceder los 100 caracteres")
        private String asunto;
        
        @NotBlank(message = "El mensaje es obligatorio")
        @Size(max = 1000, message = "El mensaje no puede exceder los 1000 caracteres")
        private String mensaje;

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getTelefono() { return telefono; }
        public void setTelefono(String telefono) { this.telefono = telefono; }
        
        public String getAsunto() { return asunto; }
        public void setAsunto(String asunto) { this.asunto = asunto; }
        
        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    }

    @PostMapping("/enviar-dto")
    public String enviarMensajeDto(@Valid MensajeContacto mensajeContacto,
                                   RedirectAttributes redirectAttributes) {
        try {
            String cuerpoMensaje = construirMensajeContacto(
                mensajeContacto.getNombre(),
                mensajeContacto.getEmail(),
                mensajeContacto.getTelefono(),
                mensajeContacto.getAsunto(),
                mensajeContacto.getMensaje()
            );
            
            emailService.enviarCorreoContacto("info@stamos.com",
                "Nuevo mensaje de contacto - " + mensajeContacto.getAsunto(),
                cuerpoMensaje
            );

            emailService.enviarCorreoContacto(mensajeContacto.getEmail(),
                "Confirmación - STAMOS S.L.",
                construirMensajeConfirmacion(mensajeContacto.getNombre(), mensajeContacto.getAsunto())
            );

            redirectAttributes.addFlashAttribute("mensaje", 
                "¡Gracias por contactarnos! Hemos recibido tu mensaje y te responderemos a la brevedad.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/contacto#formulario";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al enviar el mensaje: " + e.getMessage());
            return "redirect:/contacto";
        }
    }
}