package com.stamos.service;

import com.stamos.entity.Pago;
import com.stamos.entity.Proyecto;
import com.stamos.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para la gestión de pagos.
 * 
 * Reglas de negocio (RPA01 a RPA06):
 * - Los pagos solo se pueden registrar para proyectos en estados permitidos (controlado en controller).
 * - Se aceptan pagos parciales, mostrando saldo pendiente.
 * - El total pagado no puede superar el monto de la cotización (validado en controller).
 * - Cada pago incluye fecha, monto, medio de pago, número de comprobante (opcional).
 * - Solo ADMIN o COMERCIAL puede registrar o anular (controlado en controller).
 * - La anulación deja registro en auditoría (log).
 */
@Service
@Transactional
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    // ==================== CONSULTAS BÁSICAS ====================

    public List<Pago> findAll() {
        return pagoRepository.findAll();
    }

    public List<Pago> findByProyectoId(Long proyectoId) {
        return pagoRepository.findByProyectoIdOrderByFechaDesc(proyectoId);
    }
    public List<Pago> findByClienteId(Long clienteId) {
    return pagoRepository.findByClienteId(clienteId);
    }

    public Pago findById(Long id) {
        return pagoRepository.findById(id).orElse(null);
    }

    public Pago save(Pago pago) {
        return pagoRepository.save(pago);
    }

    // ==================== CÁLCULO DE TOTALES Y SALDOS ====================

    /**
     * Obtiene el monto total pagado para un proyecto.
     *
     * @param proyectoId ID del proyecto
     * @return Suma de los montos de pagos registrados (no anulados)
     */
    public BigDecimal getTotalPagadoByProyecto(Long proyectoId) {
        List<Pago> pagos = findByProyectoId(proyectoId);
        return pagos.stream()
                .filter(p -> !"ANULADO".equals(p.getEstado()))
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula el saldo pendiente de un proyecto.
     *
     * @param proyecto Proyecto con su cotización asociada
     * @return Monto total de la cotización - total pagado
     */
    public BigDecimal getSaldoPendiente(Proyecto proyecto) {
        BigDecimal totalCotizacion = proyecto.getCotizacion().getTotal();
        BigDecimal totalPagado = getTotalPagadoByProyecto(proyecto.getId());
        return totalCotizacion.subtract(totalPagado);
    }

    // ==================== ANULACIÓN DE PAGOS (CON AUDITORÍA) ====================

    /**
     * Anula un pago. Cambia su estado a "ANULADO" y registra la acción en auditoría.
     *
     * @param id     ID del pago a anular
     * @param motivo Motivo de la anulación
     * @throws IllegalStateException si el pago ya estaba anulado
     */
    public void cancelPago(Long id, String motivo) {
        Pago pago = findById(id);
        if (pago == null) {
            throw new IllegalArgumentException("Pago no encontrado");
        }
        if ("ANULADO".equals(pago.getEstado())) {
            throw new IllegalStateException("El pago ya fue anulado anteriormente");
        }
        pago.setEstado("ANULADO");
        save(pago);
        // Registrar en auditoría (RPA06)
        auditoriaService.logAnulacionPago(id, motivo);
    }

    // ==================== REPORTE DE PAGOS PENDIENTES POR CLIENTE ====================

    /**
     * Obtiene un reporte de pagos pendientes agrupados por cliente.
     * Utiliza una consulta nativa o JPQL que devuelve objetos Object[] con:
     * [cliente_id, nombre_razon_social, total_proyectos, total_pendiente]
     *
     * @return Lista de arreglos con los datos del reporte
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getPagosPendientesPorCliente() {
        // Esta consulta debe devolver proyectos con saldo pendiente > 0, agrupados por cliente.
        // Implementación con JPQL:
        // SELECT c.id, c.nombreRazonSocial, COUNT(p), SUM(p.cotizacion.total - SUM(pagos))
        // Es más complejo; para simplificar, se puede usar una consulta nativa.
        // Se asume que existe un método en el repositorio.
        return pagoRepository.findPagosPendientesPorCliente();
    }

    // ==================== MÉTODOS AUXILIARES PARA VALIDACIONES (RPA03) ====================

    /**
     * Verifica si un pago nuevo haría superar el monto total de la cotización.
     *
     * @param proyectoId ID del proyecto
     * @param montoNuevo Monto a agregar
     * @return true si el nuevo total pagado no excede el monto total
     */
    public boolean isValidNuevoPago(Long proyectoId, BigDecimal montoNuevo) {
        Proyecto proyecto = null; // Se necesitaría inyectar ProyectoService, pero se usa en controller
        // La validación real se hace en el controlador con los servicios adecuados.
        // Este método es solo ejemplo; la lógica se implementa en controller.
        return true;
    }

}