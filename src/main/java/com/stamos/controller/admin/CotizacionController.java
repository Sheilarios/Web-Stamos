package com.stamos.controller.admin;

import com.stamos.annotation.Auditable;
import com.stamos.model.*;
import com.stamos.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/cotizaciones")
@PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
public class CotizacionController {

    @Autowired
    private CotizacionService cotizacionService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ConfiguracionSistemaService configuracionService;

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public String listarCotizaciones(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(required = false) String estado,
                                     @RequestParam(required = false) Integer clienteId,
                                     Model model) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("fechaEmision").descending());
            
            Page<Cotizacion> cotizacionesPage;
            if (estado != null && !estado.isEmpty()) {
                cotizacionesPage = cotizacionService.findByEstado(estado, pageable);
            } else if (clienteId != null) {
                cotizacionesPage = cotizacionService.findByCliente(clienteId, pageable);
            } else {
                cotizacionesPage = cotizacionService.findAll(pageable);
            }
            
            long totalPendientes = cotizacionService.countByEstado("Pendiente");
            long totalAprobadas = cotizacionService.countByEstado("Aprobada");
            long totalAnuladas = cotizacionService.countByEstado("Anulada");
            long totalCotizaciones = cotizacionService.count();
            
            List<Cliente> clientes = clienteService.findAll();
            
            model.addAttribute("cotizaciones", cotizacionesPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", cotizacionesPage.getTotalPages());
            model.addAttribute("totalItems", cotizacionesPage.getTotalElements());
            model.addAttribute("estado", estado);
            model.addAttribute("clienteId", clienteId);
            model.addAttribute("clientes", clientes);
            model.addAttribute("totalPendientes", totalPendientes);
            model.addAttribute("totalAprobadas", totalAprobadas);
            model.addAttribute("totalAnuladas", totalAnuladas);
            model.addAttribute("totalCotizaciones", totalCotizaciones);
            
            return "admin/cotizaciones/lista";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar las cotizaciones: " + e.getMessage());
            return "admin/cotizaciones/lista";
        }
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(@RequestParam(required = false) Integer clienteId,
                                         Model model) {
        try {
            Cotizacion cotizacion = new Cotizacion();
            cotizacion.setFechaEmision(LocalDateTime.now());
            cotizacion.setFechaValidez(LocalDate.now().plusDays(15));
            cotizacion.setEstado("Pendiente");
            cotizacion.setVersionActual(1);
            
            if (clienteId != null) {
                Cliente cliente = clienteService.findById(clienteId);
                if (cliente != null) {
                    cotizacion.setIdCliente(clienteId);
                    model.addAttribute("clienteSeleccionado", cliente);
                }
            }
            
            BigDecimal tasaIgv = configuracionService.getIgv();
            
            model.addAttribute("cotizacion", cotizacion);
            model.addAttribute("clientes", clienteService.findAll());
            model.addAttribute("productos", productoService.findAll());
            model.addAttribute("tasaIgv", tasaIgv);
            model.addAttribute("titulo", "Nueva Cotización");
            model.addAttribute("accion", "crear");
            
            return "admin/cotizaciones/formulario";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "admin/cotizaciones/lista";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        try {
            Cotizacion cotizacion = cotizacionService.findById(id);
            if (cotizacion == null) {
                model.addAttribute("error", "Cotización no encontrada");
                return "redirect:/admin/cotizaciones";
            }
            
            if ("Aprobada".equals(cotizacion.getEstado())) {
                model.addAttribute("error", "No se puede modificar una cotización aprobada. " +
                                           "Si deseas cambios, crea una nueva versión.");
                return "redirect:/admin/cotizaciones/versiones/" + id;
            }
            
            List<CotizacionDetalle> detalles = cotizacionService.getDetallesByCotizacion(id);
            
            BigDecimal tasaIgv = configuracionService.getIgv();
            
            model.addAttribute("cotizacion", cotizacion);
            model.addAttribute("detalles", detalles);
            model.addAttribute("clientes", clienteService.findAll());
            model.addAttribute("productos", productoService.findAll());
            model.addAttribute("tasaIgv", tasaIgv);
            model.addAttribute("titulo", "Editar Cotización");
            model.addAttribute("accion", "editar");
            
            return "admin/cotizaciones/formulario";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "redirect:/admin/cotizaciones";
        }
    }

    @PostMapping("/crear")
    @Auditable(value = "Creación de cotización", accion = "CREATE")
    public String crearCotizacion(@Valid @ModelAttribute("cotizacion") Cotizacion cotizacion,
                                  BindingResult result,
                                  @RequestParam(value = "productosIds", required = false) List<Integer> productosIds,
                                  @RequestParam(value = "cantidades", required = false) List<Integer> cantidades,
                                  @RequestParam(value = "preciosUnitarios", required = false) List<BigDecimal> preciosUnitarios,
                                  @RequestParam(value = "descuentos", required = false) List<BigDecimal> descuentos,
                                  Authentication auth,
                                  RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("error", "Por favor, corrige los errores en el formulario.");
                return "redirect:/admin/cotizaciones/nuevo";
            }

            if (productosIds == null || productosIds.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", 
                    "La cotización debe incluir al menos un producto o servicio.");
                return "redirect:/admin/cotizaciones/nuevo";
            }

            if (productosIds.size() != cantidades.size() || 
                productosIds.size() != preciosUnitarios.size()) {
                redirectAttributes.addFlashAttribute("error", 
                    "Error en los datos de los productos. Verifica la información.");
                return "redirect:/admin/cotizaciones/nuevo";
            }

            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario vendedor no encontrado.");
                return "redirect:/admin/cotizaciones/nuevo";
            }

            cotizacion.setIdUsuarioVendedor(usuario.getIdUsuario());
            cotizacion.setFechaEmision(LocalDateTime.now());
            cotizacion.setEstado("Pendiente");
            cotizacion.setVersionActual(1);

            Cotizacion cotizacionCreada = cotizacionService.crearCotizacion(
                cotizacion, productosIds, cantidades, preciosUnitarios, descuentos, usuario.getIdUsuario()
            );

            redirectAttributes.addFlashAttribute("mensaje", 
                "Cotización creada exitosamente. ID: COT-" + String.format("%05d", cotizacionCreada.getIdCotizacion()));
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/admin/cotizaciones";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear la cotización: " + e.getMessage());
            return "redirect:/admin/cotizaciones/nuevo";
        }
    }

    @PostMapping("/actualizar/{id}")
    @Auditable(value = "Actualización de cotización (nueva versión)", accion = "UPDATE")
    public String actualizarCotizacion(@PathVariable Integer id,
                                       @ModelAttribute("cotizacion") Cotizacion cotizacion,
                                       @RequestParam(value = "productosIds", required = false) List<Integer> productosIds,
                                       @RequestParam(value = "cantidades", required = false) List<Integer> cantidades,
                                       @RequestParam(value = "preciosUnitarios", required = false) List<BigDecimal> preciosUnitarios,
                                       @RequestParam(value = "descuentos", required = false) List<BigDecimal> descuentos,
                                       @RequestParam(value = "motivoCambio", required = false) String motivoCambio,
                                       Authentication auth,
                                       RedirectAttributes redirectAttributes) {
        try {
            Cotizacion cotizacionExistente = cotizacionService.findById(id);
            if (cotizacionExistente == null) {
                redirectAttributes.addFlashAttribute("error", "Cotización no encontrada.");
                return "redirect:/admin/cotizaciones";
            }

            if ("Aprobada".equals(cotizacionExistente.getEstado())) {
                redirectAttributes.addFlashAttribute("error", 
                    "No se puede modificar una cotización aprobada. Crea una nueva versión desde el historial.");
                return "redirect:/admin/cotizaciones/versiones/" + id;
            }

            if (productosIds == null || productosIds.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", 
                    "La cotización debe incluir al menos un producto o servicio.");
                return "redirect:/admin/cotizaciones/editar/" + id;
            }

            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario vendedor no encontrado.");
                return "redirect:/admin/cotizaciones/editar/" + id;
            }

            cotizacionService.crearVersion(
                id, cotizacion, productosIds, cantidades, preciosUnitarios, 
                descuentos, motivoCambio, usuario.getIdUsuario()
            );

            redirectAttributes.addFlashAttribute("mensaje", 
                "Cotización actualizada exitosamente. Nueva versión: " + 
                (cotizacionExistente.getVersionActual() + 1));
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/admin/cotizaciones";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la cotización: " + e.getMessage());
            return "redirect:/admin/cotizaciones/editar/" + id;
        }
    }

    @PostMapping("/aprobar/{id}")
    @Auditable(value = "Aprobación de cotización y generación de proyecto", accion = "VALIDATE")
    public String aprobarCotizacion(@PathVariable Integer id,
                                    Authentication auth,
                                    RedirectAttributes redirectAttributes) {
        try {
            Cotizacion cotizacion = cotizacionService.findById(id);
            if (cotizacion == null) {
                redirectAttributes.addFlashAttribute("error", "Cotización no encontrada.");
                return "redirect:/admin/cotizaciones";
            }

            if (!"Pendiente".equals(cotizacion.getEstado())) {
                redirectAttributes.addFlashAttribute("error", 
                    "La cotización ya está " + cotizacion.getEstado().toLowerCase() + ".");
                return "redirect:/admin/cotizaciones";
            }

            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
                return "redirect:/admin/cotizaciones";
            }

            Proyecto proyecto = cotizacionService.aprobarCotizacion(id, usuario.getIdUsuario());

            redirectAttributes.addFlashAttribute("mensaje", 
                "Cotización aprobada exitosamente. Proyecto generado: " + proyecto.getNombreProyecto());
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/admin/cotizaciones";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al aprobar la cotización: " + e.getMessage());
            return "redirect:/admin/cotizaciones";
        }
    }

    @PostMapping("/anular/{id}")
    @Auditable(value = "Anulación de cotización", accion = "DELETE")
    public String anularCotizacion(@PathVariable Integer id,
                                   @RequestParam(value = "motivo", required = false) String motivo,
                                   Authentication auth,
                                   RedirectAttributes redirectAttributes) {
        try {
            Cotizacion cotizacion = cotizacionService.findById(id);
            if (cotizacion == null) {
                redirectAttributes.addFlashAttribute("error", "Cotización no encontrada.");
                return "redirect:/admin/cotizaciones";
            }

            if ("Aprobada".equals(cotizacion.getEstado())) {
                redirectAttributes.addFlashAttribute("error", 
                    "No se puede anular una cotización aprobada.");
                return "redirect:/admin/cotizaciones";
            }

            if ("Anulada".equals(cotizacion.getEstado())) {
                redirectAttributes.addFlashAttribute("error", "La cotización ya está anulada.");
                return "redirect:/admin/cotizaciones";
            }

            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
                return "redirect:/admin/cotizaciones";
            }

            cotizacionService.anularCotizacion(id, motivo, usuario.getIdUsuario());

            redirectAttributes.addFlashAttribute("mensaje", "Cotización anulada exitosamente.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/admin/cotizaciones";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al anular la cotización: " + e.getMessage());
            return "redirect:/admin/cotizaciones";
        }
    }

    @GetMapping("/versiones/{id}")
    public String verVersiones(@PathVariable Integer id, Model model) {
        try {
            Cotizacion cotizacion = cotizacionService.findById(id);
            if (cotizacion == null) {
                model.addAttribute("error", "Cotización no encontrada");
                return "redirect:/admin/cotizaciones";
            }

            List<CotizacionVersion> versiones = cotizacionService.getVersionesByCotizacion(id);
            List<CotizacionDetalle> detallesActuales = cotizacionService.getDetallesByCotizacion(id);
            model.addAttribute("cotizacion", cotizacion);
            model.addAttribute("versiones", versiones);
            model.addAttribute("detallesActuales", detallesActuales);
            
            return "admin/cotizaciones/versiones";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar las versiones: " + e.getMessage());
            return "redirect:/admin/cotizaciones";
        }
    }

    @GetMapping("/detalle-version/{id}")
    @ResponseBody
    public List<CotizacionDetalle> obtenerDetalleVersion(@PathVariable Integer idVersion) {
        try {
            return cotizacionService.getDetallesByVersion(idVersion);
        } catch (Exception e) {
            return List.of();
        }
    }

    @GetMapping("/version/{id}")
    @ResponseBody
    public CotizacionVersion obtenerVersion(@PathVariable Integer idVersion) {
        try {
            return cotizacionService.findVersionById(idVersion);
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/detalle/{id}")
    @ResponseBody
    public Cotizacion obtenerDetalleCotizacion(@PathVariable Integer id) {
        try {
            return cotizacionService.findById(id);
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/puede-editar/{id}")
    @ResponseBody
    public boolean puedeEditar(@PathVariable Integer id) {
        try {
            Cotizacion cotizacion = cotizacionService.findById(id);
            if (cotizacion == null) {
                return false;
            }
            return "Pendiente".equals(cotizacion.getEstado());
        } catch (Exception e) {
            return false;
        }
    }

    public String getEstadoLegible(String estado) {
        if (estado == null) return "Sin estado";
        return switch (estado) {
            case "Pendiente" -> "Pendiente";
            case "Aprobada" -> "Aprobada";
            case "Anulada" -> "Anulada";
            default -> estado;
        };
    }

    public String getEstadoBadgeClass(String estado) {
        if (estado == null) return "status-pendiente";
        return switch (estado) {
            case "Pendiente" -> "status-pendiente";
            case "Aprobada" -> "status-validado";
            case "Anulada" -> "status-rechazado";
            default -> "status-pendiente";
        };
    }

    public String formatearMoneda(BigDecimal monto) {
        if (monto == null) return "S/ 0.00";
        return "S/ " + monto.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public BigDecimal calcularSubtotal(List<CotizacionDetalle> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return detalles.stream()
                .map(CotizacionDetalle::getSubtotalLinea)
                .filter(s -> s != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularIgv(BigDecimal subtotal, BigDecimal tasaIgv) {
        if (subtotal == null || tasaIgv == null) {
            return BigDecimal.ZERO;
        }
        return subtotal.multiply(tasaIgv).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularTotal(BigDecimal subtotal, BigDecimal igv) {
        if (subtotal == null || igv == null) {
            return BigDecimal.ZERO;
        }
        return subtotal.add(igv);
    }
}