package com.depy.modelo;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "remisionesdetalles")
public class RemisionDetalle extends DocumentoDetalle implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2980215426007801994L;
	
	

	@Id
	@Column(name ="remisiondetalleid")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long remisiondetalleid;
	
	@ManyToOne
	@JoinColumn(name = "remisionid", nullable = false, updatable = false)
	private Remision remision;
	
	public RemisionDetalle() {
		super();
		
	}

	public RemisionDetalle(Remision remision, Empresa empresa) {
		super();
		this.remision = remision;
		this.empresa = empresa;
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

	public Long getRemisiondetalleid() {
		return remisiondetalleid;
	}

	public void setRemisiondetalleid(Long remisiondetalleid) {
		this.remisiondetalleid = remisiondetalleid;
	}

	public Remision getRemision() {
		return remision;
	}

	public void setRemision(Remision remision) {
		this.remision = remision;
	}

	
	

}
