package com.stamos.service;

import com.stamos.model.CategoriaProducto;
import com.stamos.model.Producto;
import com.stamos.repository.CategoriaProductoRepository;
import com.stamos.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoriaProductoService {

    @Autowired
    private CategoriaProductoRepository categoriaProductoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Transactional
    public CategoriaProducto crear(CategoriaProducto categoriaProducto, Integer idUsuarioCreacion) {
        if (categoriaProductoRepository.existsByNombreCategoria(categoriaProducto.getNombreCategoria())) {
            throw new RuntimeException("Ya existe una categoría con el nombre: " + 
                                      categoriaProducto.getNombreCategoria());
        }

        if (categoriaProducto.getNombreCategoria() == null || 
            categoriaProducto.getNombreCategoria().trim().isEmpty()) {
            throw new RuntimeException("El nombre de la categoría es obligatorio");
        }

        categoriaProducto.setActivo(true);
        categoriaProducto.setFechaCreacion(LocalDateTime.now());
        categoriaProducto.setIdUsuarioCreacion(idUsuarioCreacion);
        categoriaProducto.setFechaActualizacion(LocalDateTime.now());
        categoriaProducto.setIdUsuarioActualizacion(idUsuarioCreacion);

        return categoriaProductoRepository.save(categoriaProducto);
    }

    @Transactional
    public CategoriaProducto crear(String nombreCategoria, String descripcion, Integer idUsuarioCreacion) {
        CategoriaProducto categoria = new CategoriaProducto();
        categoria.setNombreCategoria(nombreCategoria);
        categoria.setDescripcion(descripcion);
        return crear(categoria, idUsuarioCreacion);
    }


    @Transactional
    public CategoriaProducto actualizar(Integer idCategoria, CategoriaProducto categoriaProducto,
                                        Integer idUsuarioActualizacion) {
        CategoriaProducto existente = categoriaProductoRepository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + idCategoria));

        if (!existente.getNombreCategoria().equals(categoriaProducto.getNombreCategoria()) &&
            categoriaProductoRepository.existsByNombreCategoriaAndIdNot(
                categoriaProducto.getNombreCategoria(), idCategoria)) {
            throw new RuntimeException("Ya existe otra categoría con el nombre: " + 
                                      categoriaProducto.getNombreCategoria());
        }

        if (categoriaProducto.getNombreCategoria() == null || 
            categoriaProducto.getNombreCategoria().trim().isEmpty()) {
            throw new RuntimeException("El nombre de la categoría es obligatorio");
        }

        existente.setNombreCategoria(categoriaProducto.getNombreCategoria());
        existente.setDescripcion(categoriaProducto.getDescripcion());
        if (categoriaProducto.getActivo() != null) {
            existente.setActivo(categoriaProducto.getActivo());
        }
        existente.setFechaActualizacion(LocalDateTime.now());
        existente.setIdUsuarioActualizacion(idUsuarioActualizacion);

        return categoriaProductoRepository.save(existente);
    }


    @Transactional
    public void eliminar(Integer idCategoria) {
        CategoriaProducto categoria = categoriaProductoRepository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + idCategoria));

        long cantidadProductos = productoRepository.countByIdCategoria(idCategoria);
        if (cantidadProductos > 0) {
            throw new RuntimeException("No se puede eliminar la categoría porque tiene " + 
                                      cantidadProductos + " productos asociados. " +
                                      "Desactívela en su lugar.");
        }

        categoriaProductoRepository.delete(categoria);
    }


    @Transactional
    public CategoriaProducto desactivar(Integer idCategoria, Integer idUsuarioActualizacion) {
        CategoriaProducto categoria = categoriaProductoRepository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + idCategoria));

        categoria.setActivo(false);
        categoria.setFechaActualizacion(LocalDateTime.now());
        categoria.setIdUsuarioActualizacion(idUsuarioActualizacion);

        return categoriaProductoRepository.save(categoria);
    }

    @Transactional
    public CategoriaProducto activar(Integer idCategoria, Integer idUsuarioActualizacion) {
        CategoriaProducto categoria = categoriaProductoRepository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + idCategoria));

        categoria.setActivo(true);
        categoria.setFechaActualizacion(LocalDateTime.now());
        categoria.setIdUsuarioActualizacion(idUsuarioActualizacion);

        return categoriaProductoRepository.save(categoria);
    }


    public CategoriaProducto findById(Integer idCategoria) {
        return categoriaProductoRepository.findById(idCategoria).orElse(null);
    }

    public CategoriaProducto findByNombre(String nombreCategoria) {
        return categoriaProductoRepository.findByNombreCategoria(nombreCategoria).orElse(null);
    }

    public List<CategoriaProducto> findAll() {
        return categoriaProductoRepository.findAllOrderByNombreCategoriaAsc();
    }

    public Page<CategoriaProducto> findAll(Pageable pageable) {
        return categoriaProductoRepository.findAll(pageable);
    }

    public List<CategoriaProducto> findAllActive() {
        return categoriaProductoRepository.findAllActive();
    }

    public Page<CategoriaProducto> findAllActive(Pageable pageable) {
        return categoriaProductoRepository.findAllActive(pageable);
    }

    public List<CategoriaProducto> findAllInactive() {
        return categoriaProductoRepository.findAllInactive();
    }

    public List<CategoriaProducto> findByNombreContaining(String nombre) {
        return categoriaProductoRepository.findByNombreContaining(nombre);
    }

    public Page<CategoriaProducto> findByNombreContaining(String nombre, Pageable pageable) {
        return categoriaProductoRepository.findByNombreContaining(nombre, pageable);
    }

    public List<CategoriaProducto> search(String searchText) {
        return categoriaProductoRepository.search(searchText);
    }

    public Page<CategoriaProducto> search(String searchText, Pageable pageable) {
        return categoriaProductoRepository.search(searchText, pageable);
    }

    public List<CategoriaProducto> findTopCategoriesByProductCount(int limit) {
        return categoriaProductoRepository.findTopCategoriesByProductCount(limit);
    }
    public List<CategoriaProducto> findTopCategoriesByActiveProductCount(int limit) {
        return categoriaProductoRepository.findTopCategoriesByActiveProductCount(limit);
    }
    public List<CategoriaProducto> findCategoriasWithProductos() {
        return categoriaProductoRepository.findCategoriasWithProductos();
    }
    public List<CategoriaProducto> findCategoriasActivasWithProductos() {
        return categoriaProductoRepository.findCategoriasActivasWithProductos();
    }
    public long count() {
        return categoriaProductoRepository.count();
    }
    public long countActive() {
        return categoriaProductoRepository.countActive();
    }
    public long countInactive() {
        return categoriaProductoRepository.countInactive();
    }
    public long countProductosByCategoria(Integer idCategoria) {
        return categoriaProductoRepository.countProductosByCategoria(idCategoria);
    }
    public long countProductosActivosByCategoria(Integer idCategoria) {
        return categoriaProductoRepository.countProductosActivosByCategoria(idCategoria);
    }
    public List<Object[]> findCategoriasWithProductCount() {
        return categoriaProductoRepository.findCategoriasWithProductCount();
    }
    public List<Object[]> findCategoriasActivasWithProductCount() {
        return categoriaProductoRepository.findCategoriasActivasWithProductCount();
    }
    public boolean existsByNombre(String nombreCategoria) {
        return categoriaProductoRepository.existsByNombreCategoria(nombreCategoria);
    }

    public boolean hasProductos(Integer idCategoria) {
        return categoriaProductoRepository.countProductosByCategoria(idCategoria) > 0;
    }

    public boolean isActivo(Integer idCategoria) {
        CategoriaProducto categoria = findById(idCategoria);
        return categoria != null && categoria.isActivo();
    }

    public List<CategoriaProducto> getCategoriasPredefinidas() {
        return List.of(
            CategoriaProducto.crearCategoriaStands(),
            CategoriaProducto.crearCategoriaMobiliario(),
            CategoriaProducto.crearCategoriaEquipamiento(),
            CategoriaProducto.crearCategoriaPublicidad()
        );
    }

    @Transactional
    public void inicializarCategoriasPredefinidas(Integer idUsuarioCreacion) {
        List<CategoriaProducto> predefinidas = getCategoriasPredefinidas();
        
        for (CategoriaProducto categoria : predefinidas) {
            if (!existsByNombre(categoria.getNombreCategoria())) {
                crear(categoria, idUsuarioCreacion);
            }
        }
    }

    public String getNombreCategoria(Integer idCategoria) {
        CategoriaProducto categoria = findById(idCategoria);
        return categoria != null ? categoria.getNombreCategoria() : null;
    }

    public String getResumenCategoria(Integer idCategoria) {
        CategoriaProducto categoria = findById(idCategoria);
        if (categoria == null) {
            return "Categoría no encontrada";
        }
        
        long totalProductos = countProductosByCategoria(idCategoria);
        long totalActivos = countProductosActivosByCategoria(idCategoria);
        
        return categoria.getNombreCategoria() + 
               " (" + totalProductos + " productos, " + 
               totalActivos + " activos)";
    }


    public boolean puedeSerEliminada(Integer idCategoria) {
        return !hasProductos(idCategoria);
    }


    public String getEstadoLegible(Integer idCategoria) {
        CategoriaProducto categoria = findById(idCategoria);
        if (categoria == null) {
            return "No existe";
        }
        return categoria.isActivo() ? "Activo" : "Inactivo";
    }

    public String getEstadoBadgeClass(Integer idCategoria) {
        CategoriaProducto categoria = findById(idCategoria);
        if (categoria == null) {
            return "badge-secondary";
        }
        return categoria.isActivo() ? "badge-success" : "badge-danger";
    }

    public String getEstadoColor(Integer idCategoria) {
        CategoriaProducto categoria = findById(idCategoria);
        if (categoria == null) {
            return "#6B7280";
        }
        return categoria.isActivo() ? "#10B981" : "#EF4444";
    }

    public String getEstadoIcono(Integer idCategoria) {
        CategoriaProducto categoria = findById(idCategoria);
        if (categoria == null) {
            return "ti ti-circle";
        }
        return categoria.isActivo() ? "ti ti-check-circle" : "ti ti-x-circle";
    }

    public String getColorByNombre(String nombreCategoria) {
        if (nombreCategoria == null) {
            return "#6B7280";
        }
        
        return switch (nombreCategoria) {
            case "Stands y estructuras feriales" -> "#3B82F6";
            case "Mobiliario para eventos" -> "#10B981";
            case "Equipamiento interactivo y audiovisual" -> "#8B5CF6";
            case "Publicidad gráfica" -> "#F59E0B";
            default -> "#6B7280";
        };
    }

    public String getIconoByNombre(String nombreCategoria) {
        if (nombreCategoria == null) {
            return "ti ti-category";
        }
        
        return switch (nombreCategoria) {
            case "Stands y estructuras feriales" -> "ti ti-building";
            case "Mobiliario para eventos" -> "ti ti-chair";
            case "Equipamiento interactivo y audiovisual" -> "ti ti-device-tv";
            case "Publicidad gráfica" -> "ti ti-printer";
            default -> "ti ti-category";
        };
    }

    public List<Object[]> getEstadisticasParaDashboard() {
        return categoriaProductoRepository.findCategoriasWithProductCount();
    }

    public double getPorcentajeActivas() {
        long total = count();
        if (total == 0) {
            return 0;
        }
        return (countActive() * 100.0) / total;
    }

    public double getPorcentajeInactivas() {
        long total = count();
        if (total == 0) {
            return 0;
        }
        return (countInactive() * 100.0) / total;
    }

    @Transactional
    public void actualizarConteosProductos() {
    }

    public CategoriaProducto findCategoriaWithProductos(Integer idCategoria) {
        CategoriaProducto categoria = findById(idCategoria);
        if (categoria != null) {
            List<Producto> productos = productoRepository.findByIdCategoria(idCategoria);
            categoria.setCantidadProductos(productos.size());
            long activos = productos.stream().filter(Producto::isActivo).count();
            categoria.setCantidadProductosActivos((int) activos);
        }
        return categoria;
    }

    public List<CategoriaProducto> findAllCategoriasWithProductos() {
        List<CategoriaProducto> categorias = findAll();
        for (CategoriaProducto categoria : categorias) {
            List<Producto> productos = productoRepository.findByIdCategoria(categoria.getIdCategoria());
            categoria.setCantidadProductos(productos.size());
            long activos = productos.stream().filter(Producto::isActivo).count();
            categoria.setCantidadProductosActivos((int) activos);
        }
        return categorias;
    }
}