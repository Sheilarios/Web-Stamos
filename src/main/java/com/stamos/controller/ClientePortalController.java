package com.stamos.controller;

import com.stamos.entity.*;
import com.stamos.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/public/cliente")  // ← CAMBIADO de "/portal" a "/public/cliente"
public class ClientePortalController {

    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private ProyectoService proyectoService;
    
    @Autowired
    private PagoService pagoService;
    
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;
    
    @Value("${app.payment.verification-hours:24}")
    private int verificationHours;

    @GetMapping("/dashboard")  // ← Ahora es /public/cliente/dashboard
    public String dashboard(Authentication auth, Model model) {
        Cliente cliente = clienteService.findByEmail(auth.getName());
        List<Proyecto> proyectos = proyectoService.findByClienteId(cliente.getId());
        
        long proyectosActivos = proyectos.stream()
            .filter(p -> !"ENTREGADO".equals(p.getEstado()) && !"ANULADO".equals(p.getEstado()))
            .count();
        
        long proyectosCompletados = proyectos.stream()
            .filter(p -> "ENTREGADO".equals(p.getEstado()))
            .count();
        
        long proyectosPendientes = proyectos.stream()
            .filter(p -> "PENDIENTE".equals(p.getEstado()) || "EN_REVISION".equals(p.getEstado()))
            .count();
        
        BigDecimal totalCotizado = proyectos.stream()
            .filter(p -> p.getCotizacion() != null)
            .map(p -> p.getCotizacion().getTotal())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalPagado = proyectos.stream()
            .flatMap(p -> p.getPagos().stream())
            .filter(pago -> !"ANULADO".equals(pago.getEstado()) && !"RECHAZADO".equals(pago.getEstado()))
            .map(Pago::getMonto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        model.addAttribute("cliente", cliente);
        model.addAttribute("proyectosActivos", proyectosActivos);
        model.addAttribute("proyectosCompletados", proyectosCompletados);
        model.addAttribute("proyectosPendientes", proyectosPendientes);
        model.addAttribute("totalCotizado", totalCotizado);
        model.addAttribute("totalPagado", totalPagado);
        model.addAttribute("saldoPendiente", totalCotizado.subtract(totalPagado));
        model.addAttribute("ultimosProyectos", proyectos.stream()
            .sorted((p1, p2) -> p2.getFechaCreacion().compareTo(p1.getFechaCreacion()))
            .limit(5)
            .toList());
        
        return "public/cliente/dashboard";  // ← Vista correcta
    }

    @GetMapping("/proyectos/{id}")
    public String verProyecto(@PathVariable Long id, 
                              Authentication auth, 
                              Model model, 
                              RedirectAttributes redirect) {
        try {
            Proyecto proyecto = proyectoService.findById(id);
            Cliente cliente = clienteService.findByEmail(auth.getName());
            
            if (proyecto == null || !proyecto.getCliente().getId().equals(cliente.getId())) {
                redirect.addFlashAttribute("error", "Proyecto no encontrado o no autorizado.");
                return "redirect:/public/cliente/dashboard";
            }
            
            model.addAttribute("proyecto", proyecto);
            model.addAttribute("archivos", proyectoService.getArchivosByProyecto(id));
            model.addAttribute("pagos", pagoService.getPagosByProyecto(id));
            model.addAttribute("saldoPendiente", pagoService.getSaldoPendiente(proyecto));
            model.addAttribute("timeline", proyectoService.getTimeline(proyecto));
            
            return "public/cliente/proyecto-detalle";
            
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/public/cliente/dashboard";
        }
    }

    @PostMapping("/proyectos/{id}/pagos")
    public String subirComprobante(@PathVariable Long id,
                                   @RequestParam("file") MultipartFile file,
                                   @RequestParam("monto") BigDecimal monto,
                                   @RequestParam("fecha") String fecha,
                                   @RequestParam(value = "medioPago", defaultValue = "TRANSFERENCIA") String medioPago,
                                   @RequestParam(value = "numeroOperacion", required = false) String numeroOperacion,
                                   Authentication auth,
                                   RedirectAttributes redirect) {
        
        if (file.isEmpty()) {
            redirect.addFlashAttribute("error", "Selecciona un archivo.");
            return "redirect:/public/cliente/proyectos/" + id;
        }
        
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            redirect.addFlashAttribute("error", "El monto debe ser mayor a cero.");
            return "redirect:/public/cliente/proyectos/" + id;
        }
        
        try {
            Cliente cliente = clienteService.findByEmail(auth.getName());
            Proyecto proyecto = proyectoService.findById(id);
            
            if (proyecto == null || !proyecto.getCliente().getId().equals(cliente.getId())) {
                redirect.addFlashAttribute("error", "Proyecto no autorizado.");
                return "redirect:/public/cliente/dashboard";
            }
            
            BigDecimal saldoPendiente = pagoService.getSaldoPendiente(proyecto);
            if (monto.compareTo(saldoPendiente) > 0) {
                redirect.addFlashAttribute("error", "El monto excede el saldo pendiente (S/ " + saldoPendiente + ")");
                return "redirect:/public/cliente/proyectos/" + id;
            }
            
            String filename = System.currentTimeMillis() + "_" + sanitizeFilename(file.getOriginalFilename());
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            file.transferTo(uploadPath.resolve(filename).toFile());
            
            Pago pago = new Pago();
            pago.setProyecto(proyecto);
            pago.setMonto(monto);
            pago.setFecha(LocalDate.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE));
            pago.setMedioPago(medioPago);
            pago.setNumeroOperacion(numeroOperacion);
            pago.setComprobanteNumero(filename);
            pago.setEstado(Pago.ESTADO_VERIFICACION);
            pago.setFechaRegistro(LocalDate.now());
            pagoService.save(pago);
            
            redirect.addFlashAttribute("success", "Comprobante subido. Verificación en " + verificationHours + " horas.");
            
        } catch (IOException e) {
            redirect.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
        }
        
        return "redirect:/public/cliente/proyectos/" + id;
    }

    @GetMapping("/historial-pagos")  // ← Cambiado para coincidir con tu HTML
    public String historialPagos(Authentication auth, Model model) {
        Cliente cliente = clienteService.findByEmail(auth.getName());
        List<Pago> pagos = pagoService.findByClienteId(cliente.getId());
        
        model.addAttribute("cliente", cliente);
        model.addAttribute("pagos", pagos);
        model.addAttribute("totalPagado", pagos.stream()
            .filter(p -> "VERIFICADO".equals(p.getEstado()))
            .map(Pago::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add));
        model.addAttribute("totalPendiente", pagos.stream()
            .filter(p -> "VERIFICACION".equals(p.getEstado()))
            .map(Pago::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add));
        
        return "public/cliente/historial-pagos";
    }

    @GetMapping("/perfil")
    public String perfil(Authentication auth, Model model) {
        model.addAttribute("cliente", clienteService.findByEmail(auth.getName()));
        return "public/cliente/perfil";
    }

    @PostMapping("/perfil")
    public String actualizarPerfil(@ModelAttribute Cliente clienteActualizado,
                                   Authentication auth,
                                   RedirectAttributes redirect) {
        try {
            Cliente cliente = clienteService.findByEmail(auth.getName());
            
            if (clienteActualizado.getTelefono() != null && !clienteActualizado.getTelefono().isEmpty()) 
                cliente.setTelefono(clienteActualizado.getTelefono());
            if (clienteActualizado.getDireccion() != null) 
                cliente.setDireccion(clienteActualizado.getDireccion());
            if (clienteActualizado.getCiudad() != null) 
                cliente.setCiudad(clienteActualizado.getCiudad());
            if (clienteActualizado.getDepartamento() != null) 
                cliente.setDepartamento(clienteActualizado.getDepartamento());
            if (clienteActualizado.getNombreContacto() != null) 
                cliente.setNombreContacto(clienteActualizado.getNombreContacto());
            
            clienteService.save(cliente);
            redirect.addFlashAttribute("success", "Perfil actualizado.");
            
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        
        return "redirect:/public/cliente/perfil";
    }

    private String sanitizeFilename(String filename) {
        return filename == null ? "archivo.pdf" : filename.replaceAll("[^a-zA-Z0-9.-]", "_");
    }
}