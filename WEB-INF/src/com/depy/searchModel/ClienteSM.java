package com.depy.searchModel;

import com.depy.modelo.Cliente;

public class ClienteSM {

	private Long id;
	private String documentoNro;
	private String razonSocial;
	private String direccion;

	public ClienteSM(Long id, String documentoNro, String razonSocial, String direccion) {
		super();
		this.id = id;
		this.documentoNro = documentoNro;
		this.razonSocial = razonSocial;
		this.direccion = direccion;
	}
	
	
	public ClienteSM(Cliente c) {
		super();
		this.id = c.getClienteid();
		this.razonSocial = c.getRazonsocial();
		this.documentoNro = c.getDocumentoNro();
		this.direccion = c.getDireccion() != null ? c.getDireccion():null;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDocumentoNro() {
		return documentoNro;
	}
	public void setRuc(String ruc) {
		this.documentoNro = ruc;
	}
	public String getRazonSocial() {
		return razonSocial;
	}
	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
	
	public Cliente getCliente() {
		
		Cliente c = new Cliente();
		c.setClienteid(this.id);
		c.setDocumentoNro(documentoNro);
		c.setRazonsocial(razonSocial);
		c.setDireccion(this.direccion != null ? this.direccion : null);		
		return c;
	}
	
	@Override
	public String toString() {
		return this.documentoNro;
	}


	public String getDireccion() {
		return direccion;
	}


	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	
	
	
}
