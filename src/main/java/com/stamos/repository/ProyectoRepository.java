package com.stamos.repository;

import com.stamos.model.Proyecto;
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
import java.util.List;
import java.util.Optional;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Integer> {

    Optional<Proyecto> findByIdCotizacion(Integer idCotizacion);

    boolean existsByIdCotizacion(Integer idCotizacion);

    List<Proyecto> findByIdCliente(Integer idCliente);

    Page<Proyecto> findByIdCliente(Integer idCliente, Pageable pageable);

    @Query("SELECT p FROM Proyecto p WHERE p.idCliente = :idCliente ORDER BY p.fechaInicio DESC")
    List<Proyecto> findByIdClienteOrderByFechaInicioDesc(@Param("idCliente") Integer idCliente);

    List<Proyecto> findByIdClienteAndIdEstadoActual(Integer idCliente, Integer idEstadoActual);

    Page<Proyecto> findByIdClienteAndIdEstadoActual(Integer idCliente, Integer idEstadoActual, Pageable pageable);

    long countByIdCliente(Integer idCliente);

    long countByIdClienteAndIdEstadoActual(Integer idCliente, Integer idEstadoActual);

    @Query("SELECT COALESCE(SUM(p.montoTotal), 0) FROM Proyecto p WHERE p.idCliente = :idCliente")
    BigDecimal sumMontoTotalByCliente(@Param("idCliente") Integer idCliente);

    @Query("SELECT COALESCE(SUM(p.montoTotal), 0) FROM Proyecto p WHERE p.idCliente = :idCliente AND p.idEstadoActual = :idEstado")
    BigDecimal sumMontoTotalByClienteAndEstado(@Param("idCliente") Integer idCliente,
                                               @Param("idEstado") Integer idEstadoActual);

    List<Proyecto> findByIdEstadoActual(Integer idEstadoActual);

    Page<Proyecto> findByIdEstadoActual(Integer idEstadoActual, Pageable pageable);

    @Query("SELECT p FROM Proyecto p WHERE p.idEstadoActual = :idEstado ORDER BY p.fechaInicio DESC")
    List<Proyecto> findByEstado(@Param("idEstado") Integer idEstado);

    @Query("SELECT p FROM Proyecto p WHERE p.idEstadoActual = :idEstado ORDER BY p.fechaInicio DESC")
    Page<Proyecto> findByEstado(@Param("idEstado") Integer idEstado, Pageable pageable);

    @Query("SELECT p FROM Proyecto p JOIN EstadoProyecto e ON p.idEstadoActual = e.idEstado WHERE e.nombreEstado = 'Aprobado' ORDER BY p.fechaInicio ASC")
    List<Proyecto> findAprobados();

    @Query("SELECT p FROM Proyecto p JOIN EstadoProyecto e ON p.idEstadoActual = e.idEstado WHERE e.nombreEstado = 'En Diseño' ORDER BY p.fechaInicio ASC")
    List<Proyecto> findEnDiseno();

    @Query("SELECT p FROM Proyecto p JOIN EstadoProyecto e ON p.idEstadoActual = e.idEstado WHERE e.nombreEstado = 'En Producción' ORDER BY p.fechaInicio ASC")
    List<Proyecto> findEnProduccion();

    @Query("SELECT p FROM Proyecto p JOIN EstadoProyecto e ON p.idEstadoActual = e.idEstado WHERE e.nombreEstado = 'Instalación/Montaje' ORDER BY p.fechaInicio ASC")
    List<Proyecto> findInstalacionMontaje();

    @Query("SELECT p FROM Proyecto p JOIN EstadoProyecto e ON p.idEstadoActual = e.idEstado WHERE e.nombreEstado = 'Entregado' ORDER BY p.fechaEntregaReal DESC")
    List<Proyecto> findEntregados();

    @Query("SELECT p FROM Proyecto p JOIN EstadoProyecto e ON p.idEstadoActual = e.idEstado WHERE e.nombreEstado = 'Anulado' ORDER BY p.fechaInicio DESC")
    List<Proyecto> findAnulados();

    @Query("SELECT p FROM Proyecto p JOIN EstadoProyecto e ON p.idEstadoActual = e.idEstado WHERE e.nombreEstado NOT IN ('Entregado', 'Anulado') ORDER BY p.fechaInicio DESC")
    List<Proyecto> findActivos();

    @Query("SELECT p FROM Proyecto p JOIN EstadoProyecto e ON p.idEstadoActual = e.idEstado WHERE e.nombreEstado NOT IN ('Entregado', 'Anulado') ORDER BY p.fechaInicio DESC")
    Page<Proyecto> findActivos(Pageable pageable);

    @Query("SELECT COUNT(p) FROM Proyecto p JOIN EstadoProyecto e ON p.idEstadoActual = e.idEstado WHERE e.nombreEstado = :estado")
    long countByEstado(@Param("estado") String estado);

    long countByIdEstadoActual(Integer idEstadoActual);

    @Query("SELECT COUNT(p) FROM Proyecto p JOIN EstadoProyecto e ON p.idEstadoActual = e.idEstado WHERE e.nombreEstado NOT IN ('Entregado', 'Anulado')")
    long countActivos();

    @Query("SELECT COUNT(p) FROM Proyecto p WHERE p.idEstadoActual NOT IN :estados")
    long countByEstadoNotIn(@Param("estados") List<Integer> estados);

    @Query("SELECT e.nombreEstado, COUNT(p) as total, COALESCE(SUM(p.montoTotal), 0) as montoTotal " +
           "FROM EstadoProyecto e LEFT JOIN Proyecto p ON e.idEstado = p.idEstadoActual " +
           "GROUP BY e.idEstado, e.nombreEstado ORDER BY e.idEstado ASC")
    List<Object[]> countByEstadoResumen();

    @Query("SELECT p FROM Proyecto p WHERE p.fechaInicio BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaInicio DESC")
    List<Proyecto> findByFechaInicioBetween(@Param("fechaInicio") LocalDate fechaInicio,
                                            @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT p FROM Proyecto p WHERE p.fechaInicio BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaInicio DESC")
    Page<Proyecto> findByFechaInicioBetween(@Param("fechaInicio") LocalDate fechaInicio,
                                            @Param("fechaFin") LocalDate fechaFin,
                                            Pageable pageable);

    @Query("SELECT p FROM Proyecto p WHERE p.fechaFinEstimada BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaFinEstimada ASC")
    List<Proyecto> findByFechaFinEstimadaBetween(@Param("fechaInicio") LocalDate fechaInicio,
                                                 @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT p FROM Proyecto p WHERE p.fechaEntregaReal BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaEntregaReal DESC")
    List<Proyecto> findByFechaEntregaRealBetween(@Param("fechaInicio") LocalDate fechaInicio,
                                                 @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT p FROM Proyecto p WHERE p.fechaVencimiento BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaVencimiento ASC")
    List<Proyecto> findByFechaVencimientoBetween(@Param("fechaInicio") LocalDate fechaInicio,
                                                 @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT p FROM Proyecto p WHERE p.fechaInicio = CURRENT_DATE ORDER BY p.fechaInicio DESC")
    List<Proyecto> findInicianHoy();

    @Query("SELECT p FROM Proyecto p WHERE p.fechaFinEstimada = CURRENT_DATE ORDER BY p.fechaFinEstimada ASC")
    List<Proyecto> findTerminanHoy();

    @Query("SELECT p FROM Proyecto p WHERE p.fechaVencimiento < :fecha AND p.idEstadoActual NOT IN (SELECT e.idEstado FROM EstadoProyecto e WHERE e.nombreEstado IN ('Entregado', 'Anulado')) ORDER BY p.fechaVencimiento ASC")
    List<Proyecto> findVencidos(@Param("fecha") LocalDate fecha);

    @Query("SELECT p FROM Proyecto p WHERE p.fechaVencimiento < :fecha AND p.idEstadoActual NOT IN (SELECT e.idEstado FROM EstadoProyecto e WHERE e.nombreEstado IN ('Entregado', 'Anulado')) ORDER BY p.fechaVencimiento ASC")
    Page<Proyecto> findVencidos(@Param("fecha") LocalDate fecha, Pageable pageable);

    @Query("SELECT p FROM Proyecto p WHERE p.fechaVencimiento BETWEEN :fechaInicio AND :fechaFin AND p.idEstadoActual NOT IN (SELECT e.idEstado FROM EstadoProyecto e WHERE e.nombreEstado IN ('Entregado', 'Anulado')) ORDER BY p.fechaVencimiento ASC")
    List<Proyecto> findProximosAVencer(@Param("fechaInicio") LocalDate fechaInicio,
                                       @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT COUNT(p) FROM Proyecto p WHERE MONTH(p.fechaInicio) = :month AND YEAR(p.fechaInicio) = :year")
    long countByFechaInicioMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT DATE(p.fechaInicio) as fecha, COUNT(p) as total " +
           "FROM Proyecto p " +
           "WHERE p.fechaInicio >= :fechaInicio " +
           "GROUP BY DATE(p.fechaInicio) " +
           "ORDER BY DATE(p.fechaInicio) DESC")
    List<Object[]> countIniciadosByDay(@Param("fechaInicio") LocalDate fechaInicio);

    @Query("SELECT p FROM Proyecto p WHERE LOWER(p.nombreProyecto) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "OR CAST(p.idCliente AS string) LIKE CONCAT('%', :searchText, '%') " +
           "OR CAST(p.idCotizacion AS string) LIKE CONCAT('%', :searchText, '%') " +
           "ORDER BY p.fechaInicio DESC")
    List<Proyecto> search(@Param("searchText") String searchText);

    @Query("SELECT p FROM Proyecto p WHERE LOWER(p.nombreProyecto) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "OR CAST(p.idCliente AS string) LIKE CONCAT('%', :searchText, '%') " +
           "OR CAST(p.idCotizacion AS string) LIKE CONCAT('%', :searchText, '%') " +
           "ORDER BY p.fechaInicio DESC")
    Page<Proyecto> search(@Param("searchText") String searchText, Pageable pageable);

    @Query("SELECT p FROM Proyecto p WHERE p.montoTotal >= :monto ORDER BY p.montoTotal DESC")
    List<Proyecto> findByMontoTotalGreaterThanEqual(@Param("monto") BigDecimal montoTotal);

    @Query("SELECT p FROM Proyecto p WHERE p.montoTotal >= :monto ORDER BY p.montoTotal DESC")
    Page<Proyecto> findByMontoTotalGreaterThanEqual(@Param("monto") BigDecimal montoTotal, Pageable pageable);

    @Query("SELECT p FROM Proyecto p WHERE p.montoTotal BETWEEN :montoMin AND :montoMax ORDER BY p.montoTotal ASC")
    List<Proyecto> findByMontoTotalBetween(@Param("montoMin") BigDecimal montoMin,
                                           @Param("montoMax") BigDecimal montoMax);

    @Query("SELECT COALESCE(SUM(p.montoTotal), 0) FROM Proyecto p")
    BigDecimal sumMontoTotal();

    @Query("SELECT COALESCE(SUM(p.montoTotal), 0) FROM Proyecto p WHERE p.idEstadoActual NOT IN (SELECT e.idEstado FROM EstadoProyecto e WHERE e.nombreEstado IN ('Entregado', 'Anulado'))")
    BigDecimal sumMontoTotalActivos();

    @Query("SELECT COALESCE(SUM(p.montoTotal), 0) FROM Proyecto p WHERE p.idEstadoActual IN (SELECT e.idEstado FROM EstadoProyecto e WHERE e.nombreEstado = 'Entregado')")
    BigDecimal sumMontoTotalEntregados();

    @Query("SELECT COALESCE(AVG(p.montoTotal), 0) FROM Proyecto p")
    BigDecimal avgMontoTotal();

    @Query("SELECT p FROM Proyecto p WHERE p.idCliente = :idCliente AND p.idEstadoActual IN (SELECT e.idEstado FROM EstadoProyecto e WHERE e.nombreEstado = :estado) ORDER BY p.fechaInicio DESC")
    List<Proyecto> findByIdClienteAndEstado(@Param("idCliente") Integer idCliente,
                                            @Param("estado") String estado);

    @Query("SELECT p FROM Proyecto p WHERE p.idCliente = :idCliente AND p.fechaInicio BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaInicio DESC")
    List<Proyecto> findByIdClienteAndFechaInicioBetween(@Param("idCliente") Integer idCliente,
                                                        @Param("fechaInicio") LocalDate fechaInicio,
                                                        @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT p FROM Proyecto p WHERE p.idEstadoActual IN (SELECT e.idEstado FROM EstadoProyecto e WHERE e.nombreEstado = :estado) AND p.fechaInicio BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaInicio DESC")
    List<Proyecto> findByEstadoAndFechaInicioBetween(@Param("estado") String estado,
                                                     @Param("fechaInicio") LocalDate fechaInicio,
                                                     @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT p FROM Proyecto p WHERE p.fechaVencimiento < :fecha AND p.idEstadoActual NOT IN (SELECT e.idEstado FROM EstadoProyecto e WHERE e.nombreEstado IN ('Entregado', 'Anulado')) " +
           "AND p.montoTotal > (SELECT COALESCE(SUM(pg.monto), 0) FROM Pago pg WHERE pg.idProyecto = p.idProyecto AND pg.estadoPago = 'Validado') " +
           "ORDER BY p.fechaVencimiento ASC")
    List<Proyecto> findProyectosConPagosVencidos(@Param("fecha") LocalDate fecha);

    @Query("SELECT p FROM Proyecto p WHERE p.idCliente = :idCliente AND p.fechaVencimiento < :fecha AND p.idEstadoActual NOT IN (SELECT e.idEstado FROM EstadoProyecto e WHERE e.nombreEstado IN ('Entregado', 'Anulado')) " +
           "AND p.montoTotal > (SELECT COALESCE(SUM(pg.monto), 0) FROM Pago pg WHERE pg.idProyecto = p.idProyecto AND pg.estadoPago = 'Validado') " +
           "ORDER BY p.fechaVencimiento ASC")
    List<Proyecto> findProyectosConPagosVencidosByCliente(@Param("idCliente") Integer idCliente,
                                                          @Param("fecha") LocalDate fecha);

    @Query("SELECT p FROM Proyecto p WHERE p.fechaVencimiento < :fecha AND p.idEstadoActual NOT IN (SELECT e.idEstado FROM EstadoProyecto e WHERE e.nombreEstado IN ('Entregado', 'Anulado')) " +
           "AND p.montoTotal > (SELECT COALESCE(SUM(pg.monto), 0) FROM Pago pg WHERE pg.idProyecto = p.idProyecto AND pg.estadoPago = 'Validado') " +
           "ORDER BY p.fechaVencimiento ASC")
    Page<Proyecto> findProyectosConPagosVencidos(@Param("fecha") LocalDate fecha, Pageable pageable);

    @Query("SELECT p.idProyecto, p.nombreProyecto, p.idEstadoActual, p.montoTotal, p.fechaInicio, p.fechaFinEstimada, p.fechaEntregaReal " +
           "FROM Proyecto p WHERE p.idCliente = :idCliente ORDER BY p.fechaInicio DESC")
    List<Object[]> findResumenByCliente(@Param("idCliente") Integer idCliente);

    @Query("SELECT p FROM Proyecto p WHERE p.idCliente = :idCliente ORDER BY p.fechaInicio DESC")
    Page<Proyecto> findResumenByCliente(@Param("idCliente") Integer idCliente, Pageable pageable);

    @Query("SELECT p FROM Proyecto p ORDER BY p.fechaInicio DESC LIMIT :limit")
    List<Proyecto> findTopN(@Param("limit") int limit);

    @Query("SELECT p FROM Proyecto p WHERE p.idEstadoActual NOT IN (SELECT e.idEstado FROM EstadoProyecto e WHERE e.nombreEstado IN ('Entregado', 'Anulado')) ORDER BY p.fechaInicio DESC LIMIT :limit")
    List<Proyecto> findTopNActivos(@Param("limit") int limit);

    @Query("SELECT p FROM Proyecto p WHERE p.idCliente = :idCliente ORDER BY p.fechaInicio DESC LIMIT :limit")
    List<Proyecto> findTopNByCliente(@Param("idCliente") Integer idCliente,
                                     @Param("limit") int limit);

    @Query("SELECT p FROM Proyecto p WHERE p.idEstadoActual IN (SELECT e.idEstado FROM EstadoProyecto e WHERE e.nombreEstado = :estado) ORDER BY p.fechaInicio DESC LIMIT :limit")
    List<Proyecto> findTopNByEstado(@Param("estado") String estado,
                                    @Param("limit") int limit);

    @Modifying
    @Transactional
    @Query("UPDATE Proyecto p SET p.idEstadoActual = :idEstado, p.fechaActualizacion = CURRENT_TIMESTAMP WHERE p.idProyecto = :idProyecto")
    void updateEstado(@Param("idProyecto") Integer idProyecto,
                      @Param("idEstado") Integer idEstadoNuevo);

    @Modifying
    @Transactional
    @Query("UPDATE Proyecto p SET p.fechaInicio = :fecha, p.fechaActualizacion = CURRENT_TIMESTAMP WHERE p.idProyecto = :idProyecto")
    void updateFechaInicio(@Param("idProyecto") Integer idProyecto,
                           @Param("fecha") LocalDate fechaInicio);

    @Modifying
    @Transactional
    @Query("UPDATE Proyecto p SET p.fechaEntregaReal = :fecha, p.fechaActualizacion = CURRENT_TIMESTAMP WHERE p.idProyecto = :idProyecto")
    void updateFechaEntregaReal(@Param("idProyecto") Integer idProyecto,
                                @Param("fecha") LocalDate fechaEntregaReal);

    @Modifying
    @Transactional
    @Query("UPDATE Proyecto p SET p.montoTotal = :monto, p.fechaActualizacion = CURRENT_TIMESTAMP WHERE p.idProyecto = :idProyecto")
    void updateMontoTotal(@Param("idProyecto") Integer idProyecto,
                          @Param("monto") BigDecimal montoTotal);

    @Modifying
    @Transactional
    @Query("UPDATE Proyecto p SET p.idEstadoActual = :idEstado, p.fechaEntregaReal = CURRENT_DATE, p.fechaActualizacion = CURRENT_TIMESTAMP WHERE p.idProyecto = :idProyecto")
    void marcarEntregado(@Param("idProyecto") Integer idProyecto,
                         @Param("idEstado") Integer idEstadoEntregado);

    @Modifying
    @Transactional
    @Query("UPDATE Proyecto p SET p.idEstadoActual = :idEstado, p.fechaActualizacion = CURRENT_TIMESTAMP WHERE p.idProyecto = :idProyecto")
    void marcarAnulado(@Param("idProyecto") Integer idProyecto,
                       @Param("idEstado") Integer idEstadoAnulado);

    @Modifying
    @Transactional
    @Query("DELETE FROM Proyecto p WHERE p.idCliente = :idCliente")
    void deleteByIdCliente(@Param("idCliente") Integer idCliente);

    @Modifying
    @Transactional
    @Query("DELETE FROM Proyecto p WHERE p.idEstadoActual IN (SELECT e.idEstado FROM EstadoProyecto e WHERE e.nombreEstado = :estado)")
    void deleteByEstado(@Param("estado") String estado);

    @Modifying
    @Transactional
    @Query("DELETE FROM Proyecto p WHERE p.idEstadoActual IN (SELECT e.idEstado FROM EstadoProyecto e WHERE e.nombreEstado = 'Anulado') AND p.fechaInicio < :fecha")
    int deleteAnuladosOlderThan(@Param("fecha") LocalDate fecha);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Proyecto p " +
           "WHERE p.idProyecto = :idProyecto AND p.idEstadoActual NOT IN (SELECT e.idEstado FROM EstadoProyecto e WHERE e.nombreEstado IN ('Entregado', 'Anulado'))")
    boolean canAnular(@Param("idProyecto") Integer idProyecto);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Proyecto p " +
           "WHERE p.idProyecto = :idProyecto AND p.idEstadoActual IN (SELECT e.idEstado FROM EstadoProyecto e WHERE e.nombreEstado IN ('Instalación/Montaje', 'En Producción', 'En Diseño'))")
    boolean canEntregar(@Param("idProyecto") Integer idProyecto);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Proyecto p " +
           "WHERE p.idProyecto = :idProyecto AND p.idEstadoActual NOT IN (SELECT e.idEstado FROM EstadoProyecto e WHERE e.nombreEstado IN ('Entregado', 'Anulado'))")
    boolean canRecibirPagos(@Param("idProyecto") Integer idProyecto);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Proyecto p " +
           "WHERE p.idCliente = :idCliente AND p.idEstadoActual NOT IN (SELECT e.idEstado FROM EstadoProyecto e WHERE e.nombreEstado IN ('Entregado', 'Anulado'))")
    boolean hasProyectosActivos(@Param("idCliente") Integer idCliente);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Proyecto p " +
           "WHERE p.idCliente = :idCliente AND p.idEstadoActual IN (SELECT e.idEstado FROM EstadoProyecto e WHERE e.nombreEstado = 'Entregado')")
    boolean hasProyectosEntregados(@Param("idCliente") Integer idCliente);

    @Query("SELECT " +
           "COUNT(p) as total, " +
           "SUM(CASE WHEN p.idEstadoActual NOT IN (SELECT e.idEstado FROM EstadoProyecto e WHERE e.nombreEstado IN ('Entregado', 'Anulado')) THEN 1 ELSE 0 END) as activos, " +
           "SUM(CASE WHEN p.idEstadoActual IN (SELECT e.idEstado FROM EstadoProyecto e WHERE e.nombreEstado = 'Entregado') THEN 1 ELSE 0 END) as entregados, " +
           "SUM(CASE WHEN p.idEstadoActual IN (SELECT e.idEstado FROM EstadoProyecto e WHERE e.nombreEstado = 'Anulado') THEN 1 ELSE 0 END) as anulados, " +
           "COALESCE(SUM(p.montoTotal), 0) as montoTotal " +
           "FROM Proyecto p")
    List<Object[]> getEstadisticasDashboard();

    @Query("SELECT " +
           "COUNT(p) as total, " +
           "SUM(CASE WHEN p.idEstadoActual NOT IN (SELECT e.idEstado FROM EstadoProyecto e WHERE e.nombreEstado IN ('Entregado', 'Anulado')) THEN 1 ELSE 0 END) as activos, " +
           "SUM(CASE WHEN p.idEstadoActual IN (SELECT e.idEstado FROM EstadoProyecto e WHERE e.nombreEstado = 'Entregado') THEN 1 ELSE 0 END) as entregados, " +
           "SUM(CASE WHEN p.idEstadoActual IN (SELECT e.idEstado FROM EstadoProyecto e WHERE e.nombreEstado = 'Anulado') THEN 1 ELSE 0 END) as anulados, " +
           "COALESCE(SUM(p.montoTotal), 0) as montoTotal " +
           "FROM Proyecto p WHERE p.idCliente = :idCliente")
    List<Object[]> getEstadisticasDashboardByCliente(@Param("idCliente") Integer idCliente);

    @Query("SELECT p.idProyecto, p.nombreProyecto, p.idEstadoActual, p.montoTotal, p.fechaInicio, p.fechaVencimiento " +
           "FROM Proyecto p ORDER BY p.fechaInicio DESC LIMIT :limit")
    List<Object[]> findProyectosDashboard(@Param("limit") int limit);

    @Query("SELECT MONTH(p.fechaInicio) as mes, COUNT(p) as total, COALESCE(SUM(p.montoTotal), 0) as monto " +
           "FROM Proyecto p WHERE YEAR(p.fechaInicio) = :year " +
           "GROUP BY MONTH(p.fechaInicio) ORDER BY mes ASC")
    List<Object[]> getEstadisticasPorMes(@Param("year") int year);

    @Query(value = "SELECT p.id_proyecto, p.nombre_proyecto, p.monto_total, p.fecha_vencimiento, " +
           "COALESCE(SUM(pg.monto), 0) as totalPagado " +
           "FROM proyecto p " +
           "LEFT JOIN pago pg ON p.id_proyecto = pg.id_proyecto AND pg.estado_pago = 'Validado' " +
           "WHERE p.fecha_vencimiento < :fecha " +
           "AND p.id_estado_actual NOT IN (SELECT id_estado FROM estado_proyecto WHERE nombre_estado IN ('Entregado', 'Anulado')) " +
           "GROUP BY p.id_proyecto " +
           "HAVING p.monto_total > COALESCE(SUM(pg.monto), 0) " +
           "ORDER BY p.fecha_vencimiento ASC " +
           "LIMIT :limit", nativeQuery = true)
    List<Object[]> findProyectosVencidosDashboard(@Param("fecha") LocalDate fecha,
                                                  @Param("limit") int limit);

    @Query("SELECT p FROM Proyecto p WHERE p.idCliente = :idCliente AND p.idEstadoActual IN (SELECT e.idEstado FROM EstadoProyecto e WHERE e.nombreEstado = :estado)")
    List<Proyecto> findByClienteAndEstado(@Param("idCliente") Integer idCliente,
                                          @Param("estado") String estado);
}