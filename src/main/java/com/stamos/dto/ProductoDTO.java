package com.stamos.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO (Data Transfer Object) para la entidad Producto.
 * 
 * <p>Este DTO se utiliza para transferir datos de productos y servicios
 * entre la capa de servicio y la capa de presentación, evitando exponer
 * directamente la entidad y permitiendo una mejor organización de los datos.</p>
 * 
 * <p>Incluye información del producto, categoría, tipo, precios,
 * disponibilidad y métricas de stock.</p>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
public class ProductoDTO {

    // ============================================
    // ATRIBUTOS PRINCIPALES
    // ============================================

    private Integer idProducto;
    private Integer idCategoria;
    private String nombreItem;
    private String descripcion;
    private String tipo;
    private BigDecimal precioBase;
    private String imagenUrl;
    private Boolean activo;

    // ============================================
    // CAMPOS DE AUDITORÍA
    // ============================================

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private Integer idUsuarioCreacion;
    private Integer idUsuarioActualizacion;
    private String nombreUsuarioCreacion;
    private String nombreUsuarioActualizacion;

    // ============================================
    // CAMPOS RELACIONADOS (DESDE CATEGORÍA)
    // ============================================

    private String nombreCategoria;
    private String descripcionCategoria;
    private Boolean categoriaActiva;

    // ============================================
    // CAMPOS DE TIPO Y FORMATO
    // ============================================

    private String tipoLegible;
    private String colorTipo;
    private String iconoTipo;

    // ============================================
    // CAMPOS DE STOCK Y DISPONIBILIDAD
    // ============================================

    private Boolean tieneStock;
    private Integer cantidadStock;
    private Integer cantidadDisponible;
    private Integer cantidadReservada;

    // ============================================
    // CAMPOS ADICIONALES
    // ============================================

    private String precioBaseFormateado;
    private String tipoBadgeClass;
    private String estadoBadgeClass;
    private String estadoLegible;
    private String estadoIcono;
    private String estadoColor;

    // ============================================
    // CONSTRUCTORES
    // ============================================

    /**
     * Constructor por defecto.
     */
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

    /**
     * Constructor con campos principales.
     * 
     * @param idProducto ID del producto
     * @param nombreItem Nombre del producto
     * @param tipo Tipo de producto
     * @param precioBase Precio base
     */
    public ProductoDTO(Integer idProducto, String nombreItem, String tipo, BigDecimal precioBase) {
        this();
        this.idProducto = idProducto;
        this.nombreItem = nombreItem;
        this.tipo = tipo;
        this.precioBase = precioBase;
        actualizarCamposFormateados();
    }

    /**
     * Constructor completo.
     * 
     * @param idProducto ID del producto
     * @param idCategoria ID de la categoría
     * @param nombreItem Nombre del producto
     * @param descripcion Descripción
     * @param tipo Tipo de producto
     * @param precioBase Precio base
     * @param imagenUrl URL de la imagen
     * @param activo Estado activo/inactivo
     */
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

    // ============================================
    // GETTERS Y SETTERS
    // ============================================

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

    // ============================================
    // MÉTODOS DE NEGOCIO
    // ============================================

    /**
     * Actualiza los campos formateados basados en los atributos.
     */
    private void actualizarCamposFormateados() {
        // Precio formateado
        if (precioBase != null) {
            this.precioBaseFormateado = "S/ " + precioBase.setScale(2, RoundingMode.HALF_UP).toString();
        } else {
            this.precioBaseFormateado = "S/ 0.00";
        }

        // Tipo de producto
        if (tipo != null) {
            switch (tipo) {
                case "Alquiler":
                    this.tipoLegible = "Alquiler";
                    this.colorTipo = "#3B82F6";
                    this.iconoTipo = "ti ti-calendar";
                    this.tipoBadgeClass = "badge-alquiler";
                    break;
                case "Venta":
                    this.tipoLegible = "Venta";
                    this.colorTipo = "#10B981";
                    this.iconoTipo = "ti ti-shopping-cart";
                    this.tipoBadgeClass = "badge-venta";
                    break;
                case "Servicio":
                    this.tipoLegible = "Servicio";
                    this.colorTipo = "#8B5CF6";
                    this.iconoTipo = "ti ti-tools";
                    this.tipoBadgeClass = "badge-servicio";
                    break;
                default:
                    this.tipoLegible = tipo;
                    this.colorTipo = "#6B7280";
                    this.iconoTipo = "ti ti-box";
                    this.tipoBadgeClass = "badge-secondary";
                    break;
            }
        }

        // Estado activo/inactivo
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

    /**
     * Verifica si el producto está activo.
     * 
     * @return true si está activo, false en caso contrario
     */
    public boolean isActivo() {
        return activo != null && activo;
    }

    /**
     * Verifica si el producto es de tipo Alquiler.
     * 
     * @return true si es Alquiler, false en caso contrario
     */
    public boolean esAlquiler() {
        return "Alquiler".equals(this.tipo);
    }

    /**
     * Verifica si el producto es de tipo Venta.
     * 
     * @return true si es Venta, false en caso contrario
     */
    public boolean esVenta() {
        return "Venta".equals(this.tipo);
    }

    /**
     * Verifica si el producto es de tipo Servicio.
     * 
     * @return true si es Servicio, false en caso contrario
     */
    public boolean esServicio() {
        return "Servicio".equals(this.tipo);
    }

    /**
     * Verifica si el producto tiene imagen.
     * 
     * @return true si tiene imagen, false en caso contrario
     */
    public boolean tieneImagen() {
        return imagenUrl != null && !imagenUrl.trim().isEmpty();
    }

    /**
     * Verifica si el producto tiene descripción.
     * 
     * @return true si tiene descripción, false en caso contrario
     */
    public boolean tieneDescripcion() {
        return descripcion != null && !descripcion.trim().isEmpty();
    }

    /**
     * Verifica si el producto tiene stock.
     * 
     * @return true si tiene stock, false en caso contrario
     */
    public boolean isTieneStock() {
        return tieneStock != null && tieneStock;
    }

    /**
     * Verifica si el producto tiene stock disponible.
     * 
     * @return true si tiene stock disponible, false en caso contrario
     */
    public boolean tieneStockDisponible() {
        return cantidadDisponible != null && cantidadDisponible > 0;
    }

    /**
     * Verifica si el producto tiene reservas activas.
     * 
     * @return true si tiene reservas, false en caso contrario
     */
    public boolean tieneReservas() {
        return cantidadReservada != null && cantidadReservada > 0;
    }

    /**
     * Calcula el porcentaje de disponibilidad.
     * 
     * @return Porcentaje de disponibilidad (0-100)
     */
    public int getPorcentajeDisponibilidad() {
        if (cantidadStock == null || cantidadStock == 0) {
            return 0;
        }
        if (cantidadDisponible == null) {
            return 0;
        }
        return (cantidadDisponible * 100) / cantidadStock;
    }

    /**
     * Obtiene la clase CSS para el badge de disponibilidad.
     * 
     * @return Clase CSS
     */
    public String getDisponibilidadBadgeClass() {
        if (!tieneStockDisponible()) {
            return "badge-danger";
        }
        int porcentaje = getPorcentajeDisponibilidad();
        if (porcentaje >= 50) {
            return "badge-success";
        } else if (porcentaje >= 25) {
            return "badge-warning";
        } else {
            return "badge-danger";
        }
    }

    /**
     * Obtiene el texto de disponibilidad.
     * 
     * @return Texto de disponibilidad
     */
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

    /**
     * Obtiene el nombre del producto con su tipo.
     * 
     * @return Nombre con tipo
     */
    public String getNombreConTipo() {
        return nombreItem + " (" + getTipoLegible() + ")";
    }

    /**
     * Obtiene el tipo con ícono.
     * 
     * @return Tipo con ícono
     */
    public String getTipoConIcono() {
        return "<i class='" + iconoTipo + "'></i> " + getTipoLegible();
    }

    /**
     * Obtiene el estado con ícono.
     * 
     * @return Estado con ícono
     */
    public String getEstadoConIcono() {
        return "<i class='" + estadoIcono + "'></i> " + getEstadoLegible();
    }

    // ============================================
    // MÉTODOS DE FORMATO PARA LA VISTA
    // ============================================

    /**
     * Obtiene el nombre del producto truncado.
     * 
     * @param maxLength Longitud máxima
     * @return Nombre truncado
     */
    public String getNombreTruncado(int maxLength) {
        if (nombreItem == null) return "";
        if (nombreItem.length() <= maxLength) return nombreItem;
        return nombreItem.substring(0, maxLength) + "...";
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
     * Obtiene la fecha de creación formateada (solo fecha).
     * 
     * @return Fecha formateada
     */
    public String getFechaCreacionSoloFecha() {
        if (fechaCreacion == null) return "-";
        return fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
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
     * Obtiene el resumen del producto.
     * 
     * @return Resumen
     */
    public String getResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append(nombreItem);
        if (tipo != null) {
            sb.append(" (").append(getTipoLegible()).append(")");
        }
        sb.append(" - ").append(getPrecioBaseFormateado());
        if (isActivo()) {
            sb.append(" ✓");
        }
        if (isTieneStock()) {
            sb.append(" | Stock: ").append(cantidadDisponible != null ? cantidadDisponible : 0);
        }
        return sb.toString();
    }

    /**
     * Obtiene la URL de la imagen o una imagen por defecto.
     * 
     * @return URL de la imagen
     */
    public String getImagenUrlOrDefault() {
        if (tieneImagen()) {
            return imagenUrl;
        }
        return "/images/placeholder-product.png";
    }

    /**
     * Obtiene la clase CSS para la tarjeta del producto.
     * 
     * @return Clase CSS
     */
    public String getCardClass() {
        if (!isActivo()) {
            return "card-inactivo";
        }
        if (!tieneStockDisponible()) {
            return "card-sin-stock";
        }
        return "card-activo";
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
        sb.append("\"idProducto\":").append(idProducto).append(",");
        sb.append("\"nombre\":\"").append(nombreItem != null ? nombreItem : "").append("\",");
        sb.append("\"tipo\":\"").append(tipo != null ? tipo : "").append("\",");
        sb.append("\"tipoLegible\":\"").append(getTipoLegible()).append("\",");
        sb.append("\"precioBase\":").append(precioBase != null ? precioBase : 0).append(",");
        sb.append("\"precioFormateado\":\"").append(getPrecioBaseFormateado()).append("\",");
        sb.append("\"activo\":").append(isActivo()).append(",");
        sb.append("\"color\":\"").append(colorTipo != null ? colorTipo : "#6B7280").append("\",");
        sb.append("\"icono\":\"").append(iconoTipo != null ? iconoTipo : "ti ti-box").append("\"");
        if (nombreCategoria != null) {
            sb.append(",\"categoria\":\"").append(nombreCategoria).append("\"");
        }
        if (imagenUrl != null) {
            sb.append(",\"imagenUrl\":\"").append(imagenUrl).append("\"");
        }
        if (cantidadDisponible != null) {
            sb.append(",\"cantidadDisponible\":").append(cantidadDisponible);
        }
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
        sb.append("\"idProducto\":").append(idProducto).append(",");
        sb.append("\"idCategoria\":").append(idCategoria).append(",");
        sb.append("\"nombre\":\"").append(nombreItem != null ? nombreItem : "").append("\",");
        sb.append("\"descripcion\":\"").append(descripcion != null ? descripcion : "").append("\",");
        sb.append("\"tipo\":\"").append(tipo != null ? tipo : "").append("\",");
        sb.append("\"tipoLegible\":\"").append(getTipoLegible()).append("\",");
        sb.append("\"precioBase\":").append(precioBase != null ? precioBase : 0).append(",");
        sb.append("\"precioFormateado\":\"").append(getPrecioBaseFormateado()).append("\",");
        sb.append("\"activo\":").append(isActivo()).append(",");
        sb.append("\"color\":\"").append(colorTipo != null ? colorTipo : "#6B7280").append("\",");
        sb.append("\"icono\":\"").append(iconoTipo != null ? iconoTipo : "ti ti-box").append("\",");
        sb.append("\"fechaCreacion\":\"").append(getFechaCreacionFormateada()).append("\"");
        if (nombreCategoria != null) {
            sb.append(",\"categoria\":\"").append(nombreCategoria).append("\"");
        }
        if (imagenUrl != null) {
            sb.append(",\"imagenUrl\":\"").append(imagenUrl).append("\"");
        }
        if (tieneStock != null) {
            sb.append(",\"tieneStock\":").append(tieneStock);
        }
        if (cantidadStock != null) {
            sb.append(",\"cantidadStock\":").append(cantidadStock);
        }
        if (cantidadDisponible != null) {
            sb.append(",\"cantidadDisponible\":").append(cantidadDisponible);
        }
        if (cantidadReservada != null) {
            sb.append(",\"cantidadReservada\":").append(cantidadReservada);
        }
        sb.append("}");
        return sb.toString();
    }

    // ============================================
    // MÉTODOS OVERRIDE
    // ============================================

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

    // ============================================
    // MÉTODOS ESTÁTICOS DE FÁBRICA
    // ============================================

    /**
     * Crea un DTO desde una entidad.
     * 
     * @param producto Entidad de producto
     * @return DTO de producto
     */
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
        
        // Campos transient
        dto.setNombreCategoria(producto.getNombreCategoria());
        dto.setDescripcionCategoria(producto.getDescripcionCategoria());
        dto.setTieneStock(producto.getTieneStock());
        dto.setCantidadStock(producto.getCantidadStock());
        dto.setCantidadDisponible(producto.getCantidadDisponible());
        
        // Actualizar campos formateados
        dto.actualizarCamposFormateados();
        
        return dto;
    }

    /**
     * Crea un DTO desde una entidad con datos de categoría.
     * 
     * @param producto Entidad de producto
     * @param categoria Entidad de categoría
     * @return DTO de producto
     */
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

    /**
     * Crea un DTO para un nuevo producto.
     * 
     * @param nombreItem Nombre del producto
     * @param idCategoria ID de la categoría
     * @param tipo Tipo de producto
     * @param precioBase Precio base
     * @return DTO de producto
     */
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

    /**
     * Convierte el DTO a una entidad.
     * 
     * @return Entidad de producto
     */
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
}