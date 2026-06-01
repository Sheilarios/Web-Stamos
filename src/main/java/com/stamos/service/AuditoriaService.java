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

/**
 * Servicio para la gestión de auditoría.
 * Registra todas las acciones críticas (creación, modificación, anulación)
 * incluyendo quién, cuándo, qué entidad, valores anteriores y nuevos.
 * 
 * Reglas de negocio:
 * - RRE05: Todas las acciones críticas deben quedar registradas en una tabla de logs.
 * - RF-31: Registro obligatorio de fecha, hora, usuario, acción, valores anterior y nuevo.
 */
@Service
@Transactional
public class AuditoriaService {

    @Autowired
    private LogAuditoriaRepository logAuditoriaRepository;

    /**
     * Registra una acción de auditoría.
     *
     * @param accion       Acción realizada (CREAR, MODIFICAR, ANULAR, CAMBIAR_ESTADO, etc.)
     * @param entidad      Nombre de la entidad afectada (ej. "CLIENTE", "COTIZACION", "PROYECTO", "PAGO", "USUARIO")
     * @param entidadId    ID de la entidad afectada (puede ser null si no aplica)
     * @param valorAnterior Valor anterior (en formato string o JSON)
     * @param valorNuevo    Valor nuevo (en formato string o JSON)
     */
    public void log(String accion, String entidad, Long entidadId, String valorAnterior, String valorNuevo) {
        LogAuditoria log = new LogAuditoria();
        log.setAccion(accion);
        log.setEntidad(entidad);
        log.setEntidadId(entidadId);
        log.setValorAnterior(valorAnterior);
        log.setValorNuevo(valorNuevo);
        log.setFechaHora(LocalDateTime.now());

        // Obtener usuario autenticado
        Usuario usuario = getCurrentUser();
        if (usuario != null) {
            log.setUsuario(usuario);
            log.setUsuarioEmail(usuario.getEmail());
        } else {
            log.setUsuarioEmail("SISTEMA");
        }

        logAuditoriaRepository.save(log);
    }

    /**
     * Registra una acción de auditoría sin valores anteriores/ nuevos (ej. eliminación lógica).
     */
    public void logSimple(String accion, String entidad, Long entidadId) {
        log(accion, entidad, entidadId, null, null);
    }

    /**
     * Registra un cambio de estado de proyecto (caso especial).
     *
     * @param proyectoId    ID del proyecto
     * @param estadoAnterior Estado anterior
     * @param estadoNuevo    Estado nuevo
     */
    public void logCambioEstadoProyecto(Long proyectoId, String estadoAnterior, String estadoNuevo) {
        log("CAMBIAR_ESTADO", "PROYECTO", proyectoId, estadoAnterior, estadoNuevo);
    }

    /**
     * Registra una anulación de pago con motivo.
     *
     * @param pagoId ID del pago
     * @param motivo Motivo de anulación
     */
    public void logAnulacionPago(Long pagoId, String motivo) {
        log("ANULAR", "PAGO", pagoId, null, "Motivo: " + motivo);
    }

    /**
     * Registra la creación de una entidad.
     *
     * @param entidad   Nombre de la entidad
     * @param entidadId ID de la entidad creada
     * @param datos     Datos de la entidad (opcional)
     */
    public void logCreacion(String entidad, Long entidadId, String datos) {
        log("CREAR", entidad, entidadId, null, datos);
    }

    /**
     * Registra la modificación de una entidad.
     *
     * @param entidad   Nombre de la entidad
     * @param entidadId ID de la entidad
     * @param oldValue  Valor anterior
     * @param newValue  Valor nuevo
     */
    public void logModificacion(String entidad, Long entidadId, String oldValue, String newValue) {
        log("MODIFICAR", entidad, entidadId, oldValue, newValue);
    }

    /**
     * Busca todos los registros de auditoría (para reportes, solo ADMIN).
     *
     * @return Lista de logs
     */
    public List<LogAuditoria> findAll() {
        return logAuditoriaRepository.findAllByOrderByFechaHoraDesc();
    }

    /**
     * Busca logs por entidad y ID de entidad.
     *
     * @param entidad   Nombre de la entidad
     * @param entidadId ID de la entidad
     * @return Lista de logs
     */
    public List<LogAuditoria> findByEntidadAndEntidadId(String entidad, Long entidadId) {
        return logAuditoriaRepository.findByEntidadAndEntidadIdOrderByFechaHoraDesc(entidad, entidadId);
    }

    /**
     * Busca logs por usuario.
     *
     * @param usuarioEmail Email del usuario
     * @return Lista de logs
     */
    public List<LogAuditoria> findByUsuarioEmail(String usuarioEmail) {
        return logAuditoriaRepository.findByUsuarioEmailOrderByFechaHoraDesc(usuarioEmail);
    }

    /**
     * Busca logs por rango de fechas.
     *
     * @param start Fecha inicio
     * @param end   Fecha fin
     * @return Lista de logs
     */
    public List<LogAuditoria> findByFechaHoraBetween(LocalDateTime start, LocalDateTime end) {
        return logAuditoriaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(start, end);
    }

    /**
     * Obtiene el usuario autenticado actualmente.
     *
     * @return Usuario o null si no hay autenticación
     */
    private Usuario getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Object principal = auth.getPrincipal();
            if (principal instanceof Usuario) {
                return (Usuario) principal;
            }
            // Si el principal es un String (email), se podría buscar en BD, pero lo ideal es que
            // CustomUserDetailsService devuelva un objeto Usuario como principal.
        }
        return null;
    }
}