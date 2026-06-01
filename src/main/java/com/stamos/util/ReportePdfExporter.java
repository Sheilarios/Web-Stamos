package com.stamos.util;

import com.stamos.entity.Pago;
import com.stamos.entity.Proyecto;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public final class ReportePdfExporter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
    private static final Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10);

    private ReportePdfExporter() {}

    public static void exportPagosPendientes(List<Object[]> data, OutputStream os) throws DocumentException, IOException {
        try (Document document = new Document(PageSize.A4)) {
            PdfWriter.getInstance(document, os);
            document.open();
            addTitle(document, "Reporte de Pagos Pendientes por Cliente");
            addDate(document);
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1, 3, 2, 2, 2});
            addTableHeader(table, "Cliente ID", "Cliente", "Total Pagado (S/)", "Total Cotización (S/)", "Saldo Pendiente (S/)");
            for (Object[] row : data) {
                table.addCell(createCell(row[0].toString(), NORMAL_FONT));
                table.addCell(createCell((String) row[1], NORMAL_FONT));
                table.addCell(createCell(formatCurrency((BigDecimal) row[2]), NORMAL_FONT));
                table.addCell(createCell(formatCurrency((BigDecimal) row[3]), NORMAL_FONT));
                table.addCell(createCell(formatCurrency((BigDecimal) row[4]), NORMAL_FONT));
            }
            document.add(table);
        }
    }

    public static void exportProyectosAtrasados(List<Proyecto> proyectos, OutputStream os) throws DocumentException, IOException {
        try (Document document = new Document(PageSize.A4.rotate())) {
            PdfWriter.getInstance(document, os);
            document.open();
            addTitle(document, "Proyectos Atrasados");
            addDate(document);
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            addTableHeader(table, "ID", "Cliente", "Estado", "Fecha Entrega Estimada", "Responsable");
            for (Proyecto p : proyectos) {
                table.addCell(createCell(String.valueOf(p.getId()), NORMAL_FONT));
                table.addCell(createCell(p.getCliente().getNombreRazonSocial(), NORMAL_FONT));
                table.addCell(createCell(p.getEstado(), NORMAL_FONT));
                String fecha = p.getFechaEntregaEstimada() != null ? p.getFechaEntregaEstimada().format(DATE_FORMATTER) : "";
                table.addCell(createCell(fecha, NORMAL_FONT));
                String responsable = p.getResponsable() != null ? p.getResponsable().getNombre() : "";
                table.addCell(createCell(responsable, NORMAL_FONT));
            }
            document.add(table);
        }
    }

    public static void exportEquiposMasAlquilados(List<Object[]> data, OutputStream os) throws DocumentException, IOException {
        try (Document document = new Document(PageSize.A4)) {
            PdfWriter.getInstance(document, os);
            document.open();
            addTitle(document, "Equipos Más Alquilados");
            addDate(document);
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            addTableHeader(table, "Equipo ID", "Nombre", "Cantidad Total Alquilada");
            for (Object[] row : data) {
                table.addCell(createCell(row[0].toString(), NORMAL_FONT));
                table.addCell(createCell((String) row[1], NORMAL_FONT));
                table.addCell(createCell(String.valueOf(row[2]), NORMAL_FONT));
            }
            document.add(table);
        }
    }

    public static void exportIngresos(List<Pago> pagos, String titulo, OutputStream os) throws DocumentException, IOException {
        try (Document document = new Document(PageSize.A4.rotate())) {
            PdfWriter.getInstance(document, os);
            document.open();
            addTitle(document, titulo);
            addDate(document);
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            addTableHeader(table, "Fecha", "Proyecto ID", "Cliente", "Monto (S/)", "Medio de Pago");
            BigDecimal total = BigDecimal.ZERO;
            for (Pago p : pagos) {
                table.addCell(createCell(p.getFecha().format(DATE_FORMATTER), NORMAL_FONT));
                table.addCell(createCell(String.valueOf(p.getProyecto().getId()), NORMAL_FONT));
                table.addCell(createCell(p.getProyecto().getCliente().getNombreRazonSocial(), NORMAL_FONT));
                table.addCell(createCell(formatCurrency(p.getMonto()), NORMAL_FONT));
                table.addCell(createCell(p.getMedioPago(), NORMAL_FONT));
                total = total.add(p.getMonto());
            }
            PdfPCell totalLabel = createCell("Total:", HEADER_FONT);
            totalLabel.setColspan(3);
            totalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(totalLabel);
            table.addCell(createCell(formatCurrency(total), NORMAL_FONT));
            table.addCell(createCell("", NORMAL_FONT));
            document.add(table);
        }
    }

    public static void exportCotizacionesEstado(Map<String, Long> estadisticas, OutputStream os) throws DocumentException, IOException {
        try (Document document = new Document(PageSize.A4)) {
            PdfWriter.getInstance(document, os);
            document.open();
            addTitle(document, "Cotizaciones Aprobadas vs Rechazadas");
            addDate(document);
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(50);
            addTableHeader(table, "Estado", "Cantidad");
            table.addCell(createCell("APROBADAS", NORMAL_FONT));
            table.addCell(createCell(String.valueOf(estadisticas.getOrDefault("APROBADAS", 0L)), NORMAL_FONT));
            table.addCell(createCell("RECHAZADAS", NORMAL_FONT));
            table.addCell(createCell(String.valueOf(estadisticas.getOrDefault("RECHAZADAS", 0L)), NORMAL_FONT));
            document.add(table);
        }
    }

    private static void addTitle(Document document, String title) throws DocumentException {
        Paragraph p = new Paragraph(title, TITLE_FONT);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(20);
        document.add(p);
    }

    private static void addDate(Document document) throws DocumentException {
        Paragraph date = new Paragraph("Fecha de generación: " + LocalDate.now().format(DATE_FORMATTER), NORMAL_FONT);
        date.setAlignment(Element.ALIGN_RIGHT);
        date.setSpacingAfter(15);
        document.add(date);
    }

    private static void addTableHeader(PdfPTable table, String... headers) {
        for (String header : headers) {
            PdfPCell cell = createCell(header, HEADER_FONT);
            // Se eliminó setBackgroundColor para evitar errores de tipo
            table.addCell(cell);
        }
    }

    private static PdfPCell createCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        return cell;
    }

    private static String formatCurrency(BigDecimal amount) {
        return String.format("S/ %.2f", amount);
    }
}