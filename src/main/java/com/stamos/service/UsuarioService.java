package com.stamos.service;

import com.stamos.entity.Usuario;
import com.stamos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Servicio para la gestión de usuarios empleados.
 * 
 * Reglas de negocio:
 * - Solo el administrador puede crear, editar o deshabilitar empleados.
 * - Las contraseñas se almacenan cifradas con BCrypt.
 * - Se puede generar un token para restablecimiento de contraseña.
 * - Los clientes NO se gestionan aquí (ver ClienteService).
 */
@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // ==================== CONSULTAS BÁSICAS ====================

    /**
     * Obtiene todos los usuarios (empleados y clientes).
     *
     * @return Lista de todos los usuarios
     */
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    /**
     * Obtiene todos los empleados (solo usuarios con esEmpleado = true).
     *
     * @return Lista de empleados
     */
    public List<Usuario> findAllEmployees() {
        return usuarioRepository.findByEsEmpleadoTrue();
    }

    /**
     * Obtiene un usuario por su ID.
     *
     * @param id ID del usuario
     * @return Usuario o null si no existe
     */
    public Usuario findById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    /**
     * Obtiene un usuario por su email.
     *
     * @param email Email del usuario
     * @return Usuario o null si no existe
     */
    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    /**
     * Guarda un usuario (empleado o cliente según el flag esEmpleado).
     *
     * @param usuario Usuario a persistir
     * @return Usuario persistido
     */
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    /**
     * Verifica si ya existe un usuario con el email dado.
     *
     * @param email Email a verificar
     * @return true si ya existe
     */
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    // ==================== GESTIÓN DE EMPLEADOS ====================

    /**
     * Deshabilita un empleado (cambia activo a false).
     *
     * @param id ID del empleado
     */
    public void disable(Long id) {
        Usuario usuario = findById(id);
        if (usuario != null && usuario.isEsEmpleado()) {
            usuario.setActivo(false);
            usuarioRepository.save(usuario);
        }
    }

    /**
     * Habilita un empleado.
     *
     * @param id ID del empleado
     */
    public void enable(Long id) {
        Usuario usuario = findById(id);
        if (usuario != null && usuario.isEsEmpleado()) {
            usuario.setActivo(true);
            usuarioRepository.save(usuario);
        }
    }

    // ==================== RESTABLECIMIENTO DE CONTRASEÑA ====================

    /**
     * Guarda un token de restablecimiento de contraseña para un usuario.
     *
     * @param usuario Usuario
     * @param token   Token generado (ej. UUID)
     */
    public void savePasswordResetToken(Usuario usuario, String token) {
        usuario.setResetToken(token);
        usuario.setResetTokenExpiry(LocalDateTime.now().plusHours(24));
        usuarioRepository.save(usuario);
    }

    /**
     * Envía un correo con un enlace para restablecer la contraseña.
     *
     * @param usuario Usuario que solicita restablecimiento
     */
    public void sendPasswordResetToken(Usuario usuario) {
        String token = UUID.randomUUID().toString();
        savePasswordResetToken(usuario, token);
        emailService.enviarResetPasswordEmpleado(usuario, token);
    }

    /**
     * Verifica si un token de restablecimiento es válido.
     *
     * @param token Token a validar
     * @return true si el token existe, no ha expirado y el usuario está activo
     */
    public boolean isValidPasswordResetToken(String token) {
        Usuario usuario = usuarioRepository.findByResetToken(token).orElse(null);
        if (usuario == null || !usuario.isActivo()) {
            return false;
        }
        return usuario.getResetTokenExpiry() != null && 
               usuario.getResetTokenExpiry().isAfter(LocalDateTime.now());
    }

    /**
     * Restablece la contraseña del usuario asociado al token.
     *
     * @param token            Token de restablecimiento
     * @param encryptedPassword Nueva contraseña (ya encriptada)
     * @return true si se actualizó correctamente
     */
    public boolean resetPassword(String token, String encryptedPassword) {
        Usuario usuario = usuarioRepository.findByResetToken(token).orElse(null);
        if (usuario == null || !isValidPasswordResetToken(token)) {
            return false;
        }
        usuario.setPassword(encryptedPassword);
        usuario.setResetToken(null);
        usuario.setResetTokenExpiry(null);
        usuarioRepository.save(usuario);
        return true;
    }

    // ==================== USUARIO ACTUAL ====================

    /**
     * Obtiene el ID del usuario autenticado actualmente.
     *
     * @return ID del usuario o null si no autenticado
     */
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Object principal = auth.getPrincipal();
            if (principal instanceof Usuario) {
                return ((Usuario) principal).getId();
            }
        }
        return null;
    }

    /**
     * Obtiene el usuario autenticado actualmente.
     *
     * @return Usuario autenticado o null
     */
    public Usuario getCurrentUser() {
        Long id = getCurrentUserId();
        return id != null ? findById(id) : null;
    }
}