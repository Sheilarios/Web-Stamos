package com.stamos.service;

import com.stamos.model.CategoriaProducto;
import com.stamos.model.Producto;
import com.stamos.repository.CategoriaProductoRepository;
import com.stamos.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaProductoRepository categoriaProductoRepository;

    @Value("${app.upload.productos.dir:./uploads/productos/}")
    private String uploadDir;

    @Transactional
    public Producto crear(Producto producto, MultipartFile imagen, Integer idUsuarioCreacion) throws IOException {
        if (productoRepository.existsByNombreItem(producto.getNombreItem())) {
            throw new RuntimeException("Ya existe un producto con el nombre: " + producto.getNombreItem());
        }

        CategoriaProducto categoria = categoriaProductoRepository.findById(producto.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        if (producto.getPrecioBase() == null || producto.getPrecioBase().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("El precio base debe ser mayor o igual a cero");
        }

        producto.setActivo(true);
        producto.setFechaCreacion(LocalDateTime.now());
        producto.setIdUsuarioCreacion(idUsuarioCreacion);
        producto.setFechaActualizacion(LocalDateTime.now());
        producto.setIdUsuarioActualizacion(idUsuarioCreacion);

        if (imagen != null && !imagen.isEmpty()) {
            String nombreImagen = guardarImagen(imagen);
            producto.setImagenUrl("/productos/" + nombreImagen);
        }

        Producto productoGuardado = productoRepository.save(producto);

        productoGuardado.setNombreCategoria(categoria.getNombreCategoria());
        productoGuardado.setDescripcionCategoria(categoria.getDescripcion());

        return productoGuardado;
    }

    @Transactional
    public Producto actualizar(Integer idProducto, Producto producto, MultipartFile imagen, Integer idUsuarioActualizacion) throws IOException {
        Producto existente = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (!existente.getNombreItem().equals(producto.getNombreItem()) &&
            productoRepository.existsByNombreItemAndIdNot(producto.getNombreItem(), idProducto)) {
            throw new RuntimeException("Ya existe otro producto con el nombre: " + producto.getNombreItem());
        }

        CategoriaProducto categoria = categoriaProductoRepository.findById(producto.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        if (producto.getPrecioBase() == null || producto.getPrecioBase().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("El precio base debe ser mayor o igual a cero");
        }

        existente.setIdCategoria(producto.getIdCategoria());
        existente.setNombreItem(producto.getNombreItem());
        existente.setDescripcion(producto.getDescripcion());
        existente.setTipo(producto.getTipo());
        existente.setPrecioBase(producto.getPrecioBase());
        existente.setActivo(producto.getActivo());
        existente.setFechaActualizacion(LocalDateTime.now());
        existente.setIdUsuarioActualizacion(idUsuarioActualizacion);

        if (imagen != null && !imagen.isEmpty()) {
            if (existente.getImagenUrl() != null && !existente.getImagenUrl().isEmpty()) {
                eliminarImagen(existente.getImagenUrl());
            }
            String nombreImagen = guardarImagen(imagen);
            existente.setImagenUrl("/productos/" + nombreImagen);
        }

        Producto productoActualizado = productoRepository.save(existente);
        productoActualizado.setNombreCategoria(categoria.getNombreCategoria());
        productoActualizado.setDescripcionCategoria(categoria.getDescripcion());

        return productoActualizado;
    }

    @Transactional
    public void eliminar(Integer idProducto) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (productoRepository.hasReferencias(idProducto)) {
            throw new RuntimeException("No se puede eliminar el producto porque tiene referencias en cotizaciones o inventario. Desactívalo en su lugar.");
        }

        if (producto.getImagenUrl() != null && !producto.getImagenUrl().isEmpty()) {
            eliminarImagen(producto.getImagenUrl());
        }

        productoRepository.delete(producto);
    }

    @Transactional
    public Producto activar(Integer idProducto, Integer idUsuarioActualizacion) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setActivo(true);
        producto.setFechaActualizacion(LocalDateTime.now());
        producto.setIdUsuarioActualizacion(idUsuarioActualizacion);

        return productoRepository.save(producto);
    }

    @Transactional
    public Producto desactivar(Integer idProducto, Integer idUsuarioActualizacion) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setActivo(false);
        producto.setFechaActualizacion(LocalDateTime.now());
        producto.setIdUsuarioActualizacion(idUsuarioActualizacion);

        return productoRepository.save(producto);
    }

    public Producto findById(Integer idProducto) {
        return productoRepository.findById(idProducto).orElse(null);
    }

    public Producto findByNombre(String nombreItem) {
        return productoRepository.findByNombreItem(nombreItem).orElse(null);
    }

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public Page<Producto> findAll(Pageable pageable) {
        return productoRepository.findAll(pageable);
    }

    public List<Producto> findActive() {
        return productoRepository.findActive();
    }

    public Page<Producto> findActive(Pageable pageable) {
        return productoRepository.findActive(pageable);
    }

    public List<Producto> findTopActive(int limit) {
        return productoRepository.findTopNActive(limit);
    }

    public List<Producto> findByCategoria(Integer idCategoria) {
        return productoRepository.findByIdCategoriaOrderByNombre(idCategoria);
    }

    public Page<Producto> findByCategoria(Integer idCategoria, Pageable pageable) {
        return productoRepository.findByIdCategoria(idCategoria, pageable);
    }

    public List<Producto> findByTipo(String tipo) {
        return productoRepository.findByTipoOrderByNombre(tipo);
    }

    public Page<Producto> findByTipo(String tipo, Pageable pageable) {
        return productoRepository.findByTipo(tipo, pageable);
    }

    public List<Producto> findAlquiler() {
        return productoRepository.findAlquiler();
    }

    public List<Producto> findVenta() {
        return productoRepository.findVenta();
    }

    public List<Producto> findServicios() {
        return productoRepository.findServicios();
    }

    public List<Producto> findByNombreContaining(String nombre) {
        return productoRepository.findByNombreItemContaining(nombre);
    }

    public Page<Producto> findByNombreContaining(String nombre, Pageable pageable) {
        return productoRepository.findByNombreItemContaining(nombre, pageable);
    }

    public List<Producto> search(String searchText) {
        return productoRepository.search(searchText);
    }

    public Page<Producto> search(String searchText, Pageable pageable) {
        return productoRepository.search(searchText, pageable);
    }

    public List<Producto> findByActivo(Boolean activo) {
        return productoRepository.findByActivo(activo);
    }

    public Page<Producto> findByActivo(Boolean activo, Pageable pageable) {
        return productoRepository.findByActivo(activo, pageable);
    }

    public List<Producto> findWithImagen() {
        return productoRepository.findWithImagen();
    }

    public List<Producto> findWithoutImagen() {
        return productoRepository.findWithoutImagen();
    }

    public long count() {
        return productoRepository.count();
    }

    public long countByTipo(String tipo) {
        return productoRepository.countByTipo(tipo);
    }

    public long countByCategoria(Integer idCategoria) {
        return productoRepository.countByIdCategoria(idCategoria);
    }

    public long countActive() {
        return productoRepository.countActive();
    }

    public long countInactive() {
        return productoRepository.countInactive();
    }

    public long countActiveByTipo(String tipo) {
        return productoRepository.countActiveByTipo(tipo);
    }

    public long countActiveByCategoria(Integer idCategoria) {
        return productoRepository.countActiveByIdCategoria(idCategoria);
    }

    public long countByActivo(boolean activo) {
        return productoRepository.countByActivo(activo);
    }

    public BigDecimal avgPrecioBase() {
        return productoRepository.avgPrecioBase();
    }

    public BigDecimal avgPrecioBaseByTipo(String tipo) {
        return productoRepository.avgPrecioBaseByTipo(tipo);
    }

    public BigDecimal minPrecioBase() {
        return productoRepository.minPrecioBase();
    }

    public BigDecimal maxPrecioBase() {
        return productoRepository.maxPrecioBase();
    }

    public boolean existsByNombre(String nombreItem) {
        return productoRepository.existsByNombreItem(nombreItem);
    }

    public boolean hasReferencias(Integer idProducto) {
        return productoRepository.hasReferencias(idProducto);
    }

    public boolean isActivo(Integer idProducto) {
        Producto producto = findById(idProducto);
        return producto != null && producto.isActivo();
    }

    public String getTipoLegible(String tipo) {
        if (tipo == null) return "No especificado";
        return switch (tipo) {
            case "Alquiler" -> "Alquiler";
            case "Venta" -> "Venta";
            case "Servicio" -> "Servicio";
            default -> tipo;
        };
    }

    public String getTipoBadgeClass(String tipo) {
        if (tipo == null) return "badge-secondary";
        return switch (tipo) {
            case "Alquiler" -> "badge-alquiler";
            case "Venta" -> "badge-venta";
            case "Servicio" -> "badge-servicio";
            default -> "badge-secondary";
        };
    }

    public String getTipoIcono(String tipo) {
        if (tipo == null) return "ti ti-box";
        return switch (tipo) {
            case "Alquiler" -> "ti ti-calendar";
            case "Venta" -> "ti ti-shopping-cart";
            case "Servicio" -> "ti ti-tools";
            default -> "ti ti-box";
        };
    }

    public String getTipoColor(String tipo) {
        if (tipo == null) return "#6B7280";
        return switch (tipo) {
            case "Alquiler" -> "#3B82F6";
            case "Venta" -> "#10B981";
            case "Servicio" -> "#8B5CF6";
            default -> "#6B7280";
        };
    }

    public String formatearMoneda(BigDecimal monto) {
        if (monto == null) return "S/ 0.00";
        return "S/ " + monto.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public String getEstadoLegible(Boolean activo) {
        if (activo == null) return "Sin estado";
        return activo ? "Activo" : "Inactivo";
    }

    public String getEstadoBadgeClass(Boolean activo) {
        if (activo == null) return "badge-secondary";
        return activo ? "badge-success" : "badge-danger";
    }

    public String getEstadoIcono(Boolean activo) {
        if (activo == null) return "ti ti-circle";
        return activo ? "ti ti-check-circle" : "ti ti-x-circle";
    }

    public String getEstadoColor(Boolean activo) {
        if (activo == null) return "#6B7280";
        return activo ? "#10B981" : "#EF4444";
    }

    public String getNombreCategoria(Integer idProducto) {
        Producto producto = findById(idProducto);
        if (producto == null) return null;
        CategoriaProducto categoria = categoriaProductoRepository.findById(producto.getIdCategoria()).orElse(null);
        return categoria != null ? categoria.getNombreCategoria() : null;
    }

    @Transactional
    public void actualizarPrecioBase(Integer idProducto, BigDecimal nuevoPrecio) {
        if (nuevoPrecio == null || nuevoPrecio.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("El precio debe ser mayor o igual a cero");
        }
        productoRepository.updatePrecioBase(idProducto, nuevoPrecio);
    }

    @Transactional
    public void actualizarImagen(Integer idProducto, MultipartFile imagen) throws IOException {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (imagen == null || imagen.isEmpty()) {
            throw new RuntimeException("Debes seleccionar una imagen");
        }

        if (producto.getImagenUrl() != null && !producto.getImagenUrl().isEmpty()) {
            eliminarImagen(producto.getImagenUrl());
        }

        String nombreImagen = guardarImagen(imagen);
        producto.setImagenUrl("/productos/" + nombreImagen);
        producto.setFechaActualizacion(LocalDateTime.now());
        productoRepository.save(producto);
    }

    @Transactional
    public void eliminarImagen(Integer idProducto) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (producto.getImagenUrl() != null && !producto.getImagenUrl().isEmpty()) {
            eliminarImagen(producto.getImagenUrl());
            producto.setImagenUrl(null);
            producto.setFechaActualizacion(LocalDateTime.now());
            productoRepository.save(producto);
        }
    }

    @Transactional
    public void update(Producto producto) {
        if (producto == null || producto.getIdProducto() == null) {
            throw new RuntimeException("Producto inválido para actualizar");
        }
        
        Producto existente = productoRepository.findById(producto.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + producto.getIdProducto()));

        if (!existente.getNombreItem().equals(producto.getNombreItem()) &&
            productoRepository.existsByNombreItemAndIdNot(producto.getNombreItem(), producto.getIdProducto())) {
            throw new RuntimeException("Ya existe otro producto con el nombre: " + producto.getNombreItem());
        }

        existente.setIdCategoria(producto.getIdCategoria());
        existente.setNombreItem(producto.getNombreItem());
        existente.setDescripcion(producto.getDescripcion());
        existente.setTipo(producto.getTipo());
        existente.setPrecioBase(producto.getPrecioBase());
        existente.setActivo(producto.getActivo());
        existente.setImagenUrl(producto.getImagenUrl());
        existente.setFechaActualizacion(LocalDateTime.now());
        
        productoRepository.save(existente);
    }

    @Transactional
    public void save(Producto producto) {
        if (producto == null) {
            throw new RuntimeException("Producto inválido para guardar");
        }
        
        if (producto.getNombreItem() == null || producto.getNombreItem().trim().isEmpty()) {
            throw new RuntimeException("El nombre del producto es obligatorio");
        }
        
        if (productoRepository.existsByNombreItem(producto.getNombreItem())) {
            throw new RuntimeException("Ya existe un producto con el nombre: " + producto.getNombreItem());
        }
        
        producto.setFechaCreacion(LocalDateTime.now());
        producto.setFechaActualizacion(LocalDateTime.now());
        productoRepository.save(producto);
    }

    @Transactional
    public void delete(Integer idProducto) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        if (productoRepository.hasReferencias(idProducto)) {
            throw new RuntimeException("No se puede eliminar el producto porque tiene referencias");
        }
        
        productoRepository.delete(producto);
    }

    public boolean tieneReferencias(Integer idProducto) {
        return productoRepository.hasReferencias(idProducto);
    }

    public List<Producto> buscar(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return productoRepository.search(query);
    }

    public Page<Producto> buscar(String query, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            return productoRepository.findAll(pageable);
        }
        return productoRepository.search(query, pageable);
    }

    private String guardarImagen(MultipartFile archivo) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String nombreOriginal = archivo.getOriginalFilename();
        String extension = getFileExtension(nombreOriginal);
        String nombreUnico = UUID.randomUUID().toString() + "." + extension;
        Path filePath = uploadPath.resolve(nombreUnico);
        Files.copy(archivo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return nombreUnico;
    }

    private void eliminarImagen(String urlImagen) {
        try {
            if (urlImagen != null && !urlImagen.isEmpty()) {
                String fileName = urlImagen.replace("/productos/", "");
                Path filePath = Paths.get(uploadDir).resolve(fileName);
                Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            System.err.println("Error al eliminar imagen: " + e.getMessage());
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    public String getImagenUrlOrDefault(Producto producto) {
        if (producto != null && producto.getImagenUrl() != null && !producto.getImagenUrl().isEmpty()) {
            return producto.getImagenUrl();
        }
        return "/images/placeholder-product.png";
    }

    public List<Object[]> getEstadisticasDashboard() {
        return productoRepository.getEstadisticasDashboard();
    }

    public List<Object[]> getEstadisticasPorCategoria() {
        return productoRepository.getEstadisticasPorCategoria();
    }

    public List<Object[]> findTopProductosVendidos() {
        return productoRepository.findTopProductosVendidos();
    }

    public List<Object[]> findTopProductosFacturacion() {
        return productoRepository.findTopProductosFacturacion();
    }

    public List<Object[]> findProductosBajoStock(int limiteStock) {
        return productoRepository.findProductosBajoStock(limiteStock);
    }
}