package com.stamos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Entidad AuditoriaSistema.
 * 
 * <p>Esta entidad registra todas las acciones críticas realizadas en el sistema,
 * permitiendo la trazabilidad de cambios y auditoría de operaciones.</p>
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
@Entity
@Table(name = "AuditoriaSistema", 
       indexes = {
           @Index(name = "idx_auditoria_usuario", columnList = "id_usuario"),
           @Index(name = "idx_auditoria_entidad", columnList = "entidad"),
           @Index(name = "idx_auditoria_accion", columnList = "accion"),
           @Index(name = "idx_auditoria_fecha", columnList = "fecha_hora"),
           @Index(name = "idx_auditoria_registro", columnList = "id_registro_afectado")
       })
public class Auditoria {

    // ============================================
    // ATRIBUTOS
    // ============================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Integer idAuditoria;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    @NotBlank(message = "La acción es obligatoria")
    @Size(max = 100, message = "La acción no puede exceder los 100 caracteres")
    @Column(name = "accion", nullable = false, length = 100)
    private String accion;

    @NotBlank(message = "La entidad es obligatoria")
    @Size(max = 50, message = "La entidad no puede exceder los 50 caracteres")
    @Column(name = "entidad", nullable = false, length = 50)
    private String entidad;

    @Column(name = "id_registro_afectado")
    private Integer idRegistroAfectado;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "datos_anteriores", columnDefinition = "JSON")
    private Map<String, Object> datosAnteriores;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "datos_nuevos", columnDefinition = "JSON")
    private Map<String, Object> datosNuevos;

    @Size(max = 45, message = "La IP no puede exceder los 45 caracteres")
    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    // ============================================
    // CAMPOS ADICIONALES (NO EN BD)
    // ============================================

    @Transient
    private String nombreUsuario;

    @Transient
    private String emailUsuario;

    // ============================================
    // CONSTRUCTORES
    // ============================================

    public Auditoria() {
        this.fechaHora = LocalDateTime.now();
        this.datosAnteriores = new HashMap<>();
        this.datosNuevos = new HashMap<>();
    }

    public Auditoria(Integer idUsuario, String accion, String entidad, 
                     Integer idRegistroAfectado, String ipOrigen) {
        this();
        this.idUsuario = idUsuario;
        this.accion = accion;
        this.entidad = entidad;
        this.idRegistroAfectado = idRegistroAfectado;
        this.ipOrigen = ipOrigen;
    }

    // ============================================
    // GETTERS Y SETTERS
    // ============================================

    public Integer getIdAuditoria() {
        return idAuditoria;
    }

    public void setIdAuditoria(Integer idAuditoria) {
        this.idAuditoria = idAuditoria;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public Integer getIdRegistroAfectado() {
        return idRegistroAfectado;
    }

    public void setIdRegistroAfectado(Integer idRegistroAfectado) {
        this.idRegistroAfectado = idRegistroAfectado;
    }

    public Map<String, Object> getDatosAnteriores() {
        return datosAnteriores;
    }

    public void setDatosAnteriores(Map<String, Object> datosAnteriores) {
        this.datosAnteriores = datosAnteriores;
    }

    public Map<String, Object> getDatosNuevos() {
        return datosNuevos;
    }

    public void setDatosNuevos(Map<String, Object> datosNuevos) {
        this.datosNuevos = datosNuevos;
    }

    public String getIpOrigen() {
        return ipOrigen;
    }

    public void setIpOrigen(String ipOrigen) {
        this.ipOrigen = ipOrigen;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    // ============================================
    // MÉTODOS DE NEGOCIO
    // ============================================

    /**
     * Agrega un dato anterior al mapa de datos anteriores.
     * 
     * @param key Clave del dato
     * @param value Valor del dato
     */
    public void agregarDatoAnterior(String key, Object value) {
        if (this.datosAnteriores == null) {
            this.datosAnteriores = new HashMap<>();
        }
        this.datosAnteriores.put(key, value);
    }

    /**
     * Agrega un dato nuevo al mapa de datos nuevos.
     * 
     * @param key Clave del dato
     * @param value Valor del dato
     */
    public void agregarDatoNuevo(String key, Object value) {
        if (this.datosNuevos == null) {
            this.datosNuevos = new HashMap<>();
        }
        this.datosNuevos.put(key, value);
    }

    /**
     * Obtiene un dato anterior por su clave.
     * 
     * @param key Clave del dato
     * @return Valor del dato
     */
    public Object getDatoAnterior(String key) {
        if (datosAnteriores == null) {
            return null;
        }
        return datosAnteriores.get(key);
    }

    /**
     * Obtiene un dato nuevo por su clave.
     * 
     * @param key Clave del dato
     * @return Valor del dato
     */
    public Object getDatoNuevo(String key) {
        if (datosNuevos == null) {
            return null;
        }
        return datosNuevos.get(key);
    }

    /**
     * Verifica si el registro tiene datos anteriores.
     * 
     * @return true si tiene datos anteriores
     */
    public boolean tieneDatosAnteriores() {
        return datosAnteriores != null && !datosAnteriores.isEmpty();
    }

    /**
     * Verifica si el registro tiene datos nuevos.
     * 
     * @return true si tiene datos nuevos
     */
    public boolean tieneDatosNuevos() {
        return datosNuevos != null && !datosNuevos.isEmpty();
    }

    /**
     * Verifica si la acción es de creación.
     * 
     * @return true si es CREATE
     */
    public boolean esCreacion() {
        return "CREATE".equals(this.accion) || "INSERT".equals(this.accion);
    }

    /**
     * Verifica si la acción es de actualización.
     * 
     * @return true si es UPDATE
     */
    public boolean esActualizacion() {
        return "UPDATE".equals(this.accion) || "MODIFY".equals(this.accion);
    }

    /**
     * Verifica si la acción es de eliminación.
     * 
     * @return true si es DELETE
     */
    public boolean esEliminacion() {
        return "DELETE".equals(this.accion) || "REMOVE".equals(this.accion);
    }

    /**
     * Verifica si la acción es de cambio de estado.
     * 
     * @return true si es STATE_CHANGE
     */
    public boolean esCambioEstado() {
        return "STATE_CHANGE".equals(this.accion);
    }

    /**
     * Verifica si la acción es de validación.
     * 
     * @return true si es VALIDATE
     */
    public boolean esValidacion() {
        return "VALIDATE".equals(this.accion);
    }

    /**
     * Verifica si la acción es de login.
     * 
     * @return true si es LOGIN
     */
    public boolean esLogin() {
        return "LOGIN".equals(this.accion);
    }

    /**
     * Verifica si la acción es de logout.
     * 
     * @return true si es LOGOUT
     */
    public boolean esLogout() {
        return "LOGOUT".equals(this.accion);
    }

    /**
     * Verifica si la acción es de deshabilitación.
     * 
     * @return true si es DISABLE
     */
    public boolean esDeshabilitacion() {
        return "DISABLE".equals(this.accion);
    }

    /**
     * Verifica si la acción es de habilitación.
     * 
     * @return true si es ENABLE
     */
    public boolean esHabilitacion() {
        return "ENABLE".equals(this.accion);
    }

    // ============================================
    // MÉTODOS DE FORMATO PARA LA VISTA
    // ============================================

    /**
     * Obtiene la acción en formato legible.
     * 
     * @return Acción legible
     */
    public String getAccionLegible() {
        if (accion == null) return "Sin acción";
        
        switch (accion) {
            case "CREATE":
                return "Creación";
            case "INSERT":
                return "Inserción";
            case "UPDATE":
                return "Actualización";
            case "MODIFY":
                return "Modificación";
            case "DELETE":
                return "Eliminación";
            case "REMOVE":
                return "Remoción";
            case "STATE_CHANGE":
                return "Cambio de Estado";
            case "VALIDATE":
                return "Validación";
            case "LOGIN":
                return "Inicio de Sesión";
            case "LOGOUT":
                return "Cierre de Sesión";
            case "DISABLE":
                return "Deshabilitación";
            case "ENABLE":
                return "Habilitación";
            case "EXECUTE":
                return "Ejecución";
            default:
                return accion;
        }
    }

    /**
     * Obtiene la clase CSS para el badge de la acción.
     * 
     * @return Clase CSS
     */
    public String getAccionBadgeClass() {
        if (accion == null) return "badge-secondary";
        
        switch (accion) {
            case "CREATE":
            case "INSERT":
                return "badge-success";
            case "UPDATE":
            case "MODIFY":
                return "badge-warning";
            case "DELETE":
            case "REMOVE":
                return "badge-danger";
            case "STATE_CHANGE":
                return "badge-info";
            case "VALIDATE":
                return "badge-primary";
            case "LOGIN":
                return "badge-success";
            case "LOGOUT":
                return "badge-secondary";
            case "DISABLE":
                return "badge-danger";
            case "ENABLE":
                return "badge-success";
            default:
                return "badge-secondary";
        }
    }

    /**
     * Obtiene el ícono para la acción.
     * 
     * @return Ícono CSS
     */
    public String getAccionIcono() {
        if (accion == null) return "ti ti-circle";
        
        switch (accion) {
            case "CREATE":
            case "INSERT":
                return "ti ti-plus-circle";
            case "UPDATE":
            case "MODIFY":
                return "ti ti-edit";
            case "DELETE":
            case "REMOVE":
                return "ti ti-trash";
            case "STATE_CHANGE":
                return "ti ti-arrows-exchange";
            case "VALIDATE":
                return "ti ti-check-circle";
            case "LOGIN":
                return "ti ti-login";
            case "LOGOUT":
                return "ti ti-logout";
            case "DISABLE":
                return "ti ti-ban";
            case "ENABLE":
                return "ti ti-check";
            default:
                return "ti ti-circle";
        }
    }

    /**
     * Obtiene la fecha y hora formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaHoraFormateada() {
        if (fechaHora == null) return "-";
        return fechaHora.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    /**
     * Obtiene la fecha formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaFormateada() {
        if (fechaHora == null) return "-";
        return fechaHora.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Obtiene la hora formateada.
     * 
     * @return Hora formateada
     */
    public String getHoraFormateada() {
        if (fechaHora == null) return "-";
        return fechaHora.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    /**
     * Obtiene la entidad en formato legible.
     * 
     * @return Entidad legible
     */
    public String getEntidadLegible() {
        if (entidad == null) return "Sin entidad";
        
        switch (entidad) {
            case "Cliente":
                return "Cliente";
            case "Proyecto":
                return "Proyecto";
            case "Cotizacion":
                return "Cotización";
            case "Pago":
                return "Pago";
            case "Inventario":
                return "Inventario";
            case "Producto":
                return "Producto";
            case "Usuario":
                return "Usuario";
            case "Documento":
                return "Documento";
            case "Configuracion":
                return "Configuración";
            default:
                return entidad;
        }
    }

    /**
     * Obtiene el color para la entidad.
     * 
     * @return Color en hexadecimal
     */
    public String getEntidadColor() {
        if (entidad == null) return "#6B7280";
        
        switch (entidad) {
            case "Cliente":
                return "#3B82F6";
            case "Proyecto":
                return "#8B5CF6";
            case "Cotizacion":
                return "#F59E0B";
            case "Pago":
                return "#10B981";
            case "Inventario":
                return "#EF4444";
            case "Producto":
                return "#EC4899";
            case "Usuario":
                return "#6366F1";
            case "Documento":
                return "#14B8A6";
            case "Configuracion":
                return "#F97316";
            default:
                return "#6B7280";
        }
    }

    /**
     * Obtiene el usuario en formato legible.
     * 
     * @return Usuario legible
     */
    public String getUsuarioLegible() {
        if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
            return nombreUsuario;
        }
        if (emailUsuario != null && !emailUsuario.isEmpty()) {
            return emailUsuario;
        }
        if (idUsuario != null) {
            return "Usuario ID: " + idUsuario;
        }
        return "Sistema";
    }

    /**
     * Obtiene el resumen de la auditoría.
     * 
     * @return Resumen
     */
    public String getResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append(getAccionLegible());
        sb.append(" en ");
        sb.append(getEntidadLegible());
        if (idRegistroAfectado != null) {
            sb.append(" ID: ").append(idRegistroAfectado);
        }
        return sb.toString();
    }

    /**
     * Obtiene los datos en formato JSON legible.
     * 
     * @return Datos formateados
     */
    public String getDatosFormateados() {
        StringBuilder sb = new StringBuilder();
        
        if (tieneDatosAnteriores()) {
            sb.append("Anteriores: ");
            sb.append(datosAnteriores.toString());
        }
        
        if (tieneDatosAnteriores() && tieneDatosNuevos()) {
            sb.append(" → ");
        }
        
        if (tieneDatosNuevos()) {
            sb.append("Nuevos: ");
            sb.append(datosNuevos.toString());
        }
        
        return sb.toString();
    }

    // ============================================
    // MÉTODOS DE CONVERSIÓN
    // ============================================

    /**
     * Convierte a una representación JSON simple.
     * 
     * @return JSON como String
     */
    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"idAuditoria\":").append(idAuditoria).append(",");
        sb.append("\"accion\":\"").append(accion != null ? accion : "").append("\",");
        sb.append("\"entidad\":\"").append(entidad != null ? entidad : "").append("\",");
        sb.append("\"idRegistroAfectado\":").append(idRegistroAfectado != null ? idRegistroAfectado : "null").append(",");
        sb.append("\"fechaHora\":\"").append(fechaHora != null ? fechaHora.toString() : "").append("\",");
        sb.append("\"ipOrigen\":\"").append(ipOrigen != null ? ipOrigen : "").append("\"");
        sb.append("}");
        return sb.toString();
    }

    // ============================================
    // MÉTODOS OVERRIDE
    // ============================================

    @Override
    public String toString() {
        return "Auditoria{" +
                "idAuditoria=" + idAuditoria +
                ", idUsuario=" + idUsuario +
                ", accion='" + accion + '\'' +
                ", entidad='" + entidad + '\'' +
                ", idRegistroAfectado=" + idRegistroAfectado +
                ", fechaHora=" + fechaHora +
                ", ipOrigen='" + ipOrigen + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Auditoria auditoria = (Auditoria) o;
        return idAuditoria != null && idAuditoria.equals(auditoria.idAuditoria);
    }

    @Override
    public int hashCode() {
        return idAuditoria != null ? idAuditoria.hashCode() : 0;
    }

    // ============================================
    // MÉTODOS ESTÁTICOS DE FÁBRICA
    // ============================================

    /**
     * Crea un registro de auditoría para una acción de creación.
     * 
     * @param idUsuario ID del usuario
     * @param entidad Entidad afectada
     * @param idRegistro ID del registro creado
     * @param datosNuevos Datos nuevos
     * @param ipOrigen IP de origen
     * @return Registro de auditoría
     */
    public static Auditoria createAuditoriaCreacion(Integer idUsuario, String entidad,
                                                   Integer idRegistro, Map<String, Object> datosNuevos,
                                                   String ipOrigen) {
        Auditoria auditoria = new Auditoria(idUsuario, "CREATE", entidad, idRegistro, ipOrigen);
        auditoria.setDatosNuevos(datosNuevos);
        return auditoria;
    }

    /**
     * Crea un registro de auditoría para una acción de actualización.
     * 
     * @param idUsuario ID del usuario
     * @param entidad Entidad afectada
     * @param idRegistro ID del registro actualizado
     * @param datosAnteriores Datos anteriores
     * @param datosNuevos Datos nuevos
     * @param ipOrigen IP de origen
     * @return Registro de auditoría
     */
    public static Auditoria createAuditoriaActualizacion(Integer idUsuario, String entidad,
                                                        Integer idRegistro, Map<String, Object> datosAnteriores,
                                                        Map<String, Object> datosNuevos, String ipOrigen) {
        Auditoria auditoria = new Auditoria(idUsuario, "UPDATE", entidad, idRegistro, ipOrigen);
        auditoria.setDatosAnteriores(datosAnteriores);
        auditoria.setDatosNuevos(datosNuevos);
        return auditoria;
    }

    /**
     * Crea un registro de auditoría para una acción de eliminación.
     * 
     * @param idUsuario ID del usuario
     * @param entidad Entidad afectada
     * @param idRegistro ID del registro eliminado
     * @param datosAnteriores Datos anteriores
     * @param ipOrigen IP de origen
     * @return Registro de auditoría
     */
    public static Auditoria createAuditoriaEliminacion(Integer idUsuario, String entidad,
                                                       Integer idRegistro, Map<String, Object> datosAnteriores,
                                                       String ipOrigen) {
        Auditoria auditoria = new Auditoria(idUsuario, "DELETE", entidad, idRegistro, ipOrigen);
        auditoria.setDatosAnteriores(datosAnteriores);
        return auditoria;
    }

    /**
     * Crea un registro de auditoría para un cambio de estado.
     * 
     * @param idUsuario ID del usuario
     * @param entidad Entidad afectada
     * @param idRegistro ID del registro
     * @param estadoAnterior Estado anterior
     * @param estadoNuevo Estado nuevo
     * @param ipOrigen IP de origen
     * @return Registro de auditoría
     */
    public static Auditoria createAuditoriaCambioEstado(Integer idUsuario, String entidad,
                                                       Integer idRegistro, String estadoAnterior,
                                                       String estadoNuevo, String ipOrigen) {
        Map<String, Object> anteriores = new HashMap<>();
        anteriores.put("estado", estadoAnterior);
        
        Map<String, Object> nuevos = new HashMap<>();
        nuevos.put("estado", estadoNuevo);
        nuevos.put("fecha_cambio", LocalDateTime.now().toString());
        
        Auditoria auditoria = new Auditoria(idUsuario, "STATE_CHANGE", entidad, idRegistro, ipOrigen);
        auditoria.setDatosAnteriores(anteriores);
        auditoria.setDatosNuevos(nuevos);
        return auditoria;
    }

    /**
     * Crea un registro de auditoría para un inicio de sesión.
     * 
     * @param idUsuario ID del usuario
     * @param ipOrigen IP de origen
     * @return Registro de auditoría
     */
    public static Auditoria createAuditoriaLogin(Integer idUsuario, String ipOrigen) {
        Map<String, Object> nuevos = new HashMap<>();
        nuevos.put("fecha_login", LocalDateTime.now().toString());
        nuevos.put("ip", ipOrigen);
        
        Auditoria auditoria = new Auditoria(idUsuario, "LOGIN", "Usuario", null, ipOrigen);
        auditoria.setDatosNuevos(nuevos);
        return auditoria;
    }

    /**
     * Crea un registro de auditoría para un cierre de sesión.
     * 
     * @param idUsuario ID del usuario
     * @param ipOrigen IP de origen
     * @return Registro de auditoría
     */
    public static Auditoria createAuditoriaLogout(Integer idUsuario, String ipOrigen) {
        Map<String, Object> nuevos = new HashMap<>();
        nuevos.put("fecha_logout", LocalDateTime.now().toString());
        
        Auditoria auditoria = new Auditoria(idUsuario, "LOGOUT", "Usuario", null, ipOrigen);
        auditoria.setDatosNuevos(nuevos);
        return auditoria;
    }

    /**
     * Crea un registro de auditoría para una deshabilitación.
     * 
     * @param idUsuario ID del usuario
     * @param entidad Entidad afectada
     * @param idRegistro ID del registro
     * @param motivo Motivo de deshabilitación
     * @param ipOrigen IP de origen
     * @return Registro de auditoría
     */
    public static Auditoria createAuditoriaDeshabilitacion(Integer idUsuario, String entidad,
                                                          Integer idRegistro, String motivo,
                                                          String ipOrigen) {
        Map<String, Object> nuevos = new HashMap<>();
        nuevos.put("motivo", motivo);
        nuevos.put("fecha_deshabilitacion", LocalDateTime.now().toString());
        
        Auditoria auditoria = new Auditoria(idUsuario, "DISABLE", entidad, idRegistro, ipOrigen);
        auditoria.setDatosNuevos(nuevos);
        return auditoria;
    }

    /**
     * Crea un registro de auditoría para una habilitación.
     * 
     * @param idUsuario ID del usuario
     * @param entidad Entidad afectada
     * @param idRegistro ID del registro
     * @param ipOrigen IP de origen
     * @return Registro de auditoría
     */
    public static Auditoria createAuditoriaHabilitacion(Integer idUsuario, String entidad,
                                                       Integer idRegistro, String ipOrigen) {
        Map<String, Object> nuevos = new HashMap<>();
        nuevos.put("fecha_habilitacion", LocalDateTime.now().toString());
        
        Auditoria auditoria = new Auditoria(idUsuario, "ENABLE", entidad, idRegistro, ipOrigen);
        auditoria.setDatosNuevos(nuevos);
        return auditoria;
    }
}