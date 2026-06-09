package com.stamos.dto;

public class ClienteDTO {

    private Long id;
    private String nombre;
    private String ruc;

    public ClienteDTO() {
    }

    public ClienteDTO(Long id, String nombre, String ruc) {
        this.id = id;
        this.nombre = nombre;
        this.ruc = ruc;
    }

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

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    @Override
    public String toString() {
        return "ClienteDTO{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", ruc='" + ruc + '\'' +
                '}';
    }
}