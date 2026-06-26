package com.stamos.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CotizacionDTO {

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
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private BigDecimal tasaIgv;
    private List<CotizacionDetalleDTO> detalles = new ArrayList<>();

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
        this();
        this.idCotizacion = idCotizacion;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.fechaEmision = fechaEmision;
        this.estado = estado;
        this.total = total;
    }

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

    public void agregarDetalle(CotizacionDetalleDTO detalle) {
        if (detalle == null) {
            return;
        }
        this.detalles.add(detalle);
        recalcularTotales();
    }

    public void eliminarDetalle(int index) {
        if (index >= 0 && index < this.detalles.size()) {
            this.detalles.remove(index);
            recalcularTotales();
        }
    }

    public void recalcularTotales() {
        this.subtotal = this.detalles.stream()
                .map(CotizacionDetalleDTO::getSubtotalLinea)
                .filter(sub -> sub != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (this.tasaIgv == null) {
            this.tasaIgv = new BigDecimal("18.00");
        }

        this.igv = this.subtotal.multiply(this.tasaIgv)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        this.total = this.subtotal.add(this.igv);
    }

    public boolean estaVencida() {
        if (fechaValidez == null) {
            return false;
        }
        return LocalDate.now().isAfter(fechaValidez);
    }

    public boolean estaPendiente() {
        return "Pendiente".equals(this.estado);
    }

    public boolean estaAprobada() {
        return "Aprobada".equals(this.estado);
    }

    public boolean estaAnulada() {
        return "Anulada".equals(this.estado);
    }

    public boolean puedeEditar() {
        return estaPendiente();
    }

    public boolean puedeAprobar() {
        return estaPendiente() && !estaVencida();
    }

    public boolean puedeAnular() {
        return estaPendiente();
    }

    public int getNumeroItems() {
        return this.detalles != null ? this.detalles.size() : 0;
    }

    public String getCodigoCotizacion() {
        if (idCotizacion == null) {
            return "COT-00000";
        }
        return "COT-" + String.format("%05d", idCotizacion);
    }

    public boolean tieneDetalles() {
        return detalles != null && !detalles.isEmpty();
    }

    public boolean tieneObservaciones() {
        return observaciones != null && !observaciones.isEmpty();
    }

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

    public static final class CotizacionDetalleDTO {
        
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

        public void calcularSubtotal() {
            if (cantidad != null && precioUnitario != null) {
                BigDecimal subtotal = precioUnitario.multiply(new BigDecimal(cantidad));
                if (descuento != null) {
                    subtotal = subtotal.subtract(descuento);
                }
                if (subtotal.compareTo(BigDecimal.ZERO) < 0) {
                    subtotal = BigDecimal.ZERO;
                }
                this.subtotalLinea = subtotal.setScale(2, RoundingMode.HALF_UP);
            } else {
                this.subtotalLinea = BigDecimal.ZERO;
            }
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