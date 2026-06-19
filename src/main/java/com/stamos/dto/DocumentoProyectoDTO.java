package com.stamos.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO (Data Transfer Object) para la entidad DocumentoProyecto.
 * 
 * <p>Este DTO se utiliza para transferir datos de documentos de proyectos
 * entre la capa de servicio y la capa de presentación, permitiendo una
 * mejor organización y manejo de los archivos asociados a proyectos.</p>
 * 
 * <p>Incluye información del documento, tipo, tamaño, fechas y
 * referencias al proyecto y usuario que lo subió.</p>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
public class DocumentoProyectoDTO {

    // ============================================
    // ATRIBUTOS PRINCIPALES
    // ============================================

    private Integer idDocumento;
    private Integer idProyecto;
    private String tipoDocumento;
    private String nombreArchivo;
    private String urlArchivo;
    private Long tamanoBytes;

    // ============================================
    // CAMPOS DE AUDITORÍA
    // ============================================

    private LocalDateTime fechaSubida;
    private Integer idUsuarioSubio;
    private String nombreUsuarioSubio;
    private String emailUsuarioSubio;

    // ============================================
    // CAMPOS RELACIONADOS (DESDE PROYECTO)
    // ============================================

    private String nombreProyecto;
    private String codigoProyecto;
    private String estadoProyecto;

    // ============================================
    // CAMPOS FORMATEADOS
    // ============================================

    private String tipoDocumentoLegible;
    private String tamanoFormateado;
    private String fechaSubidaFormateada;
    private String extensionArchivo;
    private Boolean esImagen;
    private Boolean esPdf;
    private String icono;
    private String color;

    // ============================================
    // CAMPOS ADICIONALES
    // ============================================

    private String descripcion;
    private String categoria;

    // ============================================
    // CONSTRUCTORES
    // ============================================

    /**
     * Constructor por defecto.
     */
    public DocumentoProyectoDTO() {
        this.tipoDocumento = "Otro";
        this.esImagen = false;
        this.esPdf = false;
        this.icono = "ti ti-file";
        this.color = "#6B7280";
    }

    /**
     * Constructor con campos principales.
     * 
     * @param idDocumento ID del documento
     * @param nombreArchivo Nombre del archivo
     * @param tipoDocumento Tipo de documento
     * @param fechaSubida Fecha de subida
     */
    public DocumentoProyectoDTO(Integer idDocumento, String nombreArchivo, 
                               String tipoDocumento, LocalDateTime fechaSubida) {
        this();
        this.idDocumento = idDocumento;
        this.nombreArchivo = nombreArchivo;
        this.tipoDocumento = tipoDocumento;
        this.fechaSubida = fechaSubida;
        actualizarCamposFormateados();
    }

    /**
     * Constructor completo.
     * 
     * @param idDocumento ID del documento
     * @param idProyecto ID del proyecto
     * @param tipoDocumento Tipo de documento
     * @param nombreArchivo Nombre del archivo
     * @param urlArchivo URL del archivo
     * @param tamanoBytes Tamaño en bytes
     * @param fechaSubida Fecha de subida
     * @param nombreProyecto Nombre del proyecto
     */
    public DocumentoProyectoDTO(Integer idDocumento, Integer idProyecto, String tipoDocumento,
                               String nombreArchivo, String urlArchivo, Long tamanoBytes,
                               LocalDateTime fechaSubida, String nombreProyecto) {
        this();
        this.idDocumento = idDocumento;
        this.idProyecto = idProyecto;
        this.tipoDocumento = tipoDocumento;
        this.nombreArchivo = nombreArchivo;
        this.urlArchivo = urlArchivo;
        this.tamanoBytes = tamanoBytes;
        this.fechaSubida = fechaSubida;
        this.nombreProyecto = nombreProyecto;
        actualizarCamposFormateados();
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
        actualizarCamposFormateados();
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
        actualizarCamposFormateados();
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
        actualizarCamposFormateados();
    }

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
        actualizarCamposFormateados();
    }

    public Integer getIdUsuarioSubio() {
        return idUsuarioSubio;
    }

    public void setIdUsuarioSubio(Integer idUsuarioSubio) {
        this.idUsuarioSubio = idUsuarioSubio;
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

    public String getNombreProyecto() {
        return nombreProyecto;
    }

    public void setNombreProyecto(String nombreProyecto) {
        this.nombreProyecto = nombreProyecto;
    }

    public String getCodigoProyecto() {
        return codigoProyecto;
    }

    public void setCodigoProyecto(String codigoProyecto) {
        this.codigoProyecto = codigoProyecto;
    }

    public String getEstadoProyecto() {
        return estadoProyecto;
    }

    public void setEstadoProyecto(String estadoProyecto) {
        this.estadoProyecto = estadoProyecto;
    }

    public String getTipoDocumentoLegible() {
        return tipoDocumentoLegible;
    }

    public void setTipoDocumentoLegible(String tipoDocumentoLegible) {
        this.tipoDocumentoLegible = tipoDocumentoLegible;
    }

    public String getTamanoFormateado() {
        return tamanoFormateado;
    }

    public void setTamanoFormateado(String tamanoFormateado) {
        this.tamanoFormateado = tamanoFormateado;
    }

    public String getFechaSubidaFormateada() {
        return fechaSubidaFormateada;
    }

    public void setFechaSubidaFormateada(String fechaSubidaFormateada) {
        this.fechaSubidaFormateada = fechaSubidaFormateada;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    // ============================================
    // MÉTODOS DE NEGOCIO
    // ============================================

    /**
     * Actualiza los campos formateados basados en los atributos.
     */
    private void actualizarCamposFormateados() {
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

        // Tamaño formateado
        if (tamanoBytes != null) {
            double kb = tamanoBytes / 1024.0;
            if (kb < 1024) {
                this.tamanoFormateado = String.format("%.0f KB", kb);
            } else {
                double mb = kb / 1024.0;
                this.tamanoFormateado = String.format("%.1f MB", mb);
            }
        } else {
            this.tamanoFormateado = "-";
        }

        // Fecha formateada
        if (fechaSubida != null) {
            this.fechaSubidaFormateada = fechaSubida.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } else {
            this.fechaSubidaFormateada = "-";
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

    /**
     * Obtiene el estado del proyecto en formato legible.
     * 
     * @return Estado legible
     */
    public String getEstadoProyectoLegible() {
        if (estadoProyecto == null) return "Sin estado";
        
        switch (estadoProyecto) {
            case "Aprobado":
                return "Aprobado";
            case "En Diseño":
                return "En Diseño";
            case "En Producción":
                return "En Producción";
            case "Instalación/Montaje":
                return "Instalación/Montaje";
            case "Entregado":
                return "Entregado";
            case "Anulado":
                return "Anulado";
            default:
                return estadoProyecto;
        }
    }

    /**
     * Obtiene la clase CSS para el badge del estado del proyecto.
     * 
     * @return Clase CSS
     */
    public String getEstadoProyectoBadgeClass() {
        if (estadoProyecto == null) return "status-pendiente";
        
        switch (estadoProyecto) {
            case "Aprobado":
                return "status-aprobado";
            case "En Diseño":
                return "status-diseno";
            case "En Producción":
                return "status-produccion";
            case "Instalación/Montaje":
                return "status-instalacion";
            case "Entregado":
                return "status-entregado";
            case "Anulado":
                return "status-anulado";
            default:
                return "status-pendiente";
        }
    }

    // ============================================
    // MÉTODOS DE FORMATO PARA LA VISTA
    // ============================================

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
        if (tamanoFormateado != null && !tamanoFormateado.equals("-")) {
            sb.append(" (").append(tamanoFormateado).append(")");
        }
        if (nombreProyecto != null) {
            sb.append(" - ").append(nombreProyecto);
        }
        return sb.toString();
    }

    /**
     * Obtiene el nombre del archivo con formato para mostrar.
     * 
     * @return Nombre con formato
     */
    public String getNombreArchivoDisplay() {
        if (nombreArchivo == null) return "";
        if (tipoDocumento != null && !tipoDocumento.equals("Otro")) {
            return tipoDocumentoLegible + " - " + nombreArchivo;
        }
        return nombreArchivo;
    }

    /**
     * Obtiene el ícono según el tipo de archivo.
     * 
     * @return Ícono CSS
     */
    public String getArchivoIcono() {
        if (esImagen != null && esImagen) {
            return "ti ti-photo";
        }
        if (esPdf != null && esPdf) {
            return "ti ti-file-pdf";
        }
        return icono != null ? icono : "ti ti-file";
    }

    /**
     * Obtiene el color según el tipo de archivo.
     * 
     * @return Color en hexadecimal
     */
    public String getArchivoColor() {
        if (esImagen != null && esImagen) {
            return "#8B5CF6";
        }
        if (esPdf != null && esPdf) {
            return "#EF4444";
        }
        return color != null ? color : "#6B7280";
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
        sb.append("\"fechaSubida\":\"").append(fechaSubidaFormateada != null ? fechaSubidaFormateada : "").append("\"");
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
        sb.append("\"tamanoFormateado\":\"").append(tamanoFormateado != null ? tamanoFormateado : "").append("\",");
        sb.append("\"fechaSubida\":\"").append(fechaSubidaFormateada != null ? fechaSubidaFormateada : "").append("\",");
        sb.append("\"usuarioSubio\":\"").append(getUsuarioSubioLegible()).append("\",");
        sb.append("\"esImagen\":").append(esImagen != null && esImagen).append(",");
        sb.append("\"esPdf\":").append(esPdf != null && esPdf).append(",");
        sb.append("\"icono\":\"").append(icono != null ? icono : "").append("\",");
        sb.append("\"color\":\"").append(color != null ? color : "").append("\"");
        if (nombreProyecto != null) {
            sb.append(",\"nombreProyecto\":\"").append(nombreProyecto).append("\"");
        }
        if (estadoProyecto != null) {
            sb.append(",\"estadoProyecto\":\"").append(estadoProyecto).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

    // ============================================
    // MÉTODOS OVERRIDE
    // ============================================

    @Override
    public String toString() {
        return "DocumentoProyectoDTO{" +
                "idDocumento=" + idDocumento +
                ", idProyecto=" + idProyecto +
                ", tipoDocumento='" + tipoDocumento + '\'' +
                ", nombreArchivo='" + nombreArchivo + '\'' +
                ", tamanoBytes=" + tamanoBytes +
                ", fechaSubida=" + fechaSubida +
                ", nombreProyecto='" + nombreProyecto + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentoProyectoDTO that = (DocumentoProyectoDTO) o;
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
     * Crea un DTO desde una entidad.
     * 
     * @param documento Entidad de documento
     * @return DTO de documento
     */
    public static DocumentoProyectoDTO fromEntity(com.stamos.model.DocumentoProyecto documento) {
        if (documento == null) {
            return null;
        }

        DocumentoProyectoDTO dto = new DocumentoProyectoDTO();
        dto.setIdDocumento(documento.getIdDocumento());
        dto.setIdProyecto(documento.getIdProyecto());
        dto.setTipoDocumento(documento.getTipoDocumento());
        dto.setNombreArchivo(documento.getNombreArchivo());
        dto.setUrlArchivo(documento.getUrlArchivo());
        dto.setTamanoBytes(documento.getTamanoBytes());
        dto.setFechaSubida(documento.getFechaSubida());
        dto.setIdUsuarioSubio(documento.getIdUsuarioSubio());
        dto.setNombreUsuarioSubio(documento.getNombreUsuarioSubio());
        dto.setEmailUsuarioSubio(documento.getEmailUsuarioSubio());
        dto.setNombreProyecto(documento.getNombreProyecto());
        
        // Actualizar campos formateados
        dto.actualizarCamposFormateados();
        
        return dto;
    }

    /**
     * Crea un DTO desde una entidad con datos del proyecto.
     * 
     * @param documento Entidad de documento
     * @param nombreProyecto Nombre del proyecto
     * @param codigoProyecto Código del proyecto
     * @param estadoProyecto Estado del proyecto
     * @return DTO de documento
     */
    public static DocumentoProyectoDTO fromEntityWithProject(
            com.stamos.model.DocumentoProyecto documento,
            String nombreProyecto,
            String codigoProyecto,
            String estadoProyecto) {
        
        DocumentoProyectoDTO dto = fromEntity(documento);
        if (dto != null) {
            dto.setNombreProyecto(nombreProyecto);
            dto.setCodigoProyecto(codigoProyecto);
            dto.setEstadoProyecto(estadoProyecto);
        }
        return dto;
    }

    /**
     * Crea un DTO para un nuevo documento.
     * 
     * @param idProyecto ID del proyecto
     * @param tipoDocumento Tipo de documento
     * @param nombreArchivo Nombre del archivo
     * @param urlArchivo URL del archivo
     * @param tamanoBytes Tamaño en bytes
     * @return DTO de documento
     */
    public static DocumentoProyectoDTO createNew(Integer idProyecto, String tipoDocumento,
                                                String nombreArchivo, String urlArchivo,
                                                Long tamanoBytes) {
        DocumentoProyectoDTO dto = new DocumentoProyectoDTO();
        dto.setIdProyecto(idProyecto);
        dto.setTipoDocumento(tipoDocumento);
        dto.setNombreArchivo(nombreArchivo);
        dto.setUrlArchivo(urlArchivo);
        dto.setTamanoBytes(tamanoBytes);
        dto.setFechaSubida(LocalDateTime.now());
        dto.actualizarCamposFormateados();
        return dto;
    }

    /**
     * Convierte el DTO a una entidad.
     * 
     * @return Entidad de documento
     */
    public com.stamos.model.DocumentoProyecto toEntity() {
        com.stamos.model.DocumentoProyecto documento = new com.stamos.model.DocumentoProyecto();
        documento.setIdDocumento(this.idDocumento);
        documento.setIdProyecto(this.idProyecto);
        documento.setTipoDocumento(this.tipoDocumento);
        documento.setNombreArchivo(this.nombreArchivo);
        documento.setUrlArchivo(this.urlArchivo);
        documento.setTamanoBytes(this.tamanoBytes);
        documento.setFechaSubida(this.fechaSubida);
        documento.setIdUsuarioSubio(this.idUsuarioSubio);
        return documento;
    }
}