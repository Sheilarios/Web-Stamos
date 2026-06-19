package com.stamos.controller.admin;

import com.stamos.annotation.Auditable;
import com.stamos.model.ComprobantePago;
import com.stamos.model.Pago;
import com.stamos.model.Proyecto;
import com.stamos.model.Usuario;
import com.stamos.service.ClienteService;
import com.stamos.service.PagoService;
import com.stamos.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/pagos")
@PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private ClienteService clienteService;

    @Value("${app.upload.comprobantes.dir:./uploads/comprobantes/}")
    private String uploadDir;

    @GetMapping
    public String listarPagos(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(required = false) String estado,
                              @RequestParam(required = false) Integer proyectoId,
                              @RequestParam(required = false) String metodo,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                              Model model) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("fechaPago").descending());
            
            Page<Pago> pagosPage;
            if (estado != null && !estado.isEmpty()) {
                pagosPage = pagoService.findByEstadoPago(estado, pageable);
            } else if (proyectoId != null) {
                pagosPage = pagoService.findByProyecto(proyectoId, pageable);
            } else if (fechaInicio != null && fechaFin != null) {
                pagosPage = pagoService.findByFechaBetween(fechaInicio.atStartOfDay(), fechaFin.atTime(23, 59, 59), pageable);
            } else {
                pagosPage = pagoService.findAll(pageable);
            }
            
            for (Pago pago : pagosPage.getContent()) {
                enriquecerPago(pago);
            }
            
            long totalPendientes = pagoService.countByEstadoPago("Pendiente");
            long totalValidados = pagoService.countByEstadoPago("Validado");
            long totalRechazados = pagoService.countByEstadoPago("Rechazado");
            BigDecimal totalPagadoGeneral = pagoService.getTotalPagadoGeneral();
            BigDecimal totalFacturado = pagoService.getTotalFacturado();
            BigDecimal saldoPendienteGeneral = totalFacturado.subtract(totalPagadoGeneral);
            if (saldoPendienteGeneral.compareTo(BigDecimal.ZERO) < 0) {
                saldoPendienteGeneral = BigDecimal.ZERO;
            }
            
            List<Proyecto> proyectosVencidos = proyectoService.findProyectosConPagosVencidos();
            
            model.addAttribute("pagos", pagosPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", pagosPage.getTotalPages());
            model.addAttribute("totalItems", pagosPage.getTotalElements());
            model.addAttribute("estado", estado);
            model.addAttribute("proyectoId", proyectoId);
            model.addAttribute("metodo", metodo);
            model.addAttribute("fechaInicio", fechaInicio);
            model.addAttribute("fechaFin", fechaFin);
            model.addAttribute("proyectos", proyectoService.findAll());
            model.addAttribute("totalPendientes", totalPendientes);
            model.addAttribute("totalValidados", totalValidados);
            model.addAttribute("totalRechazados", totalRechazados);
            model.addAttribute("totalPagadoGeneral", totalPagadoGeneral);
            model.addAttribute("saldoPendienteGeneral", saldoPendienteGeneral);
            model.addAttribute("totalPagosVencidos", proyectosVencidos.size());
            model.addAttribute("proyectosVencidos", proyectosVencidos);
            
            return "admin/pagos/lista";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar los pagos: " + e.getMessage());
            return "admin/pagos/lista";
        }
    }

    @GetMapping("/registrar")
    public String mostrarFormularioRegistrar(@RequestParam(required = false) Integer proyectoId,
                                             Model model) {
        try {
            Pago pago = new Pago();
            pago.setFechaPago(LocalDateTime.now());
            pago.setEstadoPago("Pendiente");
            
            if (proyectoId != null) {
                Proyecto proyecto = proyectoService.findById(proyectoId);
                if (proyecto != null) {
                    pago.setIdProyecto(proyectoId);
                    model.addAttribute("proyectoSeleccionado", proyecto);
                    BigDecimal totalPagado = pagoService.getTotalPagadoByProyecto(proyectoId);
                    BigDecimal saldoPendiente = proyecto.getMontoTotal().subtract(totalPagado);
                    if (saldoPendiente.compareTo(BigDecimal.ZERO) < 0) {
                        saldoPendiente = BigDecimal.ZERO;
                    }
                    model.addAttribute("saldoPendiente", saldoPendiente);
                    model.addAttribute("totalPagado", totalPagado);
                }
            }
            
            model.addAttribute("pago", pago);
            model.addAttribute("proyectos", proyectoService.findAll());
            model.addAttribute("metodosPago", getMetodosPago());
            model.addAttribute("titulo", "Registrar Pago");
            
            return "admin/pagos/registrar";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "redirect:/admin/pagos";
        }
    }

    @PostMapping("/registrar")
    @Auditable(value = "Registro de pago", accion = "CREATE")
    public String registrarPago(@Valid @ModelAttribute("pago") Pago pago,
                                BindingResult result,
                                @RequestParam("comprobante") MultipartFile comprobante,
                                @RequestParam("tipoComprobante") String tipoComprobante,
                                @RequestParam("numeroComprobante") String numeroComprobante,
                                Authentication auth,
                                RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("error", "Por favor, corrige los errores.");
                return "redirect:/admin/pagos/registrar?proyectoId=" + pago.getIdProyecto();
            }

            Proyecto proyecto = proyectoService.findById(pago.getIdProyecto());
            if (proyecto == null) {
                redirectAttributes.addFlashAttribute("error", "Proyecto no encontrado.");
                return "redirect:/admin/pagos/registrar";
            }

            if (pago.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
                redirectAttributes.addFlashAttribute("error", "El monto debe ser mayor a cero.");
                return "redirect:/admin/pagos/registrar?proyectoId=" + pago.getIdProyecto();
            }

            BigDecimal totalPagado = pagoService.getTotalPagadoByProyecto(pago.getIdProyecto());
            BigDecimal nuevoTotal = totalPagado.add(pago.getMonto());
            
            if (nuevoTotal.compareTo(proyecto.getMontoTotal()) > 0) {
                BigDecimal excedente = nuevoTotal.subtract(proyecto.getMontoTotal());
                redirectAttributes.addFlashAttribute("error", 
                    "El pago excede el monto total del proyecto en S/ " + 
                    excedente.setScale(2, RoundingMode.HALF_UP));
                return "redirect:/admin/pagos/registrar?proyectoId=" + pago.getIdProyecto();
            }

            if (comprobante == null || comprobante.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Debes seleccionar un comprobante de pago.");
                return "redirect:/admin/pagos/registrar?proyectoId=" + pago.getIdProyecto();
            }

            if (comprobante.getSize() > 5 * 1024 * 1024) {
                redirectAttributes.addFlashAttribute("error", 
                    "El comprobante excede el tamaño máximo permitido (5MB).");
                return "redirect:/admin/pagos/registrar?proyectoId=" + pago.getIdProyecto();
            }

            String extension = getFileExtension(comprobante.getOriginalFilename());
            if (!isValidFileType(extension)) {
                redirectAttributes.addFlashAttribute("error", 
                    "Tipo de archivo no permitido. Formatos aceptados: JPG, PNG, PDF.");
                return "redirect:/admin/pagos/registrar?proyectoId=" + pago.getIdProyecto();
            }

            String nombreArchivo = guardarComprobante(comprobante);
            
            ComprobantePago comprobantePago = new ComprobantePago();
            comprobantePago.setTipoComprobante(tipoComprobante);
            comprobantePago.setNumeroComprobante(numeroComprobante);
            comprobantePago.setArchivoUrl("/comprobantes/" + nombreArchivo);
            comprobantePago.setValidado(false);
            
            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            pago.setFechaPago(LocalDateTime.now());
            pago.setEstadoPago("Pendiente");
            
            pagoService.registrarPago(pago, comprobantePago, usuario.getIdUsuario());

            redirectAttributes.addFlashAttribute("mensaje", 
                "Pago registrado exitosamente. Pendiente de validación.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/admin/pagos";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar el pago: " + e.getMessage());
            return "redirect:/admin/pagos/registrar?proyectoId=" + pago.getIdProyecto();
        }
    }

    @PostMapping("/validar/{id}")
    @Auditable(value = "Validación de pago", accion = "VALIDATE")
    @PreAuthorize("hasRole('ADMIN')")
    public String validarPago(@PathVariable Integer id,
                              Authentication auth,
                              RedirectAttributes redirectAttributes) {
        try {
            Pago pago = pagoService.findById(id);
            if (pago == null) {
                redirectAttributes.addFlashAttribute("error", "Pago no encontrado.");
                return "redirect:/admin/pagos";
            }

            if (!"Pendiente".equals(pago.getEstadoPago())) {
                redirectAttributes.addFlashAttribute("error", 
                    "El pago ya fue " + pago.getEstadoPago().toLowerCase() + ".");
                return "redirect:/admin/pagos";
            }

            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            pagoService.validarPago(id, usuario.getIdUsuario());

            redirectAttributes.addFlashAttribute("mensaje", 
                "Pago validado exitosamente.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/admin/pagos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al validar el pago: " + e.getMessage());
            return "redirect:/admin/pagos";
        }
    }

    @PostMapping("/rechazar/{id}")
    @Auditable(value = "Rechazo de pago", accion = "VALIDATE")
    @PreAuthorize("hasRole('ADMIN')")
    public String rechazarPago(@PathVariable Integer id,
                               @RequestParam(required = false) String motivo,
                               Authentication auth,
                               RedirectAttributes redirectAttributes) {
        try {
            Pago pago = pagoService.findById(id);
            if (pago == null) {
                redirectAttributes.addFlashAttribute("error", "Pago no encontrado.");
                return "redirect:/admin/pagos";
            }

            if (!"Pendiente".equals(pago.getEstadoPago())) {
                redirectAttributes.addFlashAttribute("error", 
                    "El pago ya fue " + pago.getEstadoPago().toLowerCase() + ".");
                return "redirect:/admin/pagos";
            }

            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            pagoService.rechazarPago(id, motivo, usuario.getIdUsuario());

            redirectAttributes.addFlashAttribute("mensaje", 
                "Pago rechazado exitosamente.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/admin/pagos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al rechazar el pago: " + e.getMessage());
            return "redirect:/admin/pagos";
        }
    }

    @GetMapping("/detalle/{id}")
    public String verDetalle(@PathVariable Integer id, Model model) {
        try {
            Pago pago = pagoService.findById(id);
            if (pago == null) {
                model.addAttribute("error", "Pago no encontrado");
                return "redirect:/admin/pagos";
            }
            
            enriquecerPago(pago);
            
            ComprobantePago comprobante = pagoService.findComprobanteByPago(id);
            
            model.addAttribute("pago", pago);
            model.addAttribute("comprobante", comprobante);
            
            return "admin/pagos/detalle";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el detalle: " + e.getMessage());
            return "redirect:/admin/pagos";
        }
    }

    @GetMapping("/comprobante/{id}")
    public ResponseEntity<Resource> verComprobante(@PathVariable Integer id) {
        try {
            ComprobantePago comprobante = pagoService.findComprobanteById(id);
            if (comprobante == null) {
                return ResponseEntity.notFound().build();
            }
            
            String urlArchivo = comprobante.getArchivoUrl();
            if (urlArchivo == null || urlArchivo.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            String fileName = urlArchivo.replace("/comprobantes/", "");
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            
            String extension = getFileExtension(comprobante.getNumeroComprobante());
            MediaType mediaType = getMediaType(extension);
            
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "inline; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/vencidos")
    public String listarPagosVencidos(Model model) {
        try {
            List<Proyecto> proyectosVencidos = proyectoService.findProyectosConPagosVencidos();
            
            for (Proyecto proyecto : proyectosVencidos) {
                BigDecimal totalPagado = pagoService.getTotalPagadoByProyecto(proyecto.getIdProyecto());
                BigDecimal saldoPendiente = proyecto.getMontoTotal().subtract(totalPagado);
                proyecto.setSaldoPendiente(saldoPendiente);
                proyecto.setDiasVencidos((int) java.time.temporal.ChronoUnit.DAYS.between(
                    proyecto.getFechaVencimiento(), LocalDate.now()));
            }
            
            model.addAttribute("proyectosVencidos", proyectosVencidos);
            model.addAttribute("totalVencidos", proyectosVencidos.size());
            
            return "admin/pagos/vencidos";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar pagos vencidos: " + e.getMessage());
            return "admin/pagos/vencidos";
        }
    }

    private String guardarComprobante(MultipartFile archivo) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String nombreOriginal = archivo.getOriginalFilename();
        String nombreUnico = UUID.randomUUID().toString() + "_" + nombreOriginal;
        Path filePath = uploadPath.resolve(nombreUnico);
        Files.copy(archivo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return nombreUnico;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isValidFileType(String extension) {
        String[] validExtensions = {"jpg", "jpeg", "png", "gif", "pdf"};
        for (String valid : validExtensions) {
            if (valid.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    private MediaType getMediaType(String extension) {
        return switch (extension) {
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "png" -> MediaType.IMAGE_PNG;
            case "gif" -> MediaType.IMAGE_GIF;
            case "pdf" -> MediaType.APPLICATION_PDF;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

    private List<String> getMetodosPago() {
        return List.of("Efectivo", "Transferencia", "Tarjeta", "Cheque");
    }

    private void enriquecerPago(Pago pago) {
        if (pago == null) return;
        
        if (pago.getNombreProyecto() == null && pago.getIdProyecto() != null) {
            Proyecto proyecto = proyectoService.findById(pago.getIdProyecto());
            if (proyecto != null) {
                pago.setNombreProyecto(proyecto.getNombreProyecto());
                pago.setCodigoProyecto(proyecto.getCodigoProyecto());
                pago.setMontoTotalProyecto(proyecto.getMontoTotal());
                pago.setNombreCliente(proyecto.getNombreCliente());
            }
        }
    }

    public String getEstadoLegible(String estado) {
        if (estado == null) return "Sin estado";
        return switch (estado) {
            case "Pendiente" -> "Pendiente de validación";
            case "Validado" -> "Validado";
            case "Rechazado" -> "Rechazado";
            default -> estado;
        };
    }

    public String getEstadoBadgeClass(String estado) {
        if (estado == null) return "status-pendiente";
        return switch (estado) {
            case "Pendiente" -> "status-pendiente";
            case "Validado" -> "status-validado";
            case "Rechazado" -> "status-rechazado";
            default -> "status-pendiente";
        };
    }

    public String getMetodoBadgeClass(String metodo) {
        if (metodo == null) return "";
        return switch (metodo) {
            case "Efectivo" -> "efectivo";
            case "Transferencia" -> "transferencia";
            case "Tarjeta" -> "tarjeta";
            case "Cheque" -> "cheque";
            default -> "";
        };
    }

    public String formatearMoneda(BigDecimal monto) {
        if (monto == null) return "S/ 0.00";
        return "S/ " + monto.setScale(2, RoundingMode.HALF_UP).toString();
    }
}