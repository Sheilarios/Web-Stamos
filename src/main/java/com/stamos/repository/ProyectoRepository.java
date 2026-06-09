package com.stamos.repository;

import com.stamos.entity.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    List<Proyecto> findByEstado(String estado);
    List<Proyecto> findByClienteId(Long clienteId);
    List<Proyecto> findByEstadoNotIn(List<String> estados);
    List<Proyecto> findByEstadoIn(List<String> estados);
    List<Proyecto> findByResponsableIdAndEstadoNotIn(Long responsableId, List<String> estados);
    boolean existsByCotizacionId(Long cotizacionId);
    boolean existsByClienteId(Long clienteId);
    
    List<Proyecto> findByEstadoInAndFechaEntregaEstimadaBefore(List<String> estados, LocalDate fecha);
    List<Proyecto> findByEstadoInAndFechaEntregaEstimadaBetween(List<String> estados, LocalDate inicio, LocalDate fin);
    
    @Query("SELECT p FROM Proyecto p ORDER BY p.fechaCreacion DESC LIMIT :limit")
    List<Proyecto> findTopNByOrderByFechaCreacionDesc(@Param("limit") int limit);
    
    List<Proyecto> findTop5ByOrderByFechaCreacionDesc();
    
    long countByEstadoIn(List<String> estados);
    long countByEstadoNotIn(List<String> estados);
    int countByEstadoInAndFechaEntregaEstimadaBefore(List<String> estados, LocalDate fecha);
}