package com.depy.searchModel;

import com.depy.modelo.Localidad;

public class LocalidadSM {

	private Long localidadid;
	private String localidad;
	private Long distritoid;
	private String distrito;
	private Long departamentoid;
	private String departamento;
	
	
	
	
	public LocalidadSM(Long localidadid, String localidad, Long distritoid, String distrito, Long departamentoid,
			String departamento) {
		super();
		this.localidadid = localidadid;
		this.localidad = localidad;
		this.distritoid = distritoid;
		this.distrito = distrito;
		this.departamentoid = departamentoid;
		this.departamento = departamento;
	}
	
	
	public LocalidadSM(Localidad l) {
		
		this.localidadid = l.getLocalidadid();
		this.localidad = l.getLocalidad();
		this.distritoid = l.getDistrito().getDistritoid();
		this.distrito = l.getDistrito().getDistrito();
		this.departamentoid = l.getDistrito().getDepartamento().getDepartamentoid();
		this.departamento = l.getDistrito().getDepartamento().getDepartamento();;
		
	}
	
	public Long getLocalidadid() {
		return localidadid;
	}
	public void setLocalidadid(Long localidadid) {
		this.localidadid = localidadid;
	}
	public String getLocalidad() {
		return localidad;
	}
	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}
	public Long getDistritoid() {
		return distritoid;
	}
	public void setDistritoid(Long distritoid) {
		this.distritoid = distritoid;
	}
	public String getDistrito() {
		return distrito;
	}
	public void setDistrito(String distrito) {
		this.distrito = distrito;
	}
	public Long getDepartamentoid() {
		return departamentoid;
	}
	public void setDepartamentoid(Long departamentoid) {
		this.departamentoid = departamentoid;
	}
	public String getDepartamento() {
		return departamento;
	}
	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}
	
	@Override
	public String toString() {
		return this.localidadid+" - "+this.localidad;
	}
	
}
