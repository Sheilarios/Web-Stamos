package com.stamos.service;

import com.stamos.model.*;
import com.stamos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProyectoService {

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private CotizacionRepository cotizacionRepository;

    @Autowired
    private EstadoProyectoRepository estadoProyectoRepository;

    @Autowired
    private CambioEstadoProyectoRepository cambioEstadoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private ConfiguracionSistemaService configuracionService;

    @Autowired
    private TransicionEstadoService transicionEstadoService;

    @Transactional
    public Proyecto crearDesdeCotizacion(Integer idCotizacion, Integer idUsuarioCreacion) {
        if (idCotizacion == null) {
            throw new RuntimeException("El ID de la cotización no puede ser nulo");
        }
        Cotizacion cotizacion = cotizacionRepository.findById(idCotizacion)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        if (!"Aprobada".equals(cotizacion.getEstado())) {
            throw new RuntimeException("La cotización debe estar aprobada para generar un proyecto");
        }

        if (proyectoRepository.existsByIdCotizacion(idCotizacion)) {
            throw new RuntimeException("La cotización ya tiene un proyecto asociado");
        }

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
        proyecto.setIdUsuarioCreacion(idUsuarioCreacion);
        proyecto.setIdUsuarioActualizacion(idUsuarioCreacion);

        Integer diasVencimiento = configuracionService.getDiasVencimientoPago();
        proyecto.setFechaVencimiento(LocalDate.now().plusDays(diasVencimiento != null ? diasVencimiento : 30));

        Proyecto proyectoGuardado = proyectoRepository.save(proyecto);
        enriquecerProyecto(proyectoGuardado);
        return proyectoGuardado;
    }

    @Transactional
    public Proyecto cambiarEstado(Integer idProyecto, Integer idEstadoNuevo,
                                  Integer idUsuarioResponsable, String comentario) {
        if (idProyecto == null || idEstadoNuevo == null || idUsuarioResponsable == null) {
            throw new RuntimeException("Los IDs no pueden ser nulos");
        }
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + idProyecto));

        EstadoProyecto estadoActual = estadoProyectoRepository.findById(proyecto.getIdEstadoActual())
                .orElseThrow(() -> new RuntimeException("Estado actual no encontrado"));

        EstadoProyecto estadoNuevo = estadoProyectoRepository.findById(idEstadoNuevo)
                .orElseThrow(() -> new RuntimeException("Estado nuevo no encontrado con ID: " + idEstadoNuevo));

        if (estadoActual.esEstadoTerminal()) {
            throw new RuntimeException("No se puede cambiar el estado de un proyecto " +
                    estadoActual.getNombreEstado().toLowerCase());
        }

        if (estadoActual.getIdEstado().equals(idEstadoNuevo)) {
            throw new RuntimeException("El proyecto ya se encuentra en ese estado");
        }

        if (!transicionEstadoService.esTransicionValidaConOrden(proyecto, idEstadoNuevo, idUsuarioResponsable)) {
            throw new RuntimeException("No tienes permiso para realizar esta transición de estado o la transición no es válida");
        }

        Usuario usuario = usuarioRepository.findById(idUsuarioResponsable)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuarioResponsable));

        Integer idEstadoAnterior = proyecto.getIdEstadoActual();
        proyecto.setIdEstadoActual(idEstadoNuevo);
        proyecto.setFechaActualizacion(LocalDateTime.now());
        proyecto.setIdUsuarioActualizacion(idUsuarioResponsable);

        if (estadoNuevo.esEntregado()) {
            proyecto.setFechaEntregaReal(LocalDate.now());
        }

        if (estadoNuevo.esAnulado()) {
            proyecto.setFechaVencimiento(LocalDate.now());
        }

        Proyecto proyectoActualizado = proyectoRepository.save(proyecto);

        CambioEstadoProyecto cambio = new CambioEstadoProyecto();
        cambio.setIdProyecto(idProyecto);
        cambio.setIdEstadoAnterior(idEstadoAnterior);
        cambio.setIdEstadoNuevo(idEstadoNuevo);
        cambio.setIdUsuarioResponsable(idUsuarioResponsable);
        cambio.setFechaCambio(LocalDateTime.now());
        cambio.setComentario(comentario);
        cambio.setEstadoAnteriorNombre(estadoActual.getNombreEstado());
        cambio.setEstadoNuevoNombre(estadoNuevo.getNombreEstado());
        cambio.setResponsableNombre(usuario.getNombreCompleto());
        cambio.setResponsableEmail(usuario.getEmail());

        cambioEstadoRepository.save(cambio);
        enriquecerProyecto(proyectoActualizado);
        return proyectoActualizado;
    }

    @Transactional
    public Proyecto anularProyecto(Integer idProyecto, String motivo, Integer idUsuarioResponsable) {
        EstadoProyecto estadoAnulado = estadoProyectoRepository.findByNombreEstado("Anulado")
                .orElseThrow(() -> new RuntimeException("Estado 'Anulado' no encontrado"));

        String comentario = "Anulación: " + (motivo != null ? motivo : "Sin motivo");
        return cambiarEstado(idProyecto, estadoAnulado.getIdEstado(), idUsuarioResponsable, comentario);
    }

    @Transactional
    public Proyecto entregarProyecto(Integer idProyecto, String comentario, Integer idUsuarioResponsable) {
        EstadoProyecto estadoEntregado = estadoProyectoRepository.findByNombreEstado("Entregado")
                .orElseThrow(() -> new RuntimeException("Estado 'Entregado' no encontrado"));

        return cambiarEstado(idProyecto, estadoEntregado.getIdEstado(), idUsuarioResponsable, comentario);
    }

    @Transactional
    public Proyecto avanzarEstado(Integer idProyecto, Integer idUsuarioResponsable, String comentario) {
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + idProyecto));

        EstadoProyecto estadoActual = estadoProyectoRepository.findById(proyecto.getIdEstadoActual())
                .orElseThrow(() -> new RuntimeException("Estado actual no encontrado"));

        if (estadoActual.esEstadoTerminal()) {
            throw new RuntimeException("El proyecto está en estado terminal, no puede avanzar");
        }

        EstadoProyecto estadoSiguiente = estadoProyectoRepository.findSiguienteEstado(estadoActual.getOrdenFlujo())
                .orElseThrow(() -> new RuntimeException("No hay un estado siguiente disponible"));

        return cambiarEstado(idProyecto, estadoSiguiente.getIdEstado(), idUsuarioResponsable, comentario);
    }

    // ✅ CORREGIDO: Verifica null antes de consultar el repositorio
    public Proyecto findById(Integer idProyecto) {
        if (idProyecto == null) {
            return null;
        }
        Proyecto proyecto = proyectoRepository.findById(idProyecto).orElse(null);
        if (proyecto != null) {
            enriquecerProyecto(proyecto);
        }
        return proyecto;
    }

    public List<Proyecto> findAll() {
        List<Proyecto> proyectos = proyectoRepository.findAll();
        for (Proyecto proyecto : proyectos) {
            enriquecerProyecto(proyecto);
        }
        return proyectos;
    }

    public Page<Proyecto> findAll(Pageable pageable) {
        Page<Proyecto> proyectos = proyectoRepository.findAll(pageable);
        for (Proyecto proyecto : proyectos.getContent()) {
            enriquecerProyecto(proyecto);
        }
        return proyectos;
    }

    // ✅ CORREGIDO: Verifica null antes de consultar el repositorio
    public List<Proyecto> findByCliente(Integer idCliente) {
        if (idCliente == null) {
            return List.of();
        }
        List<Proyecto> proyectos = proyectoRepository.findByIdClienteOrderByFechaInicioDesc(idCliente);
        for (Proyecto proyecto : proyectos) {
            enriquecerProyecto(proyecto);
        }
        return proyectos;
    }

    // ✅ CORREGIDO: Verifica null antes de consultar el repositorio
    public Page<Proyecto> findByCliente(Integer idCliente, Pageable pageable) {
        if (idCliente == null) {
            return Page.empty(pageable);
        }
        Page<Proyecto> proyectos = proyectoRepository.findByIdCliente(idCliente, pageable);
        for (Proyecto proyecto : proyectos.getContent()) {
            enriquecerProyecto(proyecto);
        }
        return proyectos;
    }

    public List<Proyecto> findByEstado(String estado) {
        EstadoProyecto estadoProyecto = estadoProyectoRepository.findByNombreEstado(estado)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado: " + estado));
        List<Proyecto> proyectos = proyectoRepository.findByEstado(estadoProyecto.getIdEstado());
        for (Proyecto proyecto : proyectos) {
            enriquecerProyecto(proyecto);
        }
        return proyectos;
    }

    public Page<Proyecto> findByEstado(String estado, Pageable pageable) {
        EstadoProyecto estadoProyecto = estadoProyectoRepository.findByNombreEstado(estado)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado: " + estado));
        Page<Proyecto> proyectos = proyectoRepository.findByEstado(estadoProyecto.getIdEstado(), pageable);
        for (Proyecto proyecto : proyectos.getContent()) {
            enriquecerProyecto(proyecto);
        }
        return proyectos;
    }

    public List<Proyecto> findActivos() {
        List<Proyecto> proyectos = proyectoRepository.findActivos();
        for (Proyecto proyecto : proyectos) {
            enriquecerProyecto(proyecto);
        }
        return proyectos;
    }

    public Page<Proyecto> findActivos(Pageable pageable) {
        Page<Proyecto> proyectos = proyectoRepository.findActivos(pageable);
        for (Proyecto proyecto : proyectos.getContent()) {
            enriquecerProyecto(proyecto);
        }
        return proyectos;
    }

    public List<Proyecto> findAprobados() {
        List<Proyecto> proyectos = proyectoRepository.findAprobados();
        for (Proyecto proyecto : proyectos) {
            enriquecerProyecto(proyecto);
        }
        return proyectos;
    }

    public List<Proyecto> findEnDiseno() {
        List<Proyecto> proyectos = proyectoRepository.findEnDiseno();
        for (Proyecto proyecto : proyectos) {
            enriquecerProyecto(proyecto);
        }
        return proyectos;
    }

    public List<Proyecto> findEnProduccion() {
        List<Proyecto> proyectos = proyectoRepository.findEnProduccion();
        for (Proyecto proyecto : proyectos) {
            enriquecerProyecto(proyecto);
        }
        return proyectos;
    }

    public List<Proyecto> findInstalacionMontaje() {
        List<Proyecto> proyectos = proyectoRepository.findInstalacionMontaje();
        for (Proyecto proyecto : proyectos) {
            enriquecerProyecto(proyecto);
        }
        return proyectos;
    }

    public List<Proyecto> findEntregados() {
        List<Proyecto> proyectos = proyectoRepository.findEntregados();
        for (Proyecto proyecto : proyectos) {
            enriquecerProyecto(proyecto);
        }
        return proyectos;
    }

    public List<Proyecto> findAnulados() {
        List<Proyecto> proyectos = proyectoRepository.findAnulados();
        for (Proyecto proyecto : proyectos) {
            enriquecerProyecto(proyecto);
        }
        return proyectos;
    }

    public List<Proyecto> findVencidos() {
        List<Proyecto> proyectos = proyectoRepository.findVencidos(LocalDate.now());
        for (Proyecto proyecto : proyectos) {
            enriquecerProyecto(proyecto);
        }
        return proyectos;
    }

    public Page<Proyecto> findVencidos(Pageable pageable) {
        Page<Proyecto> proyectos = proyectoRepository.findVencidos(LocalDate.now(), pageable);
        for (Proyecto proyecto : proyectos.getContent()) {
            enriquecerProyecto(proyecto);
        }
        return proyectos;
    }

    public List<Proyecto> findProyectosConPagosVencidos() {
        List<Proyecto> proyectos = proyectoRepository.findProyectosConPagosVencidos(LocalDate.now());
        for (Proyecto proyecto : proyectos) {
            enriquecerProyecto(proyecto);
        }
        return proyectos;
    }

    // ✅ CORREGIDO: Verifica null antes de consultar el repositorio
    public List<Proyecto> findProyectosConPagosVencidosByCliente(Integer idCliente) {
        if (idCliente == null) {
            return List.of();
        }
        List<Proyecto> proyectos = proyectoRepository.findProyectosConPagosVencidosByCliente(idCliente, LocalDate.now());
        for (Proyecto proyecto : proyectos) {
            enriquecerProyecto(proyecto);
        }
        return proyectos;
    }

    public Page<Proyecto> search(String searchText, Pageable pageable) {
        return proyectoRepository.search(searchText, pageable);
    }

    public List<Proyecto> findTop10ByOrderByFechaInicioDesc() {
        List<Proyecto> proyectos = proyectoRepository.findTopN(10);
        for (Proyecto proyecto : proyectos) {
            enriquecerProyecto(proyecto);
        }
        return proyectos;
    }

    public List<Proyecto> findTopNByEstado(String estado, int limit) {
        List<Proyecto> proyectos = proyectoRepository.findTopNByEstado(estado, limit);
        for (Proyecto proyecto : proyectos) {
            enriquecerProyecto(proyecto);
        }
        return proyectos;
    }

    public Page<Proyecto> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable) {
        Page<Proyecto> proyectos = proyectoRepository.findByFechaInicioBetween(fechaInicio, fechaFin, pageable);
        for (Proyecto proyecto : proyectos.getContent()) {
            enriquecerProyecto(proyecto);
        }
        return proyectos;
    }

    public long count() {
        return proyectoRepository.count();
    }

    public long countByEstado(String estado) {
        return proyectoRepository.countByEstado(estado);
    }

    public long countByEstadoNotIn(List<String> estados) {
        List<Integer> idsEstados = estados.stream()
                .map(e -> estadoProyectoRepository.findByNombreEstado(e)
                        .map(EstadoProyecto::getIdEstado)
                        .orElse(null))
                .filter(id -> id != null)
                .toList();
        return proyectoRepository.countByEstadoNotIn(idsEstados);
    }

    public long countByFechaInicioMonthAndYear(int month, int year) {
        return proyectoRepository.countByFechaInicioMonthAndYear(month, year);
    }

    public BigDecimal sumMontoTotal() {
        return proyectoRepository.sumMontoTotal();
    }

    public BigDecimal sumMontoTotalActivos() {
        return proyectoRepository.sumMontoTotalActivos();
    }

    public BigDecimal sumMontoTotalByCliente(Integer idCliente) {
        if (idCliente == null) {
            return BigDecimal.ZERO;
        }
        return proyectoRepository.sumMontoTotalByCliente(idCliente);
    }

    public List<Object[]> getEstadisticasDashboard() {
        return proyectoRepository.getEstadisticasDashboard();
    }

    public List<Object[]> getEstadisticasDashboardByCliente(Integer idCliente) {
        if (idCliente == null) {
            return List.of();
        }
        return proyectoRepository.getEstadisticasDashboardByCliente(idCliente);
    }

    public List<Object[]> findProyectosDashboard(int limit) {
        return proyectoRepository.findProyectosDashboard(limit);
    }

    public List<Object[]> getEstadisticasPorMes(int year) {
        return proyectoRepository.getEstadisticasPorMes(year);
    }

    public List<Object[]> findProyectosVencidosDashboard(int limit) {
        return proyectoRepository.findProyectosVencidosDashboard(LocalDate.now(), limit);
    }

    public boolean existsByCotizacion(Integer idCotizacion) {
        return proyectoRepository.existsByIdCotizacion(idCotizacion);
    }

    public boolean canAnular(Integer idProyecto) {
        return proyectoRepository.canAnular(idProyecto);
    }

    public boolean canEntregar(Integer idProyecto) {
        return proyectoRepository.canEntregar(idProyecto);
    }

    public boolean canRecibirPagos(Integer idProyecto) {
        return proyectoRepository.canRecibirPagos(idProyecto);
    }

    public boolean hasProyectosActivos(Integer idCliente) {
        if (idCliente == null) {
            return false;
        }
        return proyectoRepository.hasProyectosActivos(idCliente);
    }

    public List<Proyecto> findByClienteAndEstado(Integer idCliente, String estado) {
        if (idCliente == null) {
            return List.of();
        }
        List<Proyecto> proyectos = proyectoRepository.findByClienteAndEstado(idCliente, estado);
        for (Proyecto proyecto : proyectos) {
            enriquecerProyecto(proyecto);
        }
        return proyectos;
    }

    public Page<Proyecto> buscar(String search, Pageable pageable) {
        return proyectoRepository.search(search, pageable);
    }

    private void enriquecerProyecto(Proyecto proyecto) {
        if (proyecto == null) return;

        if (proyecto.getEstado() == null && proyecto.getIdEstadoActual() != null) {
            estadoProyectoRepository.findById(proyecto.getIdEstadoActual()).ifPresent(estado -> {
                proyecto.setEstado(estado.getNombreEstado());
                proyecto.setNombreEstado(estado.getNombreEstado());
                proyecto.setColorEstado(estado.getColor());
                proyecto.setIconoEstado(estado.getIcono());
            });
        }

        if (proyecto.getNombreCliente() == null && proyecto.getIdCliente() != null) {
            clienteRepository.findById(proyecto.getIdCliente()).ifPresent(cliente -> {
                proyecto.setNombreCliente(cliente.getNombreRazonSocial());
                proyecto.setRucCliente(cliente.getRuc());
                proyecto.setEmailCliente(cliente.getEmail());
                proyecto.setTelefonoCliente(cliente.getTelefono());
            });
        }

        if (proyecto.getTotalPagado() == null) {
            BigDecimal totalPagado = pagoRepository.sumMontoByProyecto(proyecto.getIdProyecto());
            proyecto.setTotalPagado(totalPagado != null ? totalPagado : BigDecimal.ZERO);
        }

        BigDecimal saldo = proyecto.getMontoTotal().subtract(proyecto.getTotalPagado());
        if (saldo.compareTo(BigDecimal.ZERO) < 0) {
            saldo = BigDecimal.ZERO;
        }
        proyecto.setSaldoPendiente(saldo);

        if (proyecto.getMontoTotal().compareTo(BigDecimal.ZERO) > 0) {
            int porcentaje = proyecto.getTotalPagado().multiply(new BigDecimal("100"))
                    .divide(proyecto.getMontoTotal(), 0, RoundingMode.HALF_UP).intValue();
            proyecto.setPorcentajePagado(Math.min(porcentaje, 100));
        } else {
            proyecto.setPorcentajePagado(0);
        }

        if (proyecto.getFechaVencimiento() != null && !proyecto.esEntregado() && !proyecto.esAnulado()) {
            proyecto.setEstaVencido(LocalDate.now().isAfter(proyecto.getFechaVencimiento()));
        } else {
            proyecto.setEstaVencido(false);
        }

        if (proyecto.getFechaInicio() != null) {
            LocalDate hoy = LocalDate.now();
            if (proyecto.getFechaInicio().isAfter(hoy)) {
                proyecto.setDiasTranscurridos(0);
            } else {
                proyecto.setDiasTranscurridos((int) java.time.temporal.ChronoUnit.DAYS.between(
                        proyecto.getFechaInicio(), hoy));
            }
        } else {
            proyecto.setDiasTranscurridos(0);
        }

        if (proyecto.getFechaFinEstimada() != null) {
            LocalDate hoy = LocalDate.now();
            if (proyecto.getFechaFinEstimada().isBefore(hoy)) {
                proyecto.setDiasRestantes(0);
            } else {
                proyecto.setDiasRestantes((int) java.time.temporal.ChronoUnit.DAYS.between(
                        hoy, proyecto.getFechaFinEstimada()));
            }
        } else {
            proyecto.setDiasRestantes(0);
        }
    }
}