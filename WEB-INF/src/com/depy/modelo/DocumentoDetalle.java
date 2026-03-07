package com.depy.modelo;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class DocumentoDetalle extends ModeloERP{
	
	protected String itemCodigo;
	protected String itemDescripcion;
	protected double cantidad = 1;
	protected Double precioUnitario;
	
	@ManyToOne
	@JoinColumn(name = "unidadmediaid")
	protected UnidadMedida unidadMedida;
	protected Long afectacionTributaria;
	protected Integer proporcionIva;
	protected Integer tasaIva;
	
	protected String dncpG;
	protected String dncpE;
		
	public String getItemCodigo() {
		return itemCodigo;
	}
	public void setItemCodigo(String itemCodigo) {
		this.itemCodigo = itemCodigo;
	}
	public String getItemDescripcion() {
		return itemDescripcion;
	}
	public void setItemDescripcion(String itemDescripcion) {
		this.itemDescripcion = itemDescripcion;
	}
	public double getCantidad() {
		return cantidad;
	}
	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
	}
	public Double getPrecioUnitario() {
		return precioUnitario;
	}
	public void setPrecioUnitario(Double precioUnitario) {
		this.precioUnitario = precioUnitario;
	}
	public UnidadMedida getUnidadMedida() {
		return unidadMedida;
	}
	public void setUnidadMedida(UnidadMedida unidadMedida) {
		this.unidadMedida = unidadMedida;
	}
	
	public Long getAfectacionTributaria() {
		return afectacionTributaria;
	}
	public void setAfectacionTributaria(Long afectacionTributaria) {
		this.afectacionTributaria = afectacionTributaria;
	}
	public Integer getProporcionIva() {
		return proporcionIva;
	}
	public void setProporcionIva(Integer proporcionIva) {
		this.proporcionIva = proporcionIva;
	}
	public Integer getTasaIva() {
		return tasaIva;
	}
	public void setTasaIva(Integer tasaIva) {
		this.tasaIva = tasaIva;
	}
	public String getDncpG() {
		return dncpG;
	}
	public void setDncpG(String dncpG) {
		this.dncpG = dncpG;
	}
	public String getDncpE() {
		return dncpE;
	}
	public void setDncpE(String dncpE) {
		this.dncpE = dncpE;
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
