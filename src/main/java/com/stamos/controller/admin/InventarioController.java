package com.stamos.controller.admin;

import com.stamos.annotation.Auditable;
import com.stamos.controller.BaseController;
import com.stamos.model.Inventario;
import com.stamos.model.Producto;
import com.stamos.model.Proyecto;
import com.stamos.model.ReservaInventario;
import com.stamos.model.Usuario;
import com.stamos.service.ClienteService;
import com.stamos.service.InventarioService;
import com.stamos.service.ProductoService;
import com.stamos.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/inventario")
@PreAuthorize("hasAnyRole('ADMIN', 'SOPORTE_TECNICO', 'PRODUCCION')")
public class InventarioController extends BaseController {

    private static final int MAX_DIAS_RESERVA = 30;

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public String listarInventario(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(required = false) String estado,
                                   @RequestParam(required = false) Integer productoId,
                                   @RequestParam(required = false) String search,
                                   Model model) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("idInventario").descending());

            Page<Inventario> inventarioPage;
            if (estado != null && !estado.isEmpty()) {
                inventarioPage = inventarioService.findByEstadoFisico(estado);
            } else if (productoId != null) {
                inventarioPage = inventarioService.findByProducto(productoId, pageable);
            } else if (search != null && !search.isEmpty()) {
                inventarioPage = inventarioService.buscar(search, pageable);
            } else {
                inventarioPage = inventarioService.findAll(pageable);
            }

            long totalDisponibles = inventarioService.countByEstadoFisico("Disponible");
            long totalEnUso = inventarioService.countByEstadoFisico("En uso");
            long totalMantenimiento = inventarioService.countByEstadoFisico("Mantenimiento");
            long totalDanados = inventarioService.countByEstadoFisico("Dañado");
            long totalInventario = inventarioService.count();

            model.addAttribute("inventario", inventarioPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", inventarioPage.getTotalPages());
            model.addAttribute("totalItems", inventarioPage.getTotalElements());
            model.addAttribute("estado", estado);
            model.addAttribute("productoId", productoId);
            model.addAttribute("search", search);
            model.addAttribute("productos", productoService.findAll());
            model.addAttribute("totalDisponibles", totalDisponibles);
            model.addAttribute("totalEnUso", totalEnUso);
            model.addAttribute("totalMantenimiento", totalMantenimiento);
            model.addAttribute("totalDanados", totalDanados);
            model.addAttribute("totalInventario", totalInventario);
            addCommonAttributes(model);

            return "admin/inventario/equipos";

        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el inventario: " + e.getMessage());
            return "admin/inventario/equipos";
        }
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        try {
            model.addAttribute("inventario", new Inventario());
            model.addAttribute("productos", productoService.findAll());
            model.addAttribute("titulo", "Nuevo Equipo");
            model.addAttribute("accion", "crear");
            addCommonAttributes(model);
            return "admin/inventario/formulario_equipo";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "redirect:/admin/inventario";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        try {
            Inventario equipo = inventarioService.findById(id);
            if (equipo == null) {
                model.addAttribute("error", "Equipo no encontrado");
                return "redirect:/admin/inventario";
            }

            model.addAttribute("inventario", equipo);
            model.addAttribute("productos", productoService.findAll());
            model.addAttribute("titulo", "Editar Equipo");
            model.addAttribute("accion", "editar");
            addCommonAttributes(model);
            return "admin/inventario/formulario_equipo";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "redirect:/admin/inventario";
        }
    }

    @PostMapping("/crear")
    @Auditable
    public String crearEquipo(@Valid @ModelAttribute("inventario") Inventario inventario,
                              BindingResult result,
                              Authentication auth,
                              RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("error", "Por favor, corrige los errores.");
                return "redirect:/admin/inventario/nuevo";
            }

            Producto producto = productoService.findById(inventario.getIdProducto());
            if (producto == null) {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado.");
                return "redirect:/admin/inventario/nuevo";
            }

            if (inventarioService.existsByCodigoSerial(inventario.getCodigoSerial())) {
                redirectAttributes.addFlashAttribute("error",
                    "El código serial ya está registrado en el sistema.");
                return "redirect:/admin/inventario/nuevo";
            }

            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario != null) {
                inventario.setIdUsuarioRegistro(usuario.getIdUsuario());
            }

            inventario.setEstadoFisico("Disponible");
            inventario.setFechaRegistro(LocalDate.now());
            inventario.setFechaActualizacion(LocalDate.now());
            inventarioService.save(inventario);

            redirectAttributes.addFlashAttribute("mensaje",
                "Equipo registrado exitosamente. Serial: " + inventario.getCodigoSerial());
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");

            return "redirect:/admin/inventario";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar el equipo: " + e.getMessage());
            return "redirect:/admin/inventario/nuevo";
        }
    }

    @PostMapping("/actualizar/{id}")
    @Auditable
    public String actualizarEquipo(@PathVariable Integer id,
                                   @Valid @ModelAttribute("inventario") Inventario inventario,
                                   BindingResult result,
                                   Authentication auth,
                                   RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("error", "Por favor, corrige los errores.");
                return "redirect:/admin/inventario/editar/" + id;
            }

            Inventario existente = inventarioService.findById(id);
            if (existente == null) {
                redirectAttributes.addFlashAttribute("error", "Equipo no encontrado.");
                return "redirect:/admin/inventario";
            }

            if (!existente.getCodigoSerial().equals(inventario.getCodigoSerial()) &&
                inventarioService.existsByCodigoSerial(inventario.getCodigoSerial())) {
                redirectAttributes.addFlashAttribute("error",
                    "El código serial ya está registrado en el sistema.");
                return "redirect:/admin/inventario/editar/" + id;
            }

            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario != null) {
                inventario.setIdUsuarioActualizacion(usuario.getIdUsuario());
            }

            inventario.setIdInventario(id);
            inventario.setFechaActualizacion(LocalDate.now());
            inventarioService.update(inventario);

            redirectAttributes.addFlashAttribute("mensaje",
                "Equipo actualizado exitosamente.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");

            return "redirect:/admin/inventario";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el equipo: " + e.getMessage());
            return "redirect:/admin/inventario/editar/" + id;
        }
    }

    @PostMapping("/cambiar-estado/{id}")
    @Auditable
    public String cambiarEstadoEquipo(@PathVariable Integer id,
                                      @RequestParam String estado,
                                      @RequestParam(required = false) String observaciones,
                                      Authentication auth,
                                      RedirectAttributes redirectAttributes) {
        try {
            Inventario equipo = inventarioService.findById(id);
            if (equipo == null) {
                redirectAttributes.addFlashAttribute("error", "Equipo no encontrado.");
                return "redirect:/admin/inventario";
            }

            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario != null) {
                equipo.setIdUsuarioActualizacion(usuario.getIdUsuario());
            }

            equipo.setEstadoFisico(estado);
            if (observaciones != null && !observaciones.isEmpty()) {
                equipo.setObservaciones(observaciones);
            }
            equipo.setFechaActualizacion(LocalDate.now());
            inventarioService.update(equipo);

            redirectAttributes.addFlashAttribute("mensaje",
                "Estado del equipo actualizado a: " + estado);
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");

            return "redirect:/admin/inventario";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar el estado: " + e.getMessage());
            return "redirect:/admin/inventario";
        }
    }

    @GetMapping("/reservar")
    public String mostrarFormularioReserva(@RequestParam(required = false) Integer equipoId,
                                           @RequestParam(required = false) Integer proyectoId,
                                           Model model) {
        try {
            ReservaInventario reserva = new ReservaInventario();
            reserva.setEstadoReserva("Activa");
            reserva.setFechaReserva(LocalDateTime.now());

            if (equipoId != null) {
                reserva.setIdInventario(equipoId);
                Inventario equipo = inventarioService.findById(equipoId);
                if (equipo != null) {
                    model.addAttribute("equipoSeleccionado", equipo);
                }
            }

            if (proyectoId != null) {
                reserva.setIdProyecto(proyectoId);
                Proyecto proyecto = proyectoService.findById(proyectoId);
                if (proyecto != null) {
                    model.addAttribute("proyectoSeleccionado", proyecto);
                }
            }

            Page<Inventario> equiposDisponibles = inventarioService.findByEstadoFisico("Disponible");

            model.addAttribute("reserva", reserva);
            model.addAttribute("equipos", equiposDisponibles);
            model.addAttribute("proyectos", proyectoService.findAll());
            model.addAttribute("titulo", "Asignar Equipo a Proyecto");
            addCommonAttributes(model);

            return "admin/inventario/formulario_reserva";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "redirect:/admin/inventario";
        }
    }

    @PostMapping("/reservar")
    @Auditable
    public String asignarEquipo(@Valid @ModelAttribute("reserva") ReservaInventario reserva,
                                BindingResult result,
                                Authentication auth,
                                RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("error", "Por favor, corrige los errores.");
                return "redirect:/admin/inventario/reservar";
            }

            if (reserva.getFechaInicio() == null || reserva.getFechaFin() == null) {
                redirectAttributes.addFlashAttribute("error",
                    "Las fechas de inicio y fin son obligatorias.");
                return "redirect:/admin/inventario/reservar";
            }

            if (reserva.getFechaInicio().isAfter(reserva.getFechaFin())) {
                redirectAttributes.addFlashAttribute("error",
                    "La fecha de inicio no puede ser posterior a la fecha de fin.");
                return "redirect:/admin/inventario/reservar";
            }

            if (reserva.getFechaInicio().isBefore(LocalDate.now())) {
                redirectAttributes.addFlashAttribute("error",
                    "La fecha de inicio no puede ser anterior a la fecha actual.");
                return "redirect:/admin/inventario/reservar";
            }

            long dias = ChronoUnit.DAYS.between(reserva.getFechaInicio(), reserva.getFechaFin());
            if (dias > MAX_DIAS_RESERVA) {
                redirectAttributes.addFlashAttribute("error",
                    "La reserva no puede exceder los " + MAX_DIAS_RESERVA + " días.");
                return "redirect:/admin/inventario/reservar";
            }

            boolean disponible = inventarioService.verificarDisponibilidad(
                reserva.getIdInventario(),
                reserva.getFechaInicio(),
                reserva.getFechaFin()
            );

            if (!disponible) {
                redirectAttributes.addFlashAttribute("error",
                    "El equipo no está disponible en el rango de fechas seleccionado.");
                return "redirect:/admin/inventario/reservar";
            }

            Inventario equipo = inventarioService.findById(reserva.getIdInventario());
            if (equipo == null || !"Disponible".equals(equipo.getEstadoFisico())) {
                redirectAttributes.addFlashAttribute("error",
                    "El equipo no está disponible para ser asignado.");
                return "redirect:/admin/inventario/reservar";
            }

            Proyecto proyecto = proyectoService.findById(reserva.getIdProyecto());
            if (proyecto == null) {
                redirectAttributes.addFlashAttribute("error", "Proyecto no encontrado.");
                return "redirect:/admin/inventario/reservar";
            }

            if (!proyecto.isActivo()) {
                redirectAttributes.addFlashAttribute("error",
                    "El proyecto no está activo. No se puede asignar equipos.");
                return "redirect:/admin/inventario/reservar";
            }

            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario != null) {
                reserva.setIdUsuarioRegistro(usuario.getIdUsuario());
            }

            reserva.setEstadoReserva("Activa");
            reserva.setFechaReserva(LocalDateTime.now());
            reserva.setFechaActualizacion(LocalDateTime.now());
            inventarioService.crearReserva(reserva);

            equipo.setEstadoFisico("En uso");
            equipo.setFechaActualizacion(LocalDate.now());
            inventarioService.update(equipo);

            redirectAttributes.addFlashAttribute("mensaje",
                "Equipo asignado exitosamente al proyecto: " + proyecto.getNombreProyecto());
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");

            return "redirect:/admin/inventario/reservas";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al asignar el equipo: " + e.getMessage());
            return "redirect:/admin/inventario/reservar";
        }
    }

    @GetMapping("/reservas")
    public String listarReservas(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) String estado,
                                 Model model) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("fechaReserva").descending());

            Page<ReservaInventario> reservasPage;
            if (estado != null && !estado.isEmpty()) {
                reservasPage = inventarioService.findReservasByEstado(estado, pageable);
            } else {
                reservasPage = inventarioService.findAllReservas(pageable);
            }

            for (ReservaInventario reserva : reservasPage.getContent()) {
                Inventario equipo = inventarioService.findById(reserva.getIdInventario());
                if (equipo != null) {
                    reserva.setNombreEquipo(equipo.getCodigoSerial());
                    Producto producto = productoService.findById(equipo.getIdProducto());
                    if (producto != null) {
                        reserva.setNombreProducto(producto.getNombreItem());
                    }
                }
                Proyecto proyecto = proyectoService.findById(reserva.getIdProyecto());
                if (proyecto != null) {
                    reserva.setNombreProyecto(proyecto.getNombreProyecto());
                }
            }

            long totalActivas = inventarioService.countReservasByEstado("Activa");
            long totalCompletadas = inventarioService.countReservasByEstado("Completada");
            long totalCanceladas = inventarioService.countReservasByEstado("Cancelada");

            model.addAttribute("reservas", reservasPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", reservasPage.getTotalPages());
            model.addAttribute("totalItems", reservasPage.getTotalElements());
            model.addAttribute("estado", estado);
            model.addAttribute("totalActivas", totalActivas);
            model.addAttribute("totalCompletadas", totalCompletadas);
            model.addAttribute("totalCanceladas", totalCanceladas);
            addCommonAttributes(model);

            return "admin/inventario/reservas";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar las reservas: " + e.getMessage());
            return "admin/inventario/reservas";
        }
    }

    @PostMapping("/reserva/completar/{id}")
    @Auditable
    public String completarReserva(@PathVariable Integer id,
                                   @RequestParam(required = false) String notas,
                                   Authentication auth,
                                   RedirectAttributes redirectAttributes) {
        try {
            ReservaInventario reserva = inventarioService.findReservaById(id);
            if (reserva == null) {
                redirectAttributes.addFlashAttribute("error", "Reserva no encontrada.");
                return "redirect:/admin/inventario/reservas";
            }

            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario != null) {
                reserva.setIdUsuarioActualizacion(usuario.getIdUsuario());
            }

            reserva.setEstadoReserva("Completada");
            if (notas != null && !notas.isEmpty()) {
                reserva.setNotas(notas);
            }
            reserva.setFechaActualizacion(LocalDateTime.now());
            inventarioService.updateReserva(reserva);

            Inventario equipo = inventarioService.findById(reserva.getIdInventario());
            if (equipo != null) {
                equipo.setEstadoFisico("Disponible");
                equipo.setFechaActualizacion(LocalDate.now());
                inventarioService.update(equipo);
            }

            redirectAttributes.addFlashAttribute("mensaje",
                "Reserva completada exitosamente. Equipo devuelto al inventario.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");

            return "redirect:/admin/inventario/reservas";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al completar la reserva: " + e.getMessage());
            return "redirect:/admin/inventario/reservas";
        }
    }

    @PostMapping("/reserva/cancelar/{id}")
    @Auditable
    public String cancelarReserva(@PathVariable Integer id,
                                  @RequestParam(required = false) String motivo,
                                  Authentication auth,
                                  RedirectAttributes redirectAttributes) {
        try {
            ReservaInventario reserva = inventarioService.findReservaById(id);
            if (reserva == null) {
                redirectAttributes.addFlashAttribute("error", "Reserva no encontrada.");
                return "redirect:/admin/inventario/reservas";
            }

            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario != null) {
                reserva.setIdUsuarioActualizacion(usuario.getIdUsuario());
            }

            reserva.setEstadoReserva("Cancelada");
            if (motivo != null && !motivo.isEmpty()) {
                reserva.setNotas("Cancelada: " + motivo);
            }
            reserva.setFechaActualizacion(LocalDateTime.now());
            inventarioService.updateReserva(reserva);

            Inventario equipo = inventarioService.findById(reserva.getIdInventario());
            if (equipo != null && "En uso".equals(equipo.getEstadoFisico())) {
                equipo.setEstadoFisico("Disponible");
                equipo.setFechaActualizacion(LocalDate.now());
                inventarioService.update(equipo);
            }

            redirectAttributes.addFlashAttribute("mensaje",
                "Reserva cancelada exitosamente.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");

            return "redirect:/admin/inventario/reservas";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cancelar la reserva: " + e.getMessage());
            return "redirect:/admin/inventario/reservas";
        }
    }

    @GetMapping("/disponibilidad")
    public String consultarDisponibilidad(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                                          @RequestParam(required = false) Integer productoId,
                                          Model model) {
        try {
            if (fechaInicio == null) {
                fechaInicio = LocalDate.now();
            }
            if (fechaFin == null) {
                fechaFin = LocalDate.now().plusDays(1);
            }

            if (fechaInicio.isAfter(fechaFin)) {
                model.addAttribute("error", "La fecha de inicio no puede ser posterior a la fecha de fin.");
                fechaInicio = LocalDate.now();
                fechaFin = LocalDate.now().plusDays(1);
            }

            List<Inventario> equiposDisponibles;
            if (productoId != null) {
                equiposDisponibles = inventarioService.findDisponiblesByProductoAndFechas(
                    productoId, fechaInicio, fechaFin);
            } else {
                equiposDisponibles = inventarioService.findDisponiblesByFechas(fechaInicio, fechaFin);
            }

            List<ReservaInventario> reservas = inventarioService.findReservasEnRango(fechaInicio, fechaFin);

            long totalEquipos = inventarioService.count();
            long totalDisponibles = equiposDisponibles.size();
            long totalReservados = reservas.size();
            long totalMantenimiento = inventarioService.countByEstadoFisico("Mantenimiento");

            model.addAttribute("fechaInicio", fechaInicio);
            model.addAttribute("fechaFin", fechaFin);
            model.addAttribute("fechaInicioStr", fechaInicio.format(DateTimeFormatter.ISO_LOCAL_DATE));
            model.addAttribute("fechaFinStr", fechaFin.format(DateTimeFormatter.ISO_LOCAL_DATE));
            model.addAttribute("productoId", productoId);
            model.addAttribute("equiposDisponibles", equiposDisponibles);
            model.addAttribute("reservas", reservas);
            model.addAttribute("productos", productoService.findAll());
            model.addAttribute("totalEquipos", totalEquipos);
            model.addAttribute("totalDisponibles", totalDisponibles);
            model.addAttribute("totalReservados", totalReservados);
            model.addAttribute("totalMantenimiento", totalMantenimiento);
            model.addAttribute("porcentajeDisponibilidad", totalEquipos > 0 ?
                (totalDisponibles * 100 / totalEquipos) : 0);
            addCommonAttributes(model);

            return "admin/inventario/disponibilidad";
        } catch (Exception e) {
            model.addAttribute("error", "Error al consultar disponibilidad: " + e.getMessage());
            return "admin/inventario/disponibilidad";
        }
    }

    @GetMapping("/verificar-disponibilidad/{equipoId}")
    @ResponseBody
    public Map<String, Object> verificarDisponibilidad(@PathVariable Integer equipoId,
                                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            boolean disponible = inventarioService.verificarDisponibilidad(equipoId, fechaInicio, fechaFin);
            Inventario equipo = inventarioService.findById(equipoId);

            return Map.of(
                "disponible", disponible,
                "equipoId", equipoId,
                "codigoSerial", equipo != null ? equipo.getCodigoSerial() : null,
                "fechaInicio", fechaInicio.toString(),
                "fechaFin", fechaFin.toString()
            );
        } catch (Exception e) {
            return Map.of(
                "disponible", false,
                "error", e.getMessage()
            );
        }
    }

    @GetMapping("/reservas-equipo/{equipoId}")
    @ResponseBody
    public List<ReservaInventario> getReservasByEquipo(@PathVariable Integer equipoId) {
        try {
            return inventarioService.findReservasByEquipo(equipoId);
        } catch (Exception e) {
            return List.of();
        }
    }

    @GetMapping("/disponibles-por-producto/{productoId}")
    @ResponseBody
    public List<Inventario> getDisponiblesByProducto(@PathVariable Integer productoId) {
        try {
            return inventarioService.findByProductoAndEstadoFisico(productoId, "Disponible");
        } catch (Exception e) {
            return List.of();
        }
    }

    public String getEstadoFisicoLegible(String estado) {
        if (estado == null) return "Sin estado";
        return switch (estado) {
            case "Disponible" -> "Disponible";
            case "En uso" -> "En uso";
            case "Mantenimiento" -> "En mantenimiento";
            case "Dañado" -> "Dañado";
            default -> estado;
        };
    }

    public String getEstadoFisicoBadgeClass(String estado) {
        if (estado == null) return "status-pendiente";
        return switch (estado) {
            case "Disponible" -> "status-validado";
            case "En uso" -> "status-produccion";
            case "Mantenimiento" -> "status-pendiente";
            case "Dañado" -> "status-rechazado";
            default -> "status-pendiente";
        };
    }
}