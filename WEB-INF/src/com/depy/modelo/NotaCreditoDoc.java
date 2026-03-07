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
@Table(name ="notacreditodocs")
public class NotaCreditoDoc extends ModeloERP implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7672200513257106884L;

	@Id
	@Column(name ="notacreditodocid")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long notacreditodocid;
	
	@ManyToOne
	@JoinColumn(name = "notacreditoid", nullable = false, updatable = false)
	private NotaCredito notacredito;
	
	@ManyToOne
	@JoinColumn(name = "factura", nullable = false, updatable = false)
	private Factura factura;


	public NotaCreditoDoc() {
		super();
	}


	public NotaCreditoDoc(NotaCredito notacredito, Factura factura, Empresa empresa) {
		super();
		this.notacredito = notacredito;
		this.factura = factura;
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

	public Long getNotacreditodocid() {
		return notacreditodocid;
	}

	public void setNotacreditodocid(Long notacreditodocid) {
		this.notacreditodocid = notacreditodocid;
	}

	public NotaCredito getNotacredito() {
		return notacredito;
	}

	public void setNotacredito(NotaCredito notacredito) {
		this.notacredito = notacredito;
	}


	public Factura getFactura() {
		return factura;
	}


	public void setFactura(Factura factura) {
		this.factura = factura;
	}
	
	

}
