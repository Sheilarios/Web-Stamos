package com.stamos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Rol",
       indexes = {
           @Index(name = "idx_rol_nombre", columnList = "nombre_rol", unique = true)
       })
public class Rol {

    public static final String ROL_ADMINISTRADOR = "Administrador";
    public static final String ROL_VENDEDOR = "Vendedor";
    public static final String ROL_DISEÑADOR = "Diseñador";
    public static final String ROL_PRODUCCION = "Producción";
    public static final String ROL_SOPORTE_TECNICO = "Soporte Técnico";
    public static final String ROL_CLIENTE = "Cliente";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Column(name = "nombre_rol", nullable = false, unique = true, length = 50)
    private String nombreRol;

    @Size(max = 200, message = "La descripción no puede exceder los 200 caracteres")
    @Column(name = "descripcion", length = 200)
    private String descripcion;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Transient
    private String nombreLegible;

    @Transient
    private String color;

    @Transient
    private String icono;

    @Transient
    private Integer cantidadUsuarios;

    @Transient
    private List<Usuario> usuarios = new ArrayList<>();

    public Rol() {
        this.fechaCreacion = LocalDateTime.now();
        actualizarCamposTransient();
    }

    public Rol(String nombreRol, String descripcion) {
        this();
        this.nombreRol = nombreRol;
        this.descripcion = descripcion;
        actualizarCamposTransient();
    }

    public Rol(Integer idRol, String nombreRol, String descripcion) {
        this(nombreRol, descripcion);
        this.idRol = idRol;
    }

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
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

    public String getNombreLegible() {
        return nombreLegible;
    }

    public void setNombreLegible(String nombreLegible) {
        this.nombreLegible = nombreLegible;
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

    public Integer getCantidadUsuarios() {
        return cantidadUsuarios;
    }

    public void setCantidadUsuarios(Integer cantidadUsuarios) {
        this.cantidadUsuarios = cantidadUsuarios;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    private void actualizarCamposTransient() {
        if (nombreRol != null) {
            switch (nombreRol) {
                case ROL_ADMINISTRADOR -> {
                    this.nombreLegible = "Administrador";
                    this.color = "#EF4444";
                    this.icono = "ti ti-shield";
                }
                case ROL_VENDEDOR -> {
                    this.nombreLegible = "Vendedor";
                    this.color = "#3B82F6";
                    this.icono = "ti ti-user-check";
                }
                case ROL_DISEÑADOR -> {
                    this.nombreLegible = "Diseñador";
                    this.color = "#8B5CF6";
                    this.icono = "ti ti-pencil";
                }
                case ROL_PRODUCCION -> {
                    this.nombreLegible = "Producción";
                    this.color = "#F59E0B";
                    this.icono = "ti ti-tools";
                }
                case ROL_SOPORTE_TECNICO -> {
                    this.nombreLegible = "Soporte Técnico";
                    this.color = "#10B981";
                    this.icono = "ti ti-headset";
                }
                case ROL_CLIENTE -> {
                    this.nombreLegible = "Cliente";
                    this.color = "#6B7280";
                    this.icono = "ti ti-user";
                }
                default -> {
                    this.nombreLegible = nombreRol;
                    this.color = "#6B7280";
                    this.icono = "ti ti-user";
                }
            }
        }
    }

    public boolean esAdministrador() {
        return ROL_ADMINISTRADOR.equals(this.nombreRol);
    }

    public boolean esVendedor() {
        return ROL_VENDEDOR.equals(this.nombreRol);
    }

    public boolean esDiseñador() {
        return ROL_DISEÑADOR.equals(this.nombreRol);
    }

    public boolean esProduccion() {
        return ROL_PRODUCCION.equals(this.nombreRol);
    }

    public boolean esSoporteTecnico() {
        return ROL_SOPORTE_TECNICO.equals(this.nombreRol);
    }

    public boolean esCliente() {
        return ROL_CLIENTE.equals(this.nombreRol);
    }

    public boolean esRolInterno() {
        return !esCliente();
    }

    public boolean tienePermisosAdmin() {
        return esAdministrador();
    }

    public boolean tienePermisosVentas() {
        return esAdministrador() || esVendedor();
    }

    public boolean tienePermisosDiseno() {
        return esAdministrador() || esDiseñador();
    }

    public boolean tienePermisosProduccion() {
        return esAdministrador() || esProduccion();
    }

    public boolean tienePermisosSoporte() {
        return esAdministrador() || esSoporteTecnico();
    }

    public boolean tienePermisosCliente() {
        return esAdministrador() || esCliente();
    }

    public void agregarUsuario(Usuario usuario) {
        if (usuario != null && this.usuarios != null) {
            this.usuarios.add(usuario);
            this.cantidadUsuarios = this.usuarios.size();
        }
    }

    public String getRolSeguridad() {
        return "ROLE_" + nombreRol.toUpperCase().replace(" ", "_").replace("Ó", "O");
    }

    public String getNombreParaVista() {
        return nombreLegible != null ? nombreLegible : nombreRol;
    }

    public String getRolBadgeClass() {
        if (nombreRol == null) return "badge-secondary";
        
        return switch (nombreRol) {
            case ROL_ADMINISTRADOR -> "badge-danger";
            case ROL_VENDEDOR -> "badge-primary";
            case ROL_DISEÑADOR -> "badge-purple";
            case ROL_PRODUCCION -> "badge-warning";
            case ROL_SOPORTE_TECNICO -> "badge-success";
            case ROL_CLIENTE -> "badge-secondary";
            default -> "badge-secondary";
        };
    }

    public String getRolConIcono() {
        return "<i class='" + icono + "'></i> " + getNombreLegible();
    }

    public String getColorClass() {
        if (nombreRol == null) return "text-secondary";
        
        return switch (nombreRol) {
            case ROL_ADMINISTRADOR -> "text-danger";
            case ROL_VENDEDOR -> "text-primary";
            case ROL_DISEÑADOR -> "text-purple";
            case ROL_PRODUCCION -> "text-warning";
            case ROL_SOPORTE_TECNICO -> "text-success";
            case ROL_CLIENTE -> "text-secondary";
            default -> "text-secondary";
        };
    }

    public String getResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append(getNombreLegible());
        if (descripcion != null && !descripcion.isEmpty()) {
            sb.append(" - ").append(descripcion);
        }
        if (cantidadUsuarios != null) {
            sb.append(" (").append(cantidadUsuarios).append(" usuarios)");
        }
        return sb.toString();
    }

    public String getFechaCreacionFormateada() {
        if (fechaCreacion == null) return "-";
        return fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getFechaCreacionSoloFecha() {
        if (fechaCreacion == null) return "-";
        return fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getColorHex() {
        return color != null ? color : "#6B7280";
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"idRol\":").append(idRol).append(",");
        sb.append("\"nombre\":\"").append(nombreRol != null ? nombreRol : "").append("\",");
        sb.append("\"nombreLegible\":\"").append(getNombreLegible()).append("\",");
        sb.append("\"color\":\"").append(color != null ? color : "#6B7280").append("\",");
        sb.append("\"icono\":\"").append(icono != null ? icono : "ti ti-user").append("\",");
        sb.append("\"descripcion\":\"").append(descripcion != null ? descripcion : "").append("\"");
        if (cantidadUsuarios != null) {
            sb.append(",\"cantidadUsuarios\":").append(cantidadUsuarios);
        }
        sb.append("}");
        return sb.toString();
    }

    public String toJsonCompleto() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"idRol\":").append(idRol).append(",");
        sb.append("\"nombre\":\"").append(nombreRol != null ? nombreRol : "").append("\",");
        sb.append("\"nombreLegible\":\"").append(getNombreLegible()).append("\",");
        sb.append("\"descripcion\":\"").append(descripcion != null ? descripcion : "").append("\",");
        sb.append("\"color\":\"").append(color != null ? color : "#6B7280").append("\",");
        sb.append("\"icono\":\"").append(icono != null ? icono : "ti ti-user").append("\",");
        sb.append("\"fechaCreacion\":\"").append(getFechaCreacionFormateada()).append("\",");
        sb.append("\"cantidadUsuarios\":").append(cantidadUsuarios != null ? cantidadUsuarios : 0);
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Rol{" +
                "idRol=" + idRol +
                ", nombreRol='" + nombreRol + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rol rol = (Rol) o;
        return idRol != null && idRol.equals(rol.idRol);
    }

    @Override
    public int hashCode() {
        return idRol != null ? idRol.hashCode() : 0;
    }

    public static Rol crearAdministrador() {
        return new Rol(ROL_ADMINISTRADOR, "Acceso total al sistema, gestión de usuarios y configuración");
    }

    public static Rol crearVendedor() {
        return new Rol(ROL_VENDEDOR, "Gestión de clientes y cotizaciones");
    }

    public static Rol crearDiseñador() {
        return new Rol(ROL_DISEÑADOR, "Diseño 3D, renders, planos y bocetos");
    }

    public static Rol crearProduccion() {
        return new Rol(ROL_PRODUCCION, "Fabricación y logística de stands");
    }

    public static Rol crearSoporteTecnico() {
        return new Rol(ROL_SOPORTE_TECNICO, "Configuración de equipos, montaje y entrega");
    }

    public static Rol crearCliente() {
        return new Rol(ROL_CLIENTE, "Consulta de proyectos y visualización de documentos");
    }

    public static java.util.List<Rol> obtenerRolesPredefinidos() {
        return java.util.Arrays.asList(
            crearAdministrador(),
            crearVendedor(),
            crearDiseñador(),
            crearProduccion(),
            crearSoporteTecnico(),
            crearCliente()
        );
    }

    public static Rol fromDTO(com.stamos.dto.RolDTO dto) {
        if (dto == null) {
            return null;
        }
        Rol rol = new Rol();
        if (dto.getIdRol() != null) {
            rol.setIdRol(dto.getIdRol());
        }
        rol.setNombreRol(dto.getNombreRol());
        rol.setDescripcion(dto.getDescripcion());
        return rol;
    }

    public com.stamos.dto.RolDTO toDTO() {
        com.stamos.dto.RolDTO dto = new com.stamos.dto.RolDTO();
        dto.setIdRol(this.idRol);
        dto.setNombreRol(this.nombreRol);
        dto.setDescripcion(this.descripcion);
        dto.setNombreLegible(this.nombreLegible);
        dto.setColor(this.color);
        dto.setIcono(this.icono);
        dto.setCantidadUsuarios(this.cantidadUsuarios);
        return dto;
    }
}