package com.stamos.controller;

import com.stamos.dto.ReporteFiltrosDTO;
import com.stamos.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

/**
 * Controlador para la generación de reportes.
 * Accesible solo para ADMINISTRADOR y COMERCIAL (según reglas de negocio).
 * 
 * Reportes incluidos:
 * - Pagos pendientes por cliente (RRE01)
 * - Proyectos atrasados (RRE02)
 * - Equipos más alquilados (RRE03)
 * - Ingresos por período (mensual/trimestral) (RRE04)
 * - Cotizaciones aprobadas vs rechazadas
 * - Exportación a Excel y PDF (RF-26)
 */
@Controller
@RequestMapping("/internal/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    /**
     * Panel principal de reportes con enlaces a cada reporte.
     *
     * @param model Modelo
     * @return Vista del panel de reportes
     */
    @GetMapping("/panel")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String panelReportes(Model model) {
        // Datos resumen para el panel (opcional)
        model.addAttribute("totalPendientes", reporteService.getTotalPendientes());
        model.addAttribute("totalAtrasados", reporteService.getTotalProyectosAtrasados());
        return "internal/reportes/panel";
    }

    // ==================== REPORTE: PAGOS PENDIENTES POR CLIENTE ====================

    /**
     * Muestra el reporte de pagos pendientes por cliente.
     *
     * @param model Modelo
     * @return Vista de pagos pendientes
     */
    @GetMapping("/pagos-pendientes")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String reportePagosPendientes(Model model) {
        model.addAttribute("pendientes", reporteService.getPagosPendientesPorCliente());
        return "internal/reportes/pagos-pendientes";
    }

    /**
     * Exporta a Excel el reporte de pagos pendientes.
     * @param response
     * @throws java.io.IOException
     */
    @GetMapping("/pagos-pendientes/export/excel")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public void exportPagosPendientesExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=pagos_pendientes.xlsx");
        reporteService.exportPagosPendientesToExcel(response.getOutputStream());
    }

    /**
     * Exporta a PDF el reporte de pagos pendientes.
     * @param response
     * @throws java.io.IOException
     */
    @GetMapping("/pagos-pendientes/export/pdf")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public void exportPagosPendientesPdf(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=pagos_pendientes.pdf");
        reporteService.exportPagosPendientesToPdf(response.getOutputStream());
    }

    // ==================== REPORTE: PROYECTOS ATRASADOS ====================

    /**
     * Muestra el reporte de proyectos atrasados.
     *
     * @param model Modelo
     * @return Vista de proyectos atrasados
     */
    @GetMapping("/proyectos-atrasados")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String reporteProyectosAtrasados(Model model) {
        model.addAttribute("proyectos", reporteService.getProyectosAtrasados());
        return "internal/reportes/proyectos-atrasados";
    }

    /**
     * Exporta a Excel el reporte de proyectos atrasados.
     * @param response
     * @throws java.io.IOException
     */
    @GetMapping("/proyectos-atrasados/export/excel")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public void exportProyectosAtrasadosExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=proyectos_atrasados.xlsx");
        reporteService.exportProyectosAtrasadosToExcel(response.getOutputStream());
    }

    /**
     * Exporta a PDF el reporte de proyectos atrasados.
     * @param response
     * @throws java.io.IOException
     */
    @GetMapping("/proyectos-atrasados/export/pdf")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public void exportProyectosAtrasadosPdf(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=proyectos_atrasados.pdf");
        reporteService.exportProyectosAtrasadosToPdf(response.getOutputStream());
    }

    // ==================== REPORTE: EQUIPOS MÁS ALQUILADOS ====================

    /**
     * Muestra el reporte de equipos más alquilados.
     *
     * @param model Modelo
     * @return Vista de equipos más alquilados
     */
    @GetMapping("/equipos-mas-alquilados")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL', 'SOPORTE_TECNICO')")
    public String reporteEquiposMasAlquilados(Model model) {
        model.addAttribute("equipos", reporteService.getEquiposMasAlquilados());
        return "internal/reportes/equipos-mas-alquilados";
    }

    /**
     * Exporta a Excel el reporte de equipos más alquilados.
     * @param response
     * @throws java.io.IOException
     */
    @GetMapping("/equipos-mas-alquilados/export/excel")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public void exportEquiposMasAlquiladosExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=equipos_mas_alquilados.xlsx");
        reporteService.exportEquiposMasAlquiladosToExcel(response.getOutputStream());
    }

    /**
     * Exporta a PDF el reporte de equipos más alquilados.
     * @param response
     * @throws java.io.IOException
     */
    @GetMapping("/equipos-mas-alquilados/export/pdf")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public void exportEquiposMasAlquiladosPdf(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=equipos_mas_alquilados.pdf");
        reporteService.exportEquiposMasAlquiladosToPdf(response.getOutputStream());
    }

    // ==================== REPORTE: INGRESOS POR PERÍODO ====================

    /**
     * Muestra el formulario para seleccionar período (mensual o trimestral) y generar reporte de ingresos.
     *
     * @param model Modelo
     * @return Vista con filtros
     */
    @GetMapping("/ingresos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String reporteIngresosForm(Model model) {
        model.addAttribute("filtros", new ReporteFiltrosDTO());
        return "internal/reportes/ingresos-form";
    }

    /**
     * Procesa la generación del reporte de ingresos según período.
     *
     * @param tipoPeriodo "MENSUAL" o "TRIMESTRAL"
     * @param anio Año (ej. 2025)
     * @param mes Mes (solo para MENSUAL)
     * @param trimestre Trimestre (1-4, solo para TRIMESTRAL)
     * @param model Modelo
     * @return Vista con resultados
     */
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
            return "internal/reportes/ingresos-form";
        }
        model.addAttribute("resultado", resultado);
        model.addAttribute("tipoPeriodo", tipoPeriodo);
        model.addAttribute("anio", anio);
        model.addAttribute("mes", mes);
        model.addAttribute("trimestre", trimestre);
        return "internal/reportes/ingresos-resultado";
    }

    /**
     * Exporta a Excel el reporte de ingresos según los mismos filtros.
     * @param tipoPeriodo
     * @param anio
     * @param mes
     * @param trimestre
     * @param response
     * @throws java.io.IOException
     */
    @GetMapping("/ingresos/export/excel")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public void exportIngresosExcel(@RequestParam String tipoPeriodo,
                                    @RequestParam int anio,
                                    @RequestParam(required = false) Integer mes,
                                    @RequestParam(required = false) Integer trimestre,
                                    HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String filename = "ingresos_" + tipoPeriodo.toLowerCase() + "_" + anio;
        if (mes != null) filename += "_" + String.format("%02d", mes);
        if (trimestre != null) filename += "_trim" + trimestre;
        response.setHeader("Content-Disposition", "attachment; filename=" + filename + ".xlsx");
        reporteService.exportIngresosToExcel(tipoPeriodo, anio, mes, trimestre, response.getOutputStream());
    }

    /**
     * Exporta a PDF el reporte de ingresos.
     * @param tipoPeriodo
     * @param anio
     * @param mes
     * @param trimestre
     * @param response
     * @throws java.io.IOException
     */
    @GetMapping("/ingresos/export/pdf")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public void exportIngresosPdf(@RequestParam String tipoPeriodo,
                                  @RequestParam int anio,
                                  @RequestParam(required = false) Integer mes,
                                  @RequestParam(required = false) Integer trimestre,
                                  HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        String filename = "ingresos_" + tipoPeriodo.toLowerCase() + "_" + anio;
        if (mes != null) filename += "_" + String.format("%02d", mes);
        if (trimestre != null) filename += "_trim" + trimestre;
        response.setHeader("Content-Disposition", "attachment; filename=" + filename + ".pdf");
        reporteService.exportIngresosToPdf(tipoPeriodo, anio, mes, trimestre, response.getOutputStream());
    }

    // ==================== REPORTE ADICIONAL: COTIZACIONES APROBADAS VS RECHAZADAS ====================

    /**
     * Reporte de cotizaciones aprobadas vs rechazadas (solicitado en resumen de funcionalidades).
     *
     * @param model Modelo
     * @return Vista del reporte
     */
    @GetMapping("/cotizaciones-estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public String reporteCotizacionesEstado(Model model) {
        model.addAttribute("estadisticas", reporteService.getCotizacionesAprobadasVsRechazadas());
        return "internal/reportes/cotizaciones-estado";
    }

    /**
     * Exporta a Excel el reporte de cotizaciones.
     * @param response
     * @throws java.io.IOException
     */
    @GetMapping("/cotizaciones-estado/export/excel")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public void exportCotizacionesEstadoExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=cotizaciones_estado.xlsx");
        reporteService.exportCotizacionesEstadoToExcel(response.getOutputStream());
    }

    /**
     * Exporta a PDF el reporte de cotizaciones.
     * @param response
     * @throws java.io.IOException
     */
    @GetMapping("/cotizaciones-estado/export/pdf")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
    public void exportCotizacionesEstadoPdf(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=cotizaciones_estado.pdf");
        reporteService.exportCotizacionesEstadoToPdf(response.getOutputStream());
    }
}