package com.stamos.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa un archivo adjunto a un proyecto.
 * Puede ser: renders 3D, planos técnicos, bocetos, etc.
 */
@Entity
@Table(name = "archivo_proyecto")
public class ArchivoProyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @Column(name = "nombre_archivo", nullable = false)
    private String nombreArchivo;

    @Column(name = "tipo", nullable = false)
    private String tipo; // RENDER, PLANO, BOCETO, etc.

    @Lob
    @Column(name = "contenido", nullable = false)
    private byte[] contenido;

    @Column(name = "fecha_subida", nullable = false)
    private LocalDateTime fechaSubida;

    // Constructores
    public ArchivoProyecto() {
    }

    public ArchivoProyecto(Proyecto proyecto, String nombreArchivo, String tipo, byte[] contenido, LocalDateTime fechaSubida) {
        this.proyecto = proyecto;
        this.nombreArchivo = nombreArchivo;
        this.tipo = tipo;
        this.contenido = contenido;
        this.fechaSubida = fechaSubida;
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

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public byte[] getContenido() {
        return contenido;
    }

    public void setContenido(byte[] contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }
}