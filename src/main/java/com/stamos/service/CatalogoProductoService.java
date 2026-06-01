package com.stamos.service;

import com.stamos.entity.CatalogoProducto;
import com.stamos.repository.CatalogoProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para la gestión del catálogo de productos y servicios.
 * Proporciona operaciones CRUD básicas y búsquedas.
 */
@Service
@Transactional
public class CatalogoProductoService {

    @Autowired
    private CatalogoProductoRepository catalogoProductoRepository;

    /**
     * Obtiene todos los productos/servicios del catálogo.
     *
     * @return Lista de todos los productos
     */
    public List<CatalogoProducto> findAll() {
        return catalogoProductoRepository.findAll();
    }

    /**
     * Obtiene un producto/servicio por su ID.
     *
     * @param id ID del producto
     * @return Producto encontrado o null
     */
    public CatalogoProducto findById(Long id) {
        return catalogoProductoRepository.findById(id).orElse(null);
    }

    /**
     * Guarda o actualiza un producto/servicio en el catálogo.
     *
     * @param producto Producto a persistir
     * @return Producto persistido
     */
    public CatalogoProducto save(CatalogoProducto producto) {
        return catalogoProductoRepository.save(producto);
    }

    /**
     * Elimina un producto/servicio del catálogo.
     *
     * @param id ID del producto
     */
    public void delete(Long id) {
        catalogoProductoRepository.deleteById(id);
    }

    /**
     * Busca productos/servicios cuyo nombre contenga el texto (ignorando mayúsculas).
     *
     * @param nombre Texto a buscar
     * @return Lista de productos coincidentes
     */
    public List<CatalogoProducto> findByNombreContaining(String nombre) {
        return catalogoProductoRepository.findByNombreContainingIgnoreCase(nombre);
    }
}