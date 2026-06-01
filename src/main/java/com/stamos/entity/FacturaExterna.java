package com.stamos.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidad que representa una factura externa emitida por el gestor contable.
 * La empresa utiliza un gestor externo para emitir facturas electrónicas ante SUNAT.
 * El sistema solo almacena la referencia a la factura oficial (número, fecha, etc.)
 * y la vincula con un proyecto o pago interno.
 * 
 * Nota: No se genera la factura desde el sistema, solo se registra la emitida externamente.
 */
@Entity
@Table(name = "factura_externa")
public class FacturaExterna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_factura", nullable = false, unique = true, length = 20)
    private String numeroFactura;   // Número de factura emitido por el gestor (ej. F001-123)

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "monto_total", nullable = false, precision = 12, scale = 2)
    private java.math.BigDecimal montoTotal;

    @Column(length = 50)
    private String moneda = "PEN";

    @Column(name = "ruc_emisor", length = 11)
    private String rucEmisor;       // RUC del gestor o de la propia empresa (según configuración)

    @Column(name = "archivo_pdf_url", length = 500)
    private String archivoPdfUrl;   // Ruta o URL del archivo PDF de la factura (opcional)

    // Relación opcional con un proyecto (la factura puede referirse a un proyecto)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id")
    private Proyecto proyecto;

    // Relación opcional con un pago (la factura puede referirse a un pago específico)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pago_id")
    private Pago pago;

    // Fecha de registro en el sistema
    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    // Constructor por defecto
    public FacturaExterna() {
    }

    // Constructor con campos mínimos
    public FacturaExterna(String numeroFactura, LocalDate fechaEmision, java.math.BigDecimal montoTotal) {
        this.numeroFactura = numeroFactura;
        this.fechaEmision = fechaEmision;
        this.montoTotal = montoTotal;
        this.fechaRegistro = LocalDate.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public java.math.BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(java.math.BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getRucEmisor() {
        return rucEmisor;
    }

    public void setRucEmisor(String rucEmisor) {
        this.rucEmisor = rucEmisor;
    }

    public String getArchivoPdfUrl() {
        return archivoPdfUrl;
    }

    public void setArchivoPdfUrl(String archivoPdfUrl) {
        this.archivoPdfUrl = archivoPdfUrl;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FacturaExterna that = (FacturaExterna) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "FacturaExterna{" +
                "id=" + id +
                ", numeroFactura='" + numeroFactura + '\'' +
                ", fechaEmision=" + fechaEmision +
                ", montoTotal=" + montoTotal +
                '}';
    }
}