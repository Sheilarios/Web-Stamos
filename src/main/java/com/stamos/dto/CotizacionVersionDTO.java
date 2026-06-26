package com.stamos.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CotizacionVersionDTO {

    private Integer idVersion;
    private Integer idCotizacion;
    private Integer numeroVersion;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private BigDecimal tasaIgv;
    private String motivoCambio;
    private Integer idUsuarioCreador;
    private String nombreUsuarioCreador;
    private String emailUsuarioCreador;
    private LocalDateTime fechaCreacion;
    private String codigoCotizacion;
    private String estadoCotizacion;
    private String nombreCliente;
    private String rucCliente;
    private String nombreVendedor;
    private String emailVendedor;
    private LocalDateTime fechaEmisionCotizacion;
    private List<CotizacionDetalleDTO> detalles = new ArrayList<>();
    private Integer cantidadItems;
    private Boolean esVersionActual;
    private BigDecimal diferenciaTotal;
    private BigDecimal diferenciaPorcentual;
    private Boolean tieneCambios;
    private String color;
    private String icono;

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

    public CotizacionVersionDTO(Integer idVersion, Integer numeroVersion, 
                               BigDecimal total, LocalDateTime fechaCreacion) {
        this();
        this.idVersion = idVersion;
        this.numeroVersion = numeroVersion;
        this.total = total;
        this.fechaCreacion = fechaCreacion;
    }

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

    public boolean tieneDetalles() {
        return detalles != null && !detalles.isEmpty();
    }

    public boolean tieneMotivoCambio() {
        return motivoCambio != null && !motivoCambio.trim().isEmpty();
    }

    public boolean esPrimeraVersion() {
        return numeroVersion != null && numeroVersion == 1;
    }

    public boolean isEsVersionActual() {
        return esVersionActual != null && esVersionActual;
    }

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

    public void agregarDetalle(CotizacionDetalleDTO detalle) {
        if (detalle != null && this.detalles != null) {
            this.detalles.add(detalle);
            this.cantidadItems = this.detalles.size();
        }
    }

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
}