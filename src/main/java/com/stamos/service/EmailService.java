package com.stamos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.mail.MailException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.url.base:http://localhost:8080}")
    private String baseUrl;

    private final Map<String, TokenRecuperacion> tokensRecuperacion = new HashMap<>();

    public void enviarCorreoSimple(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (MailException e) {
            throw new RuntimeException("Error al enviar correo: " + e.getMessage(), e);
        }
    }


    public void enviarCorreoHtml(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        mailSender.send(message);
    }

    public String generarTokenRecuperacion(String email) {
        String token = UUID.randomUUID().toString();
        TokenRecuperacion tokenRec = new TokenRecuperacion();
        tokenRec.setEmail(email);
        tokenRec.setToken(token);
        tokenRec.setFechaCreacion(LocalDateTime.now());
        tokenRec.setFechaExpiracion(LocalDateTime.now().plusHours(24)); // 24 horas de validez
        tokensRecuperacion.put(token, tokenRec);
        return token;
    }

    public boolean validarTokenRecuperacion(String token) {
        TokenRecuperacion tokenRec = tokensRecuperacion.get(token);
        if (tokenRec == null) {
            return false;
        }
        if (tokenRec.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            tokensRecuperacion.remove(token);
            return false;
        }
        return true;
    }

    public String obtenerEmailPorToken(String token) {
        TokenRecuperacion tokenRec = tokensRecuperacion.get(token);
        return tokenRec != null ? tokenRec.getEmail() : null;
    }

    public void invalidarToken(String token) {
        tokensRecuperacion.remove(token);
    }

    public void enviarCorreoRecuperacion(String email, String resetUrl) {
        String subject = "Recuperación de Contraseña - STAMOS S.L.";
        String body = construirCuerpoRecuperacion(email, resetUrl);
        enviarCorreoSimple(email, subject, body);
    }

    public void enviarCorreoRecuperacionConToken(String email, String token) {
        String resetUrl = baseUrl + "/auth/reset-password?token=" + token;
        enviarCorreoRecuperacion(email, resetUrl);
    }

    private String construirCuerpoRecuperacion(String email, String resetUrl) {
        StringBuilder sb = new StringBuilder();
        sb.append("Estimado(a) usuario de STAMOS S.L.,\n\n");
        sb.append("Hemos recibido una solicitud para restablecer la contraseña de su cuenta asociada al correo: ").append(email).append("\n\n");
        sb.append("Para restablecer su contraseña, haga clic en el siguiente enlace:\n");
        sb.append(resetUrl).append("\n\n");
        sb.append("Este enlace es válido por 24 horas.\n\n");
        sb.append("Si no solicitó este cambio, ignore este mensaje y su contraseña permanecerá sin cambios.\n\n");
        sb.append("Saludos cordiales,\n");
        sb.append("Equipo STAMOS S.L.\n");
        sb.append("----------------------------------------\n");
        sb.append("Este es un mensaje automático, por favor no responder a este correo.");
        return sb.toString();
    }

    public String generarTokenVerificacion(String email) {
        String token = UUID.randomUUID().toString();
        TokenRecuperacion tokenRec = new TokenRecuperacion();
        tokenRec.setEmail(email);
        tokenRec.setToken(token);
        tokenRec.setFechaCreacion(LocalDateTime.now());
        tokenRec.setFechaExpiracion(LocalDateTime.now().plusHours(48)); // 48 horas de validez
        tokensRecuperacion.put("verify_" + token, tokenRec);
        return token;
    }

    public boolean validarTokenVerificacion(String token) {
        TokenRecuperacion tokenRec = tokensRecuperacion.get("verify_" + token);
        if (tokenRec == null) {
            return false;
        }
        if (tokenRec.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            tokensRecuperacion.remove("verify_" + token);
            return false;
        }
        return true;
    }

    public String obtenerEmailPorTokenVerificacion(String token) {
        TokenRecuperacion tokenRec = tokensRecuperacion.get("verify_" + token);
        return tokenRec != null ? tokenRec.getEmail() : null;
    }

    public void invalidarTokenVerificacion(String token) {
        tokensRecuperacion.remove("verify_" + token);
    }

    public void enviarCorreoVerificacion(String email, String verifyUrl) {
        String subject = "Verificación de Cuenta - STAMOS S.L.";
        String body = construirCuerpoVerificacion(email, verifyUrl);
        enviarCorreoSimple(email, subject, body);
    }

    public void enviarCorreoContacto(String to, String subject, String body) {
        enviarCorreoSimple(to, subject, body);
    }

    public void enviarCorreoContactoHtml(String to, String subject, String htmlBody) throws MessagingException {
        enviarCorreoHtml(to, subject, htmlBody);
    }

    public void enviarNotificacionCambioEstado(String to, String nombreProyecto,
                                               String estadoAnterior, String estadoNuevo) {
        String subject = "Cambio de Estado - Proyecto " + nombreProyecto;
        String body = construirCuerpoNotificacionCambioEstado(nombreProyecto, estadoAnterior, estadoNuevo);
        enviarCorreoSimple(to, subject, body);
    }

    private String construirCuerpoNotificacionCambioEstado(String nombreProyecto,
                                                          String estadoAnterior, String estadoNuevo) {
        StringBuilder sb = new StringBuilder();
        sb.append("Estimado(a) cliente de STAMOS S.L.,\n\n");
        sb.append("Le informamos que el estado de su proyecto \"").append(nombreProyecto).append("\" ha cambiado.\n\n");
        sb.append("Estado anterior: ").append(estadoAnterior).append("\n");
        sb.append("Estado nuevo: ").append(estadoNuevo).append("\n\n");
        sb.append("Puede consultar los detalles de su proyecto en nuestro portal web.\n\n");
        sb.append("Saludos cordiales,\n");
        sb.append("Equipo STAMOS S.L.\n");
        sb.append("----------------------------------------\n");
        sb.append("Este es un mensaje automático, por favor no responder a este correo.");
        return sb.toString();
    }

    public void enviarNotificacionPagoValidado(String to, String nombreProyecto,
                                               String monto, String numeroComprobante) {
        String subject = "Pago Validado - Proyecto " + nombreProyecto;
        String body = construirCuerpoNotificacionPagoValidado(nombreProyecto, monto, numeroComprobante);
        enviarCorreoSimple(to, subject, body);
    }

    private String construirCuerpoNotificacionPagoValidado(String nombreProyecto,
                                                          String monto, String numeroComprobante) {
        StringBuilder sb = new StringBuilder();
        sb.append("Estimado(a) cliente de STAMOS S.L.,\n\n");
        sb.append("Le informamos que su pago ha sido validado exitosamente.\n\n");
        sb.append("Proyecto: ").append(nombreProyecto).append("\n");
        sb.append("Monto: ").append(monto).append("\n");
        sb.append("Comprobante: ").append(numeroComprobante).append("\n\n");
        sb.append("Puede consultar el historial de pagos en nuestro portal web.\n\n");
        sb.append("Saludos cordiales,\n");
        sb.append("Equipo STAMOS S.L.\n");
        sb.append("----------------------------------------\n");
        sb.append("Este es un mensaje automático, por favor no responder a este correo.");
        return sb.toString();
    }

    public void enviarNotificacionProyectoEntregado(String to, String nombreProyecto) {
        String subject = "Proyecto Entregado - " + nombreProyecto;
        String body = construirCuerpoNotificacionProyectoEntregado(nombreProyecto);
        enviarCorreoSimple(to, subject, body);
    }

    private String construirCuerpoNotificacionProyectoEntregado(String nombreProyecto) {
        StringBuilder sb = new StringBuilder();
        sb.append("Estimado(a) cliente de STAMOS S.L.,\n\n");
        sb.append("¡Nos complace informarle que su proyecto \"").append(nombreProyecto).append("\" ha sido entregado!\n\n");
        sb.append("Agradecemos su confianza en STAMOS S.L. y esperamos poder colaborar con usted en futuros proyectos.\n\n");
        sb.append("Saludos cordiales,\n");
        sb.append("Equipo STAMOS S.L.\n");
        sb.append("----------------------------------------\n");
        sb.append("Este es un mensaje automático, por favor no responder a este correo.");
        return sb.toString();
    }

    private String construirCuerpoVerificacion(String email, String verifyUrl) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }


    private static class TokenRecuperacion {
        private String email;
        private String token;
        private LocalDateTime fechaCreacion;
        private LocalDateTime fechaExpiracion;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public LocalDateTime getFechaCreacion() { return fechaCreacion; }
        public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

        public LocalDateTime getFechaExpiracion() { return fechaExpiracion; }
        public void setFechaExpiracion(LocalDateTime fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }
    }

    public void enviarResumenCotizacion(String to, Integer cotizacionId,
                                       String clienteNombre, String total) {
        String subject = "Cotización STAMOS S.L. - COT-" + String.format("%05d", cotizacionId);
        String body = construirCuerpoResumenCotizacion(cotizacionId, clienteNombre, total);
        enviarCorreoSimple(to, subject, body);
    }


    private String construirCuerpoResumenCotizacion(Integer cotizacionId,
                                                    String clienteNombre, String total) {
        StringBuilder sb = new StringBuilder();
        sb.append("Estimado(a) ").append(clienteNombre).append(",\n\n");
        sb.append("Hemos generado una nueva cotización para usted.\n\n");
        sb.append("Cotización: COT-").append(String.format("%05d", cotizacionId)).append("\n");
        sb.append("Total: ").append(total).append("\n\n");
        sb.append("Para ver los detalles completos, por favor inicie sesión en nuestro portal.\n\n");
        sb.append("Saludos cordiales,\n");
        sb.append("Equipo STAMOS S.L.\n");
        sb.append("----------------------------------------\n");
        sb.append("Este es un mensaje automático, por favor no responder a este correo.");
        return sb.toString();
    }


    public void enviarCorreoPrueba(String to) {
        String subject = "Correo de Prueba - STAMOS S.L.";
        String body = """
                      Este es un correo de prueba para verificar la configuraci\u00f3n del servicio de correo de STAMOS S.L.
                      
                      Fecha: """ + LocalDateTime.now() + "\n\n" +
                     "Si recibió este mensaje, la configuración es correcta.\n\n" +
                     "Saludos cordiales,\n" +
                     "Equipo STAMOS S.L.";
        enviarCorreoSimple(to, subject, body);
    }
}