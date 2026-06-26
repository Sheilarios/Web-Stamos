package com.stamos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "EstadoProyecto",
       indexes = {
           @Index(name = "idx_estado_nombre", columnList = "nombre_estado", unique = true),
           @Index(name = "idx_estado_orden", columnList = "orden_flujo", unique = true)
       })
public class EstadoProyecto {

    public static final int ID_APROBADO = 1;
    public static final int ID_EN_DISENO = 2;
    public static final int ID_EN_PRODUCCION = 3;
    public static final int ID_INSTALACION_MONTAJE = 4;
    public static final int ID_ENTREGADO = 5;
    public static final int ID_ANULADO = 6;

    public static final String NOMBRE_APROBADO = "Aprobado";
    public static final String NOMBRE_EN_DISENO = "En Diseño";
    public static final String NOMBRE_EN_PRODUCCION = "En Producción";
    public static final String NOMBRE_INSTALACION_MONTAJE = "Instalación/Montaje";
    public static final String NOMBRE_ENTREGADO = "Entregado";
    public static final String NOMBRE_ANULADO = "Anulado";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado")
    private Integer idEstado;

    @NotBlank(message = "El nombre del estado es obligatorio")
    @Column(name = "nombre_estado", nullable = false, unique = true, length = 50)
    private String nombreEstado;

    @NotNull(message = "El orden del flujo es obligatorio")
    @Column(name = "orden_flujo", nullable = false, unique = true)
    private Integer ordenFlujo;

    @Size(max = 200, message = "La descripcion no puede exceder los 200 caracteres")
    @Column(name = "descripcion", length = 200)
    private String descripcion;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "id_usuario_creacion")
    private Integer idUsuarioCreacion;

    @Column(name = "id_usuario_actualizacion")
    private Integer idUsuarioActualizacion;

    @Transient
    private Boolean esEstadoInicial;

    @Transient
    private Boolean esEstadoFinal;

    @Transient
    private Boolean esEstadoActivo;

    @Transient
    private String color;

    @Transient
    private String icono;

    public EstadoProyecto() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        this.esEstadoInicial = false;
        this.esEstadoFinal = false;
        this.esEstadoActivo = true;
        this.color = "#6B7280";
        this.icono = "ti ti-circle";
    }

    public EstadoProyecto(String nombreEstado, Integer ordenFlujo, String descripcion) {
        this();
        this.nombreEstado = nombreEstado;
        this.ordenFlujo = ordenFlujo;
        this.descripcion = descripcion;
        actualizarCamposTransient();
    }

    public EstadoProyecto(Integer idEstado, String nombreEstado, Integer ordenFlujo, String descripcion) {
        this(nombreEstado, ordenFlujo, descripcion);
        this.idEstado = idEstado;
    }

    public Integer getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Integer idEstado) {
        this.idEstado = idEstado;
    }

    public String getNombreEstado() {
        return nombreEstado;
    }

    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
        actualizarCamposTransient();
    }

    public Integer getOrdenFlujo() {
        return ordenFlujo;
    }

    public void setOrdenFlujo(Integer ordenFlujo) {
        this.ordenFlujo = ordenFlujo;
        actualizarCamposTransient();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public Boolean getEsEstadoInicial() {
        return esEstadoInicial;
    }

    public void setEsEstadoInicial(Boolean esEstadoInicial) {
        this.esEstadoInicial = esEstadoInicial;
    }

    public Boolean getEsEstadoFinal() {
        return esEstadoFinal;
    }

    public void setEsEstadoFinal(Boolean esEstadoFinal) {
        this.esEstadoFinal = esEstadoFinal;
    }

    public Boolean getEsEstadoActivo() {
        return esEstadoActivo;
    }

    public void setEsEstadoActivo(Boolean esEstadoActivo) {
        this.esEstadoActivo = esEstadoActivo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    private void actualizarCamposTransient() {
        if (nombreEstado != null) {
            switch (nombreEstado) {
                case NOMBRE_APROBADO -> {
                    this.color = "#1A56DB";
                    this.icono = "ti ti-check-circle";
                    this.esEstadoInicial = true;
                    this.esEstadoFinal = false;
                    this.esEstadoActivo = true;
                }
                case NOMBRE_EN_DISENO -> {
                    this.color = "#F59E0B";
                    this.icono = "ti ti-pencil";
                    this.esEstadoInicial = false;
                    this.esEstadoFinal = false;
                    this.esEstadoActivo = true;
                }
                case NOMBRE_EN_PRODUCCION -> {
                    this.color = "#3B82F6";
                    this.icono = "ti ti-tools";
                    this.esEstadoInicial = false;
                    this.esEstadoFinal = false;
                    this.esEstadoActivo = true;
                }
                case NOMBRE_INSTALACION_MONTAJE -> {
                    this.color = "#8B5CF6";
                    this.icono = "ti ti-truck";
                    this.esEstadoInicial = false;
                    this.esEstadoFinal = false;
                    this.esEstadoActivo = true;
                }
                case NOMBRE_ENTREGADO -> {
                    this.color = "#10B981";
                    this.icono = "ti ti-check";
                    this.esEstadoInicial = false;
                    this.esEstadoFinal = true;
                    this.esEstadoActivo = false;
                }
                case NOMBRE_ANULADO -> {
                    this.color = "#EF4444";
                    this.icono = "ti ti-x-circle";
                    this.esEstadoInicial = false;
                    this.esEstadoFinal = true;
                    this.esEstadoActivo = false;
                }
                default -> {
                    this.color = "#6B7280";
                    this.icono = "ti ti-circle";
                    this.esEstadoInicial = false;
                    this.esEstadoFinal = false;
                    this.esEstadoActivo = true;
                }
            }
        }
    }

    public boolean esAprobado() {
        return NOMBRE_APROBADO.equals(this.nombreEstado);
    }

    public boolean esEnDiseno() {
        return NOMBRE_EN_DISENO.equals(this.nombreEstado);
    }

    public boolean esEnProduccion() {
        return NOMBRE_EN_PRODUCCION.equals(this.nombreEstado);
    }

    public boolean esInstalacionMontaje() {
        return NOMBRE_INSTALACION_MONTAJE.equals(this.nombreEstado);
    }

    public boolean esEntregado() {
        return NOMBRE_ENTREGADO.equals(this.nombreEstado);
    }

    public boolean esAnulado() {
        return NOMBRE_ANULADO.equals(this.nombreEstado);
    }

    public boolean esEstadoTerminal() {
        return esEntregado() || esAnulado();
    }

    public boolean esEstadoActivo() {
        return !esEstadoTerminal();
    }

    public boolean permiteTransicion() {
        return !esEstadoTerminal();
    }

    public boolean esAnteriorA(EstadoProyecto otro) {
        if (otro == null || this.ordenFlujo == null || otro.getOrdenFlujo() == null) {
            return false;
        }
        return this.ordenFlujo < otro.getOrdenFlujo();
    }

    public boolean esPosteriorA(EstadoProyecto otro) {
        if (otro == null || this.ordenFlujo == null || otro.getOrdenFlujo() == null) {
            return false;
        }
        return this.ordenFlujo > otro.getOrdenFlujo();
    }

    public String getEstadoSiguiente() {
        if (ordenFlujo == null) return null;
        
        return switch (ordenFlujo) {
            case 1 -> NOMBRE_EN_DISENO;
            case 2 -> NOMBRE_EN_PRODUCCION;
            case 3 -> NOMBRE_INSTALACION_MONTAJE;
            case 4 -> NOMBRE_ENTREGADO;
            default -> null;
        };
    }

    public String getEstadoAnterior() {
        if (ordenFlujo == null) return null;
        
        return switch (ordenFlujo) {
            case 2 -> NOMBRE_APROBADO;
            case 3 -> NOMBRE_EN_DISENO;
            case 4 -> NOMBRE_EN_PRODUCCION;
            case 5 -> NOMBRE_INSTALACION_MONTAJE;
            default -> null;
        };
    }

    public int getProgreso() {
        if (ordenFlujo == null) return 0;
        
        return switch (ordenFlujo) {
            case 1 -> 10;
            case 2 -> 30;
            case 3 -> 60;
            case 4 -> 85;
            case 5 -> 100;
            case 6 -> 0;
            default -> 0;
        };
    }

    public static EstadoProyecto crearAprobado() {
        return new EstadoProyecto(NOMBRE_APROBADO, 1, "Proyecto aprobado, pendiente de inicio");
    }

    public static EstadoProyecto crearEnDiseno() {
        return new EstadoProyecto(NOMBRE_EN_DISENO, 2, "En fase de diseño y planificacion");
    }

    public static EstadoProyecto crearEnProduccion() {
        return new EstadoProyecto(NOMBRE_EN_PRODUCCION, 3, "En fase de fabricacion");
    }

    public static EstadoProyecto crearInstalacionMontaje() {
        return new EstadoProyecto(NOMBRE_INSTALACION_MONTAJE, 4, "En fase de instalacion o montaje");
    }

    public static EstadoProyecto crearEntregado() {
        return new EstadoProyecto(NOMBRE_ENTREGADO, 5, "Proyecto completado y entregado");
    }

    public static EstadoProyecto crearAnulado() {
        return new EstadoProyecto(NOMBRE_ANULADO, 6, "Proyecto cancelado");
    }

    public static java.util.List<EstadoProyecto> obtenerEstadosPredefinidos() {
        return java.util.Arrays.asList(
            crearAprobado(),
            crearEnDiseno(),
            crearEnProduccion(),
            crearInstalacionMontaje(),
            crearEntregado(),
            crearAnulado()
        );
    }

    public String getNombreLegible() {
        if (nombreEstado == null) return "Sin estado";
        return nombreEstado;
    }

    public String getBadgeClass() {
        if (nombreEstado == null) return "status-pendiente";
        
        return switch (nombreEstado) {
            case NOMBRE_APROBADO -> "status-aprobado";
            case NOMBRE_EN_DISENO -> "status-diseno";
            case NOMBRE_EN_PRODUCCION -> "status-produccion";
            case NOMBRE_INSTALACION_MONTAJE -> "status-instalacion";
            case NOMBRE_ENTREGADO -> "status-entregado";
            case NOMBRE_ANULADO -> "status-anulado";
            default -> "status-pendiente";
        };
    }

    public String getColorHex() {
        if (color == null) return "#6B7280";
        return color;
    }

    public String getNombreConIcono() {
        return "<i class='" + icono + "'></i> " + nombreEstado;
    }

    public String getResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append(nombreEstado);
        if (ordenFlujo != null) {
            sb.append(" (Orden: ").append(ordenFlujo).append(")");
        }
        if (descripcion != null && !descripcion.isEmpty()) {
            sb.append(" - ").append(descripcion);
        }
        return sb.toString();
    }

    public String getOrdenFormateado() {
        if (ordenFlujo == null) return "-";
        return "#" + ordenFlujo;
    }

    public String getProgresoFormateado() {
        return getProgreso() + "%";
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"idEstado\":").append(idEstado).append(",");
        sb.append("\"nombre\":\"").append(nombreEstado != null ? nombreEstado : "").append("\",");
        sb.append("\"orden\":").append(ordenFlujo != null ? ordenFlujo : 0).append(",");
        sb.append("\"color\":\"").append(color != null ? color : "#6B7280").append("\",");
        sb.append("\"icono\":\"").append(icono != null ? icono : "ti ti-circle").append("\"");
        if (descripcion != null) {
            sb.append(",\"descripcion\":\"").append(descripcion).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

    public String toJsonCompleto() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"idEstado\":").append(idEstado).append(",");
        sb.append("\"nombre\":\"").append(nombreEstado != null ? nombreEstado : "").append("\",");
        sb.append("\"orden\":").append(ordenFlujo != null ? ordenFlujo : 0).append(",");
        sb.append("\"descripcion\":\"").append(descripcion != null ? descripcion : "").append("\",");
        sb.append("\"color\":\"").append(color != null ? color : "#6B7280").append("\",");
        sb.append("\"icono\":\"").append(icono != null ? icono : "ti ti-circle").append("\",");
        sb.append("\"esEstadoInicial\":").append(esEstadoInicial != null && esEstadoInicial).append(",");
        sb.append("\"esEstadoFinal\":").append(esEstadoFinal != null && esEstadoFinal).append(",");
        sb.append("\"esEstadoActivo\":").append(esEstadoActivo != null && esEstadoActivo).append(",");
        sb.append("\"progreso\":").append(getProgreso());
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "EstadoProyecto{" +
                "idEstado=" + idEstado +
                ", nombreEstado='" + nombreEstado + '\'' +
                ", ordenFlujo=" + ordenFlujo +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EstadoProyecto that = (EstadoProyecto) o;
        return idEstado != null && idEstado.equals(that.idEstado);
    }

    @Override
    public int hashCode() {
        return idEstado != null ? idEstado.hashCode() : 0;
    }

    public EstadoProyectoDTO toDTO() {
        EstadoProyectoDTO dto = new EstadoProyectoDTO();
        dto.setIdEstado(this.idEstado);
        dto.setNombreEstado(this.nombreEstado);
        dto.setOrdenFlujo(this.ordenFlujo);
        dto.setDescripcion(this.descripcion);
        dto.setColor(this.color);
        dto.setIcono(this.icono);
        dto.setEsEstadoInicial(this.esEstadoInicial);
        dto.setEsEstadoFinal(this.esEstadoFinal);
        dto.setEsEstadoActivo(this.esEstadoActivo);
        dto.setProgreso(getProgreso());
        return dto;
    }

    public static EstadoProyecto fromDTO(EstadoProyectoDTO dto) {
        if (dto == null) {
            return null;
        }
        EstadoProyecto estado = new EstadoProyecto();
        estado.setIdEstado(dto.getIdEstado());
        estado.setNombreEstado(dto.getNombreEstado());
        estado.setOrdenFlujo(dto.getOrdenFlujo());
        estado.setDescripcion(dto.getDescripcion());
        return estado;
    }

    public static class EstadoProyectoDTO {
        private Integer idEstado;
        private String nombreEstado;
        private Integer ordenFlujo;
        private String descripcion;
        private String color;
        private String icono;
        private Boolean esEstadoInicial;
        private Boolean esEstadoFinal;
        private Boolean esEstadoActivo;
        private Integer progreso;

        public EstadoProyectoDTO() {}

        public EstadoProyectoDTO(Integer idEstado, String nombreEstado, Integer ordenFlujo, String descripcion) {
            this.idEstado = idEstado;
            this.nombreEstado = nombreEstado;
            this.ordenFlujo = ordenFlujo;
            this.descripcion = descripcion;
        }

        public Integer getIdEstado() { return idEstado; }
        public void setIdEstado(Integer idEstado) { this.idEstado = idEstado; }

        public String getNombreEstado() { return nombreEstado; }
        public void setNombreEstado(String nombreEstado) { this.nombreEstado = nombreEstado; }

        public Integer getOrdenFlujo() { return ordenFlujo; }
        public void setOrdenFlujo(Integer ordenFlujo) { this.ordenFlujo = ordenFlujo; }

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }

        public String getIcono() { return icono; }
        public void setIcono(String icono) { this.icono = icono; }

        public Boolean getEsEstadoInicial() { return esEstadoInicial; }
        public void setEsEstadoInicial(Boolean esEstadoInicial) { this.esEstadoInicial = esEstadoInicial; }

        public Boolean getEsEstadoFinal() { return esEstadoFinal; }
        public void setEsEstadoFinal(Boolean esEstadoFinal) { this.esEstadoFinal = esEstadoFinal; }

        public Boolean getEsEstadoActivo() { return esEstadoActivo; }
        public void setEsEstadoActivo(Boolean esEstadoActivo) { this.esEstadoActivo = esEstadoActivo; }

        public Integer getProgreso() { return progreso; }
        public void setProgreso(Integer progreso) { this.progreso = progreso; }

        public String getBadgeClass() {
            if (nombreEstado == null) return "status-pendiente";
            return switch (nombreEstado) {
                case EstadoProyecto.NOMBRE_APROBADO -> "status-aprobado";
                case EstadoProyecto.NOMBRE_EN_DISENO -> "status-diseno";
                case EstadoProyecto.NOMBRE_EN_PRODUCCION -> "status-produccion";
                case EstadoProyecto.NOMBRE_INSTALACION_MONTAJE -> "status-instalacion";
                case EstadoProyecto.NOMBRE_ENTREGADO -> "status-entregado";
                case EstadoProyecto.NOMBRE_ANULADO -> "status-anulado";
                default -> "status-pendiente";
            };
        }

        public String getNombreLegible() {
            return nombreEstado != null ? nombreEstado : "Sin estado";
        }

        public String getNombreConIcono() {
            return "<i class='" + icono + "'></i> " + nombreEstado;
        }

        @Override
        public String toString() {
            return "EstadoProyectoDTO{" +
                    "idEstado=" + idEstado +
                    ", nombreEstado='" + nombreEstado + '\'' +
                    ", ordenFlujo=" + ordenFlujo +
                    '}';
        }
    }
}