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

@Repository
public interface ComprobantePagoRepository extends JpaRepository<ComprobantePago, Integer> {

    Optional<ComprobantePago> findByIdPago(Integer idPago);

    @Query("SELECT c FROM ComprobantePago c WHERE c.idPago = :idPago ORDER BY c.fechaRegistro DESC")
    Page<ComprobantePago> findByIdPago(@Param("idPago") Integer idPago, Pageable pageable);

    @Query("SELECT c FROM ComprobantePago c WHERE c.idPago IN :idsPago ORDER BY c.fechaRegistro DESC")
    List<ComprobantePago> findByIdPagoIn(@Param("idsPago") List<Integer> idsPago);

    List<ComprobantePago> findByTipoComprobante(String tipoComprobante);

    Page<ComprobantePago> findByTipoComprobante(String tipoComprobante, Pageable pageable);

    long countByTipoComprobante(String tipoComprobante);

    @Query("SELECT c FROM ComprobantePago c WHERE c.numeroComprobante LIKE CONCAT('%', :numero, '%') ORDER BY c.fechaRegistro DESC")
    List<ComprobantePago> findByNumeroComprobanteContaining(@Param("numero") String numeroComprobante);

    @Query("SELECT c FROM ComprobantePago c WHERE c.numeroComprobante LIKE CONCAT('%', :numero, '%') ORDER BY c.fechaRegistro DESC")
    Page<ComprobantePago> findByNumeroComprobanteContaining(@Param("numero") String numeroComprobante, Pageable pageable);

    Optional<ComprobantePago> findByNumeroComprobante(String numeroComprobante);

    List<ComprobantePago> findByValidado(Boolean validado);

    Page<ComprobantePago> findByValidado(Boolean validado, Pageable pageable);

    @Query("SELECT c FROM ComprobantePago c WHERE c.validado = false ORDER BY c.fechaRegistro ASC")
    List<ComprobantePago> findPendientes();

    @Query("SELECT c FROM ComprobantePago c WHERE c.validado = false ORDER BY c.fechaRegistro ASC")
    Page<ComprobantePago> findPendientes(Pageable pageable);

    @Query("SELECT c FROM ComprobantePago c WHERE c.validado = true ORDER BY c.fechaValidacion DESC")
    List<ComprobantePago> findValidados();


    @Query("SELECT c FROM ComprobantePago c WHERE c.validado = true ORDER BY c.fechaValidacion DESC")
    Page<ComprobantePago> findValidados(Pageable pageable);

    List<ComprobantePago> findByIdUsuarioValidador(Integer idUsuarioValidador);

    Page<ComprobantePago> findByIdUsuarioValidador(Integer idUsuarioValidador, Pageable pageable);

    @Query("SELECT c FROM ComprobantePago c WHERE c.fechaRegistro BETWEEN :fechaInicio AND :fechaFin ORDER BY c.fechaRegistro DESC")
    List<ComprobantePago> findByFechaRegistroBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                     @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT c FROM ComprobantePago c WHERE c.fechaRegistro BETWEEN :fechaInicio AND :fechaFin ORDER BY c.fechaRegistro DESC")
    Page<ComprobantePago> findByFechaRegistroBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                     @Param("fechaFin") LocalDateTime fechaFin,
                                                     Pageable pageable);

    @Query("SELECT c FROM ComprobantePago c WHERE c.fechaValidacion BETWEEN :fechaInicio AND :fechaFin ORDER BY c.fechaValidacion DESC")
    List<ComprobantePago> findByFechaValidacionBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                       @Param("fechaFin") LocalDateTime fechaFin);


    @Query("SELECT c FROM ComprobantePago c WHERE c.fechaValidacion BETWEEN :fechaInicio AND :fechaFin ORDER BY c.fechaValidacion DESC")
    Page<ComprobantePago> findByFechaValidacionBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                       @Param("fechaFin") LocalDateTime fechaFin,
                                                       Pageable pageable);


    @Query("SELECT c FROM ComprobantePago c WHERE DATE(c.fechaRegistro) = CURRENT_DATE ORDER BY c.fechaRegistro DESC")
    List<ComprobantePago> findRegistradosHoy();


    @Query("SELECT c FROM ComprobantePago c WHERE DATE(c.fechaValidacion) = CURRENT_DATE ORDER BY c.fechaValidacion DESC")
    List<ComprobantePago> findValidadosHoy();


    List<ComprobantePago> findByTipoComprobanteAndValidado(String tipoComprobante, Boolean validado);


    Page<ComprobantePago> findByTipoComprobanteAndValidado(String tipoComprobante, Boolean validado, Pageable pageable);


    @Query("SELECT c FROM ComprobantePago c WHERE c.idPago = :idPago AND c.validado = true")
    Optional<ComprobantePago> findValidadoByIdPago(@Param("idPago") Integer idPago);


    @Query("SELECT c FROM ComprobantePago c WHERE c.archivoUrl IS NOT NULL AND c.archivoUrl != '' ORDER BY c.fechaRegistro DESC")
    List<ComprobantePago> findWithArchivo();


    @Query("SELECT c FROM ComprobantePago c WHERE c.archivoUrl IS NOT NULL AND c.archivoUrl != '' ORDER BY c.fechaRegistro DESC")
    Page<ComprobantePago> findWithArchivo(Pageable pageable);


    @Query("SELECT c FROM ComprobantePago c WHERE c.archivoUrl IS NULL OR c.archivoUrl = '' ORDER BY c.fechaRegistro DESC")
    List<ComprobantePago> findWithoutArchivo();


    @Query("SELECT COUNT(c) FROM ComprobantePago c WHERE c.validado = false")
    long countPendientes();


    @Query("SELECT COUNT(c) FROM ComprobantePago c WHERE c.validado = true")
    long countValidados();


    @Query("SELECT COUNT(c) FROM ComprobantePago c WHERE c.tipoComprobante = :tipo")
    long countByTipo(@Param("tipo") String tipoComprobante);


    @Query("SELECT COUNT(c) FROM ComprobantePago c WHERE c.tipoComprobante = :tipo AND c.validado = :validado")
    long countByTipoAndValidado(@Param("tipo") String tipoComprobante, @Param("validado") Boolean validado);


    @Query("SELECT COUNT(c) FROM ComprobantePago c WHERE MONTH(c.fechaRegistro) = :month AND YEAR(c.fechaRegistro) = :year")
    long countByMonthAndYear(@Param("month") int month, @Param("year") int year);


    @Query("SELECT COUNT(c) FROM ComprobantePago c WHERE c.validado = true AND MONTH(c.fechaValidacion) = :month AND YEAR(c.fechaValidacion) = :year")
    long countValidadosByMonthAndYear(@Param("month") int month, @Param("year") int year);


    @Query("SELECT c FROM ComprobantePago c " +
           "WHERE c.idPago IN (SELECT p.idPago FROM Pago p " +
           "WHERE p.idProyecto IN (SELECT pr.idProyecto FROM Proyecto pr WHERE pr.idCliente = :idCliente)) " +
           "ORDER BY c.fechaRegistro DESC")
    List<ComprobantePago> findComprobantesByCliente(@Param("idCliente") Integer idCliente);


    @Query("SELECT c FROM ComprobantePago c " +
           "WHERE c.idPago IN (SELECT p.idPago FROM Pago p " +
           "WHERE p.idProyecto IN (SELECT pr.idProyecto FROM Proyecto pr WHERE pr.idCliente = :idCliente)) " +
           "ORDER BY c.fechaRegistro DESC")
    Page<ComprobantePago> findComprobantesByCliente(@Param("idCliente") Integer idCliente, Pageable pageable);


    @Query("SELECT c FROM ComprobantePago c " +
           "WHERE c.idPago IN (SELECT p.idPago FROM Pago p WHERE p.idProyecto = :idProyecto) " +
           "ORDER BY c.fechaRegistro DESC")
    List<ComprobantePago> findComprobantesByProyecto(@Param("idProyecto") Integer idProyecto);


    @Query("DELETE FROM ComprobantePago c WHERE c.idPago = :idPago")
    void deleteByIdPago(@Param("idPago") Integer idPago);


    @Query("DELETE FROM ComprobantePago c WHERE c.fechaRegistro < :fecha")
    int deleteOlderThan(@Param("fecha") LocalDateTime fecha);
}