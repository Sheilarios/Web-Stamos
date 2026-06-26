package com.stamos.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReservaInventarioDTO {

    private Integer idReserva;
    private Integer idInventario;
    private Integer idProyecto;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estadoReserva;
    private String notas;
    private LocalDateTime fechaReserva;
    private LocalDateTime fechaActualizacion;
    private Integer idUsuarioRegistro;
    private Integer idUsuarioActualizacion;
    private String nombreUsuarioRegistro;
    private String nombreUsuarioActualizacion;
    private String nombreProducto;
    private String codigoSerial;
    private String ubicacion;
    private String estadoFisico;
    private String nombreProyecto;
    private String codigoProyecto;
    private String estadoProyecto;
    private String estadoReservaLegible;
    private String colorEstado;
    private String iconoEstado;
    private Integer duracionDias;
    private String duracionFormateada;
    private Boolean estaActiva;
    private Boolean estaCompletada;
    private Boolean estaCancelada;
    private Boolean estaVigente;
    private String rangoFechasLegible;
    private String equipoLegible;
    private String proyectolegible;

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

    public ReservaInventarioDTO(Integer idReserva, Integer idInventario, Integer idProyecto,
                               LocalDate fechaInicio, LocalDate fechaFin, String estadoReserva,
                               String nombreProducto, String codigoSerial, String nombreProyecto) {
        this(idReserva, idInventario, idProyecto, fechaInicio, fechaFin, estadoReserva);
        this.nombreProducto = nombreProducto;
        this.codigoSerial = codigoSerial;
        this.nombreProyecto = nombreProyecto;
        actualizarCamposFormateados();
    }

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

    private void actualizarCamposFormateados() {
        if (estadoReserva != null) {
            switch (estadoReserva) {
                case "Activa" -> {
                    this.estadoReservaLegible = "Activa";
                    this.colorEstado = "#3B82F6";
                    this.iconoEstado = "ti ti-clock";
                    this.estaActiva = true;
                    this.estaCompletada = false;
                    this.estaCancelada = false;
                }
                case "Completada" -> {
                    this.estadoReservaLegible = "Completada";
                    this.colorEstado = "#10B981";
                    this.iconoEstado = "ti ti-check-circle";
                    this.estaActiva = false;
                    this.estaCompletada = true;
                    this.estaCancelada = false;
                }
                case "Cancelada" -> {
                    this.estadoReservaLegible = "Cancelada";
                    this.colorEstado = "#EF4444";
                    this.iconoEstado = "ti ti-x-circle";
                    this.estaActiva = false;
                    this.estaCompletada = false;
                    this.estaCancelada = true;
                }
                default -> {
                    this.estadoReservaLegible = estadoReserva;
                    this.colorEstado = "#6B7280";
                    this.iconoEstado = "ti ti-circle";
                    this.estaActiva = false;
                    this.estaCompletada = false;
                    this.estaCancelada = false;
                }
            }
        }

        if (fechaInicio != null && fechaFin != null) {
            int dias = (int) java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
            if (dias < 0) {
                dias = 0;
            }
            this.duracionDias = dias;
            
            if (dias == 0) {
                this.duracionFormateada = "Menos de 1 día";
            } else if (dias == 1) {
                this.duracionFormateada = "1 día";
            } else {
                this.duracionFormateada = dias + " días";
            }
        } else {
            this.duracionDias = 0;
            this.duracionFormateada = "-";
        }

        if (fechaInicio == null && fechaFin == null) {
            this.rangoFechasLegible = "Sin fechas";
        } else if (fechaInicio == null) {
            this.rangoFechasLegible = "Hasta " + getFechaFinFormateada();
        } else if (fechaFin == null) {
            this.rangoFechasLegible = "Desde " + getFechaInicioFormateada();
        } else {
            this.rangoFechasLegible = getFechaInicioFormateada() + " - " + getFechaFinFormateada();
        }

        if (nombreProducto != null && codigoSerial != null) {
            this.equipoLegible = nombreProducto + " (" + codigoSerial + ")";
        } else if (nombreProducto != null) {
            this.equipoLegible = nombreProducto;
        } else if (codigoSerial != null) {
            this.equipoLegible = codigoSerial;
        } else {
            this.equipoLegible = "Equipo ID: " + idInventario;
        }

        if (nombreProyecto != null && codigoProyecto != null) {
            this.proyectolegible = codigoProyecto + " - " + nombreProyecto;
        } else if (nombreProyecto != null) {
            this.proyectolegible = nombreProyecto;
        } else {
            this.proyectolegible = "Proyecto ID: " + idProyecto;
        }

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

    public boolean isActiva() {
        return estaActiva != null && estaActiva;
    }

    public boolean isCompletada() {
        return estaCompletada != null && estaCompletada;
    }

    public boolean isCancelada() {
        return estaCancelada != null && estaCancelada;
    }

    public boolean isVigente() {
        return estaVigente != null && estaVigente;
    }

    public boolean tieneNotas() {
        return notas != null && !notas.trim().isEmpty();
    }

    public boolean seSolapaCon(LocalDate otraFechaInicio, LocalDate otraFechaFin) {
        if (fechaInicio == null || fechaFin == null || 
            otraFechaInicio == null || otraFechaFin == null) {
            return false;
        }
        return !(fechaFin.isBefore(otraFechaInicio) || fechaInicio.isAfter(otraFechaFin));
    }

    public String getEstadoLegible() {
        return estadoReservaLegible != null ? estadoReservaLegible : estadoReserva;
    }

    public String getEstadoConIcono() {
        return "<i class='" + iconoEstado + "'></i> " + getEstadoLegible();
    }

    public String getFechaInicioFormateada() {
        if (fechaInicio == null) return "-";
        return fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getFechaFinFormateada() {
        if (fechaFin == null) return "-";
        return fechaFin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getFechaReservaFormateada() {
        if (fechaReserva == null) return "-";
        return fechaReserva.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getProyectoLegible() {
        return proyectolegible != null ? proyectolegible : "Proyecto ID: " + idProyecto;
    }

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
        dto.setNombreProducto(reserva.getNombreProducto());
        dto.setCodigoSerial(reserva.getCodigoSerial());
        dto.setNombreProyecto(reserva.getNombreProyecto());
        dto.setCodigoProyecto(reserva.getCodigoProyecto());
        dto.setEstadoProyecto(reserva.getEstadoProyecto());
        dto.actualizarCamposFormateados();
        return dto;
    }

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
}