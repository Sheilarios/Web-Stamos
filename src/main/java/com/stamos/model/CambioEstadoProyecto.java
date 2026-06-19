package com.stamos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Entidad CambioEstadoProyecto.
 * 
 * <p>Esta entidad registra el historial de cambios de estado de un proyecto,
 * permitiendo la trazabilidad completa de su evolución a lo largo del tiempo.</p>
 * 
 * <p>Implementa los requerimientos:</p>
 * <ul>
 *   <li>RN-07: Los proyectos deben seguir la secuencia de estados</li>
 *   <li>RN-08: Proyectos en "Entregado" o "Anulado" no pueden retroceder</li>
 *   <li>RN-09: Registrar fecha y responsable de cada cambio</li>
 *   <li>RF-15: Registrar fecha, responsable y cambio de estado</li>
 * </ul>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
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

    // ============================================
    // ATRIBUTOS
    // ============================================

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

    // ============================================
    // CAMPOS ADICIONALES (NO EN BD)
    // ============================================

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

    // ============================================
    // CONSTRUCTORES
    // ============================================

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

    // ============================================
    // GETTERS Y SETTERS
    // ============================================

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

    // ============================================
    // MÉTODOS DE NEGOCIO
    // ============================================

    /**
     * Verifica si el cambio fue un avance en el flujo (hacia adelante).
     * 
     * @return true si fue un avance
     */
    public boolean esAvance() {
        if (idEstadoAnterior == null || idEstadoNuevo == null) {
            return false;
        }
        // Considerando que los IDs de estado están en orden secuencial
        return idEstadoNuevo > idEstadoAnterior;
    }

    /**
     * Verifica si el cambio fue un retroceso en el flujo (hacia atrás).
     * 
     * @return true si fue un retroceso
     */
    public boolean esRetroceso() {
        if (idEstadoAnterior == null || idEstadoNuevo == null) {
            return false;
        }
        return idEstadoNuevo < idEstadoAnterior;
    }

    /**
     * Verifica si el cambio fue a "Entregado".
     * 
     * @return true si el nuevo estado es "Entregado"
     */
    public boolean esEntrega() {
        return "Entregado".equals(this.estadoNuevoNombre);
    }

    /**
     * Verifica si el cambio fue a "Anulado".
     * 
     * @return true si el nuevo estado es "Anulado"
     */
    public boolean esAnulacion() {
        return "Anulado".equals(this.estadoNuevoNombre);
    }

    /**
     * Verifica si el cambio fue desde "Aprobado" a "En Diseño".
     * 
     * @return true si es el inicio del proyecto
     */
    public boolean esInicioProyecto() {
        return "Aprobado".equals(this.estadoAnteriorNombre) && 
               "En Diseño".equals(this.estadoNuevoNombre);
    }

    /**
     * Verifica si el cambio fue desde "Instalación/Montaje" a "Entregado".
     * 
     * @return true si es la finalización del proyecto
     */
    public boolean esFinalizacionProyecto() {
        return "Instalación/Montaje".equals(this.estadoAnteriorNombre) && 
               "Entregado".equals(this.estadoNuevoNombre);
    }

    /**
     * Verifica si el cambio tiene comentario.
     * 
     * @return true si tiene comentario
     */
    public boolean tieneComentario() {
        return comentario != null && !comentario.trim().isEmpty();
    }

    /**
     * Obtiene el tipo de cambio en formato legible.
     * 
     * @return Tipo de cambio
     */
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

    /**
     * Obtiene la clase CSS para el tipo de cambio.
     * 
     * @return Clase CSS
     */
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

    /**
     * Obtiene el ícono para el tipo de cambio.
     * 
     * @return Ícono CSS
     */
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

    // ============================================
    // MÉTODOS DE FORMATO PARA LA VISTA
    // ============================================

    /**
     * Obtiene la fecha de cambio formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaCambioFormateada() {
        if (fechaCambio == null) return "-";
        return fechaCambio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    /**
     * Obtiene la fecha de cambio formateada (solo fecha).
     * 
     * @return Fecha formateada
     */
    public String getFechaFormateada() {
        if (fechaCambio == null) return "-";
        return fechaCambio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Obtiene la hora de cambio formateada.
     * 
     * @return Hora formateada
     */
    public String getHoraFormateada() {
        if (fechaCambio == null) return "-";
        return fechaCambio.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    /**
     * Obtiene el estado anterior en formato legible.
     * 
     * @return Estado anterior legible
     */
    public String getEstadoAnteriorLegible() {
        if (estadoAnteriorNombre == null) return "-";
        return estadoAnteriorNombre;
    }

    /**
     * Obtiene el estado nuevo en formato legible.
     * 
     * @return Estado nuevo legible
     */
    public String getEstadoNuevoLegible() {
        if (estadoNuevoNombre == null) return "-";
        return estadoNuevoNombre;
    }

    /**
     * Obtiene la clase CSS para el badge del estado nuevo.
     * 
     * @return Clase CSS
     */
    public String getEstadoNuevoBadgeClass() {
        if (estadoNuevoNombre == null) return "status-pendiente";
        
        switch (estadoNuevoNombre) {
            case "Aprobado":
                return "status-aprobado";
            case "En Diseño":
                return "status-diseno";
            case "En Producción":
                return "status-produccion";
            case "Instalación/Montaje":
                return "status-instalacion";
            case "Entregado":
                return "status-entregado";
            case "Anulado":
                return "status-anulado";
            default:
                return "status-pendiente";
        }
    }

    /**
     * Obtiene el color para el estado nuevo.
     * 
     * @return Color en hexadecimal
     */
    public String getEstadoNuevoColor() {
        if (estadoNuevoNombre == null) return "#6B7280";
        
        switch (estadoNuevoNombre) {
            case "Aprobado":
                return "#1A56DB";
            case "En Diseño":
                return "#F59E0B";
            case "En Producción":
                return "#3B82F6";
            case "Instalación/Montaje":
                return "#8B5CF6";
            case "Entregado":
                return "#10B981";
            case "Anulado":
                return "#EF4444";
            default:
                return "#6B7280";
        }
    }

    /**
     * Obtiene el responsable en formato legible.
     * 
     * @return Responsable legible
     */
    public String getResponsableLegible() {
        if (responsableNombre != null && !responsableNombre.isEmpty()) {
            return responsableNombre;
        }
        if (responsableEmail != null && !responsableEmail.isEmpty()) {
            return responsableEmail;
        }
        return "Usuario ID: " + idUsuarioResponsable;
    }

    /**
     * Obtiene el resumen del cambio.
     * 
     * @return Resumen
     */
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

    /**
     * Obtiene el comentario truncado.
     * 
     * @param maxLength Longitud máxima
     * @return Comentario truncado
     */
    public String getComentarioTruncado(int maxLength) {
        if (comentario == null) return "";
        if (comentario.length() <= maxLength) return comentario;
        return comentario.substring(0, maxLength) + "...";
    }

    /**
     * Obtiene el número de orden del cambio en el historial.
     * 
     * @param index Índice en el historial
     * @return Número de orden
     */
    public String getNumeroOrden(int index) {
        return "#" + (index + 1);
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

    // ============================================
    // MÉTODOS OVERRIDE
    // ============================================

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

    // ============================================
    // MÉTODOS ESTÁTICOS DE FÁBRICA
    // ============================================

    /**
     * Crea un cambio de estado para el inicio de un proyecto.
     * 
     * @param idProyecto ID del proyecto
     * @param idUsuarioResponsable ID del usuario responsable
     * @param comentario Comentario opcional
     * @return Cambio de estado
     */
    public static CambioEstadoProyecto crearInicioProyecto(Integer idProyecto, 
                                                          Integer idUsuarioResponsable,
                                                          String comentario) {
        // ID de estado "Aprobado" = 1, "En Diseño" = 2
        return new CambioEstadoProyecto(idProyecto, 1, 2, idUsuarioResponsable, comentario);
    }

    /**
     * Crea un cambio de estado para avanzar al siguiente estado.
     * 
     * @param idProyecto ID del proyecto
     * @param idEstadoActual ID del estado actual
     * @param idEstadoNuevo ID del nuevo estado
     * @param idUsuarioResponsable ID del usuario responsable
     * @param comentario Comentario opcional
     * @return Cambio de estado
     */
    public static CambioEstadoProyecto crearAvanceEstado(Integer idProyecto,
                                                        Integer idEstadoActual,
                                                        Integer idEstadoNuevo,
                                                        Integer idUsuarioResponsable,
                                                        String comentario) {
        return new CambioEstadoProyecto(idProyecto, idEstadoActual, idEstadoNuevo, 
                                       idUsuarioResponsable, comentario);
    }

    /**
     * Crea un cambio de estado para anular un proyecto.
     * 
     * @param idProyecto ID del proyecto
     * @param idEstadoActual ID del estado actual
     * @param idUsuarioResponsable ID del usuario responsable
     * @param comentario Comentario opcional
     * @return Cambio de estado
     */
    public static CambioEstadoProyecto crearAnulacionProyecto(Integer idProyecto,
                                                             Integer idEstadoActual,
                                                             Integer idUsuarioResponsable,
                                                             String comentario) {
        // ID de estado "Anulado" = 6
        return new CambioEstadoProyecto(idProyecto, idEstadoActual, 6, 
                                       idUsuarioResponsable, comentario);
    }

    /**
     * Crea un cambio de estado para entregar un proyecto.
     * 
     * @param idProyecto ID del proyecto
     * @param idEstadoActual ID del estado actual
     * @param idUsuarioResponsable ID del usuario responsable
     * @param comentario Comentario opcional
     * @return Cambio de estado
     */
    public static CambioEstadoProyecto crearEntregaProyecto(Integer idProyecto,
                                                           Integer idEstadoActual,
                                                           Integer idUsuarioResponsable,
                                                           String comentario) {
        // ID de estado "Entregado" = 5
        return new CambioEstadoProyecto(idProyecto, idEstadoActual, 5, 
                                       idUsuarioResponsable, comentario);
    }

    /**
     * Crea un cambio de estado desde un DTO.
     * 
     * @param dto DTO del cambio de estado
     * @return Cambio de estado
     */
    public static CambioEstadoProyecto fromDTO(com.stamos.dto.ProyectoDTO.CambioEstadoDTO dto) {
        CambioEstadoProyecto cambio = new CambioEstadoProyecto();
        if (dto.getIdCambio() != null) {
            cambio.setIdCambio(dto.getIdCambio());
        }
        cambio.setIdProyecto(dto.getIdProyecto());
        cambio.setComentario(dto.getComentario());
        // Los IDs de estado se deben mapear desde los nombres
        cambio.setEstadoAnteriorNombre(dto.getEstadoAnterior());
        cambio.setEstadoNuevoNombre(dto.getEstadoNuevo());
        return cambio;
    }
}