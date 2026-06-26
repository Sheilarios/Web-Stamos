package com.stamos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ComprobantePagoDTO {

    private Integer idComprobante;
    private Integer idPago;
    private String tipoComprobante;
    private String numeroComprobante;
    private String archivoUrl;
    private Boolean validado;
    private Integer idUsuarioValidador;
    private String nombreUsuarioValidador;
    private String emailUsuarioValidador;
    private LocalDateTime fechaValidacion;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaActualizacion;
    private String metodoPago;
    private BigDecimal montoPago;
    private LocalDateTime fechaPago;
    private String estadoPago;
    private Integer idProyecto;
    private String nombreProyecto;
    private String codigoProyecto;
    private BigDecimal montoTotalProyecto;
    private Integer idCliente;
    private String nombreCliente;
    private String rucCliente;
    private String emailCliente;
    private String telefonoCliente;
    private String observaciones;
    private String color;
    private String icono;

    public ComprobantePagoDTO() {
        this.validado = false;
        this.color = "#F59E0B";
        this.icono = "ti ti-file";
    }

    public ComprobantePagoDTO(Integer idComprobante, String tipoComprobante, 
                             String numeroComprobante, Boolean validado) {
        this();
        this.idComprobante = idComprobante;
        this.tipoComprobante = tipoComprobante;
        this.numeroComprobante = numeroComprobante;
        this.validado = validado;
    }

    public ComprobantePagoDTO(Integer idComprobante, Integer idPago, String tipoComprobante,
                             String numeroComprobante, String archivoUrl, Boolean validado,
                             String nombreProyecto, String nombreCliente) {
        this();
        this.idComprobante = idComprobante;
        this.idPago = idPago;
        this.tipoComprobante = tipoComprobante;
        this.numeroComprobante = numeroComprobante;
        this.archivoUrl = archivoUrl;
        this.validado = validado;
        this.nombreProyecto = nombreProyecto;
        this.nombreCliente = nombreCliente;
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

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public BigDecimal getMontoPago() {
        return montoPago;
    }

    public void setMontoPago(BigDecimal montoPago) {
        this.montoPago = montoPago;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public Integer getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(Integer idProyecto) {
        this.idProyecto = idProyecto;
    }

    public String getNombreProyecto() {
        return nombreProyecto;
    }

    public void setNombreProyecto(String nombreProyecto) {
        this.nombreProyecto = nombreProyecto;
    }

    public String getCodigoProyecto() {
        return codigoProyecto;
    }

    public void setCodigoProyecto(String codigoProyecto) {
        this.codigoProyecto = codigoProyecto;
    }

    public BigDecimal getMontoTotalProyecto() {
        return montoTotalProyecto;
    }

    public void setMontoTotalProyecto(BigDecimal montoTotalProyecto) {
        this.montoTotalProyecto = montoTotalProyecto;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getRucCliente() {
        return rucCliente;
    }

    public void setRucCliente(String rucCliente) {
        this.rucCliente = rucCliente;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public void setEmailCliente(String emailCliente) {
        this.emailCliente = emailCliente;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public boolean isValidado() {
        return validado != null && validado;
    }

    public boolean isPendiente() {
        return !isValidado();
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

    public boolean tieneValidador() {
        return idUsuarioValidador != null || 
               nombreUsuarioValidador != null || 
               emailUsuarioValidador != null;
    }

    public String getNombreArchivo() {
        if (archivoUrl == null || archivoUrl.isEmpty()) {
            return null;
        }
        String[] partes = archivoUrl.split("/");
        return partes[partes.length - 1];
    }

    public String getExtensionArchivo() {
        String nombre = getNombreArchivo();
        if (nombre == null || !nombre.contains(".")) {
            return null;
        }
        return nombre.substring(nombre.lastIndexOf(".") + 1).toLowerCase();
    }

    public boolean esImagen() {
        String extension = getExtensionArchivo();
        if (extension == null) {
            return false;
        }
        return extension.matches("(?i)(jpg|jpeg|png|gif|webp|svg)");
    }

    public boolean esPdf() {
        String extension = getExtensionArchivo();
        if (extension == null) {
            return false;
        }
        return "pdf".equalsIgnoreCase(extension);
    }

    public static ComprobantePagoDTO fromEntity(com.stamos.model.ComprobantePago comprobante) {
        if (comprobante == null) {
            return null;
        }

        ComprobantePagoDTO dto = new ComprobantePagoDTO();
        dto.setIdComprobante(comprobante.getIdComprobante());
        dto.setIdPago(comprobante.getIdPago());
        dto.setTipoComprobante(comprobante.getTipoComprobante());
        dto.setNumeroComprobante(comprobante.getNumeroComprobante());
        dto.setArchivoUrl(comprobante.getArchivoUrl());
        dto.setValidado(comprobante.getValidado());
        dto.setIdUsuarioValidador(comprobante.getIdUsuarioValidador());
        dto.setFechaValidacion(comprobante.getFechaValidacion());
        dto.setFechaRegistro(comprobante.getFechaRegistro());
        dto.setFechaActualizacion(comprobante.getFechaActualizacion());
        dto.setNombreUsuarioValidador(comprobante.getNombreUsuarioValidador());
        dto.setEmailUsuarioValidador(comprobante.getEmailUsuarioValidador());
        dto.setMetodoPago(comprobante.getMetodoPago());
        dto.setNombreProyecto(comprobante.getNombreProyecto());
        dto.setNombreCliente(comprobante.getNombreCliente());

        return dto;
    }

    public static ComprobantePagoDTO fromEntityWithDetails(
            com.stamos.model.ComprobantePago comprobante,
            String metodoPago,
            BigDecimal montoPago,
            String nombreProyecto,
            String nombreCliente) {
        
        ComprobantePagoDTO dto = fromEntity(comprobante);
        if (dto != null) {
            dto.setMetodoPago(metodoPago);
            dto.setMontoPago(montoPago);
            dto.setNombreProyecto(nombreProyecto);
            dto.setNombreCliente(nombreCliente);
        }
        return dto;
    }

    public com.stamos.model.ComprobantePago toEntity() {
        com.stamos.model.ComprobantePago comprobante = new com.stamos.model.ComprobantePago();
        comprobante.setIdComprobante(this.idComprobante);
        comprobante.setIdPago(this.idPago);
        comprobante.setTipoComprobante(this.tipoComprobante);
        comprobante.setNumeroComprobante(this.numeroComprobante);
        comprobante.setArchivoUrl(this.archivoUrl);
        comprobante.setValidado(this.validado);
        comprobante.setIdUsuarioValidador(this.idUsuarioValidador);
        comprobante.setFechaValidacion(this.fechaValidacion);
        comprobante.setFechaRegistro(this.fechaRegistro);
        comprobante.setFechaActualizacion(this.fechaActualizacion);
        return comprobante;
    }

    @Override
    public String toString() {
        return "ComprobantePagoDTO{" +
                "idComprobante=" + idComprobante +
                ", tipoComprobante='" + tipoComprobante + '\'' +
                ", numeroComprobante='" + numeroComprobante + '\'' +
                ", validado=" + validado +
                ", nombreProyecto='" + nombreProyecto + '\'' +
                ", nombreCliente='" + nombreCliente + '\'' +
                ", montoPago=" + montoPago +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComprobantePagoDTO that = (ComprobantePagoDTO) o;
        return idComprobante != null && idComprobante.equals(that.idComprobante);
    }

    @Override
    public int hashCode() {
        return idComprobante != null ? idComprobante.hashCode() : 0;
    }
}