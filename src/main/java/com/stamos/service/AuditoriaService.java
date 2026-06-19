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

/**
 * Servicio para la gestión de auditoría del sistema.
 * 
 * <p>Este servicio proporciona métodos para registrar y consultar acciones
 * críticas realizadas en el sistema, asegurando la trazabilidad de cambios
 * y operaciones.</p>
 * 
 * <p>Implementa los requerimientos:</p>
 * <ul>
 *   <li>RN-09: Registrar fecha y responsable de cambios</li>
 *   <li>RF-15: Registrar fecha, responsable y cambio de estado</li>
 *   <li>RNF-04: Control de acceso basado en roles</li>
 * </ul>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
@Service
public class AuditoriaService {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ============================================
    // REGISTRO DE ACCIONES
    // ============================================

    /**
     * Registra una acción en el sistema de auditoría.
     * 
     * @param accion Acción realizada (CREATE, UPDATE, DELETE, etc.)
     * @param entidad Entidad afectada (Cliente, Proyecto, etc.)
     * @param idRegistroAfectado ID del registro afectado
     * @param datosAnteriores Datos antes del cambio
     * @param datosNuevos Datos después del cambio
     * @param request Petición HTTP para obtener IP
     * @return Registro de auditoría creado
     */
    @Transactional
    public Auditoria registrarAccion(String accion, String entidad, Integer idRegistroAfectado,
                                     Map<String, Object> datosAnteriores, Map<String, Object> datosNuevos,
                                     HttpServletRequest request) {
        Auditoria auditoria = new Auditoria();
        
        // Obtener usuario actual
        Integer idUsuario = getUsuarioActualId();
        auditoria.setIdUsuario(idUsuario);
        
        // Establecer datos de la auditoría
        auditoria.setAccion(accion);
        auditoria.setEntidad(entidad);
        auditoria.setIdRegistroAfectado(idRegistroAfectado);
        auditoria.setDatosAnteriores(datosAnteriores != null ? datosAnteriores : new HashMap<>());
        auditoria.setDatosNuevos(datosNuevos != null ? datosNuevos : new HashMap<>());
        auditoria.setIpOrigen(getClientIP(request));
        auditoria.setFechaHora(LocalDateTime.now());
        
        // Enriquecer con información del usuario
        if (idUsuario != null) {
            usuarioRepository.findById(idUsuario).ifPresent(usuario -> {
                auditoria.setNombreUsuario(usuario.getNombreCompleto());
                auditoria.setEmailUsuario(usuario.getEmail());
            });
        }
        
        return auditoriaRepository.save(auditoria);
    }

    /**
     * Registra una acción en el sistema de auditoría (versión simplificada).
     * 
     * @param accion Acción realizada
     * @param entidad Entidad afectada
     * @param idRegistroAfectado ID del registro afectado
     * @return Registro de auditoría creado
     */
    @Transactional
    public Auditoria registrarAccion(String accion, String entidad, Integer idRegistroAfectado) {
        return registrarAccion(accion, entidad, idRegistroAfectado, null, null, null);
    }

    /**
     * Registra una acción en el sistema de auditoría (versión simplificada con IP).
     * 
     * @param accion Acción realizada
     * @param entidad Entidad afectada
     * @param idRegistroAfectado ID del registro afectado
     * @param request Petición HTTP para obtener IP
     * @return Registro de auditoría creado
     */
    @Transactional
    public Auditoria registrarAccion(String accion, String entidad, Integer idRegistroAfectado,
                                     HttpServletRequest request) {
        return registrarAccion(accion, entidad, idRegistroAfectado, null, null, request);
    }

    /**
     * Registra una acción en el sistema de auditoría con datos.
     * 
     * @param accion Acción realizada
     * @param entidad Entidad afectada
     * @param idRegistroAfectado ID del registro afectado
     * @param datosNuevos Datos después del cambio
     * @param request Petición HTTP para obtener IP
     * @return Registro de auditoría creado
     */
    @Transactional
    public Auditoria registrarAccionConDatos(String accion, String entidad, Integer idRegistroAfectado,
                                             Map<String, Object> datosNuevos, HttpServletRequest request) {
        return registrarAccion(accion, entidad, idRegistroAfectado, null, datosNuevos, request);
    }

    /**
     * Registra una acción en el sistema de auditoría con datos anteriores y nuevos.
     * 
     * @param accion Acción realizada
     * @param entidad Entidad afectada
     * @param idRegistroAfectado ID del registro afectado
     * @param datosAnteriores Datos antes del cambio
     * @param datosNuevos Datos después del cambio
     * @param request Petición HTTP para obtener IP
     * @return Registro de auditoría creado
     */
    @Transactional
    public Auditoria registrarAccionConCambios(String accion, String entidad, Integer idRegistroAfectado,
                                               Map<String, Object> datosAnteriores, 
                                               Map<String, Object> datosNuevos,
                                               HttpServletRequest request) {
        return registrarAccion(accion, entidad, idRegistroAfectado, datosAnteriores, datosNuevos, request);
    }

    // ============================================
    // REGISTRO DE ACCIONES ESPECÍFICAS
    // ============================================

    /**
     * Registra la creación de un registro.
     * 
     * @param entidad Entidad creada
     * @param idRegistro ID del registro creado
     * @param datos Datos del registro creado
     * @param request Petición HTTP
     * @return Registro de auditoría
     */
    @Transactional
    public Auditoria registrarCreacion(String entidad, Integer idRegistro, 
                                       Map<String, Object> datos, HttpServletRequest request) {
        return registrarAccion("CREATE", entidad, idRegistro, null, datos, request);
    }

    /**
     * Registra la actualización de un registro.
     * 
     * @param entidad Entidad actualizada
     * @param idRegistro ID del registro actualizado
     * @param datosAnteriores Datos antes del cambio
     * @param datosNuevos Datos después del cambio
     * @param request Petición HTTP
     * @return Registro de auditoría
     */
    @Transactional
    public Auditoria registrarActualizacion(String entidad, Integer idRegistro,
                                            Map<String, Object> datosAnteriores,
                                            Map<String, Object> datosNuevos,
                                            HttpServletRequest request) {
        return registrarAccion("UPDATE", entidad, idRegistro, datosAnteriores, datosNuevos, request);
    }

    /**
     * Registra la eliminación de un registro.
     * 
     * @param entidad Entidad eliminada
     * @param idRegistro ID del registro eliminado
     * @param datos Datos del registro eliminado
     * @param request Petición HTTP
     * @return Registro de auditoría
     */
    @Transactional
    public Auditoria registrarEliminacion(String entidad, Integer idRegistro,
                                          Map<String, Object> datos, HttpServletRequest request) {
        return registrarAccion("DELETE", entidad, idRegistro, datos, null, request);
    }

    /**
     * Registra un cambio de estado de un proyecto.
     * 
     * @param idProyecto ID del proyecto
     * @param estadoAnterior Estado anterior
     * @param estadoNuevo Estado nuevo
     * @param comentario Comentario del cambio
     * @param request Petición HTTP
     * @return Registro de auditoría
     */
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

    /**
     * Registra la validación de un pago.
     * 
     * @param idPago ID del pago
     * @param estadoAnterior Estado anterior del pago
     * @param estadoNuevo Estado nuevo del pago
     * @param observaciones Observaciones de la validación
     * @param request Petición HTTP
     * @return Registro de auditoría
     */
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

    /**
     * Registra la deshabilitación de un cliente.
     * 
     * @param idCliente ID del cliente
     * @param motivo Motivo de la deshabilitación
     * @param request Petición HTTP
     * @return Registro de auditoría
     */
    @Transactional
    public Auditoria registrarDeshabilitacionCliente(Integer idCliente, String motivo,
                                                     HttpServletRequest request) {
        Map<String, Object> datosNuevos = new HashMap<>();
        datosNuevos.put("motivo", motivo);
        datosNuevos.put("fecha_deshabilitacion", LocalDateTime.now().toString());
        
        return registrarAccion("DISABLE", "Cliente", idCliente, null, datosNuevos, request);
    }

    /**
     * Registra la habilitación de un cliente.
     * 
     * @param idCliente ID del cliente
     * @param request Petición HTTP
     * @return Registro de auditoría
     */
    @Transactional
    public Auditoria registrarHabilitacionCliente(Integer idCliente, HttpServletRequest request) {
        Map<String, Object> datosNuevos = new HashMap<>();
        datosNuevos.put("fecha_habilitacion", LocalDateTime.now().toString());
        
        return registrarAccion("ENABLE", "Cliente", idCliente, null, datosNuevos, request);
    }

    /**
     * Registra un inicio de sesión.
     * 
     * @param email Email del usuario
     * @param request Petición HTTP
     * @return Registro de auditoría
     */
    @Transactional
    public Auditoria registrarLogin(String email, HttpServletRequest request) {
        Integer idUsuario = null;
        try {
            Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
            if (usuario != null) {
                idUsuario = usuario.getIdUsuario();
            }
        } catch (Exception e) {
            // Ignorar
        }
        
        Map<String, Object> datosNuevos = new HashMap<>();
        datosNuevos.put("email", email);
        datosNuevos.put("fecha_login", LocalDateTime.now().toString());
        datosNuevos.put("ip", getClientIP(request));
        datosNuevos.put("user_agent", request.getHeader("User-Agent"));
        
        return registrarAccion("LOGIN", "Usuario", idUsuario, null, datosNuevos, request);
    }

    /**
     * Registra un cierre de sesión.
     * 
     * @param email Email del usuario
     * @param request Petición HTTP
     * @return Registro de auditoría
     */
    @Transactional
    public Auditoria registrarLogout(String email, HttpServletRequest request) {
        Integer idUsuario = null;
        try {
            Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
            if (usuario != null) {
                idUsuario = usuario.getIdUsuario();
            }
        } catch (Exception e) {
            // Ignorar
        }
        
        Map<String, Object> datosNuevos = new HashMap<>();
        datosNuevos.put("email", email);
        datosNuevos.put("fecha_logout", LocalDateTime.now().toString());
        
        return registrarAccion("LOGOUT", "Usuario", idUsuario, null, datosNuevos, request);
    }

    // ============================================
    // CONSULTAS DE AUDITORÍA
    // ============================================

    /**
     * Obtiene todos los registros de auditoría con paginación.
     * 
     * @param pageable Configuración de paginación
     * @return Página de registros de auditoría
     */
    public Page<Auditoria> findAll(Pageable pageable) {
        return auditoriaRepository.findAll(pageable);
    }

    /**
     * Busca registros de auditoría por ID de usuario.
     * 
     * @param idUsuario ID del usuario
     * @param pageable Configuración de paginación
     * @return Página de registros de auditoría
     */
    public Page<Auditoria> findByIdUsuario(Integer idUsuario, Pageable pageable) {
        return auditoriaRepository.findByIdUsuario(idUsuario, pageable);
    }

    /**
     * Busca registros de auditoría por entidad.
     * 
     * @param entidad Entidad afectada
     * @param pageable Configuración de paginación
     * @return Página de registros de auditoría
     */
    public Page<Auditoria> findByEntidad(String entidad, Pageable pageable) {
        return auditoriaRepository.findByEntidad(entidad, pageable);
    }

    /**
     * Busca registros de auditoría por acción.
     * 
     * @param accion Acción realizada
     * @param pageable Configuración de paginación
     * @return Página de registros de auditoría
     */
    public Page<Auditoria> findByAccion(String accion, Pageable pageable) {
        return auditoriaRepository.findByAccion(accion, pageable);
    }

    /**
     * Busca registros de auditoría por entidad y registro afectado.
     * 
     * @param entidad Entidad afectada
     * @param idRegistroAfectado ID del registro afectado
     * @param pageable Configuración de paginación
     * @return Página de registros de auditoría
     */
    public Page<Auditoria> findByEntidadAndIdRegistroAfectado(String entidad, Integer idRegistroAfectado,
                                                              Pageable pageable) {
        return auditoriaRepository.findByEntidadAndIdRegistroAfectado(entidad, idRegistroAfectado, pageable);
    }

    /**
     * Busca registros de auditoría por rango de fechas.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param pageable Configuración de paginación
     * @return Página de registros de auditoría
     */
    public Page<Auditoria> findByFechaHoraBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin,
                                                  Pageable pageable) {
        return auditoriaRepository.findByFechaHoraBetween(fechaInicio, fechaFin, pageable);
    }

    /**
     * Busca registros de auditoría por acción y entidad.
     * 
     * @param accion Acción realizada
     * @param entidad Entidad afectada
     * @param pageable Configuración de paginación
     * @return Página de registros de auditoría
     */
    public Page<Auditoria> findByAccionAndEntidad(String accion, String entidad, Pageable pageable) {
        return auditoriaRepository.findByAccionAndEntidad(accion, entidad, pageable);
    }

    /**
     * Busca los últimos registros de auditoría.
     * 
     * @param limit Número máximo de registros
     * @return Lista de registros de auditoría
     */
    public List<Auditoria> findLastAuditorias(int limit) {
        return auditoriaRepository.findLastAuditorias(limit);
    }

    /**
     * Busca cambios de estado de proyectos.
     * 
     * @param pageable Configuración de paginación
     * @return Página de registros de auditoría
     */
    public Page<Auditoria> findCambiosEstado(Pageable pageable) {
        return auditoriaRepository.findCambiosEstado(pageable);
    }

    /**
     * Busca cambios de estado de un proyecto específico.
     * 
     * @param idProyecto ID del proyecto
     * @param pageable Configuración de paginación
     * @return Página de registros de auditoría
     */
    public Page<Auditoria> findCambiosEstadoByProyecto(Integer idProyecto, Pageable pageable) {
        return auditoriaRepository.findCambiosEstadoByProyecto(idProyecto, pageable);
    }

    /**
     * Busca registros de inicio/cierre de sesión.
     * 
     * @param pageable Configuración de paginación
     * @return Página de registros de auditoría
     */
    public Page<Auditoria> findAccesos(Pageable pageable) {
        return auditoriaRepository.findAccesos(pageable);
    }

    /**
     * Busca registros de inicio/cierre de sesión de un usuario.
     * 
     * @param idUsuario ID del usuario
     * @param pageable Configuración de paginación
     * @return Página de registros de auditoría
     */
    public Page<Auditoria> findAccesosByUsuario(Integer idUsuario, Pageable pageable) {
        return auditoriaRepository.findAccesosByUsuario(idUsuario, pageable);
    }

    /**
     * Busca registros de auditoría por texto.
     * 
     * @param searchText Texto a buscar
     * @param pageable Configuración de paginación
     * @return Página de registros de auditoría
     */
    public Page<Auditoria> search(String searchText, Pageable pageable) {
        return auditoriaRepository.search(searchText, pageable);
    }

    // ============================================
    // MÉTRICAS Y ESTADÍSTICAS
    // ============================================

    /**
     * Obtiene el número total de registros de auditoría.
     * 
     * @return Total de registros
     */
    public long countAll() {
        return auditoriaRepository.countAll();
    }

    /**
     * Cuenta registros de auditoría por entidad.
     * 
     * @param entidad Entidad afectada
     * @return Número de registros
     */
    public long countByEntidad(String entidad) {
        return auditoriaRepository.countByEntidad(entidad);
    }

    /**
     * Cuenta registros de auditoría por acción.
     * 
     * @param accion Acción realizada
     * @return Número de registros
     */
    public long countByAccion(String accion) {
        return auditoriaRepository.countByAccion(accion);
    }

    /**
     * Cuenta registros de auditoría de hoy.
     * 
     * @return Número de registros de hoy
     */
    public long countToday() {
        return auditoriaRepository.countToday();
    }

    /**
     * Cuenta registros de auditoría del mes.
     * 
     * @return Número de registros del mes
     */
    public long countThisMonth() {
        return auditoriaRepository.countThisMonth();
    }

    /**
     * Cuenta registros de auditoría del año.
     * 
     * @return Número de registros del año
     */
    public long countThisYear() {
        return auditoriaRepository.countThisYear();
    }

    /**
     * Obtiene el resumen de acciones por entidad.
     * 
     * @return Lista de objetos con conteo por entidad
     */
    public List<Object[]> countByEntidadResumen() {
        return auditoriaRepository.countByEntidadResumen();
    }

    /**
     * Obtiene el resumen de acciones por acción.
     * 
     * @return Lista de objetos con conteo por acción
     */
    public List<Object[]> countByAccionResumen() {
        return auditoriaRepository.countByAccionResumen();
    }

    /**
     * Obtiene el total de acciones por día para los últimos N días.
     * 
     * @param days Número de días
     * @return Lista de objetos con conteo por día
     */
    public List<Object[]> countByLastDays(int days) {
        return auditoriaRepository.countByLastDays(days);
    }

    // ============================================
    // LIMPIEZA DE AUDITORÍA
    // ============================================

    /**
     * Elimina registros de auditoría anteriores a una fecha.
     * 
     * @param fecha Fecha límite
     * @return Número de registros eliminados
     */
    @Transactional
    public int deleteOlderThan(LocalDateTime fecha) {
        return auditoriaRepository.deleteOlderThan(fecha);
    }

    /**
     * Elimina registros de auditoría de más de 6 meses.
     * 
     * @return Número de registros eliminados
     */
    @Transactional
    public int deleteOldRecords() {
        return deleteOlderThan(LocalDateTime.now().minusMonths(6));
    }

    /**
     * Elimina registros de auditoría de más de N meses.
     * 
     * @param months Número de meses
     * @return Número de registros eliminados
     */
    @Transactional
    public int deleteOlderThanMonths(int months) {
        return deleteOlderThan(LocalDateTime.now().minusMonths(months));
    }

    // ============================================
    // MÉTODOS AUXILIARES
    // ============================================

    /**
     * Obtiene el ID del usuario actual autenticado.
     * 
     * @return ID del usuario, o null si no está autenticado
     */
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

    /**
     * Obtiene el email del usuario actual autenticado.
     * 
     * @return Email del usuario, o null si no está autenticado
     */
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

    /**
     * Obtiene la IP del cliente desde la petición HTTP.
     * 
     * @param request Petición HTTP
     * @return IP del cliente
     */
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
        
        // Si hay múltiples IPs, tomar la primera
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }

    /**
     * Obtiene el User-Agent de la petición.
     * 
     * @param request Petición HTTP
     * @return User-Agent
     */
    private String getUserAgent(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return request.getHeader("User-Agent");
    }

    /**
     * Crea un mapa de datos para un registro.
     * 
     * @param objeto Objeto del cual extraer datos
     * @return Mapa con los datos del objeto
     */
    public Map<String, Object> crearMapaDatos(Object objeto) {
        if (objeto == null) {
            return new HashMap<>();
        }
        
        // Usar reflexión para extraer datos del objeto
        // Esta es una implementación simplificada
        Map<String, Object> datos = new HashMap<>();
        try {
            java.lang.reflect.Field[] fields = objeto.getClass().getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(objeto);
                    datos.put(field.getName(), value);
                } catch (IllegalAccessException e) {
                    // Ignorar
                }
            }
        } catch (Exception e) {
            // Ignorar
        }
        return datos;
    }

    /**
     * Obtiene el nombre de usuario para mostrar en la auditoría.
     * 
     * @return Nombre del usuario o "Sistema"
     */
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