package com.stamos.repository;

import com.stamos.model.ConfiguracionSistema;
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
public interface ConfiguracionSistemaRepository extends JpaRepository<ConfiguracionSistema, Integer> {

    Optional<ConfiguracionSistema> findByParametro(String parametro);

    boolean existsByParametro(String parametro);

    List<ConfiguracionSistema> findByTipoDato(String tipoDato);

    Page<ConfiguracionSistema> findByTipoDato(String tipoDato, Pageable pageable);

    List<ConfiguracionSistema> findByIdUsuarioActualiza(Integer idUsuarioActualiza);

    Page<ConfiguracionSistema> findByIdUsuarioActualiza(Integer idUsuarioActualiza, Pageable pageable);

    @Query("SELECT c FROM ConfiguracionSistema c WHERE LOWER(c.valor) LIKE LOWER(CONCAT('%', :valor, '%')) ORDER BY c.parametro ASC")
    List<ConfiguracionSistema> findByValorContaining(@Param("valor") String valor);

    @Query("SELECT c FROM ConfiguracionSistema c WHERE LOWER(c.valor) LIKE LOWER(CONCAT('%', :valor, '%')) ORDER BY c.parametro ASC")
    Page<ConfiguracionSistema> findByValorContaining(@Param("valor") String valor, Pageable pageable);

    @Query("SELECT c FROM ConfiguracionSistema c WHERE LOWER(c.descripcion) LIKE LOWER(CONCAT('%', :descripcion, '%')) ORDER BY c.parametro ASC")
    List<ConfiguracionSistema> findByDescripcionContaining(@Param("descripcion") String descripcion);

    @Query("SELECT c FROM ConfiguracionSistema c WHERE LOWER(c.descripcion) LIKE LOWER(CONCAT('%', :descripcion, '%')) ORDER BY c.parametro ASC")
    Page<ConfiguracionSistema> findByDescripcionContaining(@Param("descripcion") String descripcion, Pageable pageable);

    Optional<ConfiguracionSistema> findByParametroAndTipoDato(String parametro, String tipoDato);

    @Query("SELECT c FROM ConfiguracionSistema c WHERE c.parametro = 'tasa_igv'")
    Optional<ConfiguracionSistema> findTasaIgv();

    @Query("SELECT c FROM ConfiguracionSistema c WHERE c.parametro = 'dias_vencimiento_pago'")
    Optional<ConfiguracionSistema> findDiasVencimientoPago();

    @Query("SELECT c FROM ConfiguracionSistema c WHERE c.parametro = 'formato_fecha'")
    Optional<ConfiguracionSistema> findFormatoFecha();

    @Query("SELECT c FROM ConfiguracionSistema c WHERE c.parametro = 'moneda_default'")
    Optional<ConfiguracionSistema> findMonedaDefault();

    @Modifying
    @Transactional
    @Query("UPDATE ConfiguracionSistema c SET c.valor = :valor, c.idUsuarioActualiza = :idUsuario, c.fechaActualizacion = CURRENT_TIMESTAMP WHERE c.parametro = :parametro")
    void updateValorByParametro(@Param("parametro") String parametro,
                               @Param("valor") String valor,
                               @Param("idUsuario") Integer idUsuarioActualiza);

    @Modifying
    @Transactional
    @Query("UPDATE ConfiguracionSistema c SET c.valor = :valor, c.idUsuarioActualiza = :idUsuario, c.fechaActualizacion = CURRENT_TIMESTAMP WHERE c.parametro = 'tasa_igv'")
    void updateTasaIgv(@Param("valor") String valor, @Param("idUsuario") Integer idUsuarioActualiza);

    @Modifying
    @Transactional
    @Query("UPDATE ConfiguracionSistema c SET c.valor = :valor, c.idUsuarioActualiza = :idUsuario, c.fechaActualizacion = CURRENT_TIMESTAMP WHERE c.parametro = 'dias_vencimiento_pago'")
    void updateDiasVencimiento(@Param("valor") String valor, @Param("idUsuario") Integer idUsuarioActualiza);

    long countByTipoDato(String tipoDato);

    @Modifying
    @Transactional
    @Query("DELETE FROM ConfiguracionSistema c WHERE c.parametro = :parametro")
    void deleteByParametro(@Param("parametro") String parametro);

    @Query("SELECT c FROM ConfiguracionSistema c WHERE " +
           "LOWER(c.parametro) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(c.valor) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(c.descripcion) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "ORDER BY c.parametro ASC")
    List<ConfiguracionSistema> search(@Param("searchText") String searchText);

    @Query("SELECT c FROM ConfiguracionSistema c WHERE " +
           "LOWER(c.parametro) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(c.valor) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(c.descripcion) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "ORDER BY c.parametro ASC")
    Page<ConfiguracionSistema> search(@Param("searchText") String searchText, Pageable pageable);

    @Query("SELECT c FROM ConfiguracionSistema c ORDER BY c.parametro ASC")
    List<ConfiguracionSistema> findAllOrderByParametro();

    @Query("SELECT c FROM ConfiguracionSistema c ORDER BY c.fechaActualizacion DESC")
    List<ConfiguracionSistema> findAllOrderByFechaActualizacionDesc();
}