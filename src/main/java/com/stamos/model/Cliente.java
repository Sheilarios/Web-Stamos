package com.stamos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Cliente.
 * 
 * <p>Esta entidad representa a los clientes de STAMOS S.L., almacenando
 * su información personal, de contacto y estado en el sistema.</p>
 * 
 * <p>Implementa los requerimientos:</p>
 * <ul>
 *   <li>RN-01: Solo personal autorizado puede dar de alta clientes</li>
 *   <li>RN-02: Cliente debe proporcionar nombre, teléfono, email y tipo</li>
 *   <li>RN-03: Clientes con proyectos o deudas no pueden ser eliminados</li>
 *   <li>RF-01: Registrar clientes con nombre, teléfono, email y clasificación</li>
 *   <li>RF-04: Impedir eliminación de clientes con registros asociados</li>
 * </ul>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
@Entity
@Table(name = "Cliente",
       indexes = {
           @Index(name = "idx_cliente_ruc", columnList = "ruc"),
           @Index(name = "idx_cliente_nombre", columnList = "nombre_razon_social"),
           @Index(name = "idx_cliente_email", columnList = "email"),
           @Index(name = "idx_cliente_telefono", columnList = "telefono"),
           @Index(name = "idx_cliente_estado", columnList = "estado")
       })
public class Cliente {

    // ============================================
    // ATRIBUTOS PRINCIPALES
    // ============================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

    @NotBlank(message = "El tipo de persona es obligatorio")
    @Column(name = "tipo_persona", nullable = false)
    private String tipoPersona; // 'Natural' o 'Juridica'

    @NotBlank(message = "El nombre o razón social es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder los 150 caracteres")
    @Column(name = "nombre_razon_social", nullable = false, length = 150)
    private String nombreRazonSocial;

    @Size(max = 11, message = "El RUC debe tener máximo 11 dígitos")
    @Pattern(regexp = "^[0-9]*$", message = "El RUC solo debe contener números")
    @Column(name = "ruc", length = 11, unique = true)
    private String ruc;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres")
    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Size(max = 200, message = "La dirección no puede exceder los 200 caracteres")
    @Column(name = "direccion", length = 200)
    private String direccion;

    @Column(name = "estado", nullable = false)
    private String estado = "Activo"; // 'Activo' o 'Deshabilitado'

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    // ============================================
    // RELACIONES
    // ============================================

    @Column(name = "id_usuario_regis")
    private Integer idUsuarioRegis;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    // ============================================
    // CAMPOS DE AUDITORÍA
    // ============================================

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "id_usuario_actualizacion")
    private Integer idUsuarioActualizacion;

    // ============================================
    // CAMPOS ADICIONALES (NO EN BD)
    // ============================================

    @Transient
    private String nombreUsuario;

    @Transient
    private String emailUsuario;

    @Transient
    private Integer cantidadProyectos;

    @Transient
    private Integer cantidadProyectosActivos;

    @Transient
    private Integer cantidadCotizaciones;

    @Transient
    private Integer cantidadCotizacionesPendientes;

    @Transient
    private java.math.BigDecimal totalFacturado;

    @Transient
    private java.math.BigDecimal totalPagado;

    @Transient
    private java.math.BigDecimal saldoPendiente;

    // ============================================
    // CONSTRUCTORES
    // ============================================

    public Cliente() {
        this.estado = "Activo";
        this.fechaRegistro = LocalDateTime.now();
        this.tipoPersona = "Natural";
    }

    public Cliente(String nombreRazonSocial, String telefono, String email) {
        this();
        this.nombreRazonSocial = nombreRazonSocial;
        this.telefono = telefono;
        this.email = email;
    }

    public Cliente(String tipoPersona, String nombreRazonSocial, String ruc,
                   String telefono, String email, String direccion) {
        this();
        this.tipoPersona = tipoPersona;
        this.nombreRazonSocial = nombreRazonSocial;
        this.ruc = ruc;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
    }

    // ============================================
    // GETTERS Y SETTERS
    // ============================================

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(String tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public String getNombreRazonSocial() {
        return nombreRazonSocial;
    }

    public void setNombreRazonSocial(String nombreRazonSocial) {
        this.nombreRazonSocial = nombreRazonSocial;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Integer getIdUsuarioRegis() {
        return idUsuarioRegis;
    }

    public void setIdUsuarioRegis(Integer idUsuarioRegis) {
        this.idUsuarioRegis = idUsuarioRegis;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Integer getIdUsuarioActualizacion() {
        return idUsuarioActualizacion;
    }

    public void setIdUsuarioActualizacion(Integer idUsuarioActualizacion) {
        this.idUsuarioActualizacion = idUsuarioActualizacion;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public Integer getCantidadProyectos() {
        return cantidadProyectos;
    }

    public void setCantidadProyectos(Integer cantidadProyectos) {
        this.cantidadProyectos = cantidadProyectos;
    }

    public Integer getCantidadProyectosActivos() {
        return cantidadProyectosActivos;
    }

    public void setCantidadProyectosActivos(Integer cantidadProyectosActivos) {
        this.cantidadProyectosActivos = cantidadProyectosActivos;
    }

    public Integer getCantidadCotizaciones() {
        return cantidadCotizaciones;
    }

    public void setCantidadCotizaciones(Integer cantidadCotizaciones) {
        this.cantidadCotizaciones = cantidadCotizaciones;
    }

    public Integer getCantidadCotizacionesPendientes() {
        return cantidadCotizacionesPendientes;
    }

    public void setCantidadCotizacionesPendientes(Integer cantidadCotizacionesPendientes) {
        this.cantidadCotizacionesPendientes = cantidadCotizacionesPendientes;
    }

    public java.math.BigDecimal getTotalFacturado() {
        return totalFacturado;
    }

    public void setTotalFacturado(java.math.BigDecimal totalFacturado) {
        this.totalFacturado = totalFacturado;
    }

    public java.math.BigDecimal getTotalPagado() {
        return totalPagado;
    }

    public void setTotalPagado(java.math.BigDecimal totalPagado) {
        this.totalPagado = totalPagado;
    }

    public java.math.BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(java.math.BigDecimal saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }

    // ============================================
    // MÉTODOS DE NEGOCIO
    // ============================================

    /**
     * Verifica si el cliente está activo.
     * 
     * @return true si está activo, false en caso contrario
     */
    public boolean isActivo() {
        return "Activo".equals(this.estado);
    }

    /**
     * Verifica si el cliente es persona natural.
     * 
     * @return true si es persona natural, false en caso contrario
     */
    public boolean isPersonaNatural() {
        return "Natural".equals(this.tipoPersona);
    }

    /**
     * Verifica si el cliente es persona jurídica (empresa).
     * 
     * @return true si es persona jurídica, false en caso contrario
     */
    public boolean isPersonaJuridica() {
        return "Juridica".equals(this.tipoPersona);
    }

    /**
     * Verifica si el cliente tiene RUC.
     * 
     * @return true si tiene RUC, false en caso contrario
     */
    public boolean tieneRuc() {
        return ruc != null && !ruc.trim().isEmpty();
    }

    /**
     * Verifica si el cliente tiene dirección.
     * 
     * @return true si tiene dirección, false en caso contrario
     */
    public boolean tieneDireccion() {
        return direccion != null && !direccion.trim().isEmpty();
    }

    /**
     * Activa el cliente.
     */
    public void activar() {
        this.estado = "Activo";
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Desactiva el cliente.
     */
    public void desactivar() {
        this.estado = "Deshabilitado";
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Alterna el estado del cliente (activo/inactivo).
     */
    public void toggleEstado() {
        if (isActivo()) {
            desactivar();
        } else {
            activar();
        }
    }

    /**
     * Verifica si el cliente tiene proyectos.
     * 
     * @return true si tiene proyectos, false en caso contrario
     */
    public boolean tieneProyectos() {
        return cantidadProyectos != null && cantidadProyectos > 0;
    }

    /**
     * Verifica si el cliente tiene proyectos activos.
     * 
     * @return true si tiene proyectos activos, false en caso contrario
     */
    public boolean tieneProyectosActivos() {
        return cantidadProyectosActivos != null && cantidadProyectosActivos > 0;
    }

    /**
     * Verifica si el cliente tiene cotizaciones.
     * 
     * @return true si tiene cotizaciones, false en caso contrario
     */
    public boolean tieneCotizaciones() {
        return cantidadCotizaciones != null && cantidadCotizaciones > 0;
    }

    /**
     * Verifica si el cliente tiene cotizaciones pendientes.
     * 
     * @return true si tiene cotizaciones pendientes, false en caso contrario
     */
    public boolean tieneCotizacionesPendientes() {
        return cantidadCotizacionesPendientes != null && cantidadCotizacionesPendientes > 0;
    }

    /**
     * Verifica si el cliente tiene saldo pendiente.
     * 
     * @return true si tiene saldo pendiente, false en caso contrario
     */
    public boolean tieneSaldoPendiente() {
        return saldoPendiente != null && saldoPendiente.compareTo(java.math.BigDecimal.ZERO) > 0;
    }

    /**
     * Verifica si el cliente puede ser eliminado (RN-03).
     * 
     * @return true si puede ser eliminado, false en caso contrario
     */
    public boolean puedeSerEliminado() {
        return !tieneProyectos() && !tieneCotizaciones() && !tieneSaldoPendiente();
    }

    /**
     * Obtiene el nombre completo de la persona o razón social.
     * 
     * @return Nombre completo
     */
    public String getNombreCompleto() {
        return nombreRazonSocial;
    }

    /**
     * Obtiene el nombre del cliente para mostrar en la interfaz.
     * 
     * @return Nombre para mostrar
     */
    public String getNombreDisplay() {
        if (isPersonaJuridica() && tieneRuc()) {
            return nombreRazonSocial + " (" + ruc + ")";
        }
        return nombreRazonSocial;
    }

    /**
     * Obtiene el RUC formateado (con guiones).
     * 
     * @return RUC formateado
     */
    public String getRucFormateado() {
        if (ruc == null || ruc.isEmpty()) {
            return "";
        }
        if (ruc.length() == 11) {
            return ruc.substring(0, 2) + "-" + ruc.substring(2, 5) + "-" + 
                   ruc.substring(5, 8) + "-" + ruc.substring(8);
        }
        return ruc;
    }

    // ============================================
    // MÉTODOS DE FORMATO PARA LA VISTA
    // ============================================

    /**
     * Obtiene el estado en formato legible.
     * 
     * @return Estado legible
     */
    public String getEstadoLegible() {
        return "Activo".equals(this.estado) ? "Activo" : "Deshabilitado";
    }

    /**
     * Obtiene la clase CSS para el badge del estado.
     * 
     * @return Clase CSS
     */
    public String getEstadoBadgeClass() {
        return isActivo() ? "badge-success" : "badge-danger";
    }

    /**
     * Obtiene el ícono para el estado.
     * 
     * @return Ícono CSS
     */
    public String getEstadoIcono() {
        return isActivo() ? "ti ti-check-circle" : "ti ti-x-circle";
    }

    /**
     * Obtiene el color para el estado.
     * 
     * @return Color en hexadecimal
     */
    public String getEstadoColor() {
        return isActivo() ? "#10B981" : "#EF4444";
    }

    /**
     * Obtiene el tipo de persona en formato legible.
     * 
     * @return Tipo de persona legible
     */
    public String getTipoPersonaLegible() {
        if (tipoPersona == null) return "No especificado";
        return "Natural".equals(tipoPersona) ? "Persona Natural" : "Persona Jurídica";
    }

    /**
     * Obtiene la clase CSS para el badge del tipo de persona.
     * 
     * @return Clase CSS
     */
    public String getTipoPersonaBadgeClass() {
        if (tipoPersona == null) return "badge-secondary";
        return "Natural".equals(tipoPersona) ? "badge-info" : "badge-primary";
    }

    /**
     * Obtiene el ícono para el tipo de persona.
     * 
     * @return Ícono CSS
     */
    public String getTipoPersonaIcono() {
        if (tipoPersona == null) return "ti ti-user";
        return "Natural".equals(tipoPersona) ? "ti ti-user" : "ti ti-building";
    }

    /**
     * Obtiene la fecha de registro formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaRegistroFormateada() {
        if (fechaRegistro == null) return "-";
        return fechaRegistro.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    /**
     * Obtiene la fecha de registro formateada (solo fecha).
     * 
     * @return Fecha formateada
     */
    public String getFechaRegistroSoloFecha() {
        if (fechaRegistro == null) return "-";
        return fechaRegistro.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Obtiene la fecha de actualización formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaActualizacionFormateada() {
        if (fechaActualizacion == null) return "-";
        return fechaActualizacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    /**
     * Obtiene el total facturado formateado.
     * 
     * @return Total facturado formateado
     */
    public String getTotalFacturadoFormateado() {
        if (totalFacturado == null) return "S/ 0.00";
        return "S/ " + totalFacturado.setScale(2, java.math.RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el total pagado formateado.
     * 
     * @return Total pagado formateado
     */
    public String getTotalPagadoFormateado() {
        if (totalPagado == null) return "S/ 0.00";
        return "S/ " + totalPagado.setScale(2, java.math.RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el saldo pendiente formateado.
     * 
     * @return Saldo pendiente formateado
     */
    public String getSaldoPendienteFormateado() {
        if (saldoPendiente == null) return "S/ 0.00";
        return "S/ " + saldoPendiente.setScale(2, java.math.RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el color para el saldo pendiente.
     * 
     * @return Color en hexadecimal
     */
    public String getSaldoColor() {
        if (saldoPendiente == null || saldoPendiente.compareTo(java.math.BigDecimal.ZERO) == 0) {
            return "#10B981";
        }
        return "#EF4444";
    }

    /**
     * Obtiene el resumen del cliente.
     * 
     * @return Resumen
     */
    public String getResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append(nombreRazonSocial);
        if (tieneRuc()) {
            sb.append(" - ").append(ruc);
        }
        sb.append(" (").append(getTipoPersonaLegible()).append(")");
        if (cantidadProyectos != null) {
            sb.append(" - ").append(cantidadProyectos).append(" proyectos");
        }
        if (tieneSaldoPendiente()) {
            sb.append(" - Saldo: ").append(getSaldoPendienteFormateado());
        }
        return sb.toString();
    }

    /**
     * Trunca el nombre del cliente.
     * 
     * @param maxLength Longitud máxima
     * @return Nombre truncado
     */
    public String getNombreTruncado(int maxLength) {
        if (nombreRazonSocial == null) return "";
        if (nombreRazonSocial.length() <= maxLength) return nombreRazonSocial;
        return nombreRazonSocial.substring(0, maxLength) + "...";
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
        sb.append("\"idCliente\":").append(idCliente).append(",");
        sb.append("\"nombre\":\"").append(nombreRazonSocial != null ? nombreRazonSocial : "").append("\",");
        sb.append("\"ruc\":\"").append(ruc != null ? ruc : "").append("\",");
        sb.append("\"email\":\"").append(email != null ? email : "").append("\",");
        sb.append("\"telefono\":\"").append(telefono != null ? telefono : "").append("\",");
        sb.append("\"tipo\":\"").append(tipoPersona != null ? tipoPersona : "").append("\",");
        sb.append("\"estado\":\"").append(estado != null ? estado : "").append("\"");
        if (cantidadProyectos != null) {
            sb.append(",\"cantidadProyectos\":").append(cantidadProyectos);
        }
        if (saldoPendiente != null) {
            sb.append(",\"saldoPendiente\":").append(saldoPendiente);
        }
        sb.append("}");
        return sb.toString();
    }

    // ============================================
    // MÉTODOS OVERRIDE
    // ============================================

    @Override
    public String toString() {
        return "Cliente{" +
                "idCliente=" + idCliente +
                ", tipoPersona='" + tipoPersona + '\'' +
                ", nombreRazonSocial='" + nombreRazonSocial + '\'' +
                ", ruc='" + ruc + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", estado='" + estado + '\'' +
                ", fechaRegistro=" + fechaRegistro +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return idCliente != null && idCliente.equals(cliente.idCliente);
    }

    @Override
    public int hashCode() {
        return idCliente != null ? idCliente.hashCode() : 0;
    }

    // ============================================
    // MÉTODOS ESTÁTICOS DE FÁBRICA
    // ============================================

    /**
     * Crea un cliente persona natural.
     * 
     * @param nombre Nombre completo
     * @param telefono Teléfono
     * @param email Email
     * @return Cliente
     */
    public static Cliente crearPersonaNatural(String nombre, String telefono, String email) {
        return new Cliente("Natural", nombre, null, telefono, email, null);
    }

    /**
     * Crea un cliente persona jurídica (empresa).
     * 
     * @param razonSocial Razón social
     * @param ruc RUC de la empresa
     * @param telefono Teléfono
     * @param email Email
     * @param direccion Dirección
     * @return Cliente
     */
    public static Cliente crearPersonaJuridica(String razonSocial, String ruc,
                                               String telefono, String email, String direccion) {
        return new Cliente("Juridica", razonSocial, ruc, telefono, email, direccion);
    }

    /**
     * Crea un cliente desde un DTO.
     * 
     * @param dto DTO del cliente
     * @return Cliente
     */
    public static Cliente fromDTO(com.stamos.dto.ClienteDTO dto) {
        if (dto == null) {
            return null;
        }
        Cliente cliente = new Cliente();
        if (dto.getIdCliente() != null) {
            cliente.setIdCliente(dto.getIdCliente());
        }
        cliente.setTipoPersona(dto.getTipoPersona());
        cliente.setNombreRazonSocial(dto.getNombreRazonSocial());
        cliente.setRuc(dto.getRuc());
        cliente.setTelefono(dto.getTelefono());
        cliente.setEmail(dto.getEmail());
        cliente.setDireccion(dto.getDireccion());
        if (dto.getEstado() != null) {
            cliente.setEstado(dto.getEstado());
        }
        return cliente;
    }

    /**
     * Convierte la entidad a un DTO.
     * 
     * @return DTO del cliente
     */
    public com.stamos.dto.ClienteDTO toDTO() {
        com.stamos.dto.ClienteDTO dto = new com.stamos.dto.ClienteDTO();
        dto.setIdCliente(this.idCliente);
        dto.setTipoPersona(this.tipoPersona);
        dto.setNombreRazonSocial(this.nombreRazonSocial);
        dto.setRuc(this.ruc);
        dto.setTelefono(this.telefono);
        dto.setEmail(this.email);
        dto.setDireccion(this.direccion);
        dto.setEstado(this.estado);
        dto.setFechaRegistro(this.fechaRegistro);
        dto.setCantidadProyectos(this.cantidadProyectos);
        dto.setCantidadProyectosActivos(this.cantidadProyectosActivos);
        dto.setCantidadCotizaciones(this.cantidadCotizaciones);
        dto.setCantidadCotizacionesPendientes(this.cantidadCotizacionesPendientes);
        dto.setTotalFacturado(this.totalFacturado);
        dto.setTotalPagado(this.totalPagado);
        dto.setSaldoPendiente(this.saldoPendiente);
        return dto;
    }
}