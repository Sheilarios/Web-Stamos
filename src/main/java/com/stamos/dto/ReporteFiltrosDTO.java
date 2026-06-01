package com.stamos.dto;

/**
 * DTO para encapsular los filtros utilizados en la generación de reportes.
 * Especialmente útil para el reporte de ingresos por período (mensual/trimestral).
 */
public class ReporteFiltrosDTO {

    private String tipoPeriodo; // "MENSUAL" o "TRIMESTRAL"
    private Integer anio;
    private Integer mes;        // Solo para MENSUAL (1-12)
    private Integer trimestre;  // Solo para TRIMESTRAL (1-4)

    // Constructor por defecto
    public ReporteFiltrosDTO() {
    }

    // Constructor con campos principales
    public ReporteFiltrosDTO(String tipoPeriodo, Integer anio, Integer mes, Integer trimestre) {
        this.tipoPeriodo = tipoPeriodo;
        this.anio = anio;
        this.mes = mes;
        this.trimestre = trimestre;
    }

    // Getters y Setters
    public String getTipoPeriodo() {
        return tipoPeriodo;
    }

    public void setTipoPeriodo(String tipoPeriodo) {
        this.tipoPeriodo = tipoPeriodo;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getTrimestre() {
        return trimestre;
    }

    public void setTrimestre(Integer trimestre) {
        this.trimestre = trimestre;
    }

    @Override
    public String toString() {
        return "ReporteFiltrosDTO{" +
                "tipoPeriodo='" + tipoPeriodo + '\'' +
                ", anio=" + anio +
                ", mes=" + mes +
                ", trimestre=" + trimestre +
                '}';
    }
}