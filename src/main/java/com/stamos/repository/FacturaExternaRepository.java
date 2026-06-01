package com.stamos.repository;

import com.stamos.entity.FacturaExterna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacturaExternaRepository extends JpaRepository<FacturaExterna, Long> {
}