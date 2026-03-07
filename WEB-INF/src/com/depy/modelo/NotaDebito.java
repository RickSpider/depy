package com.depy.modelo;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;

public class NotaDebito extends Documento {

	@OneToMany(mappedBy = "notadebito", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<NotaDebitoDetalle> detalles;

	@Override
	public List<NotaDebitoDetalle> getDetalles() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
