package com.stamos.repository;

import com.stamos.entity.Cotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio para la entidad Cotizacion.
 * Proporciona métodos CRUD básicos y consultas personalizadas.
 */
@Repository
public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {

    /**
     * Busca cotizaciones por su estado.
     *
     * @param estado Estado de la cotización (EMITIDA, APROBADA, RECHAZADA, ANULADA)
     * @return Lista de cotizaciones con ese estado
     */
    List<Cotizacion> findByEstado(String estado);

    /**
     * Busca cotizaciones asociadas a un cliente específico.
     *
     * @param clienteId ID del cliente
     * @return Lista de cotizaciones del cliente
     */
    List<Cotizacion> findByClienteId(Long clienteId);

    /**
     * Verifica si un cliente tiene al menos una cotización asociada.
     * Útil para validar si se puede deshabilitar un cliente.
     *
     * @param clienteId ID del cliente
     * @return true si existe al menos una cotización
     */
    boolean existsByClienteId(Long clienteId);

    /**
     * Busca todas las versiones derivadas de una cotización base.
     *
     * @param versionBase Cotización base (versión original)
     * @return Lista de cotizaciones que tienen esta como base
     */
    List<Cotizacion> findByVersionBase(Cotizacion versionBase);

    /**
     * Cuenta cuántas cotizaciones tienen un estado específico.
     * Utilizado para reportes (aprobadas vs rechazadas).
     *
     * @param estado Estado de la cotización
     * @return Número de cotizaciones con ese estado
     */
    long countByEstado(String estado);
}