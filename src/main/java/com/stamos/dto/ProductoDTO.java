package com.stamos.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ProductoDTO {

    private Integer idProducto;
    private Integer idCategoria;
    private String nombreItem;
    private String descripcion;
    private String tipo;
    private BigDecimal precioBase;
    private String imagenUrl;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private Integer idUsuarioCreacion;
    private Integer idUsuarioActualizacion;
    private String nombreUsuarioCreacion;
    private String nombreUsuarioActualizacion;
    private String nombreCategoria;
    private String descripcionCategoria;
    private Boolean categoriaActiva;
    private String tipoLegible;
    private String colorTipo;
    private String iconoTipo;
    private Boolean tieneStock;
    private Integer cantidadStock;
    private Integer cantidadDisponible;
    private Integer cantidadReservada;
    private String precioBaseFormateado;
    private String tipoBadgeClass;
    private String estadoBadgeClass;
    private String estadoLegible;
    private String estadoIcono;
    private String estadoColor;

    public ProductoDTO() {
        this.activo = true;
        this.tieneStock = false;
        this.cantidadStock = 0;
        this.cantidadDisponible = 0;
        this.cantidadReservada = 0;
        this.precioBase = BigDecimal.ZERO;
        this.tipo = "Alquiler";
        actualizarCamposFormateados();
    }

    public ProductoDTO(Integer idProducto, String nombreItem, String tipo, BigDecimal precioBase) {
        this();
        this.idProducto = idProducto;
        this.nombreItem = nombreItem;
        this.tipo = tipo;
        this.precioBase = precioBase;
        actualizarCamposFormateados();
    }

    public ProductoDTO(Integer idProducto, Integer idCategoria, String nombreItem,
                      String descripcion, String tipo, BigDecimal precioBase,
                      String imagenUrl, Boolean activo) {
        this();
        this.idProducto = idProducto;
        this.idCategoria = idCategoria;
        this.nombreItem = nombreItem;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.precioBase = precioBase;
        this.imagenUrl = imagenUrl;
        this.activo = activo;
        actualizarCamposFormateados();
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombreItem() {
        return nombreItem;
    }

    public void setNombreItem(String nombreItem) {
        this.nombreItem = nombreItem;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
        actualizarCamposFormateados();
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase;
        actualizarCamposFormateados();
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
        actualizarCamposFormateados();
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

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public String getDescripcionCategoria() {
        return descripcionCategoria;
    }

    public void setDescripcionCategoria(String descripcionCategoria) {
        this.descripcionCategoria = descripcionCategoria;
    }

    public Boolean getCategoriaActiva() {
        return categoriaActiva;
    }

    public void setCategoriaActiva(Boolean categoriaActiva) {
        this.categoriaActiva = categoriaActiva;
    }

    public String getTipoLegible() {
        return tipoLegible;
    }

    public void setTipoLegible(String tipoLegible) {
        this.tipoLegible = tipoLegible;
    }

    public String getColorTipo() {
        return colorTipo;
    }

    public void setColorTipo(String colorTipo) {
        this.colorTipo = colorTipo;
    }

    public String getIconoTipo() {
        return iconoTipo;
    }

    public void setIconoTipo(String iconoTipo) {
        this.iconoTipo = iconoTipo;
    }

    public Boolean getTieneStock() {
        return tieneStock;
    }

    public void setTieneStock(Boolean tieneStock) {
        this.tieneStock = tieneStock;
    }

    public Integer getCantidadStock() {
        return cantidadStock;
    }

    public void setCantidadStock(Integer cantidadStock) {
        this.cantidadStock = cantidadStock;
        this.tieneStock = cantidadStock != null && cantidadStock > 0;
    }

    public Integer getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(Integer cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public Integer getCantidadReservada() {
        return cantidadReservada;
    }

    public void setCantidadReservada(Integer cantidadReservada) {
        this.cantidadReservada = cantidadReservada;
    }

    public String getPrecioBaseFormateado() {
        return precioBaseFormateado;
    }

    public void setPrecioBaseFormateado(String precioBaseFormateado) {
        this.precioBaseFormateado = precioBaseFormateado;
    }

    public String getTipoBadgeClass() {
        return tipoBadgeClass;
    }

    public void setTipoBadgeClass(String tipoBadgeClass) {
        this.tipoBadgeClass = tipoBadgeClass;
    }

    public String getEstadoBadgeClass() {
        return estadoBadgeClass;
    }

    public void setEstadoBadgeClass(String estadoBadgeClass) {
        this.estadoBadgeClass = estadoBadgeClass;
    }

    public String getEstadoLegible() {
        return estadoLegible;
    }

    public void setEstadoLegible(String estadoLegible) {
        this.estadoLegible = estadoLegible;
    }

    public String getEstadoIcono() {
        return estadoIcono;
    }

    public void setEstadoIcono(String estadoIcono) {
        this.estadoIcono = estadoIcono;
    }

    public String getEstadoColor() {
        return estadoColor;
    }

    public void setEstadoColor(String estadoColor) {
        this.estadoColor = estadoColor;
    }

    private void actualizarCamposFormateados() {
        if (precioBase != null) {
            this.precioBaseFormateado = "S/ " + precioBase.setScale(2, RoundingMode.HALF_UP).toString();
        } else {
            this.precioBaseFormateado = "S/ 0.00";
        }

        if (tipo != null) {
            switch (tipo) {
                case "Alquiler" -> {
                    this.tipoLegible = "Alquiler";
                    this.colorTipo = "#3B82F6";
                    this.iconoTipo = "ti ti-calendar";
                    this.tipoBadgeClass = "badge-alquiler";
                }
                case "Venta" -> {
                    this.tipoLegible = "Venta";
                    this.colorTipo = "#10B981";
                    this.iconoTipo = "ti ti-shopping-cart";
                    this.tipoBadgeClass = "badge-venta";
                }
                case "Servicio" -> {
                    this.tipoLegible = "Servicio";
                    this.colorTipo = "#8B5CF6";
                    this.iconoTipo = "ti ti-tools";
                    this.tipoBadgeClass = "badge-servicio";
                }
                default -> {
                    this.tipoLegible = tipo;
                    this.colorTipo = "#6B7280";
                    this.iconoTipo = "ti ti-box";
                    this.tipoBadgeClass = "badge-secondary";
                }
            }
        }

        if (activo != null) {
            if (activo) {
                this.estadoLegible = "Activo";
                this.estadoBadgeClass = "badge-success";
                this.estadoIcono = "ti ti-check-circle";
                this.estadoColor = "#10B981";
            } else {
                this.estadoLegible = "Inactivo";
                this.estadoBadgeClass = "badge-danger";
                this.estadoIcono = "ti ti-x-circle";
                this.estadoColor = "#EF4444";
            }
        }
    }

    public boolean isActivo() {
        return activo != null && activo;
    }

    public boolean esAlquiler() {
        return "Alquiler".equals(this.tipo);
    }

    public boolean esVenta() {
        return "Venta".equals(this.tipo);
    }

    public boolean esServicio() {
        return "Servicio".equals(this.tipo);
    }

    public boolean tieneImagen() {
        return imagenUrl != null && !imagenUrl.trim().isEmpty();
    }

    public boolean tieneDescripcion() {
        return descripcion != null && !descripcion.trim().isEmpty();
    }

    public boolean isTieneStock() {
        return tieneStock != null && tieneStock;
    }

    public boolean tieneStockDisponible() {
        return cantidadDisponible != null && cantidadDisponible > 0;
    }

    public boolean tieneReservas() {
        return cantidadReservada != null && cantidadReservada > 0;
    }

    public int getPorcentajeDisponibilidad() {
        if (cantidadStock == null || cantidadStock == 0) {
            return 0;
        }
        if (cantidadDisponible == null) {
            return 0;
        }
        return (cantidadDisponible * 100) / cantidadStock;
    }

    public String getDisponibilidadTexto() {
        if (cantidadStock == null || cantidadStock == 0) {
            return "Sin stock";
        }
        if (cantidadDisponible == null || cantidadDisponible == 0) {
            return "Agotado";
        }
        if (cantidadReservada != null && cantidadReservada > 0) {
            return cantidadDisponible + " disponibles (" + cantidadReservada + " reservados)";
        }
        return cantidadDisponible + " disponibles";
    }

    public String getNombreConTipo() {
        return nombreItem + " (" + getTipoLegible() + ")";
    }

    public String getNombreTruncado(int maxLength) {
        if (nombreItem == null) return "";
        if (nombreItem.length() <= maxLength) return nombreItem;
        return nombreItem.substring(0, maxLength) + "...";
    }

    public String getDescripcionTruncada(int maxLength) {
        if (descripcion == null) return "";
        if (descripcion.length() <= maxLength) return descripcion;
        return descripcion.substring(0, maxLength) + "...";
    }

    public String getImagenUrlOrDefault() {
        if (tieneImagen()) {
            return imagenUrl;
        }
        return "/images/placeholder-product.png";
    }

    public static ProductoDTO fromEntity(com.stamos.model.Producto producto) {
        if (producto == null) {
            return null;
        }

        ProductoDTO dto = new ProductoDTO();
        dto.setIdProducto(producto.getIdProducto());
        dto.setIdCategoria(producto.getIdCategoria());
        dto.setNombreItem(producto.getNombreItem());
        dto.setDescripcion(producto.getDescripcion());
        dto.setTipo(producto.getTipo());
        dto.setPrecioBase(producto.getPrecioBase());
        dto.setImagenUrl(producto.getImagenUrl());
        dto.setActivo(producto.getActivo());
        dto.setFechaCreacion(producto.getFechaCreacion());
        dto.setFechaActualizacion(producto.getFechaActualizacion());
        dto.setIdUsuarioCreacion(producto.getIdUsuarioCreacion());
        dto.setIdUsuarioActualizacion(producto.getIdUsuarioActualizacion());
        dto.setNombreCategoria(producto.getNombreCategoria());
        dto.setDescripcionCategoria(producto.getDescripcionCategoria());
        dto.setTieneStock(producto.getTieneStock());
        dto.setCantidadStock(producto.getCantidadStock());
        dto.setCantidadDisponible(producto.getCantidadDisponible());
        dto.actualizarCamposFormateados();
        return dto;
    }

    public static ProductoDTO fromEntityWithCategoria(com.stamos.model.Producto producto,
                                                      com.stamos.model.CategoriaProducto categoria) {
        ProductoDTO dto = fromEntity(producto);
        if (dto != null && categoria != null) {
            dto.setNombreCategoria(categoria.getNombreCategoria());
            dto.setDescripcionCategoria(categoria.getDescripcion());
            dto.setCategoriaActiva(categoria.getActivo());
        }
        return dto;
    }

    public static ProductoDTO crearNuevoProducto(String nombreItem, Integer idCategoria,
                                                 String tipo, BigDecimal precioBase) {
        ProductoDTO dto = new ProductoDTO();
        dto.setNombreItem(nombreItem);
        dto.setIdCategoria(idCategoria);
        dto.setTipo(tipo);
        dto.setPrecioBase(precioBase);
        dto.setActivo(true);
        dto.actualizarCamposFormateados();
        return dto;
    }

    public com.stamos.model.Producto toEntity() {
        com.stamos.model.Producto producto = new com.stamos.model.Producto();
        if (this.idProducto != null) {
            producto.setIdProducto(this.idProducto);
        }
        producto.setIdCategoria(this.idCategoria);
        producto.setNombreItem(this.nombreItem);
        producto.setDescripcion(this.descripcion);
        producto.setTipo(this.tipo);
        producto.setPrecioBase(this.precioBase);
        producto.setImagenUrl(this.imagenUrl);
        producto.setActivo(this.activo);
        producto.setFechaCreacion(this.fechaCreacion);
        producto.setFechaActualizacion(this.fechaActualizacion);
        producto.setIdUsuarioCreacion(this.idUsuarioCreacion);
        producto.setIdUsuarioActualizacion(this.idUsuarioActualizacion);
        return producto;
    }

    @Override
    public String toString() {
        return "ProductoDTO{" +
                "idProducto=" + idProducto +
                ", nombreItem='" + nombreItem + '\'' +
                ", tipo='" + tipo + '\'' +
                ", precioBase=" + precioBase +
                ", activo=" + activo +
                ", nombreCategoria='" + nombreCategoria + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductoDTO that = (ProductoDTO) o;
        return idProducto != null && idProducto.equals(that.idProducto);
    }

    @Override
    public int hashCode() {
        return idProducto != null ? idProducto.hashCode() : 0;
    }
}