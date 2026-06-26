package com.stamos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "DetalleVersionCotizacion",
       indexes = {
           @Index(name = "idx_detalle_version", columnList = "id_version"),
           @Index(name = "idx_detalle_producto", columnList = "id_producto")
       })
public final class CotizacionDetalle {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_version")
    private Integer idDetalleVersion;

    @NotNull(message = "La versión de la cotización es obligatoria")
    @Column(name = "id_version", nullable = false)
    private Integer idVersion;

    @NotNull(message = "El producto es obligatorio")
    @Column(name = "id_producto", nullable = false)
    private Integer idProducto;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a 0")
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @PositiveOrZero(message = "El precio unitario debe ser mayor o igual a 0")
    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @NotNull(message = "El descuento es obligatorio")
    @PositiveOrZero(message = "El descuento debe ser mayor o igual a 0")
    @Column(name = "descuento", nullable = false, precision = 12, scale = 2)
    private BigDecimal descuento;

    @Column(name = "subtotal_linea", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotalLinea;

    @Transient
    private String nombreProducto;

    @Transient
    private String descripcionProducto;

    @Transient
    private String categoriaProducto;

    @Transient
    private String tipoProducto;

    @Transient
    private String imagenUrl;

    @Transient
    private BigDecimal precioBase;

    @Transient
    private BigDecimal precioUnitarioConIgv;

    @Transient
    private BigDecimal subtotalBruto;

    @Transient
    private BigDecimal porcentajeDescuento;

    @Transient
    private Integer idCategoriaProducto;

    public CotizacionDetalle() {
        this.cantidad = 1;
        this.precioUnitario = BigDecimal.ZERO;
        this.descuento = BigDecimal.ZERO;
        this.subtotalLinea = BigDecimal.ZERO;
    }

    public CotizacionDetalle(Integer idVersion, Integer idProducto, Integer cantidad, 
                            BigDecimal precioUnitario) {
        this();
        this.idVersion = idVersion;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }
    public CotizacionDetalle(Integer idVersion, Integer idProducto, Integer cantidad, 
                            BigDecimal precioUnitario, BigDecimal descuento) {
        this(idVersion, idProducto, cantidad, precioUnitario);
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

    public String getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(String tipoProducto) {
        this.tipoProducto = tipoProducto;
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

    public BigDecimal getPrecioUnitarioConIgv() {
        return precioUnitarioConIgv;
    }

    public void setPrecioUnitarioConIgv(BigDecimal precioUnitarioConIgv) {
        this.precioUnitarioConIgv = precioUnitarioConIgv;
    }

    public BigDecimal getSubtotalBruto() {
        return subtotalBruto;
    }

    public void setSubtotalBruto(BigDecimal subtotalBruto) {
        this.subtotalBruto = subtotalBruto;
    }

    public BigDecimal getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public void setPorcentajeDescuento(BigDecimal porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }

    public Integer getIdCategoriaProducto() {
        return idCategoriaProducto;
    }

    public void setIdCategoriaProducto(Integer idCategoriaProducto) {
        this.idCategoriaProducto = idCategoriaProducto;
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
            
            this.subtotalBruto = precioUnitario.multiply(new BigDecimal(cantidad))
                    .setScale(2, RoundingMode.HALF_UP);
            
            if (this.subtotalBruto != null && this.subtotalBruto.compareTo(BigDecimal.ZERO) > 0) {
                this.porcentajeDescuento = descuento != null ? 
                    descuento.multiply(new BigDecimal("100"))
                        .divide(this.subtotalBruto, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
            } else {
                this.porcentajeDescuento = BigDecimal.ZERO;
            }
        } else {
            this.subtotalLinea = BigDecimal.ZERO;
            this.subtotalBruto = BigDecimal.ZERO;
            this.porcentajeDescuento = BigDecimal.ZERO;
        }
    }

    public void calcularPrecioConIgv(BigDecimal tasaIgv) {
        if (precioUnitario != null && tasaIgv != null) {
            BigDecimal igv = precioUnitario.multiply(tasaIgv)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            this.precioUnitarioConIgv = precioUnitario.add(igv);
        } else {
            this.precioUnitarioConIgv = precioUnitario;
        }
    }

    public BigDecimal getTotalConIgv(BigDecimal tasaIgv) {
        if (subtotalLinea == null || tasaIgv == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal igv = subtotalLinea.multiply(tasaIgv)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        return subtotalLinea.add(igv);
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
               cantidad != null && cantidad > 0 &&
               precioUnitario != null && precioUnitario.compareTo(BigDecimal.ZERO) >= 0;
    }

    public boolean esAlquiler() {
        return "Alquiler".equals(this.tipoProducto);
    }

    public boolean esVenta() {
        return "Venta".equals(this.tipoProducto);
    }

    public boolean esServicio() {
        return "Servicio".equals(this.tipoProducto);
    }

    public String getSubtotalFormateado() {
        if (subtotalLinea == null) return "S/ 0.00";
        return "S/ " + subtotalLinea.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public String getPrecioUnitarioFormateado() {
        if (precioUnitario == null) return "S/ 0.00";
        return "S/ " + precioUnitario.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public String getDescuentoFormateado() {
        if (descuento == null) return "S/ 0.00";
        return "S/ " + descuento.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public String getTotalConIgvFormateado(BigDecimal tasaIgv) {
        BigDecimal total = getTotalConIgv(tasaIgv);
        return "S/ " + total.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public String getPrecioBaseFormateado() {
        if (precioBase == null) return "S/ 0.00";
        return "S/ " + precioBase.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public String getPorcentajeDescuentoFormateado() {
        if (porcentajeDescuento == null) return "0%";
        return porcentajeDescuento.setScale(0, RoundingMode.HALF_UP).toString() + "%";
    }

    public String getTipoProductoLegible() {
        if (tipoProducto == null) return "No especificado";
        
        return switch (tipoProducto) {
            case "Alquiler" -> "Alquiler";
            case "Venta" -> "Venta";
            case "Servicio" -> "Servicio";
            default -> tipoProducto;
        };
    }

    public String getTipoProductoBadgeClass() {
        if (tipoProducto == null) return "badge-secondary";
        
        return switch (tipoProducto) {
            case "Alquiler" -> "badge-alquiler";
            case "Venta" -> "badge-venta";
            case "Servicio" -> "badge-servicio";
            default -> "badge-secondary";
        };
    }

    public String getTipoProductoIcono() {
        if (tipoProducto == null) return "ti ti-box";
        
        return switch (tipoProducto) {
            case "Alquiler" -> "ti ti-calendar";
            case "Venta" -> "ti ti-shopping-cart";
            case "Servicio" -> "ti ti-tools";
            default -> "ti ti-box";
        };
    }

    public String getResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append(cantidad).append("x ");
        sb.append(nombreProducto != null ? nombreProducto : "Producto");
        sb.append(" - ").append(getPrecioUnitarioFormateado());
        if (tieneDescuento()) {
            sb.append(" (Desc: ").append(getDescuentoFormateado()).append(")");
        }
        return sb.toString();
    }

    public boolean isProductoActivo() {
        return true; 
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"idDetalleVersion\":").append(idDetalleVersion).append(",");
        sb.append("\"idVersion\":").append(idVersion).append(",");
        sb.append("\"idProducto\":").append(idProducto).append(",");
        sb.append("\"nombreProducto\":\"").append(nombreProducto != null ? nombreProducto : "").append("\",");
        sb.append("\"cantidad\":").append(cantidad).append(",");
        sb.append("\"precioUnitario\":").append(precioUnitario != null ? precioUnitario : 0).append(",");
        sb.append("\"descuento\":").append(descuento != null ? descuento : 0).append(",");
        sb.append("\"subtotalLinea\":").append(subtotalLinea != null ? subtotalLinea : 0);
        sb.append("}");
        return sb.toString();
    }

    public String toJsonCompleto() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"idDetalleVersion\":").append(idDetalleVersion).append(",");
        sb.append("\"idVersion\":").append(idVersion).append(",");
        sb.append("\"idProducto\":").append(idProducto).append(",");
        sb.append("\"nombreProducto\":\"").append(nombreProducto != null ? nombreProducto : "").append("\",");
        sb.append("\"descripcionProducto\":\"").append(descripcionProducto != null ? descripcionProducto : "").append("\",");
        sb.append("\"categoriaProducto\":\"").append(categoriaProducto != null ? categoriaProducto : "").append("\",");
        sb.append("\"tipoProducto\":\"").append(tipoProducto != null ? tipoProducto : "").append("\",");
        sb.append("\"cantidad\":").append(cantidad).append(",");
        sb.append("\"precioUnitario\":").append(precioUnitario != null ? precioUnitario : 0).append(",");
        sb.append("\"descuento\":").append(descuento != null ? descuento : 0).append(",");
        sb.append("\"subtotalLinea\":").append(subtotalLinea != null ? subtotalLinea : 0).append(",");
        sb.append("\"subtotalBruto\":").append(subtotalBruto != null ? subtotalBruto : 0).append(",");
        sb.append("\"porcentajeDescuento\":").append(porcentajeDescuento != null ? porcentajeDescuento : 0);
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "CotizacionDetalle{" +
                "idDetalleVersion=" + idDetalleVersion +
                ", idVersion=" + idVersion +
                ", idProducto=" + idProducto +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", descuento=" + descuento +
                ", subtotalLinea=" + subtotalLinea +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CotizacionDetalle that = (CotizacionDetalle) o;
        return idDetalleVersion != null && idDetalleVersion.equals(that.idDetalleVersion);
    }

    @Override
    public int hashCode() {
        return idDetalleVersion != null ? idDetalleVersion.hashCode() : 0;
    }

    public static CotizacionDetalle fromProducto(Integer idVersion, Producto producto, Integer cantidad) {
        if (producto == null) {
            return null;
        }
        
        CotizacionDetalle detalle = new CotizacionDetalle();
        detalle.setIdVersion(idVersion);
        detalle.setIdProducto(producto.getIdProducto());
        detalle.setCantidad(cantidad != null ? cantidad : 1);
        detalle.setPrecioUnitario(producto.getPrecioBase());
        detalle.setDescuento(BigDecimal.ZERO);
        
        detalle.setNombreProducto(producto.getNombreItem());
        detalle.setDescripcionProducto(producto.getDescripcion());
        detalle.setTipoProducto(producto.getTipo());
        detalle.setImagenUrl(producto.getImagenUrl());
        detalle.setPrecioBase(producto.getPrecioBase());
        
        detalle.calcularSubtotal();
        return detalle;
    }

    public static CotizacionDetalle fromProductoConPrecioNegociado(Integer idVersion, Producto producto,
                                                                  Integer cantidad, BigDecimal precioNegociado) {
        CotizacionDetalle detalle = fromProducto(idVersion, producto, cantidad);
        if (detalle != null && precioNegociado != null) {
            detalle.setPrecioUnitario(precioNegociado);
            detalle.calcularSubtotal();
        }
        return detalle;
    }

    public static CotizacionDetalle fromProductoConDescuento(Integer idVersion, Producto producto,
                                                            Integer cantidad, BigDecimal descuento) {
        CotizacionDetalle detalle = fromProducto(idVersion, producto, cantidad);
        if (detalle != null && descuento != null) {
            detalle.setDescuento(descuento);
            detalle.calcularSubtotal();
        }
        return detalle;
    }


    public static CotizacionDetalle fromDTO(com.stamos.dto.CotizacionDetalleDTO dto) {
        if (dto == null) {
            return null;
        }
        
        CotizacionDetalle detalle = new CotizacionDetalle();
        if (dto.getIdDetalleVersion() != null) {
            detalle.setIdDetalleVersion(dto.getIdDetalleVersion());
        }
        detalle.setIdVersion(dto.getIdVersion());
        detalle.setIdProducto(dto.getIdProducto());
        detalle.setCantidad(dto.getCantidad());
        detalle.setPrecioUnitario(dto.getPrecioUnitario());
        detalle.setDescuento(dto.getDescuento());
        
        detalle.setNombreProducto(dto.getNombreProducto());
        detalle.setDescripcionProducto(dto.getDescripcionProducto());
        detalle.setCategoriaProducto(dto.getCategoriaProducto());
        detalle.setTipoProducto(dto.getTipoProducto());
        detalle.setImagenUrl(dto.getImagenUrl());
        detalle.setPrecioBase(dto.getPrecioBase());
        
        detalle.calcularSubtotal();
        return detalle;
    }
    
    public com.stamos.dto.CotizacionDetalleDTO toDTO() {
        com.stamos.dto.CotizacionDetalleDTO dto = new com.stamos.dto.CotizacionDetalleDTO();
        dto.setIdDetalleVersion(this.idDetalleVersion);
        dto.setIdVersion(this.idVersion);
        dto.setIdProducto(this.idProducto);
        dto.setNombreProducto(this.nombreProducto);
        dto.setDescripcionProducto(this.descripcionProducto);
        dto.setCategoriaProducto(this.categoriaProducto);
        dto.setTipoProducto(this.tipoProducto);
        dto.setCantidad(this.cantidad);
        dto.setPrecioUnitario(this.precioUnitario);
        dto.setDescuento(this.descuento);
        dto.setSubtotalLinea(this.subtotalLinea);
        dto.setImagenUrl(this.imagenUrl);
        dto.setPrecioBase(this.precioBase);
        return dto;
    }
}