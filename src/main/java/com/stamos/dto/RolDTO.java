package com.stamos.dto;
import java.time.LocalDateTime;

public class RolDTO {

    private Integer idRol;
    private String nombreRol;
    private String descripcion;
    private String nombreLegible;
    private String color;
    private String icono;
    private Integer cantidadUsuarios;
    private LocalDateTime fechaCreacion;

    public RolDTO() {
        this.color = "#6B7280";
        this.icono = "ti ti-user";
        this.cantidadUsuarios = 0;
    }

    public RolDTO(Integer idRol, String nombreRol, String descripcion) {
        this();
        this.idRol = idRol;
        this.nombreRol = nombreRol;
        this.descripcion = descripcion;
        actualizarCamposFormateados();
    }

    public RolDTO(Integer idRol, String nombreRol, String descripcion, Integer cantidadUsuarios) {
        this(idRol, nombreRol, descripcion);
        this.cantidadUsuarios = cantidadUsuarios;
        actualizarCamposFormateados();
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

    private void actualizarCamposFormateados() {
        if (nombreRol != null) {
            switch (nombreRol) {
                case "Administrador" -> {
                    this.nombreLegible = "Administrador";
                    this.color = "#EF4444";
                    this.icono = "ti ti-shield";
                }
                case "Vendedor" -> {
                    this.nombreLegible = "Vendedor";
                    this.color = "#3B82F6";
                    this.icono = "ti ti-user-check";
                }
                case "Diseñador" -> {
                    this.nombreLegible = "Diseñador";
                    this.color = "#8B5CF6";
                    this.icono = "ti ti-pencil";
                }
                case "Producción" -> {
                    this.nombreLegible = "Producción";
                    this.color = "#F59E0B";
                    this.icono = "ti ti-tools";
                }
                case "Soporte Técnico" -> {
                    this.nombreLegible = "Soporte Técnico";
                    this.color = "#10B981";
                    this.icono = "ti ti-headset";
                }
                case "Cliente" -> {
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

    public String getRolSeguridad() {
        return "ROLE_" + nombreRol.toUpperCase().replace(" ", "_").replace("Ó", "O");
    }

    public String getColorHex() {
        return color != null ? color : "#6B7280";
    }

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
        dto.setNombreLegible(rol.getNombreLegible());
        dto.setColor(rol.getColor());
        dto.setIcono(rol.getIcono());
        return dto;
    }

    public static RolDTO crearNuevoRol(String nombreRol, String descripcion) {
        RolDTO dto = new RolDTO();
        dto.setNombreRol(nombreRol);
        dto.setDescripcion(descripcion);
        dto.actualizarCamposFormateados();
        return dto;
    }

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
}