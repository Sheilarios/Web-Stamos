package com.stamos.repository;

import com.stamos.model.Auditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Auditoria.
 * 
 * <p>Proporciona métodos para acceder y gestionar los registros de auditoría
 * del sistema, permitiendo la trazabilidad de acciones críticas.</p>
 * 
 * <p>Implementa los requerimientos:</p>
 * <ul>
 *   <li>RN-09: Registrar fecha y responsable de cambios</li>
 *   <li>RF-15: Registrar fecha, responsable y cambio de estado</li>
 *   <li>RNF-04: Control de acceso basado en roles</li>
 * </ul>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Integer> {

    /**
     * Busca registros de auditoría por ID de usuario.
     * 
     * @param idUsuario ID del usuario
     * @param pageable Configuración de paginación
     * @return Página de registros de auditoría
     */
    @Query("SELECT a FROM Auditoria a WHERE a.idUsuario = :idUsuario ORDER BY a.fechaHora DESC")
    Page<Auditoria> findByIdUsuario(@Param("idUsuario") Integer idUsuario, Pageable pageable);

    /**
     * Busca registros de auditoría por entidad.
     * 
     * @param entidad Nombre de la entidad
     * @param pageable Configuración de paginación
     * @return Página de registros de auditoría
     */
    @Query("SELECT a FROM Auditoria a WHERE a.entidad = :entidad ORDER BY a.fechaHora DESC")
    Page<Auditoria> findByEntidad(@Param("entidad") String entidad, Pageable pageable);

    /**
     * Busca registros de auditoría por acción.
     * 
     * @param accion Acción realizada
     * @param pageable Configuración de paginación
     * @return Página de registros de auditoría
     */
    @Query("SELECT a FROM Auditoria a WHERE a.accion = :accion ORDER BY a.fechaHora DESC")
    Page<Auditoria> findByAccion(@Param("accion") String accion, Pageable pageable);

    /**
     * Busca registros de auditoría por entidad y registro afectado.
     * 
     * @param entidad Nombre de la entidad
     * @param idRegistroAfectado ID del registro afectado
     * @param pageable Configuración de paginación
     * @return Página de registros de auditoría
     */
    @Query("SELECT a FROM Auditoria a WHERE a.entidad = :entidad AND a.idRegistroAfectado = :idRegistro ORDER BY a.fechaHora DESC")
    Page<Auditoria> findByEntidadAndIdRegistroAfectado(@Param("entidad") String entidad, 
                                                       @Param("idRegistro") Integer idRegistroAfectado, 
                                                       Pageable pageable);

    /**
     * Busca registros de auditoría por rango de fechas.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param pageable Configuración de paginación
     * @return Página de registros de auditoría
     */
    @Query("SELECT a FROM Auditoria a WHERE a.fechaHora BETWEEN :fechaInicio AND :fechaFin ORDER BY a.fechaHora DESC")
    Page<Auditoria> findByFechaHoraBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                           @Param("fechaFin") LocalDateTime fechaFin,
                                           Pageable pageable);

    /**
     * Busca registros de auditoría por acción y entidad.
     * 
     * @param accion Acción realizada
     * @param entidad Nombre de la entidad
     * @param pageable Configuración de paginación
     * @return Página de registros de auditoría
     */
    @Query("SELECT a FROM Auditoria a WHERE a.accion = :accion AND a.entidad = :entidad ORDER BY a.fechaHora DESC")
    Page<Auditoria> findByAccionAndEntidad(@Param("accion") String accion,
                                           @Param("entidad") String entidad,
                                           Pageable pageable);

    /**
     * Busca los últimos registros de auditoría.
     * 
     * @param limit Número máximo de registros
     * @return Lista de registros de auditoría
     */
    @Query("SELECT a FROM Auditoria a ORDER BY a.fechaHora DESC")
    List<Auditoria> findTopN(@Param("limit") int limit);

    /**
     * Busca registros de auditoría por IP de origen.
     * 
     * @param ipOrigen IP de origen
     * @param pageable Configuración de paginación
     * @return Página de registros de auditoría
     */
    @Query("SELECT a FROM Auditoria a WHERE a.ipOrigen = :ipOrigen ORDER BY a.fechaHora DESC")
    Page<Auditoria> findByIpOrigen(@Param("ipOrigen") String ipOrigen, Pageable pageable);

    /**
     * Cuenta registros de auditoría por entidad.
     * 
     * @param entidad Nombre de la entidad
     * @return Cantidad de registros
     */
    @Query("SELECT COUNT(a) FROM Auditoria a WHERE a.entidad = :entidad")
    long countByEntidad(@Param("entidad") String entidad);

    /**
     * Cuenta registros de auditoría por acción.
     * 
     * @param accion Acción realizada
     * @return Cantidad de registros
     */
    @Query("SELECT COUNT(a) FROM Auditoria a WHERE a.accion = :accion")
    long countByAccion(@Param("accion") String accion);

    /**
     * Cuenta registros de auditoría por usuario.
     * 
     * @param idUsuario ID del usuario
     * @return Cantidad de registros
     */
    @Query("SELECT COUNT(a) FROM Auditoria a WHERE a.idUsuario = :idUsuario")
    long countByIdUsuario(@Param("idUsuario") Integer idUsuario);

    /**
     * Obtiene el resumen de acciones por día.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de objetos con conteo por día
     */
    @Query("SELECT FUNCTION('DATE', a.fechaHora) as fecha, COUNT(a) as total " +
           "FROM Auditoria a " +
           "WHERE a.fechaHora BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY FUNCTION('DATE', a.fechaHora) " +
           "ORDER BY FUNCTION('DATE', a.fechaHora) DESC")
    List<Object[]> countByDay(@Param("fechaInicio") LocalDateTime fechaInicio,
                              @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Obtiene el resumen de acciones por entidad.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de objetos con conteo por entidad
     */
    @Query("SELECT a.entidad, COUNT(a) as total " +
           "FROM Auditoria a " +
           "WHERE a.fechaHora BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY a.entidad " +
           "ORDER BY COUNT(a) DESC")
    List<Object[]> countByEntidadInRange(@Param("fechaInicio") LocalDateTime fechaInicio,
                                         @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Obtiene el resumen de acciones por usuario.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de objetos con conteo por usuario
     */
    @Query("SELECT a.idUsuario, COUNT(a) as total " +
           "FROM Auditoria a " +
           "WHERE a.fechaHora BETWEEN :fechaInicio AND :fechaFin AND a.idUsuario IS NOT NULL " +
           "GROUP BY a.idUsuario " +
           "ORDER BY COUNT(a) DESC")
    List<Object[]> countByUsuarioInRange(@Param("fechaInicio") LocalDateTime fechaInicio,
                                         @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Busca registros de auditoría de cambios de estado.
     * 
     * @param pageable Configuración de paginación
     * @return Página de registros de auditoría
     */
    @Query("SELECT a FROM Auditoria a WHERE a.accion = 'STATE_CHANGE' ORDER BY a.fechaHora DESC")
    Page<Auditoria> findCambiosEstado(Pageable pageable);

    /**
     * Busca registros de auditoría de cambios de estado por proyecto.
     * 
     * @param idProyecto ID del proyecto
     * @param pageable Configuración de paginación
     * @return Página de registros de auditoría
     */
    @Query("SELECT a FROM Auditoria a WHERE a.accion = 'STATE_CHANGE' AND a.entidad = 'Proyecto' AND a.idRegistroAfectado = :idProyecto ORDER BY a.fechaHora DESC")
    Page<Auditoria> findCambiosEstadoByProyecto(@Param("idProyecto") Integer idProyecto, Pageable pageable);

    /**
     * Busca registros de auditoría de inicio de sesión.
     * 
     * @param pageable Configuración de paginación
     * @return Página de registros de auditoría
     */
    @Query("SELECT a FROM Auditoria a WHERE a.accion = 'LOGIN' OR a.accion = 'LOGOUT' ORDER BY a.fechaHora DESC")
    Page<Auditoria> findAccesos(Pageable pageable);

    /**
     * Busca registros de auditoría de inicio de sesión por usuario.
     * 
     * @param idUsuario ID del usuario
     * @param pageable Configuración de paginación
     * @return Página de registros de auditoría
     */
    @Query("SELECT a FROM Auditoria a WHERE (a.accion = 'LOGIN' OR a.accion = 'LOGOUT') AND a.idUsuario = :idUsuario ORDER BY a.fechaHora DESC")
    Page<Auditoria> findAccesosByUsuario(@Param("idUsuario") Integer idUsuario, Pageable pageable);

    /**
     * Elimina registros de auditoría anteriores a una fecha.
     * 
     * @param fecha Fecha límite
     * @return Número de registros eliminados
     */
    @Query("DELETE FROM Auditoria a WHERE a.fechaHora < :fecha")
    int deleteOlderThan(@Param("fecha") LocalDateTime fecha);

    /**
     * Obtiene el número total de registros de auditoría.
     * 
     * @return Total de registros
     */
    @Query("SELECT COUNT(a) FROM Auditoria a")
    long countAll();

    /**
     * Obtiene los últimos registros de auditoría para el dashboard.
     * 
     * @param limit Número máximo de registros
     * @return Lista de registros de auditoría
     */
    @Query(value = "SELECT * FROM AuditoriaSistema ORDER BY fecha_hora DESC LIMIT :limit", nativeQuery = true)
    List<Auditoria> findLastAuditorias(@Param("limit") int limit);

    /**
     * Obtiene el resumen de acciones por entidad para el dashboard.
     * 
     * @return Lista de objetos con conteo por entidad
     */
    @Query("SELECT a.entidad, COUNT(a) as total FROM Auditoria a GROUP BY a.entidad ORDER BY COUNT(a) DESC")
    List<Object[]> countByEntidadResumen();

    /**
     * Obtiene el resumen de acciones por acción para el dashboard.
     * 
     * @return Lista de objetos con conteo por acción
     */
    @Query("SELECT a.accion, COUNT(a) as total FROM Auditoria a GROUP BY a.accion ORDER BY COUNT(a) DESC")
    List<Object[]> countByAccionResumen();

    /**
     * Obtiene el total de acciones por día para los últimos N días.
     * 
     * @param days Número de días
     * @return Lista de objetos con conteo por día
     */
    @Query(value = "SELECT DATE(fecha_hora) as fecha, COUNT(*) as total " +
                   "FROM AuditoriaSistema " +
                   "WHERE fecha_hora >= DATE_SUB(CURDATE(), INTERVAL :days DAY) " +
                   "GROUP BY DATE(fecha_hora) " +
                   "ORDER BY DATE(fecha_hora) DESC", nativeQuery = true)
    List<Object[]> countByLastDays(@Param("days") int days);

    /**
     * Busca auditorías por texto en los datos JSON.
     * 
     * @param searchText Texto a buscar
     * @param pageable Configuración de paginación
     * @return Página de registros de auditoría
     */
    @Query("SELECT a FROM Auditoria a WHERE " +
           "LOWER(a.entidad) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(a.accion) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(a.ipOrigen) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "CAST(a.idRegistroAfectado AS string) LIKE CONCAT('%', :searchText, '%') " +
           "ORDER BY a.fechaHora DESC")
    Page<Auditoria> search(@Param("searchText") String searchText, Pageable pageable);

    /**
     * Obtiene el número total de registros de auditoría por fecha.
     * 
     * @param fecha Fecha a consultar
     * @return Cantidad de registros
     */
    @Query("SELECT COUNT(a) FROM Auditoria a WHERE FUNCTION('DATE', a.fechaHora) = :fecha")
    long countByDate(@Param("fecha") LocalDateTime fecha);

    /**
     * Obtiene el número total de registros de auditoría en el día actual.
     * 
     * @return Cantidad de registros del día
     */
    @Query("SELECT COUNT(a) FROM Auditoria a WHERE FUNCTION('DATE', a.fechaHora) = CURRENT_DATE")
    long countToday();

    /**
     * Obtiene el número total de registros de auditoría en el mes actual.
     * 
     * @return Cantidad de registros del mes
     */
    @Query("SELECT COUNT(a) FROM Auditoria a WHERE MONTH(a.fechaHora) = MONTH(CURRENT_DATE) AND YEAR(a.fechaHora) = YEAR(CURRENT_DATE)")
    long countThisMonth();

    /**
     * Obtiene el número total de registros de auditoría en el año actual.
     * 
     * @return Cantidad de registros del año
     */
    @Query("SELECT COUNT(a) FROM Auditoria a WHERE YEAR(a.fechaHora) = YEAR(CURRENT_DATE)")
    long countThisYear();
}