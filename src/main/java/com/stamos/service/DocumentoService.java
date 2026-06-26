package com.stamos.service;

import com.stamos.model.DocumentoProyecto;
import com.stamos.model.Proyecto;
import com.stamos.model.Usuario;
import com.stamos.repository.DocumentoProyectoRepository;
import com.stamos.repository.ProyectoRepository;
import com.stamos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentoService {

    @Autowired
    private DocumentoProyectoRepository documentoRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Value("${app.upload.documentos.dir:./uploads/documentos/}")
    private String uploadDir;

    @Transactional
    public DocumentoProyecto subirDocumento(Integer idProyecto, String tipoDocumento,
                                           MultipartFile archivo, Integer idUsuarioSubio) throws IOException {
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        if (archivo == null || archivo.isEmpty()) {
            throw new RuntimeException("Debes seleccionar un archivo");
        }

        if (archivo.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("El archivo excede el tamaño máximo permitido (10MB)");
        }

        String extension = getFileExtension(archivo.getOriginalFilename());
        if (!isValidFileType(extension)) {
            throw new RuntimeException("Tipo de archivo no permitido. Formatos aceptados: JPG, PNG, PDF, DOC, DOCX");
        }

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String nombreOriginal = archivo.getOriginalFilename();
        String nombreUnico = UUID.randomUUID().toString() + "_" + nombreOriginal;

        Path filePath = uploadPath.resolve(nombreUnico);
        Files.copy(archivo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        DocumentoProyecto documento = new DocumentoProyecto();
        documento.setIdProyecto(idProyecto);
        documento.setTipoDocumento(tipoDocumento);
        documento.setNombreArchivo(nombreOriginal);
        documento.setUrlArchivo("/documentos/" + nombreUnico);
        documento.setTamanoBytes(archivo.getSize());
        documento.setFechaSubida(LocalDateTime.now());
        documento.setIdUsuarioSubio(idUsuarioSubio);

        if (idUsuarioSubio != null) {
            usuarioRepository.findById(idUsuarioSubio).ifPresent(usuario -> {
                documento.setNombreUsuarioSubio(usuario.getNombreCompleto());
                documento.setEmailUsuarioSubio(usuario.getEmail());
            });
        }

        documento.setNombreProyecto(proyecto.getNombreProyecto());

        return documentoRepository.save(documento);
    }

    @Transactional
    public DocumentoProyecto subirDocumento(Integer idProyecto, String tipoDocumento,
                                           MultipartFile archivo, String emailUsuario) throws IOException {
        Integer idUsuario = null;
        if (emailUsuario != null) {
            Usuario usuario = usuarioRepository.findByEmail(emailUsuario).orElse(null);
            if (usuario != null) {
                idUsuario = usuario.getIdUsuario();
            }
        }
        return subirDocumento(idProyecto, tipoDocumento, archivo, idUsuario);
    }

    @Transactional
    public DocumentoProyecto actualizar(Integer idDocumento, String tipoDocumento,
                                       Integer idUsuarioActualizacion) {
        DocumentoProyecto documento = documentoRepository.findById(idDocumento)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        documento.setTipoDocumento(tipoDocumento);
        documento.setFechaSubida(LocalDateTime.now());

        return documentoRepository.save(documento);
    }

    @Transactional
    public void eliminar(Integer idDocumento) throws IOException {
        DocumentoProyecto documento = documentoRepository.findById(idDocumento)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

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

        documentoRepository.delete(documento);
    }

    @Transactional
    public void eliminarByProyecto(Integer idProyecto) throws IOException {
        List<DocumentoProyecto> documentos = documentoRepository.findByIdProyecto(idProyecto);
        for (DocumentoProyecto documento : documentos) {
            eliminar(documento.getIdDocumento());
        }
    }

    public DocumentoProyecto findById(Integer idDocumento) {
        return documentoRepository.findById(idDocumento).orElse(null);
    }

    public Page<DocumentoProyecto> findAll(Pageable pageable) {
        return documentoRepository.findAll(pageable);
    }

    public List<DocumentoProyecto> findAll() {
        return documentoRepository.findAll();
    }

    public List<DocumentoProyecto> findByProyecto(Integer idProyecto) {
        List<DocumentoProyecto> documentos = documentoRepository.findByIdProyectoOrderByFechaSubidaDesc(idProyecto);

        Proyecto proyecto = proyectoRepository.findById(idProyecto).orElse(null);
        if (proyecto != null) {
            for (DocumentoProyecto doc : documentos) {
                doc.setNombreProyecto(proyecto.getNombreProyecto());
            }
        }

        return documentos;
    }

    public Page<DocumentoProyecto> findByProyecto(Integer idProyecto, Pageable pageable) {
        return documentoRepository.findByIdProyecto(idProyecto, pageable);
    }

    public List<DocumentoProyecto> findByTipo(String tipoDocumento) {
        return documentoRepository.findByTipoDocumentoOrderByFechaSubidaDesc(tipoDocumento);
    }

    public Page<DocumentoProyecto> findByTipo(String tipoDocumento, Pageable pageable) {
        return documentoRepository.findByTipoDocumento(tipoDocumento, pageable);
    }

    public List<DocumentoProyecto> findRendersByProyecto(Integer idProyecto) {
        return documentoRepository.findByIdProyectoAndTipoDocumento(idProyecto, "Render");
    }

    public List<DocumentoProyecto> findPlanosByProyecto(Integer idProyecto) {
        return documentoRepository.findByIdProyectoAndTipoDocumento(idProyecto, "Plano");
    }

    public List<DocumentoProyecto> findBocetosByProyecto(Integer idProyecto) {
        return documentoRepository.findByIdProyectoAndTipoDocumento(idProyecto, "Boceto");
    }

    public List<DocumentoProyecto> findContratosByProyecto(Integer idProyecto) {
        return documentoRepository.findByIdProyectoAndTipoDocumento(idProyecto, "Contrato");
    }

    public List<DocumentoProyecto> findByCliente(Integer idCliente) {
        return documentoRepository.findDocumentosByCliente(idCliente);
    }

    public List<DocumentoProyecto> findByUsuarioSubio(Integer idUsuario) {
        return documentoRepository.findByIdUsuarioSubioOrderByFechaSubidaDesc(idUsuario);
    }

    public List<DocumentoProyecto> findTopN(int limit) {
        return documentoRepository.findTopN(limit);
    }

    public List<DocumentoProyecto> findTopNByProyecto(Integer idProyecto, int limit) {
        return documentoRepository.findTopNByProyecto(idProyecto, limit);
    }

    public Page<DocumentoProyecto> search(String searchText, Pageable pageable) {
        return documentoRepository.search(searchText, pageable);
    }

    public long count() {
        return documentoRepository.count();
    }

    public long countByProyecto(Integer idProyecto) {
        return documentoRepository.countByIdProyecto(idProyecto);
    }

    public long countByTipo(String tipoDocumento) {
        return documentoRepository.countByTipoDocumento(tipoDocumento);
    }

    public long countByUsuarioSubio(Integer idUsuario) {
        return documentoRepository.countByIdUsuarioSubio(idUsuario);
    }

    public List<Object[]> countByTipoResumen() {
        return documentoRepository.countByTipoDocumentoResumen();
    }

    public Long sumTamanoByProyecto(Integer idProyecto) {
        return documentoRepository.sumTamanoByProyecto(idProyecto);
    }

    public boolean hasDocumentos(Integer idProyecto) {
        return documentoRepository.existsByIdProyecto(idProyecto);
    }

    public boolean existsByProyectoAndNombre(Integer idProyecto, String nombreArchivo) {
        return documentoRepository.existsByIdProyectoAndNombreArchivo(idProyecto, nombreArchivo);
    }

    @Transactional
    public void delete(Integer id) {
        DocumentoProyecto documento = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con ID: " + id));
        documentoRepository.delete(documento);
    }

    @Transactional
    public void save(DocumentoProyecto documento) {
        if (documento == null) {
            throw new RuntimeException("Documento inválido para guardar");
        }
        if (documento.getIdProyecto() == null) {
            throw new RuntimeException("El proyecto es obligatorio");
        }
        documento.setFechaSubida(LocalDateTime.now());
        documentoRepository.save(documento);
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

    public String getTipoLegible(String tipoDocumento) {
        if (tipoDocumento == null) return "Otro";
        return switch (tipoDocumento) {
            case "Render" -> "Render";
            case "Plano" -> "Plano";
            case "Boceto" -> "Boceto";
            case "Contrato" -> "Contrato";
            default -> "Otro";
        };
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

    public String getColorByTipo(String tipoDocumento) {
        if (tipoDocumento == null) return "#6B7280";
        return switch (tipoDocumento) {
            case "Render" -> "#8B5CF6";
            case "Plano" -> "#3B82F6";
            case "Boceto" -> "#F59E0B";
            case "Contrato" -> "#10B981";
            default -> "#6B7280";
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

    public String getMimeType(String extension) {
        if (extension == null) return "application/octet-stream";
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            case "svg" -> "image/svg+xml";
            case "pdf" -> "application/pdf";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls" -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt" -> "application/vnd.ms-powerpoint";
            case "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            default -> "application/octet-stream";
        };
    }

    public boolean isImage(String extension) {
        if (extension == null) return false;
        return extension.matches("(?i)(jpg|jpeg|png|gif|webp|svg|bmp|ico)");
    }

    public boolean isPdf(String extension) {
        return "pdf".equalsIgnoreCase(extension);
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