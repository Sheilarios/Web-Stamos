package com.stamos.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
    String value() default "";
    String accion() default "EXECUTE";
    String entidad() default "";
    String nivel() default "INFO";
    boolean guardarDatos() default true;
}