package com.stamos.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;


@Entity
@Table(name = "pago")
public class Pago {

    public static final String ESTADO_VERIFICACION = "VERIFICACION";


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El proyecto es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    private LocalDate fecha;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @NotBlank(message = "El medio de pago es obligatorio")
    @Column(name = "medio_pago", nullable = false, length = 20)
    private String medioPago; 

    @Column(name = "comprobante_numero", length = 50)
    private String comprobanteNumero;

    @Column(nullable = false, length = 20)
    private String estado = "REGISTRADO";

    @OneToOne(mappedBy = "pago", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private FacturaExterna facturaExterna;

    public Pago() {
    }

    public Pago(Proyecto proyecto, LocalDate fecha, BigDecimal monto, String medioPago) {
        this.proyecto = proyecto;
        this.fecha = fecha;
        this.monto = monto;
        this.medioPago = medioPago;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
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

    public String getComprobanteNumero() {
        return comprobanteNumero;
    }

    public void setComprobanteNumero(String comprobanteNumero) {
        this.comprobanteNumero = comprobanteNumero;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public FacturaExterna getFacturaExterna() {
        return facturaExterna;
    }

    public void setFacturaExterna(FacturaExterna facturaExterna) {
        this.facturaExterna = facturaExterna;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pago pago = (Pago) o;
        return Objects.equals(id, pago.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Pago{" +
                "id=" + id +
                ", proyecto=" + (proyecto != null ? proyecto.getId() : null) +
                ", fecha=" + fecha +
                ", monto=" + monto +
                ", medioPago='" + medioPago + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }

    public void setNumeroOperacion(String numeroOperacion) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    public void setFechaRegistro(LocalDate now) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object getFechaRegistro() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}