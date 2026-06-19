package com.stamos.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO (Data Transfer Object) para la entidad Usuario.
 * 
 * <p>Este DTO se utiliza para transferir datos de usuarios entre la capa de
 * servicio y la capa de presentación, evitando exponer directamente la entidad
 * y permitiendo una mejor organización de los datos.</p>
 * 
 * <p>Incluye información personal, credenciales, rol, estado y permisos
 * del usuario.</p>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
public class UsuarioDTO {

    // ============================================
    // ATRIBUTOS PRINCIPALES
    // ============================================

    private Integer idUsuario;
    private Integer idRol;
    private String nombreCompleto;
    private String email;
    private String passwordHash;
    private String telefono;
    private Boolean activo;

    // ============================================
    // CAMPOS DE AUDITORÍA
    // ============================================

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private LocalDateTime ultimoAcceso;
    private Integer idUsuarioCreacion;
    private Integer idUsuarioActualizacion;
    private String nombreUsuarioCreacion;
    private String nombreUsuarioActualizacion;

    // ============================================
    // CAMPOS RELACIONADOS (DESDE ROL)
    // ============================================

    private String nombreRol;
    private String nombreRolLegible;
    private String colorRol;
    private String iconoRol;

    // ============================================
    // CAMPOS DE ESTADO
    // ============================================

    private String estadoLegible;
    private String estadoBadgeClass;
    private String estadoIcono;
    private String estadoColor;

    // ============================================
    // CAMPOS DE PERMISOS
    // ============================================

    private List<String> permisos = new ArrayList<>();

    // ============================================
    // CAMPOS ADICIONALES
    // ============================================

    private String nombreCompletoTruncado;
    private String fechaCreacionFormateada;
    private String ultimoAccesoFormateado;
    private String resumen;

    // ============================================
    // CONSTRUCTORES
    // ============================================

    /**
     * Constructor por defecto.
     */
    public UsuarioDTO() {
        this.activo = true;
        this.estadoLegible = "Activo";
        this.estadoBadgeClass = "badge-success";
        this.estadoIcono = "ti ti-check-circle";
        this.estadoColor = "#10B981";
        this.permisos = new ArrayList<>();
    }

    /**
     * Constructor con campos principales.
     * 
     * @param idUsuario ID del usuario
     * @param nombreCompleto Nombre completo
     * @param email Email
     * @param activo Estado activo/inactivo
     */
    public UsuarioDTO(Integer idUsuario, String nombreCompleto, String email, Boolean activo) {
        this();
        this.idUsuario = idUsuario;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.activo = activo;
        actualizarCamposFormateados();
    }

    /**
     * Constructor completo.
     * 
     * @param idUsuario ID del usuario
     * @param idRol ID del rol
     * @param nombreCompleto Nombre completo
     * @param email Email
     * @param telefono Teléfono
     * @param activo Estado activo/inactivo
     * @param nombreRol Nombre del rol
     */
    public UsuarioDTO(Integer idUsuario, Integer idRol, String nombreCompleto,
                     String email, String telefono, Boolean activo, String nombreRol) {
        this(idUsuario, nombreCompleto, email, activo);
        this.idRol = idRol;
        this.telefono = telefono;
        this.nombreRol = nombreRol;
        actualizarCamposFormateados();
    }

    // ============================================
    // GETTERS Y SETTERS
    // ============================================

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

    // ============================================
    // MÉTODOS DE NEGOCIO
    // ============================================

    /**
     * Actualiza los campos formateados basados en los atributos.
     */
    private void actualizarCamposFormateados() {
        // Estado del usuario
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

        // Rol del usuario
        if (nombreRol != null) {
            switch (nombreRol) {
                case "Administrador":
                    this.nombreRolLegible = "Administrador";
                    this.colorRol = "#EF4444";
                    this.iconoRol = "ti ti-shield";
                    break;
                case "Vendedor":
                    this.nombreRolLegible = "Vendedor";
                    this.colorRol = "#3B82F6";
                    this.iconoRol = "ti ti-user-check";
                    break;
                case "Diseñador":
                    this.nombreRolLegible = "Diseñador";
                    this.colorRol = "#8B5CF6";
                    this.iconoRol = "ti ti-pencil";
                    break;
                case "Producción":
                    this.nombreRolLegible = "Producción";
                    this.colorRol = "#F59E0B";
                    this.iconoRol = "ti ti-tools";
                    break;
                case "Soporte Técnico":
                    this.nombreRolLegible = "Soporte Técnico";
                    this.colorRol = "#10B981";
                    this.iconoRol = "ti ti-headset";
                    break;
                case "Cliente":
                    this.nombreRolLegible = "Cliente";
                    this.colorRol = "#6B7280";
                    this.iconoRol = "ti ti-user";
                    break;
                default:
                    this.nombreRolLegible = nombreRol;
                    this.colorRol = "#6B7280";
                    this.iconoRol = "ti ti-user";
                    break;
            }
        }

        // Nombre truncado
        if (nombreCompleto != null) {
            if (nombreCompleto.length() > 30) {
                this.nombreCompletoTruncado = nombreCompleto.substring(0, 30) + "...";
            } else {
                this.nombreCompletoTruncado = nombreCompleto;
            }
        }

        // Fecha de creación formateada
        if (fechaCreacion != null) {
            this.fechaCreacionFormateada = fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } else {
            this.fechaCreacionFormateada = "-";
        }

        // Último acceso formateado
        if (ultimoAcceso != null) {
            this.ultimoAccesoFormateado = ultimoAcceso.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } else {
            this.ultimoAccesoFormateado = "-";
        }

        // Resumen
        StringBuilder sb = new StringBuilder();
        sb.append(nombreCompleto != null ? nombreCompleto : "Sin nombre");
        sb.append(" (").append(getNombreRolLegible()).append(")");
        if (!isActivo()) {
            sb.append(" - Inactivo");
        }
        this.resumen = sb.toString();
    }

    /**
     * Verifica si el usuario está activo.
     * 
     * @return true si está activo, false en caso contrario
     */
    public boolean isActivo() {
        return activo != null && activo;
    }

    /**
     * Verifica si el usuario es Administrador.
     * 
     * @return true si es Administrador, false en caso contrario
     */
    public boolean esAdministrador() {
        return "Administrador".equals(this.nombreRol);
    }

    /**
     * Verifica si el usuario es Vendedor.
     * 
     * @return true si es Vendedor, false en caso contrario
     */
    public boolean esVendedor() {
        return "Vendedor".equals(this.nombreRol);
    }

    /**
     * Verifica si el usuario es Diseñador.
     * 
     * @return true si es Diseñador, false en caso contrario
     */
    public boolean esDiseñador() {
        return "Diseñador".equals(this.nombreRol);
    }

    /**
     * Verifica si el usuario es Producción.
     * 
     * @return true si es Producción, false en caso contrario
     */
    public boolean esProduccion() {
        return "Producción".equals(this.nombreRol);
    }

    /**
     * Verifica si el usuario es Soporte Técnico.
     * 
     * @return true si es Soporte Técnico, false en caso contrario
     */
    public boolean esSoporteTecnico() {
        return "Soporte Técnico".equals(this.nombreRol);
    }

    /**
     * Verifica si el usuario es Cliente.
     * 
     * @return true si es Cliente, false en caso contrario
     */
    public boolean esCliente() {
        return "Cliente".equals(this.nombreRol);
    }

    /**
     * Verifica si el usuario es interno (no cliente).
     * 
     * @return true si es interno, false en caso contrario
     */
    public boolean esRolInterno() {
        return !esCliente();
    }

    /**
     * Verifica si el usuario tiene permisos de administración.
     * 
     * @return true si tiene permisos de administración, false en caso contrario
     */
    public boolean tienePermisosAdmin() {
        return esAdministrador();
    }

    /**
     * Verifica si el usuario tiene permisos de ventas.
     * 
     * @return true si tiene permisos de ventas, false en caso contrario
     */
    public boolean tienePermisosVentas() {
        return esAdministrador() || esVendedor();
    }

    /**
     * Verifica si el usuario tiene permisos de diseño.
     * 
     * @return true si tiene permisos de diseño, false en caso contrario
     */
    public boolean tienePermisosDiseno() {
        return esAdministrador() || esDiseñador();
    }

    /**
     * Verifica si el usuario tiene permisos de producción.
     * 
     * @return true si tiene permisos de producción, false en caso contrario
     */
    public boolean tienePermisosProduccion() {
        return esAdministrador() || esProduccion();
    }

    /**
     * Verifica si el usuario tiene permisos de soporte técnico.
     * 
     * @return true si tiene permisos de soporte, false en caso contrario
     */
    public boolean tienePermisosSoporte() {
        return esAdministrador() || esSoporteTecnico();
    }

    /**
     * Verifica si el usuario tiene permisos de cliente.
     * 
     * @return true si tiene permisos de cliente, false en caso contrario
     */
    public boolean tienePermisosCliente() {
        return esAdministrador() || esCliente();
    }

    /**
     * Agrega un permiso al usuario.
     * 
     * @param permiso Permiso a agregar
     */
    public void agregarPermiso(String permiso) {
        if (permiso != null && this.permisos != null) {
            if (!this.permisos.contains(permiso)) {
                this.permisos.add(permiso);
            }
        }
    }

    /**
     * Verifica si el usuario tiene un permiso específico.
     * 
     * @param permiso Permiso a verificar
     * @return true si tiene el permiso, false en caso contrario
     */
    public boolean tienePermiso(String permiso) {
        return this.permisos != null && this.permisos.contains(permiso);
    }

    /**
     * Obtiene el nombre del rol para Spring Security.
     * 
     * @return Nombre del rol con prefijo ROLE_
     */
    public String getRolSeguridad() {
        if (nombreRol == null) return "ROLE_USER";
        return "ROLE_" + nombreRol.toUpperCase().replace(" ", "_").replace("Ó", "O");
    }

    /**
     * Verifica si el usuario tiene teléfono.
     * 
     * @return true si tiene teléfono, false en caso contrario
     */
    public boolean tieneTelefono() {
        return telefono != null && !telefono.trim().isEmpty();
    }

   
    public String getEstadoConIcono() {
        return "<i class='" + estadoIcono + "'></i> " + getEstadoLegible();
    }

    public String getRolConIcono() {
        return "<i class='" + iconoRol + "'></i> " + getNombreRolLegible();
    }

    public String getRolBadgeClass() {
        if (nombreRol == null) return "badge-secondary";
        
        switch (nombreRol) {
            case "Administrador":
                return "badge-danger";
            case "Vendedor":
                return "badge-primary";
            case "Diseñador":
                return "badge-purple";
            case "Producción":
                return "badge-warning";
            case "Soporte Técnico":
                return "badge-success";
            case "Cliente":
                return "badge-secondary";
            default:
                return "badge-secondary";
        }
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

    /**
     * Convierte a JSON con todos los campos.
     * 
     * @return JSON completo
     */
    public String toJsonCompleto() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"idUsuario\":").append(idUsuario).append(",");
        sb.append("\"idRol\":").append(idRol).append(",");
        sb.append("\"nombreCompleto\":\"").append(nombreCompleto != null ? nombreCompleto : "").append("\",");
        sb.append("\"nombreTruncado\":\"").append(getNombreCompletoTruncado()).append("\",");
        sb.append("\"email\":\"").append(email != null ? email : "").append("\",");
        sb.append("\"telefono\":\"").append(telefono != null ? telefono : "").append("\",");
        sb.append("\"rol\":\"").append(getNombreRolLegible()).append("\",");
        sb.append("\"rolColor\":\"").append(colorRol != null ? colorRol : "#6B7280").append("\",");
        sb.append("\"rolIcono\":\"").append(iconoRol != null ? iconoRol : "ti ti-user").append("\",");
        sb.append("\"activo\":").append(isActivo()).append(",");
        sb.append("\"estado\":\"").append(getEstadoLegible()).append("\",");
        sb.append("\"estadoBadgeClass\":\"").append(getEstadoBadgeClass()).append("\",");
        sb.append("\"fechaCreacion\":\"").append(getFechaCreacionFormateada()).append("\",");
        sb.append("\"fechaCreacionFormateada\":\"").append(getFechaCreacionFormateada()).append("\"");
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

    // ============================================
    // MÉTODOS OVERRIDE
    // ============================================

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

    // ============================================
    // MÉTODOS ESTÁTICOS DE FÁBRICA
    // ============================================

    /**
     * Crea un DTO desde una entidad.
     * 
     * @param usuario Entidad de usuario
     * @return DTO de usuario
     */
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
        
        // Campos transient
        dto.setNombreRol(usuario.getNombreRol());
        dto.setNombreRolLegible(usuario.getNombreRolLegible());
        dto.setColorRol(usuario.getColorRol());
        dto.setIconoRol(usuario.getIconoRol());
        dto.setEstadoLegible(usuario.getEstadoLegible());
        dto.setEstadoBadgeClass(usuario.getEstadoBadgeClass());
        dto.setEstadoIcono(usuario.getEstadoIcono());
        dto.setEstadoColor(usuario.getEstadoColor());
        
        // Actualizar campos formateados
        dto.actualizarCamposFormateados();
        
        return dto;
    }

    /**
     * Crea un DTO desde una entidad con permisos.
     * 
     * @param usuario Entidad de usuario
     * @param permisos Lista de permisos
     * @return DTO de usuario
     */
    public static UsuarioDTO fromEntityWithPermisos(com.stamos.model.Usuario usuario, List<String> permisos) {
        UsuarioDTO dto = fromEntity(usuario);
        if (dto != null && permisos != null) {
            dto.setPermisos(permisos);
        }
        return dto;
    }

    /**
     * Crea un DTO para un nuevo usuario.
     * 
     * @param nombreCompleto Nombre completo
     * @param email Email
     * @param idRol ID del rol
     * @param telefono Teléfono
     * @return DTO de usuario
     */
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

    /**
     * Convierte el DTO a una entidad.
     * 
     * @return Entidad de usuario
     */
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
}