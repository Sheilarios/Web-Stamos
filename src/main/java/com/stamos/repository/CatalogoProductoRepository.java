package com.stamos.repository;

import com.stamos.entity.CatalogoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CatalogoProductoRepository extends JpaRepository<CatalogoProducto, Long> {

    List<CatalogoProducto> findByNombreContainingIgnoreCase(String nombre);
}