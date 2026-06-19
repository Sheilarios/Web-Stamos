package com.stamos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Entidad CotizacionDetalle (DetalleVersionCotizacion).
 * 
 * <p>Esta entidad representa los detalles de una cotización, incluyendo
 * los productos o servicios, cantidades, precios y descuentos aplicados.</p>
 * 
 * <p>Implementa los requerimientos:</p>
 * <ul>
 *   <li>RN-04: Toda cotización debe incluir al menos un producto o servicio</li>
 *   <li>RF-05: Crear cotizaciones con al menos un ítem</li>
 *   <li>RF-07: Permitir registro de precios base o negociados</li>
 * </ul>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
@Entity
@Table(name = "DetalleVersionCotizacion",
       indexes = {
           @Index(name = "idx_detalle_version", columnList = "id_version"),
           @Index(name = "idx_detalle_producto", columnList = "id_producto")
       })
public class CotizacionDetalle {

    // ============================================
    // ATRIBUTOS PRINCIPALES
    // ============================================

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

    // ============================================
    // CAMPOS ADICIONALES (NO EN BD)
    // ============================================

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

    // ============================================
    // CONSTRUCTORES
    // ============================================

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
            
            // Calcular subtotal bruto (sin descuento)
            this.subtotalBruto = precioUnitario.multiply(new BigDecimal(cantidad))
                    .setScale(2, RoundingMode.HALF_UP);
            
            // Calcular porcentaje de descuento
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

    /**
     * Calcula el precio unitario con IGV.
     * 
     * @param tasaIgv Tasa de IGV (ej: 18.00 para 18%)
     */
    public void calcularPrecioConIgv(BigDecimal tasaIgv) {
        if (precioUnitario != null && tasaIgv != null) {
            BigDecimal igv = precioUnitario.multiply(tasaIgv)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            this.precioUnitarioConIgv = precioUnitario.add(igv);
        } else {
            this.precioUnitarioConIgv = precioUnitario;
        }
    }

    /**
     * Calcula el total de la línea con IGV.
     * 
     * @param tasaIgv Tasa de IGV
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
               cantidad != null && cantidad > 0 &&
               precioUnitario != null && precioUnitario.compareTo(BigDecimal.ZERO) >= 0;
    }

    /**
     * Verifica si el producto es de tipo Alquiler.
     * 
     * @return true si es Alquiler, false en caso contrario
     */
    public boolean esAlquiler() {
        return "Alquiler".equals(this.tipoProducto);
    }

    /**
     * Verifica si el producto es de tipo Venta.
     * 
     * @return true si es Venta, false en caso contrario
     */
    public boolean esVenta() {
        return "Venta".equals(this.tipoProducto);
    }

    /**
     * Verifica si el producto es de tipo Servicio.
     * 
     * @return true si es Servicio, false en caso contrario
     */
    public boolean esServicio() {
        return "Servicio".equals(this.tipoProducto);
    }

    /**
     * Obtiene el subtotal de la línea formateado.
     * 
     * @return Subtotal formateado
     */
    public String getSubtotalFormateado() {
        if (subtotalLinea == null) return "S/ 0.00";
        return "S/ " + subtotalLinea.setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el precio unitario formateado.
     * 
     * @return Precio unitario formateado
     */
    public String getPrecioUnitarioFormateado() {
        if (precioUnitario == null) return "S/ 0.00";
        return "S/ " + precioUnitario.setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el descuento formateado.
     * 
     * @return Descuento formateado
     */
    public String getDescuentoFormateado() {
        if (descuento == null) return "S/ 0.00";
        return "S/ " + descuento.setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el total con IGV formateado.
     * 
     * @param tasaIgv Tasa de IGV
     * @return Total con IGV formateado
     */
    public String getTotalConIgvFormateado(BigDecimal tasaIgv) {
        BigDecimal total = getTotalConIgv(tasaIgv);
        return "S/ " + total.setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el precio base formateado.
     * 
     * @return Precio base formateado
     */
    public String getPrecioBaseFormateado() {
        if (precioBase == null) return "S/ 0.00";
        return "S/ " + precioBase.setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el porcentaje de descuento formateado.
     * 
     * @return Porcentaje de descuento formateado
     */
    public String getPorcentajeDescuentoFormateado() {
        if (porcentajeDescuento == null) return "0%";
        return porcentajeDescuento.setScale(0, RoundingMode.HALF_UP).toString() + "%";
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
     * Obtiene el resumen del detalle.
     * 
     * @return Resumen
     */
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

    /**
     * Verifica si el producto está activo.
     * 
     * @return true si está activo, false en caso contrario
     */
    public boolean isProductoActivo() {
        // Este campo no está en la entidad, pero puede ser poblado desde el producto
        return true; // Por defecto
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

    /**
     * Convierte a JSON con todos los campos.
     * 
     * @return JSON completo
     */
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

    // ============================================
    // MÉTODOS OVERRIDE
    // ============================================

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

    // ============================================
    // MÉTODOS ESTÁTICOS DE FÁBRICA
    // ============================================

    /**
     * Crea un detalle desde un producto.
     * 
     * @param idVersion ID de la versión
     * @param producto Producto
     * @param cantidad Cantidad solicitada
     * @return Detalle de cotización
     */
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
        
        // Poblar campos transient
        detalle.setNombreProducto(producto.getNombreItem());
        detalle.setDescripcionProducto(producto.getDescripcion());
        detalle.setTipoProducto(producto.getTipo());
        detalle.setImagenUrl(producto.getImagenUrl());
        detalle.setPrecioBase(producto.getPrecioBase());
        
        detalle.calcularSubtotal();
        return detalle;
    }

    /**
     * Crea un detalle desde un producto con precio negociado.
     * 
     * @param idVersion ID de la versión
     * @param producto Producto
     * @param cantidad Cantidad solicitada
     * @param precioNegociado Precio negociado
     * @return Detalle de cotización
     */
    public static CotizacionDetalle fromProductoConPrecioNegociado(Integer idVersion, Producto producto,
                                                                  Integer cantidad, BigDecimal precioNegociado) {
        CotizacionDetalle detalle = fromProducto(idVersion, producto, cantidad);
        if (detalle != null && precioNegociado != null) {
            detalle.setPrecioUnitario(precioNegociado);
            detalle.calcularSubtotal();
        }
        return detalle;
    }

    /**
     * Crea un detalle desde un producto con descuento.
     * 
     * @param idVersion ID de la versión
     * @param producto Producto
     * @param cantidad Cantidad solicitada
     * @param descuento Descuento aplicado
     * @return Detalle de cotización
     */
    public static CotizacionDetalle fromProductoConDescuento(Integer idVersion, Producto producto,
                                                            Integer cantidad, BigDecimal descuento) {
        CotizacionDetalle detalle = fromProducto(idVersion, producto, cantidad);
        if (detalle != null && descuento != null) {
            detalle.setDescuento(descuento);
            detalle.calcularSubtotal();
        }
        return detalle;
    }

    /**
     * Crea un detalle desde un DTO.
     * 
     * @param dto DTO del detalle
     * @return Detalle de cotización
     */
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
        
        // Poblar campos transient
        detalle.setNombreProducto(dto.getNombreProducto());
        detalle.setDescripcionProducto(dto.getDescripcionProducto());
        detalle.setCategoriaProducto(dto.getCategoriaProducto());
        detalle.setTipoProducto(dto.getTipoProducto());
        detalle.setImagenUrl(dto.getImagenUrl());
        detalle.setPrecioBase(dto.getPrecioBase());
        
        detalle.calcularSubtotal();
        return detalle;
    }

    /**
     * Convierte el detalle a un DTO.
     * 
     * @return DTO del detalle
     */
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