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

    @Transactional
    public CambioEstadoProyecto registrarCambioEstado(Integer idProyecto, Integer idEstadoAnterior,
                                                      Integer idEstadoNuevo, Integer idUsuarioResponsable,
                                                      String comentario) {

        EstadoProyecto estadoAnterior = estadoProyectoRepository.findById(idEstadoAnterior)
                .orElseThrow(() -> new RuntimeException("Estado anterior no encontrado con ID: " + idEstadoAnterior));

        EstadoProyecto estadoNuevo = estadoProyectoRepository.findById(idEstadoNuevo)
                .orElseThrow(() -> new RuntimeException("Estado nuevo no encontrado con ID: " + idEstadoNuevo));

        Usuario usuario = usuarioRepository.findById(idUsuarioResponsable)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuarioResponsable));

        if (estadoAnterior.esEstadoTerminal()) {
            throw new RuntimeException("No se puede cambiar el estado de un proyecto " + 
                                      estadoAnterior.getNombreEstado().toLowerCase());
        }

        if (idEstadoAnterior.equals(idEstadoNuevo)) {
            throw new RuntimeException("El nuevo estado es igual al estado actual");
        }

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

        return cambioEstadoProyectoRepository.save(cambio);
    }

    @Transactional
    public CambioEstadoProyecto registrarCambioEstado(Integer idProyecto, Integer idEstadoAnterior,
                                                      Integer idEstadoNuevo, Integer idUsuarioResponsable) {
        return registrarCambioEstado(idProyecto, idEstadoAnterior, idEstadoNuevo, 
                                    idUsuarioResponsable, null);
    }

    @Transactional
    public CambioEstadoProyecto registrarInicioProyecto(Integer idProyecto, Integer idUsuarioResponsable,
                                                        String comentario) {
        return registrarCambioEstado(idProyecto, 1, 2, idUsuarioResponsable, comentario);
    }

    @Transactional
    public CambioEstadoProyecto registrarAvanceEstado(Integer idProyecto, Integer idEstadoNuevo,
                                                      Integer idUsuarioResponsable, String comentario) {
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + idProyecto));
        
        Integer idEstadoActual = proyecto.getIdEstadoActual();
        
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

    @Transactional
    public CambioEstadoProyecto registrarAnulacionProyecto(Integer idProyecto, Integer idUsuarioResponsable,
                                                           String motivo) {
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + idProyecto));
        
        Integer idEstadoActual = proyecto.getIdEstadoActual();
        
        EstadoProyecto estadoActual = estadoProyectoRepository.findById(idEstadoActual)
                .orElseThrow(() -> new RuntimeException("Estado actual no encontrado"));
        
        if (estadoActual.esEstadoTerminal()) {
            throw new RuntimeException("No se puede anular un proyecto que ya está " + 
                                      estadoActual.getNombreEstado().toLowerCase());
        }
        
        return registrarCambioEstado(idProyecto, idEstadoActual, 6, 
                                    idUsuarioResponsable, "Anulación: " + (motivo != null ? motivo : "Sin motivo"));
    }

    @Transactional
    public CambioEstadoProyecto registrarEntregaProyecto(Integer idProyecto, Integer idUsuarioResponsable,
                                                         String comentario) {
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + idProyecto));
        
        Integer idEstadoActual = proyecto.getIdEstadoActual();
        
        EstadoProyecto estadoActual = estadoProyectoRepository.findById(idEstadoActual)
                .orElseThrow(() -> new RuntimeException("Estado actual no encontrado"));
        
        if (!estadoActual.esInstalacionMontaje() && !estadoActual.esEnProduccion()) {
            throw new RuntimeException("Para entregar un proyecto debe estar en estado " +
                                      "Instalación/Montaje o En Producción");
        }
        
        return registrarCambioEstado(idProyecto, idEstadoActual, 5, 
                                    idUsuarioResponsable, "Entrega: " + (comentario != null ? comentario : "Proyecto entregado"));
    }

    public List<CambioEstadoProyecto> findByProyecto(Integer idProyecto) {
        List<CambioEstadoProyecto> cambios = cambioEstadoProyectoRepository.findByIdProyecto(idProyecto);
        
        for (CambioEstadoProyecto cambio : cambios) {
            enriquecerCambio(cambio);
        }
        
        return cambios;
    }

    public Page<CambioEstadoProyecto> findByProyecto(Integer idProyecto, Pageable pageable) {
        Page<CambioEstadoProyecto> cambios = cambioEstadoProyectoRepository.findByIdProyecto(idProyecto, pageable);
        
        for (CambioEstadoProyecto cambio : cambios.getContent()) {
            enriquecerCambio(cambio);
        }
        
        return cambios;
    }

    public CambioEstadoProyecto findLastByProyecto(Integer idProyecto) {
        CambioEstadoProyecto cambio = cambioEstadoProyectoRepository.findLastByProyecto(idProyecto);
        if (cambio != null) {
            enriquecerCambio(cambio);
        }
        return cambio;
    }

    public Page<CambioEstadoProyecto> findByUsuarioResponsable(Integer idUsuarioResponsable, Pageable pageable) {
        Page<CambioEstadoProyecto> cambios = cambioEstadoProyectoRepository.findByIdUsuarioResponsable(
                idUsuarioResponsable, pageable);
        
        for (CambioEstadoProyecto cambio : cambios.getContent()) {
            enriquecerCambio(cambio);
        }
        
        return cambios;
    }

    public Page<CambioEstadoProyecto> findByEstadoNuevo(Integer idEstadoNuevo, Pageable pageable) {
        Page<CambioEstadoProyecto> cambios = cambioEstadoProyectoRepository.findByIdEstadoNuevo(idEstadoNuevo, pageable);
        
        for (CambioEstadoProyecto cambio : cambios.getContent()) {
            enriquecerCambio(cambio);
        }
        
        return cambios;
    }

    public Page<CambioEstadoProyecto> findByFechaCambioBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin,
                                                               Pageable pageable) {
        Page<CambioEstadoProyecto> cambios = cambioEstadoProyectoRepository.findByFechaCambioBetween(
                fechaInicio, fechaFin, pageable);
        
        for (CambioEstadoProyecto cambio : cambios.getContent()) {
            enriquecerCambio(cambio);
        }
        
        return cambios;
    }

    public Page<CambioEstadoProyecto> findAvances(Pageable pageable) {
        Page<CambioEstadoProyecto> cambios = cambioEstadoProyectoRepository.findAvances(pageable);
        
        for (CambioEstadoProyecto cambio : cambios.getContent()) {
            enriquecerCambio(cambio);
        }
        
        return cambios;
    }

    public Page<CambioEstadoProyecto> findRetrocesos(Pageable pageable) {
        Page<CambioEstadoProyecto> cambios = cambioEstadoProyectoRepository.findRetrocesos(pageable);
        
        for (CambioEstadoProyecto cambio : cambios.getContent()) {
            enriquecerCambio(cambio);
        }
        
        return cambios;
    }

    public List<CambioEstadoProyecto> findAvancesByProyecto(Integer idProyecto) {
        List<CambioEstadoProyecto> cambios = cambioEstadoProyectoRepository.findAvancesByProyecto(idProyecto);
        
        for (CambioEstadoProyecto cambio : cambios) {
            enriquecerCambio(cambio);
        }
        
        return cambios;
    }

    public List<CambioEstadoProyecto> findRetrocesosByProyecto(Integer idProyecto) {
        List<CambioEstadoProyecto> cambios = cambioEstadoProyectoRepository.findRetrocesosByProyecto(idProyecto);
        
        for (CambioEstadoProyecto cambio : cambios) {
            enriquecerCambio(cambio);
        }
        
        return cambios;
    }

    public Page<CambioEstadoProyecto> findWithComentario(Pageable pageable) {
        Page<CambioEstadoProyecto> cambios = cambioEstadoProyectoRepository.findWithComentario(pageable);
        
        for (CambioEstadoProyecto cambio : cambios.getContent()) {
            enriquecerCambio(cambio);
        }
        
        return cambios;
    }

    public List<CambioEstadoProyecto> findWithComentarioByProyecto(Integer idProyecto) {
        List<CambioEstadoProyecto> cambios = cambioEstadoProyectoRepository.findWithComentarioByProyecto(idProyecto);
        
        for (CambioEstadoProyecto cambio : cambios) {
            enriquecerCambio(cambio);
        }
        
        return cambios;
    }

    public List<CambioEstadoProyecto> findLastChangeForEachProject() {
        List<CambioEstadoProyecto> cambios = cambioEstadoProyectoRepository.findLastChangeForEachProject();
        
        for (CambioEstadoProyecto cambio : cambios) {
            enriquecerCambio(cambio);
        }
        
        return cambios;
    }


    public long countByProyecto(Integer idProyecto) {
        return cambioEstadoProyectoRepository.countByProyecto(idProyecto);
    }

    public long countByEstadoNuevo(Integer idEstadoNuevo) {
        return cambioEstadoProyectoRepository.countByEstadoNuevo(idEstadoNuevo);
    }

    public long countByUsuarioResponsable(Integer idUsuarioResponsable) {
        return cambioEstadoProyectoRepository.countByUsuarioResponsable(idUsuarioResponsable);
    }

    public Double getTiempoPromedioEntreCambios(Integer idProyecto) {
        return cambioEstadoProyectoRepository.getTiempoPromedioEntreCambios(idProyecto);
    }

    @Transactional
    public void deleteByProyecto(Integer idProyecto) {
        cambioEstadoProyectoRepository.deleteByIdProyecto(idProyecto);
    }


    @Transactional
    public int deleteOlderThan(LocalDateTime fecha) {
        return 0;
    }

    private void enriquecerCambio(CambioEstadoProyecto cambio) {
        if (cambio == null) {
            return;
        }

        if (cambio.getNombreProyecto() == null && cambio.getIdProyecto() != null) {
            try {
                Proyecto proyecto = proyectoRepository.findById(cambio.getIdProyecto()).orElse(null);
                if (proyecto != null) {
                    cambio.setNombreProyecto(proyecto.getNombreProyecto());
                }
            } catch (Exception e) {
            }
        }

        if (cambio.getEstadoAnteriorNombre() == null && cambio.getIdEstadoAnterior() != null) {
            try {
                EstadoProyecto estado = estadoProyectoRepository.findById(cambio.getIdEstadoAnterior()).orElse(null);
                if (estado != null) {
                    cambio.setEstadoAnteriorNombre(estado.getNombreEstado());
                }
            } catch (Exception e) {
            }
        }

        if (cambio.getEstadoNuevoNombre() == null && cambio.getIdEstadoNuevo() != null) {
            try {
                EstadoProyecto estado = estadoProyectoRepository.findById(cambio.getIdEstadoNuevo()).orElse(null);
                if (estado != null) {
                    cambio.setEstadoNuevoNombre(estado.getNombreEstado());
                }
            } catch (Exception e) {
            }
        }

        if (cambio.getResponsableNombre() == null && cambio.getIdUsuarioResponsable() != null) {
            try {
                Usuario usuario = usuarioRepository.findById(cambio.getIdUsuarioResponsable()).orElse(null);
                if (usuario != null) {
                    cambio.setResponsableNombre(usuario.getNombreCompleto());
                    cambio.setResponsableEmail(usuario.getEmail());
                }
            } catch (Exception e) {
            }
        }
    }

    public boolean isValidTransition(Integer idProyecto, Integer idEstadoNuevo, Integer idUsuarioResponsable) {
        try {
            Proyecto proyecto = proyectoRepository.findById(idProyecto)
                    .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
            
            EstadoProyecto estadoActual = estadoProyectoRepository.findById(proyecto.getIdEstadoActual())
                    .orElseThrow(() -> new RuntimeException("Estado actual no encontrado"));
            
            if (estadoActual.esEstadoTerminal()) {
                return false;
            }
            
            EstadoProyecto estadoNuevo = estadoProyectoRepository.findById(idEstadoNuevo)
                    .orElseThrow(() -> new RuntimeException("Estado nuevo no encontrado"));
            
            if (estadoActual.getIdEstado().equals(estadoNuevo.getIdEstado())) {
                return false;
            }
            
            if (estadoNuevo.getOrdenFlujo() <= estadoActual.getOrdenFlujo() && 
                !estadoNuevo.esAnulado()) {
                return false;
            }
            
            Usuario usuario = usuarioRepository.findById(idUsuarioResponsable)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            return hasPermissionForTransition(usuario, estadoActual, estadoNuevo);
            
        } catch (Exception e) {
            return false;
        }
    }


    private boolean hasPermissionForTransition(Usuario usuario, EstadoProyecto estadoActual, 
                                               EstadoProyecto estadoNuevo) {
        String rol = usuario.getNombreRol();
        
        if (estadoNuevo.esAnulado()) {
            return "Administrador".equals(rol);
        }
        
        if (estadoNuevo.esEntregado()) {
            return "Administrador".equals(rol) || "Soporte Técnico".equals(rol);
        }
        
        if ("Vendedor".equals(rol)) {
            return estadoActual.esAprobado() && estadoNuevo.esEnDiseno();
        }
        
        if ("Diseñador".equals(rol)) {
            return estadoActual.esEnDiseno() && estadoNuevo.esEnProduccion();
        }
        
        if ("Producción".equals(rol)) {
            return estadoActual.esEnProduccion() && estadoNuevo.esInstalacionMontaje();
        }
        
        return "Administrador".equals(rol);
    }

    public List<CambioEstadoProyecto> getHistorialCompleto(Integer idProyecto) {
        List<CambioEstadoProyecto> cambios = findByProyecto(idProyecto);
        cambios.sort((c1, c2) -> c1.getFechaCambio().compareTo(c2.getFechaCambio()));
        return cambios;
    }


    public String getEstadoActualFromHistorial(Integer idProyecto) {
        CambioEstadoProyecto ultimoCambio = findLastByProyecto(idProyecto);
        if (ultimoCambio != null) {
            return ultimoCambio.getEstadoNuevoNombre();
        }
        return null;
    }
}