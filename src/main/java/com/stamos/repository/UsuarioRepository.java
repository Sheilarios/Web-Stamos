package com.stamos.repository;

import com.stamos.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmail(String email);

    @Query("SELECT u FROM Usuario u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<Usuario> findByEmailIgnoreCase(@Param("email") String email);

    boolean existsByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuario u " +
           "WHERE LOWER(u.email) = LOWER(:email) AND u.idUsuario != :idUsuario")
    boolean existsByEmailAndIdNot(@Param("email") String email,
                                  @Param("idUsuario") Integer idUsuario);

    List<Usuario> findByIdRol(Integer idRol);

    Page<Usuario> findByIdRol(Integer idRol, Pageable pageable);

    @Query("SELECT u FROM Usuario u WHERE u.idRol = :idRol ORDER BY u.nombreCompleto ASC")
    List<Usuario> findByIdRolOrderByNombre(@Param("idRol") Integer idRol);

    @Query("SELECT u FROM Usuario u JOIN Rol r ON u.idRol = r.idRol WHERE r.nombreRol = :nombreRol ORDER BY u.nombreCompleto ASC")
    List<Usuario> findByNombreRol(@Param("nombreRol") String nombreRol);

    @Query("SELECT u FROM Usuario u JOIN Rol r ON u.idRol = r.idRol WHERE r.nombreRol = :nombreRol ORDER BY u.nombreCompleto ASC")
    Page<Usuario> findByNombreRol(@Param("nombreRol") String nombreRol, Pageable pageable);

    @Query("SELECT u FROM Usuario u JOIN Rol r ON u.idRol = r.idRol WHERE r.nombreRol = 'Administrador' ORDER BY u.nombreCompleto ASC")
    List<Usuario> findAdministradores();

    @Query("SELECT u FROM Usuario u JOIN Rol r ON u.idRol = r.idRol WHERE r.nombreRol = 'Vendedor' ORDER BY u.nombreCompleto ASC")
    List<Usuario> findVendedores();

    @Query("SELECT u FROM Usuario u JOIN Rol r ON u.idRol = r.idRol WHERE r.nombreRol = 'Diseñador' ORDER BY u.nombreCompleto ASC")
    List<Usuario> findDisenadores();

    @Query("SELECT u FROM Usuario u JOIN Rol r ON u.idRol = r.idRol WHERE r.nombreRol = 'Producción' ORDER BY u.nombreCompleto ASC")
    List<Usuario> findProduccion();

    @Query("SELECT u FROM Usuario u JOIN Rol r ON u.idRol = r.idRol WHERE r.nombreRol = 'Soporte Técnico' ORDER BY u.nombreCompleto ASC")
    List<Usuario> findSoporteTecnico();

    @Query("SELECT u FROM Usuario u JOIN Rol r ON u.idRol = r.idRol WHERE r.nombreRol = 'Cliente' ORDER BY u.nombreCompleto ASC")
    List<Usuario> findClientes();

    long countByIdRol(Integer idRol);

    @Query("SELECT COUNT(u) FROM Usuario u JOIN Rol r ON u.idRol = r.idRol WHERE r.nombreRol = :nombreRol")
    long countByNombreRol(@Param("nombreRol") String nombreRol);

    List<Usuario> findByActivo(Boolean activo);

    Page<Usuario> findByActivo(Boolean activo, Pageable pageable);

    @Query("SELECT u FROM Usuario u WHERE u.activo = true ORDER BY u.nombreCompleto ASC")
    List<Usuario> findActivos();

    @Query("SELECT u FROM Usuario u WHERE u.activo = true ORDER BY u.nombreCompleto ASC")
    Page<Usuario> findActivos(Pageable pageable);

    @Query("SELECT u FROM Usuario u WHERE u.activo = false ORDER BY u.nombreCompleto ASC")
    List<Usuario> findInactivos();

    @Query("SELECT u FROM Usuario u WHERE u.activo = false ORDER BY u.nombreCompleto ASC")
    Page<Usuario> findInactivos(Pageable pageable);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.activo = true")
    long countActivos();

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.activo = false")
    long countInactivos();

    @Query("SELECT u.activo, COUNT(u) as total FROM Usuario u GROUP BY u.activo")
    List<Object[]> countByEstadoResumen();

    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nombreCompleto) LIKE LOWER(CONCAT('%', :nombre, '%')) ORDER BY u.nombreCompleto ASC")
    List<Usuario> findByNombreCompletoContaining(@Param("nombre") String nombreCompleto);

    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nombreCompleto) LIKE LOWER(CONCAT('%', :nombre, '%')) ORDER BY u.nombreCompleto ASC")
    Page<Usuario> findByNombreCompletoContaining(@Param("nombre") String nombreCompleto, Pageable pageable);

    Optional<Usuario> findByTelefono(String telefono);

    @Query("SELECT u FROM Usuario u WHERE u.telefono LIKE CONCAT('%', :telefono, '%') ORDER BY u.nombreCompleto ASC")
    List<Usuario> findByTelefonoContaining(@Param("telefono") String telefono);

    @Query("SELECT u FROM Usuario u WHERE u.fechaCreacion BETWEEN :fechaInicio AND :fechaFin ORDER BY u.fechaCreacion DESC")
    List<Usuario> findByFechaCreacionBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                             @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT u FROM Usuario u WHERE u.fechaCreacion BETWEEN :fechaInicio AND :fechaFin ORDER BY u.fechaCreacion DESC")
    Page<Usuario> findByFechaCreacionBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                             @Param("fechaFin") LocalDateTime fechaFin,
                                             Pageable pageable);

    @Query("SELECT u FROM Usuario u WHERE DATE(u.fechaCreacion) = CURRENT_DATE ORDER BY u.fechaCreacion DESC")
    List<Usuario> findCreadosHoy();

    @Query("SELECT u FROM Usuario u WHERE u.ultimoAcceso BETWEEN :fechaInicio AND :fechaFin ORDER BY u.ultimoAcceso DESC")
    List<Usuario> findByUltimoAccesoBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                            @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT u FROM Usuario u WHERE DATE(u.ultimoAcceso) = CURRENT_DATE ORDER BY u.ultimoAcceso DESC")
    List<Usuario> findAccedieronHoy();

    @Query("SELECT u FROM Usuario u WHERE u.ultimoAcceso < :fecha OR u.ultimoAcceso IS NULL ORDER BY u.nombreCompleto ASC")
    List<Usuario> findInactivosDesde(@Param("fecha") LocalDateTime fecha);

    List<Usuario> findByIdRolAndActivo(Integer idRol, Boolean activo);

    Page<Usuario> findByIdRolAndActivo(Integer idRol, Boolean activo, Pageable pageable);

    @Query("SELECT u FROM Usuario u JOIN Rol r ON u.idRol = r.idRol WHERE r.nombreRol = :nombreRol AND u.activo = :activo ORDER BY u.nombreCompleto ASC")
    List<Usuario> findByNombreRolAndActivo(@Param("nombreRol") String nombreRol,
                                           @Param("activo") Boolean activo);

    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nombreCompleto) LIKE LOWER(CONCAT('%', :nombre, '%')) AND u.idRol = :idRol ORDER BY u.nombreCompleto ASC")
    List<Usuario> findByNombreContainingAndRol(@Param("nombre") String nombreCompleto,
                                               @Param("idRol") Integer idRol);

    @Query("SELECT u FROM Usuario u WHERE " +
           "LOWER(u.nombreCompleto) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "u.telefono LIKE CONCAT('%', :searchText, '%') " +
           "ORDER BY u.nombreCompleto ASC")
    List<Usuario> search(@Param("searchText") String searchText);

    @Query("SELECT u FROM Usuario u WHERE " +
           "LOWER(u.nombreCompleto) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "u.telefono LIKE CONCAT('%', :searchText, '%') " +
           "ORDER BY u.nombreCompleto ASC")
    Page<Usuario> search(@Param("searchText") String searchText, Pageable pageable);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE MONTH(u.fechaCreacion) = :month AND YEAR(u.fechaCreacion) = :year")
    long countByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT DATE(u.fechaCreacion) as fecha, COUNT(u) as total " +
           "FROM Usuario u " +
           "WHERE u.fechaCreacion >= :fechaInicio " +
           "GROUP BY DATE(u.fechaCreacion) " +
           "ORDER BY DATE(u.fechaCreacion) DESC")
    List<Object[]> countCreadosByDay(@Param("fechaInicio") LocalDateTime fechaInicio);

    @Query("SELECT r.nombreRol, u.activo, COUNT(u) as total " +
           "FROM Usuario u JOIN Rol r ON u.idRol = r.idRol " +
           "GROUP BY r.nombreRol, u.activo " +
           "ORDER BY r.nombreRol ASC, u.activo DESC")
    List<Object[]> countByRolAndEstado();

    @Query("SELECT COUNT(u) FROM Usuario u")
    long countTotal();

    @Query("SELECT u FROM Usuario u ORDER BY u.fechaCreacion DESC LIMIT :limit")
    List<Usuario> findTopN(@Param("limit") int limit);

    @Query("SELECT u FROM Usuario u WHERE u.activo = true ORDER BY u.fechaCreacion DESC LIMIT :limit")
    List<Usuario> findTopNActivos(@Param("limit") int limit);

    @Query("SELECT u FROM Usuario u WHERE u.idRol = :idRol ORDER BY u.fechaCreacion DESC LIMIT :limit")
    List<Usuario> findTopNByRol(@Param("idRol") Integer idRol, @Param("limit") int limit);

    @Modifying
    @Transactional
    @Query("UPDATE Usuario u SET u.passwordHash = :password, u.fechaActualizacion = CURRENT_TIMESTAMP WHERE u.idUsuario = :idUsuario")
    void updatePassword(@Param("idUsuario") Integer idUsuario,
                        @Param("password") String passwordHash);

    @Modifying
    @Transactional
    @Query("UPDATE Usuario u SET u.activo = :activo, u.fechaActualizacion = CURRENT_TIMESTAMP WHERE u.idUsuario = :idUsuario")
    void updateActivo(@Param("idUsuario") Integer idUsuario,
                      @Param("activo") Boolean activo);

    @Modifying
    @Transactional
    @Query("UPDATE Usuario u SET u.idRol = :idRol, u.fechaActualizacion = CURRENT_TIMESTAMP WHERE u.idUsuario = :idUsuario")
    void updateRol(@Param("idUsuario") Integer idUsuario,
                   @Param("idRol") Integer idRol);

    @Modifying
    @Transactional
    @Query("UPDATE Usuario u SET u.ultimoAcceso = CURRENT_TIMESTAMP, u.fechaActualizacion = CURRENT_TIMESTAMP WHERE u.idUsuario = :idUsuario")
    void updateUltimoAcceso(@Param("idUsuario") Integer idUsuario);

    @Modifying
    @Transactional
    @Query("UPDATE Usuario u SET u.email = :email, u.fechaActualizacion = CURRENT_TIMESTAMP WHERE u.idUsuario = :idUsuario")
    void updateEmail(@Param("idUsuario") Integer idUsuario,
                     @Param("email") String email);

    @Modifying
    @Transactional
    @Query("UPDATE Usuario u SET u.telefono = :telefono, u.fechaActualizacion = CURRENT_TIMESTAMP WHERE u.idUsuario = :idUsuario")
    void updateTelefono(@Param("idUsuario") Integer idUsuario,
                        @Param("telefono") String telefono);

    @Modifying
    @Transactional
    @Query("DELETE FROM Usuario u WHERE u.email = :email")
    void deleteByEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query("DELETE FROM Usuario u WHERE u.activo = false AND u.fechaCreacion < :fecha")
    int deleteInactivosOlderThan(@Param("fecha") LocalDateTime fecha);

    @Modifying
    @Transactional
    @Query("DELETE FROM Usuario u WHERE u.idRol = :idRol")
    void deleteByRol(@Param("idRol") Integer idRol);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuario u " +
           "JOIN Rol r ON u.idRol = r.idRol WHERE u.idUsuario = :idUsuario AND r.nombreRol = 'Administrador'")
    boolean isAdministrador(@Param("idUsuario") Integer idUsuario);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuario u " +
           "JOIN Rol r ON u.idRol = r.idRol WHERE u.idUsuario = :idUsuario AND r.nombreRol = :nombreRol")
    boolean hasRol(@Param("idUsuario") Integer idUsuario,
                   @Param("nombreRol") String nombreRol);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuario u " +
           "WHERE u.idUsuario = :idUsuario AND u.activo = true")
    boolean isActivo(@Param("idUsuario") Integer idUsuario);

    @Query("SELECT " +
           "COUNT(u) as total, " +
           "SUM(CASE WHEN u.activo = true THEN 1 ELSE 0 END) as activos, " +
           "SUM(CASE WHEN u.activo = false THEN 1 ELSE 0 END) as inactivos, " +
           "SUM(CASE WHEN r.nombreRol = 'Administrador' THEN 1 ELSE 0 END) as administradores, " +
           "SUM(CASE WHEN r.nombreRol = 'Vendedor' THEN 1 ELSE 0 END) as vendedores, " +
           "SUM(CASE WHEN r.nombreRol = 'Cliente' THEN 1 ELSE 0 END) as clientes " +
           "FROM Usuario u JOIN Rol r ON u.idRol = r.idRol")
    List<Object[]> getEstadisticasDashboard();

    @Query("SELECT r.nombreRol, COUNT(u) as total " +
           "FROM Rol r LEFT JOIN Usuario u ON r.idRol = u.idRol " +
           "GROUP BY r.idRol, r.nombreRol " +
           "ORDER BY r.nombreRol ASC")
    List<Object[]> getEstadisticasPorRol();

    @Query("SELECT u.idUsuario, u.nombreCompleto, u.email, r.nombreRol, u.activo, u.ultimoAcceso " +
           "FROM Usuario u JOIN Rol r ON u.idRol = r.idRol ORDER BY u.fechaCreacion DESC LIMIT :limit")
    List<Object[]> findUsuariosDashboard(@Param("limit") int limit);

    @Query("SELECT r.nombreRol FROM Rol r WHERE r.idRol = :idRol")
    String findNombreRolByIdRol(@Param("idRol") Integer idRol);

    public long countByActivo(boolean activo);
}