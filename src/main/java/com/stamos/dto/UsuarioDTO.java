package com.stamos.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDTO {

    private Integer idUsuario;
    private Integer idRol;
    private String nombreCompleto;
    private String email;
    private String passwordHash;
    private String telefono;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private LocalDateTime ultimoAcceso;
    private Integer idUsuarioCreacion;
    private Integer idUsuarioActualizacion;
    private String nombreUsuarioCreacion;
    private String nombreUsuarioActualizacion;
    private String nombreRol;
    private String nombreRolLegible;
    private String colorRol;
    private String iconoRol;
    private String estadoLegible;
    private String estadoBadgeClass;
    private String estadoIcono;
    private String estadoColor;
    private List<String> permisos = new ArrayList<>();
    private String nombreCompletoTruncado;
    private String fechaCreacionFormateada;
    private String ultimoAccesoFormateado;
    private String resumen;

    public UsuarioDTO() {
        this.activo = true;
        this.estadoLegible = "Activo";
        this.estadoBadgeClass = "badge-success";
        this.estadoIcono = "ti ti-check-circle";
        this.estadoColor = "#10B981";
        this.permisos = new ArrayList<>();
    }

    public UsuarioDTO(Integer idUsuario, String nombreCompleto, String email, Boolean activo) {
        this();
        this.idUsuario = idUsuario;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.activo = activo;
        actualizarCamposFormateados();
    }

    public UsuarioDTO(Integer idUsuario, Integer idRol, String nombreCompleto,
                     String email, String telefono, Boolean activo, String nombreRol) {
        this(idUsuario, nombreCompleto, email, activo);
        this.idRol = idRol;
        this.telefono = telefono;
        this.nombreRol = nombreRol;
        actualizarCamposFormateados();
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
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
        actualizarCamposFormateados();
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
        actualizarCamposFormateados();
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
        actualizarCamposFormateados();
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
        actualizarCamposFormateados();
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

    public String getNombreUsuarioCreacion() {
        return nombreUsuarioCreacion;
    }

    public void setNombreUsuarioCreacion(String nombreUsuarioCreacion) {
        this.nombreUsuarioCreacion = nombreUsuarioCreacion;
    }

    public String getNombreUsuarioActualizacion() {
        return nombreUsuarioActualizacion;
    }

    public void setNombreUsuarioActualizacion(String nombreUsuarioActualizacion) {
        this.nombreUsuarioActualizacion = nombreUsuarioActualizacion;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
        actualizarCamposFormateados();
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

    public String getNombreCompletoTruncado() {
        return nombreCompletoTruncado;
    }

    public void setNombreCompletoTruncado(String nombreCompletoTruncado) {
        this.nombreCompletoTruncado = nombreCompletoTruncado;
    }

    public String getFechaCreacionFormateada() {
        return fechaCreacionFormateada;
    }

    public void setFechaCreacionFormateada(String fechaCreacionFormateada) {
        this.fechaCreacionFormateada = fechaCreacionFormateada;
    }

    public String getUltimoAccesoFormateado() {
        return ultimoAccesoFormateado;
    }

    public void setUltimoAccesoFormateado(String ultimoAccesoFormateado) {
        this.ultimoAccesoFormateado = ultimoAccesoFormateado;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    private void actualizarCamposFormateados() {
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

        if (nombreRol != null) {
            switch (nombreRol) {
                case "Administrador" -> {
                    this.nombreRolLegible = "Administrador";
                    this.colorRol = "#EF4444";
                    this.iconoRol = "ti ti-shield";
                }
                case "Vendedor" -> {
                    this.nombreRolLegible = "Vendedor";
                    this.colorRol = "#3B82F6";
                    this.iconoRol = "ti ti-user-check";
                }
                case "Diseñador" -> {
                    this.nombreRolLegible = "Diseñador";
                    this.colorRol = "#8B5CF6";
                    this.iconoRol = "ti ti-pencil";
                }
                case "Producción" -> {
                    this.nombreRolLegible = "Producción";
                    this.colorRol = "#F59E0B";
                    this.iconoRol = "ti ti-tools";
                }
                case "Soporte Técnico" -> {
                    this.nombreRolLegible = "Soporte Técnico";
                    this.colorRol = "#10B981";
                    this.iconoRol = "ti ti-headset";
                }
                case "Cliente" -> {
                    this.nombreRolLegible = "Cliente";
                    this.colorRol = "#6B7280";
                    this.iconoRol = "ti ti-user";
                }
                default -> {
                    this.nombreRolLegible = nombreRol;
                    this.colorRol = "#6B7280";
                    this.iconoRol = "ti ti-user";
                }
            }
        }

        if (nombreCompleto != null) {
            if (nombreCompleto.length() > 30) {
                this.nombreCompletoTruncado = nombreCompleto.substring(0, 30) + "...";
            } else {
                this.nombreCompletoTruncado = nombreCompleto;
            }
        }

        if (fechaCreacion != null) {
            this.fechaCreacionFormateada = fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } else {
            this.fechaCreacionFormateada = "-";
        }

        if (ultimoAcceso != null) {
            this.ultimoAccesoFormateado = ultimoAcceso.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } else {
            this.ultimoAccesoFormateado = "-";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(nombreCompleto != null ? nombreCompleto : "Sin nombre");
        sb.append(" (").append(getNombreRolLegible()).append(")");
        if (!isActivo()) {
            sb.append(" - Inactivo");
        }
        this.resumen = sb.toString();
    }

    public boolean isActivo() {
        return activo != null && activo;
    }

    public boolean esAdministrador() {
        return "Administrador".equals(this.nombreRol);
    }

    public boolean esVendedor() {
        return "Vendedor".equals(this.nombreRol);
    }

    public boolean esDiseñador() {
        return "Diseñador".equals(this.nombreRol);
    }

    public boolean esProduccion() {
        return "Producción".equals(this.nombreRol);
    }

    public boolean esSoporteTecnico() {
        return "Soporte Técnico".equals(this.nombreRol);
    }

    public boolean esCliente() {
        return "Cliente".equals(this.nombreRol);
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

    public String getRolSeguridad() {
        if (nombreRol == null) return "ROLE_USER";
        return "ROLE_" + nombreRol.toUpperCase().replace(" ", "_").replace("Ó", "O");
    }

    public boolean tieneTelefono() {
        return telefono != null && !telefono.trim().isEmpty();
    }

    public String getNombreTruncado(int maxLength) {
        if (nombreCompleto == null) return "";
        if (nombreCompleto.length() <= maxLength) return nombreCompleto;
        return nombreCompleto.substring(0, maxLength) + "...";
    }

    public String getEmailTruncado(int maxLength) {
        if (email == null) return "";
        if (email.length() <= maxLength) return email;
        return email.substring(0, maxLength) + "...";
    }

    public static UsuarioDTO fromEntity(com.stamos.model.Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setIdRol(usuario.getIdRol());
        dto.setNombreCompleto(usuario.getNombreCompleto());
        dto.setEmail(usuario.getEmail());
        dto.setPasswordHash(usuario.getPasswordHash());
        dto.setTelefono(usuario.getTelefono());
        dto.setActivo(usuario.getActivo());
        dto.setFechaCreacion(usuario.getFechaCreacion());
        dto.setFechaActualizacion(usuario.getFechaActualizacion());
        dto.setUltimoAcceso(usuario.getUltimoAcceso());
        dto.setIdUsuarioCreacion(usuario.getIdUsuarioCreacion());
        dto.setIdUsuarioActualizacion(usuario.getIdUsuarioActualizacion());
        dto.setNombreRol(usuario.getNombreRol());
        dto.setNombreRolLegible(usuario.getNombreRolLegible());
        dto.setColorRol(usuario.getColorRol());
        dto.setIconoRol(usuario.getIconoRol());
        dto.setEstadoLegible(usuario.getEstadoLegible());
        dto.setEstadoBadgeClass(usuario.getEstadoBadgeClass());
        dto.setEstadoIcono(usuario.getEstadoIcono());
        dto.setEstadoColor(usuario.getEstadoColor());
        dto.actualizarCamposFormateados();
        return dto;
    }

    public static UsuarioDTO fromEntityWithPermisos(com.stamos.model.Usuario usuario, List<String> permisos) {
        UsuarioDTO dto = fromEntity(usuario);
        if (dto != null && permisos != null) {
            dto.setPermisos(permisos);
        }
        return dto;
    }

    public static UsuarioDTO crearNuevoUsuario(String nombreCompleto, String email, 
                                               Integer idRol, String telefono) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombreCompleto(nombreCompleto);
        dto.setEmail(email);
        dto.setIdRol(idRol);
        dto.setTelefono(telefono);
        dto.setActivo(true);
        dto.actualizarCamposFormateados();
        return dto;
    }

    public com.stamos.model.Usuario toEntity() {
        com.stamos.model.Usuario usuario = new com.stamos.model.Usuario();
        if (this.idUsuario != null) {
            usuario.setIdUsuario(this.idUsuario);
        }
        usuario.setIdRol(this.idRol);
        usuario.setNombreCompleto(this.nombreCompleto);
        usuario.setEmail(this.email);
        usuario.setPasswordHash(this.passwordHash);
        usuario.setTelefono(this.telefono);
        usuario.setActivo(this.activo);
        usuario.setFechaCreacion(this.fechaCreacion);
        usuario.setFechaActualizacion(this.fechaActualizacion);
        usuario.setUltimoAcceso(this.ultimoAcceso);
        usuario.setIdUsuarioCreacion(this.idUsuarioCreacion);
        usuario.setIdUsuarioActualizacion(this.idUsuarioActualizacion);
        return usuario;
    }

    @Override
    public String toString() {
        return "UsuarioDTO{" +
                "idUsuario=" + idUsuario +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", email='" + email + '\'' +
                ", rol='" + getNombreRolLegible() + '\'' +
                ", activo=" + activo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioDTO that = (UsuarioDTO) o;
        return idUsuario != null && idUsuario.equals(that.idUsuario);
    }

    @Override
    public int hashCode() {
        return idUsuario != null ? idUsuario.hashCode() : 0;
    }
}