package com.stamos.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PagoDTO {

    private Integer idPago;
    private Integer idProyecto;
    private BigDecimal monto;
    private LocalDateTime fechaPago;
    private String metodoPago;
    private String estadoPago;
    private String observaciones;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaActualizacion;
    private Integer idUsuarioRegistro;
    private Integer idUsuarioActualizacion;
    private Integer idUsuarioValidacion;
    private LocalDateTime fechaValidacion;
    private String nombreUsuarioRegistro;
    private String nombreUsuarioValidacion;
    private String nombreProyecto;
    private String codigoProyecto;
    private BigDecimal montoTotalProyecto;
    private String nombreCliente;
    private String emailCliente;
    private String rucCliente;
    private String telefonoCliente;
    private BigDecimal saldoPendiente;
    private Integer porcentajePagado;
    private Boolean esPagoCompleto;
    private String metodoPagoLegible;
    private String estadoPagoLegible;
    private String colorEstado;
    private String iconoEstado;
    private String colorMetodo;
    private String iconoMetodo;
    private String numeroComprobante;
    private String tipoComprobante;
    private String archivoComprobanteUrl;
    private Boolean comprobanteValidado;

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

    public PagoDTO(Integer idPago, Integer idProyecto, BigDecimal monto,
                  LocalDateTime fechaPago, String metodoPago, String estadoPago,
                  String nombreProyecto, String nombreCliente) {
        this(idPago, idProyecto, monto, metodoPago, estadoPago);
        this.fechaPago = fechaPago;
        this.nombreProyecto = nombreProyecto;
        this.nombreCliente = nombreCliente;
        actualizarCamposEstado();
    }

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

    private void actualizarCamposEstado() {
        if (estadoPago != null) {
            switch (estadoPago) {
                case "Pendiente" -> {
                    this.estadoPagoLegible = "Pendiente de validación";
                    this.colorEstado = "#F59E0B";
                    this.iconoEstado = "ti ti-clock";
                }
                case "Validado" -> {
                    this.estadoPagoLegible = "Validado";
                    this.colorEstado = "#10B981";
                    this.iconoEstado = "ti ti-check-circle";
                }
                case "Rechazado" -> {
                    this.estadoPagoLegible = "Rechazado";
                    this.colorEstado = "#EF4444";
                    this.iconoEstado = "ti ti-x-circle";
                }
                default -> {
                    this.estadoPagoLegible = estadoPago;
                    this.colorEstado = "#6B7280";
                    this.iconoEstado = "ti ti-circle";
                }
            }
        }

        if (metodoPago != null) {
            switch (metodoPago) {
                case "Efectivo" -> {
                    this.metodoPagoLegible = "Efectivo";
                    this.colorMetodo = "#10B981";
                    this.iconoMetodo = "ti ti-coin";
                }
                case "Transferencia" -> {
                    this.metodoPagoLegible = "Transferencia";
                    this.colorMetodo = "#3B82F6";
                    this.iconoMetodo = "ti ti-transfer";
                }
                case "Tarjeta" -> {
                    this.metodoPagoLegible = "Tarjeta";
                    this.colorMetodo = "#8B5CF6";
                    this.iconoMetodo = "ti ti-credit-card";
                }
                case "Cheque" -> {
                    this.metodoPagoLegible = "Cheque";
                    this.colorMetodo = "#F59E0B";
                    this.iconoMetodo = "ti ti-file";
                }
                default -> {
                    this.metodoPagoLegible = metodoPago;
                    this.colorMetodo = "#6B7280";
                    this.iconoMetodo = "ti ti-credit-card";
                }
            }
        }
    }

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

    public boolean isPendiente() {
        return "Pendiente".equals(this.estadoPago);
    }

    public boolean isValidado() {
        return "Validado".equals(this.estadoPago);
    }

    public boolean isRechazado() {
        return "Rechazado".equals(this.estadoPago);
    }

    public boolean puedeValidar() {
        return isPendiente();
    }

    public boolean puedeRechazar() {
        return isPendiente();
    }

    public boolean esEfectivo() {
        return "Efectivo".equals(this.metodoPago);
    }

    public boolean esTransferencia() {
        return "Transferencia".equals(this.metodoPago);
    }

    public boolean esTarjeta() {
        return "Tarjeta".equals(this.metodoPago);
    }

    public boolean esCheque() {
        return "Cheque".equals(this.metodoPago);
    }

    public boolean tieneObservaciones() {
        return observaciones != null && !observaciones.trim().isEmpty();
    }

    public boolean tieneComprobante() {
        return numeroComprobante != null && !numeroComprobante.trim().isEmpty();
    }

    public boolean isComprobanteValidado() {
        return comprobanteValidado != null && comprobanteValidado;
    }

    public boolean isPagoCompleto() {
        return esPagoCompleto != null && esPagoCompleto;
    }

    public String getEstadoLegible() {
        return estadoPagoLegible != null ? estadoPagoLegible : estadoPago;
    }

    public String getMetodoLegible() {
        return metodoPagoLegible != null ? metodoPagoLegible : metodoPago;
    }

    public static PagoDTO fromEntity(com.stamos.model.Pago pago, 
                                     com.stamos.model.Proyecto proyecto,
                                     com.stamos.model.Usuario usuarioRegistro,
                                     com.stamos.model.Usuario usuarioValidacion) {
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
        dto.setNombreProyecto(pago.getNombreProyecto());

        if (proyecto != null) {
            dto.setCodigoProyecto(proyecto.getCodigoProyecto());
            dto.setMontoTotalProyecto(proyecto.getMontoTotal());
            dto.setNombreCliente(proyecto.getNombreCliente());
        }

        if (usuarioRegistro != null) {
            dto.setNombreUsuarioRegistro(usuarioRegistro.getNombreCompleto());
        }

        if (usuarioValidacion != null) {
            dto.setNombreUsuarioValidacion(usuarioValidacion.getNombreCompleto());
        }

        dto.actualizarCamposEstado();
        dto.calcularMetricasFinancieras();

        return dto;
    }

    public static PagoDTO fromEntityWithComprobante(com.stamos.model.Pago pago,
                                                    com.stamos.model.Proyecto proyecto,
                                                    com.stamos.model.Usuario usuarioRegistro,
                                                    com.stamos.model.Usuario usuarioValidacion,
                                                    com.stamos.model.ComprobantePago comprobante) {
        PagoDTO dto = fromEntity(pago, proyecto, usuarioRegistro, usuarioValidacion);
        if (dto != null && comprobante != null) {
            dto.setTipoComprobante(comprobante.getTipoComprobante());
            dto.setNumeroComprobante(comprobante.getNumeroComprobante());
            dto.setArchivoComprobanteUrl(comprobante.getArchivoUrl());
            dto.setComprobanteValidado(comprobante.getValidado());
        }
        return dto;
    }

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
}