package com.stamos.controller.cliente;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.stamos.controller.BaseController;
import com.stamos.model.Cliente;
import com.stamos.model.Cotizacion;
import com.stamos.model.CotizacionDetalle;
import com.stamos.model.Usuario;
import com.stamos.repository.CotizacionRepository;
import com.stamos.service.ClienteService;
import com.stamos.service.CotizacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Controller
@RequestMapping("/cliente")
public class ClienteCotizacionController extends BaseController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private CotizacionService cotizacionService;

    @Autowired
    private CotizacionRepository cotizacionRepository;

    @GetMapping("/cotizaciones")
    public String listarCotizaciones(Model model, Authentication auth) {
        Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        Cliente cliente = clienteService.findByUsuario(usuario);
        if (cliente == null) {
            throw new RuntimeException("Cliente no encontrado");
        }

        List<Cotizacion> cotizaciones = cotizacionRepository.findByIdCliente(cliente.getIdCliente());
        for (Cotizacion c : cotizaciones) {
            List<CotizacionDetalle> detalles = cotizacionService.getDetallesByCotizacion(c.getIdCotizacion());
            c.setDetalles(detalles);
            c.recalcularTotales();
        }

        model.addAttribute("cotizaciones", cotizaciones);
        model.addAttribute("cliente", cliente);
        addCommonAttributes(model);

        return "cliente/mis-cotizaciones";
    }

    @GetMapping("/cotizacion/{id}/pdf")
    public ResponseEntity<InputStreamResource> generarPDFCotizacion(
            @PathVariable("id") Integer idCotizacion,
            Authentication auth) throws Exception {

        Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        Cliente cliente = clienteService.findByUsuario(usuario);
        if (cliente == null) {
            throw new RuntimeException("Cliente no encontrado");
        }

        Cotizacion cotizacion = cotizacionService.findById(idCotizacion);
        if (cotizacion == null) {
            throw new RuntimeException("Cotización no encontrada");
        }

        if (!cotizacion.getIdCliente().equals(cliente.getIdCliente())) {
            throw new AccessDeniedException("No tienes acceso a esta cotización");
        }

        List<CotizacionDetalle> detalles = cotizacionService.getDetallesByCotizacion(idCotizacion);

        Document documento = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(documento, out);
        documento.open();

        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font subtituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

        documento.add(new Paragraph("COTIZACIÓN", tituloFont));
        documento.add(new Paragraph("Código: " + cotizacion.getCodigoCotizacion(), subtituloFont));
        documento.add(new Paragraph("Cliente: " + cliente.getNombreRazonSocial()));
        documento.add(new Paragraph("RUC: " + (cliente.getRuc() != null ? cliente.getRuc() : "No registrado")));
        documento.add(new Paragraph("Fecha de emisión: " + cotizacion.getFechaEmisionFormateada()));
        documento.add(new Paragraph("Fecha de validez: " + cotizacion.getFechaValidezFormateada()));
        documento.add(new Paragraph("Estado: " + cotizacion.getEstadoLegible()));
        documento.add(new Paragraph(" "));

        documento.add(new Paragraph("DETALLE DE PRODUCTOS Y SERVICIOS", subtituloFont));
        PdfPTable tabla = new PdfPTable(5);
        tabla.setWidthPercentage(100);
        tabla.addCell("Producto");
        tabla.addCell("Cantidad");
        tabla.addCell("Precio Unit.");
        tabla.addCell("Descuento");
        tabla.addCell("Subtotal");

        for (CotizacionDetalle d : detalles) {
            tabla.addCell(d.getNombreProducto() != null ? d.getNombreProducto() : "N/A");
            tabla.addCell(String.valueOf(d.getCantidad()));
            tabla.addCell(formatearMoneda(d.getPrecioUnitario()));
            tabla.addCell(formatearMoneda(d.getDescuento()));
            tabla.addCell(formatearMoneda(d.getSubtotalLinea()));
        }
        documento.add(tabla);
        documento.add(new Paragraph(" "));

        documento.add(new Paragraph("RESUMEN FINANCIERO", subtituloFont));
        PdfPTable resumen = new PdfPTable(2);
        resumen.setWidthPercentage(100);
        resumen.addCell("Subtotal:");
        resumen.addCell(formatearMoneda(cotizacion.getSubtotal()));
        resumen.addCell("IGV (" + cotizacion.getTasaIgv() + "%):");
        resumen.addCell(formatearMoneda(cotizacion.getIgv()));
        resumen.addCell("Total:");
        resumen.addCell(formatearMoneda(cotizacion.getTotal()));
        documento.add(resumen);
        documento.add(new Paragraph(" "));

        documento.add(new Paragraph("Observaciones: " + (cotizacion.getObservaciones() != null ? cotizacion.getObservaciones() : "Ninguna"), normalFont));

        documento.close();

        ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=cotizacion_" + cotizacion.getCodigoCotizacion() + ".pdf");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(input));
    }
}