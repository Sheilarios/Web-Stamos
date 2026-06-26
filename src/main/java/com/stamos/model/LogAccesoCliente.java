package com.stamos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "LogAccesoCliente",
       indexes = {
           @Index(name = "idx_log_cliente", columnList = "id_cliente"),
           @Index(name = "idx_log_fecha", columnList = "fecha_acceso"),
           @Index(name = "idx_log_ip", columnList = "ip_origen")
       })
public class LogAccesoCliente {

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

    private void parsearNavegador() {
        if (navegador == null || navegador.isEmpty()) {
            this.navegadorSimplificado = "Desconocido";
            this.tipoDispositivo = "Desconocido";
            this.sistemaOperativo = "Desconocido";
            return;
        }

        String userAgent = navegador.toLowerCase();

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

        if (userAgent.contains("mobile") || userAgent.contains("android") || userAgent.contains("iphone")) {
            this.tipoDispositivo = "Móvil";
        } else if (userAgent.contains("tablet") || userAgent.contains("ipad")) {
            this.tipoDispositivo = "Tablet";
        } else {
            this.tipoDispositivo = "Escritorio";
        }

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

    public boolean esDispositivoMovil() {
        return "Móvil".equals(this.tipoDispositivo);
    }

    public boolean esTablet() {
        return "Tablet".equals(this.tipoDispositivo);
    }

    public boolean esEscritorio() {
        return "Escritorio".equals(this.tipoDispositivo);
    }

    public boolean esNavegadorModerno() {
        return !"Internet Explorer".equals(this.navegadorSimplificado) && 
               !"Otro".equals(this.navegadorSimplificado);
    }

    public String getClienteDisplay() {
        if (nombreCliente != null && !nombreCliente.isEmpty()) {
            return nombreCliente;
        }
        if (emailCliente != null && !emailCliente.isEmpty()) {
            return emailCliente;
        }
        return "Cliente ID: " + idCliente;
    }

    public String getDispositivoIcono() {
        if (tipoDispositivo == null) return "ti ti-device-laptop";
        
        return switch (tipoDispositivo) {
            case "Móvil" -> "ti ti-device-mobile";
            case "Tablet" -> "ti ti-device-tablet";
            case "Escritorio" -> "ti ti-device-laptop";
            default -> "ti ti-device-laptop";
        };
    }

    public String getDispositivoColor() {
        if (tipoDispositivo == null) return "#6B7280";
        
        return switch (tipoDispositivo) {
            case "Móvil" -> "#3B82F6";
            case "Tablet" -> "#8B5CF6";
            case "Escritorio" -> "#10B981";
            default -> "#6B7280";
        };
    }

    public String getNavegadorIcono() {
        if (navegadorSimplificado == null) return "ti ti-world";
        
        return switch (navegadorSimplificado) {
            case "Chrome" -> "ti ti-brand-chrome";
            case "Firefox" -> "ti ti-brand-firefox";
            case "Safari" -> "ti ti-brand-safari";
            case "Edge" -> "ti ti-brand-edge";
            case "Opera" -> "ti ti-brand-opera";
            case "Brave" -> "ti ti-brand-brave";
            default -> "ti ti-world";
        };
    }

    public String getNavegadorColor() {
        if (navegadorSimplificado == null) return "#6B7280";
        
        return switch (navegadorSimplificado) {
            case "Chrome" -> "#4285F4";
            case "Firefox" -> "#FF7139";
            case "Safari" -> "#1A74E6";
            case "Edge" -> "#0078D7";
            case "Opera" -> "#FF1B2D";
            case "Brave" -> "#FB542B";
            default -> "#6B7280";
        };
    }

    public String getFechaAccesoFormateada() {
        if (fechaAcceso == null) return "-";
        return fechaAcceso.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public String getFechaAccesoFormateadaSoloFecha() {
        if (fechaAcceso == null) return "-";
        return fechaAcceso.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getFechaAccesoFormateadaSoloHora() {
        if (fechaAcceso == null) return "-";
        return fechaAcceso.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public String getIpFormateada() {
        if (ipOrigen == null || ipOrigen.isEmpty()) {
            return "IP desconocida";
        }
        return ipOrigen;
    }

    public String getNavegadorCompleto() {
        if (navegador == null || navegador.isEmpty()) {
            return "Navegador desconocido";
        }
        return navegador;
    }

    public String getNavegadorConIcono() {
        return "<i class='" + getNavegadorIcono() + "'></i> " + navegadorSimplificado;
    }

    public String getDispositivoConIcono() {
        return "<i class='" + getDispositivoIcono() + "'></i> " + tipoDispositivo;
    }

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

    public String getDispositivoBadgeClass() {
        if (tipoDispositivo == null) return "badge-secondary";
        
        return switch (tipoDispositivo) {
            case "Móvil" -> "badge-info";
            case "Tablet" -> "badge-warning";
            case "Escritorio" -> "badge-success";
            default -> "badge-secondary";
        };
    }

    public String getNavegadorBadgeClass() {
        if (navegadorSimplificado == null) return "badge-secondary";
        
        return switch (navegadorSimplificado) {
            case "Chrome" -> "badge-primary";
            case "Firefox" -> "badge-danger";
            case "Safari" -> "badge-info";
            case "Edge" -> "badge-primary";
            case "Opera" -> "badge-danger";
            case "Brave" -> "badge-warning";
            default -> "badge-secondary";
        };
    }

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

    public static LogAccesoCliente fromCliente(Integer idCliente, String ipOrigen, String userAgent) {
        return new LogAccesoCliente(idCliente, ipOrigen, userAgent);
    }

    public static LogAccesoCliente fromClienteConFecha(Integer idCliente, String ipOrigen, 
                                                       String userAgent, LocalDateTime fechaAcceso) {
        return new LogAccesoCliente(idCliente, ipOrigen, userAgent, fechaAcceso);
    }

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

        public String getFechaAccesoFormateada() {
            if (fechaAcceso == null) return "-";
            return fechaAcceso.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        }

        public String getDispositivoIcono() {
            if (tipoDispositivo == null) return "ti ti-device-laptop";
            return switch (tipoDispositivo) {
                case "Móvil" -> "ti ti-device-mobile";
                case "Tablet" -> "ti ti-device-tablet";
                case "Escritorio" -> "ti ti-device-laptop";
                default -> "ti ti-device-laptop";
            };
        }

        public String getNavegadorIcono() {
            if (navegadorSimplificado == null) return "ti ti-world";
            return switch (navegadorSimplificado) {
                case "Chrome" -> "ti ti-brand-chrome";
                case "Firefox" -> "ti ti-brand-firefox";
                case "Safari" -> "ti ti-brand-safari";
                case "Edge" -> "ti ti-brand-edge";
                default -> "ti ti-world";
            };
        }

        public String getDispositivoBadgeClass() {
            if (tipoDispositivo == null) return "badge-secondary";
            return switch (tipoDispositivo) {
                case "Móvil" -> "badge-info";
                case "Tablet" -> "badge-warning";
                case "Escritorio" -> "badge-success";
                default -> "badge-secondary";
            };
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