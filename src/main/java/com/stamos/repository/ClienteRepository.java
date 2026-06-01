package com.stamos.repository;

import com.stamos.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Cliente.
 * Proporciona métodos CRUD básicos y consultas personalizadas.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Verifica si ya existe un cliente con el correo electrónico dado.
     *
     * @param email Correo electrónico
     * @return true si existe
     */
    boolean existsByEmail(String email);

    /**
     * Verifica si ya existe un cliente con el RUC dado.
     *
     * @param ruc RUC del cliente
     * @return true si existe
     */
    boolean existsByRuc(String ruc);

    /**
     * Obtiene todos los clientes activos (no deshabilitados).
     *
     * @return Lista de clientes activos
     */
    List<Cliente> findByActivoTrue();
    Optional<Cliente> findByEmail(String email);


    /**
     * Búsqueda de clientes por coincidencia parcial en nombre/razón social, RUC o teléfono.
     * La búsqueda es insensible a mayúsculas/minúsculas.
     *
     * @param term Término de búsqueda envuelto en % (ej. "%juan%")
     * @return Lista de clientes que coinciden
     */
    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nombreRazonSocial) LIKE :term OR LOWER(c.ruc) LIKE :term OR LOWER(c.telefono) LIKE :term")
    List<Cliente> searchByNombreOrRucOrTelefono(@Param("term") String term);
}