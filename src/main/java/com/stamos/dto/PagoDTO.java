package com.stamos.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO (Data Transfer Object) para la entidad Pago.
 * 
 * <p>Este DTO se utiliza para transferir datos de pagos entre la capa de
 * servicio y la capa de presentación, evitando exponer directamente la entidad
 * y permitiendo una mejor organización de los datos.</p>
 * 
 * <p>Incluye información del pago, método, estado, proyecto asociado
 * y métricas financieras como saldo pendiente y porcentaje pagado.</p>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
public class PagoDTO {

    // ============================================
    // ATRIBUTOS PRINCIPALES
    // ============================================

    private Integer idPago;
    private Integer idProyecto;
    private BigDecimal monto;
    private LocalDateTime fechaPago;
    private String metodoPago;
    private String estadoPago;
    private String observaciones;

    // ============================================
    // CAMPOS DE AUDITORÍA
    // ============================================

    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaActualizacion;
    private Integer idUsuarioRegistro;
    private Integer idUsuarioActualizacion;
    private Integer idUsuarioValidacion;
    private LocalDateTime fechaValidacion;
    private String nombreUsuarioRegistro;
    private String nombreUsuarioValidacion;

    // ============================================
    // CAMPOS RELACIONADOS (DESDE PROYECTO)
    // ============================================

    private String nombreProyecto;
    private String codigoProyecto;
    private BigDecimal montoTotalProyecto;

    // ============================================
    // CAMPOS RELACIONADOS (DESDE CLIENTE)
    // ============================================

    private String nombreCliente;
    private String emailCliente;
    private String rucCliente;
    private String telefonoCliente;

    // ============================================
    // CAMPOS DE MÉTRICAS FINANCIERAS
    // ============================================

    private BigDecimal saldoPendiente;
    private Integer porcentajePagado;
    private Boolean esPagoCompleto;

    // ============================================
    // CAMPOS DE FORMATO Y ESTADO
    // ============================================

    private String metodoPagoLegible;
    private String estadoPagoLegible;
    private String colorEstado;
    private String iconoEstado;
    private String colorMetodo;
    private String iconoMetodo;

    // ============================================
    // CAMPOS ADICIONALES
    // ============================================

    private String numeroComprobante;
    private String tipoComprobante;
    private String archivoComprobanteUrl;
    private Boolean comprobanteValidado;

    // ============================================
    // CONSTRUCTORES
    // ============================================

    /**
     * Constructor por defecto.
     */
    public PagoDTO() {
        this.estadoPago = "Pendiente";
        this.monto = BigDecimal.ZERO;
        this.saldoPendiente = BigDecimal.ZERO;
        this.porcentajePagado = 0;
        this.esPagoCompleto = false;
        this.comprobanteValidado = false;
        this.colorEstado = "#F59E0B";
        this.iconoEstado = "ti ti-clock";
        this.estadoPagoLegible = "Pendiente de validación";
    }

    /**
     * Constructor con campos principales.
     * 
     * @param idPago ID del pago
     * @param idProyecto ID del proyecto
     * @param monto Monto del pago
     * @param metodoPago Método de pago
     * @param estadoPago Estado del pago
     */
    public PagoDTO(Integer idPago, Integer idProyecto, BigDecimal monto, 
                  String metodoPago, String estadoPago) {
        this();
        this.idPago = idPago;
        this.idProyecto = idProyecto;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.estadoPago = estadoPago;
        actualizarCamposEstado();
    }

    /**
     * Constructor completo.
     * 
     * @param idPago ID del pago
     * @param idProyecto ID del proyecto
     * @param monto Monto del pago
     * @param fechaPago Fecha del pago
     * @param metodoPago Método de pago
     * @param estadoPago Estado del pago
     * @param nombreProyecto Nombre del proyecto
     * @param nombreCliente Nombre del cliente
     */
    public PagoDTO(Integer idPago, Integer idProyecto, BigDecimal monto,
                  LocalDateTime fechaPago, String metodoPago, String estadoPago,
                  String nombreProyecto, String nombreCliente) {
        this(idPago, idProyecto, monto, metodoPago, estadoPago);
        this.fechaPago = fechaPago;
        this.nombreProyecto = nombreProyecto;
        this.nombreCliente = nombreCliente;
        actualizarCamposEstado();
    }

    // ============================================
    // GETTERS Y SETTERS
    // ============================================

    public Integer getIdPago() {
        return idPago;
    }

    public void setIdPago(Integer idPago) {
        this.idPago = idPago;
    }

    public Integer getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(Integer idProyecto) {
        this.idProyecto = idProyecto;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
        calcularMetricasFinancieras();
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
        actualizarCamposEstado();
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
        actualizarCamposEstado();
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
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

    public Integer getIdUsuarioRegistro() {
        return idUsuarioRegistro;
    }

    public void setIdUsuarioRegistro(Integer idUsuarioRegistro) {
        this.idUsuarioRegistro = idUsuarioRegistro;
    }

    public Integer getIdUsuarioActualizacion() {
        return idUsuarioActualizacion;
    }

    public void setIdUsuarioActualizacion(Integer idUsuarioActualizacion) {
        this.idUsuarioActualizacion = idUsuarioActualizacion;
    }

    public Integer getIdUsuarioValidacion() {
        return idUsuarioValidacion;
    }

    public void setIdUsuarioValidacion(Integer idUsuarioValidacion) {
        this.idUsuarioValidacion = idUsuarioValidacion;
    }

    public LocalDateTime getFechaValidacion() {
        return fechaValidacion;
    }

    public void setFechaValidacion(LocalDateTime fechaValidacion) {
        this.fechaValidacion = fechaValidacion;
    }

    public String getNombreUsuarioRegistro() {
        return nombreUsuarioRegistro;
    }

    public void setNombreUsuarioRegistro(String nombreUsuarioRegistro) {
        this.nombreUsuarioRegistro = nombreUsuarioRegistro;
    }

    public String getNombreUsuarioValidacion() {
        return nombreUsuarioValidacion;
    }

    public void setNombreUsuarioValidacion(String nombreUsuarioValidacion) {
        this.nombreUsuarioValidacion = nombreUsuarioValidacion;
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
        calcularMetricasFinancieras();
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

    public String getRucCliente() {
        return rucCliente;
    }

    public void setRucCliente(String rucCliente) {
        this.rucCliente = rucCliente;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(BigDecimal saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }

    public Integer getPorcentajePagado() {
        return porcentajePagado;
    }

    public void setPorcentajePagado(Integer porcentajePagado) {
        this.porcentajePagado = porcentajePagado;
    }

    public Boolean getEsPagoCompleto() {
        return esPagoCompleto;
    }

    public void setEsPagoCompleto(Boolean esPagoCompleto) {
        this.esPagoCompleto = esPagoCompleto;
    }

    public String getMetodoPagoLegible() {
        return metodoPagoLegible;
    }

    public void setMetodoPagoLegible(String metodoPagoLegible) {
        this.metodoPagoLegible = metodoPagoLegible;
    }

    public String getEstadoPagoLegible() {
        return estadoPagoLegible;
    }

    public void setEstadoPagoLegible(String estadoPagoLegible) {
        this.estadoPagoLegible = estadoPagoLegible;
    }

    public String getColorEstado() {
        return colorEstado;
    }

    public void setColorEstado(String colorEstado) {
        this.colorEstado = colorEstado;
    }

    public String getIconoEstado() {
        return iconoEstado;
    }

    public void setIconoEstado(String iconoEstado) {
        this.iconoEstado = iconoEstado;
    }

    public String getColorMetodo() {
        return colorMetodo;
    }

    public void setColorMetodo(String colorMetodo) {
        this.colorMetodo = colorMetodo;
    }

    public String getIconoMetodo() {
        return iconoMetodo;
    }

    public void setIconoMetodo(String iconoMetodo) {
        this.iconoMetodo = iconoMetodo;
    }

    public String getNumeroComprobante() {
        return numeroComprobante;
    }

    public void setNumeroComprobante(String numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public String getArchivoComprobanteUrl() {
        return archivoComprobanteUrl;
    }

    public void setArchivoComprobanteUrl(String archivoComprobanteUrl) {
        this.archivoComprobanteUrl = archivoComprobanteUrl;
    }

    public Boolean getComprobanteValidado() {
        return comprobanteValidado;
    }

    public void setComprobanteValidado(Boolean comprobanteValidado) {
        this.comprobanteValidado = comprobanteValidado;
    }

    // ============================================
    // MÉTODOS DE NEGOCIO
    // ============================================

    /**
     * Actualiza los campos de estado y método de pago.
     */
    private void actualizarCamposEstado() {
        // Estado del pago
        if (estadoPago != null) {
            switch (estadoPago) {
                case "Pendiente":
                    this.estadoPagoLegible = "Pendiente de validación";
                    this.colorEstado = "#F59E0B";
                    this.iconoEstado = "ti ti-clock";
                    break;
                case "Validado":
                    this.estadoPagoLegible = "Validado";
                    this.colorEstado = "#10B981";
                    this.iconoEstado = "ti ti-check-circle";
                    break;
                case "Rechazado":
                    this.estadoPagoLegible = "Rechazado";
                    this.colorEstado = "#EF4444";
                    this.iconoEstado = "ti ti-x-circle";
                    break;
                default:
                    this.estadoPagoLegible = estadoPago;
                    this.colorEstado = "#6B7280";
                    this.iconoEstado = "ti ti-circle";
                    break;
            }
        }

        // Método de pago
        if (metodoPago != null) {
            switch (metodoPago) {
                case "Efectivo":
                    this.metodoPagoLegible = "Efectivo";
                    this.colorMetodo = "#10B981";
                    this.iconoMetodo = "ti ti-coin";
                    break;
                case "Transferencia":
                    this.metodoPagoLegible = "Transferencia";
                    this.colorMetodo = "#3B82F6";
                    this.iconoMetodo = "ti ti-transfer";
                    break;
                case "Tarjeta":
                    this.metodoPagoLegible = "Tarjeta";
                    this.colorMetodo = "#8B5CF6";
                    this.iconoMetodo = "ti ti-credit-card";
                    break;
                case "Cheque":
                    this.metodoPagoLegible = "Cheque";
                    this.colorMetodo = "#F59E0B";
                    this.iconoMetodo = "ti ti-file";
                    break;
                default:
                    this.metodoPagoLegible = metodoPago;
                    this.colorMetodo = "#6B7280";
                    this.iconoMetodo = "ti ti-credit-card";
                    break;
            }
        }
    }

    /**
     * Calcula las métricas financieras (saldo pendiente, porcentaje pagado).
     */
    private void calcularMetricasFinancieras() {
        if (montoTotalProyecto != null && monto != null) {
            this.saldoPendiente = montoTotalProyecto.subtract(monto);
            if (this.saldoPendiente.compareTo(BigDecimal.ZERO) < 0) {
                this.saldoPendiente = BigDecimal.ZERO;
            }
            
            if (montoTotalProyecto.compareTo(BigDecimal.ZERO) > 0) {
                this.porcentajePagado = monto.multiply(new BigDecimal(100))
                        .divide(montoTotalProyecto, 0, RoundingMode.HALF_UP)
                        .intValue();
                if (this.porcentajePagado > 100) {
                    this.porcentajePagado = 100;
                }
                this.esPagoCompleto = this.porcentajePagado >= 100;
            } else {
                this.porcentajePagado = 0;
                this.esPagoCompleto = false;
            }
        } else {
            this.saldoPendiente = BigDecimal.ZERO;
            this.porcentajePagado = 0;
            this.esPagoCompleto = false;
        }
    }

    /**
     * Verifica si el pago está pendiente de validación.
     * 
     * @return true si está pendiente, false en caso contrario
     */
    public boolean isPendiente() {
        return "Pendiente".equals(this.estadoPago);
    }

    /**
     * Verifica si el pago está validado.
     * 
     * @return true si está validado, false en caso contrario
     */
    public boolean isValidado() {
        return "Validado".equals(this.estadoPago);
    }

    /**
     * Verifica si el pago está rechazado.
     * 
     * @return true si está rechazado, false en caso contrario
     */
    public boolean isRechazado() {
        return "Rechazado".equals(this.estadoPago);
    }

    /**
     * Verifica si el pago puede ser validado.
     * 
     * @return true si puede ser validado, false en caso contrario
     */
    public boolean puedeValidar() {
        return isPendiente();
    }

    /**
     * Verifica si el pago puede ser rechazado.
     * 
     * @return true si puede ser rechazado, false en caso contrario
     */
    public boolean puedeRechazar() {
        return isPendiente();
    }

    /**
     * Verifica si el pago es en efectivo.
     * 
     * @return true si es en efectivo, false en caso contrario
     */
    public boolean esEfectivo() {
        return "Efectivo".equals(this.metodoPago);
    }

    /**
     * Verifica si el pago es por transferencia.
     * 
     * @return true si es por transferencia, false en caso contrario
     */
    public boolean esTransferencia() {
        return "Transferencia".equals(this.metodoPago);
    }

    /**
     * Verifica si el pago es con tarjeta.
     * 
     * @return true si es con tarjeta, false en caso contrario
     */
    public boolean esTarjeta() {
        return "Tarjeta".equals(this.metodoPago);
    }

    /**
     * Verifica si el pago es con cheque.
     * 
     * @return true si es con cheque, false en caso contrario
     */
    public boolean esCheque() {
        return "Cheque".equals(this.metodoPago);
    }

    /**
     * Verifica si el pago tiene observaciones.
     * 
     * @return true si tiene observaciones, false en caso contrario
     */
    public boolean tieneObservaciones() {
        return observaciones != null && !observaciones.trim().isEmpty();
    }

    /**
     * Verifica si el pago tiene comprobante.
     * 
     * @return true si tiene comprobante, false en caso contrario
     */
    public boolean tieneComprobante() {
        return numeroComprobante != null && !numeroComprobante.trim().isEmpty();
    }

    /**
     * Verifica si el comprobante está validado.
     * 
     * @return true si está validado, false en caso contrario
     */
    public boolean isComprobanteValidado() {
        return comprobanteValidado != null && comprobanteValidado;
    }

    /**
     * Verifica si el pago es completo (100% del proyecto).
     * 
     * @return true si es completo, false en caso contrario
     */
    public boolean isPagoCompleto() {
        return esPagoCompleto != null && esPagoCompleto;
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
        return estadoPagoLegible != null ? estadoPagoLegible : estadoPago;
    }

    /**
     * Obtiene el método de pago en formato legible.
     * 
     * @return Método de pago legible
     */
    public String getMetodoLegible() {
        return metodoPagoLegible != null ? metodoPagoLegible : metodoPago;
    }

    /**
     * Obtiene la clase CSS para el badge del estado.
     * 
     * @return Clase CSS
     */
    public String getEstadoBadgeClass() {
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
     * Obtiene el estado con ícono.
     * 
     * @return Estado con ícono
     */
    public String getEstadoConIcono() {
        return "<i class='" + iconoEstado + "'></i> " + getEstadoLegible();
    }

    /**
     * Obtiene el método de pago con ícono.
     * 
     * @return Método de pago con ícono
     */
    public String getMetodoConIcono() {
        return "<i class='" + iconoMetodo + "'></i> " + getMetodoLegible();
    }

    /**
     * Obtiene la clase CSS para el badge del método de pago.
     * 
     * @return Clase CSS
     */
    public String getMetodoBadgeClass() {
        if (metodoPago == null) return "";
        
        switch (metodoPago) {
            case "Efectivo":
                return "efectivo";
            case "Transferencia":
                return "transferencia";
            case "Tarjeta":
                return "tarjeta";
            case "Cheque":
                return "cheque";
            default:
                return "";
        }
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
     * Obtiene la fecha de pago formateada (solo fecha).
     * 
     * @return Fecha formateada
     */
    public String getFechaPagoSoloFecha() {
        if (fechaPago == null) return "-";
        return fechaPago.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
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
     * Obtiene la fecha de validación formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaValidacionFormateada() {
        if (fechaValidacion == null) return "-";
        return fechaValidacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    /**
     * Obtiene el monto formateado.
     * 
     * @return Monto formateado
     */
    public String getMontoFormateado() {
        if (monto == null) return "S/ 0.00";
        return "S/ " + monto.setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el saldo pendiente formateado.
     * 
     * @return Saldo pendiente formateado
     */
    public String getSaldoPendienteFormateado() {
        if (saldoPendiente == null) return "S/ 0.00";
        return "S/ " + saldoPendiente.setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el monto total del proyecto formateado.
     * 
     * @return Monto total formateado
     */
    public String getMontoTotalProyectoFormateado() {
        if (montoTotalProyecto == null) return "S/ 0.00";
        return "S/ " + montoTotalProyecto.setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el color para el saldo pendiente.
     * 
     * @return Color en hexadecimal
     */
    public String getSaldoColor() {
        if (saldoPendiente == null || saldoPendiente.compareTo(BigDecimal.ZERO) == 0) {
            return "#10B981";
        }
        return "#EF4444";
    }

    /**
     * Obtiene el validador en formato legible.
     * 
     * @return Validador legible
     */
    public String getValidadorLegible() {
        if (nombreUsuarioValidacion != null && !nombreUsuarioValidacion.isEmpty()) {
            return nombreUsuarioValidacion;
        }
        if (idUsuarioValidacion != null) {
            return "Usuario ID: " + idUsuarioValidacion;
        }
        return "Sin validar";
    }

    /**
     * Obtiene el registrador en formato legible.
     * 
     * @return Registrador legible
     */
    public String getRegistradorLegible() {
        if (nombreUsuarioRegistro != null && !nombreUsuarioRegistro.isEmpty()) {
            return nombreUsuarioRegistro;
        }
        if (idUsuarioRegistro != null) {
            return "Usuario ID: " + idUsuarioRegistro;
        }
        return "Sistema";
    }

    /**
     * Obtiene el cliente en formato legible.
     * 
     * @return Cliente legible
     */
    public String getClienteLegible() {
        if (nombreCliente != null && !nombreCliente.isEmpty()) {
            if (rucCliente != null && !rucCliente.isEmpty()) {
                return nombreCliente + " (" + rucCliente + ")";
            }
            return nombreCliente;
        }
        if (emailCliente != null && !emailCliente.isEmpty()) {
            return emailCliente;
        }
        return "Cliente no especificado";
    }

    /**
     * Obtiene el proyecto en formato legible.
     * 
     * @return Proyecto legible
     */
    public String getProyectoLegible() {
        if (nombreProyecto != null && !nombreProyecto.isEmpty()) {
            if (codigoProyecto != null && !codigoProyecto.isEmpty()) {
                return codigoProyecto + " - " + nombreProyecto;
            }
            return nombreProyecto;
        }
        return "Proyecto ID: " + idProyecto;
    }

    /**
     * Obtiene el tipo de comprobante en formato legible.
     * 
     * @return Tipo de comprobante legible
     */
    public String getTipoComprobanteLegible() {
        if (tipoComprobante == null) return "No especificado";
        return tipoComprobante;
    }

    /**
     * Obtiene la clase CSS para el badge del tipo de comprobante.
     * 
     * @return Clase CSS
     */
    public String getTipoComprobanteBadgeClass() {
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
     * Obtiene el resumen del pago.
     * 
     * @return Resumen
     */
    public String getResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pago de ").append(getMontoFormateado());
        sb.append(" - ").append(getMetodoLegible());
        if (nombreProyecto != null) {
            sb.append(" - ").append(nombreProyecto);
        }
        sb.append(" (").append(getEstadoLegible()).append(")");
        return sb.toString();
    }

    /**
     * Obtiene el estado del comprobante en formato legible.
     * 
     * @return Estado del comprobante
     */
    public String getEstadoComprobanteLegible() {
        if (comprobanteValidado == null) return "Sin validar";
        return comprobanteValidado ? "Validado" : "Pendiente";
    }

    /**
     * Obtiene la clase CSS para el badge del estado del comprobante.
     * 
     * @return Clase CSS
     */
    public String getEstadoComprobanteBadgeClass() {
        if (comprobanteValidado == null) return "badge-secondary";
        return comprobanteValidado ? "badge-success" : "badge-warning";
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
        sb.append("\"idPago\":").append(idPago).append(",");
        sb.append("\"idProyecto\":").append(idProyecto).append(",");
        sb.append("\"monto\":").append(monto != null ? monto : 0).append(",");
        sb.append("\"montoFormateado\":\"").append(getMontoFormateado()).append("\",");
        sb.append("\"metodoPago\":\"").append(metodoPago != null ? metodoPago : "").append("\",");
        sb.append("\"estadoPago\":\"").append(estadoPago != null ? estadoPago : "").append("\",");
        sb.append("\"estadoLegible\":\"").append(getEstadoLegible()).append("\",");
        sb.append("\"colorEstado\":\"").append(colorEstado != null ? colorEstado : "#6B7280").append("\",");
        sb.append("\"fechaPago\":\"").append(getFechaPagoFormateada()).append("\"");
        if (nombreProyecto != null) {
            sb.append(",\"proyecto\":\"").append(nombreProyecto).append("\"");
        }
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
        sb.append("\"idPago\":").append(idPago).append(",");
        sb.append("\"idProyecto\":").append(idProyecto).append(",");
        sb.append("\"monto\":").append(monto != null ? monto : 0).append(",");
        sb.append("\"montoFormateado\":\"").append(getMontoFormateado()).append("\",");
        sb.append("\"metodoPago\":\"").append(metodoPago != null ? metodoPago : "").append("\",");
        sb.append("\"metodoLegible\":\"").append(getMetodoLegible()).append("\",");
        sb.append("\"estadoPago\":\"").append(estadoPago != null ? estadoPago : "").append("\",");
        sb.append("\"estadoLegible\":\"").append(getEstadoLegible()).append("\",");
        sb.append("\"colorEstado\":\"").append(colorEstado != null ? colorEstado : "#6B7280").append("\",");
        sb.append("\"iconoEstado\":\"").append(iconoEstado != null ? iconoEstado : "ti ti-circle").append("\",");
        sb.append("\"colorMetodo\":\"").append(colorMetodo != null ? colorMetodo : "#6B7280").append("\",");
        sb.append("\"iconoMetodo\":\"").append(iconoMetodo != null ? iconoMetodo : "ti ti-credit-card").append("\",");
        sb.append("\"fechaPago\":\"").append(getFechaPagoFormateada()).append("\",");
        sb.append("\"fechaValidacion\":\"").append(getFechaValidacionFormateada()).append("\",");
        sb.append("\"validador\":\"").append(getValidadorLegible()).append("\",");
        sb.append("\"registrador\":\"").append(getRegistradorLegible()).append("\",");
        sb.append("\"observaciones\":\"").append(observaciones != null ? observaciones : "").append("\",");
        sb.append("\"tieneComprobante\":").append(tieneComprobante()).append(",");
        sb.append("\"comprobanteValidado\":").append(isComprobanteValidado()).append(",");
        sb.append("\"tipoComprobante\":\"").append(tipoComprobante != null ? tipoComprobante : "").append("\",");
        sb.append("\"numeroComprobante\":\"").append(numeroComprobante != null ? numeroComprobante : "").append("\"");
        if (nombreProyecto != null) {
            sb.append(",\"proyecto\":\"").append(nombreProyecto).append("\"");
        }
        if (nombreCliente != null) {
            sb.append(",\"cliente\":\"").append(nombreCliente).append("\"");
        }
        if (montoTotalProyecto != null) {
            sb.append(",\"montoTotalProyecto\":").append(montoTotalProyecto);
            sb.append(",\"saldoPendiente\":").append(saldoPendiente != null ? saldoPendiente : 0);
            sb.append(",\"porcentajePagado\":").append(porcentajePagado != null ? porcentajePagado : 0);
            sb.append(",\"esPagoCompleto\":").append(isPagoCompleto());
        }
        sb.append("}");
        return sb.toString();
    }

    // ============================================
    // MÉTODOS OVERRIDE
    // ============================================

    @Override
    public String toString() {
        return "PagoDTO{" +
                "idPago=" + idPago +
                ", idProyecto=" + idProyecto +
                ", monto=" + monto +
                ", metodoPago='" + metodoPago + '\'' +
                ", estadoPago='" + estadoPago + '\'' +
                ", nombreProyecto='" + nombreProyecto + '\'' +
                ", nombreCliente='" + nombreCliente + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PagoDTO pagoDTO = (PagoDTO) o;
        return idPago != null && idPago.equals(pagoDTO.idPago);
    }

    @Override
    public int hashCode() {
        return idPago != null ? idPago.hashCode() : 0;
    }

    // ============================================
    // MÉTODOS ESTÁTICOS DE FÁBRICA
    // ============================================

    /**
     * Crea un DTO desde una entidad.
     * 
     * @param pago Entidad de pago
     * @return DTO de pago
     */
    public static PagoDTO fromEntity(com.stamos.model.Pago pago) {
        if (pago == null) {
            return null;
        }

        PagoDTO dto = new PagoDTO();
        dto.setIdPago(pago.getIdPago());
        dto.setIdProyecto(pago.getIdProyecto());
        dto.setMonto(pago.getMonto());
        dto.setFechaPago(pago.getFechaPago());
        dto.setMetodoPago(pago.getMetodoPago());
        dto.setEstadoPago(pago.getEstadoPago());
        dto.setObservaciones(pago.getObservaciones());
        dto.setFechaRegistro(pago.getFechaRegistro());
        dto.setFechaActualizacion(pago.getFechaActualizacion());
        dto.setIdUsuarioRegistro(pago.getIdUsuarioRegistro());
        dto.setIdUsuarioActualizacion(pago.getIdUsuarioActualizacion());
        dto.setIdUsuarioValidacion(pago.getIdUsuarioValidacion());
        dto.setFechaValidacion(pago.getFechaValidacion());
        
        // Campos transient
        dto.setNombreProyecto(pago.getNombreProyecto());
        dto.setCodigoProyecto(pago.getCodigoProyecto());
        dto.setMontoTotalProyecto(pago.getMontoTotalProyecto());
        dto.setNombreCliente(pago.getNombreCliente());
        dto.setEmailCliente(pago.getEmailCliente());
        dto.setRucCliente(pago.getRucCliente());
        dto.setNombreUsuarioRegistro(pago.getNombreUsuarioRegistro());
        dto.setNombreUsuarioValidacion(pago.getNombreUsuarioValidacion());
        
        // Actualizar campos de estado
        dto.actualizarCamposEstado();
        dto.calcularMetricasFinancieras();
        
        return dto;
    }

    /**
     * Crea un DTO desde una entidad con comprobante.
     * 
     * @param pago Entidad de pago
     * @param comprobante Entidad de comprobante
     * @return DTO de pago
     */
    public static PagoDTO fromEntityWithComprobante(com.stamos.model.Pago pago,
                                                    com.stamos.model.ComprobantePago comprobante) {
        PagoDTO dto = fromEntity(pago);
        if (dto != null && comprobante != null) {
            dto.setTipoComprobante(comprobante.getTipoComprobante());
            dto.setNumeroComprobante(comprobante.getNumeroComprobante());
            dto.setArchivoComprobanteUrl(comprobante.getArchivoUrl());
            dto.setComprobanteValidado(comprobante.getValidado());
        }
        return dto;
    }

    /**
     * Crea un DTO para un nuevo pago.
     * 
     * @param idProyecto ID del proyecto
     * @param monto Monto del pago
     * @param metodoPago Método de pago
     * @return DTO de pago
     */
    public static PagoDTO crearNuevoPago(Integer idProyecto, BigDecimal monto, String metodoPago) {
        PagoDTO dto = new PagoDTO();
        dto.setIdProyecto(idProyecto);
        dto.setMonto(monto);
        dto.setMetodoPago(metodoPago);
        dto.setEstadoPago("Pendiente");
        dto.setFechaPago(LocalDateTime.now());
        dto.actualizarCamposEstado();
        return dto;
    }

    /**
     * Convierte el DTO a una entidad.
     * 
     * @return Entidad de pago
     */
    public com.stamos.model.Pago toEntity() {
        com.stamos.model.Pago pago = new com.stamos.model.Pago();
        if (this.idPago != null) {
            pago.setIdPago(this.idPago);
        }
        pago.setIdProyecto(this.idProyecto);
        pago.setMonto(this.monto);
        pago.setFechaPago(this.fechaPago != null ? this.fechaPago : LocalDateTime.now());
        pago.setMetodoPago(this.metodoPago);
        pago.setEstadoPago(this.estadoPago != null ? this.estadoPago : "Pendiente");
        pago.setObservaciones(this.observaciones);
        pago.setFechaRegistro(this.fechaRegistro);
        pago.setFechaActualizacion(this.fechaActualizacion);
        pago.setIdUsuarioRegistro(this.idUsuarioRegistro);
        pago.setIdUsuarioActualizacion(this.idUsuarioActualizacion);
        pago.setIdUsuarioValidacion(this.idUsuarioValidacion);
        pago.setFechaValidacion(this.fechaValidacion);
        return pago;
    }
}