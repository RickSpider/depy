/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.depy.utilde.modelo;

/**
 *
 * @author BlackSpider
 */
public class DEDetalle {

	private String itemCodigo;
	private String itemDescripcion;
	private Long itemUndMedida;
	private String itemUndMedidaStr;

	private double cantidad;
	private double precioUnitario;

	private Long afectacionTributaria;
	private int proporcionIVA;
	private int tasaIVA;
	
	private Double descuento;
	private Double descuentoGlobal;
	    
	private Double anticipo;
	private Double anticipoGlobal;
	    
	private String cdcAnticipo;
	    
	private String dncpG;
	private String dncpE;
	

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

	public double getPrecioUnitario() {
		return precioUnitario;
	}

	public void setPrecioUnitario(double precioUnitario) {
		this.precioUnitario = precioUnitario;
	}

	public int getProporcionIVA() {
		return proporcionIVA;
	}

	public void setProporcionIVA(int proporcionIVA) {
		this.proporcionIVA = proporcionIVA;
	}

	public int getTasaIVA() {
		return tasaIVA;
	}

	public void setTasaIVA(int tasaIVA) {
		this.tasaIVA = tasaIVA;
	}

	public Long getItemUndMedida() {
		return itemUndMedida;
	}

	public void setItemUndMedida(Long itemUndMedida) {
		this.itemUndMedida = itemUndMedida;
	}

	public Long getAfectacionTributaria() {
		return afectacionTributaria;
	}

	/**
	 * Posibles valores
	 * 
	 * 1: Gravado IVA 2: Exonerado (Art. 83- Ley 125/91) 3: Exento 4: Gravado
	 * parcial (Grav-Exento)
	 * 
	 * @param afectacionTributaria
	 */
	public void setAfectacionTributaria(Long afectacionTributaria) {
		this.afectacionTributaria = afectacionTributaria;
	}

	public double getDescuento() {
		return descuento;
	}

	public String getItemUndMedidaStr() {
		return itemUndMedidaStr;
	}

	public void setItemUndMedidaStr(String itemUndMedidaStr) {
		this.itemUndMedidaStr = itemUndMedidaStr;
	}


	public Double getDescuentoGlobal() {
		return descuentoGlobal;
	}

	public void setDescuentoGlobal(Double descuentoGlobal) {
		this.descuentoGlobal = descuentoGlobal;
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

	public void setDescuento(Double descuento) {
		this.descuento = descuento;
	}

	public void setAnticipoGlobal(double anticipoGlobal) {
		this.anticipoGlobal = anticipoGlobal;
	}

	public String getCdcAnticipo() {
		return cdcAnticipo;
	}

	public void setCdcAnticipo(String cdcAnticipo) {
		this.cdcAnticipo = cdcAnticipo;
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
	
	
	

}
