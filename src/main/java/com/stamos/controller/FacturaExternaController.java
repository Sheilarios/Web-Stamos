package com.stamos.controller;

import com.stamos.entity.FacturaExterna;
import com.stamos.service.FacturaExternaService;
import com.stamos.service.PagoService;
import com.stamos.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/internal/facturas")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class FacturaExternaController {

    @Autowired
    private FacturaExternaService facturaService;
    @Autowired
    private ProyectoService proyectoService;
    @Autowired
    private PagoService pagoService;

    @GetMapping("/nueva")
    public String nuevaFactura(Model model) {
        model.addAttribute("factura", new FacturaExterna());
        model.addAttribute("proyectos", proyectoService.findAll());
        model.addAttribute("pagos", pagoService.findAll());
        return "internal/facturas/formulario";
    }

    @PostMapping("/guardar")
    public String guardarFactura(@ModelAttribute FacturaExterna factura) {
        facturaService.save(factura);
        return "redirect:/internal/facturas/lista";
    }

    @GetMapping("/lista")
    public String listarFacturas(Model model) {
        model.addAttribute("facturas", facturaService.findAll());
        return "internal/facturas/lista";
    }
}