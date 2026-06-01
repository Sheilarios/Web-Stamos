package com.stamos.dto;

import java.math.BigDecimal;

/**
 * DTO para transferir datos de un detalle de cotización (ítem).
 * Utilizado en formularios y listados de cotizaciones.
 */
public class DetalleCotizacionDTO {

    private Long id;
    private Long cotizacionId;
    private String concepto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal; // calculado: cantidad * precioUnitario

    // Constructor por defecto
    public DetalleCotizacionDTO() {
    }

    // Constructor con campos principales
    public DetalleCotizacionDTO(Long id, Long cotizacionId, String concepto, 
                                Integer cantidad, BigDecimal precioUnitario) {
        this.id = id;
        this.cotizacionId = cotizacionId;
        this.concepto = concepto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = calcularSubtotal();
    }

    // Método auxiliar para calcular subtotal
    private BigDecimal calcularSubtotal() {
        if (cantidad == null || precioUnitario == null) {
            return BigDecimal.ZERO;
        }
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCotizacionId() {
        return cotizacionId;
    }

    public void setCotizacionId(Long cotizacionId) {
        this.cotizacionId = cotizacionId;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        this.subtotal = calcularSubtotal();
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        this.subtotal = calcularSubtotal();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    @Override
    public String toString() {
        return "DetalleCotizacionDTO{" +
                "id=" + id +
                ", cotizacionId=" + cotizacionId +
                ", concepto='" + concepto + '\'' +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + subtotal +
                '}';
    }
}