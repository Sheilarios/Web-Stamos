package com.stamos.service;

import com.stamos.model.Notificacion;
import com.stamos.model.Usuario;
import com.stamos.model.Cliente;
import com.stamos.repository.NotificacionRepository;
import com.stamos.repository.UsuarioRepository;
import com.stamos.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${stamos.app.url:http://localhost:8080}")
    private String baseUrl;


    @Transactional
    public Notificacion crear(Notificacion notificacion) {
        notificacion.setFechaEnvio(LocalDateTime.now());
        notificacion.setEstadoEnvio("Pendiente");
        notificacion.setIntentos(0);
        notificacion.setLeido(false);

        if (notificacion.getIdUsuarioDestino() != null) {
            usuarioRepository.findById(notificacion.getIdUsuarioDestino()).ifPresent(usuario -> {
                notificacion.setNombreUsuarioDestino(usuario.getNombreCompleto());
            });
        }

        if (notificacion.getIdClienteDestino() != null) {
            clienteRepository.findById(notificacion.getIdClienteDestino()).ifPresent(cliente -> {
                notificacion.setNombreClienteDestino(cliente.getNombreRazonSocial());
            });
        }

        Notificacion saved = notificacionRepository.save(notificacion);
        enviarEmailAsync(saved);
        return saved;
    }


    @Async
    public void enviarEmailAsync(Notificacion notificacion) {
        try {
            String emailDestino = obtenerEmailDestino(notificacion);
            if (emailDestino == null || emailDestino.isEmpty()) {
                log.warn("No se encontro email de destino para notificacion ID: {}", notificacion.getIdNotificacion());
                return;
            }

            String cuerpo = construirCuerpoCorreo(notificacion);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(emailDestino);
            message.setSubject("STAMOS S.L. - " + notificacion.getAsunto());
            message.setText(cuerpo);

            mailSender.send(message);

            notificacion.setEstadoEnvio("Enviado");
            notificacionRepository.save(notificacion);
            log.info("Notificacion enviada exitosamente a: {}", emailDestino);

        } catch (Exception e) {
            log.error("Error al enviar notificacion ID {}: {}", notificacion.getIdNotificacion(), e.getMessage());
            notificacion.setEstadoEnvio("Error");
            notificacion.incrementarIntentos();
            notificacionRepository.save(notificacion);
        }
    }


    @Scheduled(fixedDelay = 300000) 
    @Transactional
    public void reintentarNotificacionesFallidas() {
        List<Notificacion> fallidas = notificacionRepository.findByEstadoEnvioAndIntentosLessThan("Error", 3);
        for (Notificacion n : fallidas) {
            n.incrementarIntentos();
            if (n.puedeReintentar()) {
                enviarEmailAsync(n);
            } else {
                n.setEstadoEnvio("Error");
                notificacionRepository.save(n);
                log.warn("Notificacion ID {} alcanzo el maximo de reintentos", n.getIdNotificacion());
            }
        }
    }


    public Page<Notificacion> findByIdUsuario(Integer idUsuario, Pageable pageable) {
        return notificacionRepository.findByIdUsuarioDestinoOrderByFechaEnvioDesc(idUsuario, pageable);
    }

    public Page<Notificacion> findByIdCliente(Integer idCliente, Pageable pageable) {
        return notificacionRepository.findByIdClienteDestinoOrderByFechaEnvioDesc(idCliente, pageable);
    }

    public Page<Notificacion> findAll(Pageable pageable) {
        return notificacionRepository.findAll(pageable);
    }

    public long countNoLeidasByUsuario(Integer idUsuario) {
        return notificacionRepository.countByIdUsuarioDestinoAndLeidoFalse(idUsuario);
    }

    public long countNoLeidasByCliente(Integer idCliente) {
        return notificacionRepository.countByIdClienteDestinoAndLeidoFalse(idCliente);
    }

    public long countNoLeidas(Integer idUsuario, Integer idCliente) {
        long count = 0;
        if (idUsuario != null) count += countNoLeidasByUsuario(idUsuario);
        if (idCliente != null) count += countNoLeidasByCliente(idCliente);
        return count;
    }

    public Notificacion findById(Integer id) {
        return notificacionRepository.findById(id).orElse(null);
    }

    public Page<Notificacion> search(String searchText, Pageable pageable) {
        return notificacionRepository.search(searchText, pageable);
    }


    @Transactional
    public void marcarLeida(Integer idNotificacion) {
        try {
            Notificacion n = notificacionRepository.findById(idNotificacion)
                    .orElseThrow(() -> new RuntimeException("Notificacion no encontrada con ID: " + idNotificacion));
            
            log.info("Marcando notificacion {} como leida", idNotificacion);
            n.marcarLeida();
            notificacionRepository.save(n);
            log.info("Notificacion {} marcada como leida exitosamente", idNotificacion);
            
        } catch (Exception e) {
            log.error("Error al marcar notificacion {} como leida: {}", idNotificacion, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void marcarTodasLeidasByUsuario(Integer idUsuario) {
        try {
            log.info("Marcando todas las notificaciones como leidas para usuario: {}", idUsuario);
            notificacionRepository.marcarTodasLeidasPorUsuario(idUsuario);
            log.info("Todas las notificaciones marcadas como leidas para usuario: {}", idUsuario);
        } catch (Exception e) {
            log.error("Error al marcar todas las notificaciones como leidas para usuario {}: {}", idUsuario, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void marcarTodasLeidasByCliente(Integer idCliente) {
        try {
            log.info("Marcando todas las notificaciones como leidas para cliente: {}", idCliente);
            notificacionRepository.marcarTodasLeidasPorCliente(idCliente);
            log.info("Todas las notificaciones marcadas como leidas para cliente: {}", idCliente);
        } catch (Exception e) {
            log.error("Error al marcar todas las notificaciones como leidas para cliente {}: {}", idCliente, e.getMessage());
            throw e;
        }
    }


    public void notificarCambioEstadoProyecto(Integer idClienteDestino,
            String nombreProyecto, String estadoAnterior, String estadoNuevo, Integer idProyecto) {
        Notificacion n = Notificacion.crearCambioEstadoProyecto(
                idClienteDestino, nombreProyecto, estadoAnterior, estadoNuevo, idProyecto);
        crear(n);
    }

    public void notificarCotizacionAprobada(Integer idClienteDestino,
            String codigoCotizacion, String nombreCliente, Integer idCotizacion) {
        Notificacion n = Notificacion.crearCotizacionAprobada(
                idClienteDestino, codigoCotizacion, nombreCliente, idCotizacion);
        crear(n);
    }

    public void notificarCotizacionAnulada(Integer idClienteDestino,
            String codigoCotizacion, String nombreCliente, String motivo, Integer idCotizacion) {
        Notificacion n = Notificacion.crearCotizacionAnulada(
                idClienteDestino, codigoCotizacion, nombreCliente, motivo, idCotizacion);
        crear(n);
    }

    public void notificarPagoValidado(Integer idClienteDestino,
            String nombreProyecto, String monto, Integer idPago) {
        Notificacion n = Notificacion.crearPagoValidado(
                idClienteDestino, nombreProyecto, monto, idPago);
        crear(n);
    }

    public void notificarPagoRechazado(Integer idClienteDestino,
            String nombreProyecto, String monto, String motivo, Integer idPago) {
        Notificacion n = Notificacion.crearPagoRechazado(
                idClienteDestino, nombreProyecto, monto, motivo, idPago);
        crear(n);
    }

    public void notificarNuevoDocumento(Integer idClienteDestino,
            String nombreProyecto, String tipoDocumento, Integer idDocumento) {
        Notificacion n = Notificacion.crearNuevoDocumento(
                idClienteDestino, nombreProyecto, tipoDocumento, idDocumento);
        crear(n);
    }

    public void notificarProyectoEntregado(Integer idClienteDestino,
            String nombreProyecto, Integer idProyecto) {
        Notificacion n = Notificacion.crearProyectoEntregado(
                idClienteDestino, nombreProyecto, idProyecto);
        crear(n);
    }

    public void notificarPagoVencido(Integer idClienteDestino,
            String nombreProyecto, String saldo, Integer idProyecto) {
        Notificacion n = Notificacion.crearPagoVencido(
                idClienteDestino, nombreProyecto, saldo, idProyecto);
        crear(n);
    }

    public void notificarProyectoSinAvance(Integer idClienteDestino,
            String nombreProyecto, Integer dias, Integer idProyecto) {
        Notificacion n = Notificacion.crearProyectoSinAvance(
                idClienteDestino, nombreProyecto, dias, idProyecto);
        crear(n);
    }


    public void notificarCambioEstadoProyectoAUsuario(Integer idUsuarioDestino,
            String nombreProyecto, String estadoAnterior, String estadoNuevo, Integer idProyecto) {
        
        String asunto = "Cambio de estado - Proyecto " + nombreProyecto;
        String mensaje = "El proyecto '" + nombreProyecto +
                "' ha cambiado de estado de '" + estadoAnterior +
                "' a '" + estadoNuevo + "'.";
        
        Notificacion n = Notificacion.paraUsuario(idUsuarioDestino, asunto, mensaje, "CAMBIO_ESTADO_PROYECTO");
        n.setIdReferencia(idProyecto);
        n.setEntidadReferencia("Proyecto");
        crear(n);
    }


    public void notificarProyectoAnulado(Integer idUsuarioDestino,
            String nombreProyecto, String motivo, Integer idProyecto) {
        Notificacion n = Notificacion.crearProyectoAnulado(
                idUsuarioDestino, nombreProyecto, motivo, idProyecto);
        crear(n);
    }


    public void notificarStockBajo(Integer idUsuarioDestino, String detalles) {
        String asunto = "ALERTA: Stock bajo en inventario";
        String mensaje = "Se ha detectado stock bajo en los siguientes productos:\n\n" + detalles;
        Notificacion n = Notificacion.paraUsuario(idUsuarioDestino, asunto, mensaje, "INVENTARIO_BAJO_STOCK");
        crear(n);
    }


    public void enviarResumenSemanal(Integer idUsuarioDestino, String resumen) {
        String asunto = "Resumen Semanal - STAMOS S.L.";
        Notificacion n = Notificacion.paraUsuario(idUsuarioDestino, asunto, resumen, "RESUMEN_SEMANAL");
        crear(n);
    }


    public void notificarConsultaCliente(Integer idVendedorDestino, 
            String nombreCliente, String nombreProyecto, Integer idProyecto) {
        String asunto = "Consulta de cliente - " + nombreProyecto;
        String mensaje = "El cliente " + nombreCliente + 
                " ha consultado el estado del proyecto '" + nombreProyecto + "'.";
        Notificacion n = Notificacion.paraUsuario(idVendedorDestino, asunto, mensaje, "CLIENTE_CONSULTA");
        n.setIdReferencia(idProyecto);
        n.setEntidadReferencia("Proyecto");
        crear(n);
    }


    public void notificarPagoRegistrado(Integer idUsuarioDestino,
            String nombreProyecto, String monto, String comprobante, Integer idPago) {
        Notificacion n = Notificacion.crearPagoRegistrado(
                idUsuarioDestino, nombreProyecto, monto, comprobante, idPago);
        crear(n);
    }

    public void notificarPagoVencidoAUsuario(Integer idUsuarioDestino,
            String nombreProyecto, String saldo, Integer idProyecto) {
        String asunto = "Pago Vencido - Proyecto " + nombreProyecto;
        String mensaje = "El pago para el proyecto '" + nombreProyecto +
                "' esta vencido. Saldo pendiente: " + saldo +
                ". Por favor, regularice su situacion.";
        Notificacion n = Notificacion.paraUsuario(idUsuarioDestino, asunto, mensaje, "PAGO_VENCIDO");
        n.setIdReferencia(idProyecto);
        n.setEntidadReferencia("Proyecto");
        crear(n);
    }


    private String obtenerEmailDestino(Notificacion notificacion) {
        if (notificacion.getIdUsuarioDestino() != null) {
            return usuarioRepository.findById(notificacion.getIdUsuarioDestino())
                    .map(Usuario::getEmail)
                    .orElse(null);
        }
        if (notificacion.getIdClienteDestino() != null) {
            return clienteRepository.findById(notificacion.getIdClienteDestino())
                    .map(Cliente::getEmail)
                    .orElse(null);
        }
        return null;
    }

    private String construirCuerpoCorreo(Notificacion notificacion) {
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


    @Scheduled(cron = "0 0 3 * * ?") 
    @Transactional
    public void limpiarNotificacionesAntiguas() {
        LocalDateTime fechaLimite = LocalDateTime.now().minusDays(90);
        int eliminadas = notificacionRepository.deleteOlderThan(fechaLimite);
        if (eliminadas > 0) {
            log.info("Se eliminaron {} notificaciones antiguas", eliminadas);
        }
    }
}