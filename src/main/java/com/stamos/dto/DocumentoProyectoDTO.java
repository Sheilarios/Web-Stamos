package com.stamos.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DocumentoProyectoDTO {

    private Integer idDocumento;
    private Integer idProyecto;
    private String tipoDocumento;
    private String nombreArchivo;
    private String urlArchivo;
    private Long tamanoBytes;
    private LocalDateTime fechaSubida;
    private Integer idUsuarioSubio;
    private String nombreUsuarioSubio;
    private String emailUsuarioSubio;
    private String nombreProyecto;
    private String codigoProyecto;
    private String estadoProyecto;
    private String tipoDocumentoLegible;
    private String tamanoFormateado;
    private String fechaSubidaFormateada;
    private String extensionArchivo;
    private Boolean esImagen;
    private Boolean esPdf;
    private String icono;
    private String color;
    private String descripcion;
    private String categoria;

    public DocumentoProyectoDTO() {
        this.tipoDocumento = "Otro";
        this.esImagen = false;
        this.esPdf = false;
        this.icono = "ti ti-file";
        this.color = "#6B7280";
    }

    public DocumentoProyectoDTO(Integer idDocumento, String nombreArchivo, 
                               String tipoDocumento, LocalDateTime fechaSubida) {
        this();
        this.idDocumento = idDocumento;
        this.nombreArchivo = nombreArchivo;
        this.tipoDocumento = tipoDocumento;
        this.fechaSubida = fechaSubida;
        actualizarCamposFormateados();
    }

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

    private void actualizarCamposFormateados() {
        if (tipoDocumento != null) {
            switch (tipoDocumento) {
                case "Render" -> {
                    this.tipoDocumentoLegible = "Render";
                    this.icono = "ti ti-photo";
                    this.color = "#8B5CF6";
                }
                case "Plano" -> {
                    this.tipoDocumentoLegible = "Plano";
                    this.icono = "ti ti-ruler-2";
                    this.color = "#3B82F6";
                }
                case "Boceto" -> {
                    this.tipoDocumentoLegible = "Boceto";
                    this.icono = "ti ti-pencil";
                    this.color = "#F59E0B";
                }
                case "Contrato" -> {
                    this.tipoDocumentoLegible = "Contrato";
                    this.icono = "ti ti-file-contract";
                    this.color = "#10B981";
                }
                default -> {
                    this.tipoDocumentoLegible = "Otro";
                    this.icono = "ti ti-file";
                    this.color = "#6B7280";
                }
            }
        }

        if (nombreArchivo != null && nombreArchivo.contains(".")) {
            this.extensionArchivo = nombreArchivo.substring(nombreArchivo.lastIndexOf(".") + 1).toLowerCase();
            this.esImagen = extensionArchivo.matches("(?i)(jpg|jpeg|png|gif|webp|svg|bmp|ico)");
            this.esPdf = "pdf".equalsIgnoreCase(extensionArchivo);
        } else {
            this.extensionArchivo = "";
            this.esImagen = false;
            this.esPdf = false;
        }

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

        if (fechaSubida != null) {
            this.fechaSubidaFormateada = fechaSubida.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } else {
            this.fechaSubidaFormateada = "-";
        }
    }

    public boolean esRender() {
        return "Render".equals(this.tipoDocumento);
    }

    public boolean esPlano() {
        return "Plano".equals(this.tipoDocumento);
    }

    public boolean esBoceto() {
        return "Boceto".equals(this.tipoDocumento);
    }

    public boolean esContrato() {
        return "Contrato".equals(this.tipoDocumento);
    }

    public boolean tieneArchivo() {
        return urlArchivo != null && !urlArchivo.trim().isEmpty();
    }

    public String getNombreArchivoSinExtension() {
        if (nombreArchivo == null) return "";
        if (nombreArchivo.contains(".")) {
            return nombreArchivo.substring(0, nombreArchivo.lastIndexOf("."));
        }
        return nombreArchivo;
    }

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

    public String getTipoBadgeClass() {
        if (tipoDocumento == null) return "otro";
        return switch (tipoDocumento) {
            case "Render" -> "render";
            case "Plano" -> "plano";
            case "Boceto" -> "boceto";
            case "Contrato" -> "contrato";
            default -> "otro";
        };
    }

    public String getTipoMime() {
        if (extensionArchivo == null) return "application/octet-stream";
        return switch (extensionArchivo) {
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
        dto.actualizarCamposFormateados();
        return dto;
    }

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
}