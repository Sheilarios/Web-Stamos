package com.stamos.repository;

import com.stamos.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByProyectoIdOrderByFechaDesc(Long proyectoId);

    @Query("SELECT SUM(p.monto) FROM Pago p WHERE p.fecha BETWEEN :start AND :end AND p.estado != 'ANULADO'")
    BigDecimal sumMontoByFechaBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT p FROM Pago p WHERE p.fecha BETWEEN :start AND :end AND p.estado != 'ANULADO'")
    List<Pago> findByFechaBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT p FROM Pago p WHERE p.proyecto.cliente.id = :clienteId")
    List<Pago> findByClienteId(@Param("clienteId") Long clienteId);

    @Query(value = "SELECT " +
                   "  c.id, " +
                   "  c.nombre_razon_social, " +
                   "  COALESCE(SUM(p.monto), 0) AS total_pagado, " +
                   "  cot.total AS total_cotizacion, " +
                   "  (cot.total - COALESCE(SUM(p.monto), 0)) AS saldo_pendiente " +
                   "FROM proyecto pro " +
                   "JOIN cotizacion cot ON pro.cotizacion_id = cot.id " +
                   "JOIN cliente c ON cot.cliente_id = c.id " +
                   "LEFT JOIN pago p ON p.proyecto_id = pro.id AND p.estado != 'ANULADO' " +
                   "WHERE pro.estado NOT IN ('ENTREGADO', 'ANULADO') " +
                   "GROUP BY c.id, c.nombre_razon_social, cot.total " +
                   "HAVING saldo_pendiente > 0 " +
                   "ORDER BY c.nombre_razon_social", nativeQuery = true)
    List<Object[]> findPagosPendientesPorCliente();
}