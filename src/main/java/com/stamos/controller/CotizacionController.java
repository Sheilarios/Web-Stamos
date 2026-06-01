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

/**
 * Controlador para la gestión de cotizaciones.
 * Accesible por usuarios con roles ADMINISTRADOR o COMERCIAL.
 * El rol DISEÑADOR solo puede visualizar (no modificar).
 * 
 * Reglas de negocio aplicadas:
 * - Una cotización debe contener al menos un ítem.
 * - Calcula automáticamente subtotal, IGV (configurable) y total.
 * - Permite precios base o negociados manualmente.
 * - Una vez aprobada por el cliente, no se puede modificar.
 * - Se guarda historial de versiones (si se modifica antes de aprobar).
 * - Se puede enviar por correo electrónico al cliente.
 * - A partir de una cotización aprobada se puede crear un proyecto.
 */
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

    @Autowired
    private EmailService emailService;

    // ==================== LISTAR COTIZACIONES ====================

    /**
     * Lista todas las cotizaciones con filtros opcionales.
     * 
     * @param estado Filtro por estado (emitida, aprobada, rechazada)
     * @param clienteId Filtro por cliente
     * @param model Modelo
     * @return Vista lista de cotizaciones
     */
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

    // ==================== CREAR COTIZACIÓN ====================

    /**
     * Muestra el formulario para crear una nueva cotización.
     * 
     * @param model Modelo con una cotización vacía y listas auxiliares
     * @return Vista formulario de cotización
     */
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

    /**
     * Guarda una nueva cotización (versión 1).
     * Valida que tenga al menos un ítem.
     * Calcula automáticamente subtotal, IGV y total.
     * 
     * @param cotizacion Datos de la cotización con sus items
     * @param bindingResult Validación
     * @param redirectAttrs Mensajes flash
     * @return Redirección a lista o formulario con errores
     */
    @PostMapping("/guardar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String saveCotizacion(@Valid @ModelAttribute("cotizacion") Cotizacion cotizacion,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttrs) {
        // Validar al menos un ítem
        if (cotizacion.getItems() == null || cotizacion.getItems().isEmpty()) {
            bindingResult.rejectValue("items", "error.cotizacion", 
                "La cotización debe contener al menos un ítem (producto o servicio)");
        }
        if (bindingResult.hasErrors()) {
            return "internal/cotizaciones/formulario";
        }
        
        // Calcular totales antes de guardar
        cotizacionService.calcularTotales(cotizacion);
        cotizacion.setVersion(1);
        cotizacion.setVersionBase(null); // Es la versión inicial
        Cotizacion saved = cotizacionService.save(cotizacion);
        emailService.enviarCotizacion(saved);
        
        redirectAttrs.addFlashAttribute("success", 
            "Cotización #" + saved.getId() + " creada exitosamente.");
        return "redirect:/internal/cotizaciones/lista";
    }

    // ==================== EDITAR COTIZACIÓN (solo si no aprobada) ====================

    /**
     * Muestra el formulario para editar una cotización.
     * Solo permitido si el estado no es "APROBADA".
     * Al editar, se crea una nueva versión.
     * 
     * @param id ID de la cotización
     * @param model Modelo
     * @return Vista formulario de edición
     */
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
        
        // Preparar una nueva versión basada en la actual
        Cotizacion nuevaVersion = cotizacionService.crearNuevaVersion(cotizacion);
        model.addAttribute("cotizacion", nuevaVersion);
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("productos", catalogoProductoService.findAll());
        model.addAttribute("action", "editar");
        model.addAttribute("versionAnterior", cotizacion.getVersion());
        return "internal/cotizaciones/formulario";
    }

    /**
     * Actualiza una cotización creando una nueva versión.
     * 
     * @param id ID de la cotización original (no se modifica)
     * @param cotizacion Datos de la nueva versión
     * @param bindingResult Validación
     * @param redirectAttrs Mensajes flash
     * @return Redirección
     */
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
        cotizacion.setCliente(original.getCliente()); // mantener el mismo cliente
        Cotizacion nuevaVersion = cotizacionService.save(cotizacion);
        
        redirectAttrs.addFlashAttribute("success", 
            "Se ha creado la versión " + nuevaVersion.getVersion() + " de la cotización.");
        return "redirect:/internal/cotizaciones/lista";
    }

    // ==================== VISUALIZAR DETALLE Y VERSIONES ====================

    /**
     * Muestra el detalle de una cotización específica.
     * 
     * @param id ID de la cotización
     * @param model Modelo
     * @return Vista detalle
     */
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

    // ==================== CAMBIAR ESTADO (APROBAR/RECHAZAR) ====================

    /**
     * Aprueba una cotización. Una vez aprobada, no se puede modificar.
     * Además, permite crear un proyecto a partir de ella.
     * 
     * @param id ID de la cotización
     * @param redirectAttrs Mensajes flash
     * @return Redirección al detalle
     */
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
        
        // Enviar correo de notificación al cliente (opcional)
        emailService.enviarCotizacionAprobada(cotizacion);
        
        redirectAttrs.addFlashAttribute("success", 
            "Cotización aprobada. Ahora puede crear un proyecto a partir de ella.");
        return "redirect:/internal/cotizaciones/detalle/" + id;
    }

    /**
     * Rechaza una cotización.
     * 
     * @param id ID de la cotización
     * @param motivo Motivo del rechazo (opcional)
     * @param redirectAttrs Mensajes flash
     * @return Redirección al detalle
     */
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
        
        if (motivo != null && !motivo.isEmpty()) {
            emailService.enviarCotizacionRechazada(cotizacion, motivo);
        }
        redirectAttrs.addFlashAttribute("info", "Cotización rechazada.");
        return "redirect:/internal/cotizaciones/detalle/" + id;
    }

    // ==================== CREAR PROYECTO DESDE COTIZACIÓN APROBADA ====================

    /**
     * Crea un proyecto a partir de una cotización aprobada.
     * Aplica regla RPY01: "Un proyecto se crea únicamente a partir de una cotización aprobada".
     * 
     * @param id ID de la cotización
     * @param redirectAttrs Mensajes flash
     * @return Redirección al nuevo proyecto
     */
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
        
        // Verificar si ya existe un proyecto asociado a esta cotización
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
        // La fecha de entrega se puede calcular como fechaEmision + validezDias + margen
        proyecto.setFechaEntregaEstimada(cotizacion.getFechaEmision().plusDays(cotizacion.getValidezDias() + 15));
        proyecto.setResponsable(null); // Por asignar
        proyectoService.save(proyecto);
        
        redirectAttrs.addFlashAttribute("success", 
            "Proyecto creado exitosamente a partir de la cotización.");
        return "redirect:/internal/proyectos/detalle/" + proyecto.getId();
    }

    // ==================== ENVIAR CORREO ====================

    /**
     * Envía la cotización por correo electrónico al cliente.
     * 
     * @param id ID de la cotización
     * @param redirectAttrs Mensajes flash
     * @return Redirección al detalle
     */
    @PostMapping("/enviar-correo/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String sendQuoteEmail(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        Cotizacion cotizacion = cotizacionService.findById(id);
        if (cotizacion == null) {
            redirectAttrs.addFlashAttribute("error", "Cotización no encontrada.");
            return "redirect:/internal/cotizaciones/lista";
        }
        try {
            emailService.enviarCotizacion(cotizacion);
            redirectAttrs.addFlashAttribute("success", 
                "Cotización enviada al correo " + cotizacion.getCliente().getEmail());
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", 
                "Error al enviar el correo: " + e.getMessage());
        }
        return "redirect:/internal/cotizaciones/detalle/" + id;
    }

    // ==================== ANULAR COTIZACIÓN (SOLO ADMIN) ====================

    /**
     * Anula una cotización (solo para administrador).
     * 
     * @param id ID de la cotización
     * @param motivo Motivo de anulación
     * @param redirectAttrs Mensajes flash
     * @return Redirección a lista
     */
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
            // Si está aprobada, también habría que considerar si ya tiene proyecto
            redirectAttrs.addFlashAttribute("error", 
                "No se puede anular una cotización aprobada. Si es necesario, anule el proyecto asociado.");
            return "redirect:/internal/cotizaciones/detalle/" + id;
        }
        cotizacion.setEstado("ANULADA");
        cotizacionService.save(cotizacion);
        // Registrar auditoría manualmente o mediante AOP
        redirectAttrs.addFlashAttribute("success", "Cotización anulada correctamente. Motivo: " + motivo);
        return "redirect:/internal/cotizaciones/lista";
    }
}