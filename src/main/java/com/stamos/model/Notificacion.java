package com.stamos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "Notificacion", indexes = {
    @Index(name = "idx_notificacion_usuario", columnList = "id_usuario_destino"),
    @Index(name = "idx_notificacion_cliente", columnList = "id_cliente_destino"),
    @Index(name = "idx_notificacion_estado", columnList = "estado_envio"),
    @Index(name = "idx_notificacion_leido", columnList = "leido"),
    @Index(name = "idx_notificacion_fecha", columnList = "fecha_envio"),
    @Index(name = "idx_notificacion_tipo", columnList = "tipo_evento")
})
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Integer idNotificacion;

    @Column(name = "id_usuario_destino")
    private Integer idUsuarioDestino;

    @Column(name = "id_cliente_destino")
    private Integer idClienteDestino;

    @NotBlank(message = "El asunto es obligatorio")
    @Size(max = 200, message = "El asunto no puede exceder los 200 caracteres")
    @Column(name = "asunto", nullable = false, length = 200)
    private String asunto;

    @NotBlank(message = "El mensaje es obligatorio")
    @Column(name = "mensaje", nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @NotBlank(message = "El tipo de evento es obligatorio")
    @Size(max = 50, message = "El tipo de evento no puede exceder los 50 caracteres")
    @Column(name = "tipo_evento", nullable = false, length = 50)
    private String tipoEvento;

    @Column(name = "id_referencia")
    private Integer idReferencia;

    @Size(max = 50, message = "La entidad de referencia no puede exceder los 50 caracteres")
    @Column(name = "entidad_referencia", length = 50)
    private String entidadReferencia;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio;

    @Column(name = "estado_envio", nullable = false)
    private String estadoEnvio = "Pendiente";

    @Column(name = "intentos", nullable = false)
    private Integer intentos = 0;

    @Column(name = "leido", nullable = false)
    private Boolean leido = false;

    @Column(name = "fecha_lectura")
    private LocalDateTime fechaLectura;

    @Size(max = 100, message = "El campo enviado_por no puede exceder los 100 caracteres")
    @Column(name = "enviado_por", length = 100)
    private String enviadoPor;

    @Transient
    private String nombreUsuarioDestino;

    @Transient
    private String nombreClienteDestino;

    @Transient
    private String tipoEventoLegible;

    @Transient
    private String colorIcono;

    @Transient
    private String icono;


    public Notificacion() {
        this.fechaEnvio = LocalDateTime.now();
        this.estadoEnvio = "Pendiente";
        this.intentos = 0;
        this.leido = false;
    }

    public Notificacion(Integer idDestino, boolean esUsuario, String asunto, String mensaje, String tipoEvento) {
        this();
        if (esUsuario) {
            this.idUsuarioDestino = idDestino;
        } else {
            this.idClienteDestino = idDestino;
        }
        this.asunto = asunto;
        this.mensaje = mensaje;
        this.tipoEvento = tipoEvento;
        actualizarCamposTransient();
    }


    public static Notificacion paraUsuario(Integer idUsuarioDestino, String asunto, String mensaje, String tipoEvento) {
        Notificacion n = new Notificacion();
        n.setIdUsuarioDestino(idUsuarioDestino);
        n.setAsunto(asunto);
        n.setMensaje(mensaje);
        n.setTipoEvento(tipoEvento);
        n.actualizarCamposTransient();
        return n;
    }

    public static Notificacion paraCliente(Integer idClienteDestino, String asunto, String mensaje, String tipoEvento) {
        Notificacion n = new Notificacion();
        n.setIdClienteDestino(idClienteDestino);
        n.setAsunto(asunto);
        n.setMensaje(mensaje);
        n.setTipoEvento(tipoEvento);
        n.actualizarCamposTransient();
        return n;
    }


    public Integer getIdNotificacion() { return idNotificacion; }
    public void setIdNotificacion(Integer idNotificacion) { this.idNotificacion = idNotificacion; }

    public Integer getIdUsuarioDestino() { return idUsuarioDestino; }
    public void setIdUsuarioDestino(Integer idUsuarioDestino) { this.idUsuarioDestino = idUsuarioDestino; }

    public Integer getIdClienteDestino() { return idClienteDestino; }
    public void setIdClienteDestino(Integer idClienteDestino) { this.idClienteDestino = idClienteDestino; }

    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { 
        this.tipoEvento = tipoEvento;
        actualizarCamposTransient();
    }

    public Integer getIdReferencia() { return idReferencia; }
    public void setIdReferencia(Integer idReferencia) { this.idReferencia = idReferencia; }

    public String getEntidadReferencia() { return entidadReferencia; }
    public void setEntidadReferencia(String entidadReferencia) { this.entidadReferencia = entidadReferencia; }

    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }

    public String getEstadoEnvio() { return estadoEnvio; }
    public void setEstadoEnvio(String estadoEnvio) { 
        this.estadoEnvio = estadoEnvio;
        actualizarCamposTransient();
    }

    public Integer getIntentos() { return intentos; }
    public void setIntentos(Integer intentos) { this.intentos = intentos; }

    public Boolean getLeido() { return leido; }
    public void setLeido(Boolean leido) { this.leido = leido; }

    public LocalDateTime getFechaLectura() { return fechaLectura; }
    public void setFechaLectura(LocalDateTime fechaLectura) { this.fechaLectura = fechaLectura; }

    public String getEnviadoPor() { return enviadoPor; }
    public void setEnviadoPor(String enviadoPor) { this.enviadoPor = enviadoPor; }

    public String getNombreUsuarioDestino() { return nombreUsuarioDestino; }
    public void setNombreUsuarioDestino(String nombreUsuarioDestino) { this.nombreUsuarioDestino = nombreUsuarioDestino; }

    public String getNombreClienteDestino() { return nombreClienteDestino; }
    public void setNombreClienteDestino(String nombreClienteDestino) { this.nombreClienteDestino = nombreClienteDestino; }

    public String getTipoEventoLegible() { return tipoEventoLegible; }
    public void setTipoEventoLegible(String tipoEventoLegible) { this.tipoEventoLegible = tipoEventoLegible; }

    public String getColorIcono() { return colorIcono; }
    public void setColorIcono(String colorIcono) { this.colorIcono = colorIcono; }

    public String getIcono() { return icono; }
    public void setIcono(String icono) { this.icono = icono; }


    private void actualizarCamposTransient() {
        if (tipoEvento != null) {
            switch (tipoEvento) {
                case "CAMBIO_ESTADO_PROYECTO" -> {
                    this.tipoEventoLegible = "Cambio de Estado";
                    this.icono = "ti ti-arrows-exchange";
                    this.colorIcono = "#8B5CF6";
                }
                case "COTIZACION_APROBADA" -> {
                    this.tipoEventoLegible = "Cotizacion Aprobada";
                    this.icono = "ti ti-check-circle";
                    this.colorIcono = "#10B981";
                }
                case "COTIZACION_ANULADA" -> {
                    this.tipoEventoLegible = "Cotizacion Anulada";
                    this.icono = "ti ti-x-circle";
                    this.colorIcono = "#EF4444";
                }
                case "NUEVO_DOCUMENTO" -> {
                    this.tipoEventoLegible = "Nuevo Documento";
                    this.icono = "ti ti-file";
                    this.colorIcono = "#3B82F6";
                }
                case "PAGO_REGISTRADO" -> {
                    this.tipoEventoLegible = "Pago Registrado";
                    this.icono = "ti ti-credit-card";
                    this.colorIcono = "#F59E0B";
                }
                case "PAGO_VALIDADO" -> {
                    this.tipoEventoLegible = "Pago Validado";
                    this.icono = "ti ti-check";
                    this.colorIcono = "#10B981";
                }
                case "PAGO_RECHAZADO" -> {
                    this.tipoEventoLegible = "Pago Rechazado";
                    this.icono = "ti ti-x";
                    this.colorIcono = "#EF4444";
                }
                case "PAGO_VENCIDO" -> {
                    this.tipoEventoLegible = "Pago Vencido";
                    this.icono = "ti ti-alert-triangle";
                    this.colorIcono = "#EF4444";
                }
                case "PROYECTO_ENTREGADO" -> {
                    this.tipoEventoLegible = "Proyecto Entregado";
                    this.icono = "ti ti-package";
                    this.colorIcono = "#10B981";
                }
                case "PROYECTO_ANULADO" -> {
                    this.tipoEventoLegible = "Proyecto Anulado";
                    this.icono = "ti ti-ban";
                    this.colorIcono = "#EF4444";
                }
                case "INVENTARIO_BAJO_STOCK" -> {
                    this.tipoEventoLegible = "Stock Bajo";
                    this.icono = "ti ti-box";
                    this.colorIcono = "#F59E0B";
                }
                case "PROYECTO_SIN_AVANCE" -> {
                    this.tipoEventoLegible = "Proyecto Sin Avance";
                    this.icono = "ti ti-clock";
                    this.colorIcono = "#F59E0B";
                }
                case "CLIENTE_CONSULTA" -> {
                    this.tipoEventoLegible = "Consulta de Cliente";
                    this.icono = "ti ti-user-check";
                    this.colorIcono = "#3B82F6";
                }
                case "RESUMEN_SEMANAL" -> {
                    this.tipoEventoLegible = "Resumen Semanal";
                    this.icono = "ti ti-file-text";
                    this.colorIcono = "#8B5CF6";
                }
                default -> {
                    this.tipoEventoLegible = tipoEvento;
                    this.icono = "ti ti-bell";
                    this.colorIcono = "#6B7280";
                }
            }
        }
    }


    public void marcarLeida() {
        this.leido = true;
        this.fechaLectura = LocalDateTime.now();
    }

    public void incrementarIntentos() {
        if (this.intentos == null) this.intentos = 0;
        this.intentos++;
    }

    public boolean puedeReintentar() {
        return this.intentos < 3 && !"Enviado".equals(this.estadoEnvio);
    }

    public boolean esParaUsuario() { return idUsuarioDestino != null; }
    public boolean esParaCliente() { return idClienteDestino != null; }

    public String getDestinatarioLegible() {
        if (nombreUsuarioDestino != null && !nombreUsuarioDestino.isEmpty()) return nombreUsuarioDestino;
        if (nombreClienteDestino != null && !nombreClienteDestino.isEmpty()) return nombreClienteDestino;
        if (idUsuarioDestino != null) return "Usuario ID: " + idUsuarioDestino;
        if (idClienteDestino != null) return "Cliente ID: " + idClienteDestino;
        return "Sin destinatario";
    }

    public String getFechaEnvioFormateada() {
        if (fechaEnvio == null) return "-";
        return fechaEnvio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getFechaLecturaFormateada() {
        if (fechaLectura == null) return "-";
        return fechaLectura.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getEstadoBadgeClass() {
        if (estadoEnvio == null) return "badge-secondary";
        return switch (estadoEnvio) {
            case "Pendiente" -> "badge-warning";
            case "Enviado" -> "badge-success";
            case "Error" -> "badge-danger";
            default -> "badge-secondary";
        };
    }

    public String getLeidoBadgeClass() {
        return leido != null && leido ? "badge-success" : "badge-warning";
    }

    public String getLeidoTexto() {
        return leido != null && leido ? "Leida" : "No leida";
    }

    public String getMensajeTruncado(int maxLength) {
        if (mensaje == null) return "";
        if (mensaje.length() <= maxLength) return mensaje;
        return mensaje.substring(0, maxLength) + "...";
    }

    public static Notificacion crearCambioEstadoProyecto(Integer idUsuarioDestino,
            String nombreProyecto, String estadoAnterior, String estadoNuevo, Integer idProyecto) {
        String asunto = "Cambio de estado - Proyecto " + nombreProyecto;
        String mensaje = "El proyecto '" + nombreProyecto +
                "' ha cambiado de estado de '" + estadoAnterior +
                "' a '" + estadoNuevo + "'.";
        Notificacion n = Notificacion.paraUsuario(idUsuarioDestino, asunto, mensaje, "CAMBIO_ESTADO_PROYECTO");
        n.setIdReferencia(idProyecto);
        n.setEntidadReferencia("Proyecto");
        return n;
    }

    public static Notificacion crearCotizacionAprobada(Integer idClienteDestino,
            String codigoCotizacion, String nombreCliente, Integer idCotizacion) {
        String asunto = "Cotizacion Aprobada - " + codigoCotizacion;
        String mensaje = "Estimado(a) " + nombreCliente +
                ", su cotizacion " + codigoCotizacion +
                " ha sido aprobada. Se ha generado un proyecto automaticamente.";
        Notificacion n = Notificacion.paraCliente(idClienteDestino, asunto, mensaje, "COTIZACION_APROBADA");
        n.setIdReferencia(idCotizacion);
        n.setEntidadReferencia("Cotizacion");
        return n;
    }

    public static Notificacion crearCotizacionAnulada(Integer idClienteDestino,
            String codigoCotizacion, String nombreCliente, String motivo, Integer idCotizacion) {
        String asunto = "Cotizacion Anulada - " + codigoCotizacion;
        String mensaje = "Estimado(a) " + nombreCliente +
                ", su cotizacion " + codigoCotizacion +
                " ha sido anulada. Motivo: " + (motivo != null ? motivo : "No especificado") +
                ". Si tiene dudas, contacte con su asesor.";
        Notificacion n = Notificacion.paraCliente(idClienteDestino, asunto, mensaje, "COTIZACION_ANULADA");
        n.setIdReferencia(idCotizacion);
        n.setEntidadReferencia("Cotizacion");
        return n;
    }

    public static Notificacion crearPagoValidado(Integer idClienteDestino,
            String nombreProyecto, String monto, Integer idPago) {
        String asunto = "Pago Validado - Proyecto " + nombreProyecto;
        String mensaje = "Su pago de " + monto +
                " para el proyecto '" + nombreProyecto +
                "' ha sido validado correctamente.";
        Notificacion n = Notificacion.paraCliente(idClienteDestino, asunto, mensaje, "PAGO_VALIDADO");
        n.setIdReferencia(idPago);
        n.setEntidadReferencia("Pago");
        return n;
    }

    public static Notificacion crearPagoRechazado(Integer idClienteDestino,
            String nombreProyecto, String monto, String motivo, Integer idPago) {
        String asunto = "Pago Rechazado - Proyecto " + nombreProyecto;
        String mensaje = "Su pago de " + monto +
                " para el proyecto '" + nombreProyecto +
                "' ha sido rechazado. Motivo: " + motivo;
        Notificacion n = Notificacion.paraCliente(idClienteDestino, asunto, mensaje, "PAGO_RECHAZADO");
        n.setIdReferencia(idPago);
        n.setEntidadReferencia("Pago");
        return n;
    }

    public static Notificacion crearNuevoDocumento(Integer idClienteDestino,
            String nombreProyecto, String tipoDocumento, Integer idDocumento) {
        String asunto = "Nuevo Documento - Proyecto " + nombreProyecto;
        String mensaje = "Se ha subido un nuevo " + tipoDocumento +
                " para el proyecto '" + nombreProyecto + "'.";
        Notificacion n = Notificacion.paraCliente(idClienteDestino, asunto, mensaje, "NUEVO_DOCUMENTO");
        n.setIdReferencia(idDocumento);
        n.setEntidadReferencia("DocumentoProyecto");
        return n;
    }

    public static Notificacion crearProyectoEntregado(Integer idClienteDestino,
            String nombreProyecto, Integer idProyecto) {
        String asunto = "Proyecto Entregado - " + nombreProyecto;
        String mensaje = "El proyecto '" + nombreProyecto +
                "' ha sido entregado. Agradecemos su confianza en STAMOS S.L.";
        Notificacion n = Notificacion.paraCliente(idClienteDestino, asunto, mensaje, "PROYECTO_ENTREGADO");
        n.setIdReferencia(idProyecto);
        n.setEntidadReferencia("Proyecto");
        return n;
    }

    public static Notificacion crearPagoVencido(Integer idClienteDestino,
            String nombreProyecto, String saldo, Integer idProyecto) {
        String asunto = "Pago Vencido - Proyecto " + nombreProyecto;
        String mensaje = "El pago para el proyecto '" + nombreProyecto +
                "' esta vencido. Saldo pendiente: " + saldo +
                ". Por favor, regularice su situacion.";
        Notificacion n = Notificacion.paraCliente(idClienteDestino, asunto, mensaje, "PAGO_VENCIDO");
        n.setIdReferencia(idProyecto);
        n.setEntidadReferencia("Proyecto");
        return n;
    }

    public static Notificacion crearProyectoAnulado(Integer idUsuarioDestino,
            String nombreProyecto, String motivo, Integer idProyecto) {
        String asunto = "Proyecto Anulado - " + nombreProyecto;
        String mensaje = "El proyecto '" + nombreProyecto +
                "' ha sido anulado. Motivo: " + (motivo != null ? motivo : "Sin motivo especificado");
        Notificacion n = Notificacion.paraUsuario(idUsuarioDestino, asunto, mensaje, "PROYECTO_ANULADO");
        n.setIdReferencia(idProyecto);
        n.setEntidadReferencia("Proyecto");
        return n;
    }

    public static Notificacion crearPagoRegistrado(Integer idUsuarioDestino,
            String nombreProyecto, String monto, String comprobante, Integer idPago) {
        String asunto = "Nuevo Pago Registrado - Proyecto " + nombreProyecto;
        String mensaje = "Se ha registrado un nuevo pago de " + monto +
                " para el proyecto '" + nombreProyecto +
                "'. Comprobante: " + comprobante +
                ". Pendiente de validacion.";
        Notificacion n = Notificacion.paraUsuario(idUsuarioDestino, asunto, mensaje, "PAGO_REGISTRADO");
        n.setIdReferencia(idPago);
        n.setEntidadReferencia("Pago");
        return n;
    }

    public static Notificacion crearProyectoSinAvance(Integer idClienteDestino,
            String nombreProyecto, Integer dias, Integer idProyecto) {
        String asunto = "Proyecto sin Avance - " + nombreProyecto;
        String mensaje = "El proyecto '" + nombreProyecto +
                "' no ha tenido avances en los ultimos " + dias + " dias." +
                " Contacte con su asesor para conocer el estado.";
        Notificacion n = Notificacion.paraCliente(idClienteDestino, asunto, mensaje, "PROYECTO_SIN_AVANCE");
        n.setIdReferencia(idProyecto);
        n.setEntidadReferencia("Proyecto");
        return n;
    }

    public static Notificacion crearStockBajo(Integer idUsuarioDestino,
            String detalles) {
        String asunto = "ALERTA: Stock bajo en inventario";
        String mensaje = "Se ha detectado stock bajo en los siguientes productos:\n\n" + detalles;
        Notificacion n = Notificacion.paraUsuario(idUsuarioDestino, asunto, mensaje, "INVENTARIO_BAJO_STOCK");
        return n;
    }

    public static Notificacion crearResumenSemanal(Integer idUsuarioDestino,
            String resumen) {
        String asunto = "Resumen Semanal - STAMOS S.L.";
        Notificacion n = Notificacion.paraUsuario(idUsuarioDestino, asunto, resumen, "RESUMEN_SEMANAL");
        return n;
    }

    public static Notificacion crearConsultaCliente(Integer idVendedorDestino,
            String nombreCliente, String nombreProyecto, Integer idProyecto) {
        String asunto = "Consulta de cliente - " + nombreProyecto;
        String mensaje = "El cliente " + nombreCliente + 
                " ha consultado el estado del proyecto '" + nombreProyecto + "'.";
        Notificacion n = Notificacion.paraUsuario(idVendedorDestino, asunto, mensaje, "CLIENTE_CONSULTA");
        n.setIdReferencia(idProyecto);
        n.setEntidadReferencia("Proyecto");
        return n;
    }

    public static Notificacion crearPagoVencidoAUsuario(Integer idUsuarioDestino,
            String nombreProyecto, String saldo, Integer idProyecto) {
        String asunto = "Pago Vencido - Proyecto " + nombreProyecto;
        String mensaje = "El pago para el proyecto '" + nombreProyecto +
                "' esta vencido. Saldo pendiente: " + saldo +
                ". Por favor, regularice su situacion.";
        Notificacion n = Notificacion.paraUsuario(idUsuarioDestino, asunto, mensaje, "PAGO_VENCIDO");
        n.setIdReferencia(idProyecto);
        n.setEntidadReferencia("Proyecto");
        return n;
    }


    @Override
    public String toString() {
        return "Notificacion{" +
                "idNotificacion=" + idNotificacion +
                ", asunto='" + asunto + '\'' +
                ", tipoEvento='" + tipoEvento + '\'' +
                ", estadoEnvio='" + estadoEnvio + '\'' +
                ", leido=" + leido +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notificacion that = (Notificacion) o;
        return idNotificacion != null && idNotificacion.equals(that.idNotificacion);
    }

    @Override
    public int hashCode() {
        return idNotificacion != null ? idNotificacion.hashCode() : 0;
    }
}