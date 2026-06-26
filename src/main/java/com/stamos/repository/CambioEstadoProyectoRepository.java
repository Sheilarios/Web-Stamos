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


@Repository
public interface CambioEstadoProyectoRepository extends JpaRepository<CambioEstadoProyecto, Integer> {

    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idProyecto = :idProyecto ORDER BY c.fechaCambio DESC")
    List<CambioEstadoProyecto> findByIdProyecto(@Param("idProyecto") Integer idProyecto);

    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idProyecto = :idProyecto ORDER BY c.fechaCambio DESC")
    Page<CambioEstadoProyecto> findByIdProyecto(@Param("idProyecto") Integer idProyecto, Pageable pageable);

    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idUsuarioResponsable = :idUsuario ORDER BY c.fechaCambio DESC")
    Page<CambioEstadoProyecto> findByIdUsuarioResponsable(@Param("idUsuario") Integer idUsuarioResponsable, 
                                                          Pageable pageable);

    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idEstadoAnterior = :idAnterior AND c.idEstadoNuevo = :idNuevo ORDER BY c.fechaCambio DESC")
    Page<CambioEstadoProyecto> findByEstados(@Param("idAnterior") Integer idEstadoAnterior,
                                             @Param("idNuevo") Integer idEstadoNuevo,
                                             Pageable pageable);

    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idEstadoNuevo = :idEstado ORDER BY c.fechaCambio DESC")
    Page<CambioEstadoProyecto> findByIdEstadoNuevo(@Param("idEstado") Integer idEstadoNuevo, Pageable pageable);

    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.fechaCambio BETWEEN :fechaInicio AND :fechaFin ORDER BY c.fechaCambio DESC")
    Page<CambioEstadoProyecto> findByFechaCambioBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                        @Param("fechaFin") LocalDateTime fechaFin,
                                                        Pageable pageable);


    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idEstadoNuevo > c.idEstadoAnterior ORDER BY c.fechaCambio DESC")
    Page<CambioEstadoProyecto> findAvances(Pageable pageable);


    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idEstadoNuevo < c.idEstadoAnterior ORDER BY c.fechaCambio DESC")
    Page<CambioEstadoProyecto> findRetrocesos(Pageable pageable);


    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idProyecto = :idProyecto AND c.idEstadoNuevo > c.idEstadoAnterior ORDER BY c.fechaCambio DESC")
    List<CambioEstadoProyecto> findAvancesByProyecto(@Param("idProyecto") Integer idProyecto);

    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idProyecto = :idProyecto AND c.idEstadoNuevo < c.idEstadoAnterior ORDER BY c.fechaCambio DESC")
    List<CambioEstadoProyecto> findRetrocesosByProyecto(@Param("idProyecto") Integer idProyecto);

    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idProyecto = :idProyecto ORDER BY c.fechaCambio DESC")
    CambioEstadoProyecto findLastByProyecto(@Param("idProyecto") Integer idProyecto);

    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.comentario IS NOT NULL AND c.comentario != '' ORDER BY c.fechaCambio DESC")
    Page<CambioEstadoProyecto> findWithComentario(Pageable pageable);


    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idProyecto = :idProyecto AND c.comentario IS NOT NULL AND c.comentario != '' ORDER BY c.fechaCambio DESC")
    List<CambioEstadoProyecto> findWithComentarioByProyecto(@Param("idProyecto") Integer idProyecto);

    @Query("SELECT COUNT(c) FROM CambioEstadoProyecto c WHERE c.idProyecto = :idProyecto")
    long countByProyecto(@Param("idProyecto") Integer idProyecto);

    @Query("SELECT COUNT(c) FROM CambioEstadoProyecto c WHERE c.idEstadoNuevo = :idEstado")
    long countByEstadoNuevo(@Param("idEstado") Integer idEstadoNuevo);

    @Query("SELECT COUNT(c) FROM CambioEstadoProyecto c WHERE c.idUsuarioResponsable = :idUsuario")
    long countByUsuarioResponsable(@Param("idUsuario") Integer idUsuarioResponsable);

    @Query("SELECT AVG(TIMESTAMPDIFF(HOUR, c.fechaCambio, " +
           "(SELECT c2.fechaCambio FROM CambioEstadoProyecto c2 WHERE c2.idProyecto = c.idProyecto AND c2.fechaCambio > c.fechaCambio ORDER BY c2.fechaCambio LIMIT 1))) " +
           "FROM CambioEstadoProyecto c WHERE c.idProyecto = :idProyecto")
    Double getTiempoPromedioEntreCambios(@Param("idProyecto") Integer idProyecto);

    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.fechaCambio IN " +
           "(SELECT MAX(c2.fechaCambio) FROM CambioEstadoProyecto c2 WHERE c2.idProyecto = c.idProyecto) " +
           "ORDER BY c.fechaCambio DESC")
    List<CambioEstadoProyecto> findLastChangeForEachProject();

    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idProyecto = :idProyecto ORDER BY c.fechaCambio ASC")
    List<CambioEstadoProyecto> findByIdProyectoAsc(@Param("idProyecto") Integer idProyecto);

    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idProyecto = :idProyecto AND " +
           "(:esAvance = true AND c.idEstadoNuevo > c.idEstadoAnterior OR " +
           ":esAvance = false AND c.idEstadoNuevo < c.idEstadoAnterior) " +
           "ORDER BY c.fechaCambio DESC")
    List<CambioEstadoProyecto> findByIdProyectoAndTipo(@Param("idProyecto") Integer idProyecto,
                                                       @Param("esAvance") boolean esAvance);

    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idProyecto = :idProyecto AND " +
           "c.idEstadoNuevo = 6 AND c.idEstadoAnterior != 6")
    List<CambioEstadoProyecto> findAnulacionesByProyecto(@Param("idProyecto") Integer idProyecto);

    @Query("SELECT c FROM CambioEstadoProyecto c WHERE c.idProyecto = :idProyecto AND " +
           "c.idEstadoNuevo = 5 AND c.idEstadoAnterior != 5")
    List<CambioEstadoProyecto> findEntregasByProyecto(@Param("idProyecto") Integer idProyecto);

    @Query("DELETE FROM CambioEstadoProyecto c WHERE c.idProyecto = :idProyecto")
    void deleteByIdProyecto(@Param("idProyecto") Integer idProyecto);
}