package com.stamos.controller.admin;

import com.stamos.annotation.Auditable;
import com.stamos.controller.BaseController;
import com.stamos.model.ConfiguracionSistema;
import com.stamos.model.Usuario;
import com.stamos.service.ClienteService;
import com.stamos.service.ConfiguracionSistemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/configuracion")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class ConfiguracionController extends BaseController {

    @Autowired
    private ConfiguracionSistemaService configuracionService;

    @Autowired
    private ClienteService clienteService;

    // ================================
    // LISTAR TODOS LOS PARÁMETROS
    // ================================
    @GetMapping
    public String listarConfiguraciones(Model model) {
        try {
            List<ConfiguracionSistema> configuraciones = configuracionService.findAll();
            model.addAttribute("configuraciones", configuraciones);
            addCommonAttributes(model);
            return "admin/configuracion/index";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar la configuración: " + e.getMessage());
            return "admin/configuracion/index";
        }
    }

    // ================================
    // MOSTRAR FORMULARIO PARA NUEVO PARÁMETRO
    // ================================
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        try {
            model.addAttribute("configuracion", new ConfiguracionSistema());
            model.addAttribute("titulo", "Nuevo Parámetro");
            model.addAttribute("accion", "crear");
            addCommonAttributes(model);
            return "admin/configuracion/nuevo";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "redirect:/admin/configuracion";
        }
    }

    // ================================
    // CREAR NUEVO PARÁMETRO
    // ================================
    @PostMapping("/crear")
    @Auditable
    public String crearConfiguracion(@Valid @ModelAttribute("configuracion") ConfiguracionSistema config,
                                     BindingResult result,
                                     Authentication auth,
                                     RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("error", "Por favor, corrige los errores en el formulario.");
                return "redirect:/admin/configuracion/nuevo";
            }

            Usuario usuarioAdmin = clienteService.findUsuarioByEmail(auth.getName());
            if (usuarioAdmin == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario administrador no encontrado.");
                return "redirect:/admin/configuracion/nuevo";
            }

            if (configuracionService.existsByParametro(config.getParametro())) {
                redirectAttributes.addFlashAttribute("error", "Ya existe un parámetro con el nombre: " + config.getParametro());
                return "redirect:/admin/configuracion/nuevo";
            }

            config.setFechaActualizacion(LocalDateTime.now());
            config.setIdUsuarioActualiza(usuarioAdmin.getIdUsuario());

            configuracionService.crear(config, usuarioAdmin.getIdUsuario());

            redirectAttributes.addFlashAttribute("mensaje", "Parámetro creado exitosamente.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            return "redirect:/admin/configuracion";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el parámetro: " + e.getMessage());
            return "redirect:/admin/configuracion/nuevo";
        }
    }

    // ================================
    // EDITAR PARÁMETRO
    // ================================
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        try {
            ConfiguracionSistema config = configuracionService.findById(id);
            if (config == null) {
                model.addAttribute("error", "Parámetro de configuración no encontrado");
                return "redirect:/admin/configuracion";
            }
            model.addAttribute("configuracion", config);
            model.addAttribute("titulo", "Editar Configuración");
            model.addAttribute("accion", "editar");
            addCommonAttributes(model);
            return "admin/configuracion/editar";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar la configuración: " + e.getMessage());
            return "redirect:/admin/configuracion";
        }
    }

    @PostMapping("/actualizar/{id}")
    @Auditable
    public String actualizarConfiguracion(@PathVariable Integer id,
                                          @Valid @ModelAttribute("configuracion") ConfiguracionSistema config,
                                          BindingResult result,
                                          Authentication auth,
                                          RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("error", "Por favor, corrige los errores en el formulario.");
                return "redirect:/admin/configuracion/editar/" + id;
            }

            ConfiguracionSistema configExistente = configuracionService.findById(id);
            if (configExistente == null) {
                redirectAttributes.addFlashAttribute("error", "Parámetro de configuración no encontrado.");
                return "redirect:/admin/configuracion";
            }

            Usuario usuarioAdmin = clienteService.findUsuarioByEmail(auth.getName());
            if (usuarioAdmin == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario administrador no encontrado.");
                return "redirect:/admin/configuracion";
            }

            config.setIdConfig(id);
            config.setIdUsuarioActualiza(usuarioAdmin.getIdUsuario());
            config.setFechaActualizacion(LocalDateTime.now());

            configuracionService.update(config);

            if ("tasa_igv".equals(config.getParametro())) {
                redirectAttributes.addFlashAttribute("mensaje",
                        "La tasa IGV ha sido actualizada a " + config.getValor() + "%. " +
                        "Las nuevas cotizaciones usarán este valor. Las cotizaciones existentes mantienen su tasa original.");
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "Configuración actualizada exitosamente.");
            }

            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            return "redirect:/admin/configuracion";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la configuración: " + e.getMessage());
            return "redirect:/admin/configuracion/editar/" + id;
        }
    }

    // ================================
    // RESTAURAR VALOR POR DEFECTO
    // ================================
    @PostMapping("/restaurar/{id}")
    @Auditable
    public String restaurarValorPorDefecto(@PathVariable Integer id,
                                           Authentication auth,
                                           RedirectAttributes redirectAttributes) {
        try {
            ConfiguracionSistema config = configuracionService.findById(id);
            if (config == null) {
                redirectAttributes.addFlashAttribute("error", "Parámetro de configuración no encontrado.");
                return "redirect:/admin/configuracion";
            }

            Usuario usuarioAdmin = clienteService.findUsuarioByEmail(auth.getName());
            if (usuarioAdmin == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario administrador no encontrado.");
                return "redirect:/admin/configuracion";
            }

            configuracionService.restaurarValorPorDefecto(id, usuarioAdmin.getIdUsuario());

            redirectAttributes.addFlashAttribute("mensaje", "Configuración restaurada a su valor por defecto.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            return "redirect:/admin/configuracion";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al restaurar la configuración: " + e.getMessage());
            return "redirect:/admin/configuracion";
        }
    }

    // ================================
    // ENDPOINTS AJAX
    // ================================
    @GetMapping("/valor")
    @ResponseBody
    public String obtenerValor(@RequestParam("parametro") String parametro) {
        try {
            ConfiguracionSistema config = configuracionService.findByParametro(parametro);
            return config != null ? config.getValor() : "";
        } catch (Exception e) {
            return "";
        }
    }

    @GetMapping("/igv")
    @ResponseBody
    public String obtenerIgv() {
        try {
            BigDecimal igv = configuracionService.getIgv();
            return igv != null ? igv.toString() : "18.00";
        } catch (Exception e) {
            return "18.00";
        }
    }

    @GetMapping("/dias-vencimiento")
    @ResponseBody
    public String obtenerDiasVencimiento() {
        try {
            Integer dias = configuracionService.getDiasVencimientoPago();
            return dias != null ? dias.toString() : "30";
        } catch (Exception e) {
            return "30";
        }
    }

    @GetMapping("/moneda")
    @ResponseBody
    public String obtenerMoneda() {
        try {
            return configuracionService.getMonedaDefault();
        } catch (Exception e) {
            return "PEN";
        }
    }

    @GetMapping("/formato-fecha")
    @ResponseBody
    public String obtenerFormatoFecha() {
        try {
            return configuracionService.getFormatoFecha();
        } catch (Exception e) {
            return "%d/%m/%Y";
        }
    }

    @GetMapping("/existe")
    @ResponseBody
    public boolean existeParametro(@RequestParam("parametro") String parametro) {
        try {
            return configuracionService.existsByParametro(parametro);
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/descripcion")
    @ResponseBody
    public String obtenerDescripcion(@RequestParam("parametro") String parametro) {
        try {
            ConfiguracionSistema config = configuracionService.findByParametro(parametro);
            return config != null ? config.getDescripcion() : "";
        } catch (Exception e) {
            return "";
        }
    }

    @GetMapping("/tipo-dato")
    @ResponseBody
    public String obtenerTipoDato(@RequestParam("parametro") String parametro) {
        try {
            ConfiguracionSistema config = configuracionService.findByParametro(parametro);
            return config != null ? config.getTipoDato() : "";
        } catch (Exception e) {
            return "";
        }
    }

    // ================================
    // MÉTODOS AUXILIARES
    // ================================
    public String formatearValor(String valor, String tipoDato) {
        if (valor == null || tipoDato == null) return valor;
        return switch (tipoDato) {
            case "DECIMAL" -> {
                try {
                    double d = Double.parseDouble(valor);
                    yield String.format("%.2f", d);
                } catch (NumberFormatException e) {
                    yield valor;
                }
            }
            case "BOOLEAN" -> "true".equalsIgnoreCase(valor) ? "Sí" : "No";
            case "DATE" -> valor;
            default -> valor;
        };
    }

    public String getTipoDatoClass(String tipoDato) {
        if (tipoDato == null) return "badge-secondary";
        return switch (tipoDato) {
            case "DECIMAL" -> "badge-info";
            case "STRING" -> "badge-primary";
            case "INT" -> "badge-warning";
            case "BOOLEAN" -> "badge-success";
            case "DATE" -> "badge-danger";
            default -> "badge-secondary";
        };
    }
}