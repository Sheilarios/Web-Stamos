package com.stamos.controller;

import com.stamos.service.ClienteService;
import com.stamos.service.CotizacionService;
import com.stamos.service.PagoService;
import com.stamos.service.ProyectoService;
import com.stamos.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/internal")
public class InternalDashboardController {

    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private ProyectoService proyectoService;
    
    @Autowired
    private CotizacionService cotizacionService;
    
    @Autowired
    private PagoService pagoService;
    
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        System.out.println("=== DASHBOARD CONTROLLER ===");
        
        if (auth == null) {
            System.out.println("Auth es null - redirigiendo a login");
            return "redirect:/public/login";
        }
        
        System.out.println("Usuario: " + auth.getName());
        System.out.println("Roles: " + auth.getAuthorities());
        String rol = auth.getAuthorities().iterator().next().getAuthority();
        System.out.println("Rol original: " + rol);
        
        if (rol.startsWith("ROLE_")) {
            rol = rol.substring(5);
        }
        
        System.out.println("Rol procesado: " + rol);
        cargarMetricas(model);
        cargarListasRecientes(model);
        model.addAttribute("usuario", auth.getName());
        model.addAttribute("rol", rol);
        model.addAttribute("fechaActual", LocalDate.now());
        model.addAttribute("fechaFormateada", LocalDate.now().toString());
        String template = getTemplateByRol(rol);
        System.out.println("Retornando template: " + template);
        return template;
    }
  
    private void cargarMetricas(Model model) {
        try {
            long totalClientes = clienteService.countActive();
            System.out.println("Total clientes activos: " + totalClientes);
            model.addAttribute("totalClientes", totalClientes);
            
            long totalClientesTotal = clienteService.countAll();
            model.addAttribute("totalClientesTotal", totalClientesTotal);
            
            long proyectosActivos = proyectoService.countByEstadosNot("ENTREGADO", "ANULADO");
            System.out.println("Proyectos activos: " + proyectosActivos);
            model.addAttribute("proyectosActivos", proyectosActivos);
            
            long proyectosEntregados = proyectoService.countByEstadosIn(List.of("ENTREGADO"));
            model.addAttribute("proyectosEntregados", proyectosEntregados);
            
            long proyectosTotales = proyectoService.count();
            model.addAttribute("proyectosTotales", proyectosTotales);
            
            YearMonth yearMonth = YearMonth.now();
            LocalDate inicioMes = yearMonth.atDay(1);
            LocalDate finMes = yearMonth.atEndOfMonth();
            long cotizacionesMes = cotizacionService.countByFechaBetween(inicioMes, finMes);
            System.out.println("Cotizaciones del mes: " + cotizacionesMes);
            model.addAttribute("cotizacionesMes", cotizacionesMes);
            
            long cotizacionesAprobadas = cotizacionService.countByEstado("APROBADA");
            model.addAttribute("cotizacionesAprobadas", cotizacionesAprobadas);
            
            BigDecimal ingresosMes = pagoService.getTotalPagadoEnMes(LocalDate.now());
            if (ingresosMes == null) {
                ingresosMes = BigDecimal.ZERO;
            }
            System.out.println("Ingresos del mes: S/ " + ingresosMes);
            model.addAttribute("ingresosMes", ingresosMes);
            
            BigDecimal totalPorCobrar = pagoService.getTotalPorCobrar();
            if (totalPorCobrar == null) {
                totalPorCobrar = BigDecimal.ZERO;
            }
            System.out.println("Total por cobrar: S/ " + totalPorCobrar);
            model.addAttribute("totalPorCobrar", totalPorCobrar);
            
            BigDecimal pagosVencidos = pagoService.getTotalVencido();
            if (pagosVencidos == null) {
                pagosVencidos = BigDecimal.ZERO;
            }
            System.out.println("Pagos vencidos: S/ " + pagosVencidos);
            model.addAttribute("pagosVencidos", pagosVencidos);
            
            int comprobantesPendientes = pagoService.countComprobantesPendientes();
            System.out.println("Comprobantes pendientes: " + comprobantesPendientes);
            model.addAttribute("comprobantesPendientes", comprobantesPendientes);
            
            long usuariosActivos = usuarioService.countByActivoTrue();
            model.addAttribute("usuariosActivos", usuariosActivos);
            
            BigDecimal totalCobradoHistorico = pagoService.getTotalPagadoHistorico();
            if (totalCobradoHistorico == null) {
                totalCobradoHistorico = BigDecimal.ZERO;
            }
            model.addAttribute("totalCobradoHistorico", totalCobradoHistorico);
            
            double porcentajeOcupacion = calcularPorcentajeOcupacion(proyectosActivos, proyectosTotales);
            model.addAttribute("porcentajeOcupacion", porcentajeOcupacion);
            
        } catch (Exception e) {
            System.err.println("Error al cargar métricas: " + e.getMessage());
            model.addAttribute("totalClientes", 0L);
            model.addAttribute("proyectosActivos", 0L);
            model.addAttribute("cotizacionesMes", 0L);
            model.addAttribute("ingresosMes", BigDecimal.ZERO);
            model.addAttribute("totalPorCobrar", BigDecimal.ZERO);
            model.addAttribute("pagosVencidos", BigDecimal.ZERO);
            model.addAttribute("comprobantesPendientes", 0);
        }
    }
    

    private void cargarListasRecientes(Model model) {
        try {
            var proyectosRecientes = proyectoService.findTop5ByOrderByFechaCreacionDesc();
            System.out.println("Proyectos recientes encontrados: " + (proyectosRecientes != null ? proyectosRecientes.size() : 0));
            model.addAttribute("proyectosRecientes", proyectosRecientes != null ? proyectosRecientes : List.of());
            
            var pagosRecientes = pagoService.findTop5ByOrderByFechaDesc();
            System.out.println("Pagos recientes encontrados: " + (pagosRecientes != null ? pagosRecientes.size() : 0));
            model.addAttribute("pagosRecientes", pagosRecientes != null ? pagosRecientes : List.of());
            
            var cotizacionesRecientes = cotizacionService.findTop5ByOrderByFechaEmisionDesc();
            System.out.println("Cotizaciones recientes encontradas: " + (cotizacionesRecientes != null ? cotizacionesRecientes.size() : 0));
            model.addAttribute("cotizacionesRecientes", cotizacionesRecientes != null ? cotizacionesRecientes : List.of());
            
            var clientesRecientes = clienteService.findTop5ByOrderByIdDesc();
            model.addAttribute("clientesRecientes", clientesRecientes != null ? clientesRecientes : List.of());
            
        } catch (Exception e) {
            System.err.println("Error al cargar listas recientes: " + e.getMessage());
            model.addAttribute("proyectosRecientes", List.of());
            model.addAttribute("pagosRecientes", List.of());
            model.addAttribute("cotizacionesRecientes", List.of());
            model.addAttribute("clientesRecientes", List.of());
        }
    }
    
    private String getTemplateByRol(String rol) {
        return switch (rol) {
            case "ADMINISTRADOR" -> "internal/dashboard/admin";
            case "COMERCIAL" -> "internal/dashboard/comercial";
            case "DISENADOR" -> "internal/dashboard/disenador";
            case "SOPORTE_TECNICO", "SOPORTE" -> "internal/dashboard/soporte";
            case "PRODUCCION" -> "internal/dashboard/produccion";
            default -> "internal/dashboard/admin";
        };
    }
    
    private double calcularPorcentajeOcupacion(long proyectosActivos, long proyectosTotales) {
        if (proyectosTotales == 0) return 0.0;
        return (proyectosActivos * 100.0) / proyectosTotales;
    }
    
    @GetMapping("/dashboard/data")
    @ResponseBody
    public Map<String, Object> dashboardData(Authentication auth) {
        Map<String, Object> data = new HashMap<>();
        
        if (auth == null) {
            data.put("error", "No autenticado");
            data.put("authenticated", false);
            return data;
        }
        
        try {
            data.put("authenticated", true);
            data.put("usuario", auth.getName());
            data.put("totalClientes", clienteService.countActive());
            data.put("proyectosActivos", proyectoService.countByEstadosNot("ENTREGADO", "ANULADO"));
            data.put("ingresosMes", pagoService.getTotalPagadoEnMes(LocalDate.now()));
            data.put("totalPorCobrar", pagoService.getTotalPorCobrar());
            data.put("fechaActual", LocalDate.now().toString());
        } catch (Exception e) {
            data.put("error", e.getMessage());
        }
        
        return data;
    }
    
    @GetMapping("/dashboard/metrics")
    @ResponseBody
    public Map<String, Object> dashboardMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        try {
            LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
            LocalDate finMes = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
            metrics.put("totalClientes", clienteService.countActive());
            metrics.put("proyectosActivos", proyectoService.countByEstadosNot("ENTREGADO", "ANULADO"));
            metrics.put("cotizacionesMes", cotizacionService.countByFechaBetween(inicioMes, finMes));
            metrics.put("ingresosMes", pagoService.getTotalPagadoEnMes(LocalDate.now()));
            metrics.put("totalPorCobrar", pagoService.getTotalPorCobrar());
            metrics.put("pagosVencidos", pagoService.getTotalVencido());
            metrics.put("comprobantesPendientes", pagoService.countComprobantesPendientes());
            metrics.put("success", true);
        } catch (Exception e) {
            metrics.put("success", false);
            metrics.put("error", e.getMessage());
        }
        return metrics;
    }
}