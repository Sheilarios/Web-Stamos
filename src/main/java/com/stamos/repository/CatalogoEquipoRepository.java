package com.stamos.repository;

import com.stamos.entity.CatalogoEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio para la entidad CatalogoEquipo.
 * Proporciona métodos CRUD básicos y consultas personalizadas para la gestión de equipos.
 */
@Repository
public interface CatalogoEquipoRepository extends JpaRepository<CatalogoEquipo, Long> {

    /**
     * Busca equipos cuyo nombre contenga el texto ignorando mayúsculas/minúsculas.
     * Útil para búsquedas en el catálogo.
     *
     * @param nombre Texto a buscar en el nombre
     * @return Lista de equipos que coinciden
     */
    List<CatalogoEquipo> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Busca equipos con cantidad total mayor o igual a un valor mínimo.
     * Útil para filtrar equipos con stock disponible.
     *
     * @param cantidad Cantidad mínima
     * @return Lista de equipos que cumplen
     */
    List<CatalogoEquipo> findByCantidadTotalGreaterThanEqual(int cantidad);

    /**
     * Verifica si existe un equipo con un nombre específico (ignorando mayúsculas).
     *
     * @param nombre Nombre del equipo
     * @return true si ya existe
     */
    boolean existsByNombreIgnoreCase(String nombre);
}