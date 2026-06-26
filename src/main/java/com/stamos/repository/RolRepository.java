package com.stamos.repository;

import com.stamos.model.Rol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

    Optional<Rol> findByNombreRol(String nombreRol);

    @Query("SELECT r FROM Rol r WHERE LOWER(r.nombreRol) = LOWER(:nombreRol)")
    Optional<Rol> findByNombreRolIgnoreCase(@Param("nombreRol") String nombreRol);

    boolean existsByNombreRol(String nombreRol);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Rol r " +
           "WHERE r.nombreRol = :nombreRol AND r.idRol != :idRol")
    boolean existsByNombreRolAndIdNot(@Param("nombreRol") String nombreRol,
                                      @Param("idRol") Integer idRol);

    @Query("SELECT r FROM Rol r WHERE LOWER(r.descripcion) LIKE LOWER(CONCAT('%', :descripcion, '%')) ORDER BY r.nombreRol ASC")
    List<Rol> findByDescripcionContaining(@Param("descripcion") String descripcion);

    @Query("SELECT r FROM Rol r WHERE LOWER(r.descripcion) LIKE LOWER(CONCAT('%', :descripcion, '%')) ORDER BY r.nombreRol ASC")
    Page<Rol> findByDescripcionContaining(@Param("descripcion") String descripcion, Pageable pageable);

    @Query("SELECT r FROM Rol r WHERE r.nombreRol != 'Cliente' ORDER BY r.nombreRol ASC")
    List<Rol> findRolesInternos();

    @Query("SELECT r FROM Rol r WHERE r.nombreRol != 'Cliente' ORDER BY r.nombreRol ASC")
    Page<Rol> findRolesInternos(Pageable pageable);

    @Query("SELECT r FROM Rol r WHERE r.nombreRol = 'Cliente' ORDER BY r.nombreRol ASC")
    List<Rol> findRolesCliente();

    @Query("SELECT r FROM Rol r WHERE r.nombreRol = 'Administrador' ORDER BY r.nombreRol ASC")
    List<Rol> findRolesAdministracion();

    @Query("SELECT r FROM Rol r WHERE r.nombreRol IN ('Administrador', 'Vendedor') ORDER BY r.nombreRol ASC")
    List<Rol> findRolesVentas();

    @Query("SELECT r FROM Rol r WHERE r.nombreRol IN ('Administrador', 'Producción') ORDER BY r.nombreRol ASC")
    List<Rol> findRolesProduccion();

    @Query("SELECT r FROM Rol r WHERE r.nombreRol IN ('Administrador', 'Diseñador') ORDER BY r.nombreRol ASC")
    List<Rol> findRolesDiseno();

    @Query("SELECT r FROM Rol r WHERE r.nombreRol IN ('Administrador', 'Soporte Técnico') ORDER BY r.nombreRol ASC")
    List<Rol> findRolesSoporte();

    @Query("SELECT r FROM Rol r WHERE " +
           "LOWER(r.nombreRol) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(r.descripcion) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "ORDER BY r.nombreRol ASC")
    List<Rol> search(@Param("searchText") String searchText);

    @Query("SELECT r FROM Rol r WHERE " +
           "LOWER(r.nombreRol) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(r.descripcion) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "ORDER BY r.nombreRol ASC")
    Page<Rol> search(@Param("searchText") String searchText, Pageable pageable);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.idRol = :idRol")
    long countUsuariosByRol(@Param("idRol") Integer idRol);

    @Query("SELECT r.idRol, r.nombreRol, COUNT(u) as total " +
           "FROM Rol r LEFT JOIN Usuario u ON r.idRol = u.idRol " +
           "GROUP BY r.idRol, r.nombreRol " +
           "ORDER BY r.nombreRol ASC")
    List<Object[]> countUsuariosByRolResumen();

    @Query("SELECT r.idRol, r.nombreRol, COUNT(u) as total " +
           "FROM Rol r LEFT JOIN Usuario u ON r.idRol = u.idRol AND u.activo = true " +
           "GROUP BY r.idRol, r.nombreRol " +
           "ORDER BY r.nombreRol ASC")
    List<Object[]> countUsuariosActivosByRolResumen();

    @Query("SELECT COUNT(r) FROM Rol r")
    long countTotal();

    @Query("SELECT r FROM Rol r WHERE r.nombreRol IN ('Administrador', 'Vendedor', 'Diseñador', 'Producción', 'Soporte Técnico', 'Cliente') ORDER BY r.idRol ASC")
    List<Rol> findRolesPredefinidos();

    @Query("SELECT r FROM Rol r WHERE r.nombreRol IN ('Administrador', 'Vendedor', 'Diseñador', 'Producción', 'Soporte Técnico') ORDER BY r.nombreRol ASC")
    List<Rol> findRolesConPermisosEstado();

    @Query("SELECT r FROM Rol r WHERE r.nombreRol = 'Administrador' ORDER BY r.nombreRol ASC")
    List<Rol> findRolesConPermisoAnular();

    @Query("SELECT r FROM Rol r WHERE r.nombreRol IN ('Administrador', 'Soporte Técnico') ORDER BY r.nombreRol ASC")
    List<Rol> findRolesConPermisoEntregar();

    @Query("SELECT r FROM Rol r WHERE r.nombreRol IN ('Administrador', 'Vendedor') ORDER BY r.nombreRol ASC")
    List<Rol> findRolesConPermisoIniciar();

    @Modifying
    @Transactional
    @Query("UPDATE Rol r SET r.descripcion = :descripcion WHERE r.idRol = :idRol")
    void updateDescripcion(@Param("idRol") Integer idRol,
                           @Param("descripcion") String nuevaDescripcion);

    @Modifying
    @Transactional
    @Query("UPDATE Rol r SET r.nombreRol = :nombre WHERE r.idRol = :idRol")
    void updateNombreRol(@Param("idRol") Integer idRol,
                         @Param("nombre") String nuevoNombre);

    @Modifying
    @Transactional
    @Query("DELETE FROM Rol r WHERE r.nombreRol = :nombre")
    void deleteByNombreRol(@Param("nombre") String nombreRol);

    @Modifying
    @Transactional
    @Query("DELETE FROM Rol r WHERE r.idRol NOT IN (SELECT DISTINCT u.idRol FROM Usuario u)")
    int deleteUnused();

    @Modifying
    @Transactional
    @Query("DELETE FROM Rol r WHERE r.nombreRol NOT IN ('Administrador', 'Vendedor', 'Diseñador', 'Producción', 'Soporte Técnico', 'Cliente')")
    int deleteCustomRoles();

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuario u WHERE u.idRol = :idRol")
    boolean hasUsuarios(@Param("idRol") Integer idRol);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Rol r " +
           "WHERE r.nombreRol = :nombre AND r.nombreRol IN ('Administrador', 'Vendedor', 'Diseñador', 'Producción', 'Soporte Técnico', 'Cliente')")
    boolean isPredefinido(@Param("nombre") String nombreRol);

    @Query("SELECT r.idRol, r.nombreRol FROM Rol r ORDER BY r.nombreRol ASC")
    List<Object[]> findForSelect();

    @Query("SELECT r.idRol, r.nombreRol FROM Rol r WHERE r.nombreRol != 'Cliente' ORDER BY r.nombreRol ASC")
    List<Object[]> findInternosForSelect();

    @Query("SELECT r.idRol, r.nombreRol FROM Rol r WHERE r.nombreRol = 'Cliente' ORDER BY r.nombreRol ASC")
    List<Object[]> findClienteForSelect();

    @Query("SELECT r.idRol, r.nombreRol, COUNT(u) as totalUsuarios " +
           "FROM Rol r LEFT JOIN Usuario u ON r.idRol = u.idRol " +
           "GROUP BY r.idRol, r.nombreRol " +
           "ORDER BY r.nombreRol ASC")
    List<Object[]> getEstadisticasDashboard();

    @Query("SELECT r.idRol, r.nombreRol, COUNT(u) as totalUsuarios " +
           "FROM Rol r LEFT JOIN Usuario u ON r.idRol = u.idRol AND u.activo = true " +
           "GROUP BY r.idRol, r.nombreRol " +
           "ORDER BY r.nombreRol ASC")
    List<Object[]> getEstadisticasDashboardActivos();

    @Query("SELECT r.idRol, r.nombreRol, COUNT(u) as total " +
           "FROM Rol r JOIN Usuario u ON r.idRol = u.idRol " +
           "GROUP BY r.idRol, r.nombreRol " +
           "ORDER BY COUNT(u) DESC")
    List<Object[]> findRolMasUsuarios(@Param("limit") int limit);
}