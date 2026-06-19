package com.stamos.repository;

import com.stamos.model.ComprobantePago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad ComprobantePago.
 * 
 * <p>Proporciona métodos para acceder y gestionar los comprobantes de pago
 * asociados a los pagos realizados por los clientes de STAMOS S.L.</p>
 * 
 * <p>Implementa los requerimientos:</p>
 * <ul>
 *   <li>RN-12: Todo comprobante de pago debe ser validado por el área administrativa</li>
 *   <li>RF-24: Registrar pagos con número de comprobante</li>
 *   <li>RF-25: Validar comprobantes de pago antes de confirmar el pago</li>
 * </ul>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
@Repository
public interface ComprobantePagoRepository extends JpaRepository<ComprobantePago, Integer> {

    // ============================================
    // BÚSQUEDAS POR PAGO
    // ============================================

    /**
     * Busca un comprobante por ID de pago.
     * 
     * @param idPago ID del pago
     * @return Comprobante encontrado
     */
    Optional<ComprobantePago> findByIdPago(Integer idPago);

    /**
     * Busca comprobantes por ID de pago con paginación.
     * 
     * @param idPago ID del pago
     * @param pageable Configuración de paginación
     * @return Página de comprobantes
     */
    @Query("SELECT c FROM ComprobantePago c WHERE c.idPago = :idPago ORDER BY c.fechaRegistro DESC")
    Page<ComprobantePago> findByIdPago(@Param("idPago") Integer idPago, Pageable pageable);

    /**
     * Busca comprobantes por IDs de pago.
     * 
     * @param idsPago Lista de IDs de pago
     * @return Lista de comprobantes
     */
    @Query("SELECT c FROM ComprobantePago c WHERE c.idPago IN :idsPago ORDER BY c.fechaRegistro DESC")
    List<ComprobantePago> findByIdPagoIn(@Param("idsPago") List<Integer> idsPago);

    // ============================================
    // BÚSQUEDAS POR TIPO DE COMPROBANTE
    // ============================================

    /**
     * Busca comprobantes por tipo.
     * 
     * @param tipoComprobante Tipo de comprobante (Factura, Boleta, Ticket, Otro)
     * @return Lista de comprobantes
     */
    List<ComprobantePago> findByTipoComprobante(String tipoComprobante);

    /**
     * Busca comprobantes por tipo con paginación.
     * 
     * @param tipoComprobante Tipo de comprobante
     * @param pageable Configuración de paginación
     * @return Página de comprobantes
     */
    Page<ComprobantePago> findByTipoComprobante(String tipoComprobante, Pageable pageable);

    /**
     * Cuenta comprobantes por tipo.
     * 
     * @param tipoComprobante Tipo de comprobante
     * @return Número de comprobantes
     */
    long countByTipoComprobante(String tipoComprobante);

    // ============================================
    // BÚSQUEDAS POR NÚMERO DE COMPROBANTE
    // ============================================

    /**
     * Busca comprobantes por número (búsqueda parcial).
     * 
     * @param numeroComprobante Número del comprobante
     * @return Lista de comprobantes
     */
    @Query("SELECT c FROM ComprobantePago c WHERE c.numeroComprobante LIKE CONCAT('%', :numero, '%') ORDER BY c.fechaRegistro DESC")
    List<ComprobantePago> findByNumeroComprobanteContaining(@Param("numero") String numeroComprobante);

    /**
     * Busca comprobantes por número (búsqueda parcial) con paginación.
     * 
     * @param numeroComprobante Número del comprobante
     * @param pageable Configuración de paginación
     * @return Página de comprobantes
     */
    @Query("SELECT c FROM ComprobantePago c WHERE c.numeroComprobante LIKE CONCAT('%', :numero, '%') ORDER BY c.fechaRegistro DESC")
    Page<ComprobantePago> findByNumeroComprobanteContaining(@Param("numero") String numeroComprobante, Pageable pageable);

    /**
     * Busca comprobantes por número exacto.
     * 
     * @param numeroComprobante Número del comprobante
     * @return Comprobante encontrado
     */
    Optional<ComprobantePago> findByNumeroComprobante(String numeroComprobante);

    // ============================================
    // BÚSQUEDAS POR ESTADO DE VALIDACIÓN
    // ============================================

    /**
     * Busca comprobantes por estado de validación.
     * 
     * @param validado Estado de validación
     * @return Lista de comprobantes
     */
    List<ComprobantePago> findByValidado(Boolean validado);

    /**
     * Busca comprobantes por estado de validación con paginación.
     * 
     * @param validado Estado de validación
     * @param pageable Configuración de paginación
     * @return Página de comprobantes
     */
    Page<ComprobantePago> findByValidado(Boolean validado, Pageable pageable);

    /**
     * Busca comprobantes pendientes de validación.
     * 
     * @return Lista de comprobantes pendientes
     */
    @Query("SELECT c FROM ComprobantePago c WHERE c.validado = false ORDER BY c.fechaRegistro ASC")
    List<ComprobantePago> findPendientes();

    /**
     * Busca comprobantes pendientes de validación con paginación.
     * 
     * @param pageable Configuración de paginación
     * @return Página de comprobantes pendientes
     */
    @Query("SELECT c FROM ComprobantePago c WHERE c.validado = false ORDER BY c.fechaRegistro ASC")
    Page<ComprobantePago> findPendientes(Pageable pageable);

    /**
     * Busca comprobantes validados.
     * 
     * @return Lista de comprobantes validados
     */
    @Query("SELECT c FROM ComprobantePago c WHERE c.validado = true ORDER BY c.fechaValidacion DESC")
    List<ComprobantePago> findValidados();

    /**
     * Busca comprobantes validados con paginación.
     * 
     * @param pageable Configuración de paginación
     * @return Página de comprobantes validados
     */
    @Query("SELECT c FROM ComprobantePago c WHERE c.validado = true ORDER BY c.fechaValidacion DESC")
    Page<ComprobantePago> findValidados(Pageable pageable);

    // ============================================
    // BÚSQUEDAS POR VALIDADOR
    // ============================================

    /**
     * Busca comprobantes validados por un usuario.
     * 
     * @param idUsuarioValidador ID del usuario validador
     * @return Lista de comprobantes
     */
    List<ComprobantePago> findByIdUsuarioValidador(Integer idUsuarioValidador);

    /**
     * Busca comprobantes validados por un usuario con paginación.
     * 
     * @param idUsuarioValidador ID del usuario validador
     * @param pageable Configuración de paginación
     * @return Página de comprobantes
     */
    Page<ComprobantePago> findByIdUsuarioValidador(Integer idUsuarioValidador, Pageable pageable);

    // ============================================
    // BÚSQUEDAS POR FECHAS
    // ============================================

    /**
     * Busca comprobantes por rango de fechas de registro.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de comprobantes
     */
    @Query("SELECT c FROM ComprobantePago c WHERE c.fechaRegistro BETWEEN :fechaInicio AND :fechaFin ORDER BY c.fechaRegistro DESC")
    List<ComprobantePago> findByFechaRegistroBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                     @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Busca comprobantes por rango de fechas de registro con paginación.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param pageable Configuración de paginación
     * @return Página de comprobantes
     */
    @Query("SELECT c FROM ComprobantePago c WHERE c.fechaRegistro BETWEEN :fechaInicio AND :fechaFin ORDER BY c.fechaRegistro DESC")
    Page<ComprobantePago> findByFechaRegistroBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                     @Param("fechaFin") LocalDateTime fechaFin,
                                                     Pageable pageable);

    /**
     * Busca comprobantes por rango de fechas de validación.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de comprobantes
     */
    @Query("SELECT c FROM ComprobantePago c WHERE c.fechaValidacion BETWEEN :fechaInicio AND :fechaFin ORDER BY c.fechaValidacion DESC")
    List<ComprobantePago> findByFechaValidacionBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                       @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Busca comprobantes por rango de fechas de validación con paginación.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param pageable Configuración de paginación
     * @return Página de comprobantes
     */
    @Query("SELECT c FROM ComprobantePago c WHERE c.fechaValidacion BETWEEN :fechaInicio AND :fechaFin ORDER BY c.fechaValidacion DESC")
    Page<ComprobantePago> findByFechaValidacionBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                       @Param("fechaFin") LocalDateTime fechaFin,
                                                       Pageable pageable);

    /**
     * Busca comprobantes registrados en el día actual.
     * 
     * @return Lista de comprobantes
     */
    @Query("SELECT c FROM ComprobantePago c WHERE DATE(c.fechaRegistro) = CURRENT_DATE ORDER BY c.fechaRegistro DESC")
    List<ComprobantePago> findRegistradosHoy();

    /**
     * Busca comprobantes validados en el día actual.
     * 
     * @return Lista de comprobantes
     */
    @Query("SELECT c FROM ComprobantePago c WHERE DATE(c.fechaValidacion) = CURRENT_DATE ORDER BY c.fechaValidacion DESC")
    List<ComprobantePago> findValidadosHoy();

    // ============================================
    // BÚSQUEDAS COMBINADAS
    // ============================================

    /**
     * Busca comprobantes por tipo y estado de validación.
     * 
     * @param tipoComprobante Tipo de comprobante
     * @param validado Estado de validación
     * @return Lista de comprobantes
     */
    List<ComprobantePago> findByTipoComprobanteAndValidado(String tipoComprobante, Boolean validado);

    /**
     * Busca comprobantes por tipo y estado de validación con paginación.
     * 
     * @param tipoComprobante Tipo de comprobante
     * @param validado Estado de validación
     * @param pageable Configuración de paginación
     * @return Página de comprobantes
     */
    Page<ComprobantePago> findByTipoComprobanteAndValidado(String tipoComprobante, Boolean validado, Pageable pageable);

    /**
     * Busca comprobantes de un pago que estén validados.
     * 
     * @param idPago ID del pago
     * @return Comprobante encontrado
     */
    @Query("SELECT c FROM ComprobantePago c WHERE c.idPago = :idPago AND c.validado = true")
    Optional<ComprobantePago> findValidadoByIdPago(@Param("idPago") Integer idPago);

    // ============================================
    // BÚSQUEDAS CON ARCHIVO
    // ============================================

    /**
     * Busca comprobantes que tienen archivo adjunto.
     * 
     * @return Lista de comprobantes con archivo
     */
    @Query("SELECT c FROM ComprobantePago c WHERE c.archivoUrl IS NOT NULL AND c.archivoUrl != '' ORDER BY c.fechaRegistro DESC")
    List<ComprobantePago> findWithArchivo();

    /**
     * Busca comprobantes que tienen archivo adjunto con paginación.
     * 
     * @param pageable Configuración de paginación
     * @return Página de comprobantes con archivo
     */
    @Query("SELECT c FROM ComprobantePago c WHERE c.archivoUrl IS NOT NULL AND c.archivoUrl != '' ORDER BY c.fechaRegistro DESC")
    Page<ComprobantePago> findWithArchivo(Pageable pageable);

    /**
     * Busca comprobantes sin archivo adjunto.
     * 
     * @return Lista de comprobantes sin archivo
     */
    @Query("SELECT c FROM ComprobantePago c WHERE c.archivoUrl IS NULL OR c.archivoUrl = '' ORDER BY c.fechaRegistro DESC")
    List<ComprobantePago> findWithoutArchivo();

    // ============================================
    // MÉTRICAS Y CONTADORES
    // ============================================

    /**
     * Cuenta comprobantes pendientes de validación.
     * 
     * @return Número de comprobantes pendientes
     */
    @Query("SELECT COUNT(c) FROM ComprobantePago c WHERE c.validado = false")
    long countPendientes();

    /**
     * Cuenta comprobantes validados.
     * 
     * @return Número de comprobantes validados
     */
    @Query("SELECT COUNT(c) FROM ComprobantePago c WHERE c.validado = true")
    long countValidados();

    /**
     * Cuenta comprobantes por tipo.
     * 
     * @param tipoComprobante Tipo de comprobante
     * @return Número de comprobantes
     */
    @Query("SELECT COUNT(c) FROM ComprobantePago c WHERE c.tipoComprobante = :tipo")
    long countByTipo(@Param("tipo") String tipoComprobante);

    /**
     * Cuenta comprobantes por tipo y estado de validación.
     * 
     * @param tipoComprobante Tipo de comprobante
     * @param validado Estado de validación
     * @return Número de comprobantes
     */
    @Query("SELECT COUNT(c) FROM ComprobantePago c WHERE c.tipoComprobante = :tipo AND c.validado = :validado")
    long countByTipoAndValidado(@Param("tipo") String tipoComprobante, @Param("validado") Boolean validado);

    /**
     * Cuenta comprobantes por mes y año.
     * 
     * @param month Mes (1-12)
     * @param year Año
     * @return Número de comprobantes
     */
    @Query("SELECT COUNT(c) FROM ComprobantePago c WHERE MONTH(c.fechaRegistro) = :month AND YEAR(c.fechaRegistro) = :year")
    long countByMonthAndYear(@Param("month") int month, @Param("year") int year);

    /**
     * Cuenta comprobantes validados por mes y año.
     * 
     * @param month Mes (1-12)
     * @param year Año
     * @return Número de comprobantes
     */
    @Query("SELECT COUNT(c) FROM ComprobantePago c WHERE c.validado = true AND MONTH(c.fechaValidacion) = :month AND YEAR(c.fechaValidacion) = :year")
    long countValidadosByMonthAndYear(@Param("month") int month, @Param("year") int year);

    // ============================================
    // BÚSQUEDAS PARA EL PORTAL DEL CLIENTE
    // ============================================

    /**
     * Busca comprobantes de pagos de un cliente.
     * 
     * @param idCliente ID del cliente
     * @return Lista de comprobantes
     */
    @Query("SELECT c FROM ComprobantePago c " +
           "WHERE c.idPago IN (SELECT p.idPago FROM Pago p " +
           "WHERE p.idProyecto IN (SELECT pr.idProyecto FROM Proyecto pr WHERE pr.idCliente = :idCliente)) " +
           "ORDER BY c.fechaRegistro DESC")
    List<ComprobantePago> findComprobantesByCliente(@Param("idCliente") Integer idCliente);

    /**
     * Busca comprobantes de pagos de un cliente con paginación.
     * 
     * @param idCliente ID del cliente
     * @param pageable Configuración de paginación
     * @return Página de comprobantes
     */
    @Query("SELECT c FROM ComprobantePago c " +
           "WHERE c.idPago IN (SELECT p.idPago FROM Pago p " +
           "WHERE p.idProyecto IN (SELECT pr.idProyecto FROM Proyecto pr WHERE pr.idCliente = :idCliente)) " +
           "ORDER BY c.fechaRegistro DESC")
    Page<ComprobantePago> findComprobantesByCliente(@Param("idCliente") Integer idCliente, Pageable pageable);

    /**
     * Busca comprobantes de un proyecto.
     * 
     * @param idProyecto ID del proyecto
     * @return Lista de comprobantes
     */
    @Query("SELECT c FROM ComprobantePago c " +
           "WHERE c.idPago IN (SELECT p.idPago FROM Pago p WHERE p.idProyecto = :idProyecto) " +
           "ORDER BY c.fechaRegistro DESC")
    List<ComprobantePago> findComprobantesByProyecto(@Param("idProyecto") Integer idProyecto);

    // ============================================
    // ELIMINACIÓN
    // ============================================

    /**
     * Elimina comprobantes por ID de pago.
     * 
     * @param idPago ID del pago
     */
    @Query("DELETE FROM ComprobantePago c WHERE c.idPago = :idPago")
    void deleteByIdPago(@Param("idPago") Integer idPago);

    /**
     * Elimina comprobantes anteriores a una fecha.
     * 
     * @param fecha Fecha límite
     * @return Número de comprobantes eliminados
     */
    @Query("DELETE FROM ComprobantePago c WHERE c.fechaRegistro < :fecha")
    int deleteOlderThan(@Param("fecha") LocalDateTime fecha);
}