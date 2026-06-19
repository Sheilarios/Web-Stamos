package com.stamos.service;

import com.stamos.model.CategoriaProducto;
import com.stamos.model.Producto;
import com.stamos.repository.CategoriaProductoRepository;
import com.stamos.repository.ProductoRepository;
import com.stamos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de categorías de productos.
 * 
 * <p>Este servicio proporciona métodos para gestionar las categorías de
 * productos y servicios del catálogo de STAMOS S.L.</p>
 * 
 * <p>Implementa los requerimientos:</p>
 * <ul>
 *   <li>RF-11: Gestionar catálogo de productos y servicios con categorías</li>
 * </ul>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
@Service
public class CategoriaProductoService {

    @Autowired
    private CategoriaProductoRepository categoriaProductoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ============================================
    // CRUD - CREACIÓN
    // ============================================

    /**
     * Crea una nueva categoría de producto.
     * 
     * @param categoriaProducto Categoría a crear
     * @param idUsuarioCreacion ID del usuario que crea
     * @return Categoría creada
     * @throws RuntimeException si ya existe una categoría con el mismo nombre
     */
    @Transactional
    public CategoriaProducto crear(CategoriaProducto categoriaProducto, Integer idUsuarioCreacion) {
        // Verificar que no exista una categoría con el mismo nombre
        if (categoriaProductoRepository.existsByNombreCategoria(categoriaProducto.getNombreCategoria())) {
            throw new RuntimeException("Ya existe una categoría con el nombre: " + 
                                      categoriaProducto.getNombreCategoria());
        }

        // Validar datos
        if (categoriaProducto.getNombreCategoria() == null || 
            categoriaProducto.getNombreCategoria().trim().isEmpty()) {
            throw new RuntimeException("El nombre de la categoría es obligatorio");
        }

        // Establecer datos de auditoría
        categoriaProducto.setActivo(true);
        categoriaProducto.setFechaCreacion(LocalDateTime.now());
        categoriaProducto.setIdUsuarioCreacion(idUsuarioCreacion);
        categoriaProducto.setFechaActualizacion(LocalDateTime.now());
        categoriaProducto.setIdUsuarioActualizacion(idUsuarioCreacion);

        return categoriaProductoRepository.save(categoriaProducto);
    }

    /**
     * Crea una nueva categoría de producto (versión simplificada).
     * 
     * @param nombreCategoria Nombre de la categoría
     * @param descripcion Descripción de la categoría
     * @param idUsuarioCreacion ID del usuario que crea
     * @return Categoría creada
     */
    @Transactional
    public CategoriaProducto crear(String nombreCategoria, String descripcion, Integer idUsuarioCreacion) {
        CategoriaProducto categoria = new CategoriaProducto();
        categoria.setNombreCategoria(nombreCategoria);
        categoria.setDescripcion(descripcion);
        return crear(categoria, idUsuarioCreacion);
    }

    // ============================================
    // CRUD - ACTUALIZACIÓN
    // ============================================

    /**
     * Actualiza una categoría existente.
     * 
     * @param idCategoria ID de la categoría
     * @param categoriaProducto Datos actualizados
     * @param idUsuarioActualizacion ID del usuario que actualiza
     * @return Categoría actualizada
     * @throws RuntimeException si la categoría no existe o el nombre ya está en uso
     */
    @Transactional
    public CategoriaProducto actualizar(Integer idCategoria, CategoriaProducto categoriaProducto,
                                        Integer idUsuarioActualizacion) {
        // Verificar que la categoría existe
        CategoriaProducto existente = categoriaProductoRepository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + idCategoria));

        // Verificar que el nombre no esté en uso por otra categoría
        if (!existente.getNombreCategoria().equals(categoriaProducto.getNombreCategoria()) &&
            categoriaProductoRepository.existsByNombreCategoriaAndIdNot(
                categoriaProducto.getNombreCategoria(), idCategoria)) {
            throw new RuntimeException("Ya existe otra categoría con el nombre: " + 
                                      categoriaProducto.getNombreCategoria());
        }

        // Validar datos
        if (categoriaProducto.getNombreCategoria() == null || 
            categoriaProducto.getNombreCategoria().trim().isEmpty()) {
            throw new RuntimeException("El nombre de la categoría es obligatorio");
        }

        // Actualizar datos
        existente.setNombreCategoria(categoriaProducto.getNombreCategoria());
        existente.setDescripcion(categoriaProducto.getDescripcion());
        if (categoriaProducto.getActivo() != null) {
            existente.setActivo(categoriaProducto.getActivo());
        }
        existente.setFechaActualizacion(LocalDateTime.now());
        existente.setIdUsuarioActualizacion(idUsuarioActualizacion);

        return categoriaProductoRepository.save(existente);
    }

    // ============================================
    // CRUD - ELIMINACIÓN
    // ============================================

    /**
     * Elimina una categoría (solo si no tiene productos asociados).
     * 
     * @param idCategoria ID de la categoría
     * @throws RuntimeException si la categoría tiene productos asociados
     */
    @Transactional
    public void eliminar(Integer idCategoria) {
        // Verificar que la categoría existe
        CategoriaProducto categoria = categoriaProductoRepository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + idCategoria));

        // Verificar que no tenga productos asociados
        long cantidadProductos = productoRepository.countByIdCategoria(idCategoria);
        if (cantidadProductos > 0) {
            throw new RuntimeException("No se puede eliminar la categoría porque tiene " + 
                                      cantidadProductos + " productos asociados. " +
                                      "Desactívela en su lugar.");
        }

        categoriaProductoRepository.delete(categoria);
    }

    /**
     * Desactiva una categoría (cambia estado a inactivo).
     * 
     * @param idCategoria ID de la categoría
     * @param idUsuarioActualizacion ID del usuario que actualiza
     * @return Categoría desactivada
     */
    @Transactional
    public CategoriaProducto desactivar(Integer idCategoria, Integer idUsuarioActualizacion) {
        CategoriaProducto categoria = categoriaProductoRepository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + idCategoria));

        categoria.setActivo(false);
        categoria.setFechaActualizacion(LocalDateTime.now());
        categoria.setIdUsuarioActualizacion(idUsuarioActualizacion);

        return categoriaProductoRepository.save(categoria);
    }

    /**
     * Activa una categoría (cambia estado a activo).
     * 
     * @param idCategoria ID de la categoría
     * @param idUsuarioActualizacion ID del usuario que actualiza
     * @return Categoría activada
     */
    @Transactional
    public CategoriaProducto activar(Integer idCategoria, Integer idUsuarioActualizacion) {
        CategoriaProducto categoria = categoriaProductoRepository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + idCategoria));

        categoria.setActivo(true);
        categoria.setFechaActualizacion(LocalDateTime.now());
        categoria.setIdUsuarioActualizacion(idUsuarioActualizacion);

        return categoriaProductoRepository.save(categoria);
    }

    // ============================================
    // CRUD - CONSULTAS
    // ============================================

    /**
     * Busca una categoría por ID.
     * 
     * @param idCategoria ID de la categoría
     * @return Categoría encontrada
     */
    public CategoriaProducto findById(Integer idCategoria) {
        return categoriaProductoRepository.findById(idCategoria).orElse(null);
    }

    /**
     * Busca una categoría por nombre.
     * 
     * @param nombreCategoria Nombre de la categoría
     * @return Categoría encontrada
     */
    public CategoriaProducto findByNombre(String nombreCategoria) {
        return categoriaProductoRepository.findByNombreCategoria(nombreCategoria).orElse(null);
    }

    /**
     * Obtiene todas las categorías.
     * 
     * @return Lista de categorías
     */
    public List<CategoriaProducto> findAll() {
        return categoriaProductoRepository.findAllOrderByNombreCategoriaAsc();
    }

    /**
     * Obtiene todas las categorías con paginación.
     * 
     * @param pageable Configuración de paginación
     * @return Página de categorías
     */
    public Page<CategoriaProducto> findAll(Pageable pageable) {
        return categoriaProductoRepository.findAll(pageable);
    }

    /**
     * Obtiene categorías activas.
     * 
     * @return Lista de categorías activas
     */
    public List<CategoriaProducto> findAllActive() {
        return categoriaProductoRepository.findAllActive();
    }

    /**
     * Obtiene categorías activas con paginación.
     * 
     * @param pageable Configuración de paginación
     * @return Página de categorías activas
     */
    public Page<CategoriaProducto> findAllActive(Pageable pageable) {
        return categoriaProductoRepository.findAllActive(pageable);
    }

    /**
     * Obtiene categorías inactivas.
     * 
     * @return Lista de categorías inactivas
     */
    public List<CategoriaProducto> findAllInactive() {
        return categoriaProductoRepository.findAllInactive();
    }

    /**
     * Busca categorías por nombre que contengan el texto.
     * 
     * @param nombre Texto a buscar
     * @return Lista de categorías
     */
    public List<CategoriaProducto> findByNombreContaining(String nombre) {
        return categoriaProductoRepository.findByNombreContaining(nombre);
    }

    /**
     * Busca categorías por nombre que contengan el texto con paginación.
     * 
     * @param nombre Texto a buscar
     * @param pageable Configuración de paginación
     * @return Página de categorías
     */
    public Page<CategoriaProducto> findByNombreContaining(String nombre, Pageable pageable) {
        return categoriaProductoRepository.findByNombreContaining(nombre, pageable);
    }

    /**
     * Busca categorías por nombre o descripción.
     * 
     * @param searchText Texto a buscar
     * @return Lista de categorías
     */
    public List<CategoriaProducto> search(String searchText) {
        return categoriaProductoRepository.search(searchText);
    }

    /**
     * Busca categorías por nombre o descripción con paginación.
     * 
     * @param searchText Texto a buscar
     * @param pageable Configuración de paginación
     * @return Página de categorías
     */
    public Page<CategoriaProducto> search(String searchText, Pageable pageable) {
        return categoriaProductoRepository.search(searchText, pageable);
    }

    /**
     * Obtiene las categorías con más productos.
     * 
     * @param limit Número máximo de resultados
     * @return Lista de categorías
     */
    public List<CategoriaProducto> findTopCategoriesByProductCount(int limit) {
        return categoriaProductoRepository.findTopCategoriesByProductCount(limit);
    }

    /**
     * Obtiene las categorías con más productos activos.
     * 
     * @param limit Número máximo de resultados
     * @return Lista de categorías
     */
    public List<CategoriaProducto> findTopCategoriesByActiveProductCount(int limit) {
        return categoriaProductoRepository.findTopCategoriesByActiveProductCount(limit);
    }

    /**
     * Obtiene categorías con productos asociados.
     * 
     * @return Lista de categorías
     */
    public List<CategoriaProducto> findCategoriasWithProductos() {
        return categoriaProductoRepository.findCategoriasWithProductos();
    }

    /**
     * Obtiene categorías activas con productos asociados.
     * 
     * @return Lista de categorías
     */
    public List<CategoriaProducto> findCategoriasActivasWithProductos() {
        return categoriaProductoRepository.findCategoriasActivasWithProductos();
    }

    // ============================================
    // MÉTRICAS Y ESTADÍSTICAS
    // ============================================

    /**
     * Cuenta el número total de categorías.
     * 
     * @return Número de categorías
     */
    public long count() {
        return categoriaProductoRepository.count();
    }

    /**
     * Cuenta el número de categorías activas.
     * 
     * @return Número de categorías activas
     */
    public long countActive() {
        return categoriaProductoRepository.countActive();
    }

    /**
     * Cuenta el número de categorías inactivas.
     * 
     * @return Número de categorías inactivas
     */
    public long countInactive() {
        return categoriaProductoRepository.countInactive();
    }

    /**
     * Cuenta el número de productos de una categoría.
     * 
     * @param idCategoria ID de la categoría
     * @return Número de productos
     */
    public long countProductosByCategoria(Integer idCategoria) {
        return categoriaProductoRepository.countProductosByCategoria(idCategoria);
    }

    /**
     * Cuenta el número de productos activos de una categoría.
     * 
     * @param idCategoria ID de la categoría
     * @return Número de productos activos
     */
    public long countProductosActivosByCategoria(Integer idCategoria) {
        return categoriaProductoRepository.countProductosActivosByCategoria(idCategoria);
    }

    /**
     * Obtiene las categorías con sus conteos de productos.
     * 
     * @return Lista de objetos con categoría y conteo
     */
    public List<Object[]> findCategoriasWithProductCount() {
        return categoriaProductoRepository.findCategoriasWithProductCount();
    }

    /**
     * Obtiene las categorías activas con sus conteos de productos activos.
     * 
     * @return Lista de objetos con categoría y conteo
     */
    public List<Object[]> findCategoriasActivasWithProductCount() {
        return categoriaProductoRepository.findCategoriasActivasWithProductCount();
    }

    // ============================================
    // VERIFICACIONES
    // ============================================

    /**
     * Verifica si existe una categoría con el nombre proporcionado.
     * 
     * @param nombreCategoria Nombre de la categoría
     * @return true si existe, false en caso contrario
     */
    public boolean existsByNombre(String nombreCategoria) {
        return categoriaProductoRepository.existsByNombreCategoria(nombreCategoria);
    }

    /**
     * Verifica si una categoría tiene productos asociados.
     * 
     * @param idCategoria ID de la categoría
     * @return true si tiene productos, false en caso contrario
     */
    public boolean hasProductos(Integer idCategoria) {
        return categoriaProductoRepository.countProductosByCategoria(idCategoria) > 0;
    }

    /**
     * Verifica si una categoría está activa.
     * 
     * @param idCategoria ID de la categoría
     * @return true si está activa, false en caso contrario
     */
    public boolean isActivo(Integer idCategoria) {
        CategoriaProducto categoria = findById(idCategoria);
        return categoria != null && categoria.isActivo();
    }

    // ============================================
    // MÉTODOS DE UTILIDAD
    // ============================================

    /**
     * Obtiene las categorías predefinidas del sistema.
     * 
     * @return Lista de categorías predefinidas
     */
    public List<CategoriaProducto> getCategoriasPredefinidas() {
        return List.of(
            CategoriaProducto.crearCategoriaStands(),
            CategoriaProducto.crearCategoriaMobiliario(),
            CategoriaProducto.crearCategoriaEquipamiento(),
            CategoriaProducto.crearCategoriaPublicidad()
        );
    }

    /**
     * Inicializa las categorías predefinidas si no existen.
     * 
     * @param idUsuarioCreacion ID del usuario que crea
     */
    @Transactional
    public void inicializarCategoriasPredefinidas(Integer idUsuarioCreacion) {
        List<CategoriaProducto> predefinidas = getCategoriasPredefinidas();
        
        for (CategoriaProducto categoria : predefinidas) {
            if (!existsByNombre(categoria.getNombreCategoria())) {
                crear(categoria, idUsuarioCreacion);
            }
        }
    }

    /**
     * Obtiene el nombre de una categoría por su ID.
     * 
     * @param idCategoria ID de la categoría
     * @return Nombre de la categoría, o null si no existe
     */
    public String getNombreCategoria(Integer idCategoria) {
        CategoriaProducto categoria = findById(idCategoria);
        return categoria != null ? categoria.getNombreCategoria() : null;
    }

    /**
     * Obtiene el resumen de una categoría para mostrar.
     * 
     * @param idCategoria ID de la categoría
     * @return Resumen de la categoría
     */
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

    /**
     * Verifica si una categoría puede ser eliminada.
     * 
     * @param idCategoria ID de la categoría
     * @return true si puede ser eliminada, false en caso contrario
     */
    public boolean puedeSerEliminada(Integer idCategoria) {
        return !hasProductos(idCategoria);
    }

    /**
     * Obtiene el estado de una categoría en formato legible.
     * 
     * @param idCategoria ID de la categoría
     * @return Estado legible
     */
    public String getEstadoLegible(Integer idCategoria) {
        CategoriaProducto categoria = findById(idCategoria);
        if (categoria == null) {
            return "No existe";
        }
        return categoria.isActivo() ? "Activo" : "Inactivo";
    }

    /**
     * Obtiene la clase CSS para el badge del estado de una categoría.
     * 
     * @param idCategoria ID de la categoría
     * @return Clase CSS
     */
    public String getEstadoBadgeClass(Integer idCategoria) {
        CategoriaProducto categoria = findById(idCategoria);
        if (categoria == null) {
            return "badge-secondary";
        }
        return categoria.isActivo() ? "badge-success" : "badge-danger";
    }

    /**
     * Obtiene el color del estado de una categoría.
     * 
     * @param idCategoria ID de la categoría
     * @return Color en hexadecimal
     */
    public String getEstadoColor(Integer idCategoria) {
        CategoriaProducto categoria = findById(idCategoria);
        if (categoria == null) {
            return "#6B7280";
        }
        return categoria.isActivo() ? "#10B981" : "#EF4444";
    }

    /**
     * Obtiene el ícono del estado de una categoría.
     * 
     * @param idCategoria ID de la categoría
     * @return Ícono CSS
     */
    public String getEstadoIcono(Integer idCategoria) {
        CategoriaProducto categoria = findById(idCategoria);
        if (categoria == null) {
            return "ti ti-circle";
        }
        return categoria.isActivo() ? "ti ti-check-circle" : "ti ti-x-circle";
    }

    /**
     * Obtiene el color para una categoría basado en su nombre.
     * 
     * @param nombreCategoria Nombre de la categoría
     * @return Color en hexadecimal
     */
    public String getColorByNombre(String nombreCategoria) {
        if (nombreCategoria == null) {
            return "#6B7280";
        }
        
        // Asignar colores según el nombre de la categoría
        switch (nombreCategoria) {
            case "Stands y estructuras feriales":
                return "#3B82F6";
            case "Mobiliario para eventos":
                return "#10B981";
            case "Equipamiento interactivo y audiovisual":
                return "#8B5CF6";
            case "Publicidad gráfica":
                return "#F59E0B";
            default:
                return "#6B7280";
        }
    }

    /**
     * Obtiene el ícono para una categoría basado en su nombre.
     * 
     * @param nombreCategoria Nombre de la categoría
     * @return Ícono CSS
     */
    public String getIconoByNombre(String nombreCategoria) {
        if (nombreCategoria == null) {
            return "ti ti-category";
        }
        
        // Asignar íconos según el nombre de la categoría
        switch (nombreCategoria) {
            case "Stands y estructuras feriales":
                return "ti ti-building";
            case "Mobiliario para eventos":
                return "ti ti-chair";
            case "Equipamiento interactivo y audiovisual":
                return "ti ti-device-tv";
            case "Publicidad gráfica":
                return "ti ti-printer";
            default:
                return "ti ti-category";
        }
    }

    /**
     * Obtiene el conteo de productos por categoría para el dashboard.
     * 
     * @return Lista de objetos con categoría y conteo
     */
    public List<Object[]> getEstadisticasParaDashboard() {
        return categoriaProductoRepository.findCategoriasWithProductCount();
    }

    /**
     * Obtiene el porcentaje de categorías activas.
     * 
     * @return Porcentaje de categorías activas
     */
    public double getPorcentajeActivas() {
        long total = count();
        if (total == 0) {
            return 0;
        }
        return (countActive() * 100.0) / total;
    }

    /**
     * Obtiene el porcentaje de categorías inactivas.
     * 
     * @return Porcentaje de categorías inactivas
     */
    public double getPorcentajeInactivas() {
        long total = count();
        if (total == 0) {
            return 0;
        }
        return (countInactive() * 100.0) / total;
    }

    /**
     * Actualiza el conteo de productos de todas las categorías.
     * Este método es útil para refrescar los datos de la vista.
     */
    @Transactional
    public void actualizarConteosProductos() {
        // Este método puede ser implementado si se necesita actualizar
        // los conteos en la vista de manera manual
        // Por ahora, los conteos se calculan dinámicamente
    }

    /**
     * Obtiene una categoría con todos sus productos.
     * 
     * @param idCategoria ID de la categoría
     * @return Categoría con productos
     */
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

    /**
     * Obtiene todas las categorías con sus productos.
     * 
     * @return Lista de categorías con productos
     */
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