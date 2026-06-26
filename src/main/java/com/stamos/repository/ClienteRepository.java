package com.stamos.repository;

import com.stamos.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByRuc(String ruc);

    Optional<Cliente> findByEmail(String email);

    Optional<Cliente> findByTelefono(String telefono);

    Optional<Cliente> findByNombreRazonSocial(String nombreRazonSocial);

    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nombreRazonSocial) LIKE LOWER(CONCAT('%', :nombre, '%')) ORDER BY c.nombreRazonSocial ASC")
    List<Cliente> findByNombreRazonSocialContaining(@Param("nombre") String nombre);

    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nombreRazonSocial) LIKE LOWER(CONCAT('%', :nombre, '%')) ORDER BY c.nombreRazonSocial ASC")
    Page<Cliente> findByNombreRazonSocialContaining(@Param("nombre") String nombre, Pageable pageable);

    @Query("SELECT c FROM Cliente c WHERE " +
           "LOWER(c.nombreRazonSocial) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "c.ruc LIKE CONCAT('%', :searchText, '%') OR " +
           "c.telefono LIKE CONCAT('%', :searchText, '%') OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "ORDER BY c.nombreRazonSocial ASC")
    List<Cliente> search(@Param("searchText") String searchText);

    @Query("SELECT c FROM Cliente c WHERE " +
           "LOWER(c.nombreRazonSocial) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "c.ruc LIKE CONCAT('%', :searchText, '%') OR " +
           "c.telefono LIKE CONCAT('%', :searchText, '%') OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "ORDER BY c.nombreRazonSocial ASC")
    Page<Cliente> search(@Param("searchText") String searchText, Pageable pageable);

    @Query("SELECT c FROM Cliente c WHERE " +
           "LOWER(c.nombreRazonSocial) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "c.ruc LIKE CONCAT('%', :searchText, '%') " +
           "ORDER BY c.nombreRazonSocial ASC")
    List<Cliente> findByNombreOrRuc(@Param("searchText") String searchText);

    @Query("SELECT c FROM Cliente c WHERE " +
           "LOWER(c.nombreRazonSocial) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "c.ruc LIKE CONCAT('%', :searchText, '%') " +
           "ORDER BY c.nombreRazonSocial ASC")
    Page<Cliente> findByNombreOrRuc(@Param("searchText") String searchText, Pageable pageable);

    List<Cliente> findByEstado(String estado);

    Page<Cliente> findByEstado(String estado, Pageable pageable);

    @Query("SELECT c FROM Cliente c WHERE c.estado = 'Activo' ORDER BY c.nombreRazonSocial ASC")
    List<Cliente> findActive();

    @Query("SELECT c FROM Cliente c WHERE c.estado = 'Activo' ORDER BY c.nombreRazonSocial ASC")
    Page<Cliente> findActive(Pageable pageable);

    @Query("SELECT c FROM Cliente c WHERE c.estado = 'Deshabilitado' ORDER BY c.nombreRazonSocial ASC")
    List<Cliente> findDisabled();

    List<Cliente> findByTipoPersona(String tipoPersona);

    @Query("SELECT c FROM Cliente c WHERE c.tipoPersona = 'Natural' ORDER BY c.nombreRazonSocial ASC")
    List<Cliente> findPersonasNaturales();

    @Query("SELECT c FROM Cliente c WHERE c.tipoPersona = 'Juridica' ORDER BY c.nombreRazonSocial ASC")
    List<Cliente> findPersonasJuridicas();

    List<Cliente> findByTipoPersonaAndEstado(String tipoPersona, String estado);

    @Query("SELECT c FROM Cliente c WHERE c.fechaRegistro BETWEEN :fechaInicio AND :fechaFin ORDER BY c.fechaRegistro DESC")
    List<Cliente> findByFechaRegistroBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                             @Param("fechaFin") LocalDateTime fechaFin);


    @Query("SELECT c FROM Cliente c WHERE c.fechaRegistro BETWEEN :fechaInicio AND :fechaFin ORDER BY c.fechaRegistro DESC")
    Page<Cliente> findByFechaRegistroBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                             @Param("fechaFin") LocalDateTime fechaFin,
                                             Pageable pageable);

    @Query("SELECT c FROM Cliente c WHERE DATE(c.fechaRegistro) = CURRENT_DATE ORDER BY c.fechaRegistro DESC")
    List<Cliente> findRegistradosHoy();


    @Query("SELECT c FROM Cliente c WHERE c.fechaRegistro >= :fecha ORDER BY c.fechaRegistro DESC")
    List<Cliente> findRegistradosUltimosDias(@Param("fecha") LocalDateTime fecha);


    List<Cliente> findByIdUsuarioRegis(Integer idUsuarioRegis);

    Optional<Cliente> findByIdUsuario(Integer idUsuario);

    long countByEstado(String estado);

    long countByTipoPersona(String tipoPersona);

    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.tipoPersona = :tipoPersona AND c.estado = 'Activo'")
    long countActivosByTipoPersona(@Param("tipoPersona") String tipoPersona);

    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.estado = 'Activo'")
    long countActive();

    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.estado = 'Deshabilitado'")
    long countDisabled();

    @Query("SELECT COUNT(c) FROM Cliente c WHERE MONTH(c.fechaRegistro) = :month AND YEAR(c.fechaRegistro) = :year")
    long countByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT DATE(c.fechaRegistro) as fecha, COUNT(c) as total " +
           "FROM Cliente c " +
           "WHERE c.fechaRegistro >= :fechaInicio " +
           "GROUP BY DATE(c.fechaRegistro) " +
           "ORDER BY DATE(c.fechaRegistro) DESC")
    List<Object[]> countRegistrosByDay(@Param("fechaInicio") LocalDateTime fechaInicio);


    boolean existsByRuc(String ruc);
    boolean existsByEmail(String email);
    boolean existsByTelefono(String telefono);
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cliente c " +
           "WHERE c.ruc = :ruc AND c.idCliente != :idCliente")
    boolean existsByRucAndIdNot(@Param("ruc") String ruc, @Param("idCliente") Integer idCliente);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cliente c " +
           "WHERE c.email = :email AND c.idCliente != :idCliente")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("idCliente") Integer idCliente);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Proyecto p WHERE p.idCliente = :idCliente")
    boolean hasProyectos(@Param("idCliente") Integer idCliente);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cotizacion c WHERE c.idCliente = :idCliente")
    boolean hasCotizaciones(@Param("idCliente") Integer idCliente);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Pago p " +
           "WHERE p.idProyecto IN (SELECT pr.idProyecto FROM Proyecto pr WHERE pr.idCliente = :idCliente)")
    boolean hasPagos(@Param("idCliente") Integer idCliente);

    @Query("SELECT c FROM Cliente c WHERE c.idUsuario IS NOT NULL AND c.estado = 'Activo' ORDER BY c.nombreRazonSocial ASC")
    List<Cliente> findClientesWithUsuario();

    @Query("SELECT c FROM Cliente c WHERE c.idUsuario IS NOT NULL AND c.estado = 'Activo' ORDER BY c.nombreRazonSocial ASC")
    Page<Cliente> findClientesWithUsuario(Pageable pageable);

    @Query("SELECT c FROM Cliente c WHERE c.idUsuario IS NULL ORDER BY c.nombreRazonSocial ASC")
    List<Cliente> findClientesWithoutUsuario();

    @Query("UPDATE Cliente c SET c.estado = 'Deshabilitado'")
    void desactivarAll();

    @Query("UPDATE Cliente c SET c.estado = 'Activo'")
    void activarAll();

    @Query("UPDATE Cliente c SET c.idUsuario = :idUsuario WHERE c.idCliente = :idCliente")
    void updateIdUsuario(@Param("idCliente") Integer idCliente, @Param("idUsuario") Integer idUsuario);

    @Query("UPDATE Cliente c SET c.idUsuario = NULL WHERE c.idCliente = :idCliente")
    void removeUsuario(@Param("idCliente") Integer idCliente);

    public long countByIdUsuarioRegis(Integer idUsuario);
}