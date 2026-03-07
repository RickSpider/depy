package com.depy.modelo;

import java.io.Serializable;

import com.doxacore.modelo.Modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "unidadmedida")
public class UnidadMedida extends Modelo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4236154283751618244L;
	
	@Id
	@Column(name ="unidadmedidaid")
	private long unidadmedidaid;
	private String abreviatura;
	private String nombre;
	private String descripcion;
	
	public long getUnidadmedidaid() {
		return unidadmedidaid;
	}
	public void setUnidadmedidaid(long unidadmedidaid) {
		this.unidadmedidaid = unidadmedidaid;
	}
	public String getAbreviatura() {
		return abreviatura;
	}
	public void setAbreviatura(String abreviatura) {
		this.abreviatura = abreviatura;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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
