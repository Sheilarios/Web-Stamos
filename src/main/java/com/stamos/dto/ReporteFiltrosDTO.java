package com.stamos.dto;
public class ReporteFiltrosDTO {

    private String tipoPeriodo; 
    private Integer anio;
    private Integer mes;        
    private Integer trimestre;  

    public ReporteFiltrosDTO() {
    }

    public ReporteFiltrosDTO(String tipoPeriodo, Integer anio, Integer mes, Integer trimestre) {
        this.tipoPeriodo = tipoPeriodo;
        this.anio = anio;
        this.mes = mes;
        this.trimestre = trimestre;
    }

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