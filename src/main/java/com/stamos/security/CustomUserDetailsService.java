package com.stamos.security;

import com.stamos.model.Usuario;
import com.stamos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("=== INICIO loadUserByUsername ===");
        System.out.println("Username recibido: '" + username + "'");

        if (username == null || username.trim().isEmpty()) {
            System.out.println("ERROR: Username vacio");
            throw new UsernameNotFoundException("Email no puede estar vacio");
        }

        String email = username.trim();
        System.out.println("Buscando usuario con email: '" + email + "'");

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("ERROR: Usuario NO encontrado con email: " + email);
                    return new UsernameNotFoundException("Usuario no encontrado con email: " + email);
                });

        System.out.println("Usuario encontrado:");
        System.out.println("  - ID: " + usuario.getIdUsuario());
        System.out.println("  - Email: " + usuario.getEmail());
        System.out.println("  - Activo: " + usuario.isActivo());
        System.out.println("  - ID Rol: " + usuario.getIdRol());
        System.out.println("  - Nombre Rol: '" + usuario.getNombreRol() + "'");
        System.out.println("  - Password Hash: " + (usuario.getPasswordHash() != null ? "PRESENTE" : "NULL"));

        if (!usuario.isActivo()) {
            System.out.println("ERROR: Usuario inactivo");
            throw new UsernameNotFoundException("El usuario con email: " + username + " esta deshabilitado");
        }

        Collection<GrantedAuthority> authorities = getAuthorities(usuario);
        System.out.println("Authorities asignadas (" + authorities.size() + "):");
        for (GrantedAuthority auth : authorities) {
            System.out.println("  - " + auth.getAuthority());
        }

        UserDetails userDetails = new User(
                usuario.getEmail(),
                usuario.getPasswordHash() != null ? usuario.getPasswordHash() : "",
                usuario.isActivo(),
                true,
                true,
                true,
                authorities
        );

        System.out.println("UserDetails creado exitosamente");
        System.out.println("=== FIN loadUserByUsername ===");
        return userDetails;
    }

    private Collection<GrantedAuthority> getAuthorities(Usuario usuario) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        String nombreRol = usuario.getNombreRol();

        System.out.println("=== getAuthorities ===");
        System.out.println("Nombre Rol original: '" + nombreRol + "'");

        if (nombreRol == null || nombreRol.trim().isEmpty()) {
            System.out.println("NombreRol es null o vacio, obteniendo desde la tabla Rol usando idRol=" + usuario.getIdRol());
            try {
                nombreRol = usuarioRepository.findNombreRolByIdRol(usuario.getIdRol());
                System.out.println("Rol obtenido de la tabla Rol: '" + nombreRol + "'");
                if (nombreRol == null || nombreRol.trim().isEmpty()) {
                    System.out.println("No se encontro rol en la tabla Rol, asignando CLIENTE por defecto");
                    nombreRol = "CLIENTE";
                }
            } catch (Exception e) {
                System.err.println("Error al obtener rol de la tabla Rol: " + e.getMessage());
                nombreRol = "CLIENTE";
            }
        }

        String rolNormalizado = nombreRol.trim().toUpperCase().replace(" ", "_").replace("Ó", "O").replace("É", "E").replace("Í", "I");
        System.out.println("Rol normalizado: '" + rolNormalizado + "'");

        authorities.add(new SimpleGrantedAuthority("ROLE_" + rolNormalizado));
        System.out.println("Agregado: ROLE_" + rolNormalizado);

        if (rolNormalizado.equals("CLIENTE")) {
            authorities.add(new SimpleGrantedAuthority("ROLE_CLIENTE"));
            authorities.add(new SimpleGrantedAuthority("Cliente"));
            System.out.println("Agregado: ROLE_CLIENTE");
            System.out.println("Agregado: Cliente");
        }

        if (rolNormalizado.equals("ADMINISTRADOR")) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"));
            authorities.add(new SimpleGrantedAuthority("Administrador"));
            System.out.println("Agregado: ROLE_ADMIN");
            System.out.println("Agregado: ROLE_ADMINISTRADOR");
            System.out.println("Agregado: Administrador");
        }

        if (rolNormalizado.equals("VENDEDOR")) {
            authorities.add(new SimpleGrantedAuthority("ROLE_VENDEDOR"));
            authorities.add(new SimpleGrantedAuthority("Vendedor"));
            System.out.println("Agregado: ROLE_VENDEDOR");
        }

        if (rolNormalizado.equals("DISEÑADOR")) {
            authorities.add(new SimpleGrantedAuthority("ROLE_DISEÑADOR"));
            authorities.add(new SimpleGrantedAuthority("Disenador"));
            System.out.println("Agregado: ROLE_DISEÑADOR");
        }

        if (rolNormalizado.equals("PRODUCCION")) {
            authorities.add(new SimpleGrantedAuthority("ROLE_PRODUCCION"));
            authorities.add(new SimpleGrantedAuthority("Produccion"));
            System.out.println("Agregado: ROLE_PRODUCCION");
        }

        if (rolNormalizado.equals("SOPORTE_TECNICO")) {
            authorities.add(new SimpleGrantedAuthority("ROLE_SOPORTE_TECNICO"));
            authorities.add(new SimpleGrantedAuthority("SoporteTecnico"));
            System.out.println("Agregado: ROLE_SOPORTE_TECNICO");
        }

        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority(rolNormalizado));
        System.out.println("Agregado: ROLE_USER");
        System.out.println("Agregado: " + rolNormalizado);

        authorities.addAll(getPermisosByRol(rolNormalizado));
        System.out.println("=== FIN getAuthorities ===");

        return authorities;
    }

    private List<GrantedAuthority> getPermisosByRol(String nombreRol) {
        List<GrantedAuthority> permisos = new ArrayList<>();

        if (nombreRol == null) {
            return permisos;
        }

        String rol = nombreRol.toUpperCase();

        switch (rol) {
            case "ADMINISTRADOR":
                permisos.add(new SimpleGrantedAuthority("PERMISO_ADMIN"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_GESTION_USUARIOS"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_GESTION_CLIENTES"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_GESTION_PROYECTOS"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_GESTION_COTIZACIONES"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_GESTION_INVENTARIO"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_GESTION_PAGOS"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_GESTION_CONFIGURACION"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_GESTION_DOCUMENTOS"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_CAMBIAR_ESTADO_PROYECTO"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_ANULAR_PROYECTO"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_ENTREGAR_PROYECTO"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_VER_REPORTES"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_ELIMINAR_REGISTROS"));
                break;

            case "VENDEDOR":
                permisos.add(new SimpleGrantedAuthority("PERMISO_GESTION_CLIENTES"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_GESTION_COTIZACIONES"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_VER_PROYECTOS"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_INICIAR_PROYECTO"));
                break;

            case "DISEÑADOR":
                permisos.add(new SimpleGrantedAuthority("PERMISO_VER_PROYECTOS"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_GESTION_DOCUMENTOS"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_CAMBIAR_ESTADO_PROYECTO"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_MOVER_DISENO_PRODUCCION"));
                break;

            case "PRODUCCION":
                permisos.add(new SimpleGrantedAuthority("PERMISO_VER_PROYECTOS"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_GESTION_INVENTARIO"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_CAMBIAR_ESTADO_PROYECTO"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_MOVER_PRODUCCION_INSTALACION"));
                break;

            case "SOPORTE_TECNICO":
                permisos.add(new SimpleGrantedAuthority("PERMISO_VER_PROYECTOS"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_GESTION_INVENTARIO"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_ENTREGAR_PROYECTO"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_CAMBIAR_ESTADO_PROYECTO"));
                break;

            case "CLIENTE":
                permisos.add(new SimpleGrantedAuthority("PERMISO_VER_MIS_PROYECTOS"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_VER_DOCUMENTOS"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_VER_PAGOS"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_EDITAR_PERFIL"));
                break;

            default:
                permisos.add(new SimpleGrantedAuthority("PERMISO_VER_MIS_PROYECTOS"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_VER_DOCUMENTOS"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_VER_PAGOS"));
                permisos.add(new SimpleGrantedAuthority("PERMISO_EDITAR_PERFIL"));
                break;
        }

        return permisos;
    }

    @Transactional
    public void updateUltimoAcceso(Integer idUsuario) {
        usuarioRepository.updateUltimoAcceso(idUsuario);
    }

    @Transactional(readOnly = true)
    public Usuario loadUsuarioById(Integer idUsuario) throws UsernameNotFoundException {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con ID: " + idUsuario));
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean isUsuarioActivo(String email) {
        return usuarioRepository.findByEmail(email)
                .map(Usuario::isActivo)
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public String getRolByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .map(Usuario::getNombreRol)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public Integer getIdRolByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .map(Usuario::getIdRol)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public String getNombreCompletoByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .map(Usuario::getNombreCompleto)
                .orElse(null);
    }

    @Transactional
    public void updateUltimoAccesoByEmail(String email) {
        usuarioRepository.findByEmail(email).ifPresent(usuario -> {
            usuarioRepository.updateUltimoAcceso(usuario.getIdUsuario());
        });
    }

    @Transactional(readOnly = true)
    public List<Usuario> findUsuariosActivos() {
        return usuarioRepository.findActivos();
    }

    @Transactional(readOnly = true)
    public List<Usuario> findUsuariosByRol(String nombreRol) {
        return usuarioRepository.findByNombreRol(nombreRol);
    }

    @Transactional(readOnly = true)
    public List<Usuario> findUsuariosByRolAndActivo(String nombreRol, Boolean activo) {
        return usuarioRepository.findByNombreRolAndActivo(nombreRol, activo);
    }

    @Transactional
    public Usuario crearUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario actualizarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void cambiarPassword(Integer idUsuario, String passwordHash) {
        usuarioRepository.updatePassword(idUsuario, passwordHash);
    }

    @Transactional
    public void cambiarEstado(Integer idUsuario, Boolean activo) {
        usuarioRepository.updateActivo(idUsuario, activo);
    }

    @Transactional
    public void cambiarRol(Integer idUsuario, Integer idRol) {
        usuarioRepository.updateRol(idUsuario, idRol);
    }

    @Transactional
    public void eliminarUsuario(Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        usuarioRepository.delete(usuario);
    }

    public String getDisplayName(String email) {
        if (email == null) {
            return "Usuario";
        }
        return usuarioRepository.findByEmail(email)
                .map(Usuario::getNombreCompleto)
                .orElse(email);
    }

    public boolean hasRol(String email, String nombreRol) {
        return usuarioRepository.findByEmail(email)
                .map(usuario -> nombreRol.equals(usuario.getNombreRol()))
                .orElse(false);
    }

    public boolean isAdministrador(String email) {
        return hasRol(email, "Administrador");
    }

    public boolean isCliente(String email) {
        return hasRol(email, "Cliente");
    }

    public boolean isUsuarioInterno(String email) {
        return !isCliente(email);
    }

    public Integer getUsuarioId(String email) {
        return usuarioRepository.findByEmail(email)
                .map(Usuario::getIdUsuario)
                .orElse(null);
    }
}