package com.stamos.controller.cliente;

import com.stamos.model.Cliente;
import com.stamos.model.Pago;
import com.stamos.model.Proyecto;
import com.stamos.model.Usuario;
import com.stamos.service.ClienteService;
import com.stamos.service.PagoService;
import com.stamos.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cliente")
public class ClientePagoController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private PagoService pagoService;

    @GetMapping("/pagos")
    public String listarPagos(Model model, Authentication auth) {
        try {
            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado");
            }

            Cliente cliente = clienteService.findByUsuario(usuario);
            if (cliente == null) {
                throw new RuntimeException("Cliente no encontrado");
            }

            List<Pago> pagos = pagoService.findByCliente(cliente.getIdCliente());
            
            List<Proyecto> proyectos = proyectoService.findByCliente(cliente.getIdCliente());

            for (Pago pago : pagos) {
                Proyecto proyecto = proyectoService.findById(pago.getIdProyecto());
                if (proyecto != null) {
                    pago.setNombreProyecto(proyecto.getNombreProyecto());
                    pago.setMontoTotalProyecto(proyecto.getMontoTotal());
                }
            }

            BigDecimal totalPagado = pagoService.getTotalPagadoByCliente(cliente.getIdCliente());
            if (totalPagado == null) {
                totalPagado = BigDecimal.ZERO;
            }

            BigDecimal totalCotizado = proyectos.stream()
                    .map(Proyecto::getMontoTotal)
                    .filter(monto -> monto != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal saldoPendiente = totalCotizado.subtract(totalPagado);
            if (saldoPendiente.compareTo(BigDecimal.ZERO) < 0) {
                saldoPendiente = BigDecimal.ZERO;
            }

            long pagosPendientesValidacion = pagos.stream()
                    .filter(p -> "Pendiente".equals(p.getEstadoPago()))
                    .count();

            long pagosValidados = pagos.stream()
                    .filter(p -> "Validado".equals(p.getEstadoPago()))
                    .count();
            long pagosRechazados = pagos.stream()
                    .filter(p -> "Rechazado".equals(p.getEstadoPago()))
                    .count();

            long efectivo = pagos.stream()
                    .filter(p -> "Efectivo".equals(p.getMetodoPago()))
                    .count();
            long transferencia = pagos.stream()
                    .filter(p -> "Transferencia".equals(p.getMetodoPago()))
                    .count();
            long tarjeta = pagos.stream()
                    .filter(p -> "Tarjeta".equals(p.getMetodoPago()))
                    .count();
            long cheque = pagos.stream()
                    .filter(p -> "Cheque".equals(p.getMetodoPago()))
                    .count();

            int porcentajePagado = 0;
            if (totalCotizado.compareTo(BigDecimal.ZERO) > 0) {
                porcentajePagado = totalPagado.multiply(new BigDecimal(100))
                        .divide(totalCotizado, 0, RoundingMode.HALF_UP)
                        .intValue();
                if (porcentajePagado > 100) {
                    porcentajePagado = 100;
                }
            }

            List<Proyecto> proyectosVencidos = proyectoService.findProyectosConPagosVencidosByCliente(cliente.getIdCliente());
            
            for (Proyecto proyecto : proyectosVencidos) {
                BigDecimal totalPagadoProyecto = pagoService.getTotalPagadoByProyecto(proyecto.getIdProyecto());
                BigDecimal saldoPendienteProyecto = proyecto.getMontoTotal().subtract(totalPagadoProyecto);
                if (saldoPendienteProyecto.compareTo(BigDecimal.ZERO) < 0) {
                    saldoPendienteProyecto = BigDecimal.ZERO;
                }
                proyecto.setSaldoPendiente(saldoPendienteProyecto);
                proyecto.setTotalPagado(totalPagadoProyecto);
                
                if (proyecto.getFechaVencimiento() != null) {
                    long diasVencidos = java.time.temporal.ChronoUnit.DAYS.between(
                        proyecto.getFechaVencimiento(), LocalDate.now());
                    proyecto.setDiasVencidos((int) Math.max(0, diasVencidos));
                }
            }

            model.addAttribute("cliente", cliente);
            model.addAttribute("pagos", pagos);
            model.addAttribute("proyectos", proyectos);
            model.addAttribute("totalProyectos", proyectos.size());
            model.addAttribute("totalPagado", totalPagado);
            model.addAttribute("saldoPendiente", saldoPendiente);
            model.addAttribute("porcentajePagado", porcentajePagado);
            model.addAttribute("pagosPendientesValidacion", pagosPendientesValidacion);
            model.addAttribute("pagosValidados", pagosValidados);
            model.addAttribute("pagosRechazados", pagosRechazados);
            model.addAttribute("efectivo", efectivo);
            model.addAttribute("transferencia", transferencia);
            model.addAttribute("tarjeta", tarjeta);
            model.addAttribute("cheque", cheque);
            model.addAttribute("proyectosVencidos", proyectosVencidos);
            model.addAttribute("tienePagosVencidos", !proyectosVencidos.isEmpty());

            return "cliente/historial-pagos";

        } catch (RuntimeException e) {
            model.addAttribute("error", "Error al cargar los pagos: " + e.getMessage());
            return "cliente/dashboard";
        }
    }

    @GetMapping("/pagos/detalle/{id}")
    @ResponseBody
    public Pago getDetallePago(@PathVariable("id") Integer idPago, Authentication auth) {
        try {
            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado");
            }

            Cliente cliente = clienteService.findByUsuario(usuario);
            if (cliente == null) {
                throw new RuntimeException("Cliente no encontrado");
            }

            Pago pago = pagoService.findById(idPago);
            if (pago == null) {
                throw new RuntimeException("Pago no encontrado");
            }

            Proyecto proyecto = proyectoService.findById(pago.getIdProyecto());
            if (proyecto == null || !proyecto.getIdCliente().equals(cliente.getIdCliente())) {
                throw new AccessDeniedException("No tienes acceso a este pago");
            }

            pago.setNombreProyecto(proyecto.getNombreProyecto());
            pago.setMontoTotalProyecto(proyecto.getMontoTotal());

            return pago;

        } catch (AccessDeniedException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al obtener el detalle del pago: " + e.getMessage());
        }
    }

    @GetMapping("/pagos/filtrar")
    @ResponseBody
    public List<Pago> filtrarPagos(@RequestParam(value = "estado", required = false) String estado,
                                   @RequestParam(value = "metodo", required = false) String metodo,
                                   @RequestParam(value = "proyecto", required = false) Integer idProyecto,
                                   Authentication auth) {
        try {
            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado");
            }

            Cliente cliente = clienteService.findByUsuario(usuario);
            if (cliente == null) {
                throw new RuntimeException("Cliente no encontrado");
            }

            List<Pago> pagos = pagoService.findByCliente(cliente.getIdCliente());

            for (Pago pago : pagos) {
                Proyecto proyecto = proyectoService.findById(pago.getIdProyecto());
                if (proyecto != null) {
                    pago.setNombreProyecto(proyecto.getNombreProyecto());
                }
            }

            if (estado != null && !estado.isEmpty() && !"todos".equals(estado)) {
                pagos = pagos.stream()
                        .filter(p -> estado.equals(p.getEstadoPago()))
                        .collect(Collectors.toList());
            }

            if (metodo != null && !metodo.isEmpty() && !"todos".equals(metodo)) {
                pagos = pagos.stream()
                        .filter(p -> metodo.equals(p.getMetodoPago()))
                        .collect(Collectors.toList());
            }

            if (idProyecto != null) {
                Proyecto proyecto = proyectoService.findById(idProyecto);
                if (proyecto != null && proyecto.getIdCliente().equals(cliente.getIdCliente())) {
                    pagos = pagos.stream()
                            .filter(p -> p.getIdProyecto().equals(idProyecto))
                            .collect(Collectors.toList());
                } else {
                    return List.of();
                }
            }

            return pagos;

        } catch (RuntimeException e) {
            return List.of();
        }
    }

    @GetMapping("/pagos/resumen")
    @ResponseBody
    public ResumenFinanciero getResumenFinanciero(Authentication auth) {
        try {
            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado");
            }

            Cliente cliente = clienteService.findByUsuario(usuario);
            if (cliente == null) {
                throw new RuntimeException("Cliente no encontrado");
            }

            List<Proyecto> proyectos = proyectoService.findByCliente(cliente.getIdCliente());
            List<Pago> pagos = pagoService.findByCliente(cliente.getIdCliente());

            BigDecimal totalCotizado = proyectos.stream()
                    .map(Proyecto::getMontoTotal)
                    .filter(monto -> monto != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalPagado = pagoService.getTotalPagadoByCliente(cliente.getIdCliente());
            if (totalPagado == null) {
                totalPagado = BigDecimal.ZERO;
            }

            BigDecimal saldoPendiente = totalCotizado.subtract(totalPagado);
            if (saldoPendiente.compareTo(BigDecimal.ZERO) < 0) {
                saldoPendiente = BigDecimal.ZERO;
            }

            int porcentajePagado = 0;
            if (totalCotizado.compareTo(BigDecimal.ZERO) > 0) {
                porcentajePagado = totalPagado.multiply(new BigDecimal(100))
                        .divide(totalCotizado, 0, RoundingMode.HALF_UP)
                        .intValue();
                if (porcentajePagado > 100) {
                    porcentajePagado = 100;
                }
            }

            List<Proyecto> proyectosVencidos = proyectoService.findProyectosConPagosVencidosByCliente(cliente.getIdCliente());

            ResumenFinanciero resumen = new ResumenFinanciero();
            resumen.setTotalProyectos(proyectos.size());
            resumen.setTotalCotizado(totalCotizado);
            resumen.setTotalPagado(totalPagado);
            resumen.setSaldoPendiente(saldoPendiente);
            resumen.setPorcentajePagado(porcentajePagado);
            resumen.setTotalPagos((long) pagos.size());
            resumen.setPagosPendientes(pagos.stream().filter(p -> "Pendiente".equals(p.getEstadoPago())).count());
            resumen.setPagosValidados(pagos.stream().filter(p -> "Validado".equals(p.getEstadoPago())).count());
            resumen.setPagosRechazados(pagos.stream().filter(p -> "Rechazado".equals(p.getEstadoPago())).count());
            resumen.setTotalVencidos((long) proyectosVencidos.size());

            return resumen;

        } catch (RuntimeException e) {
            ResumenFinanciero resumen = new ResumenFinanciero();
            resumen.setError(e.getMessage());
            return resumen;
        }
    }

    @GetMapping("/pagos/vencidos")
    @ResponseBody
    public List<Proyecto> getPagosVencidos(Authentication auth) {
        try {
            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado");
            }

            Cliente cliente = clienteService.findByUsuario(usuario);
            if (cliente == null) {
                throw new RuntimeException("Cliente no encontrado");
            }

            List<Proyecto> proyectosVencidos = proyectoService.findProyectosConPagosVencidosByCliente(cliente.getIdCliente());

            for (Proyecto proyecto : proyectosVencidos) {
                BigDecimal totalPagado = pagoService.getTotalPagadoByProyecto(proyecto.getIdProyecto());
                BigDecimal saldoPendiente = proyecto.getMontoTotal().subtract(totalPagado);
                if (saldoPendiente.compareTo(BigDecimal.ZERO) < 0) {
                    saldoPendiente = BigDecimal.ZERO;
                }
                proyecto.setSaldoPendiente(saldoPendiente);
                proyecto.setTotalPagado(totalPagado);
                
                if (proyecto.getFechaVencimiento() != null) {
                    long diasVencidos = java.time.temporal.ChronoUnit.DAYS.between(
                        proyecto.getFechaVencimiento(), LocalDate.now());
                    proyecto.setDiasVencidos((int) Math.max(0, diasVencidos));
                }
            }

            return proyectosVencidos;

        } catch (RuntimeException e) {
            return List.of();
        }
    }

    @GetMapping("/pagos/saldo-proyecto/{id}")
    @ResponseBody
    public SaldoProyecto getSaldoProyecto(@PathVariable("id") Integer idProyecto, Authentication auth) {
        try {
            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado");
            }

            Cliente cliente = clienteService.findByUsuario(usuario);
            if (cliente == null) {
                throw new RuntimeException("Cliente no encontrado");
            }

            Proyecto proyecto = proyectoService.findById(idProyecto);
            if (proyecto == null || !proyecto.getIdCliente().equals(cliente.getIdCliente())) {
                throw new AccessDeniedException("No tienes acceso a este proyecto");
            }

            BigDecimal totalPagado = pagoService.getTotalPagadoByProyecto(idProyecto);
            if (totalPagado == null) {
                totalPagado = BigDecimal.ZERO;
            }

            BigDecimal saldoPendiente = proyecto.getMontoTotal().subtract(totalPagado);
            if (saldoPendiente.compareTo(BigDecimal.ZERO) < 0) {
                saldoPendiente = BigDecimal.ZERO;
            }

            int porcentajePagado = 0;
            if (proyecto.getMontoTotal().compareTo(BigDecimal.ZERO) > 0) {
                porcentajePagado = totalPagado.multiply(new BigDecimal(100))
                        .divide(proyecto.getMontoTotal(), 0, RoundingMode.HALF_UP)
                        .intValue();
                if (porcentajePagado > 100) {
                    porcentajePagado = 100;
                }
            }

            SaldoProyecto saldo = new SaldoProyecto();
            saldo.setIdProyecto(idProyecto);
            saldo.setNombreProyecto(proyecto.getNombreProyecto());
            saldo.setMontoTotal(proyecto.getMontoTotal());
            saldo.setTotalPagado(totalPagado);
            saldo.setSaldoPendiente(saldoPendiente);
            saldo.setPorcentajePagado(porcentajePagado);
            saldo.setFechaVencimiento(proyecto.getFechaVencimiento());
            
            if (proyecto.getFechaVencimiento() != null && saldoPendiente.compareTo(BigDecimal.ZERO) > 0) {
                LocalDate hoy = LocalDate.now();
                saldo.setVencido(proyecto.getFechaVencimiento().isBefore(hoy));
                if (saldo.isVencido()) {
                    long diasVencidos = java.time.temporal.ChronoUnit.DAYS.between(
                        proyecto.getFechaVencimiento(), hoy);
                    saldo.setDiasVencidos((int) diasVencidos);
                }
            }

            return saldo;

        } catch (AccessDeniedException e) {
            throw e;
        } catch (RuntimeException e) {
            SaldoProyecto saldo = new SaldoProyecto();
            saldo.setError(e.getMessage());
            return saldo;
        }
    }

    public String getEstadoLegible(String estadoPago) {
        if (estadoPago == null) return "Sin estado";
        
        return switch (estadoPago) {
            case "Validado" -> "Validado";
            case "Pendiente" -> "Pendiente de validación";
            case "Rechazado" -> "Rechazado";
            default -> estadoPago;
        };
    }

    public String getEstadoBadgeClass(String estadoPago) {
        if (estadoPago == null) return "status-pendiente";
        
        return switch (estadoPago) {
            case "Validado" -> "status-validado";
            case "Pendiente" -> "status-pendiente";
            case "Rechazado" -> "status-rechazado";
            default -> "status-pendiente";
        };
    }

    public String getMetodoBadgeClass(String metodoPago) {
        if (metodoPago == null) return "";
        
        return switch (metodoPago) {
            case "Efectivo" -> "efectivo";
            case "Transferencia" -> "transferencia";
            case "Tarjeta" -> "tarjeta";
            case "Cheque" -> "cheque";
            default -> "";
        };
    }

    public String getMetodoIcono(String metodoPago) {
        if (metodoPago == null) return "ti ti-credit-card";
        
        return switch (metodoPago) {
            case "Efectivo" -> "ti ti-coin";
            case "Transferencia" -> "ti ti-transfer";
            case "Tarjeta" -> "ti ti-credit-card";
            case "Cheque" -> "ti ti-file";
            default -> "ti ti-credit-card";
        };
    }

    public String formatearMoneda(BigDecimal monto) {
        if (monto == null) return "S/ 0.00";
        return "S/ " + monto.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public String formatearFecha(LocalDateTime fecha) {
        if (fecha == null) return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fecha.format(formatter);
    }

    public String formatearFechaOnly(LocalDate fecha) {
        if (fecha == null) return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return fecha.format(formatter);
    }

    public String getSaldoColor(BigDecimal saldoPendiente) {
        if (saldoPendiente == null || saldoPendiente.compareTo(BigDecimal.ZERO) == 0) {
            return "#10B981";
        }
        return "#EF4444";
    }

    public static class ResumenFinanciero {
        private Integer totalProyectos;
        private BigDecimal totalCotizado;
        private BigDecimal totalPagado;
        private BigDecimal saldoPendiente;
        private Integer porcentajePagado;
        private Long totalPagos;
        private Long pagosPendientes;
        private Long pagosValidados;
        private Long pagosRechazados;
        private Long totalVencidos;
        private String error;

        public Integer getTotalProyectos() { return totalProyectos; }
        public void setTotalProyectos(Integer totalProyectos) { this.totalProyectos = totalProyectos; }
        
        public BigDecimal getTotalCotizado() { return totalCotizado; }
        public void setTotalCotizado(BigDecimal totalCotizado) { this.totalCotizado = totalCotizado; }
        
        public BigDecimal getTotalPagado() { return totalPagado; }
        public void setTotalPagado(BigDecimal totalPagado) { this.totalPagado = totalPagado; }
        
        public BigDecimal getSaldoPendiente() { return saldoPendiente; }
        public void setSaldoPendiente(BigDecimal saldoPendiente) { this.saldoPendiente = saldoPendiente; }
        
        public Integer getPorcentajePagado() { return porcentajePagado; }
        public void setPorcentajePagado(Integer porcentajePagado) { this.porcentajePagado = porcentajePagado; }
        
        public Long getTotalPagos() { return totalPagos; }
        public void setTotalPagos(Long totalPagos) { this.totalPagos = totalPagos; }
        
        public Long getPagosPendientes() { return pagosPendientes; }
        public void setPagosPendientes(Long pagosPendientes) { this.pagosPendientes = pagosPendientes; }
        
        public Long getPagosValidados() { return pagosValidados; }
        public void setPagosValidados(Long pagosValidados) { this.pagosValidados = pagosValidados; }
        
        public Long getPagosRechazados() { return pagosRechazados; }
        public void setPagosRechazados(Long pagosRechazados) { this.pagosRechazados = pagosRechazados; }
        
        public Long getTotalVencidos() { return totalVencidos; }
        public void setTotalVencidos(Long totalVencidos) { this.totalVencidos = totalVencidos; }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }

    public static class SaldoProyecto {
        private Integer idProyecto;
        private String nombreProyecto;
        private BigDecimal montoTotal;
        private BigDecimal totalPagado;
        private BigDecimal saldoPendiente;
        private Integer porcentajePagado;
        private LocalDate fechaVencimiento;
        private Boolean vencido;
        private Integer diasVencidos;
        private String error;

        public Integer getIdProyecto() { return idProyecto; }
        public void setIdProyecto(Integer idProyecto) { this.idProyecto = idProyecto; }
        
        public String getNombreProyecto() { return nombreProyecto; }
        public void setNombreProyecto(String nombreProyecto) { this.nombreProyecto = nombreProyecto; }
        
        public BigDecimal getMontoTotal() { return montoTotal; }
        public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }
        
        public BigDecimal getTotalPagado() { return totalPagado; }
        public void setTotalPagado(BigDecimal totalPagado) { this.totalPagado = totalPagado; }
        
        public BigDecimal getSaldoPendiente() { return saldoPendiente; }
        public void setSaldoPendiente(BigDecimal saldoPendiente) { this.saldoPendiente = saldoPendiente; }
        
        public Integer getPorcentajePagado() { return porcentajePagado; }
        public void setPorcentajePagado(Integer porcentajePagado) { this.porcentajePagado = porcentajePagado; }
        
        public LocalDate getFechaVencimiento() { return fechaVencimiento; }
        public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
        
        public Boolean isVencido() { return vencido; }
        public void setVencido(Boolean vencido) { this.vencido = vencido; }
        
        public Integer getDiasVencidos() { return diasVencidos; }
        public void setDiasVencidos(Integer diasVencidos) { this.diasVencidos = diasVencidos; }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}