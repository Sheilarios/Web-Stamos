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

@Controller
@RequestMapping("/clientes")
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

    @GetMapping
    public String listClientes(@RequestParam(required = false) String search,
                               @RequestParam(required = false) boolean activos,
                               Model model) {
        List<Cliente> clientes;
        
        if (search != null && !search.trim().isEmpty()) {
            clientes = clienteService.search(search);
            model.addAttribute("search", search);
        } else if (activos) {
            clientes = clienteService.findActive();
            model.addAttribute("activos", true);
        } else {
            clientes = clienteService.findAll();
        }
        
        model.addAttribute("clientes", clientes);
        model.addAttribute("total", clientes.size());
        return "internal/clientes/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoCliente(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("tiposPersona", List.of("PERSONA_NATURAL", "EMPRESA"));
        return "internal/clientes/formulario";
    }

    @PostMapping
    public String crearCliente(@Valid @ModelAttribute("cliente") Cliente cliente,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirect) {
        if ("EMPRESA".equals(cliente.getTipoPersona()) && 
            (cliente.getRuc() == null || cliente.getRuc().trim().isEmpty())) {
            result.rejectValue("ruc", "error.cliente", "El RUC es obligatorio para empresas");
        }
        
        if (clienteService.existsByEmail(cliente.getEmail())) {
            result.rejectValue("email", "error.cliente", "Ya existe un cliente con ese correo electrónico");
        }
        
        if (cliente.getRuc() != null && !cliente.getRuc().trim().isEmpty() && 
            clienteService.existsByRuc(cliente.getRuc())) {
            result.rejectValue("ruc", "error.cliente", "Ya existe un cliente con ese RUC");
        }
        
        if (result.hasErrors()) {
            model.addAttribute("tiposPersona", List.of("PERSONA_NATURAL", "EMPRESA"));
            return "internal/clientes/formulario";
        }

        cliente.setActivo(true);
        clienteService.save(cliente);
        redirect.addFlashAttribute("success", "Cliente registrado exitosamente.");
        return "redirect:/clientes";
    }

    @GetMapping("/{id}/editar")
    public String editarCliente(@PathVariable Long id, 
                                Model model, 
                                RedirectAttributes redirect) {
        Cliente cliente = clienteService.findById(id);
        if (cliente == null) {
            redirect.addFlashAttribute("error", "Cliente no encontrado.");
            return "redirect:/clientes";
        }
        
        model.addAttribute("cliente", cliente);
        model.addAttribute("tiposPersona", List.of("PERSONA_NATURAL", "EMPRESA"));
        return "internal/clientes/formulario";  
    }

    @PutMapping("/{id}")
    public String actualizarCliente(@PathVariable Long id,
                                    @Valid @ModelAttribute("cliente") Cliente cliente,
                                    BindingResult result,
                                    Model model,
                                    RedirectAttributes redirect) {
        Cliente existing = clienteService.findById(id);
        if (existing == null) {
            redirect.addFlashAttribute("error", "Cliente no encontrado.");
            return "redirect:/clientes";
        }
        
        if (!existing.getEmail().equals(cliente.getEmail()) && 
            clienteService.existsByEmail(cliente.getEmail())) {
            result.rejectValue("email", "error.cliente", "Ya existe otro cliente con ese correo");
        }
        
        if (cliente.getRuc() != null && !cliente.getRuc().trim().isEmpty() &&
            (existing.getRuc() == null || !existing.getRuc().equals(cliente.getRuc())) && 
            clienteService.existsByRuc(cliente.getRuc())) {
            result.rejectValue("ruc", "error.cliente", "Ya existe otro cliente con ese RUC");
        }
        
        if (result.hasErrors()) {
            model.addAttribute("tiposPersona", List.of("PERSONA_NATURAL", "EMPRESA"));
            return "internal/clientes/formulario";  
        }

        existing.setNombreRazonSocial(cliente.getNombreRazonSocial());
        existing.setTipoPersona(cliente.getTipoPersona());
        existing.setTelefono(cliente.getTelefono());
        existing.setEmail(cliente.getEmail());
        existing.setRuc(cliente.getRuc());
        existing.setDireccion(cliente.getDireccion());
        existing.setCiudad(cliente.getCiudad());
        existing.setDepartamento(cliente.getDepartamento());
        existing.setNombreContacto(cliente.getNombreContacto());
        
        clienteService.save(existing);
        redirect.addFlashAttribute("success", "Cliente actualizado correctamente.");
        return "redirect:/clientes";
    }

    @GetMapping("/{id}")
    public String verCliente(@PathVariable Long id, 
                             Model model, 
                             RedirectAttributes redirect) {
        Cliente cliente = clienteService.findById(id);
        if (cliente == null) {
            redirect.addFlashAttribute("error", "Cliente no encontrado.");
            return "redirect:/clientes";
        }
        
        List<Cotizacion> cotizaciones = cotizacionService.findByClienteIdOrderByFechaEmisionDesc(id);
        List<Proyecto> proyectos = proyectoService.findByClienteId(id);
        List<Pago> pagos = pagoService.findByClienteId(id);
        
        model.addAttribute("cliente", cliente);
        model.addAttribute("cotizaciones", cotizaciones);
        model.addAttribute("proyectos", proyectos);
        model.addAttribute("pagos", pagos);
        model.addAttribute("totalCotizaciones", cotizaciones.size());
        model.addAttribute("totalProyectos", proyectos.size());
        model.addAttribute("totalPagos", pagos.size());
        
        return "internal/clientes/detalle"; 
    }

    @DeleteMapping("/{id}/deshabilitar")
    public String deshabilitarCliente(@PathVariable Long id, RedirectAttributes redirect) {
        Cliente cliente = clienteService.findById(id);
        if (cliente == null) {
            redirect.addFlashAttribute("error", "Cliente no encontrado.");
            return "redirect:/clientes";
        }
        
        if (!cliente.isActivo()) {
            redirect.addFlashAttribute("warning", "El cliente ya está deshabilitado.");
            return "redirect:/clientes";
        }
        
        boolean tieneCotizaciones = cotizacionService.existsByClienteId(id);
        boolean tieneProyectos = proyectoService.existsByClienteId(id);
        
        if (tieneCotizaciones || tieneProyectos) {
            redirect.addFlashAttribute("error", 
                "No se puede deshabilitar el cliente porque tiene " + 
                (tieneCotizaciones ? "cotizaciones " : "") + 
                (tieneProyectos ? "proyectos " : "") + 
                "asociados.");
            return "redirect:/clientes/" + id;
        }
        
        cliente.setActivo(false);
        clienteService.save(cliente);
        redirect.addFlashAttribute("success", "Cliente deshabilitado correctamente.");
        return "redirect:/clientes";
    }

    @PutMapping("/{id}/habilitar")
    public String habilitarCliente(@PathVariable Long id, RedirectAttributes redirect) {
        Cliente cliente = clienteService.findById(id);
        if (cliente == null) {
            redirect.addFlashAttribute("error", "Cliente no encontrado.");
            return "redirect:/clientes";
        }
        
        if (cliente.isActivo()) {
            redirect.addFlashAttribute("warning", "El cliente ya está habilitado.");
            return "redirect:/clientes";
        }
        
        cliente.setActivo(true);
        clienteService.save(cliente);
        redirect.addFlashAttribute("success", "Cliente habilitado correctamente.");
        return "redirect:/clientes";
    }

    @GetMapping("/buscar")
    @ResponseBody
    public List<ClienteDTO> buscarClientes(@RequestParam("q") String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return clienteService.searchDTO(query);
    }
}