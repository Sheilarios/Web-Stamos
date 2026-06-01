package com.stamos.util;

import com.stamos.entity.Pago;
import com.stamos.entity.Proyecto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Utilidad para exportar reportes a formato Excel (XLSX).
 * Utiliza Apache POI para generar archivos Excel con formato profesional.
 */
public final class ReporteExcelExporter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ReporteExcelExporter() {
        // Constructor privado para evitar instanciación
    }

    /**
     * Exporta el reporte de pagos pendientes por cliente.
     *
     * @param data Lista de Object[] con: [clienteId, nombreCliente, totalPagado, totalCotizacion, saldoPendiente]
     * @param os   OutputStream donde se escribirá el archivo Excel
     * @throws IOException si ocurre un error al escribir
     */
    public static void exportPagosPendientes(List<Object[]> data, OutputStream os) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Pagos Pendientes");
            // Estilo para encabezados
            CellStyle headerStyle = createHeaderStyle(workbook);
            // Crear fila de encabezados
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Cliente ID", "Cliente", "Total Pagado (S/)", "Total Cotización (S/)", "Saldo Pendiente (S/)"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }
            // Llenar datos
            int rowNum = 1;
            for (Object[] row : data) {
                Row excelRow = sheet.createRow(rowNum++);
                excelRow.createCell(0).setCellValue(((Number) row[0]).longValue());
                excelRow.createCell(1).setCellValue((String) row[1]);
                excelRow.createCell(2).setCellValue(((BigDecimal) row[2]).doubleValue());
                excelRow.createCell(3).setCellValue(((BigDecimal) row[3]).doubleValue());
                excelRow.createCell(4).setCellValue(((BigDecimal) row[4]).doubleValue());
            }
            // Autoajustar columnas
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(os);
        }
    }

    /**
     * Exporta el reporte de proyectos atrasados.
     *
     * @param proyectos Lista de proyectos atrasados
     * @param os        OutputStream donde se escribirá el archivo Excel
     * @throws IOException si ocurre un error al escribir
     */
    public static void exportProyectosAtrasados(List<Proyecto> proyectos, OutputStream os) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Proyectos Atrasados");
            CellStyle headerStyle = createHeaderStyle(workbook);
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Cliente", "Estado", "Fecha Entrega Estimada", "Responsable"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }
            int rowNum = 1;
            for (Proyecto p : proyectos) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(p.getId());
                row.createCell(1).setCellValue(p.getCliente().getNombreRazonSocial());
                row.createCell(2).setCellValue(p.getEstado());
                row.createCell(3).setCellValue(p.getFechaEntregaEstimada() != null ? p.getFechaEntregaEstimada().format(DATE_FORMATTER) : "");
                row.createCell(4).setCellValue(p.getResponsable() != null ? p.getResponsable().getNombre() : "");
            }
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(os);
        }
    }

    /**
     * Exporta el reporte de equipos más alquilados.
     *
     * @param data Lista de Object[] con: [equipoId, nombreEquipo, cantidadTotalAlquilada]
     * @param os   OutputStream
     * @throws IOException si ocurre un error
     */
    public static void exportEquiposMasAlquilados(List<Object[]> data, OutputStream os) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Equipos Más Alquilados");
            CellStyle headerStyle = createHeaderStyle(workbook);
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Equipo ID", "Nombre", "Cantidad Total Alquilada"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }
            int rowNum = 1;
            for (Object[] row : data) {
                Row excelRow = sheet.createRow(rowNum++);
                excelRow.createCell(0).setCellValue(((Number) row[0]).longValue());
                excelRow.createCell(1).setCellValue((String) row[1]);
                excelRow.createCell(2).setCellValue(((Number) row[2]).intValue());
            }
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(os);
        }
    }

    /**
     * Exporta el reporte de ingresos por período.
     *
     * @param pagos Lista de pagos en el período
     * @param titulo Título del reporte (ej. "Ingresos - Enero 2025")
     * @param os OutputStream
     * @throws IOException si ocurre un error
     */
    public static void exportIngresos(List<Pago> pagos, String titulo, OutputStream os) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Ingresos");
            CellStyle headerStyle = createHeaderStyle(workbook);
            // Fila de título
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(titulo);
            titleCell.setCellStyle(headerStyle);
            // Fila de encabezados
            Row headerRow = sheet.createRow(1);
            String[] columns = {"Fecha", "Proyecto ID", "Cliente", "Monto (S/)", "Medio de Pago"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }
            int rowNum = 2;
            BigDecimal total = BigDecimal.ZERO;
            for (Pago p : pagos) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(p.getFecha().format(DATE_FORMATTER));
                row.createCell(1).setCellValue(p.getProyecto().getId());
                row.createCell(2).setCellValue(p.getProyecto().getCliente().getNombreRazonSocial());
                row.createCell(3).setCellValue(p.getMonto().doubleValue());
                row.createCell(4).setCellValue(p.getMedioPago());
                total = total.add(p.getMonto());
            }
            // Fila de total
            Row totalRow = sheet.createRow(rowNum);
            totalRow.createCell(3).setCellValue("Total:");
            totalRow.createCell(4).setCellValue(total.doubleValue());
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(os);
        }
    }

    /**
     * Exporta el reporte de cotizaciones aprobadas vs rechazadas.
     *
     * @param estadisticas Mapa con llaves "APROBADAS" y "RECHAZADAS"
     * @param os OutputStream
     * @throws IOException si ocurre un error
     */
    public static void exportCotizacionesEstado(Map<String, Long> estadisticas, OutputStream os) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Cotizaciones Estado");
            CellStyle headerStyle = createHeaderStyle(workbook);
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Estado");
            headerRow.createCell(1).setCellValue("Cantidad");
            headerRow.getCell(0).setCellStyle(headerStyle);
            headerRow.getCell(1).setCellStyle(headerStyle);
            Row row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue("APROBADAS");
            row1.createCell(1).setCellValue(estadisticas.getOrDefault("APROBADAS", 0L));
            Row row2 = sheet.createRow(2);
            row2.createCell(0).setCellValue("RECHAZADAS");
            row2.createCell(1).setCellValue(estadisticas.getOrDefault("RECHAZADAS", 0L));
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            workbook.write(os);
        }
    }

    // Método privado para crear estilo de encabezado
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}