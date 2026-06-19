package com.stamos.repository;

import com.stamos.model.ReservaInventario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaInventarioRepository extends JpaRepository<ReservaInventario, Integer> {

    String ESTADO_ACTIVA = "Activa";
    String ESTADO_COMPLETADA = "Completada";
    String ESTADO_CANCELADA = "Cancelada";

    List<ReservaInventario> findByIdInventario(Integer idInventario);

    Page<ReservaInventario> findByIdInventario(Integer idInventario, Pageable pageable);

    @Query("SELECT r FROM ReservaInventario r WHERE r.idInventario = :idInventario ORDER BY r.fechaInicio DESC")
    List<ReservaInventario> findByIdInventarioOrderByFechaInicioDesc(@Param("idInventario") Integer idInventario);

    @Query("SELECT r FROM ReservaInventario r WHERE r.idInventario = :idInventario AND r.estadoReserva = 'Activa' ORDER BY r.fechaInicio ASC")
    List<ReservaInventario> findActivasByIdInventario(@Param("idInventario") Integer idInventario);

    @Query("SELECT r FROM ReservaInventario r WHERE r.idInventario = :idInventario AND r.estadoReserva = 'Activa' " +
           "AND NOT (r.fechaFin < :fechaInicio OR r.fechaInicio > :fechaFin) " +
           "ORDER BY r.fechaInicio ASC")
    List<ReservaInventario> findActivasByIdInventarioAndFechas(@Param("idInventario") Integer idInventario,
                                                               @Param("fechaInicio") LocalDate fechaInicio,
                                                               @Param("fechaFin") LocalDate fechaFin);

    long countByIdInventario(Integer idInventario);

    @Query("SELECT COUNT(r) FROM ReservaInventario r WHERE r.idInventario = :idInventario AND r.estadoReserva = 'Activa'")
    long countActivasByIdInventario(@Param("idInventario") Integer idInventario);

    List<ReservaInventario> findByIdProyecto(Integer idProyecto);

    Page<ReservaInventario> findByIdProyecto(Integer idProyecto, Pageable pageable);

    @Query("SELECT r FROM ReservaInventario r WHERE r.idProyecto = :idProyecto ORDER BY r.fechaInicio DESC")
    List<ReservaInventario> findByIdProyectoOrderByFechaInicioDesc(@Param("idProyecto") Integer idProyecto);

    @Query("SELECT r FROM ReservaInventario r WHERE r.idProyecto = :idProyecto AND r.estadoReserva = 'Activa' ORDER BY r.fechaInicio ASC")
    List<ReservaInventario> findActivasByIdProyecto(@Param("idProyecto") Integer idProyecto);

    long countByIdProyecto(Integer idProyecto);

    @Query("SELECT COUNT(r) FROM ReservaInventario r WHERE r.idProyecto = :idProyecto AND r.estadoReserva = 'Activa'")
    long countActivasByIdProyecto(@Param("idProyecto") Integer idProyecto);

    List<ReservaInventario> findByEstadoReserva(String estadoReserva);

    Page<ReservaInventario> findByEstadoReserva(String estadoReserva, Pageable pageable);

    @Query("SELECT r FROM ReservaInventario r WHERE r.estadoReserva = 'Activa' ORDER BY r.fechaInicio ASC")
    List<ReservaInventario> findActivas();

    @Query("SELECT r FROM ReservaInventario r WHERE r.estadoReserva = 'Activa' ORDER BY r.fechaInicio ASC")
    Page<ReservaInventario> findActivas(Pageable pageable);

    @Query("SELECT r FROM ReservaInventario r WHERE r.estadoReserva = 'Completada' ORDER BY r.fechaReserva DESC")
    List<ReservaInventario> findCompletadas();

    @Query("SELECT r FROM ReservaInventario r WHERE r.estadoReserva = 'Completada' ORDER BY r.fechaReserva DESC")
    Page<ReservaInventario> findCompletadas(Pageable pageable);

    @Query("SELECT r FROM ReservaInventario r WHERE r.estadoReserva = 'Cancelada' ORDER BY r.fechaReserva DESC")
    List<ReservaInventario> findCanceladas();

    @Query("SELECT r FROM ReservaInventario r WHERE r.estadoReserva = 'Cancelada' ORDER BY r.fechaReserva DESC")
    Page<ReservaInventario> findCanceladas(Pageable pageable);

    long countByEstadoReserva(String estadoReserva);

    @Query("SELECT r.estadoReserva, COUNT(r) as total FROM ReservaInventario r GROUP BY r.estadoReserva ORDER BY r.estadoReserva ASC")
    List<Object[]> countByEstadoReservaResumen();

    @Query("SELECT r FROM ReservaInventario r WHERE r.fechaInicio BETWEEN :fechaInicio AND :fechaFin ORDER BY r.fechaInicio ASC")
    List<ReservaInventario> findByFechaInicioBetween(@Param("fechaInicio") LocalDate fechaInicio,
                                                     @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT r FROM ReservaInventario r WHERE r.fechaFin BETWEEN :fechaInicio AND :fechaFin ORDER BY r.fechaFin ASC")
    List<ReservaInventario> findByFechaFinBetween(@Param("fechaInicio") LocalDate fechaInicio,
                                                  @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT r FROM ReservaInventario r WHERE r.estadoReserva = 'Activa' " +
           "AND NOT (r.fechaFin < :fechaInicio OR r.fechaInicio > :fechaFin) " +
           "ORDER BY r.fechaInicio ASC")
    List<ReservaInventario> findReservasEnRango(@Param("fechaInicio") LocalDate fechaInicio,
                                                @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT r FROM ReservaInventario r WHERE r.idInventario = :idInventario AND r.estadoReserva = 'Activa' " +
           "AND NOT (r.fechaFin < :fechaInicio OR r.fechaInicio > :fechaFin) " +
           "ORDER BY r.fechaInicio ASC")
    List<ReservaInventario> findReservasEnRangoByEquipo(@Param("idInventario") Integer idInventario,
                                                        @Param("fechaInicio") LocalDate fechaInicio,
                                                        @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT r FROM ReservaInventario r WHERE r.fechaInicio = CURRENT_DATE AND r.estadoReserva = 'Activa' ORDER BY r.fechaReserva DESC")
    List<ReservaInventario> findInicianHoy();

    @Query("SELECT r FROM ReservaInventario r WHERE r.fechaFin = CURRENT_DATE AND r.estadoReserva = 'Activa' ORDER BY r.fechaReserva DESC")
    List<ReservaInventario> findTerminanHoy();

    @Query("SELECT r FROM ReservaInventario r WHERE r.fechaFin < :fecha AND r.estadoReserva = 'Activa' ORDER BY r.fechaFin ASC")
    List<ReservaInventario> findVencidas(@Param("fecha") LocalDate fecha);

    @Query("SELECT r FROM ReservaInventario r WHERE r.fechaFin < :fecha AND r.estadoReserva = 'Activa' ORDER BY r.fechaFin ASC")
    Page<ReservaInventario> findVencidas(@Param("fecha") LocalDate fecha, Pageable pageable);

    @Query("SELECT r FROM ReservaInventario r WHERE r.fechaFin BETWEEN :fechaInicio AND :fechaFin AND r.estadoReserva = 'Activa' ORDER BY r.fechaFin ASC")
    List<ReservaInventario> findProximasAFinalizar(@Param("fechaInicio") LocalDate fechaInicio,
                                                   @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT COUNT(r) FROM ReservaInventario r WHERE MONTH(r.fechaReserva) = :month AND YEAR(r.fechaReserva) = :year")
    long countByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT DATE(r.fechaReserva) as fecha, COUNT(r) as total " +
           "FROM ReservaInventario r " +
           "WHERE r.fechaReserva >= :fechaInicio " +
           "GROUP BY DATE(r.fechaReserva) " +
           "ORDER BY DATE(r.fechaReserva) DESC")
    List<Object[]> countReservasByDay(@Param("fechaInicio") LocalDateTime fechaInicio);

    List<ReservaInventario> findByIdInventarioAndEstadoReserva(Integer idInventario, String estadoReserva);

    Page<ReservaInventario> findByIdInventarioAndEstadoReserva(Integer idInventario, String estadoReserva, Pageable pageable);

    List<ReservaInventario> findByIdProyectoAndEstadoReserva(Integer idProyecto, String estadoReserva);

    Page<ReservaInventario> findByIdProyectoAndEstadoReserva(Integer idProyecto, String estadoReserva, Pageable pageable);

    List<ReservaInventario> findByIdInventarioAndIdProyectoAndEstadoReserva(Integer idInventario, Integer idProyecto, 
                                                                            String estadoReserva);

    @Query("SELECT r FROM ReservaInventario r WHERE r.idProyecto = :idProyecto AND r.estadoReserva = 'Activa' ORDER BY r.fechaInicio ASC")
    List<ReservaInventario> findActivasByProyecto(@Param("idProyecto") Integer idProyecto);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM ReservaInventario r " +
           "WHERE r.idInventario = :idInventario AND r.estadoReserva = 'Activa' " +
           "AND NOT (r.fechaFin < :fechaInicio OR r.fechaInicio > :fechaFin)")
    boolean hasReservasEnRango(@Param("idInventario") Integer idInventario,
                               @Param("fechaInicio") LocalDate fechaInicio,
                               @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT CASE WHEN COUNT(r) = 0 THEN true ELSE false END FROM ReservaInventario r " +
           "WHERE r.idInventario = :idInventario AND r.estadoReserva = 'Activa' " +
           "AND NOT (r.fechaFin < :fechaInicio OR r.fechaInicio > :fechaFin)")
    boolean isDisponible(@Param("idInventario") Integer idInventario,
                         @Param("fechaInicio") LocalDate fechaInicio,
                         @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM ReservaInventario r " +
           "WHERE r.idInventario = :idInventario AND r.estadoReserva = 'Activa' AND r.fechaInicio > :fecha")
    boolean hasReservasFuturas(@Param("idInventario") Integer idInventario,
                               @Param("fecha") LocalDate fecha);

    @Query("SELECT r FROM ReservaInventario r WHERE " +
           "CAST(r.idInventario AS string) LIKE CONCAT('%', :searchText, '%') OR " +
           "CAST(r.idProyecto AS string) LIKE CONCAT('%', :searchText, '%') OR " +
           "LOWER(r.estadoReserva) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "ORDER BY r.fechaReserva DESC")
    List<ReservaInventario> search(@Param("searchText") String searchText);

    @Query("SELECT r FROM ReservaInventario r WHERE " +
           "CAST(r.idInventario AS string) LIKE CONCAT('%', :searchText, '%') OR " +
           "CAST(r.idProyecto AS string) LIKE CONCAT('%', :searchText, '%') OR " +
           "LOWER(r.estadoReserva) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "ORDER BY r.fechaReserva DESC")
    Page<ReservaInventario> search(@Param("searchText") String searchText, Pageable pageable);

    @Query("SELECT r FROM ReservaInventario r ORDER BY r.fechaReserva DESC LIMIT :limit")
    List<ReservaInventario> findTopN(@Param("limit") int limit);

    @Query("SELECT r FROM ReservaInventario r WHERE r.idInventario = :idInventario ORDER BY r.fechaReserva DESC LIMIT :limit")
    List<ReservaInventario> findTopNByEquipo(@Param("idInventario") Integer idInventario, @Param("limit") int limit);

    @Query("SELECT r FROM ReservaInventario r WHERE r.idProyecto = :idProyecto ORDER BY r.fechaReserva DESC LIMIT :limit")
    List<ReservaInventario> findTopNByProyecto(@Param("idProyecto") Integer idProyecto, @Param("limit") int limit);

    @Query("SELECT r FROM ReservaInventario r WHERE r.estadoReserva = 'Activa' ORDER BY r.fechaReserva DESC LIMIT :limit")
    List<ReservaInventario> findTopNActivas(@Param("limit") int limit);

    @Modifying
    @Transactional
    @Query("UPDATE ReservaInventario r SET r.estadoReserva = :estado, r.fechaActualizacion = CURRENT_TIMESTAMP WHERE r.idReserva = :idReserva")
    void updateEstado(@Param("idReserva") Integer idReserva,
                      @Param("estado") String nuevoEstado);

    @Modifying
    @Transactional
    @Query("UPDATE ReservaInventario r SET r.estadoReserva = 'Completada', r.fechaActualizacion = CURRENT_TIMESTAMP WHERE r.idReserva = :idReserva AND r.estadoReserva = 'Activa'")
    int completarReserva(@Param("idReserva") Integer idReserva);

    @Modifying
    @Transactional
    @Query("UPDATE ReservaInventario r SET r.estadoReserva = 'Cancelada', r.fechaActualizacion = CURRENT_TIMESTAMP WHERE r.idReserva = :idReserva AND r.estadoReserva = 'Activa'")
    int cancelarReserva(@Param("idReserva") Integer idReserva);

    @Modifying
    @Transactional
    @Query("UPDATE ReservaInventario r SET r.notas = :notas, r.fechaActualizacion = CURRENT_TIMESTAMP WHERE r.idReserva = :idReserva")
    void updateNotas(@Param("idReserva") Integer idReserva,
                     @Param("notas") String notas);

    @Modifying
    @Transactional
    @Query("UPDATE ReservaInventario r SET r.estadoReserva = 'Completada', r.fechaActualizacion = CURRENT_TIMESTAMP " +
           "WHERE r.estadoReserva = 'Activa' AND r.fechaFin < :fecha")
    int completarReservasVencidas(@Param("fecha") LocalDate fecha);

    @Modifying
    @Transactional
    @Query("DELETE FROM ReservaInventario r WHERE r.idInventario = :idInventario")
    void deleteByIdInventario(@Param("idInventario") Integer idInventario);

    @Modifying
    @Transactional
    @Query("DELETE FROM ReservaInventario r WHERE r.idProyecto = :idProyecto")
    void deleteByIdProyecto(@Param("idProyecto") Integer idProyecto);

    @Modifying
    @Transactional
    @Query("DELETE FROM ReservaInventario r WHERE r.estadoReserva = :estado")
    void deleteByEstadoReserva(@Param("estado") String estadoReserva);

    @Modifying
    @Transactional
    @Query("DELETE FROM ReservaInventario r WHERE r.estadoReserva IN ('Cancelada', 'Completada') AND r.fechaReserva < :fecha")
    int deleteOldCompletadasCanceladas(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM ReservaInventario r " +
           "WHERE r.idInventario = :idInventario AND r.estadoReserva = 'Activa'")
    boolean hasReservasActivas(@Param("idInventario") Integer idInventario);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM ReservaInventario r " +
           "WHERE r.idProyecto = :idProyecto AND r.estadoReserva = 'Activa'")
    boolean hasReservasActivasByProyecto(@Param("idProyecto") Integer idProyecto);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM ReservaInventario r " +
           "WHERE r.idInventario = :idInventario AND r.estadoReserva = 'Activa' " +
           "AND NOT (r.fechaFin < :fechaInicio OR r.fechaInicio > :fechaFin)")
    boolean existsReservaEnRango(@Param("idInventario") Integer idInventario,
                                 @Param("fechaInicio") LocalDate fechaInicio,
                                 @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM ReservaInventario r " +
           "WHERE r.idProyecto = :idProyecto AND r.estadoReserva = 'Activa' " +
           "AND NOT (r.fechaFin < :fechaInicio OR r.fechaInicio > :fechaFin)")
    boolean existsReservaEnRangoByProyecto(@Param("idProyecto") Integer idProyecto,
                                           @Param("fechaInicio") LocalDate fechaInicio,
                                           @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT " +
           "COUNT(r) as total, " +
           "SUM(CASE WHEN r.estadoReserva = 'Activa' THEN 1 ELSE 0 END) as activas, " +
           "SUM(CASE WHEN r.estadoReserva = 'Completada' THEN 1 ELSE 0 END) as completadas, " +
           "SUM(CASE WHEN r.estadoReserva = 'Cancelada' THEN 1 ELSE 0 END) as canceladas " +
           "FROM ReservaInventario r")
    List<Object[]> getEstadisticasDashboard();

    @Query("SELECT " +
           "COUNT(r) as total, " +
           "SUM(CASE WHEN r.estadoReserva = 'Activa' THEN 1 ELSE 0 END) as activas, " +
           "SUM(CASE WHEN r.estadoReserva = 'Completada' THEN 1 ELSE 0 END) as completadas, " +
           "SUM(CASE WHEN r.estadoReserva = 'Cancelada' THEN 1 ELSE 0 END) as canceladas " +
           "FROM ReservaInventario r WHERE r.idProyecto = :idProyecto")
    List<Object[]> getEstadisticasDashboardByProyecto(@Param("idProyecto") Integer idProyecto);

    @Query("SELECT " +
           "COUNT(r) as total, " +
           "SUM(CASE WHEN r.estadoReserva = 'Activa' THEN 1 ELSE 0 END) as activas, " +
           "SUM(CASE WHEN r.estadoReserva = 'Completada' THEN 1 ELSE 0 END) as completadas, " +
           "SUM(CASE WHEN r.estadoReserva = 'Cancelada' THEN 1 ELSE 0 END) as canceladas " +
           "FROM ReservaInventario r WHERE r.idInventario = :idInventario")
    List<Object[]> getEstadisticasDashboardByEquipo(@Param("idInventario") Integer idInventario);

    @Query("SELECT r.idInventario, COUNT(r) as total FROM ReservaInventario r GROUP BY r.idInventario ORDER BY total DESC")
    List<Object[]> findTopEquiposReservados();

    @Query("SELECT r.idProyecto, COUNT(r) as total FROM ReservaInventario r GROUP BY r.idProyecto ORDER BY total DESC")
    List<Object[]> findTopProyectosConReservas();

    @Query("SELECT r.idReserva, r.idInventario, r.idProyecto, r.fechaInicio, r.fechaFin, r.estadoReserva " +
           "FROM ReservaInventario r ORDER BY r.fechaReserva DESC")
    List<Object[]> findReservasDashboard();
}