package com.stamos.controller.admin;

import com.stamos.annotation.Auditable;
import com.stamos.model.Cliente;
import com.stamos.model.Usuario;
import com.stamos.model.Proyecto;
import com.stamos.service.ClienteService;
import com.stamos.service.PagoService;
import com.stamos.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin/clientes")
@PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private PagoService pagoService;

    @GetMapping
    public String listarClientes(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) String search,
                                 @RequestParam(required = false) String estado,
                                 Model model) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("nombreRazonSocial").ascending());
            
            Page<Cliente> clientesPage;
            if (search != null && !search.isEmpty()) {
                clientesPage = clienteService.buscarPorNombreORuc(search, pageable);
            } else if (estado != null && !estado.isEmpty()) {
                clientesPage = clienteService.findByEstado(estado, pageable);
            } else {
                clientesPage = clienteService.findAll(pageable);
            }
            
            model.addAttribute("clientes", clientesPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", clientesPage.getTotalPages());
            model.addAttribute("totalItems", clientesPage.getTotalElements());
            model.addAttribute("search", search);
            model.addAttribute("estado", estado);
            
            return "admin/clientes/lista";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar los clientes: " + e.getMessage());
            return "admin/clientes/lista";
        }
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("titulo", "Nuevo Cliente");
        model.addAttribute("accion", "crear");
        return "admin/clientes/formulario";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        try {
            Cliente cliente = clienteService.findById(id);
            if (cliente == null) {
                model.addAttribute("error", "Cliente no encontrado");
                return "redirect:/admin/clientes";
            }
            
            model.addAttribute("cliente", cliente);
            model.addAttribute("titulo", "Editar Cliente");
            model.addAttribute("accion", "editar");
            return "admin/clientes/formulario";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el cliente: " + e.getMessage());
            return "redirect:/admin/clientes";
        }
    }

    @PostMapping("/crear")
    public String crearCliente(@Valid @ModelAttribute("cliente") Cliente cliente,
                               BindingResult result,
                               @RequestParam("password") String password,
                               @RequestParam("confirmPassword") String confirmPassword,
                               Authentication auth,
                               RedirectAttributes redirectAttributes) {
        try {
            // Validar errores del formulario
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("error", "Por favor, corrige los errores en el formulario.");
                return "redirect:/admin/clientes/nuevo";
            }

            // Validar contraseña
            if (password == null || password.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "La contraseña es obligatoria.");
                return "redirect:/admin/clientes/nuevo";
            }
            
            if (password.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres.");
                return "redirect:/admin/clientes/nuevo";
            }
            
            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden.");
                return "redirect:/admin/clientes/nuevo";
            }

            // Validar email único
            Cliente clienteExistente = clienteService.findByEmail(cliente.getEmail());
            if (clienteExistente != null) {
                redirectAttributes.addFlashAttribute("error", "Ya existe un cliente con este correo electrónico.");
                return "redirect:/admin/clientes/nuevo";
            }

            // Validar RUC único
            if (cliente.getRuc() != null && !cliente.getRuc().isEmpty()) {
                Cliente clienteRucExistente = clienteService.findByRuc(cliente.getRuc());
                if (clienteRucExistente != null) {
                    redirectAttributes.addFlashAttribute("error", "Ya existe un cliente con este RUC.");
                    return "redirect:/admin/clientes/nuevo";
                }
            }

            // Validar teléfono único
            if (cliente.getTelefono() != null && !cliente.getTelefono().isEmpty()) {
                Cliente clienteTelefonoExistente = clienteService.findByTelefono(cliente.getTelefono());
                if (clienteTelefonoExistente != null) {
                    redirectAttributes.addFlashAttribute("error", "Ya existe un cliente con este teléfono.");
                    return "redirect:/admin/clientes/nuevo";
                }
            }

            // Obtener usuario administrador
            Usuario usuarioAdmin = clienteService.findUsuarioByEmail(auth.getName());
            if (usuarioAdmin != null) {
                cliente.setIdUsuarioRegis(usuarioAdmin.getIdUsuario());
            }

            // Registrar cliente
            clienteService.registrar(cliente, password);
            
            redirectAttributes.addFlashAttribute("mensaje", "Cliente creado exitosamente.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/admin/clientes";
            
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/clientes/nuevo";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el cliente: " + e.getMessage());
            return "redirect:/admin/clientes/nuevo";
        }
    }

    @PostMapping("/actualizar/{id}")
    @Auditable(value = "Actualización de cliente", accion = "UPDATE")
    public String actualizarCliente(@PathVariable Integer id,
                                    @Valid @ModelAttribute("cliente") Cliente cliente,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("error", "Por favor, corrige los errores en el formulario.");
                return "redirect:/admin/clientes/editar/" + id;
            }

            Cliente clienteExistente = clienteService.findById(id);
            if (clienteExistente == null) {
                redirectAttributes.addFlashAttribute("error", "Cliente no encontrado.");
                return "redirect:/admin/clientes";
            }

            // Validar email único
            Cliente clienteEmailExistente = clienteService.findByEmail(cliente.getEmail());
            if (clienteEmailExistente != null && !clienteEmailExistente.getIdCliente().equals(id)) {
                redirectAttributes.addFlashAttribute("error", "Ya existe otro cliente con este correo electrónico.");
                return "redirect:/admin/clientes/editar/" + id;
            }

            // Validar RUC único
            if (cliente.getRuc() != null && !cliente.getRuc().isEmpty()) {
                Cliente clienteRucExistente = clienteService.findByRuc(cliente.getRuc());
                if (clienteRucExistente != null && !clienteRucExistente.getIdCliente().equals(id)) {
                    redirectAttributes.addFlashAttribute("error", "Ya existe otro cliente con este RUC.");
                    return "redirect:/admin/clientes/editar/" + id;
                }
            }

            // Validar teléfono único
            if (cliente.getTelefono() != null && !cliente.getTelefono().isEmpty()) {
                Cliente clienteTelefonoExistente = clienteService.findByTelefono(cliente.getTelefono());
                if (clienteTelefonoExistente != null && !clienteTelefonoExistente.getIdCliente().equals(id)) {
                    redirectAttributes.addFlashAttribute("error", "Ya existe otro cliente con este teléfono.");
                    return "redirect:/admin/clientes/editar/" + id;
                }
            }

            cliente.setIdCliente(id);
            clienteService.actualizar(cliente);
            
            redirectAttributes.addFlashAttribute("mensaje", "Cliente actualizado exitosamente.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/admin/clientes";
            
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/clientes/editar/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el cliente: " + e.getMessage());
            return "redirect:/admin/clientes/editar/" + id;
        }
    }

    @GetMapping("/historial/{id}")
    public String verHistorial(@PathVariable Integer id, Model model) {
        try {
            Cliente cliente = clienteService.findById(id);
            if (cliente == null) {
                model.addAttribute("error", "Cliente no encontrado");
                return "redirect:/admin/clientes";
            }
            
            List<Proyecto> proyectos = proyectoService.findByCliente(id);
            
            model.addAttribute("cliente", cliente);
            model.addAttribute("proyectos", proyectos);
            
            return "admin/clientes/historial";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el historial: " + e.getMessage());
            return "redirect:/admin/clientes";
        }
    }

    @PostMapping("/deshabilitar/{id}")
    @Auditable(value = "Deshabilitación de cliente", accion = "DISABLE")
    @PreAuthorize("hasRole('ADMIN')")
    public String deshabilitarCliente(@PathVariable Integer id,
                                      Authentication auth,
                                      RedirectAttributes redirectAttributes) {
        try {
            Cliente cliente = clienteService.findById(id);
            if (cliente == null) {
                redirectAttributes.addFlashAttribute("error", "Cliente no encontrado.");
                return "redirect:/admin/clientes";
            }
            
            if (!"Activo".equals(cliente.getEstado())) {
                redirectAttributes.addFlashAttribute("error", "El cliente ya está deshabilitado.");
                return "redirect:/admin/clientes";
            }
            
            Usuario usuarioAdmin = clienteService.findUsuarioByEmail(auth.getName());
            if (usuarioAdmin == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario administrador no encontrado.");
                return "redirect:/admin/clientes";
            }
            
            clienteService.deshabilitar(id, usuarioAdmin.getIdUsuario());
            
            redirectAttributes.addFlashAttribute("mensaje", "Cliente deshabilitado exitosamente.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/admin/clientes";
            
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/clientes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al deshabilitar el cliente: " + e.getMessage());
            return "redirect:/admin/clientes";
        }
    }

    @PostMapping("/habilitar/{id}")
    @Auditable(value = "Habilitación de cliente", accion = "ENABLE")
    @PreAuthorize("hasRole('ADMIN')")
    public String habilitarCliente(@PathVariable Integer id,
                                   Authentication auth,
                                   RedirectAttributes redirectAttributes) {
        try {
            Cliente cliente = clienteService.findById(id);
            if (cliente == null) {
                redirectAttributes.addFlashAttribute("error", "Cliente no encontrado.");
                return "redirect:/admin/clientes";
            }
            
            if ("Activo".equals(cliente.getEstado())) {
                redirectAttributes.addFlashAttribute("error", "El cliente ya está activo.");
                return "redirect:/admin/clientes";
            }
            
            Usuario usuarioAdmin = clienteService.findUsuarioByEmail(auth.getName());
            if (usuarioAdmin == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario administrador no encontrado.");
                return "redirect:/admin/clientes";
            }
            
            clienteService.habilitar(id, usuarioAdmin.getIdUsuario());
            
            redirectAttributes.addFlashAttribute("mensaje", "Cliente habilitado exitosamente.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/admin/clientes";
            
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/clientes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al habilitar el cliente: " + e.getMessage());
            return "redirect:/admin/clientes";
        }
    }

    @GetMapping("/buscar")
    @ResponseBody
    public List<Cliente> buscarClientes(@RequestParam("q") String query) {
        try {
            if (query == null || query.length() < 2) {
                return List.of();
            }
            return clienteService.buscarPorNombreORuc(query);
        } catch (Exception e) {
            return List.of();
        }
    }

    @GetMapping("/verificar-email")
    @ResponseBody
    public boolean verificarEmail(@RequestParam("email") String email,
                                  @RequestParam(value = "id", required = false) Integer id) {
        try {
            Cliente cliente = clienteService.findByEmail(email);
            if (cliente == null) {
                return true;
            }
            if (id != null && cliente.getIdCliente().equals(id)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/verificar-ruc")
    @ResponseBody
    public boolean verificarRuc(@RequestParam("ruc") String ruc,
                                @RequestParam(value = "id", required = false) Integer id) {
        try {
            if (ruc == null || ruc.isEmpty()) {
                return true;
            }
            Cliente cliente = clienteService.findByRuc(ruc);
            if (cliente == null) {
                return true;
            }
            if (id != null && cliente.getIdCliente().equals(id)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/verificar-telefono")
    @ResponseBody
    public boolean verificarTelefono(@RequestParam("telefono") String telefono,
                                     @RequestParam(value = "id", required = false) Integer id) {
        try {
            if (telefono == null || telefono.isEmpty()) {
                return true;
            }
            Cliente cliente = clienteService.findByTelefono(telefono);
            if (cliente == null) {
                return true;
            }
            if (id != null && cliente.getIdCliente().equals(id)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}