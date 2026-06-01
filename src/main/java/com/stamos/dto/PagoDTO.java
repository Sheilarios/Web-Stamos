package com.stamos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para transferir datos básicos de un pago.
 * Utilizado en listados y búsquedas para evitar enviar entidades completas.
 */
public class PagoDTO {

    private Long id;
    private Long proyectoId;
    private String proyectoNombre;
    private LocalDate fecha;
    private BigDecimal monto;
    private String medioPago;
    private String estado;

    // Constructor por defecto
    public PagoDTO() {
    }

    // Constructor con campos principales
    public PagoDTO(Long id, Long proyectoId, String proyectoNombre, 
                   LocalDate fecha, BigDecimal monto, 
                   String medioPago, String estado) {
        this.id = id;
        this.proyectoId = proyectoId;
        this.proyectoNombre = proyectoNombre;
        this.fecha = fecha;
        this.monto = monto;
        this.medioPago = medioPago;
        this.estado = estado;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProyectoId() {
        return proyectoId;
    }

    public void setProyectoId(Long proyectoId) {
        this.proyectoId = proyectoId;
    }

    public String getProyectoNombre() {
        return proyectoNombre;
    }

    public void setProyectoNombre(String proyectoNombre) {
        this.proyectoNombre = proyectoNombre;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(String medioPago) {
        this.medioPago = medioPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "PagoDTO{" +
                "id=" + id +
                ", proyectoId=" + proyectoId +
                ", proyectoNombre='" + proyectoNombre + '\'' +
                ", fecha=" + fecha +
                ", monto=" + monto +
                ", medioPago='" + medioPago + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}