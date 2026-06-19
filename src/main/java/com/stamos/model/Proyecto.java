package com.stamos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "Proyecto")
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proyecto")
    private Integer idProyecto;

    @Column(name = "id_cotizacion", nullable = false)
    private Integer idCotizacion;

    @Column(name = "id_cliente", nullable = false)
    private Integer idCliente;

    @Column(name = "nombre_proyecto", nullable = false, length = 200)
    private String nombreProyecto;

    @Column(name = "id_estado_actual", nullable = false)
    private Integer idEstadoActual;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin_estimada")
    private LocalDate fechaFinEstimada;

    @Column(name = "fecha_entrega_real")
    private LocalDate fechaEntregaReal;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "monto_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoTotal = BigDecimal.ZERO;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "id_usuario_creacion")
    private Integer idUsuarioCreacion;

    @Column(name = "id_usuario_actualizacion")
    private Integer idUsuarioActualizacion;

    @Transient
    private String nombreEstado;

    @Transient
    private String colorEstado;

    @Transient
    private String nombreCliente;

    @Transient
    private String rucCliente;

    @Transient
    private String emailCliente;

    @Transient
    private String telefonoCliente;

    @Transient
    private BigDecimal totalPagado;

    @Transient
    private BigDecimal saldoPendiente;

    @Transient
    private Integer porcentajePagado;

    // Constructor por defecto
    public Proyecto() {
        this.montoTotal = BigDecimal.ZERO;
        this.fechaCreacion = LocalDateTime.now();
    }

    // Getters y Setters principales
    public Integer getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(Integer idProyecto) {
        this.idProyecto = idProyecto;
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

    public String getNombreProyecto() {
        return nombreProyecto;
    }

    public void setNombreProyecto(String nombreProyecto) {
        this.nombreProyecto = nombreProyecto;
    }

    public Integer getIdEstadoActual() {
        return idEstadoActual;
    }

    public void setIdEstadoActual(Integer idEstadoActual) {
        this.idEstadoActual = idEstadoActual;
        actualizarEstadoPorId(idEstadoActual);
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFinEstimada() {
        return fechaFinEstimada;
    }

    public void setFechaFinEstimada(LocalDate fechaFinEstimada) {
        this.fechaFinEstimada = fechaFinEstimada;
    }

    public LocalDate getFechaEntregaReal() {
        return fechaEntregaReal;
    }

    public void setFechaEntregaReal(LocalDate fechaEntregaReal) {
        this.fechaEntregaReal = fechaEntregaReal;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal != null ? montoTotal : BigDecimal.ZERO;
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

    // Getters y Setters Transient
    public String getNombreEstado() {
        return nombreEstado;
    }

    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }

    public String getColorEstado() {
        return colorEstado;
    }

    public void setColorEstado(String colorEstado) {
        this.colorEstado = colorEstado;
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

    public BigDecimal getTotalPagado() {
        return totalPagado;
    }

    public void setTotalPagado(BigDecimal totalPagado) {
        this.totalPagado = totalPagado;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(BigDecimal saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }

    public Integer getPorcentajePagado() {
        return porcentajePagado;
    }

    public void setPorcentajePagado(Integer porcentajePagado) {
        this.porcentajePagado = porcentajePagado;
    }

    // Métodos de negocio
    private void actualizarEstadoPorId(Integer idEstado) {
        if (idEstado == null) return;
        
        switch (idEstado) {
            case 1:
                this.nombreEstado = "Aprobado";
                this.colorEstado = "#1A56DB";
                break;
            case 2:
                this.nombreEstado = "En Diseño";
                this.colorEstado = "#F59E0B";
                break;
            case 3:
                this.nombreEstado = "En Producción";
                this.colorEstado = "#3B82F6";
                break;
            case 4:
                this.nombreEstado = "Instalación/Montaje";
                this.colorEstado = "#8B5CF6";
                break;
            case 5:
                this.nombreEstado = "Entregado";
                this.colorEstado = "#10B981";
                break;
            case 6:
                this.nombreEstado = "Anulado";
                this.colorEstado = "#EF4444";
                break;
            default:
                this.nombreEstado = "Desconocido";
                this.colorEstado = "#6B7280";
                break;
        }
    }

    public boolean isEntregado() {
        return idEstadoActual != null && idEstadoActual == 5;
    }

    public boolean isAnulado() {
        return idEstadoActual != null && idEstadoActual == 6;
    }

    public boolean isActivo() {
        return idEstadoActual != null && idEstadoActual != 5 && idEstadoActual != 6;
    }

    // Métodos de formato
    public String getCodigoProyecto() {
        if (idProyecto == null) return "PRO-00000";
        return "PRO-" + String.format("%05d", idProyecto);
    }

    public String getEstadoBadgeClass() {
        if (idEstadoActual == null) return "status-pendiente";
        
        switch (idEstadoActual) {
            case 1: return "status-aprobado";
            case 2: return "status-diseno";
            case 3: return "status-produccion";
            case 4: return "status-instalacion";
            case 5: return "status-entregado";
            case 6: return "status-anulado";
            default: return "status-pendiente";
        }
    }

    public String getEstadoConIcono() {
        if (nombreEstado == null) return "-";
        return nombreEstado;
    }

    public String getMontoTotalFormateado() {
        if (montoTotal == null) return "S/ 0.00";
        return "S/ " + montoTotal.setScale(2, java.math.RoundingMode.HALF_UP).toString();
    }

    public String getFechaInicioFormateada() {
        if (fechaInicio == null) return "-";
        return fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getFechaFinEstimadaFormateada() {
        if (fechaFinEstimada == null) return "-";
        return fechaFinEstimada.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getFechaEntregaRealFormateada() {
        if (fechaEntregaReal == null) return "-";
        return fechaEntregaReal.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getFechaVencimientoFormateada() {
        if (fechaVencimiento == null) return "-";
        return fechaVencimiento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append(getCodigoProyecto());
        sb.append(" - ").append(nombreProyecto != null ? nombreProyecto : "Sin nombre");
        if (nombreEstado != null) {
            sb.append(" (").append(nombreEstado).append(")");
        }
        return sb.toString();
    }

    public int getProgreso() {
        if (idEstadoActual == null) return 0;
        
        switch (idEstadoActual) {
            case 1: return 10;
            case 2: return 30;
            case 3: return 60;
            case 4: return 85;
            case 5: return 100;
            case 6: return 0;
            default: return 0;
        }
    }

    @Override
    public String toString() {
        return "Proyecto{" +
                "idProyecto=" + idProyecto +
                ", nombreProyecto='" + nombreProyecto + '\'' +
                ", idEstadoActual=" + idEstadoActual +
                ", montoTotal=" + montoTotal +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proyecto proyecto = (Proyecto) o;
        return idProyecto != null && idProyecto.equals(proyecto.idProyecto);
    }

    @Override
    public int hashCode() {
        return idProyecto != null ? idProyecto.hashCode() : 0;
    }

    public String getEstado() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setDiasVencidos(int i) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public String getObservaciones() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setDiasRestantes(int i) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setDiasTranscurridos(int i) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setEstaVencido(boolean after) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public boolean esEntregado() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setIconoEstado(String icono) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setEstado(String nombreEstado) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public boolean esAnulado() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public boolean puedeRecibirPagos() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}