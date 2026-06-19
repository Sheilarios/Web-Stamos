package com.stamos.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * DTO (Data Transfer Object) para la entidad Inventario.
 * 
 * <p>Este DTO se utiliza para transferir datos de inventario entre la capa de
 * servicio y la capa de presentación, evitando exponer directamente la entidad
 * y permitiendo una mejor organización de los datos.</p>
 * 
 * <p>Incluye información del equipo, estado, ubicación, valor y datos
 * del producto asociado.</p>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
public class InventarioDTO {

    // ============================================
    // ATRIBUTOS PRINCIPALES
    // ============================================

    private Integer idInventario;
    private Integer idProducto;
    private String codigoSerial;
    private String ubicacion;
    private String estadoFisico;
    private LocalDate fechaAdquisicion;
    private BigDecimal valorCompra;
    private String observaciones;

    // ============================================
    // CAMPOS DE AUDITORÍA
    // ============================================

    private LocalDate fechaRegistro;
    private LocalDate fechaActualizacion;
    private Integer idUsuarioRegistro;
    private Integer idUsuarioActualizacion;
    private String nombreUsuarioRegistro;
    private String nombreUsuarioActualizacion;

    // ============================================
    // CAMPOS RELACIONADOS (DESDE PRODUCTO)
    // ============================================

    private String nombreProducto;
    private String descripcionProducto;
    private String categoriaProducto;
    private Integer idCategoria;
    private String tipoProducto;
    private BigDecimal precioBase;
    private String imagenUrl;

    // ============================================
    // CAMPOS DE ESTADO Y FORMATO
    // ============================================

    private Boolean estaDisponible;
    private String estadoFisicoLegible;
    private String colorEstado;
    private String iconoEstado;

    // ============================================
    // CAMPOS ADICIONALES
    // ============================================

    private Integer antiguedad;
    private String antiguedadLegible;
    private Boolean tieneReservasActivas;
    private Integer cantidadReservasActivas;

    // ============================================
    // CONSTRUCTORES
    // ============================================

    /**
     * Constructor por defecto.
     */
    public InventarioDTO() {
        this.estadoFisico = "Disponible";
        this.estaDisponible = true;
        this.colorEstado = "#10B981";
        this.iconoEstado = "ti ti-check-circle";
        this.estadoFisicoLegible = "Disponible";
        this.tieneReservasActivas = false;
        this.cantidadReservasActivas = 0;
    }

    /**
     * Constructor con campos principales.
     * 
     * @param idInventario ID del inventario
     * @param codigoSerial Código serial del equipo
     * @param estadoFisico Estado físico del equipo
     * @param ubicacion Ubicación del equipo
     */
    public InventarioDTO(Integer idInventario, String codigoSerial, String estadoFisico, String ubicacion) {
        this();
        this.idInventario = idInventario;
        this.codigoSerial = codigoSerial;
        this.estadoFisico = estadoFisico;
        this.ubicacion = ubicacion;
        actualizarCamposEstado();
    }

    /**
     * Constructor completo.
     * 
     * @param idInventario ID del inventario
     * @param idProducto ID del producto
     * @param codigoSerial Código serial
     * @param ubicacion Ubicación
     * @param estadoFisico Estado físico
     * @param valorCompra Valor de compra
     * @param nombreProducto Nombre del producto
     */
    public InventarioDTO(Integer idInventario, Integer idProducto, String codigoSerial,
                        String ubicacion, String estadoFisico, BigDecimal valorCompra,
                        String nombreProducto) {
        this();
        this.idInventario = idInventario;
        this.idProducto = idProducto;
        this.codigoSerial = codigoSerial;
        this.ubicacion = ubicacion;
        this.estadoFisico = estadoFisico;
        this.valorCompra = valorCompra;
        this.nombreProducto = nombreProducto;
        actualizarCamposEstado();
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
        actualizarCamposEstado();
    }

    public LocalDate getFechaAdquisicion() {
        return fechaAdquisicion;
    }

    public void setFechaAdquisicion(LocalDate fechaAdquisicion) {
        this.fechaAdquisicion = fechaAdquisicion;
        calcularAntiguedad();
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

    public String getNombreUsuarioRegistro() {
        return nombreUsuarioRegistro;
    }

    public void setNombreUsuarioRegistro(String nombreUsuarioRegistro) {
        this.nombreUsuarioRegistro = nombreUsuarioRegistro;
    }

    public String getNombreUsuarioActualizacion() {
        return nombreUsuarioActualizacion;
    }

    public void setNombreUsuarioActualizacion(String nombreUsuarioActualizacion) {
        this.nombreUsuarioActualizacion = nombreUsuarioActualizacion;
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

    public Integer getAntiguedad() {
        return antiguedad;
    }

    public void setAntiguedad(Integer antiguedad) {
        this.antiguedad = antiguedad;
    }

    public String getAntiguedadLegible() {
        return antiguedadLegible;
    }

    public void setAntiguedadLegible(String antiguedadLegible) {
        this.antiguedadLegible = antiguedadLegible;
    }

    public Boolean getTieneReservasActivas() {
        return tieneReservasActivas;
    }

    public void setTieneReservasActivas(Boolean tieneReservasActivas) {
        this.tieneReservasActivas = tieneReservasActivas;
    }

    public Integer getCantidadReservasActivas() {
        return cantidadReservasActivas;
    }

    public void setCantidadReservasActivas(Integer cantidadReservasActivas) {
        this.cantidadReservasActivas = cantidadReservasActivas;
    }

    // ============================================
    // MÉTODOS DE NEGOCIO
    // ============================================

    /**
     * Actualiza los campos de estado basados en el estado físico.
     */
    private void actualizarCamposEstado() {
        if (estadoFisico != null) {
            switch (estadoFisico) {
                case "Disponible":
                    this.estadoFisicoLegible = "Disponible";
                    this.colorEstado = "#10B981";
                    this.iconoEstado = "ti ti-check-circle";
                    this.estaDisponible = true;
                    break;
                case "En uso":
                    this.estadoFisicoLegible = "En uso";
                    this.colorEstado = "#3B82F6";
                    this.iconoEstado = "ti ti-clock";
                    this.estaDisponible = false;
                    break;
                case "Mantenimiento":
                    this.estadoFisicoLegible = "En mantenimiento";
                    this.colorEstado = "#F59E0B";
                    this.iconoEstado = "ti ti-tools";
                    this.estaDisponible = false;
                    break;
                case "Dañado":
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
     * Calcula la antigüedad del equipo.
     */
    private void calcularAntiguedad() {
        if (fechaAdquisicion == null) {
            this.antiguedad = null;
            this.antiguedadLegible = "-";
            return;
        }
        
        this.antiguedad = java.time.Period.between(fechaAdquisicion, LocalDate.now()).getYears();
        
        if (antiguedad == 0) {
            this.antiguedadLegible = "Menos de 1 año";
        } else if (antiguedad == 1) {
            this.antiguedadLegible = "1 año";
        } else {
            this.antiguedadLegible = antiguedad + " años";
        }
    }

    /**
     * Verifica si el equipo está disponible.
     * 
     * @return true si está disponible, false en caso contrario
     */
    public boolean isDisponible() {
        return estaDisponible != null && estaDisponible;
    }

    /**
     * Verifica si el equipo está en uso.
     * 
     * @return true si está en uso, false en caso contrario
     */
    public boolean isEnUso() {
        return "En uso".equals(this.estadoFisico);
    }

    /**
     * Verifica si el equipo está en mantenimiento.
     * 
     * @return true si está en mantenimiento, false en caso contrario
     */
    public boolean isEnMantenimiento() {
        return "Mantenimiento".equals(this.estadoFisico);
    }

    /**
     * Verifica si el equipo está dañado.
     * 
     * @return true si está dañado, false en caso contrario
     */
    public boolean isDanado() {
        return "Dañado".equals(this.estadoFisico);
    }

    /**
     * Verifica si el equipo está en buen estado.
     * 
     * @return true si está en buen estado, false en caso contrario
     */
    public boolean isEnBuenEstado() {
        return isDisponible() || isEnUso();
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
     * Verifica si el equipo tiene reservas activas.
     * 
     * @return true si tiene reservas activas, false en caso contrario
     */
    public boolean isTieneReservasActivas() {
        return tieneReservasActivas != null && tieneReservasActivas;
    }

    /**
     * Obtiene el nombre completo del equipo.
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

    // ============================================
    // MÉTODOS DE FORMATO PARA LA VISTA
    // ============================================

    /**
     * Obtiene el estado en formato legible.
     * 
     * @return Estado legible
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
            case "Disponible":
                return "status-validado";
            case "En uso":
                return "status-produccion";
            case "Mantenimiento":
                return "status-pendiente";
            case "Dañado":
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
     * Obtiene el estado con ícono.
     * 
     * @return Estado con ícono
     */
    public String getEstadoConIcono() {
        return "<i class='" + getIconoEstadoHtml() + "'></i> " + getEstadoLegible();
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
     * Obtiene la fecha de actualización formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaActualizacionFormateada() {
        if (fechaActualizacion == null) return "-";
        return fechaActualizacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Obtiene el valor de compra formateado.
     * 
     * @return Valor de compra formateado
     */
    public String getValorCompraFormateado() {
        if (valorCompra == null) return "S/ 0.00";
        return "S/ " + valorCompra.setScale(2, RoundingMode.HALF_UP).toString();
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
        sb.append(codigoSerial != null ? codigoSerial : "Sin serial");
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
     * Obtiene el nombre del producto para mostrar en la vista.
     * 
     * @return Nombre del producto
     */
    public String getProductoDisplay() {
        if (nombreProducto != null) {
            return nombreProducto;
        }
        return "Producto ID: " + idProducto;
    }

    /**
     * Obtiene la clase CSS para la tarjeta del equipo.
     * 
     * @return Clase CSS
     */
    public String getCardClass() {
        if (isDisponible()) {
            return "card-disponible";
        }
        if (isEnUso()) {
            return "card-en-uso";
        }
        if (isEnMantenimiento()) {
            return "card-mantenimiento";
        }
        if (isDanado()) {
            return "card-danado";
        }
        return "card-default";
    }

    /**
     * Obtiene el color para el estado de disponibilidad.
     * 
     * @return Color en hexadecimal
     */
    public String getDisponibilidadColor() {
        if (isDisponible()) {
            return "#10B981";
        }
        return "#EF4444";
    }

    /**
     * Obtiene el texto de disponibilidad.
     * 
     * @return Texto de disponibilidad
     */
    public String getDisponibilidadTexto() {
        return isDisponible() ? "Disponible" : "No disponible";
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
        sb.append("\"color\":\"").append(colorEstado != null ? colorEstado : "#6B7280").append("\",");
        sb.append("\"disponible\":").append(isDisponible());
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
        sb.append("\"disponible\":").append(isDisponible()).append(",");
        sb.append("\"antiguedad\":").append(antiguedad != null ? antiguedad : 0).append(",");
        sb.append("\"antiguedadLegible\":\"").append(antiguedadLegible != null ? antiguedadLegible : "-").append("\",");
        sb.append("\"tieneReservas\":").append(isTieneReservasActivas()).append(",");
        sb.append("\"cantidadReservas\":").append(cantidadReservasActivas != null ? cantidadReservasActivas : 0);
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
        return "InventarioDTO{" +
                "idInventario=" + idInventario +
                ", idProducto=" + idProducto +
                ", codigoSerial='" + codigoSerial + '\'' +
                ", ubicacion='" + ubicacion + '\'' +
                ", estadoFisico='" + estadoFisico + '\'' +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", valorCompra=" + valorCompra +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventarioDTO that = (InventarioDTO) o;
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
     * Crea un DTO desde una entidad.
     * 
     * @param inventario Entidad de inventario
     * @return DTO de inventario
     */
    public static InventarioDTO fromEntity(com.stamos.model.Inventario inventario) {
        if (inventario == null) {
            return null;
        }

        InventarioDTO dto = new InventarioDTO();
        dto.setIdInventario(inventario.getIdInventario());
        dto.setIdProducto(inventario.getIdProducto());
        dto.setCodigoSerial(inventario.getCodigoSerial());
        dto.setUbicacion(inventario.getUbicacion());
        dto.setEstadoFisico(inventario.getEstadoFisico());
        dto.setValorCompra(inventario.getValorCompra());
        dto.setObservaciones(inventario.getObservaciones());
        dto.setFechaAdquisicion(inventario.getFechaAdquisicion());
        dto.setFechaRegistro(inventario.getFechaRegistro());
        dto.setFechaActualizacion(inventario.getFechaActualizacion());
        dto.setIdUsuarioRegistro(inventario.getIdUsuarioRegistro());
        dto.setIdUsuarioActualizacion(inventario.getIdUsuarioActualizacion());
        
        // Campos transient
        dto.setNombreProducto(inventario.getNombreProducto());
        dto.setDescripcionProducto(inventario.getDescripcionProducto());
        dto.setCategoriaProducto(inventario.getCategoriaProducto());
        dto.setTipoProducto(inventario.getTipoProducto());
        dto.setPrecioBase(inventario.getPrecioBase());
        dto.setImagenUrl(inventario.getImagenUrl());
        dto.setEstaDisponible(inventario.getEstaDisponible());
        
        // Actualizar campos de estado
        dto.actualizarCamposEstado();
        dto.calcularAntiguedad();
        
        return dto;
    }

    /**
     * Crea un DTO desde una entidad con datos del producto.
     * 
     * @param inventario Entidad de inventario
     * @param producto Entidad de producto
     * @return DTO de inventario
     */
    public static InventarioDTO fromEntityWithProduct(com.stamos.model.Inventario inventario,
                                                      com.stamos.model.Producto producto) {
        InventarioDTO dto = fromEntity(inventario);
        if (dto != null && producto != null) {
            dto.setNombreProducto(producto.getNombreItem());
            dto.setDescripcionProducto(producto.getDescripcion());
            dto.setTipoProducto(producto.getTipo());
            dto.setPrecioBase(producto.getPrecioBase());
            dto.setImagenUrl(producto.getImagenUrl());
            dto.setIdCategoria(producto.getIdCategoria());
        }
        return dto;
    }

    /**
     * Convierte el DTO a una entidad.
     * 
     * @return Entidad de inventario
     */
    public com.stamos.model.Inventario toEntity() {
        com.stamos.model.Inventario inventario = new com.stamos.model.Inventario();
        if (this.idInventario != null) {
            inventario.setIdInventario(this.idInventario);
        }
        inventario.setIdProducto(this.idProducto);
        inventario.setCodigoSerial(this.codigoSerial);
        inventario.setUbicacion(this.ubicacion);
        inventario.setEstadoFisico(this.estadoFisico);
        inventario.setValorCompra(this.valorCompra);
        inventario.setObservaciones(this.observaciones);
        inventario.setFechaAdquisicion(this.fechaAdquisicion);
        inventario.setFechaRegistro(this.fechaRegistro);
        inventario.setFechaActualizacion(this.fechaActualizacion);
        inventario.setIdUsuarioRegistro(this.idUsuarioRegistro);
        inventario.setIdUsuarioActualizacion(this.idUsuarioActualizacion);
        return inventario;
    }

    // ============================================
    // CLASE PARA CREAR UN NUEVO EQUIPO
    // ============================================

    /**
     * Crea un DTO para un nuevo equipo.
     * 
     * @param idProducto ID del producto
     * @param codigoSerial Código serial
     * @param ubicacion Ubicación
     * @param valorCompra Valor de compra
     * @return DTO de inventario
     */
    public static InventarioDTO crearNuevoEquipo(Integer idProducto, String codigoSerial,
                                                 String ubicacion, BigDecimal valorCompra) {
        InventarioDTO dto = new InventarioDTO();
        dto.setIdProducto(idProducto);
        dto.setCodigoSerial(codigoSerial);
        dto.setUbicacion(ubicacion);
        dto.setValorCompra(valorCompra);
        dto.setEstadoFisico("Disponible");
        dto.setFechaAdquisicion(LocalDate.now());
        dto.actualizarCamposEstado();
        dto.calcularAntiguedad();
        return dto;
    }
}