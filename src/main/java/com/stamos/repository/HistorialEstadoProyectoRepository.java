package com.stamos.repository;

import com.stamos.entity.HistorialEstadoProyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio para la entidad HistorialEstadoProyecto.
 */
@Repository
public interface HistorialEstadoProyectoRepository extends JpaRepository<HistorialEstadoProyecto, Long> {

    /**
     * Obtiene el historial de cambios de estado de un proyecto,
     * ordenado por fecha y hora descendente.
     *
     * @param proyectoId ID del proyecto
     * @return Lista de registros
     */
    List<HistorialEstadoProyecto> findByProyectoIdOrderByFechaHoraDesc(Long proyectoId);
}