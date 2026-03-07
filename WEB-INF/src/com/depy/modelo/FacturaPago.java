package com.depy.modelo;

import java.io.Serializable;

import com.doxacore.modelo.Tipo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "facturaspagos")
public class FacturaPago extends ModeloERP implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2871070305399267828L;

	@Id
	@Column(name = "facturapagoid")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long facturapagoid;

	@ManyToOne
	@JoinColumn(name = "facturaid", nullable = false, updatable = false)
	private Factura factura;

	@ManyToOne
	@JoinColumn(name = "tipopagoid", nullable = false, updatable = false)
	private Tipo pagoTipo;
	private Double monto;

	// seccion tarjeta
	private Long denominacionTarjeta;
	private Long formaProcesamientoTarjeta;
	private String procesadoraTarjeta;
	private String procesadoraRucTarjeta;
	private Integer procesadoraDVTarjeta;

	private Integer codigoAutorizacionTarjeta;
	private String tarjetaNombre;
	private Integer nroTarjeta;

	// seccion cheque
	private String chequeNro;
	private String chequeBanco;

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

	public Long getFacturapagoid() {
		return facturapagoid;
	}

	public void setFacturapagoid(Long facturapagoid) {
		this.facturapagoid = facturapagoid;
	}

	public Factura getFactura() {
		return factura;
	}

	public void setFactura(Factura factura) {
		this.factura = factura;
	}
	
	public Double getMonto() {
		return monto;
	}

	public void setMonto(Double monto) {
		this.monto = monto;
	}

	public Long getDenominacionTarjeta() {
		return denominacionTarjeta;
	}

	public void setDenominacionTarjeta(Long denominacionTarjeta) {
		this.denominacionTarjeta = denominacionTarjeta;
	}

	public Long getFormaProcesamientoTarjeta() {
		return formaProcesamientoTarjeta;
	}

	public void setFormaProcesamientoTarjeta(Long formaProcesamientoTarjeta) {
		this.formaProcesamientoTarjeta = formaProcesamientoTarjeta;
	}

	public String getProcesadoraTarjeta() {
		return procesadoraTarjeta;
	}

	public void setProcesadoraTarjeta(String procesadoraTarjeta) {
		this.procesadoraTarjeta = procesadoraTarjeta;
	}

	public String getProcesadoraRucTarjeta() {
		return procesadoraRucTarjeta;
	}

	public void setProcesadoraRucTarjeta(String procesadoraRucTarjeta) {
		this.procesadoraRucTarjeta = procesadoraRucTarjeta;
	}

	public Integer getProcesadoraDVTarjeta() {
		return procesadoraDVTarjeta;
	}

	public void setProcesadoraDVTarjeta(Integer procesadoraDVTarjeta) {
		this.procesadoraDVTarjeta = procesadoraDVTarjeta;
	}

	public Integer getCodigoAutorizacionTarjeta() {
		return codigoAutorizacionTarjeta;
	}

	public void setCodigoAutorizacionTarjeta(Integer codigoAutorizacionTarjeta) {
		this.codigoAutorizacionTarjeta = codigoAutorizacionTarjeta;
	}

	public String getTarjetaNombre() {
		return tarjetaNombre;
	}

	public void setTarjetaNombre(String tarjetaNombre) {
		this.tarjetaNombre = tarjetaNombre;
	}

	public Integer getNroTarjeta() {
		return nroTarjeta;
	}

	public void setNroTarjeta(Integer nroTarjeta) {
		this.nroTarjeta = nroTarjeta;
	}

	public String getChequeNro() {
		return chequeNro;
	}

	public void setChequeNro(String chequeNro) {
		this.chequeNro = chequeNro;
	}

	public String getChequeBanco() {
		return chequeBanco;
	}

	public void setChequeBanco(String chequeBanco) {
		this.chequeBanco = chequeBanco;
	}

	public Tipo getPagoTipo() {
		return pagoTipo;
	}

	public void setPagoTipo(Tipo pagoTipo) {
		this.pagoTipo = pagoTipo;
	}
	
	
	
}
