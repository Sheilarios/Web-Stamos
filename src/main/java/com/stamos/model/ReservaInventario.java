package com.stamos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Entidad ReservaInventario.
 * 
 * <p>Esta entidad representa las reservas de equipos y mobiliario del inventario
 * asignados a proyectos específicos, gestionando la disponibilidad y el uso
 * de los recursos físicos de STAMOS S.L.</p>
 * 
 * <p>Implementa los requerimientos:</p>
 * <ul>
 *   <li>RN-14: No se puede asignar un equipo si no hay disponibilidad para las fechas requeridas</li>
 *   <li>RF-18: Asignar equipos y mobiliario a proyectos de acuerdo con las fechas programadas</li>
 *   <li>RF-19: Descontar temporalmente la disponibilidad de equipos al realizar una reserva</li>
 *   <li>RF-20: Bloquear asignaciones cuando no exista disponibilidad o exista solapamiento de fechas</li>
 *   <li>RF-21: Consultar la disponibilidad de equipos por rango de fechas</li>
 * </ul>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
@Entity
@Table(name = "ReservaInventario",
       indexes = {
           @Index(name = "idx_reserva_inventario", columnList = "id_inventario"),
           @Index(name = "idx_reserva_proyecto", columnList = "id_proyecto"),
           @Index(name = "idx_reserva_estado", columnList = "estado_reserva"),
           @Index(name = "idx_reserva_fechas", columnList = "fecha_inicio, fecha_fin"),
           @Index(name = "idx_reserva_fecha_creacion", columnList = "fecha_reserva")
       })
public class ReservaInventario {

    // ============================================
    // CONSTANTES PARA ESTADOS DE RESERVA
    // ============================================

    public static final String ESTADO_ACTIVA = "Activa";
    public static final String ESTADO_COMPLETADA = "Completada";
    public static final String ESTADO_CANCELADA = "Cancelada";

    // ============================================
    // ATRIBUTOS PRINCIPALES
    // ============================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer idReserva;

    @NotNull(message = "El inventario es obligatorio")
    @Column(name = "id_inventario", nullable = false)
    private Integer idInventario;

    @NotNull(message = "El proyecto es obligatorio")
    @Column(name = "id_proyecto", nullable = false)
    private Integer idProyecto;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "estado_reserva", nullable = false)
    private String estadoReserva = ESTADO_ACTIVA;

    @Column(name = "fecha_reserva", nullable = false)
    private LocalDateTime fechaReserva;

    @Size(max = 500, message = "Las notas no pueden exceder los 500 caracteres")
    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    // ============================================
    // CAMPOS DE AUDITORÍA
    // ============================================

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "id_usuario_registro")
    private Integer idUsuarioRegistro;

    @Column(name = "id_usuario_actualizacion")
    private Integer idUsuarioActualizacion;

    // ============================================
    // CAMPOS ADICIONALES (NO EN BD)
    // ============================================

    @Transient
    private String nombreEquipo;

    @Transient
    private String codigoSerial;

    @Transient
    private String nombreProducto;

    @Transient
    private String nombreProyecto;

    @Transient
    private String codigoProyecto;

    @Transient
    private String estadoReservaLegible;

    @Transient
    private String colorEstado;

    @Transient
    private String iconoEstado;

    @Transient
    private Integer duracionDias;

    @Transient
    private Boolean estaActiva;

    @Transient
    private Boolean estaCompletada;

    @Transient
    private Boolean estaCancelada;

    @Transient
    private Boolean estaVigente;

    // ============================================
    // CONSTRUCTORES
    // ============================================

    public ReservaInventario() {
        this.estadoReserva = ESTADO_ACTIVA;
        this.fechaReserva = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        this.estaActiva = true;
        this.estaCompletada = false;
        this.estaCancelada = false;
        this.estaVigente = true;
        actualizarCamposTransient();
    }

    public ReservaInventario(Integer idInventario, Integer idProyecto, 
                            LocalDate fechaInicio, LocalDate fechaFin) {
        this();
        this.idInventario = idInventario;
        this.idProyecto = idProyecto;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        calcularDuracion();
        actualizarCamposTransient();
    }

    public ReservaInventario(Integer idInventario, Integer idProyecto,
                            LocalDate fechaInicio, LocalDate fechaFin,
                            String notas) {
        this(idInventario, idProyecto, fechaInicio, fechaFin);
        this.notas = notas;
    }

    // ============================================
    // GETTERS Y SETTERS
    // ============================================

    public Integer getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Integer idReserva) {
        this.idReserva = idReserva;
    }

    public Integer getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(Integer idInventario) {
        this.idInventario = idInventario;
    }

    public Integer getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(Integer idProyecto) {
        this.idProyecto = idProyecto;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
        calcularDuracion();
        actualizarVigencia();
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
        calcularDuracion();
        actualizarVigencia();
    }

    public String getEstadoReserva() {
        return estadoReserva;
    }

    public void setEstadoReserva(String estadoReserva) {
        this.estadoReserva = estadoReserva;
        actualizarCamposTransient();
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDateTime fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
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

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public String getCodigoSerial() {
        return codigoSerial;
    }

    public void setCodigoSerial(String codigoSerial) {
        this.codigoSerial = codigoSerial;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getNombreProyecto() {
        return nombreProyecto;
    }

    public void setNombreProyecto(String nombreProyecto) {
        this.nombreProyecto = nombreProyecto;
    }

    public String getCodigoProyecto() {
        return codigoProyecto;
    }

    public void setCodigoProyecto(String codigoProyecto) {
        this.codigoProyecto = codigoProyecto;
    }

    public String getEstadoReservaLegible() {
        return estadoReservaLegible;
    }

    public void setEstadoReservaLegible(String estadoReservaLegible) {
        this.estadoReservaLegible = estadoReservaLegible;
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

    public Integer getDuracionDias() {
        return duracionDias;
    }

    public void setDuracionDias(Integer duracionDias) {
        this.duracionDias = duracionDias;
    }

    public Boolean getEstaActiva() {
        return estaActiva;
    }

    public void setEstaActiva(Boolean estaActiva) {
        this.estaActiva = estaActiva;
    }

    public Boolean getEstaCompletada() {
        return estaCompletada;
    }

    public void setEstaCompletada(Boolean estaCompletada) {
        this.estaCompletada = estaCompletada;
    }

    public Boolean getEstaCancelada() {
        return estaCancelada;
    }

    public void setEstaCancelada(Boolean estaCancelada) {
        this.estaCancelada = estaCancelada;
    }

    public Boolean getEstaVigente() {
        return estaVigente;
    }

    public void setEstaVigente(Boolean estaVigente) {
        this.estaVigente = estaVigente;
    }

    // ============================================
    // MÉTODOS DE NEGOCIO
    // ============================================

    /**
     * Actualiza los campos transient basados en el estado de la reserva.
     */
    private void actualizarCamposTransient() {
        if (estadoReserva != null) {
            switch (estadoReserva) {
                case ESTADO_ACTIVA:
                    this.estadoReservaLegible = "Activa";
                    this.colorEstado = "#3B82F6";
                    this.iconoEstado = "ti ti-clock";
                    this.estaActiva = true;
                    this.estaCompletada = false;
                    this.estaCancelada = false;
                    break;
                case ESTADO_COMPLETADA:
                    this.estadoReservaLegible = "Completada";
                    this.colorEstado = "#10B981";
                    this.iconoEstado = "ti ti-check-circle";
                    this.estaActiva = false;
                    this.estaCompletada = true;
                    this.estaCancelada = false;
                    break;
                case ESTADO_CANCELADA:
                    this.estadoReservaLegible = "Cancelada";
                    this.colorEstado = "#EF4444";
                    this.iconoEstado = "ti ti-x-circle";
                    this.estaActiva = false;
                    this.estaCompletada = false;
                    this.estaCancelada = true;
                    break;
                default:
                    this.estadoReservaLegible = estadoReserva;
                    this.colorEstado = "#6B7280";
                    this.iconoEstado = "ti ti-circle";
                    this.estaActiva = false;
                    this.estaCompletada = false;
                    this.estaCancelada = false;
                    break;
            }
        }
        actualizarVigencia();
    }

    /**
     * Calcula la duración de la reserva en días.
     */
    private void calcularDuracion() {
        if (fechaInicio != null && fechaFin != null) {
            this.duracionDias = (int) java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
            if (this.duracionDias < 0) {
                this.duracionDias = 0;
            }
        } else {
            this.duracionDias = 0;
        }
    }

    /**
     * Actualiza si la reserva está vigente.
     */
    private void actualizarVigencia() {
        if (estaActiva != null && estaActiva) {
            LocalDate hoy = LocalDate.now();
            if (fechaInicio != null && fechaFin != null) {
                this.estaVigente = !hoy.isAfter(fechaFin) && !hoy.isBefore(fechaInicio);
            } else {
                this.estaVigente = true;
            }
        } else {
            this.estaVigente = false;
        }
    }

    /**
     * Verifica si la reserva está activa.
     * 
     * @return true si está activa, false en caso contrario
     */
    public boolean isActiva() {
        return estaActiva != null && estaActiva;
    }

    /**
     * Verifica si la reserva está completada.
     * 
     * @return true si está completada, false en caso contrario
     */
    public boolean isCompletada() {
        return estaCompletada != null && estaCompletada;
    }

    /**
     * Verifica si la reserva está cancelada.
     * 
     * @return true si está cancelada, false en caso contrario
     */
    public boolean isCancelada() {
        return estaCancelada != null && estaCancelada;
    }

    /**
     * Verifica si la reserva está vigente (en el rango de fechas actual).
     * 
     * @return true si está vigente, false en caso contrario
     */
    public boolean isVigente() {
        return estaVigente != null && estaVigente;
    }

    /**
     * Completa la reserva.
     */
    public void completar() {
        this.estadoReserva = ESTADO_COMPLETADA;
        this.fechaActualizacion = LocalDateTime.now();
        actualizarCamposTransient();
    }

    /**
     * Cancela la reserva.
     */
    public void cancelar() {
        this.estadoReserva = ESTADO_CANCELADA;
        this.fechaActualizacion = LocalDateTime.now();
        actualizarCamposTransient();
    }

    /**
     * Reactiva una reserva cancelada o completada.
     */
    public void reactivar() {
        this.estadoReserva = ESTADO_ACTIVA;
        this.fechaActualizacion = LocalDateTime.now();
        actualizarCamposTransient();
    }

    /**
     * Verifica si la reserva está en el rango de fechas actual.
     * 
     * @return true si está en el rango actual, false en caso contrario
     */
    public boolean estaEnRangoActual() {
        LocalDate hoy = LocalDate.now();
        return fechaInicio != null && fechaFin != null &&
               !hoy.isAfter(fechaFin) && !hoy.isBefore(fechaInicio);
    }

    /**
     * Verifica si la reserva se solapa con otro rango de fechas.
     * 
     * @param otraFechaInicio Fecha de inicio de la otra reserva
     * @param otraFechaFin Fecha de fin de la otra reserva
     * @return true si hay solapamiento, false en caso contrario
     */
    public boolean seSolapaCon(LocalDate otraFechaInicio, LocalDate otraFechaFin) {
        if (fechaInicio == null || fechaFin == null || 
            otraFechaInicio == null || otraFechaFin == null) {
            return false;
        }
        return !(fechaFin.isBefore(otraFechaInicio) || fechaInicio.isAfter(otraFechaFin));
    }

    /**
     * Verifica si la reserva tiene notas.
     * 
     * @return true si tiene notas, false en caso contrario
     */
    public boolean tieneNotas() {
        return notas != null && !notas.trim().isEmpty();
    }

    /**
     * Obtiene el rango de fechas en formato legible.
     * 
     * @return Rango de fechas legible
     */
    public String getRangoFechasLegible() {
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

    // ============================================
    // MÉTODOS DE FORMATO PARA LA VISTA
    // ============================================

    /**
     * Obtiene el estado en formato legible.
     * 
     * @return Estado legible
     */
    public String getEstadoLegible() {
        return estadoReservaLegible != null ? estadoReservaLegible : estadoReserva;
    }

    /**
     * Obtiene la clase CSS para el badge del estado.
     * 
     * @return Clase CSS
     */
    public String getEstadoBadgeClass() {
        if (estadoReserva == null) return "status-pendiente";
        
        switch (estadoReserva) {
            case ESTADO_ACTIVA:
                return "status-produccion";
            case ESTADO_COMPLETADA:
                return "status-validado";
            case ESTADO_CANCELADA:
                return "status-rechazado";
            default:
                return "status-pendiente";
        }
    }

    /**
     * Obtiene el estado con ícono.
     * 
     * @return Estado con ícono
     */
    public String getEstadoConIcono() {
        return "<i class='" + iconoEstado + "'></i> " + getEstadoLegible();
    }

    /**
     * Obtiene la fecha de inicio formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaInicioFormateada() {
        if (fechaInicio == null) return "-";
        return fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Obtiene la fecha de fin formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaFinFormateada() {
        if (fechaFin == null) return "-";
        return fechaFin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Obtiene la fecha de reserva formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaReservaFormateada() {
        if (fechaReserva == null) return "-";
        return fechaReserva.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    /**
     * Obtiene la duración en días formateada.
     * 
     * @return Duración formateada
     */
    public String getDuracionFormateada() {
        if (duracionDias == null) return "-";
        if (duracionDias == 0) return "Menos de 1 día";
        if (duracionDias == 1) return "1 día";
        return duracionDias + " días";
    }

    /**
     * Obtiene el equipo en formato legible.
     * 
     * @return Equipo legible
     */
    public String getEquipoLegible() {
        if (nombreProducto != null && codigoSerial != null) {
            return nombreProducto + " (" + codigoSerial + ")";
        }
        if (nombreProducto != null) {
            return nombreProducto;
        }
        if (codigoSerial != null) {
            return codigoSerial;
        }
        return "Equipo ID: " + idInventario;
    }

    /**
     * Obtiene el proyecto en formato legible.
     * 
     * @return Proyecto legible
     */
    public String getProyectoLegible() {
        if (nombreProyecto != null && codigoProyecto != null) {
            return codigoProyecto + " - " + nombreProyecto;
        }
        if (nombreProyecto != null) {
            return nombreProyecto;
        }
        return "Proyecto ID: " + idProyecto;
    }

    /**
     * Obtiene el resumen de la reserva.
     * 
     * @return Resumen
     */
    public String getResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append("Reserva de ").append(getEquipoLegible());
        sb.append(" para ").append(getProyectoLegible());
        sb.append(" (").append(getRangoFechasLegible()).append(")");
        sb.append(" - ").append(getEstadoLegible());
        return sb.toString();
    }

    /**
     * Obtiene la clase CSS para el badge de vigencia.
     * 
     * @return Clase CSS
     */
    public String getVigenciaBadgeClass() {
        if (isVigente()) {
            return "badge-success";
        }
        if (isActiva()) {
            return "badge-warning";
        }
        return "badge-secondary";
    }

    /**
     * Obtiene el texto de vigencia.
     * 
     * @return Texto de vigencia
     */
    public String getVigenciaTexto() {
        if (isVigente()) {
            return "Vigente";
        }
        if (isActiva()) {
            return "Pendiente";
        }
        return "Finalizada";
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
        sb.append("\"idReserva\":").append(idReserva).append(",");
        sb.append("\"idInventario\":").append(idInventario).append(",");
        sb.append("\"idProyecto\":").append(idProyecto).append(",");
        sb.append("\"fechaInicio\":\"").append(getFechaInicioFormateada()).append("\",");
        sb.append("\"fechaFin\":\"").append(getFechaFinFormateada()).append("\",");
        sb.append("\"estado\":\"").append(estadoReserva != null ? estadoReserva : "").append("\",");
        sb.append("\"estadoLegible\":\"").append(getEstadoLegible()).append("\",");
        sb.append("\"color\":\"").append(colorEstado != null ? colorEstado : "#6B7280").append("\",");
        sb.append("\"icono\":\"").append(iconoEstado != null ? iconoEstado : "ti ti-circle").append("\"");
        if (nombreProducto != null) {
            sb.append(",\"producto\":\"").append(nombreProducto).append("\"");
        }
        if (nombreProyecto != null) {
            sb.append(",\"proyecto\":\"").append(nombreProyecto).append("\"");
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
        sb.append("\"idReserva\":").append(idReserva).append(",");
        sb.append("\"idInventario\":").append(idInventario).append(",");
        sb.append("\"idProyecto\":").append(idProyecto).append(",");
        sb.append("\"fechaInicio\":\"").append(getFechaInicioFormateada()).append("\",");
        sb.append("\"fechaFin\":\"").append(getFechaFinFormateada()).append("\",");
        sb.append("\"estado\":\"").append(estadoReserva != null ? estadoReserva : "").append("\",");
        sb.append("\"estadoLegible\":\"").append(getEstadoLegible()).append("\",");
        sb.append("\"color\":\"").append(colorEstado != null ? colorEstado : "#6B7280").append("\",");
        sb.append("\"icono\":\"").append(iconoEstado != null ? iconoEstado : "ti ti-circle").append("\",");
        sb.append("\"duracionDias\":").append(duracionDias != null ? duracionDias : 0).append(",");
        sb.append("\"duracionFormateada\":\"").append(getDuracionFormateada()).append("\",");
        sb.append("\"fechaReserva\":\"").append(getFechaReservaFormateada()).append("\",");
        sb.append("\"estaActiva\":").append(isActiva()).append(",");
        sb.append("\"estaVigente\":").append(isVigente());
        if (nombreProducto != null) {
            sb.append(",\"nombreProducto\":\"").append(nombreProducto).append("\"");
        }
        if (codigoSerial != null) {
            sb.append(",\"codigoSerial\":\"").append(codigoSerial).append("\"");
        }
        if (nombreProyecto != null) {
            sb.append(",\"nombreProyecto\":\"").append(nombreProyecto).append("\"");
        }
        if (codigoProyecto != null) {
            sb.append(",\"codigoProyecto\":\"").append(codigoProyecto).append("\"");
        }
        if (notas != null) {
            sb.append(",\"notas\":\"").append(notas).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

    // ============================================
    // MÉTODOS OVERRIDE
    // ============================================

    @Override
    public String toString() {
        return "ReservaInventario{" +
                "idReserva=" + idReserva +
                ", idInventario=" + idInventario +
                ", idProyecto=" + idProyecto +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", estadoReserva='" + estadoReserva + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservaInventario that = (ReservaInventario) o;
        return idReserva != null && idReserva.equals(that.idReserva);
    }

    @Override
    public int hashCode() {
        return idReserva != null ? idReserva.hashCode() : 0;
    }

    // ============================================
    // MÉTODOS ESTÁTICOS DE FÁBRICA
    // ============================================

    /**
     * Crea una reserva activa para un equipo y proyecto.
     * 
     * @param idInventario ID del inventario
     * @param idProyecto ID del proyecto
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Reserva de inventario
     */
    public static ReservaInventario crearReserva(Integer idInventario, Integer idProyecto,
                                                 LocalDate fechaInicio, LocalDate fechaFin) {
        return new ReservaInventario(idInventario, idProyecto, fechaInicio, fechaFin);
    }

    /**
     * Crea una reserva activa con notas.
     * 
     * @param idInventario ID del inventario
     * @param idProyecto ID del proyecto
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param notas Notas adicionales
     * @return Reserva de inventario
     */
    public static ReservaInventario crearReservaConNotas(Integer idInventario, Integer idProyecto,
                                                         LocalDate fechaInicio, LocalDate fechaFin,
                                                         String notas) {
        return new ReservaInventario(idInventario, idProyecto, fechaInicio, fechaFin, notas);
    }

    /**
     * Crea una reserva desde un DTO.
     * 
     * @param dto DTO de la reserva
     * @return Reserva de inventario
     */
    public static ReservaInventario fromDTO(com.stamos.dto.ReservaInventarioDTO dto) {
        if (dto == null) {
            return null;
        }
        ReservaInventario reserva = new ReservaInventario();
        if (dto.getIdReserva() != null) {
            reserva.setIdReserva(dto.getIdReserva());
        }
        reserva.setIdInventario(dto.getIdInventario());
        reserva.setIdProyecto(dto.getIdProyecto());
        reserva.setFechaInicio(dto.getFechaInicio());
        reserva.setFechaFin(dto.getFechaFin());
        reserva.setEstadoReserva(dto.getEstadoReserva());
        reserva.setNotas(dto.getNotas());
        return reserva;
    }

    /**
     * Convierte la reserva a un DTO.
     * 
     * @return DTO de la reserva
     */
    public com.stamos.dto.ReservaInventarioDTO toDTO() {
        com.stamos.dto.ReservaInventarioDTO dto = new com.stamos.dto.ReservaInventarioDTO();
        dto.setIdReserva(this.idReserva);
        dto.setIdInventario(this.idInventario);
        dto.setIdProyecto(this.idProyecto);
        dto.setFechaInicio(this.fechaInicio);
        dto.setFechaFin(this.fechaFin);
        dto.setEstadoReserva(this.estadoReserva);
        dto.setNotas(this.notas);
        dto.setNombreProducto(this.nombreProducto);
        dto.setCodigoSerial(this.codigoSerial);
        dto.setNombreProyecto(this.nombreProyecto);
        dto.setCodigoProyecto(this.codigoProyecto);
        dto.setEstadoReservaLegible(this.estadoReservaLegible);
        dto.setColorEstado(this.colorEstado);
        dto.setIconoEstado(this.iconoEstado);
        dto.setDuracionDias(this.duracionDias);
        dto.setEstaActiva(isActiva());
        dto.setEstaVigente(isVigente());
        return dto;
    }

    public String getEstadoProyecto() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}