package com.stamos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entidad que representa un producto o servicio en el catálogo de STAMOS.
 * Los ítems de cotización pueden partir de estos registros (con precio base)
 * o pueden ser conceptos personalizados con precio negociado.
 */
@Entity
@Table(name = "catalogo_producto")
public class CatalogoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(name = "precio_base", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioBase;

    // Constructor por defecto
    public CatalogoProducto() {
    }

    // Constructor con campos mínimos
    public CatalogoProducto(String nombre, BigDecimal precioBase) {
        this.nombre = nombre;
        this.precioBase = precioBase;
    }

    // Constructor completo
    public CatalogoProducto(String nombre, String descripcion, BigDecimal precioBase) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioBase = precioBase;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CatalogoProducto that = (CatalogoProducto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CatalogoProducto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", precioBase=" + precioBase +
                '}';
    }
}