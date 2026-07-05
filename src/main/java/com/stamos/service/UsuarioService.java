package com.stamos.service;

import com.stamos.model.Rol;
import com.stamos.model.Usuario;
import com.stamos.repository.ClienteRepository;
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
    private ClienteRepository clienteRepository;

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
        enriquecerUsuario(usuarioGuardado, rol);

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
        enriquecerUsuario(usuarioActualizado, rol);

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
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));

        if (usuario.esAdministrador()) {
            long countAdmin = usuarioRepository.countByNombreRol("Administrador");
            if (countAdmin <= 1) {
                throw new RuntimeException("No se puede eliminar el último administrador del sistema");
            }
        }

        if (usuario.esCliente()) {
            long countClientes = clienteRepository.countByIdUsuarioRegis(idUsuario);
            if (countClientes > 0) {
                throw new RuntimeException("No se puede eliminar un usuario que tiene clientes asociados");
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

    public long countByActivo(boolean activo) {
        return usuarioRepository.countByActivo(activo);
    }

    public Page<Usuario> findByActivo(Boolean activo, Pageable pageable) {
        if (activo == null) {
            return usuarioRepository.findAll(pageable);
        }
        return usuarioRepository.findByActivo(activo, pageable);
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

    private void enriquecerUsuario(Usuario usuario, Rol rol) {
        if (usuario == null) return;

        usuario.setNombreRol(rol.getNombreRol());
        usuario.setNombreRolLegible(rol.getNombreLegible());
        usuario.setColorRol(rol.getColor());
        usuario.setIconoRol(rol.getIcono());
        usuario.setEstadoLegible(usuario.isActivo() ? "Activo" : "Inactivo");
        usuario.setEstadoBadgeClass(usuario.isActivo() ? "badge-success" : "badge-danger");
        usuario.setEstadoIcono(usuario.isActivo() ? "ti ti-check-circle" : "ti ti-x-circle");
        usuario.setEstadoColor(usuario.isActivo() ? "#10B981" : "#EF4444");
    }
}

