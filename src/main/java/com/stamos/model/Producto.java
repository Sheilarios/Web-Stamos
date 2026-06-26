package com.stamos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "productoServicio",
       indexes = {
           @Index(name = "idx_producto_categoria", columnList = "id_categoria"),
           @Index(name = "idx_producto_nombre", columnList = "nombre_item"),
           @Index(name = "idx_producto_tipo", columnList = "tipo"),
           @Index(name = "idx_producto_activo", columnList = "activo")
       })
public class Producto {

    public static final String TIPO_ALQUILER = "Alquiler";
    public static final String TIPO_VENTA = "Venta";
    public static final String TIPO_SERVICIO = "Servicio";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @NotNull(message = "La categoría es obligatoria")
    @Column(name = "id_categoria", nullable = false)
    private Integer idCategoria;

    @NotBlank(message = "El nombre del ítem es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder los 150 caracteres")
    @Column(name = "nombre_item", nullable = false, length = 150)
    private String nombreItem;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @NotBlank(message = "El tipo de producto es obligatorio")
    @Column(name = "tipo", nullable = false)
    private String tipo;

    @NotNull(message = "El precio base es obligatorio")
    @PositiveOrZero(message = "El precio base debe ser mayor o igual a 0")
    @Column(name = "precio_base", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioBase;

    @Size(max = 255, message = "La URL de la imagen no puede exceder los 255 caracteres")
    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;

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
    private String nombreCategoria;

    @Transient
    private String descripcionCategoria;

    @Transient
    private String tipoLegible;

    @Transient
    private String colorTipo;

    @Transient
    private String iconoTipo;

    @Transient
    private Boolean tieneStock;

    @Transient
    private Integer cantidadStock;

    @Transient
    private Integer cantidadDisponible;

    public Producto() {
        this.activo = true;
        this.precioBase = BigDecimal.ZERO;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        this.tieneStock = false;
        this.cantidadStock = 0;
        this.cantidadDisponible = 0;
        actualizarCamposTransient();
    }

    public Producto(Integer idCategoria, String nombreItem, String tipo, BigDecimal precioBase) {
        this();
        this.idCategoria = idCategoria;
        this.nombreItem = nombreItem;
        this.tipo = tipo;
        this.precioBase = precioBase;
        actualizarCamposTransient();
    }

    public Producto(Integer idCategoria, String nombreItem, String descripcion, 
                   String tipo, BigDecimal precioBase, String imagenUrl) {
        this(idCategoria, nombreItem, tipo, precioBase);
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
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
        actualizarCamposTransient();
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase;
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

    private void actualizarCamposTransient() {
        if (tipo != null) {
            switch (tipo) {
                case TIPO_ALQUILER -> {
                    this.tipoLegible = "Alquiler";
                    this.colorTipo = "#3B82F6";
                    this.iconoTipo = "ti ti-calendar";
                }
                case TIPO_VENTA -> {
                    this.tipoLegible = "Venta";
                    this.colorTipo = "#10B981";
                    this.iconoTipo = "ti ti-shopping-cart";
                }
                case TIPO_SERVICIO -> {
                    this.tipoLegible = "Servicio";
                    this.colorTipo = "#8B5CF6";
                    this.iconoTipo = "ti ti-tools";
                }
                default -> {
                    this.tipoLegible = tipo;
                    this.colorTipo = "#6B7280";
                    this.iconoTipo = "ti ti-box";
                }
            }
        }
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

    public boolean esAlquiler() {
        return TIPO_ALQUILER.equals(this.tipo);
    }

    public boolean esVenta() {
        return TIPO_VENTA.equals(this.tipo);
    }

    public boolean esServicio() {
        return TIPO_SERVICIO.equals(this.tipo);
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

    public String getPrecioBaseFormateado() {
        if (precioBase == null) return "S/ 0.00";
        return "S/ " + precioBase.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public String getNombreConTipo() {
        return nombreItem + " (" + getTipoLegible() + ")";
    }

    public String getTipoBadgeClass() {
        if (tipo == null) return "badge-secondary";
        return switch (tipo) {
            case TIPO_ALQUILER -> "badge-alquiler";
            case TIPO_VENTA -> "badge-venta";
            case TIPO_SERVICIO -> "badge-servicio";
            default -> "badge-secondary";
        };
    }

    public String getTipoConIcono() {
        return "<i class='" + iconoTipo + "'></i> " + getTipoLegible();
    }

    public String getEstadoBadgeClass() {
        return isActivo() ? "badge-success" : "badge-danger";
    }

    public String getEstadoLegible() {
        return isActivo() ? "Activo" : "Inactivo";
    }

    public String getEstadoIcono() {
        return isActivo() ? "ti ti-check-circle" : "ti ti-x-circle";
    }

    public String getEstadoColor() {
        return isActivo() ? "#10B981" : "#EF4444";
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
        return fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getFechaActualizacionFormateada() {
        if (fechaActualizacion == null) return "-";
        return fechaActualizacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getNombreUpper() {
        return nombreItem != null ? nombreItem.toUpperCase() : "";
    }

    public boolean tieneStockDisponible() {
        return cantidadDisponible != null && cantidadDisponible > 0;
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

    public String getDisponibilidadTexto() {
        if (cantidadStock == null || cantidadStock == 0) {
            return "Sin stock";
        }
        if (cantidadDisponible == null || cantidadDisponible == 0) {
            return "Agotado";
        }
        return cantidadDisponible + " disponibles";
    }

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
        sb.append("}");
        return sb.toString();
    }

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
        sb.append("\"icono\":\"").append(iconoTipo != null ? iconoTipo : "ti ti-box").append("\"");
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
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Producto{" +
                "idProducto=" + idProducto +
                ", idCategoria=" + idCategoria +
                ", nombreItem='" + nombreItem + '\'' +
                ", tipo='" + tipo + '\'' +
                ", precioBase=" + precioBase +
                ", activo=" + activo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Producto producto = (Producto) o;
        return idProducto != null && idProducto.equals(producto.idProducto);
    }

    @Override
    public int hashCode() {
        return idProducto != null ? idProducto.hashCode() : 0;
    }

    public static Producto crearAlquiler(Integer idCategoria, String nombreItem, BigDecimal precioBase) {
        return new Producto(idCategoria, nombreItem, TIPO_ALQUILER, precioBase);
    }

    public static Producto crearVenta(Integer idCategoria, String nombreItem, BigDecimal precioBase) {
        return new Producto(idCategoria, nombreItem, TIPO_VENTA, precioBase);
    }

    public static Producto crearServicio(Integer idCategoria, String nombreItem, BigDecimal precioBase) {
        return new Producto(idCategoria, nombreItem, TIPO_SERVICIO, precioBase);
    }

    public static Producto fromDTO(com.stamos.dto.ProductoDTO dto) {
        if (dto == null) {
            return null;
        }
        Producto producto = new Producto();
        if (dto.getIdProducto() != null) {
            producto.setIdProducto(dto.getIdProducto());
        }
        producto.setIdCategoria(dto.getIdCategoria());
        producto.setNombreItem(dto.getNombreItem());
        producto.setDescripcion(dto.getDescripcion());
        producto.setTipo(dto.getTipo());
        producto.setPrecioBase(dto.getPrecioBase());
        producto.setImagenUrl(dto.getImagenUrl());
        if (dto.getActivo() != null) {
            producto.setActivo(dto.getActivo());
        }
        return producto;
    }

    public com.stamos.dto.ProductoDTO toDTO() {
        com.stamos.dto.ProductoDTO dto = new com.stamos.dto.ProductoDTO();
        dto.setIdProducto(this.idProducto);
        dto.setIdCategoria(this.idCategoria);
        dto.setNombreItem(this.nombreItem);
        dto.setDescripcion(this.descripcion);
        dto.setTipo(this.tipo);
        dto.setPrecioBase(this.precioBase);
        dto.setImagenUrl(this.imagenUrl);
        dto.setActivo(this.activo);
        dto.setNombreCategoria(this.nombreCategoria);
        dto.setTipoLegible(this.tipoLegible);
        dto.setColorTipo(this.colorTipo);
        dto.setIconoTipo(this.iconoTipo);
        dto.setTieneStock(this.tieneStock);
        dto.setCantidadStock(this.cantidadStock);
        dto.setCantidadDisponible(this.cantidadDisponible);
        return dto;
    }
}