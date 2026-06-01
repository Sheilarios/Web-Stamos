package com.stamos.service;

import com.stamos.dto.ClienteDTO;
import com.stamos.entity.Cliente;
import com.stamos.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de clientes.
 * Implementa las reglas de negocio:
 * - Los clientes no se eliminan físicamente, solo se deshabilitan.
 * - Búsqueda por nombre, RUC o teléfono.
 * - Validación de unicidad de email y RUC.
 * - Conversión a DTO para autocompletado.
 */
@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Obtiene todos los clientes (activos y deshabilitados).
     *
     * @return Lista de todos los clientes
     */
    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    /**
     * Obtiene un cliente por su ID.
     *
     * @param id ID del cliente
     * @return Cliente o null si no existe
     */
    public Cliente findById(Long id) {
        return clienteRepository.findById(id).orElse(null);
    }

    /**
     * Guarda o actualiza un cliente.
     *
     * @param cliente Cliente a persistir
     * @return Cliente persistido
     */
    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    /**
     * Búsqueda de clientes por nombre/razón social, RUC o teléfono (coincidencia parcial).
     *
     * @param searchTerm Término de búsqueda
     * @return Lista de clientes que coinciden
     */
    public List<Cliente> search(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAll();
        }
        String term = "%" + searchTerm.toLowerCase() + "%";
        return clienteRepository.searchByNombreOrRucOrTelefono(term);
    }

    /**
     * Verifica si existe un cliente con el mismo email.
     *
     * @param email Email a verificar
     * @return true si ya existe
     */
    public boolean existsByEmail(String email) {
        return clienteRepository.existsByEmail(email);
    }

    /**
     * Verifica si existe un cliente con el mismo RUC (solo para empresas).
     *
     * @param ruc RUC a verificar
     * @return true si ya existe
     */
    public boolean existsByRuc(String ruc) {
        if (ruc == null || ruc.isEmpty()) {
            return false;
        }
        return clienteRepository.existsByRuc(ruc);
    }

    /**
     * Deshabilita un cliente (cambia activo a false).
     * No elimina físicamente.
     *
     * @param id ID del cliente
     */
    public void disable(Long id) {
        Cliente cliente = findById(id);
        if (cliente != null) {
            cliente.setActivo(false);
            clienteRepository.save(cliente);
        }
    }

    /**
     * Habilita un cliente previamente deshabilitado.
     *
     * @param id ID del cliente
     */
    public void enable(Long id) {
        Cliente cliente = findById(id);
        if (cliente != null) {
            cliente.setActivo(true);
            clienteRepository.save(cliente);
        }
    }

    /**
     * Busca clientes y los devuelve como DTOs (id, nombre, ruc) para autocompletado.
     *
     * @param term Término de búsqueda
     * @return Lista de ClienteDTO
     */
    public List<ClienteDTO> searchDTO(String term) {
        List<Cliente> clientes = search(term);
        return clientes.stream()
                .map(c -> new ClienteDTO(c.getId(), c.getNombreRazonSocial(), c.getRuc()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene solo los clientes activos (útiles para formularios de nueva cotización).
     *
     * @return Lista de clientes activos
     */
    public List<Cliente> findActive() {
        return clienteRepository.findByActivoTrue();
    }
    public Cliente findByEmail(String email) {
    return clienteRepository.findByEmail(email).orElse(null);
    }
}