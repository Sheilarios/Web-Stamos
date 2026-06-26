package com.stamos.service;

import com.stamos.model.EstadoProyecto;
import com.stamos.model.Proyecto;
import com.stamos.model.Usuario;
import com.stamos.repository.EstadoProyectoRepository;
import com.stamos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransicionEstadoService {

    @Autowired
    private EstadoProyectoRepository estadoProyectoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public boolean esTransicionValida(Proyecto proyecto, Integer idEstadoNuevo, Integer idUsuarioResponsable) {
        Usuario usuario = usuarioRepository.findById(idUsuarioResponsable)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuarioResponsable));

        EstadoProyecto estadoActual = estadoProyectoRepository.findById(proyecto.getIdEstadoActual())
                .orElseThrow(() -> new RuntimeException("Estado actual no encontrado con ID: " + proyecto.getIdEstadoActual()));

        EstadoProyecto estadoNuevo = estadoProyectoRepository.findById(idEstadoNuevo)
                .orElseThrow(() -> new RuntimeException("Estado nuevo no encontrado con ID: " + idEstadoNuevo));

        if (estadoActual.getIdEstado().equals(estadoNuevo.getIdEstado())) {
            return false;
        }

        if (estadoActual.esEstadoTerminal()) {
            return false;
        }

        if (estadoNuevo.esAnulado()) {
            return "Administrador".equals(usuario.getNombreRol());
        }

        if (estadoNuevo.esEntregado()) {
            return "Administrador".equals(usuario.getNombreRol()) ||
                   "Soporte Técnico".equals(usuario.getNombreRol());
        }

        if ("Vendedor".equals(usuario.getNombreRol())) {
            return estadoActual.esAprobado() && estadoNuevo.esEnDiseno();
        }

        if ("Diseñador".equals(usuario.getNombreRol())) {
            return estadoActual.esEnDiseno() && estadoNuevo.esEnProduccion();
        }

        if ("Producción".equals(usuario.getNombreRol())) {
            return estadoActual.esEnProduccion() && estadoNuevo.esInstalacionMontaje();
        }

        return "Administrador".equals(usuario.getNombreRol());
    }

    public boolean esTransicionValidaConOrden(Proyecto proyecto, Integer idEstadoNuevo, Integer idUsuarioResponsable) {
        if (!esTransicionValida(proyecto, idEstadoNuevo, idUsuarioResponsable)) {
            return false;
        }

        EstadoProyecto estadoActual = estadoProyectoRepository.findById(proyecto.getIdEstadoActual())
                .orElseThrow(() -> new RuntimeException("Estado actual no encontrado"));

        EstadoProyecto estadoNuevo = estadoProyectoRepository.findById(idEstadoNuevo)
                .orElseThrow(() -> new RuntimeException("Estado nuevo no encontrado"));

        if (!estadoNuevo.esAnulado()) {
            return estadoNuevo.getOrdenFlujo() > estadoActual.getOrdenFlujo();
        }

        return true;
    }
}