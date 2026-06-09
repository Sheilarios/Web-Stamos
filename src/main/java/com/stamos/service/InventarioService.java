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

@Service
@Transactional
public class InventarioService {
    @Autowired
    private CatalogoEquipoRepository catalogoEquipoRepository;
    @Autowired
    
    private ReservaEquipoRepository reservaEquipoRepository;
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

    public boolean hasReservasAsociadas(Long equipoId) {
        return reservaEquipoRepository.existsByEquipoId(equipoId);
    }

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

    private int getCantidadReservadaEnRango(Long equipoId, LocalDate fechaInicio, LocalDate fechaFin, Long reservaIdExcluir) {
        List<ReservaEquipo> reservas = reservaEquipoRepository.findByEquipoIdAndPeriodoOverlap(equipoId, fechaInicio, fechaFin);
        int total = 0;
        for (ReservaEquipo r : reservas) {
            if (reservaIdExcluir != null && r.getId().equals(reservaIdExcluir)) continue;
            total += r.getCantidad();
        }
        return total;
    }

    public int getCantidadDisponible(Long equipoId, LocalDate fechaInicio, LocalDate fechaFin) {
        CatalogoEquipo equipo = findEquipoById(equipoId);
        if (equipo == null) return 0;
        int reservado = getCantidadReservadaEnRango(equipoId, fechaInicio, fechaFin, null);
        return equipo.getCantidadTotal() - reservado;
    }

    public Map<CatalogoEquipo, Integer> getDisponibilidadPorRango(LocalDate fechaInicio, LocalDate fechaFin) {
        List<CatalogoEquipo> equipos = findAllEquipos();
        Map<CatalogoEquipo, Integer> disponibilidad = new LinkedHashMap<>();
        for (CatalogoEquipo equipo : equipos) {
            int disponible = getCantidadDisponible(equipo.getId(), fechaInicio, fechaFin);
            disponibilidad.put(equipo, disponible);
        }
        return disponibilidad;
    }

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
            evento.put("end", r.getFechaFin().plusDays(1).toString()); 
            evento.put("backgroundColor", getColorParaEquipo(r.getEquipo().getId()));
            return evento;
        }).collect(Collectors.toList());
    }

    private String getColorParaEquipo(Long equipoId) {
        // Colores predefinidos cíclicos (pueden ser configurados)
        String[] colores = {"#3788d8", "#d9534f", "#5cb85c", "#f0ad4e", "#5bc0de", "#9467bd"};
        int index = (int) (equipoId % colores.length);
        return colores[index];
    }

    public ReservaEquipo crearReservaParaProyecto(Long proyectoId, Long equipoId, LocalDate fechaInicio,
                                                  LocalDate fechaFin, int cantidad) {
        if (!verificarDisponibilidad(equipoId, fechaInicio, fechaFin, cantidad, null)) {
            throw new IllegalStateException("No hay suficiente disponibilidad del equipo para las fechas solicitadas.");
        }
        ReservaEquipo reserva = new ReservaEquipo();
        reserva.setEquipo(findEquipoById(equipoId));
        return saveReserva(reserva);
    }
}