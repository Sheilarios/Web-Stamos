package com.stamos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Usuario",
       indexes = {
           @Index(name = "idx_usuario_email", columnList = "email", unique = true),
           @Index(name = "idx_usuario_rol", columnList = "id_rol"),
           @Index(name = "idx_usuario_activo", columnList = "activo")
       })
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @NotNull(message = "El rol es obligatorio")
    @Column(name = "id_rol", nullable = false)
    private Integer idRol;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder los 150 caracteres")
    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(max = 255, message = "La contraseña no puede exceder los 255 caracteres")
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres")
    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "id_usuario_creacion")
    private Integer idUsuarioCreacion;

    @Column(name = "id_usuario_actualizacion")
    private Integer idUsuarioActualizacion;

    @Transient
    private String nombreRol;

    @Transient
    private String nombreRolLegible;

    @Transient
    private String colorRol;

    @Transient
    private String iconoRol;

    @Transient
    private String estadoLegible;

    @Transient
    private String estadoBadgeClass;

    @Transient
    private String estadoIcono;

    @Transient
    private String estadoColor;

    @Transient
    private List<String> permisos = new ArrayList<>();

    public Usuario() {
        this.activo = true;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        actualizarCamposTransient();
    }

    public Usuario(Integer idRol, String nombreCompleto, String email, String passwordHash) {
        this();
        this.idRol = idRol;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.passwordHash = passwordHash;
        actualizarCamposTransient();
    }

    public Usuario(Integer idRol, String nombreCompleto, String email, 
                   String passwordHash, String telefono) {
        this(idRol, nombreCompleto, email, passwordHash);
        this.telefono = telefono;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
        actualizarCamposTransient();
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
        actualizarCamposTransient();
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
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

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public String getNombreRolLegible() {
        return nombreRolLegible;
    }

    public void setNombreRolLegible(String nombreRolLegible) {
        this.nombreRolLegible = nombreRolLegible;
    }

    public String getColorRol() {
        return colorRol;
    }

    public void setColorRol(String colorRol) {
        this.colorRol = colorRol;
    }

    public String getIconoRol() {
        return iconoRol;
    }

    public void setIconoRol(String iconoRol) {
        this.iconoRol = iconoRol;
    }

    public String getEstadoLegible() {
        return estadoLegible;
    }

    public void setEstadoLegible(String estadoLegible) {
        this.estadoLegible = estadoLegible;
    }

    public String getEstadoBadgeClass() {
        return estadoBadgeClass;
    }

    public void setEstadoBadgeClass(String estadoBadgeClass) {
        this.estadoBadgeClass = estadoBadgeClass;
    }

    public String getEstadoIcono() {
        return estadoIcono;
    }

    public void setEstadoIcono(String estadoIcono) {
        this.estadoIcono = estadoIcono;
    }

    public String getEstadoColor() {
        return estadoColor;
    }

    public void setEstadoColor(String estadoColor) {
        this.estadoColor = estadoColor;
    }

    public List<String> getPermisos() {
        return permisos;
    }

    public void setPermisos(List<String> permisos) {
        this.permisos = permisos;
    }

    private void actualizarCamposTransient() {
        if (activo != null) {
            if (activo) {
                this.estadoLegible = "Activo";
                this.estadoBadgeClass = "badge-success";
                this.estadoIcono = "ti ti-check-circle";
                this.estadoColor = "#10B981";
            } else {
                this.estadoLegible = "Inactivo";
                this.estadoBadgeClass = "badge-danger";
                this.estadoIcono = "ti ti-x-circle";
                this.estadoColor = "#EF4444";
            }
        }
    }


    public void setRol(Rol rol) {
        if (rol != null) {
            this.idRol = rol.getIdRol();
            this.nombreRol = rol.getNombreRol();
            this.nombreRolLegible = rol.getNombreLegible();
            this.colorRol = rol.getColor();
            this.iconoRol = rol.getIcono();
            actualizarCamposTransient();
        }
    }


    public boolean isActivo() {
        return activo != null && activo;
    }

    public void activar() {
        this.activo = true;
        this.fechaActualizacion = LocalDateTime.now();
        actualizarCamposTransient();
    }

    public void desactivar() {
        this.activo = false;
        this.fechaActualizacion = LocalDateTime.now();
        actualizarCamposTransient();
    }

    public void toggleActivo() {
        this.activo = !isActivo();
        this.fechaActualizacion = LocalDateTime.now();
        actualizarCamposTransient();
    }

    public void registrarAcceso() {
        this.ultimoAcceso = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    public boolean esAdministrador() {
        return Rol.ROL_ADMINISTRADOR.equals(this.nombreRol);
    }


    public boolean esVendedor() {
        return Rol.ROL_VENDEDOR.equals(this.nombreRol);
    }

    public boolean esDiseñador() {
        return Rol.ROL_DISEÑADOR.equals(this.nombreRol);
    }

    public boolean esProduccion() {
        return Rol.ROL_PRODUCCION.equals(this.nombreRol);
    }

    public boolean esSoporteTecnico() {
        return Rol.ROL_SOPORTE_TECNICO.equals(this.nombreRol);
    }

    public boolean esCliente() {
        return Rol.ROL_CLIENTE.equals(this.nombreRol);
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

    public void agregarPermiso(String permiso) {
        if (permiso != null && this.permisos != null) {
            if (!this.permisos.contains(permiso)) {
                this.permisos.add(permiso);
            }
        }
    }

    public boolean tienePermiso(String permiso) {
        return this.permisos != null && this.permisos.contains(permiso);
    }

    public String getNombreUsuario() {
        return nombreCompleto;
    }

    public String getRolSeguridad() {
        if (nombreRol == null) return "ROLE_USER";
        return "ROLE_" + nombreRol.toUpperCase().replace(" ", "_").replace("Ó", "O");
    }

    public String getEstadoConIcono() {
        return "<i class='" + estadoIcono + "'></i> " + getEstadoLegible();
    }

    public String getRolConIcono() {
        return "<i class='" + iconoRol + "'></i> " + getNombreRolLegible();
    }


    public String getRolBadgeClass() {
        if (nombreRol == null) return "badge-secondary";
        
        return switch (nombreRol) {
            case Rol.ROL_ADMINISTRADOR -> "badge-danger";
            case Rol.ROL_VENDEDOR -> "badge-primary";
            case Rol.ROL_DISEÑADOR -> "badge-purple";
            case Rol.ROL_PRODUCCION -> "badge-warning";
            case Rol.ROL_SOPORTE_TECNICO -> "badge-success";
            case Rol.ROL_CLIENTE -> "badge-secondary";
            default -> "badge-secondary";
        };
    }

    public String getFechaCreacionFormateada() {
        if (fechaCreacion == null) return "-";
        return fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getFechaCreacionSoloFecha() {
        if (fechaCreacion == null) return "-";
        return fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getUltimoAccesoFormateado() {
        if (ultimoAcceso == null) return "-";
        return ultimoAcceso.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getFechaActualizacionFormateada() {
        if (fechaActualizacion == null) return "-";
        return fechaActualizacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append(nombreCompleto);
        sb.append(" (").append(getNombreRolLegible()).append(")");
        if (!isActivo()) {
            sb.append(" - Inactivo");
        }
        return sb.toString();
    }

    public String getNombreTruncado(int maxLength) {
        if (nombreCompleto == null) return "";
        if (nombreCompleto.length() <= maxLength) return nombreCompleto;
        return nombreCompleto.substring(0, maxLength) + "...";
    }

    public boolean tieneTelefono() {
        return telefono != null && !telefono.trim().isEmpty();
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"idUsuario\":").append(idUsuario).append(",");
        sb.append("\"nombre\":\"").append(nombreCompleto != null ? nombreCompleto : "").append("\",");
        sb.append("\"email\":\"").append(email != null ? email : "").append("\",");
        sb.append("\"rol\":\"").append(getNombreRolLegible()).append("\",");
        sb.append("\"rolColor\":\"").append(colorRol != null ? colorRol : "#6B7280").append("\",");
        sb.append("\"rolIcono\":\"").append(iconoRol != null ? iconoRol : "ti ti-user").append("\",");
        sb.append("\"activo\":").append(isActivo()).append(",");
        sb.append("\"estado\":\"").append(getEstadoLegible()).append("\"");
        if (ultimoAcceso != null) {
            sb.append(",\"ultimoAcceso\":\"").append(getUltimoAccesoFormateado()).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

    public String toJsonCompleto() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"idUsuario\":").append(idUsuario).append(",");
        sb.append("\"idRol\":").append(idRol).append(",");
        sb.append("\"nombreCompleto\":\"").append(nombreCompleto != null ? nombreCompleto : "").append("\",");
        sb.append("\"email\":\"").append(email != null ? email : "").append("\",");
        sb.append("\"telefono\":\"").append(telefono != null ? telefono : "").append("\",");
        sb.append("\"rol\":\"").append(getNombreRolLegible()).append("\",");
        sb.append("\"rolColor\":\"").append(colorRol != null ? colorRol : "#6B7280").append("\",");
        sb.append("\"rolIcono\":\"").append(iconoRol != null ? iconoRol : "ti ti-user").append("\",");
        sb.append("\"activo\":").append(isActivo()).append(",");
        sb.append("\"estado\":\"").append(getEstadoLegible()).append("\",");
        sb.append("\"fechaCreacion\":\"").append(getFechaCreacionFormateada()).append("\"");
        if (ultimoAcceso != null) {
            sb.append(",\"ultimoAcceso\":\"").append(getUltimoAccesoFormateado()).append("\"");
        }
        if (permisos != null && !permisos.isEmpty()) {
            sb.append(",\"permisos\":[");
            for (int i = 0; i < permisos.size(); i++) {
                sb.append("\"").append(permisos.get(i)).append("\"");
                if (i < permisos.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("]");
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", idRol=" + idRol +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", email='" + email + '\'' +
                ", activo=" + activo +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return idUsuario != null && idUsuario.equals(usuario.idUsuario);
    }

    @Override
    public int hashCode() {
        return idUsuario != null ? idUsuario.hashCode() : 0;
    }

    public static Usuario crearAdministrador(String nombreCompleto, String email, String passwordHash) {
        return new Usuario(1, nombreCompleto, email, passwordHash);
    }

    public static Usuario crearVendedor(String nombreCompleto, String email, String passwordHash) {
        return new Usuario(2, nombreCompleto, email, passwordHash);
    }

    public static Usuario crearDiseñador(String nombreCompleto, String email, String passwordHash) {
        return new Usuario(3, nombreCompleto, email, passwordHash);
    }

    public static Usuario crearProduccion(String nombreCompleto, String email, String passwordHash) {
        return new Usuario(4, nombreCompleto, email, passwordHash);
    }

    public static Usuario crearSoporteTecnico(String nombreCompleto, String email, String passwordHash) {
        return new Usuario(5, nombreCompleto, email, passwordHash);
    }

    public static Usuario crearCliente(String nombreCompleto, String email, String passwordHash) {
        return new Usuario(6, nombreCompleto, email, passwordHash);
    }

    public static Usuario fromDTO(com.stamos.dto.UsuarioDTO dto) {
        if (dto == null) {
            return null;
        }
        Usuario usuario = new Usuario();
        if (dto.getIdUsuario() != null) {
            usuario.setIdUsuario(dto.getIdUsuario());
        }
        usuario.setIdRol(dto.getIdRol());
        usuario.setNombreCompleto(dto.getNombreCompleto());
        usuario.setEmail(dto.getEmail());
        usuario.setPasswordHash(dto.getPasswordHash());
        usuario.setTelefono(dto.getTelefono());
        usuario.setActivo(dto.getActivo());
        usuario.setUltimoAcceso(dto.getUltimoAcceso());
        return usuario;
    }

    public com.stamos.dto.UsuarioDTO toDTO() {
        com.stamos.dto.UsuarioDTO dto = new com.stamos.dto.UsuarioDTO();
        dto.setIdUsuario(this.idUsuario);
        dto.setIdRol(this.idRol);
        dto.setNombreCompleto(this.nombreCompleto);
        dto.setEmail(this.email);
        dto.setPasswordHash(this.passwordHash);
        dto.setTelefono(this.telefono);
        dto.setActivo(this.activo);
        dto.setFechaCreacion(this.fechaCreacion);
        dto.setUltimoAcceso(this.ultimoAcceso);
        dto.setNombreRol(this.nombreRol);
        dto.setNombreRolLegible(this.nombreRolLegible);
        dto.setColorRol(this.colorRol);
        dto.setIconoRol(this.iconoRol);
        dto.setEstadoLegible(this.estadoLegible);
        dto.setEstadoBadgeClass(this.estadoBadgeClass);
        dto.setEstadoIcono(this.estadoIcono);
        dto.setEstadoColor(this.estadoColor);
        return dto;
    }

    public Object getRol() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}