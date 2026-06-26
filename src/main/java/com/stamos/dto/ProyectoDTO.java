package com.stamos.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ProyectoDTO {

    private Integer idProyecto;
    private Integer idCotizacion;
    private String codigoCotizacion;
    private Integer idCliente;
    private String nombreCliente;
    private String rucCliente;
    private String emailCliente;
    private String telefonoCliente;
    private String nombreProyecto;
    private Integer idEstadoActual;
    private String estado;
    private LocalDate fechaInicio;
    private LocalDate fechaFinEstimada;
    private LocalDate fechaEntregaReal;
    private LocalDate fechaVencimiento;
    private BigDecimal montoTotal;
    private BigDecimal totalPagado;
    private BigDecimal saldoPendiente;
    private Integer porcentajePagado;
    private String ultimoCambioEstado;
    private LocalDateTime fechaUltimoCambio;
    private String responsableUltimoCambio;
    private Integer numeroCambiosEstado;
    private List<DocumentoDTO> documentos = new ArrayList<>();
    private List<PagoDTO> pagos = new ArrayList<>();
    private List<CambioEstadoDTO> historialEstados = new ArrayList<>();
    private String observaciones;
    private Integer diasTranscurridos;
    private Integer diasRestantes;
    private boolean vencido;
    private boolean completado;
    private boolean anulado;
    private String prioridad;

    public ProyectoDTO() {
        this.montoTotal = BigDecimal.ZERO;
        this.totalPagado = BigDecimal.ZERO;
        this.saldoPendiente = BigDecimal.ZERO;
        this.porcentajePagado = 0;
        this.numeroCambiosEstado = 0;
        this.documentos = new ArrayList<>();
        this.pagos = new ArrayList<>();
        this.historialEstados = new ArrayList<>();
        this.vencido = false;
        this.completado = false;
        this.anulado = false;
        this.prioridad = "Normal";
    }

    public ProyectoDTO(Integer idProyecto, String nombreProyecto, String estado, BigDecimal montoTotal) {
        this();
        this.idProyecto = idProyecto;
        this.nombreProyecto = nombreProyecto;
        this.estado = estado;
        this.montoTotal = montoTotal;
    }

    public ProyectoDTO(Integer idProyecto, Integer idCotizacion, Integer idCliente,
                       String nombreCliente, String nombreProyecto, String estado,
                       LocalDate fechaInicio, LocalDate fechaFinEstimada, BigDecimal montoTotal) {
        this();
        this.idProyecto = idProyecto;
        this.idCotizacion = idCotizacion;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.nombreProyecto = nombreProyecto;
        this.estado = estado;
        this.fechaInicio = fechaInicio;
        this.fechaFinEstimada = fechaFinEstimada;
        this.montoTotal = montoTotal;
    }

    public Integer getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(Integer idProyecto) {
        this.idProyecto = idProyecto;
    }

    public Integer getIdCotizacion() {
        return idCotizacion;
    }

    public void setIdCotizacion(Integer idCotizacion) {
        this.idCotizacion = idCotizacion;
    }

    public String getCodigoCotizacion() {
        return codigoCotizacion;
    }

    public void setCodigoCotizacion(String codigoCotizacion) {
        this.codigoCotizacion = codigoCotizacion;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getRucCliente() {
        return rucCliente;
    }

    public void setRucCliente(String rucCliente) {
        this.rucCliente = rucCliente;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public void setEmailCliente(String emailCliente) {
        this.emailCliente = emailCliente;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }

    public String getNombreProyecto() {
        return nombreProyecto;
    }

    public void setNombreProyecto(String nombreProyecto) {
        this.nombreProyecto = nombreProyecto;
    }

    public Integer getIdEstadoActual() {
        return idEstadoActual;
    }

    public void setIdEstadoActual(Integer idEstadoActual) {
        this.idEstadoActual = idEstadoActual;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
        actualizarEstadoFlags();
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
        calcularDias();
    }

    public LocalDate getFechaFinEstimada() {
        return fechaFinEstimada;
    }

    public void setFechaFinEstimada(LocalDate fechaFinEstimada) {
        this.fechaFinEstimada = fechaFinEstimada;
        calcularDias();
    }

    public LocalDate getFechaEntregaReal() {
        return fechaEntregaReal;
    }

    public void setFechaEntregaReal(LocalDate fechaEntregaReal) {
        this.fechaEntregaReal = fechaEntregaReal;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
        actualizarVencimiento();
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
        calcularFinanzas();
    }

    public BigDecimal getTotalPagado() {
        return totalPagado;
    }

    public void setTotalPagado(BigDecimal totalPagado) {
        this.totalPagado = totalPagado;
        calcularFinanzas();
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(BigDecimal saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }

    public Integer getPorcentajePagado() {
        return porcentajePagado != null ? porcentajePagado : 0;
    }

    public void setPorcentajePagado(Integer porcentajePagado) {
        this.porcentajePagado = porcentajePagado;
    }

    public String getUltimoCambioEstado() {
        return ultimoCambioEstado;
    }

    public void setUltimoCambioEstado(String ultimoCambioEstado) {
        this.ultimoCambioEstado = ultimoCambioEstado;
    }

    public LocalDateTime getFechaUltimoCambio() {
        return fechaUltimoCambio;
    }

    public void setFechaUltimoCambio(LocalDateTime fechaUltimoCambio) {
        this.fechaUltimoCambio = fechaUltimoCambio;
    }

    public String getResponsableUltimoCambio() {
        return responsableUltimoCambio;
    }

    public void setResponsableUltimoCambio(String responsableUltimoCambio) {
        this.responsableUltimoCambio = responsableUltimoCambio;
    }

    public Integer getNumeroCambiosEstado() {
        return numeroCambiosEstado != null ? numeroCambiosEstado : 0;
    }

    public void setNumeroCambiosEstado(Integer numeroCambiosEstado) {
        this.numeroCambiosEstado = numeroCambiosEstado;
    }

    public List<DocumentoDTO> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<DocumentoDTO> documentos) {
        this.documentos = documentos;
    }

    public List<PagoDTO> getPagos() {
        return pagos;
    }

    public void setPagos(List<PagoDTO> pagos) {
        this.pagos = pagos;
    }

    public List<CambioEstadoDTO> getHistorialEstados() {
        return historialEstados;
    }

    public void setHistorialEstados(List<CambioEstadoDTO> historialEstados) {
        this.historialEstados = historialEstados;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Integer getDiasTranscurridos() {
        return diasTranscurridos != null ? diasTranscurridos : 0;
    }

    public void setDiasTranscurridos(Integer diasTranscurridos) {
        this.diasTranscurridos = diasTranscurridos;
    }

    public Integer getDiasRestantes() {
        return diasRestantes != null ? diasRestantes : 0;
    }

    public void setDiasRestantes(Integer diasRestantes) {
        this.diasRestantes = diasRestantes;
    }

    public boolean isVencido() {
        return vencido;
    }

    public void setVencido(boolean vencido) {
        this.vencido = vencido;
    }

    public boolean isCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }

    public boolean isAnulado() {
        return anulado;
    }

    public void setAnulado(boolean anulado) {
        this.anulado = anulado;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    private void actualizarEstadoFlags() {
        this.completado = "Entregado".equals(this.estado);
        this.anulado = "Anulado".equals(this.estado);
        if (this.completado || this.anulado) {
            this.vencido = false;
        } else {
            actualizarVencimiento();
        }
    }

    private void actualizarVencimiento() {
        if (fechaVencimiento != null && !completado && !anulado) {
            this.vencido = LocalDate.now().isAfter(fechaVencimiento);
        }
    }

    private void calcularDias() {
        LocalDate hoy = LocalDate.now();
        
        if (fechaInicio != null) {
            if (fechaInicio.isAfter(hoy)) {
                this.diasTranscurridos = 0;
            } else {
                this.diasTranscurridos = (int) java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, hoy);
            }
        } else {
            this.diasTranscurridos = 0;
        }

        if (fechaFinEstimada != null) {
            if (fechaFinEstimada.isBefore(hoy)) {
                this.diasRestantes = 0;
            } else {
                this.diasRestantes = (int) java.time.temporal.ChronoUnit.DAYS.between(hoy, fechaFinEstimada);
            }
        } else {
            this.diasRestantes = 0;
        }
    }

    private void calcularFinanzas() {
        if (montoTotal != null && totalPagado != null) {
            this.saldoPendiente = montoTotal.subtract(totalPagado);
            if (this.saldoPendiente.compareTo(BigDecimal.ZERO) < 0) {
                this.saldoPendiente = BigDecimal.ZERO;
            }

            if (montoTotal.compareTo(BigDecimal.ZERO) > 0) {
                this.porcentajePagado = totalPagado.multiply(new BigDecimal(100))
                        .divide(montoTotal, 0, RoundingMode.HALF_UP)
                        .intValue();
                if (this.porcentajePagado > 100) {
                    this.porcentajePagado = 100;
                }
            } else {
                this.porcentajePagado = 0;
            }
        } else {
            this.saldoPendiente = BigDecimal.ZERO;
            this.porcentajePagado = 0;
        }
    }

    public void agregarDocumento(DocumentoDTO documento) {
        if (documento != null && this.documentos != null) {
            this.documentos.add(documento);
        }
    }

    public void agregarPago(PagoDTO pago) {
        if (pago != null && this.pagos != null) {
            this.pagos.add(pago);
            BigDecimal total = this.pagos.stream()
                    .map(PagoDTO::getMonto)
                    .filter(m -> m != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            this.totalPagado = total;
            calcularFinanzas();
        }
    }

    public void agregarCambioEstado(CambioEstadoDTO cambio) {
        if (cambio != null && this.historialEstados != null) {
            this.historialEstados.add(cambio);
            this.numeroCambiosEstado = this.historialEstados.size();
            
            if (!this.historialEstados.isEmpty()) {
                CambioEstadoDTO ultimo = this.historialEstados.get(this.historialEstados.size() - 1);
                this.ultimoCambioEstado = ultimo.getEstadoNuevo();
                this.fechaUltimoCambio = ultimo.getFechaCambio();
                this.responsableUltimoCambio = ultimo.getResponsable();
            }
        }
    }

    public boolean tieneDocumentos() {
        return documentos != null && !documentos.isEmpty();
    }

    public boolean tienePagos() {
        return pagos != null && !pagos.isEmpty();
    }

    public boolean estaActivo() {
        return estado != null && 
               !estado.equals("Entregado") && 
               !estado.equals("Anulado");
    }

    public boolean puedeEditar() {
        return estaActivo();
    }

    public boolean puedeAnular() {
        return estaActivo();
    }

    public boolean puedeEntregar() {
        return estaActivo() && estado != null && !estado.equals("Instalación/Montaje");
    }

    public int getDocumentosByTipo(String tipo) {
        if (documentos == null || tipo == null) {
            return 0;
        }
        return (int) documentos.stream()
                .filter(d -> tipo.equals(d.getTipo()))
                .count();
    }

    public int getTotalDocumentos() {
        return documentos != null ? documentos.size() : 0;
    }

    public int getTotalPagos() {
        return pagos != null ? pagos.size() : 0;
    }

    public int getTotalCambiosEstado() {
        return historialEstados != null ? historialEstados.size() : 0;
    }

    public String getCodigoProyecto() {
        if (idProyecto == null) {
            return "PRO-00000";
        }
        return "PRO-" + String.format("%05d", idProyecto);
    }

    public static ProyectoDTO fromEntity(com.stamos.model.Proyecto proyecto) {
        if (proyecto == null) {
            return null;
        }

        ProyectoDTO dto = new ProyectoDTO();
        dto.setIdProyecto(proyecto.getIdProyecto());
        dto.setIdCotizacion(proyecto.getIdCotizacion());
        dto.setIdCliente(proyecto.getIdCliente());
        dto.setNombreProyecto(proyecto.getNombreProyecto());
        dto.setEstado(proyecto.getEstado());
        dto.setIdEstadoActual(proyecto.getIdEstadoActual());
        dto.setFechaInicio(proyecto.getFechaInicio());
        dto.setFechaFinEstimada(proyecto.getFechaFinEstimada());
        dto.setFechaEntregaReal(proyecto.getFechaEntregaReal());
        dto.setFechaVencimiento(proyecto.getFechaVencimiento());
        dto.setMontoTotal(proyecto.getMontoTotal());
        dto.setObservaciones(proyecto.getObservaciones());

        return dto;
    }

    @Override
    public String toString() {
        return "ProyectoDTO{" +
                "idProyecto=" + idProyecto +
                ", codigo='" + getCodigoProyecto() + '\'' +
                ", nombreProyecto='" + nombreProyecto + '\'' +
                ", cliente='" + nombreCliente + '\'' +
                ", estado='" + estado + '\'' +
                ", montoTotal=" + montoTotal +
                ", porcentajePagado=" + getPorcentajePagado() +
                ", totalDocumentos=" + getTotalDocumentos() +
                ", totalPagos=" + getTotalPagos() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProyectoDTO that = (ProyectoDTO) o;
        return idProyecto != null && idProyecto.equals(that.idProyecto);
    }

    @Override
    public int hashCode() {
        return idProyecto != null ? idProyecto.hashCode() : 0;
    }

    public static class DocumentoDTO {
        private Integer idDocumento;
        private Integer idProyecto;
        private String tipo;
        private String nombreArchivo;
        private String urlArchivo;
        private Long tamanoBytes;
        private LocalDateTime fechaSubida;
        private String usuarioSubio;

        public Integer getIdDocumento() { return idDocumento; }
        public void setIdDocumento(Integer idDocumento) { this.idDocumento = idDocumento; }

        public Integer getIdProyecto() { return idProyecto; }
        public void setIdProyecto(Integer idProyecto) { this.idProyecto = idProyecto; }

        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }

        public String getNombreArchivo() { return nombreArchivo; }
        public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }

        public String getUrlArchivo() { return urlArchivo; }
        public void setUrlArchivo(String urlArchivo) { this.urlArchivo = urlArchivo; }

        public Long getTamanoBytes() { return tamanoBytes; }
        public void setTamanoBytes(Long tamanoBytes) { this.tamanoBytes = tamanoBytes; }

        public LocalDateTime getFechaSubida() { return fechaSubida; }
        public void setFechaSubida(LocalDateTime fechaSubida) { this.fechaSubida = fechaSubida; }

        public String getUsuarioSubio() { return usuarioSubio; }
        public void setUsuarioSubio(String usuarioSubio) { this.usuarioSubio = usuarioSubio; }
    }

    public static class PagoDTO {
        private Integer idPago;
        private Integer idProyecto;
        private BigDecimal monto;
        private LocalDateTime fechaPago;
        private String metodoPago;
        private String estadoPago;
        private String observaciones;

        public Integer getIdPago() { return idPago; }
        public void setIdPago(Integer idPago) { this.idPago = idPago; }

        public Integer getIdProyecto() { return idProyecto; }
        public void setIdProyecto(Integer idProyecto) { this.idProyecto = idProyecto; }

        public BigDecimal getMonto() { return monto; }
        public void setMonto(BigDecimal monto) { this.monto = monto; }

        public LocalDateTime getFechaPago() { return fechaPago; }
        public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }

        public String getMetodoPago() { return metodoPago; }
        public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

        public String getEstadoPago() { return estadoPago; }
        public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }

        public String getObservaciones() { return observaciones; }
        public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    }

    public static class CambioEstadoDTO {
        private Integer idCambio;
        private Integer idProyecto;
        private String estadoAnterior;
        private String estadoNuevo;
        private LocalDateTime fechaCambio;
        private String responsable;
        private String comentario;

        public Integer getIdCambio() { return idCambio; }
        public void setIdCambio(Integer idCambio) { this.idCambio = idCambio; }

        public Integer getIdProyecto() { return idProyecto; }
        public void setIdProyecto(Integer idProyecto) { this.idProyecto = idProyecto; }

        public String getEstadoAnterior() { return estadoAnterior; }
        public void setEstadoAnterior(String estadoAnterior) { this.estadoAnterior = estadoAnterior; }

        public String getEstadoNuevo() { return estadoNuevo; }
        public void setEstadoNuevo(String estadoNuevo) { this.estadoNuevo = estadoNuevo; }

        public LocalDateTime getFechaCambio() { return fechaCambio; }
        public void setFechaCambio(LocalDateTime fechaCambio) { this.fechaCambio = fechaCambio; }

        public String getResponsable() { return responsable; }
        public void setResponsable(String responsable) { this.responsable = responsable; }

        public String getComentario() { return comentario; }
        public void setComentario(String comentario) { this.comentario = comentario; }
    }
}