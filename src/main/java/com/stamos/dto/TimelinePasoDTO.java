package com.stamos.dto;

import java.time.LocalDate;

public class TimelinePasoDTO {
    private String titulo;
    private String descripcion;
    private String estado;    
    private String estadoTexto;
    private String icono;      
    private LocalDate fecha;
    private Integer orden;

    public TimelinePasoDTO() {}

    public TimelinePasoDTO(String titulo, String descripcion, String estado, 
                          String estadoTexto, String icono, LocalDate fecha, Integer orden) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.estadoTexto = estadoTexto;
        this.icono = icono;
        this.fecha = fecha;
        this.orden = orden;
    }

    public String getTitulo() { 
        return titulo; 
    }
    
    public void setTitulo(String titulo) { 
        this.titulo = titulo; 
    }
    
    public String getDescripcion() { 
        return descripcion; 
    }
    
    public void setDescripcion(String descripcion) { 
        this.descripcion = descripcion; 
    }
    
    public String getEstado() { 
        return estado; 
    }
    
    public void setEstado(String estado) { 
        this.estado = estado; 
    }
    
    public String getEstadoTexto() { 
        return estadoTexto; 
    }
    
    public void setEstadoTexto(String estadoTexto) { 
        this.estadoTexto = estadoTexto; 
    }
    
    public String getIcono() { 
        return icono; 
    }
    
    public void setIcono(String icono) { 
        this.icono = icono; 
    }
    
    public LocalDate getFecha() { 
        return fecha; 
    }
    
    public void setFecha(LocalDate fecha) { 
        this.fecha = fecha; 
    }
    
    public Integer getOrden() { 
        return orden; 
    }
    
    public void setOrden(Integer orden) { 
        this.orden = orden; 
    }

    @Override
    public String toString() {
        return "TimelinePasoDTO{" +
                "titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", estado='" + estado + '\'' +
                ", estadoTexto='" + estadoTexto + '\'' +
                ", icono='" + icono + '\'' +
                ", fecha=" + fecha +
                ", orden=" + orden +
                '}';
    }
}