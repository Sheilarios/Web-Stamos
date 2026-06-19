package com.stamos.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO (Data Transfer Object) para la entidad Proyecto.
 * 
 * <p>Este DTO se utiliza para transferir datos de proyectos entre la capa de
 * servicio y la capa de presentación, evitando exponer directamente la entidad
 * y permitiendo una mejor organización de los datos.</p>
 * 
 * <p>Incluye información del cliente, cotización asociada, estados,
 * métricas financieras y documentos del proyecto.</p>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
public class ProyectoDTO {

    // ============================================
    // ATRIBUTOS PRINCIPALES
    // ============================================

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

    // ============================================
    // CAMPOS FINANCIEROS
    // ============================================

    private BigDecimal totalPagado;
    private BigDecimal saldoPendiente;
    private Integer porcentajePagado;

    // ============================================
    // CAMPOS DE AUDITORÍA
    // ============================================

    private String ultimoCambioEstado;
    private LocalDateTime fechaUltimoCambio;
    private String responsableUltimoCambio;
    private Integer numeroCambiosEstado;

    // ============================================
    // LISTAS RELACIONADAS
    // ============================================

    private List<DocumentoDTO> documentos = new ArrayList<>();
    private List<PagoDTO> pagos = new ArrayList<>();
    private List<CambioEstadoDTO> historialEstados = new ArrayList<>();

    // ============================================
    // CAMPOS ADICIONALES
    // ============================================

    private String observaciones;
    private Integer diasTranscurridos;
    private Integer diasRestantes;
    private boolean vencido;
    private boolean completado;
    private boolean anulado;
    private String prioridad;

    // ============================================
    // CONSTRUCTORES
    // ============================================

    /**
     * Constructor por defecto.
     */
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

    /**
     * Constructor con los campos principales.
     * 
     * @param idProyecto ID del proyecto
     * @param nombreProyecto Nombre del proyecto
     * @param estado Estado actual
     * @param montoTotal Monto total
     */
    public ProyectoDTO(Integer idProyecto, String nombreProyecto, String estado, BigDecimal montoTotal) {
        this();
        this.idProyecto = idProyecto;
        this.nombreProyecto = nombreProyecto;
        this.estado = estado;
        this.montoTotal = montoTotal;
    }

    /**
     * Constructor completo.
     * 
     * @param idProyecto ID del proyecto
     * @param idCotizacion ID de la cotización
     * @param idCliente ID del cliente
     * @param nombreCliente Nombre del cliente
     * @param nombreProyecto Nombre del proyecto
     * @param estado Estado actual
     * @param fechaInicio Fecha de inicio
     * @param fechaFinEstimada Fecha estimada de fin
     * @param montoTotal Monto total
     */
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

    // ============================================
    // GETTERS Y SETTERS
    // ============================================

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
        return porcentajePagado;
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
        return numeroCambiosEstado;
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
        return diasTranscurridos;
    }

    public void setDiasTranscurridos(Integer diasTranscurridos) {
        this.diasTranscurridos = diasTranscurridos;
    }

    public Integer getDiasRestantes() {
        return diasRestantes;
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

    // ============================================
    // MÉTODOS DE NEGOCIO
    // ============================================

    /**
     * Actualiza las flags de estado (completado, anulado).
     */
    private void actualizarEstadoFlags() {
        this.completado = "Entregado".equals(this.estado);
        this.anulado = "Anulado".equals(this.estado);
        
        // Si está completado o anulado, no está vencido
        if (this.completado || this.anulado) {
            this.vencido = false;
        } else {
            actualizarVencimiento();
        }
    }

    /**
     * Actualiza el estado de vencimiento.
     */
    private void actualizarVencimiento() {
        if (fechaVencimiento != null && !completado && !anulado) {
            this.vencido = LocalDate.now().isAfter(fechaVencimiento);
        }
    }

    /**
     * Calcula los días transcurridos y restantes.
     */
    private void calcularDias() {
        LocalDate hoy = LocalDate.now();
        
        // Días transcurridos
        if (fechaInicio != null) {
            if (fechaInicio.isAfter(hoy)) {
                this.diasTranscurridos = 0;
            } else {
                this.diasTranscurridos = (int) java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, hoy);
            }
        } else {
            this.diasTranscurridos = 0;
        }

        // Días restantes
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

    /**
     * Calcula las métricas financieras (porcentaje pagado, saldo pendiente).
     */
    private void calcularFinanzas() {
        if (montoTotal != null && totalPagado != null) {
            // Saldo pendiente
            this.saldoPendiente = montoTotal.subtract(totalPagado);
            if (this.saldoPendiente.compareTo(BigDecimal.ZERO) < 0) {
                this.saldoPendiente = BigDecimal.ZERO;
            }

            // Porcentaje pagado
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

    /**
     * Agrega un documento al proyecto.
     * 
     * @param documento DTO del documento
     */
    public void agregarDocumento(DocumentoDTO documento) {
        if (documento != null && this.documentos != null) {
            this.documentos.add(documento);
        }
    }

    /**
     * Agrega un pago al proyecto.
     * 
     * @param pago DTO del pago
     */
    public void agregarPago(PagoDTO pago) {
        if (pago != null && this.pagos != null) {
            this.pagos.add(pago);
            // Recalcular finanzas
            BigDecimal total = this.pagos.stream()
                    .map(PagoDTO::getMonto)
                    .filter(m -> m != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            this.totalPagado = total;
            calcularFinanzas();
        }
    }

    /**
     * Agrega un cambio de estado al historial.
     * 
     * @param cambio DTO del cambio de estado
     */
    public void agregarCambioEstado(CambioEstadoDTO cambio) {
        if (cambio != null && this.historialEstados != null) {
            this.historialEstados.add(cambio);
            this.numeroCambiosEstado = this.historialEstados.size();
            
            // Actualizar último cambio
            if (this.historialEstados.size() > 0) {
                CambioEstadoDTO ultimo = this.historialEstados.get(this.historialEstados.size() - 1);
                this.ultimoCambioEstado = ultimo.getEstadoNuevo();
                this.fechaUltimoCambio = ultimo.getFechaCambio();
                this.responsableUltimoCambio = ultimo.getResponsable();
            }
        }
    }

    /**
     * Verifica si el proyecto tiene documentos.
     * 
     * @return true si tiene documentos, false en caso contrario
     */
    public boolean tieneDocumentos() {
        return documentos != null && !documentos.isEmpty();
    }

    /**
     * Verifica si el proyecto tiene pagos.
     * 
     * @return true si tiene pagos, false en caso contrario
     */
    public boolean tienePagos() {
        return pagos != null && !pagos.isEmpty();
    }

    /**
     * Verifica si el proyecto está activo.
     * 
     * @return true si está activo, false en caso contrario
     */
    public boolean estaActivo() {
        return estado != null && 
               !estado.equals("Entregado") && 
               !estado.equals("Anulado");
    }

    /**
     * Verifica si el proyecto puede ser editado.
     * 
     * @return true si se puede editar, false en caso contrario
     */
    public boolean puedeEditar() {
        return estaActivo();
    }

    /**
     * Verifica si el proyecto puede ser anulado.
     * 
     * @return true si se puede anular, false en caso contrario
     */
    public boolean puedeAnular() {
        return estaActivo();
    }

    /**
     * Verifica si el proyecto puede ser entregado.
     * 
     * @return true si se puede entregar, false en caso contrario
     */
    public boolean puedeEntregar() {
        return estaActivo() && estado != null && !estado.equals("Instalación/Montaje");
    }

    /**
     * Obtiene el número de documentos por tipo.
     * 
     * @param tipo Tipo de documento (Render, Plano, Boceto, etc.)
     * @return Cantidad de documentos
     */
    public int getDocumentosByTipo(String tipo) {
        if (documentos == null || tipo == null) {
            return 0;
        }
        return (int) documentos.stream()
                .filter(d -> tipo.equals(d.getTipo()))
                .count();
    }

    /**
     * Obtiene el total de documentos.
     * 
     * @return Total de documentos
     */
    public int getTotalDocumentos() {
        return documentos != null ? documentos.size() : 0;
    }

    /**
     * Obtiene el total de pagos.
     * 
     * @return Total de pagos
     */
    public int getTotalPagos() {
        return pagos != null ? pagos.size() : 0;
    }

    /**
     * Obtiene el total de cambios de estado.
     * 
     * @return Total de cambios
     */
    public int getTotalCambiosEstado() {
        return historialEstados != null ? historialEstados.size() : 0;
    }

    // ============================================
    // MÉTODOS DE FORMATO PARA LA VISTA
    // ============================================

    /**
     * Obtiene el código del proyecto en formato legible.
     * 
     * @return Código del proyecto
     */
    public String getCodigoProyecto() {
        if (idProyecto == null) {
            return "PRO-00000";
        }
        return "PRO-" + String.format("%05d", idProyecto);
    }

    /**
     * Obtiene el monto total formateado como moneda.
     * 
     * @return Monto formateado
     */
    public String getMontoTotalFormateado() {
        if (montoTotal == null) {
            return "S/ 0.00";
        }
        return "S/ " + montoTotal.setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el total pagado formateado como moneda.
     * 
     * @return Total pagado formateado
     */
    public String getTotalPagadoFormateado() {
        if (totalPagado == null) {
            return "S/ 0.00";
        }
        return "S/ " + totalPagado.setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el saldo pendiente formateado como moneda.
     * 
     * @return Saldo pendiente formateado
     */
    public String getSaldoPendienteFormateado() {
        if (saldoPendiente == null) {
            return "S/ 0.00";
        }
        return "S/ " + saldoPendiente.setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el porcentaje pagado formateado.
     * 
     * @return Porcentaje formateado
     */
    public String getPorcentajePagadoFormateado() {
        if (porcentajePagado == null) {
            return "0%";
        }
        return porcentajePagado + "%";
    }

    /**
     * Obtiene la fecha de inicio formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaInicioFormateada() {
        if (fechaInicio == null) {
            return "-";
        }
        return fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Obtiene la fecha estimada de fin formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaFinEstimadaFormateada() {
        if (fechaFinEstimada == null) {
            return "-";
        }
        return fechaFinEstimada.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Obtiene la fecha de entrega real formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaEntregaRealFormateada() {
        if (fechaEntregaReal == null) {
            return "-";
        }
        return fechaEntregaReal.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Obtiene la fecha de vencimiento formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaVencimientoFormateada() {
        if (fechaVencimiento == null) {
            return "-";
        }
        return fechaVencimiento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Obtiene la fecha del último cambio formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaUltimoCambioFormateada() {
        if (fechaUltimoCambio == null) {
            return "-";
        }
        return fechaUltimoCambio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    /**
     * Obtiene la clase CSS para el badge del estado.
     * 
     * @return Clase CSS
     */
    public String getEstadoBadgeClass() {
        if (estado == null) return "status-pendiente";
        
        switch (estado) {
            case "Aprobado":
                return "status-aprobado";
            case "En Diseño":
                return "status-diseno";
            case "En Producción":
                return "status-produccion";
            case "Instalación/Montaje":
                return "status-instalacion";
            case "Entregado":
                return "status-entregado";
            case "Anulado":
                return "status-anulado";
            default:
                return "status-pendiente";
        }
    }

    /**
     * Obtiene el estado en formato legible.
     * 
     * @return Estado legible
     */
    public String getEstadoLegible() {
        if (estado == null) return "Sin estado";
        
        switch (estado) {
            case "Aprobado":
                return "Aprobado";
            case "En Diseño":
                return "En Diseño";
            case "En Producción":
                return "En Producción";
            case "Instalación/Montaje":
                return "Instalación/Montaje";
            case "Entregado":
                return "Entregado";
            case "Anulado":
                return "Anulado";
            default:
                return estado;
        }
    }

    /**
     * Obtiene el ícono para el estado.
     * 
     * @return Ícono CSS
     */
    public String getEstadoIcono() {
        if (estado == null) return "ti ti-circle";
        
        switch (estado) {
            case "Aprobado":
                return "ti ti-check-circle";
            case "En Diseño":
                return "ti ti-pencil";
            case "En Producción":
                return "ti ti-tools";
            case "Instalación/Montaje":
                return "ti ti-truck";
            case "Entregado":
                return "ti ti-check";
            case "Anulado":
                return "ti ti-x-circle";
            default:
                return "ti ti-circle";
        }
    }

    /**
     * Obtiene el color para el estado.
     * 
     * @return Color en hexadecimal
     */
    public String getEstadoColor() {
        if (estado == null) return "#6B7280";
        
        switch (estado) {
            case "Aprobado":
                return "#1A56DB";
            case "En Diseño":
                return "#F59E0B";
            case "En Producción":
                return "#3B82F6";
            case "Instalación/Montaje":
                return "#8B5CF6";
            case "Entregado":
                return "#10B981";
            case "Anulado":
                return "#EF4444";
            default:
                return "#6B7280";
        }
    }

    /**
     * Obtiene la clase CSS para el badge de prioridad.
     * 
     * @return Clase CSS
     */
    public String getPrioridadBadgeClass() {
        if (prioridad == null) return "badge-secondary";
        
        switch (prioridad) {
            case "Alta":
                return "badge-danger";
            case "Media":
                return "badge-warning";
            case "Baja":
                return "badge-success";
            default:
                return "badge-secondary";
        }
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
        if (saldoPendiente.compareTo(montoTotal) == 0) {
            return "#EF4444";
        }
        return "#F59E0B";
    }

    /**
     * Obtiene el mensaje de estado del saldo.
     * 
     * @return Mensaje descriptivo
     */
    public String getSaldoMensaje() {
        if (saldoPendiente == null || saldoPendiente.compareTo(BigDecimal.ZERO) == 0) {
            return "Completamente pagado";
        }
        if (saldoPendiente.compareTo(montoTotal) == 0) {
            return "Sin pagos registrados";
        }
        return "Pago parcial";
    }

    /**
     * Obtiene el progreso del proyecto en porcentaje.
     * 
     * @return Porcentaje de progreso
     */
    public int getProgreso() {
        if (estado == null) return 0;
        
        switch (estado) {
            case "Aprobado":
                return 10;
            case "En Diseño":
                return 30;
            case "En Producción":
                return 60;
            case "Instalación/Montaje":
                return 85;
            case "Entregado":
                return 100;
            case "Anulado":
                return 0;
            default:
                return 0;
        }
    }

    /**
     * Obtiene el color del progreso.
     * 
     * @return Color en hexadecimal
     */
    public String getProgresoColor() {
        int progreso = getProgreso();
        if (progreso >= 100) return "#10B981";
        if (progreso >= 60) return "#3B82F6";
        if (progreso >= 30) return "#F59E0B";
        return "#EF4444";
    }

    /**
     * Obtiene el mensaje de progreso.
     * 
     * @return Mensaje descriptivo
     */
    public String getProgresoMensaje() {
        int progreso = getProgreso();
        if (progreso >= 100) return "Proyecto completado";
        if (progreso >= 60) return "En fase avanzada";
        if (progreso >= 30) return "En proceso";
        return "Recién iniciado";
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
        sb.append("\"idProyecto\":").append(idProyecto).append(",");
        sb.append("\"codigo\":\"").append(getCodigoProyecto()).append("\",");
        sb.append("\"nombre\":\"").append(nombreProyecto != null ? nombreProyecto : "").append("\",");
        sb.append("\"cliente\":\"").append(nombreCliente != null ? nombreCliente : "").append("\",");
        sb.append("\"estado\":\"").append(estado != null ? estado : "").append("\",");
        sb.append("\"montoTotal\":").append(montoTotal != null ? montoTotal : 0).append(",");
        sb.append("\"porcentajePagado\":").append(porcentajePagado != null ? porcentajePagado : 0);
        sb.append("}");
        return sb.toString();
    }

    // ============================================
    // MÉTODOS OVERRIDE
    // ============================================

    @Override
    public String toString() {
        return "ProyectoDTO{" +
                "idProyecto=" + idProyecto +
                ", codigo='" + getCodigoProyecto() + '\'' +
                ", nombreProyecto='" + nombreProyecto + '\'' +
                ", cliente='" + nombreCliente + '\'' +
                ", estado='" + estado + '\'' +
                ", montoTotal=" + montoTotal +
                ", porcentajePagado=" + porcentajePagado +
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

    // ============================================
    // CLASES INTERNAS (DTOs)
    // ============================================

    /**
     * DTO para documentos del proyecto.
     */
    public static class DocumentoDTO {
        private Integer idDocumento;
        private Integer idProyecto;
        private String tipo;
        private String nombreArchivo;
        private String urlArchivo;
        private Long tamanoBytes;
        private LocalDateTime fechaSubida;
        private String usuarioSubio;

        // Getters y Setters
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

        // Métodos de formato
        public String getTipoLegible() {
            if (tipo == null) return "Otro";
            switch (tipo) {
                case "Render": return "Render";
                case "Plano": return "Plano";
                case "Boceto": return "Boceto";
                case "Contrato": return "Contrato";
                default: return tipo;
            }
        }

        public String getTipoBadgeClass() {
            if (tipo == null) return "otro";
            switch (tipo) {
                case "Render": return "render";
                case "Plano": return "plano";
                case "Boceto": return "boceto";
                case "Contrato": return "contrato";
                default: return "otro";
            }
        }

        public String getFechaSubidaFormateada() {
            if (fechaSubida == null) return "-";
            return fechaSubida.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }

        public String getTamanoFormateado() {
            if (tamanoBytes == null) return "-";
            double kb = tamanoBytes / 1024.0;
            if (kb < 1024) {
                return String.format("%.0f KB", kb);
            }
            double mb = kb / 1024.0;
            return String.format("%.1f MB", mb);
        }
    }

    /**
     * DTO para pagos del proyecto.
     */
    public static class PagoDTO {
        private Integer idPago;
        private Integer idProyecto;
        private BigDecimal monto;
        private LocalDateTime fechaPago;
        private String metodoPago;
        private String estadoPago;
        private String observaciones;

        // Getters y Setters
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

        // Métodos de formato
        public String getMontoFormateado() {
            if (monto == null) return "S/ 0.00";
            return "S/ " + monto.setScale(2, RoundingMode.HALF_UP).toString();
        }

        public String getFechaPagoFormateada() {
            if (fechaPago == null) return "-";
            return fechaPago.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }

        public String getEstadoLegible() {
            if (estadoPago == null) return "Pendiente";
            switch (estadoPago) {
                case "Validado": return "Validado";
                case "Pendiente": return "Pendiente de validación";
                case "Rechazado": return "Rechazado";
                default: return estadoPago;
            }
        }

        public String getEstadoBadgeClass() {
            if (estadoPago == null) return "status-pendiente";
            switch (estadoPago) {
                case "Validado": return "status-validado";
                case "Pendiente": return "status-pendiente";
                case "Rechazado": return "status-rechazado";
                default: return "status-pendiente";
            }
        }

        public String getMetodoLegible() {
            if (metodoPago == null) return "No especificado";
            switch (metodoPago) {
                case "Efectivo": return "Efectivo";
                case "Transferencia": return "Transferencia";
                case "Tarjeta": return "Tarjeta";
                case "Cheque": return "Cheque";
                default: return metodoPago;
            }
        }
    }

    /**
     * DTO para cambios de estado del proyecto.
     */
    public static class CambioEstadoDTO {
        private Integer idCambio;
        private Integer idProyecto;
        private String estadoAnterior;
        private String estadoNuevo;
        private LocalDateTime fechaCambio;
        private String responsable;
        private String comentario;

        // Getters y Setters
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

        // Métodos de formato
        public String getFechaCambioFormateada() {
            if (fechaCambio == null) return "-";
            return fechaCambio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }

        public String getEstadoAnteriorLegible() {
            return estadoAnterior != null ? estadoAnterior : "-";
        }

        public String getEstadoNuevoLegible() {
            return estadoNuevo != null ? estadoNuevo : "-";
        }

        public String getEstadoNuevoBadgeClass() {
            if (estadoNuevo == null) return "status-pendiente";
            switch (estadoNuevo) {
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

    // ============================================
    // MÉTODOS ESTÁTICOS DE FÁBRICA
    // ============================================

    /**
     * Crea un DTO de proyecto desde una entidad.
     * 
     * @param proyecto Entidad de proyecto
     * @return DTO de proyecto
     */
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
}