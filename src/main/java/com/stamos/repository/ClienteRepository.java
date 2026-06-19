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

/**
 * Repositorio para la entidad Cliente.
 * 
 * <p>Proporciona métodos para acceder y gestionar los clientes de STAMOS S.L.,
 * incluyendo búsquedas, filtros y consultas de métricas.</p>
 * 
 * <p>Implementa los requerimientos:</p>
 * <ul>
 *   <li>RN-01: Solo personal autorizado puede dar de alta clientes</li>
 *   <li>RN-02: Cliente debe proporcionar nombre, teléfono, email y tipo</li>
 *   <li>RN-03: Clientes con proyectos o deudas no pueden ser eliminados</li>
 *   <li>RF-01: Registrar clientes con nombre, teléfono, email y clasificación</li>
 *   <li>RF-02: Buscar clientes por nombre, RUC o teléfono</li>
 *   <li>RF-03: Mostrar historial de cotizaciones y proyectos por cliente</li>
 *   <li>RF-04: Impedir eliminación de clientes con registros asociados</li>
 * </ul>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    // ============================================
    // BÚSQUEDAS PRINCIPALES
    // ============================================

    /**
     * Busca un cliente por su RUC.
     * 
     * @param ruc RUC del cliente
     * @return Cliente encontrado
     */
    Optional<Cliente> findByRuc(String ruc);

    /**
     * Busca un cliente por su email.
     * 
     * @param email Email del cliente
     * @return Cliente encontrado
     */
    Optional<Cliente> findByEmail(String email);

    /**
     * Busca un cliente por su teléfono.
     * 
     * @param telefono Teléfono del cliente
     * @return Cliente encontrado
     */
    Optional<Cliente> findByTelefono(String telefono);

    /**
     * Busca un cliente por nombre o razón social.
     * 
     * @param nombreRazonSocial Nombre o razón social
     * @return Cliente encontrado
     */
    Optional<Cliente> findByNombreRazonSocial(String nombreRazonSocial);

    /**
     * Busca clientes por nombre o razón social que contengan el texto.
     * 
     * @param nombre Texto a buscar
     * @return Lista de clientes
     */
    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nombreRazonSocial) LIKE LOWER(CONCAT('%', :nombre, '%')) ORDER BY c.nombreRazonSocial ASC")
    List<Cliente> findByNombreRazonSocialContaining(@Param("nombre") String nombre);

    /**
     * Busca clientes por nombre o razón social que contengan el texto con paginación.
     * 
     * @param nombre Texto a buscar
     * @param pageable Configuración de paginación
     * @return Página de clientes
     */
    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nombreRazonSocial) LIKE LOWER(CONCAT('%', :nombre, '%')) ORDER BY c.nombreRazonSocial ASC")
    Page<Cliente> findByNombreRazonSocialContaining(@Param("nombre") String nombre, Pageable pageable);

    // ============================================
    // BÚSQUEDAS COMBINADAS
    // ============================================

    /**
     * Busca clientes por nombre, RUC o teléfono.
     * 
     * @param searchText Texto a buscar
     * @return Lista de clientes
     */
    @Query("SELECT c FROM Cliente c WHERE " +
           "LOWER(c.nombreRazonSocial) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "c.ruc LIKE CONCAT('%', :searchText, '%') OR " +
           "c.telefono LIKE CONCAT('%', :searchText, '%') OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "ORDER BY c.nombreRazonSocial ASC")
    List<Cliente> search(@Param("searchText") String searchText);

    /**
     * Busca clientes por nombre, RUC o teléfono con paginación.
     * 
     * @param searchText Texto a buscar
     * @param pageable Configuración de paginación
     * @return Página de clientes
     */
    @Query("SELECT c FROM Cliente c WHERE " +
           "LOWER(c.nombreRazonSocial) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "c.ruc LIKE CONCAT('%', :searchText, '%') OR " +
           "c.telefono LIKE CONCAT('%', :searchText, '%') OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "ORDER BY c.nombreRazonSocial ASC")
    Page<Cliente> search(@Param("searchText") String searchText, Pageable pageable);

    /**
     * Busca clientes por nombre o RUC.
     * 
     * @param searchText Texto a buscar
     * @return Lista de clientes
     */
    @Query("SELECT c FROM Cliente c WHERE " +
           "LOWER(c.nombreRazonSocial) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "c.ruc LIKE CONCAT('%', :searchText, '%') " +
           "ORDER BY c.nombreRazonSocial ASC")
    List<Cliente> findByNombreOrRuc(@Param("searchText") String searchText);

    /**
     * Busca clientes por nombre o RUC con paginación.
     * 
     * @param searchText Texto a buscar
     * @param pageable Configuración de paginación
     * @return Página de clientes
     */
    @Query("SELECT c FROM Cliente c WHERE " +
           "LOWER(c.nombreRazonSocial) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "c.ruc LIKE CONCAT('%', :searchText, '%') " +
           "ORDER BY c.nombreRazonSocial ASC")
    Page<Cliente> findByNombreOrRuc(@Param("searchText") String searchText, Pageable pageable);

    // ============================================
    // BÚSQUEDAS POR ESTADO
    // ============================================

    /**
     * Busca clientes por estado.
     * 
     * @param estado Estado del cliente (Activo, Deshabilitado)
     * @return Lista de clientes
     */
    List<Cliente> findByEstado(String estado);

    /**
     * Busca clientes por estado con paginación.
     * 
     * @param estado Estado del cliente (Activo, Deshabilitado)
     * @param pageable Configuración de paginación
     * @return Página de clientes
     */
    Page<Cliente> findByEstado(String estado, Pageable pageable);

    /**
     * Busca clientes activos.
     * 
     * @return Lista de clientes activos
     */
    @Query("SELECT c FROM Cliente c WHERE c.estado = 'Activo' ORDER BY c.nombreRazonSocial ASC")
    List<Cliente> findActive();

    /**
     * Busca clientes activos con paginación.
     * 
     * @param pageable Configuración de paginación
     * @return Página de clientes activos
     */
    @Query("SELECT c FROM Cliente c WHERE c.estado = 'Activo' ORDER BY c.nombreRazonSocial ASC")
    Page<Cliente> findActive(Pageable pageable);

    /**
     * Busca clientes deshabilitados.
     * 
     * @return Lista de clientes deshabilitados
     */
    @Query("SELECT c FROM Cliente c WHERE c.estado = 'Deshabilitado' ORDER BY c.nombreRazonSocial ASC")
    List<Cliente> findDisabled();

    // ============================================
    // BÚSQUEDAS POR TIPO DE PERSONA
    // ============================================

    /**
     * Busca clientes por tipo de persona.
     * 
     * @param tipoPersona Tipo de persona (Natural, Juridica)
     * @return Lista de clientes
     */
    List<Cliente> findByTipoPersona(String tipoPersona);

    /**
     * Busca clientes personas naturales.
     * 
     * @return Lista de clientes personas naturales
     */
    @Query("SELECT c FROM Cliente c WHERE c.tipoPersona = 'Natural' ORDER BY c.nombreRazonSocial ASC")
    List<Cliente> findPersonasNaturales();

    /**
     * Busca clientes personas jurídicas.
     * 
     * @return Lista de clientes personas jurídicas
     */
    @Query("SELECT c FROM Cliente c WHERE c.tipoPersona = 'Juridica' ORDER BY c.nombreRazonSocial ASC")
    List<Cliente> findPersonasJuridicas();

    /**
     * Busca clientes por tipo de persona y estado.
     * 
     * @param tipoPersona Tipo de persona
     * @param estado Estado del cliente
     * @return Lista de clientes
     */
    List<Cliente> findByTipoPersonaAndEstado(String tipoPersona, String estado);

    // ============================================
    // BÚSQUEDAS POR FECHAS
    // ============================================

    /**
     * Busca clientes registrados entre dos fechas.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de clientes
     */
    @Query("SELECT c FROM Cliente c WHERE c.fechaRegistro BETWEEN :fechaInicio AND :fechaFin ORDER BY c.fechaRegistro DESC")
    List<Cliente> findByFechaRegistroBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                             @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Busca clientes registrados entre dos fechas con paginación.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param pageable Configuración de paginación
     * @return Página de clientes
     */
    @Query("SELECT c FROM Cliente c WHERE c.fechaRegistro BETWEEN :fechaInicio AND :fechaFin ORDER BY c.fechaRegistro DESC")
    Page<Cliente> findByFechaRegistroBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                             @Param("fechaFin") LocalDateTime fechaFin,
                                             Pageable pageable);

    /**
     * Busca clientes registrados en el día actual.
     * 
     * @return Lista de clientes
     */
    @Query("SELECT c FROM Cliente c WHERE DATE(c.fechaRegistro) = CURRENT_DATE ORDER BY c.fechaRegistro DESC")
    List<Cliente> findRegistradosHoy();

    /**
     * Busca clientes registrados en los últimos N días.
     * 
     * @param days Número de días
     * @return Lista de clientes
     */
    @Query("SELECT c FROM Cliente c WHERE c.fechaRegistro >= :fecha ORDER BY c.fechaRegistro DESC")
    List<Cliente> findRegistradosUltimosDias(@Param("fecha") LocalDateTime fecha);

    // ============================================
    // BÚSQUEDAS POR USUARIO
    // ============================================

    /**
     * Busca clientes registrados por un usuario.
     * 
     * @param idUsuarioRegis ID del usuario que registró
     * @return Lista de clientes
     */
    List<Cliente> findByIdUsuarioRegis(Integer idUsuarioRegis);

    /**
     * Busca un cliente por usuario asociado.
     * 
     * @param idUsuario ID del usuario
     * @return Cliente encontrado
     */
    Optional<Cliente> findByIdUsuario(Integer idUsuario);

    // ============================================
    // MÉTRICAS Y CONTADORES
    // ============================================

    /**
     * Cuenta clientes por estado.
     * 
     * @param estado Estado del cliente
     * @return Número de clientes
     */
    long countByEstado(String estado);

    /**
     * Cuenta clientes por tipo de persona.
     * 
     * @param tipoPersona Tipo de persona
     * @return Número de clientes
     */
    long countByTipoPersona(String tipoPersona);

    /**
     * Cuenta clientes activos por tipo de persona.
     * 
     * @param tipoPersona Tipo de persona
     * @return Número de clientes
     */
    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.tipoPersona = :tipoPersona AND c.estado = 'Activo'")
    long countActivosByTipoPersona(@Param("tipoPersona") String tipoPersona);

    /**
     * Obtiene el total de clientes activos.
     * 
     * @return Número de clientes activos
     */
    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.estado = 'Activo'")
    long countActive();

    /**
     * Obtiene el total de clientes deshabilitados.
     * 
     * @return Número de clientes deshabilitados
     */
    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.estado = 'Deshabilitado'")
    long countDisabled();

    /**
     * Obtiene el total de clientes por mes.
     * 
     * @param month Mes (1-12)
     * @param year Año
     * @return Número de clientes
     */
    @Query("SELECT COUNT(c) FROM Cliente c WHERE MONTH(c.fechaRegistro) = :month AND YEAR(c.fechaRegistro) = :year")
    long countByMonthAndYear(@Param("month") int month, @Param("year") int year);

    /**
     * Obtiene el número de clientes registrados por día en el último mes.
     * 
     * @return Lista de objetos con fecha y conteo
     */
    @Query("SELECT DATE(c.fechaRegistro) as fecha, COUNT(c) as total " +
           "FROM Cliente c " +
           "WHERE c.fechaRegistro >= :fechaInicio " +
           "GROUP BY DATE(c.fechaRegistro) " +
           "ORDER BY DATE(c.fechaRegistro) DESC")
    List<Object[]> countRegistrosByDay(@Param("fechaInicio") LocalDateTime fechaInicio);

    // ============================================
    // VERIFICACIONES
    // ============================================

    /**
     * Verifica si existe un cliente con el RUC proporcionado.
     * 
     * @param ruc RUC del cliente
     * @return true si existe, false en caso contrario
     */
    boolean existsByRuc(String ruc);

    /**
     * Verifica si existe un cliente con el email proporcionado.
     * 
     * @param email Email del cliente
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmail(String email);

    /**
     * Verifica si existe un cliente con el teléfono proporcionado.
     * 
     * @param telefono Teléfono del cliente
     * @return true si existe, false en caso contrario
     */
    boolean existsByTelefono(String telefono);

    /**
     * Verifica si existe un cliente con el RUC proporcionado, excluyendo un ID.
     * 
     * @param ruc RUC del cliente
     * @param idCliente ID del cliente a excluir
     * @return true si existe, false en caso contrario
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cliente c " +
           "WHERE c.ruc = :ruc AND c.idCliente != :idCliente")
    boolean existsByRucAndIdNot(@Param("ruc") String ruc, @Param("idCliente") Integer idCliente);

    /**
     * Verifica si existe un cliente con el email proporcionado, excluyendo un ID.
     * 
     * @param email Email del cliente
     * @param idCliente ID del cliente a excluir
     * @return true si existe, false en caso contrario
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cliente c " +
           "WHERE c.email = :email AND c.idCliente != :idCliente")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("idCliente") Integer idCliente);

    /**
     * Verifica si un cliente tiene proyectos asociados.
     * 
     * @param idCliente ID del cliente
     * @return true si tiene proyectos, false en caso contrario
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Proyecto p WHERE p.idCliente = :idCliente")
    boolean hasProyectos(@Param("idCliente") Integer idCliente);

    /**
     * Verifica si un cliente tiene cotizaciones asociadas.
     * 
     * @param idCliente ID del cliente
     * @return true si tiene cotizaciones, false en caso contrario
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cotizacion c WHERE c.idCliente = :idCliente")
    boolean hasCotizaciones(@Param("idCliente") Integer idCliente);

    /**
     * Verifica si un cliente tiene pagos asociados.
     * 
     * @param idCliente ID del cliente
     * @return true si tiene pagos, false en caso contrario
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Pago p " +
           "WHERE p.idProyecto IN (SELECT pr.idProyecto FROM Proyecto pr WHERE pr.idCliente = :idCliente)")
    boolean hasPagos(@Param("idCliente") Integer idCliente);

    // ============================================
    // MÉTODOS PARA EL PORTAL DEL CLIENTE
    // ============================================

    /**
     * Busca clientes con usuario asociado y activos.
     * 
     * @return Lista de clientes
     */
    @Query("SELECT c FROM Cliente c WHERE c.idUsuario IS NOT NULL AND c.estado = 'Activo' ORDER BY c.nombreRazonSocial ASC")
    List<Cliente> findClientesWithUsuario();

    /**
     * Busca clientes con usuario asociado y activos con paginación.
     * 
     * @param pageable Configuración de paginación
     * @return Página de clientes
     */
    @Query("SELECT c FROM Cliente c WHERE c.idUsuario IS NOT NULL AND c.estado = 'Activo' ORDER BY c.nombreRazonSocial ASC")
    Page<Cliente> findClientesWithUsuario(Pageable pageable);

    /**
     * Busca clientes sin usuario asociado.
     * 
     * @return Lista de clientes
     */
    @Query("SELECT c FROM Cliente c WHERE c.idUsuario IS NULL ORDER BY c.nombreRazonSocial ASC")
    List<Cliente> findClientesWithoutUsuario();

    // ============================================
    // MÉTODOS DE ACTUALIZACIÓN MASIVA
    // ============================================

    /**
     * Desactiva todos los clientes.
     */
    @Query("UPDATE Cliente c SET c.estado = 'Deshabilitado'")
    void desactivarAll();

    /**
     * Activa todos los clientes.
     */
    @Query("UPDATE Cliente c SET c.estado = 'Activo'")
    void activarAll();

    /**
     * Actualiza el usuario asociado a un cliente.
     * 
     * @param idCliente ID del cliente
     * @param idUsuario ID del usuario
     */
    @Query("UPDATE Cliente c SET c.idUsuario = :idUsuario WHERE c.idCliente = :idCliente")
    void updateIdUsuario(@Param("idCliente") Integer idCliente, @Param("idUsuario") Integer idUsuario);

    /**
     * Desvincula el usuario de un cliente.
     * 
     * @param idCliente ID del cliente
     */
    @Query("UPDATE Cliente c SET c.idUsuario = NULL WHERE c.idCliente = :idCliente")
    void removeUsuario(@Param("idCliente") Integer idCliente);
}