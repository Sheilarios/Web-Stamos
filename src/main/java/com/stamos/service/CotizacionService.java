package com.stamos.service;

import com.stamos.model.*;
import com.stamos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CotizacionService {

    @Autowired
    private CotizacionRepository cotizacionRepository;

    @Autowired
    private CotizacionVersionRepository cotizacionVersionRepository;

    @Autowired
    private CotizacionDetalleRepository cotizacionDetalleRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private EstadoProyectoRepository estadoProyectoRepository;

    @Autowired
    private ConfiguracionSistemaService configuracionService;

    @Transactional
    public Cotizacion crearCotizacion(Cotizacion cotizacion, List<Integer> productosIds,
                                      List<Integer> cantidades, List<BigDecimal> preciosUnitarios,
                                      List<BigDecimal> descuentos, Integer idUsuarioCreador) {
        if (productosIds == null || productosIds.isEmpty()) {
            throw new RuntimeException("La cotización debe incluir al menos un producto o servicio");
        }

        if (productosIds.size() != cantidades.size() ||
            productosIds.size() != preciosUnitarios.size()) {
            throw new RuntimeException("Error en los datos de los productos");
        }

        BigDecimal tasaIgv = configuracionService.getIgv();

        cotizacion.setEstado("Pendiente");
        cotizacion.setVersionActual(1);
        cotizacion.setFechaEmision(LocalDateTime.now());
        cotizacion.setFechaActualizacion(LocalDateTime.now());
        cotizacion.setTasaIgv(tasaIgv);

        Cotizacion cotizacionGuardada = cotizacionRepository.save(cotizacion);

        CotizacionVersion version = crearVersion(cotizacionGuardada, 1, idUsuarioCreador, null);

        List<CotizacionDetalle> detalles = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (int i = 0; i < productosIds.size(); i++) {
            Integer idProducto = productosIds.get(i);
            Integer cantidad = cantidades.get(i);
            BigDecimal precioUnitario = preciosUnitarios.get(i);
            BigDecimal descuento = descuentos != null && i < descuentos.size() ? 
                                   descuentos.get(i) : BigDecimal.ZERO;

            Producto producto = productoRepository.findById(idProducto)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + idProducto));

            CotizacionDetalle detalle = new CotizacionDetalle();
            detalle.setIdVersion(version.getIdVersion());
            detalle.setIdProducto(idProducto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setDescuento(descuento);
            detalle.calcularSubtotal();

            detalle.setNombreProducto(producto.getNombreItem());
            detalle.setDescripcionProducto(producto.getDescripcion());
            detalle.setTipoProducto(producto.getTipo());
            detalle.setImagenUrl(producto.getImagenUrl());
            detalle.setPrecioBase(producto.getPrecioBase());

            CotizacionDetalle detalleGuardado = cotizacionDetalleRepository.save(detalle);
            detalles.add(detalleGuardado);

            subtotal = subtotal.add(detalle.getSubtotalLinea());
        }

        BigDecimal igv = subtotal.multiply(tasaIgv)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(igv);

        version.setSubtotal(subtotal);
        version.setIgv(igv);
        version.setTotal(total);
        version.setCantidadItems(detalles.size());
        version.setDetalles(detalles);
        cotizacionVersionRepository.save(version);

        cotizacionGuardada.setSubtotal(subtotal);
        cotizacionGuardada.setIgv(igv);
        cotizacionGuardada.setTotal(total);
        cotizacionGuardada.setCantidadItems(detalles.size());
        cotizacionGuardada.setDetalles(detalles);

        return cotizacionRepository.save(cotizacionGuardada);
    }

    @Transactional
    public CotizacionVersion crearVersion(Integer idCotizacion, Cotizacion cotizacion,
                                          List<Integer> productosIds, List<Integer> cantidades,
                                          List<BigDecimal> preciosUnitarios, List<BigDecimal> descuentos,
                                          String motivoCambio, Integer idUsuarioCreador) {
        Cotizacion cotizacionExistente = cotizacionRepository.findById(idCotizacion)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        if ("Aprobada".equals(cotizacionExistente.getEstado())) {
            throw new RuntimeException("No se puede modificar una cotización aprobada. " +
                                      "Cree una nueva versión desde el historial.");
        }

        if (productosIds == null || productosIds.isEmpty()) {
            throw new RuntimeException("La cotización debe incluir al menos un producto o servicio");
        }

        BigDecimal tasaIgv = cotizacionExistente.getTasaIgv() != null ? 
                            cotizacionExistente.getTasaIgv() : configuracionService.getIgv();

        int nuevoNumeroVersion = cotizacionExistente.getVersionActual() + 1;

        CotizacionVersion version = new CotizacionVersion();
        version.setIdCotizacion(idCotizacion);
        version.setNumeroVersion(nuevoNumeroVersion);
        version.setTasaIgv(tasaIgv);
        version.setIdUsuarioCreador(idUsuarioCreador);
        version.setMotivoCambio(motivoCambio);
        version.setFechaCreacion(LocalDateTime.now());

        CotizacionVersion versionGuardada = cotizacionVersionRepository.save(version);

        List<CotizacionDetalle> detalles = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (int i = 0; i < productosIds.size(); i++) {
            Integer idProducto = productosIds.get(i);
            Integer cantidad = cantidades.get(i);
            BigDecimal precioUnitario = preciosUnitarios.get(i);
            BigDecimal descuento = descuentos != null && i < descuentos.size() ? 
                                   descuentos.get(i) : BigDecimal.ZERO;

            Producto producto = productoRepository.findById(idProducto)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + idProducto));

            CotizacionDetalle detalle = new CotizacionDetalle();
            detalle.setIdVersion(versionGuardada.getIdVersion());
            detalle.setIdProducto(idProducto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setDescuento(descuento);
            detalle.calcularSubtotal();

            detalle.setNombreProducto(producto.getNombreItem());
            detalle.setDescripcionProducto(producto.getDescripcion());
            detalle.setTipoProducto(producto.getTipo());
            detalle.setImagenUrl(producto.getImagenUrl());
            detalle.setPrecioBase(producto.getPrecioBase());

            CotizacionDetalle detalleGuardado = cotizacionDetalleRepository.save(detalle);
            detalles.add(detalleGuardado);

            subtotal = subtotal.add(detalle.getSubtotalLinea());
        }

        BigDecimal igv = subtotal.multiply(tasaIgv)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(igv);

        versionGuardada.setSubtotal(subtotal);
        versionGuardada.setIgv(igv);
        versionGuardada.setTotal(total);
        versionGuardada.setCantidadItems(detalles.size());
        versionGuardada.setDetalles(detalles);
        cotizacionVersionRepository.save(versionGuardada);

        cotizacionExistente.setVersionActual(nuevoNumeroVersion);
        cotizacionExistente.setFechaActualizacion(LocalDateTime.now());
        cotizacionExistente.setSubtotal(subtotal);
        cotizacionExistente.setIgv(igv);
        cotizacionExistente.setTotal(total);
        cotizacionExistente.setCantidadItems(detalles.size());
        cotizacionExistente.setDetalles(detalles);
        cotizacionRepository.save(cotizacionExistente);

        return versionGuardada;
    }

    private CotizacionVersion crearVersion(Cotizacion cotizacion, Integer numeroVersion,
                                          Integer idUsuarioCreador, String motivoCambio) {
        CotizacionVersion version = new CotizacionVersion();
        version.setIdCotizacion(cotizacion.getIdCotizacion());
        version.setNumeroVersion(numeroVersion);
        version.setTasaIgv(cotizacion.getTasaIgv());
        version.setIdUsuarioCreador(idUsuarioCreador);
        version.setMotivoCambio(motivoCambio);
        version.setFechaCreacion(LocalDateTime.now());
        return cotizacionVersionRepository.save(version);
    }

    @Transactional
    public Proyecto aprobarCotizacion(Integer idCotizacion, Integer idUsuarioAprobacion) {
        Cotizacion cotizacion = cotizacionRepository.findById(idCotizacion)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        if (!"Pendiente".equals(cotizacion.getEstado())) {
            throw new RuntimeException("La cotización ya está " + cotizacion.getEstado().toLowerCase());
        }

        if (!cotizacion.tieneDetalles()) {
            throw new RuntimeException("La cotización no tiene detalles");
        }

        if (proyectoRepository.existsByIdCotizacion(idCotizacion)) {
            throw new RuntimeException("La cotización ya tiene un proyecto asociado");
        }

        cotizacion.aprobar(idUsuarioAprobacion);
        cotizacionRepository.save(cotizacion);

        EstadoProyecto estadoAprobado = estadoProyectoRepository.findByNombreEstado("Aprobado")
                .orElseThrow(() -> new RuntimeException("Estado 'Aprobado' no encontrado"));

        Proyecto proyecto = new Proyecto();
        proyecto.setIdCotizacion(idCotizacion);
        proyecto.setIdCliente(cotizacion.getIdCliente());
        proyecto.setNombreProyecto("Proyecto - " + cotizacion.getCodigoCotizacion());
        proyecto.setIdEstadoActual(estadoAprobado.getIdEstado());
        proyecto.setMontoTotal(cotizacion.getTotal());
        proyecto.setFechaInicio(LocalDate.now());
        proyecto.setFechaCreacion(LocalDateTime.now());
        proyecto.setIdUsuarioCreacion(idUsuarioAprobacion);
        proyecto.setIdUsuarioActualizacion(idUsuarioAprobacion);

        Integer diasVencimiento = configuracionService.getDiasVencimientoPago();
        proyecto.setFechaVencimiento(LocalDate.now().plusDays(diasVencimiento != null ? diasVencimiento : 30));

        return proyectoRepository.save(proyecto);
    }

    @Transactional
    public void anularCotizacion(Integer idCotizacion, String motivo, Integer idUsuarioAnulacion) {
        Cotizacion cotizacion = cotizacionRepository.findById(idCotizacion)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        if ("Aprobada".equals(cotizacion.getEstado())) {
            throw new RuntimeException("No se puede anular una cotización aprobada");
        }

        if ("Anulada".equals(cotizacion.getEstado())) {
            throw new RuntimeException("La cotización ya está anulada");
        }

        cotizacion.anular(idUsuarioAnulacion);
        cotizacion.setObservaciones((cotizacion.getObservaciones() != null ? 
                                   cotizacion.getObservaciones() + "\n" : "") + 
                                   "Motivo de anulación: " + (motivo != null ? motivo : "Sin motivo"));
        cotizacionRepository.save(cotizacion);
    }

    public Cotizacion findById(Integer idCotizacion) {
        return cotizacionRepository.findById(idCotizacion).orElse(null);
    }

    public Page<Cotizacion> findAll(Pageable pageable) {
        return cotizacionRepository.findAll(pageable);
    }

    public Page<Cotizacion> findByEstado(String estado, Pageable pageable) {
        return cotizacionRepository.findByEstado(estado, pageable);
    }

    public Page<Cotizacion> findByCliente(Integer idCliente, Pageable pageable) {
        return cotizacionRepository.findByIdCliente(idCliente, pageable);
    }

    public List<Cotizacion> findPendientes() {
        return cotizacionRepository.findPendientes();
    }

    public Page<Cotizacion> findPendientes(Pageable pageable) {
        return cotizacionRepository.findPendientes(pageable);
    }

    public Page<Cotizacion> findAprobadas(Pageable pageable) {
        return cotizacionRepository.findAprobadas(pageable);
    }

    public List<Cotizacion> findVencidas() {
        return cotizacionRepository.findVencidas(LocalDate.now());
    }

    public Page<Cotizacion> search(String searchText, Pageable pageable) {
        return cotizacionRepository.search(searchText, pageable);
    }

    public List<Cotizacion> findTopN(int limit) {
        return cotizacionRepository.findTopN(limit);
    }

    public List<Cotizacion> findTopNPendientes(int limit) {
        return cotizacionRepository.findTopNPendientes(limit);
    }

    public List<CotizacionVersion> getVersionesByCotizacion(Integer idCotizacion) {
        List<CotizacionVersion> versiones = cotizacionVersionRepository.findByIdCotizacionOrderByNumeroVersionDesc(idCotizacion);
        
        Cotizacion cotizacion = findById(idCotizacion);
        if (cotizacion != null) {
            for (CotizacionVersion version : versiones) {
                if (version.getNumeroVersion().equals(cotizacion.getVersionActual())) {
                    version.setEsVersionActual(true);
                }
            }
        }
        
        return versiones;
    }

    public CotizacionVersion findVersionById(Integer idVersion) {
        return cotizacionVersionRepository.findById(idVersion).orElse(null);
    }

    public List<CotizacionDetalle> getDetallesByCotizacion(Integer idCotizacion) {
        return cotizacionDetalleRepository.findDetallesByCotizacion(idCotizacion);
    }

    public List<CotizacionDetalle> getDetallesByVersion(Integer idVersion) {
        return cotizacionDetalleRepository.findByIdVersion(idVersion);
    }

    public long count() {
        return cotizacionRepository.count();
    }

    public long countByEstado(String estado) {
        return cotizacionRepository.countByEstado(estado);
    }

    public long countByCliente(Integer idCliente) {
        return cotizacionRepository.countByIdCliente(idCliente);
    }

    public BigDecimal sumTotalAprobadas() {
        return cotizacionRepository.sumTotalAprobadas();
    }

    public BigDecimal sumTotalByCliente(Integer idCliente) {
        return cotizacionRepository.sumTotalByCliente(idCliente);
    }

    public List<Object[]> countByEstadoResumen() {
        return cotizacionRepository.countByEstadoResumen();
    }

    public boolean hasProyecto(Integer idCotizacion) {
        return proyectoRepository.existsByIdCotizacion(idCotizacion);
    }

    public boolean canEdit(Integer idCotizacion) {
        Cotizacion cotizacion = findById(idCotizacion);
        if (cotizacion == null) {
            return false;
        }
        return cotizacion.puedeEditar();
    }

    public boolean canApprove(Integer idCotizacion) {
        Cotizacion cotizacion = findById(idCotizacion);
        if (cotizacion == null) {
            return false;
        }
        return cotizacion.puedeAprobar() && !hasProyecto(idCotizacion);
    }

    public boolean canAnular(Integer idCotizacion) {
        Cotizacion cotizacion = findById(idCotizacion);
        if (cotizacion == null) {
            return false;
        }
        return cotizacion.puedeAnular();
    }

    public BigDecimal calcularSubtotal(List<CotizacionDetalle> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return detalles.stream()
                .map(CotizacionDetalle::getSubtotalLinea)
                .filter(s -> s != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularIgv(BigDecimal subtotal, BigDecimal tasaIgv) {
        if (subtotal == null || tasaIgv == null) {
            return BigDecimal.ZERO;
        }
        return subtotal.multiply(tasaIgv)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularTotal(BigDecimal subtotal, BigDecimal igv) {
        if (subtotal == null || igv == null) {
            return BigDecimal.ZERO;
        }
        return subtotal.add(igv);
    }

    public String getCodigoCotizacion(Integer idCotizacion) {
        if (idCotizacion == null) {
            return "COT-00000";
        }
        return "COT-" + String.format("%05d", idCotizacion);
    }

    public String getEstadoLegible(String estado) {
        if (estado == null) {
            return "Sin estado";
        }
        return switch (estado) {
            case "Pendiente" -> "Pendiente";
            case "Aprobada" -> "Aprobada";
            case "Anulada" -> "Anulada";
            default -> estado;
        };
    }

    public String getEstadoBadgeClass(String estado) {
        if (estado == null) {
            return "status-pendiente";
        }
        return switch (estado) {
            case "Pendiente" -> "status-pendiente";
            case "Aprobada" -> "status-validado";
            case "Anulada" -> "status-rechazado";
            default -> "status-pendiente";
        };
    }

    public String getEstadoColor(String estado) {
        if (estado == null) {
            return "#6B7280";
        }
        return switch (estado) {
            case "Pendiente" -> "#F59E0B";
            case "Aprobada" -> "#10B981";
            case "Anulada" -> "#EF4444";
            default -> "#6B7280";
        };
    }

    public List<Cotizacion> findTop10ByEstadoOrderByFechaEmisionDesc(String estado) {
        return cotizacionRepository.findByEstado(estado, PageRequest.of(0, 10, Sort.by("fechaEmision").descending()))
                .getContent();
    }

    public List<Cotizacion> findPendientesAntiguas(int limit) {
        return cotizacionRepository.findTopNPendientes(limit);
    }
}