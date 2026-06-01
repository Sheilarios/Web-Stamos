package com.stamos.repository;

import com.stamos.entity.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio para la entidad Proyecto.
 * Proporciona métodos CRUD básicos y consultas personalizadas.
 */
@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    /**
     * Busca proyectos por su estado.
     *
     * @param estado Estado del proyecto (COTIZADO, APROBADO, EN_DISEÑO, EN_PRODUCCION, INSTALACION_MONTAJE, ENTREGADO, ANULADO)
     * @return Lista de proyectos con ese estado
     */
    List<Proyecto> findByEstado(String estado);

    /**
     * Busca proyectos por ID de cliente.
     *
     * @param clienteId ID del cliente
     * @return Lista de proyectos del cliente
     */
    List<Proyecto> findByClienteId(Long clienteId);

    /**
     * Busca proyectos cuyo estado no esté en una lista dada.
     *
     * @param estados Lista de estados a excluir
     * @return Lista de proyectos que no están en esos estados
     */
    List<Proyecto> findByEstadoNotIn(List<String> estados);

    /**
     * Busca proyectos asignados a un responsable cuyo estado no esté en una lista dada.
     *
     * @param responsableId ID del responsable (usuario)
     * @param estados       Lista de estados a excluir
     * @return Lista de proyectos del responsable que no están en esos estados
     */
    List<Proyecto> findByResponsableIdAndEstadoNotIn(Long responsableId, List<String> estados);

    /**
     * Verifica si ya existe un proyecto asociado a una cotización.
     *
     * @param cotizacionId ID de la cotización
     * @return true si existe
     */
    boolean existsByCotizacionId(Long cotizacionId);
    boolean existsByClienteId(Long clienteId);
    List<Proyecto> findByEstadoIn(List<String> estados);

    /**
     * Busca proyectos cuyo estado esté en una lista dada y cuya fecha de entrega estimada sea anterior a la fecha especificada.
     * Utilizado para reporte de proyectos atrasados.
     *
     * @param estados Lista de estados permitidos (no finalizados)
     * @param fecha   Fecha límite (hoy)
     * @return Lista de proyectos atrasados
     */
    List<Proyecto> findByEstadoInAndFechaEntregaEstimadaBefore(List<String> estados, LocalDate fecha);

    /**
     * Cuenta proyectos cuyo estado esté en una lista dada y cuya fecha de entrega estimada sea anterior a la fecha especificada.
     * Utilizado para métrica de proyectos atrasados.
     *
     * @param estados Lista de estados permitidos
     * @param fecha   Fecha límite
     * @return Número de proyectos atrasados
     */
    int countByEstadoInAndFechaEntregaEstimadaBefore(List<String> estados, LocalDate fecha);
}