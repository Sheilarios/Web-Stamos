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
import java.util.Arrays;
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
    private DocumentoProyectoRepository documentoRepository;

    @Autowired
    private ConfiguracionSistemaService configuracionService;

    @Transactional
    public Proyecto crearDesdeCotizacion(Integer idCotizacion, Integer idUsuarioCreacion) {
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
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        EstadoProyecto estadoActual = estadoProyectoRepository.findById(proyecto.getIdEstadoActual())
                .orElseThrow(() -> new RuntimeException("Estado actual no encontrado"));

        EstadoProyecto estadoNuevo = estadoProyectoRepository.findById(idEstadoNuevo)
                .orElseThrow(() -> new RuntimeException("Estado nuevo no encontrado"));

        if (estadoActual.esEstadoTerminal()) {
            throw new RuntimeException("No se puede cambiar el estado de un proyecto " +
                    estadoActual.getNombreEstado().toLowerCase());
        }

        if (estadoActual.getIdEstado().equals(idEstadoNuevo)) {
            throw new RuntimeException("El proyecto ya se encuentra en ese estado");
        }

        if (estadoNuevo.getOrdenFlujo() <= estadoActual.getOrdenFlujo() && !estadoNuevo.esAnulado()) {
            throw new RuntimeException("No se puede retroceder en el flujo de estados");
        }

        Usuario usuario = usuarioRepository.findById(idUsuarioResponsable)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String rol = usuario.getNombreRol();

        if (estadoNuevo.esAnulado() && !"Administrador".equals(rol)) {
            throw new RuntimeException("Solo el administrador puede anular un proyecto");
        }

        if (estadoNuevo.esEntregado() && !"Administrador".equals(rol) && !"Soporte Técnico".equals(rol)) {
            throw new RuntimeException("Solo el administrador o soporte técnico pueden entregar un proyecto");
        }

        if ("Vendedor".equals(rol)) {
            if (!(estadoActual.esAprobado() && estadoNuevo.esEnDiseno())) {
                throw new RuntimeException("El vendedor solo puede iniciar un proyecto (Aprobado -> En Diseño)");
            }
        }

        if ("Diseñador".equals(rol)) {
            if (!(estadoActual.esEnDiseno() && estadoNuevo.esEnProduccion())) {
                throw new RuntimeException("El diseñador solo puede mover entre En Diseño y En Producción");
            }
        }

        if ("Producción".equals(rol)) {
            if (!(estadoActual.esEnProduccion() && estadoNuevo.esInstalacionMontaje())) {
                throw new RuntimeException("Producción solo puede mover de En Producción a Instalación/Montaje");
            }
        }

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
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        EstadoProyecto estadoActual = estadoProyectoRepository.findById(proyecto.getIdEstadoActual())
                .orElseThrow(() -> new RuntimeException("Estado actual no encontrado"));

        if (estadoActual.esEstadoTerminal()) {
            throw new RuntimeException("El proyecto está en estado terminal, no puede avanzar");
        }

        EstadoProyecto estadoSiguiente = estadoProyectoRepository.findSiguienteEstado(estadoActual.getOrdenFlujo())
                .orElseThrow(() -> new RuntimeException("No hay un estado siguiente disponible"));

        return cambiarEstado(idProyecto, estadoSiguiente.getIdEstado(), idUsuarioResponsable, comentario);
    }

    public Proyecto findById(Integer idProyecto) {
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

    public List<Proyecto> findByCliente(Integer idCliente) {
        List<Proyecto> proyectos = proyectoRepository.findByIdClienteOrderByFechaInicioDesc(idCliente);
        for (Proyecto proyecto : proyectos) {
            enriquecerProyecto(proyecto);
        }
        return proyectos;
    }

    public Page<Proyecto> findByCliente(Integer idCliente, Pageable pageable) {
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

    public List<Proyecto> findProyectosConPagosVencidosByCliente(Integer idCliente) {
        List<Proyecto> proyectos = proyectoRepository.findProyectosConPagosVencidosByCliente(idCliente, LocalDate.now());
        for (Proyecto proyecto : proyectos) {
            enriquecerProyecto(proyecto);
        }
        return proyectos;
    }

    public Page<Proyecto> search(String searchText, Pageable pageable) {
        Page<Proyecto> proyectos = proyectoRepository.search(searchText, pageable);
        for (Proyecto proyecto : proyectos.getContent()) {
            enriquecerProyecto(proyecto);
        }
        return proyectos;
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
        return proyectoRepository.sumMontoTotalByCliente(idCliente);
    }

    public List<Object[]> getEstadisticasDashboard() {
        return proyectoRepository.getEstadisticasDashboard();
    }

    public List<Object[]> getEstadisticasDashboardByCliente(Integer idCliente) {
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
        return proyectoRepository.hasProyectosActivos(idCliente);
    }

    public List<Proyecto> findByClienteAndEstado(Integer idCliente, String estado) {
        List<Proyecto> proyectos = proyectoRepository.findByClienteAndEstado(idCliente, estado);
        for (Proyecto proyecto : proyectos) {
            enriquecerProyecto(proyecto);
        }
        return proyectos;
    }

    public String getEstadoLegible(String estado) {
        if (estado == null) return "Sin estado";
        return switch (estado) {
            case "Aprobado" -> "Aprobado";
            case "En Diseño" -> "En Diseño";
            case "En Producción" -> "En Producción";
            case "Instalación/Montaje" -> "Instalación/Montaje";
            case "Entregado" -> "Entregado";
            case "Anulado" -> "Anulado";
            default -> estado;
        };
    }

    public String getEstadoBadgeClass(String estado) {
        if (estado == null) return "status-pendiente";
        return switch (estado) {
            case "Aprobado" -> "status-aprobado";
            case "En Diseño" -> "status-diseno";
            case "En Producción" -> "status-produccion";
            case "Instalación/Montaje" -> "status-instalacion";
            case "Entregado" -> "status-entregado";
            case "Anulado" -> "status-anulado";
            default -> "status-pendiente";
        };
    }

    public String getEstadoIcono(String estado) {
        if (estado == null) return "ti ti-circle";
        return switch (estado) {
            case "Aprobado" -> "ti ti-check-circle";
            case "En Diseño" -> "ti ti-pencil";
            case "En Producción" -> "ti ti-tools";
            case "Instalación/Montaje" -> "ti ti-truck";
            case "Entregado" -> "ti ti-check";
            case "Anulado" -> "ti ti-x-circle";
            default -> "ti ti-circle";
        };
    }

    public String getEstadoColor(String estado) {
        if (estado == null) return "#6B7280";
        return switch (estado) {
            case "Aprobado" -> "#1A56DB";
            case "En Diseño" -> "#F59E0B";
            case "En Producción" -> "#3B82F6";
            case "Instalación/Montaje" -> "#8B5CF6";
            case "Entregado" -> "#10B981";
            case "Anulado" -> "#EF4444";
            default -> "#6B7280";
        };
    }

    public int getProgresoByEstado(String estado) {
        if (estado == null) return 0;
        return switch (estado) {
            case "Aprobado" -> 10;
            case "En Diseño" -> 30;
            case "En Producción" -> 60;
            case "Instalación/Montaje" -> 85;
            case "Entregado" -> 100;
            case "Anulado" -> 0;
            default -> 0;
        };
    }

    public String getProgresoColor(int progreso) {
        if (progreso >= 100) return "#10B981";
        if (progreso >= 60) return "#3B82F6";
        if (progreso >= 30) return "#F59E0B";
        return "#EF4444";
    }

    public String formatearMoneda(BigDecimal monto) {
        if (monto == null) return "S/ 0.00";
        return "S/ " + monto.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public String formatearFecha(LocalDate fecha) {
        if (fecha == null) return "-";
        return fecha.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String formatearFechaHora(LocalDateTime fecha) {
        if (fecha == null) return "-";
        return fecha.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getCodigoProyecto(Integer idProyecto) {
        if (idProyecto == null) return "PRO-00000";
        return "PRO-" + String.format("%05d", idProyecto);
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

    public Page<Proyecto> buscar(String search, Pageable pageable) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}