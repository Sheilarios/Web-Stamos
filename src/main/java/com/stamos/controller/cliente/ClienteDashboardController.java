package com.stamos.controller.cliente;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.stamos.controller.BaseController;
import com.stamos.dto.TimelinePasoDTO;
import com.stamos.model.CambioEstadoProyecto;
import com.stamos.model.Cliente;
import com.stamos.model.Proyecto;
import com.stamos.model.Usuario;
import com.stamos.service.CambioEstadoProyectoService;
import com.stamos.service.ClienteService;
import com.stamos.service.PagoService;
import com.stamos.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/cliente")
public class ClienteDashboardController extends BaseController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private PagoService pagoService;

    @Autowired
    private CambioEstadoProyectoService cambioEstadoProyectoService;

    // ==================== DASHBOARD ====================

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        String email = auth.getName();

        try {
            Usuario usuario = clienteService.findUsuarioByEmail(email);
            if (usuario == null) {
                addEmptyAttributes(model);
                addCommonAttributes(model);
                return "cliente/dashboard";
            }

            Cliente cliente = clienteService.findByUsuario(usuario);
            if (cliente == null) {
                addEmptyAttributes(model);
                addCommonAttributes(model);
                return "cliente/dashboard";
            }

            List<Proyecto> proyectos = proyectoService.findByCliente(cliente.getIdCliente());
            if (proyectos == null) {
                proyectos = new ArrayList<>();
            }

            long proyectosActivos = proyectos.stream()
                    .filter(p -> p.getIdEstadoActual() != null && p.getIdEstadoActual() != 5 && p.getIdEstadoActual() != 6)
                    .count();

            BigDecimal totalCotizado = proyectos.stream()
                    .map(Proyecto::getMontoTotal)
                    .filter(monto -> monto != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalPagado = pagoService.getTotalPagadoByCliente(cliente.getIdCliente());
            if (totalPagado == null) totalPagado = BigDecimal.ZERO;

            BigDecimal saldoPendiente = totalCotizado.subtract(totalPagado);
            if (saldoPendiente.compareTo(BigDecimal.ZERO) < 0) saldoPendiente = BigDecimal.ZERO;

            LocalDateTime ultimoAcceso = clienteService.getUltimoAcceso(cliente.getIdCliente());

            List<Proyecto> proyectosActivosList = proyectos.stream()
                    .filter(p -> p.getIdEstadoActual() != null && p.getIdEstadoActual() != 5 && p.getIdEstadoActual() != 6)
                    .toList();

            List<Map<String, Object>> proyectosConTimeline = new ArrayList<>();
            for (Proyecto proyecto : proyectosActivosList) {
                Map<String, Object> data = new HashMap<>();
                data.put("proyecto", proyecto);
                data.put("pasos", generarPasosTimeline(proyecto));
                data.put("progreso", calcularProgreso(proyecto));
                proyectosConTimeline.add(data);
            }

            List<Proyecto> proyectosRecientes = proyectos.stream()
                    .filter(p -> p.getIdProyecto() != null)
                    .sorted((p1, p2) -> p2.getIdProyecto().compareTo(p1.getIdProyecto()))
                    .limit(5)
                    .toList();

            for (Proyecto proyecto : proyectosRecientes) {
                BigDecimal totalPagadoProyecto = pagoService.getTotalPagadoByProyecto(proyecto.getIdProyecto());
                if (totalPagadoProyecto == null) totalPagadoProyecto = BigDecimal.ZERO;
                proyecto.setTotalPagado(totalPagadoProyecto);

                BigDecimal saldoPendienteProyecto = proyecto.getMontoTotal().subtract(totalPagadoProyecto);
                if (saldoPendienteProyecto.compareTo(BigDecimal.ZERO) < 0) saldoPendienteProyecto = BigDecimal.ZERO;
                proyecto.setSaldoPendiente(saldoPendienteProyecto);
            }

            String totalCotizadoFormateado = formatearMoneda(totalCotizado);
            String totalPagadoFormateado = formatearMoneda(totalPagado);
            String saldoPendienteFormateado = formatearMoneda(saldoPendiente);

            model.addAttribute("cliente", cliente);
            model.addAttribute("proyectos", proyectosRecientes);
            model.addAttribute("proyectosActivos", proyectosActivos);
            model.addAttribute("totalCotizado", totalCotizado);
            model.addAttribute("totalPagado", totalPagado);
            model.addAttribute("saldoPendiente", saldoPendiente);
            model.addAttribute("totalCotizadoFormateado", totalCotizadoFormateado);
            model.addAttribute("totalPagadoFormateado", totalPagadoFormateado);
            model.addAttribute("saldoPendienteFormateado", saldoPendienteFormateado);
            model.addAttribute("ultimoAcceso", ultimoAcceso);
            model.addAttribute("totalProyectos", proyectos.size());

            model.addAttribute("proyectosConTimeline", proyectosConTimeline);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            model.addAttribute("fechaActual", LocalDateTime.now().format(formatter));
            model.addAttribute("titulo", "Dashboard - Cliente");
            model.addAttribute("rol", "Cliente");

            addCommonAttributes(model);
            clienteService.registrarAcceso(cliente.getIdCliente());

            return "cliente/dashboard";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar el dashboard: " + e.getMessage());
            addEmptyAttributes(model);
            addCommonAttributes(model);
            return "cliente/dashboard";
        }
    }

    // ==================== GENERACIÓN DE PDF ====================

    @GetMapping("/reporte/proyectos-pdf")
    public ResponseEntity<InputStreamResource> generarReporteProyectosPDF(Authentication auth) throws Exception {
        // Obtener el cliente autenticado
        Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        Cliente cliente = clienteService.findByUsuario(usuario);
        if (cliente == null) {
            throw new RuntimeException("Cliente no encontrado");
        }

        List<Proyecto> proyectos = proyectoService.findByCliente(cliente.getIdCliente());
        if (proyectos == null) {
            proyectos = new ArrayList<>();
        }

        // Crear el documento PDF
        Document documento = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(documento, out);
        documento.open();

        documento.add(new Paragraph("REPORTE DE PROYECTOS"));
        documento.add(new Paragraph("Cliente: " + cliente.getNombreRazonSocial()));
        documento.add(new Paragraph("RUC: " + (cliente.getRuc() != null ? cliente.getRuc() : "No registrado")));
        documento.add(new Paragraph("Fecha de generación: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        documento.add(new Paragraph(" "));

        PdfPTable tabla = new PdfPTable(5);
        tabla.setWidthPercentage(100);
        tabla.addCell("ID");
        tabla.addCell("Proyecto");
        tabla.addCell("Estado");
        tabla.addCell("Monto Total");
        tabla.addCell("Fecha Inicio");

        for (Proyecto p : proyectos) {
            tabla.addCell(String.valueOf(p.getIdProyecto()));
            tabla.addCell(p.getNombreProyecto() != null ? p.getNombreProyecto() : "Sin nombre");
            tabla.addCell(p.getEstado() != null ? p.getEstado() : "Sin estado");
            tabla.addCell(p.getMontoTotal() != null ? formatearMoneda(p.getMontoTotal()) : "S/ 0.00");
            tabla.addCell(p.getFechaInicio() != null ? p.getFechaInicio().toString() : "-");
        }

        documento.add(tabla);
        documento.close();

        // Preparar la respuesta HTTP
        ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=reporte_proyectos_" + cliente.getIdCliente() + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(input));
    }

    // ==================== MÉTODOS PRIVADOS DE APOYO ====================

    private List<TimelinePasoDTO> generarPasosTimeline(Proyecto proyecto) {
        List<TimelinePasoDTO> pasos = new ArrayList<>();

        List<String> ordenEstados = Arrays.asList("Aprobado", "En Diseño", "En Producción", "Instalación/Montaje", "Entregado");
        Map<String, Integer> ordenMap = new HashMap<>();
        for (int i = 0; i < ordenEstados.size(); i++) {
            ordenMap.put(ordenEstados.get(i), i + 1);
        }

        Map<String, Object[]> estadoInfo = new HashMap<>();
        estadoInfo.put("Aprobado", new Object[]{"ti ti-check-circle", "Proyecto aprobado"});
        estadoInfo.put("En Diseño", new Object[]{"ti ti-pencil", "Diseño en desarrollo"});
        estadoInfo.put("En Producción", new Object[]{"ti ti-tools", "Fabricación"});
        estadoInfo.put("Instalación/Montaje", new Object[]{"ti ti-truck", "Instalación en sitio"});
        estadoInfo.put("Entregado", new Object[]{"ti ti-check", "Proyecto entregado"});

        List<CambioEstadoProyecto> cambios = cambioEstadoProyectoService.findByProyecto(proyecto.getIdProyecto());
        String estadoActual = getEstadoNombrePorId(proyecto.getIdEstadoActual());
        int indiceActual = ordenMap.getOrDefault(estadoActual, 1);

        for (String estado : ordenEstados) {
            String icono = (String) estadoInfo.get(estado)[0];
            String descripcion = (String) estadoInfo.get(estado)[1];
            int indiceEstado = ordenMap.get(estado);

            String estadoPaso;
            String estadoTexto;
            LocalDate fecha = null;

            if (cambios != null && !cambios.isEmpty()) {
                for (CambioEstadoProyecto cambio : cambios) {
                    String nombreEstadoNuevo = cambio.getEstadoNuevoNombre();
                    if (nombreEstadoNuevo != null && nombreEstadoNuevo.equals(estado)) {
                        fecha = cambio.getFechaCambio().toLocalDate();
                        break;
                    }
                }
            }

            if (indiceEstado < indiceActual) {
                estadoPaso = "COMPLETADO";
                estadoTexto = "Completado";
            } else if (indiceEstado == indiceActual) {
                estadoPaso = "ACTUAL";
                estadoTexto = "En progreso";
            } else {
                estadoPaso = "PENDIENTE";
                estadoTexto = "Pendiente";
            }

            if (estado.equals("Aprobado") && fecha == null && proyecto.getFechaInicio() != null) {
                fecha = proyecto.getFechaInicio();
            }

            pasos.add(new TimelinePasoDTO(
                    estado,
                    descripcion,
                    estadoPaso,
                    estadoTexto,
                    icono,
                    fecha,
                    indiceEstado
            ));
        }

        return pasos;
    }

    private Integer calcularProgreso(Proyecto proyecto) {
        if (proyecto == null) return 0;
        String estado = getEstadoNombrePorId(proyecto.getIdEstadoActual());
        if (estado == null) return 0;
        switch (estado) {
            case "Aprobado": return 10;
            case "En Diseño": return 30;
            case "En Producción": return 60;
            case "Instalación/Montaje": return 85;
            case "Entregado": return 100;
            case "Anulado": return 0;
            default: return 0;
        }
    }

    private void addEmptyAttributes(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("proyectos", new ArrayList<>());
        model.addAttribute("proyectosActivos", 0L);
        model.addAttribute("totalCotizado", BigDecimal.ZERO);
        model.addAttribute("totalPagado", BigDecimal.ZERO);
        model.addAttribute("saldoPendiente", BigDecimal.ZERO);
        model.addAttribute("totalCotizadoFormateado", "S/ 0.00");
        model.addAttribute("totalPagadoFormateado", "S/ 0.00");
        model.addAttribute("saldoPendienteFormateado", "S/ 0.00");
        model.addAttribute("totalProyectos", 0);
        model.addAttribute("proyectosConTimeline", new ArrayList<>());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        model.addAttribute("fechaActual", LocalDateTime.now().format(formatter));
        model.addAttribute("rol", "Cliente");
    }

    // ==================== MÉTODOS PÚBLICOS PARA LA VISTA ====================

    public String getEstadoNombrePorId(Integer idEstado) {
        if (idEstado == null) return "Sin estado";
        switch (idEstado) {
            case 1: return "Aprobado";
            case 2: return "En Diseño";
            case 3: return "En Producción";
            case 4: return "Instalación/Montaje";
            case 5: return "Entregado";
            case 6: return "Anulado";
            default: return "Desconocido";
        }
    }

    public long getProyectosActivos(Cliente cliente) {
        if (cliente == null || cliente.getIdCliente() == null) return 0;
        List<Proyecto> proyectos = proyectoService.findByCliente(cliente.getIdCliente());
        if (proyectos == null) return 0;
        return proyectos.stream()
                .filter(p -> p.getIdEstadoActual() != null && p.getIdEstadoActual() != 5 && p.getIdEstadoActual() != 6)
                .count();
    }

    public BigDecimal getTotalCotizado(Cliente cliente) {
        if (cliente == null || cliente.getIdCliente() == null) return BigDecimal.ZERO;
        List<Proyecto> proyectos = proyectoService.findByCliente(cliente.getIdCliente());
        if (proyectos == null) return BigDecimal.ZERO;
        return proyectos.stream()
                .map(Proyecto::getMontoTotal)
                .filter(monto -> monto != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalPagado(Cliente cliente) {
        if (cliente == null || cliente.getIdCliente() == null) return BigDecimal.ZERO;
        BigDecimal total = pagoService.getTotalPagadoByCliente(cliente.getIdCliente());
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getSaldoPendiente(Cliente cliente) {
        BigDecimal totalCotizado = getTotalCotizado(cliente);
        BigDecimal totalPagado = getTotalPagado(cliente);
        BigDecimal saldo = totalCotizado.subtract(totalPagado);
        return saldo.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : saldo;
    }

    public LocalDateTime getUltimoAcceso(Cliente cliente) {
        if (cliente == null || cliente.getIdCliente() == null) return null;
        return clienteService.getUltimoAcceso(cliente.getIdCliente());
    }

    public String getEstadoLegible(Proyecto proyecto) {
        if (proyecto == null) return "Sin estado";
        Integer estado = proyecto.getIdEstadoActual();
        if (estado == null) return "Sin estado";
        return switch (estado) {
            case 1 -> "Aprobado";
            case 2 -> "En Diseño";
            case 3 -> "En Producción";
            case 4 -> "Instalación/Montaje";
            case 5 -> "Entregado";
            case 6 -> "Anulado";
            default -> "Desconocido";
        };
    }

    public String getEstadoBadgeClass(Proyecto proyecto) {
        if (proyecto == null) return "status-pendiente";
        Integer estado = proyecto.getIdEstadoActual();
        if (estado == null) return "status-pendiente";
        return switch (estado) {
            case 1 -> "status-aprobado";
            case 2 -> "status-diseno";
            case 3 -> "status-produccion";
            case 4 -> "status-instalacion";
            case 5 -> "status-entregado";
            case 6 -> "status-anulado";
            default -> "status-pendiente";
        };
    }

    @Override
    public String formatearMoneda(BigDecimal monto) {
        if (monto == null) return "S/ 0.00";
        return "S/ " + monto.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public String formatearFecha(LocalDateTime fecha) {
        if (fecha == null) return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fecha.format(formatter);
    }

    public String formatearFechaLocal(LocalDate fecha) {
        if (fecha == null) return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return fecha.format(formatter);
    }

    public boolean tieneProyectosActivos(Cliente cliente) {
        return getProyectosActivos(cliente) > 0;
    }

    public String getSaldoEstado(BigDecimal saldoPendiente) {
        if (saldoPendiente == null || saldoPendiente.compareTo(BigDecimal.ZERO) == 0) {
            return "success";
        }
        return "warning";
    }

    public String getSaldoMensaje(BigDecimal saldoPendiente) {
        if (saldoPendiente == null || saldoPendiente.compareTo(BigDecimal.ZERO) == 0) {
            return "¡Todo pagado!";
        }
        return "Pendiente de pago";
    }

    @GetMapping({"", "/"})
    public String redirectToDashboard() {
        return "redirect:/cliente/dashboard";
    }
}