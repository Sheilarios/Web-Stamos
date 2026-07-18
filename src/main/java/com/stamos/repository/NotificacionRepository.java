package com.stamos.repository;

import com.stamos.model.Notificacion;
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

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {

    Page<Notificacion> findByIdUsuarioDestinoOrderByFechaEnvioDesc(Integer idUsuarioDestino, Pageable pageable);
    List<Notificacion> findByIdUsuarioDestinoAndLeidoFalse(Integer idUsuarioDestino);

    Page<Notificacion> findByIdClienteDestinoOrderByFechaEnvioDesc(Integer idClienteDestino, Pageable pageable);
    List<Notificacion> findByIdClienteDestinoAndLeidoFalse(Integer idClienteDestino);

    Page<Notificacion> findByEstadoEnvio(String estadoEnvio, Pageable pageable);
    List<Notificacion> findByEstadoEnvioAndIntentosLessThan(String estadoEnvio, Integer maxIntentos);

    List<Notificacion> findByTipoEvento(String tipoEvento);
    Page<Notificacion> findByTipoEvento(String tipoEvento, Pageable pageable);

    List<Notificacion> findByLeidoFalse();
    List<Notificacion> findByLeidoTrue();

    long countByIdUsuarioDestinoAndLeidoFalse(Integer idUsuarioDestino);
    long countByIdClienteDestinoAndLeidoFalse(Integer idClienteDestino);

    @Query("SELECT n FROM Notificacion n WHERE " +
           "LOWER(n.asunto) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(n.mensaje) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "ORDER BY n.fechaEnvio DESC")
    Page<Notificacion> search(@Param("searchText") String searchText, Pageable pageable);

    @Query("SELECT n.tipoEvento, COUNT(n) FROM Notificacion n GROUP BY n.tipoEvento")
    List<Object[]> countByTipoEvento();

    @Query("SELECT COUNT(n) FROM Notificacion n WHERE n.estadoEnvio = 'Pendiente'")
    long countPendientes();

    @Query("SELECT COUNT(n) FROM Notificacion n WHERE n.estadoEnvio = 'Error'")
    long countErrores();

    @Query("SELECT COUNT(n) FROM Notificacion n WHERE DATE(n.fechaEnvio) = CURRENT_DATE")
    long countHoy();

    @Modifying
    @Transactional
    @Query("DELETE FROM Notificacion n WHERE n.idUsuarioDestino = :idUsuario")
    void deleteByUsuarioDestino(@Param("idUsuario") Integer idUsuario);

    @Modifying
    @Transactional
    @Query("DELETE FROM Notificacion n WHERE n.idClienteDestino = :idCliente")
    void deleteByClienteDestino(@Param("idCliente") Integer idCliente);

    @Modifying
    @Transactional
    @Query("DELETE FROM Notificacion n WHERE n.fechaEnvio < :fecha")
    int deleteOlderThan(@Param("fecha") LocalDateTime fecha);

    @Modifying
    @Transactional
    @Query("UPDATE Notificacion n SET n.leido = true, n.fechaLectura = CURRENT_TIMESTAMP " +
           "WHERE n.idUsuarioDestino = :idUsuario AND n.leido = false")
    void marcarTodasLeidasPorUsuario(@Param("idUsuario") Integer idUsuario);

    @Modifying
    @Transactional
    @Query("UPDATE Notificacion n SET n.leido = true, n.fechaLectura = CURRENT_TIMESTAMP " +
           "WHERE n.idClienteDestino = :idCliente AND n.leido = false")
    void marcarTodasLeidasPorCliente(@Param("idCliente") Integer idCliente);

    @Modifying
    @Transactional
    @Query("UPDATE Notificacion n SET n.intentos = n.intentos + 1 WHERE n.idNotificacion = :id")
    void incrementarIntento(@Param("id") Integer id);

    @Modifying
    @Transactional
    @Query("UPDATE Notificacion n SET n.estadoEnvio = :estado WHERE n.idNotificacion = :id")
    void actualizarEstado(@Param("id") Integer id, @Param("estado") String estado);
}