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

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> findAllEmployees() {
        return usuarioRepository.findByEsEmpleadoTrue();
    }

    public Usuario findById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public long countByActivoTrue() {
        return usuarioRepository.countByActivoTrue();
    }


    public void disable(Long id) {
        Usuario usuario = findById(id);
        if (usuario != null && usuario.isEsEmpleado()) {
            usuario.setActivo(false);
            usuarioRepository.save(usuario);
        }
    }

    public void enable(Long id) {
        Usuario usuario = findById(id);
        if (usuario != null && usuario.isEsEmpleado()) {
            usuario.setActivo(true);
            usuarioRepository.save(usuario);
        }
    }


    public void savePasswordResetToken(Usuario usuario, String token) {
        usuario.setResetToken(token);
        usuario.setResetTokenExpiry(LocalDateTime.now().plusHours(24));
        usuarioRepository.save(usuario);
    }

    public void sendPasswordResetToken(Usuario usuario) {
        String token = UUID.randomUUID().toString();
        savePasswordResetToken(usuario, token);
    }

    public boolean isValidPasswordResetToken(String token) {
        Usuario usuario = usuarioRepository.findByResetToken(token).orElse(null);
        if (usuario == null || !usuario.isActivo()) {
            return false;
        }
        return usuario.getResetTokenExpiry() != null && 
               usuario.getResetTokenExpiry().isAfter(LocalDateTime.now());
    }

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


    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Object principal = auth.getPrincipal();
            if (principal instanceof Usuario usuario) {
                return usuario.getId();
            }
        }
        return null;
    }

    public Usuario getCurrentUser() {
        Long id = getCurrentUserId();
        return id != null ? findById(id) : null;
    }
}