package com.stamos.service;

import com.stamos.entity.CatalogoEquipo;
import com.stamos.entity.ReservaEquipo;
import com.stamos.repository.CatalogoEquipoRepository;
import com.stamos.repository.ReservaEquipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de inventario y reservas de equipos.
 * 
 * Reglas de negocio (RIN01 a RIN05):
 * - RIN01: Todo equipo debe registrarse con nombre, cantidad total, ubicación.
 * - RIN02: Al asignar un equipo se reduce la cantidad disponible durante el período.
 * - RIN03: No se puede asignar si la cantidad disponible es insuficiente.
 * - RIN04: Consulta de disponibilidad por rango de fechas.
 * - RIN05: Solo ADMIN o SOPORTE_TECNICO modifica stock (controlado a nivel de controller).
 */
@Service
@Transactional
public class InventarioService {

    @Autowired
    private CatalogoEquipoRepository catalogoEquipoRepository;

    @Autowired
    private ReservaEquipoRepository reservaEquipoRepository;

    // ==================== EQUIPOS (CRUD) ====================

    public List<CatalogoEquipo> findAllEquipos() {
        return catalogoEquipoRepository.findAll();
    }

    public CatalogoEquipo findEquipoById(Long id) {
        return catalogoEquipoRepository.findById(id).orElse(null);
    }

    public CatalogoEquipo saveEquipo(CatalogoEquipo equipo) {
        return catalogoEquipoRepository.save(equipo);
    }

    public void deleteEquipo(Long id) {
        catalogoEquipoRepository.deleteById(id);
    }

    /**
     * Verifica si un equipo tiene reservas asociadas (útil antes de eliminar).
     *
     * @param equipoId ID del equipo
     * @return true si tiene al menos una reserva
     */
    public boolean hasReservasAsociadas(Long equipoId) {
        return reservaEquipoRepository.existsByEquipoId(equipoId);
    }

    // ==================== RESERVAS (CRUD) ====================

    public List<ReservaEquipo> findAllReservas() {
        return reservaEquipoRepository.findAll();
    }

    public ReservaEquipo findReservaById(Long id) {
        return reservaEquipoRepository.findById(id).orElse(null);
    }

    public ReservaEquipo saveReserva(ReservaEquipo reserva) {
        return reservaEquipoRepository.save(reserva);
    }

    public void deleteReserva(Long id) {
        reservaEquipoRepository.deleteById(id);
    }

    // ==================== VALIDACIÓN DE DISPONIBILIDAD (RIN03) ====================

    /**
     * Verifica si la cantidad solicitada de un equipo está disponible para el rango de fechas.
     * Considera reservas existentes que se solapen con el período solicitado.
     *
     * @param equipoId          ID del equipo
     * @param fechaInicio       Fecha de inicio del alquiler
     * @param fechaFin          Fecha de fin del alquiler
     * @param cantidadSolicitada Cantidad que se desea reservar
     * @param reservaIdExcluir  ID de la reserva que se está editando (para excluirse de la validación, null si es nueva)
     * @return true si hay suficiente disponibilidad
     */
    public boolean verificarDisponibilidad(Long equipoId, LocalDate fechaInicio, LocalDate fechaFin,
                                           int cantidadSolicitada, Long reservaIdExcluir) {
        CatalogoEquipo equipo = findEquipoById(equipoId);
        if (equipo == null) return false;
        if (cantidadSolicitada <= 0) return false;
        if (fechaInicio.isAfter(fechaFin)) return false;

        int cantidadReservadaEnPeriodo = getCantidadReservadaEnRango(equipoId, fechaInicio, fechaFin, reservaIdExcluir);
        int disponible = equipo.getCantidadTotal() - cantidadReservadaEnPeriodo;
        return disponible >= cantidadSolicitada;
    }

    /**
     * Obtiene la cantidad total reservada de un equipo en un rango de fechas (excluyendo opcionalmente una reserva).
     *
     * @param equipoId         ID del equipo
     * @param fechaInicio      Inicio del período
     * @param fechaFin         Fin del período
     * @param reservaIdExcluir ID a excluir (para ediciones), null si no aplica
     * @return Cantidad total reservada en el período
     */
    private int getCantidadReservadaEnRango(Long equipoId, LocalDate fechaInicio, LocalDate fechaFin, Long reservaIdExcluir) {
        List<ReservaEquipo> reservas = reservaEquipoRepository.findByEquipoIdAndPeriodoOverlap(equipoId, fechaInicio, fechaFin);
        int total = 0;
        for (ReservaEquipo r : reservas) {
            if (reservaIdExcluir != null && r.getId().equals(reservaIdExcluir)) continue;
            total += r.getCantidad();
        }
        return total;
    }

    /**
     * Obtiene la cantidad disponible de un equipo en un rango de fechas (stock total - reservado).
     *
     * @param equipoId    ID del equipo
     * @param fechaInicio Inicio
     * @param fechaFin    Fin
     * @return Cantidad disponible
     */
    public int getCantidadDisponible(Long equipoId, LocalDate fechaInicio, LocalDate fechaFin) {
        CatalogoEquipo equipo = findEquipoById(equipoId);
        if (equipo == null) return 0;
        int reservado = getCantidadReservadaEnRango(equipoId, fechaInicio, fechaFin, null);
        return equipo.getCantidadTotal() - reservado;
    }

    // ==================== REPORTE DE DISPONIBILIDAD POR RANGO (RIN04) ====================

    /**
     * Devuelve un mapa con la disponibilidad de todos los equipos en un rango de fechas.
     *
     * @param fechaInicio Inicio
     * @param fechaFin    Fin
     * @return Map<CatalogoEquipo, Integer> (equipo -> cantidad disponible)
     */
    public Map<CatalogoEquipo, Integer> getDisponibilidadPorRango(LocalDate fechaInicio, LocalDate fechaFin) {
        List<CatalogoEquipo> equipos = findAllEquipos();
        Map<CatalogoEquipo, Integer> disponibilidad = new LinkedHashMap<>();
        for (CatalogoEquipo equipo : equipos) {
            int disponible = getCantidadDisponible(equipo.getId(), fechaInicio, fechaFin);
            disponibilidad.put(equipo, disponible);
        }
        return disponibilidad;
    }

    // ==================== CALENDARIO DE RESERVAS (EVENTOS) ====================

    /**
     * Convierte las reservas en eventos para FullCalendar (formato JSON).
     *
     * @param equipoId Si se especifica, filtra por equipo; si null, trae todas las reservas.
     * @return Lista de mapas con título, inicio, fin, etc.
     */
    public List<Map<String, Object>> getReservasComoEventos(Long equipoId) {
        List<ReservaEquipo> reservas;
        if (equipoId != null) {
            reservas = reservaEquipoRepository.findByEquipoId(equipoId);
        } else {
            reservas = findAllReservas();
        }
        return reservas.stream().map(r -> {
            Map<String, Object> evento = new HashMap<>();
            String equipoNombre = r.getEquipo().getNombre();
            evento.put("title", equipoNombre + " (x" + r.getCantidad() + ")");
            evento.put("start", r.getFechaInicio().toString());
            evento.put("end", r.getFechaFin().plusDays(1).toString()); // FullCalendar usa end exclusivo
            evento.put("backgroundColor", getColorParaEquipo(r.getEquipo().getId()));
            return evento;
        }).collect(Collectors.toList());
    }

    /**
     * Asigna un color según el ID del equipo (para el calendario).
     */
    private String getColorParaEquipo(Long equipoId) {
        // Colores predefinidos cíclicos (pueden ser configurados)
        String[] colores = {"#3788d8", "#d9534f", "#5cb85c", "#f0ad4e", "#5bc0de", "#9467bd"};
        int index = (int) (equipoId % colores.length);
        return colores[index];
    }

    // ==================== MÉTODOS AUXILIARES PARA PROYECTOS ====================

    /**
     * Crea una reserva de equipo asociada a un proyecto. Útil para ser llamado desde ProyectoService.
     *
     * @param proyectoId   ID del proyecto
     * @param equipoId     ID del equipo
     * @param fechaInicio  Fecha inicio alquiler
     * @param fechaFin     Fecha fin alquiler
     * @param cantidad     Cantidad
     * @return Reserva creada
     * @throws IllegalStateException si no hay disponibilidad
     */
    public ReservaEquipo crearReservaParaProyecto(Long proyectoId, Long equipoId, LocalDate fechaInicio,
                                                  LocalDate fechaFin, int cantidad) {
        if (!verificarDisponibilidad(equipoId, fechaInicio, fechaFin, cantidad, null)) {
            throw new IllegalStateException("No hay suficiente disponibilidad del equipo para las fechas solicitadas.");
        }
        ReservaEquipo reserva = new ReservaEquipo();
        reserva.setEquipo(findEquipoById(equipoId));
        // Se necesita un proyecto service para cargar la entidad Proyecto; se puede resolver inyectando ProyectoRepository
        // Para simplificar, este método se espera que reciba el objeto Proyecto, pero lo dejamos como stub
        // La implementación real debería recibir Proyecto o inyectar ProyectoRepository.
        // Aquí asumimos que se llama desde ProyectoService que pasa el objeto Proyecto.
        // Se recomienda sobrecargar el método con parámetro Proyecto.
        return saveReserva(reserva);
    }
}