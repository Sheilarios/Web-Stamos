package com.stamos.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CategoriaProductoDTO {

    private Integer idCategoria;
    private String nombreCategoria;
    private String descripcion;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private Integer idUsuarioCreacion;
    private Integer idUsuarioActualizacion;
    private String nombreUsuarioCreacion;
    private String nombreUsuarioActualizacion;
    private Integer cantidadProductos;
    private Integer cantidadProductosActivos;
    private Integer cantidadProductosInactivos;
    private Integer cantidadProductosAlquiler;
    private Integer cantidadProductosVenta;
    private Integer cantidadProductosServicio;
    private List<ProductoDTO> productos = new ArrayList<>();
    private String color;
    private String icono;

    public CategoriaProductoDTO() {
        this.activo = true;
        this.cantidadProductos = 0;
        this.cantidadProductosActivos = 0;
        this.cantidadProductosInactivos = 0;
        this.cantidadProductosAlquiler = 0;
        this.cantidadProductosVenta = 0;
        this.cantidadProductosServicio = 0;
        this.productos = new ArrayList<>();
        this.color = "#3B82F6";
        this.icono = "ti ti-category";
    }

    public CategoriaProductoDTO(Integer idCategoria, String nombreCategoria, 
                               String descripcion, Boolean activo) {
        this();
        this.idCategoria = idCategoria;
        this.nombreCategoria = nombreCategoria;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    public CategoriaProductoDTO(Integer idCategoria, String nombreCategoria, 
                               String descripcion, Boolean activo,
                               Integer cantidadProductos, Integer cantidadProductosActivos) {
        this(idCategoria, nombreCategoria, descripcion, activo);
        this.cantidadProductos = cantidadProductos;
        this.cantidadProductosActivos = cantidadProductosActivos;
        if (cantidadProductos != null && cantidadProductosActivos != null) {
            this.cantidadProductosInactivos = cantidadProductos - cantidadProductosActivos;
        }
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

    public String getNombreUsuarioCreacion() {
        return nombreUsuarioCreacion;
    }

    public void setNombreUsuarioCreacion(String nombreUsuarioCreacion) {
        this.nombreUsuarioCreacion = nombreUsuarioCreacion;
    }

    public String getNombreUsuarioActualizacion() {
        return nombreUsuarioActualizacion;
    }

    public void setNombreUsuarioActualizacion(String nombreUsuarioActualizacion) {
        this.nombreUsuarioActualizacion = nombreUsuarioActualizacion;
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

    public Integer getCantidadProductosInactivos() {
        return cantidadProductosInactivos;
    }

    public void setCantidadProductosInactivos(Integer cantidadProductosInactivos) {
        this.cantidadProductosInactivos = cantidadProductosInactivos;
    }

    public Integer getCantidadProductosAlquiler() {
        return cantidadProductosAlquiler;
    }

    public void setCantidadProductosAlquiler(Integer cantidadProductosAlquiler) {
        this.cantidadProductosAlquiler = cantidadProductosAlquiler;
    }

    public Integer getCantidadProductosVenta() {
        return cantidadProductosVenta;
    }

    public void setCantidadProductosVenta(Integer cantidadProductosVenta) {
        this.cantidadProductosVenta = cantidadProductosVenta;
    }

    public Integer getCantidadProductosServicio() {
        return cantidadProductosServicio;
    }

    public void setCantidadProductosServicio(Integer cantidadProductosServicio) {
        this.cantidadProductosServicio = cantidadProductosServicio;
    }

    public List<ProductoDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoDTO> productos) {
        this.productos = productos;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public boolean isActivo() {
        return activo != null && activo;
    }

    public boolean tieneProductos() {
        return cantidadProductos != null && cantidadProductos > 0;
    }

    public boolean tieneProductosActivos() {
        return cantidadProductosActivos != null && cantidadProductosActivos > 0;
    }

    public boolean tieneProductosInactivos() {
        return cantidadProductosInactivos != null && cantidadProductosInactivos > 0;
    }

    public void agregarProducto(ProductoDTO producto) {
        if (producto != null && this.productos != null) {
            this.productos.add(producto);
        }
    }

    public static CategoriaProductoDTO fromEntity(com.stamos.model.CategoriaProducto categoria) {
        if (categoria == null) {
            return null;
        }

        CategoriaProductoDTO dto = new CategoriaProductoDTO();
        dto.setIdCategoria(categoria.getIdCategoria());
        dto.setNombreCategoria(categoria.getNombreCategoria());
        dto.setDescripcion(categoria.getDescripcion());
        dto.setActivo(categoria.getActivo());
        dto.setFechaCreacion(categoria.getFechaCreacion());
        dto.setFechaActualizacion(categoria.getFechaActualizacion());
        dto.setIdUsuarioCreacion(categoria.getIdUsuarioCreacion());
        dto.setIdUsuarioActualizacion(categoria.getIdUsuarioActualizacion());
        
        if (categoria.getCantidadProductos() != null) {
            dto.setCantidadProductos(categoria.getCantidadProductos());
        }
        if (categoria.getCantidadProductosActivos() != null) {
            dto.setCantidadProductosActivos(categoria.getCantidadProductosActivos());
        }
        
        return dto;
    }

    public static CategoriaProductoDTO fromEntityWithMetrics(
            com.stamos.model.CategoriaProducto categoria,
            Integer cantidadProductos,
            Integer cantidadProductosActivos,
            Integer cantidadProductosInactivos) {
        
        CategoriaProductoDTO dto = fromEntity(categoria);
        if (dto != null) {
            dto.setCantidadProductos(cantidadProductos);
            dto.setCantidadProductosActivos(cantidadProductosActivos);
            dto.setCantidadProductosInactivos(cantidadProductosInactivos);
        }
        return dto;
    }

    public static CategoriaProductoDTO fromEntityWithAllMetrics(
            com.stamos.model.CategoriaProducto categoria,
            Integer cantidadProductos,
            Integer cantidadProductosActivos,
            Integer cantidadProductosInactivos,
            Integer cantidadProductosAlquiler,
            Integer cantidadProductosVenta,
            Integer cantidadProductosServicio) {
        
        CategoriaProductoDTO dto = fromEntityWithMetrics(
            categoria, cantidadProductos, cantidadProductosActivos, cantidadProductosInactivos);
        
        if (dto != null) {
            dto.setCantidadProductosAlquiler(cantidadProductosAlquiler);
            dto.setCantidadProductosVenta(cantidadProductosVenta);
            dto.setCantidadProductosServicio(cantidadProductosServicio);
        }
        return dto;
    }

    public static List<CategoriaProductoDTO> fromEntityList(List<com.stamos.model.CategoriaProducto> categorias) {
        if (categorias == null) {
            return new ArrayList<>();
        }
        
        List<CategoriaProductoDTO> dtos = new ArrayList<>();
        for (com.stamos.model.CategoriaProducto categoria : categorias) {
            CategoriaProductoDTO dto = fromEntity(categoria);
            if (dto != null) {
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public com.stamos.model.CategoriaProducto toEntity() {
        com.stamos.model.CategoriaProducto categoria = new com.stamos.model.CategoriaProducto();
        categoria.setIdCategoria(this.idCategoria);
        categoria.setNombreCategoria(this.nombreCategoria);
        categoria.setDescripcion(this.descripcion);
        categoria.setActivo(this.activo);
        categoria.setFechaCreacion(this.fechaCreacion);
        categoria.setFechaActualizacion(this.fechaActualizacion);
        categoria.setIdUsuarioCreacion(this.idUsuarioCreacion);
        categoria.setIdUsuarioActualizacion(this.idUsuarioActualizacion);
        return categoria;
    }

    public static class ProductoDTO {
        private Integer idProducto;
        private String nombreItem;
        private String descripcion;
        private String tipo;
        private java.math.BigDecimal precioBase;
        private String imagenUrl;
        private Boolean activo;

        public Integer getIdProducto() { return idProducto; }
        public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }

        public String getNombreItem() { return nombreItem; }
        public void setNombreItem(String nombreItem) { this.nombreItem = nombreItem; }

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }

        public java.math.BigDecimal getPrecioBase() { return precioBase; }
        public void setPrecioBase(java.math.BigDecimal precioBase) { this.precioBase = precioBase; }

        public String getImagenUrl() { return imagenUrl; }
        public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

        public Boolean getActivo() { return activo; }
        public void setActivo(Boolean activo) { this.activo = activo; }

        public boolean isActivo() {
            return activo != null && activo;
        }

        @Override
        public String toString() {
            return "ProductoDTO{" +
                    "idProducto=" + idProducto +
                    ", nombreItem='" + nombreItem + '\'' +
                    ", tipo='" + tipo + '\'' +
                    ", precioBase=" + precioBase +
                    ", activo=" + activo +
                    '}';
        }
    }
}