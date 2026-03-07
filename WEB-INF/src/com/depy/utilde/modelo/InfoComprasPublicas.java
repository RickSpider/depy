package com.depy.utilde.modelo;

import java.util.Date;

public class InfoComprasPublicas {

	private String modalidad;
    private Long entidad;
    private Long ano;
    private Long secuencia;
    private Date fechaEmision;

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    public Long getEntidad() {
        return entidad;
    }

    public void setEntidad(Long entidad) {
        this.entidad = entidad;
    }

    public Long getAno() {
        return ano;
    }

    public void setAno(Long ano) {
        this.ano = ano;
    }

    public Long getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(Long secuencia) {
        this.secuencia = secuencia;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }
	
}
