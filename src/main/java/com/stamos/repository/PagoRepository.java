package com.stamos.repository;

import com.stamos.model.Pago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

    String ESTADO_PENDIENTE = "Pendiente";
    String ESTADO_VALIDADO = "Validado";
    String ESTADO_RECHAZADO = "Rechazado";

    List<Pago> findByIdProyecto(Integer idProyecto);

    Page<Pago> findByIdProyecto(Integer idProyecto, Pageable pageable);

    @Query("SELECT p FROM Pago p WHERE p.idProyecto = :idProyecto ORDER BY p.fechaPago DESC")
    List<Pago> findByIdProyectoOrderByFechaPagoDesc(@Param("idProyecto") Integer idProyecto);

    List<Pago> findByIdProyectoAndEstadoPago(Integer idProyecto, String estadoPago);

    Page<Pago> findByIdProyectoAndEstadoPago(Integer idProyecto, String estadoPago, Pageable pageable);

    long countByIdProyecto(Integer idProyecto);

    long countByIdProyectoAndEstadoPago(Integer idProyecto, String estadoPago);

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p WHERE p.idProyecto = :idProyecto AND p.estadoPago = 'Validado'")
    BigDecimal sumMontoByProyecto(@Param("idProyecto") Integer idProyecto);

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p WHERE p.idProyecto = :idProyecto AND p.estadoPago IN ('Validado', 'Pendiente')")
    BigDecimal sumMontoTotalByProyecto(@Param("idProyecto") Integer idProyecto);

    List<Pago> findByEstadoPago(String estadoPago);

    Page<Pago> findByEstadoPago(String estadoPago, Pageable pageable);

    @Query("SELECT p FROM Pago p WHERE p.estadoPago = 'Pendiente' ORDER BY p.fechaPago ASC")
    List<Pago> findPendientes();

    @Query("SELECT p FROM Pago p WHERE p.estadoPago = 'Pendiente' ORDER BY p.fechaPago ASC")
    Page<Pago> findPendientes(Pageable pageable);

    @Query("SELECT p FROM Pago p WHERE p.estadoPago = 'Validado' ORDER BY p.fechaPago DESC")
    List<Pago> findValidados();

    @Query("SELECT p FROM Pago p WHERE p.estadoPago = 'Validado' ORDER BY p.fechaPago DESC")
    Page<Pago> findValidados(Pageable pageable);

    @Query("SELECT p FROM Pago p WHERE p.estadoPago = 'Rechazado' ORDER BY p.fechaPago DESC")
    List<Pago> findRechazados();

    @Query("SELECT p FROM Pago p WHERE p.estadoPago = 'Rechazado' ORDER BY p.fechaPago DESC")
    Page<Pago> findRechazados(Pageable pageable);

    long countByEstadoPago(String estadoPago);

    @Query("SELECT p.estadoPago, COUNT(p) as total FROM Pago p GROUP BY p.estadoPago ORDER BY p.estadoPago ASC")
    List<Object[]> countByEstadoPagoResumen();

    List<Pago> findByMetodoPago(String metodoPago);

    Page<Pago> findByMetodoPago(String metodoPago, Pageable pageable);

    List<Pago> findByMetodoPagoAndEstadoPago(String metodoPago, String estadoPago);

    long countByMetodoPago(String metodoPago);

    @Query("SELECT p.metodoPago, COUNT(p) as total FROM Pago p GROUP BY p.metodoPago ORDER BY p.metodoPago ASC")
    List<Object[]> countByMetodoPagoResumen();

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p WHERE p.metodoPago = :metodo AND p.estadoPago = 'Validado'")
    BigDecimal sumMontoByMetodoPago(@Param("metodo") String metodoPago);

    @Query("SELECT p FROM Pago p WHERE p.fechaPago BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaPago DESC")
    List<Pago> findByFechaPagoBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                      @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT p FROM Pago p WHERE p.fechaPago BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaPago DESC")
    Page<Pago> findByFechaPagoBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                      @Param("fechaFin") LocalDateTime fechaFin,
                                      Pageable pageable);

    @Query("SELECT p FROM Pago p WHERE p.fechaRegistro BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaRegistro DESC")
    List<Pago> findByFechaRegistroBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                          @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT p FROM Pago p WHERE p.fechaRegistro BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaRegistro DESC")
    Page<Pago> findByFechaRegistroBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                          @Param("fechaFin") LocalDateTime fechaFin,
                                          Pageable pageable);

    @Query("SELECT p FROM Pago p WHERE p.fechaValidacion BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaValidacion DESC")
    List<Pago> findByFechaValidacionBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                            @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT p FROM Pago p WHERE DATE(p.fechaPago) = CURRENT_DATE ORDER BY p.fechaPago DESC")
    List<Pago> findPagosHoy();

    @Query("SELECT p FROM Pago p WHERE p.fechaPago >= :fecha ORDER BY p.fechaPago DESC")
    List<Pago> findPagosUltimosDias(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT p FROM Pago p WHERE p.fechaPago >= :fecha ORDER BY p.fechaPago DESC")
    Page<Pago> findPagosUltimosDias(@Param("fecha") LocalDateTime fecha, Pageable pageable);

    @Query("SELECT p FROM Pago p WHERE p.idProyecto IN (SELECT pr.idProyecto FROM Proyecto pr WHERE pr.idCliente = :idCliente) ORDER BY p.fechaPago DESC")
    List<Pago> findPagosByCliente(@Param("idCliente") Integer idCliente);

    @Query("SELECT p FROM Pago p WHERE p.idProyecto IN (SELECT pr.idProyecto FROM Proyecto pr WHERE pr.idCliente = :idCliente) ORDER BY p.fechaPago DESC")
    Page<Pago> findPagosByCliente(@Param("idCliente") Integer idCliente, Pageable pageable);

    @Query("SELECT p FROM Pago p WHERE p.idProyecto IN (SELECT pr.idProyecto FROM Proyecto pr WHERE pr.idCliente = :idCliente) AND p.estadoPago = :estado ORDER BY p.fechaPago DESC")
    List<Pago> findPagosByClienteAndEstado(@Param("idCliente") Integer idCliente,
                                           @Param("estado") String estadoPago);

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p WHERE p.idProyecto IN (SELECT pr.idProyecto FROM Proyecto pr WHERE pr.idCliente = :idCliente) AND p.estadoPago = 'Validado'")
    BigDecimal sumMontoByCliente(@Param("idCliente") Integer idCliente);

    List<Pago> findByIdProyectoAndMetodoPago(Integer idProyecto, String metodoPago);

    @Query("SELECT p FROM Pago p WHERE p.idProyecto = :idProyecto AND p.fechaPago BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaPago DESC")
    List<Pago> findByIdProyectoAndFechaBetween(@Param("idProyecto") Integer idProyecto,
                                               @Param("fechaInicio") LocalDateTime fechaInicio,
                                               @Param("fechaFin") LocalDateTime fechaFin);

    List<Pago> findByMetodoPagoAndEstadoPagoOrderByFechaPagoDesc(String metodoPago, String estadoPago);

    @Query("SELECT p FROM Pago p WHERE p.monto >= :monto ORDER BY p.monto DESC")
    List<Pago> findByMontoGreaterThanEqual(@Param("monto") BigDecimal monto);

    @Query("SELECT p FROM Pago p WHERE p.monto >= :monto ORDER BY p.monto DESC")
    Page<Pago> findByMontoGreaterThanEqual(@Param("monto") BigDecimal monto, Pageable pageable);

    @Query("SELECT p FROM Pago p WHERE p.monto BETWEEN :montoMin AND :montoMax ORDER BY p.monto DESC")
    List<Pago> findByMontoBetween(@Param("montoMin") BigDecimal montoMin,
                                  @Param("montoMax") BigDecimal montoMax);

    @Query("SELECT COALESCE(AVG(p.monto), 0) FROM Pago p WHERE p.estadoPago = 'Validado'")
    BigDecimal avgMontoValidado();

    @Query("SELECT p FROM Pago p WHERE " +
           "CAST(p.idProyecto AS string) LIKE CONCAT('%', :searchText, '%') OR " +
           "LOWER(p.metodoPago) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(p.estadoPago) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(p.observaciones) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "ORDER BY p.fechaPago DESC")
    List<Pago> search(@Param("searchText") String searchText);

    @Query("SELECT p FROM Pago p WHERE " +
           "CAST(p.idProyecto AS string) LIKE CONCAT('%', :searchText, '%') OR " +
           "LOWER(p.metodoPago) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(p.estadoPago) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(p.observaciones) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "ORDER BY p.fechaPago DESC")
    Page<Pago> search(@Param("searchText") String searchText, Pageable pageable);

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p WHERE p.estadoPago = 'Validado'")
    BigDecimal sumTotalPagadoGeneral();

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p WHERE p.estadoPago IN ('Validado', 'Pendiente')")
    BigDecimal sumTotalPagadoConPendientes();

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p WHERE p.estadoPago = 'Pendiente'")
    BigDecimal sumTotalPendiente();

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p WHERE MONTH(p.fechaPago) = :month AND YEAR(p.fechaPago) = :year AND p.estadoPago = 'Validado'")
    BigDecimal sumMontoByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT COUNT(p) FROM Pago p WHERE MONTH(p.fechaPago) = :month AND YEAR(p.fechaPago) = :year")
    long countByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT COUNT(p) FROM Pago p WHERE p.estadoPago = 'Validado' AND MONTH(p.fechaValidacion) = :month AND YEAR(p.fechaValidacion) = :year")
    long countValidadosByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT DATE(p.fechaPago) as fecha, COUNT(p) as total " +
           "FROM Pago p " +
           "WHERE p.fechaPago >= :fechaInicio " +
           "GROUP BY DATE(p.fechaPago) " +
           "ORDER BY DATE(p.fechaPago) DESC")
    List<Object[]> countPagosByDay(@Param("fechaInicio") LocalDateTime fechaInicio);

    @Query("SELECT DATE(p.fechaPago) as fecha, COALESCE(SUM(p.monto), 0) as total " +
           "FROM Pago p " +
           "WHERE p.fechaPago >= :fechaInicio AND p.estadoPago = 'Validado' " +
           "GROUP BY DATE(p.fechaPago) " +
           "ORDER BY DATE(p.fechaPago) DESC")
    List<Object[]> sumPagosByDay(@Param("fechaInicio") LocalDateTime fechaInicio);

    @Query("SELECT p FROM Pago p ORDER BY p.fechaPago DESC LIMIT :limit")
    List<Pago> findTopN(@Param("limit") int limit);

    @Query("SELECT p FROM Pago p WHERE p.idProyecto = :idProyecto ORDER BY p.fechaPago DESC LIMIT :limit")
    List<Pago> findTopNByProyecto(@Param("idProyecto") Integer idProyecto, @Param("limit") int limit);

    @Query("SELECT p FROM Pago p WHERE p.idProyecto IN (SELECT pr.idProyecto FROM Proyecto pr WHERE pr.idCliente = :idCliente) ORDER BY p.fechaPago DESC LIMIT :limit")
    List<Pago> findTopNByCliente(@Param("idCliente") Integer idCliente, @Param("limit") int limit);

    @Query("SELECT p FROM Pago p WHERE p.estadoPago = 'Pendiente' ORDER BY p.fechaPago ASC LIMIT :limit")
    List<Pago> findTopNPendientes(@Param("limit") int limit);

    @Modifying
    @Transactional
    @Query("UPDATE Pago p SET p.estadoPago = :estado, p.idUsuarioValidacion = :idUsuario, p.fechaValidacion = CURRENT_TIMESTAMP, p.fechaActualizacion = CURRENT_TIMESTAMP WHERE p.idPago = :idPago")
    void updateEstado(@Param("idPago") Integer idPago,
                      @Param("estado") String nuevoEstado,
                      @Param("idUsuario") Integer idUsuarioValidacion);

    @Modifying
    @Transactional
    @Query("UPDATE Pago p SET p.estadoPago = 'Validado', p.idUsuarioValidacion = :idUsuario, p.fechaValidacion = CURRENT_TIMESTAMP, p.fechaActualizacion = CURRENT_TIMESTAMP WHERE p.idPago = :idPago AND p.estadoPago = 'Pendiente'")
    int validarPago(@Param("idPago") Integer idPago,
                    @Param("idUsuario") Integer idUsuarioValidacion);

    @Modifying
    @Transactional
    @Query("UPDATE Pago p SET p.estadoPago = 'Rechazado', p.idUsuarioValidacion = :idUsuario, p.fechaValidacion = CURRENT_TIMESTAMP, p.fechaActualizacion = CURRENT_TIMESTAMP WHERE p.idPago = :idPago AND p.estadoPago = 'Pendiente'")
    int rechazarPago(@Param("idPago") Integer idPago,
                     @Param("idUsuario") Integer idUsuarioValidacion);

    @Modifying
    @Transactional
    @Query("DELETE FROM Pago p WHERE p.idProyecto = :idProyecto")
    void deleteByIdProyecto(@Param("idProyecto") Integer idProyecto);

    @Modifying
    @Transactional
    @Query("DELETE FROM Pago p WHERE p.estadoPago = :estado")
    void deleteByEstadoPago(@Param("estado") String estadoPago);

    @Modifying
    @Transactional
    @Query("DELETE FROM Pago p WHERE p.estadoPago = 'Rechazado' AND p.fechaRegistro < :fecha")
    int deleteRechazadosOlderThan(@Param("fecha") LocalDateTime fecha);

    boolean existsByIdProyecto(Integer idProyecto);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Pago p WHERE p.idProyecto = :idProyecto AND p.estadoPago = 'Validado'")
    boolean hasPagosValidados(@Param("idProyecto") Integer idProyecto);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Pago p WHERE p.idProyecto = :idProyecto AND p.estadoPago = 'Pendiente'")
    boolean hasPagosPendientes(@Param("idProyecto") Integer idProyecto);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Pago p WHERE p.idPago = :idPago AND p.estadoPago = 'Pendiente'")
    boolean canValidar(@Param("idPago") Integer idPago);

    @Query("SELECT " +
           "COUNT(p) as total, " +
           "SUM(CASE WHEN p.estadoPago = 'Validado' THEN 1 ELSE 0 END) as validados, " +
           "SUM(CASE WHEN p.estadoPago = 'Pendiente' THEN 1 ELSE 0 END) as pendientes, " +
           "SUM(CASE WHEN p.estadoPago = 'Rechazado' THEN 1 ELSE 0 END) as rechazados, " +
           "COALESCE(SUM(CASE WHEN p.estadoPago = 'Validado' THEN p.monto ELSE 0 END), 0) as totalValidado " +
           "FROM Pago p")
    List<Object[]> getEstadisticasDashboard();

    @Query("SELECT " +
           "COUNT(p) as total, " +
           "SUM(CASE WHEN p.estadoPago = 'Validado' THEN 1 ELSE 0 END) as validados, " +
           "SUM(CASE WHEN p.estadoPago = 'Pendiente' THEN 1 ELSE 0 END) as pendientes, " +
           "SUM(CASE WHEN p.estadoPago = 'Rechazado' THEN 1 ELSE 0 END) as rechazados, " +
           "COALESCE(SUM(CASE WHEN p.estadoPago = 'Validado' THEN p.monto ELSE 0 END), 0) as totalValidado " +
           "FROM Pago p WHERE p.idProyecto = :idProyecto")
    List<Object[]> getEstadisticasDashboardByProyecto(@Param("idProyecto") Integer idProyecto);

    @Query("SELECT " +
           "COUNT(p) as total, " +
           "SUM(CASE WHEN p.estadoPago = 'Validado' THEN 1 ELSE 0 END) as validados, " +
           "SUM(CASE WHEN p.estadoPago = 'Pendiente' THEN 1 ELSE 0 END) as pendientes, " +
           "SUM(CASE WHEN p.estadoPago = 'Rechazado' THEN 1 ELSE 0 END) as rechazados, " +
           "COALESCE(SUM(CASE WHEN p.estadoPago = 'Validado' THEN p.monto ELSE 0 END), 0) as totalValidado " +
           "FROM Pago p WHERE p.idProyecto IN (SELECT pr.idProyecto FROM Proyecto pr WHERE pr.idCliente = :idCliente)")
    List<Object[]> getEstadisticasDashboardByCliente(@Param("idCliente") Integer idCliente);
}