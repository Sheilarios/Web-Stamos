package com.stamos.controller.admin;

import com.stamos.annotation.Auditable;
import com.stamos.controller.BaseController;
import com.stamos.dto.TimelinePasoDTO;
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
import java.util.*;

@Controller
@RequestMapping("/admin/proyectos")
@PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'DISEÑADOR', 'PRODUCCION', 'SOPORTE_TECNICO')")
public class ProyectoController extends BaseController {

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
                proyecto.setEstado(getEstadoNombrePorId(proyecto.getIdEstadoActual()));
            }
            
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
            model.addAttribute("estados", estadoProyectoRepository.findAllOrderByOrdenFlujo());
            model.addAttribute("totalProyectos", proyectoService.count());
            model.addAttribute("totalActivos", proyectoService.countByEstadoNotIn(Arrays.asList("Entregado", "Anulado")));
            model.addAttribute("totalEntregados", proyectoService.countByEstado("Entregado"));
            model.addAttribute("totalAnulados", proyectoService.countByEstado("Anulado"));
            addCommonAttributes(model);
            
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

            List<TimelinePasoDTO> pasosTimeline = new ArrayList<>();
            Integer progresoPorcentaje = 0;
            
            if (proyecto != null) {
                List<CambioEstadoProyecto> cambios = cambioEstadoService.findByProyecto(proyecto.getIdProyecto());
                
                List<String> ordenEstados = Arrays.asList("Aprobado", "En Diseño", "En Producción", "Instalación/Montaje", "Entregado");
                Map<String, Integer> ordenMap = new HashMap<>();
                for (int i = 0; i < ordenEstados.size(); i++) {
                    ordenMap.put(ordenEstados.get(i), i + 1);
                }
                
                Map<String, Object[]> estadoInfo = new HashMap<>();
                estadoInfo.put("Aprobado", new Object[]{"bi bi-check-circle", "Proyecto aprobado e iniciado"});
                estadoInfo.put("En Diseño", new Object[]{"bi bi-pencil", "Diseño 3D y planos en desarrollo"});
                estadoInfo.put("En Producción", new Object[]{"bi bi-tools", "Fabricación de componentes"});
                estadoInfo.put("Instalación/Montaje", new Object[]{"bi bi-truck", "Instalación en sitio del cliente"});
                estadoInfo.put("Entregado", new Object[]{"bi bi-package", "Proyecto finalizado y entregado"});
                
                String estadoActual = getEstadoNombrePorId(proyecto.getIdEstadoActual());
                int indiceActual = ordenMap.getOrDefault(estadoActual, 1);
                
                for (String estado : ordenEstados) {
                    String titulo = estado;
                    String icono = (String) estadoInfo.get(estado)[0];
                    String descripcion = (String) estadoInfo.get(estado)[1];
                    int indiceEstado = ordenMap.get(estado);
                    
                    String estadoPaso;
                    String estadoTexto;
                    LocalDate fecha = null;
                    
                    if (cambios != null && !cambios.isEmpty()) {
                        for (CambioEstadoProyecto cambio : cambios) {
                            String nombreEstadoNuevo = cambio.getEstadoNuevoNombre();
                            if (nombreEstadoNuevo != null && nombreEstadoNuevo.equals(estado)) {
                                fecha = cambio.getFechaCambio().toLocalDate();
                                break;
                            }
                        }
                    }
                    
                    if (indiceEstado < indiceActual) {
                        estadoPaso = "COMPLETADO";
                        estadoTexto = "Completado";
                    } else if (indiceEstado == indiceActual) {
                        estadoPaso = "ACTUAL";
                        estadoTexto = "En progreso";
                    } else {
                        estadoPaso = "PENDIENTE";
                        estadoTexto = "Pendiente";
                    }
                    
                    if (estado.equals("Aprobado") && fecha == null && proyecto.getFechaInicio() != null) {
                        fecha = proyecto.getFechaInicio();
                    }
                    
                    pasosTimeline.add(new TimelinePasoDTO(
                            titulo,
                            descripcion,
                            estadoPaso,
                            estadoTexto,
                            icono,
                            fecha,
                            indiceEstado
                    ));
                }
                
                progresoPorcentaje = (int) Math.round(((double) indiceActual / ordenEstados.size()) * 100);
                if (progresoPorcentaje > 100) progresoPorcentaje = 100;
            }
            

            model.addAttribute("proyecto", proyecto);
            model.addAttribute("cotizacion", cotizacion);
            model.addAttribute("detallesCotizacion", detallesCotizacion);
            model.addAttribute("pagos", pagos);
            model.addAttribute("historialEstados", historialEstados);
            model.addAttribute("totalPagado", totalPagado);
            model.addAttribute("saldoPendiente", saldoPendiente);
            model.addAttribute("porcentajePagado", porcentajePagado);
            
            model.addAttribute("pasosTimeline", pasosTimeline);
            model.addAttribute("progresoPorcentaje", progresoPorcentaje);
            model.addAttribute("proyectoActivo", proyecto);
            
            model.addAttribute("estadosDisponibles", 
                estadoProyectoRepository.findEstadosDisponibles(proyecto.getIdEstadoActual()));
            addCommonAttributes(model);
            
            return "admin/proyectos/detalle";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el detalle: " + e.getMessage());
            return "redirect:/admin/proyectos";
        }
    }

    @PostMapping("/cambiar-estado/{id}")
    @Auditable
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
                redirectAttributes.addFlashAttribute("error", "El proyecto ya se encuentra en ese estado.");
                return "redirect:/admin/proyectos/detalle/" + id;
            }

            String estadoActual = proyecto.getEstado();
            if (estadoActual == null || estadoActual.equals("Entregado") || estadoActual.equals("Anulado")) {
                redirectAttributes.addFlashAttribute("error", 
                    "No se puede cambiar el estado de un proyecto " + (estadoActual != null ? estadoActual.toLowerCase() : "finalizado") + ".");
                return "redirect:/admin/proyectos/detalle/" + id;
            }

            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
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

            model.addAttribute("proyecto", proyecto);
            model.addAttribute("estadosDisponibles", 
                estadoProyectoRepository.findEstadosDisponibles(proyecto.getIdEstadoActual()));
            addCommonAttributes(model);
            
            return "admin/proyectos/cambiar_estado";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "redirect:/admin/proyectos";
        }
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

    public String getEstadoNombrePorId(Integer idEstado) {
        if (idEstado == null) return "Sin estado";
        switch (idEstado) {
            case 1: return "Aprobado";
            case 2: return "En Diseño";
            case 3: return "En Producción";
            case 4: return "Instalación/Montaje";
            case 5: return "Entregado";
            case 6: return "Anulado";
            default: return "Desconocido";
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

    @Override
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
        if (estado == null) return "bi bi-circle";
        return switch (estado) {
            case "Aprobado" -> "bi bi-check-circle";
            case "En Diseño" -> "bi bi-pencil";
            case "En Producción" -> "bi bi-tools";
            case "Instalación/Montaje" -> "bi bi-truck";
            case "Entregado" -> "bi bi-check";
            case "Anulado" -> "bi bi-x-circle";
            default -> "bi bi-circle";
        };
    }

    @Override
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

    @Override
    public String formatearMoneda(BigDecimal monto) {
        if (monto == null) return "S/ 0.00";
        return "S/ " + monto.setScale(2, RoundingMode.HALF_UP).toString();
    }

    @Override
    public String formatearFecha(LocalDate fecha) {
        if (fecha == null) return "-";
        return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    @Override
    public String formatearFechaHora(LocalDateTime fecha) {
        if (fecha == null) return "-";
        return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}