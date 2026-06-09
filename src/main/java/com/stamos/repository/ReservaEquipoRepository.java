package com.stamos.repository;

import com.stamos.entity.ReservaEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaEquipoRepository extends JpaRepository<ReservaEquipo, Long> {

    List<ReservaEquipo> findByEquipoId(Long equipoId);

    boolean existsByEquipoId(Long equipoId);

    @Query("SELECT r FROM ReservaEquipo r WHERE r.equipo.id = :equipoId " +
           "AND r.fechaInicio <= :fechaFin AND r.fechaFin >= :fechaInicio")
    List<ReservaEquipo> findByEquipoIdAndPeriodoOverlap(@Param("equipoId") Long equipoId,
                                                        @Param("fechaInicio") LocalDate fechaInicio,
                                                        @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT r.equipo.id, r.equipo.nombre, SUM(r.cantidad) " +
           "FROM ReservaEquipo r " +
           "GROUP BY r.equipo.id, r.equipo.nombre " +
           "ORDER BY SUM(r.cantidad) DESC")
    List<Object[]> findEquiposMasAlquilados();
}