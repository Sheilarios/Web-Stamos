package com.stamos.service;

import com.stamos.model.Inventario;
import com.stamos.model.Producto;
import com.stamos.model.Proyecto;
import com.stamos.model.ReservaInventario;
import com.stamos.repository.InventarioRepository;
import com.stamos.repository.ProductoRepository;
import com.stamos.repository.ProyectoRepository;
import com.stamos.repository.ReservaInventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventarioService {

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private ReservaInventarioRepository reservaRepository;


    @Transactional
    public Inventario registrar(Inventario inventario) {
        if (inventarioRepository.existsByCodigoSerial(inventario.getCodigoSerial())) {
            throw new RuntimeException("El código serial ya está registrado: " + 
                                      inventario.getCodigoSerial());
        }

        Producto producto = productoRepository.findById(inventario.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (inventario.getEstadoFisico() == null) {
            inventario.setEstadoFisico("Disponible");
        }

        inventario.setFechaRegistro(LocalDate.now());
        inventario.setFechaActualizacion(LocalDate.now());
        inventario.setNombreProducto(producto.getNombreItem());
        inventario.setDescripcionProducto(producto.getDescripcion());
        inventario.setCategoriaProducto(producto.getNombreCategoria());
        inventario.setTipoProducto(producto.getTipo());
        inventario.setPrecioBase(producto.getPrecioBase());
        inventario.setImagenUrl(producto.getImagenUrl());

        return inventarioRepository.save(inventario);
    }

    @Transactional
    public Inventario registrar(Integer idProducto, String codigoSerial, String ubicacion,
                               BigDecimal valorCompra, LocalDate fechaAdquisicion) {
        Inventario inventario = new Inventario();
        inventario.setIdProducto(idProducto);
        inventario.setCodigoSerial(codigoSerial);
        inventario.setUbicacion(ubicacion);
        inventario.setValorCompra(valorCompra);
        inventario.setFechaAdquisicion(fechaAdquisicion);
        inventario.setEstadoFisico("Disponible");
        return registrar(inventario);
    }

    @Transactional
    public Inventario actualizar(Inventario inventario) {
        Inventario existente = inventarioRepository.findById(inventario.getIdInventario())
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        if (!existente.getCodigoSerial().equals(inventario.getCodigoSerial()) &&
            inventarioRepository.existsByCodigoSerialAndIdNot(inventario.getCodigoSerial(),
                                                             inventario.getIdInventario())) {
            throw new RuntimeException("El código serial ya está registrado: " + 
                                      inventario.getCodigoSerial());
        }

        existente.setIdProducto(inventario.getIdProducto());
        existente.setCodigoSerial(inventario.getCodigoSerial());
        existente.setUbicacion(inventario.getUbicacion());
        existente.setEstadoFisico(inventario.getEstadoFisico());
        existente.setValorCompra(inventario.getValorCompra());
        existente.setFechaAdquisicion(inventario.getFechaAdquisicion());
        existente.setObservaciones(inventario.getObservaciones());
        existente.setFechaActualizacion(LocalDate.now());

        if (!existente.getIdProducto().equals(inventario.getIdProducto())) {
            Producto producto = productoRepository.findById(inventario.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            existente.setNombreProducto(producto.getNombreItem());
            existente.setDescripcionProducto(producto.getDescripcion());
            existente.setCategoriaProducto(producto.getNombreCategoria());
            existente.setTipoProducto(producto.getTipo());
            existente.setPrecioBase(producto.getPrecioBase());
            existente.setImagenUrl(producto.getImagenUrl());
        }

        return inventarioRepository.save(existente);
    }

    @Transactional
    public void update(Inventario inventario) {
        if (inventario == null || inventario.getIdInventario() == null) {
            throw new RuntimeException("Inventario inválido para actualizar");
        }
        
        Inventario existente = inventarioRepository.findById(inventario.getIdInventario())
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado con ID: " + inventario.getIdInventario()));

        if (!existente.getCodigoSerial().equals(inventario.getCodigoSerial()) &&
            inventarioRepository.existsByCodigoSerialAndIdNot(inventario.getCodigoSerial(), inventario.getIdInventario())) {
            throw new RuntimeException("El código serial ya está registrado: " + inventario.getCodigoSerial());
        }

        existente.setIdProducto(inventario.getIdProducto());
        existente.setCodigoSerial(inventario.getCodigoSerial());
        existente.setUbicacion(inventario.getUbicacion());
        existente.setEstadoFisico(inventario.getEstadoFisico());
        existente.setValorCompra(inventario.getValorCompra());
        existente.setFechaAdquisicion(inventario.getFechaAdquisicion());
        existente.setObservaciones(inventario.getObservaciones());
        existente.setFechaActualizacion(LocalDate.now());
        
        inventarioRepository.save(existente);
    }

    @Transactional
    public void save(Inventario inventario) {
        if (inventario == null) {
            throw new RuntimeException("Inventario inválido para guardar");
        }
        
        if (inventarioRepository.existsByCodigoSerial(inventario.getCodigoSerial())) {
            throw new RuntimeException("El código serial ya está registrado: " + inventario.getCodigoSerial());
        }
        
        inventario.setFechaRegistro(LocalDate.now());
        inventario.setFechaActualizacion(LocalDate.now());
        inventarioRepository.save(inventario);
    }

    @Transactional
    public Inventario actualizarEstado(Integer idInventario, String nuevoEstado,
                                      Integer idUsuarioActualizacion) {
        Inventario equipo = inventarioRepository.findById(idInventario)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        equipo.setEstadoFisico(nuevoEstado);
        equipo.setFechaActualizacion(LocalDate.now());

        return inventarioRepository.save(equipo);
    }

    @Transactional
    public void eliminar(Integer idInventario) {
        Inventario equipo = inventarioRepository.findById(idInventario)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        long reservasActivas = reservaRepository.countActivasByIdInventario(idInventario);
        if (reservasActivas > 0) {
            throw new RuntimeException("No se puede eliminar el equipo porque tiene " + 
                                      reservasActivas + " reservas activas. " +
                                      "Desactívelo en su lugar.");
        }

        inventarioRepository.delete(equipo);
    }

    @Transactional
    public ReservaInventario crearReserva(ReservaInventario reserva) {
        Inventario equipo = inventarioRepository.findById(reserva.getIdInventario())
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        Proyecto proyecto = proyectoRepository.findById(reserva.getIdProyecto())
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        if (reserva.getFechaInicio().isAfter(reserva.getFechaFin())) {
            throw new RuntimeException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        if (reserva.getFechaInicio().isBefore(LocalDate.now())) {
            throw new RuntimeException("La fecha de inicio no puede ser anterior a la fecha actual");
        }

        if (!verificarDisponibilidad(reserva.getIdInventario(), 
                                    reserva.getFechaInicio(), 
                                    reserva.getFechaFin())) {
            throw new RuntimeException("El equipo no está disponible en el rango de fechas seleccionado");
        }

        if (!"Disponible".equals(equipo.getEstadoFisico())) {
            throw new RuntimeException("El equipo no está disponible para ser asignado");
        }

        reserva.setEstadoReserva("Activa");
        reserva.setFechaReserva(LocalDateTime.now());
        reserva.setFechaActualizacion(LocalDateTime.now());

        ReservaInventario reservaGuardada = reservaRepository.save(reserva);

        equipo.setEstadoFisico("En uso");
        equipo.setFechaActualizacion(LocalDate.now());
        inventarioRepository.save(equipo);

        reservaGuardada.setNombreProducto(equipo.getNombreProducto());
        reservaGuardada.setCodigoSerial(equipo.getCodigoSerial());
        reservaGuardada.setNombreProyecto(proyecto.getNombreProyecto());

        return reservaGuardada;
    }

    @Transactional
    public ReservaInventario completarReserva(Integer idReserva, Integer idUsuarioActualizacion) {
        ReservaInventario reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (!"Activa".equals(reserva.getEstadoReserva())) {
            throw new RuntimeException("La reserva no está activa");
        }

        reserva.setEstadoReserva("Completada");
        reserva.setFechaActualizacion(LocalDateTime.now());
        reservaRepository.save(reserva);

        Inventario equipo = inventarioRepository.findById(reserva.getIdInventario())
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
        equipo.setEstadoFisico("Disponible");
        equipo.setFechaActualizacion(LocalDate.now());
        inventarioRepository.save(equipo);

        return reserva;
    }

    @Transactional
    public ReservaInventario cancelarReserva(Integer idReserva, String motivo,
                                            Integer idUsuarioActualizacion) {
        ReservaInventario reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (!"Activa".equals(reserva.getEstadoReserva())) {
            throw new RuntimeException("La reserva no está activa");
        }

        reserva.setEstadoReserva("Cancelada");
        reserva.setNotas((reserva.getNotas() != null ? reserva.getNotas() + "\n" : "") + 
                        "Cancelada: " + (motivo != null ? motivo : "Sin motivo"));
        reserva.setFechaActualizacion(LocalDateTime.now());
        reservaRepository.save(reserva);

        Inventario equipo = inventarioRepository.findById(reserva.getIdInventario())
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
        if ("En uso".equals(equipo.getEstadoFisico())) {
            equipo.setEstadoFisico("Disponible");
            equipo.setFechaActualizacion(LocalDate.now());
            inventarioRepository.save(equipo);
        }

        return reserva;
    }

    @Transactional
    public ReservaInventario updateReserva(ReservaInventario reserva) {
        if (reserva == null || reserva.getIdReserva() == null) {
            throw new RuntimeException("Reserva inválida para actualizar");
        }
        reserva.setFechaActualizacion(LocalDateTime.now());
        return reservaRepository.save(reserva);
    }

    public boolean verificarDisponibilidad(Integer idInventario, LocalDate fechaInicio,
                                          LocalDate fechaFin) {
        return reservaRepository.isDisponible(idInventario, fechaInicio, fechaFin);
    }

    public List<Inventario> findDisponiblesByFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return inventarioRepository.findDisponiblesByFechas(fechaInicio, fechaFin);
    }

    public List<Inventario> findDisponiblesByProductoAndFechas(Integer idProducto,
                                                              LocalDate fechaInicio,
                                                              LocalDate fechaFin) {
        return inventarioRepository.findDisponiblesByProductoAndFechas(idProducto, fechaInicio, fechaFin);
    }

    public List<ReservaInventario> findReservasEnRango(LocalDate fechaInicio, LocalDate fechaFin) {
        return reservaRepository.findReservasEnRango(fechaInicio, fechaFin);
    }

    public Inventario findById(Integer idInventario) {
        return inventarioRepository.findById(idInventario).orElse(null);
    }

    public Inventario findByCodigoSerial(String codigoSerial) {
        return inventarioRepository.findByCodigoSerial(codigoSerial).orElse(null);
    }

    public Page<Inventario> findAll(Pageable pageable) {
        return inventarioRepository.findAll(pageable);
    }

    public Page<Inventario> findByProducto(Integer idProducto, Pageable pageable) {
        return inventarioRepository.findByIdProducto(idProducto, pageable);
    }

    public Page<Inventario> findByEstadoFisico(String estadoFisico, Pageable unpaged) {
        return inventarioRepository.findByEstadoFisico(estadoFisico, Pageable.unpaged());
    }

    public List<Inventario> findDisponibles() {
        return inventarioRepository.findDisponibles();
    }

    public List<Inventario> findEnUso() {
        return inventarioRepository.findEnUso();
    }

    public List<Inventario> findEnMantenimiento() {
        return inventarioRepository.findEnMantenimiento();
    }

    public List<Inventario> findDanados() {
        return inventarioRepository.findDanados();
    }

    public List<Inventario> findEnBuenEstado() {
        return inventarioRepository.findEnBuenEstado();
    }

    public List<Inventario> findNecesitanMantenimiento() {
        return inventarioRepository.findNecesitanMantenimiento();
    }

    public Page<Inventario> buscar(String searchText, Pageable pageable) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return inventarioRepository.findAll(pageable);
        }
        return inventarioRepository.search(searchText, pageable);
    }

    public List<Inventario> findByUbicacion(String ubicacion) {
        return inventarioRepository.findByUbicacion(ubicacion);
    }

    public List<String> findUbicacionesUnicas() {
        return inventarioRepository.findUbicacionesUnicas();
    }

    public ReservaInventario findReservaById(Integer idReserva) {
        return reservaRepository.findById(idReserva).orElse(null);
    }

    public Page<ReservaInventario> findAllReservas(Pageable pageable) {
        return reservaRepository.findAll(pageable);
    }

    public Page<ReservaInventario> findReservasByEstado(String estadoReserva, Pageable pageable) {
        return reservaRepository.findByEstadoReserva(estadoReserva, pageable);
    }

    public List<ReservaInventario> findReservasActivas() {
        return reservaRepository.findActivas();
    }

    public List<ReservaInventario> findReservasByEquipo(Integer idInventario) {
        return reservaRepository.findByIdInventarioOrderByFechaInicioDesc(idInventario);
    }

    public List<ReservaInventario> findReservasByProyecto(Integer idProyecto) {
        return reservaRepository.findByIdProyectoOrderByFechaInicioDesc(idProyecto);
    }

    public List<ReservaInventario> findReservasActivasByProyecto(Integer idProyecto) {
        return reservaRepository.findActivasByProyecto(idProyecto);
    }

    public long count() {
        return inventarioRepository.count();
    }

    public long countByEstadoFisico(String estadoFisico) {
        return inventarioRepository.countByEstadoFisico(estadoFisico);
    }

    public long countByProducto(Integer idProducto) {
        return inventarioRepository.countByIdProducto(idProducto);
    }

    public BigDecimal sumValorCompra() {
        return inventarioRepository.sumValorCompra();
    }

    public BigDecimal sumValorCompraByEstado(String estadoFisico) {
        return inventarioRepository.sumValorCompraByEstado(estadoFisico);
    }

    public long countReservasByEstado(String estadoReserva) {
        return reservaRepository.countByEstadoReserva(estadoReserva);
    }

    public List<Object[]> countByEstadoFisicoResumen() {
        return inventarioRepository.countByEstadoFisicoResumen();
    }

    public boolean existsByCodigoSerial(String codigoSerial) {
        return inventarioRepository.existsByCodigoSerial(codigoSerial);
    }

    public boolean hasReservasActivas(Integer idInventario) {
        return reservaRepository.hasReservasActivas(idInventario);
    }

    public boolean isDisponible(Integer idInventario) {
        Inventario equipo = findById(idInventario);
        return equipo != null && "Disponible".equals(equipo.getEstadoFisico());
    }

    public String getEstadoFisicoLegible(String estadoFisico) {
        if (estadoFisico == null) {
            return "Sin estado";
        }
        return switch (estadoFisico) {
            case "Disponible" -> "Disponible";
            case "En uso" -> "En uso";
            case "Mantenimiento" -> "En mantenimiento";
            case "Dañado" -> "Dañado";
            default -> estadoFisico;
        };
    }

    public String getEstadoFisicoBadgeClass(String estadoFisico) {
        if (estadoFisico == null) {
            return "status-pendiente";
        }
        return switch (estadoFisico) {
            case "Disponible" -> "status-validado";
            case "En uso" -> "status-produccion";
            case "Mantenimiento" -> "status-pendiente";
            case "Dañado" -> "status-rechazado";
            default -> "status-pendiente";
        };
    }

    public String getEstadoFisicoColor(String estadoFisico) {
        if (estadoFisico == null) {
            return "#6B7280";
        }
        return switch (estadoFisico) {
            case "Disponible" -> "#10B981";
            case "En uso" -> "#3B82F6";
            case "Mantenimiento" -> "#F59E0B";
            case "Dañado" -> "#EF4444";
            default -> "#6B7280";
        };
    }

    public String getEstadoReservaLegible(String estadoReserva) {
        if (estadoReserva == null) {
            return "Sin estado";
        }
        return switch (estadoReserva) {
            case "Activa" -> "Activa";
            case "Completada" -> "Completada";
            case "Cancelada" -> "Cancelada";
            default -> estadoReserva;
        };
    }

    public String getEstadoReservaBadgeClass(String estadoReserva) {
        if (estadoReserva == null) {
            return "status-pendiente";
        }
        return switch (estadoReserva) {
            case "Activa" -> "status-produccion";
            case "Completada" -> "status-validado";
            case "Cancelada" -> "status-rechazado";
            default -> "status-pendiente";
        };
    }

    public String getEstadoReservaColor(String estadoReserva) {
        if (estadoReserva == null) {
            return "#6B7280";
        }
        return switch (estadoReserva) {
            case "Activa" -> "#3B82F6";
            case "Completada" -> "#10B981";
            case "Cancelada" -> "#EF4444";
            default -> "#6B7280";
        };
    }

    public String formatearDuracion(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            return "-";
        }
        int dias = (int) java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
        if (dias == 0) {
            return "Menos de 1 día";
        } else if (dias == 1) {
            return "1 día";
        }
        return dias + " días";
    }

    @Transactional
    public int liberarEquiposReservasVencidas() {
        return inventarioRepository.liberarEquiposReservasVencidas();
    }

    @Transactional
    public int completarReservasVencidas() {
        return reservaRepository.completarReservasVencidas(LocalDate.now());
    }

    public List<Inventario> findInventarioBajo() {
        return inventarioRepository.findBajoStock();
    }

    public Inventario findOldest() {
        return inventarioRepository.findOldest().orElse(null);
    }

    public Inventario findMostValuable() {
        return inventarioRepository.findMostValuable().orElse(null);
    }

    public Inventario findLeastValuable() {
        return inventarioRepository.findLeastValuable().orElse(null);
    }

    public List<Object[]> getEstadisticasDashboard() {
        return inventarioRepository.getEstadisticasPorEstado();
    }

    public List<Object[]> getEstadisticasPorCategoria() {
        return inventarioRepository.getEstadisticasPorCategoria();
    }

    public List<Object[]> findTopEquiposReservados(int limit) {
        return reservaRepository.findTopEquiposReservados();
    }

    public List<Object[]> findTopProyectosConReservas(int limit) {
        return reservaRepository.findTopProyectosConReservas();
    }

    public List<Object[]> findReservasDashboard(int limit) {
        return reservaRepository.findReservasDashboard();
    }

    public List<Inventario> findByProductoAndEstadoFisico(Integer productoId, String estado) {
        return inventarioRepository.findByIdProductoAndEstadoFisico(productoId, estado);
    }
}