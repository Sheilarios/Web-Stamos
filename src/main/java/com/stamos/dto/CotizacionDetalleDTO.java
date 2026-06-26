package com.stamos.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CotizacionDetalleDTO {

    private Integer idDetalleVersion;
    private Integer idVersion;
    private Integer idProducto;
    private String nombreProducto;
    private String descripcionProducto;
    private String categoriaProducto;
    private Integer idCategoria;
    private String tipoProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuento;
    private BigDecimal subtotalLinea;
    private String imagenUrl;
    private BigDecimal precioBase;
    private boolean activo;
    private String unidadMedida;

    public CotizacionDetalleDTO() {
        this.cantidad = 1;
        this.precioUnitario = BigDecimal.ZERO;
        this.descuento = BigDecimal.ZERO;
        this.subtotalLinea = BigDecimal.ZERO;
        this.activo = true;
        this.unidadMedida = "Unidad";
    }

    public CotizacionDetalleDTO(Integer idProducto, String nombreProducto, 
                               Integer cantidad, BigDecimal precioUnitario) {
        this();
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }

    public CotizacionDetalleDTO(Integer idProducto, String nombreProducto, 
                               String descripcionProducto, String categoriaProducto,
                               String tipoProducto, Integer cantidad, 
                               BigDecimal precioUnitario, BigDecimal descuento) {
        this();
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.descripcionProducto = descripcionProducto;
        this.categoriaProducto = categoriaProducto;
        this.tipoProducto = tipoProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.descuento = descuento != null ? descuento : BigDecimal.ZERO;
        calcularSubtotal();
    }

    public Integer getIdDetalleVersion() {
        return idDetalleVersion;
    }

    public void setIdDetalleVersion(Integer idDetalleVersion) {
        this.idDetalleVersion = idDetalleVersion;
    }

    public Integer getIdVersion() {
        return idVersion;
    }

    public void setIdVersion(Integer idVersion) {
        this.idVersion = idVersion;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getDescripcionProducto() {
        return descripcionProducto;
    }

    public void setDescripcionProducto(String descripcionProducto) {
        this.descripcionProducto = descripcionProducto;
    }

    public String getCategoriaProducto() {
        return categoriaProducto;
    }

    public void setCategoriaProducto(String categoriaProducto) {
        this.categoriaProducto = categoriaProducto;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(String tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        calcularSubtotal();
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento != null ? descuento : BigDecimal.ZERO;
        calcularSubtotal();
    }

    public BigDecimal getSubtotalLinea() {
        return subtotalLinea;
    }

    public void setSubtotalLinea(BigDecimal subtotalLinea) {
        this.subtotalLinea = subtotalLinea;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public void calcularSubtotal() {
        if (cantidad != null && precioUnitario != null) {
            BigDecimal subtotal = precioUnitario.multiply(new BigDecimal(cantidad));
            if (descuento != null) {
                subtotal = subtotal.subtract(descuento);
            }
            if (subtotal.compareTo(BigDecimal.ZERO) < 0) {
                subtotal = BigDecimal.ZERO;
            }
            this.subtotalLinea = subtotal.setScale(2, RoundingMode.HALF_UP);
        } else {
            this.subtotalLinea = BigDecimal.ZERO;
        }
    }

    public void aplicarDescuentoPorcentual(BigDecimal porcentajeDescuento) {
        if (porcentajeDescuento != null && porcentajeDescuento.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal descuentoCalculado = precioUnitario
                .multiply(porcentajeDescuento)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            this.descuento = descuentoCalculado;
            calcularSubtotal();
        }
    }

    public boolean tieneDescuento() {
        return descuento != null && descuento.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean esValido() {
        return idProducto != null && 
               nombreProducto != null && !nombreProducto.trim().isEmpty() &&
               cantidad != null && cantidad > 0 &&
               precioUnitario != null && precioUnitario.compareTo(BigDecimal.ZERO) >= 0;
    }

    public BigDecimal getTotalConIgv(BigDecimal tasaIgv) {
        if (subtotalLinea == null || tasaIgv == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal igv = subtotalLinea.multiply(tasaIgv)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        return subtotalLinea.add(igv);
    }

    public BigDecimal getIgvLinea(BigDecimal tasaIgv) {
        if (subtotalLinea == null || tasaIgv == null) {
            return BigDecimal.ZERO;
        }
        return subtotalLinea.multiply(tasaIgv)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    public CotizacionDetalleDTO clonar() {
        CotizacionDetalleDTO clone = new CotizacionDetalleDTO();
        clone.setIdDetalleVersion(this.idDetalleVersion);
        clone.setIdVersion(this.idVersion);
        clone.setIdProducto(this.idProducto);
        clone.setNombreProducto(this.nombreProducto);
        clone.setDescripcionProducto(this.descripcionProducto);
        clone.setCategoriaProducto(this.categoriaProducto);
        clone.setIdCategoria(this.idCategoria);
        clone.setTipoProducto(this.tipoProducto);
        clone.setCantidad(this.cantidad);
        clone.setPrecioUnitario(this.precioUnitario);
        clone.setDescuento(this.descuento);
        clone.setSubtotalLinea(this.subtotalLinea);
        clone.setImagenUrl(this.imagenUrl);
        clone.setPrecioBase(this.precioBase);
        clone.setActivo(this.activo);
        clone.setUnidadMedida(this.unidadMedida);
        return clone;
    }

    public static CotizacionDetalleDTO fromProducto(com.stamos.model.Producto producto, Integer cantidad) {
        CotizacionDetalleDTO dto = new CotizacionDetalleDTO();
        dto.setIdProducto(producto.getIdProducto());
        dto.setNombreProducto(producto.getNombreItem());
        dto.setDescripcionProducto(producto.getDescripcion());
        dto.setTipoProducto(producto.getTipo());
        dto.setPrecioUnitario(producto.getPrecioBase());
        dto.setPrecioBase(producto.getPrecioBase());
        dto.setImagenUrl(producto.getImagenUrl());
        dto.setActivo(producto.isActivo());
        dto.setCantidad(cantidad != null ? cantidad : 1);
        dto.setDescuento(BigDecimal.ZERO);
        dto.calcularSubtotal();
        return dto;
    }

    public static CotizacionDetalleDTO fromEntity(com.stamos.model.CotizacionDetalle detalle, 
                                                   com.stamos.model.Producto producto) {
        CotizacionDetalleDTO dto = new CotizacionDetalleDTO();
        dto.setIdDetalleVersion(detalle.getIdDetalleVersion());
        dto.setIdVersion(detalle.getIdVersion());
        dto.setIdProducto(detalle.getIdProducto());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setDescuento(detalle.getDescuento());
        dto.setSubtotalLinea(detalle.getSubtotalLinea());
        
        if (producto != null) {
            dto.setNombreProducto(producto.getNombreItem());
            dto.setDescripcionProducto(producto.getDescripcion());
            dto.setTipoProducto(producto.getTipo());
            dto.setPrecioBase(producto.getPrecioBase());
            dto.setImagenUrl(producto.getImagenUrl());
            dto.setActivo(producto.isActivo());
        }
        
        return dto;
    }

    public com.stamos.model.CotizacionDetalle toEntity() {
        com.stamos.model.CotizacionDetalle detalle = new com.stamos.model.CotizacionDetalle();
        detalle.setIdDetalleVersion(this.idDetalleVersion);
        detalle.setIdVersion(this.idVersion);
        detalle.setIdProducto(this.idProducto);
        detalle.setCantidad(this.cantidad);
        detalle.setPrecioUnitario(this.precioUnitario);
        detalle.setDescuento(this.descuento);
        detalle.setSubtotalLinea(this.subtotalLinea);
        return detalle;
    }

    @Override
    public String toString() {
        return "CotizacionDetalleDTO{" +
                "idDetalleVersion=" + idDetalleVersion +
                ", idProducto=" + idProducto +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", descuento=" + descuento +
                ", subtotalLinea=" + subtotalLinea +
                ", tipoProducto='" + tipoProducto + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CotizacionDetalleDTO that = (CotizacionDetalleDTO) o;
        if (idDetalleVersion != null) {
            return idDetalleVersion.equals(that.idDetalleVersion);
        }
        return idProducto != null && idProducto.equals(that.idProducto);
    }

    @Override
    public int hashCode() {
        return idDetalleVersion != null ? idDetalleVersion.hashCode() : 
               (idProducto != null ? idProducto.hashCode() : 0);
    }
}