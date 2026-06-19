package com.stamos.service;

import com.stamos.model.ComprobantePago;
import com.stamos.model.Pago;
import com.stamos.model.Proyecto;
import com.stamos.repository.ComprobantePagoRepository;
import com.stamos.repository.PagoRepository;
import com.stamos.repository.ProyectoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private ComprobantePagoRepository comprobanteRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Value("${app.upload.comprobantes.dir:./uploads/comprobantes/}")
    private String uploadDir;

    @Transactional
    public Pago registrarPago(Pago pago, ComprobantePago comprobante, Integer idUsuarioRegistro) {
        Proyecto proyecto = proyectoRepository.findById(pago.getIdProyecto())
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        if (!proyecto.puedeRecibirPagos()) {
            throw new RuntimeException("El proyecto no puede recibir pagos en su estado actual: " + 
                                      proyecto.getEstado());
        }

        if (pago.getMonto() == null || pago.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El monto del pago debe ser mayor a cero");
        }

        BigDecimal totalPagado = getTotalPagadoByProyecto(pago.getIdProyecto());
        BigDecimal nuevoTotal = totalPagado.add(pago.getMonto());

        if (nuevoTotal.compareTo(proyecto.getMontoTotal()) > 0) {
            BigDecimal excedente = nuevoTotal.subtract(proyecto.getMontoTotal());
            throw new RuntimeException("El pago excede el monto total del proyecto en S/ " + 
                                      excedente.setScale(2, RoundingMode.HALF_UP));
        }

        pago.setFechaPago(LocalDateTime.now());
        pago.setEstadoPago("Pendiente");
        pago.setFechaRegistro(LocalDateTime.now());
        pago.setFechaActualizacion(LocalDateTime.now());
        pago.setIdUsuarioRegistro(idUsuarioRegistro);
        pago.setIdUsuarioActualizacion(idUsuarioRegistro);
        pago.setNombreProyecto(proyecto.getNombreProyecto());
        pago.setCodigoProyecto(proyecto.getCodigoProyecto());
        pago.setMontoTotalProyecto(proyecto.getMontoTotal());
        Pago pagoGuardado = pagoRepository.save(pago);

        if (comprobante != null) {
            comprobante.setIdPago(pagoGuardado.getIdPago());
            comprobante.setValidado(false);
            comprobante.setFechaRegistro(LocalDateTime.now());
            comprobante.setFechaActualizacion(LocalDateTime.now());
            
            comprobante.setMetodoPago(pago.getMetodoPago());
            comprobante.setNombreProyecto(proyecto.getNombreProyecto());
            comprobante.setNombreCliente(proyecto.getNombreCliente());
            
            comprobanteRepository.save(comprobante);
        }

        return pagoGuardado;
    }


    @Transactional
    public Pago registrarPagoConComprobante(Pago pago, String tipoComprobante, String numeroComprobante,
                                           MultipartFile archivo, Integer idUsuarioRegistro) throws IOException {
        if (archivo == null || archivo.isEmpty()) {
            throw new RuntimeException("Debes seleccionar un comprobante de pago");
        }

        if (archivo.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("El comprobante excede el tamaño máximo permitido (5MB)");
        }

        String extension = getFileExtension(archivo.getOriginalFilename());
        if (!isValidFileType(extension)) {
            throw new RuntimeException("Tipo de archivo no permitido. Formatos aceptados: JPG, PNG, PDF");
        }

        String nombreArchivo = guardarComprobante(archivo);

        ComprobantePago comprobante = new ComprobantePago();
        comprobante.setTipoComprobante(tipoComprobante);
        comprobante.setNumeroComprobante(numeroComprobante);
        comprobante.setArchivoUrl("/comprobantes/" + nombreArchivo);
        comprobante.setValidado(false);

        return registrarPago(pago, comprobante, idUsuarioRegistro);
    }


    @Transactional
    public Pago validarPago(Integer idPago, Integer idUsuarioValidacion) {
        Pago pago = pagoRepository.findById(idPago)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        if (!"Pendiente".equals(pago.getEstadoPago())) {
            throw new RuntimeException("El pago ya fue " + pago.getEstadoPago().toLowerCase());
        }

        pago.validar(idUsuarioValidacion);

        ComprobantePago comprobante = comprobanteRepository.findByIdPago(idPago).orElse(null);
        if (comprobante != null) {
            comprobante.validar(idUsuarioValidacion);
            comprobanteRepository.save(comprobante);
        }

        return pagoRepository.save(pago);
    }

    @Transactional
    public Pago rechazarPago(Integer idPago, String motivo, Integer idUsuarioValidacion) {
        Pago pago = pagoRepository.findById(idPago)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        if (!"Pendiente".equals(pago.getEstadoPago())) {
            throw new RuntimeException("El pago ya fue " + pago.getEstadoPago().toLowerCase());
        }

        pago.rechazar(idUsuarioValidacion);
        pago.setObservaciones((pago.getObservaciones() != null ? pago.getObservaciones() + "\n" : "") + 
                            "Motivo de rechazo: " + (motivo != null ? motivo : "Sin motivo"));

        ComprobantePago comprobante = comprobanteRepository.findByIdPago(idPago).orElse(null);
        if (comprobante != null) {
            comprobante.rechazar(idUsuarioValidacion);
            comprobanteRepository.save(comprobante);
        }

        return pagoRepository.save(pago);
    }

    public Pago findById(Integer idPago) {
        return pagoRepository.findById(idPago).orElse(null);
    }

    public ComprobantePago findComprobanteById(Integer idComprobante) {
        return comprobanteRepository.findById(idComprobante).orElse(null);
    }

    public ComprobantePago findComprobanteByPago(Integer idPago) {
        return comprobanteRepository.findByIdPago(idPago).orElse(null);
    }

    public Page<Pago> findAll(Pageable pageable) {
        return pagoRepository.findAll(pageable);
    }

    public Page<Pago> findByProyecto(Integer idProyecto, Pageable pageable) {
        return pagoRepository.findByIdProyecto(idProyecto, pageable);
    }

    public List<Pago> findByProyecto(Integer idProyecto) {
        return pagoRepository.findByIdProyectoOrderByFechaPagoDesc(idProyecto);
    }

    public Page<Pago> findByEstadoPago(String estadoPago, Pageable pageable) {
        return pagoRepository.findByEstadoPago(estadoPago, pageable);
    }

    public List<Pago> findPendientes() {
        return pagoRepository.findPendientes();
    }

    public Page<Pago> findPendientes(Pageable pageable) {
        return pagoRepository.findPendientes(pageable);
    }

    public Page<Pago> findValidados(Pageable pageable) {
        return pagoRepository.findValidados(pageable);
    }

    public Page<Pago> findByMetodoPago(String metodoPago, Pageable pageable) {
        return pagoRepository.findByMetodoPago(metodoPago, pageable);
    }

    public Page<Pago> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin,
                                        Pageable pageable) {
        return pagoRepository.findByFechaPagoBetween(fechaInicio, fechaFin, pageable);
    }

    public List<Pago> findByCliente(Integer idCliente) {
        return pagoRepository.findPagosByCliente(idCliente);
    }

    public Page<Pago> findByCliente(Integer idCliente, Pageable pageable) {
        return pagoRepository.findPagosByCliente(idCliente, pageable);
    }

    public Page<Pago> search(String searchText, Pageable pageable) {
        return pagoRepository.search(searchText, pageable);
    }

    public List<Pago> findTopN(int limit) {
        return pagoRepository.findTopN(limit);
    }

    public List<Pago> findTopNByProyecto(Integer idProyecto, int limit) {
        return pagoRepository.findTopNByProyecto(idProyecto, limit);
    }


    public BigDecimal getTotalPagadoByProyecto(Integer idProyecto) {
        return pagoRepository.sumMontoByProyecto(idProyecto);
    }


    public BigDecimal getTotalPagadoConPendientesByProyecto(Integer idProyecto) {
        return pagoRepository.sumMontoTotalByProyecto(idProyecto);
    }


    public BigDecimal getTotalPagadoByCliente(Integer idCliente) {
        return pagoRepository.sumMontoByCliente(idCliente);
    }


    public BigDecimal getTotalPagadoGeneral() {
        return pagoRepository.sumTotalPagadoGeneral();
    }


    public BigDecimal getTotalPagadoConPendientesGeneral() {
        return pagoRepository.sumTotalPagadoConPendientes();
    }


    public BigDecimal getTotalPendiente() {
        return pagoRepository.sumTotalPendiente();
    }


    public BigDecimal getTotalFacturado() {
        return proyectoRepository.sumMontoTotal();
    }


    public BigDecimal getTotalFacturadoActivos() {
        return proyectoRepository.sumMontoTotalActivos();
    }

    public BigDecimal getSaldoPendienteGeneral() {
        BigDecimal totalFacturado = getTotalFacturado();
        BigDecimal totalPagado = getTotalPagadoGeneral();
        BigDecimal saldo = totalFacturado.subtract(totalPagado);
        return saldo.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : saldo;
    }


    public BigDecimal getSaldoPendienteByProyecto(Integer idProyecto) {
        Proyecto proyecto = proyectoRepository.findById(idProyecto).orElse(null);
        if (proyecto == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalPagado = getTotalPagadoByProyecto(idProyecto);
        BigDecimal saldo = proyecto.getMontoTotal().subtract(totalPagado);
        return saldo.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : saldo;
    }

 
    public BigDecimal getSaldoPendienteByCliente(Integer idCliente) {
        BigDecimal totalFacturado = proyectoRepository.sumMontoTotalByCliente(idCliente);
        BigDecimal totalPagado = getTotalPagadoByCliente(idCliente);
        BigDecimal saldo = totalFacturado.subtract(totalPagado);
        return saldo.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : saldo;
    }


    public int getPorcentajeCobranzaGeneral() {
        BigDecimal totalFacturado = getTotalFacturado();
        if (totalFacturado.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        BigDecimal totalPagado = getTotalPagadoGeneral();
        BigDecimal porcentaje = totalPagado.multiply(new BigDecimal("100"))
                .divide(totalFacturado, 0, RoundingMode.HALF_UP);
        return Math.min(porcentaje.intValue(), 100);
    }


    public int getPorcentajeCobranzaByProyecto(Integer idProyecto) {
        Proyecto proyecto = proyectoRepository.findById(idProyecto).orElse(null);
        if (proyecto == null || proyecto.getMontoTotal().compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        BigDecimal totalPagado = getTotalPagadoByProyecto(idProyecto);
        BigDecimal porcentaje = totalPagado.multiply(new BigDecimal("100"))
                .divide(proyecto.getMontoTotal(), 0, RoundingMode.HALF_UP);
        return Math.min(porcentaje.intValue(), 100);
    }


    public int getPorcentajeCobranzaByCliente(Integer idCliente) {
        BigDecimal totalFacturado = proyectoRepository.sumMontoTotalByCliente(idCliente);
        if (totalFacturado.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        BigDecimal totalPagado = getTotalPagadoByCliente(idCliente);
        BigDecimal porcentaje = totalPagado.multiply(new BigDecimal("100"))
                .divide(totalFacturado, 0, RoundingMode.HALF_UP);
        return Math.min(porcentaje.intValue(), 100);
    }

    public long count() {
        return pagoRepository.count();
    }

  
    public long countByEstadoPago(String estadoPago) {
        return pagoRepository.countByEstadoPago(estadoPago);
    }

    public long countByProyecto(Integer idProyecto) {
        return pagoRepository.countByIdProyecto(idProyecto);
    }


    public long countByMetodoPago(String metodoPago) {
        return pagoRepository.countByMetodoPago(metodoPago);
    }

    public BigDecimal sumMontoByMonthAndYear(int month, int year) {
        return pagoRepository.sumMontoByMonthAndYear(month, year);
    }

    public long countByMonthAndYear(int month, int year) {
        return pagoRepository.countByMonthAndYear(month, year);
    }


    public List<Object[]> getEstadisticasDashboard() {
        return pagoRepository.getEstadisticasDashboard();
    }

    public List<Object[]> getEstadisticasDashboardByProyecto(Integer idProyecto) {
        return pagoRepository.getEstadisticasDashboardByProyecto(idProyecto);
    }


    public List<Object[]> getEstadisticasDashboardByCliente(Integer idCliente) {
        return pagoRepository.getEstadisticasDashboardByCliente(idCliente);
    }


    public boolean hasPagos(Integer idProyecto) {
        return pagoRepository.existsByIdProyecto(idProyecto);
    }


    public boolean hasPagosValidados(Integer idProyecto) {
        return pagoRepository.hasPagosValidados(idProyecto);
    }


    public boolean hasPagosPendientes(Integer idProyecto) {
        return pagoRepository.hasPagosPendientes(idProyecto);
    }


    public boolean canValidar(Integer idPago) {
        return pagoRepository.canValidar(idPago);
    }

    public boolean canRechazar(Integer idPago) {
        return pagoRepository.canValidar(idPago);
    }


    private String guardarComprobante(MultipartFile archivo) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String nombreOriginal = archivo.getOriginalFilename();
        String nombreUnico = UUID.randomUUID().toString() + "_" + nombreOriginal;
        Path filePath = uploadPath.resolve(nombreUnico);
        Files.copy(archivo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return nombreUnico;
    }


    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }


    private boolean isValidFileType(String extension) {
        String[] validExtensions = {"jpg", "jpeg", "png", "gif", "pdf"};
        for (String valid : validExtensions) {
            if (valid.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }


    public String getEstadoLegible(String estadoPago) {
        if (estadoPago == null) {
            return "Sin estado";
        }
        return switch (estadoPago) {
            case "Pendiente" -> "Pendiente de validación";
            case "Validado" -> "Validado";
            case "Rechazado" -> "Rechazado";
            default -> estadoPago;
        };
    }


    public String getEstadoBadgeClass(String estadoPago) {
        if (estadoPago == null) {
            return "status-pendiente";
        }
        return switch (estadoPago) {
            case "Pendiente" -> "status-pendiente";
            case "Validado" -> "status-validado";
            case "Rechazado" -> "status-rechazado";
            default -> "status-pendiente";
        };
    }


    public String getEstadoColor(String estadoPago) {
        if (estadoPago == null) {
            return "#6B7280";
        }
        return switch (estadoPago) {
            case "Pendiente" -> "#F59E0B";
            case "Validado" -> "#10B981";
            case "Rechazado" -> "#EF4444";
            default -> "#6B7280";
        };
    }


    public String getMetodoLegible(String metodoPago) {
        if (metodoPago == null) {
            return "No especificado";
        }
        return switch (metodoPago) {
            case "Efectivo" -> "Efectivo";
            case "Transferencia" -> "Transferencia";
            case "Tarjeta" -> "Tarjeta";
            case "Cheque" -> "Cheque";
            default -> metodoPago;
        };
    }


    public String getMetodoBadgeClass(String metodoPago) {
        if (metodoPago == null) {
            return "";
        }
        return switch (metodoPago) {
            case "Efectivo" -> "efectivo";
            case "Transferencia" -> "transferencia";
            case "Tarjeta" -> "tarjeta";
            case "Cheque" -> "cheque";
            default -> "";
        };
    }

    public String getMetodoIcono(String metodoPago) {
        if (metodoPago == null) {
            return "ti ti-credit-card";
        }
        return switch (metodoPago) {
            case "Efectivo" -> "ti ti-coin";
            case "Transferencia" -> "ti ti-transfer";
            case "Tarjeta" -> "ti ti-credit-card";
            case "Cheque" -> "ti ti-file";
            default -> "ti ti-credit-card";
        };
    }


    public String formatearMoneda(BigDecimal monto) {
        if (monto == null) {
            return "S/ 0.00";
        }
        return "S/ " + monto.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public boolean esEfectivo(String metodoPago) {
        return "Efectivo".equals(metodoPago);
    }


    public boolean esTransferencia(String metodoPago) {
        return "Transferencia".equals(metodoPago);
    }


    public boolean esTarjeta(String metodoPago) {
        return "Tarjeta".equals(metodoPago);
    }


    public boolean esCheque(String metodoPago) {
        return "Cheque".equals(metodoPago);
    }

    public BigDecimal sumByMonthAndYear(int monthValue, int year) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public long countByEstado(String pendiente) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}