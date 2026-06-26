package com.stamos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConfiguracionSistemaDTO {

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
    private String color;
    private String icono;
    private List<ConfiguracionHistorialDTO> historialCambios = new ArrayList<>();

    public ConfiguracionSistemaDTO() {
        this.tipoDato = "STRING";
        this.categoria = "GENERAL";
        this.color = "#3B82F6";
        this.icono = "ti ti-settings";
        this.historialCambios = new ArrayList<>();
    }

    public ConfiguracionSistemaDTO(Integer idConfig, String parametro, String valor, String tipoDato) {
        this();
        this.idConfig = idConfig;
        this.parametro = parametro;
        this.valor = valor;
        this.tipoDato = tipoDato;
    }

    public ConfiguracionSistemaDTO(Integer idConfig, String parametro, String valor, 
                                  String tipoDato, String descripcion, String categoria) {
        this(idConfig, parametro, valor, tipoDato);
        this.descripcion = descripcion;
        this.categoria = categoria;
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
        this.valorFormateado = formatearValor(valor);
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
        if (valorFormateado == null && valor != null) {
            valorFormateado = formatearValor(valor);
        }
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

    public List<ConfiguracionHistorialDTO> getHistorialCambios() {
        return historialCambios;
    }

    public void setHistorialCambios(List<ConfiguracionHistorialDTO> historialCambios) {
        this.historialCambios = historialCambios;
    }

    public BigDecimal getValorDecimal() {
        if (!"DECIMAL".equals(this.tipoDato) || valor == null) {
            return null;
        }
        try {
            return new BigDecimal(valor);
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

    private String formatearValor(String valor) {
        if (valor == null) return "-";
        
        if (esBoolean()) {
            return getValorBoolean() != null && getValorBoolean() ? "Sí" : "No";
        }
        
        if (esDecimal()) {
            try {
                BigDecimal dec = new BigDecimal(valor);
                return dec.setScale(2, java.math.RoundingMode.HALF_UP).toString();
            } catch (NumberFormatException e) {
                return valor;
            }
        }
        
        if (esDate()) {
            try {
                LocalDateTime date = LocalDateTime.parse(valor);
                return date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            } catch (Exception e) {
                return valor;
            }
        }
        
        return valor;
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

    public void agregarCambio(ConfiguracionHistorialDTO cambio) {
        if (cambio != null && this.historialCambios != null) {
            this.historialCambios.add(cambio);
        }
    }

    public static ConfiguracionSistemaDTO fromEntity(com.stamos.model.ConfiguracionSistema config) {
        if (config == null) {
            return null;
        }

        ConfiguracionSistemaDTO dto = new ConfiguracionSistemaDTO();
        dto.setIdConfig(config.getIdConfig());
        dto.setParametro(config.getParametro());
        dto.setValor(config.getValor());
        dto.setTipoDato(config.getTipoDato());
        dto.setDescripcion(config.getDescripcion());
        dto.setFechaActualizacion(config.getFechaActualizacion());
        dto.setIdUsuarioActualiza(config.getIdUsuarioActualiza());
        dto.setNombreUsuarioActualiza(config.getNombreUsuarioActualiza());
        dto.setEmailUsuarioActualiza(config.getEmailUsuarioActualiza());
        dto.setValorFormateado(config.getValorFormateado());
        dto.setCategoria(config.getCategoria());
        dto.setColor(obtenerColorCategoria(config.getCategoria()));
        dto.setIcono(obtenerIconoCategoria(config.getCategoria()));

        return dto;
    }

    private static String obtenerColorCategoria(String categoria) {
        if (categoria == null) return "#3B82F6";
        
        return switch (categoria) {
            case "FINANCIERO" -> "#10B981";
            case "GENERAL" -> "#3B82F6";
            case "SEGURIDAD" -> "#EF4444";
            case "NOTIFICACIONES" -> "#8B5CF6";
            default -> "#3B82F6";
        };
    }

    private static String obtenerIconoCategoria(String categoria) {
        if (categoria == null) return "ti ti-settings";
        
        return switch (categoria) {
            case "FINANCIERO" -> "ti ti-currency-dollar";
            case "GENERAL" -> "ti ti-settings";
            case "SEGURIDAD" -> "ti ti-shield";
            case "NOTIFICACIONES" -> "ti ti-bell";
            default -> "ti ti-settings";
        };
    }

    public static ConfiguracionSistemaDTO crearTasaIgv(BigDecimal igv) {
        ConfiguracionSistemaDTO dto = new ConfiguracionSistemaDTO();
        dto.setParametro("tasa_igv");
        dto.setValor(igv != null ? igv.toString() : "18.00");
        dto.setTipoDato("DECIMAL");
        dto.setDescripcion("Porcentaje de IGV aplicado a cotizaciones y facturación");
        dto.setCategoria("FINANCIERO");
        dto.setColor("#10B981");
        dto.setIcono("ti ti-currency-dollar");
        return dto;
    }

    public static ConfiguracionSistemaDTO crearDiasVencimiento(Integer dias) {
        ConfiguracionSistemaDTO dto = new ConfiguracionSistemaDTO();
        dto.setParametro("dias_vencimiento_pago");
        dto.setValor(dias != null ? dias.toString() : "30");
        dto.setTipoDato("INT");
        dto.setDescripcion("Días después de inicio del proyecto para considerar pago vencido");
        dto.setCategoria("GENERAL");
        dto.setColor("#3B82F6");
        dto.setIcono("ti ti-settings");
        return dto;
    }

    public static ConfiguracionSistemaDTO crearFormatoFecha(String formato) {
        ConfiguracionSistemaDTO dto = new ConfiguracionSistemaDTO();
        dto.setParametro("formato_fecha");
        dto.setValor(formato != null ? formato : "%d/%m/%Y");
        dto.setTipoDato("STRING");
        dto.setDescripcion("Formato predeterminado de fechas en el sistema");
        dto.setCategoria("GENERAL");
        dto.setColor("#8B5CF6");
        dto.setIcono("ti ti-calendar");
        return dto;
    }

    public static ConfiguracionSistemaDTO crearMonedaDefault(String moneda) {
        ConfiguracionSistemaDTO dto = new ConfiguracionSistemaDTO();
        dto.setParametro("moneda_default");
        dto.setValor(moneda != null ? moneda : "PEN");
        dto.setTipoDato("STRING");
        dto.setDescripcion("Moneda por defecto (PEN: Sol Peruano)");
        dto.setCategoria("FINANCIERO");
        dto.setColor("#10B981");
        dto.setIcono("ti ti-currency-dollar");
        return dto;
    }

    public com.stamos.model.ConfiguracionSistema toEntity() {
        com.stamos.model.ConfiguracionSistema config = new com.stamos.model.ConfiguracionSistema();
        config.setIdConfig(this.idConfig);
        config.setParametro(this.parametro);
        config.setValor(this.valor);
        config.setTipoDato(this.tipoDato);
        config.setDescripcion(this.descripcion);
        config.setFechaActualizacion(this.fechaActualizacion);
        config.setIdUsuarioActualiza(this.idUsuarioActualiza);
        config.setCategoria(this.categoria);
        return config;
    }

    public static class ConfiguracionHistorialDTO {
        
        private Integer idHistorial;
        private String valorAnterior;
        private String valorNuevo;
        private LocalDateTime fechaCambio;
        private Integer idUsuarioCambio;
        private String nombreUsuarioCambio;
        private String emailUsuarioCambio;

        public ConfiguracionHistorialDTO() {}

        public ConfiguracionHistorialDTO(String valorAnterior, String valorNuevo, 
                                        LocalDateTime fechaCambio, String nombreUsuarioCambio) {
            this.valorAnterior = valorAnterior;
            this.valorNuevo = valorNuevo;
            this.fechaCambio = fechaCambio;
            this.nombreUsuarioCambio = nombreUsuarioCambio;
        }

        public Integer getIdHistorial() { return idHistorial; }
        public void setIdHistorial(Integer idHistorial) { this.idHistorial = idHistorial; }

        public String getValorAnterior() { return valorAnterior; }
        public void setValorAnterior(String valorAnterior) { this.valorAnterior = valorAnterior; }

        public String getValorNuevo() { return valorNuevo; }
        public void setValorNuevo(String valorNuevo) { this.valorNuevo = valorNuevo; }

        public LocalDateTime getFechaCambio() { return fechaCambio; }
        public void setFechaCambio(LocalDateTime fechaCambio) { this.fechaCambio = fechaCambio; }

        public Integer getIdUsuarioCambio() { return idUsuarioCambio; }
        public void setIdUsuarioCambio(Integer idUsuarioCambio) { this.idUsuarioCambio = idUsuarioCambio; }

        public String getNombreUsuarioCambio() { return nombreUsuarioCambio; }
        public void setNombreUsuarioCambio(String nombreUsuarioCambio) { this.nombreUsuarioCambio = nombreUsuarioCambio; }

        public String getEmailUsuarioCambio() { return emailUsuarioCambio; }
        public void setEmailUsuarioCambio(String emailUsuarioCambio) { this.emailUsuarioCambio = emailUsuarioCambio; }
    }

    @Override
    public String toString() {
        return "ConfiguracionSistemaDTO{" +
                "idConfig=" + idConfig +
                ", parametro='" + parametro + '\'' +
                ", valor='" + valor + '\'' +
                ", tipoDato='" + tipoDato + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", categoria='" + categoria + '\'' +
                ", valorFormateado='" + valorFormateado + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfiguracionSistemaDTO that = (ConfiguracionSistemaDTO) o;
        return idConfig != null && idConfig.equals(that.idConfig);
    }

    @Override
    public int hashCode() {
        return idConfig != null ? idConfig.hashCode() : 0;
    }
}