package com.stamos.controller.cliente;

import com.stamos.controller.BaseController;
import com.stamos.model.Cliente;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cliente")
public class ClienteDocumentoController extends BaseController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private DocumentoService documentoService;

    @Value("${app.upload.documentos.dir:./uploads/documentos/}")
    private String uploadDir;

    @GetMapping("/documentos")
    public String listarDocumentos(Model model, Authentication auth) {
        try {
            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado");
            }

            Cliente cliente = clienteService.findByUsuario(usuario);
            if (cliente == null) {
                throw new RuntimeException("Cliente no encontrado");
            }

            List<Proyecto> proyectos = proyectoService.findByCliente(cliente.getIdCliente());
            List<DocumentoProyecto> documentos = documentoService.findByCliente(cliente.getIdCliente());
            
            for (DocumentoProyecto doc : documentos) {
                Proyecto proyecto = proyectoService.findById(doc.getIdProyecto());
                if (proyecto != null) {
                    doc.setNombreProyecto(proyecto.getNombreProyecto());
                }
            }

            long renders = documentos.stream().filter(d -> "Render".equals(d.getTipoDocumento())).count();
            long planos = documentos.stream().filter(d -> "Plano".equals(d.getTipoDocumento())).count();
            long bocetos = documentos.stream().filter(d -> "Boceto".equals(d.getTipoDocumento())).count();
            long contratos = documentos.stream().filter(d -> "Contrato".equals(d.getTipoDocumento())).count();
            long otros = documentos.stream().filter(d -> !"Render".equals(d.getTipoDocumento()) && 
                                !"Plano".equals(d.getTipoDocumento()) &&
                                !"Boceto".equals(d.getTipoDocumento()) &&
                                !"Contrato".equals(d.getTipoDocumento())).count();

            model.addAttribute("cliente", cliente);
            model.addAttribute("documentos", documentos);
            model.addAttribute("proyectos", proyectos);
            model.addAttribute("totalDocumentos", documentos.size());
            model.addAttribute("renders", renders);
            model.addAttribute("planos", planos);
            model.addAttribute("bocetos", bocetos);
            model.addAttribute("contratos", contratos);
            model.addAttribute("otros", otros);
            addCommonAttributes(model);

            return "cliente/documentos";

        } catch (RuntimeException e) {
            model.addAttribute("error", "Error al cargar los documentos: " + e.getMessage());
            addCommonAttributes(model);
            return "cliente/dashboard";
        }
    }

    @GetMapping("/documentos/proyecto/{id}")
    @ResponseBody
    public List<DocumentoProyecto> getDocumentosByProyecto(@PathVariable("id") Integer idProyecto, 
                                                           Authentication auth) {
        try {
            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado");
            }

            Cliente cliente = clienteService.findByUsuario(usuario);
            if (cliente == null) {
                throw new RuntimeException("Cliente no encontrado");
            }

            Proyecto proyecto = proyectoService.findById(idProyecto);
            if (proyecto == null || !proyecto.getIdCliente().equals(cliente.getIdCliente())) {
                throw new AccessDeniedException("No tienes acceso a este proyecto");
            }

            List<DocumentoProyecto> documentos = documentoService.findByProyecto(idProyecto);
            for (DocumentoProyecto doc : documentos) {
                doc.setNombreProyecto(proyecto.getNombreProyecto());
            }

            return documentos;

        } catch (RuntimeException e) {
            return List.of();
        }
    }

    @GetMapping("/documentos/filtrar")
    @ResponseBody
    public List<DocumentoProyecto> filtrarDocumentos(@RequestParam(value = "proyecto", required = false) Integer idProyecto,
                                                     @RequestParam(value = "tipo", required = false) String tipo,
                                                     Authentication auth) {
        try {
            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado");
            }

            Cliente cliente = clienteService.findByUsuario(usuario);
            if (cliente == null) {
                throw new RuntimeException("Cliente no encontrado");
            }

            List<DocumentoProyecto> documentos = documentoService.findByCliente(cliente.getIdCliente());
            for (DocumentoProyecto doc : documentos) {
                Proyecto proyecto = proyectoService.findById(doc.getIdProyecto());
                if (proyecto != null) {
                    doc.setNombreProyecto(proyecto.getNombreProyecto());
                }
            }

            if (idProyecto != null) {
                Proyecto proyecto = proyectoService.findById(idProyecto);
                if (proyecto == null || !proyecto.getIdCliente().equals(cliente.getIdCliente())) {
                    throw new AccessDeniedException("No tienes acceso a este proyecto");
                }
                documentos = documentos.stream()
                        .filter(d -> d.getIdProyecto().equals(idProyecto))
                        .collect(Collectors.toList());
            }

            if (tipo != null && !tipo.isEmpty() && !"todos".equals(tipo)) {
                documentos = documentos.stream()
                        .filter(d -> tipo.equals(d.getTipoDocumento()))
                        .collect(Collectors.toList());
            }

            return documentos;

        } catch (RuntimeException e) {
            return List.of();
        }
    }

    @GetMapping("/documentos/ver/{id}")
    public ResponseEntity<Resource> verDocumento(@PathVariable("id") Integer idDocumento, 
                                                 Authentication auth) {
        try {
            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado");
            }

            Cliente cliente = clienteService.findByUsuario(usuario);
            if (cliente == null) {
                throw new RuntimeException("Cliente no encontrado");
            }

            DocumentoProyecto documento = documentoService.findById(idDocumento);
            if (documento == null) {
                throw new RuntimeException("Documento no encontrado");
            }

            Proyecto proyecto = proyectoService.findById(documento.getIdProyecto());
            if (proyecto == null || !proyecto.getIdCliente().equals(cliente.getIdCliente())) {
                throw new AccessDeniedException("No tienes acceso a este documento");
            }

            String urlArchivo = documento.getUrlArchivo();
            if (urlArchivo == null || urlArchivo.isEmpty()) {
                throw new RuntimeException("URL del archivo no disponible");
            }

            String fileName = urlArchivo.replace("/documentos/", "");
            Path filePath = Paths.get(uploadDir).resolve(fileName);

            String extension = getFileExtension(documento.getNombreArchivo());
            MediaType mediaType = getMediaType(extension);

            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("El archivo no existe o no se puede leer");
            }

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "inline; filename=\"" + documento.getNombreArchivo() + "\"")
                    .body(resource);

        } catch (AccessDeniedException e) {
            throw e;
        } catch (MalformedURLException | RuntimeException e) {
            throw new RuntimeException("Error al cargar el documento: " + e.getMessage());
        }
    }

    @GetMapping("/documentos/descargar/{id}")
    public ResponseEntity<Resource> descargarDocumento(@PathVariable("id") Integer idDocumento, 
                                                       Authentication auth) {
        try {
            Usuario usuario = clienteService.findUsuarioByEmail(auth.getName());
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado");
            }

            Cliente cliente = clienteService.findByUsuario(usuario);
            if (cliente == null) {
                throw new RuntimeException("Cliente no encontrado");
            }

            DocumentoProyecto documento = documentoService.findById(idDocumento);
            if (documento == null) {
                throw new RuntimeException("Documento no encontrado");
            }

            Proyecto proyecto = proyectoService.findById(documento.getIdProyecto());
            if (proyecto == null || !proyecto.getIdCliente().equals(cliente.getIdCliente())) {
                throw new AccessDeniedException("No tienes acceso a este documento");
            }

            String urlArchivo = documento.getUrlArchivo();
            if (urlArchivo == null || urlArchivo.isEmpty()) {
                throw new RuntimeException("URL del archivo no disponible");
            }

            String fileName = urlArchivo.replace("/documentos/", "");
            Path filePath = Paths.get(uploadDir).resolve(fileName);

            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("El archivo no existe o no se puede leer");
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + documento.getNombreArchivo() + "\"")
                    .body(resource);

        } catch (AccessDeniedException e) {
            throw e;
        } catch (MalformedURLException | RuntimeException e) {
            throw new RuntimeException("Error al cargar el documento: " + e.getMessage());
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

    private String getFileExtension(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            return "";
        }
        return nombreArchivo.substring(nombreArchivo.lastIndexOf(".") + 1).toLowerCase();
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

    public boolean isImageExtension(String extension) {
        return extension.matches("(?i)(jpg|jpeg|png|gif|webp|svg)");
    }

    public boolean isPdfExtension(String extension) {
        return "pdf".equalsIgnoreCase(extension);
    }

    public String formatearFecha(java.time.LocalDateTime fecha) {
        if (fecha == null) return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fecha.format(formatter);
    }

    public String formatearTamaño(Long bytes) {
        if (bytes == null || bytes == 0) return "-";
        double kb = bytes / 1024.0;
        if (kb < 1024) {
            return String.format("%.0f KB", kb);
        }
        double mb = kb / 1024.0;
        return String.format("%.1f MB", mb);
    }
}