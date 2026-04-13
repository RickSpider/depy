package com.depy.modelo;

import java.util.Calendar;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name ="facturascuotas")
public class FacturaCuota {
	
	@Id
	@Column(name ="facturacuotaid")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long facturacuotaid;
	
	@ManyToOne
	@JoinColumn(name = "facturaid", nullable = false, updatable = false)
	private Factura factura;
	
	private String moneda;
	private Double monto;
	private Calendar vencimiento;

	public Long getFacturacuotaid() {
		return facturacuotaid;
	}
	public void setFacturacuotaid(Long facturacuotaid) {
		this.facturacuotaid = facturacuotaid;
	}
	public Factura getFactura() {
		return factura;
	}
	public void setFactura(Factura factura) {
		this.factura = factura;
	}
	public String getMoneda() {
		return moneda;
	}
	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}
	public Double getMonto() {
		return monto;
	}
	public void setMonto(Double monto) {
		this.monto = monto;
	}
	public Calendar getVencimiento() {
		return vencimiento;
	}
	public void setVencimiento(Calendar vencimiento) {
		this.vencimiento = vencimiento;
	}
	
	

}
