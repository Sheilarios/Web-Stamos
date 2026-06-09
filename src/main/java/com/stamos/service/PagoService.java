package com.stamos.service;
import com.stamos.entity.Pago;
import com.stamos.entity.Proyecto;
import com.stamos.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@Transactional
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private AuditoriaService auditoriaService;


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

    public List<Pago> findTop5ByOrderByFechaDesc() {
        return pagoRepository.findTop5ByOrderByFechaDesc();
    }

    public BigDecimal getTotalPagadoEnMes(LocalDate fecha) {
        if (fecha == null) {
            fecha = LocalDate.now();
        }
        YearMonth yearMonth = YearMonth.from(fecha);
        LocalDate inicioMes = yearMonth.atDay(1);
        LocalDate finMes = yearMonth.atEndOfMonth();
        
        BigDecimal total = pagoRepository.sumMontoByFechaBetweenAndEstadoNot(inicioMes, finMes, "ANULADO");
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getTotalPorCobrar() {
        BigDecimal total = pagoRepository.sumSaldoPendienteByProyectosActivos();
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getTotalVencido() {
        List<String> estadosExcluidos = List.of("VERIFICADO", "ANULADO");
        BigDecimal total = pagoRepository.sumMontoVencidoByFechaBeforeAndEstadoNot(LocalDate.now(), estadosExcluidos);
        return total != null ? total : BigDecimal.ZERO;
    }

    public int countComprobantesPendientes() {
        return pagoRepository.countByEstado("VERIFICACION");
    }

    public BigDecimal getTotalPagadoHistorico() {
        BigDecimal total = pagoRepository.sumTotalPagado();
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getTotalPagadoByProyecto(Long proyectoId) {
        BigDecimal total = pagoRepository.sumMontoByProyectoAndEstadoNot(proyectoId, "ANULADO");
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getSaldoPendiente(Proyecto proyecto) {
        if (proyecto == null || proyecto.getCotizacion() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalCotizacion = proyecto.getCotizacion().getTotal();
        BigDecimal totalPagado = getTotalPagadoByProyecto(proyecto.getId());
        return totalCotizacion.subtract(totalPagado);
    }

    public void cancelPago(Long id, String motivo) {
        Pago pago = findById(id);
        if (pago == null) {
            throw new IllegalArgumentException("Pago no encontrado");
        }
        if ("ANULADO".equals(pago.getEstado())) {
            throw new IllegalStateException("El pago ya fue anulado anteriormente");
        }
        
        String estadoAnterior = pago.getEstado();
        pago.setEstado("ANULADO");
        save(pago);
        
        if (auditoriaService != null) {
            auditoriaService.logAnulacionPago(id, motivo, estadoAnterior);
        }
    }

    public List<Object[]> getPagosPendientesPorCliente() {
        return pagoRepository.findPagosPendientesPorCliente();
    }

    public List<Pago> getPagosByProyecto(Long proyectoId) {
        return findByProyectoId(proyectoId);
    }

    public boolean isValidNuevoPago(Long proyectoId, BigDecimal montoNuevo) {
        return true;
    }
}