package com.stamos.service;

import com.stamos.entity.LogAuditoria;
import com.stamos.entity.Usuario;
import com.stamos.repository.LogAuditoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AuditoriaService {

    @Autowired
    private LogAuditoriaRepository logAuditoriaRepository;
    public void log(String accion, String entidad, Long entidadId, String valorAnterior, String valorNuevo) {
        LogAuditoria log = new LogAuditoria();
        log.setAccion(accion);
        log.setEntidad(entidad);
        log.setEntidadId(entidadId);
        log.setValorAnterior(valorAnterior);
        log.setValorNuevo(valorNuevo);
        log.setFechaHora(LocalDateTime.now());

        Usuario usuario = getCurrentUser();
        if (usuario != null) {
            log.setUsuario(usuario);
            log.setUsuarioEmail(usuario.getEmail());
        } else {
            log.setUsuarioEmail("SISTEMA");
        }

        logAuditoriaRepository.save(log);
    }

    public void logSimple(String accion, String entidad, Long entidadId) {
        log(accion, entidad, entidadId, null, null);
    }

    public void logCambioEstadoProyecto(Long proyectoId, String estadoAnterior, String estadoNuevo, Long usuarioId) {
        log("CAMBIAR_ESTADO", "PROYECTO", proyectoId, estadoAnterior, estadoNuevo);
    }

    public void logAnulacionPago(Long pagoId, String motivo, String estadoAnterior) {
        log("ANULAR", "PAGO", pagoId, null, "Motivo: " + motivo);
    }

    public void logCreacion(String entidad, Long entidadId, String datos) {
        log("CREAR", entidad, entidadId, null, datos);
    }

    public void logModificacion(String entidad, Long entidadId, String oldValue, String newValue) {
        log("MODIFICAR", entidad, entidadId, oldValue, newValue);
    }

    public List<LogAuditoria> findAll() {
        return logAuditoriaRepository.findAllByOrderByFechaHoraDesc();
    }

    public List<LogAuditoria> findByEntidadAndEntidadId(String entidad, Long entidadId) {
        return logAuditoriaRepository.findByEntidadAndEntidadIdOrderByFechaHoraDesc(entidad, entidadId);
    }

    public List<LogAuditoria> findByUsuarioEmail(String usuarioEmail) {
        return logAuditoriaRepository.findByUsuarioEmailOrderByFechaHoraDesc(usuarioEmail);
    }

    public List<LogAuditoria> findByFechaHoraBetween(LocalDateTime start, LocalDateTime end) {
        return logAuditoriaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(start, end);
    }

    private Usuario getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Object principal = auth.getPrincipal();
            if (principal instanceof Usuario) {
                return (Usuario) principal;
            }
        }
        return null;
    }
}