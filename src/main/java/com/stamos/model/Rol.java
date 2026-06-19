package com.stamos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Rol.
 * 
 * <p>Esta entidad representa los roles de usuario en el sistema STAMOS S.L.,
 * definiendo los permisos y accesos de cada tipo de usuario.</p>
 * 
 * <p>Roles definidos:</p>
 * <ul>
 *   <li>Administrador - Acceso total al sistema, gestión de usuarios y configuración</li>
 *   <li>Vendedor - Gestión de clientes y cotizaciones</li>
 *   <li>Diseñador - Diseño 3D, renders, planos y bocetos</li>
 *   <li>Producción - Fabricación y logística de stands</li>
 *   <li>Soporte Técnico - Configuración de equipos, montaje y entrega</li>
 *   <li>Cliente - Consulta de proyectos y visualización de documentos</li>
 * </ul>
 * 
 * <p>Implementa los requerimientos:</p>
 * <ul>
 *   <li>RNF-02: Autenticación obligatoria mediante usuario y contraseña</li>
 *   <li>RNF-04: Control de acceso basado en roles</li>
 *   <li>RN-15 a RN-19: Permisos de cambio de estado por rol</li>
 * </ul>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
@Entity
@Table(name = "Rol",
       indexes = {
           @Index(name = "idx_rol_nombre", columnList = "nombre_rol", unique = true)
       })
public class Rol {

    // ============================================
    // CONSTANTES PARA NOMBRES DE ROLES
    // ============================================

    public static final String ROL_ADMINISTRADOR = "Administrador";
    public static final String ROL_VENDEDOR = "Vendedor";
    public static final String ROL_DISEÑADOR = "Diseñador";
    public static final String ROL_PRODUCCION = "Producción";
    public static final String ROL_SOPORTE_TECNICO = "Soporte Técnico";
    public static final String ROL_CLIENTE = "Cliente";

    // ============================================
    // ATRIBUTOS PRINCIPALES
    // ============================================

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

    // ============================================
    // CAMPOS ADICIONALES (NO EN BD)
    // ============================================

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

    // ============================================
    // CONSTRUCTORES
    // ============================================

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

    // ============================================
    // MÉTODOS DE NEGOCIO
    // ============================================

    /**
     * Actualiza los campos transient basados en el nombre del rol.
     */
    private void actualizarCamposTransient() {
        if (nombreRol != null) {
            switch (nombreRol) {
                case ROL_ADMINISTRADOR:
                    this.nombreLegible = "Administrador";
                    this.color = "#EF4444";
                    this.icono = "ti ti-shield";
                    break;
                case ROL_VENDEDOR:
                    this.nombreLegible = "Vendedor";
                    this.color = "#3B82F6";
                    this.icono = "ti ti-user-check";
                    break;
                case ROL_DISEÑADOR:
                    this.nombreLegible = "Diseñador";
                    this.color = "#8B5CF6";
                    this.icono = "ti ti-pencil";
                    break;
                case ROL_PRODUCCION:
                    this.nombreLegible = "Producción";
                    this.color = "#F59E0B";
                    this.icono = "ti ti-tools";
                    break;
                case ROL_SOPORTE_TECNICO:
                    this.nombreLegible = "Soporte Técnico";
                    this.color = "#10B981";
                    this.icono = "ti ti-headset";
                    break;
                case ROL_CLIENTE:
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
        return ROL_ADMINISTRADOR.equals(this.nombreRol);
    }

    /**
     * Verifica si el rol es Vendedor.
     * 
     * @return true si es Vendedor, false en caso contrario
     */
    public boolean esVendedor() {
        return ROL_VENDEDOR.equals(this.nombreRol);
    }

    /**
     * Verifica si el rol es Diseñador.
     * 
     * @return true si es Diseñador, false en caso contrario
     */
    public boolean esDiseñador() {
        return ROL_DISEÑADOR.equals(this.nombreRol);
    }

    /**
     * Verifica si el rol es Producción.
     * 
     * @return true si es Producción, false en caso contrario
     */
    public boolean esProduccion() {
        return ROL_PRODUCCION.equals(this.nombreRol);
    }

    /**
     * Verifica si el rol es Soporte Técnico.
     * 
     * @return true si es Soporte Técnico, false en caso contrario
     */
    public boolean esSoporteTecnico() {
        return ROL_SOPORTE_TECNICO.equals(this.nombreRol);
    }

    /**
     * Verifica si el rol es Cliente.
     * 
     * @return true si es Cliente, false en caso contrario
     */
    public boolean esCliente() {
        return ROL_CLIENTE.equals(this.nombreRol);
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
     * Verifica si el rol tiene permisos de cliente.
     * 
     * @return true si tiene permisos de cliente, false en caso contrario
     */
    public boolean tienePermisosCliente() {
        return esAdministrador() || esCliente();
    }

    /**
     * Agrega un usuario al rol.
     * 
     * @param usuario Usuario a agregar
     */
    public void agregarUsuario(Usuario usuario) {
        if (usuario != null && this.usuarios != null) {
            this.usuarios.add(usuario);
            this.cantidadUsuarios = this.usuarios.size();
        }
    }

    /**
     * Obtiene el nombre del rol para Spring Security.
     * 
     * @return Nombre del rol con prefijo ROLE_
     */
    public String getRolSeguridad() {
        return "ROLE_" + nombreRol.toUpperCase().replace(" ", "_").replace("Ó", "O");
    }

    /**
     * Obtiene el nombre del rol para mostrar en la vista.
     * 
     * @return Nombre legible del rol
     */
    public String getNombreParaVista() {
        return nombreLegible != null ? nombreLegible : nombreRol;
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
            case ROL_ADMINISTRADOR:
                return "badge-danger";
            case ROL_VENDEDOR:
                return "badge-primary";
            case ROL_DISEÑADOR:
                return "badge-purple";
            case ROL_PRODUCCION:
                return "badge-warning";
            case ROL_SOPORTE_TECNICO:
                return "badge-success";
            case ROL_CLIENTE:
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
     * Obtiene la clase CSS para el color del rol.
     * 
     * @return Clase CSS
     */
    public String getColorClass() {
        if (nombreRol == null) return "text-secondary";
        
        switch (nombreRol) {
            case ROL_ADMINISTRADOR:
                return "text-danger";
            case ROL_VENDEDOR:
                return "text-primary";
            case ROL_DISEÑADOR:
                return "text-purple";
            case ROL_PRODUCCION:
                return "text-warning";
            case ROL_SOPORTE_TECNICO:
                return "text-success";
            case ROL_CLIENTE:
                return "text-secondary";
            default:
                return "text-secondary";
        }
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
        if (cantidadUsuarios != null) {
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

    /**
     * Obtiene la fecha de creación formateada (solo fecha).
     * 
     * @return Fecha formateada
     */
    public String getFechaCreacionSoloFecha() {
        if (fechaCreacion == null) return "-";
        return fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
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
     * Obtiene el ícono del rol.
     * 
     * @return Ícono CSS
     */


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

    // ============================================
    // MÉTODOS ESTÁTICOS DE FÁBRICA
    // ============================================

    /**
     * Crea el rol Administrador.
     * 
     * @return Rol Administrador
     */
    public static Rol crearAdministrador() {
        return new Rol(ROL_ADMINISTRADOR, "Acceso total al sistema, gestión de usuarios y configuración");
    }

    /**
     * Crea el rol Vendedor.
     * 
     * @return Rol Vendedor
     */
    public static Rol crearVendedor() {
        return new Rol(ROL_VENDEDOR, "Gestión de clientes y cotizaciones");
    }

    /**
     * Crea el rol Diseñador.
     * 
     * @return Rol Diseñador
     */
    public static Rol crearDiseñador() {
        return new Rol(ROL_DISEÑADOR, "Diseño 3D, renders, planos y bocetos");
    }

    /**
     * Crea el rol Producción.
     * 
     * @return Rol Producción
     */
    public static Rol crearProduccion() {
        return new Rol(ROL_PRODUCCION, "Fabricación y logística de stands");
    }

    /**
     * Crea el rol Soporte Técnico.
     * 
     * @return Rol Soporte Técnico
     */
    public static Rol crearSoporteTecnico() {
        return new Rol(ROL_SOPORTE_TECNICO, "Configuración de equipos, montaje y entrega");
    }

    /**
     * Crea el rol Cliente.
     * 
     * @return Rol Cliente
     */
    public static Rol crearCliente() {
        return new Rol(ROL_CLIENTE, "Consulta de proyectos y visualización de documentos");
    }

    /**
     * Obtiene la lista de todos los roles predefinidos.
     * 
     * @return Lista de roles
     */
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

    /**
     * Crea un rol desde un DTO.
     * 
     * @param dto DTO del rol
     * @return Rol
     */
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

    /**
     * Convierte el rol a un DTO.
     * 
     * @return DTO del rol
     */
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