package com.stamos.repository;

import com.stamos.entity.LogAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface LogAuditoriaRepository extends JpaRepository<LogAuditoria, Long> {
    List<LogAuditoria> findAllByOrderByFechaHoraDesc();
    List<LogAuditoria> findByEntidadAndEntidadIdOrderByFechaHoraDesc(String entidad, Long entidadId);
    List<LogAuditoria> findByUsuarioEmailOrderByFechaHoraDesc(String usuarioEmail);
    List<LogAuditoria> findByFechaHoraBetweenOrderByFechaHoraDesc(LocalDateTime start, LocalDateTime end);
}