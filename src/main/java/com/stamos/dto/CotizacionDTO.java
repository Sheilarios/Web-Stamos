package com.stamos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para transferir datos básicos de una cotización.
 * Utilizado en listados y búsquedas para evitar enviar entidades completas.
 */
public class CotizacionDTO {

    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private LocalDate fechaEmision;
    private String estado;
    private Integer version;
    private BigDecimal total;

    // Constructor por defecto
    public CotizacionDTO() {
    }

    // Constructor con campos principales
    public CotizacionDTO(Long id, Long clienteId, String clienteNombre, 
                         LocalDate fechaEmision, String estado, 
                         Integer version, BigDecimal total) {
        this.id = id;
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.fechaEmision = fechaEmision;
        this.estado = estado;
        this.version = version;
        this.total = total;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "CotizacionDTO{" +
                "id=" + id +
                ", clienteId=" + clienteId +
                ", clienteNombre='" + clienteNombre + '\'' +
                ", fechaEmision=" + fechaEmision +
                ", estado='" + estado + '\'' +
                ", version=" + version +
                ", total=" + total +
                '}';
    }
}