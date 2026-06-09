package com.stamos.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Entity
@Table(name = "cotizacion")
public class Cotizacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "El cliente es obligatorio")
    
    @ManyToOne(fetch = FetchType.LAZY)
    
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @NotNull(message = "La fecha de emisión es obligatoria")
    
    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;
    
    @NotNull(message = "La validez en días es obligatoria")
    
    @Positive(message = "La validez debe ser mayor a cero")
    
    @Column(name = "validez_dias", nullable = false)
    private Integer validezDias;
    
    @NotBlank(message = "El estado es obligatorio")
    
    @Column(nullable = false, length = 20)
    private String estado; 
    
    @Column(nullable = false)
    private Integer version = 1;
    
    @ManyToOne(fetch = FetchType.LAZY)
    
    @JoinColumn(name = "version_base_id")
    private Cotizacion versionBase; 
    
    @OneToMany(mappedBy = "cotizacion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DetalleCotizacion> items = new ArrayList<>();

    @Column(precision = 12, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal igv = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    public Cotizacion() {
    }

    public Cotizacion(Cliente cliente, LocalDate fechaEmision, Integer validezDias, String estado) {
        this.cliente = cliente;
        this.fechaEmision = fechaEmision;
        this.validezDias = validezDias;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public Integer getValidezDias() {
        return validezDias;
    }

    public void setValidezDias(Integer validezDias) {
        this.validezDias = validezDias;
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

    public Cotizacion getVersionBase() {
        return versionBase;
    }

    public void setVersionBase(Cotizacion versionBase) {
        this.versionBase = versionBase;
    }

    public List<DetalleCotizacion> getItems() {
        return items;
    }

    public void setItems(List<DetalleCotizacion> items) {
        this.items = items;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getIgv() {
        return igv;
    }

    public void setIgv(BigDecimal igv) {
        this.igv = igv;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public void addItem(DetalleCotizacion item) {
        items.add(item);
        item.setCotizacion(this);
    }

    public void removeItem(DetalleCotizacion item) {
        items.remove(item);
        item.setCotizacion(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cotizacion that = (Cotizacion) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Cotizacion{" +
                "id=" + id +
                ", cliente=" + (cliente != null ? cliente.getId() : null) +
                ", fechaEmision=" + fechaEmision +
                ", estado='" + estado + '\'' +
                ", version=" + version +
                ", total=" + total +
                '}';
    }
}