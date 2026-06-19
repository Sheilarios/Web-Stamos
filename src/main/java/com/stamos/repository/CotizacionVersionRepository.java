package com.stamos.repository;

import com.stamos.model.CotizacionVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CotizacionVersionRepository extends JpaRepository<CotizacionVersion, Integer> {

    List<CotizacionVersion> findByIdCotizacion(Integer idCotizacion);

    Page<CotizacionVersion> findByIdCotizacion(Integer idCotizacion, Pageable pageable);

    @Query("SELECT v FROM CotizacionVersion v WHERE v.idCotizacion = :idCotizacion ORDER BY v.numeroVersion DESC")
    List<CotizacionVersion> findByIdCotizacionOrderByNumeroVersionDesc(@Param("idCotizacion") Integer idCotizacion);

    @Query("SELECT v FROM CotizacionVersion v WHERE v.idCotizacion = :idCotizacion ORDER BY v.numeroVersion ASC")
    List<CotizacionVersion> findByIdCotizacionOrderByNumeroVersionAsc(@Param("idCotizacion") Integer idCotizacion);

    @Query("SELECT v FROM CotizacionVersion v WHERE v.idCotizacion = :idCotizacion ORDER BY v.numeroVersion DESC")
    Optional<CotizacionVersion> findVersionActual(@Param("idCotizacion") Integer idCotizacion);

    Optional<CotizacionVersion> findByIdCotizacionAndNumeroVersion(Integer idCotizacion, Integer numeroVersion);

    List<CotizacionVersion> findByIdUsuarioCreador(Integer idUsuarioCreador);

    Page<CotizacionVersion> findByIdUsuarioCreador(Integer idUsuarioCreador, Pageable pageable);

    List<CotizacionVersion> findByIdUsuarioCreadorAndIdCotizacion(Integer idUsuarioCreador, Integer idCotizacion);

    @Query("SELECT v FROM CotizacionVersion v WHERE v.fechaCreacion BETWEEN :fechaInicio AND :fechaFin ORDER BY v.fechaCreacion DESC")
    List<CotizacionVersion> findByFechaCreacionBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                       @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT v FROM CotizacionVersion v WHERE v.fechaCreacion BETWEEN :fechaInicio AND :fechaFin ORDER BY v.fechaCreacion DESC")
    Page<CotizacionVersion> findByFechaCreacionBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                       @Param("fechaFin") LocalDateTime fechaFin,
                                                       Pageable pageable);

    @Query("SELECT v FROM CotizacionVersion v WHERE DATE(v.fechaCreacion) = CURRENT_DATE ORDER BY v.fechaCreacion DESC")
    List<CotizacionVersion> findCreadasHoy();

    @Query("SELECT v FROM CotizacionVersion v WHERE v.total >= :total ORDER BY v.total DESC")
    List<CotizacionVersion> findByTotalGreaterThanEqual(@Param("total") BigDecimal total);

    @Query("SELECT v FROM CotizacionVersion v WHERE v.total BETWEEN :totalMin AND :totalMax ORDER BY v.total ASC")
    List<CotizacionVersion> findByTotalBetween(@Param("totalMin") BigDecimal totalMin,
                                               @Param("totalMax") BigDecimal totalMax);

    @Query("SELECT v FROM CotizacionVersion v WHERE v.total >= :total ORDER BY v.total DESC")
    Page<CotizacionVersion> findByTotalGreaterThanEqual(@Param("total") BigDecimal total,
                                                        Pageable pageable);

    List<CotizacionVersion> findByTasaIgv(BigDecimal tasaIgv);

    Page<CotizacionVersion> findByTasaIgv(BigDecimal tasaIgv, Pageable pageable);

    @Query("SELECT v FROM CotizacionVersion v WHERE v.tasaIgv != :tasaIgv ORDER BY v.fechaCreacion DESC")
    List<CotizacionVersion> findByTasaIgvNot(@Param("tasaIgv") BigDecimal tasaIgv);

    @Query("SELECT v FROM CotizacionVersion v WHERE v.motivoCambio IS NOT NULL AND v.motivoCambio != '' ORDER BY v.fechaCreacion DESC")
    List<CotizacionVersion> findWithMotivoCambio();

    @Query("SELECT v FROM CotizacionVersion v WHERE v.motivoCambio IS NOT NULL AND v.motivoCambio != '' ORDER BY v.fechaCreacion DESC")
    Page<CotizacionVersion> findWithMotivoCambio(Pageable pageable);

    @Query("SELECT v FROM CotizacionVersion v WHERE LOWER(v.motivoCambio) LIKE LOWER(CONCAT('%', :motivo, '%')) ORDER BY v.fechaCreacion DESC")
    List<CotizacionVersion> findByMotivoCambioContaining(@Param("motivo") String motivo);

    List<CotizacionVersion> findByIdCotizacionAndIdUsuarioCreador(Integer idCotizacion, Integer idUsuarioCreador);

    @Query("SELECT v FROM CotizacionVersion v WHERE v.idCotizacion = :idCotizacion AND v.total BETWEEN :totalMin AND :totalMax ORDER BY v.numeroVersion DESC")
    List<CotizacionVersion> findByIdCotizacionAndTotalBetween(@Param("idCotizacion") Integer idCotizacion,
                                                              @Param("totalMin") BigDecimal totalMin,
                                                              @Param("totalMax") BigDecimal totalMax);

    long countByIdCotizacion(Integer idCotizacion);

    long countByIdUsuarioCreador(Integer idUsuarioCreador);

    @Query("SELECT COUNT(v) FROM CotizacionVersion v WHERE MONTH(v.fechaCreacion) = :month AND YEAR(v.fechaCreacion) = :year")
    long countByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT v.idCotizacion, COUNT(v) as total FROM CotizacionVersion v GROUP BY v.idCotizacion ORDER BY total DESC")
    List<Object[]> countVersionesByCotizacion();

    @Query("SELECT v.idUsuarioCreador, COUNT(v) as total FROM CotizacionVersion v GROUP BY v.idUsuarioCreador ORDER BY total DESC")
    List<Object[]> countByUsuarioCreador();

    @Query("SELECT v.tasaIgv, COUNT(v) as total FROM CotizacionVersion v GROUP BY v.tasaIgv ORDER BY v.tasaIgv DESC")
    List<Object[]> countByTasaIgv();

    @Query("SELECT COALESCE(AVG(v.total), 0) FROM CotizacionVersion v")
    BigDecimal avgTotal();

    @Query("SELECT COALESCE(MAX(v.total), 0) FROM CotizacionVersion v")
    BigDecimal maxTotal();

    @Query("SELECT COALESCE(MIN(v.total), 0) FROM CotizacionVersion v")
    BigDecimal minTotal();

    @Query("SELECT COALESCE(AVG(v.subtotal), 0) FROM CotizacionVersion v")
    BigDecimal avgSubtotal();

    @Query("SELECT COALESCE(AVG(v.igv), 0) FROM CotizacionVersion v")
    BigDecimal avgIgv();

    boolean existsByIdCotizacion(Integer idCotizacion);

    boolean existsByIdCotizacionAndNumeroVersion(Integer idCotizacion, Integer numeroVersion);

    @Query("SELECT CASE WHEN COUNT(v) > 1 THEN true ELSE false END FROM CotizacionVersion v WHERE v.idCotizacion = :idCotizacion")
    boolean hasMultipleVersions(@Param("idCotizacion") Integer idCotizacion);

    @Modifying
    @Transactional
    @Query("DELETE FROM CotizacionVersion v WHERE v.idCotizacion = :idCotizacion")
    void deleteByIdCotizacion(@Param("idCotizacion") Integer idCotizacion);

    @Modifying
    @Transactional
    @Query("DELETE FROM CotizacionVersion v WHERE v.fechaCreacion < :fecha")
    int deleteOlderThan(@Param("fecha") LocalDateTime fecha);

    @Modifying
    @Transactional
    @Query("DELETE FROM CotizacionVersion v WHERE v.idCotizacion IN (SELECT c.idCotizacion FROM Cotizacion c WHERE c.estado = 'Anulada')")
    int deleteByCotizacionesAnuladas();

    @Query("SELECT v FROM CotizacionVersion v ORDER BY v.fechaCreacion DESC LIMIT :limit")
    List<CotizacionVersion> findTopN(@Param("limit") int limit);

    @Query("SELECT v FROM CotizacionVersion v WHERE v.idCotizacion = :idCotizacion ORDER BY v.numeroVersion DESC LIMIT :limit")
    List<CotizacionVersion> findTopNByCotizacion(@Param("idCotizacion") Integer idCotizacion, @Param("limit") int limit);

    @Query("SELECT v FROM CotizacionVersion v WHERE " +
           "LOWER(v.motivoCambio) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "CAST(v.idCotizacion AS string) LIKE CONCAT('%', :searchText, '%') " +
           "ORDER BY v.fechaCreacion DESC")
    List<CotizacionVersion> search(@Param("searchText") String searchText);

    @Query("SELECT v FROM CotizacionVersion v WHERE " +
           "LOWER(v.motivoCambio) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "CAST(v.idCotizacion AS string) LIKE CONCAT('%', :searchText, '%') " +
           "ORDER BY v.fechaCreacion DESC")
    Page<CotizacionVersion> search(@Param("searchText") String searchText, Pageable pageable);
}