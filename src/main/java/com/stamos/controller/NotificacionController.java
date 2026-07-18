package com.stamos.controller;

import com.stamos.model.Usuario;
import com.stamos.model.Notificacion;
import com.stamos.service.NotificacionService;
import com.stamos.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/notificaciones")
public class NotificacionController {

    private static final Logger log = LoggerFactory.getLogger(NotificacionController.class);

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listarNotificaciones(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            Authentication auth,
            Model model) {

        try {
            Usuario usuario = usuarioService.findByEmail(auth.getName());
            if (usuario == null) {
                return "redirect:/login";
            }

            var pageable = PageRequest.of(page, size, Sort.by("fechaEnvio").descending());
            var notificaciones = notificacionService.findByIdUsuario(usuario.getIdUsuario(), pageable);
            long noLeidas = notificacionService.countNoLeidasByUsuario(usuario.getIdUsuario());

            model.addAttribute("notificaciones", notificaciones);
            model.addAttribute("notificacionesNoLeidas", noLeidas);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", notificaciones.getTotalPages());
            model.addAttribute("totalItems", notificaciones.getTotalElements());
            model.addAttribute("search", search);
            model.addAttribute("username", usuario.getNombreCompleto() != null ? usuario.getNombreCompleto() : usuario.getEmail());
            model.addAttribute("rol", usuario.getNombreRol() != null ? usuario.getNombreRol() : "Usuario");
            model.addAttribute("fechaActual", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

            return "notificaciones/lista";

        } catch (Exception e) {
            log.error("Error al cargar notificaciones: {}", e.getMessage());
            model.addAttribute("error", "Error al cargar notificaciones: " + e.getMessage());
            model.addAttribute("username", "Usuario");
            model.addAttribute("rol", "Usuario");
            model.addAttribute("fechaActual", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            return "notificaciones/lista";
        }
    }

    @PostMapping("/marcar-leida/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> marcarLeida(@PathVariable Integer id, Authentication auth) {
        Map<String, Object> response = new HashMap<>();
        try {
            log.info("=== MARCAR LEIDA ===");
            log.info("ID: {}", id);
            
            if (auth == null) {
                log.error("Auth es null");
                response.put("success", false);
                response.put("message", "Usuario no autenticado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            log.info("Usuario: {}", auth.getName());
            
            Usuario usuario = usuarioService.findByEmail(auth.getName());
            if (usuario == null) {
                log.error("Usuario no encontrado: {}", auth.getName());
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            log.info("Usuario ID: {}", usuario.getIdUsuario());

            Notificacion notificacion = notificacionService.findById(id);
            if (notificacion == null) {
                log.error("Notificacion no encontrada: {}", id);
                response.put("success", false);
                response.put("message", "Notificacion no encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            log.info("Notificacion encontrada - ID: {}, Leido: {}", notificacion.getIdNotificacion(), notificacion.getLeido());

            notificacionService.marcarLeida(id);
            
            log.info("Notificacion marcada como leida exitosamente");
            response.put("success", true);
            response.put("message", "Notificacion marcada como leida");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error al marcar notificacion {} como leida: {}", id, e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/marcar-todas-leidas")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> marcarTodasLeidas(Authentication auth) {
        Map<String, Object> response = new HashMap<>();
        try {
            log.info("=== MARCAR TODAS LEIDAS ===");
            
            Usuario usuario = usuarioService.findByEmail(auth.getName());
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Usuario no autenticado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            notificacionService.marcarTodasLeidasByUsuario(usuario.getIdUsuario());
            response.put("success", true);
            response.put("message", "Todas las notificaciones marcadas como leidas");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error al marcar todas las notificaciones: {}", e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/contador")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getContadorNoLeidas(Authentication auth) {
        Map<String, Object> response = new HashMap<>();
        try {
            Usuario usuario = usuarioService.findByEmail(auth.getName());
            if (usuario == null) {
                response.put("count", 0);
                return ResponseEntity.ok(response);
            }
            
            long count = notificacionService.countNoLeidasByUsuario(usuario.getIdUsuario());
            response.put("count", count);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error al obtener contador: {}", e.getMessage());
            response.put("count", 0);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}