package com.stamos.service;

import com.stamos.entity.Resena;
import com.stamos.repository.ResenaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para la gestión de reseñas y calificaciones.
 * 
 * Reglas de negocio:
 * - Solo clientes con proyecto entregado pueden crear reseña (validado en controller).
 * - Las reseñas se crean con estado PENDIENTE (moderación).
 * - Solo administrador puede aprobar, rechazar o eliminar reseñas.
 * - Las reseñas aprobadas son visibles en la web pública.
 */
@Service
@Transactional
public class ResenaService {

    @Autowired
    private ResenaRepository resenaRepository;

    /**
     * Obtiene todas las reseñas (para administración).
     *
     * @return Lista de todas las reseñas
     */
    public List<Resena> findAll() {
        return resenaRepository.findAll();
    }

    /**
     * Obtiene reseñas por estado (PENDIENTE, APROBADA, RECHAZADA).
     *
     * @param estado Estado de la reseña
     * @return Lista de reseñas con ese estado
     */
    public List<Resena> findByEstado(String estado) {
        return resenaRepository.findByEstado(estado);
    }

    /**
     * Obtiene una reseña por su ID.
     *
     * @param id ID de la reseña
     * @return Resena o null si no existe
     */
    public Resena findById(Long id) {
        return resenaRepository.findById(id).orElse(null);
    }

    /**
     * Guarda o actualiza una reseña.
     *
     * @param resena Reseña a persistir
     * @return Reseña persistida
     */
    public Resena save(Resena resena) {
        return resenaRepository.save(resena);
    }

    /**
     * Elimina una reseña permanentemente (solo administrador).
     *
     * @param id ID de la reseña
     */
    public void delete(Long id) {
        resenaRepository.deleteById(id);
    }

    /**
     * Verifica si ya existe una reseña para un proyecto dado.
     *
     * @param proyectoId ID del proyecto
     * @return true si ya existe una reseña (en cualquier estado)
     */
    public boolean existsByProyectoId(Long proyectoId) {
        return resenaRepository.existsByProyectoId(proyectoId);
    }
}