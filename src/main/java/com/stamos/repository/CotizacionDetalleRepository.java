package com.stamos.repository;

import com.stamos.model.CotizacionDetalle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CotizacionDetalleRepository extends JpaRepository<CotizacionDetalle, Integer> {

    List<CotizacionDetalle> findByIdVersion(Integer idVersion);

    Page<CotizacionDetalle> findByIdVersion(Integer idVersion, Pageable pageable);

    @Query("SELECT d FROM CotizacionDetalle d WHERE d.idVersion = :idVersion ORDER BY d.idProducto ASC")
    List<CotizacionDetalle> findByIdVersionOrderByProducto(@Param("idVersion") Integer idVersion);

    @Query("SELECT d FROM CotizacionDetalle d WHERE d.idVersion IN :idsVersion ORDER BY d.idVersion DESC, d.idProducto ASC")
    List<CotizacionDetalle> findByIdVersionIn(@Param("idsVersion") List<Integer> idsVersion);

    List<CotizacionDetalle> findByIdProducto(Integer idProducto);

    Page<CotizacionDetalle> findByIdProducto(Integer idProducto, Pageable pageable);

    @Query("SELECT d FROM CotizacionDetalle d WHERE d.idProducto = :idProducto AND d.idVersion IN :idsVersion")
    List<CotizacionDetalle> findByIdProductoAndIdVersionIn(@Param("idProducto") Integer idProducto,
                                                           @Param("idsVersion") List<Integer> idsVersion);

    @Query("SELECT d FROM CotizacionDetalle d " +
           "WHERE d.idVersion IN (SELECT v.idVersion FROM CotizacionVersion v WHERE v.idCotizacion = :idCotizacion) " +
           "ORDER BY d.idVersion DESC, d.idProducto ASC")
    List<CotizacionDetalle> findDetallesByCotizacion(@Param("idCotizacion") Integer idCotizacion);

    @Query("SELECT d FROM CotizacionDetalle d " +
           "WHERE d.idVersion = (SELECT v.idVersion FROM CotizacionVersion v " +
           "WHERE v.idCotizacion = :idCotizacion AND v.numeroVersion = :versionActual)")
    List<CotizacionDetalle> findDetallesVersionActual(@Param("idCotizacion") Integer idCotizacion,
                                                      @Param("versionActual") Integer versionActual);

    @Query("SELECT d FROM CotizacionDetalle d " +
           "WHERE d.idVersion = (SELECT v.idVersion FROM CotizacionVersion v " +
           "WHERE v.idCotizacion = :idCotizacion AND v.numeroVersion = :versionActual)")
    Page<CotizacionDetalle> findDetallesVersionActual(@Param("idCotizacion") Integer idCotizacion,
                                                      @Param("versionActual") Integer versionActual,
                                                      Pageable pageable);

    @Query("SELECT d FROM CotizacionDetalle d " +
           "WHERE d.idVersion IN (SELECT v.idVersion FROM CotizacionVersion v " +
           "WHERE v.idCotizacion IN (SELECT c.idCotizacion FROM Cotizacion c WHERE c.idCliente = :idCliente)) " +
           "ORDER BY d.idVersion DESC")
    List<CotizacionDetalle> findDetallesByCliente(@Param("idCliente") Integer idCliente);

    @Query("SELECT d FROM CotizacionDetalle d WHERE d.precioUnitario >= :precio ORDER BY d.precioUnitario DESC")
    List<CotizacionDetalle> findByPrecioUnitarioGreaterThanEqual(@Param("precio") BigDecimal precio);

    @Query("SELECT d FROM CotizacionDetalle d WHERE d.precioUnitario BETWEEN :precioMin AND :precioMax ORDER BY d.precioUnitario ASC")
    List<CotizacionDetalle> findByPrecioUnitarioBetween(@Param("precioMin") BigDecimal precioMin,
                                                        @Param("precioMax") BigDecimal precioMax);

    @Query("SELECT d FROM CotizacionDetalle d WHERE d.descuento > 0 ORDER BY d.descuento DESC")
    List<CotizacionDetalle> findWithDescuento();

    @Query("SELECT d FROM CotizacionDetalle d WHERE d.descuento > 0 ORDER BY d.descuento DESC")
    Page<CotizacionDetalle> findWithDescuento(Pageable pageable);

    @Query("SELECT d FROM CotizacionDetalle d WHERE d.cantidad >= :cantidad ORDER BY d.cantidad DESC")
    List<CotizacionDetalle> findByCantidadGreaterThanEqual(@Param("cantidad") Integer cantidad);

    @Query("SELECT d FROM CotizacionDetalle d WHERE d.cantidad BETWEEN :cantidadMin AND :cantidadMax ORDER BY d.cantidad ASC")
    List<CotizacionDetalle> findByCantidadBetween(@Param("cantidadMin") Integer cantidadMin,
                                                  @Param("cantidadMax") Integer cantidadMax);

    long countByIdVersion(Integer idVersion);

    long countByIdProducto(Integer idProducto);

    @Query("SELECT COALESCE(SUM(d.subtotalLinea), 0) FROM CotizacionDetalle d WHERE d.idVersion = :idVersion")
    BigDecimal sumSubtotalByVersion(@Param("idVersion") Integer idVersion);

    @Query("SELECT COALESCE(SUM(d.subtotalLinea), 0) FROM CotizacionDetalle d " +
           "WHERE d.idVersion IN (SELECT v.idVersion FROM CotizacionVersion v WHERE v.idCotizacion = :idCotizacion)")
    BigDecimal sumSubtotalByCotizacion(@Param("idCotizacion") Integer idCotizacion);

    @Query("SELECT COALESCE(SUM(d.descuento), 0) FROM CotizacionDetalle d WHERE d.idVersion = :idVersion")
    BigDecimal sumDescuentoByVersion(@Param("idVersion") Integer idVersion);

    @Query("SELECT COALESCE(SUM(d.descuento), 0) FROM CotizacionDetalle d " +
           "WHERE d.idVersion IN (SELECT v.idVersion FROM CotizacionVersion v WHERE v.idCotizacion = :idCotizacion)")
    BigDecimal sumDescuentoByCotizacion(@Param("idCotizacion") Integer idCotizacion);

    @Query("SELECT COALESCE(SUM(d.cantidad), 0) FROM CotizacionDetalle d WHERE d.idVersion = :idVersion")
    Integer sumCantidadByVersion(@Param("idVersion") Integer idVersion);

    @Query("SELECT COALESCE(SUM(d.cantidad), 0) FROM CotizacionDetalle d " +
           "WHERE d.idVersion IN (SELECT v.idVersion FROM CotizacionVersion v WHERE v.idCotizacion = :idCotizacion)")
    Integer sumCantidadByCotizacion(@Param("idCotizacion") Integer idCotizacion);

    @Query("SELECT COALESCE(AVG(d.precioUnitario), 0) FROM CotizacionDetalle d WHERE d.idVersion = :idVersion")
    BigDecimal avgPrecioUnitarioByVersion(@Param("idVersion") Integer idVersion);

    @Query("SELECT COALESCE(AVG(d.subtotalLinea), 0) FROM CotizacionDetalle d WHERE d.idVersion = :idVersion")
    BigDecimal avgSubtotalByVersion(@Param("idVersion") Integer idVersion);

    @Query("SELECT d.idProducto, SUM(d.cantidad) as total FROM CotizacionDetalle d " +
           "GROUP BY d.idProducto ORDER BY total DESC")
    List<Object[]> findTopProductosByCantidad();

    @Query("SELECT d.idProducto, SUM(d.subtotalLinea) as total FROM CotizacionDetalle d " +
           "GROUP BY d.idProducto ORDER BY total DESC")
    List<Object[]> findTopProductosByFacturacion();

    @Modifying
    @Transactional
    @Query("DELETE FROM CotizacionDetalle d WHERE d.idVersion = :idVersion")
    void deleteByIdVersion(@Param("idVersion") Integer idVersion);

    @Modifying
    @Transactional
    @Query("DELETE FROM CotizacionDetalle d " +
           "WHERE d.idVersion IN (SELECT v.idVersion FROM CotizacionVersion v WHERE v.idCotizacion = :idCotizacion)")
    void deleteByCotizacion(@Param("idCotizacion") Integer idCotizacion);

    @Modifying
    @Transactional
    @Query("DELETE FROM CotizacionDetalle d WHERE d.idProducto = :idProducto")
    void deleteByIdProducto(@Param("idProducto") Integer idProducto);

    @Query("SELECT d FROM CotizacionDetalle d " +
           "WHERE d.idProducto IN (SELECT p.idProducto FROM Producto p " +
           "WHERE LOWER(p.nombreItem) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "ORDER BY d.idVersion DESC")
    List<CotizacionDetalle> findByNombreProductoContaining(@Param("nombre") String nombreProducto);

    @Query("SELECT d FROM CotizacionDetalle d " +
           "WHERE d.idProducto IN (SELECT p.idProducto FROM Producto p " +
           "WHERE LOWER(p.nombreItem) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "ORDER BY d.idVersion DESC")
    Page<CotizacionDetalle> findByNombreProductoContaining(@Param("nombre") String nombreProducto,
                                                           Pageable pageable);
}