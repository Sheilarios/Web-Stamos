package com.stamos.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
     * Verifica si la categoría tiene productos.
     * 
     * @return true si tiene productos, false en caso contrario
     */
    public boolean tieneProductos() {
        return cantidadProductos != null && cantidadProductos > 0;
    }

    /**
     * Verifica si la categoría tiene productos activos.
     * 
     * @return true si tiene productos activos, false en caso contrario
     */
    public boolean tieneProductosActivos() {
        return cantidadProductosActivos != null && cantidadProductosActivos > 0;
    }

    /**
     * Verifica si la categoría tiene productos inactivos.
     * 
     * @return true si tiene productos inactivos, false en caso contrario
     */
    public boolean tieneProductosInactivos() {
        return cantidadProductosInactivos != null && cantidadProductosInactivos > 0;
    }

    /**
     * Agrega un producto a la lista de productos.
     * 
     * @param producto DTO del producto
     */
    public void agregarProducto(ProductoDTO producto) {
        if (producto != null && this.productos != null) {
            this.productos.add(producto);
        }
    }

    /**
     * Obtiene el porcentaje de productos activos.
     * 
     * @return Porcentaje de productos activos
     */
    public int getPorcentajeActivos() {
        if (cantidadProductos == null || cantidadProductos == 0) {
            return 0;
        }
        if (cantidadProductosActivos == null) {
            return 0;
        }
        return (cantidadProductosActivos * 100) / cantidadProductos;
    }

    /**
     * Obtiene el porcentaje de productos inactivos.
     * 
     * @return Porcentaje de productos inactivos
     */
    public int getPorcentajeInactivos() {
        if (cantidadProductos == null || cantidadProductos == 0) {
            return 0;
        }
        if (cantidadProductosInactivos == null) {
            return 0;
        }
        return (cantidadProductosInactivos * 100) / cantidadProductos;
    }

    /**
     * Obtiene el total de productos por tipo.
     * 
     * @return Total de productos
     */
    public int getTotalProductosPorTipo() {
        int total = 0;
        if (cantidadProductosAlquiler != null) total += cantidadProductosAlquiler;
        if (cantidadProductosVenta != null) total += cantidadProductosVenta;
        if (cantidadProductosServicio != null) total += cantidadProductosServicio;
        return total;
    }

    /**
     * Verifica si el total de productos por tipo coincide con la cantidad total.
     * 
     * @return true si coinciden, false en caso contrario
     */
    public boolean validarTotales() {
        return cantidadProductos != null && 
               cantidadProductos.equals(getTotalProductosPorTipo());
    }

    // ============================================
    // MÉTODOS DE FORMATO PARA LA VISTA
    // ============================================

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
     * Obtiene el nombre de la categoría en mayúsculas.
     * 
     * @return Nombre en mayúsculas
     */
    public String getNombreUpper() {
        return nombreCategoria != null ? nombreCategoria.toUpperCase() : "";
    }

    /**
     * Obtiene la descripción truncada.
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
     * Obtiene la fecha de creación formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaCreacionFormateada() {
        if (fechaCreacion == null) return "-";
        return fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    /**
     * Obtiene la fecha de actualización formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaActualizacionFormateada() {
        if (fechaActualizacion == null) return "-";
        return fechaActualizacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    /**
     * Obtiene la fecha de creación formateada (solo fecha).
     * 
     * @return Fecha formateada
     */
    public String getFechaCreacionSoloFecha() {
        if (fechaCreacion == null) return "-";
        return fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
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
            if (cantidadProductosActivos != null) {
                sb.append(", ").append(cantidadProductosActivos).append(" activos");
            }
        }
        return sb.toString();
    }

    /**
     * Obtiene la clase CSS para el badge de productos.
     * 
     * @return Clase CSS
     */
    public String getProductosBadgeClass() {
        if (cantidadProductos == null || cantidadProductos == 0) {
            return "badge-secondary";
        }
        if (cantidadProductosActivos != null && cantidadProductosActivos > 0) {
            return "badge-success";
        }
        return "badge-warning";
    }

    /**
     * Obtiene el color para el ícono de la categoría.
     * 
     * @return Color en hexadecimal
     */
    public String getIconoColor() {
        if (color == null) return "#3B82F6";
        return color;
    }

    /**
     * Obtiene el nombre de la categoría con su ícono.
     * 
     * @return Nombre con ícono
     */
    public String getNombreConIcono() {
        return "<i class='" + icono + "'></i> " + nombreCategoria;
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
        sb.append("\"activo\":").append(isActivo()).append(",");
        sb.append("\"cantidadProductos\":").append(cantidadProductos != null ? cantidadProductos : 0).append(",");
        sb.append("\"cantidadProductosActivos\":").append(cantidadProductosActivos != null ? cantidadProductosActivos : 0);
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convierte a una representación JSON con todos los campos.
     * 
     * @return JSON completo
     */
    public String toJsonCompleto() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"idCategoria\":").append(idCategoria).append(",");
        sb.append("\"nombre\":\"").append(nombreCategoria != null ? nombreCategoria : "").append("\",");
        sb.append("\"descripcion\":\"").append(descripcion != null ? descripcion : "").append("\",");
        sb.append("\"activo\":").append(isActivo()).append(",");
        sb.append("\"cantidadProductos\":").append(cantidadProductos != null ? cantidadProductos : 0).append(",");
        sb.append("\"cantidadProductosActivos\":").append(cantidadProductosActivos != null ? cantidadProductosActivos : 0).append(",");
        sb.append("\"cantidadProductosInactivos\":").append(cantidadProductosInactivos != null ? cantidadProductosInactivos : 0).append(",");
        sb.append("\"cantidadProductosAlquiler\":").append(cantidadProductosAlquiler != null ? cantidadProductosAlquiler : 0).append(",");
        sb.append("\"cantidadProductosVenta\":").append(cantidadProductosVenta != null ? cantidadProductosVenta : 0).append(",");
        sb.append("\"cantidadProductosServicio\":").append(cantidadProductosServicio != null ? cantidadProductosServicio : 0).append(",");
        sb.append("\"fechaCreacion\":\"").append(getFechaCreacionFormateada()).append("\"");
        if (fechaActualizacion != null) {
            sb.append(",\"fechaActualizacion\":\"").append(getFechaActualizacionFormateada()).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

    // ============================================
    // MÉTODOS OVERRIDE
    // ============================================

    @Override
    public String toString() {
        return "CategoriaProductoDTO{" +
                "idCategoria=" + idCategoria +
                ", nombreCategoria='" + nombreCategoria + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", activo=" + activo +
                ", cantidadProductos=" + cantidadProductos +
                ", cantidadProductosActivos=" + cantidadProductosActivos +
                ", cantidadProductosInactivos=" + cantidadProductosInactivos +
                ", cantidadProductosAlquiler=" + cantidadProductosAlquiler +
                ", cantidadProductosVenta=" + cantidadProductosVenta +
                ", cantidadProductosServicio=" + cantidadProductosServicio +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoriaProductoDTO that = (CategoriaProductoDTO) o;
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
     * Crea un DTO desde una entidad.
     * 
     * @param categoria Entidad de categoría
     * @return DTO de categoría
     */
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
        
        // Asignar métricas si están disponibles
        if (categoria.getCantidadProductos() != null) {
            dto.setCantidadProductos(categoria.getCantidadProductos());
        }
        if (categoria.getCantidadProductosActivos() != null) {
            dto.setCantidadProductosActivos(categoria.getCantidadProductosActivos());
        }
        
        return dto;
    }

    /**
     * Crea un DTO desde una entidad con métricas completas.
     * 
     * @param categoria Entidad de categoría
     * @param cantidadProductos Total de productos
     * @param cantidadProductosActivos Productos activos
     * @param cantidadProductosInactivos Productos inactivos
     * @return DTO de categoría
     */
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

    /**
     * Crea un DTO desde una entidad con métricas por tipo.
     * 
     * @param categoria Entidad de categoría
     * @param cantidadProductos Total de productos
     * @param cantidadProductosActivos Productos activos
     * @param cantidadProductosInactivos Productos inactivos
     * @param cantidadProductosAlquiler Productos de alquiler
     * @param cantidadProductosVenta Productos de venta
     * @param cantidadProductosServicio Productos de servicio
     * @return DTO de categoría
     */
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

    /**
     * Crea una lista de DTOs desde una lista de entidades.
     * 
     * @param categorias Lista de entidades
     * @return Lista de DTOs
     */
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

    /**
     * Convierte el DTO a una entidad.
     * 
     * @return Entidad de categoría
     */
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

    // ============================================
    // CLASE INTERNA PARA PRODUCTO DTO
    // ============================================

    /**
     * DTO para productos dentro de la categoría.
     */
    public static class ProductoDTO {
        private Integer idProducto;
        private String nombreItem;
        private String descripcion;
        private String tipo;
        private java.math.BigDecimal precioBase;
        private String imagenUrl;
        private Boolean activo;

        // Getters y Setters
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

        // Métodos de formato
        public String getPrecioBaseFormateado() {
            if (precioBase == null) return "S/ 0.00";
            return "S/ " + precioBase.setScale(2, java.math.RoundingMode.HALF_UP).toString();
        }

        public boolean isActivo() {
            return activo != null && activo;
        }

        public String getEstadoLegible() {
            return isActivo() ? "Activo" : "Inactivo";
        }

        public String getTipoLegible() {
            if (tipo == null) return "No especificado";
            switch (tipo) {
                case "Alquiler": return "Alquiler";
                case "Venta": return "Venta";
                case "Servicio": return "Servicio";
                default: return tipo;
            }
        }

        public String getTipoBadgeClass() {
            if (tipo == null) return "badge-secondary";
            switch (tipo) {
                case "Alquiler": return "badge-alquiler";
                case "Venta": return "badge-venta";
                case "Servicio": return "badge-servicio";
                default: return "badge-secondary";
            }
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