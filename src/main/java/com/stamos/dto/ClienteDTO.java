package com.stamos.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO (Data Transfer Object) para la entidad Cliente.
 * 
 * <p>Este DTO se utiliza para transferir datos de clientes entre la capa de
 * servicio y la capa de presentación, evitando exponer directamente la entidad
 * y permitiendo una mejor organización de los datos.</p>
 * 
 * <p>Incluye información personal, de contacto, métricas de proyectos,
 * cotizaciones y datos financieros del cliente.</p>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
public class ClienteDTO {

    // ============================================
    // ATRIBUTOS PRINCIPALES
    // ============================================

    private Integer idCliente;
    private String tipoPersona;
    private String nombreRazonSocial;
    private String ruc;
    private String telefono;
    private String email;
    private String direccion;
    private String estado;

    // ============================================
    // CAMPOS DE AUDITORÍA
    // ============================================

    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaActualizacion;
    private Integer idUsuarioRegis;
    private Integer idUsuarioActualizacion;
    private String nombreUsuarioRegis;
    private String nombreUsuarioActualizacion;

    // ============================================
    // CAMPOS DE MÉTRICAS
    // ============================================

    private Integer cantidadProyectos;
    private Integer cantidadProyectosActivos;
    private Integer cantidadProyectosCompletados;
    private Integer cantidadProyectosAnulados;
    private Integer cantidadCotizaciones;
    private Integer cantidadCotizacionesPendientes;
    private Integer cantidadCotizacionesAprobadas;
    private Integer cantidadCotizacionesAnuladas;
    private Integer cantidadPagos;
    private Integer cantidadPagosValidados;

    // ============================================
    // CAMPOS FINANCIEROS
    // ============================================

    private BigDecimal totalFacturado;
    private BigDecimal totalPagado;
    private BigDecimal saldoPendiente;
    private BigDecimal promedioProyecto;

    // ============================================
    // CAMPOS ADICIONALES
    // ============================================

    private List<ProyectoResumenDTO> proyectosRecientes = new ArrayList<>();
    private List<CotizacionResumenDTO> cotizacionesRecientes = new ArrayList<>();
    private String color;
    private String icono;

    // ============================================
    // CONSTRUCTORES
    // ============================================

    /**
     * Constructor por defecto.
     */
    public ClienteDTO() {
        this.estado = "Activo";
        this.tipoPersona = "Natural";
        this.totalFacturado = BigDecimal.ZERO;
        this.totalPagado = BigDecimal.ZERO;
        this.saldoPendiente = BigDecimal.ZERO;
        this.promedioProyecto = BigDecimal.ZERO;
        this.cantidadProyectos = 0;
        this.cantidadCotizaciones = 0;
        this.cantidadPagos = 0;
        this.proyectosRecientes = new ArrayList<>();
        this.cotizacionesRecientes = new ArrayList<>();
        this.color = "#3B82F6";
        this.icono = "ti ti-user";
    }

    /**
     * Constructor con campos principales.
     * 
     * @param idCliente ID del cliente
     * @param nombreRazonSocial Nombre o razón social
     * @param telefono Teléfono
     * @param email Email
     */
    public ClienteDTO(Integer idCliente, String nombreRazonSocial, String telefono, String email) {
        this();
        this.idCliente = idCliente;
        this.nombreRazonSocial = nombreRazonSocial;
        this.telefono = telefono;
        this.email = email;
    }

    /**
     * Constructor completo.
     * 
     * @param idCliente ID del cliente
     * @param tipoPersona Tipo de persona (Natural/Juridica)
     * @param nombreRazonSocial Nombre o razón social
     * @param ruc RUC
     * @param telefono Teléfono
     * @param email Email
     * @param direccion Dirección
     * @param estado Estado (Activo/Deshabilitado)
     */
    public ClienteDTO(Integer idCliente, String tipoPersona, String nombreRazonSocial,
                     String ruc, String telefono, String email, String direccion, String estado) {
        this();
        this.idCliente = idCliente;
        this.tipoPersona = tipoPersona;
        this.nombreRazonSocial = nombreRazonSocial;
        this.ruc = ruc;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.estado = estado;
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

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Integer getIdUsuarioRegis() {
        return idUsuarioRegis;
    }

    public void setIdUsuarioRegis(Integer idUsuarioRegis) {
        this.idUsuarioRegis = idUsuarioRegis;
    }

    public Integer getIdUsuarioActualizacion() {
        return idUsuarioActualizacion;
    }

    public void setIdUsuarioActualizacion(Integer idUsuarioActualizacion) {
        this.idUsuarioActualizacion = idUsuarioActualizacion;
    }

    public String getNombreUsuarioRegis() {
        return nombreUsuarioRegis;
    }

    public void setNombreUsuarioRegis(String nombreUsuarioRegis) {
        this.nombreUsuarioRegis = nombreUsuarioRegis;
    }

    public String getNombreUsuarioActualizacion() {
        return nombreUsuarioActualizacion;
    }

    public void setNombreUsuarioActualizacion(String nombreUsuarioActualizacion) {
        this.nombreUsuarioActualizacion = nombreUsuarioActualizacion;
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

    public Integer getCantidadProyectosCompletados() {
        return cantidadProyectosCompletados;
    }

    public void setCantidadProyectosCompletados(Integer cantidadProyectosCompletados) {
        this.cantidadProyectosCompletados = cantidadProyectosCompletados;
    }

    public Integer getCantidadProyectosAnulados() {
        return cantidadProyectosAnulados;
    }

    public void setCantidadProyectosAnulados(Integer cantidadProyectosAnulados) {
        this.cantidadProyectosAnulados = cantidadProyectosAnulados;
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

    public Integer getCantidadCotizacionesAprobadas() {
        return cantidadCotizacionesAprobadas;
    }

    public void setCantidadCotizacionesAprobadas(Integer cantidadCotizacionesAprobadas) {
        this.cantidadCotizacionesAprobadas = cantidadCotizacionesAprobadas;
    }

    public Integer getCantidadCotizacionesAnuladas() {
        return cantidadCotizacionesAnuladas;
    }

    public void setCantidadCotizacionesAnuladas(Integer cantidadCotizacionesAnuladas) {
        this.cantidadCotizacionesAnuladas = cantidadCotizacionesAnuladas;
    }

    public Integer getCantidadPagos() {
        return cantidadPagos;
    }

    public void setCantidadPagos(Integer cantidadPagos) {
        this.cantidadPagos = cantidadPagos;
    }

    public Integer getCantidadPagosValidados() {
        return cantidadPagosValidados;
    }

    public void setCantidadPagosValidados(Integer cantidadPagosValidados) {
        this.cantidadPagosValidados = cantidadPagosValidados;
    }

    public BigDecimal getTotalFacturado() {
        return totalFacturado;
    }

    public void setTotalFacturado(BigDecimal totalFacturado) {
        this.totalFacturado = totalFacturado;
    }

    public BigDecimal getTotalPagado() {
        return totalPagado;
    }

    public void setTotalPagado(BigDecimal totalPagado) {
        this.totalPagado = totalPagado;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(BigDecimal saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }

    public BigDecimal getPromedioProyecto() {
        return promedioProyecto;
    }

    public void setPromedioProyecto(BigDecimal promedioProyecto) {
        this.promedioProyecto = promedioProyecto;
    }

    public List<ProyectoResumenDTO> getProyectosRecientes() {
        return proyectosRecientes;
    }

    public void setProyectosRecientes(List<ProyectoResumenDTO> proyectosRecientes) {
        this.proyectosRecientes = proyectosRecientes;
    }

    public List<CotizacionResumenDTO> getCotizacionesRecientes() {
        return cotizacionesRecientes;
    }

    public void setCotizacionesRecientes(List<CotizacionResumenDTO> cotizacionesRecientes) {
        this.cotizacionesRecientes = cotizacionesRecientes;
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
     * Verifica si el cliente está deshabilitado.
     * 
     * @return true si está deshabilitado, false en caso contrario
     */
    public boolean isDeshabilitado() {
        return "Deshabilitado".equals(this.estado);
    }

    /**
     * Verifica si es persona natural.
     * 
     * @return true si es persona natural, false en caso contrario
     */
    public boolean isPersonaNatural() {
        return "Natural".equals(this.tipoPersona);
    }

    /**
     * Verifica si es persona jurídica.
     * 
     * @return true si es persona jurídica, false en caso contrario
     */
    public boolean isPersonaJuridica() {
        return "Juridica".equals(this.tipoPersona);
    }

    /**
     * Verifica si tiene RUC.
     * 
     * @return true si tiene RUC, false en caso contrario
     */
    public boolean tieneRuc() {
        return ruc != null && !ruc.trim().isEmpty();
    }

    /**
     * Verifica si tiene proyectos.
     * 
     * @return true si tiene proyectos, false en caso contrario
     */
    public boolean tieneProyectos() {
        return cantidadProyectos != null && cantidadProyectos > 0;
    }

    /**
     * Verifica si tiene proyectos activos.
     * 
     * @return true si tiene proyectos activos, false en caso contrario
     */
    public boolean tieneProyectosActivos() {
        return cantidadProyectosActivos != null && cantidadProyectosActivos > 0;
    }

    /**
     * Verifica si tiene cotizaciones.
     * 
     * @return true si tiene cotizaciones, false en caso contrario
     */
    public boolean tieneCotizaciones() {
        return cantidadCotizaciones != null && cantidadCotizaciones > 0;
    }

    /**
     * Verifica si tiene cotizaciones pendientes.
     * 
     * @return true si tiene cotizaciones pendientes, false en caso contrario
     */
    public boolean tieneCotizacionesPendientes() {
        return cantidadCotizacionesPendientes != null && cantidadCotizacionesPendientes > 0;
    }

    /**
     * Verifica si tiene saldo pendiente.
     * 
     * @return true si tiene saldo pendiente, false en caso contrario
     */
    public boolean tieneSaldoPendiente() {
        return saldoPendiente != null && saldoPendiente.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Verifica si el cliente puede ser eliminado.
     * 
     * @return true si puede ser eliminado, false en caso contrario
     */
    public boolean puedeSerEliminado() {
        return !tieneProyectos() && !tieneCotizaciones() && !tieneSaldoPendiente();
    }

    /**
     * Calcula el porcentaje de cobranza.
     * 
     * @return Porcentaje de cobranza
     */
    public int getPorcentajeCobranza() {
        if (totalFacturado == null || totalFacturado.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        if (totalPagado == null) {
            return 0;
        }
        BigDecimal porcentaje = totalPagado.multiply(new BigDecimal(100))
                .divide(totalFacturado, 0, RoundingMode.HALF_UP);
        return Math.min(porcentaje.intValue(), 100);
    }

    /**
     * Obtiene el estado del cliente en formato para badge.
     * 
     * @return Estado para badge
     */
    public String getEstadoBadge() {
        if (isActivo()) {
            return "Activo";
        }
        return "Deshabilitado";
    }

    /**
     * Obtiene el tipo de persona en formato legible.
     * 
     * @return Tipo de persona legible
     */
    public String getTipoPersonaLegible() {
        if (tipoPersona == null) return "No especificado";
        return isPersonaNatural() ? "Persona Natural" : "Persona Jurídica";
    }

    /**
     * Obtiene el nombre completo del cliente.
     * 
     * @return Nombre completo
     */
    public String getNombreCompleto() {
        return nombreRazonSocial;
    }

    /**
     * Obtiene el nombre para mostrar.
     * 
     * @return Nombre para mostrar
     */
    public String getNombreDisplay() {
        if (isPersonaJuridica() && tieneRuc()) {
            return nombreRazonSocial + " (" + ruc + ")";
        }
        return nombreRazonSocial;
    }

    // ============================================
    // MÉTODOS DE FORMATO PARA LA VISTA
    // ============================================

    /**
     * Obtiene el estado legible.
     * 
     * @return Estado legible
     */
    public String getEstadoLegible() {
        return isActivo() ? "Activo" : "Deshabilitado";
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
    public String getTipoPersonaDisplay() {
        if (tipoPersona == null) return "No especificado";
        return isPersonaNatural() ? "Persona Natural" : "Persona Jurídica";
    }

    /**
     * Obtiene la clase CSS para el badge del tipo de persona.
     * 
     * @return Clase CSS
     */
    public String getTipoPersonaBadgeClass() {
        if (tipoPersona == null) return "badge-secondary";
        return isPersonaNatural() ? "badge-info" : "badge-primary";
    }

    /**
     * Obtiene el ícono para el tipo de persona.
     * 
     * @return Ícono CSS
     */
    public String getTipoPersonaIcono() {
        if (tipoPersona == null) return "ti ti-user";
        return isPersonaNatural() ? "ti ti-user" : "ti ti-building";
    }

    /**
     * Obtiene el RUC formateado.
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
        return "S/ " + totalFacturado.setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el total pagado formateado.
     * 
     * @return Total pagado formateado
     */
    public String getTotalPagadoFormateado() {
        if (totalPagado == null) return "S/ 0.00";
        return "S/ " + totalPagado.setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el saldo pendiente formateado.
     * 
     * @return Saldo pendiente formateado
     */
    public String getSaldoPendienteFormateado() {
        if (saldoPendiente == null) return "S/ 0.00";
        return "S/ " + saldoPendiente.setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el promedio de proyecto formateado.
     * 
     * @return Promedio de proyecto formateado
     */
    public String getPromedioProyectoFormateado() {
        if (promedioProyecto == null) return "S/ 0.00";
        return "S/ " + promedioProyecto.setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el color para el saldo pendiente.
     * 
     * @return Color en hexadecimal
     */
    public String getSaldoColor() {
        if (saldoPendiente == null || saldoPendiente.compareTo(BigDecimal.ZERO) == 0) {
            return "#10B981";
        }
        return "#EF4444";
    }

    /**
     * Obtiene el color para el porcentaje de cobranza.
     * 
     * @return Color en hexadecimal
     */
    public String getCobranzaColor() {
        int porcentaje = getPorcentajeCobranza();
        if (porcentaje >= 80) return "#10B981";
        if (porcentaje >= 50) return "#F59E0B";
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
        if (tieneProyectos()) {
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

    /**
     * Obtiene la clase CSS para el badge de proyectos.
     * 
     * @return Clase CSS
     */
    public String getProyectosBadgeClass() {
        if (cantidadProyectos == null || cantidadProyectos == 0) {
            return "badge-secondary";
        }
        if (cantidadProyectosActivos != null && cantidadProyectosActivos > 0) {
            return "badge-info";
        }
        return "badge-warning";
    }

    // ============================================
    // MÉTODOS DE CONVERSIÓN
    // ============================================

    /**
     * Convierte a JSON simple.
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

    /**
     * Convierte a JSON completo.
     * 
     * @return JSON completo
     */
    public String toJsonCompleto() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"idCliente\":").append(idCliente).append(",");
        sb.append("\"nombre\":\"").append(nombreRazonSocial != null ? nombreRazonSocial : "").append("\",");
        sb.append("\"ruc\":\"").append(ruc != null ? ruc : "").append("\",");
        sb.append("\"email\":\"").append(email != null ? email : "").append("\",");
        sb.append("\"telefono\":\"").append(telefono != null ? telefono : "").append("\",");
        sb.append("\"tipo\":\"").append(tipoPersona != null ? tipoPersona : "").append("\",");
        sb.append("\"estado\":\"").append(estado != null ? estado : "").append("\",");
        sb.append("\"cantidadProyectos\":").append(cantidadProyectos != null ? cantidadProyectos : 0).append(",");
        sb.append("\"cantidadProyectosActivos\":").append(cantidadProyectosActivos != null ? cantidadProyectosActivos : 0).append(",");
        sb.append("\"cantidadCotizaciones\":").append(cantidadCotizaciones != null ? cantidadCotizaciones : 0).append(",");
        sb.append("\"totalFacturado\":").append(totalFacturado != null ? totalFacturado : 0).append(",");
        sb.append("\"totalPagado\":").append(totalPagado != null ? totalPagado : 0).append(",");
        sb.append("\"saldoPendiente\":").append(saldoPendiente != null ? saldoPendiente : 0);
        sb.append("}");
        return sb.toString();
    }

    // ============================================
    // MÉTODOS OVERRIDE
    // ============================================

    @Override
    public String toString() {
        return "ClienteDTO{" +
                "idCliente=" + idCliente +
                ", nombreRazonSocial='" + nombreRazonSocial + '\'' +
                ", ruc='" + ruc + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", estado='" + estado + '\'' +
                ", cantidadProyectos=" + cantidadProyectos +
                ", totalFacturado=" + totalFacturado +
                ", saldoPendiente=" + saldoPendiente +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClienteDTO that = (ClienteDTO) o;
        return idCliente != null && idCliente.equals(that.idCliente);
    }

    @Override
    public int hashCode() {
        return idCliente != null ? idCliente.hashCode() : 0;
    }

    // ============================================
    // CLASES INTERNAS (DTOs)
    // ============================================

    /**
     * DTO para resumen de proyectos.
     */
    public static class ProyectoResumenDTO {
        private Integer idProyecto;
        private String nombreProyecto;
        private String estado;
        private BigDecimal montoTotal;
        private LocalDateTime fechaInicio;
        private LocalDateTime fechaFinEstimada;

        // Getters y Setters
        public Integer getIdProyecto() { return idProyecto; }
        public void setIdProyecto(Integer idProyecto) { this.idProyecto = idProyecto; }

        public String getNombreProyecto() { return nombreProyecto; }
        public void setNombreProyecto(String nombreProyecto) { this.nombreProyecto = nombreProyecto; }

        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }

        public BigDecimal getMontoTotal() { return montoTotal; }
        public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }

        public LocalDateTime getFechaInicio() { return fechaInicio; }
        public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

        public LocalDateTime getFechaFinEstimada() { return fechaFinEstimada; }
        public void setFechaFinEstimada(LocalDateTime fechaFinEstimada) { this.fechaFinEstimada = fechaFinEstimada; }

        // Métodos de formato
        public String getMontoTotalFormateado() {
            if (montoTotal == null) return "S/ 0.00";
            return "S/ " + montoTotal.setScale(2, RoundingMode.HALF_UP).toString();
        }

        public String getEstadoLegible() {
            if (estado == null) return "Sin estado";
            switch (estado) {
                case "Aprobado": return "Aprobado";
                case "En Diseño": return "En Diseño";
                case "En Producción": return "En Producción";
                case "Instalación/Montaje": return "Instalación/Montaje";
                case "Entregado": return "Entregado";
                case "Anulado": return "Anulado";
                default: return estado;
            }
        }

        public String getEstadoBadgeClass() {
            if (estado == null) return "status-pendiente";
            switch (estado) {
                case "Aprobado": return "status-aprobado";
                case "En Diseño": return "status-diseno";
                case "En Producción": return "status-produccion";
                case "Instalación/Montaje": return "status-instalacion";
                case "Entregado": return "status-entregado";
                case "Anulado": return "status-anulado";
                default: return "status-pendiente";
            }
        }
    }

    /**
     * DTO para resumen de cotizaciones.
     */
    public static class CotizacionResumenDTO {
        private Integer idCotizacion;
        private BigDecimal total;
        private String estado;
        private LocalDateTime fechaEmision;

        // Getters y Setters
        public Integer getIdCotizacion() { return idCotizacion; }
        public void setIdCotizacion(Integer idCotizacion) { this.idCotizacion = idCotizacion; }

        public BigDecimal getTotal() { return total; }
        public void setTotal(BigDecimal total) { this.total = total; }

        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }

        public LocalDateTime getFechaEmision() { return fechaEmision; }
        public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }

        // Métodos de formato
        public String getTotalFormateado() {
            if (total == null) return "S/ 0.00";
            return "S/ " + total.setScale(2, RoundingMode.HALF_UP).toString();
        }

        public String getEstadoLegible() {
            if (estado == null) return "Sin estado";
            switch (estado) {
                case "Pendiente": return "Pendiente";
                case "Aprobada": return "Aprobada";
                case "Anulada": return "Anulada";
                default: return estado;
            }
        }

        public String getEstadoBadgeClass() {
            if (estado == null) return "status-pendiente";
            switch (estado) {
                case "Pendiente": return "status-pendiente";
                case "Aprobada": return "status-validado";
                case "Anulada": return "status-rechazado";
                default: return "status-pendiente";
            }
        }
    }

    // ============================================
    // MÉTODOS ESTÁTICOS DE FÁBRICA
    // ============================================

    /**
     * Crea un DTO desde una entidad.
     * 
     * @param cliente Entidad de cliente
     * @return DTO de cliente
     */
    public static ClienteDTO fromEntity(com.stamos.model.Cliente cliente) {
        if (cliente == null) {
            return null;
        }

        ClienteDTO dto = new ClienteDTO();
        dto.setIdCliente(cliente.getIdCliente());
        dto.setTipoPersona(cliente.getTipoPersona());
        dto.setNombreRazonSocial(cliente.getNombreRazonSocial());
        dto.setRuc(cliente.getRuc());
        dto.setTelefono(cliente.getTelefono());
        dto.setEmail(cliente.getEmail());
        dto.setDireccion(cliente.getDireccion());
        dto.setEstado(cliente.getEstado());
        dto.setFechaRegistro(cliente.getFechaRegistro());
        dto.setFechaActualizacion(cliente.getFechaActualizacion());
        dto.setIdUsuarioRegis(cliente.getIdUsuarioRegis());
        dto.setIdUsuarioActualizacion(cliente.getIdUsuarioActualizacion());

        // Asignar métricas si están disponibles
        if (cliente.getCantidadProyectos() != null) {
            dto.setCantidadProyectos(cliente.getCantidadProyectos());
        }
        if (cliente.getCantidadProyectosActivos() != null) {
            dto.setCantidadProyectosActivos(cliente.getCantidadProyectosActivos());
        }
        if (cliente.getCantidadCotizaciones() != null) {
            dto.setCantidadCotizaciones(cliente.getCantidadCotizaciones());
        }
        if (cliente.getCantidadCotizacionesPendientes() != null) {
            dto.setCantidadCotizacionesPendientes(cliente.getCantidadCotizacionesPendientes());
        }
        if (cliente.getTotalFacturado() != null) {
            dto.setTotalFacturado(cliente.getTotalFacturado());
        }
        if (cliente.getTotalPagado() != null) {
            dto.setTotalPagado(cliente.getTotalPagado());
        }
        if (cliente.getSaldoPendiente() != null) {
            dto.setSaldoPendiente(cliente.getSaldoPendiente());
        }

        return dto;
    }

    /**
     * Crea un DTO desde una entidad con métricas completas.
     * 
     * @param cliente Entidad de cliente
     * @param cantidadProyectos Cantidad de proyectos
     * @param cantidadProyectosActivos Proyectos activos
     * @param cantidadCotizaciones Cantidad de cotizaciones
     * @param totalFacturado Total facturado
     * @param totalPagado Total pagado
     * @return DTO de cliente
     */
    public static ClienteDTO fromEntityWithMetrics(
            com.stamos.model.Cliente cliente,
            Integer cantidadProyectos,
            Integer cantidadProyectosActivos,
            Integer cantidadCotizaciones,
            BigDecimal totalFacturado,
            BigDecimal totalPagado) {
        
        ClienteDTO dto = fromEntity(cliente);
        if (dto != null) {
            dto.setCantidadProyectos(cantidadProyectos);
            dto.setCantidadProyectosActivos(cantidadProyectosActivos);
            dto.setCantidadCotizaciones(cantidadCotizaciones);
            dto.setTotalFacturado(totalFacturado);
            dto.setTotalPagado(totalPagado);
            
            if (totalFacturado != null && totalPagado != null) {
                dto.setSaldoPendiente(totalFacturado.subtract(totalPagado));
            }
        }
        return dto;
    }

    /**
     * Convierte el DTO a una entidad.
     * 
     * @return Entidad de cliente
     */
    public com.stamos.model.Cliente toEntity() {
        com.stamos.model.Cliente cliente = new com.stamos.model.Cliente();
        cliente.setIdCliente(this.idCliente);
        cliente.setTipoPersona(this.tipoPersona);
        cliente.setNombreRazonSocial(this.nombreRazonSocial);
        cliente.setRuc(this.ruc);
        cliente.setTelefono(this.telefono);
        cliente.setEmail(this.email);
        cliente.setDireccion(this.direccion);
        cliente.setEstado(this.estado);
        cliente.setFechaRegistro(this.fechaRegistro);
        cliente.setFechaActualizacion(this.fechaActualizacion);
        cliente.setIdUsuarioRegis(this.idUsuarioRegis);
        cliente.setIdUsuarioActualizacion(this.idUsuarioActualizacion);
        return cliente;
    }
}