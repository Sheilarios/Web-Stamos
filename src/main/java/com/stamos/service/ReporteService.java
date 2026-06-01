package com.stamos.service;

import com.stamos.entity.Pago;
import com.stamos.entity.Proyecto;
import com.stamos.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Importar solo las clases necesarias de OpenPDF (sin comodín para evitar conflicto con List)
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Chunk;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Servicio para la generación de reportes.
 * Incluye exportación a Excel (Apache POI) y PDF (OpenPDF).
 */
@Service
public class ReporteService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private ReservaEquipoRepository reservaEquipoRepository;

    @Autowired
    private CotizacionRepository cotizacionRepository;

    // ==================== MÉTRICAS RESUMEN ====================

    public BigDecimal getTotalPendientes() {
        // Suma de saldos pendientes de proyectos no entregados
        List<Proyecto> proyectosActivos = proyectoRepository.findByEstadoNotIn(Arrays.asList("ENTREGADO", "ANULADO"));
        BigDecimal total = BigDecimal.ZERO;
        for (Proyecto p : proyectosActivos) {
            // Calculamos el total pagado sumando los pagos del proyecto
            BigDecimal pagado = pagoRepository.findByProyectoIdOrderByFechaDesc(p.getId())
                    .stream()
                    .filter(pago -> !"ANULADO".equals(pago.getEstado()))
                    .map(Pago::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalCotizacion = p.getCotizacion().getTotal();
            total = total.add(totalCotizacion.subtract(pagado));
        }
        return total;
    }

    public int getTotalProyectosAtrasados() {
        LocalDate today = LocalDate.now();
        List<String> estadosNoFinales = Arrays.asList("COTIZADO", "APROBADO", "EN_DISEÑO", "EN_PRODUCCION", "INSTALACION_MONTAJE");
        return proyectoRepository.countByEstadoInAndFechaEntregaEstimadaBefore(estadosNoFinales, today);
    }

    // ==================== REPORTE: PAGOS PENDIENTES POR CLIENTE ====================

    public List<Object[]> getPagosPendientesPorCliente() {
        return pagoRepository.findPagosPendientesPorCliente();
    }

    public void exportPagosPendientesToExcel(OutputStream os) throws IOException {
        List<Object[]> data = getPagosPendientesPorCliente();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Pagos Pendientes");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Cliente ID");
            header.createCell(1).setCellValue("Cliente");
            header.createCell(2).setCellValue("Total Pagado (S/)");
            header.createCell(3).setCellValue("Total Cotización (S/)");
            header.createCell(4).setCellValue("Saldo Pendiente (S/)");

            int rowNum = 1;
            for (Object[] row : data) {
                Row r = sheet.createRow(rowNum++);
                r.createCell(0).setCellValue(((Number) row[0]).longValue());
                r.createCell(1).setCellValue((String) row[1]);
                r.createCell(2).setCellValue(((BigDecimal) row[2]).doubleValue());
                r.createCell(3).setCellValue(((BigDecimal) row[3]).doubleValue());
                r.createCell(4).setCellValue(((BigDecimal) row[4]).doubleValue());
            }
            for (int i = 0; i < 5; i++) sheet.autoSizeColumn(i);
            workbook.write(os);
        }
    }

    public void exportPagosPendientesToPdf(OutputStream os) throws DocumentException {
        List<Object[]> data = getPagosPendientesPorCliente();
        try (Document document = new Document(PageSize.A4)) {
            PdfWriter.getInstance(document, os);
            document.open();
            document.add(new Paragraph("Reporte de Pagos Pendientes por Cliente", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            document.add(new Paragraph("Generado: " + LocalDate.now(), FontFactory.getFont(FontFactory.HELVETICA, 10)));
            document.add(Chunk.NEWLINE);
            
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.addCell("Cliente ID");
            table.addCell("Cliente");
            table.addCell("Total Pagado (S/)");
            table.addCell("Total Cotización (S/)");
            table.addCell("Saldo Pendiente (S/)");
            
            for (Object[] row : data) {
                table.addCell(row[0].toString());
                table.addCell((String) row[1]);
                table.addCell(((BigDecimal) row[2]).toString());
                table.addCell(((BigDecimal) row[3]).toString());
                table.addCell(((BigDecimal) row[4]).toString());
            }
            document.add(table);
        }
    }

    // ==================== REPORTE: PROYECTOS ATRASADOS ====================

    public List<Proyecto> getProyectosAtrasados() {
        LocalDate today = LocalDate.now();
        List<String> estadosNoFinales = Arrays.asList("COTIZADO", "APROBADO", "EN_DISEÑO", "EN_PRODUCCION", "INSTALACION_MONTAJE");
        return proyectoRepository.findByEstadoInAndFechaEntregaEstimadaBefore(estadosNoFinales, today);
    }

    public void exportProyectosAtrasadosToExcel(OutputStream os) throws IOException {
        List<Proyecto> proyectos = getProyectosAtrasados();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Proyectos Atrasados");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Cliente");
            header.createCell(2).setCellValue("Estado");
            header.createCell(3).setCellValue("Fecha Entrega Estimada");
            header.createCell(4).setCellValue("Responsable");

            int rowNum = 1;
            for (Proyecto p : proyectos) {
                Row r = sheet.createRow(rowNum++);
                r.createCell(0).setCellValue(p.getId());
                r.createCell(1).setCellValue(p.getCliente().getNombreRazonSocial());
                r.createCell(2).setCellValue(p.getEstado());
                r.createCell(3).setCellValue(p.getFechaEntregaEstimada().toString());
                r.createCell(4).setCellValue(p.getResponsable() != null ? p.getResponsable().getNombre() : "");
            }
            for (int i = 0; i < 5; i++) sheet.autoSizeColumn(i);
            workbook.write(os);
        }
    }

    public void exportProyectosAtrasadosToPdf(OutputStream os) throws DocumentException {
        List<Proyecto> proyectos = getProyectosAtrasados();
        try (Document document = new Document(PageSize.A4.rotate())) {
            PdfWriter.getInstance(document, os);
            document.open();
            document.add(new Paragraph("Proyectos Atrasados", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            document.add(new Paragraph("Fecha: " + LocalDate.now()));
            document.add(Chunk.NEWLINE);
            
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.addCell("ID");
            table.addCell("Cliente");
            table.addCell("Estado");
            table.addCell("Fecha Entrega Estimada");
            table.addCell("Responsable");
            
            for (Proyecto p : proyectos) {
                table.addCell(String.valueOf(p.getId()));
                table.addCell(p.getCliente().getNombreRazonSocial());
                table.addCell(p.getEstado());
                table.addCell(p.getFechaEntregaEstimada().toString());
                table.addCell(p.getResponsable() != null ? p.getResponsable().getNombre() : "");
            }
            document.add(table);
        }
    }

    // ==================== REPORTE: EQUIPOS MÁS ALQUILADOS ====================

    public List<Object[]> getEquiposMasAlquilados() {
        return reservaEquipoRepository.findEquiposMasAlquilados();
    }

    public void exportEquiposMasAlquiladosToExcel(OutputStream os) throws IOException {
        List<Object[]> data = getEquiposMasAlquilados();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Equipos Más Alquilados");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Equipo ID");
            header.createCell(1).setCellValue("Nombre");
            header.createCell(2).setCellValue("Cantidad Total Alquilada");

            int rowNum = 1;
            for (Object[] row : data) {
                Row r = sheet.createRow(rowNum++);
                r.createCell(0).setCellValue(((Number) row[0]).longValue());
                r.createCell(1).setCellValue((String) row[1]);
                r.createCell(2).setCellValue(((Number) row[2]).intValue());
            }
            for (int i = 0; i < 3; i++) sheet.autoSizeColumn(i);
            workbook.write(os);
        }
    }

    public void exportEquiposMasAlquiladosToPdf(OutputStream os) throws DocumentException {
        List<Object[]> data = getEquiposMasAlquilados();
        try (Document document = new Document(PageSize.A4)) {
            PdfWriter.getInstance(document, os);
            document.open();
            document.add(new Paragraph("Equipos más alquilados", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            document.add(Chunk.NEWLINE);
            
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.addCell("Equipo ID");
            table.addCell("Nombre");
            table.addCell("Cantidad Total Alquilada");
            
            for (Object[] row : data) {
                table.addCell(row[0].toString());
                table.addCell((String) row[1]);
                table.addCell(row[2].toString());
            }
            document.add(table);
        }
    }

    // ==================== REPORTE: INGRESOS POR PERÍODO ====================

    public Map<String, Object> getIngresosMensual(int anio, int mes) {
        YearMonth yearMonth = YearMonth.of(anio, mes);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();
        BigDecimal ingresos = pagoRepository.sumMontoByFechaBetween(start, end);
        ingresos = ingresos != null ? ingresos : BigDecimal.ZERO;
        Map<String, Object> result = new HashMap<>();
        result.put("periodo", yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        result.put("ingresos", ingresos);
        result.put("detalle", pagoRepository.findByFechaBetween(start, end));
        return result;
    }

    public Map<String, Object> getIngresosTrimestral(int anio, int trimestre) {
        int startMonth = (trimestre - 1) * 3 + 1;
        int endMonth = startMonth + 2;
        LocalDate start = LocalDate.of(anio, startMonth, 1);
        LocalDate end = LocalDate.of(anio, endMonth, YearMonth.of(anio, endMonth).lengthOfMonth());
        BigDecimal ingresos = pagoRepository.sumMontoByFechaBetween(start, end);
        ingresos = ingresos != null ? ingresos : BigDecimal.ZERO;
        Map<String, Object> result = new HashMap<>();
        result.put("periodo", "Trimestre " + trimestre + " de " + anio);
        result.put("ingresos", ingresos);
        result.put("detalle", pagoRepository.findByFechaBetween(start, end));
        return result;
    }

    public void exportIngresosToExcel(String tipoPeriodo, int anio, Integer mes, Integer trimestre, OutputStream os) throws IOException {
        List<Pago> pagos;
        if ("MENSUAL".equalsIgnoreCase(tipoPeriodo) && mes != null) {
            YearMonth ym = YearMonth.of(anio, mes);
            pagos = pagoRepository.findByFechaBetween(ym.atDay(1), ym.atEndOfMonth());
        } else if ("TRIMESTRAL".equalsIgnoreCase(tipoPeriodo) && trimestre != null) {
            int startMonth = (trimestre - 1) * 3 + 1;
            int endMonth = startMonth + 2;
            LocalDate start = LocalDate.of(anio, startMonth, 1);
            LocalDate end = LocalDate.of(anio, endMonth, YearMonth.of(anio, endMonth).lengthOfMonth());
            pagos = pagoRepository.findByFechaBetween(start, end);
        } else {
            throw new IllegalArgumentException("Parámetros inválidos");
        }
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Ingresos");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Fecha");
            header.createCell(1).setCellValue("Proyecto ID");
            header.createCell(2).setCellValue("Cliente");
            header.createCell(3).setCellValue("Monto (S/)");
            header.createCell(4).setCellValue("Medio de Pago");

            int rowNum = 1;
            for (Pago p : pagos) {
                Row r = sheet.createRow(rowNum++);
                r.createCell(0).setCellValue(p.getFecha().toString());
                r.createCell(1).setCellValue(p.getProyecto().getId());
                r.createCell(2).setCellValue(p.getProyecto().getCliente().getNombreRazonSocial());
                r.createCell(3).setCellValue(p.getMonto().doubleValue());
                r.createCell(4).setCellValue(p.getMedioPago());
            }
            Row totalRow = sheet.createRow(rowNum);
            totalRow.createCell(3).setCellValue("Total:");
            totalRow.createCell(4).setCellValue(pagos.stream().map(Pago::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue());
            for (int i = 0; i < 5; i++) sheet.autoSizeColumn(i);
            workbook.write(os);
        }
    }

    public void exportIngresosToPdf(String tipoPeriodo, int anio, Integer mes, Integer trimestre, OutputStream os) throws DocumentException {
        List<Pago> pagos;
        String titulo;
        if ("MENSUAL".equalsIgnoreCase(tipoPeriodo) && mes != null) {
            YearMonth ym = YearMonth.of(anio, mes);
            pagos = pagoRepository.findByFechaBetween(ym.atDay(1), ym.atEndOfMonth());
            titulo = "Ingresos - " + ym.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        } else if ("TRIMESTRAL".equalsIgnoreCase(tipoPeriodo) && trimestre != null) {
            int startMonth = (trimestre - 1) * 3 + 1;
            int endMonth = startMonth + 2;
            LocalDate start = LocalDate.of(anio, startMonth, 1);
            LocalDate end = LocalDate.of(anio, endMonth, YearMonth.of(anio, endMonth).lengthOfMonth());
            pagos = pagoRepository.findByFechaBetween(start, end);
            titulo = "Ingresos - Trimestre " + trimestre + " de " + anio;
        } else {
            throw new IllegalArgumentException("Parámetros inválidos");
        }
        try (Document document = new Document(PageSize.A4.rotate())) {
            PdfWriter.getInstance(document, os);
            document.open();
            document.add(new Paragraph(titulo, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            document.add(Chunk.NEWLINE);
            
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.addCell("Fecha");
            table.addCell("Proyecto ID");
            table.addCell("Cliente");
            table.addCell("Monto (S/)");
            table.addCell("Medio de Pago");
            
            BigDecimal total = BigDecimal.ZERO;
            for (Pago p : pagos) {
                table.addCell(p.getFecha().toString());
                table.addCell(p.getProyecto().getId().toString());
                table.addCell(p.getProyecto().getCliente().getNombreRazonSocial());
                table.addCell(p.getMonto().toString());
                table.addCell(p.getMedioPago());
                total = total.add(p.getMonto());
            }
            document.add(table);
            document.add(new Paragraph("Total: S/ " + total.toString()));
        }
    }

    // ==================== REPORTE: COTIZACIONES APROBADAS VS RECHAZADAS ====================

    public Map<String, Long> getCotizacionesAprobadasVsRechazadas() {
        long aprobadas = cotizacionRepository.countByEstado("APROBADA");
        long rechazadas = cotizacionRepository.countByEstado("RECHAZADA");
        Map<String, Long> result = new HashMap<>();
        result.put("APROBADAS", aprobadas);
        result.put("RECHAZADAS", rechazadas);
        return result;
    }

    public void exportCotizacionesEstadoToExcel(OutputStream os) throws IOException {
        Map<String, Long> data = getCotizacionesAprobadasVsRechazadas();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Cotizaciones Estado");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Estado");
            header.createCell(1).setCellValue("Cantidad");
            Row row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue("APROBADAS");
            row1.createCell(1).setCellValue(data.get("APROBADAS"));
            Row row2 = sheet.createRow(2);
            row2.createCell(0).setCellValue("RECHAZADAS");
            row2.createCell(1).setCellValue(data.get("RECHAZADAS"));
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            workbook.write(os);
        }
    }

    public void exportCotizacionesEstadoToPdf(OutputStream os) throws DocumentException {
        Map<String, Long> data = getCotizacionesAprobadasVsRechazadas();
        try (Document document = new Document()) {
            PdfWriter.getInstance(document, os);
            document.open();
            document.add(new Paragraph("Cotizaciones Aprobadas vs Rechazadas"));
            document.add(Chunk.NEWLINE);
            PdfPTable table = new PdfPTable(2);
            table.addCell("Estado");
            table.addCell("Cantidad");
            table.addCell("APROBADAS");
            table.addCell(data.get("APROBADAS").toString());
            table.addCell("RECHAZADAS");
            table.addCell(data.get("RECHAZADAS").toString());
            document.add(table);
        }
    }
}