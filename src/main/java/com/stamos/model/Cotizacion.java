package com.stamos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Cotizacion.
 * 
 * <p>Esta entidad representa las cotizaciones generadas para los clientes
 * de STAMOS S.L., incluyendo productos, servicios, precios y estados.</p>
 * 
 * <p>Implementa los requerimientos:</p>
 * <ul>
 *   <li>RN-04: Toda cotización debe incluir al menos un producto o servicio</li>
 *   <li>RN-05: Solo personal autorizado puede emitir, modificar o anular cotizaciones</li>
 *   <li>RN-06: Cotización aprobada no puede modificarse, se crea nueva versión</li>
 *   <li>RN-20: Una cotización aprobada genera exactamente un proyecto</li>
 *   <li>RF-05: Crear cotizaciones con al menos un ítem</li>
 *   <li>RF-08: Registrar versiones de cotizaciones modificadas</li>
 *   <li>RF-09: Bloquear modificación de cotizaciones aprobadas</li>
 *   <li>RF-10: Generar proyecto automáticamente al aprobar cotización</li>
 * </ul>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
@Entity
@Table(name = "Cotizacion",
       indexes = {
           @Index(name = "idx_cotizacion_cliente", columnList = "id_cliente"),
           @Index(name = "idx_cotizacion_vendedor", columnList = "id_usuario_vendedor"),
           @Index(name = "idx_cotizacion_estado", columnList = "estado"),
           @Index(name = "idx_cotizacion_fecha", columnList = "fecha_emision"),
           @Index(name = "idx_cotizacion_validez", columnList = "fecha_validez")
       })
public class Cotizacion {

    // ============================================
    // ATRIBUTOS PRINCIPALES
    // ============================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cotizacion")
    private Integer idCotizacion;

    @NotNull(message = "El cliente es obligatorio")
    @Column(name = "id_cliente", nullable = false)
    private Integer idCliente;

    @NotNull(message = "El vendedor es obligatorio")
    @Column(name = "id_usuario_vendedor", nullable = false)
    private Integer idUsuarioVendedor;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    @Column(name = "fecha_validez")
    private LocalDate fechaValidez;

    @Column(name = "estado", nullable = false)
    private String estado; // 'Pendiente', 'Aprobada', 'Anulada'

    @Column(name = "version_actual", nullable = false)
    private Integer versionActual = 1;

    @Size(max = 500, message = "Las observaciones no pueden exceder los 500 caracteres")
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    // ============================================
    // CAMPOS DE AUDITORÍA
    // ============================================

    @Column(name = "fecha_aprobacion")
    private LocalDateTime fechaAprobacion;

    @Column(name = "id_usuario_aprobacion")
    private Integer idUsuarioAprobacion;

    @Column(name = "fecha_anulacion")
    private LocalDateTime fechaAnulacion;

    @Column(name = "id_usuario_anulacion")
    private Integer idUsuarioAnulacion;

    // ============================================
    // CAMPOS ADICIONALES (NO EN BD)
    // ============================================

    @Transient
    private String nombreCliente;

    @Transient
    private String rucCliente;

    @Transient
    private String emailCliente;

    @Transient
    private String nombreVendedor;

    @Transient
    private String emailVendedor;

    @Transient
    private BigDecimal subtotal;

    @Transient
    private BigDecimal igv;

    @Transient
    private BigDecimal total;

    @Transient
    private BigDecimal tasaIgv;

    @Transient
    private List<CotizacionDetalle> detalles = new ArrayList<>();

    @Transient
    private Integer cantidadItems;

    @Transient
    private Integer cantidadVersiones;

    @Transient
    private Boolean tieneProyectoAsociado;

    @Transient
    private Integer idProyectoAsociado;

    // ============================================
    // CONSTRUCTORES
    // ============================================

    public Cotizacion() {
        this.estado = "Pendiente";
        this.versionActual = 1;
        this.fechaEmision = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        this.subtotal = BigDecimal.ZERO;
        this.igv = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
        this.tasaIgv = new BigDecimal("18.00");
        this.detalles = new ArrayList<>();
    }

    public Cotizacion(Integer idCliente, Integer idUsuarioVendedor) {
        this();
        this.idCliente = idCliente;
        this.idUsuarioVendedor = idUsuarioVendedor;
    }

    public Cotizacion(Integer idCliente, Integer idUsuarioVendedor, LocalDate fechaValidez) {
        this(idCliente, idUsuarioVendedor);
        this.fechaValidez = fechaValidez;
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

    public Integer getIdUsuarioVendedor() {
        return idUsuarioVendedor;
    }

    public void setIdUsuarioVendedor(Integer idUsuarioVendedor) {
        this.idUsuarioVendedor = idUsuarioVendedor;
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

    public LocalDateTime getFechaAprobacion() {
        return fechaAprobacion;
    }

    public void setFechaAprobacion(LocalDateTime fechaAprobacion) {
        this.fechaAprobacion = fechaAprobacion;
    }

    public Integer getIdUsuarioAprobacion() {
        return idUsuarioAprobacion;
    }

    public void setIdUsuarioAprobacion(Integer idUsuarioAprobacion) {
        this.idUsuarioAprobacion = idUsuarioAprobacion;
    }

    public LocalDateTime getFechaAnulacion() {
        return fechaAnulacion;
    }

    public void setFechaAnulacion(LocalDateTime fechaAnulacion) {
        this.fechaAnulacion = fechaAnulacion;
    }

    public Integer getIdUsuarioAnulacion() {
        return idUsuarioAnulacion;
    }

    public void setIdUsuarioAnulacion(Integer idUsuarioAnulacion) {
        this.idUsuarioAnulacion = idUsuarioAnulacion;
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

    public List<CotizacionDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<CotizacionDetalle> detalles) {
        this.detalles = detalles;
    }

    public Integer getCantidadItems() {
        return cantidadItems;
    }

    public void setCantidadItems(Integer cantidadItems) {
        this.cantidadItems = cantidadItems;
    }

    public Integer getCantidadVersiones() {
        return cantidadVersiones;
    }

    public void setCantidadVersiones(Integer cantidadVersiones) {
        this.cantidadVersiones = cantidadVersiones;
    }

    public Boolean getTieneProyectoAsociado() {
        return tieneProyectoAsociado;
    }

    public void setTieneProyectoAsociado(Boolean tieneProyectoAsociado) {
        this.tieneProyectoAsociado = tieneProyectoAsociado;
    }

    public Integer getIdProyectoAsociado() {
        return idProyectoAsociado;
    }

    public void setIdProyectoAsociado(Integer idProyectoAsociado) {
        this.idProyectoAsociado = idProyectoAsociado;
    }

    // ============================================
    // MÉTODOS DE NEGOCIO
    // ============================================

    /**
     * Verifica si la cotización está pendiente.
     * 
     * @return true si está pendiente, false en caso contrario
     */
    public boolean isPendiente() {
        return "Pendiente".equals(this.estado);
    }

    /**
     * Verifica si la cotización está aprobada.
     * 
     * @return true si está aprobada, false en caso contrario
     */
    public boolean isAprobada() {
        return "Aprobada".equals(this.estado);
    }

    /**
     * Verifica si la cotización está anulada.
     * 
     * @return true si está anulada, false en caso contrario
     */
    public boolean isAnulada() {
        return "Anulada".equals(this.estado);
    }

    /**
     * Verifica si la cotización está vencida.
     * 
     * @return true si está vencida, false en caso contrario
     */
    public boolean isVencida() {
        if (fechaValidez == null) {
            return false;
        }
        return LocalDate.now().isAfter(fechaValidez);
    }

    /**
     * Verifica si la cotización puede ser editada.
     * 
     * @return true si se puede editar, false en caso contrario
     */
    public boolean puedeEditar() {
        return isPendiente();
    }

    /**
     * Verifica si la cotización puede ser aprobada.
     * 
     * @return true si se puede aprobar, false en caso contrario
     */
    public boolean puedeAprobar() {
        return isPendiente() && !isVencida() && tieneDetalles();
    }

    /**
     * Verifica si la cotización puede ser anulada.
     * 
     * @return true si se puede anular, false en caso contrario
     */
    public boolean puedeAnular() {
        return isPendiente();
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
        return observaciones != null && !observaciones.trim().isEmpty();
    }

    /**
     * Aprueba la cotización.
     * 
     * @param idUsuario ID del usuario que aprueba
     */
    public void aprobar(Integer idUsuario) {
        this.estado = "Aprobada";
        this.fechaAprobacion = LocalDateTime.now();
        this.idUsuarioAprobacion = idUsuario;
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Anula la cotización.
     * 
     * @param idUsuario ID del usuario que anula
     */
    public void anular(Integer idUsuario) {
        this.estado = "Anulada";
        this.fechaAnulacion = LocalDateTime.now();
        this.idUsuarioAnulacion = idUsuario;
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Incrementa la versión de la cotización.
     */
    public void incrementarVersion() {
        this.versionActual++;
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Recalcula subtotal, IGV y total basado en los detalles.
     */
    public void recalcularTotales() {
        if (detalles == null || detalles.isEmpty()) {
            this.subtotal = BigDecimal.ZERO;
            this.igv = BigDecimal.ZERO;
            this.total = BigDecimal.ZERO;
            this.cantidadItems = 0;
            return;
        }

        this.subtotal = detalles.stream()
                .map(CotizacionDetalle::getSubtotalLinea)
                .filter(s -> s != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (this.tasaIgv == null) {
            this.tasaIgv = new BigDecimal("18.00");
        }

        this.igv = this.subtotal.multiply(this.tasaIgv)
                .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);

        this.total = this.subtotal.add(this.igv);
        this.cantidadItems = detalles.size();
    }

    /**
     * Agrega un detalle a la cotización y recalcula totales.
     * 
     * @param detalle Detalle a agregar
     */
    public void agregarDetalle(CotizacionDetalle detalle) {
        if (detalle != null) {
            this.detalles.add(detalle);
            recalcularTotales();
        }
    }

    /**
     * Elimina un detalle de la cotización y recalcula totales.
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
                return "#F59E0B";
            case "Aprobada":
                return "#10B981";
            case "Anulada":
                return "#EF4444";
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

    // ============================================
    // MÉTODOS DE FORMATO PARA LA VISTA
    // ============================================

    /**
     * Obtiene la fecha de emisión formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaEmisionFormateada() {
        if (fechaEmision == null) return "-";
        return fechaEmision.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    /**
     * Obtiene la fecha de emisión formateada (solo fecha).
     * 
     * @return Fecha formateada
     */
    public String getFechaEmisionSoloFecha() {
        if (fechaEmision == null) return "-";
        return fechaEmision.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Obtiene la fecha de validez formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaValidezFormateada() {
        if (fechaValidez == null) return "-";
        return fechaValidez.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
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
     * Obtiene la fecha de aprobación formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaAprobacionFormateada() {
        if (fechaAprobacion == null) return "-";
        return fechaAprobacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    /**
     * Obtiene la fecha de anulación formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaAnulacionFormateada() {
        if (fechaAnulacion == null) return "-";
        return fechaAnulacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    /**
     * Obtiene el subtotal formateado.
     * 
     * @return Subtotal formateado
     */
    public String getSubtotalFormateado() {
        if (subtotal == null) return "S/ 0.00";
        return "S/ " + subtotal.setScale(2, java.math.RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el IGV formateado.
     * 
     * @return IGV formateado
     */
    public String getIgvFormateado() {
        if (igv == null) return "S/ 0.00";
        return "S/ " + igv.setScale(2, java.math.RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el total formateado.
     * 
     * @return Total formateado
     */
    public String getTotalFormateado() {
        if (total == null) return "S/ 0.00";
        return "S/ " + total.setScale(2, java.math.RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el vendedor en formato legible.
     * 
     * @return Vendedor legible
     */
    public String getVendedorLegible() {
        if (nombreVendedor != null && !nombreVendedor.isEmpty()) {
            return nombreVendedor;
        }
        if (emailVendedor != null && !emailVendedor.isEmpty()) {
            return emailVendedor;
        }
        return "Vendedor ID: " + idUsuarioVendedor;
    }

    /**
     * Obtiene el cliente en formato legible.
     * 
     * @return Cliente legible
     */
    public String getClienteLegible() {
        if (nombreCliente != null && !nombreCliente.isEmpty()) {
            if (rucCliente != null && !rucCliente.isEmpty()) {
                return nombreCliente + " (" + rucCliente + ")";
            }
            return nombreCliente;
        }
        return "Cliente ID: " + idCliente;
    }

    /**
     * Obtiene el resumen de la cotización.
     * 
     * @return Resumen
     */
    public String getResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append(getCodigoCotizacion());
        sb.append(" - ").append(getClienteLegible());
        sb.append(" - ").append(getTotalFormateado());
        sb.append(" (").append(getEstadoLegible()).append(")");
        return sb.toString();
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
        sb.append("\"fechaEmision\":\"").append(getFechaEmisionFormateada()).append("\"");
        sb.append("}");
        return sb.toString();
    }

    // ============================================
    // MÉTODOS OVERRIDE
    // ============================================

    @Override
    public String toString() {
        return "Cotizacion{" +
                "idCotizacion=" + idCotizacion +
                ", idCliente=" + idCliente +
                ", idUsuarioVendedor=" + idUsuarioVendedor +
                ", fechaEmision=" + fechaEmision +
                ", fechaValidez=" + fechaValidez +
                ", estado='" + estado + '\'' +
                ", versionActual=" + versionActual +
                ", total=" + total +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cotizacion that = (Cotizacion) o;
        return idCotizacion != null && idCotizacion.equals(that.idCotizacion);
    }

    @Override
    public int hashCode() {
        return idCotizacion != null ? idCotizacion.hashCode() : 0;
    }
}