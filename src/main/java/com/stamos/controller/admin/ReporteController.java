package com.stamos.controller.admin;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.stamos.controller.BaseController;
import com.stamos.model.*;
import com.stamos.service.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/reportes")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class ReporteController extends BaseController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private CotizacionService cotizacionService;

    @Autowired
    private PagoService pagoService;

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CambioEstadoProyectoService cambioEstadoProyectoService;

    @GetMapping
    public String mostrarPaginaReportes(Model model, Authentication auth) {
        String username = auth.getName();
        model.addAttribute("username", username);
        model.addAttribute("rol", getRolUsuario());
        addCommonAttributes(model);
        return "admin/reportes";
    }

    @GetMapping("/datos")
    @ResponseBody
    public List<Map<String, Object>> getDatosReporte(
            @RequestParam String entidad,
            @RequestParam List<String> columnas) {

        List<Map<String, Object>> resultado = new ArrayList<>();

        switch (entidad) {
            case "clientes" -> {
                List<Cliente> clientes = clienteService.findAll();
                for (Cliente c : clientes) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (String col : columnas) {
                        switch (col) {
                            case "ID" -> row.put("ID", c.getIdCliente());
                            case "Nombre" -> row.put("Nombre", c.getNombreRazonSocial());
                            case "RUC" -> row.put("RUC", c.getRuc() != null ? c.getRuc() : "-");
                            case "Telefono" -> row.put("Telefono", c.getTelefono());
                            case "Email" -> row.put("Email", c.getEmail());
                            case "Estado" -> row.put("Estado", c.getEstado());
                            case "Tipo" -> row.put("Tipo", c.getTipoPersona());
                            default -> row.put(col, "");
                        }
                    }
                    resultado.add(row);
                }
            }
            case "proyectos" -> {
                List<Proyecto> proyectos = proyectoService.findAll();
                for (Proyecto p : proyectos) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (String col : columnas) {
                        switch (col) {
                            case "ID" -> row.put("ID", p.getIdProyecto());
                            case "Proyecto" -> row.put("Proyecto", p.getNombreProyecto());
                            case "Cliente" -> row.put("Cliente", p.getNombreCliente() != null ? p.getNombreCliente() : "N/A");
                            case "Estado" -> row.put("Estado", p.getEstado());
                            case "Monto" -> row.put("Monto", p.getMontoTotal());
                            case "Fecha Inicio" -> row.put("Fecha Inicio", p.getFechaInicio() != null ? p.getFechaInicio().toString() : "-");
                            case "Fecha Fin Estimada" -> row.put("Fecha Fin Estimada", p.getFechaFinEstimada() != null ? p.getFechaFinEstimada().toString() : "-");
                            case "Fecha Entrega" -> row.put("Fecha Entrega", p.getFechaEntregaReal() != null ? p.getFechaEntregaReal().toString() : "-");
                            default -> row.put(col, "");
                        }
                    }
                    resultado.add(row);
                }
            }
            case "cotizaciones" -> {
                List<Cotizacion> cotizaciones = cotizacionService.findAll(Pageable.unpaged()).getContent();
                for (Cotizacion c : cotizaciones) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (String col : columnas) {
                        switch (col) {
                            case "ID" -> row.put("ID", c.getIdCotizacion());
                            case "Codigo" -> row.put("Codigo", c.getCodigoCotizacion());
                            case "Cliente" -> row.put("Cliente", c.getNombreCliente() != null ? c.getNombreCliente() : "N/A");
                            case "Vendedor" -> row.put("Vendedor", c.getNombreVendedor() != null ? c.getNombreVendedor() : "N/A");
                            case "Fecha Emision" -> row.put("Fecha Emision", c.getFechaEmision() != null ? c.getFechaEmision().toString() : "-");
                            case "Estado" -> row.put("Estado", c.getEstado());
                            case "Total" -> row.put("Total", c.getTotal());
                            default -> row.put(col, "");
                        }
                    }
                    resultado.add(row);
                }
            }
            case "pagos" -> {
                List<Pago> pagos = pagoService.findAll(Pageable.unpaged()).getContent();
                for (Pago p : pagos) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (String col : columnas) {
                        switch (col) {
                            case "ID" -> row.put("ID", p.getIdPago());
                            case "Proyecto" -> row.put("Proyecto", p.getNombreProyecto() != null ? p.getNombreProyecto() : "N/A");
                            case "Monto" -> row.put("Monto", p.getMonto());
                            case "Metodo" -> row.put("Metodo", p.getMetodoPago());
                            case "Fecha Pago" -> row.put("Fecha Pago", p.getFechaPago() != null ? p.getFechaPago().toString() : "-");
                            case "Estado" -> row.put("Estado", p.getEstadoPago());
                            default -> row.put(col, "");
                        }
                    }
                    resultado.add(row);
                }
            }
            case "inventario" -> {
                List<Inventario> inventario = inventarioService.findAll(Pageable.unpaged()).getContent();
                for (Inventario i : inventario) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (String col : columnas) {
                        switch (col) {
                            case "ID" -> row.put("ID", i.getIdInventario());
                            case "Producto" -> row.put("Producto", i.getNombreProducto() != null ? i.getNombreProducto() : "N/A");
                            case "Serial" -> row.put("Serial", i.getCodigoSerial());
                            case "Ubicacion" -> row.put("Ubicacion", i.getUbicacion() != null ? i.getUbicacion() : "-");
                            case "Estado" -> row.put("Estado", i.getEstadoFisico());
                            case "Valor" -> row.put("Valor", i.getValorCompra());
                            default -> row.put(col, "");
                        }
                    }
                    resultado.add(row);
                }
            }
            case "productos" -> {
                List<Producto> productos = productoService.findAll();
                for (Producto p : productos) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (String col : columnas) {
                        switch (col) {
                            case "ID" -> row.put("ID", p.getIdProducto());
                            case "Nombre" -> row.put("Nombre", p.getNombreItem());
                            case "Categoria" -> row.put("Categoria", p.getNombreCategoria() != null ? p.getNombreCategoria() : "N/A");
                            case "Tipo" -> row.put("Tipo", p.getTipo());
                            case "Precio Base" -> row.put("Precio Base", p.getPrecioBase());
                            case "Activo" -> row.put("Activo", p.getActivo() ? "Si" : "No");
                            default -> row.put(col, "");
                        }
                    }
                    resultado.add(row);
                }
            }
            default -> {
                // entidad no soportada
            }
        }
        return resultado;
    }

    @GetMapping("/datos/excel")
    public ResponseEntity<InputStreamResource> exportarExcelPersonalizado(
            @RequestParam String entidad,
            @RequestParam List<String> columnas) throws Exception {

        List<Map<String, Object>> datos = getDatosReporte(entidad, columnas);

        if (datos.isEmpty() || columnas.isEmpty()) {
            throw new RuntimeException("No hay datos para exportar");
        }

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Reporte");

            // Estilo de cabecera
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columnas.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas.get(i));
                cell.setCellStyle(headerStyle);
            }

            // Datos
            int rowNum = 1;
            for (Map<String, Object> rowData : datos) {
                Row row = sheet.createRow(rowNum++);
                int colNum = 0;
                for (String col : columnas) {
                    Object value = rowData.get(col);
                    Cell cell = row.createCell(colNum++);
                    if (value instanceof Number) {
                        cell.setCellValue(((Number) value).doubleValue());
                    } else if (value instanceof Boolean) {
                        cell.setCellValue((Boolean) value);
                    } else {
                        cell.setCellValue(value != null ? value.toString() : "");
                    }
                }
            }

            // Auto ajustar ancho
            for (int i = 0; i < columnas.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=reporte_" + entidad + "_" + LocalDate.now() + ".xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(input));
        }
    }

    @GetMapping("/datos/pdf")
    public ResponseEntity<InputStreamResource> exportarPdfPersonalizado(
            @RequestParam String entidad,
            @RequestParam List<String> columnas) throws Exception {

        List<Map<String, Object>> datos = getDatosReporte(entidad, columnas);

        if (datos.isEmpty() || columnas.isEmpty()) {
            throw new RuntimeException("No hay datos para exportar");
        }

        Document documento = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(documento, out);
        documento.open();

        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font subtituloFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

        documento.add(new Paragraph("REPORTE PERSONALIZADO", tituloFont));
        documento.add(new Paragraph("Entidad: " + entidad, subtituloFont));
        documento.add(new Paragraph("Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), subtituloFont));
        documento.add(new Paragraph(" "));

        PdfPTable tabla = new PdfPTable(columnas.size());
        tabla.setWidthPercentage(100);

        // Cabecera
        for (String col : columnas) {
            tabla.addCell(col);
        }

        // Datos
        for (Map<String, Object> rowData : datos) {
            for (String col : columnas) {
                Object value = rowData.get(col);
                tabla.addCell(value != null ? value.toString() : "");
            }
        }

        documento.add(tabla);
        documento.close();

        ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=reporte_" + entidad + "_" + LocalDate.now() + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(input));
    }

    // ========== REPORTES PREDEFINIDOS (PDF) ==========

    @GetMapping("/clientes")
    public ResponseEntity<InputStreamResource> reporteClientes() throws Exception {
        List<Cliente> clientes = clienteService.findAll();

        Document documento = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(documento, out);
        documento.open();

        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        documento.add(new Paragraph("REPORTE DE CLIENTES", tituloFont));
        documento.add(new Paragraph("Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        documento.add(new Paragraph(" "));

        PdfPTable tabla = new PdfPTable(6);
        tabla.setWidthPercentage(100);
        tabla.addCell("ID");
        tabla.addCell("Nombre / Razon Social");
        tabla.addCell("RUC");
        tabla.addCell("Telefono");
        tabla.addCell("Email");
        tabla.addCell("Estado");

        for (Cliente c : clientes) {
            tabla.addCell(String.valueOf(c.getIdCliente()));
            tabla.addCell(c.getNombreRazonSocial());
            tabla.addCell(c.getRuc() != null ? c.getRuc() : "-");
            tabla.addCell(c.getTelefono());
            tabla.addCell(c.getEmail());
            tabla.addCell(c.getEstado());
        }

        documento.add(tabla);
        documento.close();
        return buildPdfResponse(out, "clientes.pdf");
    }

    @GetMapping("/proyectos")
    public ResponseEntity<InputStreamResource> reporteProyectos(
            @RequestParam(required = false) String estado) throws Exception {

        List<Proyecto> proyectos;
        if (estado != null && !estado.isEmpty()) {
            proyectos = proyectoService.findByEstado(estado);
        } else {
            proyectos = proyectoService.findAll();
        }

        Document documento = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(documento, out);
        documento.open();

        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        documento.add(new Paragraph("REPORTE DE PROYECTOS", tituloFont));
        documento.add(new Paragraph("Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        if (estado != null) {
            documento.add(new Paragraph("Filtro por estado: " + estado));
        }
        documento.add(new Paragraph(" "));

        PdfPTable tabla = new PdfPTable(7);
        tabla.setWidthPercentage(100);
        tabla.addCell("ID");
        tabla.addCell("Nombre");
        tabla.addCell("Cliente");
        tabla.addCell("Estado");
        tabla.addCell("Fecha Inicio");
        tabla.addCell("Monto Total");
        tabla.addCell("Pagado");

        for (Proyecto p : proyectos) {
            BigDecimal pagado = pagoService.getTotalPagadoByProyecto(p.getIdProyecto());
            if (pagado == null) pagado = BigDecimal.ZERO;

            tabla.addCell(String.valueOf(p.getIdProyecto()));
            tabla.addCell(p.getNombreProyecto());
            tabla.addCell(p.getNombreCliente() != null ? p.getNombreCliente() : "N/A");
            tabla.addCell(p.getEstado());
            tabla.addCell(p.getFechaInicio() != null ? p.getFechaInicio().toString() : "-");
            tabla.addCell(formatearMoneda(p.getMontoTotal()));
            tabla.addCell(formatearMoneda(pagado));
        }

        documento.add(tabla);
        documento.close();
        return buildPdfResponse(out, "proyectos.pdf");
    }

    @GetMapping("/cotizaciones")
    public ResponseEntity<InputStreamResource> reporteCotizaciones(
            @RequestParam(required = false) String estado) throws Exception {

        List<Cotizacion> cotizaciones;
        if (estado != null && !estado.isEmpty()) {
            cotizaciones = cotizacionService.findByEstado(estado, Pageable.unpaged()).getContent();
        } else {
            cotizaciones = cotizacionService.findAll(Pageable.unpaged()).getContent();
        }

        Document documento = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(documento, out);
        documento.open();

        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        documento.add(new Paragraph("REPORTE DE COTIZACIONES", tituloFont));
        documento.add(new Paragraph("Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        if (estado != null) {
            documento.add(new Paragraph("Filtro por estado: " + estado));
        }
        documento.add(new Paragraph(" "));

        PdfPTable tabla = new PdfPTable(6);
        tabla.setWidthPercentage(100);
        tabla.addCell("ID");
        tabla.addCell("Cliente");
        tabla.addCell("Vendedor");
        tabla.addCell("Fecha Emision");
        tabla.addCell("Estado");
        tabla.addCell("Total");

        for (Cotizacion c : cotizaciones) {
            tabla.addCell(String.valueOf(c.getIdCotizacion()));
            tabla.addCell(c.getNombreCliente() != null ? c.getNombreCliente() : "N/A");
            tabla.addCell(c.getNombreVendedor() != null ? c.getNombreVendedor() : "N/A");
            tabla.addCell(c.getFechaEmisionFormateada());
            tabla.addCell(c.getEstado());
            tabla.addCell(formatearMoneda(c.getTotal()));
        }

        documento.add(tabla);
        documento.close();
        return buildPdfResponse(out, "cotizaciones.pdf");
    }

    @GetMapping("/pagos")
    public ResponseEntity<InputStreamResource> reportePagos(
            @RequestParam(required = false) String estado) throws Exception {

        List<Pago> pagos;
        if (estado != null && !estado.isEmpty()) {
            pagos = pagoService.findByEstadoPago(estado, Pageable.unpaged()).getContent();
        } else {
            pagos = pagoService.findAll(Pageable.unpaged()).getContent();
        }

        Document documento = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(documento, out);
        documento.open();

        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        documento.add(new Paragraph("REPORTE DE PAGOS", tituloFont));
        documento.add(new Paragraph("Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        if (estado != null) {
            documento.add(new Paragraph("Filtro por estado: " + estado));
        }
        documento.add(new Paragraph(" "));

        PdfPTable tabla = new PdfPTable(6);
        tabla.setWidthPercentage(100);
        tabla.addCell("ID");
        tabla.addCell("Proyecto");
        tabla.addCell("Monto");
        tabla.addCell("Metodo");
        tabla.addCell("Fecha");
        tabla.addCell("Estado");

        for (Pago p : pagos) {
            tabla.addCell(String.valueOf(p.getIdPago()));
            tabla.addCell(p.getNombreProyecto() != null ? p.getNombreProyecto() : "N/A");
            tabla.addCell(formatearMoneda(p.getMonto()));
            tabla.addCell(p.getMetodoPago());
            tabla.addCell(p.getFechaPagoFormateada());
            tabla.addCell(p.getEstadoPago());
        }

        documento.add(tabla);
        documento.close();
        return buildPdfResponse(out, "pagos.pdf");
    }

    @GetMapping("/inventario")
    public ResponseEntity<InputStreamResource> reporteInventario(
            @RequestParam(required = false) String estado) throws Exception {

        List<Inventario> inventario;
        if (estado != null && !estado.isEmpty()) {
            inventario = inventarioService.findByEstadoFisico(estado, Pageable.unpaged()).getContent();
        } else {
            inventario = inventarioService.findAll(Pageable.unpaged()).getContent();
        }

        Document documento = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(documento, out);
        documento.open();

        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        documento.add(new Paragraph("REPORTE DE INVENTARIO", tituloFont));
        documento.add(new Paragraph("Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        if (estado != null) {
            documento.add(new Paragraph("Filtro por estado: " + estado));
        }
        documento.add(new Paragraph(" "));

        PdfPTable tabla = new PdfPTable(6);
        tabla.setWidthPercentage(100);
        tabla.addCell("ID");
        tabla.addCell("Producto");
        tabla.addCell("Serial");
        tabla.addCell("Ubicacion");
        tabla.addCell("Estado Fisico");
        tabla.addCell("Valor Compra");

        for (Inventario i : inventario) {
            tabla.addCell(String.valueOf(i.getIdInventario()));
            tabla.addCell(i.getNombreProducto() != null ? i.getNombreProducto() : "N/A");
            tabla.addCell(i.getCodigoSerial());
            tabla.addCell(i.getUbicacion() != null ? i.getUbicacion() : "-");
            tabla.addCell(i.getEstadoFisico());
            tabla.addCell(formatearMoneda(i.getValorCompra()));
        }

        documento.add(tabla);
        documento.close();
        return buildPdfResponse(out, "inventario.pdf");
    }

    @GetMapping("/productos")
    public ResponseEntity<InputStreamResource> reporteProductos(
            @RequestParam(required = false) String tipo) throws Exception {

        List<Producto> productos;
        if (tipo != null && !tipo.isEmpty()) {
            productos = productoService.findByTipo(tipo);
        } else {
            productos = productoService.findAll();
        }

        Document documento = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(documento, out);
        documento.open();

        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        documento.add(new Paragraph("REPORTE DE PRODUCTOS/SERVICIOS", tituloFont));
        documento.add(new Paragraph("Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        if (tipo != null) {
            documento.add(new Paragraph("Filtro por tipo: " + tipo));
        }
        documento.add(new Paragraph(" "));

        PdfPTable tabla = new PdfPTable(5);
        tabla.setWidthPercentage(100);
        tabla.addCell("ID");
        tabla.addCell("Nombre");
        tabla.addCell("Categoria");
        tabla.addCell("Tipo");
        tabla.addCell("Precio Base");

        for (Producto p : productos) {
            tabla.addCell(String.valueOf(p.getIdProducto()));
            tabla.addCell(p.getNombreItem());
            tabla.addCell(p.getNombreCategoria() != null ? p.getNombreCategoria() : "N/A");
            tabla.addCell(p.getTipo());
            tabla.addCell(formatearMoneda(p.getPrecioBase()));
        }

        documento.add(tabla);
        documento.close();
        return buildPdfResponse(out, "productos.pdf");
    }

    @GetMapping("/resumen-financiero-clientes")
    public ResponseEntity<InputStreamResource> reporteResumenFinancieroClientes() throws Exception {
        List<Cliente> clientes = clienteService.findAll();

        Document documento = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(documento, out);
        documento.open();

        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        documento.add(new Paragraph("RESUMEN FINANCIERO POR CLIENTE", tituloFont));
        documento.add(new Paragraph("Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        documento.add(new Paragraph(" "));

        PdfPTable tabla = new PdfPTable(5);
        tabla.setWidthPercentage(100);
        tabla.addCell("Cliente");
        tabla.addCell("Total Cotizado");
        tabla.addCell("Total Pagado");
        tabla.addCell("Saldo Pendiente");
        tabla.addCell("Proyectos");

        for (Cliente c : clientes) {
            BigDecimal totalCotizado = proyectoService.sumMontoTotalByCliente(c.getIdCliente());
            if (totalCotizado == null) totalCotizado = BigDecimal.ZERO;
            BigDecimal totalPagado = pagoService.getTotalPagadoByCliente(c.getIdCliente());
            if (totalPagado == null) totalPagado = BigDecimal.ZERO;
            BigDecimal saldo = totalCotizado.subtract(totalPagado);
            if (saldo.compareTo(BigDecimal.ZERO) < 0) saldo = BigDecimal.ZERO;
            long cantidadProyectos = proyectoService.findByCliente(c.getIdCliente()).size();

            tabla.addCell(c.getNombreRazonSocial());
            tabla.addCell(formatearMoneda(totalCotizado));
            tabla.addCell(formatearMoneda(totalPagado));
            tabla.addCell(formatearMoneda(saldo));
            tabla.addCell(String.valueOf(cantidadProyectos));
        }

        documento.add(tabla);
        documento.close();
        return buildPdfResponse(out, "resumen_financiero_clientes.pdf");
    }

    @GetMapping("/proyectos-vencidos")
    public ResponseEntity<InputStreamResource> reporteProyectosVencidos() throws Exception {
        List<Proyecto> proyectosVencidos = proyectoService.findProyectosConPagosVencidos();

        Document documento = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(documento, out);
        documento.open();

        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        documento.add(new Paragraph("PROYECTOS CON PAGOS VENCIDOS", tituloFont));
        documento.add(new Paragraph("Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        documento.add(new Paragraph(" "));

        if (proyectosVencidos.isEmpty()) {
            documento.add(new Paragraph("No hay proyectos con pagos vencidos."));
        } else {
            PdfPTable tabla = new PdfPTable(6);
            tabla.setWidthPercentage(100);
            tabla.addCell("ID");
            tabla.addCell("Proyecto");
            tabla.addCell("Cliente");
            tabla.addCell("Monto Total");
            tabla.addCell("Saldo Pendiente");
            tabla.addCell("Dias Vencidos");

            for (Proyecto p : proyectosVencidos) {
                BigDecimal totalPagado = pagoService.getTotalPagadoByProyecto(p.getIdProyecto());
                if (totalPagado == null) totalPagado = BigDecimal.ZERO;
                BigDecimal saldo = p.getMontoTotal().subtract(totalPagado);
                if (saldo.compareTo(BigDecimal.ZERO) < 0) saldo = BigDecimal.ZERO;

                int diasVencidos = 0;
                if (p.getFechaVencimiento() != null) {
                    diasVencidos = (int) java.time.temporal.ChronoUnit.DAYS.between(p.getFechaVencimiento(), LocalDate.now());
                    if (diasVencidos < 0) diasVencidos = 0;
                }

                tabla.addCell(String.valueOf(p.getIdProyecto()));
                tabla.addCell(p.getNombreProyecto());
                tabla.addCell(p.getNombreCliente() != null ? p.getNombreCliente() : "N/A");
                tabla.addCell(formatearMoneda(p.getMontoTotal()));
                tabla.addCell(formatearMoneda(saldo));
                tabla.addCell(String.valueOf(diasVencidos));
            }
            documento.add(tabla);
        }

        documento.close();
        return buildPdfResponse(out, "proyectos_vencidos.pdf");
    }

    @GetMapping("/proyecto/{idProyecto}")
    public ResponseEntity<InputStreamResource> reporteProyectoDetalle(@PathVariable Integer idProyecto) throws Exception {
        Proyecto proyecto = proyectoService.findById(idProyecto);
        if (proyecto == null) {
            throw new RuntimeException("Proyecto no encontrado con ID: " + idProyecto);
        }

        Cotizacion cotizacion = cotizacionService.findById(proyecto.getIdCotizacion());
        List<CotizacionDetalle> detalles = cotizacionService.getDetallesByCotizacion(proyecto.getIdCotizacion());
        List<Pago> pagos = pagoService.findByProyecto(idProyecto);
        List<CambioEstadoProyecto> historial = cambioEstadoProyectoService.findByProyecto(idProyecto);

        Document documento = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(documento, out);
        documento.open();

        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font subtituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

        documento.add(new Paragraph("REPORTE DETALLADO DE PROYECTO", tituloFont));
        documento.add(new Paragraph("Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        documento.add(new Paragraph(" "));

        documento.add(new Paragraph("1. INFORMACION DEL PROYECTO", subtituloFont));
        PdfPTable infoProyecto = new PdfPTable(2);
        infoProyecto.setWidthPercentage(100);
        infoProyecto.addCell("ID Proyecto:");
        infoProyecto.addCell(String.valueOf(proyecto.getIdProyecto()));
        infoProyecto.addCell("Nombre:");
        infoProyecto.addCell(proyecto.getNombreProyecto());
        infoProyecto.addCell("Cliente:");
        infoProyecto.addCell(proyecto.getNombreCliente() != null ? proyecto.getNombreCliente() : "N/A");
        infoProyecto.addCell("Estado:");
        infoProyecto.addCell(proyecto.getEstado());
        infoProyecto.addCell("Fecha Inicio:");
        infoProyecto.addCell(proyecto.getFechaInicio() != null ? proyecto.getFechaInicio().toString() : "-");
        infoProyecto.addCell("Fecha Vencimiento:");
        infoProyecto.addCell(proyecto.getFechaVencimiento() != null ? proyecto.getFechaVencimiento().toString() : "-");
        infoProyecto.addCell("Monto Total:");
        infoProyecto.addCell(formatearMoneda(proyecto.getMontoTotal()));
        BigDecimal totalPagado = pagoService.getTotalPagadoByProyecto(idProyecto);
        infoProyecto.addCell("Total Pagado:");
        infoProyecto.addCell(formatearMoneda(totalPagado != null ? totalPagado : BigDecimal.ZERO));
        BigDecimal saldo = proyecto.getMontoTotal().subtract(totalPagado != null ? totalPagado : BigDecimal.ZERO);
        if (saldo.compareTo(BigDecimal.ZERO) < 0) saldo = BigDecimal.ZERO;
        infoProyecto.addCell("Saldo Pendiente:");
        infoProyecto.addCell(formatearMoneda(saldo));
        documento.add(infoProyecto);
        documento.add(new Paragraph(" "));

        if (cotizacion != null) {
            documento.add(new Paragraph("2. COTIZACION (" + cotizacion.getCodigoCotizacion() + ")", subtituloFont));
            PdfPTable infoCotizacion = new PdfPTable(2);
            infoCotizacion.setWidthPercentage(100);
            infoCotizacion.addCell("Subtotal:");
            infoCotizacion.addCell(formatearMoneda(cotizacion.getSubtotal()));
            infoCotizacion.addCell("IGV (" + cotizacion.getTasaIgv() + "%):");
            infoCotizacion.addCell(formatearMoneda(cotizacion.getIgv()));
            infoCotizacion.addCell("Total:");
            infoCotizacion.addCell(formatearMoneda(cotizacion.getTotal()));
            documento.add(infoCotizacion);
            documento.add(new Paragraph(" "));

            if (detalles != null && !detalles.isEmpty()) {
                documento.add(new Paragraph("Productos y Servicios:", normalFont));
                PdfPTable tablaDetalles = new PdfPTable(5);
                tablaDetalles.setWidthPercentage(100);
                tablaDetalles.addCell("Producto");
                tablaDetalles.addCell("Cantidad");
                tablaDetalles.addCell("Precio Unit.");
                tablaDetalles.addCell("Descuento");
                tablaDetalles.addCell("Subtotal");

                for (CotizacionDetalle d : detalles) {
                    tablaDetalles.addCell(d.getNombreProducto() != null ? d.getNombreProducto() : "N/A");
                    tablaDetalles.addCell(String.valueOf(d.getCantidad()));
                    tablaDetalles.addCell(formatearMoneda(d.getPrecioUnitario()));
                    tablaDetalles.addCell(formatearMoneda(d.getDescuento()));
                    tablaDetalles.addCell(formatearMoneda(d.getSubtotalLinea()));
                }
                documento.add(tablaDetalles);
                documento.add(new Paragraph(" "));
            }
        }

        if (pagos != null && !pagos.isEmpty()) {
            documento.add(new Paragraph("3. PAGOS REGISTRADOS", subtituloFont));
            PdfPTable tablaPagos = new PdfPTable(5);
            tablaPagos.setWidthPercentage(100);
            tablaPagos.addCell("ID");
            tablaPagos.addCell("Monto");
            tablaPagos.addCell("Metodo");
            tablaPagos.addCell("Fecha");
            tablaPagos.addCell("Estado");

            for (Pago p : pagos) {
                tablaPagos.addCell(String.valueOf(p.getIdPago()));
                tablaPagos.addCell(formatearMoneda(p.getMonto()));
                tablaPagos.addCell(p.getMetodoPago());
                tablaPagos.addCell(p.getFechaPagoFormateada());
                tablaPagos.addCell(p.getEstadoPago());
            }
            documento.add(tablaPagos);
            documento.add(new Paragraph(" "));
        } else {
            documento.add(new Paragraph("3. PAGOS REGISTRADOS: No hay pagos registrados.", normalFont));
            documento.add(new Paragraph(" "));
        }

        if (historial != null && !historial.isEmpty()) {
            documento.add(new Paragraph("4. HISTORIAL DE CAMBIOS DE ESTADO", subtituloFont));
            PdfPTable tablaHistorial = new PdfPTable(4);
            tablaHistorial.setWidthPercentage(100);
            tablaHistorial.addCell("Estado Anterior");
            tablaHistorial.addCell("Estado Nuevo");
            tablaHistorial.addCell("Responsable");
            tablaHistorial.addCell("Fecha");

            for (CambioEstadoProyecto c : historial) {
                tablaHistorial.addCell(c.getEstadoAnteriorNombre() != null ? c.getEstadoAnteriorNombre() : "N/A");
                tablaHistorial.addCell(c.getEstadoNuevoNombre() != null ? c.getEstadoNuevoNombre() : "N/A");
                tablaHistorial.addCell(c.getResponsableNombre() != null ? c.getResponsableNombre() : "N/A");
                tablaHistorial.addCell(c.getFechaCambioFormateada());
            }
            documento.add(tablaHistorial);
        } else {
            documento.add(new Paragraph("4. HISTORIAL DE CAMBIOS DE ESTADO: No hay cambios registrados.", normalFont));
        }

        documento.close();
        return buildPdfResponse(out, "proyecto_" + idProyecto + ".pdf");
    }

    @GetMapping("/resumen-proyectos")
    public ResponseEntity<InputStreamResource> reporteResumenProyectos() throws Exception {
        List<Proyecto> proyectos = proyectoService.findAll();

        long totalProyectos = proyectos.size();
        long activos = proyectoService.countByEstadoNotIn(List.of("Entregado", "Anulado"));
        long entregados = proyectoService.countByEstado("Entregado");
        long anulados = proyectoService.countByEstado("Anulado");

        BigDecimal montoTotalActivos = proyectoService.sumMontoTotalActivos();
        BigDecimal montoTotalEntregados = proyectoService.findByEstado("Entregado")
                .stream()
                .map(Proyecto::getMontoTotal)
                .filter(m -> m != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal montoTotalGlobal = proyectoService.sumMontoTotal();

        Document documento = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(documento, out);
        documento.open();

        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font subtituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

        documento.add(new Paragraph("RESUMEN EJECUTIVO DE PROYECTOS", tituloFont));
        documento.add(new Paragraph("Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        documento.add(new Paragraph(" "));

        documento.add(new Paragraph("METRICAS GLOBALES", subtituloFont));
        PdfPTable metricas = new PdfPTable(2);
        metricas.setWidthPercentage(100);
        metricas.addCell("Total Proyectos:");
        metricas.addCell(String.valueOf(totalProyectos));
        metricas.addCell("Proyectos Activos:");
        metricas.addCell(String.valueOf(activos));
        metricas.addCell("Proyectos Entregados:");
        metricas.addCell(String.valueOf(entregados));
        metricas.addCell("Proyectos Anulados:");
        metricas.addCell(String.valueOf(anulados));
        metricas.addCell("Monto Total Global:");
        metricas.addCell(formatearMoneda(montoTotalGlobal));
        metricas.addCell("Monto Total Activos:");
        metricas.addCell(formatearMoneda(montoTotalActivos));
        metricas.addCell("Monto Total Entregados:");
        metricas.addCell(formatearMoneda(montoTotalEntregados));
        documento.add(metricas);
        documento.add(new Paragraph(" "));

        documento.add(new Paragraph("RESUMEN POR ESTADO", subtituloFont));
        List<Object[]> resumenEstados = proyectoService.getEstadisticasDashboard();
        PdfPTable tablaEstados = new PdfPTable(3);
        tablaEstados.setWidthPercentage(100);
        tablaEstados.addCell("Estado");
        tablaEstados.addCell("Cantidad");
        tablaEstados.addCell("Monto Total");

        for (Object[] row : resumenEstados) {
            String estado = (String) row[0];
            Long cantidad = (Long) row[1];
            BigDecimal monto = (BigDecimal) row[2];
            tablaEstados.addCell(estado != null ? estado : "Sin estado");
            tablaEstados.addCell(String.valueOf(cantidad != null ? cantidad : 0));
            tablaEstados.addCell(formatearMoneda(monto != null ? monto : BigDecimal.ZERO));
        }
        documento.add(tablaEstados);
        documento.add(new Paragraph(" "));

        documento.add(new Paragraph("TOP 5 PROYECTOS POR MONTO", subtituloFont));
        List<Proyecto> topProyectos = proyectoService.findTop10ByOrderByFechaInicioDesc()
                .stream()
                .limit(5)
                .collect(Collectors.toList());

        if (!topProyectos.isEmpty()) {
            PdfPTable tablaTop = new PdfPTable(4);
            tablaTop.setWidthPercentage(100);
            tablaTop.addCell("ID");
            tablaTop.addCell("Proyecto");
            tablaTop.addCell("Cliente");
            tablaTop.addCell("Monto");

            for (Proyecto p : topProyectos) {
                tablaTop.addCell(String.valueOf(p.getIdProyecto()));
                tablaTop.addCell(p.getNombreProyecto());
                tablaTop.addCell(p.getNombreCliente() != null ? p.getNombreCliente() : "N/A");
                tablaTop.addCell(formatearMoneda(p.getMontoTotal()));
            }
            documento.add(tablaTop);
        } else {
            documento.add(new Paragraph("No hay proyectos registrados.", normalFont));
        }

        documento.close();
        return buildPdfResponse(out, "resumen_proyectos.pdf");
    }

    // ========== MÉTODOS AUXILIARES ==========

    private ResponseEntity<InputStreamResource> buildPdfResponse(ByteArrayOutputStream out, String filename) {
        ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=" + filename);
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(input));
    }
}