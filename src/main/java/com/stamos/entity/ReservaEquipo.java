package com.stamos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidad que representa una reserva de equipo (alquiler) para un proyecto.
 * 
 * Reglas de negocio:
 * - RPY06: Si el proyecto incluye alquiler de equipos, se debe registrar fecha de inicio y fin.
 * - RPY07: El sistema debe impedir solapamiento de reservas (control de disponibilidad).
 * - RIN02: Al asignar un equipo a un proyecto, se reduce la cantidad disponible durante el período.
 * - RIN03: No se puede asignar si la cantidad disponible es insuficiente.
 */
@Entity
@Table(name = "reserva_equipo")
public class ReservaEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El equipo es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_id", nullable = false)
    private CatalogoEquipo equipo;

    @NotNull(message = "El proyecto es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(nullable = false)
    private int cantidad;

    // Constructor por defecto
    public ReservaEquipo() {
    }

    // Constructor con campos obligatorios
    public ReservaEquipo(CatalogoEquipo equipo, Proyecto proyecto, LocalDate fechaInicio, LocalDate fechaFin, int cantidad) {
        this.equipo = equipo;
        this.proyecto = proyecto;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cantidad = cantidad;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CatalogoEquipo getEquipo() {
        return equipo;
    }

    public void setEquipo(CatalogoEquipo equipo) {
        this.equipo = equipo;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservaEquipo that = (ReservaEquipo) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ReservaEquipo{" +
                "id=" + id +
                ", equipo=" + (equipo != null ? equipo.getId() : null) +
                ", proyecto=" + (proyecto != null ? proyecto.getId() : null) +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", cantidad=" + cantidad +
                '}';
    }
}