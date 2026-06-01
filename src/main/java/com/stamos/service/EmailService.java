package com.stamos.service;

import com.stamos.entity.Cotizacion;
import com.stamos.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Servicio para el envío de correos electrónicos.
 * Utiliza JavaMailSender configurado en application.properties.
 * 
 * Métodos principales:
 * - Envío de cotización al cliente (contenido simple o enlace)
 * - Notificación de cotización aprobada/rechazada
 * - Credenciales temporales para nuevos empleados
 * - Enlace de restablecimiento de contraseña
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${stamos.mail.from}")
    private String fromEmail;

    @Value("${stamos.app.url}")
    private String appUrl;

    /**
     * Envía un correo electrónico simple.
     *
     * @param to      Destinatario
     * @param subject Asunto
     * @param text    Cuerpo del mensaje
     */
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    /**
     * Envía la cotización al cliente (versión resumida o enlace).
     *
     * @param cotizacion Cotización generada
     */
    public void enviarCotizacion(Cotizacion cotizacion) {
        String clienteEmail = cotizacion.getCliente().getEmail();
        String subject = "STAMOS - Nueva cotización #" + cotizacion.getId();
        String text;
        text = String.format("""
                             Estimado(a) %s,
                             adjuntamos la cotizacion generada para su solicitud.
                             Monto total: S/ %.2f
                             Validez: %d d\u00edas
                             er el detalle completo, ingrese a su portal de cliente:
                             %s/public/cliente/login
                             s cordiales,
                             Equipo STAMOS""",
                cotizacion.getCliente().getNombreRazonSocial(),
                cotizacion.getTotal(),
                cotizacion.getValidezDias(),
                appUrl
        );
        sendEmail(clienteEmail, subject, text);
    }

    /**
     * Notifica al cliente que su cotización ha sido aprobada (por parte del cliente)
     * y que se puede proceder a crear el proyecto.
     *
     * @param cotizacion Cotización aprobada
     */
    public void enviarCotizacionAprobada(Cotizacion cotizacion) {
        String clienteEmail = cotizacion.getCliente().getEmail();
        String subject = "STAMOS - Cotización aprobada #" + cotizacion.getId();
        String text = String.format("""
                                    Estimado(a) %s,
                                    
                                    Su cotizacion ha sido aprobada. El equipo de STAMOS comenzar\u00e1 con la ejecuci\u00f3n del proyecto.
                                    Puede dar seguimiento desde su portal de cliente.
                                    
                                    Gracias por confiar en nosotros.
                                    
                                    Saludos cordiales,
                                    Equipo STAMOS""",
                cotizacion.getCliente().getNombreRazonSocial()
        );
        sendEmail(clienteEmail, subject, text);
    }

    /**
     * Notifica al cliente que su cotización ha sido rechazada (opcional).
     *
     * @param cotizacion Cotización rechazada
     * @param motivo     Motivo del rechazo
     */
    public void enviarCotizacionRechazada(Cotizacion cotizacion, String motivo) {
        String clienteEmail = cotizacion.getCliente().getEmail();
        String subject = "STAMOS - Cotización rechazada #" + cotizacion.getId();
        String text = String.format("""
                                    Estimado(a) %s,
                                    
                                    Lamentamos informarle que su cotizaci\u00f3n ha sido rechazada.
                                    Motivo: %s
                                    
                                    Si tiene alguna consulta, no dude en contactarnos.
                                    
                                    Saludos cordiales,
                                    Equipo STAMOS""",
                cotizacion.getCliente().getNombreRazonSocial(),
                motivo != null ? motivo : "No especificado"
        );
        sendEmail(clienteEmail, subject, text);
    }

    /**
     * Envía credenciales temporales a un nuevo empleado.
     *
     * @param usuario       Empleado recién creado
     * @param tempPassword  Contraseña temporal
     */
    public void enviarCredencialesEmpleado(Usuario usuario, String tempPassword) {
        String subject = "STAMOS - Credenciales de acceso al sistema";
        String text = String.format("""
                                    Hola %s,
                                    
                                    Se ha creado una cuenta para usted en el sistema de gesti\u00f3n STAMOS.
                                    Correo: %s
                                    Contrase\u00f1a temporal: %s
                                    
                                    Por seguridad, deber\u00e1 cambiar su contrase\u00f1a al primer inicio de sesi\u00f3n.
                                    Acceda al sistema: %s/internal/login
                                    
                                    Saludos cordiales,
                                    Administraci\u00f3n STAMOS""",
                usuario.getNombre(),
                usuario.getEmail(),
                tempPassword,
                appUrl
        );
        sendEmail(usuario.getEmail(), subject, text);
    }

    /**
     * Envía un enlace para restablecer la contraseña de un empleado o cliente.
     *
     * @param usuario Usuario que solicita restablecimiento
     * @param token   Token único generado
     */
    public void enviarResetPasswordEmpleado(Usuario usuario, String token) {
        String resetLink = appUrl + "/resetear-contraseña?token=" + token;
        String subject = "STAMOS - Restablecimiento de contraseña";
        String text = String.format("""
                                    Hola %s,
                                    
                                    Hemos recibido una solicitud para restablecer su contrase\u00f1a.
                                    Haga clic en el siguiente enlace para crear una nueva contrase\u00f1a:
                                    %s
                                    
                                    Este enlace es v\u00e1lido por 24 horas.
                                    
                                    Si no solicit\u00f3 este cambio, ignore este mensaje.
                                    
                                    Saludos cordiales,
                                    Equipo STAMOS""",
                usuario.getNombre(),
                resetLink
        );
        sendEmail(usuario.getEmail(), subject, text);
    }
}