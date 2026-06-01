package com.stamos.repository;

import com.stamos.entity.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Resena.
 * Proporciona métodos CRUD básicos y consultas personalizadas.
 */
@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {

    /**
     * Busca reseñas por su estado (PENDIENTE, APROBADA, RECHAZADA).
     *
     * @param estado Estado de la reseña
     * @return Lista de reseñas con ese estado
     */
    List<Resena> findByEstado(String estado);

    /**
     * Verifica si ya existe una reseña para un proyecto específico.
     * Utilizado para evitar que un cliente califique más de una vez el mismo proyecto.
     *
     * @param proyectoId ID del proyecto
     * @return true si ya existe una reseña
     */
    boolean existsByProyectoId(Long proyectoId);
}