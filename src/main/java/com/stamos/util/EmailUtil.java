package com.stamos.util;

import com.stamos.model.Notificacion;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;

@Component
public class EmailUtil {

    private static final Logger log = LoggerFactory.getLogger(EmailUtil.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Value("${stamos.app.url:http://localhost:8080}")
    private String baseUrl;

    public EmailUtil(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean enviarCorreo(String to, String subject, String body) {
        if (to == null || to.isEmpty()) {
            log.warn("No se puede enviar correo: destinatario vacio");
            return false;
        }

        if (fromEmail == null || fromEmail.isEmpty()) {
            log.warn("No se puede enviar correo: email remitente no configurado");
            return false;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Correo enviado exitosamente a: {}", to);
            return true;
        } catch (MailException e) {
            log.error("Error al enviar correo a {}: {}", to, e.getMessage());
            return false;
        }
    }

    public String construirCuerpoNotificacion(Notificacion notificacion) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append(" STAMOS S.L. - NOTIFICACION\n");
        sb.append("========================================\n\n");
        sb.append("Asunto: ").append(notificacion.getAsunto()).append("\n\n");
        sb.append(notificacion.getMensaje()).append("\n\n");
        sb.append("Tipo: ").append(notificacion.getTipoEventoLegible()).append("\n");
        sb.append("Fecha: ").append(notificacion.getFechaEnvioFormateada()).append("\n\n");

        if (notificacion.getIdReferencia() != null) {
            sb.append("Referencia: ").append(notificacion.getIdReferencia());
            if (notificacion.getEntidadReferencia() != null) {
                sb.append(" (").append(notificacion.getEntidadReferencia()).append(")");
            }
            sb.append("\n\n");
        }

        sb.append("Para mas detalles, ingresa al portal de STAMOS:\n");
        sb.append(baseUrl).append("\n\n");
        sb.append("========================================\n");
        sb.append("Este es un mensaje automatico, no responder a este correo.\n");
        sb.append("========================================\n");
        return sb.toString();
    }

    public String construirCuerpoNotificacionHtml(Notificacion notificacion) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html>\n");
        sb.append("<head><meta charset=\"UTF-8\"></head>\n");
        sb.append("<body style=\"font-family: 'Segoe UI', Arial, sans-serif; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;\">\n");
        sb.append("<div style=\"background: #000000; padding: 20px; text-align: center; border-radius: 10px 10px 0 0;\">\n");
        sb.append("  <h2 style=\"color: #FFFFFF; margin: 0;\">STAMOS <span style=\"color: #F5C400;\">.</span></h2>\n");
        sb.append("  <p style=\"color: #C0C0C0; margin: 4px 0 0; font-size: 12px;\">Sistema de Gestion Empresarial</p>\n");
        sb.append("</div>\n");
        sb.append("<div style=\"background: #FFFFFF; padding: 24px; border: 1px solid #E8E8E8; border-top: none; border-radius: 0 0 10px 10px;\">\n");
        sb.append("  <h3 style=\"color: #1A1F2E; margin-top: 0;\">").append(notificacion.getAsunto()).append("</h3>\n");
        sb.append("  <div style=\"background: #F8F8F8; padding: 16px; border-radius: 8px; margin: 16px 0;\">\n");
        sb.append("    <p style=\"margin: 0; line-height: 1.6;\">").append(notificacion.getMensaje().replace("\n", "<br>")).append("</p>\n");
        sb.append("  </div>\n");
        sb.append("  <table style=\"width: 100%; font-size: 13px;\">\n");
        sb.append("    <tr><td style=\"padding: 4px 0; color: #8A8A8A;\"><strong>Tipo:</strong></td><td style=\"padding: 4px 0;\">").append(notificacion.getTipoEventoLegible()).append("</td></tr>\n");
        sb.append("    <tr><td style=\"padding: 4px 0; color: #8A8A8A;\"><strong>Fecha:</strong></td><td style=\"padding: 4px 0;\">").append(notificacion.getFechaEnvioFormateada()).append("</td></tr>\n");
        if (notificacion.getIdReferencia() != null) {
            sb.append("    <tr><td style=\"padding: 4px 0; color: #8A8A8A;\"><strong>Referencia:</strong></td><td style=\"padding: 4px 0;\">").append(notificacion.getIdReferencia());
            if (notificacion.getEntidadReferencia() != null) {
                sb.append(" (").append(notificacion.getEntidadReferencia()).append(")");
            }
            sb.append("</td></tr>\n");
        }
        sb.append("  </table>\n");
        sb.append("  <div style=\"margin-top: 20px; padding-top: 16px; border-top: 1px solid #E8E8E8; text-align: center;\">\n");
        sb.append("    <a href=\"").append(baseUrl).append("/notificaciones\" style=\"display: inline-block; background: #F5C400; color: #000000; padding: 10px 24px; border-radius: 8px; text-decoration: none; font-weight: 600;\">Ver en el portal</a>\n");
        sb.append("  </div>\n");
        sb.append("  <p style=\"font-size: 11px; color: #8A8A8A; text-align: center; margin-top: 20px; border-top: 1px solid #E8E8E8; padding-top: 16px;\">\n");
        sb.append("    Este es un mensaje automatico, por favor no responder a este correo.\n");
        sb.append("  </p>\n");
        sb.append("</div>\n");
        sb.append("</body>\n");
        sb.append("</html>\n");
        return sb.toString();
    }

    public boolean enviarCorreoHtml(String to, String subject, String htmlBody) {
        if (to == null || to.isEmpty()) {
            log.warn("No se puede enviar correo HTML: destinatario vacio");
            return false;
        }

        if (fromEmail == null || fromEmail.isEmpty()) {
            log.warn("No se puede enviar correo HTML: email remitente no configurado");
            return false;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Correo HTML enviado exitosamente a: {}", to);
            return true;
        } catch (MessagingException | MailException e) {
            log.error("Error al enviar correo HTML a {}: {}", to, e.getMessage());
            return false;
        }
    }

    public boolean enviarNotificacion(String to, Notificacion notificacion) {
        if (to == null || to.isEmpty()) {
            log.warn("No se puede enviar notificacion: destinatario vacio");
            return false;
        }

        String subject = "STAMOS S.L. - " + notificacion.getAsunto();
        String body = construirCuerpoNotificacion(notificacion);
        return enviarCorreo(to, subject, body);
    }

    public boolean enviarNotificacionHtml(String to, Notificacion notificacion) {
        if (to == null || to.isEmpty()) {
            log.warn("No se puede enviar notificacion HTML: destinatario vacio");
            return false;
        }

        String subject = "STAMOS S.L. - " + notificacion.getAsunto();
        String htmlBody = construirCuerpoNotificacionHtml(notificacion);
        return enviarCorreoHtml(to, subject, htmlBody);
    }

    public boolean isEmailConfigured() {
        return fromEmail != null && !fromEmail.isEmpty() && 
               !"stamos@email.com".equals(fromEmail) &&
               !"tu-email@gmail.com".equals(fromEmail) &&
               !"tu-email@email.com".equals(fromEmail);
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public boolean enviarCorreoPrueba(String to) {
        String subject = "Correo de Prueba - STAMOS S.L.";
        String body = construirCuerpoCorreoPrueba();
        return enviarCorreo(to, subject, body);
    }

    private String construirCuerpoCorreoPrueba() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append(" STAMOS S.L. - CORREO DE PRUEBA\n");
        sb.append("========================================\n\n");
        sb.append("Este es un correo de prueba para verificar la configuracion del servicio de correo.\n\n");
        sb.append("Fecha: ").append(java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\n\n");
        sb.append("Si recibio este mensaje, la configuracion es correcta.\n\n");
        sb.append("Saludos cordiales,\n");
        sb.append("Equipo STAMOS S.L.\n");
        sb.append("========================================\n");
        return sb.toString();
    }
}