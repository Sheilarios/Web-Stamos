package com.stamos.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "archivos_proyecto")
public class ArchivoProyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;

    @Column(name = "ruta", length = 500)
    private String ruta;

    @Column(name = "tamano")
    private Long tamano;

    @Lob
    @Column(name = "contenido", columnDefinition = "LONGBLOB")
    private byte[] contenido;

    @Column(name = "fecha_subida", nullable = false)
    private LocalDateTime fechaSubida;

    @Column(name = "subido_por")
    private String subidoPor;

    public ArchivoProyecto() {
    }

    public ArchivoProyecto(Proyecto proyecto, String nombre, String tipo, 
                           byte[] contenido, LocalDateTime fechaSubida) {
        this.proyecto = proyecto;
        this.nombre = nombre;
        this.tipo = tipo;
        this.contenido = contenido;
        this.fechaSubida = fechaSubida;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public Long getTamano() {
        return tamano;
    }

    public void setTamano(Long tamano) {
        this.tamano = tamano;
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

    public String getSubidoPor() {
        return subidoPor;
    }

    public void setSubidoPor(String subidoPor) {
        this.subidoPor = subidoPor;
    }

    public void setNombreArchivo(String originalFilename) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}