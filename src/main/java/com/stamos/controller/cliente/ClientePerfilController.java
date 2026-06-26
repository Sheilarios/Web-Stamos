package com.stamos.controller.cliente;

import com.stamos.controller.BaseController;
import com.stamos.model.Cliente;
import com.stamos.model.Usuario;
import com.stamos.service.ClienteService;
import com.stamos.service.PagoService;
import com.stamos.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/cliente")
public class ClientePerfilController extends BaseController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private PagoService pagoService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/perfil")
    public String verPerfil(Model model, Authentication auth) {
        try {
            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado");
            }

            Cliente cliente = clienteService.findByUsuario(usuario);
            if (cliente == null) {
                throw new RuntimeException("Cliente no encontrado");
            }

            List<com.stamos.model.Proyecto> proyectos = proyectoService.findByCliente(cliente.getIdCliente());
            
            long proyectosActivos = proyectos.stream()
                    .filter(p -> p.getEstado() != null && 
                               !p.getEstado().equals("Entregado") && 
                               !p.getEstado().equals("Anulado"))
                    .count();

            long proyectosCompletados = proyectos.stream()
                    .filter(p -> p.getEstado() != null && p.getEstado().equals("Entregado"))
                    .count();

            long proyectosAnulados = proyectos.stream()
                    .filter(p -> p.getEstado() != null && p.getEstado().equals("Anulado"))
                    .count();

            BigDecimal totalCotizado = proyectos.stream()
                    .map(com.stamos.model.Proyecto::getMontoTotal)
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

            LocalDateTime ultimoAcceso = clienteService.getUltimoAcceso(cliente.getIdCliente());

            model.addAttribute("cliente", cliente);
            model.addAttribute("totalProyectos", proyectos.size());
            model.addAttribute("proyectosActivos", proyectosActivos);
            model.addAttribute("proyectosCompletados", proyectosCompletados);
            model.addAttribute("proyectosAnulados", proyectosAnulados);
            model.addAttribute("totalCotizado", totalCotizado);
            model.addAttribute("totalPagado", totalPagado);
            model.addAttribute("saldoPendiente", saldoPendiente);
            model.addAttribute("ultimoAcceso", ultimoAcceso);
            addCommonAttributes(model);

            return "cliente/perfil";

        } catch (RuntimeException e) {
            model.addAttribute("error", "Error al cargar el perfil: " + e.getMessage());
            addCommonAttributes(model);
            return "cliente/dashboard";
        }
    }

    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@Valid @ModelAttribute("cliente") Cliente cliente,
                                   BindingResult result,
                                   Authentication auth,
                                   RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("mensajeError", 
                    "Por favor, corrige los errores en el formulario.");
                return "redirect:/cliente/perfil";
            }

            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado");
            }

            Cliente clienteActual = clienteService.findByUsuario(usuario);
            if (clienteActual == null) {
                throw new RuntimeException("Cliente no encontrado");
            }

            if (!clienteActual.getIdCliente().equals(cliente.getIdCliente())) {
                throw new AccessDeniedException("No tienes permiso para modificar este perfil");
            }

            Cliente clienteExistente = clienteService.findByEmail(cliente.getEmail());
            if (clienteExistente != null && !clienteExistente.getIdCliente().equals(cliente.getIdCliente())) {
                redirectAttributes.addFlashAttribute("mensajeError", 
                    "El correo electrónico ya está registrado por otro usuario.");
                return "redirect:/cliente/perfil";
            }

            if (cliente.getRuc() != null && !cliente.getRuc().isEmpty()) {
                Cliente clienteRucExistente = clienteService.findByRuc(cliente.getRuc());
                if (clienteRucExistente != null && !clienteRucExistente.getIdCliente().equals(cliente.getIdCliente())) {
                    redirectAttributes.addFlashAttribute("mensajeError", 
                        "El RUC ya está registrado por otro cliente.");
                    return "redirect:/cliente/perfil";
                }
            }

            clienteActual.setNombreRazonSocial(cliente.getNombreRazonSocial());
            clienteActual.setRuc(cliente.getRuc());
            clienteActual.setTelefono(cliente.getTelefono());
            clienteActual.setEmail(cliente.getEmail());
            clienteActual.setDireccion(cliente.getDireccion());

            clienteService.actualizar(clienteActual);

            Usuario usuarioActual = clienteService.findUsuarioByEmail(auth.getName());
            if (usuarioActual != null && !usuarioActual.getEmail().equals(cliente.getEmail())) {
                usuarioActual.setEmail(cliente.getEmail());
            }

            redirectAttributes.addFlashAttribute("mensajeExito", 
                "Tu perfil ha sido actualizado correctamente.");

            return "redirect:/cliente/perfil";

        } catch (AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("mensajeError", 
                "No tienes permiso para realizar esta acción.");
            return "redirect:/cliente/perfil";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("mensajeError", 
                "Error al actualizar el perfil: " + e.getMessage());
            return "redirect:/cliente/perfil";
        }
    }

    @PostMapping("/perfil/cambiar-password")
    public String cambiarPassword(@RequestParam("idCliente") Integer idCliente,
                                  @RequestParam("passwordActual") String passwordActual,
                                  @RequestParam("passwordNueva") String passwordNueva,
                                  @RequestParam("passwordConfirmar") String confirmPassword,
                                  Authentication auth,
                                  RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado");
            }

            Cliente cliente = clienteService.findByUsuario(usuario);
            if (cliente == null) {
                throw new RuntimeException("Cliente no encontrado");
            }

            if (!cliente.getIdCliente().equals(idCliente)) {
                throw new AccessDeniedException("No tienes permiso para cambiar la contraseña de este perfil");
            }

            if (passwordActual == null || passwordActual.isEmpty()) {
                redirectAttributes.addFlashAttribute("mensajeError", 
                    "Debes ingresar tu contraseña actual.");
                return "redirect:/cliente/perfil";
            }

            if (passwordNueva == null || passwordNueva.isEmpty()) {
                redirectAttributes.addFlashAttribute("mensajeError", 
                    "Debes ingresar una nueva contraseña.");
                return "redirect:/cliente/perfil";
            }

            if (passwordNueva.length() < 6) {
                redirectAttributes.addFlashAttribute("mensajeError", 
                    "La nueva contraseña debe tener al menos 6 caracteres.");
                return "redirect:/cliente/perfil";
            }

            if (!passwordNueva.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("mensajeError", 
                    "Las contraseñas no coinciden.");
                return "redirect:/cliente/perfil";
            }

            Usuario usuarioActual = clienteService.findUsuarioByEmail(auth.getName());
            if (usuarioActual == null) {
                throw new RuntimeException("Usuario no encontrado");
            }

            if (!passwordEncoder.matches(passwordActual, usuarioActual.getPasswordHash())) {
                redirectAttributes.addFlashAttribute("mensajeError", 
                    "La contraseña actual es incorrecta.");
                return "redirect:/cliente/perfil";
            }

            clienteService.cambiarPassword(cliente.getIdUsuario(), passwordNueva);

            redirectAttributes.addFlashAttribute("mensajeExito", 
                "Tu contraseña ha sido cambiada correctamente.");

            return "redirect:/cliente/perfil";

        } catch (AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("mensajeError", 
                "No tienes permiso para realizar esta acción.");
            return "redirect:/cliente/perfil";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("mensajeError", 
                "Error al cambiar la contraseña: " + e.getMessage());
            return "redirect:/cliente/perfil";
        }
    }

    @GetMapping("/perfil/validar-email")
    @ResponseBody
    public boolean validarEmail(@RequestParam("email") String email, Authentication auth) {
        try {
            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                return false;
            }

            Cliente cliente = clienteService.findByUsuario(usuario);
            if (cliente == null) {
                return false;
            }

            Cliente clienteExistente = clienteService.findByEmail(email);
            if (clienteExistente == null) {
                return true; 
            }

            return clienteExistente.getIdCliente().equals(cliente.getIdCliente());

        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/perfil/validar-ruc")
    @ResponseBody
    public boolean validarRuc(@RequestParam("ruc") String ruc, Authentication auth) {
        try {
            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                return false;
            }

            Cliente cliente = clienteService.findByUsuario(usuario);
            if (cliente == null) {
                return false;
            }

            if (ruc == null || ruc.isEmpty()) {
                return true;
            }

            Cliente clienteExistente = clienteService.findByRuc(ruc);
            if (clienteExistente == null) {
                return true;
            }

            return clienteExistente.getIdCliente().equals(cliente.getIdCliente());

        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/perfil/estadisticas")
    @ResponseBody
    public EstadisticasCliente getEstadisticas(Authentication auth) {
        try {
            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado");
            }

            Cliente cliente = clienteService.findByUsuario(usuario);
            if (cliente == null) {
                throw new RuntimeException("Cliente no encontrado");
            }

            List<com.stamos.model.Proyecto> proyectos = proyectoService.findByCliente(cliente.getIdCliente());
            
            long proyectosActivos = proyectos.stream()
                    .filter(p -> p.getEstado() != null && 
                               !p.getEstado().equals("Entregado") && 
                               !p.getEstado().equals("Anulado"))
                    .count();

            long proyectosCompletados = proyectos.stream()
                    .filter(p -> p.getEstado() != null && p.getEstado().equals("Entregado"))
                    .count();

            long proyectosAnulados = proyectos.stream()
                    .filter(p -> p.getEstado() != null && p.getEstado().equals("Anulado"))
                    .count();

            BigDecimal totalCotizado = proyectos.stream()
                    .map(com.stamos.model.Proyecto::getMontoTotal)
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

            LocalDateTime ultimoAcceso = clienteService.getUltimoAcceso(cliente.getIdCliente());

            EstadisticasCliente estadisticas = new EstadisticasCliente();
            estadisticas.setIdCliente(cliente.getIdCliente());
            estadisticas.setNombre(cliente.getNombreRazonSocial());
            estadisticas.setEmail(cliente.getEmail());
            estadisticas.setTelefono(cliente.getTelefono());
            estadisticas.setEstado(cliente.getEstado());
            estadisticas.setFechaRegistro(cliente.getFechaRegistro());
            estadisticas.setTotalProyectos(proyectos.size());
            estadisticas.setProyectosActivos(proyectosActivos);
            estadisticas.setProyectosCompletados(proyectosCompletados);
            estadisticas.setProyectosAnulados(proyectosAnulados);
            estadisticas.setTotalCotizado(totalCotizado);
            estadisticas.setTotalPagado(totalPagado);
            estadisticas.setSaldoPendiente(saldoPendiente);
            estadisticas.setUltimoAcceso(ultimoAcceso);

            return estadisticas;

        } catch (RuntimeException e) {
            EstadisticasCliente estadisticas = new EstadisticasCliente();
            estadisticas.setError(e.getMessage());
            return estadisticas;
        }
    }

    public String getEstadoLegible(String estado) {
        if (estado == null) return "Sin estado";
        return switch (estado) {
            case "Activo" -> "Activo";
            case "Deshabilitado" -> "Deshabilitado";
            default -> estado;
        };
    }

    public String getEstadoBadgeClass(String estado) {
        if (estado == null) return "status-pendiente";
        return switch (estado) {
            case "Activo" -> "status-validado";
            case "Deshabilitado" -> "status-rechazado";
            default -> "status-pendiente";
        };
    }

    public String formatearTelefono(String telefono) {
        if (telefono == null || telefono.isEmpty()) {
            return "No registrado";
        }
        return telefono;
    }

    public String formatearMoneda(BigDecimal monto) {
        if (monto == null) return "S/ 0.00";
        return "S/ " + monto.setScale(2, java.math.RoundingMode.HALF_UP).toString();
    }

    public String formatearFecha(LocalDateTime fecha) {
        if (fecha == null) return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fecha.format(formatter);
    }

    public String truncarTexto(String texto, int maxLength) {
        if (texto == null) return "";
        if (texto.length() <= maxLength) return texto;
        return texto.substring(0, maxLength) + "...";
    }

    public String getSaldoColor(BigDecimal saldoPendiente) {
        if (saldoPendiente == null || saldoPendiente.compareTo(BigDecimal.ZERO) == 0) {
            return "#10B981";
        }
        return "#EF4444";
    }

    public String getSaldoMensaje(BigDecimal saldoPendiente) {
        if (saldoPendiente == null || saldoPendiente.compareTo(BigDecimal.ZERO) == 0) {
            return "¡Todo pagado!";
        }
        return "Pendiente de pago";
    }

    public String getTipoPersonaLegible(String tipoPersona) {
        if (tipoPersona == null) return "No especificado";
        return switch (tipoPersona) {
            case "Natural" -> "Persona Natural";
            case "Juridica" -> "Persona Jurídica";
            default -> tipoPersona;
        };
    }

    public static class EstadisticasCliente {
        private Integer idCliente;
        private String nombre;
        private String email;
        private String telefono;
        private String estado;
        private LocalDateTime fechaRegistro;
        private Integer totalProyectos;
        private Long proyectosActivos;
        private Long proyectosCompletados;
        private Long proyectosAnulados;
        private BigDecimal totalCotizado;
        private BigDecimal totalPagado;
        private BigDecimal saldoPendiente;
        private LocalDateTime ultimoAcceso;
        private String error;

        public Integer getIdCliente() { return idCliente; }
        public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }
        
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getTelefono() { return telefono; }
        public void setTelefono(String telefono) { this.telefono = telefono; }
        
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
        
        public LocalDateTime getFechaRegistro() { return fechaRegistro; }
        public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
        
        public Integer getTotalProyectos() { return totalProyectos; }
        public void setTotalProyectos(Integer totalProyectos) { this.totalProyectos = totalProyectos; }
        
        public Long getProyectosActivos() { return proyectosActivos; }
        public void setProyectosActivos(Long proyectosActivos) { this.proyectosActivos = proyectosActivos; }
        
        public Long getProyectosCompletados() { return proyectosCompletados; }
        public void setProyectosCompletados(Long proyectosCompletados) { this.proyectosCompletados = proyectosCompletados; }
        
        public Long getProyectosAnulados() { return proyectosAnulados; }
        public void setProyectosAnulados(Long proyectosAnulados) { this.proyectosAnulados = proyectosAnulados; }
        
        public BigDecimal getTotalCotizado() { return totalCotizado; }
        public void setTotalCotizado(BigDecimal totalCotizado) { this.totalCotizado = totalCotizado; }
        
        public BigDecimal getTotalPagado() { return totalPagado; }
        public void setTotalPagado(BigDecimal totalPagado) { this.totalPagado = totalPagado; }
        
        public BigDecimal getSaldoPendiente() { return saldoPendiente; }
        public void setSaldoPendiente(BigDecimal saldoPendiente) { this.saldoPendiente = saldoPendiente; }
        
        public LocalDateTime getUltimoAcceso() { return ultimoAcceso; }
        public void setUltimoAcceso(LocalDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}