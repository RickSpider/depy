package com.depy.searchModel;

public class FacturaSM {
	
	private Long facturaid;
	private String facturaNro;
	private String documentoNro;
	private String razonSocial;
	private String fecha;
	private double totalDetalle;
	private String moneda;
	private double monedaCambio;
	
	public FacturaSM(Long facturaid, String facturaNro ,String documentoNro, String razonSocial, String fecha, double totalDetalle ,String moneda, double monedaCambio) {
		super();
		this.facturaid = facturaid;
		this.facturaNro = facturaNro;
		this.documentoNro = documentoNro;
		this.razonSocial = razonSocial;
		this.fecha = fecha;
		this.totalDetalle = totalDetalle;
		this.moneda = moneda;
		this.monedaCambio = monedaCambio;
	}
	
	public Long getFacturaid() {
		return facturaid;
	}
	public void setFacturaid(Long facturaid) {
		this.facturaid = facturaid;
	}
	public String getDocumentoNro() {
		return documentoNro;
	}
	public void setDocumentoNro(String documentoNro) {
		this.documentoNro = documentoNro;
	}
	public String getRazonSocial() {
		return razonSocial;
	}
	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	
	public String getFacturaNro() {
		return facturaNro;
	}

	public void setFacturaNro(String facturaNro) {
		this.facturaNro = facturaNro;
	}

	public double getTotalDetalle() {
		return totalDetalle;
	}

	public void setTotalDetalle(double totalDetalle) {
		this.totalDetalle = totalDetalle;
	}

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

	public double getMonedaCambio() {
		return monedaCambio;
	}

	public void setMonedaCambio(double monedaCambio) {
		this.monedaCambio = monedaCambio;
	}

	@Override
	public String toString() {
		return this.facturaNro;
	}

}
