package com.stamos.controller;

import com.stamos.dto.ReporteFiltrosDTO;
import com.stamos.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String panelReportes(Model model) {
        model.addAttribute("totalPendientes", reporteService.getTotalPendientes());
        model.addAttribute("totalAtrasados", reporteService.getTotalProyectosAtrasados());
        return "reportes/panel";
    }

    @GetMapping("/pagos-pendientes")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String reportePagosPendientes(Model model) {
        model.addAttribute("pendientes", reporteService.getPagosPendientesPorCliente());
        return "reportes/pagos-pendientes";
    }

    @GetMapping("/proyectos-atrasados")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String reporteProyectosAtrasados(Model model) {
        model.addAttribute("proyectos", reporteService.getProyectosAtrasados());
        return "reportes/proyectos-atrasados";
    }

    @GetMapping("/equipos-mas-alquilados")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL', 'SOPORTE_TECNICO')")
    public String reporteEquiposMasAlquilados(Model model) {
        model.addAttribute("equipos", reporteService.getEquiposMasAlquilados());
        return "reportes/equipos-mas-alquilados";
    }

    @GetMapping("/ingresos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String reporteIngresosForm(Model model) {
        model.addAttribute("filtros", new ReporteFiltrosDTO());
        return "reportes/ingresos-form";
    }

    @GetMapping("/ingresos/generar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String generarReporteIngresos(@RequestParam String tipoPeriodo,
                                         @RequestParam int anio,
                                         @RequestParam(required = false) Integer mes,
                                         @RequestParam(required = false) Integer trimestre,
                                         Model model) {
        Map<String, Object> resultado;
        
        if ("MENSUAL".equalsIgnoreCase(tipoPeriodo) && mes != null) {
            resultado = reporteService.getIngresosMensual(anio, mes);
            model.addAttribute("periodo", anio + "-" + String.format("%02d", mes));
        } else if ("TRIMESTRAL".equalsIgnoreCase(tipoPeriodo) && trimestre != null) {
            resultado = reporteService.getIngresosTrimestral(anio, trimestre);
            model.addAttribute("periodo", "Trimestre " + trimestre + " de " + anio);
        } else {
            model.addAttribute("error", "Parámetros inválidos.");
            return "reportes/ingresos-form";
        }
        
        model.addAttribute("resultado", resultado);
        model.addAttribute("tipoPeriodo", tipoPeriodo);
        model.addAttribute("anio", anio);
        model.addAttribute("mes", mes);
        model.addAttribute("trimestre", trimestre);
        
        return "reportes/ingresos-resultado";
    }

    @GetMapping("/cotizaciones-estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String reporteCotizacionesEstado(Model model) {
        model.addAttribute("estadisticas", reporteService.getCotizacionesAprobadasVsRechazadas());
        return "reportes/cotizaciones-estado";
    }
}