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
import java.util.List;

/**
 * Servicio para la gestión de cotizaciones.
 * Implementa reglas de negocio:
 * - Cálculo automático de subtotal, IGV y total.
 * - Gestión de versiones (historial antes de aprobación).
 * - Bloqueo de modificación de cotizaciones aprobadas.
 * - Validación de existencia para clientes (útil en deshabilitación).
 */
@Service
@Transactional
public class CotizacionService {

    @Autowired
    private CotizacionRepository cotizacionRepository;

    @Autowired
    private DetalleCotizacionRepository detalleCotizacionRepository;

    @Value("${stamos.igv.default:18.0}")
    private double igvPorcentaje;

    // ==================== CONSULTAS BÁSICAS ====================

    /**
     * Obtiene todas las cotizaciones.
     * @return 
     */
    public List<Cotizacion> findAll() {
        return cotizacionRepository.findAll();
    }

    /**
     * Obtiene cotizaciones por estado.
     *
     * @param estado Estado (EMITIDA, APROBADA, RECHAZADA, ANULADA)
     * @return 
     */
    public List<Cotizacion> findByEstado(String estado) {
        return cotizacionRepository.findByEstado(estado);
    }

    /**
     * Obtiene cotizaciones por cliente.
     * @param clienteId
     * @return 
     */
    public List<Cotizacion> findByClienteId(Long clienteId) {
        return cotizacionRepository.findByClienteId(clienteId);
    }

    /**
     * Obtiene una cotización por su ID.
     * @param id
     * @return 
     */
    public Cotizacion findById(Long id) {
        return cotizacionRepository.findById(id).orElse(null);
    }

    /**
     * Verifica si un cliente tiene cotizaciones asociadas (útil para deshabilitar cliente).
     * @param clienteId
     * @return 
     */
    public boolean existsByClienteId(Long clienteId) {
        return cotizacionRepository.existsByClienteId(clienteId);
    }

    // ==================== CÁLCULO DE TOTALES ====================

    /**
     * Calcula subtotal, IGV y total de una cotización en base a sus ítems.
     * Actualiza los campos de la entidad.
     *
     * @param cotizacion Cotización a calcular (debe tener items cargados)
     */
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

    // ==================== GUARDADO Y VERSIONADO ====================

    /**
     * Guarda una cotización. Si la cotización tiene ítems, estos se persisten en cascada.
     * No se permite guardar si la cotización está aprobada y se intenta modificar.
     *
     * @param cotizacion Cotización a guardar
     * @return Cotización persistida
     * @throws IllegalStateException si se intenta guardar una cotización aprobada con cambios
     */
    public Cotizacion save(Cotizacion cotizacion) {
        // Validar que no se modifique una cotización aprobada (excepto si es nueva)
        if (cotizacion.getId() != null) {
            Cotizacion existing = findById(cotizacion.getId());
            if (existing != null && "APROBADA".equals(existing.getEstado())) {
                throw new IllegalStateException("No se puede modificar una cotización aprobada.");
            }
        }
        return cotizacionRepository.save(cotizacion);
    }

    /**
     * Crea una nueva versión de una cotización a partir de una existente.
     * Copia los datos básicos y los ítems, incrementa la versión y deja la versión base.
     *
     * @param original Cotización original (no se modifica)
     * @return Nueva cotización (versión +1) lista para ser editada (aún no persistida)
     */
    public Cotizacion crearNuevaVersion(Cotizacion original) {
        Cotizacion nueva = new Cotizacion();
        nueva.setCliente(original.getCliente());
        nueva.setFechaEmision(original.getFechaEmision());
        nueva.setValidezDias(original.getValidezDias());
        nueva.setEstado("EMITIDA"); // Nueva versión inicia como emitida
        nueva.setVersion(original.getVersion() + 1);
        nueva.setVersionBase(original); // Relación con versión anterior

        // Copiar ítems
        for (DetalleCotizacion item : original.getItems()) {
            DetalleCotizacion nuevoItem = new DetalleCotizacion();
            nuevoItem.setConcepto(item.getConcepto());
            nuevoItem.setCantidad(item.getCantidad());
            nuevoItem.setPrecioUnitario(item.getPrecioUnitario());
            nuevoItem.setCotizacion(nueva);
            nueva.getItems().add(nuevoItem);
        }

        // Calcular totales basado en los ítems copiados
        calcularTotales(nueva);
        return nueva;
    }

    /**
     * Obtiene todas las versiones de una cotización (incluyendo la base y las derivadas).
     *
     * @param baseId ID de la cotización base (versión original)
     * @return Lista de cotizaciones que tienen como base la indicada o que son la base
     */
    public List<Cotizacion> findVersionesByBaseId(Long baseId) {
        Cotizacion base = findById(baseId);
        if (base == null) return List.of();
        return cotizacionRepository.findByVersionBase(base);
    }

    // ==================== MÉTODOS AUXILIARES PARA ELIMINACIÓN LÓGICA DE CLIENTES ====================

    /**
     * Verifica si un cliente tiene cotizaciones en estado distinto a ANULADA o RECHAZADA.
     * Útil para determinar si se puede deshabilitar.
     *
     * @param clienteId ID del cliente
     * @return true si tiene cotizaciones activas (no anuladas ni rechazadas)
     */
    public boolean hasActiveQuotesForClient(Long clienteId) {
        List<Cotizacion> cotizaciones = findByClienteId(clienteId);
        return cotizaciones.stream()
                .anyMatch(c -> !List.of("ANULADA", "RECHAZADA").contains(c.getEstado()));
    }
}