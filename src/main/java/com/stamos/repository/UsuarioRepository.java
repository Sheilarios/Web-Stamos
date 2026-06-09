package com.stamos.repository;

import com.stamos.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByResetToken(String token);
    List<Usuario> findByEsEmpleadoTrue();
    boolean existsByEmail(String email);
    long countByActivoTrue();
}