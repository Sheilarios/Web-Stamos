package com.stamos.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * DTO (Data Transfer Object) para los detalles de una cotización.
 * 
 * <p>Este DTO se utiliza para transferir datos de los detalles de cotización
 * entre la capa de servicio y la capa de presentación, permitiendo una mejor
 * organización y manejo de los datos de cada ítem en la cotización.</p>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
public class CotizacionDetalleDTO {

    // ============================================
    // ATRIBUTOS PRINCIPALES
    // ============================================

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

    // ============================================
    // CAMPOS ADICIONALES PARA LA VISTA
    // ============================================

    private String imagenUrl;
    private BigDecimal precioBase;
    private boolean activo;
    private String unidadMedida;

    // ============================================
    // CONSTRUCTORES
    // ============================================

    /**
     * Constructor por defecto.
     * Inicializa los valores por defecto.
     */
    public CotizacionDetalleDTO() {
        this.cantidad = 1;
        this.precioUnitario = BigDecimal.ZERO;
        this.descuento = BigDecimal.ZERO;
        this.subtotalLinea = BigDecimal.ZERO;
        this.activo = true;
        this.unidadMedida = "Unidad";
    }

    /**
     * Constructor con los campos principales.
     * 
     * @param idProducto ID del producto
     * @param nombreProducto Nombre del producto
     * @param cantidad Cantidad solicitada
     * @param precioUnitario Precio unitario
     */
    public CotizacionDetalleDTO(Integer idProducto, String nombreProducto, 
                               Integer cantidad, BigDecimal precioUnitario) {
        this();
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }

    /**
     * Constructor completo.
     * 
     * @param idProducto ID del producto
     * @param nombreProducto Nombre del producto
     * @param descripcionProducto Descripción del producto
     * @param categoriaProducto Categoría del producto
     * @param tipoProducto Tipo de producto (Alquiler, Venta, Servicio)
     * @param cantidad Cantidad solicitada
     * @param precioUnitario Precio unitario
     * @param descuento Descuento aplicado
     */
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

    // ============================================
    // GETTERS Y SETTERS
    // ============================================

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

    // ============================================
    // MÉTODOS DE NEGOCIO
    // ============================================

    /**
     * Calcula el subtotal de la línea basado en cantidad, precio unitario y descuento.
     * Fórmula: (cantidad * precioUnitario) - descuento
     */
    public void calcularSubtotal() {
        if (cantidad != null && precioUnitario != null) {
            BigDecimal subtotal = precioUnitario.multiply(new BigDecimal(cantidad));
            if (descuento != null) {
                subtotal = subtotal.subtract(descuento);
            }
            // Asegurar que el subtotal no sea negativo
            if (subtotal.compareTo(BigDecimal.ZERO) < 0) {
                subtotal = BigDecimal.ZERO;
            }
            this.subtotalLinea = subtotal.setScale(2, RoundingMode.HALF_UP);
        } else {
            this.subtotalLinea = BigDecimal.ZERO;
        }
    }

    /**
     * Aplica un descuento porcentual al precio unitario.
     * 
     * @param porcentajeDescuento Porcentaje de descuento (ej: 10.0 para 10%)
     */
    public void aplicarDescuentoPorcentual(BigDecimal porcentajeDescuento) {
        if (porcentajeDescuento != null && porcentajeDescuento.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal descuentoCalculado = precioUnitario
                .multiply(porcentajeDescuento)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            this.descuento = descuentoCalculado;
            calcularSubtotal();
        }
    }

    /**
     * Verifica si el detalle tiene descuento aplicado.
     * 
     * @return true si tiene descuento, false en caso contrario
     */
    public boolean tieneDescuento() {
        return descuento != null && descuento.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Verifica si el detalle es válido.
     * 
     * @return true si es válido, false en caso contrario
     */
    public boolean esValido() {
        return idProducto != null && 
               nombreProducto != null && !nombreProducto.trim().isEmpty() &&
               cantidad != null && cantidad > 0 &&
               precioUnitario != null && precioUnitario.compareTo(BigDecimal.ZERO) >= 0;
    }

    /**
     * Obtiene el total de la línea incluyendo IGV (si se aplica).
     * 
     * @param tasaIgv Tasa de IGV (ej: 18.00 para 18%)
     * @return Total con IGV
     */
    public BigDecimal getTotalConIgv(BigDecimal tasaIgv) {
        if (subtotalLinea == null || tasaIgv == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal igv = subtotalLinea.multiply(tasaIgv)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        return subtotalLinea.add(igv);
    }

    /**
     * Obtiene el IGV de la línea.
     * 
     * @param tasaIgv Tasa de IGV (ej: 18.00 para 18%)
     * @return IGV calculado
     */
    public BigDecimal getIgvLinea(BigDecimal tasaIgv) {
        if (subtotalLinea == null || tasaIgv == null) {
            return BigDecimal.ZERO;
        }
        return subtotalLinea.multiply(tasaIgv)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    /**
     * Clona el detalle actual.
     * 
     * @return Nueva instancia con los mismos valores
     */
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

    // ============================================
    // MÉTODOS DE FORMATO PARA LA VISTA
    // ============================================

    /**
     * Obtiene el subtotal formateado como moneda.
     * 
     * @return Subtotal formateado
     */
    public String getSubtotalFormateado() {
        if (subtotalLinea == null) {
            return "S/ 0.00";
        }
        return "S/ " + subtotalLinea.setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el precio unitario formateado como moneda.
     * 
     * @return Precio unitario formateado
     */
    public String getPrecioUnitarioFormateado() {
        if (precioUnitario == null) {
            return "S/ 0.00";
        }
        return "S/ " + precioUnitario.setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el descuento formateado como moneda.
     * 
     * @return Descuento formateado
     */
    public String getDescuentoFormateado() {
        if (descuento == null) {
            return "S/ 0.00";
        }
        return "S/ " + descuento.setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el total con IGV formateado como moneda.
     * 
     * @param tasaIgv Tasa de IGV
     * @return Total con IGV formateado
     */
    public String getTotalConIgvFormateado(BigDecimal tasaIgv) {
        BigDecimal total = getTotalConIgv(tasaIgv);
        return "S/ " + total.setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el tipo de producto en formato legible.
     * 
     * @return Tipo de producto legible
     */
    public String getTipoProductoLegible() {
        if (tipoProducto == null) return "No especificado";
        
        switch (tipoProducto) {
            case "Alquiler":
                return "Alquiler";
            case "Venta":
                return "Venta";
            case "Servicio":
                return "Servicio";
            default:
                return tipoProducto;
        }
    }

    /**
     * Obtiene la clase CSS para el badge del tipo de producto.
     * 
     * @return Clase CSS
     */
    public String getTipoProductoBadgeClass() {
        if (tipoProducto == null) return "badge-secondary";
        
        switch (tipoProducto) {
            case "Alquiler":
                return "badge-alquiler";
            case "Venta":
                return "badge-venta";
            case "Servicio":
                return "badge-servicio";
            default:
                return "badge-secondary";
        }
    }

    /**
     * Obtiene el ícono para el tipo de producto.
     * 
     * @return Ícono CSS
     */
    public String getTipoProductoIcono() {
        if (tipoProducto == null) return "ti ti-box";
        
        switch (tipoProducto) {
            case "Alquiler":
                return "ti ti-calendar";
            case "Venta":
                return "ti ti-shopping-cart";
            case "Servicio":
                return "ti ti-tools";
            default:
                return "ti ti-box";
        }
    }

    /**
     * Obtiene el subtotal sin descuento.
     * 
     * @return Subtotal bruto
     */
    public BigDecimal getSubtotalBruto() {
        if (cantidad != null && precioUnitario != null) {
            return precioUnitario.multiply(new BigDecimal(cantidad))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Obtiene el subtotal bruto formateado.
     * 
     * @return Subtotal bruto formateado
     */
    public String getSubtotalBrutoFormateado() {
        return "S/ " + getSubtotalBruto().setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * Calcula el porcentaje de descuento aplicado.
     * 
     * @return Porcentaje de descuento
     */
    public BigDecimal getPorcentajeDescuento() {
        if (descuento == null || precioUnitario == null || 
            precioUnitario.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return descuento.multiply(new BigDecimal("100"))
                .divide(precioUnitario, 2, RoundingMode.HALF_UP);
    }

    /**
     * Obtiene el porcentaje de descuento formateado.
     * 
     * @return Porcentaje de descuento formateado
     */
    public String getPorcentajeDescuentoFormateado() {
        BigDecimal porcentaje = getPorcentajeDescuento();
        return porcentaje.setScale(0, RoundingMode.HALF_UP).toString() + "%";
    }

    // ============================================
    // MÉTODOS OVERRIDE
    // ============================================

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

    // ============================================
    // MÉTODOS ESTÁTICOS DE FÁBRICA
    // ============================================

    /**
     * Crea un DTO de detalle desde un producto.
     * 
     * @param producto Producto
     * @param cantidad Cantidad solicitada
     * @return DTO de detalle
     */
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

    /**
     * Crea un DTO de detalle desde una entidad de detalle de cotización.
     * 
     * @param detalle Entidad de detalle
     * @param producto Producto asociado
     * @return DTO de detalle
     */
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

    /**
     * Convierte el DTO a una entidad de detalle.
     * 
     * @return Entidad de detalle
     */
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
}