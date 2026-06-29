package com.stamos.controller.admin;

import com.stamos.controller.BaseController;
import com.stamos.model.Rol;
import com.stamos.model.Usuario;
import com.stamos.service.RolService;
import com.stamos.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/admin/usuarios")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class UsuarioController extends BaseController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolService rolService;

    @GetMapping
    public String listarUsuarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer rol,
            @RequestParam(required = false) Boolean estado,
            Model model) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("nombreCompleto").ascending());
            Page<Usuario> usuariosPage;

            if (search != null && !search.isEmpty()) {
                usuariosPage = usuarioService.search(search, pageable);
            } else if (rol != null) {
                usuariosPage = usuarioService.findByRol(rol, pageable);
            } else if (estado != null) {
                usuariosPage = usuarioService.findByActivo(estado, pageable);
            } else {
                usuariosPage = usuarioService.findAll(pageable);
            }

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null ? auth.getName() : "Administrador";
            String rolNombre = "Administrador";
            
            Usuario usuarioActual = usuarioService.findByEmail(username);
            if (usuarioActual != null) {
                rolNombre = usuarioActual.getNombreRol() != null ? usuarioActual.getNombreRol() : "Administrador";
            }

            long totalActivos = usuarioService.countActivos();
            long totalInactivos = usuarioService.countInactivos();

            model.addAttribute("usuarios", usuariosPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", usuariosPage.getTotalPages());
            model.addAttribute("totalItems", usuariosPage.getTotalElements());
            model.addAttribute("totalUsuarios", usuarioService.count());
            model.addAttribute("totalActivos", totalActivos);
            model.addAttribute("totalInactivos", totalInactivos);
            model.addAttribute("search", search);
            model.addAttribute("size", size);
            model.addAttribute("filtroRol", rol);
            model.addAttribute("filtroEstado", estado != null ? estado.toString() : "");
            model.addAttribute("username", username);
            model.addAttribute("rol", rolNombre);
            model.addAttribute("fechaActual", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            model.addAttribute("titulo", "Gestión de Usuarios");
            model.addAttribute("roles", rolService.findAll());
            
            if (usuarioActual != null && usuarioActual.getUltimoAcceso() != null) {
                model.addAttribute("ultimoAcceso", usuarioActual.getUltimoAcceso());
            }
            
            addCommonAttributes(model);

            return "admin/usuarios";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar los usuarios: " + e.getMessage());
            model.addAttribute("username", "Administrador");
            model.addAttribute("rol", "Administrador");
            model.addAttribute("fechaActual", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            model.addAttribute("roles", rolService.findAll());
            addCommonAttributes(model);
            return "admin/usuarios";
        }
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null ? auth.getName() : "Administrador";
            
            model.addAttribute("usuario", new Usuario());
            model.addAttribute("roles", rolService.findAll());
            model.addAttribute("titulo", "Nuevo Usuario");
            model.addAttribute("accion", "crear");
            model.addAttribute("username", username);
            model.addAttribute("fechaActual", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            addCommonAttributes(model);
            return "admin/usuarios/nuevo";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            model.addAttribute("username", "Administrador");
            model.addAttribute("fechaActual", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            addCommonAttributes(model);
            return "admin/usuarios/nuevo";
        }
    }

    @PostMapping("/crear")
    public String crearUsuario(
            @Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult result,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("error", "Por favor, corrige los errores en el formulario.");
                return "redirect:/admin/usuarios/nuevo";
            }

            if (password == null || password.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "La contraseña es obligatoria.");
                return "redirect:/admin/usuarios/nuevo";
            }

            if (password.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres.");
                return "redirect:/admin/usuarios/nuevo";
            }

            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden.");
                return "redirect:/admin/usuarios/nuevo";
            }

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Integer idUsuarioCreacion = null;
            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                Usuario usuarioActual = usuarioService.findByEmail(auth.getName());
                if (usuarioActual != null) {
                    idUsuarioCreacion = usuarioActual.getIdUsuario();
                }
            }

            usuario.setIdUsuarioCreacion(idUsuarioCreacion);
            usuarioService.crear(usuario, password);

            redirectAttributes.addFlashAttribute("mensaje", "Usuario creado exitosamente.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            return "redirect:/admin/usuarios";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/usuarios/nuevo";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el usuario: " + e.getMessage());
            return "redirect:/admin/usuarios/nuevo";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        try {
            Usuario usuario = usuarioService.findById(id);
            if (usuario == null) {
                model.addAttribute("error", "Usuario no encontrado");
                return "redirect:/admin/usuarios";
            }

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null ? auth.getName() : "Administrador";

            model.addAttribute("usuario", usuario);
            model.addAttribute("roles", rolService.findAll());
            model.addAttribute("titulo", "Editar Usuario");
            model.addAttribute("accion", "editar");
            model.addAttribute("username", username);
            model.addAttribute("fechaActual", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            addCommonAttributes(model);
            return "admin/usuarios/nuevo";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            model.addAttribute("username", "Administrador");
            model.addAttribute("fechaActual", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            addCommonAttributes(model);
            return "admin/usuarios/nuevo";
        }
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarUsuario(
            @PathVariable Integer id,
            @Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult result,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
            RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("error", "Por favor, corrige los errores en el formulario.");
                return "redirect:/admin/usuarios/editar/" + id;
            }

            Usuario existente = usuarioService.findById(id);
            if (existente == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
                return "redirect:/admin/usuarios";
            }

            if (password != null && !password.trim().isEmpty()) {
                if (password.length() < 6) {
                    redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres.");
                    return "redirect:/admin/usuarios/editar/" + id;
                }
                if (!password.equals(confirmPassword)) {
                    redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden.");
                    return "redirect:/admin/usuarios/editar/" + id;
                }
                usuarioService.cambiarPassword(id, password);
            }

            existente.setIdRol(usuario.getIdRol());
            existente.setNombreCompleto(usuario.getNombreCompleto());
            existente.setEmail(usuario.getEmail());
            existente.setTelefono(usuario.getTelefono());
            existente.setActivo(usuario.getActivo());
            
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                Usuario usuarioActual = usuarioService.findByEmail(auth.getName());
                if (usuarioActual != null) {
                    existente.setIdUsuarioActualizacion(usuarioActual.getIdUsuario());
                }
            }

            usuarioService.actualizar(existente);

            redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado exitosamente.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            return "redirect:/admin/usuarios";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/usuarios/editar/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el usuario: " + e.getMessage());
            return "redirect:/admin/usuarios/editar/" + id;
        }
    }

    @PostMapping("/activar/{id}")
    public String activarUsuario(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.findById(id);
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
                return "redirect:/admin/usuarios";
            }

            if (usuario.isActivo()) {
                redirectAttributes.addFlashAttribute("error", "El usuario ya está activo.");
                return "redirect:/admin/usuarios";
            }

            usuarioService.activar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario activado exitosamente.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            return "redirect:/admin/usuarios";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al activar el usuario: " + e.getMessage());
            return "redirect:/admin/usuarios";
        }
    }

    @PostMapping("/desactivar/{id}")
    public String desactivarUsuario(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.findById(id);
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
                return "redirect:/admin/usuarios";
            }

            if (!usuario.isActivo()) {
                redirectAttributes.addFlashAttribute("error", "El usuario ya está desactivado.");
                return "redirect:/admin/usuarios";
            }

            // No permitir desactivar al último administrador
            if (usuario.esAdministrador()) {
                long countAdmin = usuarioService.countByNombreRol("Administrador");
                if (countAdmin <= 1) {
                    redirectAttributes.addFlashAttribute("error", "No se puede desactivar al último administrador del sistema.");
                    return "redirect:/admin/usuarios";
                }
            }

            usuarioService.desactivar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario desactivado exitosamente.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            return "redirect:/admin/usuarios";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al desactivar el usuario: " + e.getMessage());
            return "redirect:/admin/usuarios";
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.findById(id);
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
                return "redirect:/admin/usuarios";
            }

            if (usuario.esAdministrador()) {
                long countAdmin = usuarioService.countByNombreRol("Administrador");
                if (countAdmin <= 1) {
                    redirectAttributes.addFlashAttribute("error", "No se puede eliminar al último administrador del sistema.");
                    return "redirect:/admin/usuarios";
                }
            }

            usuarioService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado exitosamente.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            return "redirect:/admin/usuarios";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/usuarios";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el usuario: " + e.getMessage());
            return "redirect:/admin/usuarios";
        }
    }

    @GetMapping("/verificar-email")
    @ResponseBody
    public boolean verificarEmail(@RequestParam("email") String email, @RequestParam(value = "id", required = false) Integer id) {
        try {
            if (email == null || email.isEmpty()) {
                return true;
            }
            
            Usuario usuario = usuarioService.findByEmail(email);
            if (usuario == null) {
                return true;
            }
            return id != null && usuario.getIdUsuario().equals(id);
        } catch (Exception e) {
            return false;
        }
    }
}