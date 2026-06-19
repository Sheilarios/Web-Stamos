package com.stamos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO (Data Transfer Object) para la disponibilidad de inventario.
 * 
 * <p>Este DTO se utiliza para transferir información sobre la disponibilidad
 * de equipos y mobiliario en el inventario, incluyendo fechas de reserva,
 * estado y detalles del equipo.</p>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
public class DisponibilidadDTO {

    // ============================================
    // ATRIBUTOS PRINCIPALES
    // ============================================

    private Integer idInventario;
    private Integer idProducto;
    private String nombreProducto;
    private String codigoSerial;
    private String categoria;
    private String tipoProducto;
    private String estadoFisico;
    private String ubicacion;
    private BigDecimal valorCompra;

    // ============================================
    // CAMPOS DE DISPONIBILIDAD
    // ============================================

    private boolean disponible;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String disponibilidadMensaje;
    private List<ReservaDTO> reservasActivas = new ArrayList<>();

    // ============================================
    // CAMPOS ADICIONALES
    // ============================================

    private String imagenUrl;
    private String descripcion;
    private Integer cantidadDisponible;
    private Integer cantidadTotal;
    private BigDecimal precioBase;
    private String observaciones;

    // ============================================
    // CONSTRUCTORES
    // ============================================

    /**
     * Constructor por defecto.
     */
    public DisponibilidadDTO() {
        this.disponible = true;
        this.cantidadDisponible = 0;
        this.cantidadTotal = 0;
        this.reservasActivas = new ArrayList<>();
    }

    /**
     * Constructor con los campos principales.
     * 
     * @param idInventario ID del inventario
     * @param nombreProducto Nombre del producto
     * @param codigoSerial Código serial del equipo
     * @param disponible Estado de disponibilidad
     */
    public DisponibilidadDTO(Integer idInventario, String nombreProducto, 
                            String codigoSerial, boolean disponible) {
        this();
        this.idInventario = idInventario;
        this.nombreProducto = nombreProducto;
        this.codigoSerial = codigoSerial;
        this.disponible = disponible;
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

    // ============================================
    // MÉTODOS DE NEGOCIO
    // ============================================

    /**
     * Actualiza el mensaje de disponibilidad basado en el estado y fechas.
     */
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

    /**
     * Verifica si el equipo está disponible en un rango de fechas específico.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return true si está disponible, false en caso contrario
     */
    public boolean estaDisponibleEnFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (!disponible) {
            return false;
        }

        if (fechaInicio == null || fechaFin == null) {
            return true;
        }

        // Verificar solapamiento con reservas activas
        for (ReservaDTO reserva : reservasActivas) {
            if (reserva.getFechaInicio() != null && reserva.getFechaFin() != null) {
                // Verificar si hay solapamiento
                if (!(fechaFin.isBefore(reserva.getFechaInicio()) || 
                      fechaInicio.isAfter(reserva.getFechaFin()))) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Agrega una reserva activa al equipo.
     * 
     * @param reserva DTO de la reserva
     */
    public void agregarReserva(ReservaDTO reserva) {
        if (reserva != null && this.reservasActivas != null) {
            this.reservasActivas.add(reserva);
            actualizarDisponibilidad();
        }
    }

    /**
     * Elimina una reserva activa del equipo.
     * 
     * @param idReserva ID de la reserva
     */
    public void eliminarReserva(Integer idReserva) {
        if (idReserva != null && this.reservasActivas != null) {
            this.reservasActivas.removeIf(r -> r.getIdReserva().equals(idReserva));
            actualizarDisponibilidad();
        }
    }

    /**
     * Actualiza el estado de disponibilidad basado en las reservas activas.
     */
    private void actualizarDisponibilidad() {
        if (estadoFisico == null || !estadoFisico.equals("Disponible")) {
            this.disponible = false;
        } else if (reservasActivas != null && !reservasActivas.isEmpty()) {
            // Verificar si hay alguna reserva activa en el rango de fechas actual
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

    /**
     * Calcula el porcentaje de disponibilidad.
     * 
     * @return Porcentaje de disponibilidad
     */
    public int getPorcentajeDisponibilidad() {
        if (cantidadTotal == null || cantidadTotal == 0) {
            return 0;
        }
        if (cantidadDisponible == null) {
            cantidadDisponible = 0;
        }
        return (cantidadDisponible * 100) / cantidadTotal;
    }

    /**
     * Verifica si hay inventario disponible (cantidad > 0).
     * 
     * @return true si hay inventario, false en caso contrario
     */
    public boolean tieneInventario() {
        return cantidadDisponible != null && cantidadDisponible > 0;
    }

    /**
     * Verifica si el equipo está en buen estado.
     * 
     * @return true si está en buen estado, false en caso contrario
     */
    public boolean estaEnBuenEstado() {
        return estadoFisico != null && 
               (estadoFisico.equals("Disponible") || estadoFisico.equals("En uso"));
    }

    /**
     * Obtiene el estado físico en formato legible.
     * 
     * @return Estado físico legible
     */
    public String getEstadoFisicoLegible() {
        if (estadoFisico == null) return "Sin estado";
        
        switch (estadoFisico) {
            case "Disponible":
                return "Disponible";
            case "En uso":
                return "En uso";
            case "Mantenimiento":
                return "En mantenimiento";
            case "Dañado":
                return "Dañado";
            default:
                return estadoFisico;
        }
    }

    /**
     * Obtiene la clase CSS para el badge del estado físico.
     * 
     * @return Clase CSS
     */
    public String getEstadoFisicoBadgeClass() {
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
     * Obtiene el color para el estado de disponibilidad.
     * 
     * @return Color en hexadecimal
     */
    public String getDisponibilidadColor() {
        if (disponible) {
            return "#10B981"; // Verde
        }
        return "#EF4444"; // Rojo
    }

    /**
     * Obtiene el ícono para el estado de disponibilidad.
     * 
     * @return Ícono CSS
     */
    public String getDisponibilidadIcono() {
        if (disponible) {
            return "ti ti-check-circle";
        }
        return "ti ti-x-circle";
    }

    // ============================================
    // MÉTODOS DE FORMATO PARA LA VISTA
    // ============================================

    /**
     * Formatea el precio base como moneda.
     * 
     * @return Precio formateado
     */
    public String getPrecioBaseFormateado() {
        if (precioBase == null) {
            return "S/ 0.00";
        }
        return "S/ " + precioBase.setScale(2, java.math.RoundingMode.HALF_UP).toString();
    }

    /**
     * Formatea el valor de compra como moneda.
     * 
     * @return Valor formateado
     */
    public String getValorCompraFormateado() {
        if (valorCompra == null) {
            return "S/ 0.00";
        }
        return "S/ " + valorCompra.setScale(2, java.math.RoundingMode.HALF_UP).toString();
    }

    /**
     * Formatea la fecha de inicio.
     * 
     * @return Fecha formateada
     */
    public String getFechaInicioFormateada() {
        if (fechaInicio == null) {
            return "-";
        }
        return fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Formatea la fecha de fin.
     * 
     * @return Fecha formateada
     */
    public String getFechaFinFormateada() {
        if (fechaFin == null) {
            return "-";
        }
        return fechaFin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Formatea el rango de fechas completo.
     * 
     * @return Rango de fechas formateado
     */
    public String getRangoFechasFormateado() {
        if (fechaInicio == null && fechaFin == null) {
            return "Sin fechas";
        }
        if (fechaInicio == null) {
            return "Hasta " + getFechaFinFormateada();
        }
        if (fechaFin == null) {
            return "Desde " + getFechaInicioFormateada();
        }
        return getFechaInicioFormateada() + " - " + getFechaFinFormateada();
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
    // MÉTODOS OVERRIDE
    // ============================================

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

    // ============================================
    // CLASE INTERNA PARA RESERVA
    // ============================================

    /**
     * DTO para las reservas de inventario.
     */
    public static class ReservaDTO {
        
        private Integer idReserva;
        private Integer idInventario;
        private Integer idProyecto;
        private String nombreProyecto;
        private LocalDate fechaInicio;
        private LocalDate fechaFin;
        private String estadoReserva;
        private String notas;

        // Constructores
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

        // Getters y Setters
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

        // Métodos de formato
        public String getEstadoReservaLegible() {
            if (estadoReserva == null) return "Sin estado";
            
            switch (estadoReserva) {
                case "Activa":
                    return "Activa";
                case "Completada":
                    return "Completada";
                case "Cancelada":
                    return "Cancelada";
                default:
                    return estadoReserva;
            }
        }

        public String getEstadoReservaBadgeClass() {
            if (estadoReserva == null) return "status-pendiente";
            
            switch (estadoReserva) {
                case "Activa":
                    return "status-produccion";
                case "Completada":
                    return "status-validado";
                case "Cancelada":
                    return "status-rechazado";
                default:
                    return "status-pendiente";
            }
        }

        public String getFechaInicioFormateada() {
            if (fechaInicio == null) return "-";
            return fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }

        public String getFechaFinFormateada() {
            if (fechaFin == null) return "-";
            return fechaFin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }

        public String getRangoFechasFormateado() {
            return getFechaInicioFormateada() + " - " + getFechaFinFormateada();
        }

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

    // ============================================
    // MÉTODOS ESTÁTICOS DE FÁBRICA
    // ============================================

    /**
     * Crea un DTO de disponibilidad desde un equipo de inventario.
     * 
     * @param inventario Entidad de inventario
     * @return DTO de disponibilidad
     */
    public static DisponibilidadDTO fromInventario(com.stamos.model.Inventario inventario) {
        DisponibilidadDTO dto = new DisponibilidadDTO();
        dto.setIdInventario(inventario.getIdInventario());
        dto.setIdProducto(inventario.getIdProducto());
        dto.setCodigoSerial(inventario.getCodigoSerial());
        dto.setEstadoFisico(inventario.getEstadoFisico());
        dto.setUbicacion(inventario.getUbicacion());
        dto.setValorCompra(inventario.getValorCompra());
        dto.setObservaciones(inventario.getObservaciones());
        
        // Determinar disponibilidad basada en el estado físico
        dto.setDisponible("Disponible".equals(inventario.getEstadoFisico()));
        
        return dto;
    }

    /**
     * Crea un DTO de disponibilidad desde un producto y su inventario.
     * 
     * @param producto Entidad de producto
     * @param inventario Entidad de inventario
     * @return DTO de disponibilidad
     */
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

    /**
     * Crea un DTO de disponibilidad con cantidad desde una lista de inventario.
     * 
     * @param inventarios Lista de inventario
     * @param nombreProducto Nombre del producto
     * @param idProducto ID del producto
     * @return DTO de disponibilidad
     */
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
            
            // Tomar el primer inventario como referencia
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
}