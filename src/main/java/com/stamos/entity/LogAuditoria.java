package com.stamos.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "log_auditoria")
public class LogAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "accion", nullable = false, length = 50)
    private String accion; 

    @Column(name = "entidad", nullable = false, length = 50)
    private String entidad; 

    @Column(name = "entidad_id")
    private Long entidadId; 

    @Column(name = "valor_anterior", columnDefinition = "TEXT")
    private String valorAnterior; 

    @Column(name = "valor_nuevo", columnDefinition = "TEXT")
    private String valorNuevo; 

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario; 

    @Column(name = "usuario_email", length = 100)
    private String usuarioEmail; 

    public LogAuditoria() {
    }

    public LogAuditoria(String accion, String entidad, Long entidadId, String valorAnterior,
                        String valorNuevo, LocalDateTime fechaHora, Usuario usuario, String usuarioEmail) {
        this.accion = accion;
        this.entidad = entidad;
        this.entidadId = entidadId;
        this.valorAnterior = valorAnterior;
        this.valorNuevo = valorNuevo;
        this.fechaHora = fechaHora;
        this.usuario = usuario;
        this.usuarioEmail = usuarioEmail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public Long getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(Long entidadId) {
        this.entidadId = entidadId;
    }

    public String getValorAnterior() {
        return valorAnterior;
    }

    public void setValorAnterior(String valorAnterior) {
        this.valorAnterior = valorAnterior;
    }

    public String getValorNuevo() {
        return valorNuevo;
    }

    public void setValorNuevo(String valorNuevo) {
        this.valorNuevo = valorNuevo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getUsuarioEmail() {
        return usuarioEmail;
    }

    public void setUsuarioEmail(String usuarioEmail) {
        this.usuarioEmail = usuarioEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogAuditoria that = (LogAuditoria) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "LogAuditoria{" +
                "id=" + id +
                ", accion='" + accion + '\'' +
                ", entidad='" + entidad + '\'' +
                ", entidadId=" + entidadId +
                ", fechaHora=" + fechaHora +
                ", usuarioEmail='" + usuarioEmail + '\'' +
                '}';
    }
}