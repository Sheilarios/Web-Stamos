package com.stamos.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO (Data Transfer Object) para la entidad ReservaInventario.
 * 
 * <p>Este DTO se utiliza para transferir datos de reservas de inventario
 * entre la capa de servicio y la capa de presentación, evitando exponer
 * directamente la entidad y permitiendo una mejor organización de los datos.</p>
 * 
 * <p>Incluye información de la reserva, fechas, estado, equipo asignado
 * y proyecto asociado.</p>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
public class ReservaInventarioDTO {

    // ============================================
    // ATRIBUTOS PRINCIPALES
    // ============================================

    private Integer idReserva;
    private Integer idInventario;
    private Integer idProyecto;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estadoReserva;
    private String notas;

    // ============================================
    // CAMPOS DE AUDITORÍA
    // ============================================

    private LocalDateTime fechaReserva;
    private LocalDateTime fechaActualizacion;
    private Integer idUsuarioRegistro;
    private Integer idUsuarioActualizacion;
    private String nombreUsuarioRegistro;
    private String nombreUsuarioActualizacion;

    // ============================================
    // CAMPOS RELACIONADOS (DESDE INVENTARIO)
    // ============================================

    private String nombreProducto;
    private String codigoSerial;
    private String ubicacion;
    private String estadoFisico;

    // ============================================
    // CAMPOS RELACIONADOS (DESDE PROYECTO)
    // ============================================

    private String nombreProyecto;
    private String codigoProyecto;
    private String estadoProyecto;

    // ============================================
    // CAMPOS DE ESTADO Y FORMATO
    // ============================================

    private String estadoReservaLegible;
    private String colorEstado;
    private String iconoEstado;
    private Integer duracionDias;
    private String duracionFormateada;
    private Boolean estaActiva;
    private Boolean estaCompletada;
    private Boolean estaCancelada;
    private Boolean estaVigente;

    // ============================================
    // CAMPOS ADICIONALES
    // ============================================

    private String rangoFechasLegible;
    private String equipoLegible;
    private String proyectolegible;

    // ============================================
    // CONSTRUCTORES
    // ============================================

    /**
     * Constructor por defecto.
     */
    public ReservaInventarioDTO() {
        this.estadoReserva = "Activa";
        this.estaActiva = true;
        this.estaCompletada = false;
        this.estaCancelada = false;
        this.estaVigente = true;
        this.colorEstado = "#3B82F6";
        this.iconoEstado = "ti ti-clock";
        this.estadoReservaLegible = "Activa";
        this.duracionDias = 0;
        this.duracionFormateada = "-";
    }

    /**
     * Constructor con campos principales.
     * 
     * @param idReserva ID de la reserva
     * @param idInventario ID del inventario
     * @param idProyecto ID del proyecto
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param estadoReserva Estado de la reserva
     */
    public ReservaInventarioDTO(Integer idReserva, Integer idInventario, Integer idProyecto,
                               LocalDate fechaInicio, LocalDate fechaFin, String estadoReserva) {
        this();
        this.idReserva = idReserva;
        this.idInventario = idInventario;
        this.idProyecto = idProyecto;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estadoReserva = estadoReserva;
        actualizarCamposFormateados();
    }

    /**
     * Constructor completo.
     * 
     * @param idReserva ID de la reserva
     * @param idInventario ID del inventario
     * @param idProyecto ID del proyecto
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param estadoReserva Estado de la reserva
     * @param nombreProducto Nombre del producto
     * @param codigoSerial Código serial
     * @param nombreProyecto Nombre del proyecto
     */
    public ReservaInventarioDTO(Integer idReserva, Integer idInventario, Integer idProyecto,
                               LocalDate fechaInicio, LocalDate fechaFin, String estadoReserva,
                               String nombreProducto, String codigoSerial, String nombreProyecto) {
        this(idReserva, idInventario, idProyecto, fechaInicio, fechaFin, estadoReserva);
        this.nombreProducto = nombreProducto;
        this.codigoSerial = codigoSerial;
        this.nombreProyecto = nombreProyecto;
        actualizarCamposFormateados();
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
        actualizarCamposFormateados();
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
        actualizarCamposFormateados();
    }

    public String getEstadoReserva() {
        return estadoReserva;
    }

    public void setEstadoReserva(String estadoReserva) {
        this.estadoReserva = estadoReserva;
        actualizarCamposFormateados();
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDateTime fechaReserva) {
        this.fechaReserva = fechaReserva;
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
        actualizarCamposFormateados();
    }

    public String getCodigoSerial() {
        return codigoSerial;
    }

    public void setCodigoSerial(String codigoSerial) {
        this.codigoSerial = codigoSerial;
        actualizarCamposFormateados();
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
    }

    public String getNombreProyecto() {
        return nombreProyecto;
    }

    public void setNombreProyecto(String nombreProyecto) {
        this.nombreProyecto = nombreProyecto;
        actualizarCamposFormateados();
    }

    public String getCodigoProyecto() {
        return codigoProyecto;
    }

    public void setCodigoProyecto(String codigoProyecto) {
        this.codigoProyecto = codigoProyecto;
        actualizarCamposFormateados();
    }

    public String getEstadoProyecto() {
        return estadoProyecto;
    }

    public void setEstadoProyecto(String estadoProyecto) {
        this.estadoProyecto = estadoProyecto;
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
        actualizarCamposFormateados();
    }

    public String getDuracionFormateada() {
        return duracionFormateada;
    }

    public void setDuracionFormateada(String duracionFormateada) {
        this.duracionFormateada = duracionFormateada;
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

    public String getRangoFechasLegible() {
        return rangoFechasLegible;
    }

    public void setRangoFechasLegible(String rangoFechasLegible) {
        this.rangoFechasLegible = rangoFechasLegible;
    }

    public String getEquipoLegible() {
        return equipoLegible;
    }

    public void setEquipoLegible(String equipoLegible) {
        this.equipoLegible = equipoLegible;
    }

    public String getProyectolegible() {
        return proyectolegible;
    }

    public void setProyectolegible(String proyectolegible) {
        this.proyectolegible = proyectolegible;
    }

    // ============================================
    // MÉTODOS DE NEGOCIO
    // ============================================

    /**
     * Actualiza los campos formateados basados en los atributos.
     */
    private void actualizarCamposFormateados() {
        // Estado de la reserva
        if (estadoReserva != null) {
            switch (estadoReserva) {
                case "Activa":
                    this.estadoReservaLegible = "Activa";
                    this.colorEstado = "#3B82F6";
                    this.iconoEstado = "ti ti-clock";
                    this.estaActiva = true;
                    this.estaCompletada = false;
                    this.estaCancelada = false;
                    break;
                case "Completada":
                    this.estadoReservaLegible = "Completada";
                    this.colorEstado = "#10B981";
                    this.iconoEstado = "ti ti-check-circle";
                    this.estaActiva = false;
                    this.estaCompletada = true;
                    this.estaCancelada = false;
                    break;
                case "Cancelada":
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

        // Calcular duración
        if (fechaInicio != null && fechaFin != null) {
            this.duracionDias = (int) java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
            if (this.duracionDias < 0) {
                this.duracionDias = 0;
            }
            
            if (this.duracionDias == 0) {
                this.duracionFormateada = "Menos de 1 día";
            } else if (this.duracionDias == 1) {
                this.duracionFormateada = "1 día";
            } else {
                this.duracionFormateada = this.duracionDias + " días";
            }
        } else {
            this.duracionDias = 0;
            this.duracionFormateada = "-";
        }

        // Rango de fechas legible
        if (fechaInicio == null && fechaFin == null) {
            this.rangoFechasLegible = "Sin fechas";
        } else if (fechaInicio == null) {
            this.rangoFechasLegible = "Hasta " + getFechaFinFormateada();
        } else if (fechaFin == null) {
            this.rangoFechasLegible = "Desde " + getFechaInicioFormateada();
        } else {
            this.rangoFechasLegible = getFechaInicioFormateada() + " - " + getFechaFinFormateada();
        }

        // Equipo legible
        if (nombreProducto != null && codigoSerial != null) {
            this.equipoLegible = nombreProducto + " (" + codigoSerial + ")";
        } else if (nombreProducto != null) {
            this.equipoLegible = nombreProducto;
        } else if (codigoSerial != null) {
            this.equipoLegible = codigoSerial;
        } else {
            this.equipoLegible = "Equipo ID: " + idInventario;
        }

        // Proyecto legible
        if (nombreProyecto != null && codigoProyecto != null) {
            this.proyectolegible = codigoProyecto + " - " + nombreProyecto;
        } else if (nombreProyecto != null) {
            this.proyectolegible = nombreProyecto;
        } else {
            this.proyectolegible = "Proyecto ID: " + idProyecto;
        }

        // Vigencia
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
     * Verifica si la reserva está vigente.
     * 
     * @return true si está vigente, false en caso contrario
     */
    public boolean isVigente() {
        return estaVigente != null && estaVigente;
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
     * Obtiene la duración formateada.
     * 
     * @return Duración formateada
     */

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

    /**
     * Obtiene el estado físico del equipo en formato legible.
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
     * Obtiene el estado del proyecto en formato legible.
     * 
     * @return Estado del proyecto legible
     */
    public String getEstadoProyectoLegible() {
        if (estadoProyecto == null) return "Sin estado";
        
        switch (estadoProyecto) {
            case "Aprobado":
                return "Aprobado";
            case "En Diseño":
                return "En Diseño";
            case "En Producción":
                return "En Producción";
            case "Instalación/Montaje":
                return "Instalación/Montaje";
            case "Entregado":
                return "Entregado";
            case "Anulado":
                return "Anulado";
            default:
                return estadoProyecto;
        }
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
     * Obtiene el equipo legible.
     * 
     * @return Equipo legible
     */

    /**
     * Obtiene el proyecto legible.
     * 
     * @return Proyecto legible
     */
    public String getProyectoLegible() {
        return proyectolegible != null ? proyectolegible : "Proyecto ID: " + idProyecto;
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
        sb.append("\"icono\":\"").append(iconoEstado != null ? iconoEstado : "ti ti-circle").append("\",");
        sb.append("\"duracionDias\":").append(duracionDias != null ? duracionDias : 0);
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
        if (ubicacion != null) {
            sb.append(",\"ubicacion\":\"").append(ubicacion).append("\"");
        }
        if (estadoFisico != null) {
            sb.append(",\"estadoFisico\":\"").append(estadoFisico).append("\"");
        }
        if (nombreProyecto != null) {
            sb.append(",\"nombreProyecto\":\"").append(nombreProyecto).append("\"");
        }
        if (codigoProyecto != null) {
            sb.append(",\"codigoProyecto\":\"").append(codigoProyecto).append("\"");
        }
        if (estadoProyecto != null) {
            sb.append(",\"estadoProyecto\":\"").append(estadoProyecto).append("\"");
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
        return "ReservaInventarioDTO{" +
                "idReserva=" + idReserva +
                ", idInventario=" + idInventario +
                ", idProyecto=" + idProyecto +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", estadoReserva='" + estadoReserva + '\'' +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", nombreProyecto='" + nombreProyecto + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservaInventarioDTO that = (ReservaInventarioDTO) o;
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
     * Crea un DTO desde una entidad.
     * 
     * @param reserva Entidad de reserva
     * @return DTO de reserva
     */
    public static ReservaInventarioDTO fromEntity(com.stamos.model.ReservaInventario reserva) {
        if (reserva == null) {
            return null;
        }

        ReservaInventarioDTO dto = new ReservaInventarioDTO();
        dto.setIdReserva(reserva.getIdReserva());
        dto.setIdInventario(reserva.getIdInventario());
        dto.setIdProyecto(reserva.getIdProyecto());
        dto.setFechaInicio(reserva.getFechaInicio());
        dto.setFechaFin(reserva.getFechaFin());
        dto.setEstadoReserva(reserva.getEstadoReserva());
        dto.setNotas(reserva.getNotas());
        dto.setFechaReserva(reserva.getFechaReserva());
        dto.setFechaActualizacion(reserva.getFechaActualizacion());
        dto.setIdUsuarioRegistro(reserva.getIdUsuarioRegistro());
        dto.setIdUsuarioActualizacion(reserva.getIdUsuarioActualizacion());
        
        // Campos transient
        dto.setNombreProducto(reserva.getNombreProducto());
        dto.setCodigoSerial(reserva.getCodigoSerial());
        dto.setNombreProyecto(reserva.getNombreProyecto());
        dto.setCodigoProyecto(reserva.getCodigoProyecto());
        dto.setEstadoProyecto(reserva.getEstadoProyecto());
        
        // Actualizar campos formateados
        dto.actualizarCamposFormateados();
        
        return dto;
    }

    /**
     * Crea un DTO desde una entidad con información completa.
     * 
     * @param reserva Entidad de reserva
     * @param inventario Entidad de inventario
     * @param proyecto Entidad de proyecto
     * @return DTO de reserva
     */
    public static ReservaInventarioDTO fromEntityWithDetails(com.stamos.model.ReservaInventario reserva,
                                                            com.stamos.model.Inventario inventario,
                                                            com.stamos.model.Proyecto proyecto) {
        ReservaInventarioDTO dto = fromEntity(reserva);
        if (dto != null) {
            if (inventario != null) {
                dto.setCodigoSerial(inventario.getCodigoSerial());
                dto.setUbicacion(inventario.getUbicacion());
                dto.setEstadoFisico(inventario.getEstadoFisico());
            }
            if (proyecto != null) {
                dto.setCodigoProyecto(proyecto.getCodigoProyecto());
                dto.setEstadoProyecto(proyecto.getEstado());
            }
            dto.actualizarCamposFormateados();
        }
        return dto;
    }

    /**
     * Crea un DTO para una nueva reserva.
     * 
     * @param idInventario ID del inventario
     * @param idProyecto ID del proyecto
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return DTO de reserva
     */
    public static ReservaInventarioDTO crearNuevaReserva(Integer idInventario, Integer idProyecto,
                                                         LocalDate fechaInicio, LocalDate fechaFin) {
        ReservaInventarioDTO dto = new ReservaInventarioDTO();
        dto.setIdInventario(idInventario);
        dto.setIdProyecto(idProyecto);
        dto.setFechaInicio(fechaInicio);
        dto.setFechaFin(fechaFin);
        dto.setEstadoReserva("Activa");
        dto.setFechaReserva(LocalDateTime.now());
        dto.actualizarCamposFormateados();
        return dto;
    }

    /**
     * Convierte el DTO a una entidad.
     * 
     * @return Entidad de reserva
     */
    public com.stamos.model.ReservaInventario toEntity() {
        com.stamos.model.ReservaInventario reserva = new com.stamos.model.ReservaInventario();
        if (this.idReserva != null) {
            reserva.setIdReserva(this.idReserva);
        }
        reserva.setIdInventario(this.idInventario);
        reserva.setIdProyecto(this.idProyecto);
        reserva.setFechaInicio(this.fechaInicio);
        reserva.setFechaFin(this.fechaFin);
        reserva.setEstadoReserva(this.estadoReserva);
        reserva.setNotas(this.notas);
        reserva.setFechaReserva(this.fechaReserva);
        reserva.setFechaActualizacion(this.fechaActualizacion);
        reserva.setIdUsuarioRegistro(this.idUsuarioRegistro);
        reserva.setIdUsuarioActualizacion(this.idUsuarioActualizacion);
        return reserva;
    }
}