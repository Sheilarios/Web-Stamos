package com.stamos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Entidad DocumentoProyecto.
 * 
 * <p>Esta entidad representa los documentos asociados a un proyecto,
 * como renders, planos, bocetos, contratos y otros archivos.</p>
 * 
 * <p>Implementa los requerimientos:</p>
 * <ul>
 *   <li>RF-23: Visualización de renders, planos, bocetos y documentación</li>
 *   <li>RF-14: Mostrar información del proyecto y documentación asociada</li>
 * </ul>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
@Entity
@Table(name = "DocumentoProyecto",
       indexes = {
           @Index(name = "idx_documento_proyecto", columnList = "id_proyecto"),
           @Index(name = "idx_documento_tipo", columnList = "tipo_documento"),
           @Index(name = "idx_documento_usuario", columnList = "id_usuario_subio"),
           @Index(name = "idx_documento_fecha", columnList = "fecha_subida")
       })
public class DocumentoProyecto {

    // ============================================
    // ATRIBUTOS PRINCIPALES
    // ============================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_documento")
    private Integer idDocumento;

    @NotNull(message = "El proyecto es obligatorio")
    @Column(name = "id_proyecto", nullable = false)
    private Integer idProyecto;

    @NotBlank(message = "El tipo de documento es obligatorio")
    @Column(name = "tipo_documento", nullable = false)
    private String tipoDocumento; // 'Render', 'Plano', 'Boceto', 'Contrato', 'Otro'

    @NotBlank(message = "El nombre del archivo es obligatorio")
    @Size(max = 255, message = "El nombre del archivo no puede exceder los 255 caracteres")
    @Column(name = "nombre_archivo", nullable = false, length = 255)
    private String nombreArchivo;

    @NotBlank(message = "La URL del archivo es obligatoria")
    @Size(max = 500, message = "La URL del archivo no puede exceder los 500 caracteres")
    @Column(name = "url_archivo", nullable = false, length = 500)
    private String urlArchivo;

    @Column(name = "tamano_bytes")
    private Long tamanoBytes;

    @Column(name = "fecha_subida", nullable = false)
    private LocalDateTime fechaSubida;

    @Column(name = "id_usuario_subio")
    private Integer idUsuarioSubio;

    // ============================================
    // CAMPOS ADICIONALES (NO EN BD)
    // ============================================

    @Transient
    private String nombreProyecto;

    @Transient
    private String nombreUsuarioSubio;

    @Transient
    private String emailUsuarioSubio;

    @Transient
    private String tipoDocumentoLegible;

    @Transient
    private String icono;

    @Transient
    private String color;

    @Transient
    private String extensionArchivo;

    @Transient
    private Boolean esImagen;

    @Transient
    private Boolean esPdf;

    // ============================================
    // CONSTRUCTORES
    // ============================================

    public DocumentoProyecto() {
        this.fechaSubida = LocalDateTime.now();
        this.tipoDocumento = "Otro";
    }

    public DocumentoProyecto(Integer idProyecto, String tipoDocumento, 
                            String nombreArchivo, String urlArchivo) {
        this();
        this.idProyecto = idProyecto;
        this.tipoDocumento = tipoDocumento;
        this.nombreArchivo = nombreArchivo;
        this.urlArchivo = urlArchivo;
    }

    public DocumentoProyecto(Integer idProyecto, String tipoDocumento, 
                            String nombreArchivo, String urlArchivo, 
                            Long tamanoBytes, Integer idUsuarioSubio) {
        this(idProyecto, tipoDocumento, nombreArchivo, urlArchivo);
        this.tamanoBytes = tamanoBytes;
        this.idUsuarioSubio = idUsuarioSubio;
    }

    // ============================================
    // GETTERS Y SETTERS
    // ============================================

    public Integer getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(Integer idDocumento) {
        this.idDocumento = idDocumento;
    }

    public Integer getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(Integer idProyecto) {
        this.idProyecto = idProyecto;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
        actualizarCamposTransient();
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
        actualizarCamposTransient();
    }

    public String getUrlArchivo() {
        return urlArchivo;
    }

    public void setUrlArchivo(String urlArchivo) {
        this.urlArchivo = urlArchivo;
    }

    public Long getTamanoBytes() {
        return tamanoBytes;
    }

    public void setTamanoBytes(Long tamanoBytes) {
        this.tamanoBytes = tamanoBytes;
    }

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public Integer getIdUsuarioSubio() {
        return idUsuarioSubio;
    }

    public void setIdUsuarioSubio(Integer idUsuarioSubio) {
        this.idUsuarioSubio = idUsuarioSubio;
    }

    public String getNombreProyecto() {
        return nombreProyecto;
    }

    public void setNombreProyecto(String nombreProyecto) {
        this.nombreProyecto = nombreProyecto;
    }

    public String getNombreUsuarioSubio() {
        return nombreUsuarioSubio;
    }

    public void setNombreUsuarioSubio(String nombreUsuarioSubio) {
        this.nombreUsuarioSubio = nombreUsuarioSubio;
    }

    public String getEmailUsuarioSubio() {
        return emailUsuarioSubio;
    }

    public void setEmailUsuarioSubio(String emailUsuarioSubio) {
        this.emailUsuarioSubio = emailUsuarioSubio;
    }

    public String getTipoDocumentoLegible() {
        return tipoDocumentoLegible;
    }

    public void setTipoDocumentoLegible(String tipoDocumentoLegible) {
        this.tipoDocumentoLegible = tipoDocumentoLegible;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getExtensionArchivo() {
        return extensionArchivo;
    }

    public void setExtensionArchivo(String extensionArchivo) {
        this.extensionArchivo = extensionArchivo;
    }

    public Boolean getEsImagen() {
        return esImagen;
    }

    public void setEsImagen(Boolean esImagen) {
        this.esImagen = esImagen;
    }

    public Boolean getEsPdf() {
        return esPdf;
    }

    public void setEsPdf(Boolean esPdf) {
        this.esPdf = esPdf;
    }

    // ============================================
    // MÉTODOS DE NEGOCIO
    // ============================================

    /**
     * Actualiza los campos transient basados en el tipo y nombre del archivo.
     */
    private void actualizarCamposTransient() {
        // Tipo de documento legible
        if (tipoDocumento != null) {
            switch (tipoDocumento) {
                case "Render":
                    this.tipoDocumentoLegible = "Render";
                    this.icono = "ti ti-photo";
                    this.color = "#8B5CF6";
                    break;
                case "Plano":
                    this.tipoDocumentoLegible = "Plano";
                    this.icono = "ti ti-ruler-2";
                    this.color = "#3B82F6";
                    break;
                case "Boceto":
                    this.tipoDocumentoLegible = "Boceto";
                    this.icono = "ti ti-pencil";
                    this.color = "#F59E0B";
                    break;
                case "Contrato":
                    this.tipoDocumentoLegible = "Contrato";
                    this.icono = "ti ti-file-contract";
                    this.color = "#10B981";
                    break;
                default:
                    this.tipoDocumentoLegible = "Otro";
                    this.icono = "ti ti-file";
                    this.color = "#6B7280";
                    break;
            }
        }

        // Extensión del archivo
        if (nombreArchivo != null && nombreArchivo.contains(".")) {
            this.extensionArchivo = nombreArchivo.substring(nombreArchivo.lastIndexOf(".") + 1).toLowerCase();
            
            // Verificar si es imagen
            this.esImagen = extensionArchivo.matches("(?i)(jpg|jpeg|png|gif|webp|svg|bmp|ico)");
            
            // Verificar si es PDF
            this.esPdf = "pdf".equalsIgnoreCase(extensionArchivo);
        } else {
            this.extensionArchivo = "";
            this.esImagen = false;
            this.esPdf = false;
        }
    }

    /**
     * Verifica si el documento es de tipo Render.
     * 
     * @return true si es Render, false en caso contrario
     */
    public boolean esRender() {
        return "Render".equals(this.tipoDocumento);
    }

    /**
     * Verifica si el documento es de tipo Plano.
     * 
     * @return true si es Plano, false en caso contrario
     */
    public boolean esPlano() {
        return "Plano".equals(this.tipoDocumento);
    }

    /**
     * Verifica si el documento es de tipo Boceto.
     * 
     * @return true si es Boceto, false en caso contrario
     */
    public boolean esBoceto() {
        return "Boceto".equals(this.tipoDocumento);
    }

    /**
     * Verifica si el documento es de tipo Contrato.
     * 
     * @return true si es Contrato, false en caso contrario
     */
    public boolean esContrato() {
        return "Contrato".equals(this.tipoDocumento);
    }

    /**
     * Verifica si el documento tiene archivo asociado.
     * 
     * @return true si tiene archivo, false en caso contrario
     */
    public boolean tieneArchivo() {
        return urlArchivo != null && !urlArchivo.trim().isEmpty();
    }

    /**
     * Obtiene el tamaño del archivo formateado.
     * 
     * @return Tamaño formateado (KB o MB)
     */
    public String getTamanoFormateado() {
        if (tamanoBytes == null) return "-";
        
        double kb = tamanoBytes / 1024.0;
        if (kb < 1024) {
            return String.format("%.0f KB", kb);
        }
        double mb = kb / 1024.0;
        return String.format("%.1f MB", mb);
    }

    /**
     * Obtiene el nombre del archivo sin extensión.
     * 
     * @return Nombre del archivo sin extensión
     */
    public String getNombreArchivoSinExtension() {
        if (nombreArchivo == null) return "";
        if (nombreArchivo.contains(".")) {
            return nombreArchivo.substring(0, nombreArchivo.lastIndexOf("."));
        }
        return nombreArchivo;
    }

    /**
     * Obtiene el usuario que subió el documento en formato legible.
     * 
     * @return Usuario legible
     */
    public String getUsuarioSubioLegible() {
        if (nombreUsuarioSubio != null && !nombreUsuarioSubio.isEmpty()) {
            return nombreUsuarioSubio;
        }
        if (emailUsuarioSubio != null && !emailUsuarioSubio.isEmpty()) {
            return emailUsuarioSubio;
        }
        if (idUsuarioSubio != null) {
            return "Usuario ID: " + idUsuarioSubio;
        }
        return "Sistema";
    }

    /**
     * Obtiene la clase CSS para el badge del tipo de documento.
     * 
     * @return Clase CSS
     */
    public String getTipoBadgeClass() {
        if (tipoDocumento == null) return "otro";
        
        switch (tipoDocumento) {
            case "Render":
                return "render";
            case "Plano":
                return "plano";
            case "Boceto":
                return "boceto";
            case "Contrato":
                return "contrato";
            default:
                return "otro";
        }
    }

    // ============================================
    // MÉTODOS DE FORMATO PARA LA VISTA
    // ============================================

    /**
     * Obtiene la fecha de subida formateada.
     * 
     * @return Fecha formateada
     */
    public String getFechaSubidaFormateada() {
        if (fechaSubida == null) return "-";
        return fechaSubida.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    /**
     * Obtiene la fecha de subida formateada (solo fecha).
     * 
     * @return Fecha formateada
     */
    public String getFechaSubidaSoloFecha() {
        if (fechaSubida == null) return "-";
        return fechaSubida.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Obtiene el nombre del archivo truncado.
     * 
     * @param maxLength Longitud máxima
     * @return Nombre truncado
     */
    public String getNombreArchivoTruncado(int maxLength) {
        if (nombreArchivo == null) return "";
        if (nombreArchivo.length() <= maxLength) return nombreArchivo;
        return nombreArchivo.substring(0, maxLength) + "...";
    }

    /**
     * Obtiene el tipo de documento con ícono.
     * 
     * @return Tipo con ícono
     */
    public String getTipoConIcono() {
        return "<i class='" + icono + "'></i> " + tipoDocumentoLegible;
    }

    /**
     * Obtiene el resumen del documento.
     * 
     * @return Resumen
     */
    public String getResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append(tipoDocumentoLegible);
        sb.append(": ").append(nombreArchivo);
        if (tamanoBytes != null) {
            sb.append(" (").append(getTamanoFormateado()).append(")");
        }
        if (nombreProyecto != null) {
            sb.append(" - ").append(nombreProyecto);
        }
        return sb.toString();
    }

    /**
     * Obtiene el tipo de archivo para el atributo accept de input file.
     * 
     * @return Tipo MIME
     */
    public String getTipoMime() {
        if (extensionArchivo == null) return "application/octet-stream";
        
        switch (extensionArchivo) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            case "svg":
                return "image/svg+xml";
            case "pdf":
                return "application/pdf";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls":
                return "application/vnd.ms-excel";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt":
                return "application/vnd.ms-powerpoint";
            case "pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            default:
                return "application/octet-stream";
        }
    }

    // ============================================
    // MÉTODOS DE CONVERSIÓN
    // ============================================

    /**
     * Convierte a una representación JSON simple.
     * 
     * @return JSON como String
     */
    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"idDocumento\":").append(idDocumento).append(",");
        sb.append("\"idProyecto\":").append(idProyecto).append(",");
        sb.append("\"tipo\":\"").append(tipoDocumento != null ? tipoDocumento : "").append("\",");
        sb.append("\"nombre\":\"").append(nombreArchivo != null ? nombreArchivo : "").append("\",");
        sb.append("\"url\":\"").append(urlArchivo != null ? urlArchivo : "").append("\",");
        sb.append("\"tamano\":").append(tamanoBytes != null ? tamanoBytes : 0).append(",");
        sb.append("\"fechaSubida\":\"").append(getFechaSubidaFormateada()).append("\"");
        if (nombreProyecto != null) {
            sb.append(",\"proyecto\":\"").append(nombreProyecto).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convierte a JSON con todos los campos.
     * 
     * @return JSON completo
     */
    public String toJsonCompleto() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"idDocumento\":").append(idDocumento).append(",");
        sb.append("\"idProyecto\":").append(idProyecto).append(",");
        sb.append("\"tipoDocumento\":\"").append(tipoDocumento != null ? tipoDocumento : "").append("\",");
        sb.append("\"tipoLegible\":\"").append(tipoDocumentoLegible != null ? tipoDocumentoLegible : "").append("\",");
        sb.append("\"nombreArchivo\":\"").append(nombreArchivo != null ? nombreArchivo : "").append("\",");
        sb.append("\"urlArchivo\":\"").append(urlArchivo != null ? urlArchivo : "").append("\",");
        sb.append("\"tamanoBytes\":").append(tamanoBytes != null ? tamanoBytes : 0).append(",");
        sb.append("\"tamanoFormateado\":\"").append(getTamanoFormateado()).append("\",");
        sb.append("\"fechaSubida\":\"").append(getFechaSubidaFormateada()).append("\",");
        sb.append("\"usuarioSubio\":\"").append(getUsuarioSubioLegible()).append("\",");
        sb.append("\"esImagen\":").append(esImagen != null && esImagen).append(",");
        sb.append("\"esPdf\":").append(esPdf != null && esPdf);
        if (nombreProyecto != null) {
            sb.append(",\"nombreProyecto\":\"").append(nombreProyecto).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

    // ============================================
    // MÉTODOS OVERRIDE
    // ============================================

    @Override
    public String toString() {
        return "DocumentoProyecto{" +
                "idDocumento=" + idDocumento +
                ", idProyecto=" + idProyecto +
                ", tipoDocumento='" + tipoDocumento + '\'' +
                ", nombreArchivo='" + nombreArchivo + '\'' +
                ", tamanoBytes=" + tamanoBytes +
                ", fechaSubida=" + fechaSubida +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentoProyecto that = (DocumentoProyecto) o;
        return idDocumento != null && idDocumento.equals(that.idDocumento);
    }

    @Override
    public int hashCode() {
        return idDocumento != null ? idDocumento.hashCode() : 0;
    }

    // ============================================
    // MÉTODOS ESTÁTICOS DE FÁBRICA
    // ============================================

    /**
     * Crea un documento de tipo Render.
     * 
     * @param idProyecto ID del proyecto
     * @param nombreArchivo Nombre del archivo
     * @param urlArchivo URL del archivo
     * @return Documento
     */
    public static DocumentoProyecto crearRender(Integer idProyecto, String nombreArchivo, String urlArchivo) {
        return new DocumentoProyecto(idProyecto, "Render", nombreArchivo, urlArchivo);
    }

    /**
     * Crea un documento de tipo Plano.
     * 
     * @param idProyecto ID del proyecto
     * @param nombreArchivo Nombre del archivo
     * @param urlArchivo URL del archivo
     * @return Documento
     */
    public static DocumentoProyecto crearPlano(Integer idProyecto, String nombreArchivo, String urlArchivo) {
        return new DocumentoProyecto(idProyecto, "Plano", nombreArchivo, urlArchivo);
    }

    /**
     * Crea un documento de tipo Boceto.
     * 
     * @param idProyecto ID del proyecto
     * @param nombreArchivo Nombre del archivo
     * @param urlArchivo URL del archivo
     * @return Documento
     */
    public static DocumentoProyecto crearBoceto(Integer idProyecto, String nombreArchivo, String urlArchivo) {
        return new DocumentoProyecto(idProyecto, "Boceto", nombreArchivo, urlArchivo);
    }

    /**
     * Crea un documento de tipo Contrato.
     * 
     * @param idProyecto ID del proyecto
     * @param nombreArchivo Nombre del archivo
     * @param urlArchivo URL del archivo
     * @return Documento
     */
    public static DocumentoProyecto crearContrato(Integer idProyecto, String nombreArchivo, String urlArchivo) {
        return new DocumentoProyecto(idProyecto, "Contrato", nombreArchivo, urlArchivo);
    }

    /**
     * Crea un documento de tipo Otro.
     * 
     * @param idProyecto ID del proyecto
     * @param nombreArchivo Nombre del archivo
     * @param urlArchivo URL del archivo
     * @return Documento
     */
    public static DocumentoProyecto crearOtro(Integer idProyecto, String nombreArchivo, String urlArchivo) {
        return new DocumentoProyecto(idProyecto, "Otro", nombreArchivo, urlArchivo);
    }

    /**
     * Crea un documento desde un DTO.
     * 
     * @param dto DTO del documento
     * @return Documento
     */
    public static DocumentoProyecto fromDTO(com.stamos.dto.DocumentoProyectoDTO dto) {
        if (dto == null) {
            return null;
        }
        
        DocumentoProyecto documento = new DocumentoProyecto();
        if (dto.getIdDocumento() != null) {
            documento.setIdDocumento(dto.getIdDocumento());
        }
        documento.setIdProyecto(dto.getIdProyecto());
        documento.setTipoDocumento(dto.getTipoDocumento());
        documento.setNombreArchivo(dto.getNombreArchivo());
        documento.setUrlArchivo(dto.getUrlArchivo());
        documento.setTamanoBytes(dto.getTamanoBytes());
        documento.setIdUsuarioSubio(dto.getIdUsuarioSubio());
        return documento;
    }

    /**
     * Convierte el documento a un DTO.
     * 
     * @return DTO del documento
     */
    public com.stamos.dto.DocumentoProyectoDTO toDTO() {
        com.stamos.dto.DocumentoProyectoDTO dto = new com.stamos.dto.DocumentoProyectoDTO();
        dto.setIdDocumento(this.idDocumento);
        dto.setIdProyecto(this.idProyecto);
        dto.setTipoDocumento(this.tipoDocumento);
        dto.setNombreArchivo(this.nombreArchivo);
        dto.setUrlArchivo(this.urlArchivo);
        dto.setTamanoBytes(this.tamanoBytes);
        dto.setFechaSubida(this.fechaSubida);
        dto.setIdUsuarioSubio(this.idUsuarioSubio);
        dto.setNombreProyecto(this.nombreProyecto);
        dto.setNombreUsuarioSubio(this.nombreUsuarioSubio);
        dto.setEmailUsuarioSubio(this.emailUsuarioSubio);
        return dto;
    }
}