package com.stamos.controller.admin;

import com.stamos.annotation.Auditable;
import com.stamos.controller.BaseController;
import com.stamos.model.CategoriaProducto;
import com.stamos.model.Producto;
import com.stamos.model.Usuario;
import com.stamos.service.CategoriaProductoService;
import com.stamos.service.ClienteService;
import com.stamos.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/productos")
@PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
public class ProductoController extends BaseController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaProductoService categoriaProductoService;

    @Autowired
    private ClienteService clienteService;

    @Value("${app.upload.productos.dir:./uploads/productos/}")
    private String uploadDir;

    @GetMapping
    public String listarProductos(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(required = false) String tipo,
                                  @RequestParam(required = false) Integer categoriaId,
                                  @RequestParam(required = false) String search,
                                  @RequestParam(required = false) Boolean activo,
                                  Model model) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("nombreItem").ascending());
            
            Page<Producto> productosPage;
            if (tipo != null && !tipo.isEmpty()) {
                productosPage = productoService.findByTipo(tipo, pageable);
            } else if (categoriaId != null) {
                productosPage = productoService.findByCategoria(categoriaId, pageable);
            } else if (search != null && !search.isEmpty()) {
                productosPage = productoService.buscar(search, pageable);
            } else if (activo != null) {
                productosPage = productoService.findByActivo(activo, pageable);
            } else {
                productosPage = productoService.findAll(pageable);
            }
            
            model.addAttribute("productos", productosPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", productosPage.getTotalPages());
            model.addAttribute("totalItems", productosPage.getTotalElements());
            model.addAttribute("tipo", tipo);
            model.addAttribute("categoriaId", categoriaId);
            model.addAttribute("search", search);
            model.addAttribute("activo", activo);
            model.addAttribute("categorias", categoriaProductoService.findAll());
            model.addAttribute("totalProductos", productoService.count());
            model.addAttribute("totalAlquiler", productoService.countByTipo("Alquiler"));
            model.addAttribute("totalVenta", productoService.countByTipo("Venta"));
            model.addAttribute("totalServicio", productoService.countByTipo("Servicio"));
            model.addAttribute("totalActivos", productoService.countByActivo(true));
            model.addAttribute("totalInactivos", productoService.countByActivo(false));
            addCommonAttributes(model);
            
            return "admin/productos/lista";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar los productos: " + e.getMessage());
            return "admin/productos/lista";
        }
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        try {
            model.addAttribute("producto", new Producto());
            model.addAttribute("categorias", categoriaProductoService.findAll());
            model.addAttribute("tipos", getTiposProducto());
            model.addAttribute("titulo", "Nuevo Producto/Servicio");
            model.addAttribute("accion", "crear");
            addCommonAttributes(model);
            return "admin/productos/formulario";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "redirect:/admin/productos";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        try {
            Producto producto = productoService.findById(id);
            if (producto == null) {
                model.addAttribute("error", "Producto no encontrado");
                return "redirect:/admin/productos";
            }
            
            model.addAttribute("producto", producto);
            model.addAttribute("categorias", categoriaProductoService.findAll());
            model.addAttribute("tipos", getTiposProducto());
            model.addAttribute("titulo", "Editar Producto/Servicio");
            model.addAttribute("accion", "editar");
            addCommonAttributes(model);
            return "admin/productos/formulario";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "redirect:/admin/productos";
        }
    }

    @PostMapping("/crear")
    @Auditable
    public String crearProducto(@Valid @ModelAttribute("producto") Producto producto,
                                BindingResult result,
                                @RequestParam("imagen") MultipartFile imagen,
                                Authentication auth,
                                RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("error", "Por favor, corrige los errores.");
                return "redirect:/admin/productos/nuevo";
            }

            if (producto.getIdCategoria() == null || categoriaProductoService.findById(producto.getIdCategoria()) == null) {
                redirectAttributes.addFlashAttribute("error", "Categoría no encontrada.");
                return "redirect:/admin/productos/nuevo";
            }

            if (producto.getPrecioBase() == null || producto.getPrecioBase().compareTo(BigDecimal.ZERO) < 0) {
                redirectAttributes.addFlashAttribute("error", "El precio base debe ser mayor o igual a cero.");
                return "redirect:/admin/productos/nuevo";
            }

            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
                return "redirect:/admin/productos/nuevo";
            }

            if (imagen != null && !imagen.isEmpty()) {
                producto.setImagenUrl("/productos/" + guardarImagen(imagen));
            }

            producto.setActivo(true);
            productoService.save(producto);

            redirectAttributes.addFlashAttribute("mensaje", "Producto creado exitosamente.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/admin/productos";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la imagen: " + e.getMessage());
            return "redirect:/admin/productos/nuevo";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el producto: " + e.getMessage());
            return "redirect:/admin/productos/nuevo";
        }
    }

    @PostMapping("/actualizar/{id}")
    @Auditable
    public String actualizarProducto(@PathVariable Integer id,
                                     @Valid @ModelAttribute("producto") Producto producto,
                                     BindingResult result,
                                     @RequestParam("imagen") MultipartFile imagen,
                                     Authentication auth,
                                     RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("error", "Por favor, corrige los errores.");
                return "redirect:/admin/productos/editar/" + id;
            }

            Producto existente = productoService.findById(id);
            if (existente == null) {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado.");
                return "redirect:/admin/productos";
            }

            if (producto.getIdCategoria() == null || categoriaProductoService.findById(producto.getIdCategoria()) == null) {
                redirectAttributes.addFlashAttribute("error", "Categoría no encontrada.");
                return "redirect:/admin/productos/editar/" + id;
            }

            if (producto.getPrecioBase() == null || producto.getPrecioBase().compareTo(BigDecimal.ZERO) < 0) {
                redirectAttributes.addFlashAttribute("error", "El precio base debe ser mayor o igual a cero.");
                return "redirect:/admin/productos/editar/" + id;
            }

            if (imagen != null && !imagen.isEmpty()) {
                if (existente.getImagenUrl() != null && !existente.getImagenUrl().isEmpty()) {
                    eliminarImagen(existente.getImagenUrl());
                }
                producto.setImagenUrl("/productos/" + guardarImagen(imagen));
            } else {
                producto.setImagenUrl(existente.getImagenUrl());
            }

            producto.setIdProducto(id);
            productoService.update(producto);

            redirectAttributes.addFlashAttribute("mensaje", "Producto actualizado exitosamente.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/admin/productos";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la imagen: " + e.getMessage());
            return "redirect:/admin/productos/editar/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el producto: " + e.getMessage());
            return "redirect:/admin/productos/editar/" + id;
        }
    }

    @GetMapping("/detalle/{id}")
    public String verDetalle(@PathVariable Integer id, Model model) {
        try {
            Producto producto = productoService.findById(id);
            if (producto == null) {
                model.addAttribute("error", "Producto no encontrado");
                return "redirect:/admin/productos";
            }
            
            model.addAttribute("producto", producto);
            model.addAttribute("categoria", categoriaProductoService.findById(producto.getIdCategoria()));
            addCommonAttributes(model);
            return "admin/productos/detalle";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el detalle: " + e.getMessage());
            return "redirect:/admin/productos";
        }
    }

    @PostMapping("/activar/{id}")
    @Auditable
    public String activarProducto(@PathVariable Integer id,
                                  Authentication auth,
                                  RedirectAttributes redirectAttributes) {
        try {
            Producto producto = productoService.findById(id);
            if (producto == null) {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado.");
                return "redirect:/admin/productos";
            }

            producto.setActivo(true);
            productoService.update(producto);

            redirectAttributes.addFlashAttribute("mensaje", "Producto activado exitosamente.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/admin/productos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al activar el producto: " + e.getMessage());
            return "redirect:/admin/productos";
        }
    }

    @PostMapping("/desactivar/{id}")
    @Auditable
    public String desactivarProducto(@PathVariable Integer id,
                                     Authentication auth,
                                     RedirectAttributes redirectAttributes) {
        try {
            Producto producto = productoService.findById(id);
            if (producto == null) {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado.");
                return "redirect:/admin/productos";
            }

            producto.setActivo(false);
            productoService.update(producto);

            redirectAttributes.addFlashAttribute("mensaje", "Producto desactivado exitosamente.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/admin/productos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al desactivar el producto: " + e.getMessage());
            return "redirect:/admin/productos";
        }
    }

    @PostMapping("/eliminar/{id}")
    @Auditable
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminarProducto(@PathVariable Integer id,
                                   Authentication auth,
                                   RedirectAttributes redirectAttributes) {
        try {
            Producto producto = productoService.findById(id);
            if (producto == null) {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado.");
                return "redirect:/admin/productos";
            }

            if (productoService.tieneReferencias(id)) {
                redirectAttributes.addFlashAttribute("error", 
                    "No se puede eliminar el producto porque tiene referencias. Desactívalo en su lugar.");
                return "redirect:/admin/productos";
            }

            if (producto.getImagenUrl() != null && !producto.getImagenUrl().isEmpty()) {
                eliminarImagen(producto.getImagenUrl());
            }

            productoService.delete(id);

            redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado exitosamente.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/admin/productos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el producto: " + e.getMessage());
            return "redirect:/admin/productos";
        }
    }

    @GetMapping("/imagen/{nombre}")
    public ResponseEntity<Resource> verImagen(@PathVariable String nombre) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(nombre);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            
            String extension = getFileExtension(nombre);
            MediaType mediaType = getMediaType(extension);
            
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nombre + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/categoria/{categoriaId}")
    @ResponseBody
    public List<Producto> getProductosByCategoria(@PathVariable Integer categoriaId) {
        try {
            return productoService.findByCategoria(categoriaId);
        } catch (Exception e) {
            return List.of();
        }
    }

    @GetMapping("/tipo/{tipo}")
    @ResponseBody
    public List<Producto> getProductosByTipo(@PathVariable String tipo) {
        try {
            return productoService.findByTipo(tipo);
        } catch (Exception e) {
            return List.of();
        }
    }

    @GetMapping("/buscar")
    @ResponseBody
    public List<Producto> buscarProductos(@RequestParam("q") String query) {
        try {
            if (query == null || query.length() < 2) {
                return List.of();
            }
            return productoService.buscar(query);
        } catch (Exception e) {
            return List.of();
        }
    }

    @GetMapping("/activos")
    @ResponseBody
    public List<Producto> getProductosActivos() {
        try {
            return productoService.findActive();
        } catch (Exception e) {
            return List.of();
        }
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

    private MediaType getMediaType(String extension) {
        return switch (extension) {
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "png" -> MediaType.IMAGE_PNG;
            case "gif" -> MediaType.IMAGE_GIF;
            case "webp" -> MediaType.valueOf("image/webp");
            case "svg" -> MediaType.valueOf("image/svg+xml");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

    private List<String> getTiposProducto() {
        return List.of("Alquiler", "Venta", "Servicio");
    }

    public String getTipoBadgeClass(String tipo) {
        if (tipo == null) return "";
        return switch (tipo) {
            case "Alquiler" -> "badge-alquiler";
            case "Venta" -> "badge-venta";
            case "Servicio" -> "badge-servicio";
            default -> "";
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

    @Override
    public String formatearMoneda(BigDecimal monto) {
        if (monto == null) return "S/ 0.00";
        return "S/ " + monto.setScale(2, java.math.RoundingMode.HALF_UP).toString();
    }
}