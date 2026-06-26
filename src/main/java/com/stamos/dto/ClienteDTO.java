package com.stamos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClienteDTO {

    private Integer idCliente;
    private String tipoPersona;
    private String nombreRazonSocial;
    private String ruc;
    private String telefono;
    private String email;
    private String direccion;
    private String estado;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaActualizacion;
    private Integer idUsuarioRegis;
    private Integer idUsuarioActualizacion;
    private String nombreUsuarioRegis;
    private String nombreUsuarioActualizacion;
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
    private BigDecimal totalFacturado;
    private BigDecimal totalPagado;
    private BigDecimal saldoPendiente;
    private BigDecimal promedioProyecto;
    private List<ProyectoResumenDTO> proyectosRecientes = new ArrayList<>();
    private List<CotizacionResumenDTO> cotizacionesRecientes = new ArrayList<>();
    private String color;
    private String icono;

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

    public ClienteDTO(Integer idCliente, String nombreRazonSocial, String telefono, String email) {
        this();
        this.idCliente = idCliente;
        this.nombreRazonSocial = nombreRazonSocial;
        this.telefono = telefono;
        this.email = email;
    }

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

    public boolean isActivo() {
        return "Activo".equals(this.estado);
    }

    public boolean isDeshabilitado() {
        return "Deshabilitado".equals(this.estado);
    }

    public boolean isPersonaNatural() {
        return "Natural".equals(this.tipoPersona);
    }

    public boolean isPersonaJuridica() {
        return "Juridica".equals(this.tipoPersona);
    }

    public boolean tieneRuc() {
        return ruc != null && !ruc.trim().isEmpty();
    }

    public boolean tieneProyectos() {
        return cantidadProyectos != null && cantidadProyectos > 0;
    }

    public boolean tieneProyectosActivos() {
        return cantidadProyectosActivos != null && cantidadProyectosActivos > 0;
    }

    public boolean tieneCotizaciones() {
        return cantidadCotizaciones != null && cantidadCotizaciones > 0;
    }

    public boolean tieneCotizacionesPendientes() {
        return cantidadCotizacionesPendientes != null && cantidadCotizacionesPendientes > 0;
    }

    public boolean tieneSaldoPendiente() {
        return saldoPendiente != null && saldoPendiente.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean puedeSerEliminado() {
        return !tieneProyectos() && !tieneCotizaciones() && !tieneSaldoPendiente();
    }

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

    public static class ProyectoResumenDTO {
        private Integer idProyecto;
        private String nombreProyecto;
        private String estado;
        private BigDecimal montoTotal;
        private LocalDateTime fechaInicio;
        private LocalDateTime fechaFinEstimada;

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
    }

    public static class CotizacionResumenDTO {
        private Integer idCotizacion;
        private BigDecimal total;
        private String estado;
        private LocalDateTime fechaEmision;

        public Integer getIdCotizacion() { return idCotizacion; }
        public void setIdCotizacion(Integer idCotizacion) { this.idCotizacion = idCotizacion; }

        public BigDecimal getTotal() { return total; }
        public void setTotal(BigDecimal total) { this.total = total; }

        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }

        public LocalDateTime getFechaEmision() { return fechaEmision; }
        public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }
    }
}