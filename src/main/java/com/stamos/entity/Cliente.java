package com.stamos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidad que representa un cliente (persona natural o empresa).
 * 
 * Reglas de negocio:
 * - RC01: Todo cliente debe registrarse antes de generar una cotización.
 * - RC02: Datos mínimos: nombre o razón social, teléfono y email. RUC opcional.
 * - RC03: Tipo de cliente: PERSONA_NATURAL o EMPRESA.
 * - RC04: No se elimina físicamente si tiene proyectos/cotizaciones, solo se deshabilita (activo = false).
 */
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre o razón social es obligatorio")
    @Column(name = "nombre_razon_social", nullable = false, length = 200)
    private String nombreRazonSocial;

    @NotBlank(message = "El tipo de persona es obligatorio")
    @Column(name = "tipo_persona", nullable = false, length = 20)
    private String tipoPersona; // PERSONA_NATURAL o EMPRESA

    @NotBlank(message = "El teléfono es obligatorio")
    @Column(nullable = false, length = 20)
    private String telefono;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(length = 20)
    private String ruc; // Opcional, solo para empresas

    @Column(nullable = false)
    private boolean activo = true; // Para deshabilitación lógica (RC04)

    // Relaciones
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cotizacion> cotizaciones = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Proyecto> proyectos = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    private List<Resena> resenas = new ArrayList<>();

    // Relación con usuario (para login en portal público)
    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Constructores
    public Cliente() {
    }

    public Cliente(String nombreRazonSocial, String tipoPersona, String telefono, String email, String ruc) {
        this.nombreRazonSocial = nombreRazonSocial;
        this.tipoPersona = tipoPersona;
        this.telefono = telefono;
        this.email = email;
        this.ruc = ruc;
        this.activo = true;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreRazonSocial() {
        return nombreRazonSocial;
    }

    public void setNombreRazonSocial(String nombreRazonSocial) {
        this.nombreRazonSocial = nombreRazonSocial;
    }

    public String getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(String tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<Cotizacion> getCotizaciones() {
        return cotizaciones;
    }

    public void setCotizaciones(List<Cotizacion> cotizaciones) {
        this.cotizaciones = cotizaciones;
    }

    public List<Proyecto> getProyectos() {
        return proyectos;
    }

    public void setProyectos(List<Proyecto> proyectos) {
        this.proyectos = proyectos;
    }

    public List<Resena> getResenas() {
        return resenas;
    }

    public void setResenas(List<Resena> resenas) {
        this.resenas = resenas;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    // Métodos de conveniencia
    public void addCotizacion(Cotizacion cotizacion) {
        cotizaciones.add(cotizacion);
        cotizacion.setCliente(this);
    }

    public void addProyecto(Proyecto proyecto) {
        proyectos.add(proyecto);
        proyecto.setCliente(this);
    }

    // equals, hashCode y toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(id, cliente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nombreRazonSocial='" + nombreRazonSocial + '\'' +
                ", tipoPersona='" + tipoPersona + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", ruc='" + ruc + '\'' +
                ", activo=" + activo +
                '}';
    }
}