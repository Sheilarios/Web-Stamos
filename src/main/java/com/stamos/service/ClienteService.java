package com.stamos.service;

import com.stamos.model.Cliente;
import com.stamos.model.Rol;
import com.stamos.model.Usuario;
import com.stamos.repository.ClienteRepository;
import com.stamos.repository.RolRepository;
import com.stamos.repository.UsuarioRepository;
import com.stamos.repository.LogAccesoClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private LogAccesoClienteRepository logAccesoClienteRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Transactional(rollbackFor = Exception.class)
    public Cliente registrar(Cliente cliente, String password) {
        try {
            // Validar que el email no esté registrado
            if (usuarioRepository.existsByEmail(cliente.getEmail())) {
                throw new RuntimeException("El email ya está registrado en el sistema");
            }

            // Validar RUC si existe
            if (cliente.getRuc() != null && !cliente.getRuc().isEmpty() &&
                clienteRepository.existsByRuc(cliente.getRuc())) {
                throw new RuntimeException("El RUC ya está registrado en el sistema");
            }

            // Validar teléfono si existe
            if (cliente.getTelefono() != null && !cliente.getTelefono().isEmpty() &&
                clienteRepository.existsByTelefono(cliente.getTelefono())) {
                throw new RuntimeException("El teléfono ya está registrado en el sistema");
            }

            // Obtener rol Cliente
            Rol rolCliente = rolRepository.findByNombreRol("Cliente")
                    .orElseThrow(() -> new RuntimeException("Rol de cliente no encontrado"));

            // Validar contraseña
            if (password == null || password.length() < 6) {
                throw new RuntimeException("La contraseña debe tener al menos 6 caracteres");
            }

            // Crear usuario
            Usuario usuario = new Usuario();
            usuario.setIdRol(rolCliente.getIdRol());
            usuario.setNombreCompleto(cliente.getNombreRazonSocial());
            usuario.setEmail(cliente.getEmail());
            usuario.setPasswordHash(passwordEncoder.encode(password));
            usuario.setTelefono(cliente.getTelefono());
            usuario.setActivo(true);
            usuario.setFechaCreacion(LocalDateTime.now());
            usuario.setFechaActualizacion(LocalDateTime.now());

            Usuario usuarioGuardado = usuarioRepository.save(usuario);

            // Crear cliente
            cliente.setIdUsuario(usuarioGuardado.getIdUsuario());
            cliente.setIdUsuarioRegis(usuarioGuardado.getIdUsuario());
            cliente.setEstado("Activo");
            cliente.setFechaRegistro(LocalDateTime.now());
            cliente.setFechaActualizacion(LocalDateTime.now());

            return clienteRepository.save(cliente);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar el cliente: " + e.getMessage(), e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Cliente actualizar(Cliente cliente) {
        try {
            Cliente existente = clienteRepository.findById(cliente.getIdCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

            // Validar email único
            if (!existente.getEmail().equals(cliente.getEmail()) &&
                usuarioRepository.existsByEmail(cliente.getEmail())) {
                throw new RuntimeException("El email ya está registrado en el sistema");
            }

            // Validar RUC único
            if (cliente.getRuc() != null && !cliente.getRuc().isEmpty() &&
                !existente.getRuc().equals(cliente.getRuc()) &&
                clienteRepository.existsByRuc(cliente.getRuc())) {
                throw new RuntimeException("El RUC ya está registrado en el sistema");
            }

            // Validar teléfono único
            if (cliente.getTelefono() != null && !cliente.getTelefono().isEmpty() &&
                !existente.getTelefono().equals(cliente.getTelefono()) &&
                clienteRepository.existsByTelefono(cliente.getTelefono())) {
                throw new RuntimeException("El teléfono ya está registrado en el sistema");
            }

            // Actualizar cliente
            existente.setTipoPersona(cliente.getTipoPersona());
            existente.setNombreRazonSocial(cliente.getNombreRazonSocial());
            existente.setRuc(cliente.getRuc());
            existente.setTelefono(cliente.getTelefono());
            existente.setEmail(cliente.getEmail());
            existente.setDireccion(cliente.getDireccion());
            existente.setFechaActualizacion(LocalDateTime.now());

            // Actualizar usuario asociado
            if (existente.getIdUsuario() != null) {
                Usuario usuario = usuarioRepository.findById(existente.getIdUsuario()).orElse(null);
                if (usuario != null) {
                    usuario.setNombreCompleto(cliente.getNombreRazonSocial());
                    usuario.setEmail(cliente.getEmail());
                    usuario.setTelefono(cliente.getTelefono());
                    usuario.setFechaActualizacion(LocalDateTime.now());
                    usuarioRepository.save(usuario);
                }
            }

            return clienteRepository.save(existente);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el cliente: " + e.getMessage(), e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void cambiarPassword(Integer idUsuario, String newPassword) {
        try {
            if (newPassword == null || newPassword.length() < 6) {
                throw new RuntimeException("La contraseña debe tener al menos 6 caracteres");
            }

            Usuario usuario = usuarioRepository.findById(idUsuario)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            usuario.setPasswordHash(passwordEncoder.encode(newPassword));
            usuario.setFechaActualizacion(LocalDateTime.now());
            usuarioRepository.save(usuario);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al cambiar la contraseña: " + e.getMessage(), e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deshabilitar(Integer idCliente, Integer idUsuarioAdministrador) {
        try {
            Cliente cliente = clienteRepository.findById(idCliente)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

            if (!"Activo".equals(cliente.getEstado())) {
                throw new RuntimeException("El cliente ya está deshabilitado");
            }

            cliente.setEstado("Deshabilitado");
            cliente.setFechaActualizacion(LocalDateTime.now());
            cliente.setIdUsuarioActualizacion(idUsuarioAdministrador);
            clienteRepository.save(cliente);

            if (cliente.getIdUsuario() != null) {
                Usuario usuario = usuarioRepository.findById(cliente.getIdUsuario()).orElse(null);
                if (usuario != null) {
                    usuario.setActivo(false);
                    usuario.setFechaActualizacion(LocalDateTime.now());
                    usuario.setIdUsuarioActualizacion(idUsuarioAdministrador);
                    usuarioRepository.save(usuario);
                }
            }

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al deshabilitar el cliente: " + e.getMessage(), e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void habilitar(Integer idCliente, Integer idUsuarioAdministrador) {
        try {
            Cliente cliente = clienteRepository.findById(idCliente)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

            if ("Activo".equals(cliente.getEstado())) {
                throw new RuntimeException("El cliente ya está activo");
            }

            cliente.setEstado("Activo");
            cliente.setFechaActualizacion(LocalDateTime.now());
            cliente.setIdUsuarioActualizacion(idUsuarioAdministrador);
            clienteRepository.save(cliente);

            if (cliente.getIdUsuario() != null) {
                Usuario usuario = usuarioRepository.findById(cliente.getIdUsuario()).orElse(null);
                if (usuario != null) {
                    usuario.setActivo(true);
                    usuario.setFechaActualizacion(LocalDateTime.now());
                    usuario.setIdUsuarioActualizacion(idUsuarioAdministrador);
                    usuarioRepository.save(usuario);
                }
            }

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al habilitar el cliente: " + e.getMessage(), e);
        }
    }

    // Métodos de consulta (sin transacción o solo lectura)
    public Cliente findById(Integer idCliente) {
        return clienteRepository.findById(idCliente).orElse(null);
    }

    public Cliente findByRuc(String ruc) {
        return clienteRepository.findByRuc(ruc).orElse(null);
    }

    public Cliente findByEmail(String email) {
        return clienteRepository.findByEmail(email).orElse(null);
    }

    public Cliente findByTelefono(String telefono) {
        return clienteRepository.findByTelefono(telefono).orElse(null);
    }

    public Cliente findByUsuario(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return clienteRepository.findByIdUsuario(usuario.getIdUsuario()).orElse(null);
    }

    public Cliente findByUsuarioId(Integer idUsuario) {
        return clienteRepository.findByIdUsuario(idUsuario).orElse(null);
    }

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Page<Cliente> findAll(Pageable pageable) {
        return clienteRepository.findAll(pageable);
    }

    public List<Cliente> findActive() {
        return clienteRepository.findActive();
    }

    public Page<Cliente> findActive(Pageable pageable) {
        return clienteRepository.findActive(pageable);
    }

    public List<Cliente> findByEstado(String estado) {
        return clienteRepository.findByEstado(estado);
    }

    public Page<Cliente> findByEstado(String estado, Pageable pageable) {
        return clienteRepository.findByEstado(estado, pageable);
    }

    public List<Cliente> buscarPorNombreORuc(String searchText) {
        return clienteRepository.findByNombreOrRuc(searchText);
    }

    public Page<Cliente> buscarPorNombreORuc(String searchText, Pageable pageable) {
        return clienteRepository.findByNombreOrRuc(searchText, pageable);
    }

    public List<Cliente> search(String searchText) {
        return clienteRepository.search(searchText);
    }

    public Page<Cliente> search(String searchText, Pageable pageable) {
        return clienteRepository.search(searchText, pageable);
    }

    public List<Cliente> findClientesWithUsuario() {
        return clienteRepository.findClientesWithUsuario();
    }

    public Page<Cliente> findClientesWithUsuario(Pageable pageable) {
        return clienteRepository.findClientesWithUsuario(pageable);
    }

    public List<Cliente> findClientesWithoutUsuario() {
        return clienteRepository.findClientesWithoutUsuario();
    }

    public Usuario findUsuarioByEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    public Usuario findUsuarioById(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario).orElse(null);
    }

    public boolean existsUsuarioByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Transactional
    public void actualizarUsuario(Usuario usuario) {
        if (usuario != null && usuario.getIdUsuario() != null) {
            usuario.setFechaActualizacion(LocalDateTime.now());
            usuarioRepository.save(usuario);
        }
    }

    @Transactional
    public void registrarAcceso(Integer idCliente) {
        Cliente cliente = findById(idCliente);
        if (cliente != null && cliente.getIdUsuario() != null) {
            Usuario usuario = findUsuarioById(cliente.getIdUsuario());
            if (usuario != null) {
                usuario.setUltimoAcceso(LocalDateTime.now());
                usuario.setFechaActualizacion(LocalDateTime.now());
                usuarioRepository.save(usuario);
            }
        }
    }

    public LocalDateTime getUltimoAcceso(Integer idCliente) {
        Cliente cliente = findById(idCliente);
        if (cliente == null || cliente.getIdUsuario() == null) {
            return null;
        }
        Usuario usuario = findUsuarioById(cliente.getIdUsuario());
        return usuario != null ? usuario.getUltimoAcceso() : null;
    }

    public long count() {
        return clienteRepository.count();
    }

    public long countActive() {
        return clienteRepository.countActive();
    }

    public long countDisabled() {
        return clienteRepository.countDisabled();
    }

    public long countByEstado(String estado) {
        return clienteRepository.countByEstado(estado);
    }

    public boolean hasProyectos(Integer idCliente) {
        return clienteRepository.hasProyectos(idCliente);
    }

    public boolean hasCotizaciones(Integer idCliente) {
        return clienteRepository.hasCotizaciones(idCliente);
    }

    public boolean hasPagos(Integer idCliente) {
        return clienteRepository.hasPagos(idCliente);
    }

    public boolean puedeSerEliminado(Integer idCliente) {
        return !hasProyectos(idCliente) && !hasCotizaciones(idCliente) && !hasPagos(idCliente);
    }

    public boolean isActivo(Integer idCliente) {
        Cliente cliente = findById(idCliente);
        return cliente != null && cliente.isActivo();
    }

    public boolean hasUsuario(Integer idCliente) {
        Cliente cliente = findById(idCliente);
        return cliente != null && cliente.getIdUsuario() != null;
    }

    public String getNombreCliente(Integer idCliente) {
        Cliente cliente = findById(idCliente);
        return cliente != null ? cliente.getNombreRazonSocial() : "Cliente no encontrado";
    }

    public String getResumenCliente(Integer idCliente) {
        Cliente cliente = findById(idCliente);
        if (cliente == null) {
            return "Cliente no encontrado";
        }
        return cliente.getNombreRazonSocial() + 
               (cliente.getRuc() != null ? " (" + cliente.getRuc() + ")" : "");
    }

    public String getEstadoBadgeClass(Integer idCliente) {
        Cliente cliente = findById(idCliente);
        if (cliente == null) {
            return "badge-secondary";
        }
        return cliente.isActivo() ? "badge-success" : "badge-danger";
    }

    public String getEstadoLegible(Integer idCliente) {
        Cliente cliente = findById(idCliente);
        if (cliente == null) {
            return "No existe";
        }
        return cliente.isActivo() ? "Activo" : "Deshabilitado";
    }

    public boolean isPersonaNatural(Integer idCliente) {
        Cliente cliente = findById(idCliente);
        return cliente != null && cliente.isPersonaNatural();
    }

    public boolean isPersonaJuridica(Integer idCliente) {
        Cliente cliente = findById(idCliente);
        return cliente != null && cliente.isPersonaJuridica();
    }
}