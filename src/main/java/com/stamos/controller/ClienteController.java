package com.stamos.controller;

import com.stamos.dto.ClienteDTO;
import com.stamos.entity.Cliente;
import com.stamos.entity.Cotizacion;
import com.stamos.entity.Pago;
import com.stamos.entity.Proyecto;
import com.stamos.service.ClienteService;
import com.stamos.service.CotizacionService;
import com.stamos.service.PagoService;
import com.stamos.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controlador para la gestión de clientes dentro del sistema interno.
 * Solo accesible por usuarios con roles ADMINISTRADOR o COMERCIAL.
 * 
 * Reglas de negocio aplicadas:
 * - No se puede eliminar físicamente un cliente con proyectos o cotizaciones asociadas.
 * - En su lugar, se cambia el estado a "Deshabilitado".
 * - Los datos mínimos: nombre/razón social, teléfono, email.
 * - Cliente puede ser persona natural o empresa.
 */
@Controller
@RequestMapping("/internal/clientes")
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COMERCIAL')")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private CotizacionService cotizacionService;

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private PagoService pagoService;

    /**
     * Lista todos los clientes activos y deshabilitados, con opción de búsqueda.
     * 
     * @param searchTerm Término de búsqueda (nombre, RUC, teléfono) - opcional
     * @param model      Modelo para Thymeleaf
     * @return           Vista de lista de clientes
     */
    @GetMapping("/lista")
    public String listClientes(@RequestParam(required = false) String searchTerm,
                               Model model) {
        List<Cliente> clientes;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            clientes = clienteService.search(searchTerm);
            model.addAttribute("searchTerm", searchTerm);
        } else {
            clientes = clienteService.findAll();
        }
        model.addAttribute("clientes", clientes);
        return "internal/clientes/lista";
    }

    /**
     * Muestra el formulario para crear un nuevo cliente.
     * 
     * @param model Modelo con un cliente vacío
     * @return      Vista del formulario
     */
    @GetMapping("/nuevo")
    public String showCreateForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("action", "crear");
        return "internal/clientes/formulario";
    }

    /**
     * Procesa la creación de un nuevo cliente.
     * Valida campos obligatorios: nombre/razón social, teléfono, email.
     * 
     * @param cliente         Cliente a crear (validado)
     * @param bindingResult   Resultado de validación
     * @param redirectAttrs   Para mensajes flash
     * @return                Redirección a lista o formulario con errores
     */
    @PostMapping("/guardar")
    public String saveCliente(@Valid @ModelAttribute("cliente") Cliente cliente,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            return "internal/clientes/formulario";
        }
        // Verificar si ya existe un cliente con el mismo email o RUC (si aplica)
        if (clienteService.existsByEmail(cliente.getEmail())) {
            bindingResult.rejectValue("email", "error.cliente", "Ya existe un cliente con ese correo electrónico");
            return "internal/clientes/formulario";
        }
        if (cliente.getRuc() != null && !cliente.getRuc().isEmpty() && 
            clienteService.existsByRuc(cliente.getRuc())) {
            bindingResult.rejectValue("ruc", "error.cliente", "Ya existe un cliente con ese RUC");
            return "internal/clientes/formulario";
        }

        cliente.setActivo(true); // Por defecto, activo
        clienteService.save(cliente);
        redirectAttrs.addFlashAttribute("success", "Cliente registrado exitosamente.");
        return "redirect:/internal/clientes/lista";
    }

    /**
     * Muestra el formulario para editar un cliente existente.
     * 
     * @param id    ID del cliente
     * @param model Modelo con el cliente a editar
     * @return      Vista del formulario
     */
    @GetMapping("/editar/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.findById(id);
        if (cliente == null) {
            return "redirect:/internal/clientes/lista?error=notfound";
        }
        model.addAttribute("cliente", cliente);
        model.addAttribute("action", "editar");
        return "internal/clientes/formulario";
    }

    /**
     * Procesa la actualización de un cliente.
     * 
     * @param id              ID del cliente a actualizar
     * @param cliente         Datos actualizados
     * @param bindingResult   Validación
     * @param redirectAttrs   Mensajes flash
     * @return                Redirección a lista o formulario
     */
    @PostMapping("/actualizar/{id}")
    public String updateCliente(@PathVariable Long id,
                                @Valid @ModelAttribute("cliente") Cliente cliente,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            return "internal/clientes/formulario";
        }
        Cliente existing = clienteService.findById(id);
        if (existing == null) {
            redirectAttrs.addFlashAttribute("error", "Cliente no encontrado.");
            return "redirect:/internal/clientes/lista";
        }
        // Validar unicidad de email y RUC excluyendo el propio cliente
        if (!existing.getEmail().equals(cliente.getEmail()) && 
            clienteService.existsByEmail(cliente.getEmail())) {
            bindingResult.rejectValue("email", "error.cliente", "Ya existe otro cliente con ese correo");
            return "internal/clientes/formulario";
        }
        if (cliente.getRuc() != null && !cliente.getRuc().isEmpty() &&
            !existing.getRuc().equals(cliente.getRuc()) && 
            clienteService.existsByRuc(cliente.getRuc())) {
            bindingResult.rejectValue("ruc", "error.cliente", "Ya existe otro cliente con ese RUC");
            return "internal/clientes/formulario";
        }

        existing.setNombreRazonSocial(cliente.getNombreRazonSocial());
        existing.setTipoPersona(cliente.getTipoPersona());
        existing.setTelefono(cliente.getTelefono());
        existing.setEmail(cliente.getEmail());
        existing.setRuc(cliente.getRuc());
        // activo no se modifica aquí (se maneja por separado)
        clienteService.save(existing);
        redirectAttrs.addFlashAttribute("success", "Cliente actualizado correctamente.");
        return "redirect:/internal/clientes/lista";
    }

    /**
     * Muestra el detalle completo de un cliente: historial de cotizaciones,
     * proyectos y pagos.
     * 
     * @param id    ID del cliente
     * @param model Modelo con cliente, cotizaciones, proyectos, pagos
     * @return      Vista de detalle
     */
    @GetMapping("/detalle/{id}")
    public String viewClientDetail(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.findById(id);
        if (cliente == null) {
            return "redirect:/internal/clientes/lista?error=notfound";
        }
        List<Cotizacion> cotizaciones = cotizacionService.findByClienteId(id);
        List<Proyecto> proyectos = proyectoService.findByClienteId(id);
        List<Pago> pagos = pagoService.findByClienteId(id);
        
        model.addAttribute("cliente", cliente);
        model.addAttribute("cotizaciones", cotizaciones);
        model.addAttribute("proyectos", proyectos);
        model.addAttribute("pagos", pagos);
        return "internal/clientes/detalle";
    }

    /**
     * Deshabilita un cliente (no eliminación física).
     * Solo permitido si el cliente no tiene proyectos o cotizaciones asociados.
     * Si los tiene, se deniega la operación y se muestra un error.
     * 
     * @param id            ID del cliente
     * @param redirectAttrs Mensajes flash
     * @return              Redirección a lista
     */
    @PostMapping("/deshabilitar/{id}")
    public String disableCliente(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        Cliente cliente = clienteService.findById(id);
        if (cliente == null) {
            redirectAttrs.addFlashAttribute("error", "Cliente no encontrado.");
            return "redirect:/internal/clientes/lista";
        }
        
        // Verificar si tiene proyectos o cotizaciones asociadas
        boolean hasCotizaciones = cotizacionService.existsByClienteId(id);
        boolean hasProyectos = proyectoService.existsByClienteId(id);
        
        if (hasCotizaciones || hasProyectos) {
            redirectAttrs.addFlashAttribute("error", 
                "No se puede deshabilitar el cliente porque tiene cotizaciones o proyectos asociados.");
            return "redirect:/internal/clientes/detalle/" + id;
        }
        
        cliente.setActivo(false);
        clienteService.save(cliente);
        redirectAttrs.addFlashAttribute("success", "Cliente deshabilitado correctamente.");
        return "redirect:/internal/clientes/lista";
    }

    /**
     * Habilita un cliente previamente deshabilitado.
     * 
     * @param id            ID del cliente
     * @param redirectAttrs Mensajes flash
     * @return              Redirección a lista
     */
    @PostMapping("/habilitar/{id}")
    public String enableCliente(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        Cliente cliente = clienteService.findById(id);
        if (cliente == null) {
            redirectAttrs.addFlashAttribute("error", "Cliente no encontrado.");
            return "redirect:/internal/clientes/lista";
        }
        cliente.setActivo(true);
        clienteService.save(cliente);
        redirectAttrs.addFlashAttribute("success", "Cliente habilitado correctamente.");
        return "redirect:/internal/clientes/lista";
    }

    /**
     * Endpoint AJAX para búsqueda rápida de clientes (autocomplete).
     * Devuelve una lista de clientes en formato JSON.
     * 
     * @param term Término de búsqueda
     * @return     Lista de DTOs con id, nombre, RUC
     */
    @GetMapping("/buscar")
    @ResponseBody
    public List<ClienteDTO> searchClientesJson(@RequestParam("term") String term) {
        return clienteService.searchDTO(term);
    }
}