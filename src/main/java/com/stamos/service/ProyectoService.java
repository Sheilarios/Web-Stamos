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
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de proyectos.
 * 
 * Reglas de negocio (RPY01 a RPY07):
 * - Un proyecto se crea únicamente a partir de una cotización aprobada (controlado en controller).
 * - Estados obligatorios: COTIZADO, APROBADO, EN_DISEÑO, EN_PRODUCCION, INSTALACION_MONTAJE, ENTREGADO, ANULADO.
 * - Solo diseñador o admin cambian estado (seguridad a nivel controller).
 * - Registro de historial de cambios (fecha, hora, usuario).
 * - No retroceso desde ENTREGADO o ANULADO.
 * - Si incluye alquiler de equipos, registrar fechas (delega en InventarioService).
 * - Impide solapamiento de equipos (vía InventarioService).
 */
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

    // ==================== CONSULTAS BÁSICAS ====================

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

    // ==================== FLUJO DE ESTADOS (RPY02, RPY05) ====================

    /**
     * Devuelve los estados permitidos a partir de un estado actual.
     *
     * @param currentState Estado actual del proyecto
     * @return Lista de estados siguientes permitidos
     */
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
        }; // No se puede cambiar
    }

    /**
     * Guarda un registro de historial de cambio de estado (RPY04).
     *
     * @param historial Entidad HistorialEstadoProyecto
     */
    public void saveHistorial(HistorialEstadoProyecto historial) {
        historial.setFechaHora(LocalDateTime.now());
        historialEstadoRepository.save(historial);
    }

    /**
     * Obtiene el historial de cambios de estado de un proyecto.
     *
     * @param proyectoId ID del proyecto
     * @return Lista de registros ordenados por fecha descendente
     */
    public List<HistorialEstadoProyecto> getHistorialEstados(Long proyectoId) {
        return historialEstadoRepository.findByProyectoIdOrderByFechaHoraDesc(proyectoId);
    }

    // ==================== ARCHIVOS (RENDERS, PLANOS, BOCETOS) ====================

    /**
     * Guarda un archivo asociado a un proyecto (RF-28).
     *
     * @param proyecto Proyecto
     * @param file     Archivo a subir
     * @param tipo     Tipo de archivo (RENDER, PLANO, BOCETO)
     * @throws IOException Si falla la lectura del archivo
     */
    public void guardarArchivo(Proyecto proyecto, MultipartFile file, String tipo) throws IOException {
        ArchivoProyecto archivo = new ArchivoProyecto();
        archivo.setProyecto(proyecto);
        archivo.setNombreArchivo(file.getOriginalFilename());
        archivo.setTipo(tipo);
        archivo.setContenido(file.getBytes());
        archivo.setFechaSubida(LocalDateTime.now());
        archivoProyectoRepository.save(archivo);
    }

    /**
     * Obtiene la lista de archivos de un proyecto.
     *
     * @param proyectoId ID del proyecto
     * @return Lista de archivos
     */
    public List<ArchivoProyecto> getArchivosByProyecto(Long proyectoId) {
        return archivoProyectoRepository.findByProyectoIdOrderByFechaSubidaDesc(proyectoId);
    }

    // ==================== ALQUILER DE EQUIPOS (RPY06, RPY07) ====================

    /**
     * Agrega una reserva de equipo (alquiler) a un proyecto.
     * Valida disponibilidad mediante InventarioService.
     *
     * @param proyecto    Proyecto
     * @param equipoId    ID del equipo
     * @param fechaInicio Fecha inicio
     * @param fechaFin    Fecha fin
     * @param cantidad    Cantidad
     * @throws IllegalStateException si no hay disponibilidad
     */
    public void agregarReservaEquipo(Proyecto proyecto, Long equipoId, LocalDate fechaInicio,
                                     LocalDate fechaFin, int cantidad) {
        // Verificar disponibilidad (RPY07)
        boolean disponible = inventarioService.verificarDisponibilidad(equipoId, fechaInicio, fechaFin, cantidad, null);
        if (!disponible) {
            throw new IllegalStateException("El equipo no está disponible en las fechas solicitadas.");
        }
        // Crear reserva asociada al proyecto
        ReservaEquipo reserva = new ReservaEquipo();
        reserva.setProyecto(proyecto);
        reserva.setEquipo(inventarioService.findEquipoById(equipoId));
        reserva.setFechaInicio(fechaInicio);
        reserva.setFechaFin(fechaFin);
        reserva.setCantidad(cantidad);
        inventarioService.saveReserva(reserva);
    }

    // ==================== REPORTE DE PROYECTOS ATRASADOS (RRE02) ====================

    /**
     * Obtiene proyectos atrasados: fecha de entrega estimada < fecha actual y estado no ENTREGADO.
     *
     * @return Lista de proyectos atrasados
     */
    public List<Proyecto> findAtrasados() {
        LocalDate today = LocalDate.now();
        List<String> estadosNoFinales = List.of("COTIZADO", "APROBADO", "EN_DISEÑO", "EN_PRODUCCION", "INSTALACION_MONTAJE");
        return proyectoRepository.findByEstadoInAndFechaEntregaEstimadaBefore(estadosNoFinales, today);
    }

   public boolean existsByClienteId(Long clienteId) {
    return proyectoRepository.existsByClienteId(clienteId);
}

public List<Proyecto> findByEstados(List<String> estados) {
    return proyectoRepository.findByEstadoIn(estados);
}
}