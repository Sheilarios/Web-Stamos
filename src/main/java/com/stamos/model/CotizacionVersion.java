package com.stamos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "VersionCotizacion",
       indexes = {
           @Index(name = "idx_version_cotizacion", columnList = "id_cotizacion"),
           @Index(name = "idx_version_numero", columnList = "numero_version"),
           @Index(name = "idx_version_usuario", columnList = "id_usuario_creador"),
           @Index(name = "idx_version_fecha", columnList = "fecha_creacion")
       })
public class CotizacionVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_version")
    private Integer idVersion;

    @NotNull(message = "La cotización es obligatoria")
    @Column(name = "id_cotizacion", nullable = false)
    private Integer idCotizacion;

    @NotNull(message = "El número de versión es obligatorio")
    @Column(name = "numero_version", nullable = false)
    private Integer numeroVersion = 1;

    @NotNull(message = "El subtotal es obligatorio")
    @PositiveOrZero(message = "El subtotal debe ser mayor o igual a 0")
    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @NotNull(message = "El IGV es obligatorio")
    @PositiveOrZero(message = "El IGV debe ser mayor o igual a 0")
    @Column(name = "igv", nullable = false, precision = 12, scale = 2)
    private BigDecimal igv;

    @NotNull(message = "El total es obligatorio")
    @PositiveOrZero(message = "El total debe ser mayor o igual a 0")
    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @NotNull(message = "La tasa IGV es obligatoria")
    @PositiveOrZero(message = "La tasa IGV debe ser mayor o igual a 0")
    @Column(name = "tasa_igv", nullable = false, precision = 5, scale = 2)
    private BigDecimal tasaIgv = new BigDecimal("18.00");

    @Size(max = 500, message = "El motivo del cambio no puede exceder los 500 caracteres")
    @Column(name = "motivo_cambio", columnDefinition = "TEXT")
    private String motivoCambio;

    @NotNull(message = "El usuario creador es obligatorio")
    @Column(name = "id_usuario_creador", nullable = false)
    private Integer idUsuarioCreador;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Transient
    private String nombreUsuarioCreador;

    @Transient
    private String emailUsuarioCreador;

    @Transient
    private String nombreCliente;

    @Transient
    private String rucCliente;

    @Transient
    private String nombreVendedor;

    @Transient
    private List<CotizacionDetalle> detalles = new ArrayList<>();

    @Transient
    private Integer cantidadItems;

    @Transient
    private Boolean esVersionActual;

    @Transient
    private String estadoCotizacion;

    @Transient
    private String codigoCotizacion;

    public CotizacionVersion() {
        this.subtotal = BigDecimal.ZERO;
        this.igv = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
        this.tasaIgv = new BigDecimal("18.00");
        this.numeroVersion = 1;
        this.fechaCreacion = LocalDateTime.now();
        this.detalles = new ArrayList<>();
    }

    public CotizacionVersion(Integer idCotizacion, Integer numeroVersion, BigDecimal subtotal,
                            BigDecimal igv, BigDecimal total, BigDecimal tasaIgv,
                            Integer idUsuarioCreador) {
        this();
        this.idCotizacion = idCotizacion;
        this.numeroVersion = numeroVersion;
        this.subtotal = subtotal != null ? subtotal : BigDecimal.ZERO;
        this.igv = igv != null ? igv : BigDecimal.ZERO;
        this.total = total != null ? total : BigDecimal.ZERO;
        this.tasaIgv = tasaIgv != null ? tasaIgv : new BigDecimal("18.00");
        this.idUsuarioCreador = idUsuarioCreador;
    }

    public CotizacionVersion(Integer idCotizacion, Integer numeroVersion, BigDecimal subtotal,
                            BigDecimal igv, BigDecimal total, BigDecimal tasaIgv,
                            Integer idUsuarioCreador, String motivoCambio) {
        this(idCotizacion, numeroVersion, subtotal, igv, total, tasaIgv, idUsuarioCreador);
        this.motivoCambio = motivoCambio;
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
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

    public List<CotizacionDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<CotizacionDetalle> detalles) {
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

    public String getEstadoCotizacion() {
        return estadoCotizacion;
    }

    public void setEstadoCotizacion(String estadoCotizacion) {
        this.estadoCotizacion = estadoCotizacion;
    }

    public String getCodigoCotizacion() {
        return codigoCotizacion;
    }

    public void setCodigoCotizacion(String codigoCotizacion) {
        this.codigoCotizacion = codigoCotizacion;
    }

    public void recalcularTotales() {
        if (detalles == null || detalles.isEmpty()) {
            this.subtotal = BigDecimal.ZERO;
            this.igv = BigDecimal.ZERO;
            this.total = BigDecimal.ZERO;
            this.cantidadItems = 0;
            return;
        }

        this.subtotal = detalles.stream()
                .map(CotizacionDetalle::getSubtotalLinea)
                .filter(s -> s != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (this.tasaIgv == null) {
            this.tasaIgv = new BigDecimal("18.00");
        }

        this.igv = this.subtotal.multiply(this.tasaIgv)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        this.total = this.subtotal.add(this.igv);
        this.cantidadItems = detalles.size();
    }

    public void agregarDetalle(CotizacionDetalle detalle) {
        if (detalle != null) {
            this.detalles.add(detalle);
            recalcularTotales();
        }
    }

    public void eliminarDetalle(int index) {
        if (index >= 0 && index < this.detalles.size()) {
            this.detalles.remove(index);
            recalcularTotales();
        }
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

    public BigDecimal getDiferenciaTotal(CotizacionVersion otraVersion) {
        if (otraVersion == null || otraVersion.getTotal() == null) {
            return this.total != null ? this.total : BigDecimal.ZERO;
        }
        BigDecimal totalActual = this.total != null ? this.total : BigDecimal.ZERO;
        BigDecimal totalOtro = otraVersion.getTotal() != null ? otraVersion.getTotal() : BigDecimal.ZERO;
        return totalActual.subtract(totalOtro);
    }

    public BigDecimal getDiferenciaPorcentual(CotizacionVersion otraVersion) {
        if (otraVersion == null || otraVersion.getTotal() == null || 
            otraVersion.getTotal().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal diferencia = getDiferenciaTotal(otraVersion);
        return diferencia.multiply(new BigDecimal("100"))
                .divide(otraVersion.getTotal(), 2, RoundingMode.HALF_UP);
    }

    public String getFechaCreacionFormateada() {
        if (fechaCreacion == null) return "-";
        return fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getFechaCreacionSoloFecha() {
        if (fechaCreacion == null) return "-";
        return fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getSubtotalFormateado() {
        if (subtotal == null) return "S/ 0.00";
        return "S/ " + subtotal.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public String getIgvFormateado() {
        if (igv == null) return "S/ 0.00";
        return "S/ " + igv.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public String getTotalFormateado() {
        if (total == null) return "S/ 0.00";
        return "S/ " + total.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public String getTasaIgvFormateada() {
        if (tasaIgv == null) return "18.00%";
        return tasaIgv.setScale(2, RoundingMode.HALF_UP).toString() + "%";
    }

    public String getNumeroVersionLegible() {
        if (numeroVersion == null) return "V0";
        return "V" + numeroVersion;
    }

    public String getCreadorLegible() {
        if (nombreUsuarioCreador != null && !nombreUsuarioCreador.isEmpty()) {
            return nombreUsuarioCreador;
        }
        if (emailUsuarioCreador != null && !emailUsuarioCreador.isEmpty()) {
            return emailUsuarioCreador;
        }
        return "Usuario ID: " + idUsuarioCreador;
    }

    public String getVersionBadgeClass() {
        if (esVersionActual != null && esVersionActual) {
            return "badge-success";
        }
        return "badge-secondary";
    }

    public String getVersionBadgeText() {
        if (esVersionActual != null && esVersionActual) {
            return "Actual";
        }
        return "Histórica";
    }

    public String getResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append("Versión ").append(numeroVersion);
        sb.append(" - ").append(getTotalFormateado());
        if (tieneMotivoCambio()) {
            sb.append(" - ").append(motivoCambio);
        }
        if (esVersionActual != null && esVersionActual) {
            sb.append(" (Actual)");
        }
        return sb.toString();
    }

    public String getClienteLegible() {
        if (nombreCliente != null && !nombreCliente.isEmpty()) {
            if (rucCliente != null && !rucCliente.isEmpty()) {
                return nombreCliente + " (" + rucCliente + ")";
            }
            return nombreCliente;
        }
        return "Cliente no especificado";
    }

    public String getVendedorLegible() {
        if (nombreVendedor != null && !nombreVendedor.isEmpty()) {
            return nombreVendedor;
        }
        return "Vendedor no especificado";
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"idVersion\":").append(idVersion).append(",");
        sb.append("\"numeroVersion\":").append(numeroVersion).append(",");
        sb.append("\"subtotal\":").append(subtotal != null ? subtotal : 0).append(",");
        sb.append("\"igv\":").append(igv != null ? igv : 0).append(",");
        sb.append("\"total\":").append(total != null ? total : 0).append(",");
        sb.append("\"tasaIgv\":").append(tasaIgv != null ? tasaIgv : 18.00).append(",");
        sb.append("\"fechaCreacion\":\"").append(getFechaCreacionFormateada()).append("\",");
        sb.append("\"esActual\":").append(esVersionActual != null && esVersionActual);
        if (motivoCambio != null) {
            sb.append(",\"motivo\":\"").append(motivoCambio).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

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
        sb.append("\"esActual\":").append(esVersionActual != null && esVersionActual);
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "CotizacionVersion{" +
                "idVersion=" + idVersion +
                ", idCotizacion=" + idCotizacion +
                ", numeroVersion=" + numeroVersion +
                ", subtotal=" + subtotal +
                ", igv=" + igv +
                ", total=" + total +
                ", tasaIgv=" + tasaIgv +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CotizacionVersion that = (CotizacionVersion) o;
        return idVersion != null && idVersion.equals(that.idVersion);
    }

    @Override
    public int hashCode() {
        return idVersion != null ? idVersion.hashCode() : 0;
    }

    public static CotizacionVersion fromCotizacion(Cotizacion cotizacion, Integer idUsuarioCreador,
                                                  String motivoCambio) {
        if (cotizacion == null) {
            return null;
        }

        CotizacionVersion version = new CotizacionVersion();
        version.setIdCotizacion(cotizacion.getIdCotizacion());
        version.setNumeroVersion(cotizacion.getVersionActual() + 1);
        version.setSubtotal(cotizacion.getSubtotal());
        version.setIgv(cotizacion.getIgv());
        version.setTotal(cotizacion.getTotal());
        version.setTasaIgv(cotizacion.getTasaIgv());
        version.setIdUsuarioCreador(idUsuarioCreador);
        version.setMotivoCambio(motivoCambio);
        
        return version;
    }

    public static CotizacionVersion fromVersion(CotizacionVersion versionExistente, 
                                               Integer idUsuarioCreador, String motivoCambio) {
        if (versionExistente == null) {
            return null;
        }

        CotizacionVersion version = new CotizacionVersion();
        version.setIdCotizacion(versionExistente.getIdCotizacion());
        version.setNumeroVersion(versionExistente.getNumeroVersion() + 1);
        version.setSubtotal(versionExistente.getSubtotal());
        version.setIgv(versionExistente.getIgv());
        version.setTotal(versionExistente.getTotal());
        version.setTasaIgv(versionExistente.getTasaIgv());
        version.setIdUsuarioCreador(idUsuarioCreador);
        version.setMotivoCambio(motivoCambio);
        
        return version;
    }

    public static CotizacionVersion fromDTO(com.stamos.dto.CotizacionVersionDTO dto) {
        if (dto == null) {
            return null;
        }

        CotizacionVersion version = new CotizacionVersion();
        if (dto.getIdVersion() != null) {
            version.setIdVersion(dto.getIdVersion());
        }
        version.setIdCotizacion(dto.getIdCotizacion());
        version.setNumeroVersion(dto.getNumeroVersion());
        version.setSubtotal(dto.getSubtotal());
        version.setIgv(dto.getIgv());
        version.setTotal(dto.getTotal());
        version.setTasaIgv(dto.getTasaIgv());
        version.setMotivoCambio(dto.getMotivoCambio());
        version.setIdUsuarioCreador(dto.getIdUsuarioCreador());
        version.setFechaCreacion(dto.getFechaCreacion());
        
        version.setNombreUsuarioCreador(dto.getNombreUsuarioCreador());
        version.setEmailUsuarioCreador(dto.getEmailUsuarioCreador());
        version.setNombreCliente(dto.getNombreCliente());
        version.setRucCliente(dto.getRucCliente());
        version.setNombreVendedor(dto.getNombreVendedor());
        version.setEsVersionActual(dto.getEsVersionActual());
        version.setEstadoCotizacion(dto.getEstadoCotizacion());
        version.setCodigoCotizacion(dto.getCodigoCotizacion());
        
        return version;
    }

    public com.stamos.dto.CotizacionVersionDTO toDTO() {
        com.stamos.dto.CotizacionVersionDTO dto = new com.stamos.dto.CotizacionVersionDTO();
        dto.setIdVersion(this.idVersion);
        dto.setIdCotizacion(this.idCotizacion);
        dto.setNumeroVersion(this.numeroVersion);
        dto.setSubtotal(this.subtotal);
        dto.setIgv(this.igv);
        dto.setTotal(this.total);
        dto.setTasaIgv(this.tasaIgv);
        dto.setMotivoCambio(this.motivoCambio);
        dto.setIdUsuarioCreador(this.idUsuarioCreador);
        dto.setFechaCreacion(this.fechaCreacion);
        dto.setNombreUsuarioCreador(this.nombreUsuarioCreador);
        dto.setEmailUsuarioCreador(this.emailUsuarioCreador);
        dto.setNombreCliente(this.nombreCliente);
        dto.setRucCliente(this.rucCliente);
        dto.setNombreVendedor(this.nombreVendedor);
        dto.setEsVersionActual(this.esVersionActual);
        dto.setEstadoCotizacion(this.estadoCotizacion);
        dto.setCodigoCotizacion(this.codigoCotizacion);
        return dto;
    }
}