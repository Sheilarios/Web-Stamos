package com.stamos.controller.cliente;

import com.stamos.model.Cliente;
import com.stamos.model.Proyecto;
import com.stamos.model.Usuario;
import com.stamos.service.ClienteService;
import com.stamos.service.PagoService;
import com.stamos.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cliente")
public class ClienteDashboardController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private PagoService pagoService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        System.out.println("=== Dashboard Cliente GET ===");
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            System.out.println("Usuario no autenticado, redirigiendo a login");
            return "redirect:/login";
        }

        String email = auth.getName();
        System.out.println("Usuario autenticado: " + email);

        try {
            Usuario usuario = clienteService.findUsuarioByEmail(email);
            if (usuario == null) {
                System.out.println("Usuario no encontrado: " + email);
                model.addAttribute("error", "Usuario no encontrado");
                model.addAttribute("cliente", new Cliente());
                model.addAttribute("proyectos", new ArrayList<>());
                model.addAttribute("proyectosActivos", 0L);
                model.addAttribute("totalCotizado", BigDecimal.ZERO);
                model.addAttribute("totalPagado", BigDecimal.ZERO);
                model.addAttribute("saldoPendiente", BigDecimal.ZERO);
                model.addAttribute("totalProyectos", 0);
                return "cliente/dashboard";
            }

            Cliente cliente = clienteService.findByUsuario(usuario);
            if (cliente == null) {
                System.out.println("Cliente no encontrado para usuario ID: " + usuario.getIdUsuario());
                model.addAttribute("error", "Cliente no encontrado");
                model.addAttribute("cliente", new Cliente());
                model.addAttribute("proyectos", new ArrayList<>());
                model.addAttribute("proyectosActivos", 0L);
                model.addAttribute("totalCotizado", BigDecimal.ZERO);
                model.addAttribute("totalPagado", BigDecimal.ZERO);
                model.addAttribute("saldoPendiente", BigDecimal.ZERO);
                model.addAttribute("totalProyectos", 0);
                return "cliente/dashboard";
            }

            System.out.println("Cliente encontrado - ID: " + cliente.getIdCliente() + ", Nombre: " + cliente.getNombreRazonSocial());

            List<Proyecto> proyectos = new ArrayList<>();
            try {
                proyectos = proyectoService.findByCliente(cliente.getIdCliente());
                if (proyectos == null) {
                    proyectos = new ArrayList<>();
                }
            } catch (Exception e) {
                System.err.println("Error al cargar proyectos: " + e.getMessage());
                proyectos = new ArrayList<>();
            }
            System.out.println("Proyectos encontrados: " + proyectos.size());

            long proyectosActivos = proyectos.stream()
                    .filter(p -> {
                        Integer estado = p.getIdEstadoActual();
                        return estado != null && estado != 5 && estado != 6;
                    })
                    .count();

            BigDecimal totalCotizado = proyectos.stream()
                    .map(Proyecto::getMontoTotal)
                    .filter(monto -> monto != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalPagado = BigDecimal.ZERO;
            try {
                totalPagado = pagoService.getTotalPagadoByCliente(cliente.getIdCliente());
                if (totalPagado == null) {
                    totalPagado = BigDecimal.ZERO;
                }
            } catch (Exception e) {
                System.err.println("Error al obtener pagos: " + e.getMessage());
                totalPagado = BigDecimal.ZERO;
            }

            BigDecimal saldoPendiente = totalCotizado.subtract(totalPagado);
            if (saldoPendiente.compareTo(BigDecimal.ZERO) < 0) {
                saldoPendiente = BigDecimal.ZERO;
            }

            LocalDateTime ultimoAcceso = null;
            try {
                ultimoAcceso = clienteService.getUltimoAcceso(cliente.getIdCliente());
            } catch (Exception e) {
                System.err.println("Error al obtener ultimo acceso: " + e.getMessage());
            }

            List<Proyecto> proyectosRecientes = proyectos.stream()
                    .filter(p -> p.getIdProyecto() != null)
                    .sorted((p1, p2) -> {
                        if (p1.getIdProyecto() == null || p2.getIdProyecto() == null) return 0;
                        return p2.getIdProyecto().compareTo(p1.getIdProyecto());
                    })
                    .limit(5)
                    .toList();

            for (Proyecto proyecto : proyectosRecientes) {
                try {
                    BigDecimal totalPagadoProyecto = pagoService.getTotalPagadoByProyecto(proyecto.getIdProyecto());
                    if (totalPagadoProyecto == null) {
                        totalPagadoProyecto = BigDecimal.ZERO;
                    }
                    proyecto.setTotalPagado(totalPagadoProyecto);
                    BigDecimal saldoPendienteProyecto = proyecto.getMontoTotal().subtract(totalPagadoProyecto);
                    if (saldoPendienteProyecto.compareTo(BigDecimal.ZERO) < 0) {
                        saldoPendienteProyecto = BigDecimal.ZERO;
                    }
                    proyecto.setSaldoPendiente(saldoPendienteProyecto);
                } catch (Exception e) {
                    System.err.println("Error al obtener pagos del proyecto: " + e.getMessage());
                    proyecto.setTotalPagado(BigDecimal.ZERO);
                    proyecto.setSaldoPendiente(proyecto.getMontoTotal());
                }
            }

            model.addAttribute("cliente", cliente);
            model.addAttribute("proyectos", proyectosRecientes);
            model.addAttribute("proyectosActivos", proyectosActivos);
            model.addAttribute("totalCotizado", totalCotizado);
            model.addAttribute("totalPagado", totalPagado);
            model.addAttribute("saldoPendiente", saldoPendiente);
            model.addAttribute("ultimoAcceso", ultimoAcceso);
            model.addAttribute("totalProyectos", proyectos.size());
            model.addAttribute("titulo", "Dashboard - Cliente");

            try {
                clienteService.registrarAcceso(cliente.getIdCliente());
            } catch (Exception e) {
                System.err.println("Error al registrar acceso del cliente: " + e.getMessage());
            }

            return "cliente/dashboard";

        } catch (Exception e) {
            System.err.println("Error en dashboard: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar el dashboard: " + e.getMessage());
            model.addAttribute("cliente", new Cliente());
            model.addAttribute("proyectos", new ArrayList<>());
            model.addAttribute("proyectosActivos", 0L);
            model.addAttribute("totalCotizado", BigDecimal.ZERO);
            model.addAttribute("totalPagado", BigDecimal.ZERO);
            model.addAttribute("saldoPendiente", BigDecimal.ZERO);
            model.addAttribute("totalProyectos", 0);
            return "cliente/dashboard";
        }
    }

    @GetMapping({"", "/"})
    public String redirectToDashboard() {
        return "redirect:/cliente/dashboard";
    }

    public long getProyectosActivos(Cliente cliente) {
        if (cliente == null || cliente.getIdCliente() == null) {
            return 0;
        }
        List<Proyecto> proyectos = proyectoService.findByCliente(cliente.getIdCliente());
        if (proyectos == null) return 0;
        return proyectos.stream()
                .filter(p -> {
                    Integer estado = p.getIdEstadoActual();
                    return estado != null && estado != 5 && estado != 6;
                })
                .count();
    }

    public BigDecimal getTotalCotizado(Cliente cliente) {
        if (cliente == null || cliente.getIdCliente() == null) {
            return BigDecimal.ZERO;
        }
        List<Proyecto> proyectos = proyectoService.findByCliente(cliente.getIdCliente());
        if (proyectos == null) return BigDecimal.ZERO;
        return proyectos.stream()
                .map(Proyecto::getMontoTotal)
                .filter(monto -> monto != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalPagado(Cliente cliente) {
        if (cliente == null || cliente.getIdCliente() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = pagoService.getTotalPagadoByCliente(cliente.getIdCliente());
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getSaldoPendiente(Cliente cliente) {
        BigDecimal totalCotizado = getTotalCotizado(cliente);
        BigDecimal totalPagado = getTotalPagado(cliente);
        BigDecimal saldo = totalCotizado.subtract(totalPagado);
        return saldo.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : saldo;
    }

    public LocalDateTime getUltimoAcceso(Cliente cliente) {
        if (cliente == null || cliente.getIdCliente() == null) {
            return null;
        }
        return clienteService.getUltimoAcceso(cliente.getIdCliente());
    }

    public String getEstadoLegible(Proyecto proyecto) {
        if (proyecto == null) {
            return "Sin estado";
        }
        Integer estado = proyecto.getIdEstadoActual();
        if (estado == null) {
            return "Sin estado";
        }
        switch (estado) {
            case 1:
                return "Aprobado";
            case 2:
                return "En Diseño";
            case 3:
                return "En Producción";
            case 4:
                return "Instalación/Montaje";
            case 5:
                return "Entregado";
            case 6:
                return "Anulado";
            default:
                return "Desconocido";
        }
    }

    public String getEstadoBadgeClass(Proyecto proyecto) {
        if (proyecto == null) {
            return "status-pendiente";
        }
        Integer estado = proyecto.getIdEstadoActual();
        if (estado == null) {
            return "status-pendiente";
        }
        switch (estado) {
            case 1:
                return "status-aprobado";
            case 2:
                return "status-diseno";
            case 3:
                return "status-produccion";
            case 4:
                return "status-instalacion";
            case 5:
                return "status-entregado";
            case 6:
                return "status-anulado";
            default:
                return "status-pendiente";
        }
    }

    public String formatearMoneda(BigDecimal monto) {
        if (monto == null) return "S/ 0.00";
        return "S/ " + monto.setScale(2, java.math.RoundingMode.HALF_UP).toString();
    }

    public String formatearFecha(LocalDateTime fecha) {
        if (fecha == null) return "-";
        java.time.format.DateTimeFormatter formatter = 
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fecha.format(formatter);
    }

    public boolean tieneProyectosActivos(Cliente cliente) {
        return getProyectosActivos(cliente) > 0;
    }

    public String getSaldoEstado(BigDecimal saldoPendiente) {
        if (saldoPendiente == null || saldoPendiente.compareTo(BigDecimal.ZERO) == 0) {
            return "success";
        }
        return "warning";
    }

    public String getSaldoMensaje(BigDecimal saldoPendiente) {
        if (saldoPendiente == null || saldoPendiente.compareTo(BigDecimal.ZERO) == 0) {
            return "¡Todo pagado!";
        }
        return "Pendiente de pago";
    }
}