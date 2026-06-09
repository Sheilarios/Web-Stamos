package com.stamos.service;

import com.stamos.entity.Pago;
import com.stamos.entity.Proyecto;
import com.stamos.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Chunk;
import com.lowagie.text.FontFactory;


import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ReporteService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private ReservaEquipoRepository reservaEquipoRepository;

    @Autowired
    private CotizacionRepository cotizacionRepository;


    public BigDecimal getTotalPendientes() {
        List<Proyecto> proyectosActivos = proyectoRepository.findByEstadoNotIn(Arrays.asList("ENTREGADO", "ANULADO"));
        BigDecimal total = BigDecimal.ZERO;
        for (Proyecto p : proyectosActivos) {
            BigDecimal pagado = pagoRepository.findByProyectoIdOrderByFechaDesc(p.getId())
                    .stream()
                    .filter(pago -> !"ANULADO".equals(pago.getEstado()))
                    .map(Pago::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalCotizacion = p.getCotizacion().getTotal();
            total = total.add(totalCotizacion.subtract(pagado));
        }
        return total;
    }

    public int getTotalProyectosAtrasados() {
        LocalDate today = LocalDate.now();
        List<String> estadosNoFinales = Arrays.asList("COTIZADO", "APROBADO", "EN_DISEÑO", "EN_PRODUCCION", "INSTALACION_MONTAJE");
        return proyectoRepository.countByEstadoInAndFechaEntregaEstimadaBefore(estadosNoFinales, today);
    }


    public List<Object[]> getPagosPendientesPorCliente() {
        return pagoRepository.findPagosPendientesPorCliente();
    }


    public List<Proyecto> getProyectosAtrasados() {
        LocalDate today = LocalDate.now();
        List<String> estadosNoFinales = Arrays.asList("COTIZADO", "APROBADO", "EN_DISEÑO", "EN_PRODUCCION", "INSTALACION_MONTAJE");
        return proyectoRepository.findByEstadoInAndFechaEntregaEstimadaBefore(estadosNoFinales, today);
    }

    
    public List<Object[]> getEquiposMasAlquilados() {
        return reservaEquipoRepository.findEquiposMasAlquilados();
    }

    public Map<String, Object> getIngresosMensual(int anio, int mes) {
        YearMonth yearMonth = YearMonth.of(anio, mes);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();
        BigDecimal ingresos = pagoRepository.sumMontoByFechaBetween(start, end);
        ingresos = ingresos != null ? ingresos : BigDecimal.ZERO;
        Map<String, Object> result = new HashMap<>();
        result.put("periodo", yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        result.put("ingresos", ingresos);
        result.put("detalle", pagoRepository.findByFechaBetween(start, end));
        return result;
    }

    public Map<String, Object> getIngresosTrimestral(int anio, int trimestre) {
        int startMonth = (trimestre - 1) * 3 + 1;
        int endMonth = startMonth + 2;
        LocalDate start = LocalDate.of(anio, startMonth, 1);
        LocalDate end = LocalDate.of(anio, endMonth, YearMonth.of(anio, endMonth).lengthOfMonth());
        BigDecimal ingresos = pagoRepository.sumMontoByFechaBetween(start, end);
        ingresos = ingresos != null ? ingresos : BigDecimal.ZERO;
        Map<String, Object> result = new HashMap<>();
        result.put("periodo", "Trimestre " + trimestre + " de " + anio);
        result.put("ingresos", ingresos);
        result.put("detalle", pagoRepository.findByFechaBetween(start, end));
        return result;
    }


    public Map<String, Long> getCotizacionesAprobadasVsRechazadas() {
        long aprobadas = cotizacionRepository.countByEstado("APROBADA");
        long rechazadas = cotizacionRepository.countByEstado("RECHAZADA");
        Map<String, Long> result = new HashMap<>();
        result.put("APROBADAS", aprobadas);
        result.put("RECHAZADAS", rechazadas);
        return result;
    }
}