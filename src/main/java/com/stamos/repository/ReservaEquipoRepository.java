package com.stamos.repository;

import com.stamos.entity.ReservaEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio para la entidad ReservaEquipo.
 * Proporciona métodos CRUD básicos y consultas personalizadas para la gestión de alquileres.
 */
@Repository
public interface ReservaEquipoRepository extends JpaRepository<ReservaEquipo, Long> {

    /**
     * Obtiene todas las reservas de un equipo específico.
     *
     * @param equipoId ID del equipo
     * @return Lista de reservas del equipo
     */
    List<ReservaEquipo> findByEquipoId(Long equipoId);

    /**
     * Verifica si un equipo tiene al menos una reserva asociada.
     * Útil para saber si se puede eliminar un equipo del catálogo.
     *
     * @param equipoId ID del equipo
     * @return true si existe al menos una reserva
     */
    boolean existsByEquipoId(Long equipoId);

    /**
     * Encuentra reservas de un equipo que se solapen con un rango de fechas.
     * Utilizado para validar disponibilidad (RIN03).
     *
     * @param equipoId     ID del equipo
     * @param fechaInicio  Fecha de inicio del período a verificar
     * @param fechaFin     Fecha de fin del período a verificar
     * @return Lista de reservas que se solapan
     */
    @Query("SELECT r FROM ReservaEquipo r WHERE r.equipo.id = :equipoId " +
           "AND r.fechaInicio <= :fechaFin AND r.fechaFin >= :fechaInicio")
    List<ReservaEquipo> findByEquipoIdAndPeriodoOverlap(@Param("equipoId") Long equipoId,
                                                        @Param("fechaInicio") LocalDate fechaInicio,
                                                        @Param("fechaFin") LocalDate fechaFin);

    /**
     * Reporte de equipos más alquilados (cantidad total reservada).
     * Devuelve objetos con: [equipo_id, nombre, cantidad_total_alquilada]
     *
     * @return Lista de arreglos ordenados por cantidad descendente
     */
    @Query("SELECT r.equipo.id, r.equipo.nombre, SUM(r.cantidad) " +
           "FROM ReservaEquipo r " +
           "GROUP BY r.equipo.id, r.equipo.nombre " +
           "ORDER BY SUM(r.cantidad) DESC")
    List<Object[]> findEquiposMasAlquilados();
}