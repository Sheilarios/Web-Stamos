package com.stamos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    @Column(name = "datos_anteriores", columnDefinition = "TEXT")
    private String datosAnteriores;

    @Column(name = "datos_nuevos", columnDefinition = "TEXT")
    private String datosNuevos;

    @Size(max = 45, message = "La IP no puede exceder los 45 caracteres")
    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Transient
    private String nombreUsuario;

    @Transient
    private String emailUsuario;

    public Auditoria() {
        this.fechaHora = LocalDateTime.now();
        this.datosAnteriores = "{}";
        this.datosNuevos = "{}";
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

    public String getDatosAnteriores() {
        return datosAnteriores;
    }

    public void setDatosAnteriores(String datosAnteriores) {
        this.datosAnteriores = datosAnteriores;
    }

    public String getDatosNuevos() {
        return datosNuevos;
    }

    public void setDatosNuevos(String datosNuevos) {
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

    public void agregarDatoAnterior(String key, Object value) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            if (this.datosAnteriores != null && !this.datosAnteriores.equals("{}")) {
                map = mapper.readValue(this.datosAnteriores, java.util.Map.class);
            }
            map.put(key, value);
            this.datosAnteriores = mapper.writeValueAsString(map);
        } catch (Exception e) {
            this.datosAnteriores = "{}";
        }
    }

    public void agregarDatoNuevo(String key, Object value) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            if (this.datosNuevos != null && !this.datosNuevos.equals("{}")) {
                map = mapper.readValue(this.datosNuevos, java.util.Map.class);
            }
            map.put(key, value);
            this.datosNuevos = mapper.writeValueAsString(map);
        } catch (Exception e) {
            this.datosNuevos = "{}";
        }
    }

    public Object getDatoAnterior(String key) {
        try {
            if (datosAnteriores == null || datosAnteriores.equals("{}")) {
                return null;
            }
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.Map<String, Object> map = mapper.readValue(datosAnteriores, java.util.Map.class);
            return map.get(key);
        } catch (Exception e) {
            return null;
        }
    }

    public Object getDatoNuevo(String key) {
        try {
            if (datosNuevos == null || datosNuevos.equals("{}")) {
                return null;
            }
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.Map<String, Object> map = mapper.readValue(datosNuevos, java.util.Map.class);
            return map.get(key);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean tieneDatosAnteriores() {
        return datosAnteriores != null && !datosAnteriores.equals("{}") && !datosAnteriores.isEmpty();
    }

    public boolean tieneDatosNuevos() {
        return datosNuevos != null && !datosNuevos.equals("{}") && !datosNuevos.isEmpty();
    }

    public boolean esCreacion() {
        return "CREATE".equals(this.accion) || "INSERT".equals(this.accion);
    }

    public boolean esActualizacion() {
        return "UPDATE".equals(this.accion) || "MODIFY".equals(this.accion);
    }

    public boolean esEliminacion() {
        return "DELETE".equals(this.accion) || "REMOVE".equals(this.accion);
    }

    public boolean esCambioEstado() {
        return "STATE_CHANGE".equals(this.accion);
    }

    public boolean esValidacion() {
        return "VALIDATE".equals(this.accion);
    }

    public boolean esLogin() {
        return "LOGIN".equals(this.accion);
    }

    public boolean esLogout() {
        return "LOGOUT".equals(this.accion);
    }

    public boolean esDeshabilitacion() {
        return "DISABLE".equals(this.accion);
    }

    public boolean esHabilitacion() {
        return "ENABLE".equals(this.accion);
    }

    public String getAccionLegible() {
        if (accion == null) return "Sin acción";
        switch (accion) {
            case "CREATE": return "Creación";
            case "INSERT": return "Inserción";
            case "UPDATE": return "Actualización";
            case "MODIFY": return "Modificación";
            case "DELETE": return "Eliminación";
            case "REMOVE": return "Remoción";
            case "STATE_CHANGE": return "Cambio de Estado";
            case "VALIDATE": return "Validación";
            case "LOGIN": return "Inicio de Sesión";
            case "LOGOUT": return "Cierre de Sesión";
            case "DISABLE": return "Deshabilitación";
            case "ENABLE": return "Habilitación";
            case "EXECUTE": return "Ejecución";
            default: return accion;
        }
    }

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

    public String getFechaHoraFormateada() {
        if (fechaHora == null) return "-";
        return fechaHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public String getFechaFormateada() {
        if (fechaHora == null) return "-";
        return fechaHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getHoraFormateada() {
        if (fechaHora == null) return "-";
        return fechaHora.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public String getEntidadLegible() {
        if (entidad == null) return "Sin entidad";
        switch (entidad) {
            case "Cliente": return "Cliente";
            case "Proyecto": return "Proyecto";
            case "Cotizacion": return "Cotización";
            case "Pago": return "Pago";
            case "Inventario": return "Inventario";
            case "Producto": return "Producto";
            case "Usuario": return "Usuario";
            case "Documento": return "Documento";
            case "Configuracion": return "Configuración";
            default: return entidad;
        }
    }

    public String getEntidadColor() {
        if (entidad == null) return "#6B7280";
        switch (entidad) {
            case "Cliente": return "#3B82F6";
            case "Proyecto": return "#8B5CF6";
            case "Cotizacion": return "#F59E0B";
            case "Pago": return "#10B981";
            case "Inventario": return "#EF4444";
            case "Producto": return "#EC4899";
            case "Usuario": return "#6366F1";
            case "Documento": return "#14B8A6";
            case "Configuracion": return "#F97316";
            default: return "#6B7280";
        }
    }

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

    public String getDatosFormateados() {
        StringBuilder sb = new StringBuilder();
        if (tieneDatosAnteriores()) {
            sb.append("Anteriores: ").append(datosAnteriores);
        }
        if (tieneDatosAnteriores() && tieneDatosNuevos()) {
            sb.append(" → ");
        }
        if (tieneDatosNuevos()) {
            sb.append("Nuevos: ").append(datosNuevos);
        }
        return sb.toString();
    }

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

    public static Auditoria createAuditoriaCreacion(Integer idUsuario, String entidad,
                                                   Integer idRegistro, String datosNuevos,
                                                   String ipOrigen) {
        Auditoria auditoria = new Auditoria(idUsuario, "CREATE", entidad, idRegistro, ipOrigen);
        auditoria.setDatosNuevos(datosNuevos != null ? datosNuevos : "{}");
        return auditoria;
    }

    public static Auditoria createAuditoriaActualizacion(Integer idUsuario, String entidad,
                                                        Integer idRegistro, String datosAnteriores,
                                                        String datosNuevos, String ipOrigen) {
        Auditoria auditoria = new Auditoria(idUsuario, "UPDATE", entidad, idRegistro, ipOrigen);
        auditoria.setDatosAnteriores(datosAnteriores != null ? datosAnteriores : "{}");
        auditoria.setDatosNuevos(datosNuevos != null ? datosNuevos : "{}");
        return auditoria;
    }

    public static Auditoria createAuditoriaEliminacion(Integer idUsuario, String entidad,
                                                       Integer idRegistro, String datosAnteriores,
                                                       String ipOrigen) {
        Auditoria auditoria = new Auditoria(idUsuario, "DELETE", entidad, idRegistro, ipOrigen);
        auditoria.setDatosAnteriores(datosAnteriores != null ? datosAnteriores : "{}");
        return auditoria;
    }

    public static Auditoria createAuditoriaCambioEstado(Integer idUsuario, String entidad,
                                                       Integer idRegistro, String estadoAnterior,
                                                       String estadoNuevo, String ipOrigen) {
        java.util.Map<String, Object> anteriores = new java.util.HashMap<>();
        anteriores.put("estado", estadoAnterior);
        java.util.Map<String, Object> nuevos = new java.util.HashMap<>();
        nuevos.put("estado", estadoNuevo);
        nuevos.put("fecha_cambio", LocalDateTime.now().toString());
        
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Auditoria auditoria = new Auditoria(idUsuario, "STATE_CHANGE", entidad, idRegistro, ipOrigen);
            auditoria.setDatosAnteriores(mapper.writeValueAsString(anteriores));
            auditoria.setDatosNuevos(mapper.writeValueAsString(nuevos));
            return auditoria;
        } catch (Exception e) {
            Auditoria auditoria = new Auditoria(idUsuario, "STATE_CHANGE", entidad, idRegistro, ipOrigen);
            auditoria.setDatosAnteriores("{}");
            auditoria.setDatosNuevos("{}");
            return auditoria;
        }
    }

    public static Auditoria createAuditoriaLogin(Integer idUsuario, String ipOrigen) {
        java.util.Map<String, Object> nuevos = new java.util.HashMap<>();
        nuevos.put("fecha_login", LocalDateTime.now().toString());
        nuevos.put("ip", ipOrigen);
        
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Auditoria auditoria = new Auditoria(idUsuario, "LOGIN", "Usuario", null, ipOrigen);
            auditoria.setDatosNuevos(mapper.writeValueAsString(nuevos));
            return auditoria;
        } catch (Exception e) {
            return new Auditoria(idUsuario, "LOGIN", "Usuario", null, ipOrigen);
        }
    }

    public static Auditoria createAuditoriaLogout(Integer idUsuario, String ipOrigen) {
        java.util.Map<String, Object> nuevos = new java.util.HashMap<>();
        nuevos.put("fecha_logout", LocalDateTime.now().toString());
        
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Auditoria auditoria = new Auditoria(idUsuario, "LOGOUT", "Usuario", null, ipOrigen);
            auditoria.setDatosNuevos(mapper.writeValueAsString(nuevos));
            return auditoria;
        } catch (Exception e) {
            return new Auditoria(idUsuario, "LOGOUT", "Usuario", null, ipOrigen);
        }
    }

    public static Auditoria createAuditoriaDeshabilitacion(Integer idUsuario, String entidad,
                                                          Integer idRegistro, String motivo,
                                                          String ipOrigen) {
        java.util.Map<String, Object> nuevos = new java.util.HashMap<>();
        nuevos.put("motivo", motivo);
        nuevos.put("fecha_deshabilitacion", LocalDateTime.now().toString());
        
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Auditoria auditoria = new Auditoria(idUsuario, "DISABLE", entidad, idRegistro, ipOrigen);
            auditoria.setDatosNuevos(mapper.writeValueAsString(nuevos));
            return auditoria;
        } catch (Exception e) {
            return new Auditoria(idUsuario, "DISABLE", entidad, idRegistro, ipOrigen);
        }
    }

    public static Auditoria createAuditoriaHabilitacion(Integer idUsuario, String entidad,
                                                       Integer idRegistro, String ipOrigen) {
        java.util.Map<String, Object> nuevos = new java.util.HashMap<>();
        nuevos.put("fecha_habilitacion", LocalDateTime.now().toString());
        
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Auditoria auditoria = new Auditoria(idUsuario, "ENABLE", entidad, idRegistro, ipOrigen);
            auditoria.setDatosNuevos(mapper.writeValueAsString(nuevos));
            return auditoria;
        } catch (Exception e) {
            return new Auditoria(idUsuario, "ENABLE", entidad, idRegistro, ipOrigen);
        }
    }
}