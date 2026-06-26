package com.stamos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "CategoriaProducto", 
       indexes = {
           @Index(name = "idx_categoria_nombre", columnList = "nombre_categoria"),
           @Index(name = "idx_categoria_activo", columnList = "activo")
       })
public class CategoriaProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer idCategoria;

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    @Column(name = "nombre_categoria", nullable = false, unique = true, length = 100)
    private String nombreCategoria;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "id_usuario_creacion")
    private Integer idUsuarioCreacion;

    @Column(name = "id_usuario_actualizacion")
    private Integer idUsuarioActualizacion;

    @Transient
    private Integer cantidadProductos;

    @Transient
    private Integer cantidadProductosActivos;

    public CategoriaProducto() {
        this.activo = true;
        this.fechaCreacion = LocalDateTime.now();
    }

    public CategoriaProducto(String nombreCategoria, String descripcion) {
        this();
        this.nombreCategoria = nombreCategoria;
        this.descripcion = descripcion;
    }

    public CategoriaProducto(Integer idCategoria, String nombreCategoria, 
                            String descripcion, Boolean activo) {
        this.idCategoria = idCategoria;
        this.nombreCategoria = nombreCategoria;
        this.descripcion = descripcion;
        this.activo = activo;
        this.fechaCreacion = LocalDateTime.now();
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Integer getIdUsuarioCreacion() {
        return idUsuarioCreacion;
    }

    public void setIdUsuarioCreacion(Integer idUsuarioCreacion) {
        this.idUsuarioCreacion = idUsuarioCreacion;
    }

    public Integer getIdUsuarioActualizacion() {
        return idUsuarioActualizacion;
    }

    public void setIdUsuarioActualizacion(Integer idUsuarioActualizacion) {
        this.idUsuarioActualizacion = idUsuarioActualizacion;
    }

    public Integer getCantidadProductos() {
        return cantidadProductos;
    }

    public void setCantidadProductos(Integer cantidadProductos) {
        this.cantidadProductos = cantidadProductos;
    }

    public Integer getCantidadProductosActivos() {
        return cantidadProductosActivos;
    }

    public void setCantidadProductosActivos(Integer cantidadProductosActivos) {
        this.cantidadProductosActivos = cantidadProductosActivos;
    }

    public boolean isActivo() {
        return activo != null && activo;
    }

    public void activar() {
        this.activo = true;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void desactivar() {
        this.activo = false;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void toggleActivo() {
        this.activo = !isActivo();
        this.fechaActualizacion = LocalDateTime.now();
    }

    public boolean tieneProductos(Integer cantidadProductos) {
        return cantidadProductos != null && cantidadProductos > 0;
    }

    public boolean tieneProductosActivos(Integer cantidadProductosActivos) {
        return cantidadProductosActivos != null && cantidadProductosActivos > 0;
    }

    public String getNombreLegible() {
        if (nombreCategoria == null) return "Sin categoría";
        return nombreCategoria;
    }

    public String getEstadoLegible() {
        return isActivo() ? "Activo" : "Inactivo";
    }

    public String getEstadoBadgeClass() {
        return isActivo() ? "badge-success" : "badge-danger";
    }

    public String getEstadoIcono() {
        return isActivo() ? "ti ti-check-circle" : "ti ti-x-circle";
    }

    public String getEstadoColor() {
        return isActivo() ? "#10B981" : "#EF4444";
    }

    public String getResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append(nombreCategoria);
        if (cantidadProductos != null) {
            sb.append(" (").append(cantidadProductos).append(" productos)");
        }
        if (cantidadProductosActivos != null) {
            sb.append(", ").append(cantidadProductosActivos).append(" activos");
        }
        return sb.toString();
    }

    public void auditarCreacion(Integer idUsuario) {
        this.fechaCreacion = LocalDateTime.now();
        this.idUsuarioCreacion = idUsuario;
        this.fechaActualizacion = LocalDateTime.now();
        this.idUsuarioActualizacion = idUsuario;
    }

    public void auditarActualizacion(Integer idUsuario) {
        this.fechaActualizacion = LocalDateTime.now();
        this.idUsuarioActualizacion = idUsuario;
    }

    public String getFechaCreacionFormateada() {
        if (fechaCreacion == null) return "-";
        return fechaCreacion.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getFechaActualizacionFormateada() {
        if (fechaActualizacion == null) return "-";
        return fechaActualizacion.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getDescripcionTruncada(int maxLength) {
        if (descripcion == null) return "";
        if (descripcion.length() <= maxLength) return descripcion;
        return descripcion.substring(0, maxLength) + "...";
    }

    public String getNombreUpper() {
        return nombreCategoria != null ? nombreCategoria.toUpperCase() : "";
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"idCategoria\":").append(idCategoria).append(",");
        sb.append("\"nombre\":\"").append(nombreCategoria != null ? nombreCategoria : "").append("\",");
        sb.append("\"descripcion\":\"").append(descripcion != null ? descripcion : "").append("\",");
        sb.append("\"activo\":").append(isActivo());
        if (cantidadProductos != null) {
            sb.append(",\"cantidadProductos\":").append(cantidadProductos);
        }
        if (cantidadProductosActivos != null) {
            sb.append(",\"cantidadProductosActivos\":").append(cantidadProductosActivos);
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "CategoriaProducto{" +
                "idCategoria=" + idCategoria +
                ", nombreCategoria='" + nombreCategoria + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", activo=" + activo +
                ", cantidadProductos=" + cantidadProductos +
                ", cantidadProductosActivos=" + cantidadProductosActivos +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoriaProducto that = (CategoriaProducto) o;
        return idCategoria != null && idCategoria.equals(that.idCategoria);
    }

    @Override
    public int hashCode() {
        return idCategoria != null ? idCategoria.hashCode() : 0;
    }

    public static CategoriaProducto crearCategoriaStands() {
        return new CategoriaProducto(
            "Stands y estructuras feriales",
            "Módulos, islas, mostradores y estructuras temporales para ferias y eventos"
        );
    }

    public static CategoriaProducto crearCategoriaMobiliario() {
        return new CategoriaProducto(
            "Mobiliario para eventos",
            "Sillas, mesas, sofás, estanterías y counters para eventos corporativos"
        );
    }

    public static CategoriaProducto crearCategoriaEquipamiento() {
        return new CategoriaProducto(
            "Equipamiento interactivo y audiovisual",
            "Pantallas, video walls, proyectores y sonido para experiencias inmersivas"
        );
    }

    public static CategoriaProducto crearCategoriaPublicidad() {
        return new CategoriaProducto(
            "Publicidad gráfica",
            "Lonas, vinilos, banners, roll-ups y displays para publicidad"
        );
    }

    public static CategoriaProducto fromDTO(com.stamos.dto.CategoriaProductoDTO dto) {
        if (dto == null) {
            return null;
        }
        CategoriaProducto categoria = new CategoriaProducto();
        if (dto.getIdCategoria() != null) {
            categoria.setIdCategoria(dto.getIdCategoria());
        }
        categoria.setNombreCategoria(dto.getNombreCategoria());
        categoria.setDescripcion(dto.getDescripcion());
        if (dto.getActivo() != null) {
            categoria.setActivo(dto.getActivo());
        }
        return categoria;
    }

    public com.stamos.dto.CategoriaProductoDTO toDTO() {
        com.stamos.dto.CategoriaProductoDTO dto = new com.stamos.dto.CategoriaProductoDTO();
        dto.setIdCategoria(this.idCategoria);
        dto.setNombreCategoria(this.nombreCategoria);
        dto.setDescripcion(this.descripcion);
        dto.setActivo(this.activo);
        dto.setCantidadProductos(this.cantidadProductos);
        dto.setCantidadProductosActivos(this.cantidadProductosActivos);
        dto.setFechaCreacion(this.fechaCreacion);
        dto.setFechaActualizacion(this.fechaActualizacion);
        return dto;
    }

    public static class CategoriaProductoDTO {
        private Integer idCategoria;
        private String nombreCategoria;
        private String descripcion;
        private Boolean activo;
        private Integer cantidadProductos;
        private Integer cantidadProductosActivos;
        private LocalDateTime fechaCreacion;
        private LocalDateTime fechaActualizacion;

        public Integer getIdCategoria() { return idCategoria; }
        public void setIdCategoria(Integer idCategoria) { this.idCategoria = idCategoria; }

        public String getNombreCategoria() { return nombreCategoria; }
        public void setNombreCategoria(String nombreCategoria) { this.nombreCategoria = nombreCategoria; }

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

        public Boolean getActivo() { return activo; }
        public void setActivo(Boolean activo) { this.activo = activo; }

        public Integer getCantidadProductos() { return cantidadProductos; }
        public void setCantidadProductos(Integer cantidadProductos) { this.cantidadProductos = cantidadProductos; }

        public Integer getCantidadProductosActivos() { return cantidadProductosActivos; }
        public void setCantidadProductosActivos(Integer cantidadProductosActivos) { this.cantidadProductosActivos = cantidadProductosActivos; }

        public LocalDateTime getFechaCreacion() { return fechaCreacion; }
        public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

        public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
        public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

        public String getEstadoLegible() {
            return activo != null && activo ? "Activo" : "Inactivo";
        }

        public String getEstadoBadgeClass() {
            return activo != null && activo ? "badge-success" : "badge-danger";
        }

        public String getFechaCreacionFormateada() {
            if (fechaCreacion == null) return "-";
            return fechaCreacion.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }

        public String getNombreUpper() {
            return nombreCategoria != null ? nombreCategoria.toUpperCase() : "";
        }

        @Override
        public String toString() {
            return "CategoriaProductoDTO{" +
                    "idCategoria=" + idCategoria +
                    ", nombreCategoria='" + nombreCategoria + '\'' +
                    ", activo=" + activo +
                    ", cantidadProductos=" + cantidadProductos +
                    ", cantidadProductosActivos=" + cantidadProductosActivos +
                    '}';
        }
    }
}