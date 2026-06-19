package com.stamos.repository;

import com.stamos.model.CambioEstadoProyecto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad CambioEstadoProyecto.
 * 
 * <p>Proporciona métodos para acceder y gestionar el historial de cambios
 * de estado de los proyectos en el sistema.</p>
 * 
 * <p>Implementa los requerimientos:</p>
 * <ul>
 *   <li>RN-07: Los proyectos deben seguir la secuencia de estados</li>
 *   <li>RN-08: Proyectos en "Entregado" o "Anulado" no pueden retroceder</li>
 *   <li>RN-09: Registrar fecha y responsable de cada cambio</li>
 *   <li>RF-15: Registrar fecha, responsable y cambio de estado</li>
 * </ul>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
@Repository
public interface CambioEstadoProyectoRepository extends JpaRepository<CambioEstadoProyecto, Integer> {

    /**
     * Busca cambios de estado por ID de proyecto.
     * 
     * @param idProyecto ID del proyecto
     * @return Lista de cambios de estado ordenados por fecha descendente
     */
    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idProyecto = :idProyecto ORDER BY c.fechaCambio DESC")
    List<CambioEstadoProyecto> findByIdProyecto(@Param("idProyecto") Integer idProyecto);

    /**
     * Busca cambios de estado por ID de proyecto con paginación.
     * 
     * @param idProyecto ID del proyecto
     * @param pageable Configuración de paginación
     * @return Página de cambios de estado
     */
    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idProyecto = :idProyecto ORDER BY c.fechaCambio DESC")
    Page<CambioEstadoProyecto> findByIdProyecto(@Param("idProyecto") Integer idProyecto, Pageable pageable);

    /**
     * Busca cambios de estado por ID de usuario responsable.
     * 
     * @param idUsuarioResponsable ID del usuario responsable
     * @param pageable Configuración de paginación
     * @return Página de cambios de estado
     */
    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idUsuarioResponsable = :idUsuario ORDER BY c.fechaCambio DESC")
    Page<CambioEstadoProyecto> findByIdUsuarioResponsable(@Param("idUsuario") Integer idUsuarioResponsable, 
                                                          Pageable pageable);

    /**
     * Busca cambios de estado por estado anterior y nuevo.
     * 
     * @param idEstadoAnterior ID del estado anterior
     * @param idEstadoNuevo ID del estado nuevo
     * @param pageable Configuración de paginación
     * @return Página de cambios de estado
     */
    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idEstadoAnterior = :idAnterior AND c.idEstadoNuevo = :idNuevo ORDER BY c.fechaCambio DESC")
    Page<CambioEstadoProyecto> findByEstados(@Param("idAnterior") Integer idEstadoAnterior,
                                             @Param("idNuevo") Integer idEstadoNuevo,
                                             Pageable pageable);

    /**
     * Busca cambios de estado por estado nuevo.
     * 
     * @param idEstadoNuevo ID del estado nuevo
     * @param pageable Configuración de paginación
     * @return Página de cambios de estado
     */
    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idEstadoNuevo = :idEstado ORDER BY c.fechaCambio DESC")
    Page<CambioEstadoProyecto> findByIdEstadoNuevo(@Param("idEstado") Integer idEstadoNuevo, Pageable pageable);

    /**
     * Busca cambios de estado por rango de fechas.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param pageable Configuración de paginación
     * @return Página de cambios de estado
     */
    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.fechaCambio BETWEEN :fechaInicio AND :fechaFin ORDER BY c.fechaCambio DESC")
    Page<CambioEstadoProyecto> findByFechaCambioBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                        @Param("fechaFin") LocalDateTime fechaFin,
                                                        Pageable pageable);

    /**
     * Busca cambios de estado que sean avances en el flujo.
     * 
     * @param pageable Configuración de paginación
     * @return Página de cambios de estado
     */
    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idEstadoNuevo > c.idEstadoAnterior ORDER BY c.fechaCambio DESC")
    Page<CambioEstadoProyecto> findAvances(Pageable pageable);

    /**
     * Busca cambios de estado que sean retrocesos en el flujo.
     * 
     * @param pageable Configuración de paginación
     * @return Página de cambios de estado
     */
    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idEstadoNuevo < c.idEstadoAnterior ORDER BY c.fechaCambio DESC")
    Page<CambioEstadoProyecto> findRetrocesos(Pageable pageable);

    /**
     * Busca cambios de estado de un proyecto que sean avances.
     * 
     * @param idProyecto ID del proyecto
     * @return Lista de cambios de estado
     */
    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idProyecto = :idProyecto AND c.idEstadoNuevo > c.idEstadoAnterior ORDER BY c.fechaCambio DESC")
    List<CambioEstadoProyecto> findAvancesByProyecto(@Param("idProyecto") Integer idProyecto);

    /**
     * Busca cambios de estado de un proyecto que sean retrocesos.
     * 
     * @param idProyecto ID del proyecto
     * @return Lista de cambios de estado
     */
    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idProyecto = :idProyecto AND c.idEstadoNuevo < c.idEstadoAnterior ORDER BY c.fechaCambio DESC")
    List<CambioEstadoProyecto> findRetrocesosByProyecto(@Param("idProyecto") Integer idProyecto);

    /**
     * Busca el último cambio de estado de un proyecto.
     * 
     * @param idProyecto ID del proyecto
     * @return Último cambio de estado
     */
    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idProyecto = :idProyecto ORDER BY c.fechaCambio DESC")
    CambioEstadoProyecto findLastByProyecto(@Param("idProyecto") Integer idProyecto);

    /**
     * Busca cambios de estado con comentario.
     * 
     * @param pageable Configuración de paginación
     * @return Página de cambios de estado
     */
    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.comentario IS NOT NULL AND c.comentario != '' ORDER BY c.fechaCambio DESC")
    Page<CambioEstadoProyecto> findWithComentario(Pageable pageable);

    /**
     * Busca cambios de estado de un proyecto con comentario.
     * 
     * @param idProyecto ID del proyecto
     * @return Lista de cambios de estado
     */
    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idProyecto = :idProyecto AND c.comentario IS NOT NULL AND c.comentario != '' ORDER BY c.fechaCambio DESC")
    List<CambioEstadoProyecto> findWithComentarioByProyecto(@Param("idProyecto") Integer idProyecto);

    /**
     * Cuenta cambios de estado por proyecto.
     * 
     * @param idProyecto ID del proyecto
     * @return Cantidad de cambios
     */
    @Query("SELECT COUNT(c) FROM CambioEstadoProyecto c WHERE c.idProyecto = :idProyecto")
    long countByProyecto(@Param("idProyecto") Integer idProyecto);

    /**
     * Cuenta cambios de estado por estado nuevo.
     * 
     * @param idEstadoNuevo ID del estado nuevo
     * @return Cantidad de cambios
     */
    @Query("SELECT COUNT(c) FROM CambioEstadoProyecto c WHERE c.idEstadoNuevo = :idEstado")
    long countByEstadoNuevo(@Param("idEstado") Integer idEstadoNuevo);

    /**
     * Cuenta cambios de estado por usuario responsable.
     * 
     * @param idUsuarioResponsable ID del usuario responsable
     * @return Cantidad de cambios
     */
    @Query("SELECT COUNT(c) FROM CambioEstadoProyecto c WHERE c.idUsuarioResponsable = :idUsuario")
    long countByUsuarioResponsable(@Param("idUsuario") Integer idUsuarioResponsable);

    /**
     * Obtiene el tiempo promedio entre cambios de estado de un proyecto.
     * 
     * @param idProyecto ID del proyecto
     * @return Tiempo promedio en horas
     */
    @Query("SELECT AVG(TIMESTAMPDIFF(HOUR, c.fechaCambio, " +
           "(SELECT c2.fechaCambio FROM CambioEstadoProyecto c2 WHERE c2.idProyecto = c.idProyecto AND c2.fechaCambio > c.fechaCambio ORDER BY c2.fechaCambio LIMIT 1))) " +
           "FROM CambioEstadoProyecto c WHERE c.idProyecto = :idProyecto")
    Double getTiempoPromedioEntreCambios(@Param("idProyecto") Integer idProyecto);

    /**
     * Obtiene el último cambio de estado para cada proyecto.
     * 
     * @return Lista de cambios de estado (último por proyecto)
     */
    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.fechaCambio IN " +
           "(SELECT MAX(c2.fechaCambio) FROM CambioEstadoProyecto c2 WHERE c2.idProyecto = c.idProyecto) " +
           "ORDER BY c.fechaCambio DESC")
    List<CambioEstadoProyecto> findLastChangeForEachProject();

    /**
     * Busca cambios de estado de un proyecto ordenados de forma ascendente.
     * 
     * @param idProyecto ID del proyecto
     * @return Lista de cambios de estado ordenados ascendente
     */
    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idProyecto = :idProyecto ORDER BY c.fechaCambio ASC")
    List<CambioEstadoProyecto> findByIdProyectoAsc(@Param("idProyecto") Integer idProyecto);

    /**
     * Busca cambios de estado de un proyecto por tipo de cambio.
     * 
     * @param idProyecto ID del proyecto
     * @param esAvance true para avances, false para retrocesos
     * @return Lista de cambios de estado
     */
    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idProyecto = :idProyecto AND " +
           "(:esAvance = true AND c.idEstadoNuevo > c.idEstadoAnterior OR " +
           ":esAvance = false AND c.idEstadoNuevo < c.idEstadoAnterior) " +
           "ORDER BY c.fechaCambio DESC")
    List<CambioEstadoProyecto> findByIdProyectoAndTipo(@Param("idProyecto") Integer idProyecto,
                                                       @Param("esAvance") boolean esAvance);

    /**
     * Busca cambios de estado de un proyecto que sean anulaciones.
     * 
     * @param idProyecto ID del proyecto
     * @return Lista de cambios de estado
     */
    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idProyecto = :idProyecto AND " +
           "c.idEstadoNuevo = 6 AND c.idEstadoAnterior != 6")
    List<CambioEstadoProyecto> findAnulacionesByProyecto(@Param("idProyecto") Integer idProyecto);

    /**
     * Busca cambios de estado de un proyecto que sean entregas.
     * 
     * @param idProyecto ID del proyecto
     * @return Lista de cambios de estado
     */
    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idProyecto = :idProyecto AND " +
           "c.idEstadoNuevo = 5 AND c.idEstadoAnterior != 5")
    List<CambioEstadoProyecto> findEntregasByProyecto(@Param("idProyecto") Integer idProyecto);

    /**
     * Elimina todos los cambios de estado de un proyecto.
     * 
     * @param idProyecto ID del proyecto
     */
    @Query("DELETE FROM CambioEstadoProyecto c WHERE c.idProyecto = :idProyecto")
    void deleteByIdProyecto(@Param("idProyecto") Integer idProyecto);
}