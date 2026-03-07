package com.depy.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name ="notacreditodetalles")
public class NotaCreditoDetalle extends DocumentoDetalle {
	
	

	@Id
	@Column(name ="notacreditodetalleid")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long notacreditodetalleid;
	
	@ManyToOne
	@JoinColumn(name = "notacreditoid", nullable = false, updatable = false)
	private NotaCredito notacredito;
	
	public NotaCreditoDetalle() {
		super();
		
	}
	
	public NotaCreditoDetalle(NotaCredito notacredito, Integer iva, Long afectacionTributaria, Integer proporcionIva, Empresa empresa ) {
		super();
		this.notacredito = notacredito;
		this.tasaIva = iva;
		this.afectacionTributaria = afectacionTributaria;
		this.proporcionIva = proporcionIva;
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

	public Long getNotacreditodetalleid() {
		return notacreditodetalleid;
	}

	public void setNotacreditodetalleid(Long notacreditodetalleid) {
		this.notacreditodetalleid = notacreditodetalleid;
	}

	public NotaCredito getNotacredito() {
		return notacredito;
	}

	public void setNotacredito(NotaCredito notacredito) {
		this.notacredito = notacredito;
	}

	
	
	

}
