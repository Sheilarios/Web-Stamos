package com.stamos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "Pago")
public class Pago {

    public static final String ESTADO_PENDIENTE = "Pendiente";
    public static final String ESTADO_VALIDADO = "Validado";
    public static final String ESTADO_RECHAZADO = "Rechazado";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer idPago;

    @NotNull(message = "El proyecto es obligatorio")
    @Column(name = "id_proyecto", nullable = false)
    private Integer idProyecto;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a 0")
    @Column(name = "monto", nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @NotNull(message = "El método de pago es obligatorio")
    @Column(name = "metodo_pago", nullable = false)
    private String metodoPago;

    @Column(name = "estado_pago", nullable = false)
    private String estadoPago = ESTADO_PENDIENTE;

    @Size(max = 500, message = "Las observaciones no pueden exceder los 500 caracteres")
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "id_usuario_registro")
    private Integer idUsuarioRegistro;

    @Column(name = "id_usuario_actualizacion")
    private Integer idUsuarioActualizacion;

    @Column(name = "id_usuario_validacion")
    private Integer idUsuarioValidacion;

    @Column(name = "fecha_validacion")
    private LocalDateTime fechaValidacion;

    @Transient
    private String nombreProyecto;

    @Transient
    private String metodoPagoLegible;

    @Transient
    private String estadoPagoLegible;

    @Transient
    private String colorEstado;

    public Pago() {
        this.estadoPago = ESTADO_PENDIENTE;
        this.fechaPago = LocalDateTime.now();
        this.fechaRegistro = LocalDateTime.now();
        this.monto = BigDecimal.ZERO;
    }

    public Pago(Integer idProyecto, BigDecimal monto, String metodoPago) {
        this();
        this.idProyecto = idProyecto;
        this.monto = monto;
        this.metodoPago = metodoPago;
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
        actualizarMetodoPagoLegible(metodoPago);
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
        actualizarEstadoPagoLegible(estadoPago);
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

    public String getNombreProyecto() {
        return nombreProyecto;
    }

    public void setNombreProyecto(String nombreProyecto) {
        this.nombreProyecto = nombreProyecto;
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

    private void actualizarMetodoPagoLegible(String metodo) {
        if (metodo == null) {
            this.metodoPagoLegible = "No especificado";
            return;
        }
        switch (metodo) {
            case "Efectivo":
                this.metodoPagoLegible = "Efectivo";
                break;
            case "Transferencia":
                this.metodoPagoLegible = "Transferencia";
                break;
            case "Tarjeta":
                this.metodoPagoLegible = "Tarjeta";
                break;
            case "Cheque":
                this.metodoPagoLegible = "Cheque";
                break;
            default:
                this.metodoPagoLegible = metodo;
                break;
        }
    }

    private void actualizarEstadoPagoLegible(String estado) {
        if (estado == null) {
            this.estadoPagoLegible = "Pendiente";
            this.colorEstado = "#F59E0B";
            return;
        }
        switch (estado) {
            case ESTADO_PENDIENTE -> {
                this.estadoPagoLegible = "Pendiente";
                this.colorEstado = "#F59E0B";
            }
            case ESTADO_VALIDADO -> {
                this.estadoPagoLegible = "Validado";
                this.colorEstado = "#10B981";
            }
            case ESTADO_RECHAZADO -> {
                this.estadoPagoLegible = "Rechazado";
                this.colorEstado = "#EF4444";
            }
            default -> {
                this.estadoPagoLegible = estado;
                this.colorEstado = "#6B7280";
            }
        }
    }

    public boolean isPendiente() {
        return ESTADO_PENDIENTE.equals(this.estadoPago);
    }

    public boolean isValidado() {
        return ESTADO_VALIDADO.equals(this.estadoPago);
    }

    public boolean isRechazado() {
        return ESTADO_RECHAZADO.equals(this.estadoPago);
    }

    public void validar(Integer idUsuario) {
        this.estadoPago = ESTADO_VALIDADO;
        this.idUsuarioValidacion = idUsuario;
        this.fechaValidacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        actualizarEstadoPagoLegible(this.estadoPago);
    }

    public void rechazar(Integer idUsuario) {
        this.estadoPago = ESTADO_RECHAZADO;
        this.idUsuarioValidacion = idUsuario;
        this.fechaValidacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        actualizarEstadoPagoLegible(this.estadoPago);
    }

    public String getMontoFormateado() {
        if (monto == null) return "S/ 0.00";
        return "S/ " + monto.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public String getFechaPagoFormateada() {
        if (fechaPago == null) return "-";
        return fechaPago.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getFechaValidacionFormateada() {
        if (fechaValidacion == null) return "-";
        return fechaValidacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getEstadoBadgeClass() {
        if (estadoPago == null) return "badge-warning";
        
        return switch (estadoPago) {
            case ESTADO_PENDIENTE -> "badge-warning";
            case ESTADO_VALIDADO -> "badge-success";
            case ESTADO_RECHAZADO -> "badge-danger";
            default -> "badge-secondary";
        };
    }

    public String getEstadoLegible() {
        return estadoPagoLegible != null ? estadoPagoLegible : estadoPago;
    }

    public String getMetodoLegible() {
        return metodoPagoLegible != null ? metodoPagoLegible : metodoPago;
    }

    @Override
    public String toString() {
        return "Pago{" +
                "idPago=" + idPago +
                ", idProyecto=" + idProyecto +
                ", monto=" + monto +
                ", estadoPago='" + estadoPago + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pago pago = (Pago) o;
        return idPago != null && idPago.equals(pago.idPago);
    }

    @Override
    public int hashCode() {
        return idPago != null ? idPago.hashCode() : 0;
    }
}