package com.stamos.controller;

import com.stamos.entity.CatalogoEquipo;
import com.stamos.entity.ReservaEquipo;
import com.stamos.entity.Proyecto;
import com.stamos.service.InventarioService;
import com.stamos.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controlador para la gestión de inventario de equipos y mobiliario.
 * 
 * Reglas de negocio aplicadas (RIN01 a RIN05):
 * - Todo equipo debe registrarse con nombre, cantidad total disponible y ubicación.
 * - Al asignar un equipo a un proyecto, se reduce la cantidad disponible durante el período del evento.
 * - No se puede asignar un equipo si la cantidad disponible es insuficiente para las fechas solicitadas.
 * - Se puede consultar disponibilidad por rango de fechas.
 * - Solo ADMINISTRADOR o SOPORTE_TECNICO puede modificar el stock.
 */
@Controller
@RequestMapping("/internal/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private ProyectoService proyectoService;

    // ==================== EQUIPOS (CRUD) ====================

    /**
     * Lista todos los equipos registrados en el catálogo.
     * Accesible para ADMIN, SOPORTE_TECNICO, y otros roles con solo lectura.
     */
    @GetMapping("/equipos/lista")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL', 'DISEÑADOR', 'PRODUCCION', 'SOPORTE_TECNICO')")
    public String listEquipos(Model model) {
        List<CatalogoEquipo> equipos = inventarioService.findAllEquipos();
        model.addAttribute("equipos", equipos);
        return "internal/inventario/equipos/lista";
    }

    /**
     * Muestra el formulario para crear un nuevo equipo.
     * Solo ADMIN o SOPORTE_TECNICO.
     */
    @GetMapping("/equipos/nuevo")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SOPORTE_TECNICO')")
    public String showCreateEquipoForm(Model model) {
        model.addAttribute("equipo", new CatalogoEquipo());
        model.addAttribute("action", "crear");
        return "internal/inventario/equipos/formulario";
    }

    /**
     * Guarda un nuevo equipo en el catálogo.
     */
    @PostMapping("/equipos/guardar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SOPORTE_TECNICO')")
    public String saveEquipo(@Valid @ModelAttribute("equipo") CatalogoEquipo equipo,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            return "internal/inventario/equipos/formulario";
        }
        // Validar que cantidad total no sea negativa
        if (equipo.getCantidadTotal() < 0) {
            bindingResult.rejectValue("cantidadTotal", "error.equipo", "La cantidad total no puede ser negativa");
            return "internal/inventario/equipos/formulario";
        }
        inventarioService.saveEquipo(equipo);
        redirectAttrs.addFlashAttribute("success", "Equipo registrado correctamente.");
        return "redirect:/internal/inventario/equipos/lista";
    }

    /**
     * Muestra el formulario para editar un equipo existente.
     */
    @GetMapping("/equipos/editar/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SOPORTE_TECNICO')")
    public String showEditEquipoForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        CatalogoEquipo equipo = inventarioService.findEquipoById(id);
        if (equipo == null) {
            redirectAttrs.addFlashAttribute("error", "Equipo no encontrado.");
            return "redirect:/internal/inventario/equipos/lista";
        }
        model.addAttribute("equipo", equipo);
        model.addAttribute("action", "editar");
        return "internal/inventario/equipos/formulario";
    }

    /**
     * Actualiza los datos de un equipo.
     */
    @PostMapping("/equipos/actualizar/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SOPORTE_TECNICO')")
    public String updateEquipo(@PathVariable Long id,
                               @Valid @ModelAttribute("equipo") CatalogoEquipo equipo,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttrs) {
        CatalogoEquipo existing = inventarioService.findEquipoById(id);
        if (existing == null) {
            redirectAttrs.addFlashAttribute("error", "Equipo no encontrado.");
            return "redirect:/internal/inventario/equipos/lista";
        }
        if (bindingResult.hasErrors()) {
            return "internal/inventario/equipos/formulario";
        }
        existing.setNombre(equipo.getNombre());
        existing.setCantidadTotal(equipo.getCantidadTotal());
        existing.setUbicacion(equipo.getUbicacion());
        inventarioService.saveEquipo(existing);
        redirectAttrs.addFlashAttribute("success", "Equipo actualizado correctamente.");
        return "redirect:/internal/inventario/equipos/lista";
    }

    /**
     * Elimina un equipo solo si no tiene reservas asociadas.
     * (Alternativa: se podría deshabilitar lógicamente, pero por simplicidad se elimina físicamente)
     */
    @PostMapping("/equipos/eliminar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String deleteEquipo(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        CatalogoEquipo equipo = inventarioService.findEquipoById(id);
        if (equipo == null) {
            redirectAttrs.addFlashAttribute("error", "Equipo no encontrado.");
            return "redirect:/internal/inventario/equipos/lista";
        }
        if (inventarioService.hasReservasAsociadas(id)) {
            redirectAttrs.addFlashAttribute("error", 
                "No se puede eliminar el equipo porque tiene reservas asociadas.");
            return "redirect:/internal/inventario/equipos/lista";
        }
        inventarioService.deleteEquipo(id);
        redirectAttrs.addFlashAttribute("success", "Equipo eliminado correctamente.");
        return "redirect:/internal/inventario/equipos/lista";
    }

    // ==================== RESERVAS (ALQUILERES) ====================

    /**
     * Lista todas las reservas (alquileres) de equipos.
     */
    @GetMapping("/reservas/lista")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SOPORTE_TECNICO', 'COMERCIAL')")
    public String listReservas(Model model) {
        List<ReservaEquipo> reservas = inventarioService.findAllReservas();
        model.addAttribute("reservas", reservas);
        return "internal/inventario/reservas/lista-reservas";
    }

    /**
     * Muestra un calendario visual de reservas para todos los equipos.
     * Utiliza FullCalendar.js en la vista.
     */
    @GetMapping("/reservas/calendario")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SOPORTE_TECNICO', 'COMERCIAL', 'DISEÑADOR')")
    public String showCalendar(Model model) {
        List<CatalogoEquipo> equipos = inventarioService.findAllEquipos();
        model.addAttribute("equipos", equipos);
        return "internal/inventario/reservas/calendario";
    }

    /**
     * Endpoint AJAX para obtener eventos del calendario en formato JSON.
     */
    @GetMapping("/reservas/eventos")
    @ResponseBody
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SOPORTE_TECNICO', 'COMERCIAL', 'DISEÑADOR')")
    public List<Map<String, Object>> getReservasEventos(@RequestParam(required = false) Long equipoId) {
        return inventarioService.getReservasComoEventos(equipoId);
    }

    /**
     * Muestra el formulario para crear una nueva reserva.
     */
    @GetMapping("/reservas/nueva")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SOPORTE_TECNICO', 'COMERCIAL')")
    public String showCreateReservaForm(Model model) {
        model.addAttribute("reserva", new ReservaEquipo());
        model.addAttribute("equipos", inventarioService.findAllEquipos());
        model.addAttribute("proyectos", proyectoService.findAll()); // Proyectos existentes para asociar
        return "internal/inventario/reservas/nueva-reserva";
    }

    /**
     * Guarda una nueva reserva, validando disponibilidad según fechas.
     * Aplica RIN03: No se puede asignar si la cantidad disponible es insuficiente.
     */
    @PostMapping("/reservas/guardar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SOPORTE_TECNICO', 'COMERCIAL')")
    public String saveReserva(@Valid @ModelAttribute("reserva") ReservaEquipo reserva,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttrs,
                              Model model) {
        // Validar fechas: fecha inicio no puede ser posterior a fecha fin
        if (reserva.getFechaInicio() != null && reserva.getFechaFin() != null &&
            reserva.getFechaInicio().isAfter(reserva.getFechaFin())) {
            bindingResult.rejectValue("fechaFin", "error.reserva", "La fecha de fin debe ser posterior a la fecha de inicio");
        }
        if (reserva.getCantidad() <= 0) {
            bindingResult.rejectValue("cantidad", "error.reserva", "La cantidad debe ser mayor a cero");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("equipos", inventarioService.findAllEquipos());
            model.addAttribute("proyectos", proyectoService.findAll());
            return "internal/inventario/reservas/nueva-reserva";
        }

        // Verificar disponibilidad (RIN03)
        boolean disponible = inventarioService.verificarDisponibilidad(
            reserva.getEquipo().getId(),
            reserva.getFechaInicio(),
            reserva.getFechaFin(),
            reserva.getCantidad(),
            null // ID de reserva actual es null para nueva
        );
        if (!disponible) {
            redirectAttrs.addFlashAttribute("error", 
                "No hay suficiente disponibilidad del equipo para las fechas seleccionadas. " +
                "Verifique el stock y los conflictos existentes.");
            return "redirect:/internal/inventario/reservas/nueva";
        }

        inventarioService.saveReserva(reserva);
        redirectAttrs.addFlashAttribute("success", "Reserva creada correctamente.");
        return "redirect:/internal/inventario/reservas/lista";
    }

    /**
     * Muestra el formulario para editar una reserva existente.
     * También verifica disponibilidad antes de guardar cambios.
     */
    @GetMapping("/reservas/editar/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SOPORTE_TECNICO')")
    public String showEditReservaForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        ReservaEquipo reserva = inventarioService.findReservaById(id);
        if (reserva == null) {
            redirectAttrs.addFlashAttribute("error", "Reserva no encontrada.");
            return "redirect:/internal/inventario/reservas/lista";
        }
        model.addAttribute("reserva", reserva);
        model.addAttribute("equipos", inventarioService.findAllEquipos());
        model.addAttribute("proyectos", proyectoService.findAll());
        return "internal/inventario/reservas/nueva-reserva";
    }

    /**
     * Actualiza una reserva, verificando disponibilidad (excluyéndose a sí misma).
     */
    @PostMapping("/reservas/actualizar/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SOPORTE_TECNICO')")
    public String updateReserva(@PathVariable Long id,
                                @Valid @ModelAttribute("reserva") ReservaEquipo reserva,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttrs,
                                Model model) {
        ReservaEquipo existing = inventarioService.findReservaById(id);
        if (existing == null) {
            redirectAttrs.addFlashAttribute("error", "Reserva no encontrada.");
            return "redirect:/internal/inventario/reservas/lista";
        }
        if (reserva.getFechaInicio().isAfter(reserva.getFechaFin())) {
            bindingResult.rejectValue("fechaFin", "error.reserva", "La fecha de fin debe ser posterior a la fecha de inicio");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("equipos", inventarioService.findAllEquipos());
            model.addAttribute("proyectos", proyectoService.findAll());
            return "internal/inventario/reservas/nueva-reserva";
        }

        // Verificar disponibilidad excluyendo la reserva actual
        boolean disponible = inventarioService.verificarDisponibilidad(
            reserva.getEquipo().getId(),
            reserva.getFechaInicio(),
            reserva.getFechaFin(),
            reserva.getCantidad(),
            id
        );
        if (!disponible) {
            redirectAttrs.addFlashAttribute("error", 
                "No se puede actualizar la reserva porque genera conflicto de disponibilidad.");
            return "redirect:/internal/inventario/reservas/editar/" + id;
        }

        existing.setEquipo(reserva.getEquipo());
        existing.setProyecto(reserva.getProyecto());
        existing.setFechaInicio(reserva.getFechaInicio());
        existing.setFechaFin(reserva.getFechaFin());
        existing.setCantidad(reserva.getCantidad());
        inventarioService.saveReserva(existing);
        redirectAttrs.addFlashAttribute("success", "Reserva actualizada correctamente.");
        return "redirect:/internal/inventario/reservas/lista";
    }

    /**
     * Cancela (elimina) una reserva. Útil si un proyecto se cancela o cambia de fechas.
     */
    @PostMapping("/reservas/cancelar/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SOPORTE_TECNICO')")
    public String cancelReserva(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        ReservaEquipo reserva = inventarioService.findReservaById(id);
        if (reserva == null) {
            redirectAttrs.addFlashAttribute("error", "Reserva no encontrada.");
            return "redirect:/internal/inventario/reservas/lista";
        }
        inventarioService.deleteReserva(id);
        redirectAttrs.addFlashAttribute("success", "Reserva cancelada correctamente.");
        return "redirect:/internal/inventario/reservas/lista";
    }

    // ==================== CONSULTA DE DISPONIBILIDAD (RF-24) ====================

    /**
     * Muestra un formulario para consultar disponibilidad de equipos por rango de fechas.
     */
    @GetMapping("/disponibilidad")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SOPORTE_TECNICO', 'COMERCIAL', 'DISEÑADOR')")
    public String showDisponibilidadForm(Model model) {
        model.addAttribute("equipos", inventarioService.findAllEquipos());
        return "internal/inventario/disponibilidad";
    }

    /**
     * Procesa la consulta de disponibilidad y devuelve una tabla con los equipos disponibles.
     */
    @GetMapping("/disponibilidad/consultar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SOPORTE_TECNICO', 'COMERCIAL', 'DISEÑADOR')")
    public String consultarDisponibilidad(@RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                                          @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                                          Model model) {
        if (fechaInicio.isAfter(fechaFin)) {
            model.addAttribute("error", "La fecha de inicio no puede ser posterior a la fecha de fin.");
            model.addAttribute("equipos", inventarioService.findAllEquipos());
            return "internal/inventario/disponibilidad";
        }
        Map<CatalogoEquipo, Integer> disponibilidad = inventarioService.getDisponibilidadPorRango(fechaInicio, fechaFin);
        model.addAttribute("disponibilidad", disponibilidad);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("equipos", inventarioService.findAllEquipos());
        return "internal/inventario/disponibilidad";
    }

    // ==================== ENDPOINTS AJAX ADICIONALES ====================

    /**
     * Devuelve la disponibilidad de un equipo específico en formato JSON.
     * Útil para validaciones asíncronas en el formulario de reserva.
     */
    @GetMapping("/api/disponibilidad-equipo")
    @ResponseBody
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SOPORTE_TECNICO', 'COMERCIAL')")
    public Map<String, Object> checkDisponibilidadEquipo(@RequestParam Long equipoId,
                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                                                         @RequestParam int cantidad,
                                                         @RequestParam(required = false) Long reservaId) {
        boolean disponible = inventarioService.verificarDisponibilidad(equipoId, fechaInicio, fechaFin, cantidad, reservaId);
        int cantidadDisponible = inventarioService.getCantidadDisponible(equipoId, fechaInicio, fechaFin);
        return Map.of(
            "disponible", disponible,
            "cantidadDisponible", cantidadDisponible
        );
    }
}