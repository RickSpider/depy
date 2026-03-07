package com.depy.modelo;

import java.io.Serializable;

import com.doxacore.modelo.Usuario;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="sucursalesusuarios",
	indexes = {
        @Index(name = "idx_sucursalusuario_empresa_sucursal_usuario", columnList = "empresaid, sucursalid ,usuarioid")
        })
public class SucursalUsuario extends ModeloERP implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -854546937783122321L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long sucursalusuarioid;

	@ManyToOne
	@JoinColumn(name = "usuarioid", nullable = false)
	private Usuario usuario;
	
	@ManyToOne
	@JoinColumn(name = "sucursalid", nullable = false)
	private Sucursal sucursal;
	
	private String puntoExpedicion;
	
	private boolean actual = false;

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

	
	public Long getSucursalusuarioid() {
		return sucursalusuarioid;
	}

	public void setSucursalusuarioid(Long sucursalusuarioid) {
		this.sucursalusuarioid = sucursalusuarioid;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Sucursal getSucursal() {
		return sucursal;
	}

	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}

	public String getPuntoExpedicion() {
		return puntoExpedicion;
	}

	public void setPuntoExpedicion(String puntoExpedicion) {
		this.puntoExpedicion = puntoExpedicion;
	}

	public boolean isActual() {
		return actual;
	}

	public void setActual(boolean actual) {
		this.actual = actual;
	}

	
	
	
}
