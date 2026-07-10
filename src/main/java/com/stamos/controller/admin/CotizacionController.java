package com.stamos.controller.admin;

import com.stamos.annotation.Auditable;
import com.stamos.controller.BaseController;
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
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/cotizaciones")
@PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
public class CotizacionController extends BaseController {

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
            
            List<Cotizacion> cotizaciones = cotizacionesPage.getContent();
            for (Cotizacion c : cotizaciones) {
                List<CotizacionVersion> versiones = cotizacionService.getVersionesByCotizacion(c.getIdCotizacion());
                if (!versiones.isEmpty()) {
                    CotizacionVersion version = versiones.get(0);
                    c.setSubtotal(version.getSubtotal());
                    c.setIgv(version.getIgv());
                    c.setTotal(version.getTotal());
                }
            }
            
            long totalPendientes = cotizacionService.countByEstado("Pendiente");
            long totalAprobadas = cotizacionService.countByEstado("Aprobada");
            long totalAnuladas = cotizacionService.countByEstado("Anulada");
            long totalCotizaciones = cotizacionService.count();
            
            List<Cliente> clientes = clienteService.findAll();
            
            model.addAttribute("cotizaciones", cotizaciones);
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
            addCommonAttributes(model);
            
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
            List<Producto> productosActivos = productoService.findActive();
            
            model.addAttribute("cotizacion", cotizacion);
            model.addAttribute("clientes", clienteService.findAll());
            model.addAttribute("productos", productosActivos);
            model.addAttribute("tasaIgv", tasaIgv);
            model.addAttribute("titulo", "Nueva Cotización");
            model.addAttribute("accion", "crear");
            model.addAttribute("detalles", new ArrayList<>());
            addCommonAttributes(model);
            
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
                model.addAttribute("error", "No se puede modificar una cotización aprobada.");
                return "redirect:/admin/cotizaciones";
            }
            
            List<CotizacionDetalle> detalles = cotizacionService.getDetallesByCotizacion(id);
            if (detalles == null) {
                detalles = new ArrayList<>();
            }
            
            BigDecimal tasaIgv = configuracionService.getIgv();
            List<Producto> productosActivos = productoService.findActive();
            
            model.addAttribute("cotizacion", cotizacion);
            model.addAttribute("detalles", detalles);
            model.addAttribute("clientes", clienteService.findAll());
            model.addAttribute("productos", productosActivos);
            model.addAttribute("tasaIgv", tasaIgv);
            model.addAttribute("titulo", "Editar Cotización");
            model.addAttribute("accion", "editar");
            addCommonAttributes(model);
            
            return "admin/cotizaciones/formulario";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "redirect:/admin/cotizaciones";
        }
    }

    @PostMapping("/crear")
    @Auditable
    public String crearCotizacion(@Valid @ModelAttribute("cotizacion") Cotizacion cotizacion,
                                  BindingResult result,
                                  @RequestParam(value = "productosIds", required = false) List<Integer> productosIds,
                                  @RequestParam(value = "cantidades", required = false) List<Integer> cantidades,
                                  @RequestParam(value = "preciosUnitarios", required = false) List<BigDecimal> preciosUnitarios,
                                  @RequestParam(value = "descuentos", required = false) List<BigDecimal> descuentos,
                                  Authentication auth,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                cargarDatosFormulario(model, cotizacion, "crear", "Nueva Cotización");
                return "admin/cotizaciones/formulario";
            }

            if (productosIds == null || productosIds.isEmpty()) {
                model.addAttribute("error", "La cotización debe incluir al menos un producto o servicio.");
                cargarDatosFormulario(model, cotizacion, "crear", "Nueva Cotización");
                return "admin/cotizaciones/formulario";
            }

            if (productosIds.size() != cantidades.size() || 
                productosIds.size() != preciosUnitarios.size()) {
                model.addAttribute("error", "Error en los datos de los productos. Verifica la información.");
                cargarDatosFormulario(model, cotizacion, "crear", "Nueva Cotización");
                return "admin/cotizaciones/formulario";
            }

            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                model.addAttribute("error", "Usuario vendedor no encontrado.");
                cargarDatosFormulario(model, cotizacion, "crear", "Nueva Cotización");
                return "admin/cotizaciones/formulario";
            }

            cotizacion.setIdUsuarioVendedor(usuario.getIdUsuario());
            cotizacion.setFechaEmision(LocalDateTime.now());
            cotizacion.setEstado("Pendiente");
            cotizacion.setVersionActual(1);

            Cotizacion cotizacionCreada = cotizacionService.crearCotizacion(
                cotizacion, productosIds, cantidades, preciosUnitarios, descuentos, usuario.getIdUsuario()
            );

            redirectAttributes.addFlashAttribute("mensaje", 
                "Cotización creada exitosamente. ID: " + cotizacionCreada.getCodigoCotizacion());
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/admin/cotizaciones";

        } catch (Exception e) {
            model.addAttribute("error", "Error al crear la cotización: " + e.getMessage());
            cargarDatosFormulario(model, cotizacion, "crear", "Nueva Cotización");
            return "admin/cotizaciones/formulario";
        }
    }

    @PostMapping("/actualizar/{id}")
    @Auditable
    public String actualizarCotizacion(@PathVariable Integer id,
                                       @Valid @ModelAttribute("cotizacion") Cotizacion cotizacion,
                                       BindingResult result,
                                       @RequestParam(value = "productosIds", required = false) List<Integer> productosIds,
                                       @RequestParam(value = "cantidades", required = false) List<Integer> cantidades,
                                       @RequestParam(value = "preciosUnitarios", required = false) List<BigDecimal> preciosUnitarios,
                                       @RequestParam(value = "descuentos", required = false) List<BigDecimal> descuentos,
                                       @RequestParam(value = "motivoCambio", required = false) String motivoCambio,
                                       Authentication auth,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                cargarDatosFormulario(model, cotizacion, "editar", "Editar Cotización");
                return "admin/cotizaciones/formulario";
            }

            Cotizacion cotizacionExistente = cotizacionService.findById(id);
            if (cotizacionExistente == null) {
                model.addAttribute("error", "Cotización no encontrada.");
                cargarDatosFormulario(model, cotizacion, "editar", "Editar Cotización");
                return "admin/cotizaciones/formulario";
            }

            if ("Aprobada".equals(cotizacionExistente.getEstado())) {
                model.addAttribute("error", "No se puede modificar una cotización aprobada.");
                cargarDatosFormulario(model, cotizacion, "editar", "Editar Cotización");
                return "admin/cotizaciones/formulario";
            }

            if (productosIds == null || productosIds.isEmpty()) {
                model.addAttribute("error", "La cotización debe incluir al menos un producto o servicio.");
                cargarDatosFormulario(model, cotizacion, "editar", "Editar Cotización");
                return "admin/cotizaciones/formulario";
            }

            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                model.addAttribute("error", "Usuario vendedor no encontrado.");
                cargarDatosFormulario(model, cotizacion, "editar", "Editar Cotización");
                return "admin/cotizaciones/formulario";
            }

            // USAR EL MÉTODO EXISTENTE crearVersion
            cotizacionService.crearVersion(
                id, cotizacion, productosIds, cantidades, preciosUnitarios, 
                descuentos, motivoCambio, usuario.getIdUsuario()
            );

            redirectAttributes.addFlashAttribute("mensaje", 
                "Cotización actualizada exitosamente.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/admin/cotizaciones";

        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar la cotización: " + e.getMessage());
            cargarDatosFormulario(model, cotizacion, "editar", "Editar Cotización");
            return "admin/cotizaciones/formulario";
        }
    }

    private void cargarDatosFormulario(Model model, Cotizacion cotizacion, String accion, String titulo) {
        model.addAttribute("cotizacion", cotizacion);
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("productos", productoService.findActive());
        model.addAttribute("tasaIgv", configuracionService.getIgv());
        model.addAttribute("detalles", cotizacion.getDetalles() != null ? cotizacion.getDetalles() : new ArrayList<>());
        model.addAttribute("accion", accion);
        model.addAttribute("titulo", titulo);
        addCommonAttributes(model);
    }

    @PostMapping("/aprobar/{id}")
    @Auditable
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
    @Auditable
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
                redirectAttributes.addFlashAttribute("error", "No se puede anular una cotización aprobada.");
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
        switch (estado) {
            case "Pendiente": return "Pendiente";
            case "Aprobada": return "Aprobada";
            case "Anulada": return "Anulada";
            default: return estado;
        }
    }

    public String getEstadoBadgeClass(String estado) {
        if (estado == null) return "status-pendiente";
        switch (estado) {
            case "Pendiente": return "status-pendiente";
            case "Aprobada": return "status-validado";
            case "Anulada": return "status-rechazado";
            default: return "status-pendiente";
        }
    }

    @Override
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