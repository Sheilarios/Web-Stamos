package com.stamos.repository;

import com.stamos.model.Producto;
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
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    String TIPO_ALQUILER = "Alquiler";
    String TIPO_VENTA = "Venta";
    String TIPO_SERVICIO = "Servicio";

    List<Producto> findByIdCategoria(Integer idCategoria);

    Page<Producto> findByIdCategoria(Integer idCategoria, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.idCategoria = :idCategoria ORDER BY p.nombreItem ASC")
    List<Producto> findByIdCategoriaOrderByNombre(@Param("idCategoria") Integer idCategoria);

    @Query("SELECT p FROM Producto p WHERE p.idCategoria = :idCategoria AND p.activo = true ORDER BY p.nombreItem ASC")
    List<Producto> findActiveByIdCategoria(@Param("idCategoria") Integer idCategoria);

    @Query("SELECT p FROM Producto p WHERE p.idCategoria = :idCategoria AND p.activo = true ORDER BY p.nombreItem ASC")
    Page<Producto> findActiveByIdCategoria(@Param("idCategoria") Integer idCategoria, Pageable pageable);

    long countByIdCategoria(Integer idCategoria);

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.idCategoria = :idCategoria AND p.activo = true")
    long countActiveByIdCategoria(@Param("idCategoria") Integer idCategoria);

    @Query("SELECT p.idCategoria, COUNT(p) as total FROM Producto p GROUP BY p.idCategoria ORDER BY p.idCategoria ASC")
    List<Object[]> countByCategoriaResumen();

    List<Producto> findByTipo(String tipo);

    Page<Producto> findByTipo(String tipo, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.tipo = :tipo ORDER BY p.nombreItem ASC")
    List<Producto> findByTipoOrderByNombre(@Param("tipo") String tipo);

    @Query("SELECT p FROM Producto p WHERE p.tipo = :tipo AND p.activo = true ORDER BY p.nombreItem ASC")
    List<Producto> findActiveByTipo(@Param("tipo") String tipo);

    @Query("SELECT p FROM Producto p WHERE p.tipo = :tipo AND p.activo = true ORDER BY p.nombreItem ASC")
    Page<Producto> findActiveByTipo(@Param("tipo") String tipo, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.tipo = 'Alquiler' AND p.activo = true ORDER BY p.nombreItem ASC")
    List<Producto> findAlquiler();

    @Query("SELECT p FROM Producto p WHERE p.tipo = 'Alquiler' AND p.activo = true ORDER BY p.nombreItem ASC")
    Page<Producto> findAlquiler(Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.tipo = 'Venta' AND p.activo = true ORDER BY p.nombreItem ASC")
    List<Producto> findVenta();

    @Query("SELECT p FROM Producto p WHERE p.tipo = 'Venta' AND p.activo = true ORDER BY p.nombreItem ASC")
    Page<Producto> findVenta(Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.tipo = 'Servicio' AND p.activo = true ORDER BY p.nombreItem ASC")
    List<Producto> findServicios();

    @Query("SELECT p FROM Producto p WHERE p.tipo = 'Servicio' AND p.activo = true ORDER BY p.nombreItem ASC")
    Page<Producto> findServicios(Pageable pageable);

    long countByTipo(String tipo);

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.tipo = :tipo AND p.activo = true")
    long countActiveByTipo(@Param("tipo") String tipo);

    @Query("SELECT p.tipo, COUNT(p) as total FROM Producto p GROUP BY p.tipo ORDER BY p.tipo ASC")
    List<Object[]> countByTipoResumen();

    Optional<Producto> findByNombreItem(String nombreItem);

    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombreItem) LIKE LOWER(CONCAT('%', :nombre, '%')) ORDER BY p.nombreItem ASC")
    List<Producto> findByNombreItemContaining(@Param("nombre") String nombreItem);

    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombreItem) LIKE LOWER(CONCAT('%', :nombre, '%')) ORDER BY p.nombreItem ASC")
    Page<Producto> findByNombreItemContaining(@Param("nombre") String nombreItem, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.activo = true AND LOWER(p.nombreItem) LIKE LOWER(CONCAT('%', :nombre, '%')) ORDER BY p.nombreItem ASC")
    List<Producto> findActiveByNombreItemContaining(@Param("nombre") String nombreItem);

    @Query("SELECT p FROM Producto p WHERE p.activo = true AND LOWER(p.nombreItem) LIKE LOWER(CONCAT('%', :nombre, '%')) ORDER BY p.nombreItem ASC")
    Page<Producto> findActiveByNombreItemContaining(@Param("nombre") String nombreItem, Pageable pageable);

    boolean existsByNombreItem(String nombreItem);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Producto p " +
           "WHERE p.nombreItem = :nombre AND p.idProducto != :idProducto")
    boolean existsByNombreItemAndIdNot(@Param("nombre") String nombreItem,
                                       @Param("idProducto") Integer idProducto);

    @Query("SELECT p FROM Producto p WHERE LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :descripcion, '%')) ORDER BY p.nombreItem ASC")
    List<Producto> findByDescripcionContaining(@Param("descripcion") String descripcion);

    @Query("SELECT p FROM Producto p WHERE LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :descripcion, '%')) ORDER BY p.nombreItem ASC")
    Page<Producto> findByDescripcionContaining(@Param("descripcion") String descripcion, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.precioBase >= :precio ORDER BY p.precioBase DESC")
    List<Producto> findByPrecioBaseGreaterThanEqual(@Param("precio") BigDecimal precioBase);

    @Query("SELECT p FROM Producto p WHERE p.precioBase >= :precio ORDER BY p.precioBase DESC")
    Page<Producto> findByPrecioBaseGreaterThanEqual(@Param("precio") BigDecimal precioBase, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.precioBase BETWEEN :precioMin AND :precioMax ORDER BY p.precioBase ASC")
    List<Producto> findByPrecioBaseBetween(@Param("precioMin") BigDecimal precioMin,
                                           @Param("precioMax") BigDecimal precioMax);

    @Query("SELECT p FROM Producto p WHERE p.precioBase BETWEEN :precioMin AND :precioMax ORDER BY p.precioBase ASC")
    Page<Producto> findByPrecioBaseBetween(@Param("precioMin") BigDecimal precioMin,
                                           @Param("precioMax") BigDecimal precioMax,
                                           Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.precioBase <= :precio ORDER BY p.precioBase ASC")
    List<Producto> findByPrecioBaseLessThanEqual(@Param("precio") BigDecimal precioBase);

    @Query("SELECT COALESCE(AVG(p.precioBase), 0) FROM Producto p")
    BigDecimal avgPrecioBase();

    @Query("SELECT COALESCE(AVG(p.precioBase), 0) FROM Producto p WHERE p.tipo = :tipo")
    BigDecimal avgPrecioBaseByTipo(@Param("tipo") String tipo);

    @Query("SELECT COALESCE(MIN(p.precioBase), 0) FROM Producto p WHERE p.activo = true")
    BigDecimal minPrecioBase();

    @Query("SELECT COALESCE(MAX(p.precioBase), 0) FROM Producto p WHERE p.activo = true")
    BigDecimal maxPrecioBase();

    List<Producto> findByActivo(Boolean activo);

    Page<Producto> findByActivo(Boolean activo, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.activo = true ORDER BY p.nombreItem ASC")
    List<Producto> findActive();

    @Query("SELECT p FROM Producto p WHERE p.activo = true ORDER BY p.nombreItem ASC")
    Page<Producto> findActive(Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.activo = false ORDER BY p.nombreItem ASC")
    List<Producto> findInactive();

    @Query("SELECT p FROM Producto p WHERE p.activo = false ORDER BY p.nombreItem ASC")
    Page<Producto> findInactive(Pageable pageable);

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.activo = true")
    long countActive();

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.activo = false")
    long countInactive();

    @Query("SELECT p FROM Producto p WHERE p.imagenUrl IS NOT NULL AND p.imagenUrl != '' ORDER BY p.nombreItem ASC")
    List<Producto> findWithImagen();

    @Query("SELECT p FROM Producto p WHERE p.imagenUrl IS NOT NULL AND p.imagenUrl != '' ORDER BY p.nombreItem ASC")
    Page<Producto> findWithImagen(Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.imagenUrl IS NULL OR p.imagenUrl = '' ORDER BY p.nombreItem ASC")
    List<Producto> findWithoutImagen();

    List<Producto> findByIdCategoriaAndTipo(Integer idCategoria, String tipo);

    Page<Producto> findByIdCategoriaAndTipo(Integer idCategoria, String tipo, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.idCategoria = :idCategoria AND p.tipo = :tipo AND p.activo = true ORDER BY p.nombreItem ASC")
    List<Producto> findActiveByIdCategoriaAndTipo(@Param("idCategoria") Integer idCategoria,
                                                  @Param("tipo") String tipo);

    @Query("SELECT p FROM Producto p WHERE p.idCategoria = :idCategoria AND LOWER(p.nombreItem) LIKE LOWER(CONCAT('%', :nombre, '%')) ORDER BY p.nombreItem ASC")
    List<Producto> findByIdCategoriaAndNombreContaining(@Param("idCategoria") Integer idCategoria,
                                                        @Param("nombre") String nombreItem);

    @Query("SELECT p FROM Producto p WHERE p.tipo = :tipo AND LOWER(p.nombreItem) LIKE LOWER(CONCAT('%', :nombre, '%')) ORDER BY p.nombreItem ASC")
    List<Producto> findByTipoAndNombreContaining(@Param("tipo") String tipo,
                                                 @Param("nombre") String nombreItem);

    @Query("SELECT p FROM Producto p WHERE p.tipo = 'Alquiler' AND p.activo = true ORDER BY p.precioBase ASC")
    List<Producto> findAlquilerOrderByPrecio();

    @Query("SELECT p FROM Producto p WHERE " +
           "LOWER(p.nombreItem) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(p.tipo) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "p.idCategoria IN (SELECT c.idCategoria FROM CategoriaProducto c WHERE LOWER(c.nombreCategoria) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
           "ORDER BY p.nombreItem ASC")
    List<Producto> search(@Param("searchText") String searchText);

    @Query("SELECT p FROM Producto p WHERE " +
           "LOWER(p.nombreItem) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(p.tipo) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "p.idCategoria IN (SELECT c.idCategoria FROM CategoriaProducto c WHERE LOWER(c.nombreCategoria) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
           "ORDER BY p.nombreItem ASC")
    Page<Producto> search(@Param("searchText") String searchText, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.activo = true AND (" +
           "LOWER(p.nombreItem) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(p.tipo) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "p.idCategoria IN (SELECT c.idCategoria FROM CategoriaProducto c WHERE LOWER(c.nombreCategoria) LIKE LOWER(CONCAT('%', :searchText, '%')))) " +
           "ORDER BY p.nombreItem ASC")
    List<Producto> searchActive(@Param("searchText") String searchText);

    @Query("SELECT p FROM Producto p WHERE p.activo = true AND (" +
           "LOWER(p.nombreItem) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(p.tipo) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "p.idCategoria IN (SELECT c.idCategoria FROM CategoriaProducto c WHERE LOWER(c.nombreCategoria) LIKE LOWER(CONCAT('%', :searchText, '%')))) " +
           "ORDER BY p.nombreItem ASC")
    Page<Producto> searchActive(@Param("searchText") String searchText, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Producto p WHERE MONTH(p.fechaCreacion) = :month AND YEAR(p.fechaCreacion) = :year")
    long countByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT DATE(p.fechaCreacion) as fecha, COUNT(p) as total " +
           "FROM Producto p " +
           "WHERE p.fechaCreacion >= :fechaInicio " +
           "GROUP BY DATE(p.fechaCreacion) " +
           "ORDER BY DATE(p.fechaCreacion) DESC")
    List<Object[]> countCreadosByDay(@Param("fechaInicio") LocalDateTime fechaInicio);

    @Query("SELECT COALESCE(SUM(p.precioBase), 0) FROM Producto p")
    BigDecimal sumPrecioBase();

    @Query("SELECT COALESCE(SUM(p.precioBase), 0) FROM Producto p WHERE p.tipo = :tipo")
    BigDecimal sumPrecioBaseByTipo(@Param("tipo") String tipo);

    @Query("SELECT p FROM Producto p ORDER BY p.fechaCreacion DESC LIMIT :limit")
    List<Producto> findTopN(@Param("limit") int limit);

    @Query("SELECT p FROM Producto p WHERE p.activo = true ORDER BY p.fechaCreacion DESC LIMIT :limit")
    List<Producto> findTopNActive(@Param("limit") int limit);

    @Query("SELECT p FROM Producto p WHERE p.idCategoria = :idCategoria ORDER BY p.fechaCreacion DESC LIMIT :limit")
    List<Producto> findTopNByCategoria(@Param("idCategoria") Integer idCategoria, @Param("limit") int limit);

    @Query("SELECT p FROM Producto p WHERE p.activo = true ORDER BY p.precioBase DESC LIMIT :limit")
    List<Producto> findTopNByPrecio(@Param("limit") int limit);

    @Modifying
    @Transactional
    @Query("UPDATE Producto p SET p.precioBase = :precio, p.fechaActualizacion = CURRENT_TIMESTAMP WHERE p.idProducto = :idProducto")
    void updatePrecioBase(@Param("idProducto") Integer idProducto,
                          @Param("precio") BigDecimal nuevoPrecio);

    @Modifying
    @Transactional
    @Query("UPDATE Producto p SET p.imagenUrl = :imagen, p.fechaActualizacion = CURRENT_TIMESTAMP WHERE p.idProducto = :idProducto")
    void updateImagenUrl(@Param("idProducto") Integer idProducto,
                         @Param("imagen") String nuevaImagen);

    @Modifying
    @Transactional
    @Query("UPDATE Producto p SET p.activo = true, p.fechaActualizacion = CURRENT_TIMESTAMP WHERE p.idProducto = :idProducto")
    void activar(@Param("idProducto") Integer idProducto);

    @Modifying
    @Transactional
    @Query("UPDATE Producto p SET p.activo = false, p.fechaActualizacion = CURRENT_TIMESTAMP WHERE p.idProducto = :idProducto")
    void desactivar(@Param("idProducto") Integer idProducto);

    @Modifying
    @Transactional
    @Query("UPDATE Producto p SET p.activo = :activo, p.fechaActualizacion = CURRENT_TIMESTAMP WHERE p.idCategoria = :idCategoria")
    void updateActivoByCategoria(@Param("idCategoria") Integer idCategoria,
                                 @Param("activo") Boolean activo);

    @Modifying
    @Transactional
    @Query("DELETE FROM Producto p WHERE p.idCategoria = :idCategoria")
    void deleteByIdCategoria(@Param("idCategoria") Integer idCategoria);

    @Modifying
    @Transactional
    @Query("DELETE FROM Producto p WHERE p.activo = false AND p.fechaCreacion < :fecha")
    int deleteInactiveOlderThan(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM CotizacionDetalle d WHERE d.idProducto = :idProducto")
    boolean hasReferenciasEnCotizaciones(@Param("idProducto") Integer idProducto);

    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM Inventario i WHERE i.idProducto = :idProducto")
    boolean hasReferenciasEnInventario(@Param("idProducto") Integer idProducto);

    @Query("SELECT CASE WHEN COUNT(d) > 0 OR COUNT(i) > 0 THEN true ELSE false END " +
           "FROM CotizacionDetalle d, Inventario i " +
           "WHERE d.idProducto = :idProducto OR i.idProducto = :idProducto")
    boolean hasReferencias(@Param("idProducto") Integer idProducto);

    @Query("SELECT " +
           "COUNT(p) as total, " +
           "SUM(CASE WHEN p.activo = true THEN 1 ELSE 0 END) as activos, " +
           "SUM(CASE WHEN p.tipo = 'Alquiler' THEN 1 ELSE 0 END) as alquiler, " +
           "SUM(CASE WHEN p.tipo = 'Venta' THEN 1 ELSE 0 END) as venta, " +
           "SUM(CASE WHEN p.tipo = 'Servicio' THEN 1 ELSE 0 END) as servicio, " +
           "COALESCE(AVG(p.precioBase), 0) as precioPromedio " +
           "FROM Producto p")
    List<Object[]> getEstadisticasDashboard();

    @Query("SELECT c.nombreCategoria, " +
           "COUNT(p) as total, " +
           "SUM(CASE WHEN p.activo = true THEN 1 ELSE 0 END) as activos " +
           "FROM CategoriaProducto c LEFT JOIN Producto p ON c.idCategoria = p.idCategoria " +
           "GROUP BY c.idCategoria " +
           "ORDER BY c.nombreCategoria ASC")
    List<Object[]> getEstadisticasPorCategoria();

    @Query("SELECT p.idProducto, p.nombreItem, SUM(d.cantidad) as total " +
           "FROM Producto p JOIN CotizacionDetalle d ON p.idProducto = d.idProducto " +
           "GROUP BY p.idProducto, p.nombreItem " +
           "ORDER BY total DESC")
    List<Object[]> findTopProductosVendidos();

    @Query("SELECT p.idProducto, p.nombreItem, SUM(d.subtotalLinea) as total " +
           "FROM Producto p JOIN CotizacionDetalle d ON p.idProducto = d.idProducto " +
           "GROUP BY p.idProducto, p.nombreItem " +
           "ORDER BY total DESC")
    List<Object[]> findTopProductosFacturacion();

    @Query("SELECT p.idProducto, p.nombreItem, COUNT(i) as stock " +
           "FROM Producto p JOIN Inventario i ON p.idProducto = i.idProducto " +
           "WHERE i.estadoFisico = 'Disponible' " +
           "GROUP BY p.idProducto, p.nombreItem " +
           "HAVING COUNT(i) < :limiteStock " +
           "ORDER BY COUNT(i) ASC")
    List<Object[]> findProductosBajoStock(@Param("limiteStock") int limiteStock);

    public long countByActivo(boolean activo);
}