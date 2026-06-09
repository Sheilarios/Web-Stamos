package com.stamos.controller;

import com.stamos.dto.CotizacionDTO;
import com.stamos.dto.DetalleCotizacionDTO;
import com.stamos.entity.*;
import com.stamos.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/internal/cotizaciones")
public class CotizacionController {

    @Autowired
    private CotizacionService cotizacionService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private CatalogoProductoService catalogoProductoService;

    @Autowired
    private ProyectoService proyectoService;

    @GetMapping("/lista")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL', 'DISEÑADOR')")
    public String listCotizaciones(@RequestParam(required = false) String estado,
                                   @RequestParam(required = false) Long clienteId,
                                   Model model) {
        List<Cotizacion> cotizaciones;
        if (estado != null && !estado.isEmpty()) {
            cotizaciones = cotizacionService.findByEstado(estado);
        } else if (clienteId != null) {
            cotizaciones = cotizacionService.findByClienteId(clienteId);
        } else {
            cotizaciones = cotizacionService.findAll();
        }
        model.addAttribute("cotizaciones", cotizaciones);
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("clienteId", clienteId);
        model.addAttribute("clientes", clienteService.findAll());
        return "internal/cotizaciones/lista";
    }

    @GetMapping("/nueva")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String showCreateForm(Model model) {
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setFechaEmision(LocalDate.now());
        cotizacion.setValidezDias(15); // valor por defecto
        cotizacion.setEstado("EMITIDA");
        cotizacion.setItems(new ArrayList<>());
        
        model.addAttribute("cotizacion", cotizacion);
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("productos", catalogoProductoService.findAll());
        model.addAttribute("action", "crear");
        return "internal/cotizaciones/formulario";
    }

    @PostMapping("/guardar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String saveCotizacion(@Valid @ModelAttribute("cotizacion") Cotizacion cotizacion,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttrs) {
        if (cotizacion.getItems() == null || cotizacion.getItems().isEmpty()) {
            bindingResult.rejectValue("items", "error.cotizacion", 
                "La cotización debe contener al menos un ítem (producto o servicio)");
        }
        if (bindingResult.hasErrors()) {
            return "internal/cotizaciones/formulario";
        }
        
        cotizacionService.calcularTotales(cotizacion);
        cotizacion.setVersion(1);
        cotizacion.setVersionBase(null);
        Cotizacion saved = cotizacionService.save(cotizacion);        
        redirectAttrs.addFlashAttribute("success", 
            "Cotización #" + saved.getId() + " creada exitosamente.");
        return "redirect:/internal/cotizaciones/lista";
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        Cotizacion cotizacion = cotizacionService.findById(id);
        if (cotizacion == null) {
            redirectAttrs.addFlashAttribute("error", "Cotización no encontrada.");
            return "redirect:/internal/cotizaciones/lista";
        }
        if ("APROBADA".equals(cotizacion.getEstado())) {
            redirectAttrs.addFlashAttribute("error", 
                "No se puede editar una cotización aprobada. Si necesita cambios, debe anularla y crear una nueva.");
            return "redirect:/internal/cotizaciones/lista";
        }
        
        Cotizacion nuevaVersion = cotizacionService.crearNuevaVersion(cotizacion);
        model.addAttribute("cotizacion", nuevaVersion);
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("productos", catalogoProductoService.findAll());
        model.addAttribute("action", "editar");
        model.addAttribute("versionAnterior", cotizacion.getVersion());
        return "internal/cotizaciones/formulario";
    }

    @PostMapping("/actualizar/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String updateCotizacion(@PathVariable Long id,
                                   @Valid @ModelAttribute("cotizacion") Cotizacion cotizacion,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttrs) {
        Cotizacion original = cotizacionService.findById(id);
        if (original == null || "APROBADA".equals(original.getEstado())) {
            redirectAttrs.addFlashAttribute("error", "No se puede modificar una cotización aprobada.");
            return "redirect:/internal/cotizaciones/lista";
        }
        if (cotizacion.getItems() == null || cotizacion.getItems().isEmpty()) {
            bindingResult.rejectValue("items", "error.cotizacion", 
                "Debe tener al menos un ítem");
            return "internal/cotizaciones/formulario";
        }
        
        cotizacionService.calcularTotales(cotizacion);
        cotizacion.setVersion(original.getVersion() + 1);
        cotizacion.setVersionBase(original);
        cotizacion.setCliente(original.getCliente()); 
        Cotizacion nuevaVersion = cotizacionService.save(cotizacion);
        
        redirectAttrs.addFlashAttribute("success", 
            "Se ha creado la versión " + nuevaVersion.getVersion() + " de la cotización.");
        return "redirect:/internal/cotizaciones/lista";
    }

    @GetMapping("/detalle/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL', 'DISEÑADOR')")
    public String viewDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        Cotizacion cotizacion = cotizacionService.findById(id);
        if (cotizacion == null) {
            redirectAttrs.addFlashAttribute("error", "Cotización no encontrada.");
            return "redirect:/internal/cotizaciones/lista";
        }
        List<Cotizacion> versiones = cotizacionService.findVersionesByBaseId(id);
        model.addAttribute("cotizacion", cotizacion);
        model.addAttribute("versiones", versiones);
        model.addAttribute("puedeAprobar", 
            !"APROBADA".equals(cotizacion.getEstado()) && 
            !"RECHAZADA".equals(cotizacion.getEstado()));
        return "internal/cotizaciones/detalle";
    }

    @PostMapping("/aprobar/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String approveCotizacion(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        Cotizacion cotizacion = cotizacionService.findById(id);
        if (cotizacion == null) {
            redirectAttrs.addFlashAttribute("error", "Cotización no encontrada.");
            return "redirect:/internal/cotizaciones/lista";
        }
        if ("APROBADA".equals(cotizacion.getEstado())) {
            redirectAttrs.addFlashAttribute("warning", "La cotización ya estaba aprobada.");
            return "redirect:/internal/cotizaciones/detalle/" + id;
        }
        cotizacion.setEstado("APROBADA");
        cotizacionService.save(cotizacion);
        
        
        redirectAttrs.addFlashAttribute("success", 
            "Cotización aprobada. Ahora puede crear un proyecto a partir de ella.");
        return "redirect:/internal/cotizaciones/detalle/" + id;
    }

    @PostMapping("/rechazar/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String rejectCotizacion(@PathVariable Long id,
                                   @RequestParam(required = false) String motivo,
                                   RedirectAttributes redirectAttrs) {
        Cotizacion cotizacion = cotizacionService.findById(id);
        if (cotizacion == null) {
            redirectAttrs.addFlashAttribute("error", "Cotización no encontrada.");
            return "redirect:/internal/cotizaciones/lista";
        }
        cotizacion.setEstado("RECHAZADA");
        cotizacionService.save(cotizacion);
        redirectAttrs.addFlashAttribute("info", "Cotización rechazada.");
        return "redirect:/internal/cotizaciones/detalle/" + id;
    }

    @PostMapping("/crear-proyecto/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String createProjectFromQuote(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        Cotizacion cotizacion = cotizacionService.findById(id);
        if (cotizacion == null) {
            redirectAttrs.addFlashAttribute("error", "Cotización no encontrada.");
            return "redirect:/internal/cotizaciones/lista";
        }
        if (!"APROBADA".equals(cotizacion.getEstado())) {
            redirectAttrs.addFlashAttribute("error", 
                "Solo se puede crear un proyecto a partir de una cotización aprobada.");
            return "redirect:/internal/cotizaciones/detalle/" + id;
        }
        
        if (proyectoService.existsByCotizacionId(id)) {
            redirectAttrs.addFlashAttribute("warning", 
                "Ya existe un proyecto para esta cotización.");
            return "redirect:/internal/cotizaciones/detalle/" + id;
        }
        
        Proyecto proyecto = new Proyecto();
        proyecto.setCotizacion(cotizacion);
        proyecto.setCliente(cotizacion.getCliente());
        proyecto.setEstado("COTIZADO");
        proyecto.setFechaCreacion(LocalDate.now());
        proyecto.setFechaEntregaEstimada(cotizacion.getFechaEmision().plusDays(cotizacion.getValidezDias() + 15));
        proyecto.setResponsable(null); 
        proyectoService.save(proyecto);
        
        redirectAttrs.addFlashAttribute("success", 
            "Proyecto creado exitosamente a partir de la cotización.");
        return "redirect:/internal/proyectos/detalle/" + proyecto.getId();
    }

    @PostMapping("/enviar-correo/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String sendQuoteEmail(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        Cotizacion cotizacion = cotizacionService.findById(id);
        if (cotizacion == null) {
            redirectAttrs.addFlashAttribute("error", "Cotización no encontrada.");
            return "redirect:/internal/cotizaciones/lista";
        }
        try {
            redirectAttrs.addFlashAttribute("success", 
                "Cotización enviada al correo " + cotizacion.getCliente().getEmail());
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", 
                "Error al enviar el correo: " + e.getMessage());
        }
        return "redirect:/internal/cotizaciones/detalle/" + id;
    }

    @PostMapping("/anular/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String cancelCotizacion(@PathVariable Long id,
                                   @RequestParam String motivo,
                                   RedirectAttributes redirectAttrs) {
        Cotizacion cotizacion = cotizacionService.findById(id);
        if (cotizacion == null) {
            redirectAttrs.addFlashAttribute("error", "Cotización no encontrada.");
            return "redirect:/internal/cotizaciones/lista";
        }
        if ("APROBADA".equals(cotizacion.getEstado())) {
            redirectAttrs.addFlashAttribute("error", 
                "No se puede anular una cotización aprobada. Si es necesario, anule el proyecto asociado.");
            return "redirect:/internal/cotizaciones/detalle/" + id;
        }
        cotizacion.setEstado("ANULADA");
        cotizacionService.save(cotizacion);
        redirectAttrs.addFlashAttribute("success", "Cotización anulada correctamente. Motivo: " + motivo);
        return "redirect:/internal/cotizaciones/lista";
    }
}