package com.stamos.repository;

import com.stamos.entity.LogAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad LogAuditoria.
 * Proporciona métodos CRUD básicos y consultas personalizadas para la auditoría.
 */
@Repository
public interface LogAuditoriaRepository extends JpaRepository<LogAuditoria, Long> {

    /**
     * Obtiene todos los registros de auditoría ordenados por fecha descendente (más reciente primero).
     *
     * @return Lista de logs ordenados
     */
    List<LogAuditoria> findAllByOrderByFechaHoraDesc();

    /**
     * Obtiene logs por entidad y ID de entidad, ordenados por fecha descendente.
     *
     * @param entidad   Nombre de la entidad (ej. "CLIENTE", "COTIZACION", "PROYECTO", "PAGO")
     * @param entidadId ID de la entidad afectada
     * @return Lista de logs de esa entidad
     */
    List<LogAuditoria> findByEntidadAndEntidadIdOrderByFechaHoraDesc(String entidad, Long entidadId);

    /**
     * Obtiene logs por email del usuario, ordenados por fecha descendente.
     *
     * @param usuarioEmail Email del usuario que realizó la acción
     * @return Lista de logs del usuario
     */
    List<LogAuditoria> findByUsuarioEmailOrderByFechaHoraDesc(String usuarioEmail);

    /**
     * Obtiene logs dentro de un rango de fechas, ordenados por fecha descendente.
     *
     * @param start Fecha y hora de inicio
     * @param end   Fecha y hora de fin
     * @return Lista de logs en el rango
     */
    List<LogAuditoria> findByFechaHoraBetweenOrderByFechaHoraDesc(LocalDateTime start, LocalDateTime end);
}