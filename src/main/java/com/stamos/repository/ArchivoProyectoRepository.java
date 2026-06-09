package com.stamos.repository;

import com.stamos.entity.ArchivoProyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArchivoProyectoRepository extends JpaRepository<ArchivoProyecto, Long> {
    List<ArchivoProyecto> findByProyectoIdOrderByFechaSubidaDesc(Long proyectoId);
}