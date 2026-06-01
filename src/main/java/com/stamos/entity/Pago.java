package com.stamos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidad que representa un pago realizado por un cliente para un proyecto.
 * 
 * Reglas de negocio:
 * - RPA01: Los pagos solo pueden registrarse para proyectos en estados específicos (validado en controller).
 * - RPA02: Se aceptan pagos parciales, el saldo pendiente se calcula dinámicamente.
 * - RPA03: El total de pagos no puede superar el monto de la cotización asociada (validado en controller).
 * - RPA04: Cada pago incluye fecha, monto, medio de pago y opcionalmente número de comprobante.
 * - RPA05: Solo ADMIN o COMERCIAL puede registrar o anular pagos (seguridad en controller).
 * - RPA06: Al anular un pago se registra motivo (vía auditoría).
 */
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
    private String medioPago; // EFECTIVO, TRANSFERENCIA, DEPOSITO

    @Column(name = "comprobante_numero", length = 50)
    private String comprobanteNumero; // Número de factura/boleta (opcional)

    @Column(nullable = false, length = 20)
    private String estado = "REGISTRADO"; // REGISTRADO, ANULADO

    // Relación opcional con factura externa (emitida por gestor)
    @OneToOne(mappedBy = "pago", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private FacturaExterna facturaExterna;

    // Constructor por defecto
    public Pago() {
    }

    // Constructor con campos obligatorios
    public Pago(Proyecto proyecto, LocalDate fecha, BigDecimal monto, String medioPago) {
        this.proyecto = proyecto;
        this.fecha = fecha;
        this.monto = monto;
        this.medioPago = medioPago;
    }

    // Getters y Setters
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

    // equals y hashCode
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
}