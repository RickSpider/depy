package com.depy.modelo;

import com.doxacore.modelo.Modelo;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ModeloERP extends Modelo  {

	@ManyToOne
	@JoinColumn(name = "empresaid")
	protected Empresa empresa;

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	
	
	
	
}
