package com.stamos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Entidad Inventario.
 * 
 * <p>Esta entidad representa los equipos físicos y mobiliario ferial
 * disponibles en el inventario de STAMOS S.L., incluyendo su estado,
 * ubicación y datos de adquisición.</p>
 * 
 * <p>Implementa los requerimientos:</p>
 * <ul>
 *   <li>RN-13: Todo equipo o mobiliario destinado al alquiler debe estar registrado</li>
 *   <li>RN-14: No se puede asignar un equipo si no hay disponibilidad</li>
 *   <li>RF-16: Registrar equipos y mobiliario indicando stock, ubicación y estado</li>
 *   <li>RF-17: Actualizar la disponibilidad de equipos según su uso o devolución</li>
 * </ul>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
@Entity
@Table(name = "Inventario",
       indexes = {
           @Index(name = "idx_inventario_producto", columnList = "id_producto"),
           @Index(name = "idx_inventario_serial", columnList = "codigo_serial", unique = true),
           @Index(name = "idx_inventario_estado", columnList = "estado_fisico"),
           @Index(name = "idx_inventario_ubicacion", columnList = "ubicacion")
       })
public class Inventario {

    // ============================================
    // CONSTANTES PARA ESTADOS FÍSICOS
    // ============================================

    public static final String ESTADO_DISPONIBLE = "Disponible";
    public static final String ESTADO_EN_USO = "En uso";
    public static final String ESTADO_MANTENIMIENTO = "Mantenimiento";
    public static final String ESTADO_DANADO = "Dañado";

    // ============================================
    // ATRIBUTOS PRINCIPALES
    // ============================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario")
    private Integer idInventario;

    @NotNull(message = "El producto es obligatorio")
    @Column(name = "id_producto", nullable = false)
    private Integer idProducto;

    @NotBlank(message = "El código serial es obligatorio")
    @Size(max = 100, message = "El código serial no puede exceder los 100 caracteres")
    @Column(name = "codigo_serial", nullable = false, unique = true, length = 100)
    private String codigoSerial;

    @Size(max = 150, message = "La ubicación no puede exceder los 150 caracteres")
    @Column(name = "ubicacion", length = 150)
    private String ubicacion;

    @Column(name = "estado_fisico", nullable = false)
    private String estadoFisico = ESTADO_DISPONIBLE;

    @Column(name = "fecha_adquisicion")
    private LocalDate fechaAdquisicion;

    @PositiveOrZero(message = "El valor de compra debe ser mayor o igual a 0")
    @Column(name = "valor_compra", precision = 12, scale = 2)
    private BigDecimal valorCompra;

    @Size(max = 500, message = "Las observaciones no pueden exceder los 500 caracteres")
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    // ============================================
    // CAMPOS DE AUDITORÍA
    // ============================================

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDate fechaRegistro;

    @Column(name = "fecha_actualizacion")
    private LocalDate fechaActualizacion;

    @Column(name = "id_usuario_registro")
    private Integer idUsuarioRegistro;

    @Column(name = "id_usuario_actualizacion")
    private Integer idUsuarioActualizacion;

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
    private BigDecimal precioBase;

    @Transient
    private String imagenUrl;

    @Transient
    private Boolean estaDisponible;

    @Transient
    private String estadoFisicoLegible;

    @Transient
    private String colorEstado;

    @Transient
    private String iconoEstado;

    // ============================================
    // CONSTRUCTORES
    // ============================================

    public Inventario() {
        this.estadoFisico = ESTADO_DISPONIBLE;
        this.fechaRegistro = LocalDate.now();
        this.fechaActualizacion = LocalDate.now();
        this.estaDisponible = true;
        this.colorEstado = "#10B981";
        this.iconoEstado = "ti ti-check-circle";
        this.estadoFisicoLegible = "Disponible";
    }

    public Inventario(Integer idProducto, String codigoSerial, String ubicacion) {
        this();
        this.idProducto = idProducto;
        this.codigoSerial = codigoSerial;
        this.ubicacion = ubicacion;
    }

    public Inventario(Integer idProducto, String codigoSerial, String ubicacion, 
                     String estadoFisico, LocalDate fechaAdquisicion, BigDecimal valorCompra) {
        this(idProducto, codigoSerial, ubicacion);
        this.estadoFisico = estadoFisico;
        this.fechaAdquisicion = fechaAdquisicion;
        this.valorCompra = valorCompra;
        actualizarCamposTransient();
    }

    // ============================================
    // GETTERS Y SETTERS
    // ============================================

    public Integer getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(Integer idInventario) {
        this.idInventario = idInventario;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigoSerial() {
        return codigoSerial;
    }

    public void setCodigoSerial(String codigoSerial) {
        this.codigoSerial = codigoSerial;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getEstadoFisico() {
        return estadoFisico;
    }

    public void setEstadoFisico(String estadoFisico) {
        this.estadoFisico = estadoFisico;
        actualizarCamposTransient();
    }

    public LocalDate getFechaAdquisicion() {
        return fechaAdquisicion;
    }

    public void setFechaAdquisicion(LocalDate fechaAdquisicion) {
        this.fechaAdquisicion = fechaAdquisicion;
    }

    public BigDecimal getValorCompra() {
        return valorCompra;
    }

    public void setValorCompra(BigDecimal valorCompra) {
        this.valorCompra = valorCompra;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDate getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDate fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Integer getIdUsuarioRegistro() {
        return idUsuarioRegistro;
    }

    public void setIdUsuarioRegistro(Integer idUsuarioRegistro) {
        this.idUsuarioRegistro = idUsuarioRegistro;
    }

    public Integer getIdUsuarioActualizacion() {
        return idUsuarioActualizacion;
    }

    public void setIdUsuarioActualizacion(Integer idUsuarioActualizacion) {
        this.idUsuarioActualizacion = idUsuarioActualizacion;
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

    public Boolean getEstaDisponible() {
        return estaDisponible;
    }

    public void setEstaDisponible(Boolean estaDisponible) {
        this.estaDisponible = estaDisponible;
    }

    public String getEstadoFisicoLegible() {
        return estadoFisicoLegible;
    }

    public void setEstadoFisicoLegible(String estadoFisicoLegible) {
        this.estadoFisicoLegible = estadoFisicoLegible;
    }

    public String getColorEstado() {
        return colorEstado;
    }

    public void setColorEstado(String colorEstado) {
        this.colorEstado = colorEstado;
    }

    public String getIconoEstado() {
        return iconoEstado;
    }

    public void setIconoEstado(String iconoEstado) {
        this.iconoEstado = iconoEstado;
    }

    // ============================================
    // MÉTODOS DE NEGOCIO
    // ============================================

    /**
     * Actualiza los campos transient basados en el estado físico.
     */
    private void actualizarCamposTransient() {
        if (estadoFisico != null) {
            switch (estadoFisico) {
                case ESTADO_DISPONIBLE:
                    this.estadoFisicoLegible = "Disponible";
                    this.colorEstado = "#10B981";
                    this.iconoEstado = "ti ti-check-circle";
                    this.estaDisponible = true;
                    break;
                case ESTADO_EN_USO:
                    this.estadoFisicoLegible = "En uso";
                    this.colorEstado = "#3B82F6";
                    this.iconoEstado = "ti ti-clock";
                    this.estaDisponible = false;
                    break;
                case ESTADO_MANTENIMIENTO:
                    this.estadoFisicoLegible = "En mantenimiento";
                    this.colorEstado = "#F59E0B";
                    this.iconoEstado = "ti ti-tools";
                    this.estaDisponible = false;
                    break;
                case ESTADO_DANADO:
                    this.estadoFisicoLegible = "Dañado";
                    this.colorEstado = "#EF4444";
                    this.iconoEstado = "ti ti-alert-triangle";
                    this.estaDisponible = false;
                    break;
                default:
                    this.estadoFisicoLegible = estadoFisico;
                    this.colorEstado = "#6B7280";
                    this.iconoEstado = "ti ti-circle";
                    this.estaDisponible = false;
                    break;
            }
        }
    }

    /**
     * Verifica si el equipo está disponible.
     * 
     * @return true si está disponible, false en caso contrario
     */
    public boolean isDisponible() {
        return ESTADO_DISPONIBLE.equals(this.estadoFisico);
    }

    /**
     * Verifica si el equipo está en uso.
     * 
     * @return true si está en uso, false en caso contrario
     */
    public boolean isEnUso() {
        return ESTADO_EN_USO.equals(this.estadoFisico);
    }

    /**
     * Verifica si el equipo está en mantenimiento.
     * 
     * @return true si está en mantenimiento, false en caso contrario
     */
    public boolean isEnMantenimiento() {
        return ESTADO_MANTENIMIENTO.equals(this.estadoFisico);
    }

    /**
     * Verifica si el equipo está dañado.
     * 
     * @return true si está dañado, false en caso contrario
     */
    public boolean isDanado() {
        return ESTADO_DANADO.equals(this.estadoFisico);
    }

    /**
     * Verifica si el equipo está en buen estado (Disponible o En uso).
     * 
     * @return true si está en buen estado, false en caso contrario
     */
    public boolean isEnBuenEstado() {
        return isDisponible() || isEnUso();
    }

    /**
     * Cambia el estado del equipo a Disponible.
     */
    public void marcarDisponible() {
        this.estadoFisico = ESTADO_DISPONIBLE;
        this.fechaActualizacion = LocalDate.now();
        actualizarCamposTransient();
    }

    /**
     * Cambia el estado del equipo a En uso.
     */
    public void marcarEnUso() {
        this.estadoFisico = ESTADO_EN_USO;
        this.fechaActualizacion = LocalDate.now();
        actualizarCamposTransient();
    }

    /**
     * Cambia el estado del equipo a Mantenimiento.
     */
    public void marcarMantenimiento() {
        this.estadoFisico = ESTADO_MANTENIMIENTO;
        this.fechaActualizacion = LocalDate.now();
        actualizarCamposTransient();
    }

    /**
     * Cambia el estado del equipo a Dañado.
     */
    public void marcarDanado() {
        this.estadoFisico = ESTADO_DANADO;
        this.fechaActualizacion = LocalDate.now();
        actualizarCamposTransient();
    }

    /**
     * Verifica si el equipo tiene código serial.
     * 
     * @return true si tiene código serial, false en caso contrario
     */
    public boolean tieneCodigoSerial() {
        return codigoSerial != null && !codigoSerial.trim().isEmpty();
    }

    /**
     * Verifica si el equipo tiene ubicación.
     * 
     * @return true si tiene ubicación, false en caso contrario
     */
    public boolean tieneUbicacion() {
        return ubicacion != null && !ubicacion.trim().isEmpty();
    }

    /**
     * Verifica si el equipo tiene valor de compra.
     * 
     * @return true si tiene valor de compra, false en caso contrario
     */
    public boolean tieneValorCompra() {
        return valorCompra != null && valorCompra.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Verifica si el equipo tiene observaciones.
     * 
     * @return true si tiene observaciones, false en caso contrario
     */
    public boolean tieneObservaciones() {
        return observaciones != null && !observaciones.trim().isEmpty();
    }

    /**
     * Obtiene la antigüedad del equipo en años.
     * 
     * @return Antigüedad en años, o null si no tiene fecha de adquisición
     */
    public Integer getAntiguedad() {
        if (fechaAdquisicion == null) {
            return null;
        }
        return java.time.Period.between(fechaAdquisicion, LocalDate.now()).getYears();
    }

    // ============================================
    // MÉTODOS DE FORMATO PARA LA VISTA
    // ============================================

    /**
     * Obtiene el estado físico en formato legible.
     * 
     * @return Estado físico legible
     */
    public String getEstadoLegible() {
        return estadoFisicoLegible != null ? estadoFisicoLegible : estadoFisico;
    }

    /**
     * Obtiene la clase CSS para el badge del estado físico.
     * 
     * @return Clase CSS
     */
    public String getEstadoBadgeClass() {
        if (estadoFisico == null) return "status-pendiente";
        
        switch (estadoFisico) {
            case ESTADO_DISPONIBLE:
                return "status-validado";
            case ESTADO_EN_USO:
                return "status-produccion";
            case ESTADO_MANTENIMIENTO:
                return "status-pendiente";
            case ESTADO_DANADO:
                return "status-rechazado";
            default:
                return "status-pendiente";
        }
    }

    /**
     * Obtiene el color del estado en hexadecimal.
     * 
     * @return Color en hexadecimal
     */
    public String getColorEstadoHex() {
        return colorEstado != null ? colorEstado : "#6B7280";
    }

    /**
     * Obtiene el ícono del estado.
     * 
     * @return Ícono CSS
     */
    public String getIconoEstadoHtml() {
        return iconoEstado != null ? iconoEstado : "ti ti-circle";
    }

    /**
     * Obtiene la fecha de adquisición formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaAdquisicionFormateada() {
        if (fechaAdquisicion == null) return "-";
        return fechaAdquisicion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Obtiene la fecha de registro formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaRegistroFormateada() {
        if (fechaRegistro == null) return "-";
        return fechaRegistro.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Obtiene el valor de compra formateado.
     * 
     * @return Valor de compra formateado
     */
    public String getValorCompraFormateado() {
        if (valorCompra == null) return "S/ 0.00";
        return "S/ " + valorCompra.setScale(2, java.math.RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el código serial truncado.
     * 
     * @param maxLength Longitud máxima
     * @return Código serial truncado
     */
    public String getCodigoSerialTruncado(int maxLength) {
        if (codigoSerial == null) return "";
        if (codigoSerial.length() <= maxLength) return codigoSerial;
        return codigoSerial.substring(0, maxLength) + "...";
    }

    /**
     * Obtiene el resumen del equipo.
     * 
     * @return Resumen
     */
    public String getResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append(codigoSerial);
        if (nombreProducto != null) {
            sb.append(" - ").append(nombreProducto);
        }
        sb.append(" (").append(getEstadoLegible()).append(")");
        if (ubicacion != null && !ubicacion.isEmpty()) {
            sb.append(" - ").append(ubicacion);
        }
        return sb.toString();
    }

    /**
     * Obtiene el nombre completo del producto con código serial.
     * 
     * @return Nombre completo
     */
    public String getNombreCompleto() {
        if (nombreProducto != null && codigoSerial != null) {
            return nombreProducto + " (" + codigoSerial + ")";
        }
        if (nombreProducto != null) {
            return nombreProducto;
        }
        return codigoSerial != null ? codigoSerial : "Sin nombre";
    }

    /**
     * Obtiene la antigüedad formateada.
     * 
     * @return Antigüedad formateada
     */
    public String getAntiguedadFormateada() {
        Integer antiguedad = getAntiguedad();
        if (antiguedad == null) return "-";
        if (antiguedad == 0) return "Menos de 1 año";
        if (antiguedad == 1) return "1 año";
        return antiguedad + " años";
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
        sb.append("\"idInventario\":").append(idInventario).append(",");
        sb.append("\"idProducto\":").append(idProducto).append(",");
        sb.append("\"codigoSerial\":\"").append(codigoSerial != null ? codigoSerial : "").append("\",");
        sb.append("\"ubicacion\":\"").append(ubicacion != null ? ubicacion : "").append("\",");
        sb.append("\"estadoFisico\":\"").append(estadoFisico != null ? estadoFisico : "").append("\",");
        sb.append("\"estadoLegible\":\"").append(getEstadoLegible()).append("\",");
        sb.append("\"color\":\"").append(colorEstado != null ? colorEstado : "#6B7280").append("\"");
        if (nombreProducto != null) {
            sb.append(",\"nombreProducto\":\"").append(nombreProducto).append("\"");
        }
        if (valorCompra != null) {
            sb.append(",\"valorCompra\":").append(valorCompra);
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
        sb.append("\"idInventario\":").append(idInventario).append(",");
        sb.append("\"idProducto\":").append(idProducto).append(",");
        sb.append("\"codigoSerial\":\"").append(codigoSerial != null ? codigoSerial : "").append("\",");
        sb.append("\"ubicacion\":\"").append(ubicacion != null ? ubicacion : "").append("\",");
        sb.append("\"estadoFisico\":\"").append(estadoFisico != null ? estadoFisico : "").append("\",");
        sb.append("\"estadoLegible\":\"").append(getEstadoLegible()).append("\",");
        sb.append("\"color\":\"").append(colorEstado != null ? colorEstado : "#6B7280").append("\",");
        sb.append("\"icono\":\"").append(iconoEstado != null ? iconoEstado : "ti ti-circle").append("\",");
        sb.append("\"fechaAdquisicion\":\"").append(getFechaAdquisicionFormateada()).append("\",");
        sb.append("\"valorCompra\":").append(valorCompra != null ? valorCompra : 0).append(",");
        sb.append("\"valorCompraFormateado\":\"").append(getValorCompraFormateado()).append("\",");
        sb.append("\"disponible\":").append(isDisponible());
        if (nombreProducto != null) {
            sb.append(",\"nombreProducto\":\"").append(nombreProducto).append("\"");
        }
        if (categoriaProducto != null) {
            sb.append(",\"categoria\":\"").append(categoriaProducto).append("\"");
        }
        if (tipoProducto != null) {
            sb.append(",\"tipo\":\"").append(tipoProducto).append("\"");
        }
        if (observaciones != null) {
            sb.append(",\"observaciones\":\"").append(observaciones).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

    // ============================================
    // MÉTODOS OVERRIDE
    // ============================================

    @Override
    public String toString() {
        return "Inventario{" +
                "idInventario=" + idInventario +
                ", idProducto=" + idProducto +
                ", codigoSerial='" + codigoSerial + '\'' +
                ", ubicacion='" + ubicacion + '\'' +
                ", estadoFisico='" + estadoFisico + '\'' +
                ", valorCompra=" + valorCompra +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inventario that = (Inventario) o;
        return idInventario != null && idInventario.equals(that.idInventario);
    }

    @Override
    public int hashCode() {
        return idInventario != null ? idInventario.hashCode() : 0;
    }

    // ============================================
    // MÉTODOS ESTÁTICOS DE FÁBRICA
    // ============================================

    /**
     * Crea un inventario desde un producto.
     * 
     * @param idProducto ID del producto
     * @param codigoSerial Código serial único
     * @param ubicacion Ubicación física
     * @return Inventario
     */
    public static Inventario fromProducto(Integer idProducto, String codigoSerial, String ubicacion) {
        return new Inventario(idProducto, codigoSerial, ubicacion);
    }

    /**
     * Crea un inventario desde un producto con valor de compra.
     * 
     * @param idProducto ID del producto
     * @param codigoSerial Código serial único
     * @param ubicacion Ubicación física
     * @param valorCompra Valor de compra
     * @return Inventario
     */
    public static Inventario fromProductoConValor(Integer idProducto, String codigoSerial, 
                                                  String ubicacion, BigDecimal valorCompra) {
        Inventario inventario = new Inventario(idProducto, codigoSerial, ubicacion);
        inventario.setValorCompra(valorCompra);
        inventario.setFechaAdquisicion(LocalDate.now());
        return inventario;
    }

    /**
     * Crea un inventario desde un DTO.
     * 
     * @param dto DTO de inventario
     * @return Inventario
     */
    public static Inventario fromDTO(com.stamos.dto.InventarioDTO dto) {
        if (dto == null) {
            return null;
        }
        Inventario inventario = new Inventario();
        if (dto.getIdInventario() != null) {
            inventario.setIdInventario(dto.getIdInventario());
        }
        inventario.setIdProducto(dto.getIdProducto());
        inventario.setCodigoSerial(dto.getCodigoSerial());
        inventario.setUbicacion(dto.getUbicacion());
        inventario.setEstadoFisico(dto.getEstadoFisico());
        inventario.setValorCompra(dto.getValorCompra());
        inventario.setObservaciones(dto.getObservaciones());
        return inventario;
    }

    /**
     * Convierte la entidad a un DTO.
     * 
     * @return DTO de inventario
     */
    public com.stamos.dto.InventarioDTO toDTO() {
        com.stamos.dto.InventarioDTO dto = new com.stamos.dto.InventarioDTO();
        dto.setIdInventario(this.idInventario);
        dto.setIdProducto(this.idProducto);
        dto.setCodigoSerial(this.codigoSerial);
        dto.setUbicacion(this.ubicacion);
        dto.setEstadoFisico(this.estadoFisico);
        dto.setValorCompra(this.valorCompra);
        dto.setObservaciones(this.observaciones);
        dto.setNombreProducto(this.nombreProducto);
        dto.setDescripcionProducto(this.descripcionProducto);
        dto.setCategoriaProducto(this.categoriaProducto);
        dto.setTipoProducto(this.tipoProducto);
        dto.setPrecioBase(this.precioBase);
        dto.setImagenUrl(this.imagenUrl);
        dto.setEstaDisponible(isDisponible());
        dto.setEstadoFisicoLegible(getEstadoLegible());
        dto.setColorEstado(this.colorEstado);
        dto.setIconoEstado(this.iconoEstado);
        return dto;
    }
}