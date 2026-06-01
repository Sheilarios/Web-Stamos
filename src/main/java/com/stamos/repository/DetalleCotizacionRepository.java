package com.stamos.repository;

import com.stamos.entity.DetalleCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio para la entidad DetalleCotizacion.
 * Proporciona métodos CRUD básicos y consultas personalizadas.
 */
@Repository
public interface DetalleCotizacionRepository extends JpaRepository<DetalleCotizacion, Long> {

    /**
     * Obtiene todos los detalles (ítems) de una cotización específica.
     *
     * @param cotizacionId ID de la cotización
     * @return Lista de detalles de la cotización
     */
    List<DetalleCotizacion> findByCotizacionId(Long cotizacionId);
}