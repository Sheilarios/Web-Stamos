package com.stamos.entity;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Entidad que representa un equipo o mobiliario en el catálogo de inventario.
 * Cumple con la regla RIN01: nombre, cantidad total disponible y ubicación.
 */
@Entity
@Table(name = "catalogo_equipo")
public class CatalogoEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "cantidad_total", nullable = false)
    private Integer cantidadTotal;

    @Column(nullable = false, length = 200)
    private String ubicacion;

    // Constructor por defecto
    public CatalogoEquipo() {
    }

    // Constructor con campos obligatorios
    public CatalogoEquipo(String nombre, Integer cantidadTotal, String ubicacion) {
        this.nombre = nombre;
        this.cantidadTotal = cantidadTotal;
        this.ubicacion = ubicacion;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCantidadTotal() {
        return cantidadTotal;
    }

    public void setCantidadTotal(Integer cantidadTotal) {
        this.cantidadTotal = cantidadTotal;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CatalogoEquipo that = (CatalogoEquipo) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CatalogoEquipo{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", cantidadTotal=" + cantidadTotal +
                ", ubicacion='" + ubicacion + '\'' +
                '}';
    }
}