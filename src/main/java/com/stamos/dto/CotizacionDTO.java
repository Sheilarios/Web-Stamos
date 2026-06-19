package com.stamos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO (Data Transfer Object) para la entidad Cotizacion.
 * 
 * <p>Este DTO se utiliza para transferir datos de cotizaciones entre la capa de
 * servicio y la capa de presentación, evitando exponer directamente la entidad
 * y permitiendo una mejor organización de los datos.</p>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
public class CotizacionDTO {

    // ============================================
    // ATRIBUTOS PRINCIPALES
    // ============================================

    private Integer idCotizacion;
    private Integer idCliente;
    private String nombreCliente;
    private String rucCliente;
    private String emailCliente;
    private String telefonoCliente;
    private Integer idUsuarioVendedor;
    private String nombreVendedor;
    private String emailVendedor;
    private LocalDateTime fechaEmision;
    private LocalDate fechaValidez;
    private String estado;
    private Integer versionActual;
    private String observaciones;
    private LocalDateTime fechaActualizacion;

    // ============================================
    // CAMPOS CALCULADOS
    // ============================================

    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private BigDecimal tasaIgv;

    // ============================================
    // DETALLES DE LA COTIZACIÓN
    // ============================================

    private List<CotizacionDetalleDTO> detalles = new ArrayList<>();

    // ============================================
    // CONSTRUCTORES
    // ============================================

    public CotizacionDTO() {
        this.subtotal = BigDecimal.ZERO;
        this.igv = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
        this.tasaIgv = new BigDecimal("18.00");
        this.estado = "Pendiente";
        this.versionActual = 1;
        this.detalles = new ArrayList<>();
    }

    public CotizacionDTO(Integer idCotizacion, Integer idCliente, String nombreCliente, 
                         LocalDateTime fechaEmision, String estado, BigDecimal total) {
        this.idCotizacion = idCotizacion;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.fechaEmision = fechaEmision;
        this.estado = estado;
        this.total = total;
        this.subtotal = BigDecimal.ZERO;
        this.igv = BigDecimal.ZERO;
        this.tasaIgv = new BigDecimal("18.00");
        this.versionActual = 1;
        this.detalles = new ArrayList<>();
    }

    // ============================================
    // GETTERS Y SETTERS
    // ============================================

    public Integer getIdCotizacion() {
        return idCotizacion;
    }

    public void setIdCotizacion(Integer idCotizacion) {
        this.idCotizacion = idCotizacion;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getRucCliente() {
        return rucCliente;
    }

    public void setRucCliente(String rucCliente) {
        this.rucCliente = rucCliente;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public void setEmailCliente(String emailCliente) {
        this.emailCliente = emailCliente;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }

    public Integer getIdUsuarioVendedor() {
        return idUsuarioVendedor;
    }

    public void setIdUsuarioVendedor(Integer idUsuarioVendedor) {
        this.idUsuarioVendedor = idUsuarioVendedor;
    }

    public String getNombreVendedor() {
        return nombreVendedor;
    }

    public void setNombreVendedor(String nombreVendedor) {
        this.nombreVendedor = nombreVendedor;
    }

    public String getEmailVendedor() {
        return emailVendedor;
    }

    public void setEmailVendedor(String emailVendedor) {
        this.emailVendedor = emailVendedor;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public LocalDate getFechaValidez() {
        return fechaValidez;
    }

    public void setFechaValidez(LocalDate fechaValidez) {
        this.fechaValidez = fechaValidez;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getVersionActual() {
        return versionActual;
    }

    public void setVersionActual(Integer versionActual) {
        this.versionActual = versionActual;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getIgv() {
        return igv;
    }

    public void setIgv(BigDecimal igv) {
        this.igv = igv;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getTasaIgv() {
        return tasaIgv;
    }

    public void setTasaIgv(BigDecimal tasaIgv) {
        this.tasaIgv = tasaIgv;
    }

    public List<CotizacionDetalleDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<CotizacionDetalleDTO> detalles) {
        this.detalles = detalles;
    }

    // ============================================
    // MÉTODOS DE NEGOCIO
    // ============================================

    /**
     * Agrega un detalle a la cotización y recalcula los totales.
     * 
     * @param detalle Detalle a agregar
     */
    public void agregarDetalle(CotizacionDetalleDTO detalle) {
        if (detalle == null) {
            return;
        }
        this.detalles.add(detalle);
        recalcularTotales();
    }

    /**
     * Elimina un detalle de la cotización y recalcula los totales.
     * 
     * @param index Índice del detalle a eliminar
     */
    public void eliminarDetalle(int index) {
        if (index >= 0 && index < this.detalles.size()) {
            this.detalles.remove(index);
            recalcularTotales();
        }
    }

    /**
     * Recalcula el subtotal, IGV y total basado en los detalles.
     */
    public void recalcularTotales() {
        this.subtotal = this.detalles.stream()
                .map(CotizacionDetalleDTO::getSubtotalLinea)
                .filter(sub -> sub != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (this.tasaIgv == null) {
            this.tasaIgv = new BigDecimal("18.00");
        }

        this.igv = this.subtotal.multiply(this.tasaIgv)
                .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);

        this.total = this.subtotal.add(this.igv);
    }

    /**
     * Verifica si la cotización está vencida.
     * 
     * @return true si está vencida, false en caso contrario
     */
    public boolean estaVencida() {
        if (fechaValidez == null) {
            return false;
        }
        return LocalDate.now().isAfter(fechaValidez);
    }

    /**
     * Verifica si la cotización está pendiente.
     * 
     * @return true si está pendiente, false en caso contrario
     */
    public boolean estaPendiente() {
        return "Pendiente".equals(this.estado);
    }

    /**
     * Verifica si la cotización está aprobada.
     * 
     * @return true si está aprobada, false en caso contrario
     */
    public boolean estaAprobada() {
        return "Aprobada".equals(this.estado);
    }

    /**
     * Verifica si la cotización está anulada.
     * 
     * @return true si está anulada, false en caso contrario
     */
    public boolean estaAnulada() {
        return "Anulada".equals(this.estado);
    }

    /**
     * Verifica si la cotización puede ser editada.
     * 
     * @return true si se puede editar, false en caso contrario
     */
    public boolean puedeEditar() {
        return estaPendiente();
    }

    /**
     * Verifica si la cotización puede ser aprobada.
     * 
     * @return true si se puede aprobar, false en caso contrario
     */
    public boolean puedeAprobar() {
        return estaPendiente() && !estaVencida();
    }

    /**
     * Verifica si la cotización puede ser anulada.
     * 
     * @return true si se puede anular, false en caso contrario
     */
    public boolean puedeAnular() {
        return estaPendiente();
    }

    /**
     * Obtiene el número de ítems en la cotización.
     * 
     * @return Número de ítems
     */
    public int getNumeroItems() {
        return this.detalles != null ? this.detalles.size() : 0;
    }

    /**
     * Obtiene el total de la cotización en formato de moneda.
     * 
     * @return Total formateado
     */
    public String getTotalFormateado() {
        if (total == null) {
            return "S/ 0.00";
        }
        return "S/ " + total.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * Obtiene el subtotal en formato de moneda.
     * 
     * @return Subtotal formateado
     */
    public String getSubtotalFormateado() {
        if (subtotal == null) {
            return "S/ 0.00";
        }
        return "S/ " + subtotal.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * Obtiene el IGV en formato de moneda.
     * 
     * @return IGV formateado
     */
    public String getIgvFormateado() {
        if (igv == null) {
            return "S/ 0.00";
        }
        return "S/ " + igv.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * Obtiene la clase CSS para el badge del estado.
     * 
     * @return Clase CSS
     */
    public String getEstadoBadgeClass() {
        if (estado == null) return "status-pendiente";
        
        switch (estado) {
            case "Pendiente":
                return "status-pendiente";
            case "Aprobada":
                return "status-validado";
            case "Anulada":
                return "status-rechazado";
            default:
                return "status-pendiente";
        }
    }

    /**
     * Obtiene el estado en formato legible.
     * 
     * @return Estado legible
     */
    public String getEstadoLegible() {
        if (estado == null) return "Sin estado";
        
        switch (estado) {
            case "Pendiente":
                return "Pendiente";
            case "Aprobada":
                return "Aprobada";
            case "Anulada":
                return "Anulada";
            default:
                return estado;
        }
    }

    /**
     * Obtiene el ícono para el estado.
     * 
     * @return Ícono CSS
     */
    public String getEstadoIcono() {
        if (estado == null) return "ti ti-circle";
        
        switch (estado) {
            case "Pendiente":
                return "ti ti-clock";
            case "Aprobada":
                return "ti ti-check-circle";
            case "Anulada":
                return "ti ti-x-circle";
            default:
                return "ti ti-circle";
        }
    }

    /**
     * Obtiene el color para el estado.
     * 
     * @return Color en hexadecimal
     */
    public String getEstadoColor() {
        if (estado == null) return "#6B7280";
        
        switch (estado) {
            case "Pendiente":
                return "#F59E0B"; // Amarillo
            case "Aprobada":
                return "#10B981"; // Verde
            case "Anulada":
                return "#EF4444"; // Rojo
            default:
                return "#6B7280";
        }
    }

    /**
     * Obtiene el código de la cotización en formato legible.
     * 
     * @return Código de cotización
     */
    public String getCodigoCotizacion() {
        if (idCotizacion == null) {
            return "COT-00000";
        }
        return "COT-" + String.format("%05d", idCotizacion);
    }

    /**
     * Verifica si la cotización tiene detalles.
     * 
     * @return true si tiene detalles, false en caso contrario
     */
    public boolean tieneDetalles() {
        return detalles != null && !detalles.isEmpty();
    }

    /**
     * Verifica si la cotización tiene observaciones.
     * 
     * @return true si tiene observaciones, false en caso contrario
     */
    public boolean tieneObservaciones() {
        return observaciones != null && !observaciones.isEmpty();
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
        sb.append("\"idCotizacion\":").append(idCotizacion).append(",");
        sb.append("\"codigo\":\"").append(getCodigoCotizacion()).append("\",");
        sb.append("\"cliente\":\"").append(nombreCliente != null ? nombreCliente : "").append("\",");
        sb.append("\"estado\":\"").append(estado != null ? estado : "").append("\",");
        sb.append("\"total\":").append(total != null ? total : 0).append(",");
        sb.append("\"fechaEmision\":\"").append(fechaEmision != null ? fechaEmision.toString() : "").append("\"");
        sb.append("}");
        return sb.toString();
    }

    // ============================================
    // MÉTODOS OVERRIDE
    // ============================================

    @Override
    public String toString() {
        return "CotizacionDTO{" +
                "idCotizacion=" + idCotizacion +
                ", codigo='" + getCodigoCotizacion() + '\'' +
                ", idCliente=" + idCliente +
                ", nombreCliente='" + nombreCliente + '\'' +
                ", estado='" + estado + '\'' +
                ", subtotal=" + subtotal +
                ", igv=" + igv +
                ", total=" + total +
                ", numeroItems=" + getNumeroItems() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CotizacionDTO that = (CotizacionDTO) o;
        return idCotizacion != null && idCotizacion.equals(that.idCotizacion);
    }

    @Override
    public int hashCode() {
        return idCotizacion != null ? idCotizacion.hashCode() : 0;
    }

    // ============================================
    // CLASE INTERNA PARA DETALLE DE COTIZACIÓN
    // ============================================

    /**
     * DTO para los detalles de la cotización.
     */
    public static class CotizacionDetalleDTO {
        
        private Integer idDetalleVersion;
        private Integer idVersion;
        private Integer idProducto;
        private String nombreProducto;
        private String descripcionProducto;
        private String categoriaProducto;
        private String tipoProducto;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal descuento;
        private BigDecimal subtotalLinea;

        // Constructores
        public CotizacionDetalleDTO() {
            this.cantidad = 1;
            this.precioUnitario = BigDecimal.ZERO;
            this.descuento = BigDecimal.ZERO;
            this.subtotalLinea = BigDecimal.ZERO;
        }

        public CotizacionDetalleDTO(Integer idProducto, String nombreProducto, 
                                   Integer cantidad, BigDecimal precioUnitario) {
            this.idProducto = idProducto;
            this.nombreProducto = nombreProducto;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
            this.descuento = BigDecimal.ZERO;
            calcularSubtotal();
        }

        // Getters y Setters
        public Integer getIdDetalleVersion() { return idDetalleVersion; }
        public void setIdDetalleVersion(Integer idDetalleVersion) { this.idDetalleVersion = idDetalleVersion; }

        public Integer getIdVersion() { return idVersion; }
        public void setIdVersion(Integer idVersion) { this.idVersion = idVersion; }

        public Integer getIdProducto() { return idProducto; }
        public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }

        public String getNombreProducto() { return nombreProducto; }
        public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

        public String getDescripcionProducto() { return descripcionProducto; }
        public void setDescripcionProducto(String descripcionProducto) { this.descripcionProducto = descripcionProducto; }

        public String getCategoriaProducto() { return categoriaProducto; }
        public void setCategoriaProducto(String categoriaProducto) { this.categoriaProducto = categoriaProducto; }

        public String getTipoProducto() { return tipoProducto; }
        public void setTipoProducto(String tipoProducto) { this.tipoProducto = tipoProducto; }

        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { 
            this.cantidad = cantidad; 
            calcularSubtotal();
        }

        public BigDecimal getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(BigDecimal precioUnitario) { 
            this.precioUnitario = precioUnitario; 
            calcularSubtotal();
        }

        public BigDecimal getDescuento() { return descuento; }
        public void setDescuento(BigDecimal descuento) { 
            this.descuento = descuento; 
            calcularSubtotal();
        }

        public BigDecimal getSubtotalLinea() { return subtotalLinea; }
        public void setSubtotalLinea(BigDecimal subtotalLinea) { this.subtotalLinea = subtotalLinea; }

        // Métodos de negocio
        public void calcularSubtotal() {
            if (cantidad != null && precioUnitario != null) {
                BigDecimal subtotal = precioUnitario.multiply(new BigDecimal(cantidad));
                if (descuento != null) {
                    subtotal = subtotal.subtract(descuento);
                }
                if (subtotal.compareTo(BigDecimal.ZERO) < 0) {
                    subtotal = BigDecimal.ZERO;
                }
                this.subtotalLinea = subtotal;
            } else {
                this.subtotalLinea = BigDecimal.ZERO;
            }
        }

        public String getSubtotalFormateado() {
            if (subtotalLinea == null) return "S/ 0.00";
            return "S/ " + subtotalLinea.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        }

        public String getPrecioUnitarioFormateado() {
            if (precioUnitario == null) return "S/ 0.00";
            return "S/ " + precioUnitario.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        }

        public String getDescuentoFormateado() {
            if (descuento == null) return "S/ 0.00";
            return "S/ " + descuento.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        }

        public boolean tieneDescuento() {
            return descuento != null && descuento.compareTo(BigDecimal.ZERO) > 0;
        }

        @Override
        public String toString() {
            return "CotizacionDetalleDTO{" +
                    "idProducto=" + idProducto +
                    ", nombreProducto='" + nombreProducto + '\'' +
                    ", cantidad=" + cantidad +
                    ", precioUnitario=" + precioUnitario +
                    ", descuento=" + descuento +
                    ", subtotalLinea=" + subtotalLinea +
                    '}';
        }
    }
}