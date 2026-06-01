package com.stamos.repository;

import com.stamos.entity.CatalogoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio para la entidad CatalogoProducto.
 * Proporciona métodos CRUD básicos y consultas personalizadas.
 */
@Repository
public interface CatalogoProductoRepository extends JpaRepository<CatalogoProducto, Long> {

    /**
     * Busca productos/servicios cuyo nombre contenga el texto (ignorando mayúsculas/minúsculas).
     *
     * @param nombre Texto a buscar
     * @return Lista de productos coincidentes
     */
    List<CatalogoProducto> findByNombreContainingIgnoreCase(String nombre);
}