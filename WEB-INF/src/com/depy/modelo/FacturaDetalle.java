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
@Table(name ="facturasdetalles")
public class FacturaDetalle extends DocumentoDetalle implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8321012325489678576L;

	@Id
	@Column(name ="faturadetalleid")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long faturadetalleid;
	
	@ManyToOne
	@JoinColumn(name = "facturaid", nullable = false, updatable = false)
	private Factura factura;
	
	
	private Double descuento;
	private Double descuentoGolbal;
	private Double anticipo;
	private Double anticipoGlobal;

	
	
	
	public FacturaDetalle() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public FacturaDetalle(Factura factura, Integer iva, Long afectacionTributaria, Integer proporcionIva, Empresa empresa ) {
		super();
		this.factura = factura;
		this.tasaIva = iva;
		this.afectacionTributaria = afectacionTributaria;
		this.proporcionIva = proporcionIva;
		this.empresa = empresa;
	}
	
	public Long getFaturadetalleid() {
		return faturadetalleid;
	}

	public void setFaturadetalleid(Long faturadetalleid) {
		this.faturadetalleid = faturadetalleid;
	}

	public Factura getFactura() {
		return factura;
	}
	public void setFactura(Factura factura) {
		this.factura = factura;
	}
	public Double getDescuento() {
		return descuento;
	}
	public void setDescuento(Double descuento) {
		this.descuento = descuento;
	}
	public Double getDescuentoGolbal() {
		return descuentoGolbal;
	}
	public void setDescuentoGolbal(Double descuentoGolbal) {
		this.descuentoGolbal = descuentoGolbal;
	}
	public Double getAnticipo() {
		return anticipo;
	}
	public void setAnticipo(Double anticipo) {
		this.anticipo = anticipo;
	}
	public Double getAnticipoGlobal() {
		return anticipoGlobal;
	}
	public void setAnticipoGlobal(Double anticipoGlobal) {
		this.anticipoGlobal = anticipoGlobal;
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
