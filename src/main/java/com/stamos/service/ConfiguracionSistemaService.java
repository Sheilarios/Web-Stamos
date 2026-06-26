package com.stamos.service;

import com.stamos.model.ConfiguracionSistema;
import com.stamos.repository.ConfiguracionSistemaRepository;
import com.stamos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConfiguracionSistemaService {

    @Autowired
    private ConfiguracionSistemaRepository configuracionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    public static final String PARAM_TASA_IGV = "tasa_igv";
    public static final String PARAM_DIAS_VENCIMIENTO = "dias_vencimiento_pago";
    public static final String PARAM_FORMATO_FECHA = "formato_fecha";
    public static final String PARAM_MONEDA_DEFAULT = "moneda_default";

    public static final String CATEGORIA_FINANCIERO = "FINANCIERO";
    public static final String CATEGORIA_GENERAL = "GENERAL";
    public static final String CATEGORIA_SEGURIDAD = "SEGURIDAD";
    public static final String CATEGORIA_NOTIFICACIONES = "NOTIFICACIONES";

    @Transactional
    public ConfiguracionSistema crear(ConfiguracionSistema configuracion, Integer idUsuarioActualiza) {
        if (configuracionRepository.existsByParametro(configuracion.getParametro())) {
            throw new RuntimeException("Ya existe un parámetro con el nombre: " + configuracion.getParametro());
        }

        if (configuracion.getParametro() == null || configuracion.getParametro().trim().isEmpty()) {
            throw new RuntimeException("El nombre del parámetro es obligatorio");
        }
        if (configuracion.getValor() == null || configuracion.getValor().trim().isEmpty()) {
            throw new RuntimeException("El valor del parámetro es obligatorio");
        }

        configuracion.setFechaActualizacion(LocalDateTime.now());
        configuracion.setIdUsuarioActualiza(idUsuarioActualiza);

        if (idUsuarioActualiza != null) {
            usuarioRepository.findById(idUsuarioActualiza).ifPresent(usuario -> {
                configuracion.setNombreUsuarioActualiza(usuario.getNombreCompleto());
                configuracion.setEmailUsuarioActualiza(usuario.getEmail());
            });
        }

        return configuracionRepository.save(configuracion);
    }

    @Transactional
    public ConfiguracionSistema actualizar(ConfiguracionSistema configuracion) {
        ConfiguracionSistema existente = configuracionRepository.findById(configuracion.getIdConfig())
                .orElseThrow(() -> new RuntimeException("Configuración no encontrada"));

        if (!existente.getParametro().equals(configuracion.getParametro()) &&
            configuracionRepository.existsByParametro(configuracion.getParametro())) {
            throw new RuntimeException("Ya existe otro parámetro con el nombre: " + configuracion.getParametro());
        }

        if (configuracion.getParametro() == null || configuracion.getParametro().trim().isEmpty()) {
            throw new RuntimeException("El nombre del parámetro es obligatorio");
        }
        if (configuracion.getValor() == null || configuracion.getValor().trim().isEmpty()) {
            throw new RuntimeException("El valor del parámetro es obligatorio");
        }

        if (!validarValor(configuracion.getTipoDato(), configuracion.getValor())) {
            throw new RuntimeException("El valor no es válido para el tipo de dato: " + configuracion.getTipoDato());
        }

        existente.setParametro(configuracion.getParametro());
        existente.setValor(configuracion.getValor());
        existente.setTipoDato(configuracion.getTipoDato());
        existente.setDescripcion(configuracion.getDescripcion());
        existente.setFechaActualizacion(LocalDateTime.now());
        existente.setIdUsuarioActualiza(configuracion.getIdUsuarioActualiza());

        if (configuracion.getIdUsuarioActualiza() != null) {
            usuarioRepository.findById(configuracion.getIdUsuarioActualiza()).ifPresent(usuario -> {
                existente.setNombreUsuarioActualiza(usuario.getNombreCompleto());
                existente.setEmailUsuarioActualiza(usuario.getEmail());
            });
        }

        return configuracionRepository.save(existente);
    }

    @Transactional
    public void actualizarValor(String parametro, String valor, Integer idUsuarioActualiza) {
        if (!configuracionRepository.existsByParametro(parametro)) {
            throw new RuntimeException("Parámetro no encontrado: " + parametro);
        }

        ConfiguracionSistema config = configuracionRepository.findByParametro(parametro)
                .orElseThrow(() -> new RuntimeException("Configuración no encontrada"));
        
        if (!validarValor(config.getTipoDato(), valor)) {
            throw new RuntimeException("El valor no es válido para el tipo de dato: " + config.getTipoDato());
        }

        configuracionRepository.updateValorByParametro(parametro, valor, idUsuarioActualiza);
    }

    @Transactional
    public void actualizarTasaIgv(BigDecimal igv, Integer idUsuarioActualiza) {
        if (igv == null || igv.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("La tasa IGV debe ser mayor o igual a 0");
        }
        if (igv.compareTo(new BigDecimal("100")) > 0) {
            throw new RuntimeException("La tasa IGV no puede ser mayor a 100");
        }

        BigDecimal valorAnterior = getIgv();
        
        configuracionRepository.updateTasaIgv(igv.toString(), idUsuarioActualiza);

        Map<String, Object> datos = new HashMap<>();
        datos.put("parametro", "tasa_igv");
        datos.put("valor_anterior", valorAnterior != null ? valorAnterior.toString() : "18.00");
        datos.put("nuevo_valor", igv.toString());
        datos.put("fecha_cambio", LocalDateTime.now().toString());

        auditoriaService.registrarAccion(
            "UPDATE",
            "ConfiguracionSistema",
            null,
            null,
            datos,
            null
        );
    }

    @Transactional
    public void actualizarDiasVencimiento(Integer dias, Integer idUsuarioActualiza) {
        if (dias == null || dias < 0) {
            throw new RuntimeException("Los días de vencimiento deben ser mayores o iguales a 0");
        }
        if (dias > 365) {
            throw new RuntimeException("Los días de vencimiento no pueden ser mayores a 365");
        }

        Integer valorAnterior = getDiasVencimientoPago();

        configuracionRepository.updateDiasVencimiento(dias.toString(), idUsuarioActualiza);

        Map<String, Object> datos = new HashMap<>();
        datos.put("parametro", "dias_vencimiento_pago");
        datos.put("valor_anterior", valorAnterior != null ? valorAnterior.toString() : "30");
        datos.put("nuevo_valor", dias.toString());
        datos.put("fecha_cambio", LocalDateTime.now().toString());

        auditoriaService.registrarAccion(
            "UPDATE",
            "ConfiguracionSistema",
            null,
            null,
            datos,
            null
        );
    }

    @Transactional
    public void eliminar(Integer idConfig) {
        ConfiguracionSistema config = configuracionRepository.findById(idConfig)
                .orElseThrow(() -> new RuntimeException("Configuración no encontrada"));

        if (esParametroCritico(config.getParametro())) {
            throw new RuntimeException("No se puede eliminar un parámetro crítico del sistema");
        }

        configuracionRepository.delete(config);
    }

    @Transactional
    public void eliminarPorParametro(String parametro) {
        if (esParametroCritico(parametro)) {
            throw new RuntimeException("No se puede eliminar un parámetro crítico del sistema");
        }
        configuracionRepository.deleteByParametro(parametro);
    }

    public ConfiguracionSistema findById(Integer idConfig) {
        return configuracionRepository.findById(idConfig).orElse(null);
    }

    public ConfiguracionSistema findByParametro(String parametro) {
        return configuracionRepository.findByParametro(parametro).orElse(null);
    }

    public List<ConfiguracionSistema> findAll() {
        return configuracionRepository.findAllOrderByParametro();
    }

    public List<ConfiguracionSistema> findByTipoDato(String tipoDato) {
        return configuracionRepository.findByTipoDato(tipoDato);
    }

    public List<ConfiguracionSistema> search(String searchText) {
        return configuracionRepository.search(searchText);
    }

    public BigDecimal getIgv() {
        ConfiguracionSistema config = configuracionRepository.findTasaIgv().orElse(null);
        if (config == null) {
            return new BigDecimal("18.00");
        }
        return config.getValorDecimal();
    }

    public String getIgvString() {
        ConfiguracionSistema config = configuracionRepository.findTasaIgv().orElse(null);
        if (config == null) {
            return "18.00";
        }
        return config.getValor();
    }

    public Integer getDiasVencimientoPago() {
        ConfiguracionSistema config = configuracionRepository.findDiasVencimientoPago().orElse(null);
        if (config == null) {
            return 30;
        }
        return config.getValorInteger();
    }

    public String getFormatoFecha() {
        ConfiguracionSistema config = configuracionRepository.findFormatoFecha().orElse(null);
        if (config == null) {
            return "%d/%m/%Y";
        }
        return config.getValor();
    }

    public String getMonedaDefault() {
        ConfiguracionSistema config = configuracionRepository.findMonedaDefault().orElse(null);
        if (config == null) {
            return "PEN";
        }
        return config.getValor();
    }

    public boolean validarValor(String tipoDato, String valor) {
        if (valor == null) {
            return false;
        }

        switch (tipoDato) {
            case "DECIMAL" -> {
                try {
                    BigDecimal dec = new BigDecimal(valor);
                    return dec.compareTo(BigDecimal.ZERO) >= 0;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            case "INT" -> {
                try {
                    Integer.valueOf(valor);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            case "BOOLEAN" -> {
                return "true".equalsIgnoreCase(valor) || "false".equalsIgnoreCase(valor) ||
                        "1".equals(valor) || "0".equals(valor) ||
                        "si".equalsIgnoreCase(valor) || "no".equalsIgnoreCase(valor);
            }
            case "DATE" -> {
                try {
                    LocalDateTime.parse(valor);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
            case "STRING" -> {
                return true;
            }
            default -> {
                return true;
            }
        }
    }

    public boolean esParametroCritico(String parametro) {
        return PARAM_TASA_IGV.equals(parametro) ||
               PARAM_DIAS_VENCIMIENTO.equals(parametro) ||
               PARAM_FORMATO_FECHA.equals(parametro) ||
               PARAM_MONEDA_DEFAULT.equals(parametro);
    }

    @Transactional
    public void inicializarConfiguracionesDefault(Integer idUsuarioAdmin) {
        if (!configuracionRepository.existsByParametro(PARAM_TASA_IGV)) {
            ConfiguracionSistema igv = ConfiguracionSistema.crearTasaIgv(new BigDecimal("18.00"));
            crear(igv, idUsuarioAdmin);
        }

        if (!configuracionRepository.existsByParametro(PARAM_DIAS_VENCIMIENTO)) {
            ConfiguracionSistema dias = ConfiguracionSistema.crearDiasVencimiento(30);
            crear(dias, idUsuarioAdmin);
        }

        if (!configuracionRepository.existsByParametro(PARAM_FORMATO_FECHA)) {
            ConfiguracionSistema formato = ConfiguracionSistema.crearFormatoFecha("%d/%m/%Y");
            crear(formato, idUsuarioAdmin);
        }

        if (!configuracionRepository.existsByParametro(PARAM_MONEDA_DEFAULT)) {
            ConfiguracionSistema moneda = ConfiguracionSistema.crearMonedaDefault("PEN");
            crear(moneda, idUsuarioAdmin);
        }
    }

    @Transactional
    public ConfiguracionSistema restaurarValorPorDefecto(Integer idConfig, Integer idUsuarioActualiza) {
        ConfiguracionSistema config = configuracionRepository.findById(idConfig)
                .orElseThrow(() -> new RuntimeException("Configuración no encontrada"));

        String valorDefault = getValorDefault(config.getParametro());
        if (valorDefault != null) {
            config.setValor(valorDefault);
            config.setFechaActualizacion(LocalDateTime.now());
            config.setIdUsuarioActualiza(idUsuarioActualiza);
            
            if (idUsuarioActualiza != null) {
                usuarioRepository.findById(idUsuarioActualiza).ifPresent(usuario -> {
                    config.setNombreUsuarioActualiza(usuario.getNombreCompleto());
                    config.setEmailUsuarioActualiza(usuario.getEmail());
                });
            }
            
            return configuracionRepository.save(config);
        }
        
        return config;
    }

    private String getValorDefault(String parametro) {
        return switch (parametro) {
            case PARAM_TASA_IGV -> "18.00";
            case PARAM_DIAS_VENCIMIENTO -> "30";
            case PARAM_FORMATO_FECHA -> "%d/%m/%Y";
            case PARAM_MONEDA_DEFAULT -> "PEN";
            default -> null;
        };
    }

    public long count() {
        return configuracionRepository.count();
    }

    public long countByTipoDato(String tipoDato) {
        return configuracionRepository.countByTipoDato(tipoDato);
    }

    public boolean existsByParametro(String parametro) {
        return configuracionRepository.existsByParametro(parametro);
    }

    public boolean isValid(ConfiguracionSistema config) {
        if (config == null) {
            return false;
        }
        if (config.getParametro() == null || config.getParametro().trim().isEmpty()) {
            return false;
        }
        if (config.getValor() == null || config.getValor().trim().isEmpty()) {
            return false;
        }
        if (config.getTipoDato() == null) {
            return false;
        }
        return validarValor(config.getTipoDato(), config.getValor());
    }

    public boolean puedeSerEliminada(Integer idConfig) {
        ConfiguracionSistema config = findById(idConfig);
        if (config == null) {
            return false;
        }
        return !esParametroCritico(config.getParametro());
    }

    public String getValor(String parametro, String valorDefault) {
        ConfiguracionSistema config = findByParametro(parametro);
        return config != null ? config.getValor() : valorDefault;
    }

    public BigDecimal getValorDecimal(String parametro, BigDecimal valorDefault) {
        ConfiguracionSistema config = findByParametro(parametro);
        if (config != null && config.esDecimal()) {
            return config.getValorDecimal();
        }
        return valorDefault;
    }

    public Integer getValorInteger(String parametro, Integer valorDefault) {
        ConfiguracionSistema config = findByParametro(parametro);
        if (config != null && config.esInteger()) {
            return config.getValorInteger();
        }
        return valorDefault;
    }

    public Boolean getValorBoolean(String parametro, Boolean valorDefault) {
        ConfiguracionSistema config = findByParametro(parametro);
        if (config != null && config.esBoolean()) {
            return config.getValorBoolean();
        }
        return valorDefault;
    }

    public String getDescripcion(String parametro) {
        ConfiguracionSistema config = findByParametro(parametro);
        return config != null ? config.getDescripcion() : null;
    }

    public String getTipoDato(String parametro) {
        ConfiguracionSistema config = findByParametro(parametro);
        return config != null ? config.getTipoDato() : null;
    }

    public String getValorFormateado(String parametro) {
        ConfiguracionSistema config = findByParametro(parametro);
        return config != null ? config.getValorFormateadoDisplay() : null;
    }

    public String getUsuarioActualiza(String parametro) {
        ConfiguracionSistema config = findByParametro(parametro);
        if (config == null) {
            return null;
        }
        return config.getUsuarioActualizaLegible();
    }

    public String getFechaActualizacionFormateada(String parametro) {
        ConfiguracionSistema config = findByParametro(parametro);
        return config != null ? config.getFechaActualizacionFormateada() : null;
    }

    @Transactional
    public void update(ConfiguracionSistema config) {
        if (config == null || config.getIdConfig() == null) {
            throw new RuntimeException("Configuración inválida para actualizar");
        }
        
        ConfiguracionSistema existente = configuracionRepository.findById(config.getIdConfig())
                .orElseThrow(() -> new RuntimeException("Configuración no encontrada con ID: " + config.getIdConfig()));

        existente.setValor(config.getValor());
        existente.setDescripcion(config.getDescripcion());
        existente.setFechaActualizacion(LocalDateTime.now());
        existente.setIdUsuarioActualiza(config.getIdUsuarioActualiza());

        if (config.getIdUsuarioActualiza() != null) {
            usuarioRepository.findById(config.getIdUsuarioActualiza()).ifPresent(usuario -> {
                existente.setNombreUsuarioActualiza(usuario.getNombreCompleto());
                existente.setEmailUsuarioActualiza(usuario.getEmail());
            });
        }

        configuracionRepository.save(existente);
    }
}