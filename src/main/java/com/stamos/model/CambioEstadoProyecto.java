package com.stamos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "CambioEstadoProyecto",
       indexes = {
           @Index(name = "idx_cambio_proyecto", columnList = "id_proyecto"),
           @Index(name = "idx_cambio_estado_anterior", columnList = "id_estado_anterior"),
           @Index(name = "idx_cambio_estado_nuevo", columnList = "id_estado_nuevo"),
           @Index(name = "idx_cambio_usuario", columnList = "id_usuario_responsable"),
           @Index(name = "idx_cambio_fecha", columnList = "fecha_cambio")
       })
public class CambioEstadoProyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cambio")
    private Integer idCambio;

    @NotNull(message = "El proyecto es obligatorio")
    @Column(name = "id_proyecto", nullable = false)
    private Integer idProyecto;

    @NotNull(message = "El estado anterior es obligatorio")
    @Column(name = "id_estado_anterior", nullable = false)
    private Integer idEstadoAnterior;

    @NotNull(message = "El estado nuevo es obligatorio")
    @Column(name = "id_estado_nuevo", nullable = false)
    private Integer idEstadoNuevo;

    @NotNull(message = "El usuario responsable es obligatorio")
    @Column(name = "id_usuario_responsable", nullable = false)
    private Integer idUsuarioResponsable;

    @Column(name = "fecha_cambio", nullable = false)
    private LocalDateTime fechaCambio;

    @Size(max = 500, message = "El comentario no puede exceder los 500 caracteres")
    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Transient
    private String nombreProyecto;

    @Transient
    private String estadoAnteriorNombre;

    @Transient
    private String estadoNuevoNombre;

    @Transient
    private String responsableNombre;

    @Transient
    private String responsableEmail;

    public CambioEstadoProyecto() {
        this.fechaCambio = LocalDateTime.now();
    }

    public CambioEstadoProyecto(Integer idProyecto, Integer idEstadoAnterior, 
                               Integer idEstadoNuevo, Integer idUsuarioResponsable) {
        this();
        this.idProyecto = idProyecto;
        this.idEstadoAnterior = idEstadoAnterior;
        this.idEstadoNuevo = idEstadoNuevo;
        this.idUsuarioResponsable = idUsuarioResponsable;
    }

    public CambioEstadoProyecto(Integer idProyecto, Integer idEstadoAnterior, 
                               Integer idEstadoNuevo, Integer idUsuarioResponsable, 
                               String comentario) {
        this(idProyecto, idEstadoAnterior, idEstadoNuevo, idUsuarioResponsable);
        this.comentario = comentario;
    }


    public Integer getIdCambio() {
        return idCambio;
    }

    public void setIdCambio(Integer idCambio) {
        this.idCambio = idCambio;
    }

    public Integer getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(Integer idProyecto) {
        this.idProyecto = idProyecto;
    }

    public Integer getIdEstadoAnterior() {
        return idEstadoAnterior;
    }

    public void setIdEstadoAnterior(Integer idEstadoAnterior) {
        this.idEstadoAnterior = idEstadoAnterior;
    }

    public Integer getIdEstadoNuevo() {
        return idEstadoNuevo;
    }

    public void setIdEstadoNuevo(Integer idEstadoNuevo) {
        this.idEstadoNuevo = idEstadoNuevo;
    }

    public Integer getIdUsuarioResponsable() {
        return idUsuarioResponsable;
    }

    public void setIdUsuarioResponsable(Integer idUsuarioResponsable) {
        this.idUsuarioResponsable = idUsuarioResponsable;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(LocalDateTime fechaCambio) {
        this.fechaCambio = fechaCambio;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getNombreProyecto() {
        return nombreProyecto;
    }

    public void setNombreProyecto(String nombreProyecto) {
        this.nombreProyecto = nombreProyecto;
    }

    public String getEstadoAnteriorNombre() {
        return estadoAnteriorNombre;
    }

    public void setEstadoAnteriorNombre(String estadoAnteriorNombre) {
        this.estadoAnteriorNombre = estadoAnteriorNombre;
    }

    public String getEstadoNuevoNombre() {
        return estadoNuevoNombre;
    }

    public void setEstadoNuevoNombre(String estadoNuevoNombre) {
        this.estadoNuevoNombre = estadoNuevoNombre;
    }

    public String getResponsableNombre() {
        return responsableNombre;
    }

    public void setResponsableNombre(String responsableNombre) {
        this.responsableNombre = responsableNombre;
    }

    public String getResponsableEmail() {
        return responsableEmail;
    }

    public void setResponsableEmail(String responsableEmail) {
        this.responsableEmail = responsableEmail;
    }

    public boolean esAvance() {
        if (idEstadoAnterior == null || idEstadoNuevo == null) {
            return false;
        }
        return idEstadoNuevo > idEstadoAnterior;
    }

    public boolean esRetroceso() {
        if (idEstadoAnterior == null || idEstadoNuevo == null) {
            return false;
        }
        return idEstadoNuevo < idEstadoAnterior;
    }


    public boolean esEntrega() {
        return "Entregado".equals(this.estadoNuevoNombre);
    }


    public boolean esAnulacion() {
        return "Anulado".equals(this.estadoNuevoNombre);
    }

    public boolean esInicioProyecto() {
        return "Aprobado".equals(this.estadoAnteriorNombre) && 
               "En Diseño".equals(this.estadoNuevoNombre);
    }


    public boolean esFinalizacionProyecto() {
        return "Instalación/Montaje".equals(this.estadoAnteriorNombre) && 
               "Entregado".equals(this.estadoNuevoNombre);
    }

    public boolean tieneComentario() {
        return comentario != null && !comentario.trim().isEmpty();
    }

    public String getTipoCambioLegible() {
        if (esInicioProyecto()) {
            return "Inicio de Proyecto";
        }
        if (esFinalizacionProyecto()) {
            return "Finalización de Proyecto";
        }
        if (esAnulacion()) {
            return "Anulación de Proyecto";
        }
        if (esAvance()) {
            return "Avance de Estado";
        }
        if (esRetroceso()) {
            return "Retroceso de Estado";
        }
        return "Cambio de Estado";
    }

    public String getTipoCambioBadgeClass() {
        if (esInicioProyecto()) {
            return "badge-success";
        }
        if (esFinalizacionProyecto()) {
            return "badge-primary";
        }
        if (esAnulacion()) {
            return "badge-danger";
        }
        if (esAvance()) {
            return "badge-info";
        }
        if (esRetroceso()) {
            return "badge-warning";
        }
        return "badge-secondary";
    }

    public String getTipoCambioIcono() {
        if (esInicioProyecto()) {
            return "ti ti-play";
        }
        if (esFinalizacionProyecto()) {
            return "ti ti-flag";
        }
        if (esAnulacion()) {
            return "ti ti-x-circle";
        }
        if (esAvance()) {
            return "ti ti-arrow-right";
        }
        if (esRetroceso()) {
            return "ti ti-arrow-left";
        }
        return "ti ti-arrows-exchange";
    }


    public String getFechaCambioFormateada() {
        if (fechaCambio == null) return "-";
        return fechaCambio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }


    public String getFechaFormateada() {
        if (fechaCambio == null) return "-";
        return fechaCambio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }


    public String getHoraFormateada() {
        if (fechaCambio == null) return "-";
        return fechaCambio.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }


    public String getEstadoAnteriorLegible() {
        if (estadoAnteriorNombre == null) return "-";
        return estadoAnteriorNombre;
    }


    public String getEstadoNuevoLegible() {
        if (estadoNuevoNombre == null) return "-";
        return estadoNuevoNombre;
    }


    public String getEstadoNuevoBadgeClass() {
        if (estadoNuevoNombre == null) return "status-pendiente";
        
        return switch (estadoNuevoNombre) {
            case "Aprobado" -> "status-aprobado";
            case "En Diseño" -> "status-diseno";
            case "En Producción" -> "status-produccion";
            case "Instalación/Montaje" -> "status-instalacion";
            case "Entregado" -> "status-entregado";
            case "Anulado" -> "status-anulado";
            default -> "status-pendiente";
        };
    }


    public String getEstadoNuevoColor() {
        if (estadoNuevoNombre == null) return "#6B7280";
        
        return switch (estadoNuevoNombre) {
            case "Aprobado" -> "#1A56DB";
            case "En Diseño" -> "#F59E0B";
            case "En Producción" -> "#3B82F6";
            case "Instalación/Montaje" -> "#8B5CF6";
            case "Entregado" -> "#10B981";
            case "Anulado" -> "#EF4444";
            default -> "#6B7280";
        };
    }


    public String getResponsableLegible() {
        if (responsableNombre != null && !responsableNombre.isEmpty()) {
            return responsableNombre;
        }
        if (responsableEmail != null && !responsableEmail.isEmpty()) {
            return responsableEmail;
        }
        return "Usuario ID: " + idUsuarioResponsable;
    }


    public String getResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cambio de estado");
        if (estadoAnteriorNombre != null && estadoNuevoNombre != null) {
            sb.append(" de '").append(estadoAnteriorNombre);
            sb.append("' a '").append(estadoNuevoNombre).append("'");
        }
        if (responsableNombre != null) {
            sb.append(" por ").append(responsableNombre);
        }
        return sb.toString();
    }


    public String getComentarioTruncado(int maxLength) {
        if (comentario == null) return "";
        if (comentario.length() <= maxLength) return comentario;
        return comentario.substring(0, maxLength) + "...";
    }


    public String getNumeroOrden(int index) {
        return "#" + (index + 1);
    }


    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"idCambio\":").append(idCambio).append(",");
        sb.append("\"idProyecto\":").append(idProyecto).append(",");
        sb.append("\"estadoAnterior\":\"").append(estadoAnteriorNombre != null ? estadoAnteriorNombre : "").append("\",");
        sb.append("\"estadoNuevo\":\"").append(estadoNuevoNombre != null ? estadoNuevoNombre : "").append("\",");
        sb.append("\"responsable\":\"").append(getResponsableLegible()).append("\",");
        sb.append("\"fechaCambio\":\"").append(getFechaCambioFormateada()).append("\"");
        if (tieneComentario()) {
            sb.append(",\"comentario\":\"").append(comentario).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }


    @Override
    public String toString() {
        return "CambioEstadoProyecto{" +
                "idCambio=" + idCambio +
                ", idProyecto=" + idProyecto +
                ", estadoAnterior='" + estadoAnteriorNombre + '\'' +
                ", estadoNuevo='" + estadoNuevoNombre + '\'' +
                ", responsable='" + getResponsableLegible() + '\'' +
                ", fechaCambio=" + fechaCambio +
                ", comentario='" + comentario + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CambioEstadoProyecto that = (CambioEstadoProyecto) o;
        return idCambio != null && idCambio.equals(that.idCambio);
    }

    @Override
    public int hashCode() {
        return idCambio != null ? idCambio.hashCode() : 0;
    }


    public static CambioEstadoProyecto crearInicioProyecto(Integer idProyecto, 
                                                          Integer idUsuarioResponsable,
                                                          String comentario) {
        return new CambioEstadoProyecto(idProyecto, 1, 2, idUsuarioResponsable, comentario);
    }


    public static CambioEstadoProyecto crearAvanceEstado(Integer idProyecto,
                                                        Integer idEstadoActual,
                                                        Integer idEstadoNuevo,
                                                        Integer idUsuarioResponsable,
                                                        String comentario) {
        return new CambioEstadoProyecto(idProyecto, idEstadoActual, idEstadoNuevo, 
                                       idUsuarioResponsable, comentario);
    }


    public static CambioEstadoProyecto crearAnulacionProyecto(Integer idProyecto,
                                                             Integer idEstadoActual,
                                                             Integer idUsuarioResponsable,
                                                             String comentario) {
        return new CambioEstadoProyecto(idProyecto, idEstadoActual, 6, 
                                       idUsuarioResponsable, comentario);
    }


    public static CambioEstadoProyecto crearEntregaProyecto(Integer idProyecto,
                                                           Integer idEstadoActual,
                                                           Integer idUsuarioResponsable,
                                                           String comentario) {
        return new CambioEstadoProyecto(idProyecto, idEstadoActual, 5, 
                                       idUsuarioResponsable, comentario);
    }


    public static CambioEstadoProyecto fromDTO(com.stamos.dto.ProyectoDTO.CambioEstadoDTO dto) {
        CambioEstadoProyecto cambio = new CambioEstadoProyecto();
        if (dto.getIdCambio() != null) {
            cambio.setIdCambio(dto.getIdCambio());
        }
        cambio.setIdProyecto(dto.getIdProyecto());
        cambio.setComentario(dto.getComentario());
        cambio.setEstadoAnteriorNombre(dto.getEstadoAnterior());
        cambio.setEstadoNuevoNombre(dto.getEstadoNuevo());
        return cambio;
    }
}