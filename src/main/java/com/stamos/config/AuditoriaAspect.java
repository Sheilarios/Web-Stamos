package com.stamos.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stamos.annotation.Auditable;
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
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
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
              "execution(* com.stamos.service.*.validar*(..)) || " +
              "execution(* com.stamos.service.*.aprobar*(..)) || " +
              "execution(* com.stamos.service.*.anular*(..))")
    public void modifyingMethod() {}

    @Before("auditableMethod() || modifyingMethod()")
    public void auditBefore(JoinPoint joinPoint) {
        try {
            Auditoria auditoria = crearAuditoriaBase(joinPoint);
            auditoria.setAccion("EJECUTAR");
            auditoria.setDatosNuevos(obtenerDatosMetodo(joinPoint, "Inicio de ejecución"));
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
                    auditoria.setDatosNuevos(objectMapper.writeValueAsString(result));
                } catch (JsonProcessingException e) {
                    auditoria.setDatosNuevos(obtenerDatosMetodo(joinPoint, "Resultado: " + result.toString()));
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
                    datos.put("argumentos", Arrays.toString(args));
                } catch (Exception e) {
                    datos.put("argumentos", "Error al serializar argumentos");
                }
            }
            
            try {
                auditoria.setDatosNuevos(objectMapper.writeValueAsString(datos));
            } catch (JsonProcessingException e) {
                auditoria.setDatosNuevos("{}");
            }
            auditoriaRepository.save(auditoria);
        } catch (Exception e) {
            System.err.println("Error en auditoría AFTER THROWING: " + e.getMessage());
        }
    }

    private Auditoria crearAuditoriaBase(JoinPoint joinPoint) {
        Auditoria auditoria = new Auditoria();
        auditoria.setIdUsuario(getUsuarioActualId());
        auditoria.setEntidad(getEntidad(joinPoint));
        auditoria.setIpOrigen(getClientIP());
        auditoria.setFechaHora(LocalDateTime.now());
        return auditoria;
    }

    private String getEntidad(JoinPoint joinPoint) {
        String clase = joinPoint.getTarget().getClass().getSimpleName();
        return clase.replace("Service", "");
    }

    private Integer getUsuarioActualId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || 
                authentication.getName().equals("anonymousUser")) {
                return null;
            }
            
            String email = authentication.getName();
            if (email == null) {
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

    private String obtenerDatosMetodo(JoinPoint joinPoint, String mensaje) {
        try {
            Map<String, Object> datos = new HashMap<>();
            datos.put("metodo", joinPoint.getSignature().getName());
            datos.put("mensaje", mensaje);
            return objectMapper.writeValueAsString(datos);
        } catch (JsonProcessingException e) {
            return "{}";
        }
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
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Auditable auditable = method.getAnnotation(Auditable.class);
            
            if (auditable != null && !auditable.entidad().isEmpty()) {
                return null;
            }
            
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof Integer integer) {
                    return integer;
                }
                if (arg instanceof Number number) {
                    return number.intValue();
                }
                if (arg != null) {
                    try {
                        java.lang.reflect.Method getId = arg.getClass().getMethod("getId");
                        Object id = getId.invoke(arg);
                        if (id instanceof Number number) {
                            return number.intValue();
                        }
                    } catch (Exception e) {
                    }
                }
            }
            
            if (result != null) {
                try {
                    java.lang.reflect.Method getId = result.getClass().getMethod("getId");
                    Object id = getId.invoke(result);
                    if (id instanceof Number number) {
                        return number.intValue();
                    }
                } catch (Exception e) {
                }
            }
            
        } catch (Exception e) {
        }
        return null;
    }
}