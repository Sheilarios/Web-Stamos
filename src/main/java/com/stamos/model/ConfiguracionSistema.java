package com.stamos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Entidad ConfiguracionSistema.
 * 
 * <p>Esta entidad almacena los parámetros de configuración del sistema
 * de STAMOS S.L., permitiendo la personalización y ajuste dinámico
 * de valores clave como tasas impositivas, plazos y formatos.</p>
 * 
 * <p>Implementa los requerimientos:</p>
 * <ul>
 *   <li>RN-21: STAMOS S.L. aplica una tasa IGV del 18%</li>
 *   <li>RN-22: Las cotizaciones ya emitidas mantienen su tasa IGV</li>
 *   <li>RNF-07: Configuración de IGV a través de la tabla ConfiguracionSistema</li>
 * </ul>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
@Entity
@Table(name = "ConfiguracionSistema",
       indexes = {
           @Index(name = "idx_config_parametro", columnList = "parametro", unique = true),
           @Index(name = "idx_config_tipo_dato", columnList = "tipo_dato"),
           @Index(name = "idx_config_fecha", columnList = "fecha_actualizacion"),
           @Index(name = "idx_config_usuario", columnList = "id_usuario_actualiza")
       })
public class ConfiguracionSistema {

    // ============================================
    // ATRIBUTOS PRINCIPALES
    // ============================================

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

    // ============================================
    // CAMPOS ADICIONALES (NO EN BD)
    // ============================================

    @Transient
    private String nombreUsuarioActualiza;

    @Transient
    private String emailUsuarioActualiza;

    @Transient
    private String valorFormateado;

    @Transient
    private String categoria;

    // ============================================
    // CONSTRUCTORES
    // ============================================

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

    // ============================================
    // GETTERS Y SETTERS
    // ============================================

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

    // ============================================
    // MÉTODOS DE NEGOCIO
    // ============================================

    /**
     * Obtiene el valor como BigDecimal (para tipo DECIMAL).
     * 
     * @return Valor como BigDecimal, o null si no es DECIMAL
     */
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

    /**
     * Obtiene el valor como Integer (para tipo INT).
     * 
     * @return Valor como Integer, o null si no es INT
     */
    public Integer getValorInteger() {
        if (!"INT".equals(this.tipoDato) || valor == null) {
            return null;
        }
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Obtiene el valor como Boolean (para tipo BOOLEAN).
     * 
     * @return Valor como Boolean, o null si no es BOOLEAN
     */
    public Boolean getValorBoolean() {
        if (!"BOOLEAN".equals(this.tipoDato) || valor == null) {
            return null;
        }
        return "true".equalsIgnoreCase(valor) || "1".equals(valor) || "si".equalsIgnoreCase(valor);
    }

    /**
     * Obtiene el valor como LocalDate (para tipo DATE).
     * 
     * @return Valor como LocalDate, o null si no es DATE
     */
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

    /**
     * Obtiene el valor como String (para cualquier tipo).
     * 
     * @return Valor como String
     */
    public String getValorString() {
        return valor;
    }

    /**
     * Actualiza el valor del parámetro.
     * 
     * @param valor Nuevo valor
     * @param idUsuario ID del usuario que actualiza
     */
    public void actualizarValor(String valor, Integer idUsuario) {
        this.valor = valor;
        this.idUsuarioActualiza = idUsuario;
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Verifica si el parámetro es de tipo DECIMAL.
     * 
     * @return true si es DECIMAL, false en caso contrario
     */
    public boolean esDecimal() {
        return "DECIMAL".equals(this.tipoDato);
    }

    /**
     * Verifica si el parámetro es de tipo INT.
     * 
     * @return true si es INT, false en caso contrario
     */
    public boolean esInteger() {
        return "INT".equals(this.tipoDato);
    }

    /**
     * Verifica si el parámetro es de tipo BOOLEAN.
     * 
     * @return true si es BOOLEAN, false en caso contrario
     */
    public boolean esBoolean() {
        return "BOOLEAN".equals(this.tipoDato);
    }

    /**
     * Verifica si el parámetro es de tipo STRING.
     * 
     * @return true si es STRING, false en caso contrario
     */
    public boolean esString() {
        return "STRING".equals(this.tipoDato);
    }

    /**
     * Verifica si el parámetro es de tipo DATE.
     * 
     * @return true si es DATE, false en caso contrario
     */
    public boolean esDate() {
        return "DATE".equals(this.tipoDato);
    }

    /**
     * Verifica si el parámetro es la tasa IGV.
     * 
     * @return true si es tasa_igv, false en caso contrario
     */
    public boolean esTasaIgv() {
        return "tasa_igv".equals(this.parametro);
    }

    /**
     * Verifica si el parámetro es días de vencimiento.
     * 
     * @return true si es dias_vencimiento_pago, false en caso contrario
     */
    public boolean esDiasVencimiento() {
        return "dias_vencimiento_pago".equals(this.parametro);
    }

    /**
     * Verifica si el parámetro es formato de fecha.
     * 
     * @return true si es formato_fecha, false en caso contrario
     */
    public boolean esFormatoFecha() {
        return "formato_fecha".equals(this.parametro);
    }

    /**
     * Verifica si el parámetro es moneda por defecto.
     * 
     * @return true si es moneda_default, false en caso contrario
     */
    public boolean esMonedaDefault() {
        return "moneda_default".equals(this.parametro);
    }

    /**
     * Obtiene el tipo de dato en formato legible.
     * 
     * @return Tipo de dato legible
     */
    public String getTipoDatoLegible() {
        if (tipoDato == null) return "No especificado";
        
        switch (tipoDato) {
            case "DECIMAL":
                return "Decimal";
            case "INT":
                return "Entero";
            case "BOOLEAN":
                return "Booleano";
            case "STRING":
                return "Texto";
            case "DATE":
                return "Fecha";
            default:
                return tipoDato;
        }
    }

    /**
     * Obtiene la clase CSS para el badge del tipo de dato.
     * 
     * @return Clase CSS
     */
    public String getTipoDatoBadgeClass() {
        if (tipoDato == null) return "badge-secondary";
        
        switch (tipoDato) {
            case "DECIMAL":
                return "badge-info";
            case "INT":
                return "badge-warning";
            case "BOOLEAN":
                return "badge-success";
            case "STRING":
                return "badge-primary";
            case "DATE":
                return "badge-danger";
            default:
                return "badge-secondary";
        }
    }

    /**
     * Obtiene el ícono para el tipo de dato.
     * 
     * @return Ícono CSS
     */
    public String getTipoDatoIcono() {
        if (tipoDato == null) return "ti ti-file";
        
        switch (tipoDato) {
            case "DECIMAL":
                return "ti ti-currency-dollar";
            case "INT":
                return "ti ti-numbers";
            case "BOOLEAN":
                return "ti ti-toggle-left";
            case "STRING":
                return "ti ti-text";
            case "DATE":
                return "ti ti-calendar";
            default:
                return "ti ti-file";
        }
    }

    /**
     * Obtiene el valor formateado según su tipo de dato.
     * 
     * @return Valor formateado
     */
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

    /**
     * Convierte a JSON con todos los campos.
     * 
     * @return JSON completo
     */
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

    // ============================================
    // MÉTODOS DE FORMATO PARA LA VISTA
    // ============================================

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
     * Obtiene la fecha de actualización formateada (solo fecha).
     * 
     * @return Fecha formateada
     */
    public String getFechaActualizacionSoloFecha() {
        if (fechaActualizacion == null) return "-";
        return fechaActualizacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Obtiene el usuario actualizador en formato legible.
     * 
     * @return Usuario legible
     */
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

    /**
     * Obtiene la categoría del parámetro en formato legible.
     * 
     * @return Categoría legible
     */
    public String getCategoriaLegible() {
        if (categoria == null) return "General";
        return categoria;
    }

    /**
     * Obtiene la clase CSS para la categoría.
     * 
     * @return Clase CSS
     */
    public String getCategoriaBadgeClass() {
        if (categoria == null) return "badge-secondary";
        
        switch (categoria) {
            case "FINANCIERO":
                return "badge-success";
            case "GENERAL":
                return "badge-primary";
            case "SEGURIDAD":
                return "badge-danger";
            case "NOTIFICACIONES":
                return "badge-info";
            default:
                return "badge-secondary";
        }
    }

    /**
     * Obtiene el resumen del parámetro.
     * 
     * @return Resumen
     */
    public String getResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append(parametro);
        sb.append(" = ").append(getValorFormateadoDisplay());
        if (descripcion != null && !descripcion.isEmpty()) {
            sb.append(" (").append(descripcion).append(")");
        }
        return sb.toString();
    }

    // ============================================
    // MÉTODOS OVERRIDE
    // ============================================

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

    // ============================================
    // MÉTODOS ESTÁTICOS DE FÁBRICA
    // ============================================

    /**
     * Crea la configuración de la tasa IGV.
     * 
     * @param igv Valor de la tasa IGV
     * @return Configuración de sistema
     */
    public static ConfiguracionSistema crearTasaIgv(java.math.BigDecimal igv) {
        return new ConfiguracionSistema(
            "tasa_igv",
            igv != null ? igv.toString() : "18.00",
            "DECIMAL",
            "Porcentaje de IGV aplicado a cotizaciones y facturación"
        );
    }

    /**
     * Crea la configuración de días de vencimiento de pago.
     * 
     * @param dias Días de vencimiento
     * @return Configuración de sistema
     */
    public static ConfiguracionSistema crearDiasVencimiento(Integer dias) {
        return new ConfiguracionSistema(
            "dias_vencimiento_pago",
            dias != null ? dias.toString() : "30",
            "INT",
            "Días después de inicio del proyecto para considerar pago vencido"
        );
    }

    /**
     * Crea la configuración de formato de fecha.
     * 
     * @param formato Formato de fecha
     * @return Configuración de sistema
     */
    public static ConfiguracionSistema crearFormatoFecha(String formato) {
        return new ConfiguracionSistema(
            "formato_fecha",
            formato != null ? formato : "%d/%m/%Y",
            "STRING",
            "Formato predeterminado de fechas en el sistema"
        );
    }

    /**
     * Crea la configuración de moneda por defecto.
     * 
     * @param moneda Moneda por defecto
     * @return Configuración de sistema
     */
    public static ConfiguracionSistema crearMonedaDefault(String moneda) {
        return new ConfiguracionSistema(
            "moneda_default",
            moneda != null ? moneda : "PEN",
            "STRING",
            "Moneda por defecto (PEN: Sol Peruano)"
        );
    }

    /**
     * Crea la configuración desde un DTO.
     * 
     * @param dto DTO de configuración
     * @return Configuración de sistema
     */
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

    /**
     * Convierte la entidad a un DTO.
     * 
     * @return DTO de configuración
     */
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

    // ============================================
    // CLASE PARA DTO DE CONFIGURACIÓN
    // ============================================

    /**
     * DTO para ConfiguracionSistema.
     */
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

        // Getters y Setters
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

        // Métodos de formato
        public String getTipoDatoLegible() {
            if (tipoDato == null) return "No especificado";
            switch (tipoDato) {
                case "DECIMAL": return "Decimal";
                case "INT": return "Entero";
                case "BOOLEAN": return "Booleano";
                case "STRING": return "Texto";
                case "DATE": return "Fecha";
                default: return tipoDato;
            }
        }

        public String getTipoDatoBadgeClass() {
            if (tipoDato == null) return "badge-secondary";
            switch (tipoDato) {
                case "DECIMAL": return "badge-info";
                case "INT": return "badge-warning";
                case "BOOLEAN": return "badge-success";
                case "STRING": return "badge-primary";
                case "DATE": return "badge-danger";
                default: return "badge-secondary";
            }
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