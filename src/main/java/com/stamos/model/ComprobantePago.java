package com.stamos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "ComprobantePago",
       indexes = {
           @Index(name = "idx_comprobante_pago", columnList = "id_pago"),
           @Index(name = "idx_comprobante_numero", columnList = "numero_comprobante"),
           @Index(name = "idx_comprobante_validado", columnList = "validado"),
           @Index(name = "idx_comprobante_fecha", columnList = "fecha_validacion"),
           @Index(name = "idx_comprobante_usuario", columnList = "id_usuario_validador")
       })
public class ComprobantePago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comprobante")
    private Integer idComprobante;

    @NotNull(message = "El pago es obligatorio")
    @Column(name = "id_pago", nullable = false)
    private Integer idPago;

    @NotBlank(message = "El tipo de comprobante es obligatorio")
    @Column(name = "tipo_comprobante", nullable = false)
    private String tipoComprobante; // 'Boleta', 'Factura', 'Ticket', 'Otro'

    @NotBlank(message = "El número de comprobante es obligatorio")
    @Size(max = 50, message = "El número de comprobante no puede exceder los 50 caracteres")
    @Column(name = "numero_comprobante", nullable = false, length = 50)
    private String numeroComprobante;

    @Size(max = 255, message = "La URL del archivo no puede exceder los 255 caracteres")
    @Column(name = "archivo_url", length = 255)
    private String archivoUrl;

    @Column(name = "validado", nullable = false)
    private Boolean validado = false;

    @Column(name = "id_usuario_validador")
    private Integer idUsuarioValidador;

    @Column(name = "fecha_validacion")
    private LocalDateTime fechaValidacion;


    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Transient
    private String nombreUsuarioValidador;

    @Transient
    private String emailUsuarioValidador;

    @Transient
    private String metodoPago;

    @Transient
    private String nombreProyecto;

    @Transient
    private String nombreCliente;

    public ComprobantePago() {
        this.validado = false;
        this.fechaRegistro = LocalDateTime.now();
    }

    public ComprobantePago(Integer idPago, String tipoComprobante, String numeroComprobante) {
        this();
        this.idPago = idPago;
        this.tipoComprobante = tipoComprobante;
        this.numeroComprobante = numeroComprobante;
    }

    public ComprobantePago(Integer idPago, String tipoComprobante, String numeroComprobante, 
                          String archivoUrl) {
        this(idPago, tipoComprobante, numeroComprobante);
        this.archivoUrl = archivoUrl;
    }

    public Integer getIdComprobante() {
        return idComprobante;
    }

    public void setIdComprobante(Integer idComprobante) {
        this.idComprobante = idComprobante;
    }

    public Integer getIdPago() {
        return idPago;
    }

    public void setIdPago(Integer idPago) {
        this.idPago = idPago;
    }

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public String getNumeroComprobante() {
        return numeroComprobante;
    }

    public void setNumeroComprobante(String numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }

    public String getArchivoUrl() {
        return archivoUrl;
    }

    public void setArchivoUrl(String archivoUrl) {
        this.archivoUrl = archivoUrl;
    }

    public Boolean getValidado() {
        return validado;
    }

    public void setValidado(Boolean validado) {
        this.validado = validado;
    }

    public Integer getIdUsuarioValidador() {
        return idUsuarioValidador;
    }

    public void setIdUsuarioValidador(Integer idUsuarioValidador) {
        this.idUsuarioValidador = idUsuarioValidador;
    }

    public LocalDateTime getFechaValidacion() {
        return fechaValidacion;
    }

    public void setFechaValidacion(LocalDateTime fechaValidacion) {
        this.fechaValidacion = fechaValidacion;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getNombreUsuarioValidador() {
        return nombreUsuarioValidador;
    }

    public void setNombreUsuarioValidador(String nombreUsuarioValidador) {
        this.nombreUsuarioValidador = nombreUsuarioValidador;
    }

    public String getEmailUsuarioValidador() {
        return emailUsuarioValidador;
    }

    public void setEmailUsuarioValidador(String emailUsuarioValidador) {
        this.emailUsuarioValidador = emailUsuarioValidador;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getNombreProyecto() {
        return nombreProyecto;
    }

    public void setNombreProyecto(String nombreProyecto) {
        this.nombreProyecto = nombreProyecto;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public boolean isValidado() {
        return validado != null && validado;
    }

    public void validar(Integer idUsuario) {
        this.validado = true;
        this.idUsuarioValidador = idUsuario;
        this.fechaValidacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void rechazar(Integer idUsuario) {
        this.validado = false;
        this.idUsuarioValidador = idUsuario;
        this.fechaValidacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    public boolean tieneArchivo() {
        return archivoUrl != null && !archivoUrl.trim().isEmpty();
    }

    public boolean esFactura() {
        return "Factura".equals(this.tipoComprobante);
    }

    public boolean esBoleta() {
        return "Boleta".equals(this.tipoComprobante);
    }

    public boolean esTicket() {
        return "Ticket".equals(this.tipoComprobante);
    }

    public boolean esOtro() {
        return "Otro".equals(this.tipoComprobante);
    }

    public String getEstado() {
        return isValidado() ? "Validado" : "Pendiente de validación";
    }

    public String getTipoLegible() {
        if (tipoComprobante == null) return "No especificado";
        return tipoComprobante;
    }

    public String getEstadoLegible() {
        return isValidado() ? "Validado" : "Pendiente de validación";
    }

    public String getEstadoBadgeClass() {
        return isValidado() ? "badge-success" : "badge-warning";
    }

    public String getEstadoIcono() {
        return isValidado() ? "ti ti-check-circle" : "ti ti-clock";
    }

    public String getEstadoColor() {
        return isValidado() ? "#10B981" : "#F59E0B";
    }

    public String getTipoBadgeClass() {
        if (tipoComprobante == null) return "badge-secondary";
        
        return switch (tipoComprobante) {
            case "Factura" -> "badge-primary";
            case "Boleta" -> "badge-info";
            case "Ticket" -> "badge-warning";
            case "Otro" -> "badge-secondary";
            default -> "badge-secondary";
        };
    }

    public String getTipoIcono() {
        if (tipoComprobante == null) return "ti ti-file";
        
        return switch (tipoComprobante) {
            case "Factura" -> "ti ti-file-invoice";
            case "Boleta" -> "ti ti-receipt";
            case "Ticket" -> "ti ti-ticket";
            case "Otro" -> "ti ti-file";
            default -> "ti ti-file";
        };
    }

    public String getFechaRegistroFormateada() {
        if (fechaRegistro == null) return "-";
        return fechaRegistro.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getFechaRegistroSoloFecha() {
        if (fechaRegistro == null) return "-";
        return fechaRegistro.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getFechaValidacionFormateada() {
        if (fechaValidacion == null) return "-";
        return fechaValidacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getFechaValidacionSoloFecha() {
        if (fechaValidacion == null) return "-";
        return fechaValidacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getValidadorLegible() {
        if (nombreUsuarioValidador != null && !nombreUsuarioValidador.isEmpty()) {
            return nombreUsuarioValidador;
        }
        if (emailUsuarioValidador != null && !emailUsuarioValidador.isEmpty()) {
            return emailUsuarioValidador;
        }
        if (idUsuarioValidador != null) {
            return "Usuario ID: " + idUsuarioValidador;
        }
        return "Sin validar";
    }

    public String getNombreArchivo() {
        if (archivoUrl == null || archivoUrl.isEmpty()) {
            return null;
        }
        String[] partes = archivoUrl.split("/");
        return partes[partes.length - 1];
    }


    public String getResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append(tipoComprobante).append(" N° ").append(numeroComprobante);
        if (nombreProyecto != null) {
            sb.append(" - ").append(nombreProyecto);
        }
        sb.append(" (").append(getEstadoLegible()).append(")");
        return sb.toString();
    }


    public boolean tieneNumeroValido() {
        return numeroComprobante != null && !numeroComprobante.trim().isEmpty();
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"idComprobante\":").append(idComprobante).append(",");
        sb.append("\"idPago\":").append(idPago).append(",");
        sb.append("\"tipo\":\"").append(tipoComprobante != null ? tipoComprobante : "").append("\",");
        sb.append("\"numero\":\"").append(numeroComprobante != null ? numeroComprobante : "").append("\",");
        sb.append("\"validado\":").append(isValidado()).append(",");
        sb.append("\"fechaRegistro\":\"").append(getFechaRegistroFormateada()).append("\"");
        if (fechaValidacion != null) {
            sb.append(",\"fechaValidacion\":\"").append(getFechaValidacionFormateada()).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "ComprobantePago{" +
                "idComprobante=" + idComprobante +
                ", idPago=" + idPago +
                ", tipoComprobante='" + tipoComprobante + '\'' +
                ", numeroComprobante='" + numeroComprobante + '\'' +
                ", validado=" + validado +
                ", fechaRegistro=" + fechaRegistro +
                ", fechaValidacion=" + fechaValidacion +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComprobantePago that = (ComprobantePago) o;
        return idComprobante != null && idComprobante.equals(that.idComprobante);
    }

    @Override
    public int hashCode() {
        return idComprobante != null ? idComprobante.hashCode() : 0;
    }

    public static ComprobantePago crearFactura(Integer idPago, String numeroFactura, String archivoUrl) {
        return new ComprobantePago(idPago, "Factura", numeroFactura, archivoUrl);
    }

    public static ComprobantePago crearBoleta(Integer idPago, String numeroBoleta, String archivoUrl) {
        return new ComprobantePago(idPago, "Boleta", numeroBoleta, archivoUrl);
    }

    public static ComprobantePago crearTicket(Integer idPago, String numeroTicket, String archivoUrl) {
        return new ComprobantePago(idPago, "Ticket", numeroTicket, archivoUrl);
    }

    public static ComprobantePago crearOtro(Integer idPago, String numeroComprobante, String archivoUrl) {
        return new ComprobantePago(idPago, "Otro", numeroComprobante, archivoUrl);
    }

    public static ComprobantePago fromDTO(com.stamos.dto.ComprobantePagoDTO dto) {
        if (dto == null) {
            return null;
        }
        ComprobantePago comprobante = new ComprobantePago();
        if (dto.getIdComprobante() != null) {
            comprobante.setIdComprobante(dto.getIdComprobante());
        }
        comprobante.setIdPago(dto.getIdPago());
        comprobante.setTipoComprobante(dto.getTipoComprobante());
        comprobante.setNumeroComprobante(dto.getNumeroComprobante());
        comprobante.setArchivoUrl(dto.getArchivoUrl());
        if (dto.getValidado() != null) {
            comprobante.setValidado(dto.getValidado());
        }
        comprobante.setIdUsuarioValidador(dto.getIdUsuarioValidador());
        comprobante.setFechaValidacion(dto.getFechaValidacion());
        return comprobante;
    }

    public com.stamos.dto.ComprobantePagoDTO toDTO() {
        com.stamos.dto.ComprobantePagoDTO dto = new com.stamos.dto.ComprobantePagoDTO();
        dto.setIdComprobante(this.idComprobante);
        dto.setIdPago(this.idPago);
        dto.setTipoComprobante(this.tipoComprobante);
        dto.setNumeroComprobante(this.numeroComprobante);
        dto.setArchivoUrl(this.archivoUrl);
        dto.setValidado(this.validado);
        dto.setIdUsuarioValidador(this.idUsuarioValidador);
        dto.setFechaValidacion(this.fechaValidacion);
        dto.setFechaRegistro(this.fechaRegistro);
        dto.setNombreUsuarioValidador(this.nombreUsuarioValidador);
        dto.setEmailUsuarioValidador(this.emailUsuarioValidador);
        dto.setMetodoPago(this.metodoPago);
        dto.setNombreProyecto(this.nombreProyecto);
        dto.setNombreCliente(this.nombreCliente);
        return dto;
    }

    public static class ComprobantePagoDTO {
        private Integer idComprobante;
        private Integer idPago;
        private String tipoComprobante;
        private String numeroComprobante;
        private String archivoUrl;
        private Boolean validado;
        private Integer idUsuarioValidador;
        private LocalDateTime fechaValidacion;
        private LocalDateTime fechaRegistro;
        private String nombreUsuarioValidador;
        private String emailUsuarioValidador;
        private String metodoPago;
        private String nombreProyecto;
        private String nombreCliente;

        public Integer getIdComprobante() { return idComprobante; }
        public void setIdComprobante(Integer idComprobante) { this.idComprobante = idComprobante; }

        public Integer getIdPago() { return idPago; }
        public void setIdPago(Integer idPago) { this.idPago = idPago; }

        public String getTipoComprobante() { return tipoComprobante; }
        public void setTipoComprobante(String tipoComprobante) { this.tipoComprobante = tipoComprobante; }

        public String getNumeroComprobante() { return numeroComprobante; }
        public void setNumeroComprobante(String numeroComprobante) { this.numeroComprobante = numeroComprobante; }

        public String getArchivoUrl() { return archivoUrl; }
        public void setArchivoUrl(String archivoUrl) { this.archivoUrl = archivoUrl; }

        public Boolean getValidado() { return validado; }
        public void setValidado(Boolean validado) { this.validado = validado; }

        public Integer getIdUsuarioValidador() { return idUsuarioValidador; }
        public void setIdUsuarioValidador(Integer idUsuarioValidador) { this.idUsuarioValidador = idUsuarioValidador; }

        public LocalDateTime getFechaValidacion() { return fechaValidacion; }
        public void setFechaValidacion(LocalDateTime fechaValidacion) { this.fechaValidacion = fechaValidacion; }

        public LocalDateTime getFechaRegistro() { return fechaRegistro; }
        public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

        public String getNombreUsuarioValidador() { return nombreUsuarioValidador; }
        public void setNombreUsuarioValidador(String nombreUsuarioValidador) { this.nombreUsuarioValidador = nombreUsuarioValidador; }

        public String getEmailUsuarioValidador() { return emailUsuarioValidador; }
        public void setEmailUsuarioValidador(String emailUsuarioValidador) { this.emailUsuarioValidador = emailUsuarioValidador; }

        public String getMetodoPago() { return metodoPago; }
        public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

        public String getNombreProyecto() { return nombreProyecto; }
        public void setNombreProyecto(String nombreProyecto) { this.nombreProyecto = nombreProyecto; }

        public String getNombreCliente() { return nombreCliente; }
        public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

        public String getEstadoLegible() {
            return validado != null && validado ? "Validado" : "Pendiente de validación";
        }

        public String getEstadoBadgeClass() {
            return validado != null && validado ? "badge-success" : "badge-warning";
        }

        public String getTipoLegible() {
            return tipoComprobante != null ? tipoComprobante : "No especificado";
        }

        public String getTipoBadgeClass() {
            if (tipoComprobante == null) return "badge-secondary";
            return switch (tipoComprobante) {
                case "Factura" -> "badge-primary";
                case "Boleta" -> "badge-info";
                case "Ticket" -> "badge-warning";
                default -> "badge-secondary";
            };
        }

        public String getFechaValidacionFormateada() {
            if (fechaValidacion == null) return "-";
            return fechaValidacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }

        public String getFechaRegistroFormateada() {
            if (fechaRegistro == null) return "-";
            return fechaRegistro.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
    }
}