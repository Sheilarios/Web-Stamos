package com.stamos.controller;

import com.stamos.entity.Pago;
import com.stamos.entity.Proyecto;
import com.stamos.service.PagoService;
import com.stamos.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador para la gestión de pagos.
 * Accesible solo por ADMINISTRADOR y COMERCIAL.
 * 
 * Reglas de negocio aplicadas (RPA01 a RPA06):
 * - Los pagos solo pueden registrarse para proyectos en estado Aprobado, En diseño, En producción o Instalación/Montaje.
 * - Se aceptan pagos parciales, mostrando saldo pendiente.
 * - El total de pagos no puede superar el monto total de la cotización asociada.
 * - Cada pago debe incluir: fecha, monto, medio de pago, y opcionalmente número de comprobante.
 * - Solo ADMIN o COMERCIAL puede registrar o anular pagos.
 * - Al anular un pago, se deja registro de la anulación (fecha, usuario, motivo).
 */
@Controller
@RequestMapping("/internal/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @Autowired
    private ProyectoService proyectoService;

    /**
     * Lista todos los pagos registrados, con filtros opcionales.
     * 
     * @param proyectoId Filtro por proyecto
     * @param model      Modelo
     * @return Vista lista de pagos
     */
    @GetMapping("/lista")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String listPagos(@RequestParam(required = false) Long proyectoId,
                            Model model) {
        List<Pago> pagos;
        if (proyectoId != null) {
            pagos = pagoService.findByProyectoId(proyectoId);
            model.addAttribute("proyecto", proyectoService.findById(proyectoId));
        } else {
            pagos = pagoService.findAll();
        }
        model.addAttribute("pagos", pagos);
        model.addAttribute("proyectos", proyectoService.findAll());
        return "internal/pagos/lista";
    }

    /**
     * Muestra el formulario para registrar un nuevo pago.
     * Solo se muestran proyectos que cumplan con RPA01.
     * 
     * @param model Modelo
     * @return Vista formulario
     */
    @GetMapping("/nuevo")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String showCreateForm(Model model) {
        // Obtener solo proyectos en estados permitidos para pagos
        List<String> estadosPermitidos = List.of("APROBADO", "EN_DISEÑO", "EN_PRODUCCION", "INSTALACION_MONTAJE");
        List<Proyecto> proyectos = proyectoService.findByEstados(estadosPermitidos);
        
        Pago pago = new Pago();
        pago.setFecha(LocalDate.now());
        model.addAttribute("pago", pago);
        model.addAttribute("proyectos", proyectos);
        return "internal/pagos/registrar";
    }

    /**
     * Guarda un nuevo pago, validando las reglas de negocio.
     * 
     * @param pago           Datos del pago
     * @param bindingResult  Resultado de validación
     * @param redirectAttrs  Mensajes flash
     * @param model          Modelo para errores
     * @return Redirección a lista o formulario con errores
     */
    @PostMapping("/guardar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String savePago(@Valid @ModelAttribute("pago") Pago pago,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttrs,
                           Model model) {
        // Validar monto > 0
        if (pago.getMonto() == null || pago.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            bindingResult.rejectValue("monto", "error.pago", "El monto debe ser mayor a cero.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("proyectos", proyectoService.findByEstados(
                List.of("APROBADO", "EN_DISEÑO", "EN_PRODUCCION", "INSTALACION_MONTAJE")));
            return "internal/pagos/registrar";
        }

        Proyecto proyecto = proyectoService.findById(pago.getProyecto().getId());
        if (proyecto == null) {
            redirectAttrs.addFlashAttribute("error", "Proyecto no encontrado.");
            return "redirect:/internal/pagos/nuevo";
        }

        // RPA01: Validar estado del proyecto
        List<String> estadosPermitidos = List.of("APROBADO", "EN_DISEÑO", "EN_PRODUCCION", "INSTALACION_MONTAJE");
        if (!estadosPermitidos.contains(proyecto.getEstado())) {
            redirectAttrs.addFlashAttribute("error", 
                "No se puede registrar un pago para un proyecto en estado '" + proyecto.getEstado() + 
                "'. Los estados permitidos son: Aprobado, En diseño, En producción, Instalación/Montaje.");
            return "redirect:/internal/pagos/nuevo";
        }

        // RPA03: Validar que el total acumulado no supere el monto de la cotización
        BigDecimal totalPagadoActual = pagoService.getTotalPagadoByProyecto(proyecto.getId());
        BigDecimal nuevoTotal = totalPagadoActual.add(pago.getMonto());
        BigDecimal montoCotizacion = proyecto.getCotizacion().getTotal();
        if (nuevoTotal.compareTo(montoCotizacion) > 0) {
            redirectAttrs.addFlashAttribute("error", 
                "El pago excede el saldo pendiente. Monto máximo permitido: " + 
                montoCotizacion.subtract(totalPagadoActual));
            return "redirect:/internal/pagos/nuevo";
        }

        pago.setProyecto(proyecto);
        pago.setFecha(pago.getFecha() != null ? pago.getFecha() : LocalDate.now());
        pagoService.save(pago);
        redirectAttrs.addFlashAttribute("success", "Pago registrado exitosamente.");
        return "redirect:/internal/pagos/lista";
    }

    /**
     * Muestra el detalle de un pago.
     * 
     * @param id    ID del pago
     * @param model Modelo
     * @param redirectAttrs
     * @return Vista detalle
     */
    @GetMapping("/detalle/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String viewDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        Pago pago = pagoService.findById(id);
        if (pago == null) {
            redirectAttrs.addFlashAttribute("error", "Pago no encontrado.");
            return "redirect:/internal/pagos/lista";
        }
        model.addAttribute("pago", pago);
        return "internal/pagos/detalle";
    }

    /**
     * Muestra el formulario para anular un pago (solo administrador).
     * 
     * @param id    ID del pago
     * @param model Modelo
     * @param redirectAttrs
     * @return Vista de anulación
     */
    @GetMapping("/anular/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String showCancelForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        Pago pago = pagoService.findById(id);
        if (pago == null) {
            redirectAttrs.addFlashAttribute("error", "Pago no encontrado.");
            return "redirect:/internal/pagos/lista";
        }
        model.addAttribute("pago", pago);
        return "internal/pagos/anular";
    }

    /**
     * Procesa la anulación de un pago (solo administrador).
     * Registra motivo, fecha, usuario (vía auditoría).
     * 
     * @param id            ID del pago
     * @param motivo        Motivo de anulación (obligatorio)
     * @param redirectAttrs Mensajes flash
     * @return Redirección a lista
     */
    @PostMapping("/anular/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String cancelPago(@PathVariable Long id,
                             @RequestParam String motivo,
                             RedirectAttributes redirectAttrs) {
        Pago pago = pagoService.findById(id);
        if (pago == null) {
            redirectAttrs.addFlashAttribute("error", "Pago no encontrado.");
            return "redirect:/internal/pagos/lista";
        }
        if (motivo == null || motivo.trim().isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "Debe especificar un motivo para la anulación.");
            return "redirect:/internal/pagos/anular/" + id;
        }
        // El servicio registrará la anulación en la tabla de auditoría (logs)
        pagoService.cancelPago(id, motivo);
        redirectAttrs.addFlashAttribute("success", "Pago anulado correctamente. Motivo: " + motivo);
        return "redirect:/internal/pagos/lista";
    }

    /**
     * Endpoint AJAX para obtener el saldo pendiente de un proyecto.
     * Útil para formularios de registro de pago.
     * 
     * @param proyectoId ID del proyecto
     * @return Saldo pendiente como JSON
     */
    @GetMapping("/api/saldo-pendiente")
    @ResponseBody
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public BigDecimal getSaldoPendiente(@RequestParam Long proyectoId) {
        Proyecto proyecto = proyectoService.findById(proyectoId);
        if (proyecto == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalPagado = pagoService.getTotalPagadoByProyecto(proyectoId);
        return proyecto.getCotizacion().getTotal().subtract(totalPagado);
    }

    /**
     * Reporte de saldos pendientes por cliente (basado en proyectos con saldo > 0).
     * 
     * @param model Modelo
     * @return Vista de reporte de pagos pendientes
     */
    @GetMapping("/reporte-pendientes")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String reportePagosPendientes(Model model) {
        List<Object[]> pendientes = pagoService.getPagosPendientesPorCliente();
        model.addAttribute("pendientes", pendientes);
        return "internal/pagos/reporte-pendientes";
    }
}