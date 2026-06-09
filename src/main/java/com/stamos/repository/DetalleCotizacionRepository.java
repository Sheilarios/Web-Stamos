package com.stamos.repository;

import com.stamos.entity.DetalleCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface DetalleCotizacionRepository extends JpaRepository<DetalleCotizacion, Long> {
    List<DetalleCotizacion> findByCotizacionId(Long cotizacionId);
}