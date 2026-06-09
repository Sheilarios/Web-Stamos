package com.stamos.repository;

import com.stamos.entity.Cotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {

    List<Cotizacion> findByEstado(String estado);
    List<Cotizacion> findByClienteId(Long clienteId);
    List<Cotizacion> findByClienteIdOrderByFechaEmisionDesc(Long clienteId);
    List<Cotizacion> findByFechaEmisionBetween(LocalDate inicio, LocalDate fin);
    List<Cotizacion> findTop5ByOrderByFechaEmisionDesc();
    List<Cotizacion> findByVersionBase(Cotizacion versionBase);
    boolean existsByClienteId(Long clienteId);
    
    long countByEstado(String estado);
    long countByFechaEmisionBetween(LocalDate inicio, LocalDate fin);
    long countByClienteId(Long clienteId);
    
    @Query("SELECT COALESCE(SUM(c.total), 0) FROM Cotizacion c WHERE c.estado = :estado")
    BigDecimal sumTotalByEstado(@Param("estado") String estado);
    
    @Query("SELECT COALESCE(SUM(c.total), 0) FROM Cotizacion c WHERE c.cliente.id = :clienteId")
    BigDecimal sumTotalByClienteId(@Param("clienteId") Long clienteId);
    
    @Query("SELECT COALESCE(SUM(c.total), 0) FROM Cotizacion c WHERE c.fechaEmision BETWEEN :inicio AND :fin")
    BigDecimal sumTotalByFechaEmisionBetween(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}