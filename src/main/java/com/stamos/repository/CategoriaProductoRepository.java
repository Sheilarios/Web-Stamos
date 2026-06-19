package com.stamos.repository;

import com.stamos.model.CategoriaProducto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaProductoRepository extends JpaRepository<CategoriaProducto, Integer> {

    Optional<CategoriaProducto> findByNombreCategoria(String nombreCategoria);

    @Query("SELECT c FROM CategoriaProducto c WHERE c.activo = true ORDER BY c.nombreCategoria ASC")
    List<CategoriaProducto> findAllActive();

    @Query("SELECT c FROM CategoriaProducto c WHERE c.activo = true ORDER BY c.nombreCategoria ASC")
    Page<CategoriaProducto> findAllActive(Pageable pageable);

    @Query("SELECT c FROM CategoriaProducto c WHERE c.activo = false ORDER BY c.nombreCategoria ASC")
    List<CategoriaProducto> findAllInactive();

    @Query("SELECT c FROM CategoriaProducto c WHERE LOWER(c.nombreCategoria) LIKE LOWER(CONCAT('%', :nombre, '%')) ORDER BY c.nombreCategoria ASC")
    List<CategoriaProducto> findByNombreContaining(@Param("nombre") String nombre);

    @Query("SELECT c FROM CategoriaProducto c WHERE LOWER(c.nombreCategoria) LIKE LOWER(CONCAT('%', :nombre, '%')) ORDER BY c.nombreCategoria ASC")
    Page<CategoriaProducto> findByNombreContaining(@Param("nombre") String nombre, Pageable pageable);

    @Query("SELECT c FROM CategoriaProducto c WHERE LOWER(c.descripcion) LIKE LOWER(CONCAT('%', :descripcion, '%')) ORDER BY c.nombreCategoria ASC")
    List<CategoriaProducto> findByDescripcionContaining(@Param("descripcion") String descripcion);

    @Query("SELECT c FROM CategoriaProducto c WHERE " +
           "LOWER(c.nombreCategoria) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(c.descripcion) LIKE LOWER(CONCAT('%', :searchText, '%')) ORDER BY c.nombreCategoria ASC")
    List<CategoriaProducto> search(@Param("searchText") String searchText);

    @Query("SELECT c FROM CategoriaProducto c WHERE " +
           "LOWER(c.nombreCategoria) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(c.descripcion) LIKE LOWER(CONCAT('%', :searchText, '%')) ORDER BY c.nombreCategoria ASC")
    Page<CategoriaProducto> search(@Param("searchText") String searchText, Pageable pageable);

    @Query("SELECT COUNT(c) FROM CategoriaProducto c WHERE c.activo = true")
    long countActive();

    @Query("SELECT COUNT(c) FROM CategoriaProducto c WHERE c.activo = false")
    long countInactive();

    boolean existsByNombreCategoria(String nombreCategoria);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CategoriaProducto c " +
           "WHERE c.nombreCategoria = :nombreCategoria AND c.idCategoria != :idCategoria")
    boolean existsByNombreCategoriaAndIdNot(@Param("nombreCategoria") String nombreCategoria,
                                            @Param("idCategoria") Integer idCategoria);

    @Query(value = "SELECT c.*, COUNT(p.id_producto) as total_productos " +
                   "FROM CategoriaProducto c " +
                   "LEFT JOIN ProductoServicio p ON c.id_categoria = p.id_categoria " +
                   "WHERE c.activo = true " +
                   "GROUP BY c.id_categoria " +
                   "ORDER BY total_productos DESC " +
                   "LIMIT :limit", nativeQuery = true)
    List<CategoriaProducto> findTopCategoriesByProductCount(@Param("limit") int limit);

    @Query(value = "SELECT c.*, COUNT(p.id_producto) as total_productos " +
                   "FROM CategoriaProducto c " +
                   "LEFT JOIN ProductoServicio p ON c.id_categoria = p.id_categoria AND p.activo = true " +
                   "WHERE c.activo = true " +
                   "GROUP BY c.id_categoria " +
                   "ORDER BY total_productos DESC " +
                   "LIMIT :limit", nativeQuery = true)
    List<CategoriaProducto> findTopCategoriesByActiveProductCount(@Param("limit") int limit);

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.idCategoria = :idCategoria")
    long countProductosByCategoria(@Param("idCategoria") Integer idCategoria);

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.idCategoria = :idCategoria AND p.activo = true")
    long countProductosActivosByCategoria(@Param("idCategoria") Integer idCategoria);

    @Query("SELECT c, COUNT(p) as total FROM CategoriaProducto c " +
           "LEFT JOIN Producto p ON c.idCategoria = p.idCategoria " +
           "GROUP BY c.idCategoria ORDER BY c.nombreCategoria ASC")
    List<Object[]> findCategoriasWithProductCount();

    @Query("SELECT c, COUNT(p) as total FROM CategoriaProducto c " +
           "LEFT JOIN Producto p ON c.idCategoria = p.idCategoria AND p.activo = true " +
           "WHERE c.activo = true " +
           "GROUP BY c.idCategoria ORDER BY c.nombreCategoria ASC")
    List<Object[]> findCategoriasActivasWithProductCount();

    List<CategoriaProducto> findByActivoOrderByNombreCategoriaAsc(Boolean activo);

    @Query("SELECT c FROM CategoriaProducto c ORDER BY c.nombreCategoria ASC")
    List<CategoriaProducto> findAllOrderByNombreCategoriaAsc();

    @Query("SELECT DISTINCT c FROM CategoriaProducto c JOIN Producto p ON c.idCategoria = p.idCategoria ORDER BY c.nombreCategoria ASC")
    List<CategoriaProducto> findCategoriasWithProductos();

    @Query("SELECT DISTINCT c FROM CategoriaProducto c JOIN Producto p ON c.idCategoria = p.idCategoria WHERE c.activo = true ORDER BY c.nombreCategoria ASC")
    List<CategoriaProducto> findCategoriasActivasWithProductos();

    @Query("SELECT c FROM CategoriaProducto c WHERE LOWER(c.nombreCategoria) = LOWER(:nombreCategoria)")
    Optional<CategoriaProducto> findByNombreCategoriaIgnoreCase(@Param("nombreCategoria") String nombreCategoria);
}