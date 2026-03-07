package com.depy.modelo;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;

public class Autofactura extends Documento {

	@OneToMany(mappedBy = "Autofactura", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<AutofacturaDetalle> detalles;

	@Override
	public List<AutofacturaDetalle> getDetalles() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
