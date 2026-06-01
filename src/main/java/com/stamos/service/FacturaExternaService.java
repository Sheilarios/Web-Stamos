package com.stamos.service;

import com.stamos.entity.FacturaExterna;
import com.stamos.repository.FacturaExternaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FacturaExternaService {

    @Autowired
    private FacturaExternaRepository repository;

    public List<FacturaExterna> findAll() {
        return repository.findAll();
    }

    public FacturaExterna findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public FacturaExterna save(FacturaExterna factura) {
        return repository.save(factura);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}