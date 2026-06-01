package com.stamos.controller;

import com.stamos.entity.Resena;
import com.stamos.entity.Proyecto;
import com.stamos.entity.Cliente;
import com.stamos.service.ResenaService;
import com.stamos.service.ProyectoService;
import com.stamos.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controlador para la gestión de reseñas y calificaciones de clientes.
 * 
 * Reglas de negocio:
 * - Solo clientes con proyecto en estado "ENTREGADO" pueden crear una reseña (RF-30).
 * - Las reseñas quedan en estado "PENDIENTE" hasta que el administrador las apruebe (RNF-09).
 * - Solo el administrador puede aprobar, rechazar o eliminar reseñas.
 * - Las reseñas aprobadas se muestran automáticamente en la web pública.
 */
@Controller
@RequestMapping("/resenas")
public class ResenaController {

    @Autowired
    private ResenaService resenaService;

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private ClienteService clienteService;

    // ==================== CLIENTE: CREAR RESEÑA (desde portal público) ====================

    /**
     * Muestra el formulario para que el cliente cree una reseña sobre un proyecto entregado.
     * Solo accesible para clientes autenticados (rol CLIENTE).
     *
     * @param proyectoId ID del proyecto (debe pertenecer al cliente y estar ENTREGADO)
     * @param model Modelo
     * @param authentication Usuario autenticado
     * @param redirectAttrs Mensajes flash
     * @return Vista del formulario o redirección con error
     */
    @GetMapping("/cliente/nueva")
    @PreAuthorize("hasRole('CLIENTE')")
    public String showCreateForm(@RequestParam Long proyectoId,
                                 Model model,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttrs) {
        // Obtener cliente autenticado
        Cliente cliente = clienteService.findByEmail(authentication.getName());
        if (cliente == null) {
            redirectAttrs.addFlashAttribute("error", "No se encontró información del cliente.");
            return "redirect:/public/cliente/dashboard";
        }

        Proyecto proyecto = proyectoService.findById(proyectoId);
        if (proyecto == null) {
            redirectAttrs.addFlashAttribute("error", "Proyecto no encontrado.");
            return "redirect:/public/cliente/dashboard";
        }

        // Verificar que el proyecto pertenezca al cliente
        if (!proyecto.getCliente().getId().equals(cliente.getId())) {
            redirectAttrs.addFlashAttribute("error", "No tiene permiso para calificar este proyecto.");
            return "redirect:/public/cliente/dashboard";
        }

        // Verificar que el proyecto esté ENTREGADO
        if (!"ENTREGADO".equals(proyecto.getEstado())) {
            redirectAttrs.addFlashAttribute("error", "Solo puede calificar proyectos que hayan sido entregados.");
            return "redirect:/public/cliente/dashboard";
        }

        // Verificar si ya existe una reseña para este proyecto
        if (resenaService.existsByProyectoId(proyectoId)) {
            redirectAttrs.addFlashAttribute("error", "Ya has enviado una reseña para este proyecto.");
            return "redirect:/public/cliente/dashboard";
        }

        Resena resena = new Resena();
        resena.setProyecto(proyecto);
        resena.setCliente(cliente);
        model.addAttribute("resena", resena);
        model.addAttribute("proyecto", proyecto);
        return "public/cliente/resena-form";
    }

    /**
     * Procesa el envío de una nueva reseña.
     * La reseña se guarda con estado "PENDIENTE" (moderación).
     *
     * @param resena Datos de la reseña (calificación y comentario)
     * @param bindingResult Validación
     * @param redirectAttrs Mensajes flash
     * @return Redirección al dashboard del cliente
     */
    @PostMapping("/cliente/guardar")
    @PreAuthorize("hasRole('CLIENTE')")
    public String saveResena(@Valid @ModelAttribute("resena") Resena resena,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            return "public/cliente/resena-form";
        }
        // Validar calificación entre 1 y 5
        if (resena.getCalificacion() < 1 || resena.getCalificacion() > 5) {
            redirectAttrs.addFlashAttribute("error", "La calificación debe ser entre 1 y 5 estrellas.");
            return "redirect:/resenas/cliente/nueva?proyectoId=" + resena.getProyecto().getId();
        }

        resena.setEstado("PENDIENTE"); // Espera moderación
        resenaService.save(resena);
        redirectAttrs.addFlashAttribute("success", "Gracias por tu reseña. Será revisada por nuestro equipo y publicada próximamente.");
        return "redirect:/public/cliente/dashboard";
    }

    // ==================== ADMIN: GESTIÓN DE RESEÑAS ====================

    /**
     * Lista todas las reseñas (pendientes, aprobadas, rechazadas) para administración.
     *
     * @param estado Filtro opcional por estado (PENDIENTE, APROBADA, RECHAZADA)
     * @param model Modelo
     * @return Vista de administración
     */
    @GetMapping("/admin/lista")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String listResenas(@RequestParam(required = false) String estado, Model model) {
        List<Resena> resenas;
        if (estado != null && !estado.isEmpty()) {
            resenas = resenaService.findByEstado(estado);
        } else {
            resenas = resenaService.findAll();
        }
        model.addAttribute("resenas", resenas);
        model.addAttribute("estadoSeleccionado", estado);
        return "internal/resenas/lista";
    }

    /**
     * Muestra el detalle de una reseña para moderación.
     *
     * @param id ID de la reseña
     * @param model Modelo
     * @param redirectAttrs
     * @return Vista de detalle
     */
    @GetMapping("/admin/detalle/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String viewDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        Resena resena = resenaService.findById(id);
        if (resena == null) {
            redirectAttrs.addFlashAttribute("error", "Reseña no encontrada.");
            return "redirect:/resenas/admin/lista";
        }
        model.addAttribute("resena", resena);
        return "internal/resenas/detalle";
    }

    /**
     * Aprueba una reseña (cambia estado a APROBADA y se vuelve visible en web pública).
     *
     * @param id ID de la reseña
     * @param redirectAttrs Mensajes flash
     * @return Redirección a lista
     */
    @PostMapping("/admin/aprobar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String approveResena(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        Resena resena = resenaService.findById(id);
        if (resena == null) {
            redirectAttrs.addFlashAttribute("error", "Reseña no encontrada.");
            return "redirect:/resenas/admin/lista";
        }
        if (!"PENDIENTE".equals(resena.getEstado())) {
            redirectAttrs.addFlashAttribute("warning", "Esta reseña ya fue procesada.");
            return "redirect:/resenas/admin/lista";
        }
        resena.setEstado("APROBADA");
        resenaService.save(resena);
        redirectAttrs.addFlashAttribute("success", "Reseña aprobada y publicada en la web.");
        return "redirect:/resenas/admin/lista";
    }

    /**
     * Rechaza una reseña (cambia estado a RECHAZADA, no se publica).
     *
     * @param id ID de la reseña
     * @param motivo Motivo del rechazo (opcional)
     * @param redirectAttrs Mensajes flash
     * @return Redirección a lista
     */
    @PostMapping("/admin/rechazar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String rejectResena(@PathVariable Long id,
                               @RequestParam(required = false) String motivo,
                               RedirectAttributes redirectAttrs) {
        Resena resena = resenaService.findById(id);
        if (resena == null) {
            redirectAttrs.addFlashAttribute("error", "Reseña no encontrada.");
            return "redirect:/resenas/admin/lista";
        }
        if (!"PENDIENTE".equals(resena.getEstado())) {
            redirectAttrs.addFlashAttribute("warning", "Esta reseña ya fue procesada.");
            return "redirect:/resenas/admin/lista";
        }
        resena.setEstado("RECHAZADA");
        // Opcional: guardar motivo en algún campo (se podría extender la entidad)
        resenaService.save(resena);
        redirectAttrs.addFlashAttribute("success", "Reseña rechazada. No será publicada.");
        return "redirect:/resenas/admin/lista";
    }

    /**
     * Elimina una reseña (solo administrador).
     *
     * @param id ID de la reseña
     * @param redirectAttrs Mensajes flash
     * @return Redirección a lista
     */
    @PostMapping("/admin/eliminar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String deleteResena(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        Resena resena = resenaService.findById(id);
        if (resena == null) {
            redirectAttrs.addFlashAttribute("error", "Reseña no encontrada.");
            return "redirect:/resenas/admin/lista";
        }
        resenaService.delete(id);
        redirectAttrs.addFlashAttribute("success", "Reseña eliminada permanentemente.");
        return "redirect:/resenas/admin/lista";
    }

    // ==================== PÚBLICO: LISTA DE RESEÑAS APROBADAS ====================

    /**
     * Endpoint para la web pública: muestra todas las reseñas aprobadas.
     * No requiere autenticación.
     *
     * @param model Modelo
     * @return Fragmento HTML o página parcial (se puede incluir en la landing)
     */
    @GetMapping("/public/listar")
    public String listResenasPublicas(Model model) {
        List<Resena> resenas = resenaService.findByEstado("APROBADA");
        model.addAttribute("resenas", resenas);
        return "fragments/public/resenas"; // Fragmento reutilizable
    }
}