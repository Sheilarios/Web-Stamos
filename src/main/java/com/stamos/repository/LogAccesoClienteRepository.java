package com.stamos.repository;

import com.stamos.model.LogAccesoCliente;
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
public interface LogAccesoClienteRepository extends JpaRepository<LogAccesoCliente, Integer> {

    List<LogAccesoCliente> findByIdCliente(Integer idCliente);

    Page<LogAccesoCliente> findByIdCliente(Integer idCliente, Pageable pageable);

    @Query("SELECT l FROM LogAccesoCliente l WHERE l.idCliente = :idCliente ORDER BY l.fechaAcceso DESC")
    List<LogAccesoCliente> findByIdClienteOrderByFechaAccesoDesc(@Param("idCliente") Integer idCliente);

    @Query("SELECT l FROM LogAccesoCliente l WHERE l.idCliente = :idCliente ORDER BY l.fechaAcceso DESC")
    Optional<LogAccesoCliente> findLastByCliente(@Param("idCliente") Integer idCliente);

    long countByIdCliente(Integer idCliente);

    @Query("SELECT COUNT(l) FROM LogAccesoCliente l WHERE l.idCliente = :idCliente AND l.fechaAcceso BETWEEN :fechaInicio AND :fechaFin")
    long countByIdClienteAndFechaBetween(@Param("idCliente") Integer idCliente,
                                         @Param("fechaInicio") LocalDateTime fechaInicio,
                                         @Param("fechaFin") LocalDateTime fechaFin);

    List<LogAccesoCliente> findByIpOrigen(String ipOrigen);

    Page<LogAccesoCliente> findByIpOrigen(String ipOrigen, Pageable pageable);

    @Query("SELECT l FROM LogAccesoCliente l WHERE l.ipOrigen = :ipOrigen ORDER BY l.fechaAcceso DESC")
    List<LogAccesoCliente> findByIpOrigenOrderByFechaAccesoDesc(@Param("ipOrigen") String ipOrigen);

    long countByIpOrigen(String ipOrigen);

    @Query("SELECT l FROM LogAccesoCliente l WHERE LOWER(l.navegador) LIKE LOWER(CONCAT('%', :navegador, '%')) ORDER BY l.fechaAcceso DESC")
    List<LogAccesoCliente> findByNavegadorContaining(@Param("navegador") String navegador);

    @Query("SELECT l FROM LogAccesoCliente l WHERE LOWER(l.navegador) LIKE LOWER(CONCAT('%', :navegador, '%')) ORDER BY l.fechaAcceso DESC")
    Page<LogAccesoCliente> findByNavegadorContaining(@Param("navegador") String navegador, Pageable pageable);

    @Query("SELECT COUNT(l) FROM LogAccesoCliente l")
    long countTotal();

    @Query("SELECT l FROM LogAccesoCliente l WHERE l.fechaAcceso BETWEEN :fechaInicio AND :fechaFin ORDER BY l.fechaAcceso DESC")
    List<LogAccesoCliente> findByFechaAccesoBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                    @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT l FROM LogAccesoCliente l WHERE l.fechaAcceso BETWEEN :fechaInicio AND :fechaFin ORDER BY l.fechaAcceso DESC")
    Page<LogAccesoCliente> findByFechaAccesoBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                    @Param("fechaFin") LocalDateTime fechaFin,
                                                    Pageable pageable);

    @Query("SELECT l FROM LogAccesoCliente l WHERE DATE(l.fechaAcceso) = CURRENT_DATE ORDER BY l.fechaAcceso DESC")
    List<LogAccesoCliente> findAccesosHoy();

    @Query("SELECT l FROM LogAccesoCliente l WHERE DATE(l.fechaAcceso) = CURRENT_DATE ORDER BY l.fechaAcceso DESC")
    Page<LogAccesoCliente> findAccesosHoy(Pageable pageable);

    @Query("SELECT l FROM LogAccesoCliente l WHERE l.fechaAcceso >= :fecha ORDER BY l.fechaAcceso DESC")
    List<LogAccesoCliente> findAccesosUltimosDias(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT l FROM LogAccesoCliente l WHERE l.fechaAcceso >= :fecha ORDER BY l.fechaAcceso DESC")
    Page<LogAccesoCliente> findAccesosUltimosDias(@Param("fecha") LocalDateTime fecha, Pageable pageable);

    @Query("SELECT l FROM LogAccesoCliente l WHERE l.fechaAcceso >= :fecha ORDER BY l.fechaAcceso DESC")
    List<LogAccesoCliente> findAccesosUltimoMes(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT l FROM LogAccesoCliente l WHERE l.fechaAcceso >= :fecha ORDER BY l.fechaAcceso DESC")
    Page<LogAccesoCliente> findAccesosUltimoMes(@Param("fecha") LocalDateTime fecha, Pageable pageable);

    @Query("SELECT l FROM LogAccesoCliente l WHERE l.idCliente = :idCliente AND l.fechaAcceso BETWEEN :fechaInicio AND :fechaFin ORDER BY l.fechaAcceso DESC")
    List<LogAccesoCliente> findByIdClienteAndFechaBetween(@Param("idCliente") Integer idCliente,
                                                          @Param("fechaInicio") LocalDateTime fechaInicio,
                                                          @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT l FROM LogAccesoCliente l WHERE l.ipOrigen = :ipOrigen AND l.fechaAcceso BETWEEN :fechaInicio AND :fechaFin ORDER BY l.fechaAcceso DESC")
    List<LogAccesoCliente> findByIpOrigenAndFechaBetween(@Param("ipOrigen") String ipOrigen,
                                                         @Param("fechaInicio") LocalDateTime fechaInicio,
                                                         @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT l FROM LogAccesoCliente l ORDER BY l.fechaAcceso DESC LIMIT :limit")
    List<LogAccesoCliente> findTopN(@Param("limit") int limit);

    @Query("SELECT l FROM LogAccesoCliente l WHERE l.idCliente = :idCliente ORDER BY l.fechaAcceso DESC LIMIT :limit")
    List<LogAccesoCliente> findTopNByCliente(@Param("idCliente") Integer idCliente,
                                             @Param("limit") int limit);

    @Modifying
    @Transactional
    @Query("DELETE FROM LogAccesoCliente l WHERE l.idCliente = :idCliente")
    void deleteByIdCliente(@Param("idCliente") Integer idCliente);

    @Modifying
    @Transactional
    @Query("DELETE FROM LogAccesoCliente l WHERE l.fechaAcceso < :fecha")
    int deleteOlderThan(@Param("fecha") LocalDateTime fecha);

    @Modifying
    @Transactional
    @Query("DELETE FROM LogAccesoCliente l WHERE l.ipOrigen = :ipOrigen")
    void deleteByIpOrigen(@Param("ipOrigen") String ipOrigen);

    @Modifying
    @Transactional
    @Query("DELETE FROM LogAccesoCliente l WHERE l.fechaAcceso < :fecha")
    int deleteOldRecords(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT l FROM LogAccesoCliente l WHERE " +
           "LOWER(l.ipOrigen) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(l.navegador) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "CAST(l.idCliente AS string) LIKE CONCAT('%', :searchText, '%') " +
           "ORDER BY l.fechaAcceso DESC")
    List<LogAccesoCliente> search(@Param("searchText") String searchText);

    @Query("SELECT l FROM LogAccesoCliente l WHERE " +
           "LOWER(l.ipOrigen) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(l.navegador) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "CAST(l.idCliente AS string) LIKE CONCAT('%', :searchText, '%') " +
           "ORDER BY l.fechaAcceso DESC")
    Page<LogAccesoCliente> search(@Param("searchText") String searchText, Pageable pageable);
}