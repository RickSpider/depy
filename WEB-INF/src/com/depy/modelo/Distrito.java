/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.depy.modelo;

import java.io.Serializable;


import com.doxacore.modelo.Departamento;
import com.doxacore.modelo.Modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 *
 * @author BlackSpider
 */

@Entity
@Table(name = "Distritos",
	indexes = {
		@Index(name = "idx_localidad_departamento", columnList = "departamentoid")
		})
public class Distrito extends Modelo implements Serializable {
    
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 4004129898202936144L;
	
	@Id
    private Long distritoid;
    private String distrito;
    private Long codigoSifen;
    
    @ManyToOne
    @JoinColumn(name = "departamentoid")
    private Departamento departamento;

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public Long getDistritoid() {
        return distritoid;
    }

    public void setDistritoid(Long distritoid) {
        this.distritoid = distritoid;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public Long getCodigoSifen() {
        return codigoSifen;
    }

    public void setCodigoSifen(Long codigoSifen) {
        this.codigoSifen = codigoSifen;
    }

	@Override
	public Object[] getArrayObjectDatos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStringDatos() {
		// TODO Auto-generated method stub
		return null;
	}
    
    
}
