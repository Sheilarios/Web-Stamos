package com.stamos.service;

import com.stamos.model.CambioEstadoProyecto;
import com.stamos.model.EstadoProyecto;
import com.stamos.model.Proyecto;
import com.stamos.model.Usuario;
import com.stamos.repository.CambioEstadoProyectoRepository;
import com.stamos.repository.EstadoProyectoRepository;
import com.stamos.repository.ProyectoRepository;
import com.stamos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para la gestión de cambios de estado de proyectos.
 * 
 * <p>Este servicio proporciona métodos para registrar y consultar el historial
 * de cambios de estado de los proyectos en STAMOS S.L.</p>
 * 
 * <p>Implementa los requerimientos:</p>
 * <ul>
 *   <li>RN-07: Los proyectos deben seguir la secuencia de estados</li>
 *   <li>RN-08: Proyectos en "Entregado" o "Anulado" no pueden retroceder</li>
 *   <li>RN-09: Registrar fecha y responsable de cada cambio</li>
 *   <li>RF-15: Registrar fecha, responsable y cambio de estado</li>
 * </ul>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
@Service
public class CambioEstadoProyectoService {

    @Autowired
    private CambioEstadoProyectoRepository cambioEstadoProyectoRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private EstadoProyectoRepository estadoProyectoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ============================================
    // REGISTRO DE CAMBIOS DE ESTADO
    // ============================================

    /**
     * Registra un cambio de estado de un proyecto.
     * 
     * @param idProyecto ID del proyecto
     * @param idEstadoAnterior ID del estado anterior
     * @param idEstadoNuevo ID del estado nuevo
     * @param idUsuarioResponsable ID del usuario responsable
     * @param comentario Comentario sobre el cambio
     * @return Cambio de estado registrado
     * @throws RuntimeException si el proyecto o los estados no existen
     */
    @Transactional
    public CambioEstadoProyecto registrarCambioEstado(Integer idProyecto, Integer idEstadoAnterior,
                                                      Integer idEstadoNuevo, Integer idUsuarioResponsable,
                                                      String comentario) {
        // Verificar que el proyecto existe
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + idProyecto));

        // Verificar que los estados existen
        EstadoProyecto estadoAnterior = estadoProyectoRepository.findById(idEstadoAnterior)
                .orElseThrow(() -> new RuntimeException("Estado anterior no encontrado con ID: " + idEstadoAnterior));

        EstadoProyecto estadoNuevo = estadoProyectoRepository.findById(idEstadoNuevo)
                .orElseThrow(() -> new RuntimeException("Estado nuevo no encontrado con ID: " + idEstadoNuevo));

        // Verificar que el usuario existe
        Usuario usuario = usuarioRepository.findById(idUsuarioResponsable)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuarioResponsable));

        // Verificar que el proyecto no esté en estado terminal (RN-08)
        if (estadoAnterior.esEstadoTerminal()) {
            throw new RuntimeException("No se puede cambiar el estado de un proyecto " + 
                                      estadoAnterior.getNombreEstado().toLowerCase());
        }

        // Verificar que el nuevo estado sea diferente al anterior
        if (idEstadoAnterior.equals(idEstadoNuevo)) {
            throw new RuntimeException("El nuevo estado es igual al estado actual");
        }

        // Crear el registro de cambio
        CambioEstadoProyecto cambio = new CambioEstadoProyecto();
        cambio.setIdProyecto(idProyecto);
        cambio.setIdEstadoAnterior(idEstadoAnterior);
        cambio.setIdEstadoNuevo(idEstadoNuevo);
        cambio.setIdUsuarioResponsable(idUsuarioResponsable);
        cambio.setFechaCambio(LocalDateTime.now());
        cambio.setComentario(comentario);
        cambio.setEstadoAnteriorNombre(estadoAnterior.getNombreEstado());
        cambio.setEstadoNuevoNombre(estadoNuevo.getNombreEstado());
        cambio.setResponsableNombre(usuario.getNombreCompleto());
        cambio.setResponsableEmail(usuario.getEmail());

        // Guardar el cambio
        return cambioEstadoProyectoRepository.save(cambio);
    }

    /**
     * Registra un cambio de estado de un proyecto (versión simplificada).
     * 
     * @param idProyecto ID del proyecto
     * @param idEstadoAnterior ID del estado anterior
     * @param idEstadoNuevo ID del estado nuevo
     * @param idUsuarioResponsable ID del usuario responsable
     * @return Cambio de estado registrado
     */
    @Transactional
    public CambioEstadoProyecto registrarCambioEstado(Integer idProyecto, Integer idEstadoAnterior,
                                                      Integer idEstadoNuevo, Integer idUsuarioResponsable) {
        return registrarCambioEstado(idProyecto, idEstadoAnterior, idEstadoNuevo, 
                                    idUsuarioResponsable, null);
    }

    /**
     * Registra el inicio de un proyecto (cambio de Aprobado a En Diseño).
     * 
     * @param idProyecto ID del proyecto
     * @param idUsuarioResponsable ID del usuario responsable
     * @param comentario Comentario sobre el inicio
     * @return Cambio de estado registrado
     */
    @Transactional
    public CambioEstadoProyecto registrarInicioProyecto(Integer idProyecto, Integer idUsuarioResponsable,
                                                        String comentario) {
        // ID de estado "Aprobado" = 1, "En Diseño" = 2
        return registrarCambioEstado(idProyecto, 1, 2, idUsuarioResponsable, comentario);
    }

    /**
     * Registra el avance de un proyecto al siguiente estado.
     * 
     * @param idProyecto ID del proyecto
     * @param idEstadoNuevo ID del nuevo estado
     * @param idUsuarioResponsable ID del usuario responsable
     * @param comentario Comentario sobre el avance
     * @return Cambio de estado registrado
     */
    @Transactional
    public CambioEstadoProyecto registrarAvanceEstado(Integer idProyecto, Integer idEstadoNuevo,
                                                      Integer idUsuarioResponsable, String comentario) {
        // Obtener el estado actual del proyecto
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + idProyecto));
        
        Integer idEstadoActual = proyecto.getIdEstadoActual();
        
        // Verificar que el nuevo estado sea posterior al actual
        EstadoProyecto estadoActual = estadoProyectoRepository.findById(idEstadoActual)
                .orElseThrow(() -> new RuntimeException("Estado actual no encontrado"));
        
        EstadoProyecto estadoNuevo = estadoProyectoRepository.findById(idEstadoNuevo)
                .orElseThrow(() -> new RuntimeException("Estado nuevo no encontrado"));
        
        if (estadoNuevo.getOrdenFlujo() <= estadoActual.getOrdenFlujo()) {
            throw new RuntimeException("El nuevo estado debe ser posterior al estado actual");
        }
        
        return registrarCambioEstado(idProyecto, idEstadoActual, idEstadoNuevo, 
                                    idUsuarioResponsable, comentario);
    }

    /**
     * Registra la anulación de un proyecto.
     * 
     * @param idProyecto ID del proyecto
     * @param idUsuarioResponsable ID del usuario responsable
     * @param motivo Motivo de la anulación
     * @return Cambio de estado registrado
     */
    @Transactional
    public CambioEstadoProyecto registrarAnulacionProyecto(Integer idProyecto, Integer idUsuarioResponsable,
                                                           String motivo) {
        // Obtener el estado actual del proyecto
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + idProyecto));
        
        Integer idEstadoActual = proyecto.getIdEstadoActual();
        
        // Verificar que el proyecto no esté ya anulado o entregado
        EstadoProyecto estadoActual = estadoProyectoRepository.findById(idEstadoActual)
                .orElseThrow(() -> new RuntimeException("Estado actual no encontrado"));
        
        if (estadoActual.esEstadoTerminal()) {
            throw new RuntimeException("No se puede anular un proyecto que ya está " + 
                                      estadoActual.getNombreEstado().toLowerCase());
        }
        
        // ID de estado "Anulado" = 6
        return registrarCambioEstado(idProyecto, idEstadoActual, 6, 
                                    idUsuarioResponsable, "Anulación: " + (motivo != null ? motivo : "Sin motivo"));
    }

    /**
     * Registra la entrega de un proyecto.
     * 
     * @param idProyecto ID del proyecto
     * @param idUsuarioResponsable ID del usuario responsable
     * @param comentario Comentario sobre la entrega
     * @return Cambio de estado registrado
     */
    @Transactional
    public CambioEstadoProyecto registrarEntregaProyecto(Integer idProyecto, Integer idUsuarioResponsable,
                                                         String comentario) {
        // Obtener el estado actual del proyecto
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + idProyecto));
        
        Integer idEstadoActual = proyecto.getIdEstadoActual();
        
        // Verificar que el proyecto esté en estado Instalación/Montaje o En Producción
        EstadoProyecto estadoActual = estadoProyectoRepository.findById(idEstadoActual)
                .orElseThrow(() -> new RuntimeException("Estado actual no encontrado"));
        
        if (!estadoActual.esInstalacionMontaje() && !estadoActual.esEnProduccion()) {
            throw new RuntimeException("Para entregar un proyecto debe estar en estado " +
                                      "Instalación/Montaje o En Producción");
        }
        
        // ID de estado "Entregado" = 5
        return registrarCambioEstado(idProyecto, idEstadoActual, 5, 
                                    idUsuarioResponsable, "Entrega: " + (comentario != null ? comentario : "Proyecto entregado"));
    }

    // ============================================
    // CONSULTAS DE CAMBIOS DE ESTADO
    // ============================================

    /**
     * Busca todos los cambios de estado de un proyecto.
     * 
     * @param idProyecto ID del proyecto
     * @return Lista de cambios de estado
     */
    public List<CambioEstadoProyecto> findByProyecto(Integer idProyecto) {
        List<CambioEstadoProyecto> cambios = cambioEstadoProyectoRepository.findByIdProyecto(idProyecto);
        
        // Enriquecer con información adicional
        for (CambioEstadoProyecto cambio : cambios) {
            enriquecerCambio(cambio);
        }
        
        return cambios;
    }

    /**
     * Busca cambios de estado de un proyecto con paginación.
     * 
     * @param idProyecto ID del proyecto
     * @param pageable Configuración de paginación
     * @return Página de cambios de estado
     */
    public Page<CambioEstadoProyecto> findByProyecto(Integer idProyecto, Pageable pageable) {
        Page<CambioEstadoProyecto> cambios = cambioEstadoProyectoRepository.findByIdProyecto(idProyecto, pageable);
        
        // Enriquecer con información adicional
        for (CambioEstadoProyecto cambio : cambios.getContent()) {
            enriquecerCambio(cambio);
        }
        
        return cambios;
    }

    /**
     * Busca el último cambio de estado de un proyecto.
     * 
     * @param idProyecto ID del proyecto
     * @return Último cambio de estado
     */
    public CambioEstadoProyecto findLastByProyecto(Integer idProyecto) {
        CambioEstadoProyecto cambio = cambioEstadoProyectoRepository.findLastByProyecto(idProyecto);
        if (cambio != null) {
            enriquecerCambio(cambio);
        }
        return cambio;
    }

    /**
     * Busca cambios de estado por usuario responsable.
     * 
     * @param idUsuarioResponsable ID del usuario responsable
     * @param pageable Configuración de paginación
     * @return Página de cambios de estado
     */
    public Page<CambioEstadoProyecto> findByUsuarioResponsable(Integer idUsuarioResponsable, Pageable pageable) {
        Page<CambioEstadoProyecto> cambios = cambioEstadoProyectoRepository.findByIdUsuarioResponsable(
                idUsuarioResponsable, pageable);
        
        for (CambioEstadoProyecto cambio : cambios.getContent()) {
            enriquecerCambio(cambio);
        }
        
        return cambios;
    }

    /**
     * Busca cambios de estado por estado nuevo.
     * 
     * @param idEstadoNuevo ID del estado nuevo
     * @param pageable Configuración de paginación
     * @return Página de cambios de estado
     */
    public Page<CambioEstadoProyecto> findByEstadoNuevo(Integer idEstadoNuevo, Pageable pageable) {
        Page<CambioEstadoProyecto> cambios = cambioEstadoProyectoRepository.findByIdEstadoNuevo(idEstadoNuevo, pageable);
        
        for (CambioEstadoProyecto cambio : cambios.getContent()) {
            enriquecerCambio(cambio);
        }
        
        return cambios;
    }

    /**
     * Busca cambios de estado por rango de fechas.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param pageable Configuración de paginación
     * @return Página de cambios de estado
     */
    public Page<CambioEstadoProyecto> findByFechaCambioBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin,
                                                               Pageable pageable) {
        Page<CambioEstadoProyecto> cambios = cambioEstadoProyectoRepository.findByFechaCambioBetween(
                fechaInicio, fechaFin, pageable);
        
        for (CambioEstadoProyecto cambio : cambios.getContent()) {
            enriquecerCambio(cambio);
        }
        
        return cambios;
    }

    /**
     * Busca cambios de estado que sean avances.
     * 
     * @param pageable Configuración de paginación
     * @return Página de cambios de estado
     */
    public Page<CambioEstadoProyecto> findAvances(Pageable pageable) {
        Page<CambioEstadoProyecto> cambios = cambioEstadoProyectoRepository.findAvances(pageable);
        
        for (CambioEstadoProyecto cambio : cambios.getContent()) {
            enriquecerCambio(cambio);
        }
        
        return cambios;
    }

    /**
     * Busca cambios de estado que sean retrocesos.
     * 
     * @param pageable Configuración de paginación
     * @return Página de cambios de estado
     */
    public Page<CambioEstadoProyecto> findRetrocesos(Pageable pageable) {
        Page<CambioEstadoProyecto> cambios = cambioEstadoProyectoRepository.findRetrocesos(pageable);
        
        for (CambioEstadoProyecto cambio : cambios.getContent()) {
            enriquecerCambio(cambio);
        }
        
        return cambios;
    }

    /**
     * Busca avances de un proyecto.
     * 
     * @param idProyecto ID del proyecto
     * @return Lista de cambios de estado
     */
    public List<CambioEstadoProyecto> findAvancesByProyecto(Integer idProyecto) {
        List<CambioEstadoProyecto> cambios = cambioEstadoProyectoRepository.findAvancesByProyecto(idProyecto);
        
        for (CambioEstadoProyecto cambio : cambios) {
            enriquecerCambio(cambio);
        }
        
        return cambios;
    }

    /**
     * Busca retrocesos de un proyecto.
     * 
     * @param idProyecto ID del proyecto
     * @return Lista de cambios de estado
     */
    public List<CambioEstadoProyecto> findRetrocesosByProyecto(Integer idProyecto) {
        List<CambioEstadoProyecto> cambios = cambioEstadoProyectoRepository.findRetrocesosByProyecto(idProyecto);
        
        for (CambioEstadoProyecto cambio : cambios) {
            enriquecerCambio(cambio);
        }
        
        return cambios;
    }

    /**
     * Busca cambios de estado con comentario.
     * 
     * @param pageable Configuración de paginación
     * @return Página de cambios de estado
     */
    public Page<CambioEstadoProyecto> findWithComentario(Pageable pageable) {
        Page<CambioEstadoProyecto> cambios = cambioEstadoProyectoRepository.findWithComentario(pageable);
        
        for (CambioEstadoProyecto cambio : cambios.getContent()) {
            enriquecerCambio(cambio);
        }
        
        return cambios;
    }

    /**
     * Busca cambios de estado con comentario de un proyecto.
     * 
     * @param idProyecto ID del proyecto
     * @return Lista de cambios de estado
     */
    public List<CambioEstadoProyecto> findWithComentarioByProyecto(Integer idProyecto) {
        List<CambioEstadoProyecto> cambios = cambioEstadoProyectoRepository.findWithComentarioByProyecto(idProyecto);
        
        for (CambioEstadoProyecto cambio : cambios) {
            enriquecerCambio(cambio);
        }
        
        return cambios;
    }

    /**
     * Obtiene el último cambio de estado para cada proyecto.
     * 
     * @return Lista de cambios de estado
     */
    public List<CambioEstadoProyecto> findLastChangeForEachProject() {
        List<CambioEstadoProyecto> cambios = cambioEstadoProyectoRepository.findLastChangeForEachProject();
        
        for (CambioEstadoProyecto cambio : cambios) {
            enriquecerCambio(cambio);
        }
        
        return cambios;
    }

    // ============================================
    // MÉTRICAS Y ESTADÍSTICAS
    // ============================================

    /**
     * Cuenta cambios de estado de un proyecto.
     * 
     * @param idProyecto ID del proyecto
     * @return Número de cambios
     */
    public long countByProyecto(Integer idProyecto) {
        return cambioEstadoProyectoRepository.countByProyecto(idProyecto);
    }

    /**
     * Cuenta cambios de estado por estado nuevo.
     * 
     * @param idEstadoNuevo ID del estado nuevo
     * @return Número de cambios
     */
    public long countByEstadoNuevo(Integer idEstadoNuevo) {
        return cambioEstadoProyectoRepository.countByEstadoNuevo(idEstadoNuevo);
    }

    /**
     * Cuenta cambios de estado por usuario responsable.
     * 
     * @param idUsuarioResponsable ID del usuario responsable
     * @return Número de cambios
     */
    public long countByUsuarioResponsable(Integer idUsuarioResponsable) {
        return cambioEstadoProyectoRepository.countByUsuarioResponsable(idUsuarioResponsable);
    }

    /**
     * Obtiene el tiempo promedio entre cambios de estado de un proyecto.
     * 
     * @param idProyecto ID del proyecto
     * @return Tiempo promedio en horas
     */
    public Double getTiempoPromedioEntreCambios(Integer idProyecto) {
        return cambioEstadoProyectoRepository.getTiempoPromedioEntreCambios(idProyecto);
    }

    // ============================================
    // ELIMINACIÓN
    // ============================================

    /**
     * Elimina todos los cambios de estado de un proyecto.
     * 
     * @param idProyecto ID del proyecto
     */
    @Transactional
    public void deleteByProyecto(Integer idProyecto) {
        cambioEstadoProyectoRepository.deleteByIdProyecto(idProyecto);
    }

    /**
     * Elimina cambios de estado antiguos.
     * 
     * @param fecha Fecha límite
     * @return Número de cambios eliminados
     */
    @Transactional
    public int deleteOlderThan(LocalDateTime fecha) {
        // Implementar método en el repositorio si es necesario
        return 0;
    }

    // ============================================
    // MÉTODOS AUXILIARES
    // ============================================

    /**
     * Enriquece un cambio de estado con información adicional.
     * 
     * @param cambio Cambio de estado a enriquecer
     */
    private void enriquecerCambio(CambioEstadoProyecto cambio) {
        if (cambio == null) {
            return;
        }

        // Obtener nombre del proyecto
        if (cambio.getNombreProyecto() == null && cambio.getIdProyecto() != null) {
            try {
                Proyecto proyecto = proyectoRepository.findById(cambio.getIdProyecto()).orElse(null);
                if (proyecto != null) {
                    cambio.setNombreProyecto(proyecto.getNombreProyecto());
                }
            } catch (Exception e) {
                // Ignorar
            }
        }

        // Obtener nombre del estado anterior
        if (cambio.getEstadoAnteriorNombre() == null && cambio.getIdEstadoAnterior() != null) {
            try {
                EstadoProyecto estado = estadoProyectoRepository.findById(cambio.getIdEstadoAnterior()).orElse(null);
                if (estado != null) {
                    cambio.setEstadoAnteriorNombre(estado.getNombreEstado());
                }
            } catch (Exception e) {
                // Ignorar
            }
        }

        // Obtener nombre del estado nuevo
        if (cambio.getEstadoNuevoNombre() == null && cambio.getIdEstadoNuevo() != null) {
            try {
                EstadoProyecto estado = estadoProyectoRepository.findById(cambio.getIdEstadoNuevo()).orElse(null);
                if (estado != null) {
                    cambio.setEstadoNuevoNombre(estado.getNombreEstado());
                }
            } catch (Exception e) {
                // Ignorar
            }
        }

        // Obtener nombre del responsable
        if (cambio.getResponsableNombre() == null && cambio.getIdUsuarioResponsable() != null) {
            try {
                Usuario usuario = usuarioRepository.findById(cambio.getIdUsuarioResponsable()).orElse(null);
                if (usuario != null) {
                    cambio.setResponsableNombre(usuario.getNombreCompleto());
                    cambio.setResponsableEmail(usuario.getEmail());
                }
            } catch (Exception e) {
                // Ignorar
            }
        }
    }

    /**
     * Verifica si un cambio de estado es válido.
     * 
     * @param idProyecto ID del proyecto
     * @param idEstadoNuevo ID del nuevo estado
     * @param idUsuarioResponsable ID del usuario responsable
     * @return true si es válido, false en caso contrario
     */
    public boolean isValidTransition(Integer idProyecto, Integer idEstadoNuevo, Integer idUsuarioResponsable) {
        try {
            // Obtener el proyecto
            Proyecto proyecto = proyectoRepository.findById(idProyecto)
                    .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
            
            // Obtener el estado actual
            EstadoProyecto estadoActual = estadoProyectoRepository.findById(proyecto.getIdEstadoActual())
                    .orElseThrow(() -> new RuntimeException("Estado actual no encontrado"));
            
            // Verificar que el proyecto no esté en estado terminal (RN-08)
            if (estadoActual.esEstadoTerminal()) {
                return false;
            }
            
            // Obtener el nuevo estado
            EstadoProyecto estadoNuevo = estadoProyectoRepository.findById(idEstadoNuevo)
                    .orElseThrow(() -> new RuntimeException("Estado nuevo no encontrado"));
            
            // Verificar que el nuevo estado sea diferente al actual
            if (estadoActual.getIdEstado().equals(estadoNuevo.getIdEstado())) {
                return false;
            }
            
            // Verificar que el nuevo estado sea posterior en el flujo (RN-07)
            if (estadoNuevo.getOrdenFlujo() <= estadoActual.getOrdenFlujo() && 
                !estadoNuevo.esAnulado()) {
                return false;
            }
            
            // Verificar que el usuario tenga permisos para realizar el cambio (RN-15 a RN-19)
            Usuario usuario = usuarioRepository.findById(idUsuarioResponsable)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            return hasPermissionForTransition(usuario, estadoActual, estadoNuevo);
            
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica si un usuario tiene permiso para realizar una transición.
     * 
     * @param usuario Usuario
     * @param estadoActual Estado actual
     * @param estadoNuevo Estado nuevo
     * @return true si tiene permiso, false en caso contrario
     */
    private boolean hasPermissionForTransition(Usuario usuario, EstadoProyecto estadoActual, 
                                               EstadoProyecto estadoNuevo) {
        String rol = usuario.getNombreRol();
        
        // RN-15: Solo Administrador puede anular un proyecto
        if (estadoNuevo.esAnulado()) {
            return "Administrador".equals(rol);
        }
        
        // RN-16: Solo Soporte Técnico o Administrador puede marcar como Entregado
        if (estadoNuevo.esEntregado()) {
            return "Administrador".equals(rol) || "Soporte Técnico".equals(rol);
        }
        
        // RN-17: Vendedor solo puede iniciar proyecto (Aprobado -> En Diseño)
        if ("Vendedor".equals(rol)) {
            return estadoActual.esAprobado() && estadoNuevo.esEnDiseno();
        }
        
        // RN-18: Diseñador solo puede mover entre En Diseño y En Producción
        if ("Diseñador".equals(rol)) {
            return estadoActual.esEnDiseno() && estadoNuevo.esEnProduccion();
        }
        
        // RN-19: Producción solo puede mover de En Producción a Instalación/Montaje
        if ("Producción".equals(rol)) {
            return estadoActual.esEnProduccion() && estadoNuevo.esInstalacionMontaje();
        }
        
        // Administrador tiene todos los permisos
        if ("Administrador".equals(rol)) {
            return true;
        }
        
        return false;
    }

    /**
     * Obtiene el historial completo de estados de un proyecto en orden cronológico.
     * 
     * @param idProyecto ID del proyecto
     * @return Lista de cambios de estado en orden ascendente
     */
    public List<CambioEstadoProyecto> getHistorialCompleto(Integer idProyecto) {
        // Implementar método en el repositorio si es necesario
        // Por ahora, usar el método existente y ordenar
        List<CambioEstadoProyecto> cambios = findByProyecto(idProyecto);
        cambios.sort((c1, c2) -> c1.getFechaCambio().compareTo(c2.getFechaCambio()));
        return cambios;
    }

    /**
     * Obtiene el estado actual de un proyecto a partir de su historial.
     * 
     * @param idProyecto ID del proyecto
     * @return Estado actual del proyecto
     */
    public String getEstadoActualFromHistorial(Integer idProyecto) {
        CambioEstadoProyecto ultimoCambio = findLastByProyecto(idProyecto);
        if (ultimoCambio != null) {
            return ultimoCambio.getEstadoNuevoNombre();
        }
        return null;
    }
}