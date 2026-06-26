package com.stamos.controller;

import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class BaseController {

    protected String formatearFecha(LocalDate fecha) {
        if (fecha == null) return "-";
        return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    protected String formatearFechaHora(LocalDateTime fecha) {
        if (fecha == null) return "-";
        return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    protected String formatearMoneda(BigDecimal monto) {
        if (monto == null) return "S/ 0.00";
        return "S/ " + monto.setScale(2, RoundingMode.HALF_UP);
    }

    protected String truncarTexto(String texto, int maxLength) {
        if (texto == null) return "";
        if (texto.length() <= maxLength) return texto;
        return texto.substring(0, maxLength) + "...";
    }

    protected String getEstadoBadgeClass(String estado) {
        if (estado == null) return "status-pendiente";
        return switch (estado) {
            case "Activo" -> "status-validado";
            case "Inactivo" -> "status-rechazado";
            case "Aprobado" -> "status-aprobado";
            case "En Diseño" -> "status-diseno";
            case "En Producción" -> "status-produccion";
            case "Instalación/Montaje" -> "status-instalacion";
            case "Entregado" -> "status-entregado";
            case "Anulado" -> "status-anulado";
            default -> "status-pendiente";
        };
    }

    protected String getEstadoColor(String estado) {
        if (estado == null) return "#6B7280";
        return switch (estado) {
            case "Activo", "Validado" -> "#10B981";
            case "Inactivo", "Rechazado" -> "#EF4444";
            case "Aprobado" -> "#1A56DB";
            case "En Diseño" -> "#F59E0B";
            case "En Producción" -> "#3B82F6";
            case "Instalación/Montaje" -> "#8B5CF6";
            case "Entregado" -> "#10B981";
            case "Anulado" -> "#EF4444";
            default -> "#6B7280";
        };
    }

    protected void addCommonAttributes(Model model) {
        model.addAttribute("fechaActual", LocalDateTime.now());
        model.addAttribute("anioActual", LocalDateTime.now().getYear());
    }
}