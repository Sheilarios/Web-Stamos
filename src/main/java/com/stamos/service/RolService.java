package com.stamos.service;

import com.stamos.model.Rol;
import com.stamos.model.Usuario;
import com.stamos.repository.RolRepository;
import com.stamos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public static final String ROL_ADMINISTRADOR = "Administrador";
    public static final String ROL_VENDEDOR = "Vendedor";
    public static final String ROL_DISEÑADOR = "Diseñador";
    public static final String ROL_PRODUCCION = "Producción";
    public static final String ROL_SOPORTE_TECNICO = "Soporte Técnico";
    public static final String ROL_CLIENTE = "Cliente";

    @Transactional
    public Rol crear(String nombreRol, String descripcion) {
        if (rolRepository.existsByNombreRol(nombreRol)) {
            throw new RuntimeException("Ya existe un rol con el nombre: " + nombreRol);
        }

        Rol rol = new Rol();
        rol.setNombreRol(nombreRol);
        rol.setDescripcion(descripcion);
        rol.setFechaCreacion(LocalDateTime.now());

        return rolRepository.save(rol);
    }

    @Transactional
    public Rol actualizar(Integer idRol, String nombreRol, String descripcion) {
        Rol existente = rolRepository.findById(idRol)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + idRol));

        if (!existente.getNombreRol().equals(nombreRol) && 
            rolRepository.existsByNombreRolAndIdNot(nombreRol, idRol)) {
            throw new RuntimeException("Ya existe otro rol con el nombre: " + nombreRol);
        }

        existente.setNombreRol(nombreRol);
        existente.setDescripcion(descripcion);

        return rolRepository.save(existente);
    }

    @Transactional
    public void eliminar(Integer idRol) {
        Rol rol = rolRepository.findById(idRol)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + idRol));

        long cantidadUsuarios = usuarioRepository.countByIdRol(idRol);
        if (cantidadUsuarios > 0) {
            throw new RuntimeException("No se puede eliminar el rol porque tiene " + 
                    cantidadUsuarios + " usuarios asociados");
        }

        rolRepository.delete(rol);
    }

    public Rol findById(Integer idRol) {
        return rolRepository.findById(idRol).orElse(null);
    }

    public Rol findByNombre(String nombreRol) {
        return rolRepository.findByNombreRol(nombreRol).orElse(null);
    }

    public List<Rol> findAll() {
        return rolRepository.findAll();
    }

    public Page<Rol> findAll(Pageable pageable) {
        return rolRepository.findAll(pageable);
    }

    public List<Rol> findRolesInternos() {
        return rolRepository.findRolesInternos();
    }

    public Page<Rol> findRolesInternos(Pageable pageable) {
        return rolRepository.findRolesInternos(pageable);
    }

    public List<Rol> findRolesCliente() {
        return rolRepository.findRolesCliente();
    }

    public List<Rol> findRolesAdministracion() {
        return rolRepository.findRolesAdministracion();
    }

    public List<Rol> findRolesVentas() {
        return rolRepository.findRolesVentas();
    }

    public List<Rol> findRolesProduccion() {
        return rolRepository.findRolesProduccion();
    }

    public List<Rol> findRolesDiseno() {
        return rolRepository.findRolesDiseno();
    }

    public List<Rol> findRolesSoporte() {
        return rolRepository.findRolesSoporte();
    }

    public List<Rol> search(String searchText) {
        return rolRepository.search(searchText);
    }

    public Page<Rol> search(String searchText, Pageable pageable) {
        return rolRepository.search(searchText, pageable);
    }

    public long count() {
        return rolRepository.count();
    }

    public long countUsuariosByRol(Integer idRol) {
        return rolRepository.countUsuariosByRol(idRol);
    }

    public List<Object[]> countUsuariosByRolResumen() {
        return rolRepository.countUsuariosByRolResumen();
    }

    public List<Object[]> countUsuariosActivosByRolResumen() {
        return rolRepository.countUsuariosActivosByRolResumen();
    }

    public boolean existsByNombre(String nombreRol) {
        return rolRepository.existsByNombreRol(nombreRol);
    }

    public boolean hasUsuarios(Integer idRol) {
        return rolRepository.hasUsuarios(idRol);
    }

    public boolean isPredefinido(String nombreRol) {
        return rolRepository.isPredefinido(nombreRol);
    }

    public List<Rol> getRolesPredefinidos() {
        return Rol.obtenerRolesPredefinidos();
    }

    @Transactional
    public void inicializarRolesPredefinidos() {
        List<Rol> predefinidos = getRolesPredefinidos();

        for (Rol rol : predefinidos) {
            if (!existsByNombre(rol.getNombreRol())) {
                crear(rol.getNombreRol(), rol.getDescripcion());
            }
        }
    }

    public String getNombreRol(Integer idRol) {
        Rol rol = findById(idRol);
        return rol != null ? rol.getNombreRol() : null;
    }

    public String getNombreLegible(Integer idRol) {
        Rol rol = findById(idRol);
        return rol != null ? rol.getNombreLegible() : "Rol no encontrado";
    }

    public String getColorRol(Integer idRol) {
        Rol rol = findById(idRol);
        return rol != null ? rol.getColor() : "#6B7280";
    }

    public String getIconoRol(Integer idRol) {
        Rol rol = findById(idRol);
        return rol != null ? rol.getIcono() : "ti ti-user";
    }

    public String getRolBadgeClass(String nombreRol) {
        if (nombreRol == null) return "badge-secondary";

        return switch (nombreRol) {
            case ROL_ADMINISTRADOR -> "badge-danger";
            case ROL_VENDEDOR -> "badge-primary";
            case ROL_DISEÑADOR -> "badge-purple";
            case ROL_PRODUCCION -> "badge-warning";
            case ROL_SOPORTE_TECNICO -> "badge-success";
            case ROL_CLIENTE -> "badge-secondary";
            default -> "badge-secondary";
        };
    }

    public String getRolColor(String nombreRol) {
        if (nombreRol == null) return "#6B7280";

        return switch (nombreRol) {
            case ROL_ADMINISTRADOR -> "#EF4444";
            case ROL_VENDEDOR -> "#3B82F6";
            case ROL_DISEÑADOR -> "#8B5CF6";
            case ROL_PRODUCCION -> "#F59E0B";
            case ROL_SOPORTE_TECNICO -> "#10B981";
            case ROL_CLIENTE -> "#6B7280";
            default -> "#6B7280";
        };
    }

    public String getRolIcono(String nombreRol) {
        if (nombreRol == null) return "ti ti-user";

        return switch (nombreRol) {
            case ROL_ADMINISTRADOR -> "ti ti-shield";
            case ROL_VENDEDOR -> "ti ti-user-check";
            case ROL_DISEÑADOR -> "ti ti-pencil";
            case ROL_PRODUCCION -> "ti ti-tools";
            case ROL_SOPORTE_TECNICO -> "ti ti-headset";
            case ROL_CLIENTE -> "ti ti-user";
            default -> "ti ti-user";
        };
    }

    public boolean esAdministrador(Integer idRol) {
        Rol rol = findById(idRol);
        return rol != null && rol.esAdministrador();
    }

    public boolean esVendedor(Integer idRol) {
        Rol rol = findById(idRol);
        return rol != null && rol.esVendedor();
    }

    public boolean esDiseñador(Integer idRol) {
        Rol rol = findById(idRol);
        return rol != null && rol.esDiseñador();
    }

    public boolean esProduccion(Integer idRol) {
        Rol rol = findById(idRol);
        return rol != null && rol.esProduccion();
    }

    public boolean esSoporteTecnico(Integer idRol) {
        Rol rol = findById(idRol);
        return rol != null && rol.esSoporteTecnico();
    }

    public boolean esCliente(Integer idRol) {
        Rol rol = findById(idRol);
        return rol != null && rol.esCliente();
    }

    public boolean esRolInterno(Integer idRol) {
        Rol rol = findById(idRol);
        return rol != null && rol.esRolInterno();
    }

    public String getRolSeguridad(String nombreRol) {
        if (nombreRol == null) return "ROLE_USER";
        return "ROLE_" + nombreRol.toUpperCase().replace(" ", "_").replace("Ó", "O");
    }

    @Transactional
    public Rol crearRolPredefinido(String nombreRol, String descripcion) {
        if (!existsByNombre(nombreRol)) {
            return crear(nombreRol, descripcion);
        }
        return findByNombre(nombreRol);
    }

    public List<Object[]> getEstadisticasDashboard() {
        return rolRepository.getEstadisticasDashboard();
    }

    public List<Object[]> getEstadisticasDashboardActivos() {
        return rolRepository.getEstadisticasDashboardActivos();
    }

    public List<Object[]> findRolMasUsuarios(int limit) {
        return rolRepository.findRolMasUsuarios(limit);
    }

    public List<Object[]> findForSelect() {
        return rolRepository.findForSelect();
    }

    public List<Object[]> findInternosForSelect() {
        return rolRepository.findInternosForSelect();
    }

    public List<Object[]> findClienteForSelect() {
        return rolRepository.findClienteForSelect();
    }
}