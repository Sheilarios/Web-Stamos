package com.stamos.controller.admin;

import com.stamos.annotation.Auditable;
import com.stamos.controller.BaseController;
import com.stamos.model.DocumentoProyecto;
import com.stamos.model.Proyecto;
import com.stamos.model.Usuario;
import com.stamos.service.ClienteService;
import com.stamos.service.DocumentoService;
import com.stamos.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/documentos")
@PreAuthorize("hasAnyRole('ADMIN', 'DISEÑADOR')")
public class DocumentoController extends BaseController {

    @Autowired
    private DocumentoService documentoService;

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private ClienteService clienteService;

    @Value("${app.upload.documentos.dir:./uploads/documentos/}")
    private String uploadDir;

    @GetMapping
    public String listarDocumentos(@RequestParam(required = false) Integer proyectoId,
                                   @RequestParam(required = false) String tipo,
                                   Model model) {
        try {
            List<DocumentoProyecto> documentos;
            
            if (proyectoId != null) {
                documentos = documentoService.findByProyecto(proyectoId);
            } else if (tipo != null && !tipo.isEmpty()) {
                documentos = documentoService.findByTipo(tipo);
            } else {
                documentos = documentoService.findAll();
            }
            
            for (DocumentoProyecto doc : documentos) {
                Proyecto proyecto = proyectoService.findById(doc.getIdProyecto());
                if (proyecto != null) {
                    doc.setNombreProyecto(proyecto.getNombreProyecto());
                }
            }
            
            List<Proyecto> proyectos = proyectoService.findAll();
            
            long renders = documentos.stream()
                    .filter(d -> "Render".equals(d.getTipoDocumento()))
                    .count();
            long planos = documentos.stream()
                    .filter(d -> "Plano".equals(d.getTipoDocumento()))
                    .count();
            long bocetos = documentos.stream()
                    .filter(d -> "Boceto".equals(d.getTipoDocumento()))
                    .count();
            long contratos = documentos.stream()
                    .filter(d -> "Contrato".equals(d.getTipoDocumento()))
                    .count();
            long otros = documentos.stream()
                    .filter(d -> !"Render".equals(d.getTipoDocumento()) && 
                                !"Plano".equals(d.getTipoDocumento()) &&
                                !"Boceto".equals(d.getTipoDocumento()) &&
                                !"Contrato".equals(d.getTipoDocumento()))
                    .count();
            
            model.addAttribute("documentos", documentos);
            model.addAttribute("proyectos", proyectos);
            model.addAttribute("proyectoId", proyectoId);
            model.addAttribute("tipo", tipo);
            model.addAttribute("renders", renders);
            model.addAttribute("planos", planos);
            model.addAttribute("bocetos", bocetos);
            model.addAttribute("contratos", contratos);
            model.addAttribute("otros", otros);
            addCommonAttributes(model);
            
            return "admin/documentos/lista";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar los documentos: " + e.getMessage());
            return "admin/documentos/lista";
        }
    }

    @GetMapping("/subir")
    public String mostrarFormularioSubir(@RequestParam(required = false) Integer proyectoId,
                                         Model model) {
        try {
            DocumentoProyecto documento = new DocumentoProyecto();
            if (proyectoId != null) {
                documento.setIdProyecto(proyectoId);
                Proyecto proyecto = proyectoService.findById(proyectoId);
                if (proyecto != null) {
                    model.addAttribute("proyectoSeleccionado", proyecto);
                }
            }
            
            model.addAttribute("documento", documento);
            model.addAttribute("proyectos", proyectoService.findAll());
            model.addAttribute("tipos", getTiposDocumento());
            model.addAttribute("titulo", "Subir Documento");
            addCommonAttributes(model);
            
            return "admin/documentos/formulario";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "redirect:/admin/documentos";
        }
    }

    @PostMapping("/subir")
    @Auditable
    public String subirDocumento(@ModelAttribute("documento") DocumentoProyecto documento,
                                 @RequestParam("archivo") MultipartFile archivo,
                                 Authentication auth,
                                 RedirectAttributes redirectAttributes) {
        try {
            if (archivo == null || archivo.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Debes seleccionar un archivo.");
                return "redirect:/admin/documentos/subir";
            }

            Proyecto proyecto = proyectoService.findById(documento.getIdProyecto());
            if (proyecto == null) {
                redirectAttributes.addFlashAttribute("error", "Proyecto no encontrado.");
                return "redirect:/admin/documentos/subir";
            }

            if (archivo.getSize() > 10 * 1024 * 1024) {
                redirectAttributes.addFlashAttribute("error", 
                    "El archivo excede el tamaño máximo permitido (10MB).");
                return "redirect:/admin/documentos/subir";
            }

            String extension = getFileExtension(archivo.getOriginalFilename());
            if (!isValidFileType(extension)) {
                redirectAttributes.addFlashAttribute("error", 
                    "Tipo de archivo no permitido. Formatos aceptados: JPG, PNG, PDF, DOC, DOCX.");
                return "redirect:/admin/documentos/subir";
            }

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String nombreOriginal = archivo.getOriginalFilename();
            String nombreUnico = UUID.randomUUID().toString() + "_" + nombreOriginal;
            
            Path filePath = uploadPath.resolve(nombreUnico);
            Files.copy(archivo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            
            documento.setNombreArchivo(nombreOriginal);
            documento.setUrlArchivo("/documentos/" + nombreUnico);
            documento.setTamanoBytes(archivo.getSize());
            documento.setFechaSubida(LocalDateTime.now());
            if (usuario != null) {
                documento.setIdUsuarioSubio(usuario.getIdUsuario());
            }
            
            documentoService.save(documento);

            redirectAttributes.addFlashAttribute("mensaje", 
                "Documento subido exitosamente: " + nombreOriginal);
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/admin/documentos?proyectoId=" + documento.getIdProyecto();

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al guardar el archivo: " + e.getMessage());
            return "redirect:/admin/documentos/subir";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al subir el documento: " + e.getMessage());
            return "redirect:/admin/documentos/subir";
        }
    }

    @GetMapping("/detalle/{id}")
    public String verDetalle(@PathVariable Integer id, Model model) {
        try {
            DocumentoProyecto documento = documentoService.findById(id);
            if (documento == null) {
                model.addAttribute("error", "Documento no encontrado");
                return "redirect:/admin/documentos";
            }
            
            Proyecto proyecto = proyectoService.findById(documento.getIdProyecto());
            if (proyecto != null) {
                documento.setNombreProyecto(proyecto.getNombreProyecto());
            }
            
            if (documento.getIdUsuarioSubio() != null) {
                Usuario usuario = clienteService.findUsuarioById(documento.getIdUsuarioSubio());
                if (usuario != null) {
                    model.addAttribute("usuarioSubio", usuario);
                }
            }
            
            model.addAttribute("documento", documento);
            addCommonAttributes(model);
            
            return "admin/documentos/detalle";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el detalle: " + e.getMessage());
            return "redirect:/admin/documentos";
        }
    }

    @GetMapping("/ver/{id}")
    public ResponseEntity<Resource> verDocumento(@PathVariable Integer id) {
        try {
            DocumentoProyecto documento = documentoService.findById(id);
            if (documento == null) {
                return ResponseEntity.notFound().build();
            }
            
            String urlArchivo = documento.getUrlArchivo();
            if (urlArchivo == null || urlArchivo.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            String fileName = urlArchivo.replace("/documentos/", "");
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            
            String extension = getFileExtension(documento.getNombreArchivo());
            MediaType mediaType = getMediaType(extension);
            
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "inline; filename=\"" + documento.getNombreArchivo() + "\"")
                    .body(resource);
                    
        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/descargar/{id}")
    public ResponseEntity<Resource> descargarDocumento(@PathVariable Integer id) {
        try {
            DocumentoProyecto documento = documentoService.findById(id);
            if (documento == null) {
                return ResponseEntity.notFound().build();
            }
            
            String urlArchivo = documento.getUrlArchivo();
            if (urlArchivo == null || urlArchivo.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            String fileName = urlArchivo.replace("/documentos/", "");
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + documento.getNombreArchivo() + "\"")
                    .body(resource);
                    
        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/eliminar/{id}")
    @Auditable
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminarDocumento(@PathVariable Integer id,
                                    Authentication auth,
                                    RedirectAttributes redirectAttributes) {
        try {
            DocumentoProyecto documento = documentoService.findById(id);
            if (documento == null) {
                redirectAttributes.addFlashAttribute("error", "Documento no encontrado.");
                return "redirect:/admin/documentos";
            }
            
            String urlArchivo = documento.getUrlArchivo();
            if (urlArchivo != null && !urlArchivo.isEmpty()) {
                String fileName = urlArchivo.replace("/documentos/", "");
                Path filePath = Paths.get(uploadDir).resolve(fileName);
                try {
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    System.err.println("Error al eliminar archivo físico: " + e.getMessage());
                }
            }
            
            documentoService.delete(id);
            
            redirectAttributes.addFlashAttribute("mensaje", "Documento eliminado exitosamente.");
            redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
            
            return "redirect:/admin/documentos";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el documento: " + e.getMessage());
            return "redirect:/admin/documentos";
        }
    }

    @GetMapping("/proyecto/{proyectoId}")
    @ResponseBody
    public List<DocumentoProyecto> getDocumentosByProyecto(@PathVariable Integer proyectoId) {
        try {
            List<DocumentoProyecto> documentos = documentoService.findByProyecto(proyectoId);
            for (DocumentoProyecto doc : documentos) {
                Proyecto proyecto = proyectoService.findById(doc.getIdProyecto());
                if (proyecto != null) {
                    doc.setNombreProyecto(proyecto.getNombreProyecto());
                }
            }
            return documentos;
        } catch (Exception e) {
            return List.of();
        }
    }

    @GetMapping("/tipo/{tipo}")
    @ResponseBody
    public List<DocumentoProyecto> getDocumentosByTipo(@PathVariable String tipo) {
        try {
            List<DocumentoProyecto> documentos = documentoService.findByTipo(tipo);
            for (DocumentoProyecto doc : documentos) {
                Proyecto proyecto = proyectoService.findById(doc.getIdProyecto());
                if (proyecto != null) {
                    doc.setNombreProyecto(proyecto.getNombreProyecto());
                }
            }
            return documentos;
        } catch (Exception e) {
            return List.of();
        }
    }

    public String getIconoByTipo(String tipoDocumento) {
        if (tipoDocumento == null) return "ti ti-file";
        
        return switch (tipoDocumento) {
            case "Render" -> "ti ti-photo";
            case "Plano" -> "ti ti-ruler-2";
            case "Boceto" -> "ti ti-pencil";
            case "Contrato" -> "ti ti-file-contract";
            default -> "ti ti-file";
        };
    }

    public String getBadgeClassByTipo(String tipoDocumento) {
        if (tipoDocumento == null) return "otro";
        
        return switch (tipoDocumento) {
            case "Render" -> "render";
            case "Plano" -> "plano";
            case "Boceto" -> "boceto";
            case "Contrato" -> "contrato";
            default -> "otro";
        };
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isValidFileType(String extension) {
        String[] validExtensions = {"jpg", "jpeg", "png", "gif", "webp", "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx"};
        for (String valid : validExtensions) {
            if (valid.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    private MediaType getMediaType(String extension) {
        return switch (extension) {
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "png" -> MediaType.IMAGE_PNG;
            case "gif" -> MediaType.IMAGE_GIF;
            case "webp" -> MediaType.valueOf("image/webp");
            case "svg" -> MediaType.valueOf("image/svg+xml");
            case "pdf" -> MediaType.APPLICATION_PDF;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

    private List<String> getTiposDocumento() {
        return List.of("Render", "Plano", "Boceto", "Contrato", "Otro");
    }
}