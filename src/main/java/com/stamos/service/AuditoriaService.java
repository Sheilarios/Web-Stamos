package com.stamos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stamos.model.Auditoria;
import com.stamos.model.Usuario;
import com.stamos.repository.AuditoriaRepository;
import com.stamos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuditoriaService {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public Auditoria registrarAccion(String accion, String entidad, Integer idRegistroAfectado,
                                     Map<String, Object> datosAnteriores, Map<String, Object> datosNuevos,
                                     HttpServletRequest request) {
        Auditoria auditoria = new Auditoria();
        auditoria.setIdUsuario(getUsuarioActualId());
        auditoria.setAccion(accion);
        auditoria.setEntidad(entidad);
        auditoria.setIdRegistroAfectado(idRegistroAfectado);
        
        try {
            auditoria.setDatosAnteriores(datosAnteriores != null ? 
                objectMapper.writeValueAsString(datosAnteriores) : "{}");
            auditoria.setDatosNuevos(datosNuevos != null ? 
                objectMapper.writeValueAsString(datosNuevos) : "{}");
        } catch (JsonProcessingException e) {
            auditoria.setDatosAnteriores("{}");
            auditoria.setDatosNuevos("{}");
        }
        
        auditoria.setIpOrigen(getClientIP(request));
        auditoria.setFechaHora(LocalDateTime.now());
        enriquecerAuditoria(auditoria);
        
        return auditoriaRepository.save(auditoria);
    }

    public Page<Auditoria> findAll(Pageable pageable) {
        return auditoriaRepository.findAll(pageable);
    }

    public Page<Auditoria> findByIdUsuario(Integer idUsuario, Pageable pageable) {
        return auditoriaRepository.findByIdUsuario(idUsuario, pageable);
    }

    public Page<Auditoria> findByEntidad(String entidad, Pageable pageable) {
        return auditoriaRepository.findByEntidad(entidad, pageable);
    }

    public Page<Auditoria> findByAccion(String accion, Pageable pageable) {
        return auditoriaRepository.findByAccion(accion, pageable);
    }

    public Page<Auditoria> findByEntidadAndIdRegistroAfectado(String entidad, Integer idRegistroAfectado,
                                                              Pageable pageable) {
        return auditoriaRepository.findByEntidadAndIdRegistroAfectado(entidad, idRegistroAfectado, pageable);
    }

    public Page<Auditoria> findByFechaHoraBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin,
                                                  Pageable pageable) {
        return auditoriaRepository.findByFechaHoraBetween(fechaInicio, fechaFin, pageable);
    }

    public List<Auditoria> findLastAuditorias(int limit) {
        return auditoriaRepository.findLastAuditorias(limit);
    }

    public Page<Auditoria> findCambiosEstado(Pageable pageable) {
        return auditoriaRepository.findCambiosEstado(pageable);
    }

    public Page<Auditoria> findCambiosEstadoByProyecto(Integer idProyecto, Pageable pageable) {
        return auditoriaRepository.findCambiosEstadoByProyecto(idProyecto, pageable);
    }

    public Page<Auditoria> findAccesos(Pageable pageable) {
        return auditoriaRepository.findAccesos(pageable);
    }

    public Page<Auditoria> search(String searchText, Pageable pageable) {
        return auditoriaRepository.search(searchText, pageable);
    }

    public long countAll() {
        return auditoriaRepository.countAll();
    }

    public long countToday() {
        return auditoriaRepository.countToday();
    }

    public long countThisMonth() {
        return auditoriaRepository.countThisMonth();
    }

    public long countThisYear() {
        return auditoriaRepository.countThisYear();
    }

    public List<Object[]> countByEntidadResumen() {
        return auditoriaRepository.countByEntidadResumen();
    }

    public List<Object[]> countByAccionResumen() {
        return auditoriaRepository.countByAccionResumen();
    }

    public List<Object[]> countByLastDays(int days) {
        return auditoriaRepository.countByLastDays(days);
    }

    @Transactional
    public int deleteOlderThan(LocalDateTime fecha) {
        return auditoriaRepository.deleteOlderThan(fecha);
    }

    private Integer getUsuarioActualId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || 
                authentication.getName().equals("anonymousUser")) {
                return null;
            }
            
            String email = authentication.getName();
            if (email == null) {
                return null;
            }
            
            Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
            return usuario != null ? usuario.getIdUsuario() : null;
            
        } catch (Exception e) {
            return null;
        }
    }

    private String getClientIP(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }

    private void enriquecerAuditoria(Auditoria auditoria) {
        if (auditoria.getIdUsuario() != null) {
            usuarioRepository.findById(auditoria.getIdUsuario()).ifPresent(usuario -> {
                auditoria.setNombreUsuario(usuario.getNombreCompleto());
                auditoria.setEmailUsuario(usuario.getEmail());
            });
        }
    }
}