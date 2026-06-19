package com.stamos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad CategoriaProducto.
 * 
 * <p>Esta entidad representa las categorías de productos y servicios
 * ofrecidos por STAMOS S.L., permitiendo organizar el catálogo de
 * productos de manera jerárquica.</p>
 * 
 * <p>Las categorías incluyen:</p>
 * <ul>
 *   <li>Stands y estructuras feriales</li>
 *   <li>Mobiliario para eventos</li>
 *   <li>Equipamiento interactivo y audiovisual</li>
 *   <li>Publicidad gráfica</li>
 * </ul>
 * 
 * <p>Implementa el requerimiento RF-11: Gestión de catálogo de productos
 * y servicios con categorías.</p>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
@Entity
@Table(name = "CategoriaProducto", 
       indexes = {
           @Index(name = "idx_categoria_nombre", columnList = "nombre_categoria"),
           @Index(name = "idx_categoria_activo", columnList = "activo")
       })
public class CategoriaProducto {

    // ============================================
    // ATRIBUTOS
    // ============================================

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

    // ============================================
    // CAMPOS DE AUDITORÍA (OPCIONALES)
    // ============================================

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "id_usuario_creacion")
    private Integer idUsuarioCreacion;

    @Column(name = "id_usuario_actualizacion")
    private Integer idUsuarioActualizacion;

    // ============================================
    // CAMPOS ADICIONALES (NO EN BD)
    // ============================================

    @Transient
    private Integer cantidadProductos;

    @Transient
    private Integer cantidadProductosActivos;

    // ============================================
    // CONSTRUCTORES
    // ============================================

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

    // ============================================
    // GETTERS Y SETTERS
    // ============================================

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

    // ============================================
    // MÉTODOS DE NEGOCIO
    // ============================================

    /**
     * Verifica si la categoría está activa.
     * 
     * @return true si está activa, false en caso contrario
     */
    public boolean isActivo() {
        return activo != null && activo;
    }

    /**
     * Activa la categoría.
     */
    public void activar() {
        this.activo = true;
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Desactiva la categoría.
     */
    public void desactivar() {
        this.activo = false;
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Alterna el estado de la categoría (activo/inactivo).
     */
    public void toggleActivo() {
        this.activo = !isActivo();
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Verifica si la categoría tiene productos asociados.
     * 
     * @param cantidadProductos Cantidad de productos
     * @return true si tiene productos, false en caso contrario
     */
    public boolean tieneProductos(Integer cantidadProductos) {
        return cantidadProductos != null && cantidadProductos > 0;
    }

    /**
     * Verifica si la categoría tiene productos activos.
     * 
     * @param cantidadProductosActivos Cantidad de productos activos
     * @return true si tiene productos activos, false en caso contrario
     */
    public boolean tieneProductosActivos(Integer cantidadProductosActivos) {
        return cantidadProductosActivos != null && cantidadProductosActivos > 0;
    }

    /**
     * Obtiene el nombre de la categoría en formato legible.
     * 
     * @return Nombre de la categoría
     */
    public String getNombreLegible() {
        if (nombreCategoria == null) return "Sin categoría";
        return nombreCategoria;
    }

    /**
     * Obtiene el estado en formato legible.
     * 
     * @return Estado legible
     */
    public String getEstadoLegible() {
        return isActivo() ? "Activo" : "Inactivo";
    }

    /**
     * Obtiene la clase CSS para el badge del estado.
     * 
     * @return Clase CSS
     */
    public String getEstadoBadgeClass() {
        return isActivo() ? "badge-success" : "badge-danger";
    }

    /**
     * Obtiene el ícono para el estado.
     * 
     * @return Ícono CSS
     */
    public String getEstadoIcono() {
        return isActivo() ? "ti ti-check-circle" : "ti ti-x-circle";
    }

    /**
     * Obtiene el color para el estado.
     * 
     * @return Color en hexadecimal
     */
    public String getEstadoColor() {
        return isActivo() ? "#10B981" : "#EF4444";
    }

    /**
     * Obtiene el resumen de la categoría.
     * 
     * @return Resumen
     */
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

    /**
     * Actualiza la información de auditoría para creación.
     * 
     * @param idUsuario ID del usuario que crea
     */
    public void auditarCreacion(Integer idUsuario) {
        this.fechaCreacion = LocalDateTime.now();
        this.idUsuarioCreacion = idUsuario;
        this.fechaActualizacion = LocalDateTime.now();
        this.idUsuarioActualizacion = idUsuario;
    }

    /**
     * Actualiza la información de auditoría para actualización.
     * 
     * @param idUsuario ID del usuario que actualiza
     */
    public void auditarActualizacion(Integer idUsuario) {
        this.fechaActualizacion = LocalDateTime.now();
        this.idUsuarioActualizacion = idUsuario;
    }

    // ============================================
    // MÉTODOS DE FORMATO PARA LA VISTA
    // ============================================

    /**
     * Obtiene la fecha de creación formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaCreacionFormateada() {
        if (fechaCreacion == null) return "-";
        return fechaCreacion.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    /**
     * Obtiene la fecha de actualización formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaActualizacionFormateada() {
        if (fechaActualizacion == null) return "-";
        return fechaActualizacion.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    /**
     * Trunca la descripción a una longitud máxima.
     * 
     * @param maxLength Longitud máxima
     * @return Descripción truncada
     */
    public String getDescripcionTruncada(int maxLength) {
        if (descripcion == null) return "";
        if (descripcion.length() <= maxLength) return descripcion;
        return descripcion.substring(0, maxLength) + "...";
    }

    /**
     * Obtiene el nombre de la categoría en mayúsculas.
     * 
     * @return Nombre en mayúsculas
     */
    public String getNombreUpper() {
        return nombreCategoria != null ? nombreCategoria.toUpperCase() : "";
    }

    // ============================================
    // MÉTODOS DE CONVERSIÓN
    // ============================================

    /**
     * Convierte a una representación JSON simple.
     * 
     * @return JSON como String
     */
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

    // ============================================
    // MÉTODOS OVERRIDE
    // ============================================

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

    // ============================================
    // MÉTODOS ESTÁTICOS DE FÁBRICA
    // ============================================

    /**
     * Crea una categoría de Stands y estructuras feriales.
     * 
     * @return Categoría de stands
     */
    public static CategoriaProducto crearCategoriaStands() {
        return new CategoriaProducto(
            "Stands y estructuras feriales",
            "Módulos, islas, mostradores y estructuras temporales para ferias y eventos"
        );
    }

    /**
     * Crea una categoría de Mobiliario para eventos.
     * 
     * @return Categoría de mobiliario
     */
    public static CategoriaProducto crearCategoriaMobiliario() {
        return new CategoriaProducto(
            "Mobiliario para eventos",
            "Sillas, mesas, sofás, estanterías y counters para eventos corporativos"
        );
    }

    /**
     * Crea una categoría de Equipamiento interactivo.
     * 
     * @return Categoría de equipamiento
     */
    public static CategoriaProducto crearCategoriaEquipamiento() {
        return new CategoriaProducto(
            "Equipamiento interactivo y audiovisual",
            "Pantallas, video walls, proyectores y sonido para experiencias inmersivas"
        );
    }

    /**
     * Crea una categoría de Publicidad gráfica.
     * 
     * @return Categoría de publicidad
     */
    public static CategoriaProducto crearCategoriaPublicidad() {
        return new CategoriaProducto(
            "Publicidad gráfica",
            "Lonas, vinilos, banners, roll-ups y displays para publicidad"
        );
    }

    /**
     * Crea una categoría desde un DTO.
     * 
     * @param dto DTO de categoría
     * @return Categoría
     */
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

    /**
     * Convierte la entidad a un DTO.
     * 
     * @return DTO de categoría
     */
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

    // ============================================
    // CLASE PARA DTO DE CATEGORÍA
    // ============================================

    /**
     * DTO para CategoriaProducto.
     */
    public static class CategoriaProductoDTO {
        private Integer idCategoria;
        private String nombreCategoria;
        private String descripcion;
        private Boolean activo;
        private Integer cantidadProductos;
        private Integer cantidadProductosActivos;
        private LocalDateTime fechaCreacion;
        private LocalDateTime fechaActualizacion;

        // Getters y Setters
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

        // Métodos de formato
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