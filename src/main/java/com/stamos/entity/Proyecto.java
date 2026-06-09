package com.stamos.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "proyecto")
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La cotización es obligatoria")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cotizacion_id", nullable = false, unique = true)
    private Cotizacion cotizacion;

    @NotNull(message = "El cliente es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @NotBlank(message = "El estado es obligatorio")
    @Column(nullable = false, length = 30)
    private String estado; 

    @NotNull(message = "La fecha de creación es obligatoria")
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    @Column(name = "fecha_entrega_estimada")
    private LocalDate fechaEntregaEstimada;

    @Column(name = "fecha_entrega_real")
    private LocalDate fechaEntregaReal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_id")
    private Usuario responsable; 

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pago> pagos = new ArrayList<>();

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReservaEquipo> reservasEquipos = new ArrayList<>();

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ArchivoProyecto> archivos = new ArrayList<>();

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HistorialEstadoProyecto> historialEstados = new ArrayList<>();

    @OneToOne(mappedBy = "proyecto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private FacturaExterna facturaExterna;

    public Proyecto() {
    }

    public Proyecto(Cotizacion cotizacion, Cliente cliente, String estado, LocalDate fechaCreacion) {
        this.cotizacion = cotizacion;
        this.cliente = cliente;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cotizacion getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(Cotizacion cotizacion) {
        this.cotizacion = cotizacion;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDate getFechaEntregaEstimada() {
        return fechaEntregaEstimada;
    }

    public void setFechaEntregaEstimada(LocalDate fechaEntregaEstimada) {
        this.fechaEntregaEstimada = fechaEntregaEstimada;
    }

    public LocalDate getFechaEntregaReal() {
        return fechaEntregaReal;
    }

    public void setFechaEntregaReal(LocalDate fechaEntregaReal) {
        this.fechaEntregaReal = fechaEntregaReal;
    }

    public Usuario getResponsable() {
        return responsable;
    }

    public void setResponsable(Usuario responsable) {
        this.responsable = responsable;
    }

    public List<Pago> getPagos() {
        return pagos;
    }

    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
    }

    public List<ReservaEquipo> getReservasEquipos() {
        return reservasEquipos;
    }

    public void setReservasEquipos(List<ReservaEquipo> reservasEquipos) {
        this.reservasEquipos = reservasEquipos;
    }

    public List<ArchivoProyecto> getArchivos() {
        return archivos;
    }

    public void setArchivos(List<ArchivoProyecto> archivos) {
        this.archivos = archivos;
    }

    public List<HistorialEstadoProyecto> getHistorialEstados() {
        return historialEstados;
    }

    public void setHistorialEstados(List<HistorialEstadoProyecto> historialEstados) {
        this.historialEstados = historialEstados;
    }

    public FacturaExterna getFacturaExterna() {
        return facturaExterna;
    }

    public void setFacturaExterna(FacturaExterna facturaExterna) {
        this.facturaExterna = facturaExterna;
    }

    public void addPago(Pago pago) {
        pagos.add(pago);
        pago.setProyecto(this);
    }

    public void addReservaEquipo(ReservaEquipo reserva) {
        reservasEquipos.add(reserva);
        reserva.setProyecto(this);
    }

    public void addArchivo(ArchivoProyecto archivo) {
        archivos.add(archivo);
        archivo.setProyecto(this);
    }

    public void addHistorialEstado(HistorialEstadoProyecto historial) {
        historialEstados.add(historial);
        historial.setProyecto(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proyecto proyecto = (Proyecto) o;
        return Objects.equals(id, proyecto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Proyecto{" +
                "id=" + id +
                ", cotizacion=" + (cotizacion != null ? cotizacion.getId() : null) +
                ", cliente=" + (cliente != null ? cliente.getId() : null) +
                ", estado='" + estado + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}