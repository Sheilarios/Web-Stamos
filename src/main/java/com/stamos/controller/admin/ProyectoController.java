package com.stamos.controller.admin;

import com.stamos.annotation.Auditable;
import com.stamos.model.*;
import com.stamos.repository.EstadoProyectoRepository;
import com.stamos.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/proyectos")
@PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'DISEÑADOR', 'PRODUCCION', 'SOPORTE_TECNICO')")
public class ProyectoController {

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private CotizacionService cotizacionService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PagoService pagoService;

    @Autowired
    private CambioEstadoProyectoService cambioEstadoService;

    @Autowired
    private EstadoProyectoRepository estadoProyectoRepository;

    @GetMapping
    public String listarProyectos(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(required = false) String estado,
                                  @RequestParam(required = false) Integer clienteId,
                                  @RequestParam(required = false) String search,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                                  Model model) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("idProyecto").descending());
            
            Page<Proyecto> proyectosPage;
            if (estado != null && !estado.isEmpty()) {
                proyectosPage = proyectoService.findByEstado(estado, pageable);
            } else if (clienteId != null) {
                proyectosPage = proyectoService.findByCliente(clienteId, pageable);
            } else if (search != null && !search.isEmpty()) {
                proyectosPage = proyectoService.buscar(search, pageable);
            } else if (fechaInicio != null && fechaFin != null) {
                proyectosPage = proyectoService.findByFechaBetween(fechaInicio, fechaFin, pageable);
            } else {
                proyectosPage = proyectoService.findAll(pageable);
            }
            
            for (Proyecto proyecto : proyectosPage.getContent()) {
                BigDecimal totalPagado = pagoService.getTotalPagadoByProyecto(proyecto.getIdProyecto());
                proyecto.setTotalPagado(totalPagado != null ? totalPagado : BigDecimal.ZERO);
                BigDecimal saldoPendiente = proyecto.getMontoTotal().subtract(proyecto.getTotalPagado());
                if (saldoPendiente.compareTo(BigDecimal.ZERO) < 0) {
                    saldoPendiente = BigDecimal.ZERO;
                }
                proyecto.setSaldoPendiente(saldoPendiente);
            }
            
            long totalProyectos = proyectoService.count();
            long totalActivos = proyectoService.countByEstadoNotIn(Arrays.asList("Entregado", "Anulado"));
            long totalEntregados = proyectoService.countByEstado("Entregado");
            long totalAnulados = proyectoService.countByEstado("Anulado");
            
            List<EstadoProyecto> estados = estadoProyectoRepository.findAllOrderByOrdenFlujo();
            
            model.addAttribute("proyectos", proyectosPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", proyectosPage.getTotalPages());
            model.addAttribute("totalItems", proyectosPage.getTotalElements());
            model.addAttribute("estado", estado);
            model.addAttribute("clienteId", clienteId);
            model.addAttribute("search", search);
            model.addAttribute("fechaInicio", fechaInicio);
            model.addAttribute("fechaFin", fechaFin);
            model.addAttribute("clientes", clienteService.findAll());
            model.addAttribute("estados", estados);
            model.addAttribute("totalProyectos", totalProyectos);
            model.addAttribute("totalActivos", totalActivos);
            model.addAttribute("totalEntregados", totalEntregados);
            model.addAttribute("totalAnulados", totalAnulados);
            
            return "admin/proyectos/lista";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar los proyectos: " + e.getMessage());
            return "admin/proyectos/lista";
        }
    }

    @GetMapping("/detalle/{id}")
    public String verDetalle(@PathVariable Integer id, Model model) {
        try {
            Proyecto proyecto = proyectoService.findById(id);
            if (proyecto == null) {
                model.addAttribute("error", "Proyecto no encontrado");
                return "redirect:/admin/proyectos";
            }
            
            Cotizacion cotizacion = cotizacionService.findById(proyecto.getIdCotizacion());
            List<CotizacionDetalle> detallesCotizacion = cotizacionService.getDetallesByCotizacion(proyecto.getIdCotizacion());
            List<Pago> pagos = pagoService.findByProyecto(id);
            List<CambioEstadoProyecto> historialEstados = cambioEstadoService.findByProyecto(id);
            
            BigDecimal totalPagado = pagoService.getTotalPagadoByProyecto(id);
            if (totalPagado == null) {
                totalPagado = BigDecimal.ZERO;
            }
            
            BigDecimal saldoPendiente = proyecto.getMontoTotal().subtract(totalPagado);
            if (saldoPendiente.compareTo(BigDecimal.ZERO) < 0) {
                saldoPendiente = BigDecimal.ZERO;
            }
            
            int porcentajePagado = 0;
            if (proyecto.getMontoTotal().compareTo(BigDecimal.ZERO) > 0) {
                porcentajePagado = totalPagado.multiply(new BigDecimal(100))
                        .divide(proyecto.getMontoTotal(), 0, RoundingMode.HALF_UP)
                        .intValue();
                if (porcentajePagado > 100) {
                    porcentajePagado = 100;
                }
            }
            
            List<EstadoProyecto> estadosDisponibles = estadoProyectoRepository.findEstadosDisponibles(
                proyecto.getIdEstadoActual()
            );
            
            model.addAttribute("proyecto", proyecto);
            model.addAttribute("cotizacion", cotizacion);
            model.addAttribute("detallesCotizacion", detallesCotizacion);
            model.addAttribute("pagos", pagos);
            model.addAttribute("historialEstados", historialEstados);
            model.addAttribute("totalPagado", totalPagado);
            model.addAttribute("saldoPendiente", saldoPendiente);
            model.addAttribute("porcentajePagado", porcentajePagado);
            model.addAttribute("estadosDisponibles", estadosDisponibles);
            
            return "admin/proyectos/detalle";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el detalle: " + e.getMessage());
            return "redirect:/admin/proyectos";
        }
    }

    @PostMapping("/cambiar-estado/{id}")
    @Auditable(value = "Cambio de estado de proyecto", accion = "STATE_CHANGE")
    public String cambiarEstado(@PathVariable Integer id,
                                @RequestParam Integer idEstadoNuevo,
                                @RequestParam(required = false) String comentario,
                                Authentication auth,
                                RedirectAttributes redirectAttributes) {
        try {
            Proyecto proyecto = proyectoService.findById(id);
            if (proyecto == null) {
                redirectAttributes.addFlashAttribute("error", "Proyecto no encontrado.");
                return "redirect:/admin/proyectos";
            }

            if (proyecto.getIdEstadoActual().equals(idEstadoNuevo)) {
                redirectAttributes.addFlashAttribute("error", 
                    "El proyecto ya se encuentra en ese estado.");
                return "redirect:/admin/proyectos/detalle/" + id;
            }

            String estadoActual = proyecto.getEstado();
            if (estadoActual == null) {
                estadoActual = "";
            }
            
            if (estadoActual.equals("Entregado") || estadoActual.equals("Anulado")) {
                redirectAttributes.addFlashAttribute("error", 
                    "No se puede cambiar el estado de un proyecto " + estadoActual.toLowerCase() + ".");
                return "redirect:/admin/proyectos/detalle/" + id;
            }

            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
                return "redirect:/admin/proyectos/detalle/" + id;
            }

            String rol = usuario.getNombreRol();
            if (rol == null) {
                rol = "USER";
            }
            
            boolean puedeTransicionar = validarTransicionPorRol(rol, estadoActual, idEstadoNuevo);
            if (!puedeTransicionar) {
                redirectAttributes.addFlashAttribute("error", 
                    "No tienes permisos para realizar esta transición de estado.");
                return "redirect:/admin/proyectos/detalle/" + id;
            }

            Proyecto proyectoActualizado = proyectoService.cambiarEstado(
                id, idEstadoNuevo, usuario.getIdUsuario(), comentario
            );

            redirectAttributes.addFlashAttribute("mensaje", 
                "Estado del proyecto cambiado exitosamente de '" + estadoActual + 
                "' a '" + proyectoActualizado.getEstado() + "'.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/admin/proyectos/detalle/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar el estado: " + e.getMessage());
            return "redirect:/admin/proyectos/detalle/" + id;
        }
    }

    @GetMapping("/cambiar-estado/{id}")
    public String mostrarFormularioCambiarEstado(@PathVariable Integer id, Model model) {
        try {
            Proyecto proyecto = proyectoService.findById(id);
            if (proyecto == null) {
                model.addAttribute("error", "Proyecto no encontrado");
                return "redirect:/admin/proyectos";
            }

            List<EstadoProyecto> estadosDisponibles = estadoProyectoRepository.findEstadosDisponibles(
                proyecto.getIdEstadoActual()
            );

            model.addAttribute("proyecto", proyecto);
            model.addAttribute("estadosDisponibles", estadosDisponibles);
            
            return "admin/proyectos/cambiar_estado";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "redirect:/admin/proyectos";
        }
    }

    private boolean validarTransicionPorRol(String rol, String estadoActual, Integer idEstadoNuevo) {
        EstadoProyecto estadoNuevo = estadoProyectoRepository.findById(idEstadoNuevo).orElse(null);
        if (estadoNuevo == null) {
            return false;
        }
        String estadoNuevoNombre = estadoNuevo.getNombreEstado();

        if (estadoNuevoNombre.equals("Anulado")) {
            return "Administrador".equals(rol);
        }

        if (estadoNuevoNombre.equals("Entregado")) {
            return "Administrador".equals(rol) || "Soporte Técnico".equals(rol);
        }

        if ("Vendedor".equals(rol)) {
            return "Aprobado".equals(estadoActual) && "En Diseño".equals(estadoNuevoNombre);
        }

        if ("Diseñador".equals(rol)) {
            return "En Diseño".equals(estadoActual) && "En Producción".equals(estadoNuevoNombre);
        }

        if ("Producción".equals(rol)) {
            return "En Producción".equals(estadoActual) && "Instalación/Montaje".equals(estadoNuevoNombre);
        }

        return "Administrador".equals(rol);
    }

    @GetMapping("/estado/{estado}")
    @ResponseBody
    public List<Proyecto> getProyectosByEstado(@PathVariable String estado) {
        try {
            return proyectoService.findByEstado(estado);
        } catch (Exception e) {
            return List.of();
        }
    }

    @GetMapping("/cliente/{clienteId}")
    @ResponseBody
    public List<Proyecto> getProyectosByCliente(@PathVariable Integer clienteId) {
        try {
            return proyectoService.findByCliente(clienteId);
        } catch (Exception e) {
            return List.of();
        }
    }

    @GetMapping("/resumen-financiero/{id}")
    @ResponseBody
    public Map<String, Object> getResumenFinanciero(@PathVariable Integer id) {
        try {
            Proyecto proyecto = proyectoService.findById(id);
            if (proyecto == null) {
                return Map.of("error", "Proyecto no encontrado");
            }
            
            BigDecimal totalPagado = pagoService.getTotalPagadoByProyecto(id);
            if (totalPagado == null) {
                totalPagado = BigDecimal.ZERO;
            }
            
            BigDecimal saldoPendiente = proyecto.getMontoTotal().subtract(totalPagado);
            if (saldoPendiente.compareTo(BigDecimal.ZERO) < 0) {
                saldoPendiente = BigDecimal.ZERO;
            }
            
            int porcentajePagado = 0;
            if (proyecto.getMontoTotal().compareTo(BigDecimal.ZERO) > 0) {
                porcentajePagado = totalPagado.multiply(new BigDecimal(100))
                        .divide(proyecto.getMontoTotal(), 0, RoundingMode.HALF_UP)
                        .intValue();
                if (porcentajePagado > 100) {
                    porcentajePagado = 100;
                }
            }
            
            return Map.of(
                "idProyecto", id,
                "nombreProyecto", proyecto.getNombreProyecto(),
                "montoTotal", proyecto.getMontoTotal(),
                "totalPagado", totalPagado,
                "saldoPendiente", saldoPendiente,
                "porcentajePagado", porcentajePagado
            );
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }

    @GetMapping("/historial-estados/{id}")
    @ResponseBody
    public List<CambioEstadoProyecto> getHistorialEstados(@PathVariable Integer id) {
        try {
            return cambioEstadoService.findByProyecto(id);
        } catch (Exception e) {
            return List.of();
        }
    }

    public String getEstadoLegible(String estado) {
        if (estado == null) return "Sin estado";
        return switch (estado) {
            case "Aprobado" -> "Aprobado";
            case "En Diseño" -> "En Diseño";
            case "En Producción" -> "En Producción";
            case "Instalación/Montaje" -> "Instalación/Montaje";
            case "Entregado" -> "Entregado";
            case "Anulado" -> "Anulado";
            default -> estado;
        };
    }

    public String getEstadoBadgeClass(String estado) {
        if (estado == null) return "status-pendiente";
        return switch (estado) {
            case "Aprobado" -> "status-aprobado";
            case "En Diseño" -> "status-diseno";
            case "En Producción" -> "status-produccion";
            case "Instalación/Montaje" -> "status-instalacion";
            case "Entregado" -> "status-entregado";
            case "Anulado" -> "status-anulado";
            default -> "status-pendiente";
        };
    }

    public String getEstadoIcono(String estado) {
        if (estado == null) return "ti ti-circle";
        return switch (estado) {
            case "Aprobado" -> "ti ti-check-circle";
            case "En Diseño" -> "ti ti-pencil";
            case "En Producción" -> "ti ti-tools";
            case "Instalación/Montaje" -> "ti ti-truck";
            case "Entregado" -> "ti ti-check";
            case "Anulado" -> "ti ti-x-circle";
            default -> "ti ti-circle";
        };
    }

    public String getEstadoColor(String estado) {
        if (estado == null) return "#6B7280";
        return switch (estado) {
            case "Aprobado" -> "#1A56DB";
            case "En Diseño" -> "#F59E0B";
            case "En Producción" -> "#3B82F6";
            case "Instalación/Montaje" -> "#8B5CF6";
            case "Entregado" -> "#10B981";
            case "Anulado" -> "#EF4444";
            default -> "#6B7280";
        };
    }

    public String formatearMoneda(BigDecimal monto) {
        if (monto == null) return "S/ 0.00";
        return "S/ " + monto.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public String formatearFecha(LocalDate fecha) {
        if (fecha == null) return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return fecha.format(formatter);
    }

    public String formatearFechaHora(LocalDateTime fecha) {
        if (fecha == null) return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fecha.format(formatter);
    }
}