package com.stamos.repository;

import com.stamos.model.Inventario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Integer> {

    String ESTADO_DISPONIBLE = "Disponible";
    String ESTADO_EN_USO = "En uso";
    String ESTADO_MANTENIMIENTO = "Mantenimiento";
    String ESTADO_DANADO = "Dañado";

    Optional<Inventario> findByCodigoSerial(String codigoSerial);

    boolean existsByCodigoSerial(String codigoSerial);

    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM Inventario i " +
           "WHERE i.codigoSerial = :codigoSerial AND i.idInventario != :idInventario")
    boolean existsByCodigoSerialAndIdNot(@Param("codigoSerial") String codigoSerial,
                                         @Param("idInventario") Integer idInventario);

    List<Inventario> findByIdProducto(Integer idProducto);

    Page<Inventario> findByIdProducto(Integer idProducto, Pageable pageable);

    @Query("SELECT i FROM Inventario i WHERE i.idProducto = :idProducto ORDER BY i.codigoSerial ASC")
    List<Inventario> findByIdProductoOrderByCodigoSerial(@Param("idProducto") Integer idProducto);

    List<Inventario> findByIdProductoAndEstadoFisico(Integer idProducto, String estadoFisico);

    Page<Inventario> findByIdProductoAndEstadoFisico(Integer idProducto, String estadoFisico, Pageable pageable);

    long countByIdProducto(Integer idProducto);

    long countByIdProductoAndEstadoFisico(Integer idProducto, String estadoFisico);

    List<Inventario> findByEstadoFisico(String estadoFisico);

    Page<Inventario> findByEstadoFisico(String estadoFisico, Pageable pageable);

    @Query("SELECT i FROM Inventario i WHERE i.estadoFisico = 'Disponible' ORDER BY i.codigoSerial ASC")
    List<Inventario> findDisponibles();

    @Query("SELECT i FROM Inventario i WHERE i.estadoFisico = 'Disponible' ORDER BY i.codigoSerial ASC")
    Page<Inventario> findDisponibles(Pageable pageable);

    @Query("SELECT i FROM Inventario i WHERE i.estadoFisico = 'En uso' ORDER BY i.codigoSerial ASC")
    List<Inventario> findEnUso();

    @Query("SELECT i FROM Inventario i WHERE i.estadoFisico = 'Mantenimiento' ORDER BY i.codigoSerial ASC")
    List<Inventario> findEnMantenimiento();

    @Query("SELECT i FROM Inventario i WHERE i.estadoFisico = 'Dañado' ORDER BY i.codigoSerial ASC")
    List<Inventario> findDanados();

    @Query("SELECT i FROM Inventario i WHERE i.estadoFisico IN ('Disponible', 'En uso') ORDER BY i.codigoSerial ASC")
    List<Inventario> findEnBuenEstado();

    @Query("SELECT i FROM Inventario i WHERE i.estadoFisico IN ('Disponible', 'En uso') ORDER BY i.codigoSerial ASC")
    Page<Inventario> findEnBuenEstado(Pageable pageable);

    @Query("SELECT i FROM Inventario i WHERE i.estadoFisico IN ('Mantenimiento', 'Dañado') ORDER BY i.codigoSerial ASC")
    List<Inventario> findNecesitanMantenimiento();

    @Query("SELECT i FROM Inventario i WHERE i.estadoFisico IN ('Mantenimiento', 'Dañado') ORDER BY i.codigoSerial ASC")
    Page<Inventario> findNecesitanMantenimiento(Pageable pageable);

    long countByEstadoFisico(String estadoFisico);

    @Query("SELECT i.estadoFisico, COUNT(i) as total FROM Inventario i GROUP BY i.estadoFisico ORDER BY i.estadoFisico ASC")
    List<Object[]> countByEstadoFisicoResumen();

    List<Inventario> findByUbicacion(String ubicacion);

    Page<Inventario> findByUbicacion(String ubicacion, Pageable pageable);

    List<Inventario> findByUbicacionAndEstadoFisico(String ubicacion, String estadoFisico);

    @Query("SELECT DISTINCT i.ubicacion FROM Inventario i WHERE i.ubicacion IS NOT NULL AND i.ubicacion != '' ORDER BY i.ubicacion ASC")
    List<String> findUbicacionesUnicas();

    long countByUbicacion(String ubicacion);

    @Query("SELECT i.ubicacion, COUNT(i) as total FROM Inventario i WHERE i.ubicacion IS NOT NULL AND i.ubicacion != '' GROUP BY i.ubicacion ORDER BY i.ubicacion ASC")
    List<Object[]> countByUbicacionResumen();

    List<Inventario> findByFechaAdquisicion(LocalDate fechaAdquisicion);

    @Query("SELECT i FROM Inventario i WHERE i.fechaAdquisicion BETWEEN :fechaInicio AND :fechaFin ORDER BY i.fechaAdquisicion DESC")
    List<Inventario> findByFechaAdquisicionBetween(@Param("fechaInicio") LocalDate fechaInicio,
                                                   @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT i FROM Inventario i WHERE i.fechaAdquisicion BETWEEN :fechaInicio AND :fechaFin ORDER BY i.fechaAdquisicion DESC")
    Page<Inventario> findByFechaAdquisicionBetween(@Param("fechaInicio") LocalDate fechaInicio,
                                                   @Param("fechaFin") LocalDate fechaFin,
                                                   Pageable pageable);

    @Query("SELECT i FROM Inventario i WHERE i.fechaAdquisicion >= :fecha ORDER BY i.fechaAdquisicion DESC")
    List<Inventario> findAdquiridosUltimosDias(@Param("fecha") LocalDate fecha);

    @Query("SELECT i FROM Inventario i WHERE i.fechaAdquisicion >= :fecha ORDER BY i.fechaAdquisicion DESC")
    Page<Inventario> findAdquiridosUltimosDias(@Param("fecha") LocalDate fecha, Pageable pageable);

    @Query("SELECT i FROM Inventario i WHERE YEAR(CURRENT_DATE) - YEAR(i.fechaAdquisicion) >= :anios ORDER BY i.fechaAdquisicion ASC")
    List<Inventario> findByAntiguedadGreaterThanEqual(@Param("anios") Integer anios);

    @Query("SELECT i FROM Inventario i WHERE i.valorCompra >= :valor ORDER BY i.valorCompra DESC")
    List<Inventario> findByValorCompraGreaterThanEqual(@Param("valor") BigDecimal valorCompra);

    @Query("SELECT i FROM Inventario i WHERE i.valorCompra >= :valor ORDER BY i.valorCompra DESC")
    Page<Inventario> findByValorCompraGreaterThanEqual(@Param("valor") BigDecimal valorCompra, Pageable pageable);

    @Query("SELECT i FROM Inventario i WHERE i.valorCompra BETWEEN :valorMin AND :valorMax ORDER BY i.valorCompra DESC")
    List<Inventario> findByValorCompraBetween(@Param("valorMin") BigDecimal valorMin,
                                              @Param("valorMax") BigDecimal valorMax);

    @Query("SELECT COALESCE(SUM(i.valorCompra), 0) FROM Inventario i")
    BigDecimal sumValorCompra();

    @Query("SELECT COALESCE(SUM(i.valorCompra), 0) FROM Inventario i WHERE i.estadoFisico = :estado")
    BigDecimal sumValorCompraByEstado(@Param("estado") String estadoFisico);

    @Query("SELECT COALESCE(AVG(i.valorCompra), 0) FROM Inventario i WHERE i.valorCompra IS NOT NULL")
    BigDecimal avgValorCompra();

    @Query("SELECT i FROM Inventario i WHERE LOWER(i.observaciones) LIKE LOWER(CONCAT('%', :observaciones, '%')) ORDER BY i.codigoSerial ASC")
    List<Inventario> findByObservacionesContaining(@Param("observaciones") String observaciones);

    @Query("SELECT i FROM Inventario i WHERE LOWER(i.observaciones) LIKE LOWER(CONCAT('%', :observaciones, '%')) ORDER BY i.codigoSerial ASC")
    Page<Inventario> findByObservacionesContaining(@Param("observaciones") String observaciones, Pageable pageable);

    @Query("SELECT i FROM Inventario i WHERE i.observaciones IS NOT NULL AND i.observaciones != '' ORDER BY i.codigoSerial ASC")
    List<Inventario> findWithObservaciones();

    @Query("SELECT i FROM Inventario i WHERE i.idProducto = :idProducto AND i.estadoFisico = :estado ORDER BY i.codigoSerial ASC")
    List<Inventario> findByIdProductoAndEstadoFisicoOrderByCodigoSerial(@Param("idProducto") Integer idProducto,
                                                                        @Param("estado") String estadoFisico);

    @Query("SELECT i FROM Inventario i WHERE i.idProducto = :idProducto AND i.estadoFisico = 'Disponible' ORDER BY i.codigoSerial ASC")
    List<Inventario> findDisponiblesByProducto(@Param("idProducto") Integer idProducto);

    @Query("SELECT i FROM Inventario i WHERE i.idProducto = :idProducto AND i.estadoFisico = 'Disponible' ORDER BY i.codigoSerial ASC")
    Page<Inventario> findDisponiblesByProducto(@Param("idProducto") Integer idProducto, Pageable pageable);

    @Query("SELECT i FROM Inventario i WHERE i.ubicacion = :ubicacion AND i.estadoFisico = :estado ORDER BY i.codigoSerial ASC")
    List<Inventario> findByUbicacionAndEstadoFisicoOrderByCodigoSerial(@Param("ubicacion") String ubicacion,
                                                                       @Param("estado") String estadoFisico);

    @Query("SELECT i FROM Inventario i WHERE i.estadoFisico = 'Disponible' " +
           "AND i.idInventario NOT IN (" +
           "  SELECT r.idInventario FROM ReservaInventario r " +
           "  WHERE r.estadoReserva = 'Activa' " +
           "  AND NOT (r.fechaFin < :fechaInicio OR r.fechaInicio > :fechaFin)" +
           ") ORDER BY i.codigoSerial ASC")
    List<Inventario> findDisponiblesByFechas(@Param("fechaInicio") LocalDate fechaInicio,
                                             @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT i FROM Inventario i WHERE i.estadoFisico = 'Disponible' " +
           "AND i.idInventario NOT IN (" +
           "  SELECT r.idInventario FROM ReservaInventario r " +
           "  WHERE r.estadoReserva = 'Activa' " +
           "  AND NOT (r.fechaFin < :fechaInicio OR r.fechaInicio > :fechaFin)" +
           ") ORDER BY i.codigoSerial ASC")
    Page<Inventario> findDisponiblesByFechas(@Param("fechaInicio") LocalDate fechaInicio,
                                             @Param("fechaFin") LocalDate fechaFin,
                                             Pageable pageable);

    @Query("SELECT i FROM Inventario i WHERE i.idProducto = :idProducto AND i.estadoFisico = 'Disponible' " +
           "AND i.idInventario NOT IN (" +
           "  SELECT r.idInventario FROM ReservaInventario r " +
           "  WHERE r.estadoReserva = 'Activa' " +
           "  AND NOT (r.fechaFin < :fechaInicio OR r.fechaInicio > :fechaFin)" +
           ") ORDER BY i.codigoSerial ASC")
    List<Inventario> findDisponiblesByProductoAndFechas(@Param("idProducto") Integer idProducto,
                                                        @Param("fechaInicio") LocalDate fechaInicio,
                                                        @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT i FROM Inventario i WHERE i.idProducto = :idProducto AND i.estadoFisico = 'Disponible' " +
           "AND i.idInventario NOT IN (" +
           "  SELECT r.idInventario FROM ReservaInventario r " +
           "  WHERE r.estadoReserva = 'Activa' " +
           "  AND NOT (r.fechaFin < :fechaInicio OR r.fechaInicio > :fechaFin)" +
           ") ORDER BY i.codigoSerial ASC")
    Page<Inventario> findDisponiblesByProductoAndFechas(@Param("idProducto") Integer idProducto,
                                                        @Param("fechaInicio") LocalDate fechaInicio,
                                                        @Param("fechaFin") LocalDate fechaFin,
                                                        Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM Inventario i " +
           "WHERE i.idInventario = :idInventario AND i.estadoFisico = 'Disponible' " +
           "AND NOT EXISTS (" +
           "  SELECT r FROM ReservaInventario r " +
           "  WHERE r.idInventario = :idInventario " +
           "  AND r.estadoReserva = 'Activa' " +
           "  AND NOT (r.fechaFin < :fechaInicio OR r.fechaInicio > :fechaFin)" +
           ")")
    boolean isDisponibleEnFechas(@Param("idInventario") Integer idInventario,
                                 @Param("fechaInicio") LocalDate fechaInicio,
                                 @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT i FROM Inventario i WHERE " +
           "LOWER(i.codigoSerial) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(i.ubicacion) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(i.estadoFisico) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "i.idProducto IN (SELECT p.idProducto FROM Producto p WHERE LOWER(p.nombreItem) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
           "ORDER BY i.codigoSerial ASC")
    List<Inventario> search(@Param("searchText") String searchText);

    @Query("SELECT i FROM Inventario i WHERE " +
           "LOWER(i.codigoSerial) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(i.ubicacion) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(i.estadoFisico) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "i.idProducto IN (SELECT p.idProducto FROM Producto p WHERE LOWER(p.nombreItem) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
           "ORDER BY i.codigoSerial ASC")
    Page<Inventario> search(@Param("searchText") String searchText, Pageable pageable);

    @Query("SELECT COUNT(i) FROM Inventario i WHERE MONTH(i.fechaRegistro) = :month AND YEAR(i.fechaRegistro) = :year")
    long countByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT COUNT(i) FROM Inventario i WHERE MONTH(i.fechaAdquisicion) = :month AND YEAR(i.fechaAdquisicion) = :year")
    long countAdquiridosByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT DATE(i.fechaRegistro) as fecha, COUNT(i) as total " +
           "FROM Inventario i " +
           "WHERE i.fechaRegistro >= :fechaInicio " +
           "GROUP BY DATE(i.fechaRegistro) " +
           "ORDER BY DATE(i.fechaRegistro) DESC")
    List<Object[]> countRegistrosByDay(@Param("fechaInicio") LocalDate fechaInicio);

    @Query("SELECT p.tipo, COUNT(i) as total FROM Inventario i " +
           "JOIN Producto p ON i.idProducto = p.idProducto " +
           "GROUP BY p.tipo ORDER BY p.tipo ASC")
    List<Object[]> countByTipoProducto();

    @Query("SELECT c.nombreCategoria, COUNT(i) as total FROM Inventario i " +
           "JOIN Producto p ON i.idProducto = p.idProducto " +
           "JOIN CategoriaProducto c ON p.idCategoria = c.idCategoria " +
           "GROUP BY c.idCategoria ORDER BY c.nombreCategoria ASC")
    List<Object[]> countByCategoriaProducto();

    @Query("SELECT i FROM Inventario i ORDER BY i.fechaRegistro DESC LIMIT :limit")
    List<Inventario> findTopN(@Param("limit") int limit);

    @Query("SELECT i FROM Inventario i WHERE i.idProducto = :idProducto ORDER BY i.fechaRegistro DESC LIMIT :limit")
    List<Inventario> findTopNByProducto(@Param("idProducto") Integer idProducto,
                                        @Param("limit") int limit);

    @Query("SELECT i FROM Inventario i WHERE i.estadoFisico = 'Disponible' ORDER BY i.fechaRegistro DESC LIMIT :limit")
    List<Inventario> findTopNDisponibles(@Param("limit") int limit);

    @Modifying
    @Transactional
    @Query("UPDATE Inventario i SET i.estadoFisico = :estado, i.fechaActualizacion = CURRENT_DATE WHERE i.idProducto = :idProducto")
    void updateEstadoByProducto(@Param("idProducto") Integer idProducto,
                                @Param("estado") String nuevoEstado);

    @Modifying
    @Transactional
    @Query("UPDATE Inventario i SET i.estadoFisico = :estado, i.fechaActualizacion = CURRENT_DATE WHERE i.ubicacion = :ubicacion")
    void updateEstadoByUbicacion(@Param("ubicacion") String ubicacion,
                                 @Param("estado") String nuevoEstado);

    @Modifying
    @Transactional
    @Query("UPDATE Inventario i SET i.estadoFisico = 'Disponible', i.fechaActualizacion = CURRENT_DATE " +
           "WHERE i.estadoFisico = 'En uso' " +
           "AND i.idInventario IN (" +
           "  SELECT r.idInventario FROM ReservaInventario r " +
           "  WHERE r.estadoReserva = 'Activa' AND r.fechaFin < CURRENT_DATE" +
           ")")
    int liberarEquiposReservasVencidas();

    @Modifying
    @Transactional
    @Query("DELETE FROM Inventario i WHERE i.idProducto = :idProducto")
    void deleteByIdProducto(@Param("idProducto") Integer idProducto);

    @Modifying
    @Transactional
    @Query("DELETE FROM Inventario i WHERE i.estadoFisico = :estado")
    void deleteByEstadoFisico(@Param("estado") String estadoFisico);

    @Modifying
    @Transactional
    @Query("DELETE FROM Inventario i WHERE i.estadoFisico IN ('Dañado', 'Mantenimiento') AND i.fechaRegistro < :fecha")
    int deleteDanadosYMantenimientoOlderThan(@Param("fecha") LocalDate fecha);

    @Query("SELECT i FROM Inventario i " +
           "WHERE i.estadoFisico = 'Disponible' " +
           "AND i.idProducto IN (" +
           "  SELECT p.idProducto FROM Producto p WHERE p.tipo = 'Alquiler'" +
           ") " +
           "GROUP BY i.idProducto " +
           "HAVING COUNT(i.idInventario) < 3 " +
           "ORDER BY COUNT(i.idInventario) ASC")
    List<Inventario> findBajoStock();

    @Query("SELECT i FROM Inventario i WHERE i.fechaAdquisicion IS NOT NULL ORDER BY i.fechaAdquisicion ASC")
    Optional<Inventario> findOldest();

    @Query("SELECT i FROM Inventario i WHERE i.valorCompra IS NOT NULL ORDER BY i.valorCompra DESC")
    Optional<Inventario> findMostValuable();

    @Query("SELECT i FROM Inventario i WHERE i.valorCompra IS NOT NULL ORDER BY i.valorCompra ASC")
    Optional<Inventario> findLeastValuable();

    @Query("SELECT i.estadoFisico, COUNT(i) as total FROM Inventario i GROUP BY i.estadoFisico")
    List<Object[]> getEstadisticasPorEstado();

    @Query("SELECT c.nombreCategoria, COUNT(i) as total FROM Inventario i " +
           "JOIN Producto p ON i.idProducto = p.idProducto " +
           "JOIN CategoriaProducto c ON p.idCategoria = c.idCategoria " +
           "GROUP BY c.idCategoria")
    List<Object[]> getEstadisticasPorCategoria();
}