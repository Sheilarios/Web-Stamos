package com.stamos.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO (Data Transfer Object) para la entidad CotizacionVersion.
 * 
 * <p>Este DTO se utiliza para transferir datos de versiones de cotizaciones
 * entre la capa de servicio y la capa de presentación, permitiendo una
 * mejor organización y manejo de las versiones históricas.</p>
 * 
 * <p>Incluye información de la versión, totales, motivo de cambio,
 * metadatos de auditoría y referencias a la cotización y sus detalles.</p>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
public class CotizacionVersionDTO {

    // ============================================
    // ATRIBUTOS PRINCIPALES
    // ============================================

    private Integer idVersion;
    private Integer idCotizacion;
    private Integer numeroVersion;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private BigDecimal tasaIgv;
    private String motivoCambio;

    // ============================================
    // CAMPOS DE AUDITORÍA
    // ============================================

    private Integer idUsuarioCreador;
    private String nombreUsuarioCreador;
    private String emailUsuarioCreador;
    private LocalDateTime fechaCreacion;

    // ============================================
    // CAMPOS RELACIONADOS (DESDE COTIZACIÓN)
    // ============================================

    private String codigoCotizacion;
    private String estadoCotizacion;
    private String nombreCliente;
    private String rucCliente;
    private String nombreVendedor;
    private String emailVendedor;
    private LocalDateTime fechaEmisionCotizacion;

    // ============================================
    // CAMPOS DE DETALLES
    // ============================================

    private List<CotizacionDetalleDTO> detalles = new ArrayList<>();
    private Integer cantidadItems;

    // ============================================
    // CAMPOS DE MÉTRICAS
    // ============================================

    private Boolean esVersionActual;
    private BigDecimal diferenciaTotal;
    private BigDecimal diferenciaPorcentual;
    private Boolean tieneCambios;

    // ============================================
    // CAMPOS ADICIONALES
    // ============================================

    private String color;
    private String icono;

    // ============================================
    // CONSTRUCTORES
    // ============================================

    /**
     * Constructor por defecto.
     */
    public CotizacionVersionDTO() {
        this.subtotal = BigDecimal.ZERO;
        this.igv = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
        this.tasaIgv = new BigDecimal("18.00");
        this.numeroVersion = 1;
        this.detalles = new ArrayList<>();
        this.cantidadItems = 0;
        this.esVersionActual = false;
        this.tieneCambios = false;
        this.color = "#3B82F6";
        this.icono = "ti ti-file";
    }

    /**
     * Constructor con campos principales.
     * 
     * @param idVersion ID de la versión
     * @param numeroVersion Número de versión
     * @param total Total de la versión
     * @param fechaCreacion Fecha de creación
     */
    public CotizacionVersionDTO(Integer idVersion, Integer numeroVersion, 
                               BigDecimal total, LocalDateTime fechaCreacion) {
        this();
        this.idVersion = idVersion;
        this.numeroVersion = numeroVersion;
        this.total = total;
        this.fechaCreacion = fechaCreacion;
    }

    /**
     * Constructor completo.
     * 
     * @param idVersion ID de la versión
     * @param idCotizacion ID de la cotización
     * @param numeroVersion Número de versión
     * @param subtotal Subtotal
     * @param igv IGV
     * @param total Total
     * @param tasaIgv Tasa IGV
     * @param motivoCambio Motivo del cambio
     * @param fechaCreacion Fecha de creación
     * @param esVersionActual Indica si es la versión actual
     */
    public CotizacionVersionDTO(Integer idVersion, Integer idCotizacion, Integer numeroVersion,
                               BigDecimal subtotal, BigDecimal igv, BigDecimal total,
                               BigDecimal tasaIgv, String motivoCambio,
                               LocalDateTime fechaCreacion, Boolean esVersionActual) {
        this();
        this.idVersion = idVersion;
        this.idCotizacion = idCotizacion;
        this.numeroVersion = numeroVersion;
        this.subtotal = subtotal;
        this.igv = igv;
        this.total = total;
        this.tasaIgv = tasaIgv;
        this.motivoCambio = motivoCambio;
        this.fechaCreacion = fechaCreacion;
        this.esVersionActual = esVersionActual;
    }

    // ============================================
    // GETTERS Y SETTERS
    // ============================================

    public Integer getIdVersion() {
        return idVersion;
    }

    public void setIdVersion(Integer idVersion) {
        this.idVersion = idVersion;
    }

    public Integer getIdCotizacion() {
        return idCotizacion;
    }

    public void setIdCotizacion(Integer idCotizacion) {
        this.idCotizacion = idCotizacion;
    }

    public Integer getNumeroVersion() {
        return numeroVersion;
    }

    public void setNumeroVersion(Integer numeroVersion) {
        this.numeroVersion = numeroVersion;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getIgv() {
        return igv;
    }

    public void setIgv(BigDecimal igv) {
        this.igv = igv;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getTasaIgv() {
        return tasaIgv;
    }

    public void setTasaIgv(BigDecimal tasaIgv) {
        this.tasaIgv = tasaIgv;
    }

    public String getMotivoCambio() {
        return motivoCambio;
    }

    public void setMotivoCambio(String motivoCambio) {
        this.motivoCambio = motivoCambio;
    }

    public Integer getIdUsuarioCreador() {
        return idUsuarioCreador;
    }

    public void setIdUsuarioCreador(Integer idUsuarioCreador) {
        this.idUsuarioCreador = idUsuarioCreador;
    }

    public String getNombreUsuarioCreador() {
        return nombreUsuarioCreador;
    }

    public void setNombreUsuarioCreador(String nombreUsuarioCreador) {
        this.nombreUsuarioCreador = nombreUsuarioCreador;
    }

    public String getEmailUsuarioCreador() {
        return emailUsuarioCreador;
    }

    public void setEmailUsuarioCreador(String emailUsuarioCreador) {
        this.emailUsuarioCreador = emailUsuarioCreador;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getCodigoCotizacion() {
        return codigoCotizacion;
    }

    public void setCodigoCotizacion(String codigoCotizacion) {
        this.codigoCotizacion = codigoCotizacion;
    }

    public String getEstadoCotizacion() {
        return estadoCotizacion;
    }

    public void setEstadoCotizacion(String estadoCotizacion) {
        this.estadoCotizacion = estadoCotizacion;
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

    public String getNombreVendedor() {
        return nombreVendedor;
    }

    public void setNombreVendedor(String nombreVendedor) {
        this.nombreVendedor = nombreVendedor;
    }

    public String getEmailVendedor() {
        return emailVendedor;
    }

    public void setEmailVendedor(String emailVendedor) {
        this.emailVendedor = emailVendedor;
    }

    public LocalDateTime getFechaEmisionCotizacion() {
        return fechaEmisionCotizacion;
    }

    public void setFechaEmisionCotizacion(LocalDateTime fechaEmisionCotizacion) {
        this.fechaEmisionCotizacion = fechaEmisionCotizacion;
    }

    public List<CotizacionDetalleDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<CotizacionDetalleDTO> detalles) {
        this.detalles = detalles;
    }

    public Integer getCantidadItems() {
        return cantidadItems;
    }

    public void setCantidadItems(Integer cantidadItems) {
        this.cantidadItems = cantidadItems;
    }

    public Boolean getEsVersionActual() {
        return esVersionActual;
    }

    public void setEsVersionActual(Boolean esVersionActual) {
        this.esVersionActual = esVersionActual;
    }

    public BigDecimal getDiferenciaTotal() {
        return diferenciaTotal;
    }

    public void setDiferenciaTotal(BigDecimal diferenciaTotal) {
        this.diferenciaTotal = diferenciaTotal;
    }

    public BigDecimal getDiferenciaPorcentual() {
        return diferenciaPorcentual;
    }

    public void setDiferenciaPorcentual(BigDecimal diferenciaPorcentual) {
        this.diferenciaPorcentual = diferenciaPorcentual;
    }

    public Boolean getTieneCambios() {
        return tieneCambios;
    }

    public void setTieneCambios(Boolean tieneCambios) {
        this.tieneCambios = tieneCambios;
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
     * Verifica si la versión tiene detalles.
     * 
     * @return true si tiene detalles, false en caso contrario
     */
    public boolean tieneDetalles() {
        return detalles != null && !detalles.isEmpty();
    }

    /**
     * Verifica si la versión tiene motivo de cambio.
     * 
     * @return true si tiene motivo, false en caso contrario
     */
    public boolean tieneMotivoCambio() {
        return motivoCambio != null && !motivoCambio.trim().isEmpty();
    }

    /**
     * Verifica si es la primera versión.
     * 
     * @return true si es la versión 1, false en caso contrario
     */
    public boolean esPrimeraVersion() {
        return numeroVersion != null && numeroVersion == 1;
    }

    /**
     * Verifica si la versión es la actual.
     * 
     * @return true si es actual, false en caso contrario
     */
    public boolean isEsVersionActual() {
        return esVersionActual != null && esVersionActual;
    }

    /**
     * Calcula la diferencia total entre esta versión y otra.
     * 
     * @param otraVersion Otra versión para comparar
     */
    public void calcularDiferencia(CotizacionVersionDTO otraVersion) {
        if (otraVersion == null || otraVersion.getTotal() == null) {
            this.diferenciaTotal = BigDecimal.ZERO;
            this.diferenciaPorcentual = BigDecimal.ZERO;
            this.tieneCambios = false;
            return;
        }

        BigDecimal totalActual = this.total != null ? this.total : BigDecimal.ZERO;
        BigDecimal totalOtro = otraVersion.getTotal() != null ? otraVersion.getTotal() : BigDecimal.ZERO;
        
        this.diferenciaTotal = totalActual.subtract(totalOtro);
        this.tieneCambios = this.diferenciaTotal.compareTo(BigDecimal.ZERO) != 0;
        
        if (totalOtro.compareTo(BigDecimal.ZERO) != 0) {
            this.diferenciaPorcentual = this.diferenciaTotal.multiply(new BigDecimal("100"))
                    .divide(totalOtro, 2, RoundingMode.HALF_UP);
        } else {
            this.diferenciaPorcentual = BigDecimal.ZERO;
        }
    }

    /**
     * Agrega un detalle a la versión.
     * 
     * @param detalle DTO del detalle
     */
    public void agregarDetalle(CotizacionDetalleDTO detalle) {
        if (detalle != null && this.detalles != null) {
            this.detalles.add(detalle);
            this.cantidadItems = this.detalles.size();
        }
    }

    /**
     * Obtiene el estado de la cotización en formato legible.
     * 
     * @return Estado legible
     */
    public String getEstadoCotizacionLegible() {
        if (estadoCotizacion == null) return "Sin estado";
        
        switch (estadoCotizacion) {
            case "Pendiente":
                return "Pendiente";
            case "Aprobada":
                return "Aprobada";
            case "Anulada":
                return "Anulada";
            default:
                return estadoCotizacion;
        }
    }

    /**
     * Obtiene la clase CSS para el badge del estado de la cotización.
     * 
     * @return Clase CSS
     */
    public String getEstadoCotizacionBadgeClass() {
        if (estadoCotizacion == null) return "status-pendiente";
        
        switch (estadoCotizacion) {
            case "Pendiente":
                return "status-pendiente";
            case "Aprobada":
                return "status-validado";
            case "Anulada":
                return "status-rechazado";
            default:
                return "status-pendiente";
        }
    }

    // ============================================
    // MÉTODOS DE FORMATO PARA LA VISTA
    // ============================================

    /**
     * Obtiene el número de versión en formato legible.
     * 
     * @return Número de versión legible
     */
    public String getNumeroVersionLegible() {
        if (numeroVersion == null) return "V0";
        return "V" + numeroVersion;
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
     * Obtiene la fecha de emisión de la cotización formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaEmisionCotizacionFormateada() {
        if (fechaEmisionCotizacion == null) return "-";
        return fechaEmisionCotizacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    /**
     * Obtiene el subtotal formateado.
     * 
     * @return Subtotal formateado
     */
    public String getSubtotalFormateado() {
        if (subtotal == null) return "S/ 0.00";
        return "S/ " + subtotal.setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el IGV formateado.
     * 
     * @return IGV formateado
     */
    public String getIgvFormateado() {
        if (igv == null) return "S/ 0.00";
        return "S/ " + igv.setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene el total formateado.
     * 
     * @return Total formateado
     */
    public String getTotalFormateado() {
        if (total == null) return "S/ 0.00";
        return "S/ " + total.setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene la tasa IGV formateada.
     * 
     * @return Tasa IGV formateada
     */
    public String getTasaIgvFormateada() {
        if (tasaIgv == null) return "18.00%";
        return tasaIgv.setScale(2, RoundingMode.HALF_UP).toString() + "%";
    }

    /**
     * Obtiene la diferencia total formateada.
     * 
     * @return Diferencia total formateada
     */
    public String getDiferenciaTotalFormateada() {
        if (diferenciaTotal == null) return "S/ 0.00";
        String signo = diferenciaTotal.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
        return signo + "S/ " + diferenciaTotal.setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * Obtiene la diferencia porcentual formateada.
     * 
     * @return Diferencia porcentual formateada
     */
    public String getDiferenciaPorcentualFormateada() {
        if (diferenciaPorcentual == null) return "0%";
        String signo = diferenciaPorcentual.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
        return signo + diferenciaPorcentual.setScale(1, RoundingMode.HALF_UP).toString() + "%";
    }

    /**
     * Obtiene el color para la diferencia.
     * 
     * @return Color en hexadecimal
     */
    public String getDiferenciaColor() {
        if (diferenciaTotal == null) return "#6B7280";
        if (diferenciaTotal.compareTo(BigDecimal.ZERO) > 0) {
            return "#10B981"; // Verde (aumento)
        } else if (diferenciaTotal.compareTo(BigDecimal.ZERO) < 0) {
            return "#EF4444"; // Rojo (disminución)
        }
        return "#6B7280"; // Gris (sin cambio)
    }

    /**
     * Obtiene el creador en formato legible.
     * 
     * @return Creador legible
     */
    public String getCreadorLegible() {
        if (nombreUsuarioCreador != null && !nombreUsuarioCreador.isEmpty()) {
            return nombreUsuarioCreador;
        }
        if (emailUsuarioCreador != null && !emailUsuarioCreador.isEmpty()) {
            return emailUsuarioCreador;
        }
        return "Usuario ID: " + idUsuarioCreador;
    }

    /**
     * Obtiene el cliente en formato legible.
     * 
     * @return Cliente legible
     */
    public String getClienteLegible() {
        if (nombreCliente != null && !nombreCliente.isEmpty()) {
            if (rucCliente != null && !rucCliente.isEmpty()) {
                return nombreCliente + " (" + rucCliente + ")";
            }
            return nombreCliente;
        }
        return "Cliente no especificado";
    }

    /**
     * Obtiene el vendedor en formato legible.
     * 
     * @return Vendedor legible
     */
    public String getVendedorLegible() {
        if (nombreVendedor != null && !nombreVendedor.isEmpty()) {
            return nombreVendedor;
        }
        if (emailVendedor != null && !emailVendedor.isEmpty()) {
            return emailVendedor;
        }
        return "Vendedor no especificado";
    }

    /**
     * Obtiene la clase CSS para el badge de la versión.
     * 
     * @return Clase CSS
     */
    public String getVersionBadgeClass() {
        if (isEsVersionActual()) {
            return "badge-success";
        }
        return "badge-secondary";
    }

    /**
     * Obtiene el texto para el badge de la versión.
     * 
     * @return Texto del badge
     */
    public String getVersionBadgeText() {
        if (isEsVersionActual()) {
            return "Actual";
        }
        return "Histórica";
    }

    /**
     * Obtiene el resumen de la versión.
     * 
     * @return Resumen
     */
    public String getResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append("Versión ").append(numeroVersion);
        sb.append(" - ").append(getTotalFormateado());
        if (tieneMotivoCambio()) {
            sb.append(" - ").append(motivoCambio);
        }
        if (isEsVersionActual()) {
            sb.append(" (Actual)");
        }
        return sb.toString();
    }

    /**
     * Obtiene el ícono según el estado de la versión.
     * 
     * @return Ícono CSS
     */
    public String getVersionIcono() {
        if (isEsVersionActual()) {
            return "ti ti-check-circle";
        }
        return "ti ti-history";
    }

    /**
     * Obtiene el color según el estado de la versión.
     * 
     * @return Color en hexadecimal
     */
    public String getVersionColor() {
        if (isEsVersionActual()) {
            return "#10B981";
        }
        return "#6B7280";
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
        sb.append("\"idVersion\":").append(idVersion).append(",");
        sb.append("\"numeroVersion\":").append(numeroVersion).append(",");
        sb.append("\"total\":").append(total != null ? total : 0).append(",");
        sb.append("\"fechaCreacion\":\"").append(getFechaCreacionFormateada()).append("\",");
        sb.append("\"esActual\":").append(isEsVersionActual());
        if (motivoCambio != null) {
            sb.append(",\"motivo\":\"").append(motivoCambio).append("\"");
        }
        if (codigoCotizacion != null) {
            sb.append(",\"codigoCotizacion\":\"").append(codigoCotizacion).append("\"");
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
        sb.append("\"idVersion\":").append(idVersion).append(",");
        sb.append("\"idCotizacion\":").append(idCotizacion).append(",");
        sb.append("\"codigoCotizacion\":\"").append(codigoCotizacion != null ? codigoCotizacion : "").append("\",");
        sb.append("\"numeroVersion\":").append(numeroVersion).append(",");
        sb.append("\"subtotal\":").append(subtotal != null ? subtotal : 0).append(",");
        sb.append("\"igv\":").append(igv != null ? igv : 0).append(",");
        sb.append("\"total\":").append(total != null ? total : 0).append(",");
        sb.append("\"tasaIgv\":").append(tasaIgv != null ? tasaIgv : 18.00).append(",");
        sb.append("\"motivoCambio\":\"").append(motivoCambio != null ? motivoCambio : "").append("\",");
        sb.append("\"creador\":\"").append(getCreadorLegible()).append("\",");
        sb.append("\"fechaCreacion\":\"").append(getFechaCreacionFormateada()).append("\",");
        sb.append("\"cantidadItems\":").append(cantidadItems != null ? cantidadItems : 0).append(",");
        sb.append("\"esActual\":").append(isEsVersionActual());
        if (nombreCliente != null) {
            sb.append(",\"cliente\":\"").append(nombreCliente).append("\"");
        }
        if (diferenciaTotal != null) {
            sb.append(",\"diferenciaTotal\":").append(diferenciaTotal);
            sb.append(",\"diferenciaPorcentual\":").append(diferenciaPorcentual);
        }
        sb.append("}");
        return sb.toString();
    }

    // ============================================
    // MÉTODOS OVERRIDE
    // ============================================

    @Override
    public String toString() {
        return "CotizacionVersionDTO{" +
                "idVersion=" + idVersion +
                ", numeroVersion=" + numeroVersion +
                ", total=" + total +
                ", motivoCambio='" + motivoCambio + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", esVersionActual=" + esVersionActual +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CotizacionVersionDTO that = (CotizacionVersionDTO) o;
        return idVersion != null && idVersion.equals(that.idVersion);
    }

    @Override
    public int hashCode() {
        return idVersion != null ? idVersion.hashCode() : 0;
    }

    // ============================================
    // MÉTODOS ESTÁTICOS DE FÁBRICA
    // ============================================

    /**
     * Crea un DTO desde una entidad.
     * 
     * @param version Entidad de versión de cotización
     * @return DTO de versión de cotización
     */
    public static CotizacionVersionDTO fromEntity(com.stamos.model.CotizacionVersion version) {
        if (version == null) {
            return null;
        }

        CotizacionVersionDTO dto = new CotizacionVersionDTO();
        dto.setIdVersion(version.getIdVersion());
        dto.setIdCotizacion(version.getIdCotizacion());
        dto.setNumeroVersion(version.getNumeroVersion());
        dto.setSubtotal(version.getSubtotal());
        dto.setIgv(version.getIgv());
        dto.setTotal(version.getTotal());
        dto.setTasaIgv(version.getTasaIgv());
        dto.setMotivoCambio(version.getMotivoCambio());
        dto.setIdUsuarioCreador(version.getIdUsuarioCreador());
        dto.setFechaCreacion(version.getFechaCreacion());
        dto.setNombreUsuarioCreador(version.getNombreUsuarioCreador());
        dto.setEmailUsuarioCreador(version.getEmailUsuarioCreador());
        dto.setNombreCliente(version.getNombreCliente());
        dto.setRucCliente(version.getRucCliente());
        dto.setNombreVendedor(version.getNombreVendedor());
        dto.setEsVersionActual(version.getEsVersionActual());
        dto.setEstadoCotizacion(version.getEstadoCotizacion());
        dto.setCodigoCotizacion(version.getCodigoCotizacion());
        dto.setCantidadItems(version.getCantidadItems());

        return dto;
    }

    /**
     * Crea un DTO desde una entidad con detalles.
     * 
     * @param version Entidad de versión de cotización
     * @param detalles Lista de detalles
     * @return DTO de versión de cotización
     */
    public static CotizacionVersionDTO fromEntityWithDetails(
            com.stamos.model.CotizacionVersion version,
            List<CotizacionDetalleDTO> detalles) {
        
        CotizacionVersionDTO dto = fromEntity(version);
        if (dto != null && detalles != null) {
            dto.setDetalles(detalles);
            dto.setCantidadItems(detalles.size());
        }
        return dto;
    }

    /**
     * Crea un DTO desde una entidad con datos de cotización.
     * 
     * @param version Entidad de versión de cotización
     * @param cotizacion Entidad de cotización
     * @return DTO de versión de cotización
     */
    public static CotizacionVersionDTO fromEntityWithCotizacion(
            com.stamos.model.CotizacionVersion version,
            com.stamos.model.Cotizacion cotizacion) {
        
        CotizacionVersionDTO dto = fromEntity(version);
        if (dto != null && cotizacion != null) {
            dto.setCodigoCotizacion(cotizacion.getCodigoCotizacion());
            dto.setEstadoCotizacion(cotizacion.getEstado());
            dto.setFechaEmisionCotizacion(cotizacion.getFechaEmision());
            dto.setNombreCliente(cotizacion.getNombreCliente());
            dto.setRucCliente(cotizacion.getRucCliente());
            dto.setNombreVendedor(cotizacion.getNombreVendedor());
            dto.setEmailVendedor(cotizacion.getEmailVendedor());
        }
        return dto;
    }

    /**
     * Convierte el DTO a una entidad.
     * 
     * @return Entidad de versión de cotización
     */
    public com.stamos.model.CotizacionVersion toEntity() {
        com.stamos.model.CotizacionVersion version = new com.stamos.model.CotizacionVersion();
        version.setIdVersion(this.idVersion);
        version.setIdCotizacion(this.idCotizacion);
        version.setNumeroVersion(this.numeroVersion);
        version.setSubtotal(this.subtotal);
        version.setIgv(this.igv);
        version.setTotal(this.total);
        version.setTasaIgv(this.tasaIgv);
        version.setMotivoCambio(this.motivoCambio);
        version.setIdUsuarioCreador(this.idUsuarioCreador);
        version.setFechaCreacion(this.fechaCreacion);
        return version;
    }
}