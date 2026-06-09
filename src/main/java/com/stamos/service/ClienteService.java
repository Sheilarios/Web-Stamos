package com.stamos.service;
import com.stamos.dto.ClienteDTO;
import com.stamos.entity.Cliente;
import com.stamos.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Cliente findById(Long id) {
        return clienteRepository.findById(id).orElse(null);
    }

    public Cliente findByEmail(String email) {
        return clienteRepository.findByEmail(email).orElse(null);
    }

    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public List<Cliente> search(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAll();
        }
        String term = "%" + searchTerm.toLowerCase() + "%";
        return clienteRepository.searchByNombreOrRucOrTelefono(term);
    }

    public boolean existsByEmail(String email) {
        return clienteRepository.existsByEmail(email);
    }

    public boolean existsByRuc(String ruc) {
        if (ruc == null || ruc.isEmpty()) {
            return false;
        }
        return clienteRepository.existsByRuc(ruc);
    }

    public void disable(Long id) {
        Cliente cliente = findById(id);
        if (cliente != null) {
            cliente.setActivo(false);
            clienteRepository.save(cliente);
        }
    }

    public void enable(Long id) {
        Cliente cliente = findById(id);
        if (cliente != null) {
            cliente.setActivo(true);
            clienteRepository.save(cliente);
        }
    }

    public List<ClienteDTO> searchDTO(String term) {
        List<Cliente> clientes = search(term);
        return clientes.stream()
                .map(c -> new ClienteDTO(c.getId(), c.getNombreRazonSocial(), c.getRuc()))
                .collect(Collectors.toList());
    }

    public List<Cliente> findActive() {
        return clienteRepository.findByActivoTrue();
    }

    public long countActive() {
        return clienteRepository.countByActivoTrue();
    }

    public long countAll() {
        return clienteRepository.count();
    }

    public List<Cliente> findActiveOrderedByName() {
        return clienteRepository.findByActivoTrueOrderByNombreRazonSocialAsc();
    }

    public boolean hasProyectos(Long id) {
        Cliente cliente = findById(id);
        if (cliente == null) return false;
        return cliente.getProyectos() != null && !cliente.getProyectos().isEmpty();
    }

    public boolean hasCotizaciones(Long id) {
        Cliente cliente = findById(id);
        if (cliente == null) return false;
        return cliente.getCotizaciones() != null && !cliente.getCotizaciones().isEmpty();
    }

    public List<Cliente> findTop5ByOrderByIdDesc() {
        return clienteRepository.findTopNByOrderByIdDesc(5);
    }
}