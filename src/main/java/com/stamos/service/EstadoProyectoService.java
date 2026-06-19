package com.stamos.service;

import com.stamos.model.EstadoProyecto;
import com.stamos.model.Proyecto;
import com.stamos.repository.EstadoProyectoRepository;
import com.stamos.repository.ProyectoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EstadoProyectoService {

    @Autowired
    private EstadoProyectoRepository estadoProyectoRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    public static final String ESTADO_APROBADO = "Aprobado";
    public static final String ESTADO_EN_DISENO = "En Diseño";
    public static final String ESTADO_EN_PRODUCCION = "En Producción";
    public static final String ESTADO_INSTALACION_MONTAJE = "Instalación/Montaje";
    public static final String ESTADO_ENTREGADO = "Entregado";
    public static final String ESTADO_ANULADO = "Anulado";

    @Transactional
    public EstadoProyecto crear(String nombreEstado, Integer ordenFlujo, String descripcion) {
        if (estadoProyectoRepository.existsByNombreEstado(nombreEstado)) {
            throw new RuntimeException("Ya existe un estado con el nombre: " + nombreEstado);
        }
        if (estadoProyectoRepository.existsByOrdenFlujo(ordenFlujo)) {
            throw new RuntimeException("Ya existe un estado con el orden: " + ordenFlujo);
        }

        EstadoProyecto estado = new EstadoProyecto();
        estado.setNombreEstado(nombreEstado);
        estado.setOrdenFlujo(ordenFlujo);
        estado.setDescripcion(descripcion);
        estado.setFechaCreacion(LocalDateTime.now());
        estado.setFechaActualizacion(LocalDateTime.now());

        return estadoProyectoRepository.save(estado);
    }

    @Transactional
    public EstadoProyecto actualizar(Integer idEstado, String nombreEstado, Integer ordenFlujo, String descripcion) {
        EstadoProyecto existente = estadoProyectoRepository.findById(idEstado)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado con ID: " + idEstado));

        if (!existente.getNombreEstado().equals(nombreEstado) && 
            estadoProyectoRepository.existsByNombreEstado(nombreEstado)) {
            throw new RuntimeException("Ya existe otro estado con el nombre: " + nombreEstado);
        }

        if (!existente.getOrdenFlujo().equals(ordenFlujo) && 
            estadoProyectoRepository.existsByOrdenFlujo(ordenFlujo)) {
            throw new RuntimeException("Ya existe otro estado con el orden: " + ordenFlujo);
        }

        existente.setNombreEstado(nombreEstado);
        existente.setOrdenFlujo(ordenFlujo);
        existente.setDescripcion(descripcion);
        existente.setFechaActualizacion(LocalDateTime.now());

        return estadoProyectoRepository.save(existente);
    }

    @Transactional
    public void eliminar(Integer idEstado) {
        EstadoProyecto estado = estadoProyectoRepository.findById(idEstado)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado con ID: " + idEstado));

        long cantidadProyectos = estadoProyectoRepository.countProyectosByEstado(idEstado);
        if (cantidadProyectos > 0) {
            throw new RuntimeException("No se puede eliminar el estado porque tiene " + 
                                      cantidadProyectos + " proyectos asociados");
        }

        estadoProyectoRepository.delete(estado);
    }

    public EstadoProyecto findById(Integer idEstado) {
        return estadoProyectoRepository.findById(idEstado).orElse(null);
    }

    public EstadoProyecto findByNombre(String nombreEstado) {
        return estadoProyectoRepository.findByNombreEstado(nombreEstado).orElse(null);
    }

    public EstadoProyecto findByOrden(Integer ordenFlujo) {
        return estadoProyectoRepository.findByOrdenFlujo(ordenFlujo).orElse(null);
    }

    public List<EstadoProyecto> findAll() {
        return estadoProyectoRepository.findAllOrderByOrdenFlujo();
    }

    public Page<EstadoProyecto> findAll(Pageable pageable) {
        return estadoProyectoRepository.findAllOrderByOrdenFlujo(pageable);
    }

    public List<EstadoProyecto> findAllDesc() {
        return estadoProyectoRepository.findAllOrderByOrdenFlujoDesc();
    }

    public List<EstadoProyecto> findEstadosActivos() {
        return estadoProyectoRepository.findEstadosActivos();
    }

    public Page<EstadoProyecto> findEstadosActivos(Pageable pageable) {
        return estadoProyectoRepository.findEstadosActivos(pageable);
    }

    public List<EstadoProyecto> findEstadosTerminales() {
        return estadoProyectoRepository.findEstadosTerminales();
    }

    public EstadoProyecto findEstadoInicial() {
        return estadoProyectoRepository.findEstadoInicial().orElse(null);
    }

    public EstadoProyecto findEstadoFinal() {
        return estadoProyectoRepository.findEstadoFinal().orElse(null);
    }

    public EstadoProyecto findEstadoAnulado() {
        return estadoProyectoRepository.findEstadoAnulado().orElse(null);
    }

    public EstadoProyecto findEstadoEntregado() {
        return estadoProyectoRepository.findEstadoEntregado().orElse(null);
    }

    public EstadoProyecto findEstadoAprobado() {
        return estadoProyectoRepository.findEstadoAprobado().orElse(null);
    }

    public EstadoProyecto findSiguienteEstado(Integer ordenFlujoActual) {
        return estadoProyectoRepository.findSiguienteEstado(ordenFlujoActual).orElse(null);
    }

    public EstadoProyecto findEstadoAnterior(Integer ordenFlujoActual) {
        return estadoProyectoRepository.findEstadoAnterior(ordenFlujoActual).orElse(null);
    }

    public List<EstadoProyecto> findEstadosDisponibles(Integer idEstadoActual) {
        return estadoProyectoRepository.findEstadosDisponibles(idEstadoActual);
    }

    public List<EstadoProyecto> findEstadosDisponiblesByRol(Integer idEstadoActual, String rolNombre) {
        return estadoProyectoRepository.findEstadosDisponiblesByRol(idEstadoActual, rolNombre);
    }

    public List<EstadoProyecto> findEstadosPosibles(Integer idEstadoActual) {
        return estadoProyectoRepository.findEstadosPosibles(idEstadoActual);
    }

    public boolean canTransition(Integer idEstadoActual, Integer idEstadoSiguiente) {
        return estadoProyectoRepository.canTransition(idEstadoActual, idEstadoSiguiente);
    }

    public boolean canRollback(Integer idEstadoActual, Integer idEstadoAnterior) {
        return estadoProyectoRepository.canRollback(idEstadoActual, idEstadoAnterior);
    }

    public List<EstadoProyecto> search(String searchText) {
        return estadoProyectoRepository.search(searchText);
    }

    public Page<EstadoProyecto> search(String searchText, Pageable pageable) {
        return estadoProyectoRepository.search(searchText, pageable);
    }

    public long count() {
        return estadoProyectoRepository.count();
    }

    public long countEstadosActivos() {
        return estadoProyectoRepository.countEstadosActivos();
    }

    public long countEstadosTerminales() {
        return estadoProyectoRepository.countEstadosTerminales();
    }

    public long countProyectosByEstado(Integer idEstado) {
        return estadoProyectoRepository.countProyectosByEstado(idEstado);
    }

    public List<Object[]> countProyectosByEstadoResumen() {
        return estadoProyectoRepository.countProyectosByEstadoResumen();
    }

    public List<Object[]> findEstadosWithProjectStats() {
        return estadoProyectoRepository.findEstadosWithProjectStats();
    }

    public Double avgDiasEnEstado(Integer idEstado) {
        return estadoProyectoRepository.avgDiasEnEstado(idEstado);
    }

    public boolean existsByNombre(String nombreEstado) {
        return estadoProyectoRepository.existsByNombreEstado(nombreEstado);
    }

    public boolean existsByOrden(Integer ordenFlujo) {
        return estadoProyectoRepository.existsByOrdenFlujo(ordenFlujo);
    }

    public boolean isTerminal(Integer idEstado) {
        return estadoProyectoRepository.isTerminal(idEstado);
    }

    public boolean isActivo(Integer idEstado) {
        return estadoProyectoRepository.isActivo(idEstado);
    }

    public boolean isInicial(Integer idEstado) {
        return estadoProyectoRepository.isInicial(idEstado);
    }

    public boolean isFinal(Integer idEstado) {
        return estadoProyectoRepository.isFinal(idEstado);
    }

    public boolean hasProyectos(Integer idEstado) {
        return estadoProyectoRepository.hasProyectos(idEstado);
    }

    @Transactional
    public void updateDescripcion(Integer idEstado, String nuevaDescripcion) {
        estadoProyectoRepository.updateDescripcion(idEstado, nuevaDescripcion);
    }

    @Transactional
    public void updateOrdenFlujo(Integer idEstado, Integer nuevoOrden) {
        if (estadoProyectoRepository.existsByOrdenFlujo(nuevoOrden)) {
            throw new RuntimeException("Ya existe un estado con el orden: " + nuevoOrden);
        }
        estadoProyectoRepository.updateOrdenFlujo(idEstado, nuevoOrden);
    }

    @Transactional
    public void updateNombreEstado(Integer idEstado, String nuevoNombre) {
        if (estadoProyectoRepository.existsByNombreEstado(nuevoNombre)) {
            throw new RuntimeException("Ya existe un estado con el nombre: " + nuevoNombre);
        }
        estadoProyectoRepository.updateNombreEstado(idEstado, nuevoNombre);
    }

    @Transactional
    public int deleteUnused() {
        return estadoProyectoRepository.deleteUnused();
    }

    @Transactional
    public void deleteByNombre(String nombreEstado) {
        if (estadoProyectoRepository.countProyectosByEstado(
            estadoProyectoRepository.findByNombreEstado(nombreEstado).get().getIdEstado()) > 0) {
            throw new RuntimeException("No se puede eliminar el estado porque tiene proyectos asociados");
        }
        estadoProyectoRepository.deleteByNombreEstado(nombreEstado);
    }

    public String getEstadoBadgeClass(String nombreEstado) {
        if (nombreEstado == null) return "status-pendiente";
        switch (nombreEstado) {
            case ESTADO_APROBADO: return "status-aprobado";
            case ESTADO_EN_DISENO: return "status-diseno";
            case ESTADO_EN_PRODUCCION: return "status-produccion";
            case ESTADO_INSTALACION_MONTAJE: return "status-instalacion";
            case ESTADO_ENTREGADO: return "status-entregado";
            case ESTADO_ANULADO: return "status-anulado";
            default: return "status-pendiente";
        }
    }

    public String getEstadoColor(String nombreEstado) {
        if (nombreEstado == null) return "#6B7280";
        switch (nombreEstado) {
            case ESTADO_APROBADO: return "#1A56DB";
            case ESTADO_EN_DISENO: return "#F59E0B";
            case ESTADO_EN_PRODUCCION: return "#3B82F6";
            case ESTADO_INSTALACION_MONTAJE: return "#8B5CF6";
            case ESTADO_ENTREGADO: return "#10B981";
            case ESTADO_ANULADO: return "#EF4444";
            default: return "#6B7280";
        }
    }

    public String getEstadoIcono(String nombreEstado) {
        if (nombreEstado == null) return "ti ti-circle";
        switch (nombreEstado) {
            case ESTADO_APROBADO: return "ti ti-check-circle";
            case ESTADO_EN_DISENO: return "ti ti-pencil";
            case ESTADO_EN_PRODUCCION: return "ti ti-tools";
            case ESTADO_INSTALACION_MONTAJE: return "ti ti-truck";
            case ESTADO_ENTREGADO: return "ti ti-check";
            case ESTADO_ANULADO: return "ti ti-x-circle";
            default: return "ti ti-circle";
        }
    }

    public int getProgresoByEstado(String nombreEstado) {
        if (nombreEstado == null) return 0;
        switch (nombreEstado) {
            case ESTADO_APROBADO: return 10;
            case ESTADO_EN_DISENO: return 30;
            case ESTADO_EN_PRODUCCION: return 60;
            case ESTADO_INSTALACION_MONTAJE: return 85;
            case ESTADO_ENTREGADO: return 100;
            case ESTADO_ANULADO: return 0;
            default: return 0;
        }
    }

    public String getProgresoColor(int progreso) {
        if (progreso >= 100) return "#10B981";
        if (progreso >= 60) return "#3B82F6";
        if (progreso >= 30) return "#F59E0B";
        return "#EF4444";
    }

    public String getEstadoLegible(String nombreEstado) {
        if (nombreEstado == null) return "Sin estado";
        return nombreEstado;
    }

    public boolean esEstadoActivo(String nombreEstado) {
        if (nombreEstado == null) return false;
        return !nombreEstado.equals(ESTADO_ENTREGADO) && !nombreEstado.equals(ESTADO_ANULADO);
    }

    public boolean esEstadoTerminal(String nombreEstado) {
        if (nombreEstado == null) return false;
        return nombreEstado.equals(ESTADO_ENTREGADO) || nombreEstado.equals(ESTADO_ANULADO);
    }
}