package com.stamos.repository;

import com.stamos.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Usuario.
 * Proporciona métodos CRUD básicos y consultas personalizadas.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su email.
     *
     * @param email Email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica si ya existe un usuario con el email dado.
     *
     * @param email Email a verificar
     * @return true si existe
     */
    boolean existsByEmail(String email);

    /**
     * Obtiene todos los usuarios que son empleados (esEmpleado = true).
     *
     * @return Lista de empleados
     */
    List<Usuario> findByEsEmpleadoTrue();

    /**
     * Busca un usuario por su token de restablecimiento de contraseña.
     *
     * @param token Token de restablecimiento
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> findByResetToken(String token);
}