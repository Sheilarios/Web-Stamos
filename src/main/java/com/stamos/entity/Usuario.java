package com.stamos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa un usuario del sistema.
 * Puede ser un empleado (con rol específico) o un cliente (rol CLIENTE).
 * 
 * Reglas de negocio:
 * - Las contraseñas se almacenan cifradas (BCrypt).
 * - Solo el administrador puede crear/modificar empleados.
 * - Los clientes reciben credenciales generadas por el administrador (no auto-registro).
 * - Se soporta restablecimiento de contraseña mediante token.
 */
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false)
    private String password; // Almacenada cifrada con BCrypt

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El rol es obligatorio")
    @Column(nullable = false, length = 30)
    private String rol; // ADMINISTRADOR, COMERCIAL, DISEÑADOR, PRODUCCION, SOPORTE_TECNICO, CLIENTE

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "es_empleado", nullable = false)
    private boolean esEmpleado = false; // true para empleados, false para clientes

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    // Relación con cliente (solo si es cliente)
    @OneToOne(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Cliente cliente;

    // Constructores
    public Usuario() {
    }

    public Usuario(String email, String password, String nombre, String rol, boolean esEmpleado) {
        this.email = email;
        this.password = password;
        this.nombre = nombre;
        this.rol = rol;
        this.esEmpleado = esEmpleado;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public boolean isEsEmpleado() {
        return esEmpleado;
    }

    public void setEsEmpleado(boolean esEmpleado) {
        this.esEmpleado = esEmpleado;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public LocalDateTime getResetTokenExpiry() {
        return resetTokenExpiry;
    }

    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    // Método auxiliar para verificar roles
    public boolean hasRole(String roleName) {
        return this.rol != null && this.rol.equals(roleName);
    }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", nombre='" + nombre + '\'' +
                ", rol='" + rol + '\'' +
                ", activo=" + activo +
                ", esEmpleado=" + esEmpleado +
                '}';
    }
}