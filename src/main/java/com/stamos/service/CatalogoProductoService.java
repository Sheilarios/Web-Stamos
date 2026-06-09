package com.stamos.service;
import com.stamos.entity.CatalogoProducto;
import com.stamos.repository.CatalogoProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class CatalogoProductoService {

    @Autowired
    private CatalogoProductoRepository catalogoProductoRepository;
    public List<CatalogoProducto> findAll() {
        return catalogoProductoRepository.findAll();
    }
    public CatalogoProducto findById(Long id) {
        return catalogoProductoRepository.findById(id).orElse(null);
    }
    public CatalogoProducto save(CatalogoProducto producto) {
        return catalogoProductoRepository.save(producto);
    }
    public void delete(Long id) {
        catalogoProductoRepository.deleteById(id);
    }
    public List<CatalogoProducto> findByNombreContaining(String nombre) {
        return catalogoProductoRepository.findByNombreContainingIgnoreCase(nombre);
    }
}