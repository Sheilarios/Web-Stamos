package com.stamos.service;

import com.stamos.entity.Cotizacion;
import com.stamos.entity.DetalleCotizacion;
import com.stamos.repository.CotizacionRepository;
import com.stamos.repository.DetalleCotizacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class CotizacionService {

    @Autowired
    private CotizacionRepository cotizacionRepository;

    @Autowired
    private DetalleCotizacionRepository detalleCotizacionRepository;

    @Value("${stamos.igv.default:18.0}")
    private double igvPorcentaje;


    public List<Cotizacion> findAll() {
        return cotizacionRepository.findAll();
    }

    public List<Cotizacion> findByEstado(String estado) {
        return cotizacionRepository.findByEstado(estado);
    }

    public List<Cotizacion> findByClienteId(Long clienteId) {
        return cotizacionRepository.findByClienteId(clienteId);
    }

    public Cotizacion findById(Long id) {
        return cotizacionRepository.findById(id).orElse(null);
    }

    public boolean existsByClienteId(Long clienteId) {
        return cotizacionRepository.existsByClienteId(clienteId);
    }

    public long countByEstado(String estado) {
        return cotizacionRepository.countByEstado(estado);
    }

    public long countByFechaBetween(LocalDate inicio, LocalDate fin) {
        return cotizacionRepository.countByFechaEmisionBetween(inicio, fin);
    }

    public long countByMesActual() {
        LocalDate now = LocalDate.now();
        LocalDate inicioMes = now.withDayOfMonth(1);
        LocalDate finMes = now.withDayOfMonth(now.lengthOfMonth());
        return countByFechaBetween(inicioMes, finMes);
    }

    public List<Cotizacion> findTop5ByOrderByFechaEmisionDesc() {
        return cotizacionRepository.findTop5ByOrderByFechaEmisionDesc();
    }

    public List<Cotizacion> findByClienteIdOrderByFechaEmisionDesc(Long clienteId) {
        return cotizacionRepository.findByClienteIdOrderByFechaEmisionDesc(clienteId);
    }

    public List<Cotizacion> findByFechaEmisionBetween(LocalDate inicio, LocalDate fin) {
        return cotizacionRepository.findByFechaEmisionBetween(inicio, fin);
    }

    public long getTotalCotizacionesMes() {
        return countByMesActual();
    }

    public BigDecimal getTotalCotizadoMes() {
        LocalDate now = LocalDate.now();
        LocalDate inicioMes = now.withDayOfMonth(1);
        LocalDate finMes = now.withDayOfMonth(now.lengthOfMonth());
        return cotizacionRepository.sumTotalByFechaEmisionBetween(inicioMes, finMes);
    }

    public void calcularTotales(Cotizacion cotizacion) {
        if (cotizacion.getItems() == null || cotizacion.getItems().isEmpty()) {
            cotizacion.setSubtotal(BigDecimal.ZERO);
            cotizacion.setIgv(BigDecimal.ZERO);
            cotizacion.setTotal(BigDecimal.ZERO);
            return;
        }

        BigDecimal subtotal = cotizacion.getItems().stream()
                .map(item -> item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal igv = subtotal.multiply(BigDecimal.valueOf(igvPorcentaje / 100.0))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(igv);

        cotizacion.setSubtotal(subtotal);
        cotizacion.setIgv(igv);
        cotizacion.setTotal(total);
    }

    public BigDecimal calcularIgv(BigDecimal subtotal) {
        return subtotal.multiply(BigDecimal.valueOf(igvPorcentaje / 100.0))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularTotal(BigDecimal subtotal) {
        return subtotal.add(calcularIgv(subtotal));
    }

    public Cotizacion save(Cotizacion cotizacion) {
        if (cotizacion.getId() != null) {
            Cotizacion existing = findById(cotizacion.getId());
            if (existing != null && "APROBADA".equals(existing.getEstado())) {
                throw new IllegalStateException("No se puede modificar una cotización aprobada.");
            }
        }
        
        if (cotizacion.getItems() != null && !cotizacion.getItems().isEmpty()) {
            calcularTotales(cotizacion);
        }
        
        return cotizacionRepository.save(cotizacion);
    }

    @Transactional
    public Cotizacion saveWithItems(Cotizacion cotizacion, List<DetalleCotizacion> items) {
        cotizacion.setItems(items);
        for (DetalleCotizacion item : items) {
            item.setCotizacion(cotizacion);
        }
        calcularTotales(cotizacion);
        return save(cotizacion);
    }

    public Cotizacion crearNuevaVersion(Cotizacion original) {
        Cotizacion nueva = new Cotizacion();
        nueva.setCliente(original.getCliente());
        nueva.setFechaEmision(LocalDate.now());
        nueva.setValidezDias(original.getValidezDias());
        nueva.setEstado("EMITIDA");
        nueva.setVersion(original.getVersion() + 1);
        nueva.setVersionBase(original);

        for (DetalleCotizacion item : original.getItems()) {
            DetalleCotizacion nuevoItem = new DetalleCotizacion();
            nuevoItem.setConcepto(item.getConcepto());
            nuevoItem.setCantidad(item.getCantidad());
            nuevoItem.setPrecioUnitario(item.getPrecioUnitario());
            nuevoItem.setCotizacion(nueva);
            nueva.getItems().add(nuevoItem);
        }

        calcularTotales(nueva);
        return nueva;
    }

    public List<Cotizacion> findVersionesByBaseId(Long baseId) {
        Cotizacion base = findById(baseId);
        if (base == null) return List.of();
        return cotizacionRepository.findByVersionBase(base);
    }

    public void aprobarCotizacion(Long id) {
        Cotizacion cotizacion = findById(id);
        if (cotizacion == null) {
            throw new IllegalArgumentException("Cotización no encontrada");
        }
        if (!"EMITIDA".equals(cotizacion.getEstado())) {
            throw new IllegalStateException("Solo se pueden aprobar cotizaciones en estado EMITIDA");
        }
        cotizacion.setEstado("APROBADA");
        save(cotizacion);
    }

    public void rechazarCotizacion(Long id, String motivo) {
        Cotizacion cotizacion = findById(id);
        if (cotizacion == null) {
            throw new IllegalArgumentException("Cotización no encontrada");
        }
        cotizacion.setEstado("RECHAZADA");
        save(cotizacion);
    }

    public void anularCotizacion(Long id) {
        Cotizacion cotizacion = findById(id);
        if (cotizacion == null) {
            throw new IllegalArgumentException("Cotización no encontrada");
        }
        cotizacion.setEstado("ANULADA");
        save(cotizacion);
    }

    public boolean isModificable(Long id) {
        Cotizacion cotizacion = findById(id);
        if (cotizacion == null) return false;
        return !"APROBADA".equals(cotizacion.getEstado()) && !"ANULADA".equals(cotizacion.getEstado());
    }

    public boolean hasActiveQuotesForClient(Long clienteId) {
        List<Cotizacion> cotizaciones = findByClienteId(clienteId);
        return cotizaciones.stream()
                .anyMatch(c -> !List.of("ANULADA", "RECHAZADA").contains(c.getEstado()));
    }

    public BigDecimal getTotalAprobado() {
        return cotizacionRepository.sumTotalByEstado("APROBADA");
    }

    public BigDecimal getTotalByCliente(Long clienteId) {
        return cotizacionRepository.sumTotalByClienteId(clienteId);
    }
}