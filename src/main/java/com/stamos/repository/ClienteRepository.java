package com.stamos.repository;

import com.stamos.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByEmail(String email);
    boolean existsByRuc(String ruc);
    List<Cliente> findByActivoTrue();
    Optional<Cliente> findByEmail(String email);
    List<Cliente> findByActivoTrueOrderByNombreRazonSocialAsc();
    long countByActivoTrue();
    Optional<Cliente> findByRuc(String ruc);

    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nombreRazonSocial) LIKE :term OR LOWER(c.ruc) LIKE :term OR LOWER(c.telefono) LIKE :term")
    List<Cliente> searchByNombreOrRucOrTelefono(@Param("term") String term);

    @Query("SELECT c FROM Cliente c WHERE c.activo = true AND (LOWER(c.nombreRazonSocial) LIKE :term OR LOWER(c.ruc) LIKE :term)")
    List<Cliente> searchActiveByNombreOrRuc(@Param("term") String term);

    List<Cliente> findByNombreRazonSocialContainingIgnoreCase(String nombre);

    @Query("SELECT DISTINCT c FROM Cliente c JOIN c.proyectos p")
    List<Cliente> findClientesWithProyectos();

    @Query("SELECT DISTINCT c FROM Cliente c JOIN c.cotizaciones cot")
    List<Cliente> findClientesWithCotizaciones();

    @Query(value = "SELECT * FROM cliente ORDER BY id DESC LIMIT :limit", nativeQuery = true)
    List<Cliente> findTopNByOrderByIdDesc(@Param("limit") int limit);
}