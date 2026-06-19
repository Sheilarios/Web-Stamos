package com.stamos.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stamos.model.Auditoria;
import com.stamos.model.Usuario;
import com.stamos.repository.AuditoriaRepository;
import com.stamos.repository.UsuarioRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class AuditoriaAspect {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Pointcut("@annotation(com.stamos.annotation.Auditable)")
    public void auditableMethod() {}

    @Pointcut("execution(* com.stamos.service.*.save*(..)) || " +
              "execution(* com.stamos.service.*.update*(..)) || " +
              "execution(* com.stamos.service.*.delete*(..)) || " +
              "execution(* com.stamos.service.*.cambiar*(..)) || " +
              "execution(* com.stamos.service.*.registrar*(..)) || " +
              "execution(* com.stamos.service.*.deshabilitar*(..)) || " +
              "execution(* com.stamos.service.*.habilitar*(..)) || " +
              "execution(* com.stamos.service.*.validar*(..))")
    public void modifyingMethod() {}

    @Before("auditableMethod()")
    public void auditBefore(JoinPoint joinPoint) {
        try {
            Auditoria auditoria = crearAuditoriaBase(joinPoint);
            auditoria.setAccion("EJECUTAR");
            
            Map<String, Object> datos = new HashMap<>();
            datos.put("metodo", joinPoint.getSignature().getName());
            datos.put("mensaje", "Inicio de ejecución");
            auditoria.setDatosNuevos(datos);
            
            auditoriaRepository.save(auditoria);
        } catch (Exception e) {
            System.err.println("Error en auditoría BEFORE: " + e.getMessage());
        }
    }

    @AfterReturning(pointcut = "modifyingMethod() || auditableMethod()", returning = "result")
    public void auditAfterModifying(JoinPoint joinPoint, Object result) {
        try {
            Auditoria auditoria = crearAuditoriaBase(joinPoint);
            auditoria.setAccion(determinarAccion(joinPoint));
            
            if (result != null) {
                try {
                    String json = objectMapper.writeValueAsString(result);
                    Map<String, Object> datos = objectMapper.readValue(json, Map.class);
                    auditoria.setDatosNuevos(datos);
                } catch (JsonProcessingException e) {
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("resultado", result.toString());
                    auditoria.setDatosNuevos(datos);
                }
            }
            
            Integer idRegistro = obtenerIdRegistro(joinPoint, result);
            if (idRegistro != null) {
                auditoria.setIdRegistroAfectado(idRegistro);
            }
            
            auditoriaRepository.save(auditoria);
        } catch (Exception e) {
            System.err.println("Error en auditoría AFTER: " + e.getMessage());
        }
    }

    @AfterThrowing(pointcut = "modifyingMethod() || auditableMethod()", throwing = "exception")
    public void auditAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        try {
            Auditoria auditoria = crearAuditoriaBase(joinPoint);
            auditoria.setAccion("ERROR");
            
            Map<String, Object> datos = new HashMap<>();
            datos.put("error", exception.getMessage());
            datos.put("metodo", joinPoint.getSignature().getName());
            
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                try {
                    String json = objectMapper.writeValueAsString(args);
                    datos.put("argumentos", objectMapper.readValue(json, Map.class));
                } catch (JsonProcessingException e) {
                    datos.put("argumentos", Arrays.toString(args));
                }
            }
            
            auditoria.setDatosNuevos(datos);
            auditoriaRepository.save(auditoria);
        } catch (Exception e) {
            System.err.println("Error en auditoría AFTER THROWING: " + e.getMessage());
        }
    }

    private Auditoria crearAuditoriaBase(JoinPoint joinPoint) {
        Auditoria auditoria = new Auditoria();
        auditoria.setIdUsuario(getUsuarioActualId());
        String clase = joinPoint.getTarget().getClass().getSimpleName();
        auditoria.setEntidad(clase);
        auditoria.setIpOrigen(getClientIP());
        auditoria.setFechaHora(LocalDateTime.now());
        return auditoria;
    }

    private Integer getUsuarioActualId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }
            String email = authentication.getName();
            if (email == null || email.equals("anonymousUser")) {
                return null;
            }
            
            Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
            return usuario != null ? usuario.getIdUsuario() : null;
            
        } catch (Exception e) {
            return null;
        }
    }

    private String getClientIP() {
        try {
            HttpServletRequest request = getCurrentRequest();
            if (request == null) {
                return null;
            }
            
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            
            if (ip != null && ip.contains(",")) {
                ip = ip.split(",")[0].trim();
            }
            
            return ip;
            
        } catch (Exception e) {
            return null;
        }
    }

    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                return attributes.getRequest();
            }
        } catch (Exception e) {
        }
        return null;
    }

    private String determinarAccion(JoinPoint joinPoint) {
        String metodo = joinPoint.getSignature().getName().toLowerCase();
        
        if (metodo.startsWith("save") || metodo.startsWith("crear") || 
            metodo.startsWith("registrar") || metodo.startsWith("insert")) {
            return "CREATE";
        }
        if (metodo.startsWith("update") || metodo.startsWith("modificar") || 
            metodo.startsWith("editar") || metodo.startsWith("actualizar")) {
            return "UPDATE";
        }
        if (metodo.startsWith("delete") || metodo.startsWith("eliminar") || 
            metodo.startsWith("remove") || metodo.startsWith("borrar") ||
            metodo.startsWith("anular")) {
            return "DELETE";
        }
        if (metodo.startsWith("cambiar") || metodo.startsWith("transition") || 
            metodo.startsWith("mover") || metodo.startsWith("estado")) {
            return "STATE_CHANGE";
        }
        if (metodo.startsWith("validar") || metodo.startsWith("aprobar") || 
            metodo.startsWith("rechazar")) {
            return "VALIDATE";
        }
        if (metodo.startsWith("deshabilitar") || metodo.startsWith("inactivar")) {
            return "DISABLE";
        }
        if (metodo.startsWith("habilitar") || metodo.startsWith("activar")) {
            return "ENABLE";
        }
        
        return "EXECUTE";
    }

    private Integer obtenerIdRegistro(JoinPoint joinPoint, Object result) {
        try {
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof Integer integer) {
                    return integer;
                }
                if (arg instanceof Number number) {
                    return number.intValue();
                }
            }
            
            if (result != null) {
                try {
                    java.lang.reflect.Method method = result.getClass().getMethod("getId");
                    Object id = method.invoke(result);
                    if (id instanceof Number number) {
                        return number.intValue();
                    }
                } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
                }
            }
            
        } catch (Exception e) {
        }
        return null;
    }
}