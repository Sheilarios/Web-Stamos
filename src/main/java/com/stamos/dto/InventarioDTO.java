package com.stamos.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InventarioDTO {

    private Integer idInventario;
    private Integer idProducto;
    private String codigoSerial;
    private String ubicacion;
    private String estadoFisico;
    private LocalDate fechaAdquisicion;
    private BigDecimal valorCompra;
    private String observaciones;
    private LocalDate fechaRegistro;
    private LocalDate fechaActualizacion;
    private Integer idUsuarioRegistro;
    private Integer idUsuarioActualizacion;
    private String nombreUsuarioRegistro;
    private String nombreUsuarioActualizacion;
    private String nombreProducto;
    private String descripcionProducto;
    private String categoriaProducto;
    private Integer idCategoria;
    private String tipoProducto;
    private BigDecimal precioBase;
    private String imagenUrl;
    private Boolean estaDisponible;
    private String estadoFisicoLegible;
    private String colorEstado;
    private String iconoEstado;
    private Integer antiguedad;
    private String antiguedadLegible;
    private Boolean tieneReservasActivas;
    private Integer cantidadReservasActivas;

    public InventarioDTO() {
        this.estadoFisico = "Disponible";
        this.estaDisponible = true;
        this.colorEstado = "#10B981";
        this.iconoEstado = "ti ti-check-circle";
        this.estadoFisicoLegible = "Disponible";
        this.tieneReservasActivas = false;
        this.cantidadReservasActivas = 0;
    }

    public InventarioDTO(Integer idInventario, String codigoSerial, String estadoFisico, String ubicacion) {
        this();
        this.idInventario = idInventario;
        this.codigoSerial = codigoSerial;
        this.estadoFisico = estadoFisico;
        this.ubicacion = ubicacion;
        actualizarCamposEstado();
    }

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

    private void actualizarCamposEstado() {
        if (estadoFisico != null) {
            switch (estadoFisico) {
                case "Disponible" -> {
                    this.estadoFisicoLegible = "Disponible";
                    this.colorEstado = "#10B981";
                    this.iconoEstado = "ti ti-check-circle";
                    this.estaDisponible = true;
                }
                case "En uso" -> {
                    this.estadoFisicoLegible = "En uso";
                    this.colorEstado = "#3B82F6";
                    this.iconoEstado = "ti ti-clock";
                    this.estaDisponible = false;
                }
                case "Mantenimiento" -> {
                    this.estadoFisicoLegible = "En mantenimiento";
                    this.colorEstado = "#F59E0B";
                    this.iconoEstado = "ti ti-tools";
                    this.estaDisponible = false;
                }
                case "Dañado" -> {
                    this.estadoFisicoLegible = "Dañado";
                    this.colorEstado = "#EF4444";
                    this.iconoEstado = "ti ti-alert-triangle";
                    this.estaDisponible = false;
                }
                default -> {
                    this.estadoFisicoLegible = estadoFisico;
                    this.colorEstado = "#6B7280";
                    this.iconoEstado = "ti ti-circle";
                    this.estaDisponible = false;
                }
            }
        }
    }

    private void calcularAntiguedad() {
        if (fechaAdquisicion == null) {
            this.antiguedad = null;
            this.antiguedadLegible = "-";
            return;
        }
        
        this.antiguedad = java.time.Period.between(fechaAdquisicion, LocalDate.now()).getYears();
        
        if (antiguedad == null) {
            this.antiguedadLegible = antiguedad + " años";
        } else if (antiguedad == 0) {
            this.antiguedadLegible = "Menos de 1 año";
        } else if (antiguedad == 1) {
            this.antiguedadLegible = "1 año";
        } else {
            this.antiguedadLegible = antiguedad + " años";
        }
    }

    public boolean isDisponible() {
        return estaDisponible != null && estaDisponible;
    }

    public boolean isEnUso() {
        return "En uso".equals(this.estadoFisico);
    }

    public boolean isEnMantenimiento() {
        return "Mantenimiento".equals(this.estadoFisico);
    }

    public boolean isDanado() {
        return "Dañado".equals(this.estadoFisico);
    }

    public boolean isEnBuenEstado() {
        return isDisponible() || isEnUso();
    }

    public boolean tieneValorCompra() {
        return valorCompra != null && valorCompra.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean tieneObservaciones() {
        return observaciones != null && !observaciones.trim().isEmpty();
    }

    public boolean isTieneReservasActivas() {
        return tieneReservasActivas != null && tieneReservasActivas;
    }

    public String getNombreCompleto() {
        if (nombreProducto != null && codigoSerial != null) {
            return nombreProducto + " (" + codigoSerial + ")";
        }
        if (nombreProducto != null) {
            return nombreProducto;
        }
        return codigoSerial != null ? codigoSerial : "Sin nombre";
    }

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
        dto.setNombreProducto(inventario.getNombreProducto());
        dto.setDescripcionProducto(inventario.getDescripcionProducto());
        dto.setCategoriaProducto(inventario.getCategoriaProducto());
        dto.setTipoProducto(inventario.getTipoProducto());
        dto.setPrecioBase(inventario.getPrecioBase());
        dto.setImagenUrl(inventario.getImagenUrl());
        dto.setEstaDisponible(inventario.getEstaDisponible());
        dto.actualizarCamposEstado();
        dto.calcularAntiguedad();
        return dto;
    }

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
}