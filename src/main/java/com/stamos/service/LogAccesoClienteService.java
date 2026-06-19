package com.stamos.service;

import com.stamos.model.Cliente;
import com.stamos.model.LogAccesoCliente;
import com.stamos.model.Usuario;
import com.stamos.repository.ClienteRepository;
import com.stamos.repository.LogAccesoClienteRepository;
import com.stamos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogAccesoClienteService {

    @Autowired
    private LogAccesoClienteRepository logAccesoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public LogAccesoCliente registrarAcceso(Integer idCliente, String ipOrigen, String userAgent) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        LogAccesoCliente log = new LogAccesoCliente();
        log.setIdCliente(idCliente);
        log.setIpOrigen(ipOrigen);
        log.setNavegador(userAgent);
        log.setFechaAcceso(LocalDateTime.now());

        log.setNombreCliente(cliente.getNombreRazonSocial());
        log.setEmailCliente(cliente.getEmail());
        log.setTipoCliente(cliente.getTipoPersona());

        LogAccesoCliente logGuardado = logAccesoRepository.save(log);

        if (cliente.getIdUsuario() != null) {
            usuarioRepository.updateUltimoAcceso(cliente.getIdUsuario());
        }

        return logGuardado;
    }

    @Transactional
    public LogAccesoCliente registrarAcceso(Authentication authentication, HttpServletRequest request) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String email = authentication.getName();
        if (email == null || "anonymousUser".equals(email)) {
            return null;
        }

        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null) {
            return null;
        }

        Cliente cliente = clienteRepository.findByIdUsuario(usuario.getIdUsuario()).orElse(null);
        if (cliente == null) {
            return null;
        }

        String ipOrigen = getClientIP(request);
        String userAgent = request.getHeader("User-Agent");

        return registrarAcceso(cliente.getIdCliente(), ipOrigen, userAgent);
    }

    @Transactional
    public LogAccesoCliente registrarAccesoConDetalles(Integer idCliente, HttpServletRequest request) {
        String ipOrigen = getClientIP(request);
        String userAgent = request.getHeader("User-Agent");
        return registrarAcceso(idCliente, ipOrigen, userAgent);
    }

    public LogAccesoCliente findById(Integer idLog) {
        return logAccesoRepository.findById(idLog).orElse(null);
    }

    public Page<LogAccesoCliente> findAll(Pageable pageable) {
        return logAccesoRepository.findAll(pageable);
    }

    public List<LogAccesoCliente> findTopN(int limit) {
        return logAccesoRepository.findTopN(limit);
    }

    public Page<LogAccesoCliente> findByCliente(Integer idCliente, Pageable pageable) {
        return logAccesoRepository.findByIdCliente(idCliente, pageable);
    }

    public LogAccesoCliente findLastByCliente(Integer idCliente) {
        return logAccesoRepository.findLastByCliente(idCliente).orElse(null);
    }

    public Page<LogAccesoCliente> findByIpOrigen(String ipOrigen, Pageable pageable) {
        return logAccesoRepository.findByIpOrigen(ipOrigen, pageable);
    }

    public Page<LogAccesoCliente> findByNavegador(String navegador, Pageable pageable) {
        return logAccesoRepository.findByNavegadorContaining(navegador, pageable);
    }

    public Page<LogAccesoCliente> findByFechaBetween(LocalDateTime fechaInicio,
                                                     LocalDateTime fechaFin,
                                                     Pageable pageable) {
        return logAccesoRepository.findByFechaAccesoBetween(fechaInicio, fechaFin, pageable);
    }

    public Page<LogAccesoCliente> findAccesosHoy(Pageable pageable) {
        return logAccesoRepository.findAccesosHoy(pageable);
    }

    public Page<LogAccesoCliente> findAccesosUltimosDias(int days, Pageable pageable) {
        LocalDateTime fecha = LocalDateTime.now().minusDays(days);
        return logAccesoRepository.findAccesosUltimosDias(fecha, pageable);
    }

    public Page<LogAccesoCliente> search(String searchText, Pageable pageable) {
        return logAccesoRepository.search(searchText, pageable);
    }

    public long count() {
        return logAccesoRepository.countTotal();
    }

    public long countByCliente(Integer idCliente) {
        return logAccesoRepository.countByIdCliente(idCliente);
    }

    public long countByIpOrigen(String ipOrigen) {
        return logAccesoRepository.countByIpOrigen(ipOrigen);
    }

    public long countAccesosHoy() {
        return logAccesoRepository.findAccesosHoy().size();
    }

    public long countAccesosMes() {
        LocalDateTime inicio = LocalDateTime.now().withDayOfMonth(1);
        List<LogAccesoCliente> accesos = logAccesoRepository.findAccesosUltimosDias(inicio);
        return accesos != null ? accesos.size() : 0;
    }

    @Transactional
    public void deleteByCliente(Integer idCliente) {
        logAccesoRepository.deleteByIdCliente(idCliente);
    }

    @Transactional
    public int deleteOlderThan(LocalDateTime fecha) {
        return logAccesoRepository.deleteOlderThan(fecha);
    }

    @Transactional
    public int deleteOldRecords() {
        LocalDateTime fecha = LocalDateTime.now().minusMonths(6);
        return logAccesoRepository.deleteOldRecords(fecha);
    }

    @Transactional
    public void deleteByIpOrigen(String ipOrigen) {
        logAccesoRepository.deleteByIpOrigen(ipOrigen);
    }

    private String getClientIP(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    public String getTipoDispositivo(String userAgent) {
        if (userAgent == null) {
            return "Desconocido";
        }

        String ua = userAgent.toLowerCase();
        if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone")) {
            return "Móvil";
        } else if (ua.contains("tablet") || ua.contains("ipad")) {
            return "Tablet";
        } else {
            return "Escritorio";
        }
    }

    public String getNavegadorSimplificado(String userAgent) {
        if (userAgent == null) {
            return "Desconocido";
        }

        String ua = userAgent.toLowerCase();
        if (ua.contains("chrome") && !ua.contains("edg")) {
            return "Chrome";
        } else if (ua.contains("firefox")) {
            return "Firefox";
        } else if (ua.contains("safari") && !ua.contains("chrome")) {
            return "Safari";
        } else if (ua.contains("edg")) {
            return "Edge";
        } else if (ua.contains("opera") || ua.contains("opr")) {
            return "Opera";
        } else if (ua.contains("brave")) {
            return "Brave";
        } else {
            return "Otro";
        }
    }

    public String getSistemaOperativo(String userAgent) {
        if (userAgent == null) {
            return "Desconocido";
        }

        String ua = userAgent.toLowerCase();
        if (ua.contains("windows")) {
            return "Windows";
        } else if (ua.contains("mac os")) {
            return "macOS";
        } else if (ua.contains("linux") || ua.contains("ubuntu")) {
            return "Linux";
        } else if (ua.contains("android")) {
            return "Android";
        } else if (ua.contains("iphone") || ua.contains("ios")) {
            return "iOS";
        } else {
            return "Otro";
        }
    }

    public String getDispositivoIcono(String tipoDispositivo) {
        if (tipoDispositivo == null) {
            return "ti ti-device-laptop";
        }
        switch (tipoDispositivo) {
            case "Móvil":
                return "ti ti-device-mobile";
            case "Tablet":
                return "ti ti-device-tablet";
            case "Escritorio":
                return "ti ti-device-laptop";
            default:
                return "ti ti-device-laptop";
        }
    }

    public String getDispositivoColor(String tipoDispositivo) {
        if (tipoDispositivo == null) {
            return "#6B7280";
        }
        switch (tipoDispositivo) {
            case "Móvil":
                return "#3B82F6";
            case "Tablet":
                return "#8B5CF6";
            case "Escritorio":
                return "#10B981";
            default:
                return "#6B7280";
        }
    }

    public String getNavegadorIcono(String navegador) {
        if (navegador == null) {
            return "ti ti-world";
        }
        switch (navegador) {
            case "Chrome":
                return "ti ti-brand-chrome";
            case "Firefox":
                return "ti ti-brand-firefox";
            case "Safari":
                return "ti ti-brand-safari";
            case "Edge":
                return "ti ti-brand-edge";
            case "Opera":
                return "ti ti-brand-opera";
            case "Brave":
                return "ti ti-brand-brave";
            default:
                return "ti ti-world";
        }
    }

    public String getNavegadorColor(String navegador) {
        if (navegador == null) {
            return "#6B7280";
        }
        switch (navegador) {
            case "Chrome":
                return "#4285F4";
            case "Firefox":
                return "#FF7139";
            case "Safari":
                return "#1A74E6";
            case "Edge":
                return "#0078D7";
            case "Opera":
                return "#FF1B2D";
            case "Brave":
                return "#FB542B";
            default:
                return "#6B7280";
        }
    }

    public String getDispositivoBadgeClass(String tipoDispositivo) {
        if (tipoDispositivo == null) {
            return "badge-secondary";
        }
        switch (tipoDispositivo) {
            case "Móvil":
                return "badge-info";
            case "Tablet":
                return "badge-warning";
            case "Escritorio":
                return "badge-success";
            default:
                return "badge-secondary";
        }
    }

    public String getNavegadorBadgeClass(String navegador) {
        if (navegador == null) {
            return "badge-secondary";
        }
        switch (navegador) {
            case "Chrome":
                return "badge-primary";
            case "Firefox":
                return "badge-danger";
            case "Safari":
                return "badge-info";
            case "Edge":
                return "badge-primary";
            case "Opera":
                return "badge-danger";
            case "Brave":
                return "badge-warning";
            default:
                return "badge-secondary";
        }
    }
}