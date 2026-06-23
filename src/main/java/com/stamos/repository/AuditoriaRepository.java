package com.stamos.repository;

import com.stamos.model.Auditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Integer> {

    @Query("SELECT a FROM Auditoria a WHERE a.idUsuario = :idUsuario ORDER BY a.fechaHora DESC")
    Page<Auditoria> findByIdUsuario(@Param("idUsuario") Integer idUsuario, Pageable pageable);

    @Query("SELECT a FROM Auditoria a WHERE a.entidad = :entidad ORDER BY a.fechaHora DESC")
    Page<Auditoria> findByEntidad(@Param("entidad") String entidad, Pageable pageable);

    @Query("SELECT a FROM Auditoria a WHERE a.accion = :accion ORDER BY a.fechaHora DESC")
    Page<Auditoria> findByAccion(@Param("accion") String accion, Pageable pageable);

    @Query("SELECT a FROM Auditoria a WHERE a.entidad = :entidad AND a.idRegistroAfectado = :idRegistro ORDER BY a.fechaHora DESC")
    Page<Auditoria> findByEntidadAndIdRegistroAfectado(@Param("entidad") String entidad,
                                                       @Param("idRegistro") Integer idRegistroAfectado,
                                                       Pageable pageable);

    @Query("SELECT a FROM Auditoria a WHERE a.fechaHora BETWEEN :fechaInicio AND :fechaFin ORDER BY a.fechaHora DESC")
    Page<Auditoria> findByFechaHoraBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                           @Param("fechaFin") LocalDateTime fechaFin,
                                           Pageable pageable);

    @Query("SELECT a FROM Auditoria a WHERE a.accion = :accion AND a.entidad = :entidad ORDER BY a.fechaHora DESC")
    Page<Auditoria> findByAccionAndEntidad(@Param("accion") String accion,
                                           @Param("entidad") String entidad,
                                           Pageable pageable);

    @Query("SELECT a FROM Auditoria a ORDER BY a.fechaHora DESC")
    List<Auditoria> findTopN(@Param("limit") int limit);

    @Query("SELECT a FROM Auditoria a WHERE a.ipOrigen = :ipOrigen ORDER BY a.fechaHora DESC")
    Page<Auditoria> findByIpOrigen(@Param("ipOrigen") String ipOrigen, Pageable pageable);

    @Query("SELECT COUNT(a) FROM Auditoria a WHERE a.entidad = :entidad")
    long countByEntidad(@Param("entidad") String entidad);

    @Query("SELECT COUNT(a) FROM Auditoria a WHERE a.accion = :accion")
    long countByAccion(@Param("accion") String accion);

    @Query("SELECT COUNT(a) FROM Auditoria a WHERE a.idUsuario = :idUsuario")
    long countByIdUsuario(@Param("idUsuario") Integer idUsuario);

    @Query("SELECT FUNCTION('DATE', a.fechaHora) as fecha, COUNT(a) as total " +
           "FROM Auditoria a " +
           "WHERE a.fechaHora BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY FUNCTION('DATE', a.fechaHora) " +
           "ORDER BY FUNCTION('DATE', a.fechaHora) DESC")
    List<Object[]> countByDay(@Param("fechaInicio") LocalDateTime fechaInicio,
                              @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT a.entidad, COUNT(a) as total " +
           "FROM Auditoria a " +
           "WHERE a.fechaHora BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY a.entidad " +
           "ORDER BY COUNT(a) DESC")
    List<Object[]> countByEntidadInRange(@Param("fechaInicio") LocalDateTime fechaInicio,
                                         @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT a.idUsuario, COUNT(a) as total " +
           "FROM Auditoria a " +
           "WHERE a.fechaHora BETWEEN :fechaInicio AND :fechaFin AND a.idUsuario IS NOT NULL " +
           "GROUP BY a.idUsuario " +
           "ORDER BY COUNT(a) DESC")
    List<Object[]> countByUsuarioInRange(@Param("fechaInicio") LocalDateTime fechaInicio,
                                         @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT a FROM Auditoria a WHERE a.accion = 'STATE_CHANGE' ORDER BY a.fechaHora DESC")
    Page<Auditoria> findCambiosEstado(Pageable pageable);

    @Query("SELECT a FROM Auditoria a WHERE a.accion = 'STATE_CHANGE' AND a.entidad = 'Proyecto' AND a.idRegistroAfectado = :idProyecto ORDER BY a.fechaHora DESC")
    Page<Auditoria> findCambiosEstadoByProyecto(@Param("idProyecto") Integer idProyecto, Pageable pageable);

    @Query("SELECT a FROM Auditoria a WHERE a.accion = 'LOGIN' OR a.accion = 'LOGOUT' ORDER BY a.fechaHora DESC")
    Page<Auditoria> findAccesos(Pageable pageable);

    @Query("SELECT a FROM Auditoria a WHERE (a.accion = 'LOGIN' OR a.accion = 'LOGOUT') AND a.idUsuario = :idUsuario ORDER BY a.fechaHora DESC")
    Page<Auditoria> findAccesosByUsuario(@Param("idUsuario") Integer idUsuario, Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE FROM Auditoria a WHERE a.fechaHora < :fecha")
    int deleteOlderThan(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT COUNT(a) FROM Auditoria a")
    long countAll();

    @Query(value = "SELECT * FROM AuditoriaSistema ORDER BY fecha_hora DESC LIMIT :limit", nativeQuery = true)
    List<Auditoria> findLastAuditorias(@Param("limit") int limit);

    @Query("SELECT a.entidad, COUNT(a) as total FROM Auditoria a GROUP BY a.entidad ORDER BY COUNT(a) DESC")
    List<Object[]> countByEntidadResumen();

    @Query("SELECT a.accion, COUNT(a) as total FROM Auditoria a GROUP BY a.accion ORDER BY COUNT(a) DESC")
    List<Object[]> countByAccionResumen();

    @Query(value = "SELECT DATE(fecha_hora) as fecha, COUNT(*) as total " +
                   "FROM AuditoriaSistema " +
                   "WHERE fecha_hora >= DATE_SUB(CURDATE(), INTERVAL :days DAY) " +
                   "GROUP BY DATE(fecha_hora) " +
                   "ORDER BY DATE(fecha_hora) DESC", nativeQuery = true)
    List<Object[]> countByLastDays(@Param("days") int days);

    @Query("SELECT a FROM Auditoria a WHERE " +
           "LOWER(a.entidad) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(a.accion) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(a.ipOrigen) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "CAST(a.idRegistroAfectado AS string) LIKE CONCAT('%', :searchText, '%') " +
           "ORDER BY a.fechaHora DESC")
    Page<Auditoria> search(@Param("searchText") String searchText, Pageable pageable);

    @Query("SELECT COUNT(a) FROM Auditoria a WHERE FUNCTION('DATE', a.fechaHora) = :fecha")
    long countByDate(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT COUNT(a) FROM Auditoria a WHERE FUNCTION('DATE', a.fechaHora) = CURRENT_DATE")
    long countToday();

    @Query("SELECT COUNT(a) FROM Auditoria a WHERE MONTH(a.fechaHora) = MONTH(CURRENT_DATE) AND YEAR(a.fechaHora) = YEAR(CURRENT_DATE)")
    long countThisMonth();

    @Query("SELECT COUNT(a) FROM Auditoria a WHERE YEAR(a.fechaHora) = YEAR(CURRENT_DATE)")
    long countThisYear();
}