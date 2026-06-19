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

/**
 * Servicio para el envío de correos electrónicos.
 * 
 * <p>Este servicio proporciona métodos para enviar correos electrónicos
 * relacionados con recuperación de contraseña, notificaciones, confirmaciones
 * y contacto.</p>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.url.base:http://localhost:8080}")
    private String baseUrl;

    // Almacenamiento temporal de tokens (en producción usar Redis o base de datos)
    private final Map<String, TokenRecuperacion> tokensRecuperacion = new HashMap<>();

    // ============================================
    // ENVÍO DE CORREOS BÁSICOS
    // ============================================

    /**
     * Envía un correo electrónico simple.
     * 
     * @param to Destinatario
     * @param subject Asunto
     * @param body Cuerpo del mensaje
     */
    public void enviarCorreoSimple(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar correo: " + e.getMessage(), e);
        }
    }

    /**
     * Envía un correo electrónico en formato HTML.
     * 
     * @param to Destinatario
     * @param subject Asunto
     * @param htmlBody Cuerpo del mensaje en HTML
     * @throws MessagingException si ocurre un error al crear el mensaje
     */
    public void enviarCorreoHtml(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        mailSender.send(message);
    }

    // ============================================
    // RECUPERACIÓN DE CONTRASEÑA
    // ============================================

    /**
     * Genera un token de recuperación de contraseña.
     * 
     * @param email Email del usuario
     * @return Token generado
     */
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

    /**
     * Valida un token de recuperación de contraseña.
     * 
     * @param token Token a validar
     * @return true si es válido, false en caso contrario
     */
    public boolean validarTokenRecuperacion(String token) {
        TokenRecuperacion tokenRec = tokensRecuperacion.get(token);
        if (tokenRec == null) {
            return false;
        }
        // Verificar si el token ha expirado
        if (tokenRec.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            tokensRecuperacion.remove(token);
            return false;
        }
        return true;
    }

    /**
     * Obtiene el email asociado a un token de recuperación.
     * 
     * @param token Token de recuperación
     * @return Email del usuario, o null si el token no existe
     */
    public String obtenerEmailPorToken(String token) {
        TokenRecuperacion tokenRec = tokensRecuperacion.get(token);
        return tokenRec != null ? tokenRec.getEmail() : null;
    }

    /**
     * Invalida un token de recuperación.
     * 
     * @param token Token a invalidar
     */
    public void invalidarToken(String token) {
        tokensRecuperacion.remove(token);
    }

    /**
     * Envía un correo de recuperación de contraseña.
     * 
     * @param email Email del usuario
     * @param resetUrl URL de restablecimiento de contraseña
     */
    public void enviarCorreoRecuperacion(String email, String resetUrl) {
        String subject = "Recuperación de Contraseña - STAMOS S.L.";
        String body = construirCuerpoRecuperacion(email, resetUrl);
        enviarCorreoSimple(email, subject, body);
    }

    /**
     * Envía un correo de recuperación de contraseña con el token.
     * 
     * @param email Email del usuario
     * @param token Token de recuperación
     */
    public void enviarCorreoRecuperacionConToken(String email, String token) {
        String resetUrl = baseUrl + "/auth/reset-password?token=" + token;
        enviarCorreoRecuperacion(email, resetUrl);
    }

    /**
     * Construye el cuerpo del correo de recuperación de contraseña.
     * 
     * @param email Email del usuario
     * @param resetUrl URL de restablecimiento
     * @return Cuerpo del correo
     */
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

    // ============================================
    // VERIFICACIÓN DE CUENTA
    // ============================================

    /**
     * Genera un token de verificación de cuenta.
     * 
     * @param email Email del usuario
     * @return Token generado
     */
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

    /**
     * Valida un token de verificación de cuenta.
     * 
     * @param token Token a validar
     * @return true si es válido, false en caso contrario
     */
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

    /**
     * Obtiene el email asociado a un token de verificación.
     * 
     * @param token Token de verificación
     * @return Email del usuario, o null si el token no existe
     */
    public String obtenerEmailPorTokenVerificacion(String token) {
        TokenRecuperacion tokenRec = tokensRecuperacion.get("verify_" + token);
        return tokenRec != null ? tokenRec.getEmail() : null;
    }

    /**
     * Invalida un token de verificación.
     * 
     * @param token Token a invalidar
     */
    public void invalidarTokenVerificacion(String token) {
        tokensRecuperacion.remove("verify_" + token);
    }

    /**
     * Envía un correo de verificación de cuenta.
     * 
     * @param email Email del usuario
     * @param verifyUrl URL de verificación
     */
    public void enviarCorreoVerificacion(String email, String verifyUrl) {
        String subject = "Verificación de Cuenta - STAMOS S.L.";
        String body = construirCuerpoVerificacion(email, verifyUrl);
        enviarCorreoSimple(email, subject, body);
    }

    /**
     * Construye el cuerpo del correo de verificación de cuenta.
     * 
     * @param email Email del usuario
     * @param verifyUrl URL de verificación
     * @return Cuerpo del correo
     */
    private String construirCuerpoVerificacion(String email, String verifyUrl) {
        StringBuilder sb = new StringBuilder();
        sb.append("Bienvenido(a) a STAMOS S.L.,\n\n");
        sb.append("Gracias por registrarte en nuestro sistema. Para activar tu cuenta, por favor verifica tu correo electrónico haciendo clic en el siguiente enlace:\n\n");
        sb.append(verifyUrl).append("\n\n");
        sb.append("Este enlace es válido por 48 horas.\n\n");
        sb.append("Si no te registraste en STAMOS S.L., ignora este mensaje.\n\n");
        sb.append("Saludos cordiales,\n");
        sb.append("Equipo STAMOS S.L.\n");
        sb.append("----------------------------------------\n");
        sb.append("Este es un mensaje automático, por favor no responder a este correo.");
        return sb.toString();
    }

    // ============================================
    // CONTACTO
    // ============================================

    /**
     * Envía un correo de contacto a la empresa.
     * 
     * @param to Destinatario (email de la empresa)
     * @param subject Asunto
     * @param body Cuerpo del mensaje
     */
    public void enviarCorreoContacto(String to, String subject, String body) {
        enviarCorreoSimple(to, subject, body);
    }

    /**
     * Envía un correo de contacto con formato HTML.
     * 
     * @param to Destinatario
     * @param subject Asunto
     * @param htmlBody Cuerpo del mensaje en HTML
     * @throws MessagingException si ocurre un error al crear el mensaje
     */
    public void enviarCorreoContactoHtml(String to, String subject, String htmlBody) throws MessagingException {
        enviarCorreoHtml(to, subject, htmlBody);
    }

    // ============================================
    // NOTIFICACIONES
    // ============================================

    /**
     * Envía una notificación de cambio de estado de proyecto.
     * 
     * @param to Destinatario
     * @param nombreProyecto Nombre del proyecto
     * @param estadoAnterior Estado anterior
     * @param estadoNuevo Estado nuevo
     */
    public void enviarNotificacionCambioEstado(String to, String nombreProyecto,
                                               String estadoAnterior, String estadoNuevo) {
        String subject = "Cambio de Estado - Proyecto " + nombreProyecto;
        String body = construirCuerpoNotificacionCambioEstado(nombreProyecto, estadoAnterior, estadoNuevo);
        enviarCorreoSimple(to, subject, body);
    }

    /**
     * Construye el cuerpo de la notificación de cambio de estado.
     * 
     * @param nombreProyecto Nombre del proyecto
     * @param estadoAnterior Estado anterior
     * @param estadoNuevo Estado nuevo
     * @return Cuerpo del correo
     */
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

    /**
     * Envía una notificación de pago validado.
     * 
     * @param to Destinatario
     * @param nombreProyecto Nombre del proyecto
     * @param monto Monto del pago
     * @param numeroComprobante Número de comprobante
     */
    public void enviarNotificacionPagoValidado(String to, String nombreProyecto,
                                               String monto, String numeroComprobante) {
        String subject = "Pago Validado - Proyecto " + nombreProyecto;
        String body = construirCuerpoNotificacionPagoValidado(nombreProyecto, monto, numeroComprobante);
        enviarCorreoSimple(to, subject, body);
    }

    /**
     * Construye el cuerpo de la notificación de pago validado.
     * 
     * @param nombreProyecto Nombre del proyecto
     * @param monto Monto del pago
     * @param numeroComprobante Número de comprobante
     * @return Cuerpo del correo
     */
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

    /**
     * Envía una notificación de proyecto entregado.
     * 
     * @param to Destinatario
     * @param nombreProyecto Nombre del proyecto
     */
    public void enviarNotificacionProyectoEntregado(String to, String nombreProyecto) {
        String subject = "Proyecto Entregado - " + nombreProyecto;
        String body = construirCuerpoNotificacionProyectoEntregado(nombreProyecto);
        enviarCorreoSimple(to, subject, body);
    }

    /**
     * Construye el cuerpo de la notificación de proyecto entregado.
     * 
     * @param nombreProyecto Nombre del proyecto
     * @return Cuerpo del correo
     */
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

    // ============================================
    // CLASE INTERNA PARA TOKENS
    // ============================================

    /**
     * Clase interna para almacenar tokens de recuperación.
     */
    private static class TokenRecuperacion {
        private String email;
        private String token;
        private LocalDateTime fechaCreacion;
        private LocalDateTime fechaExpiracion;

        // Getters y Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public LocalDateTime getFechaCreacion() { return fechaCreacion; }
        public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

        public LocalDateTime getFechaExpiracion() { return fechaExpiracion; }
        public void setFechaExpiracion(LocalDateTime fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }
    }

    // ============================================
    // MÉTODOS DE UTILIDAD
    // ============================================

    /**
     * Envía un correo con el resumen de una cotización.
     * 
     * @param to Destinatario
     * @param cotizacionId ID de la cotización
     * @param clienteNombre Nombre del cliente
     * @param total Total de la cotización
     */
    public void enviarResumenCotizacion(String to, Integer cotizacionId,
                                       String clienteNombre, String total) {
        String subject = "Cotización STAMOS S.L. - COT-" + String.format("%05d", cotizacionId);
        String body = construirCuerpoResumenCotizacion(cotizacionId, clienteNombre, total);
        enviarCorreoSimple(to, subject, body);
    }

    /**
     * Construye el cuerpo del resumen de cotización.
     * 
     * @param cotizacionId ID de la cotización
     * @param clienteNombre Nombre del cliente
     * @param total Total de la cotización
     * @return Cuerpo del correo
     */
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

    /**
     * Envía un correo de prueba para verificar la configuración.
     * 
     * @param to Destinatario
     */
    public void enviarCorreoPrueba(String to) {
        String subject = "Correo de Prueba - STAMOS S.L.";
        String body = "Este es un correo de prueba para verificar la configuración del servicio de correo de STAMOS S.L.\n\n" +
                     "Fecha: " + LocalDateTime.now() + "\n\n" +
                     "Si recibió este mensaje, la configuración es correcta.\n\n" +
                     "Saludos cordiales,\n" +
                     "Equipo STAMOS S.L.";
        enviarCorreoSimple(to, subject, body);
    }
}