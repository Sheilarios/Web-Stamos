package com.stamos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private String iconoEstado;

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

    @Transient
    private Integer diasTranscurridos;

    @Transient
    private Integer diasRestantes;

    @Transient
    private Boolean estaVencido;

    @Transient
    private String estado;

    @Transient
    private String observaciones;

    public Proyecto() {
        this.montoTotal = BigDecimal.ZERO;
        this.fechaCreacion = LocalDateTime.now();
        this.totalPagado = BigDecimal.ZERO;
        this.saldoPendiente = BigDecimal.ZERO;
        this.porcentajePagado = 0;
        this.diasTranscurridos = 0;
        this.diasRestantes = 0;
        this.estaVencido = false;
    }

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

    public String getNombreEstado() {
        return nombreEstado;
    }

    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
        this.estado = nombreEstado;
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
        this.totalPagado = totalPagado != null ? totalPagado : BigDecimal.ZERO;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(BigDecimal saldoPendiente) {
        this.saldoPendiente = saldoPendiente != null ? saldoPendiente : BigDecimal.ZERO;
    }

    public Integer getPorcentajePagado() {
        return porcentajePagado;
    }

    public void setPorcentajePagado(Integer porcentajePagado) {
        this.porcentajePagado = porcentajePagado != null ? porcentajePagado : 0;
    }

    public Integer getDiasTranscurridos() {
        return diasTranscurridos;
    }

    public void setDiasTranscurridos(Integer diasTranscurridos) {
        this.diasTranscurridos = diasTranscurridos != null ? diasTranscurridos : 0;
    }

    public Integer getDiasRestantes() {
        return diasRestantes;
    }

    public void setDiasRestantes(Integer diasRestantes) {
        this.diasRestantes = diasRestantes != null ? diasRestantes : 0;
    }

    public Boolean getEstaVencido() {
        return estaVencido;
    }

    public void setEstaVencido(Boolean estaVencido) {
        this.estaVencido = estaVencido != null ? estaVencido : false;
    }

    public String getEstado() {
        if (estado == null && nombreEstado != null) {
            estado = nombreEstado;
        }
        return estado != null ? estado : "Sin estado";
    }

    public void setEstado(String estado) {
        this.estado = estado;
        this.nombreEstado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    private void actualizarEstadoPorId(Integer idEstado) {
        if (idEstado == null) return;
        
        switch (idEstado) {
            case 1 -> {
                this.nombreEstado = "Aprobado";
                this.colorEstado = "#1A56DB";
                this.iconoEstado = "ti ti-check-circle";
                this.estado = "Aprobado";
            }
            case 2 -> {
                this.nombreEstado = "En Diseño";
                this.colorEstado = "#F59E0B";
                this.iconoEstado = "ti ti-pencil";
                this.estado = "En Diseño";
            }
            case 3 -> {
                this.nombreEstado = "En Producción";
                this.colorEstado = "#3B82F6";
                this.iconoEstado = "ti ti-tools";
                this.estado = "En Producción";
            }
            case 4 -> {
                this.nombreEstado = "Instalación/Montaje";
                this.colorEstado = "#8B5CF6";
                this.iconoEstado = "ti ti-truck";
                this.estado = "Instalación/Montaje";
            }
            case 5 -> {
                this.nombreEstado = "Entregado";
                this.colorEstado = "#10B981";
                this.iconoEstado = "ti ti-check";
                this.estado = "Entregado";
            }
            case 6 -> {
                this.nombreEstado = "Anulado";
                this.colorEstado = "#EF4444";
                this.iconoEstado = "ti ti-x-circle";
                this.estado = "Anulado";
            }
            default -> {
                this.nombreEstado = "Desconocido";
                this.colorEstado = "#6B7280";
                this.iconoEstado = "ti ti-circle";
                this.estado = "Desconocido";
            }
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

    public boolean esEntregado() {
        return isEntregado();
    }

    public boolean esAnulado() {
        return isAnulado();
    }

    public boolean puedeRecibirPagos() {
        if (idEstadoActual == null) return false;
        return idEstadoActual >= 1 && idEstadoActual <= 4;
    }

    public String getCodigoProyecto() {
        if (idProyecto == null) return "PRO-00000";
        return "PRO-" + String.format("%05d", idProyecto);
    }

    public String getEstadoBadgeClass() {
        if (idEstadoActual == null) return "status-pendiente";
        
        return switch (idEstadoActual) {
            case 1 -> "status-aprobado";
            case 2 -> "status-diseno";
            case 3 -> "status-produccion";
            case 4 -> "status-instalacion";
            case 5 -> "status-entregado";
            case 6 -> "status-anulado";
            default -> "status-pendiente";
        };
    }

    public String getEstadoConIcono() {
        if (nombreEstado == null) return "-";
        if (iconoEstado != null) {
            return "<i class='" + iconoEstado + "'></i> " + nombreEstado;
        }
        return nombreEstado;
    }

    public String getMontoTotalFormateado() {
        if (montoTotal == null) return "S/ 0.00";
        return "S/ " + montoTotal.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public String getTotalPagadoFormateado() {
        if (totalPagado == null) return "S/ 0.00";
        return "S/ " + totalPagado.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public String getSaldoPendienteFormateado() {
        if (saldoPendiente == null) return "S/ 0.00";
        return "S/ " + saldoPendiente.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public String getPorcentajePagadoFormateado() {
        if (porcentajePagado == null) return "0%";
        return porcentajePagado + "%";
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
        
        return switch (idEstadoActual) {
            case 1 -> 10;
            case 2 -> 30;
            case 3 -> 60;
            case 4 -> 85;
            case 5 -> 100;
            case 6 -> 0;
            default -> 0;
        };
    }

    public String getProgresoColor() {
        int progreso = getProgreso();
        if (progreso >= 100) return "#10B981";
        if (progreso >= 60) return "#3B82F6";
        if (progreso >= 30) return "#F59E0B";
        return "#EF4444";
    }

    public String getProgresoMensaje() {
        int progreso = getProgreso();
        if (progreso >= 100) return "Proyecto completado";
        if (progreso >= 60) return "En fase avanzada";
        if (progreso >= 30) return "En proceso";
        return "Recién iniciado";
    }

    public boolean isVencido() {
        if (estaVencido != null) {
            return estaVencido;
        }
        if (fechaVencimiento != null && !isEntregado() && !isAnulado()) {
            return LocalDate.now().isAfter(fechaVencimiento);
        }
        return false;
    }

    public String getEstadoColorHex() {
        return colorEstado != null ? colorEstado : "#6B7280";
    }

    public String getIconoEstadoHtml() {
        return iconoEstado != null ? iconoEstado : "ti ti-circle";
    }

    public String getSaldoColor() {
        if (saldoPendiente == null || saldoPendiente.compareTo(BigDecimal.ZERO) == 0) {
            return "#10B981";
        }
        if (saldoPendiente.compareTo(montoTotal) == 0) {
            return "#EF4444";
        }
        return "#F59E0B";
    }

    public String getSaldoMensaje() {
        if (saldoPendiente == null || saldoPendiente.compareTo(BigDecimal.ZERO) == 0) {
            return "Completamente pagado";
        }
        if (saldoPendiente.compareTo(montoTotal) == 0) {
            return "Sin pagos registrados";
        }
        return "Pago parcial";
    }

    public boolean tienePagos() {
        return totalPagado != null && totalPagado.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean estaCompletamentePagado() {
        return saldoPendiente != null && saldoPendiente.compareTo(BigDecimal.ZERO) == 0;
    }

    public void setDiasRestantes(int dias) {
        this.diasRestantes = dias;
    }

    public void setDiasTranscurridos(int dias) {
        this.diasTranscurridos = dias;
    }

    public void setEstaVencido(boolean vencido) {
        this.estaVencido = vencido;
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
}