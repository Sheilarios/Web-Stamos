package com.stamos.controller;

import com.stamos.entity.*;
import com.stamos.service.*;
import com.stamos.payment.PaymentGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/public/cliente")
public class ClientePortalController {

    @Autowired
    private ClienteService clienteService;
    @Autowired
    private ProyectoService proyectoService;
    @Autowired
    private PagoService pagoService;
    @Autowired
    private PaymentGateway paymentGateway;
    @Autowired
    private EmailService emailService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        Cliente cliente = clienteService.findByEmail(auth.getName());
        List<Proyecto> proyectos = proyectoService.findByClienteId(cliente.getId());
        model.addAttribute("proyectos", proyectos);
        model.addAttribute("cliente", cliente);
        return "public/cliente/dashboard";
    }

    @GetMapping("/proyecto/{id}")
    public String verProyecto(@PathVariable Long id, Authentication auth, Model model, RedirectAttributes redirectAttrs) {
        Proyecto proyecto = proyectoService.findById(id);
        Cliente cliente = clienteService.findByEmail(auth.getName());
        if (proyecto == null || !proyecto.getCliente().getId().equals(cliente.getId())) {
            redirectAttrs.addFlashAttribute("error", "Proyecto no encontrado o no autorizado.");
            return "redirect:/public/cliente/dashboard";
        }
        List<ArchivoProyecto> archivos = proyectoService.getArchivosByProyecto(id);
        BigDecimal saldoPendiente = pagoService.getSaldoPendiente(proyecto);
        model.addAttribute("proyecto", proyecto);
        model.addAttribute("archivos", archivos);
        model.addAttribute("saldoPendiente", saldoPendiente);
        model.addAttribute("instruccionesPago", paymentGateway.obtenerInstruccionesPago());
        return "public/cliente/proyecto-detalle";
    }

    @PostMapping("/subir-comprobante/{proyectoId}")
    public String subirComprobante(@PathVariable Long proyectoId,
                                   @RequestParam("file") MultipartFile file,
                                   @RequestParam("monto") BigDecimal monto,
                                   @RequestParam("fecha") String fecha,
                                   Authentication auth,
                                   RedirectAttributes redirectAttrs) {
        Cliente cliente = clienteService.findByEmail(auth.getName());
        Proyecto proyecto = proyectoService.findById(proyectoId);
        if (proyecto == null || !proyecto.getCliente().getId().equals(cliente.getId())) {
            redirectAttrs.addFlashAttribute("error", "Proyecto inválido.");
            return "redirect:/public/cliente/dashboard";
        }
        try {
            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get("uploads/comprobantes/" + filename);
            Files.createDirectories(path.getParent());
            file.transferTo(path.toFile());

            Pago pago = new Pago();
            pago.setProyecto(proyecto);
            pago.setMonto(monto);
            pago.setFecha(LocalDate.parse(fecha));
            pago.setMedioPago("TRANSFERENCIA");
            pago.setComprobanteNumero(filename);
            pago.setEstado(Pago.ESTADO_VERIFICACION);
            pagoService.save(pago);

            emailService.sendEmail("admin@stamos.com", "Nuevo comprobante de pago",
                "Cliente " + cliente.getNombreRazonSocial() + " subió comprobante para proyecto " + proyecto.getId());
            redirectAttrs.addFlashAttribute("success", "Comprobante subido. Será verificado en 24 horas.");
        } catch (IOException e) {
            redirectAttrs.addFlashAttribute("error", "Error al guardar el comprobante.");
        }
        return "redirect:/public/cliente/proyecto/" + proyectoId;
    }
}