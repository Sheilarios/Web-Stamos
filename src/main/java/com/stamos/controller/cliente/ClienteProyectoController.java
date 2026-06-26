package com.stamos.controller.cliente;

import com.stamos.controller.BaseController;
import com.stamos.model.*;
import com.stamos.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cliente")
public class ClienteProyectoController extends BaseController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private CotizacionService cotizacionService;

    @Autowired
    private DocumentoService documentoService;

    @Autowired
    private PagoService pagoService;

    @Autowired
    private CambioEstadoProyectoService cambioEstadoProyectoService;

    @GetMapping("/proyectos")
    public String listarProyectos(Model model, Authentication auth) {
        try {
            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado");
            }

            Cliente cliente = clienteService.findByUsuario(usuario);
            if (cliente == null) {
                throw new RuntimeException("Cliente no encontrado");
            }

            List<Proyecto> proyectos = proyectoService.findByCliente(cliente.getIdCliente());

            for (Proyecto proyecto : proyectos) {
                BigDecimal totalPagado = pagoService.getTotalPagadoByProyecto(proyecto.getIdProyecto());
                proyecto.setTotalPagado(totalPagado != null ? totalPagado : BigDecimal.ZERO);
                
                BigDecimal saldoPendiente = proyecto.getMontoTotal().subtract(proyecto.getTotalPagado());
                if (saldoPendiente.compareTo(BigDecimal.ZERO) < 0) {
                    saldoPendiente = BigDecimal.ZERO;
                }
                proyecto.setSaldoPendiente(saldoPendiente);
                
                int porcentajePagado = 0;
                if (proyecto.getMontoTotal().compareTo(BigDecimal.ZERO) > 0) {
                    porcentajePagado = proyecto.getTotalPagado().multiply(new BigDecimal(100))
                            .divide(proyecto.getMontoTotal(), 0, RoundingMode.HALF_UP)
                            .intValue();
                    if (porcentajePagado > 100) {
                        porcentajePagado = 100;
                    }
                }
                proyecto.setPorcentajePagado(porcentajePagado);
            }

            long proyectosActivos = proyectos.stream()
                    .filter(p -> p.getEstado() != null && 
                               !p.getEstado().equals("Entregado") && 
                               !p.getEstado().equals("Anulado"))
                    .count();

            long proyectosCompletados = proyectos.stream()
                    .filter(p -> p.getEstado() != null && p.getEstado().equals("Entregado"))
                    .count();

            long proyectosAnulados = proyectos.stream()
                    .filter(p -> p.getEstado() != null && p.getEstado().equals("Anulado"))
                    .count();

            model.addAttribute("cliente", cliente);
            model.addAttribute("proyectos", proyectos);
            model.addAttribute("proyectosActivos", proyectosActivos);
            model.addAttribute("proyectosCompletados", proyectosCompletados);
            model.addAttribute("proyectosAnulados", proyectosAnulados);
            addCommonAttributes(model);

            return "cliente/mis-proyectos";

        } catch (RuntimeException e) {
            model.addAttribute("error", "Error al cargar los proyectos: " + e.getMessage());
            addCommonAttributes(model);
            return "cliente/dashboard";
        }
    }

    @GetMapping("/proyecto/{id}")
    public String verDetalleProyecto(@PathVariable("id") Integer idProyecto, 
                                     Model model, 
                                     Authentication auth) {
        try {
            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado");
            }

            Cliente cliente = clienteService.findByUsuario(usuario);
            if (cliente == null) {
                throw new RuntimeException("Cliente no encontrado");
            }

            Proyecto proyecto = proyectoService.findById(idProyecto);
            if (proyecto == null) {
                throw new RuntimeException("Proyecto no encontrado con ID: " + idProyecto);
            }

            if (!proyecto.getIdCliente().equals(cliente.getIdCliente())) {
                throw new AccessDeniedException("No tienes acceso a este proyecto");
            }

            Cotizacion cotizacion = cotizacionService.findById(proyecto.getIdCotizacion());
            if (cotizacion == null) {
                throw new RuntimeException("Cotización no encontrada para el proyecto");
            }

            List<CotizacionDetalle> detallesCotizacion = cotizacionService.getDetallesByCotizacion(proyecto.getIdCotizacion());
            List<DocumentoProyecto> documentos = documentoService.findByProyecto(idProyecto);
            List<Pago> pagos = pagoService.findByProyecto(idProyecto);
            List<CambioEstadoProyecto> historialEstados = cambioEstadoProyectoService.findByProyecto(idProyecto);

            BigDecimal totalPagado = pagoService.getTotalPagadoByProyecto(idProyecto);
            if (totalPagado == null) {
                totalPagado = BigDecimal.ZERO;
            }

            BigDecimal montoTotal = proyecto.getMontoTotal();
            if (montoTotal == null) {
                montoTotal = BigDecimal.ZERO;
            }

            BigDecimal saldoPendiente = montoTotal.subtract(totalPagado);
            if (saldoPendiente.compareTo(BigDecimal.ZERO) < 0) {
                saldoPendiente = BigDecimal.ZERO;
            }

            int porcentajePagado = 0;
            if (montoTotal.compareTo(BigDecimal.ZERO) > 0) {
                porcentajePagado = totalPagado.multiply(new BigDecimal(100))
                        .divide(montoTotal, 0, RoundingMode.HALF_UP)
                        .intValue();
                if (porcentajePagado > 100) {
                    porcentajePagado = 100;
                }
            }

            boolean tienePagosVencidos = false;
            if (proyecto.getFechaVencimiento() != null) {
                LocalDate hoy = LocalDate.now();
                LocalDate fechaVencimiento = proyecto.getFechaVencimiento();
                if (fechaVencimiento.isBefore(hoy) && saldoPendiente.compareTo(BigDecimal.ZERO) > 0) {
                    tienePagosVencidos = true;
                }
            }

            model.addAttribute("cliente", cliente);
            model.addAttribute("proyecto", proyecto);
            model.addAttribute("cotizacion", cotizacion);
            model.addAttribute("detallesCotizacion", detallesCotizacion);
            model.addAttribute("documentos", documentos);
            model.addAttribute("pagos", pagos);
            model.addAttribute("historialEstados", historialEstados);
            model.addAttribute("totalPagado", totalPagado);
            model.addAttribute("saldoPendiente", saldoPendiente);
            model.addAttribute("porcentajePagado", porcentajePagado);
            model.addAttribute("tienePagosVencidos", tienePagosVencidos);
            addCommonAttributes(model);

            return "cliente/detalle-proyecto";

        } catch (AccessDeniedException e) {
            throw e;
        } catch (RuntimeException e) {
            model.addAttribute("error", "Error al cargar el detalle del proyecto: " + e.getMessage());
            addCommonAttributes(model);
            return "cliente/proyectos";
        }
    }

    @GetMapping("/proyectos/json")
    @ResponseBody
    public List<Proyecto> getProyectosJson(@RequestParam(required = false) String estado,
                                           Authentication auth) {
        try {
            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado");
            }

            Cliente cliente = clienteService.findByUsuario(usuario);
            if (cliente == null) {
                throw new RuntimeException("Cliente no encontrado");
            }

            List<Proyecto> proyectos;
            if (estado != null && !estado.isEmpty()) {
                proyectos = proyectoService.findByClienteAndEstado(cliente.getIdCliente(), estado);
            } else {
                proyectos = proyectoService.findByCliente(cliente.getIdCliente());
            }

            for (Proyecto proyecto : proyectos) {
                BigDecimal totalPagado = pagoService.getTotalPagadoByProyecto(proyecto.getIdProyecto());
                proyecto.setTotalPagado(totalPagado != null ? totalPagado : BigDecimal.ZERO);
                
                BigDecimal saldoPendiente = proyecto.getMontoTotal().subtract(proyecto.getTotalPagado());
                if (saldoPendiente.compareTo(BigDecimal.ZERO) < 0) {
                    saldoPendiente = BigDecimal.ZERO;
                }
                proyecto.setSaldoPendiente(saldoPendiente);
            }

            return proyectos;

        } catch (RuntimeException e) {
            return List.of();
        }
    }

    @GetMapping("/proyecto/resumen/{id}")
    @ResponseBody
    public Map<String, Object> getResumenProyecto(@PathVariable("id") Integer idProyecto,
                                                  Authentication auth) {
        try {
            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado");
            }

            Cliente cliente = clienteService.findByUsuario(usuario);
            if (cliente == null) {
                throw new RuntimeException("Cliente no encontrado");
            }

            Proyecto proyecto = proyectoService.findById(idProyecto);
            if (proyecto == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Proyecto no encontrado");
                return error;
            }

            if (!proyecto.getIdCliente().equals(cliente.getIdCliente())) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "No tienes acceso a este proyecto");
                return error;
            }

            BigDecimal totalPagado = pagoService.getTotalPagadoByProyecto(idProyecto);
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

            long totalDocumentos = documentoService.countByProyecto(idProyecto);
            long totalPagos = pagoService.countByProyecto(idProyecto);

            Map<String, Object> resumen = new HashMap<>();
            resumen.put("idProyecto", proyecto.getIdProyecto());
            resumen.put("nombreProyecto", proyecto.getNombreProyecto() != null ? proyecto.getNombreProyecto() : "");
            resumen.put("estado", proyecto.getEstado() != null ? proyecto.getEstado() : "");
            resumen.put("montoTotal", proyecto.getMontoTotal() != null ? proyecto.getMontoTotal() : BigDecimal.ZERO);
            resumen.put("totalPagado", totalPagado);
            resumen.put("saldoPendiente", saldoPendiente);
            resumen.put("porcentajePagado", porcentajePagado);
            resumen.put("fechaInicio", proyecto.getFechaInicio());
            resumen.put("fechaFinEstimada", proyecto.getFechaFinEstimada());
            resumen.put("fechaEntregaReal", proyecto.getFechaEntregaReal());
            resumen.put("fechaVencimiento", proyecto.getFechaVencimiento());
            resumen.put("totalDocumentos", totalDocumentos);
            resumen.put("totalPagos", totalPagos);

            return resumen;

        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return error;
        }
    }

    @GetMapping("/proyecto/historial-estados/{id}")
    @ResponseBody
    public List<CambioEstadoProyecto> getHistorialEstados(@PathVariable("id") Integer idProyecto,
                                                          Authentication auth) {
        try {
            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado");
            }

            Cliente cliente = clienteService.findByUsuario(usuario);
            if (cliente == null) {
                throw new RuntimeException("Cliente no encontrado");
            }

            Proyecto proyecto = proyectoService.findById(idProyecto);
            if (proyecto == null || !proyecto.getIdCliente().equals(cliente.getIdCliente())) {
                throw new AccessDeniedException("No tienes acceso a este proyecto");
            }

            return cambioEstadoProyectoService.findByProyecto(idProyecto);

        } catch (AccessDeniedException e) {
            throw e;
        } catch (RuntimeException e) {
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

    public int getEstadoOrden(String estado) {
        if (estado == null) return 0;
        return switch (estado) {
            case "Aprobado" -> 1;
            case "En Diseño" -> 2;
            case "En Producción" -> 3;
            case "Instalación/Montaje" -> 4;
            case "Entregado" -> 5;
            case "Anulado" -> 6;
            default -> 0;
        };
    }

    public boolean isEstadoActual(String estado, String estadoActual) {
        if (estado == null || estadoActual == null) return false;
        return estado.equals(estadoActual);
    }

    public boolean isEstadoCompletado(String estado, String estadoActual) {
        if (estado == null || estadoActual == null) return false;
        if (estadoActual.equals("Anulado") || estadoActual.equals("Entregado")) {
            return true;
        }
        int ordenEstado = getEstadoOrden(estado);
        int ordenActual = getEstadoOrden(estadoActual);
        return ordenEstado < ordenActual;
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

    public String formatearPorcentaje(Integer porcentaje) {
        if (porcentaje == null) return "0%";
        return porcentaje + "%";
    }

    public String getPorcentajeColor(Integer porcentaje) {
        if (porcentaje == null) return "#6B7280";
        if (porcentaje >= 100) return "#10B981";
        if (porcentaje >= 50) return "#F59E0B";
        if (porcentaje > 0) return "#3B82F6";
        return "#EF4444";
    }

    public String getPorcentajeMensaje(Integer porcentaje) {
        if (porcentaje == null) return "Sin pagos";
        if (porcentaje >= 100) return "¡Completamente pagado!";
        if (porcentaje >= 75) return "Casi completado";
        if (porcentaje >= 50) return "Mitad del pago realizado";
        if (porcentaje > 0) return "Pagos iniciados";
        return "Sin pagos registrados";
    }
    
    public BigDecimal calcularSubtotal(List<CotizacionDetalle> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return detalles.stream()
                .map(CotizacionDetalle::getSubtotalLinea)
                .filter(s -> s != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularIgv(BigDecimal subtotal, BigDecimal tasaIgv) {
        if (subtotal == null || tasaIgv == null) {
            return BigDecimal.ZERO;
        }
        return subtotal.multiply(tasaIgv).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularTotal(BigDecimal subtotal, BigDecimal igv) {
        if (subtotal == null || igv == null) {
            return BigDecimal.ZERO;
        }
        return subtotal.add(igv);
    }
}