package com.stamos.repository;

import com.stamos.entity.ArchivoProyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio para la entidad ArchivoProyecto.
 * Proporciona métodos CRUD básicos y consultas personalizadas.
 */
@Repository
public interface ArchivoProyectoRepository extends JpaRepository<ArchivoProyecto, Long> {

    /**
     * Obtiene todos los archivos asociados a un proyecto,
     * ordenados por fecha de subida descendente (más reciente primero).
     *
     * @param proyectoId ID del proyecto
     * @return Lista de archivos del proyecto
     */
    List<ArchivoProyecto> findByProyectoIdOrderByFechaSubidaDesc(Long proyectoId);
}