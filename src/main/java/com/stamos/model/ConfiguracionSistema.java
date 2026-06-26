package com.stamos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "ConfiguracionSistema",
       indexes = {
           @Index(name = "idx_config_parametro", columnList = "parametro", unique = true),
           @Index(name = "idx_config_tipo_dato", columnList = "tipo_dato"),
           @Index(name = "idx_config_fecha", columnList = "fecha_actualizacion"),
           @Index(name = "idx_config_usuario", columnList = "id_usuario_actualiza")
       })
public class ConfiguracionSistema {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_config")
    private Integer idConfig;

    @NotBlank(message = "El nombre del parámetro es obligatorio")
    @Size(max = 50, message = "El nombre del parámetro no puede exceder los 50 caracteres")
    @Column(name = "parametro", nullable = false, unique = true, length = 50)
    private String parametro;

    @NotBlank(message = "El valor del parámetro es obligatorio")
    @Size(max = 255, message = "El valor no puede exceder los 255 caracteres")
    @Column(name = "valor", nullable = false, length = 255)
    private String valor;

    @NotNull(message = "El tipo de dato es obligatorio")
    @Column(name = "tipo_dato", nullable = false)
    private String tipoDato; // 'DECIMAL', 'STRING', 'INT', 'BOOLEAN', 'DATE'

    @Size(max = 200, message = "La descripción no puede exceder los 200 caracteres")
    @Column(name = "descripcion", length = 200)
    private String descripcion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    @Column(name = "id_usuario_actualiza")
    private Integer idUsuarioActualiza;


    @Transient
    private String nombreUsuarioActualiza;

    @Transient
    private String emailUsuarioActualiza;

    @Transient
    private String valorFormateado;

    @Transient
    private String categoria;

    public ConfiguracionSistema() {
        this.fechaActualizacion = LocalDateTime.now();
        this.tipoDato = "STRING";
    }

    public ConfiguracionSistema(String parametro, String valor, String tipoDato, String descripcion) {
        this();
        this.parametro = parametro;
        this.valor = valor;
        this.tipoDato = tipoDato;
        this.descripcion = descripcion;
    }

    public ConfiguracionSistema(Integer idConfig, String parametro, String valor, 
                               String tipoDato, String descripcion) {
        this(parametro, valor, tipoDato, descripcion);
        this.idConfig = idConfig;
    }

    public Integer getIdConfig() {
        return idConfig;
    }

    public void setIdConfig(Integer idConfig) {
        this.idConfig = idConfig;
    }

    public String getParametro() {
        return parametro;
    }

    public void setParametro(String parametro) {
        this.parametro = parametro;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getTipoDato() {
        return tipoDato;
    }

    public void setTipoDato(String tipoDato) {
        this.tipoDato = tipoDato;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Integer getIdUsuarioActualiza() {
        return idUsuarioActualiza;
    }

    public void setIdUsuarioActualiza(Integer idUsuarioActualiza) {
        this.idUsuarioActualiza = idUsuarioActualiza;
    }

    public String getNombreUsuarioActualiza() {
        return nombreUsuarioActualiza;
    }

    public void setNombreUsuarioActualiza(String nombreUsuarioActualiza) {
        this.nombreUsuarioActualiza = nombreUsuarioActualiza;
    }

    public String getEmailUsuarioActualiza() {
        return emailUsuarioActualiza;
    }

    public void setEmailUsuarioActualiza(String emailUsuarioActualiza) {
        this.emailUsuarioActualiza = emailUsuarioActualiza;
    }

    public String getValorFormateado() {
        return valorFormateado;
    }

    public void setValorFormateado(String valorFormateado) {
        this.valorFormateado = valorFormateado;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public java.math.BigDecimal getValorDecimal() {
        if (!"DECIMAL".equals(this.tipoDato) || valor == null) {
            return null;
        }
        try {
            return new java.math.BigDecimal(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Integer getValorInteger() {
        if (!"INT".equals(this.tipoDato) || valor == null) {
            return null;
        }
        try {
            return Integer.valueOf(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Boolean getValorBoolean() {
        if (!"BOOLEAN".equals(this.tipoDato) || valor == null) {
            return null;
        }
        return "true".equalsIgnoreCase(valor) || "1".equals(valor) || "si".equalsIgnoreCase(valor);
    }

    public LocalDateTime getValorDate() {
        if (!"DATE".equals(this.tipoDato) || valor == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(valor);
        } catch (Exception e) {
            return null;
        }
    }

    public String getValorString() {
        return valor;
    }

    public void actualizarValor(String valor, Integer idUsuario) {
        this.valor = valor;
        this.idUsuarioActualiza = idUsuario;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public boolean esDecimal() {
        return "DECIMAL".equals(this.tipoDato);
    }

    public boolean esInteger() {
        return "INT".equals(this.tipoDato);
    }

    public boolean esBoolean() {
        return "BOOLEAN".equals(this.tipoDato);
    }

    public boolean esString() {
        return "STRING".equals(this.tipoDato);
    }

    public boolean esDate() {
        return "DATE".equals(this.tipoDato);
    }

    public boolean esTasaIgv() {
        return "tasa_igv".equals(this.parametro);
    }

    public boolean esDiasVencimiento() {
        return "dias_vencimiento_pago".equals(this.parametro);
    }

    public boolean esFormatoFecha() {
        return "formato_fecha".equals(this.parametro);
    }

    public boolean esMonedaDefault() {
        return "moneda_default".equals(this.parametro);
    }

    public String getTipoDatoLegible() {
        if (tipoDato == null) return "No especificado";
        
        return switch (tipoDato) {
            case "DECIMAL" -> "Decimal";
            case "INT" -> "Entero";
            case "BOOLEAN" -> "Booleano";
            case "STRING" -> "Texto";
            case "DATE" -> "Fecha";
            default -> tipoDato;
        };
    }

    public String getTipoDatoBadgeClass() {
        if (tipoDato == null) return "badge-secondary";
        
        return switch (tipoDato) {
            case "DECIMAL" -> "badge-info";
            case "INT" -> "badge-warning";
            case "BOOLEAN" -> "badge-success";
            case "STRING" -> "badge-primary";
            case "DATE" -> "badge-danger";
            default -> "badge-secondary";
        };
    }

    public String getTipoDatoIcono() {
        if (tipoDato == null) return "ti ti-file";
        
        return switch (tipoDato) {
            case "DECIMAL" -> "ti ti-currency-dollar";
            case "INT" -> "ti ti-numbers";
            case "BOOLEAN" -> "ti ti-toggle-left";
            case "STRING" -> "ti ti-text";
            case "DATE" -> "ti ti-calendar";
            default -> "ti ti-file";
        };
    }

    public String getValorFormateadoDisplay() {
        if (valor == null) return "-";
        
        if (esBoolean()) {
            return getValorBoolean() != null && getValorBoolean() ? "Sí" : "No";
        }
        
        if (esDecimal()) {
            try {
                java.math.BigDecimal dec = new java.math.BigDecimal(valor);
                return dec.setScale(2, java.math.RoundingMode.HALF_UP).toString();
            } catch (NumberFormatException e) {
                return valor;
            }
        }
        
        if (esDate()) {
            try {
                LocalDateTime date = LocalDateTime.parse(valor);
                return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            } catch (Exception e) {
                return valor;
            }
        }
        
        return valor;
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"idConfig\":").append(idConfig).append(",");
        sb.append("\"parametro\":\"").append(parametro != null ? parametro : "").append("\",");
        sb.append("\"valor\":\"").append(valor != null ? valor : "").append("\",");
        sb.append("\"tipoDato\":\"").append(tipoDato != null ? tipoDato : "").append("\",");
        sb.append("\"descripcion\":\"").append(descripcion != null ? descripcion : "").append("\"");
        if (fechaActualizacion != null) {
            sb.append(",\"fechaActualizacion\":\"").append(getFechaActualizacionFormateada()).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

    public String toJsonCompleto() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"idConfig\":").append(idConfig).append(",");
        sb.append("\"parametro\":\"").append(parametro != null ? parametro : "").append("\",");
        sb.append("\"valor\":\"").append(valor != null ? valor : "").append("\",");
        sb.append("\"tipoDato\":\"").append(tipoDato != null ? tipoDato : "").append("\",");
        sb.append("\"descripcion\":\"").append(descripcion != null ? descripcion : "").append("\",");
        sb.append("\"fechaActualizacion\":\"").append(getFechaActualizacionFormateada()).append("\"");
        if (nombreUsuarioActualiza != null) {
            sb.append(",\"usuarioActualiza\":\"").append(nombreUsuarioActualiza).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

    public String getFechaActualizacionFormateada() {
        if (fechaActualizacion == null) return "-";
        return fechaActualizacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getFechaActualizacionSoloFecha() {
        if (fechaActualizacion == null) return "-";
        return fechaActualizacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getUsuarioActualizaLegible() {
        if (nombreUsuarioActualiza != null && !nombreUsuarioActualiza.isEmpty()) {
            return nombreUsuarioActualiza;
        }
        if (emailUsuarioActualiza != null && !emailUsuarioActualiza.isEmpty()) {
            return emailUsuarioActualiza;
        }
        if (idUsuarioActualiza != null) {
            return "Usuario ID: " + idUsuarioActualiza;
        }
        return "Sistema";
    }

    public String getCategoriaLegible() {
        if (categoria == null) return "General";
        return categoria;
    }

    public String getCategoriaBadgeClass() {
        if (categoria == null) return "badge-secondary";
        
        return switch (categoria) {
            case "FINANCIERO" -> "badge-success";
            case "GENERAL" -> "badge-primary";
            case "SEGURIDAD" -> "badge-danger";
            case "NOTIFICACIONES" -> "badge-info";
            default -> "badge-secondary";
        };
    }

    public String getResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append(parametro);
        sb.append(" = ").append(getValorFormateadoDisplay());
        if (descripcion != null && !descripcion.isEmpty()) {
            sb.append(" (").append(descripcion).append(")");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "ConfiguracionSistema{" +
                "idConfig=" + idConfig +
                ", parametro='" + parametro + '\'' +
                ", valor='" + valor + '\'' +
                ", tipoDato='" + tipoDato + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", fechaActualizacion=" + fechaActualizacion +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfiguracionSistema that = (ConfiguracionSistema) o;
        return idConfig != null && idConfig.equals(that.idConfig);
    }

    @Override
    public int hashCode() {
        return idConfig != null ? idConfig.hashCode() : 0;
    }

    public static ConfiguracionSistema crearTasaIgv(java.math.BigDecimal igv) {
        return new ConfiguracionSistema(
            "tasa_igv",
            igv != null ? igv.toString() : "18.00",
            "DECIMAL",
            "Porcentaje de IGV aplicado a cotizaciones y facturación"
        );
    }


    public static ConfiguracionSistema crearDiasVencimiento(Integer dias) {
        return new ConfiguracionSistema(
            "dias_vencimiento_pago",
            dias != null ? dias.toString() : "30",
            "INT",
            "Días después de inicio del proyecto para considerar pago vencido"
        );
    }

    public static ConfiguracionSistema crearFormatoFecha(String formato) {
        return new ConfiguracionSistema(
            "formato_fecha",
            formato != null ? formato : "%d/%m/%Y",
            "STRING",
            "Formato predeterminado de fechas en el sistema"
        );
    }

    public static ConfiguracionSistema crearMonedaDefault(String moneda) {
        return new ConfiguracionSistema(
            "moneda_default",
            moneda != null ? moneda : "PEN",
            "STRING",
            "Moneda por defecto (PEN: Sol Peruano)"
        );
    }

    public static ConfiguracionSistema fromDTO(com.stamos.dto.ConfiguracionSistemaDTO dto) {
        if (dto == null) {
            return null;
        }
        ConfiguracionSistema config = new ConfiguracionSistema();
        if (dto.getIdConfig() != null) {
            config.setIdConfig(dto.getIdConfig());
        }
        config.setParametro(dto.getParametro());
        config.setValor(dto.getValor());
        config.setTipoDato(dto.getTipoDato());
        config.setDescripcion(dto.getDescripcion());
        config.setCategoria(dto.getCategoria());
        return config;
    }

    public com.stamos.dto.ConfiguracionSistemaDTO toDTO() {
        com.stamos.dto.ConfiguracionSistemaDTO dto = new com.stamos.dto.ConfiguracionSistemaDTO();
        dto.setIdConfig(this.idConfig);
        dto.setParametro(this.parametro);
        dto.setValor(this.valor);
        dto.setTipoDato(this.tipoDato);
        dto.setDescripcion(this.descripcion);
        dto.setFechaActualizacion(this.fechaActualizacion);
        dto.setIdUsuarioActualiza(this.idUsuarioActualiza);
        dto.setNombreUsuarioActualiza(this.nombreUsuarioActualiza);
        dto.setEmailUsuarioActualiza(this.emailUsuarioActualiza);
        dto.setValorFormateado(this.getValorFormateadoDisplay());
        dto.setCategoria(this.categoria);
        return dto;
    }

    public static class ConfiguracionSistemaDTO {
        private Integer idConfig;
        private String parametro;
        private String valor;
        private String tipoDato;
        private String descripcion;
        private LocalDateTime fechaActualizacion;
        private Integer idUsuarioActualiza;
        private String nombreUsuarioActualiza;
        private String emailUsuarioActualiza;
        private String valorFormateado;
        private String categoria;

        public Integer getIdConfig() { return idConfig; }
        public void setIdConfig(Integer idConfig) { this.idConfig = idConfig; }

        public String getParametro() { return parametro; }
        public void setParametro(String parametro) { this.parametro = parametro; }

        public String getValor() { return valor; }
        public void setValor(String valor) { this.valor = valor; }

        public String getTipoDato() { return tipoDato; }
        public void setTipoDato(String tipoDato) { this.tipoDato = tipoDato; }

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

        public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
        public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

        public Integer getIdUsuarioActualiza() { return idUsuarioActualiza; }
        public void setIdUsuarioActualiza(Integer idUsuarioActualiza) { this.idUsuarioActualiza = idUsuarioActualiza; }

        public String getNombreUsuarioActualiza() { return nombreUsuarioActualiza; }
        public void setNombreUsuarioActualiza(String nombreUsuarioActualiza) { this.nombreUsuarioActualiza = nombreUsuarioActualiza; }

        public String getEmailUsuarioActualiza() { return emailUsuarioActualiza; }
        public void setEmailUsuarioActualiza(String emailUsuarioActualiza) { this.emailUsuarioActualiza = emailUsuarioActualiza; }

        public String getValorFormateado() { return valorFormateado; }
        public void setValorFormateado(String valorFormateado) { this.valorFormateado = valorFormateado; }

        public String getCategoria() { return categoria; }
        public void setCategoria(String categoria) { this.categoria = categoria; }

        public String getTipoDatoLegible() {
            if (tipoDato == null) return "No especificado";
            return switch (tipoDato) {
                case "DECIMAL" -> "Decimal";
                case "INT" -> "Entero";
                case "BOOLEAN" -> "Booleano";
                case "STRING" -> "Texto";
                case "DATE" -> "Fecha";
                default -> tipoDato;
            };
        }

        public String getTipoDatoBadgeClass() {
            if (tipoDato == null) return "badge-secondary";
            return switch (tipoDato) {
                case "DECIMAL" -> "badge-info";
                case "INT" -> "badge-warning";
                case "BOOLEAN" -> "badge-success";
                case "STRING" -> "badge-primary";
                case "DATE" -> "badge-danger";
                default -> "badge-secondary";
            };
        }

        public String getFechaActualizacionFormateada() {
            if (fechaActualizacion == null) return "-";
            return fechaActualizacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }

        public String getCategoriaLegible() {
            if (categoria == null) return "General";
            return categoria;
        }
    }
}