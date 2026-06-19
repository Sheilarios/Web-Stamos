package com.stamos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Entidad LogAccesoCliente.
 * 
 * <p>Esta entidad registra los accesos de los clientes al portal web,
 * permitiendo el seguimiento de actividad y auditoría de conexiones.</p>
 * 
 * <p>Implementa los requerimientos:</p>
 * <ul>
 *   <li>RNF-02: Autenticación obligatoria mediante usuario y contraseña</li>
 *   <li>RNF-04: Control de acceso basado en roles</li>
 *   <li>Portal del Cliente: Registro de accesos y permisos</li>
 * </ul>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
@Entity
@Table(name = "LogAccesoCliente",
       indexes = {
           @Index(name = "idx_log_cliente", columnList = "id_cliente"),
           @Index(name = "idx_log_fecha", columnList = "fecha_acceso"),
           @Index(name = "idx_log_ip", columnList = "ip_origen")
       })
public class LogAccesoCliente {

    // ============================================
    // ATRIBUTOS PRINCIPALES
    // ============================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_log")
    private Integer idLog;

    @NotNull(message = "El cliente es obligatorio")
    @Column(name = "id_cliente", nullable = false)
    private Integer idCliente;

    @Size(max = 45, message = "La IP no puede exceder los 45 caracteres")
    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    @Size(max = 200, message = "El navegador no puede exceder los 200 caracteres")
    @Column(name = "navegador", length = 200)
    private String navegador;

    @Column(name = "fecha_acceso", nullable = false)
    private LocalDateTime fechaAcceso;

    // ============================================
    // CAMPOS ADICIONALES (NO EN BD)
    // ============================================

    @Transient
    private String nombreCliente;

    @Transient
    private String emailCliente;

    @Transient
    private String tipoCliente;

    @Transient
    private String navegadorSimplificado;

    @Transient
    private String tipoDispositivo;

    @Transient
    private String sistemaOperativo;

    // ============================================
    // CONSTRUCTORES
    // ============================================

    public LogAccesoCliente() {
        this.fechaAcceso = LocalDateTime.now();
    }

    public LogAccesoCliente(Integer idCliente, String ipOrigen, String navegador) {
        this();
        this.idCliente = idCliente;
        this.ipOrigen = ipOrigen;
        this.navegador = navegador;
        parsearNavegador();
    }

    public LogAccesoCliente(Integer idCliente, String ipOrigen, String navegador, LocalDateTime fechaAcceso) {
        this(idCliente, ipOrigen, navegador);
        this.fechaAcceso = fechaAcceso != null ? fechaAcceso : LocalDateTime.now();
    }

    // ============================================
    // GETTERS Y SETTERS
    // ============================================

    public Integer getIdLog() {
        return idLog;
    }

    public void setIdLog(Integer idLog) {
        this.idLog = idLog;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getIpOrigen() {
        return ipOrigen;
    }

    public void setIpOrigen(String ipOrigen) {
        this.ipOrigen = ipOrigen;
    }

    public String getNavegador() {
        return navegador;
    }

    public void setNavegador(String navegador) {
        this.navegador = navegador;
        parsearNavegador();
    }

    public LocalDateTime getFechaAcceso() {
        return fechaAcceso;
    }

    public void setFechaAcceso(LocalDateTime fechaAcceso) {
        this.fechaAcceso = fechaAcceso;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public void setEmailCliente(String emailCliente) {
        this.emailCliente = emailCliente;
    }

    public String getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public String getNavegadorSimplificado() {
        return navegadorSimplificado;
    }

    public void setNavegadorSimplificado(String navegadorSimplificado) {
        this.navegadorSimplificado = navegadorSimplificado;
    }

    public String getTipoDispositivo() {
        return tipoDispositivo;
    }

    public void setTipoDispositivo(String tipoDispositivo) {
        this.tipoDispositivo = tipoDispositivo;
    }

    public String getSistemaOperativo() {
        return sistemaOperativo;
    }

    public void setSistemaOperativo(String sistemaOperativo) {
        this.sistemaOperativo = sistemaOperativo;
    }

    // ============================================
    // MÉTODOS DE NEGOCIO
    // ============================================

    /**
     * Parsea el User-Agent para obtener información del navegador y dispositivo.
     */
    private void parsearNavegador() {
        if (navegador == null || navegador.isEmpty()) {
            this.navegadorSimplificado = "Desconocido";
            this.tipoDispositivo = "Desconocido";
            this.sistemaOperativo = "Desconocido";
            return;
        }

        String userAgent = navegador.toLowerCase();

        // Detectar navegador
        if (userAgent.contains("chrome") && !userAgent.contains("edg")) {
            this.navegadorSimplificado = "Chrome";
        } else if (userAgent.contains("firefox")) {
            this.navegadorSimplificado = "Firefox";
        } else if (userAgent.contains("safari") && !userAgent.contains("chrome")) {
            this.navegadorSimplificado = "Safari";
        } else if (userAgent.contains("edg")) {
            this.navegadorSimplificado = "Edge";
        } else if (userAgent.contains("opera") || userAgent.contains("opr")) {
            this.navegadorSimplificado = "Opera";
        } else if (userAgent.contains("brave")) {
            this.navegadorSimplificado = "Brave";
        } else if (userAgent.contains("msie") || userAgent.contains("trident")) {
            this.navegadorSimplificado = "Internet Explorer";
        } else {
            this.navegadorSimplificado = "Otro";
        }

        // Detectar dispositivo
        if (userAgent.contains("mobile") || userAgent.contains("android") || userAgent.contains("iphone")) {
            this.tipoDispositivo = "Móvil";
        } else if (userAgent.contains("tablet") || userAgent.contains("ipad")) {
            this.tipoDispositivo = "Tablet";
        } else {
            this.tipoDispositivo = "Escritorio";
        }

        // Detectar sistema operativo
        if (userAgent.contains("windows")) {
            this.sistemaOperativo = "Windows";
        } else if (userAgent.contains("mac os")) {
            this.sistemaOperativo = "macOS";
        } else if (userAgent.contains("linux") || userAgent.contains("ubuntu")) {
            this.sistemaOperativo = "Linux";
        } else if (userAgent.contains("android")) {
            this.sistemaOperativo = "Android";
        } else if (userAgent.contains("iphone") || userAgent.contains("ios")) {
            this.sistemaOperativo = "iOS";
        } else {
            this.sistemaOperativo = "Otro";
        }
    }

    /**
     * Verifica si el acceso es desde un dispositivo móvil.
     * 
     * @return true si es móvil, false en caso contrario
     */
    public boolean esDispositivoMovil() {
        return "Móvil".equals(this.tipoDispositivo);
    }

    /**
     * Verifica si el acceso es desde una tablet.
     * 
     * @return true si es tablet, false en caso contrario
     */
    public boolean esTablet() {
        return "Tablet".equals(this.tipoDispositivo);
    }

    /**
     * Verifica si el acceso es desde un escritorio.
     * 
     * @return true si es escritorio, false en caso contrario
     */
    public boolean esEscritorio() {
        return "Escritorio".equals(this.tipoDispositivo);
    }

    /**
     * Verifica si el acceso es de un navegador moderno.
     * 
     * @return true si es moderno, false en caso contrario
     */
    public boolean esNavegadorModerno() {
        return !"Internet Explorer".equals(this.navegadorSimplificado) && 
               !"Otro".equals(this.navegadorSimplificado);
    }

    /**
     * Obtiene el nombre del cliente para mostrar.
     * 
     * @return Nombre del cliente
     */
    public String getClienteDisplay() {
        if (nombreCliente != null && !nombreCliente.isEmpty()) {
            return nombreCliente;
        }
        if (emailCliente != null && !emailCliente.isEmpty()) {
            return emailCliente;
        }
        return "Cliente ID: " + idCliente;
    }

    /**
     * Obtiene el icono según el tipo de dispositivo.
     * 
     * @return Ícono CSS
     */
    public String getDispositivoIcono() {
        if (tipoDispositivo == null) return "ti ti-device-laptop";
        
        switch (tipoDispositivo) {
            case "Móvil":
                return "ti ti-device-mobile";
            case "Tablet":
                return "ti ti-device-tablet";
            case "Escritorio":
                return "ti ti-device-laptop";
            default:
                return "ti ti-device-laptop";
        }
    }

    /**
     * Obtiene el color según el tipo de dispositivo.
     * 
     * @return Color en hexadecimal
     */
    public String getDispositivoColor() {
        if (tipoDispositivo == null) return "#6B7280";
        
        switch (tipoDispositivo) {
            case "Móvil":
                return "#3B82F6";
            case "Tablet":
                return "#8B5CF6";
            case "Escritorio":
                return "#10B981";
            default:
                return "#6B7280";
        }
    }

    /**
     * Obtiene el icono según el navegador.
     * 
     * @return Ícono CSS
     */
    public String getNavegadorIcono() {
        if (navegadorSimplificado == null) return "ti ti-world";
        
        switch (navegadorSimplificado) {
            case "Chrome":
                return "ti ti-brand-chrome";
            case "Firefox":
                return "ti ti-brand-firefox";
            case "Safari":
                return "ti ti-brand-safari";
            case "Edge":
                return "ti ti-brand-edge";
            case "Opera":
                return "ti ti-brand-opera";
            case "Brave":
                return "ti ti-brand-brave";
            default:
                return "ti ti-world";
        }
    }

    /**
     * Obtiene el color según el navegador.
     * 
     * @return Color en hexadecimal
     */
    public String getNavegadorColor() {
        if (navegadorSimplificado == null) return "#6B7280";
        
        switch (navegadorSimplificado) {
            case "Chrome":
                return "#4285F4";
            case "Firefox":
                return "#FF7139";
            case "Safari":
                return "#1A74E6";
            case "Edge":
                return "#0078D7";
            case "Opera":
                return "#FF1B2D";
            case "Brave":
                return "#FB542B";
            default:
                return "#6B7280";
        }
    }

    // ============================================
    // MÉTODOS DE FORMATO PARA LA VISTA
    // ============================================

    /**
     * Obtiene la fecha de acceso formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaAccesoFormateada() {
        if (fechaAcceso == null) return "-";
        return fechaAcceso.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    /**
     * Obtiene la fecha de acceso formateada (solo fecha).
     * 
     * @return Fecha formateada
     */
    public String getFechaAccesoFormateadaSoloFecha() {
        if (fechaAcceso == null) return "-";
        return fechaAcceso.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Obtiene la fecha de acceso formateada (solo hora).
     * 
     * @return Hora formateada
     */
    public String getFechaAccesoFormateadaSoloHora() {
        if (fechaAcceso == null) return "-";
        return fechaAcceso.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    /**
     * Obtiene la IP de origen con formato.
     * 
     * @return IP formateada
     */
    public String getIpFormateada() {
        if (ipOrigen == null || ipOrigen.isEmpty()) {
            return "IP desconocida";
        }
        return ipOrigen;
    }

    /**
     * Obtiene el navegador completo.
     * 
     * @return Navegador completo
     */
    public String getNavegadorCompleto() {
        if (navegador == null || navegador.isEmpty()) {
            return "Navegador desconocido";
        }
        return navegador;
    }

    /**
     * Obtiene el navegador con ícono.
     * 
     * @return Navegador con ícono
     */
    public String getNavegadorConIcono() {
        return "<i class='" + getNavegadorIcono() + "'></i> " + navegadorSimplificado;
    }

    /**
     * Obtiene el dispositivo con ícono.
     * 
     * @return Dispositivo con ícono
     */
    public String getDispositivoConIcono() {
        return "<i class='" + getDispositivoIcono() + "'></i> " + tipoDispositivo;
    }

    /**
     * Obtiene el resumen del acceso.
     * 
     * @return Resumen
     */
    public String getResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cliente: ").append(getClienteDisplay());
        sb.append(" | ").append(getFechaAccesoFormateada());
        sb.append(" | ").append(navegadorSimplificado).append(" en ").append(tipoDispositivo);
        if (ipOrigen != null && !ipOrigen.isEmpty()) {
            sb.append(" | IP: ").append(ipOrigen);
        }
        return sb.toString();
    }

    /**
     * Obtiene la clase CSS para el badge del tipo de dispositivo.
     * 
     * @return Clase CSS
     */
    public String getDispositivoBadgeClass() {
        if (tipoDispositivo == null) return "badge-secondary";
        
        switch (tipoDispositivo) {
            case "Móvil":
                return "badge-info";
            case "Tablet":
                return "badge-warning";
            case "Escritorio":
                return "badge-success";
            default:
                return "badge-secondary";
        }
    }

    /**
     * Obtiene la clase CSS para el badge del navegador.
     * 
     * @return Clase CSS
     */
    public String getNavegadorBadgeClass() {
        if (navegadorSimplificado == null) return "badge-secondary";
        
        switch (navegadorSimplificado) {
            case "Chrome":
                return "badge-primary";
            case "Firefox":
                return "badge-danger";
            case "Safari":
                return "badge-info";
            case "Edge":
                return "badge-primary";
            case "Opera":
                return "badge-danger";
            case "Brave":
                return "badge-warning";
            default:
                return "badge-secondary";
        }
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
        sb.append("\"idLog\":").append(idLog).append(",");
        sb.append("\"idCliente\":").append(idCliente).append(",");
        sb.append("\"ipOrigen\":\"").append(ipOrigen != null ? ipOrigen : "").append("\",");
        sb.append("\"fechaAcceso\":\"").append(getFechaAccesoFormateada()).append("\",");
        sb.append("\"navegador\":\"").append(navegadorSimplificado != null ? navegadorSimplificado : "").append("\",");
        sb.append("\"dispositivo\":\"").append(tipoDispositivo != null ? tipoDispositivo : "").append("\"");
        if (nombreCliente != null) {
            sb.append(",\"cliente\":\"").append(nombreCliente).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convierte a JSON con todos los campos.
     * 
     * @return JSON completo
     */
    public String toJsonCompleto() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"idLog\":").append(idLog).append(",");
        sb.append("\"idCliente\":").append(idCliente).append(",");
        sb.append("\"nombreCliente\":\"").append(nombreCliente != null ? nombreCliente : "").append("\",");
        sb.append("\"emailCliente\":\"").append(emailCliente != null ? emailCliente : "").append("\",");
        sb.append("\"ipOrigen\":\"").append(ipOrigen != null ? ipOrigen : "").append("\",");
        sb.append("\"fechaAcceso\":\"").append(getFechaAccesoFormateada()).append("\",");
        sb.append("\"navegador\":\"").append(navegador != null ? navegador : "").append("\",");
        sb.append("\"navegadorSimplificado\":\"").append(navegadorSimplificado != null ? navegadorSimplificado : "").append("\",");
        sb.append("\"tipoDispositivo\":\"").append(tipoDispositivo != null ? tipoDispositivo : "").append("\",");
        sb.append("\"sistemaOperativo\":\"").append(sistemaOperativo != null ? sistemaOperativo : "").append("\",");
        sb.append("\"esMovil\":").append(esDispositivoMovil());
        sb.append("}");
        return sb.toString();
    }

    // ============================================
    // MÉTODOS OVERRIDE
    // ============================================

    @Override
    public String toString() {
        return "LogAccesoCliente{" +
                "idLog=" + idLog +
                ", idCliente=" + idCliente +
                ", ipOrigen='" + ipOrigen + '\'' +
                ", fechaAcceso=" + fechaAcceso +
                ", navegadorSimplificado='" + navegadorSimplificado + '\'' +
                ", tipoDispositivo='" + tipoDispositivo + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogAccesoCliente that = (LogAccesoCliente) o;
        return idLog != null && idLog.equals(that.idLog);
    }

    @Override
    public int hashCode() {
        return idLog != null ? idLog.hashCode() : 0;
    }

    // ============================================
    // MÉTODOS ESTÁTICOS DE FÁBRICA
    // ============================================

    /**
     * Crea un registro de acceso desde una entidad Cliente.
     * 
     * @param idCliente ID del cliente
     * @param ipOrigen IP de origen
     * @param userAgent User-Agent del navegador
     * @return Log de acceso
     */
    public static LogAccesoCliente fromCliente(Integer idCliente, String ipOrigen, String userAgent) {
        return new LogAccesoCliente(idCliente, ipOrigen, userAgent);
    }

    /**
     * Crea un registro de acceso con fecha específica.
     * 
     * @param idCliente ID del cliente
     * @param ipOrigen IP de origen
     * @param userAgent User-Agent del navegador
     * @param fechaAcceso Fecha de acceso
     * @return Log de acceso
     */
    public static LogAccesoCliente fromClienteConFecha(Integer idCliente, String ipOrigen, 
                                                       String userAgent, LocalDateTime fechaAcceso) {
        return new LogAccesoCliente(idCliente, ipOrigen, userAgent, fechaAcceso);
    }

    /**
     * Convierte el log a un DTO.
     * 
     * @return DTO del log
     */
    public LogAccesoClienteDTO toDTO() {
        LogAccesoClienteDTO dto = new LogAccesoClienteDTO();
        dto.setIdLog(this.idLog);
        dto.setIdCliente(this.idCliente);
        dto.setNombreCliente(this.nombreCliente);
        dto.setEmailCliente(this.emailCliente);
        dto.setTipoCliente(this.tipoCliente);
        dto.setIpOrigen(this.ipOrigen);
        dto.setNavegador(this.navegador);
        dto.setNavegadorSimplificado(this.navegadorSimplificado);
        dto.setTipoDispositivo(this.tipoDispositivo);
        dto.setSistemaOperativo(this.sistemaOperativo);
        dto.setFechaAcceso(this.fechaAcceso);
        return dto;
    }

    // ============================================
    // CLASE INTERNA DTO
    // ============================================

    /**
     * DTO para LogAccesoCliente.
     */
    public static class LogAccesoClienteDTO {
        private Integer idLog;
        private Integer idCliente;
        private String nombreCliente;
        private String emailCliente;
        private String tipoCliente;
        private String ipOrigen;
        private String navegador;
        private String navegadorSimplificado;
        private String tipoDispositivo;
        private String sistemaOperativo;
        private LocalDateTime fechaAcceso;

        // Constructores
        public LogAccesoClienteDTO() {}

        public LogAccesoClienteDTO(Integer idLog, Integer idCliente, String ipOrigen, 
                                  String navegadorSimplificado, String tipoDispositivo, 
                                  LocalDateTime fechaAcceso) {
            this.idLog = idLog;
            this.idCliente = idCliente;
            this.ipOrigen = ipOrigen;
            this.navegadorSimplificado = navegadorSimplificado;
            this.tipoDispositivo = tipoDispositivo;
            this.fechaAcceso = fechaAcceso;
        }

        // Getters y Setters
        public Integer getIdLog() { return idLog; }
        public void setIdLog(Integer idLog) { this.idLog = idLog; }

        public Integer getIdCliente() { return idCliente; }
        public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }

        public String getNombreCliente() { return nombreCliente; }
        public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

        public String getEmailCliente() { return emailCliente; }
        public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }

        public String getTipoCliente() { return tipoCliente; }
        public void setTipoCliente(String tipoCliente) { this.tipoCliente = tipoCliente; }

        public String getIpOrigen() { return ipOrigen; }
        public void setIpOrigen(String ipOrigen) { this.ipOrigen = ipOrigen; }

        public String getNavegador() { return navegador; }
        public void setNavegador(String navegador) { this.navegador = navegador; }

        public String getNavegadorSimplificado() { return navegadorSimplificado; }
        public void setNavegadorSimplificado(String navegadorSimplificado) { this.navegadorSimplificado = navegadorSimplificado; }

        public String getTipoDispositivo() { return tipoDispositivo; }
        public void setTipoDispositivo(String tipoDispositivo) { this.tipoDispositivo = tipoDispositivo; }

        public String getSistemaOperativo() { return sistemaOperativo; }
        public void setSistemaOperativo(String sistemaOperativo) { this.sistemaOperativo = sistemaOperativo; }

        public LocalDateTime getFechaAcceso() { return fechaAcceso; }
        public void setFechaAcceso(LocalDateTime fechaAcceso) { this.fechaAcceso = fechaAcceso; }

        // Métodos de formato
        public String getFechaAccesoFormateada() {
            if (fechaAcceso == null) return "-";
            return fechaAcceso.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        }

        public String getDispositivoIcono() {
            if (tipoDispositivo == null) return "ti ti-device-laptop";
            switch (tipoDispositivo) {
                case "Móvil": return "ti ti-device-mobile";
                case "Tablet": return "ti ti-device-tablet";
                case "Escritorio": return "ti ti-device-laptop";
                default: return "ti ti-device-laptop";
            }
        }

        public String getNavegadorIcono() {
            if (navegadorSimplificado == null) return "ti ti-world";
            switch (navegadorSimplificado) {
                case "Chrome": return "ti ti-brand-chrome";
                case "Firefox": return "ti ti-brand-firefox";
                case "Safari": return "ti ti-brand-safari";
                case "Edge": return "ti ti-brand-edge";
                default: return "ti ti-world";
            }
        }

        public String getDispositivoBadgeClass() {
            if (tipoDispositivo == null) return "badge-secondary";
            switch (tipoDispositivo) {
                case "Móvil": return "badge-info";
                case "Tablet": return "badge-warning";
                case "Escritorio": return "badge-success";
                default: return "badge-secondary";
            }
        }

        public String getResumen() {
            StringBuilder sb = new StringBuilder();
            sb.append("Cliente: ").append(nombreCliente != null ? nombreCliente : "ID: " + idCliente);
            sb.append(" | ").append(getFechaAccesoFormateada());
            sb.append(" | ").append(navegadorSimplificado).append(" en ").append(tipoDispositivo);
            if (ipOrigen != null && !ipOrigen.isEmpty()) {
                sb.append(" | IP: ").append(ipOrigen);
            }
            return sb.toString();
        }

        @Override
        public String toString() {
            return "LogAccesoClienteDTO{" +
                    "idLog=" + idLog +
                    ", idCliente=" + idCliente +
                    ", nombreCliente='" + nombreCliente + '\'' +
                    ", ipOrigen='" + ipOrigen + '\'' +
                    ", navegadorSimplificado='" + navegadorSimplificado + '\'' +
                    ", tipoDispositivo='" + tipoDispositivo + '\'' +
                    ", fechaAcceso=" + fechaAcceso +
                    '}';
        }
    }
}