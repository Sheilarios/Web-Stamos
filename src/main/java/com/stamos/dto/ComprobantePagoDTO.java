package com.stamos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO (Data Transfer Object) para la entidad ComprobantePago.
 * 
 * <p>Este DTO se utiliza para transferir datos de comprobantes de pago
 * entre la capa de servicio y la capa de presentación, evitando exponer
 * directamente la entidad y permitiendo una mejor organización de los datos.</p>
 * 
 * <p>Incluye información del comprobante, datos de validación y
 * referencias al pago, proyecto y cliente asociados.</p>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
public class ComprobantePagoDTO {

    // ============================================
    // ATRIBUTOS PRINCIPALES
    // ============================================

    private Integer idComprobante;
    private Integer idPago;
    private String tipoComprobante;
    private String numeroComprobante;
    private String archivoUrl;
    private Boolean validado;

    // ============================================
    // CAMPOS DE VALIDACIÓN
    // ============================================

    private Integer idUsuarioValidador;
    private String nombreUsuarioValidador;
    private String emailUsuarioValidador;
    private LocalDateTime fechaValidacion;

    // ============================================
    // CAMPOS DE AUDITORÍA
    // ============================================

    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaActualizacion;

    // ============================================
    // CAMPOS RELACIONADOS (DESDE PAGO)
    // ============================================

    private String metodoPago;
    private BigDecimal montoPago;
    private LocalDateTime fechaPago;
    private String estadoPago;

    // ============================================
    // CAMPOS RELACIONADOS (DESDE PROYECTO)
    // ============================================

    private Integer idProyecto;
    private String nombreProyecto;
    private String codigoProyecto;
    private BigDecimal montoTotalProyecto;

    // ============================================
    // CAMPOS RELACIONADOS (DESDE CLIENTE)
    // ============================================

    private Integer idCliente;
    private String nombreCliente;
    private String rucCliente;
    private String emailCliente;
    private String telefonoCliente;

    // ============================================
    // CAMPOS ADICIONALES
    // ============================================

    private String observaciones;
    private String color;
    private String icono;

    // ============================================
    // CONSTRUCTORES
    // ============================================

    /**
     * Constructor por defecto.
     */
    public ComprobantePagoDTO() {
        this.validado = false;
        this.color = "#F59E0B";
        this.icono = "ti ti-file";
    }

    /**
     * Constructor con campos principales.
     * 
     * @param idComprobante ID del comprobante
     * @param tipoComprobante Tipo de comprobante
     * @param numeroComprobante Número de comprobante
     * @param validado Estado de validación
     */
    public ComprobantePagoDTO(Integer idComprobante, String tipoComprobante, 
                             String numeroComprobante, Boolean validado) {
        this();
        this.idComprobante = idComprobante;
        this.tipoComprobante = tipoComprobante;
        this.numeroComprobante = numeroComprobante;
        this.validado = validado;
    }

    /**
     * Constructor completo.
     * 
     * @param idComprobante ID del comprobante
     * @param idPago ID del pago
     * @param tipoComprobante Tipo de comprobante
     * @param numeroComprobante Número de comprobante
     * @param archivoUrl URL del archivo
     * @param validado Estado de validación
     * @param nombreProyecto Nombre del proyecto
     * @param nombreCliente Nombre del cliente
     */
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

    // ============================================
    // GETTERS Y SETTERS
    // ============================================

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

    // ============================================
    // MÉTODOS DE NEGOCIO
    // ============================================

    /**
     * Verifica si el comprobante está validado.
     * 
     * @return true si está validado, false en caso contrario
     */
    public boolean isValidado() {
        return validado != null && validado;
    }

    /**
     * Verifica si el comprobante está pendiente de validación.
     * 
     * @return true si está pendiente, false en caso contrario
     */
    public boolean isPendiente() {
        return !isValidado();
    }

    /**
     * Verifica si el comprobante tiene archivo adjunto.
     * 
     * @return true si tiene archivo, false en caso contrario
     */
    public boolean tieneArchivo() {
        return archivoUrl != null && !archivoUrl.trim().isEmpty();
    }

    /**
     * Verifica si el comprobante es una factura.
     * 
     * @return true si es factura, false en caso contrario
     */
    public boolean esFactura() {
        return "Factura".equals(this.tipoComprobante);
    }

    /**
     * Verifica si el comprobante es una boleta.
     * 
     * @return true si es boleta, false en caso contrario
     */
    public boolean esBoleta() {
        return "Boleta".equals(this.tipoComprobante);
    }

    /**
     * Verifica si el comprobante es un ticket.
     * 
     * @return true si es ticket, false en caso contrario
     */
    public boolean esTicket() {
        return "Ticket".equals(this.tipoComprobante);
    }

    /**
     * Verifica si el comprobante es otro tipo.
     * 
     * @return true si es otro tipo, false en caso contrario
     */
    public boolean esOtro() {
        return "Otro".equals(this.tipoComprobante);
    }

    /**
     * Verifica si el comprobante tiene un validador asignado.
     * 
     * @return true si tiene validador, false en caso contrario
     */
    public boolean tieneValidador() {
        return idUsuarioValidador != null || 
               nombreUsuarioValidador != null || 
               emailUsuarioValidador != null;
    }

    /**
     * Obtiene el nombre del archivo del comprobante.
     * 
     * @return Nombre del archivo
     */
    public String getNombreArchivo() {
        if (archivoUrl == null || archivoUrl.isEmpty()) {
            return null;
        }
        String[] partes = archivoUrl.split("/");
        return partes[partes.length - 1];
    }

    /**
     * Obtiene la extensión del archivo del comprobante.
     * 
     * @return Extensión del archivo
     */
    public String getExtensionArchivo() {
        String nombre = getNombreArchivo();
        if (nombre == null || !nombre.contains(".")) {
            return null;
        }
        return nombre.substring(nombre.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * Verifica si el archivo del comprobante es una imagen.
     * 
     * @return true si es imagen, false en caso contrario
     */
    public boolean esImagen() {
        String extension = getExtensionArchivo();
        if (extension == null) {
            return false;
        }
        return extension.matches("(?i)(jpg|jpeg|png|gif|webp|svg)");
    }

    /**
     * Verifica si el archivo del comprobante es un PDF.
     * 
     * @return true si es PDF, false en caso contrario
     */
    public boolean esPdf() {
        String extension = getExtensionArchivo();
        if (extension == null) {
            return false;
        }
        return "pdf".equalsIgnoreCase(extension);
    }

    // ============================================
    // MÉTODOS DE FORMATO PARA LA VISTA
    // ============================================

    /**
     * Obtiene el estado en formato legible.
     * 
     * @return Estado legible
     */
    public String getEstadoLegible() {
        return isValidado() ? "Validado" : "Pendiente de validación";
    }

    /**
     * Obtiene la clase CSS para el badge del estado.
     * 
     * @return Clase CSS
     */
    public String getEstadoBadgeClass() {
        return isValidado() ? "badge-success" : "badge-warning";
    }

    /**
     * Obtiene el ícono para el estado.
     * 
     * @return Ícono CSS
     */
    public String getEstadoIcono() {
        return isValidado() ? "ti ti-check-circle" : "ti ti-clock";
    }

    /**
     * Obtiene el color para el estado.
     * 
     * @return Color en hexadecimal
     */
    public String getEstadoColor() {
        return isValidado() ? "#10B981" : "#F59E0B";
    }

    /**
     * Obtiene el tipo de comprobante en formato legible.
     * 
     * @return Tipo legible
     */
    public String getTipoLegible() {
        if (tipoComprobante == null) return "No especificado";
        return tipoComprobante;
    }

    /**
     * Obtiene la clase CSS para el badge del tipo de comprobante.
     * 
     * @return Clase CSS
     */
    public String getTipoBadgeClass() {
        if (tipoComprobante == null) return "badge-secondary";
        
        switch (tipoComprobante) {
            case "Factura":
                return "badge-primary";
            case "Boleta":
                return "badge-info";
            case "Ticket":
                return "badge-warning";
            case "Otro":
                return "badge-secondary";
            default:
                return "badge-secondary";
        }
    }

    /**
     * Obtiene el ícono para el tipo de comprobante.
     * 
     * @return Ícono CSS
     */
    public String getTipoIcono() {
        if (tipoComprobante == null) return "ti ti-file";
        
        switch (tipoComprobante) {
            case "Factura":
                return "ti ti-file-invoice";
            case "Boleta":
                return "ti ti-receipt";
            case "Ticket":
                return "ti ti-ticket";
            case "Otro":
                return "ti ti-file";
            default:
                return "ti ti-file";
        }
    }

    /**
     * Obtiene el método de pago en formato legible.
     * 
     * @return Método de pago legible
     */
    public String getMetodoPagoLegible() {
        if (metodoPago == null) return "No especificado";
        
        switch (metodoPago) {
            case "Efectivo":
                return "Efectivo";
            case "Transferencia":
                return "Transferencia";
            case "Tarjeta":
                return "Tarjeta";
            case "Cheque":
                return "Cheque";
            default:
                return metodoPago;
        }
    }

    /**
     * Obtiene la clase CSS para el badge del método de pago.
     * 
     * @return Clase CSS
     */
    public String getMetodoPagoBadgeClass() {
        if (metodoPago == null) return "badge-secondary";
        
        switch (metodoPago) {
            case "Efectivo":
                return "badge-success";
            case "Transferencia":
                return "badge-info";
            case "Tarjeta":
                return "badge-primary";
            case "Cheque":
                return "badge-warning";
            default:
                return "badge-secondary";
        }
    }

    /**
     * Obtiene el estado del pago en formato legible.
     * 
     * @return Estado del pago legible
     */
    public String getEstadoPagoLegible() {
        if (estadoPago == null) return "Sin estado";
        
        switch (estadoPago) {
            case "Pendiente":
                return "Pendiente de validación";
            case "Validado":
                return "Validado";
            case "Rechazado":
                return "Rechazado";
            default:
                return estadoPago;
        }
    }

    /**
     * Obtiene la clase CSS para el badge del estado del pago.
     * 
     * @return Clase CSS
     */
    public String getEstadoPagoBadgeClass() {
        if (estadoPago == null) return "status-pendiente";
        
        switch (estadoPago) {
            case "Pendiente":
                return "status-pendiente";
            case "Validado":
                return "status-validado";
            case "Rechazado":
                return "status-rechazado";
            default:
                return "status-pendiente";
        }
    }

    /**
     * Obtiene la fecha de registro formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaRegistroFormateada() {
        if (fechaRegistro == null) return "-";
        return fechaRegistro.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    /**
     * Obtiene la fecha de registro formateada (solo fecha).
     * 
     * @return Fecha formateada
     */
    public String getFechaRegistroSoloFecha() {
        if (fechaRegistro == null) return "-";
        return fechaRegistro.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Obtiene la fecha de validación formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaValidacionFormateada() {
        if (fechaValidacion == null) return "-";
        return fechaValidacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    /**
     * Obtiene la fecha de validación formateada (solo fecha).
     * 
     * @return Fecha formateada
     */
    public String getFechaValidacionSoloFecha() {
        if (fechaValidacion == null) return "-";
        return fechaValidacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Obtiene la fecha de pago formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaPagoFormateada() {
        if (fechaPago == null) return "-";
        return fechaPago.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    /**
     * Obtiene el monto del pago formateado.
     * 
     * @return Monto formateado
     */
    public String getMontoPagoFormateado() {
        if (montoPago == null) return "S/ 0.00";
        return "S/ " + montoPago.setScale(2, java.math.RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el monto total del proyecto formateado.
     * 
     * @return Monto formateado
     */
    public String getMontoTotalProyectoFormateado() {
        if (montoTotalProyecto == null) return "S/ 0.00";
        return "S/ " + montoTotalProyecto.setScale(2, java.math.RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el validador en formato legible.
     * 
     * @return Validador legible
     */
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

    /**
     * Obtiene el nombre del cliente para mostrar.
     * 
     * @return Nombre del cliente
     */
    public String getClienteDisplay() {
        if (nombreCliente != null) {
            if (rucCliente != null && !rucCliente.isEmpty()) {
                return nombreCliente + " (" + rucCliente + ")";
            }
            return nombreCliente;
        }
        return "Cliente no especificado";
    }

    /**
     * Obtiene el nombre del proyecto para mostrar.
     * 
     * @return Nombre del proyecto
     */
    public String getProyectoDisplay() {
        if (nombreProyecto != null) {
            if (codigoProyecto != null && !codigoProyecto.isEmpty()) {
                return codigoProyecto + " - " + nombreProyecto;
            }
            return nombreProyecto;
        }
        return "Proyecto no especificado";
    }

    /**
     * Obtiene el resumen del comprobante.
     * 
     * @return Resumen
     */
    public String getResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append(tipoComprobante != null ? tipoComprobante : "Comprobante");
        sb.append(" N° ").append(numeroComprobante != null ? numeroComprobante : "---");
        if (nombreProyecto != null) {
            sb.append(" - ").append(nombreProyecto);
        }
        sb.append(" (").append(getEstadoLegible()).append(")");
        return sb.toString();
    }

    /**
     * Obtiene la clase CSS para el archivo adjunto.
     * 
     * @return Clase CSS
     */
    public String getArchivoBadgeClass() {
        return tieneArchivo() ? "badge-success" : "badge-secondary";
    }

    /**
     * Obtiene el texto para el archivo adjunto.
     * 
     * @return Texto del archivo
     */
    public String getArchivoTexto() {
        return tieneArchivo() ? "Adjunto" : "Sin adjunto";
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
        sb.append("\"idComprobante\":").append(idComprobante).append(",");
        sb.append("\"idPago\":").append(idPago).append(",");
        sb.append("\"tipo\":\"").append(tipoComprobante != null ? tipoComprobante : "").append("\",");
        sb.append("\"numero\":\"").append(numeroComprobante != null ? numeroComprobante : "").append("\",");
        sb.append("\"validado\":").append(isValidado()).append(",");
        sb.append("\"fechaRegistro\":\"").append(getFechaRegistroFormateada()).append("\"");
        if (fechaValidacion != null) {
            sb.append(",\"fechaValidacion\":\"").append(getFechaValidacionFormateada()).append("\"");
        }
        if (nombreProyecto != null) {
            sb.append(",\"proyecto\":\"").append(nombreProyecto).append("\"");
        }
        if (nombreCliente != null) {
            sb.append(",\"cliente\":\"").append(nombreCliente).append("\"");
        }
        if (montoPago != null) {
            sb.append(",\"monto\":").append(montoPago);
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convierte a JSON completo.
     * 
     * @return JSON completo
     */
    public String toJsonCompleto() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"idComprobante\":").append(idComprobante).append(",");
        sb.append("\"idPago\":").append(idPago).append(",");
        sb.append("\"tipoComprobante\":\"").append(tipoComprobante != null ? tipoComprobante : "").append("\",");
        sb.append("\"numeroComprobante\":\"").append(numeroComprobante != null ? numeroComprobante : "").append("\",");
        sb.append("\"archivoUrl\":\"").append(archivoUrl != null ? archivoUrl : "").append("\",");
        sb.append("\"validado\":").append(isValidado()).append(",");
        sb.append("\"fechaRegistro\":\"").append(getFechaRegistroFormateada()).append("\"");
        if (fechaValidacion != null) {
            sb.append(",\"fechaValidacion\":\"").append(getFechaValidacionFormateada()).append("\"");
        }
        if (nombreUsuarioValidador != null) {
            sb.append(",\"validador\":\"").append(nombreUsuarioValidador).append("\"");
        }
        if (metodoPago != null) {
            sb.append(",\"metodoPago\":\"").append(metodoPago).append("\"");
        }
        if (montoPago != null) {
            sb.append(",\"montoPago\":").append(montoPago);
        }
        if (nombreProyecto != null) {
            sb.append(",\"nombreProyecto\":\"").append(nombreProyecto).append("\"");
        }
        if (nombreCliente != null) {
            sb.append(",\"nombreCliente\":\"").append(nombreCliente).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

    // ============================================
    // MÉTODOS OVERRIDE
    // ============================================

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

    // ============================================
    // MÉTODOS ESTÁTICOS DE FÁBRICA
    // ============================================

    /**
     * Crea un DTO desde una entidad.
     * 
     * @param comprobante Entidad de comprobante de pago
     * @return DTO de comprobante de pago
     */
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

        // Campos transient
        dto.setNombreUsuarioValidador(comprobante.getNombreUsuarioValidador());
        dto.setEmailUsuarioValidador(comprobante.getEmailUsuarioValidador());
        dto.setMetodoPago(comprobante.getMetodoPago());
        dto.setNombreProyecto(comprobante.getNombreProyecto());
        dto.setNombreCliente(comprobante.getNombreCliente());

        return dto;
    }

    /**
     * Crea un DTO desde una entidad con datos adicionales.
     * 
     * @param comprobante Entidad de comprobante de pago
     * @param metodoPago Método de pago
     * @param montoPago Monto del pago
     * @param nombreProyecto Nombre del proyecto
     * @param nombreCliente Nombre del cliente
     * @return DTO de comprobante de pago
     */
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

    /**
     * Convierte el DTO a una entidad.
     * 
     * @return Entidad de comprobante de pago
     */
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
}