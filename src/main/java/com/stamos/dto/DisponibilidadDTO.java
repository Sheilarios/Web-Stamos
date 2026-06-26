package com.stamos.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DisponibilidadDTO {

    private Integer idInventario;
    private Integer idProducto;
    private String nombreProducto;
    private String codigoSerial;
    private String categoria;
    private String tipoProducto;
    private String estadoFisico;
    private String ubicacion;
    private BigDecimal valorCompra;
    private boolean disponible;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String disponibilidadMensaje;
    private List<ReservaDTO> reservasActivas = new ArrayList<>();
    private String imagenUrl;
    private String descripcion;
    private Integer cantidadDisponible;
    private Integer cantidadTotal;
    private BigDecimal precioBase;
    private String observaciones;

    public DisponibilidadDTO() {
        this.disponible = true;
        this.cantidadDisponible = 0;
        this.cantidadTotal = 0;
        this.reservasActivas = new ArrayList<>();
    }

    public DisponibilidadDTO(Integer idInventario, String nombreProducto, 
                            String codigoSerial, boolean disponible) {
        this();
        this.idInventario = idInventario;
        this.nombreProducto = nombreProducto;
        this.codigoSerial = codigoSerial;
        this.disponible = disponible;
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

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getCodigoSerial() {
        return codigoSerial;
    }

    public void setCodigoSerial(String codigoSerial) {
        this.codigoSerial = codigoSerial;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(String tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public String getEstadoFisico() {
        return estadoFisico;
    }

    public void setEstadoFisico(String estadoFisico) {
        this.estadoFisico = estadoFisico;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public BigDecimal getValorCompra() {
        return valorCompra;
    }

    public void setValorCompra(BigDecimal valorCompra) {
        this.valorCompra = valorCompra;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
        actualizarMensajeDisponibilidad();
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
        actualizarMensajeDisponibilidad();
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
        actualizarMensajeDisponibilidad();
    }

    public String getDisponibilidadMensaje() {
        return disponibilidadMensaje;
    }

    public void setDisponibilidadMensaje(String disponibilidadMensaje) {
        this.disponibilidadMensaje = disponibilidadMensaje;
    }

    public List<ReservaDTO> getReservasActivas() {
        return reservasActivas;
    }

    public void setReservasActivas(List<ReservaDTO> reservasActivas) {
        this.reservasActivas = reservasActivas;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(Integer cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public Integer getCantidadTotal() {
        return cantidadTotal;
    }

    public void setCantidadTotal(Integer cantidadTotal) {
        this.cantidadTotal = cantidadTotal;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    private void actualizarMensajeDisponibilidad() {
        if (!disponible) {
            if (estadoFisico != null && !estadoFisico.equals("Disponible")) {
                this.disponibilidadMensaje = "Equipo en " + estadoFisico.toLowerCase();
            } else if (fechaInicio != null && fechaFin != null) {
                this.disponibilidadMensaje = "Reservado del " + 
                    fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
                    " al " + fechaFin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } else if (!reservasActivas.isEmpty()) {
                this.disponibilidadMensaje = "Ocupado en " + reservasActivas.size() + " reserva(s)";
            } else {
                this.disponibilidadMensaje = "No disponible";
            }
        } else {
            this.disponibilidadMensaje = "Disponible para reserva";
        }
    }

    public boolean estaDisponibleEnFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (!disponible) {
            return false;
        }

        if (fechaInicio == null || fechaFin == null) {
            return true;
        }

        for (ReservaDTO reserva : reservasActivas) {
            if (reserva.getFechaInicio() != null && reserva.getFechaFin() != null) {
                if (!(fechaFin.isBefore(reserva.getFechaInicio()) || 
                      fechaInicio.isAfter(reserva.getFechaFin()))) {
                    return false;
                }
            }
        }

        return true;
    }

    public void agregarReserva(ReservaDTO reserva) {
        if (reserva != null && this.reservasActivas != null) {
            this.reservasActivas.add(reserva);
            actualizarDisponibilidad();
        }
    }

    public void eliminarReserva(Integer idReserva) {
        if (idReserva != null && this.reservasActivas != null) {
            this.reservasActivas.removeIf(r -> r.getIdReserva().equals(idReserva));
            actualizarDisponibilidad();
        }
    }

    private void actualizarDisponibilidad() {
        if (estadoFisico == null || !estadoFisico.equals("Disponible")) {
            this.disponible = false;
        } else if (reservasActivas != null && !reservasActivas.isEmpty()) {
            LocalDate hoy = LocalDate.now();
            for (ReservaDTO reserva : reservasActivas) {
                if (reserva.getFechaInicio() != null && reserva.getFechaFin() != null) {
                    if (!(hoy.isBefore(reserva.getFechaInicio()) || 
                          hoy.isAfter(reserva.getFechaFin()))) {
                        this.disponible = false;
                        break;
                    }
                }
            }
        }
        actualizarMensajeDisponibilidad();
    }

    public static DisponibilidadDTO fromInventario(com.stamos.model.Inventario inventario) {
        DisponibilidadDTO dto = new DisponibilidadDTO();
        dto.setIdInventario(inventario.getIdInventario());
        dto.setIdProducto(inventario.getIdProducto());
        dto.setCodigoSerial(inventario.getCodigoSerial());
        dto.setEstadoFisico(inventario.getEstadoFisico());
        dto.setUbicacion(inventario.getUbicacion());
        dto.setValorCompra(inventario.getValorCompra());
        dto.setObservaciones(inventario.getObservaciones());
        dto.setDisponible("Disponible".equals(inventario.getEstadoFisico()));
        return dto;
    }

    public static DisponibilidadDTO fromProductoAndInventario(com.stamos.model.Producto producto, 
                                                               com.stamos.model.Inventario inventario) {
        DisponibilidadDTO dto = fromInventario(inventario);
        if (producto != null) {
            dto.setNombreProducto(producto.getNombreItem());
            dto.setDescripcion(producto.getDescripcion());
            dto.setTipoProducto(producto.getTipo());
            dto.setPrecioBase(producto.getPrecioBase());
            dto.setImagenUrl(producto.getImagenUrl());
        }
        return dto;
    }

    public static DisponibilidadDTO fromInventarioList(List<com.stamos.model.Inventario> inventarios,
                                                       String nombreProducto, Integer idProducto) {
        DisponibilidadDTO dto = new DisponibilidadDTO();
        dto.setIdProducto(idProducto);
        dto.setNombreProducto(nombreProducto);
        dto.setCantidadTotal(inventarios != null ? inventarios.size() : 0);
        
        if (inventarios != null) {
            long disponibles = inventarios.stream()
                    .filter(i -> "Disponible".equals(i.getEstadoFisico()))
                    .count();
            dto.setCantidadDisponible((int) disponibles);
            dto.setDisponible(disponibles > 0);
            
            if (!inventarios.isEmpty()) {
                com.stamos.model.Inventario primer = inventarios.get(0);
                dto.setIdInventario(primer.getIdInventario());
                dto.setCodigoSerial(primer.getCodigoSerial());
                dto.setEstadoFisico(primer.getEstadoFisico());
                dto.setUbicacion(primer.getUbicacion());
                dto.setValorCompra(primer.getValorCompra());
                dto.setObservaciones(primer.getObservaciones());
            }
        }
        
        return dto;
    }

    @Override
    public String toString() {
        return "DisponibilidadDTO{" +
                "idInventario=" + idInventario +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", codigoSerial='" + codigoSerial + '\'' +
                ", disponible=" + disponible +
                ", estadoFisico='" + estadoFisico + '\'' +
                ", cantidadDisponible=" + cantidadDisponible +
                ", cantidadTotal=" + cantidadTotal +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DisponibilidadDTO that = (DisponibilidadDTO) o;
        return idInventario != null && idInventario.equals(that.idInventario);
    }

    @Override
    public int hashCode() {
        return idInventario != null ? idInventario.hashCode() : 0;
    }

    public static class ReservaDTO {
        
        private Integer idReserva;
        private Integer idInventario;
        private Integer idProyecto;
        private String nombreProyecto;
        private LocalDate fechaInicio;
        private LocalDate fechaFin;
        private String estadoReserva;
        private String notas;

        public ReservaDTO() {}

        public ReservaDTO(Integer idReserva, Integer idProyecto, String nombreProyecto,
                         LocalDate fechaInicio, LocalDate fechaFin, String estadoReserva) {
            this.idReserva = idReserva;
            this.idProyecto = idProyecto;
            this.nombreProyecto = nombreProyecto;
            this.fechaInicio = fechaInicio;
            this.fechaFin = fechaFin;
            this.estadoReserva = estadoReserva;
        }

        public Integer getIdReserva() { return idReserva; }
        public void setIdReserva(Integer idReserva) { this.idReserva = idReserva; }

        public Integer getIdInventario() { return idInventario; }
        public void setIdInventario(Integer idInventario) { this.idInventario = idInventario; }

        public Integer getIdProyecto() { return idProyecto; }
        public void setIdProyecto(Integer idProyecto) { this.idProyecto = idProyecto; }

        public String getNombreProyecto() { return nombreProyecto; }
        public void setNombreProyecto(String nombreProyecto) { this.nombreProyecto = nombreProyecto; }

        public LocalDate getFechaInicio() { return fechaInicio; }
        public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

        public LocalDate getFechaFin() { return fechaFin; }
        public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

        public String getEstadoReserva() { return estadoReserva; }
        public void setEstadoReserva(String estadoReserva) { this.estadoReserva = estadoReserva; }

        public String getNotas() { return notas; }
        public void setNotas(String notas) { this.notas = notas; }

        @Override
        public String toString() {
            return "ReservaDTO{" +
                    "idReserva=" + idReserva +
                    ", nombreProyecto='" + nombreProyecto + '\'' +
                    ", fechaInicio=" + fechaInicio +
                    ", fechaFin=" + fechaFin +
                    ", estadoReserva='" + estadoReserva + '\'' +
                    '}';
        }
    }
}