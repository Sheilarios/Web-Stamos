package com.stamos.dto;
import java.time.LocalDate;
public class ProyectoDTO {

    private Long id;
    private String clienteNombre;
    private String estado;
    private LocalDate fechaCreacion;
    private LocalDate fechaEntregaEstimada;
    private String responsableNombre;
    private Long clienteId;

    public ProyectoDTO() {
    }

    public ProyectoDTO(Long id, String clienteNombre, String estado, 
                       LocalDate fechaCreacion, LocalDate fechaEntregaEstimada, 
                       String responsableNombre, Long clienteId) {
        this.id = id;
        this.clienteNombre = clienteNombre;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaEntregaEstimada = fechaEntregaEstimada;
        this.responsableNombre = responsableNombre;
        this.clienteId = clienteId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
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

    public String getResponsableNombre() {
        return responsableNombre;
    }

    public void setResponsableNombre(String responsableNombre) {
        this.responsableNombre = responsableNombre;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    @Override
    public String toString() {
        return "ProyectoDTO{" +
                "id=" + id +
                ", clienteNombre='" + clienteNombre + '\'' +
                ", estado='" + estado + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaEntregaEstimada=" + fechaEntregaEstimada +
                ", responsableNombre='" + responsableNombre + '\'' +
                ", clienteId=" + clienteId +
                '}';
    }
}