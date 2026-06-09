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
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/internal/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @Autowired
    private ProyectoService proyectoService;
    
    @GetMapping("/lista")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String listPagos(@RequestParam(required = false) Long proyectoId,
                            Model model) {
        List<Pago> pagos;
        Proyecto proyectoFiltro = null;
        
        if (proyectoId != null && proyectoId > 0) {
            pagos = pagoService.findByProyectoId(proyectoId);
            proyectoFiltro = proyectoService.findById(proyectoId);
            model.addAttribute("proyectoFiltro", proyectoFiltro);
        } else {
            pagos = pagoService.findAll();
        }
        
        BigDecimal totalPagadoMes = pagoService.getTotalPagadoEnMes(LocalDate.now());
        BigDecimal totalPorCobrar = pagoService.getTotalPorCobrar();
        BigDecimal totalVencido = pagoService.getTotalVencido();
        int comprobantesPendientes = pagoService.countComprobantesPendientes();
        
        model.addAttribute("pagos", pagos);
        model.addAttribute("proyectos", proyectoService.findAll());
        model.addAttribute("totalPagadoMes", totalPagadoMes != null ? totalPagadoMes : BigDecimal.ZERO);
        model.addAttribute("totalPorCobrar", totalPorCobrar != null ? totalPorCobrar : BigDecimal.ZERO);
        model.addAttribute("totalVencido", totalVencido != null ? totalVencido : BigDecimal.ZERO);
        model.addAttribute("comprobantesPendientes", comprobantesPendientes);
        
        return "internal/pagos/lista";
    }

    @GetMapping("/nuevo")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String showCreateForm(Model model) {
        List<String> estadosPermitidos = List.of("APROBADO", "EN_DISEÑO", "EN_PRODUCCION", "INSTALACION_MONTAJE");
        List<Proyecto> proyectos = proyectoService.findByEstados(estadosPermitidos);
        
        Pago pago = new Pago();
        pago.setFecha(LocalDate.now());
        pago.setFechaRegistro(LocalDate.now());
        
        model.addAttribute("pago", pago);
        model.addAttribute("proyectos", proyectos);
        return "internal/pagos/registrar";
    }

    @PostMapping("/guardar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String savePago(@Valid @ModelAttribute("pago") Pago pago,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttrs,
                           Model model) {
        if (pago.getMonto() == null || pago.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            bindingResult.rejectValue("monto", "error.pago", "El monto debe ser mayor a cero.");
        }
        
        if (pago.getProyecto() == null || pago.getProyecto().getId() == null) {
            bindingResult.rejectValue("proyecto", "error.pago", "Debe seleccionar un proyecto.");
        }
        
        if (bindingResult.hasErrors()) {
            List<String> estadosPermitidos = List.of("APROBADO", "EN_DISEÑO", "EN_PRODUCCION", "INSTALACION_MONTAJE");
            model.addAttribute("proyectos", proyectoService.findByEstados(estadosPermitidos));
            return "internal/pagos/registrar";
        }

        Proyecto proyecto = proyectoService.findById(pago.getProyecto().getId());
        if (proyecto == null) {
            redirectAttrs.addFlashAttribute("error", "Proyecto no encontrado.");
            return "redirect:/internal/pagos/nuevo";
        }

        List<String> estadosPermitidos = List.of("APROBADO", "EN_DISEÑO", "EN_PRODUCCION", "INSTALACION_MONTAJE");
        if (!estadosPermitidos.contains(proyecto.getEstado())) {
            redirectAttrs.addFlashAttribute("error", 
                "No se puede registrar un pago para un proyecto en estado '" + proyecto.getEstado() + 
                "'. Los estados permitidos son: Aprobado, En diseño, En producción, Instalación/Montaje.");
            return "redirect:/internal/pagos/nuevo";
        }

        BigDecimal totalPagadoActual = pagoService.getTotalPagadoByProyecto(proyecto.getId());
        BigDecimal nuevoTotal = totalPagadoActual.add(pago.getMonto());
        BigDecimal montoCotizacion = proyecto.getCotizacion().getTotal();
        
        if (nuevoTotal.compareTo(montoCotizacion) > 0) {
            BigDecimal maximoPermitido = montoCotizacion.subtract(totalPagadoActual);
            redirectAttrs.addFlashAttribute("error", 
                "El pago excede el saldo pendiente. Monto máximo permitido: S/ " + maximoPermitido);
            return "redirect:/internal/pagos/nuevo";
        }

        pago.setProyecto(proyecto);
        if (pago.getFecha() == null) {
            pago.setFecha(LocalDate.now());
        }
        if (pago.getFechaRegistro() == null) {
            pago.setFechaRegistro(LocalDate.now());
        }
        if (pago.getEstado() == null) {
            pago.setEstado("VERIFICADO");
        }
        
        pagoService.save(pago);
        redirectAttrs.addFlashAttribute("success", "Pago registrado exitosamente.");
        return "redirect:/internal/pagos/lista";
    }

    @PostMapping("/registrar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String registrarPagoRapido(@RequestParam Long proyectoId,
                                      @RequestParam BigDecimal monto,
                                      @RequestParam String fecha,
                                      @RequestParam String medioPago,
                                      RedirectAttributes redirectAttrs) {
        try {
            Proyecto proyecto = proyectoService.findById(proyectoId);
            if (proyecto == null) {
                redirectAttrs.addFlashAttribute("error", "Proyecto no encontrado.");
                return "redirect:/internal/pagos/lista";
            }
            
            Pago pago = new Pago();
            pago.setProyecto(proyecto);
            pago.setMonto(monto);
            pago.setFecha(LocalDate.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE));
            pago.setMedioPago(medioPago);
            pago.setEstado("VERIFICADO");
            pago.setFechaRegistro(LocalDate.now());
            
            pagoService.save(pago);
            redirectAttrs.addFlashAttribute("success", "Pago registrado correctamente.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error al registrar pago: " + e.getMessage());
        }
        return "redirect:/internal/pagos/lista";
    }

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
        
        pagoService.cancelPago(id, motivo);
        redirectAttrs.addFlashAttribute("success", "Pago anulado correctamente. Motivo: " + motivo);
        return "redirect:/internal/pagos/lista";
    }

    @GetMapping("/api/saldo-pendiente")
    @ResponseBody
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public BigDecimal getSaldoPendiente(@RequestParam Long proyectoId) {
        Proyecto proyecto = proyectoService.findById(proyectoId);
        if (proyecto == null || proyecto.getCotizacion() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalPagado = pagoService.getTotalPagadoByProyecto(proyectoId);
        return proyecto.getCotizacion().getTotal().subtract(totalPagado);
    }

    @GetMapping("/reporte-pendientes")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String reportePagosPendientes(Model model) {
        List<Object[]> pendientes = pagoService.getPagosPendientesPorCliente();
        model.addAttribute("pendientes", pendientes);
        return "internal/pagos/saldo-pendiente";
    }
}