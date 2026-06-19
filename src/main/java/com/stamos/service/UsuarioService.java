package com.stamos.service;

import com.stamos.model.Rol;
import com.stamos.model.Usuario;
import com.stamos.repository.RolRepository;
import com.stamos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario crear(Usuario usuario, String password) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya está registrado: " + usuario.getEmail());
        }

        Rol rol = rolRepository.findById(usuario.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        usuario.setPasswordHash(passwordEncoder.encode(password));
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setFechaActualizacion(LocalDateTime.now());

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        usuarioGuardado.setNombreRol(rol.getNombreRol());
        usuarioGuardado.setNombreRolLegible(rol.getNombreLegible());
        usuarioGuardado.setColorRol(rol.getColor());
        usuarioGuardado.setIconoRol(rol.getIcono());

        return usuarioGuardado;
    }

    @Transactional
    public Usuario actualizar(Usuario usuario) {
        Usuario existente = usuarioRepository.findById(usuario.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!existente.getEmail().equals(usuario.getEmail()) &&
            usuarioRepository.existsByEmailAndIdNot(usuario.getEmail(), usuario.getIdUsuario())) {
            throw new RuntimeException("El email ya está registrado: " + usuario.getEmail());
        }

        Rol rol = rolRepository.findById(usuario.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        existente.setIdRol(usuario.getIdRol());
        existente.setNombreCompleto(usuario.getNombreCompleto());
        existente.setEmail(usuario.getEmail());
        existente.setTelefono(usuario.getTelefono());
        existente.setActivo(usuario.getActivo());
        existente.setFechaActualizacion(LocalDateTime.now());

        Usuario usuarioActualizado = usuarioRepository.save(existente);

        usuarioActualizado.setNombreRol(rol.getNombreRol());
        usuarioActualizado.setNombreRolLegible(rol.getNombreLegible());
        usuarioActualizado.setColorRol(rol.getColor());
        usuarioActualizado.setIconoRol(rol.getIcono());

        return usuarioActualizado;
    }

    @Transactional
    public void cambiarPassword(Integer idUsuario, String newPassword) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("La contraseña debe tener al menos 6 caracteres");
        }

        usuario.setPasswordHash(passwordEncoder.encode(newPassword));
        usuario.setFechaActualizacion(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void cambiarRol(Integer idUsuario, Integer idRol) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));


        usuario.setIdRol(idRol);
        usuario.setFechaActualizacion(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void activar(Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setActivo(true);
        usuario.setFechaActualizacion(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void desactivar(Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setActivo(false);
        usuario.setFechaActualizacion(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminar(Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.esAdministrador()) {
            long countAdmin = usuarioRepository.countByNombreRol("Administrador");
            if (countAdmin <= 1) {
                throw new RuntimeException("No se puede eliminar el último administrador del sistema");
            }
        }

        usuarioRepository.delete(usuario);
    }

    public Usuario findById(Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario != null) {
            enriquecerUsuario(usuario);
        }
        return usuario;
    }

    public Usuario findByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario != null) {
            enriquecerUsuario(usuario);
        }
        return usuario;
    }

    public List<Usuario> findAll() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        for (Usuario usuario : usuarios) {
            enriquecerUsuario(usuario);
        }
        return usuarios;
    }

    public Page<Usuario> findAll(Pageable pageable) {
        Page<Usuario> usuarios = usuarioRepository.findAll(pageable);
        for (Usuario usuario : usuarios.getContent()) {
            enriquecerUsuario(usuario);
        }
        return usuarios;
    }

    public List<Usuario> findByRol(Integer idRol) {
        List<Usuario> usuarios = usuarioRepository.findByIdRolOrderByNombre(idRol);
        for (Usuario usuario : usuarios) {
            enriquecerUsuario(usuario);
        }
        return usuarios;
    }

    public Page<Usuario> findByRol(Integer idRol, Pageable pageable) {
        Page<Usuario> usuarios = usuarioRepository.findByIdRol(idRol, pageable);
        for (Usuario usuario : usuarios.getContent()) {
            enriquecerUsuario(usuario);
        }
        return usuarios;
    }

    public List<Usuario> findByNombreRol(String nombreRol) {
        List<Usuario> usuarios = usuarioRepository.findByNombreRol(nombreRol);
        for (Usuario usuario : usuarios) {
            enriquecerUsuario(usuario);
        }
        return usuarios;
    }

    public List<Usuario> findActivos() {
        List<Usuario> usuarios = usuarioRepository.findActivos();
        for (Usuario usuario : usuarios) {
            enriquecerUsuario(usuario);
        }
        return usuarios;
    }

    public Page<Usuario> findActivos(Pageable pageable) {
        Page<Usuario> usuarios = usuarioRepository.findActivos(pageable);
        for (Usuario usuario : usuarios.getContent()) {
            enriquecerUsuario(usuario);
        }
        return usuarios;
    }

    public List<Usuario> findInactivos() {
        List<Usuario> usuarios = usuarioRepository.findInactivos();
        for (Usuario usuario : usuarios) {
            enriquecerUsuario(usuario);
        }
        return usuarios;
    }

    public Page<Usuario> findInactivos(Pageable pageable) {
        Page<Usuario> usuarios = usuarioRepository.findInactivos(pageable);
        for (Usuario usuario : usuarios.getContent()) {
            enriquecerUsuario(usuario);
        }
        return usuarios;
    }

    public List<Usuario> findByNombreContaining(String nombre) {
        List<Usuario> usuarios = usuarioRepository.findByNombreCompletoContaining(nombre);
        for (Usuario usuario : usuarios) {
            enriquecerUsuario(usuario);
        }
        return usuarios;
    }

    public Page<Usuario> findByNombreContaining(String nombre, Pageable pageable) {
        Page<Usuario> usuarios = usuarioRepository.findByNombreCompletoContaining(nombre, pageable);
        for (Usuario usuario : usuarios.getContent()) {
            enriquecerUsuario(usuario);
        }
        return usuarios;
    }

    public List<Usuario> search(String searchText) {
        List<Usuario> usuarios = usuarioRepository.search(searchText);
        for (Usuario usuario : usuarios) {
            enriquecerUsuario(usuario);
        }
        return usuarios;
    }

    public Page<Usuario> search(String searchText, Pageable pageable) {
        Page<Usuario> usuarios = usuarioRepository.search(searchText, pageable);
        for (Usuario usuario : usuarios.getContent()) {
            enriquecerUsuario(usuario);
        }
        return usuarios;
    }

    public List<Usuario> findTopN(int limit) {
        List<Usuario> usuarios = usuarioRepository.findTopN(limit);
        for (Usuario usuario : usuarios) {
            enriquecerUsuario(usuario);
        }
        return usuarios;
    }

    public List<Usuario> findAdministradores() {
        return usuarioRepository.findAdministradores();
    }

    public List<Usuario> findVendedores() {
        return usuarioRepository.findVendedores();
    }

    public List<Usuario> findDisenadores() {
        return usuarioRepository.findDisenadores();
    }

    public List<Usuario> findProduccion() {
        return usuarioRepository.findProduccion();
    }

    public List<Usuario> findSoporteTecnico() {
        return usuarioRepository.findSoporteTecnico();
    }

    public List<Usuario> findClientes() {
        return usuarioRepository.findClientes();
    }

    public long count() {
        return usuarioRepository.count();
    }

    public long countActivos() {
        return usuarioRepository.countActivos();
    }

    public long countInactivos() {
        return usuarioRepository.countInactivos();
    }

    public long countByRol(Integer idRol) {
        return usuarioRepository.countByIdRol(idRol);
    }

    public long countByNombreRol(String nombreRol) {
        return usuarioRepository.countByNombreRol(nombreRol);
    }

    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public boolean isActivo(Integer idUsuario) {
        return usuarioRepository.isActivo(idUsuario);
    }

    public boolean isAdministrador(Integer idUsuario) {
        return usuarioRepository.isAdministrador(idUsuario);
    }

    public boolean hasRol(Integer idUsuario, String nombreRol) {
        return usuarioRepository.hasRol(idUsuario, nombreRol);
    }

    public void actualizarUltimoAcceso(Integer idUsuario) {
        usuarioRepository.updateUltimoAcceso(idUsuario);
    }

    public String getEstadoLegible(Boolean activo) {
        if (activo == null) return "Sin estado";
        return activo ? "Activo" : "Inactivo";
    }

    public String getEstadoBadgeClass(Boolean activo) {
        if (activo == null) return "badge-secondary";
        return activo ? "badge-success" : "badge-danger";
    }

    public String getEstadoIcono(Boolean activo) {
        if (activo == null) return "ti ti-circle";
        return activo ? "ti ti-check-circle" : "ti ti-x-circle";
    }

    public String getEstadoColor(Boolean activo) {
        if (activo == null) return "#6B7280";
        return activo ? "#10B981" : "#EF4444";
    }

    public String getNombreRolLegible(String nombreRol) {
        if (nombreRol == null) return "Sin rol";
        return switch (nombreRol) {
            case "Administrador" -> "Administrador";
            case "Vendedor" -> "Vendedor";
            case "Diseñador" -> "Diseñador";
            case "Producción" -> "Producción";
            case "Soporte Técnico" -> "Soporte Técnico";
            case "Cliente" -> "Cliente";
            default -> nombreRol;
        };
    }

    public String getRolBadgeClass(String nombreRol) {
        if (nombreRol == null) return "badge-secondary";
        return switch (nombreRol) {
            case "Administrador" -> "badge-danger";
            case "Vendedor" -> "badge-primary";
            case "Diseñador" -> "badge-purple";
            case "Producción" -> "badge-warning";
            case "Soporte Técnico" -> "badge-success";
            case "Cliente" -> "badge-secondary";
            default -> "badge-secondary";
        };
    }

    public String getRolIcono(String nombreRol) {
        if (nombreRol == null) return "ti ti-user";
        return switch (nombreRol) {
            case "Administrador" -> "ti ti-shield";
            case "Vendedor" -> "ti ti-user-check";
            case "Diseñador" -> "ti ti-pencil";
            case "Producción" -> "ti ti-tools";
            case "Soporte Técnico" -> "ti ti-headset";
            case "Cliente" -> "ti ti-user";
            default -> "ti ti-user";
        };
    }

    public String getRolColor(String nombreRol) {
        if (nombreRol == null) return "#6B7280";
        return switch (nombreRol) {
            case "Administrador" -> "#EF4444";
            case "Vendedor" -> "#3B82F6";
            case "Diseñador" -> "#8B5CF6";
            case "Producción" -> "#F59E0B";
            case "Soporte Técnico" -> "#10B981";
            case "Cliente" -> "#6B7280";
            default -> "#6B7280";
        };
    }

    private void enriquecerUsuario(Usuario usuario) {
        if (usuario == null) return;

        if (usuario.getNombreRol() == null && usuario.getIdRol() != null) {
            rolRepository.findById(usuario.getIdRol()).ifPresent(rol -> {
                usuario.setNombreRol(rol.getNombreRol());
                usuario.setNombreRolLegible(rol.getNombreLegible());
                usuario.setColorRol(rol.getColor());
                usuario.setIconoRol(rol.getIcono());
            });
        }

        if (usuario.getEstadoLegible() == null) {
            usuario.setEstadoLegible(usuario.isActivo() ? "Activo" : "Inactivo");
            usuario.setEstadoBadgeClass(usuario.isActivo() ? "badge-success" : "badge-danger");
            usuario.setEstadoIcono(usuario.isActivo() ? "ti ti-check-circle" : "ti ti-x-circle");
            usuario.setEstadoColor(usuario.isActivo() ? "#10B981" : "#EF4444");
        }
    }
}