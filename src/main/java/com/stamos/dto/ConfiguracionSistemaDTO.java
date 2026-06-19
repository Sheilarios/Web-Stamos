package com.stamos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO (Data Transfer Object) para la entidad ConfiguracionSistema.
 * 
 * <p>Este DTO se utiliza para transferir datos de configuración del sistema
 * entre la capa de servicio y la capa de presentación, permitiendo una
 * mejor organización y manejo de los parámetros de configuración.</p>
 * 
 * <p>Incluye información del parámetro, su valor, tipo de dato,
 * categoría y metadatos de auditoría.</p>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
public class ConfiguracionSistemaDTO {

    // ============================================
    // ATRIBUTOS PRINCIPALES
    // ============================================

    private Integer idConfig;
    private String parametro;
    private String valor;
    private String tipoDato;
    private String descripcion;

    // ============================================
    // CAMPOS DE AUDITORÍA
    // ============================================

    private LocalDateTime fechaActualizacion;
    private Integer idUsuarioActualiza;
    private String nombreUsuarioActualiza;
    private String emailUsuarioActualiza;

    // ============================================
    // CAMPOS FORMATEADOS
    // ============================================

    private String valorFormateado;
    private String categoria;

    // ============================================
    // CAMPOS ADICIONALES
    // ============================================

    private String color;
    private String icono;
    private List<ConfiguracionHistorialDTO> historialCambios = new ArrayList<>();

    // ============================================
    // CONSTRUCTORES
    // ============================================

    /**
     * Constructor por defecto.
     */
    public ConfiguracionSistemaDTO() {
        this.tipoDato = "STRING";
        this.categoria = "GENERAL";
        this.color = "#3B82F6";
        this.icono = "ti ti-settings";
        this.historialCambios = new ArrayList<>();
    }

    /**
     * Constructor con campos principales.
     * 
     * @param idConfig ID de la configuración
     * @param parametro Nombre del parámetro
     * @param valor Valor del parámetro
     * @param tipoDato Tipo de dato
     */
    public ConfiguracionSistemaDTO(Integer idConfig, String parametro, String valor, String tipoDato) {
        this();
        this.idConfig = idConfig;
        this.parametro = parametro;
        this.valor = valor;
        this.tipoDato = tipoDato;
    }

    /**
     * Constructor completo.
     * 
     * @param idConfig ID de la configuración
     * @param parametro Nombre del parámetro
     * @param valor Valor del parámetro
     * @param tipoDato Tipo de dato
     * @param descripcion Descripción del parámetro
     * @param categoria Categoría del parámetro
     */
    public ConfiguracionSistemaDTO(Integer idConfig, String parametro, String valor, 
                                  String tipoDato, String descripcion, String categoria) {
        this(idConfig, parametro, valor, tipoDato);
        this.descripcion = descripcion;
        this.categoria = categoria;
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

    // ============================================
    // MÉTODOS DE NEGOCIO
    // ============================================

    /**
     * Obtiene el valor como BigDecimal (para tipo DECIMAL).
     * 
     * @return Valor como BigDecimal, o null si no es DECIMAL
     */
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
     * Obtiene el valor como LocalDateTime (para tipo DATE).
     * 
     * @return Valor como LocalDateTime, o null si no es DATE
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
     * Formatea el valor según su tipo de dato.
     * 
     * @param valor Valor a formatear
     * @return Valor formateado
     */
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
                return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            } catch (Exception e) {
                return valor;
            }
        }
        
        return valor;
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
     * Agrega un cambio al historial.
     * 
     * @param cambio Cambio en el historial
     */
    public void agregarCambio(ConfiguracionHistorialDTO cambio) {
        if (cambio != null && this.historialCambios != null) {
            this.historialCambios.add(cambio);
        }
    }

    /**
     * Obtiene el número de cambios en el historial.
     * 
     * @return Número de cambios
     */
    public int getTotalCambios() {
        return historialCambios != null ? historialCambios.size() : 0;
    }

    /**
     * Verifica si el parámetro ha tenido cambios.
     * 
     * @return true si tiene cambios, false en caso contrario
     */
    public boolean tieneCambios() {
        return getTotalCambios() > 0;
    }

    // ============================================
    // MÉTODOS DE FORMATO PARA LA VISTA
    // ============================================

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
     * Obtiene la clase CSS para el badge de la categoría.
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
     * Obtiene la categoría en formato legible.
     * 
     * @return Categoría legible
     */
    public String getCategoriaLegible() {
        if (categoria == null) return "General";
        
        switch (categoria) {
            case "FINANCIERO":
                return "Financiero";
            case "GENERAL":
                return "General";
            case "SEGURIDAD":
                return "Seguridad";
            case "NOTIFICACIONES":
                return "Notificaciones";
            default:
                return categoria;
        }
    }

    /**
     * Obtiene el ícono para la categoría.
     * 
     * @return Ícono CSS
     */
    public String getCategoriaIcono() {
        if (categoria == null) return "ti ti-settings";
        
        switch (categoria) {
            case "FINANCIERO":
                return "ti ti-currency-dollar";
            case "GENERAL":
                return "ti ti-settings";
            case "SEGURIDAD":
                return "ti ti-shield";
            case "NOTIFICACIONES":
                return "ti ti-bell";
            default:
                return "ti ti-settings";
        }
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
     * Obtiene el resumen del parámetro.
     * 
     * @return Resumen
     */
    public String getResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append(parametro);
        sb.append(" = ").append(getValorFormateado());
        if (descripcion != null && !descripcion.isEmpty()) {
            sb.append(" (").append(descripcion).append(")");
        }
        return sb.toString();
    }

    /**
     * Obtiene el color para el estado del parámetro.
     * 
     * @return Color en hexadecimal
     */
    public String getColorEstado() {
        if (color == null) return "#3B82F6";
        return color;
    }

    /**
     * Obtiene el nombre del parámetro en mayúsculas.
     * 
     * @return Nombre en mayúsculas
     */
    public String getParametroUpper() {
        return parametro != null ? parametro.toUpperCase() : "";
    }

    /**
     * Obtiene el parámetro con su ícono.
     * 
     * @return Parámetro con ícono
     */
    public String getParametroConIcono() {
        return "<i class='" + getIcono() + "'></i> " + parametro;
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
        sb.append("\"descripcion\":\"").append(descripcion != null ? descripcion : "").append("\",");
        sb.append("\"valorFormateado\":\"").append(getValorFormateado()).append("\"");
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
        sb.append("\"categoria\":\"").append(categoria != null ? categoria : "").append("\",");
        sb.append("\"valorFormateado\":\"").append(getValorFormateado()).append("\",");
        sb.append("\"fechaActualizacion\":\"").append(getFechaActualizacionFormateada()).append("\",");
        sb.append("\"usuarioActualiza\":\"").append(getUsuarioActualizaLegible()).append("\"");
        if (historialCambios != null && !historialCambios.isEmpty()) {
            sb.append(",\"totalCambios\":").append(historialCambios.size());
        }
        sb.append("}");
        return sb.toString();
    }

    // ============================================
    // MÉTODOS OVERRIDE
    // ============================================

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

    // ============================================
    // CLASE INTERNA PARA HISTORIAL DE CAMBIOS
    // ============================================

    /**
     * DTO para el historial de cambios de configuración.
     */
    public static class ConfiguracionHistorialDTO {
        
        private Integer idHistorial;
        private String valorAnterior;
        private String valorNuevo;
        private LocalDateTime fechaCambio;
        private Integer idUsuarioCambio;
        private String nombreUsuarioCambio;
        private String emailUsuarioCambio;

        // Constructores
        public ConfiguracionHistorialDTO() {}

        public ConfiguracionHistorialDTO(String valorAnterior, String valorNuevo, 
                                        LocalDateTime fechaCambio, String nombreUsuarioCambio) {
            this.valorAnterior = valorAnterior;
            this.valorNuevo = valorNuevo;
            this.fechaCambio = fechaCambio;
            this.nombreUsuarioCambio = nombreUsuarioCambio;
        }

        // Getters y Setters
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

        // Métodos de formato
        public String getFechaCambioFormateada() {
            if (fechaCambio == null) return "-";
            return fechaCambio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }

        public String getUsuarioLegible() {
            if (nombreUsuarioCambio != null && !nombreUsuarioCambio.isEmpty()) {
                return nombreUsuarioCambio;
            }
            if (emailUsuarioCambio != null && !emailUsuarioCambio.isEmpty()) {
                return emailUsuarioCambio;
            }
            return "Sistema";
        }

        public String getValorAnteriorFormateado() {
            return valorAnterior != null ? valorAnterior : "-";
        }

        public String getValorNuevoFormateado() {
            return valorNuevo != null ? valorNuevo : "-";
        }

        @Override
        public String toString() {
            return "ConfiguracionHistorialDTO{" +
                    "valorAnterior='" + valorAnterior + '\'' +
                    ", valorNuevo='" + valorNuevo + '\'' +
                    ", fechaCambio=" + fechaCambio +
                    ", usuario='" + getUsuarioLegible() + '\'' +
                    '}';
        }
    }

    // ============================================
    // MÉTODOS ESTÁTICOS DE FÁBRICA
    // ============================================

    /**
     * Crea un DTO desde una entidad.
     * 
     * @param config Entidad de configuración
     * @return DTO de configuración
     */
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

        // Asignar color e ícono según categoría
        dto.setColor(obtenerColorCategoria(config.getCategoria()));
        dto.setIcono(obtenerIconoCategoria(config.getCategoria()));

        return dto;
    }

    /**
     * Obtiene el color según la categoría.
     * 
     * @param categoria Categoría
     * @return Color en hexadecimal
     */
    private static String obtenerColorCategoria(String categoria) {
        if (categoria == null) return "#3B82F6";
        
        switch (categoria) {
            case "FINANCIERO":
                return "#10B981";
            case "GENERAL":
                return "#3B82F6";
            case "SEGURIDAD":
                return "#EF4444";
            case "NOTIFICACIONES":
                return "#8B5CF6";
            default:
                return "#3B82F6";
        }
    }

    /**
     * Obtiene el ícono según la categoría.
     * 
     * @param categoria Categoría
     * @return Ícono CSS
     */
    private static String obtenerIconoCategoria(String categoria) {
        if (categoria == null) return "ti ti-settings";
        
        switch (categoria) {
            case "FINANCIERO":
                return "ti ti-currency-dollar";
            case "GENERAL":
                return "ti ti-settings";
            case "SEGURIDAD":
                return "ti ti-shield";
            case "NOTIFICACIONES":
                return "ti ti-bell";
            default:
                return "ti ti-settings";
        }
    }

    /**
     * Crea un DTO para la tasa IGV.
     * 
     * @param igv Valor de la tasa IGV
     * @return DTO de configuración
     */
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

    /**
     * Crea un DTO para días de vencimiento.
     * 
     * @param dias Días de vencimiento
     * @return DTO de configuración
     */
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

    /**
     * Crea un DTO para formato de fecha.
     * 
     * @param formato Formato de fecha
     * @return DTO de configuración
     */
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

    /**
     * Crea un DTO para moneda por defecto.
     * 
     * @param moneda Moneda por defecto
     * @return DTO de configuración
     */
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

    /**
     * Convierte el DTO a una entidad.
     * 
     * @return Entidad de configuración
     */
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
}