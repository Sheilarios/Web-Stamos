package com.stamos.service;

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

    @Transactional
    public Auditoria registrarAccion(String accion, String entidad, Integer idRegistroAfectado,
                                     Map<String, Object> datosAnteriores, Map<String, Object> datosNuevos,
                                     HttpServletRequest request) {
        Auditoria auditoria = new Auditoria();
        
        Integer idUsuario = getUsuarioActualId();
        auditoria.setIdUsuario(idUsuario);
        
        auditoria.setAccion(accion);
        auditoria.setEntidad(entidad);
        auditoria.setIdRegistroAfectado(idRegistroAfectado);
        
        try {
            if (datosAnteriores != null) {
                auditoria.setDatosAnteriores(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(datosAnteriores));
            } else {
                auditoria.setDatosAnteriores("{}");
            }
        } catch (Exception e) {
            auditoria.setDatosAnteriores("{}");
        }
        
        try {
            if (datosNuevos != null) {
                auditoria.setDatosNuevos(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(datosNuevos));
            } else {
                auditoria.setDatosNuevos("{}");
            }
        } catch (Exception e) {
            auditoria.setDatosNuevos("{}");
        }
        
        auditoria.setIpOrigen(getClientIP(request));
        auditoria.setFechaHora(LocalDateTime.now());
        
        if (idUsuario != null) {
            usuarioRepository.findById(idUsuario).ifPresent(usuario -> {
                auditoria.setNombreUsuario(usuario.getNombreCompleto());
                auditoria.setEmailUsuario(usuario.getEmail());
            });
        }
        
        return auditoriaRepository.save(auditoria);
    }

    @Transactional
    public Auditoria registrarAccion(String accion, String entidad, Integer idRegistroAfectado) {
        return registrarAccion(accion, entidad, idRegistroAfectado, null, null, null);
    }

    @Transactional
    public Auditoria registrarAccion(String accion, String entidad, Integer idRegistroAfectado,
                                     HttpServletRequest request) {
        return registrarAccion(accion, entidad, idRegistroAfectado, null, null, request);
    }

    @Transactional
    public Auditoria registrarAccionConDatos(String accion, String entidad, Integer idRegistroAfectado,
                                             Map<String, Object> datosNuevos, HttpServletRequest request) {
        return registrarAccion(accion, entidad, idRegistroAfectado, null, datosNuevos, request);
    }

    @Transactional
    public Auditoria registrarAccionConCambios(String accion, String entidad, Integer idRegistroAfectado,
                                               Map<String, Object> datosAnteriores, 
                                               Map<String, Object> datosNuevos,
                                               HttpServletRequest request) {
        return registrarAccion(accion, entidad, idRegistroAfectado, datosAnteriores, datosNuevos, request);
    }

    @Transactional
    public Auditoria registrarCreacion(String entidad, Integer idRegistro, 
                                       Map<String, Object> datos, HttpServletRequest request) {
        return registrarAccion("CREATE", entidad, idRegistro, null, datos, request);
    }

    @Transactional
    public Auditoria registrarActualizacion(String entidad, Integer idRegistro,
                                            Map<String, Object> datosAnteriores,
                                            Map<String, Object> datosNuevos,
                                            HttpServletRequest request) {
        return registrarAccion("UPDATE", entidad, idRegistro, datosAnteriores, datosNuevos, request);
    }

    @Transactional
    public Auditoria registrarEliminacion(String entidad, Integer idRegistro,
                                          Map<String, Object> datos, HttpServletRequest request) {
        return registrarAccion("DELETE", entidad, idRegistro, datos, null, request);
    }

    @Transactional
    public Auditoria registrarCambioEstadoProyecto(Integer idProyecto, String estadoAnterior,
                                                   String estadoNuevo, String comentario,
                                                   HttpServletRequest request) {
        Map<String, Object> datosAnteriores = new HashMap<>();
        datosAnteriores.put("estado", estadoAnterior);
        
        Map<String, Object> datosNuevos = new HashMap<>();
        datosNuevos.put("estado", estadoNuevo);
        datosNuevos.put("comentario", comentario);
        datosNuevos.put("fecha_cambio", LocalDateTime.now().toString());
        
        return registrarAccion("STATE_CHANGE", "Proyecto", idProyecto, 
                              datosAnteriores, datosNuevos, request);
    }

    @Transactional
    public Auditoria registrarValidacionPago(Integer idPago, String estadoAnterior,
                                             String estadoNuevo, String observaciones,
                                             HttpServletRequest request) {
        Map<String, Object> datosAnteriores = new HashMap<>();
        datosAnteriores.put("estado", estadoAnterior);
        
        Map<String, Object> datosNuevos = new HashMap<>();
        datosNuevos.put("estado", estadoNuevo);
        datosNuevos.put("observaciones", observaciones);
        datosNuevos.put("fecha_validacion", LocalDateTime.now().toString());
        
        return registrarAccion("VALIDATE", "Pago", idPago, datosAnteriores, datosNuevos, request);
    }

    @Transactional
    public Auditoria registrarDeshabilitacionCliente(Integer idCliente, String motivo,
                                                     HttpServletRequest request) {
        Map<String, Object> datosNuevos = new HashMap<>();
        datosNuevos.put("motivo", motivo);
        datosNuevos.put("fecha_deshabilitacion", LocalDateTime.now().toString());
        
        return registrarAccion("DISABLE", "Cliente", idCliente, null, datosNuevos, request);
    }

    @Transactional
    public Auditoria registrarHabilitacionCliente(Integer idCliente, HttpServletRequest request) {
        Map<String, Object> datosNuevos = new HashMap<>();
        datosNuevos.put("fecha_habilitacion", LocalDateTime.now().toString());
        
        return registrarAccion("ENABLE", "Cliente", idCliente, null, datosNuevos, request);
    }

    @Transactional
    public Auditoria registrarLogin(String email, HttpServletRequest request) {
        Integer idUsuario = null;
        try {
            Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
            if (usuario != null) {
                idUsuario = usuario.getIdUsuario();
            }
        } catch (Exception e) {
        }
        
        Map<String, Object> datosNuevos = new HashMap<>();
        datosNuevos.put("email", email);
        datosNuevos.put("fecha_login", LocalDateTime.now().toString());
        datosNuevos.put("ip", getClientIP(request));
        datosNuevos.put("user_agent", request.getHeader("User-Agent"));
        
        return registrarAccion("LOGIN", "Usuario", idUsuario, null, datosNuevos, request);
    }

    @Transactional
    public Auditoria registrarLogout(String email, HttpServletRequest request) {
        Integer idUsuario = null;
        try {
            Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
            if (usuario != null) {
                idUsuario = usuario.getIdUsuario();
            }
        } catch (Exception e) {
        }
        
        Map<String, Object> datosNuevos = new HashMap<>();
        datosNuevos.put("email", email);
        datosNuevos.put("fecha_logout", LocalDateTime.now().toString());
        
        return registrarAccion("LOGOUT", "Usuario", idUsuario, null, datosNuevos, request);
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

    public Page<Auditoria> findByAccionAndEntidad(String accion, String entidad, Pageable pageable) {
        return auditoriaRepository.findByAccionAndEntidad(accion, entidad, pageable);
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

    public Page<Auditoria> findAccesosByUsuario(Integer idUsuario, Pageable pageable) {
        return auditoriaRepository.findAccesosByUsuario(idUsuario, pageable);
    }

    public Page<Auditoria> search(String searchText, Pageable pageable) {
        return auditoriaRepository.search(searchText, pageable);
    }

    public long countAll() {
        return auditoriaRepository.countAll();
    }

    public long countByEntidad(String entidad) {
        return auditoriaRepository.countByEntidad(entidad);
    }

    public long countByAccion(String accion) {
        return auditoriaRepository.countByAccion(accion);
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

    @Transactional
    public int deleteOldRecords() {
        return deleteOlderThan(LocalDateTime.now().minusMonths(6));
    }

    @Transactional
    public int deleteOlderThanMonths(int months) {
        return deleteOlderThan(LocalDateTime.now().minusMonths(months));
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

    private String getUsuarioActualEmail() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || 
                authentication.getName().equals("anonymousUser")) {
                return null;
            }
            return authentication.getName();
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
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }

    private String getUserAgent(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return request.getHeader("User-Agent");
    }

    public Map<String, Object> crearMapaDatos(Object objeto) {
        if (objeto == null) {
            return new HashMap<>();
        }
        
        Map<String, Object> datos = new HashMap<>();
        try {
            java.lang.reflect.Field[] fields = objeto.getClass().getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(objeto);
                    datos.put(field.getName(), value);
                } catch (IllegalAccessException e) {
                }
            }
        } catch (Exception e) {
        }
        return datos;
    }

    public String getNombreUsuarioActual() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || 
                authentication.getName().equals("anonymousUser")) {
                return "Sistema";
            }
            
            String email = authentication.getName();
            if (email == null) {
                return "Sistema";
            }
            
            Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
            return usuario != null ? usuario.getNombreCompleto() : "Sistema";
            
        } catch (Exception e) {
            return "Sistema";
        }
    }
}