package com.stamos.service;
import com.stamos.entity.*;
import com.stamos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProyectoService {

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private HistorialEstadoProyectoRepository historialEstadoRepository;

    @Autowired
    private ArchivoProyectoRepository archivoProyectoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private AuditoriaService auditoriaService;




    public List<Proyecto> findAll() {
        return proyectoRepository.findAll();
    }
    public List<Proyecto> findByEstado(String estado) {
        return proyectoRepository.findByEstado(estado);
    }
    public List<Proyecto> findByClienteId(Long clienteId) {
        return proyectoRepository.findByClienteId(clienteId);
    }
    public List<Proyecto> findByEstadosNotIn(List<String> estados) {
        return proyectoRepository.findByEstadoNotIn(estados);
    }
    public List<Proyecto> findByEstados(List<String> estados) {
        return proyectoRepository.findByEstadoIn(estados);
    }
    public List<Proyecto> findByResponsableIdAndEstadosNotIn(Long responsableId, List<String> estados) {
        return proyectoRepository.findByResponsableIdAndEstadoNotIn(responsableId, estados);
    }
    public Proyecto findById(Long id) {
        return proyectoRepository.findById(id).orElse(null);
    }

    public Proyecto save(Proyecto proyecto) {
        return proyectoRepository.save(proyecto);
    }

    public boolean existsByCotizacionId(Long cotizacionId) {
        return proyectoRepository.existsByCotizacionId(cotizacionId);
    }

    public boolean existsByClienteId(Long clienteId) {
        return proyectoRepository.existsByClienteId(clienteId);
    }

    public long count() {
        return proyectoRepository.count();
    }

    public long countByEstadosNot(String... estados) {
        return proyectoRepository.countByEstadoNotIn(List.of(estados));
    }

    public long countByEstadosIn(List<String> estados) {
        return proyectoRepository.countByEstadoIn(estados);
    }

    public List<Proyecto> findTop5ByOrderByFechaCreacionDesc() {
        return proyectoRepository.findTop5ByOrderByFechaCreacionDesc();
    }

    public List<Proyecto> findTopNByOrderByFechaCreacionDesc(int limit) {
        return proyectoRepository.findTopNByOrderByFechaCreacionDesc(limit);
    }

    public List<String> getNextAllowedStates(String currentState) {
        return switch (currentState) {
            case "COTIZADO" -> List.of("APROBADO", "ANULADO");
            case "APROBADO" -> List.of("EN_DISEÑO", "ANULADO");
            case "EN_DISEÑO" -> List.of("EN_PRODUCCION", "ANULADO");
            case "EN_PRODUCCION" -> List.of("INSTALACION_MONTAJE", "ANULADO");
            case "INSTALACION_MONTAJE" -> List.of("ENTREGADO", "ANULADO");
            case "ENTREGADO" -> List.of();
            case "ANULADO" -> List.of();
            default -> List.of();
        };
    }

    public void cambiarEstado(Long proyectoId, String nuevoEstado, Long usuarioId) {
        Proyecto proyecto = findById(proyectoId);
        if (proyecto == null) {
            throw new IllegalArgumentException("Proyecto no encontrado");
        }

        String estadoActual = proyecto.getEstado();
        List<String> estadosPermitidos = getNextAllowedStates(estadoActual);

        if (!estadosPermitidos.contains(nuevoEstado)) {
            throw new IllegalStateException(
                String.format("No se puede cambiar de '%s' a '%s'", estadoActual, nuevoEstado)
            );
        }

        HistorialEstadoProyecto historial = new HistorialEstadoProyecto();
        historial.setProyecto(proyecto);
        historial.setEstadoAnterior(estadoActual);
        historial.setEstadoNuevo(nuevoEstado);
        historial.setUsuario(usuarioService.findById(usuarioId));
        historial.setFechaHora(LocalDateTime.now());
        
        saveHistorial(historial);
        
        proyecto.setEstado(nuevoEstado);
        if ("ENTREGADO".equals(nuevoEstado)) {
            proyecto.setFechaEntregaReal(LocalDate.now());
        }
        
        save(proyecto);
        
        if (auditoriaService != null) {
            auditoriaService.logCambioEstadoProyecto(proyectoId, estadoActual, nuevoEstado, usuarioId);
        }
    }

    public void saveHistorial(HistorialEstadoProyecto historial) {
        if (historial.getFechaHora() == null) {
            historial.setFechaHora(LocalDateTime.now());
        }
        historialEstadoRepository.save(historial);
    }

    public List<HistorialEstadoProyecto> getHistorialEstados(Long proyectoId) {
        return historialEstadoRepository.findByProyectoIdOrderByFechaHoraDesc(proyectoId);
    }

    public List<String> getTimeline(Proyecto proyecto) {
        List<HistorialEstadoProyecto> historial = getHistorialEstados(proyecto.getId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        return historial.stream()
            .map(h -> String.format("%s - %s → %s (por %s)",
                h.getFechaHora().format(formatter),
                h.getEstadoAnterior() != null ? h.getEstadoAnterior() : "INICIO",
                h.getEstadoNuevo(),
                h.getUsuario() != null ? h.getUsuario().getNombre() : "Sistema"))
            .collect(Collectors.toList());
    }

    public void guardarArchivo(Proyecto proyecto, MultipartFile file, String tipo) throws IOException {
        ArchivoProyecto archivo = new ArchivoProyecto();
        archivo.setProyecto(proyecto);
        archivo.setNombreArchivo(file.getOriginalFilename());
        archivo.setTipo(tipo);
        archivo.setContenido(file.getBytes());
        archivo.setFechaSubida(LocalDateTime.now());
        archivoProyectoRepository.save(archivo);
    }

    public List<ArchivoProyecto> getArchivosByProyecto(Long proyectoId) {
        return archivoProyectoRepository.findByProyectoIdOrderByFechaSubidaDesc(proyectoId);
    }

    public void eliminarArchivo(Long archivoId) {
        archivoProyectoRepository.deleteById(archivoId);
    }

    public void agregarReservaEquipo(Proyecto proyecto, Long equipoId, LocalDate fechaInicio,
                                     LocalDate fechaFin, int cantidad) {
        boolean disponible = inventarioService.verificarDisponibilidad(equipoId, fechaInicio, fechaFin, cantidad, null);
        if (!disponible) {
            throw new IllegalStateException("El equipo no está disponible en las fechas solicitadas.");
        }
        ReservaEquipo reserva = new ReservaEquipo();
        reserva.setProyecto(proyecto);
        reserva.setEquipo(inventarioService.findEquipoById(equipoId));
        reserva.setFechaInicio(fechaInicio);
        reserva.setFechaFin(fechaFin);
        reserva.setCantidad(cantidad);
        inventarioService.saveReserva(reserva);
    }

    public List<Proyecto> findAtrasados() {
        LocalDate today = LocalDate.now();
        List<String> estadosNoFinales = List.of("COTIZADO", "APROBADO", "EN_DISEÑO", "EN_PRODUCCION", "INSTALACION_MONTAJE");
        return proyectoRepository.findByEstadoInAndFechaEntregaEstimadaBefore(estadosNoFinales, today);
    }

    public List<Proyecto> findProximosAVencer(int dias) {
        LocalDate today = LocalDate.now();
        LocalDate limite = today.plusDays(dias);
        List<String> estadosNoFinales = List.of("COTIZADO", "APROBADO", "EN_DISEÑO", "EN_PRODUCCION", "INSTALACION_MONTAJE");
        return proyectoRepository.findByEstadoInAndFechaEntregaEstimadaBetween(estadosNoFinales, today, limite);
    }
}