package com.stamos.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO (Data Transfer Object) para la entidad Rol.
 * 
 * <p>Este DTO se utiliza para transferir datos de roles entre la capa de
 * servicio y la capa de presentación, evitando exponer directamente la entidad
 * y permitiendo una mejor organización de los datos.</p>
 * 
 * <p>Incluye información del rol, descripción, colores, íconos y métricas
 * de usuarios asociados.</p>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
public class RolDTO {

    // ============================================
    // ATRIBUTOS PRINCIPALES
    // ============================================

    private Integer idRol;
    private String nombreRol;
    private String descripcion;

    // ============================================
    // CAMPOS DE FORMATO
    // ============================================

    private String nombreLegible;
    private String color;
    private String icono;

    // ============================================
    // CAMPOS DE MÉTRICAS
    // ============================================

    private Integer cantidadUsuarios;

    // ============================================
    // CAMPOS DE AUDITORÍA
    // ============================================

    private LocalDateTime fechaCreacion;

    // ============================================
    // CONSTRUCTORES
    // ============================================

    /**
     * Constructor por defecto.
     */
    public RolDTO() {
        this.color = "#6B7280";
        this.icono = "ti ti-user";
        this.cantidadUsuarios = 0;
    }

    /**
     * Constructor con campos principales.
     * 
     * @param idRol ID del rol
     * @param nombreRol Nombre del rol
     * @param descripcion Descripción del rol
     */
    public RolDTO(Integer idRol, String nombreRol, String descripcion) {
        this();
        this.idRol = idRol;
        this.nombreRol = nombreRol;
        this.descripcion = descripcion;
        actualizarCamposFormateados();
    }

    /**
     * Constructor completo.
     * 
     * @param idRol ID del rol
     * @param nombreRol Nombre del rol
     * @param descripcion Descripción del rol
     * @param cantidadUsuarios Cantidad de usuarios
     */
    public RolDTO(Integer idRol, String nombreRol, String descripcion, Integer cantidadUsuarios) {
        this(idRol, nombreRol, descripcion);
        this.cantidadUsuarios = cantidadUsuarios;
        actualizarCamposFormateados();
    }

    // ============================================
    // GETTERS Y SETTERS
    // ============================================

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
        actualizarCamposFormateados();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    // ============================================
    // MÉTODOS DE NEGOCIO
    // ============================================

    /**
     * Actualiza los campos formateados basados en el nombre del rol.
     */
    private void actualizarCamposFormateados() {
        if (nombreRol != null) {
            switch (nombreRol) {
                case "Administrador":
                    this.nombreLegible = "Administrador";
                    this.color = "#EF4444";
                    this.icono = "ti ti-shield";
                    break;
                case "Vendedor":
                    this.nombreLegible = "Vendedor";
                    this.color = "#3B82F6";
                    this.icono = "ti ti-user-check";
                    break;
                case "Diseñador":
                    this.nombreLegible = "Diseñador";
                    this.color = "#8B5CF6";
                    this.icono = "ti ti-pencil";
                    break;
                case "Producción":
                    this.nombreLegible = "Producción";
                    this.color = "#F59E0B";
                    this.icono = "ti ti-tools";
                    break;
                case "Soporte Técnico":
                    this.nombreLegible = "Soporte Técnico";
                    this.color = "#10B981";
                    this.icono = "ti ti-headset";
                    break;
                case "Cliente":
                    this.nombreLegible = "Cliente";
                    this.color = "#6B7280";
                    this.icono = "ti ti-user";
                    break;
                default:
                    this.nombreLegible = nombreRol;
                    this.color = "#6B7280";
                    this.icono = "ti ti-user";
                    break;
            }
        }
    }

    /**
     * Verifica si el rol es Administrador.
     * 
     * @return true si es Administrador, false en caso contrario
     */
    public boolean esAdministrador() {
        return "Administrador".equals(this.nombreRol);
    }

    /**
     * Verifica si el rol es Vendedor.
     * 
     * @return true si es Vendedor, false en caso contrario
     */
    public boolean esVendedor() {
        return "Vendedor".equals(this.nombreRol);
    }

    /**
     * Verifica si el rol es Diseñador.
     * 
     * @return true si es Diseñador, false en caso contrario
     */
    public boolean esDiseñador() {
        return "Diseñador".equals(this.nombreRol);
    }

    /**
     * Verifica si el rol es Producción.
     * 
     * @return true si es Producción, false en caso contrario
     */
    public boolean esProduccion() {
        return "Producción".equals(this.nombreRol);
    }

    /**
     * Verifica si el rol es Soporte Técnico.
     * 
     * @return true si es Soporte Técnico, false en caso contrario
     */
    public boolean esSoporteTecnico() {
        return "Soporte Técnico".equals(this.nombreRol);
    }

    /**
     * Verifica si el rol es Cliente.
     * 
     * @return true si es Cliente, false en caso contrario
     */
    public boolean esCliente() {
        return "Cliente".equals(this.nombreRol);
    }

    /**
     * Verifica si el rol es interno (no cliente).
     * 
     * @return true si es interno, false en caso contrario
     */
    public boolean esRolInterno() {
        return !esCliente();
    }

    /**
     * Verifica si el rol tiene permisos de administración.
     * 
     * @return true si tiene permisos de administración, false en caso contrario
     */
    public boolean tienePermisosAdmin() {
        return esAdministrador();
    }

    /**
     * Verifica si el rol tiene permisos de ventas.
     * 
     * @return true si tiene permisos de ventas, false en caso contrario
     */
    public boolean tienePermisosVentas() {
        return esAdministrador() || esVendedor();
    }

    /**
     * Verifica si el rol tiene permisos de diseño.
     * 
     * @return true si tiene permisos de diseño, false en caso contrario
     */
    public boolean tienePermisosDiseno() {
        return esAdministrador() || esDiseñador();
    }

    /**
     * Verifica si el rol tiene permisos de producción.
     * 
     * @return true si tiene permisos de producción, false en caso contrario
     */
    public boolean tienePermisosProduccion() {
        return esAdministrador() || esProduccion();
    }

    /**
     * Verifica si el rol tiene permisos de soporte técnico.
     * 
     * @return true si tiene permisos de soporte, false en caso contrario
     */
    public boolean tienePermisosSoporte() {
        return esAdministrador() || esSoporteTecnico();
    }

    /**
     * Obtiene el nombre del rol para Spring Security.
     * 
     * @return Nombre del rol con prefijo ROLE_
     */
    public String getRolSeguridad() {
        return "ROLE_" + nombreRol.toUpperCase().replace(" ", "_").replace("Ó", "O");
    }

    // ============================================
    // MÉTODOS DE FORMATO PARA LA VISTA
    // ============================================

    /**
     * Obtiene el nombre del rol en formato legible.
     * 
     * @return Nombre legible
     */


    /**
     * Obtiene la clase CSS para el badge del rol.
     * 
     * @return Clase CSS
     */
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

    /**
     * Obtiene el rol con ícono.
     * 
     * @return Rol con ícono
     */
    public String getRolConIcono() {
        return "<i class='" + icono + "'></i> " + getNombreLegible();
    }

    /**
     * Obtiene el color del rol en hexadecimal.
     * 
     * @return Color en hexadecimal
     */
    public String getColorHex() {
        return color != null ? color : "#6B7280";
    }

    /**
     * Obtiene el resumen del rol.
     * 
     * @return Resumen
     */
    public String getResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append(getNombreLegible());
        if (descripcion != null && !descripcion.isEmpty()) {
            sb.append(" - ").append(descripcion);
        }
        if (cantidadUsuarios != null && cantidadUsuarios > 0) {
            sb.append(" (").append(cantidadUsuarios).append(" usuarios)");
        }
        return sb.toString();
    }

    /**
     * Obtiene la fecha de creación formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaCreacionFormateada() {
        if (fechaCreacion == null) return "-";
        return fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
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

    /**
     * Convierte a JSON con todos los campos.
     * 
     * @return JSON completo
     */
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

    // ============================================
    // MÉTODOS OVERRIDE
    // ============================================

    @Override
    public String toString() {
        return "RolDTO{" +
                "idRol=" + idRol +
                ", nombreRol='" + nombreRol + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", cantidadUsuarios=" + cantidadUsuarios +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolDTO rolDTO = (RolDTO) o;
        return idRol != null && idRol.equals(rolDTO.idRol);
    }

    @Override
    public int hashCode() {
        return idRol != null ? idRol.hashCode() : 0;
    }

    // ============================================
    // MÉTODOS ESTÁTICOS DE FÁBRICA
    // ============================================

    /**
     * Crea un DTO desde una entidad.
     * 
     * @param rol Entidad de rol
     * @return DTO de rol
     */
    public static RolDTO fromEntity(com.stamos.model.Rol rol) {
        if (rol == null) {
            return null;
        }

        RolDTO dto = new RolDTO();
        dto.setIdRol(rol.getIdRol());
        dto.setNombreRol(rol.getNombreRol());
        dto.setDescripcion(rol.getDescripcion());
        dto.setFechaCreacion(rol.getFechaCreacion());
        dto.setCantidadUsuarios(rol.getCantidadUsuarios());
        
        // Campos transient
        dto.setNombreLegible(rol.getNombreLegible());
        dto.setColor(rol.getColor());
        dto.setIcono(rol.getIcono());
        
        return dto;
    }

    /**
     * Crea un DTO para un nuevo rol.
     * 
     * @param nombreRol Nombre del rol
     * @param descripcion Descripción del rol
     * @return DTO de rol
     */
    public static RolDTO crearNuevoRol(String nombreRol, String descripcion) {
        RolDTO dto = new RolDTO();
        dto.setNombreRol(nombreRol);
        dto.setDescripcion(descripcion);
        dto.actualizarCamposFormateados();
        return dto;
    }

    /**
     * Convierte el DTO a una entidad.
     * 
     * @return Entidad de rol
     */
    public com.stamos.model.Rol toEntity() {
        com.stamos.model.Rol rol = new com.stamos.model.Rol();
        if (this.idRol != null) {
            rol.setIdRol(this.idRol);
        }
        rol.setNombreRol(this.nombreRol);
        rol.setDescripcion(this.descripcion);
        rol.setFechaCreacion(this.fechaCreacion);
        return rol;
    }
}