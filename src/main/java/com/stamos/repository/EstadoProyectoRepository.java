package com.stamos.repository;

import com.stamos.model.EstadoProyecto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstadoProyectoRepository extends JpaRepository<EstadoProyecto, Integer> {

    Optional<EstadoProyecto> findByNombreEstado(String nombreEstado);

    @Query("SELECT e FROM EstadoProyecto e WHERE LOWER(e.nombreEstado) = LOWER(:nombreEstado)")
    Optional<EstadoProyecto> findByNombreEstadoIgnoreCase(@Param("nombreEstado") String nombreEstado);

    boolean existsByNombreEstado(String nombreEstado);

    Optional<EstadoProyecto> findByOrdenFlujo(Integer ordenFlujo);

    boolean existsByOrdenFlujo(Integer ordenFlujo);

    @Query("SELECT e FROM EstadoProyecto e ORDER BY e.ordenFlujo ASC")
    List<EstadoProyecto> findAllOrderByOrdenFlujo();

    @Query("SELECT e FROM EstadoProyecto e ORDER BY e.ordenFlujo ASC")
    Page<EstadoProyecto> findAllOrderByOrdenFlujo(Pageable pageable);

    @Query("SELECT e FROM EstadoProyecto e ORDER BY e.ordenFlujo DESC")
    List<EstadoProyecto> findAllOrderByOrdenFlujoDesc();

    @Query("SELECT e FROM EstadoProyecto e WHERE e.ordenFlujo = 1")
    Optional<EstadoProyecto> findEstadoInicial();

    @Query("SELECT e FROM EstadoProyecto e WHERE e.ordenFlujo = 5")
    Optional<EstadoProyecto> findEstadoFinal();

    @Query("SELECT e FROM EstadoProyecto e WHERE e.nombreEstado = 'Anulado'")
    Optional<EstadoProyecto> findEstadoAnulado();

    @Query("SELECT e FROM EstadoProyecto e WHERE e.nombreEstado = 'Entregado'")
    Optional<EstadoProyecto> findEstadoEntregado();

    @Query("SELECT e FROM EstadoProyecto e WHERE e.nombreEstado = 'Aprobado'")
    Optional<EstadoProyecto> findEstadoAprobado();

    @Query("SELECT e FROM EstadoProyecto e WHERE e.nombreEstado NOT IN ('Entregado', 'Anulado') ORDER BY e.ordenFlujo ASC")
    List<EstadoProyecto> findEstadosActivos();

    @Query("SELECT e FROM EstadoProyecto e WHERE e.nombreEstado NOT IN ('Entregado', 'Anulado') ORDER BY e.ordenFlujo ASC")
    Page<EstadoProyecto> findEstadosActivos(Pageable pageable);

    @Query("SELECT e FROM EstadoProyecto e WHERE e.nombreEstado IN ('Entregado', 'Anulado') ORDER BY e.ordenFlujo ASC")
    List<EstadoProyecto> findEstadosTerminales();

    @Query("SELECT e FROM EstadoProyecto e WHERE e.nombreEstado IN ('Entregado', 'Anulado') ORDER BY e.ordenFlujo ASC")
    Page<EstadoProyecto> findEstadosTerminales(Pageable pageable);

    @Query("SELECT e FROM EstadoProyecto e WHERE e.ordenFlujo = :ordenFlujoActual + 1")
    Optional<EstadoProyecto> findSiguienteEstado(@Param("ordenFlujoActual") Integer ordenFlujoActual);

    @Query("SELECT e FROM EstadoProyecto e WHERE e.ordenFlujo = :ordenFlujoActual - 1")
    Optional<EstadoProyecto> findEstadoAnterior(@Param("ordenFlujoActual") Integer ordenFlujoActual);

    @Query("SELECT e FROM EstadoProyecto e WHERE e.ordenFlujo > (SELECT e2.ordenFlujo FROM EstadoProyecto e2 WHERE e2.idEstado = :idEstadoActual) " +
           "AND e.nombreEstado != 'Anulado' " +
           "OR e.nombreEstado = 'Anulado' " +
           "ORDER BY e.ordenFlujo ASC")
    List<EstadoProyecto> findEstadosDisponibles(@Param("idEstadoActual") Integer idEstadoActual);

    @Query("SELECT e FROM EstadoProyecto e WHERE e.ordenFlujo > (SELECT e2.ordenFlujo FROM EstadoProyecto e2 WHERE e2.idEstado = :idEstadoActual) " +
           "AND e.idEstado != :idEstadoActual " +
           "ORDER BY e.ordenFlujo ASC")
    List<EstadoProyecto> findEstadosPosibles(@Param("idEstadoActual") Integer idEstadoActual);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM EstadoProyecto e " +
           "WHERE e.idEstado = :idEstadoSiguiente AND e.ordenFlujo > (SELECT e2.ordenFlujo FROM EstadoProyecto e2 WHERE e2.idEstado = :idEstadoActual)")
    boolean canTransition(@Param("idEstadoActual") Integer idEstadoActual,
                          @Param("idEstadoSiguiente") Integer idEstadoSiguiente);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM EstadoProyecto e " +
           "WHERE e.idEstado = :idEstadoAnterior AND e.ordenFlujo < (SELECT e2.ordenFlujo FROM EstadoProyecto e2 WHERE e2.idEstado = :idEstadoActual) " +
           "AND (SELECT e2.nombreEstado FROM EstadoProyecto e2 WHERE e2.idEstado = :idEstadoActual) NOT IN ('Entregado', 'Anulado')")
    boolean canRollback(@Param("idEstadoActual") Integer idEstadoActual,
                        @Param("idEstadoAnterior") Integer idEstadoAnterior);

    @Query("SELECT e FROM EstadoProyecto e WHERE LOWER(e.descripcion) LIKE LOWER(CONCAT('%', :descripcion, '%')) ORDER BY e.ordenFlujo ASC")
    List<EstadoProyecto> findByDescripcionContaining(@Param("descripcion") String descripcion);

    @Query("SELECT e FROM EstadoProyecto e WHERE LOWER(e.descripcion) LIKE LOWER(CONCAT('%', :descripcion, '%')) ORDER BY e.ordenFlujo ASC")
    Page<EstadoProyecto> findByDescripcionContaining(@Param("descripcion") String descripcion, Pageable pageable);

    Optional<EstadoProyecto> findByNombreEstadoAndOrdenFlujo(String nombreEstado, Integer ordenFlujo);

    @Query("SELECT e FROM EstadoProyecto e WHERE e.nombreEstado NOT IN ('Entregado', 'Anulado') ORDER BY e.ordenFlujo ASC")
    List<EstadoProyecto> findNoTerminales();

    @Query("SELECT e FROM EstadoProyecto e WHERE e.nombreEstado NOT IN ('Entregado', 'Anulado') ORDER BY e.ordenFlujo ASC")
    Page<EstadoProyecto> findNoTerminales(Pageable pageable);

    @Query("SELECT e FROM EstadoProyecto e WHERE " +
           "LOWER(e.nombreEstado) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(e.descripcion) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "ORDER BY e.ordenFlujo ASC")
    List<EstadoProyecto> search(@Param("searchText") String searchText);

    @Query("SELECT e FROM EstadoProyecto e WHERE " +
           "LOWER(e.nombreEstado) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(e.descripcion) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "ORDER BY e.ordenFlujo ASC")
    Page<EstadoProyecto> search(@Param("searchText") String searchText, Pageable pageable);

    @Query("SELECT COUNT(e) FROM EstadoProyecto e WHERE e.nombreEstado NOT IN ('Entregado', 'Anulado')")
    long countEstadosActivos();

    @Query("SELECT COUNT(e) FROM EstadoProyecto e WHERE e.nombreEstado IN ('Entregado', 'Anulado')")
    long countEstadosTerminales();

    @Query("SELECT COUNT(p) FROM Proyecto p WHERE p.idEstadoActual = :idEstado")
    long countProyectosByEstado(@Param("idEstado") Integer idEstado);

    @Query("SELECT e.nombreEstado, COUNT(p) as total " +
           "FROM EstadoProyecto e LEFT JOIN Proyecto p ON e.idEstado = p.idEstadoActual " +
           "GROUP BY e.idEstado ORDER BY e.ordenFlujo ASC")
    List<Object[]> countProyectosByEstadoResumen();

    @Query("SELECT e.idEstado, e.nombreEstado, e.ordenFlujo, " +
           "COUNT(p) as total, " +
           "COALESCE(SUM(p.montoTotal), 0) as montoTotal " +
           "FROM EstadoProyecto e LEFT JOIN Proyecto p ON e.idEstado = p.idEstadoActual " +
           "GROUP BY e.idEstado ORDER BY e.ordenFlujo ASC")
    List<Object[]> findEstadosWithProjectStats();

    @Query("SELECT AVG(DATEDIFF(c.fechaCambio, " +
           "(SELECT c2.fechaCambio FROM CambioEstadoProyecto c2 " +
           "WHERE c2.idProyecto = c.idProyecto AND c2.idEstadoNuevo = c.idEstadoAnterior " +
           "ORDER BY c2.fechaCambio LIMIT 1))) " +
           "FROM CambioEstadoProyecto c WHERE c.idEstadoNuevo = :idEstado")
    Double avgDiasEnEstado(@Param("idEstado") Integer idEstado);

    @Modifying
    @Transactional
    @Query("UPDATE EstadoProyecto e SET e.descripcion = :descripcion, e.fechaActualizacion = CURRENT_TIMESTAMP WHERE e.idEstado = :idEstado")
    void updateDescripcion(@Param("idEstado") Integer idEstado,
                          @Param("descripcion") String nuevaDescripcion);

    @Modifying
    @Transactional
    @Query("UPDATE EstadoProyecto e SET e.ordenFlujo = :orden, e.fechaActualizacion = CURRENT_TIMESTAMP WHERE e.idEstado = :idEstado")
    void updateOrdenFlujo(@Param("idEstado") Integer idEstado,
                         @Param("orden") Integer nuevoOrden);

    @Modifying
    @Transactional
    @Query("UPDATE EstadoProyecto e SET e.nombreEstado = :nombre, e.fechaActualizacion = CURRENT_TIMESTAMP WHERE e.idEstado = :idEstado")
    void updateNombreEstado(@Param("idEstado") Integer idEstado,
                           @Param("nombre") String nuevoNombre);

    @Modifying
    @Transactional
    @Query("DELETE FROM EstadoProyecto e WHERE e.nombreEstado = :nombre")
    void deleteByNombreEstado(@Param("nombre") String nombreEstado);

    @Modifying
    @Transactional
    @Query("DELETE FROM EstadoProyecto e WHERE e.idEstado NOT IN (SELECT DISTINCT p.idEstadoActual FROM Proyecto p)")
    int deleteUnused();

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Proyecto p WHERE p.idEstadoActual = :idEstado")
    boolean hasProyectos(@Param("idEstado") Integer idEstado);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM EstadoProyecto e " +
           "WHERE e.idEstado = :idEstado AND e.nombreEstado IN ('Entregado', 'Anulado')")
    boolean isTerminal(@Param("idEstado") Integer idEstado);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM EstadoProyecto e " +
           "WHERE e.idEstado = :idEstado AND e.nombreEstado NOT IN ('Entregado', 'Anulado')")
    boolean isActivo(@Param("idEstado") Integer idEstado);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM EstadoProyecto e " +
           "WHERE e.idEstado = :idEstado AND e.ordenFlujo = 1")
    boolean isInicial(@Param("idEstado") Integer idEstado);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM EstadoProyecto e " +
           "WHERE e.idEstado = :idEstado AND e.ordenFlujo = 5")
    boolean isFinal(@Param("idEstado") Integer idEstado);

    @Query("SELECT e.idEstado, e.nombreEstado, e.ordenFlujo FROM EstadoProyecto e ORDER BY e.ordenFlujo ASC")
    List<Object[]> findForSelect();

    @Query("SELECT e FROM EstadoProyecto e " +
           "WHERE e.idEstado IN (" +
           "  SELECT e2.idEstado FROM EstadoProyecto e2 " +
           "  WHERE e2.ordenFlujo > (SELECT e3.ordenFlujo FROM EstadoProyecto e3 WHERE e3.idEstado = :idEstadoActual) " +
           "  AND e2.idEstado != :idEstadoActual " +
           "  UNION " +
           "  SELECT e4.idEstado FROM EstadoProyecto e4 WHERE e4.nombreEstado = 'Anulado' " +
           ") " +
           "AND e.idEstado NOT IN (SELECT e5.idEstado FROM EstadoProyecto e5 WHERE e5.nombreEstado = 'Anulado' AND :rolNombre != 'Administrador') " +
           "ORDER BY e.ordenFlujo ASC")
    List<EstadoProyecto> findEstadosDisponiblesByRol(@Param("idEstadoActual") Integer idEstadoActual,
                                                     @Param("rolNombre") String rolNombre);
}