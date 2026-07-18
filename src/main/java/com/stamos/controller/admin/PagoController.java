package com.stamos.controller.admin;

import com.stamos.annotation.Auditable;
import com.stamos.controller.BaseController;
import com.stamos.dto.PagoDTO;
import com.stamos.model.ComprobantePago;
import com.stamos.model.Pago;
import com.stamos.model.Proyecto;
import com.stamos.model.Usuario;
import com.stamos.model.Cliente;
import com.stamos.service.ClienteService;
import com.stamos.service.NotificacionService;
import com.stamos.service.PagoService;
import com.stamos.service.ProyectoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/pagos")
@PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
public class PagoController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(PagoController.class);

    @Autowired
    private PagoService pagoService;

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private NotificacionService notificacionService;

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
            
            List<PagoDTO> pagosDTO = new ArrayList<>();
            for (Pago pago : pagosPage.getContent()) {
                Proyecto proyecto = proyectoService.findById(pago.getIdProyecto());
                Usuario usuarioRegistro = clienteService.findUsuarioById(pago.getIdUsuarioRegistro());
                Usuario usuarioValidacion = clienteService.findUsuarioById(pago.getIdUsuarioValidacion());
                ComprobantePago comprobante = pagoService.findComprobanteByPago(pago.getIdPago());
                
                PagoDTO dto = PagoDTO.fromEntityWithComprobante(pago, proyecto, usuarioRegistro, usuarioValidacion, comprobante);
                pagosDTO.add(dto);
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
            
            model.addAttribute("pagos", pagosDTO);
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
            addCommonAttributes(model);
            
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
            addCommonAttributes(model);
            
            return "admin/pagos/registrar";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "redirect:/admin/pagos";
        }
    }

    @PostMapping("/registrar")
    @Auditable
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

            if (!proyecto.puedeRecibirPagos()) {
                redirectAttributes.addFlashAttribute("error", 
                    "El proyecto no puede recibir pagos en su estado actual: " + proyecto.getEstado());
                return "redirect:/admin/pagos/registrar?proyectoId=" + pago.getIdProyecto();
            }

            if (pago.getMonto() == null || pago.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
                redirectAttributes.addFlashAttribute("error", "El monto debe ser mayor a cero.");
                return "redirect:/admin/pagos/registrar?proyectoId=" + pago.getIdProyecto();
            }

            BigDecimal totalPagado = pagoService.getTotalPagadoByProyecto(pago.getIdProyecto());
            if (totalPagado == null) {
                totalPagado = BigDecimal.ZERO;
            }
            
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
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
                return "redirect:/admin/pagos/registrar?proyectoId=" + pago.getIdProyecto();
            }
            
            pago.setFechaPago(LocalDateTime.now());
            pago.setEstadoPago("Pendiente");
            
            pagoService.registrarPago(pago, comprobantePago, usuario.getIdUsuario());

            try {
                notificarNuevoPago(proyecto, pago, usuario);
            } catch (Exception e) {
                log.error("Error al enviar notificación de pago registrado: {}", e.getMessage());
            }

            redirectAttributes.addFlashAttribute("mensaje", 
                "Pago registrado exitosamente. Pendiente de validación.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/admin/pagos";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar el pago: " + e.getMessage());
            return "redirect:/admin/pagos/registrar?proyectoId=" + pago.getIdProyecto();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar el pago: " + e.getMessage());
            return "redirect:/admin/pagos/registrar?proyectoId=" + pago.getIdProyecto();
        }
    }

    @PostMapping("/validar/{id}")
    @Auditable
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
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
                return "redirect:/admin/pagos";
            }
            
            Proyecto proyecto = proyectoService.findById(pago.getIdProyecto());
            String proyectoNombre = proyecto != null ? proyecto.getNombreProyecto() : "Proyecto";
            BigDecimal monto = pago.getMonto();
            String montoFormateado = formatearMoneda(monto);
            
            pagoService.validarPago(id, usuario.getIdUsuario());

            try {
                if (proyecto != null) {
                    Cliente cliente = clienteService.findById(proyecto.getIdCliente());
                    if (cliente != null && cliente.getIdUsuario() != null) {
                        notificacionService.notificarPagoValidado(
                            cliente.getIdUsuario(),
                            proyectoNombre,
                            montoFormateado,
                            id
                        );
                        log.info("✅ Notificación enviada al cliente: {} - Pago validado: S/{} - Proyecto: {}",
                            cliente.getEmail(), monto, proyectoNombre);
                    } else {
                        log.warn("⚠️ No se encontró cliente para notificar pago validado");
                    }
                }
            } catch (Exception e) {
                log.error("Error al enviar notificación de pago validado: {}", e.getMessage());
            }

            redirectAttributes.addFlashAttribute("mensaje", 
                "Pago validado exitosamente.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/admin/pagos";
        } catch (Exception e) {
            log.error("Error al validar el pago: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al validar el pago: " + e.getMessage());
            return "redirect:/admin/pagos";
        }
    }

    @PostMapping("/rechazar/{id}")
    @Auditable
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
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
                return "redirect:/admin/pagos";
            }
            
            Proyecto proyecto = proyectoService.findById(pago.getIdProyecto());
            String proyectoNombre = proyecto != null ? proyecto.getNombreProyecto() : "Proyecto";
            BigDecimal monto = pago.getMonto();
            String montoFormateado = formatearMoneda(monto);
            String motivoRechazo = (motivo != null && !motivo.isEmpty()) ? motivo : "Sin motivo especificado";
            
            pagoService.rechazarPago(id, motivoRechazo, usuario.getIdUsuario());

            try {
                if (proyecto != null) {
                    Cliente cliente = clienteService.findById(proyecto.getIdCliente());
                    if (cliente != null && cliente.getIdUsuario() != null) {
                        notificacionService.notificarPagoRechazado(
                            cliente.getIdUsuario(),
                            proyectoNombre,
                            montoFormateado,
                            motivoRechazo,
                            id
                        );
                        log.info("✅ Notificación enviada al cliente: {} - Pago rechazado: S/{} - Proyecto: {} - Motivo: {}",
                            cliente.getEmail(), monto, proyectoNombre, motivoRechazo);
                    } else {
                        log.warn("⚠️ No se encontró cliente para notificar pago rechazado");
                    }
                }
            } catch (Exception e) {
                log.error("Error al enviar notificación de pago rechazado: {}", e.getMessage());
            }

            redirectAttributes.addFlashAttribute("mensaje", 
                "Pago rechazado exitosamente.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/admin/pagos";
        } catch (Exception e) {
            log.error("Error al rechazar el pago: {}", e.getMessage());
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
            
            Proyecto proyecto = proyectoService.findById(pago.getIdProyecto());
            Usuario usuarioRegistro = clienteService.findUsuarioById(pago.getIdUsuarioRegistro());
            Usuario usuarioValidacion = clienteService.findUsuarioById(pago.getIdUsuarioValidacion());
            ComprobantePago comprobante = pagoService.findComprobanteByPago(pago.getIdPago());
            
            PagoDTO pagoDTO = PagoDTO.fromEntityWithComprobante(pago, proyecto, usuarioRegistro, usuarioValidacion, comprobante);
            
            model.addAttribute("pago", pagoDTO);
            model.addAttribute("comprobante", comprobante);
            addCommonAttributes(model);
            
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
            
            List<ResumenProyectoVencido> resumenes = new ArrayList<>();
            for (Proyecto proyecto : proyectosVencidos) {
                BigDecimal totalPagado = pagoService.getTotalPagadoByProyecto(proyecto.getIdProyecto());
                BigDecimal saldoPendiente = proyecto.getMontoTotal().subtract(totalPagado);
                int diasVencidos = 0;
                if (proyecto.getFechaVencimiento() != null) {
                    diasVencidos = (int) java.time.temporal.ChronoUnit.DAYS.between(
                        proyecto.getFechaVencimiento(), LocalDate.now());
                }
                
                ResumenProyectoVencido resumen = new ResumenProyectoVencido();
                resumen.setIdProyecto(proyecto.getIdProyecto());
                resumen.setNombreProyecto(proyecto.getNombreProyecto());
                resumen.setMontoTotal(proyecto.getMontoTotal());
                resumen.setTotalPagado(totalPagado);
                resumen.setSaldoPendiente(saldoPendiente);
                resumen.setFechaVencimiento(proyecto.getFechaVencimiento());
                resumen.setDiasVencidos(Math.max(0, diasVencidos));
                resumenes.add(resumen);
            }
            
            model.addAttribute("proyectosVencidos", resumenes);
            model.addAttribute("totalVencidos", resumenes.size());
            addCommonAttributes(model);
            
            return "admin/pagos/vencidos";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar pagos vencidos: " + e.getMessage());
            return "admin/pagos/vencidos";
        }
    }

    private void notificarNuevoPago(Proyecto proyecto, Pago pago, Usuario usuario) {
        try {
            List<Usuario> administradores = clienteService.findUsuariosByRol("Administrador");
            if (administradores.isEmpty()) {
                log.warn("⚠️ No se encontró ningún administrador para notificar el nuevo pago");
                return;
            }

            String montoFormateado = formatearMoneda(pago.getMonto());
            String comprobanteInfo = pago.getMetodoPago() + " - " + montoFormateado;

            for (Usuario admin : administradores) {
                notificacionService.notificarPagoRegistrado(
                    admin.getIdUsuario(),
                    proyecto.getNombreProyecto(),
                    montoFormateado,
                    comprobanteInfo,
                    pago.getIdPago()
                );
                log.info("✅ Notificación enviada al administrador: {} - Proyecto: {}, Monto: S/{}",
                    admin.getEmail(), proyecto.getNombreProyecto(), pago.getMonto());
            }
        } catch (Exception e) {
            log.error("Error al enviar notificación de pago registrado: {}", e.getMessage());
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

    public String getEstadoLegible(String estado) {
        if (estado == null) return "Sin estado";
        return switch (estado) {
            case "Pendiente" -> "Pendiente de validación";
            case "Validado" -> "Validado";
            case "Rechazado" -> "Rechazado";
            default -> estado;
        };
    }

    @Override
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

    @Override
    public String formatearMoneda(BigDecimal monto) {
        if (monto == null) return "S/ 0.00";
        return "S/ " + monto.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public static class ResumenProyectoVencido {
        private Integer idProyecto;
        private String nombreProyecto;
        private BigDecimal montoTotal;
        private BigDecimal totalPagado;
        private BigDecimal saldoPendiente;
        private LocalDate fechaVencimiento;
        private Integer diasVencidos;

        public Integer getIdProyecto() { return idProyecto; }
        public void setIdProyecto(Integer idProyecto) { this.idProyecto = idProyecto; }

        public String getNombreProyecto() { return nombreProyecto; }
        public void setNombreProyecto(String nombreProyecto) { this.nombreProyecto = nombreProyecto; }

        public BigDecimal getMontoTotal() { return montoTotal; }
        public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }

        public BigDecimal getTotalPagado() { return totalPagado; }
        public void setTotalPagado(BigDecimal totalPagado) { this.totalPagado = totalPagado; }

        public BigDecimal getSaldoPendiente() { return saldoPendiente; }
        public void setSaldoPendiente(BigDecimal saldoPendiente) { this.saldoPendiente = saldoPendiente; }

        public LocalDate getFechaVencimiento() { return fechaVencimiento; }
        public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

        public Integer getDiasVencidos() { return diasVencidos; }
        public void setDiasVencidos(Integer diasVencidos) { this.diasVencidos = diasVencidos; }
    }
}