package com.stamos.controller;
import com.stamos.entity.Proyecto;
import com.stamos.entity.Cotizacion;
import com.stamos.entity.HistorialEstadoProyecto;
import com.stamos.entity.Usuario;
import com.stamos.entity.ArchivoProyecto;
import com.stamos.service.ProyectoService;
import com.stamos.service.CotizacionService;
import com.stamos.service.UsuarioService;
import com.stamos.service.AuditoriaService;
import com.stamos.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/internal/proyectos")
@PreAuthorize("isAuthenticated()") 
public class ProyectoController {

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private CotizacionService cotizacionService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuditoriaService auditoriaService;

    @Autowired
    private InventarioService inventarioService;

    @GetMapping("/lista")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL', 'DISEÑADOR', 'PRODUCCION', 'SOPORTE_TECNICO')")
    public String listProyectos(@RequestParam(required = false) String estado,
                                @RequestParam(required = false) Long clienteId,
                                Model model) {
        List<Proyecto> proyectos;
        if (estado != null && !estado.isEmpty()) {
            proyectos = proyectoService.findByEstado(estado);
        } else if (clienteId != null) {
            proyectos = proyectoService.findByClienteId(clienteId);
        } else {
            proyectos = proyectoService.findAll();
        }
        model.addAttribute("proyectos", proyectos);
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("clienteId", clienteId);
        return "internal/proyectos/lista";
    }


    @GetMapping("/nuevo")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String showCreateForm(Model model) {
        List<Cotizacion> cotizacionesAprobadas = cotizacionService.findByEstado("APROBADA");
        model.addAttribute("cotizaciones", cotizacionesAprobadas);
        model.addAttribute("proyecto", new Proyecto());
        return "internal/proyectos/nuevo";
    }

    @PostMapping("/guardar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String saveProyecto(@RequestParam Long cotizacionId,
                               RedirectAttributes redirectAttrs) {
        Cotizacion cotizacion = cotizacionService.findById(cotizacionId);
        if (cotizacion == null) {
            redirectAttrs.addFlashAttribute("error", "Cotización no encontrada.");
            return "redirect:/internal/proyectos/nuevo";
        }
        if (!"APROBADA".equals(cotizacion.getEstado())) {
            redirectAttrs.addFlashAttribute("error", "Solo se puede crear un proyecto a partir de una cotización aprobada.");
            return "redirect:/internal/proyectos/nuevo";
        }
        if (proyectoService.existsByCotizacionId(cotizacionId)) {
            redirectAttrs.addFlashAttribute("error", "Ya existe un proyecto para esta cotización.");
            return "redirect:/internal/proyectos/nuevo";
        }
        Proyecto proyecto = new Proyecto();
        proyecto.setCotizacion(cotizacion);
        proyecto.setCliente(cotizacion.getCliente());
        proyecto.setEstado("COTIZADO");
        proyecto.setFechaCreacion(LocalDate.now());
        proyecto.setFechaEntregaEstimada(cotizacion.getFechaEmision().plusDays(cotizacion.getValidezDias() + 15));
        proyectoService.save(proyecto);

        auditoriaService.log("CREAR", "PROYECTO", proyecto.getId(), null, proyecto.getEstado());

        redirectAttrs.addFlashAttribute("success", "Proyecto creado exitosamente.");
        return "redirect:/internal/proyectos/detalle/" + proyecto.getId();
    }

    @GetMapping("/detalle/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL', 'DISEÑADOR', 'PRODUCCION', 'SOPORTE_TECNICO')")
    public String viewDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        Proyecto proyecto = proyectoService.findById(id);
        if (proyecto == null) {
            redirectAttrs.addFlashAttribute("error", "Proyecto no encontrado.");
            return "redirect:/internal/proyectos/lista";
        }
        List<HistorialEstadoProyecto> historial = proyectoService.getHistorialEstados(id);
        model.addAttribute("proyecto", proyecto);
        model.addAttribute("historial", historial);
        model.addAttribute("usuarios", usuarioService.findAll());
        return "internal/proyectos/detalle";
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL', 'DISEÑADOR')")
    public String dashboard(Model model, Authentication auth) {
        Usuario currentUser = usuarioService.findByEmail(auth.getName());
        List<Proyecto> proyectos;
        if (currentUser.hasRole("ADMINISTRADOR") || currentUser.hasRole("COMERCIAL")) {
            proyectos = proyectoService.findByEstadosNotIn(List.of("ENTREGADO", "ANULADO"));
        } else if (currentUser.hasRole("DISEÑADOR")) {
            proyectos = proyectoService.findByResponsableIdAndEstadosNotIn(currentUser.getId(), List.of("ENTREGADO", "ANULADO"));
        } else {
            proyectos = proyectoService.findByEstadosNotIn(List.of("ENTREGADO", "ANULADO"));
        }
        model.addAttribute("proyectos", proyectos);
        return "internal/proyectos/dashboard";
    }


    @GetMapping("/cambiar-estado/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DISEÑADOR')")
    public String showChangeStateForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        Proyecto proyecto = proyectoService.findById(id);
        if (proyecto == null) {
            redirectAttrs.addFlashAttribute("error", "Proyecto no encontrado.");
            return "redirect:/internal/proyectos/lista";
        }
        List<String> estadosPermitidos = proyectoService.getNextAllowedStates(proyecto.getEstado());
        model.addAttribute("proyecto", proyecto);
        model.addAttribute("estadosPermitidos", estadosPermitidos);
        return "internal/proyectos/cambiar-estado";
    }

    @PostMapping("/cambiar-estado/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DISEÑADOR')")
    public String changeState(@PathVariable Long id,
                              @RequestParam String nuevoEstado,
                              RedirectAttributes redirectAttrs,
                              Authentication auth) {
        Proyecto proyecto = proyectoService.findById(id);
        if (proyecto == null) {
            redirectAttrs.addFlashAttribute("error", "Proyecto no encontrado.");
            return "redirect:/internal/proyectos/lista";
        }
        String estadoActual = proyecto.getEstado();

        if (List.of("ENTREGADO", "ANULADO").contains(estadoActual)) {
            redirectAttrs.addFlashAttribute("error", "No se puede cambiar el estado de un proyecto entregado o anulado.");
            return "redirect:/internal/proyectos/detalle/" + id;
        }

        List<String> permitidos = proyectoService.getNextAllowedStates(estadoActual);
        if (!permitidos.contains(nuevoEstado)) {
            redirectAttrs.addFlashAttribute("error",
                "No se puede cambiar de '" + estadoActual + "' a '" + nuevoEstado + "'.");
            return "redirect:/internal/proyectos/detalle/" + id;
        }

        String estadoAnterior = proyecto.getEstado();
        proyecto.setEstado(nuevoEstado);
        proyectoService.save(proyecto);

        Usuario usuario = usuarioService.findByEmail(auth.getName());
        HistorialEstadoProyecto historial = new HistorialEstadoProyecto();
        historial.setProyecto(proyecto);
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo(nuevoEstado);
        historial.setUsuario(usuario);
        historial.setFechaHora(LocalDateTime.now());
        proyectoService.saveHistorial(historial);

        auditoriaService.log("CAMBIAR_ESTADO", "PROYECTO", proyecto.getId(), estadoAnterior, nuevoEstado);

        redirectAttrs.addFlashAttribute("success", "Estado actualizado a: " + nuevoEstado);
        return "redirect:/internal/proyectos/detalle/" + id;
    }

    @PostMapping("/asignar-responsable/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String asignarResponsable(@PathVariable Long id,
                                     @RequestParam Long usuarioId,
                                     RedirectAttributes redirectAttrs) {
        Proyecto proyecto = proyectoService.findById(id);
        if (proyecto == null) {
            redirectAttrs.addFlashAttribute("error", "Proyecto no encontrado.");
            return "redirect:/internal/proyectos/lista";
        }
        Usuario usuario = usuarioService.findById(usuarioId);
        if (usuario == null) {
            redirectAttrs.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/internal/proyectos/detalle/" + id;
        }
        proyecto.setResponsable(usuario);
        proyectoService.save(proyecto);
        redirectAttrs.addFlashAttribute("success", "Responsable asignado: " + usuario.getNombre());
        return "redirect:/internal/proyectos/detalle/" + id;
    }

    
    @PostMapping("/subir-archivo/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DISEÑADOR')")
    public String subirArchivo(@PathVariable Long id,
                               @RequestParam("file") MultipartFile file,
                               @RequestParam String tipo,
                               RedirectAttributes redirectAttrs) {
        Proyecto proyecto = proyectoService.findById(id);
        if (proyecto == null) {
            redirectAttrs.addFlashAttribute("error", "Proyecto no encontrado.");
            return "redirect:/internal/proyectos/lista";
        }
        try {
            proyectoService.guardarArchivo(proyecto, file, tipo);
            redirectAttrs.addFlashAttribute("success", "Archivo subido correctamente.");
        } catch (IOException e) {
            redirectAttrs.addFlashAttribute("error", "Error al subir archivo: " + e.getMessage());
        }
        return "redirect:/internal/proyectos/detalle/" + id;
    }

    @GetMapping("/archivos/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DISEÑADOR', 'COMERCIAL')")
    public String listArchivos(@PathVariable Long id, Model model) {
        Proyecto proyecto = proyectoService.findById(id);
        List<ArchivoProyecto> archivos = proyectoService.getArchivosByProyecto(id);
        model.addAttribute("proyecto", proyecto);
        model.addAttribute("archivos", archivos);
        return "internal/proyectos/archivos";
    }


    @PostMapping("/agregar-alquiler/{proyectoId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SOPORTE_TECNICO')")
    public String agregarAlquiler(@PathVariable Long proyectoId,
                                  @RequestParam Long equipoId,
                                  @RequestParam LocalDate fechaInicio,
                                  @RequestParam LocalDate fechaFin,
                                  @RequestParam int cantidad,
                                  RedirectAttributes redirectAttrs) {
        Proyecto proyecto = proyectoService.findById(proyectoId);
        if (proyecto == null) {
            redirectAttrs.addFlashAttribute("error", "Proyecto no encontrado.");
            return "redirect:/internal/proyectos/lista";
        }
        boolean disponible = inventarioService.verificarDisponibilidad(equipoId, fechaInicio, fechaFin, cantidad, null);
        if (!disponible) {
            redirectAttrs.addFlashAttribute("error", "El equipo no está disponible en las fechas solicitadas.");
            return "redirect:/internal/proyectos/detalle/" + proyectoId;
        }
        proyectoService.agregarReservaEquipo(proyecto, equipoId, fechaInicio, fechaFin, cantidad);
        redirectAttrs.addFlashAttribute("success", "Alquiler registrado correctamente.");
        return "redirect:/internal/proyectos/detalle/" + proyectoId;
    }


    @GetMapping("/atrasados")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String proyectosAtrasados(Model model) {
        List<Proyecto> atrasados = proyectoService.findAtrasados();
        model.addAttribute("proyectos", atrasados);
        return "internal/proyectos/atrasados";
    }
}