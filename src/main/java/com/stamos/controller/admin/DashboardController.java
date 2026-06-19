package com.stamos.controller.admin;

import com.stamos.model.*;
import com.stamos.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'DISEÑADOR', 'PRODUCCION', 'SOPORTE_TECNICO', 'ADMINISTRADOR')")
public class DashboardController {

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private CotizacionService cotizacionService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PagoService pagoService;

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private DocumentoService documentoService;

    @Autowired
    private CambioEstadoProyectoService cambioEstadoProyectoService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        System.out.println("=== Dashboard Admin GET ===");
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            System.out.println("Usuario no autenticado, redirigiendo a login");
            return "redirect:/login";
        }

        try {
            String email = auth.getName();
            System.out.println("Usuario autenticado: " + email);
            
            Usuario usuarioActual = usuarioService.findByEmail(email);
            if (usuarioActual == null) {
                System.out.println("Usuario no encontrado: " + email);
                return "redirect:/login";
            }
            
            String rol = usuarioActual.getNombreRol();
            if (rol == null) {
                rol = "Administrador";
            }
            System.out.println("Rol del usuario: " + rol);
            
            model.addAttribute("usuario", usuarioActual);
            model.addAttribute("rol", rol);
            model.addAttribute("nombreUsuario", usuarioActual.getNombreCompleto() != null ? 
                              usuarioActual.getNombreCompleto() : usuarioActual.getEmail());
            model.addAttribute("email", usuarioActual.getEmail());
            model.addAttribute("fechaActual", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            model.addAttribute("titulo", "Dashboard - " + rol);

            long totalClientes = 0;
            long totalProyectos = 0;
            long proyectosActivos = 0;
            long cotizacionesPendientes = 0;
            long totalEquipos = 0;
            long pagosPendientesValidacion = 0;

            try {
                totalClientes = clienteService.count();
            } catch (Exception e) {
                System.err.println("Error al contar clientes: " + e.getMessage());
            }

            try {
                totalProyectos = proyectoService.count();
            } catch (Exception e) {
                System.err.println("Error al contar proyectos: " + e.getMessage());
            }

            try {
                proyectosActivos = proyectoService.countByEstadoNotIn(Arrays.asList("Entregado", "Anulado"));
            } catch (Exception e) {
                System.err.println("Error al contar proyectos activos: " + e.getMessage());
                proyectosActivos = totalProyectos;
            }

            try {
                cotizacionesPendientes = cotizacionService.countByEstado("Pendiente");
            } catch (Exception e) {
                System.err.println("Error al contar cotizaciones pendientes: " + e.getMessage());
            }

            try {
                totalEquipos = inventarioService.count();
            } catch (Exception e) {
                System.err.println("Error al contar inventario: " + e.getMessage());
            }

            try {
                pagosPendientesValidacion = pagoService.countByEstado("Pendiente");
            } catch (Exception e) {
                System.err.println("Error al contar pagos pendientes: " + e.getMessage());
            }

            model.addAttribute("username", usuarioActual.getNombreCompleto() != null ? 
                              usuarioActual.getNombreCompleto() : usuarioActual.getEmail());
            model.addAttribute("totalClientes", totalClientes);
            model.addAttribute("totalProyectos", totalProyectos);
            model.addAttribute("proyectosActivos", proyectosActivos);
            model.addAttribute("cotizacionesPendientes", cotizacionesPendientes);
            model.addAttribute("totalEquipos", totalEquipos);
            model.addAttribute("pagosPendientesValidacion", pagosPendientesValidacion);
            model.addAttribute("ultimasCotizaciones", new ArrayList<>());
            model.addAttribute("ultimosProyectos", new ArrayList<>());

            return "admin/dashboard";

        } catch (Exception e) {
            System.err.println("Error en dashboard admin: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar el dashboard: " + e.getMessage());
            model.addAttribute("username", "Administrador");
            model.addAttribute("totalClientes", 0);
            model.addAttribute("totalProyectos", 0);
            model.addAttribute("proyectosActivos", 0);
            model.addAttribute("cotizacionesPendientes", 0);
            model.addAttribute("totalEquipos", 0);
            model.addAttribute("pagosPendientesValidacion", 0);
            model.addAttribute("ultimasCotizaciones", new ArrayList<>());
            model.addAttribute("ultimosProyectos", new ArrayList<>());
            return "admin/dashboard";
        }
    }

    @GetMapping({"", "/"})
    public String redirectToDashboard() {
        return "redirect:/admin/dashboard";
    }

    private void cargarDashboardCompleto(Model model) {
        try {
            long totalProyectos = proyectoService.count();
            long proyectosActivos = proyectoService.countByEstadoNotIn(Arrays.asList("Entregado", "Anulado"));
            long proyectosEnDiseno = proyectoService.countByEstado("En Diseño");
            long proyectosEnProduccion = proyectoService.countByEstado("En Producción");
            long proyectosInstalacion = proyectoService.countByEstado("Instalación/Montaje");
            long proyectosEntregados = proyectoService.countByEstado("Entregado");
            long proyectosAnulados = proyectoService.countByEstado("Anulado");
            
            long totalCotizaciones = cotizacionService.count();
            long cotizacionesPendientes = cotizacionService.countByEstado("Pendiente");
            long cotizacionesAprobadas = cotizacionService.countByEstado("Aprobada");
            long cotizacionesAnuladas = cotizacionService.countByEstado("Anulada");
            
            long totalClientes = clienteService.count();
            long clientesActivos = clienteService.countByEstado("Activo");
            long clientesDeshabilitados = clienteService.countByEstado("Deshabilitado");
            
            BigDecimal totalFacturado = pagoService.getTotalFacturado();
            if (totalFacturado == null) {
                totalFacturado = BigDecimal.ZERO;
            }
            BigDecimal totalPagado = pagoService.getTotalPagadoGeneral();
            if (totalPagado == null) {
                totalPagado = BigDecimal.ZERO;
            }
            BigDecimal totalPendiente = totalFacturado.subtract(totalPagado);
            if (totalPendiente.compareTo(BigDecimal.ZERO) < 0) {
                totalPendiente = BigDecimal.ZERO;
            }
            
            int porcentajeCobranza = 0;
            if (totalFacturado.compareTo(BigDecimal.ZERO) > 0) {
                porcentajeCobranza = totalPagado.multiply(new BigDecimal(100))
                        .divide(totalFacturado, 0, RoundingMode.HALF_UP)
                        .intValue();
                if (porcentajeCobranza > 100) {
                    porcentajeCobranza = 100;
                }
            }
            
            List<Proyecto> proyectosRecientes = new ArrayList<>();
            try {
                proyectosRecientes = proyectoService.findTop10ByOrderByFechaInicioDesc();
                if (proyectosRecientes == null) {
                    proyectosRecientes = new ArrayList<>();
                }
            } catch (Exception e) {
                System.err.println("Error al cargar proyectos recientes: " + e.getMessage());
                proyectosRecientes = new ArrayList<>();
            }
            
            List<Cotizacion> cotizacionesRecientes = new ArrayList<>();
            try {
                cotizacionesRecientes = cotizacionService.findTopNPendientes(10);
                if (cotizacionesRecientes == null) {
                    cotizacionesRecientes = new ArrayList<>();
                }
            } catch (Exception e) {
                System.err.println("Error al cargar cotizaciones recientes: " + e.getMessage());
                cotizacionesRecientes = new ArrayList<>();
            }
            
            Map<String, Long> proyectosPorEstado = new LinkedHashMap<>();
            proyectosPorEstado.put("En Diseño", proyectosEnDiseno);
            proyectosPorEstado.put("En Producción", proyectosEnProduccion);
            proyectosPorEstado.put("Instalación/Montaje", proyectosInstalacion);
            proyectosPorEstado.put("Entregados", proyectosEntregados);
            proyectosPorEstado.put("Anulados", proyectosAnulados);
            
            Map<String, Long> cotizacionesPorEstado = new LinkedHashMap<>();
            cotizacionesPorEstado.put("Pendientes", cotizacionesPendientes);
            cotizacionesPorEstado.put("Aprobadas", cotizacionesAprobadas);
            cotizacionesPorEstado.put("Anuladas", cotizacionesAnuladas);
            
            Map<String, Long> proyectosPorMes = getProyectosPorMes(6);
            Map<String, BigDecimal> pagosPorMes = getPagosPorMes(6);
            
            List<Proyecto> proyectosConPagosVencidos = new ArrayList<>();
            try {
                proyectosConPagosVencidos = proyectoService.findProyectosConPagosVencidos();
                if (proyectosConPagosVencidos == null) {
                    proyectosConPagosVencidos = new ArrayList<>();
                }
            } catch (Exception e) {
                System.err.println("Error al cargar pagos vencidos: " + e.getMessage());
                proyectosConPagosVencidos = new ArrayList<>();
            }
            long totalPagosVencidos = proyectosConPagosVencidos.size();
            
            List<String> alertas = new ArrayList<>();
            List<Inventario> inventarioBajo = new ArrayList<>();
            try {
                inventarioBajo = inventarioService.findInventarioBajo();
                if (inventarioBajo == null) {
                    inventarioBajo = new ArrayList<>();
                }
            } catch (Exception e) {
                System.err.println("Error al cargar inventario bajo: " + e.getMessage());
                inventarioBajo = new ArrayList<>();
            }
            if (!inventarioBajo.isEmpty()) {
                alertas.add(inventarioBajo.size() + " items de inventario estan en niveles bajos.");
            }
            if (totalPagosVencidos > 0) {
                alertas.add(totalPagosVencidos + " proyectos tienen pagos vencidos.");
            }
            
            List<Cotizacion> cotizacionesAntiguas = new ArrayList<>();
            try {
                cotizacionesAntiguas = cotizacionService.findPendientesAntiguas(7);
                if (cotizacionesAntiguas == null) {
                    cotizacionesAntiguas = new ArrayList<>();
                }
            } catch (Exception e) {
                System.err.println("Error al cargar cotizaciones antiguas: " + e.getMessage());
                cotizacionesAntiguas = new ArrayList<>();
            }
            if (!cotizacionesAntiguas.isEmpty()) {
                alertas.add(cotizacionesAntiguas.size() + " cotizaciones pendientes tienen mas de 7 dias.");
            }
            
            model.addAttribute("totalProyectos", totalProyectos);
            model.addAttribute("proyectosActivos", proyectosActivos);
            model.addAttribute("totalCotizaciones", totalCotizaciones);
            model.addAttribute("cotizacionesPendientes", cotizacionesPendientes);
            model.addAttribute("totalClientes", totalClientes);
            model.addAttribute("clientesActivos", clientesActivos);
            model.addAttribute("totalFacturado", totalFacturado);
            model.addAttribute("totalPagado", totalPagado);
            model.addAttribute("totalPendiente", totalPendiente);
            model.addAttribute("porcentajeCobranza", porcentajeCobranza);
            model.addAttribute("totalPagosVencidos", totalPagosVencidos);
            model.addAttribute("proyectosRecientes", proyectosRecientes);
            model.addAttribute("cotizacionesRecientes", cotizacionesRecientes);
            model.addAttribute("proyectosPorEstado", proyectosPorEstado);
            model.addAttribute("cotizacionesPorEstado", cotizacionesPorEstado);
            model.addAttribute("proyectosPorMes", proyectosPorMes);
            model.addAttribute("pagosPorMes", pagosPorMes);
            model.addAttribute("alertas", alertas);
            model.addAttribute("tieneAlertas", !alertas.isEmpty());
            model.addAttribute("proyectosEnDiseno", proyectosEnDiseno);
            model.addAttribute("proyectosEnProduccion", proyectosEnProduccion);
            model.addAttribute("proyectosInstalacion", proyectosInstalacion);
            model.addAttribute("proyectosEntregados", proyectosEntregados);
            model.addAttribute("proyectosAnulados", proyectosAnulados);
            model.addAttribute("cotizacionesAprobadas", cotizacionesAprobadas);
            model.addAttribute("cotizacionesAnuladas", cotizacionesAnuladas);
            model.addAttribute("clientesDeshabilitados", clientesDeshabilitados);
            model.addAttribute("tituloDashboard", "Dashboard - Administrador");
            
        } catch (Exception e) {
            System.err.println("Error en cargarDashboardCompleto: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar datos del dashboard: " + e.getMessage());
        }
    }

    private void cargarDashboardDisenador(Model model) {
        try {
            List<Proyecto> proyectosEnDiseno = new ArrayList<>();
            try {
                proyectosEnDiseno = proyectoService.findByEstado("En Diseño");
                if (proyectosEnDiseno == null) {
                    proyectosEnDiseno = new ArrayList<>();
                }
            } catch (Exception e) {
                System.err.println("Error al cargar proyectos en diseño: " + e.getMessage());
                proyectosEnDiseno = new ArrayList<>();
            }
            long totalEnDiseno = proyectosEnDiseno.size();
            
            List<Proyecto> proyectosRecientes = new ArrayList<>();
            try {
                proyectosRecientes = proyectoService.findTopNByEstado("En Producción", 5);
                if (proyectosRecientes == null) {
                    proyectosRecientes = new ArrayList<>();
                }
            } catch (Exception e) {
                System.err.println("Error al cargar proyectos recientes: " + e.getMessage());
                proyectosRecientes = new ArrayList<>();
            }
            
            List<DocumentoProyecto> documentosRecientes = new ArrayList<>();
            try {
                documentosRecientes = documentoService.findTopN(5);
                if (documentosRecientes == null) {
                    documentosRecientes = new ArrayList<>();
                }
            } catch (Exception e) {
                System.err.println("Error al cargar documentos recientes: " + e.getMessage());
                documentosRecientes = new ArrayList<>();
            }
            
            model.addAttribute("proyectosEnDiseno", proyectosEnDiseno);
            model.addAttribute("totalEnDiseno", totalEnDiseno);
            model.addAttribute("proyectosRecientes", proyectosRecientes);
            model.addAttribute("documentosRecientes", documentosRecientes);
            model.addAttribute("tituloDashboard", "Dashboard - Diseñador");
            
        } catch (Exception e) {
            System.err.println("Error en cargarDashboardDisenador: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar datos del dashboard: " + e.getMessage());
        }
    }

    private void cargarDashboardProduccion(Model model) {
        try {
            List<Proyecto> proyectosEnProduccion = new ArrayList<>();
            try {
                proyectosEnProduccion = proyectoService.findByEstado("En Producción");
                if (proyectosEnProduccion == null) {
                    proyectosEnProduccion = new ArrayList<>();
                }
            } catch (Exception e) {
                System.err.println("Error al cargar proyectos en producción: " + e.getMessage());
                proyectosEnProduccion = new ArrayList<>();
            }
            long totalEnProduccion = proyectosEnProduccion.size();
            
            List<Proyecto> proyectosListosInstalacion = new ArrayList<>();
            try {
                proyectosListosInstalacion = proyectoService.findByEstado("Instalación/Montaje");
                if (proyectosListosInstalacion == null) {
                    proyectosListosInstalacion = new ArrayList<>();
                }
            } catch (Exception e) {
                System.err.println("Error al cargar proyectos para instalación: " + e.getMessage());
                proyectosListosInstalacion = new ArrayList<>();
            }
            
            long inventarioDisponible = 0;
            long inventarioEnUso = 0;
            long inventarioMantenimiento = 0;
            try {
                inventarioDisponible = inventarioService.countByEstadoFisico("Disponible");
                inventarioEnUso = inventarioService.countByEstadoFisico("En uso");
                inventarioMantenimiento = inventarioService.countByEstadoFisico("Mantenimiento");
            } catch (Exception e) {
                System.err.println("Error al cargar inventario: " + e.getMessage());
            }
            
            model.addAttribute("proyectosEnProduccion", proyectosEnProduccion);
            model.addAttribute("totalEnProduccion", totalEnProduccion);
            model.addAttribute("proyectosListosInstalacion", proyectosListosInstalacion);
            model.addAttribute("inventarioDisponible", inventarioDisponible);
            model.addAttribute("inventarioEnUso", inventarioEnUso);
            model.addAttribute("inventarioMantenimiento", inventarioMantenimiento);
            model.addAttribute("tituloDashboard", "Dashboard - Producción");
            
        } catch (Exception e) {
            System.err.println("Error en cargarDashboardProduccion: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar datos del dashboard: " + e.getMessage());
        }
    }

    private void cargarDashboardSoporte(Model model) {
        try {
            List<Proyecto> proyectosInstalacion = new ArrayList<>();
            try {
                proyectosInstalacion = proyectoService.findByEstado("Instalación/Montaje");
                if (proyectosInstalacion == null) {
                    proyectosInstalacion = new ArrayList<>();
                }
            } catch (Exception e) {
                System.err.println("Error al cargar proyectos en instalación: " + e.getMessage());
                proyectosInstalacion = new ArrayList<>();
            }
            long totalInstalacion = proyectosInstalacion.size();
            
            List<Proyecto> proyectosEntregadosRecientes = new ArrayList<>();
            try {
                proyectosEntregadosRecientes = proyectoService.findTopNByEstado("Entregado", 5);
                if (proyectosEntregadosRecientes == null) {
                    proyectosEntregadosRecientes = new ArrayList<>();
                }
            } catch (Exception e) {
                System.err.println("Error al cargar proyectos entregados: " + e.getMessage());
                proyectosEntregadosRecientes = new ArrayList<>();
            }
            
            List<ReservaInventario> reservasActivas = new ArrayList<>();
            try {
                reservasActivas = inventarioService.findReservasActivas();
                if (reservasActivas == null) {
                    reservasActivas = new ArrayList<>();
                }
            } catch (Exception e) {
                System.err.println("Error al cargar reservas activas: " + e.getMessage());
                reservasActivas = new ArrayList<>();
            }
            long totalReservasActivas = reservasActivas.size();
            
            model.addAttribute("proyectosInstalacion", proyectosInstalacion);
            model.addAttribute("totalInstalacion", totalInstalacion);
            model.addAttribute("proyectosEntregadosRecientes", proyectosEntregadosRecientes);
            model.addAttribute("reservasActivas", reservasActivas);
            model.addAttribute("totalReservasActivas", totalReservasActivas);
            model.addAttribute("tituloDashboard", "Dashboard - Soporte Técnico");
            
        } catch (Exception e) {
            System.err.println("Error en cargarDashboardSoporte: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar datos del dashboard: " + e.getMessage());
        }
    }

    private Map<String, Long> getProyectosPorMes(int meses) {
        Map<String, Long> resultado = new LinkedHashMap<>();
        LocalDate hoy = LocalDate.now();
        
        for (int i = meses - 1; i >= 0; i--) {
            LocalDate fecha = hoy.minusMonths(i);
            String mes = fecha.getMonth().toString().substring(0, 3) + " " + fecha.getYear();
            long cantidad = 0;
            try {
                cantidad = proyectoService.countByFechaInicioMonthAndYear(
                    fecha.getMonthValue(), fecha.getYear()
                );
            } catch (Exception e) {
                System.err.println("Error al contar proyectos por mes: " + e.getMessage());
                cantidad = 0;
            }
            resultado.put(mes, cantidad);
        }
        
        return resultado;
    }

    private Map<String, BigDecimal> getPagosPorMes(int meses) {
        Map<String, BigDecimal> resultado = new LinkedHashMap<>();
        LocalDate hoy = LocalDate.now();
        
        for (int i = meses - 1; i >= 0; i--) {
            LocalDate fecha = hoy.minusMonths(i);
            String mes = fecha.getMonth().toString().substring(0, 3) + " " + fecha.getYear();
            BigDecimal total = BigDecimal.ZERO;
            try {
                total = pagoService.sumByMonthAndYear(
                    fecha.getMonthValue(), fecha.getYear()
                );
                if (total == null) {
                    total = BigDecimal.ZERO;
                }
            } catch (Exception e) {
                System.err.println("Error al sumar pagos por mes: " + e.getMessage());
                total = BigDecimal.ZERO;
            }
            resultado.put(mes, total);
        }
        
        return resultado;
    }

    public String getEstadoLegible(String estado) {
        if (estado == null) return "Sin estado";
        switch (estado) {
            case "Aprobado":
                return "Aprobado";
            case "En Diseño":
                return "En Diseño";
            case "En Producción":
                return "En Producción";
            case "Instalación/Montaje":
                return "Instalación/Montaje";
            case "Entregado":
                return "Entregado";
            case "Anulado":
                return "Anulado";
            default:
                return estado;
        }
    }

    public String getEstadoBadgeClass(String estado) {
        if (estado == null) return "status-pendiente";
        switch (estado) {
            case "Aprobado":
                return "status-aprobado";
            case "En Diseño":
                return "status-diseno";
            case "En Producción":
                return "status-produccion";
            case "Instalación/Montaje":
                return "status-instalacion";
            case "Entregado":
                return "status-entregado";
            case "Anulado":
                return "status-anulado";
            default:
                return "status-pendiente";
        }
    }

    public String formatearMoneda(BigDecimal monto) {
        if (monto == null) return "S/ 0.00";
        return "S/ " + monto.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public String getColorByEstado(String estado) {
        if (estado == null) return "#6B7280";
        switch (estado) {
            case "En Diseño":
                return "#F59E0B";
            case "En Producción":
                return "#3B82F6";
            case "Instalación/Montaje":
                return "#8B5CF6";
            case "Entregado":
                return "#10B981";
            case "Anulado":
                return "#EF4444";
            default:
                return "#6B7280";
        }
    }
}