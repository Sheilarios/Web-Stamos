package com.stamos.util;
public final class Constantes {

    private Constantes() {
    }

    public static final String ROL_ADMINISTRADOR = "ADMINISTRADOR";
    public static final String ROL_COMERCIAL = "COMERCIAL";
    public static final String ROL_DISEÑADOR = "DISEÑADOR";
    public static final String ROL_PRODUCCION = "PRODUCCION";
    public static final String ROL_SOPORTE_TECNICO = "SOPORTE_TECNICO";
    public static final String ROL_CLIENTE = "CLIENTE";

    public static final String COTIZACION_EMITIDA = "EMITIDA";
    public static final String COTIZACION_APROBADA = "APROBADA";
    public static final String COTIZACION_RECHAZADA = "RECHAZADA";
    public static final String COTIZACION_ANULADA = "ANULADA";

    public static final String PROYECTO_COTIZADO = "COTIZADO";
    public static final String PROYECTO_APROBADO = "APROBADO";
    public static final String PROYECTO_EN_DISEÑO = "EN_DISEÑO";
    public static final String PROYECTO_EN_PRODUCCION = "EN_PRODUCCION";
    public static final String PROYECTO_INSTALACION = "INSTALACION_MONTAJE";
    public static final String PROYECTO_ENTREGADO = "ENTREGADO";
    public static final String PROYECTO_ANULADO = "ANULADO";

    public static final String PAGO_REGISTRADO = "REGISTRADO";
    public static final String PAGO_ANULADO = "ANULADO";

    public static final String RESENA_PENDIENTE = "PENDIENTE";
    public static final String RESENA_APROBADA = "APROBADA";
    public static final String RESENA_RECHAZADA = "RECHAZADA";

    public static final String PERSONA_NATURAL = "PERSONA_NATURAL";
    public static final String EMPRESA = "EMPRESA";

    public static final String MEDIO_PAGO_EFECTIVO = "EFECTIVO";
    public static final String MEDIO_PAGO_TRANSFERENCIA = "TRANSFERENCIA";
    public static final String MEDIO_PAGO_DEPOSITO = "DEPOSITO";

    public static final String ARCHIVO_RENDER = "RENDER";
    public static final String ARCHIVO_PLANO = "PLANO";
    public static final String ARCHIVO_BOCETO = "BOCETO";

    public static final double IGV_POR_DEFECTO = 18.0;
    public static final int VALIDEZ_DIAS_DEFECTO = 15;

    public static final String URL_LOGIN = "/login";
    public static final String URL_LOGOUT = "/logout";
    public static final String URL_DASHBOARD_ADMIN = "/internal/dashboard/admin";
    public static final String URL_DASHBOARD_CLIENTE = "/public/cliente/dashboard";
}