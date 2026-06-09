package com.stamos.repository;

import com.stamos.entity.CatalogoEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface CatalogoEquipoRepository extends JpaRepository<CatalogoEquipo, Long> {

    List<CatalogoEquipo> findByNombreContainingIgnoreCase(String nombre);

    List<CatalogoEquipo> findByCantidadTotalGreaterThanEqual(int cantidad);

    boolean existsByNombreIgnoreCase(String nombre);
}