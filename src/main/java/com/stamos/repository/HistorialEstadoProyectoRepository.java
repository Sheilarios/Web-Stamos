package com.stamos.repository;

import com.stamos.entity.HistorialEstadoProyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistorialEstadoProyectoRepository extends JpaRepository<HistorialEstadoProyecto, Long> {

    List<HistorialEstadoProyecto> findByProyectoIdOrderByFechaHoraDesc(Long proyectoId);
}