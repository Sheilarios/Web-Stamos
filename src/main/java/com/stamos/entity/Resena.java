package com.stamos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa una reseña o calificación de un cliente sobre un proyecto.
 * 
 * Reglas de negocio:
 * - RF-30: El cliente puede calificar y comentar el servicio una vez que el proyecto esté entregado.
 * - RNF-09: Las reseñas quedan pendientes de aprobación por el administrador antes de publicarse.
 */
@Entity
@Table(name = "resena")
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El cliente es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @NotNull(message = "El proyecto es obligatorio")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", nullable = false, unique = true)
    private Proyecto proyecto;

    @Min(value = 1, message = "La calificación debe ser al menos 1 estrella")
    @Max(value = 5, message = "La calificación no puede ser mayor a 5 estrellas")
    @Column(nullable = false)
    private int calificacion;

    @Column(length = 1000)
    private String comentario;

    @NotBlank(message = "El estado es obligatorio")
    @Column(nullable = false, length = 20)
    private String estado = "PENDIENTE"; // PENDIENTE, APROBADA, RECHAZADA

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    // Constructor por defecto
    public Resena() {
    }

    // Constructor con campos mínimos
    public Resena(Cliente cliente, Proyecto proyecto, int calificacion, String comentario) {
        this.cliente = cliente;
        this.proyecto = proyecto;
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.fechaCreacion = LocalDateTime.now();
    }

    // Getters y Setters
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

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resena resena = (Resena) o;
        return Objects.equals(id, resena.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Resena{" +
                "id=" + id +
                ", cliente=" + (cliente != null ? cliente.getId() : null) +
                ", proyecto=" + (proyecto != null ? proyecto.getId() : null) +
                ", calificacion=" + calificacion +
                ", estado='" + estado + '\'' +
                '}';
    }
}