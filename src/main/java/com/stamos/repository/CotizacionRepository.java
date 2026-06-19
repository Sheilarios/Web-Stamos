package com.stamos.repository;

import com.stamos.model.Cotizacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CotizacionRepository extends JpaRepository<Cotizacion, Integer> {

    List<Cotizacion> findByIdCliente(Integer idCliente);

    Page<Cotizacion> findByIdCliente(Integer idCliente, Pageable pageable);

    @Query("SELECT c FROM Cotizacion c WHERE c.idCliente = :idCliente ORDER BY c.fechaEmision DESC")
    List<Cotizacion> findByIdClienteOrderByFechaEmisionDesc(@Param("idCliente") Integer idCliente);

    List<Cotizacion> findByIdClienteAndEstado(Integer idCliente, String estado);

    Page<Cotizacion> findByIdClienteAndEstado(Integer idCliente, String estado, Pageable pageable);

    List<Cotizacion> findByIdUsuarioVendedor(Integer idUsuarioVendedor);

    Page<Cotizacion> findByIdUsuarioVendedor(Integer idUsuarioVendedor, Pageable pageable);

    List<Cotizacion> findByIdUsuarioVendedorAndEstado(Integer idUsuarioVendedor, String estado);

    Page<Cotizacion> findByIdUsuarioVendedorAndEstado(Integer idUsuarioVendedor, String estado, Pageable pageable);

    List<Cotizacion> findByEstado(String estado);

    Page<Cotizacion> findByEstado(String estado, Pageable pageable);

    @Query("SELECT c FROM Cotizacion c WHERE c.estado = 'Pendiente' ORDER BY c.fechaEmision ASC")
    List<Cotizacion> findPendientes();

    @Query("SELECT c FROM Cotizacion c WHERE c.estado = 'Pendiente' ORDER BY c.fechaEmision ASC")
    Page<Cotizacion> findPendientes(Pageable pageable);

    @Query("SELECT c FROM Cotizacion c WHERE c.estado = 'Aprobada' ORDER BY c.fechaEmision DESC")
    List<Cotizacion> findAprobadas();

    @Query("SELECT c FROM Cotizacion c WHERE c.estado = 'Aprobada' ORDER BY c.fechaEmision DESC")
    Page<Cotizacion> findAprobadas(Pageable pageable);

    @Query("SELECT c FROM Cotizacion c WHERE c.estado = 'Anulada' ORDER BY c.fechaEmision DESC")
    List<Cotizacion> findAnuladas();

    @Query("SELECT c FROM Cotizacion c WHERE c.estado = 'Anulada' ORDER BY c.fechaEmision DESC")
    Page<Cotizacion> findAnuladas(Pageable pageable);

    @Query("SELECT c FROM Cotizacion c WHERE c.fechaEmision BETWEEN :fechaInicio AND :fechaFin ORDER BY c.fechaEmision DESC")
    List<Cotizacion> findByFechaEmisionBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                               @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT c FROM Cotizacion c WHERE c.fechaEmision BETWEEN :fechaInicio AND :fechaFin ORDER BY c.fechaEmision DESC")
    Page<Cotizacion> findByFechaEmisionBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                               @Param("fechaFin") LocalDateTime fechaFin,
                                               Pageable pageable);

    @Query("SELECT c FROM Cotizacion c WHERE DATE(c.fechaEmision) = CURRENT_DATE ORDER BY c.fechaEmision DESC")
    List<Cotizacion> findEmitidasHoy();

    @Query("SELECT c FROM Cotizacion c WHERE c.fechaEmision >= :fecha ORDER BY c.fechaEmision DESC")
    List<Cotizacion> findEmitidasUltimosDias(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT c FROM Cotizacion c WHERE c.fechaValidez < :fecha AND c.estado = 'Pendiente'")
    List<Cotizacion> findVencidas(@Param("fecha") LocalDate fecha);

    @Query("SELECT c FROM Cotizacion c WHERE c.fechaValidez < :fecha AND c.estado = 'Pendiente'")
    Page<Cotizacion> findVencidas(@Param("fecha") LocalDate fecha, Pageable pageable);

    List<Cotizacion> findByVersionActual(Integer versionActual);

    @Query("SELECT c FROM Cotizacion c WHERE c.versionActual > 1 ORDER BY c.versionActual DESC")
    List<Cotizacion> findWithMultipleVersions();

    @Query("SELECT c FROM Cotizacion c WHERE c.versionActual > 1 ORDER BY c.versionActual DESC")
    Page<Cotizacion> findWithMultipleVersions(Pageable pageable);

    @Query("SELECT c FROM Cotizacion c WHERE c.idCliente = :idCliente AND c.estado = :estado ORDER BY c.fechaEmision DESC")
    List<Cotizacion> findByIdClienteAndEstadoOrderByFechaEmisionDesc(@Param("idCliente") Integer idCliente,
                                                                     @Param("estado") String estado);

    @Query("SELECT c FROM Cotizacion c WHERE c.idCliente = :idCliente AND c.fechaEmision BETWEEN :fechaInicio AND :fechaFin ORDER BY c.fechaEmision DESC")
    List<Cotizacion> findByIdClienteAndFechaEmisionBetween(@Param("idCliente") Integer idCliente,
                                                           @Param("fechaInicio") LocalDateTime fechaInicio,
                                                           @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT c FROM Cotizacion c WHERE c.idUsuarioVendedor = :idVendedor AND c.estado = :estado ORDER BY c.fechaEmision DESC")
    List<Cotizacion> findByIdVendedorAndEstado(@Param("idVendedor") Integer idUsuarioVendedor,
                                               @Param("estado") String estado);

    @Query("SELECT c FROM Cotizacion c WHERE c.idCotizacion IN (SELECT p.idCotizacion FROM Proyecto p)")
    List<Cotizacion> findWithProyectoAsociado();

    @Query("SELECT c FROM Cotizacion c WHERE c.idCotizacion NOT IN (SELECT p.idCotizacion FROM Proyecto p)")
    List<Cotizacion> findWithoutProyectoAsociado();

    @Query("SELECT c FROM Cotizacion c WHERE c.estado = 'Aprobada' AND c.idCotizacion NOT IN (SELECT p.idCotizacion FROM Proyecto p)")
    List<Cotizacion> findAprobadasWithoutProyecto();

    @Query("SELECT c FROM Cotizacion c WHERE " +
           "LOWER(c.estado) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "CAST(c.idCliente AS string) LIKE CONCAT('%', :searchText, '%') OR " +
           "CAST(c.idUsuarioVendedor AS string) LIKE CONCAT('%', :searchText, '%') " +
           "ORDER BY c.fechaEmision DESC")
    List<Cotizacion> search(@Param("searchText") String searchText);

    @Query("SELECT c FROM Cotizacion c WHERE " +
           "LOWER(c.estado) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "CAST(c.idCliente AS string) LIKE CONCAT('%', :searchText, '%') OR " +
           "CAST(c.idUsuarioVendedor AS string) LIKE CONCAT('%', :searchText, '%') " +
           "ORDER BY c.fechaEmision DESC")
    Page<Cotizacion> search(@Param("searchText") String searchText, Pageable pageable);

    long countByEstado(String estado);

    long countByIdCliente(Integer idCliente);

    long countByIdUsuarioVendedor(Integer idUsuarioVendedor);

    @Query("SELECT COUNT(c) FROM Cotizacion c WHERE MONTH(c.fechaEmision) = :month AND YEAR(c.fechaEmision) = :year")
    long countByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT COUNT(c) FROM Cotizacion c WHERE c.estado = :estado AND MONTH(c.fechaEmision) = :month AND YEAR(c.fechaEmision) = :year")
    long countByEstadoAndMonthAndYear(@Param("estado") String estado,
                                      @Param("month") int month,
                                      @Param("year") int year);

    @Query("SELECT DATE(c.fechaEmision) as fecha, COUNT(c) as total " +
           "FROM Cotizacion c " +
           "WHERE c.fechaEmision >= :fechaInicio " +
           "GROUP BY DATE(c.fechaEmision) " +
           "ORDER BY DATE(c.fechaEmision) DESC")
    List<Object[]> countEmisionesByDay(@Param("fechaInicio") LocalDateTime fechaInicio);

    @Query("SELECT c.estado, COUNT(c) as total FROM Cotizacion c GROUP BY c.estado")
    List<Object[]> countByEstadoResumen();

    @Query("SELECT c.idUsuarioVendedor, COUNT(c) as total FROM Cotizacion c GROUP BY c.idUsuarioVendedor ORDER BY total DESC")
    List<Object[]> countByVendedor();

    @Query("SELECT COALESCE(SUM(v.total), 0) FROM CotizacionVersion v " +
           "WHERE v.idCotizacion IN (SELECT c.idCotizacion FROM Cotizacion c WHERE c.estado = 'Aprobada')")
    BigDecimal sumTotalAprobadas();

    @Query("SELECT COALESCE(SUM(v.total), 0) FROM CotizacionVersion v " +
           "WHERE v.idCotizacion IN (SELECT c.idCotizacion FROM Cotizacion c WHERE c.idCliente = :idCliente)")
    BigDecimal sumTotalByCliente(@Param("idCliente") Integer idCliente);

    @Query("SELECT COALESCE(AVG(v.total), 0) FROM CotizacionVersion v")
    BigDecimal avgTotal();

    @Query("SELECT COALESCE(AVG(v.total), 0) FROM CotizacionVersion v " +
           "WHERE v.idCotizacion IN (SELECT c.idCotizacion FROM Cotizacion c WHERE c.idCliente = :idCliente)")
    BigDecimal avgTotalByCliente(@Param("idCliente") Integer idCliente);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM CotizacionDetalle d " +
           "WHERE d.idVersion IN (SELECT v.idVersion FROM CotizacionVersion v WHERE v.idCotizacion = :idCotizacion)")
    boolean hasDetalles(@Param("idCotizacion") Integer idCotizacion);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Proyecto p WHERE p.idCotizacion = :idCotizacion")
    boolean hasProyecto(@Param("idCotizacion") Integer idCotizacion);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cotizacion c " +
           "WHERE c.idCotizacion = :idCotizacion AND c.estado = 'Pendiente' AND (c.fechaValidez IS NULL OR c.fechaValidez >= :fechaActual)")
    boolean canEdit(@Param("idCotizacion") Integer idCotizacion,
                    @Param("fechaActual") LocalDate fechaActual);

    @Query("SELECT c FROM Cotizacion c ORDER BY c.fechaEmision DESC LIMIT :limit")
    List<Cotizacion> findTopN(@Param("limit") int limit);

    @Query("SELECT c FROM Cotizacion c WHERE c.idCliente = :idCliente ORDER BY c.fechaEmision DESC LIMIT :limit")
    List<Cotizacion> findTopNByCliente(@Param("idCliente") Integer idCliente,
                                       @Param("limit") int limit);

    @Query("SELECT c FROM Cotizacion c WHERE c.estado = 'Pendiente' ORDER BY c.fechaEmision ASC LIMIT :limit")
    List<Cotizacion> findTopNPendientes(@Param("limit") int limit);

    @Modifying
    @Transactional
    @Query("UPDATE Cotizacion c SET c.estado = 'Anulada', c.idUsuarioAnulacion = :idUsuario, c.fechaAnulacion = CURRENT_TIMESTAMP, c.fechaActualizacion = CURRENT_TIMESTAMP WHERE c.idCliente = :idCliente AND c.estado = 'Pendiente'")
    void anularByCliente(@Param("idCliente") Integer idCliente,
                         @Param("idUsuario") Integer idUsuarioAnulacion);

    @Modifying
    @Transactional
    @Query("UPDATE Cotizacion c SET c.estado = 'Anulada', c.idUsuarioAnulacion = :idUsuario, c.fechaAnulacion = CURRENT_TIMESTAMP, c.fechaActualizacion = CURRENT_TIMESTAMP WHERE c.estado = 'Pendiente' AND c.fechaValidez < :fecha")
    void anularVencidas(@Param("fecha") LocalDate fecha,
                        @Param("idUsuario") Integer idUsuarioAnulacion);

    @Modifying
    @Transactional
    @Query("DELETE FROM Cotizacion c WHERE c.idCliente = :idCliente")
    void deleteByCliente(@Param("idCliente") Integer idCliente);

    @Modifying
    @Transactional
    @Query("DELETE FROM Cotizacion c WHERE c.fechaEmision < :fecha AND c.estado = 'Anulada'")
    int deleteOlderThan(@Param("fecha") LocalDateTime fecha);
}