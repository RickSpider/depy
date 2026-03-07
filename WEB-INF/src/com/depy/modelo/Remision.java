package com.depy.modelo;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;

public class Remision extends Documento {

	@OneToMany(mappedBy = "remision", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<RemisionDetalle> detalles;

	@Override
	public List<RemisionDetalle> getDetalles() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
