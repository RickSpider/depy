package com.depy.modelo;

import java.io.Serializable;

import com.doxacore.modelo.Usuario;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "EmpresasUsuarios",  
	indexes = {
        @Index(name = "idx_empresasusuarios_empresa_usuario", columnList = "empresaid, usuarioid")
	})
public class EmpresaUsuario extends ModeloERP implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4405699677949496907L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long empresausuarioid;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "usuarioid", nullable = false)
	private Usuario usuario;
	
	private Boolean actual = false;
	
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

	public Long getEmpresausuarioid() {
		return empresausuarioid;
	}

	public void setEmpresausuarioid(Long empresausuarioid) {
		this.empresausuarioid = empresausuarioid;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Boolean getActual() {
		return actual;
	}

	public void setActual(Boolean actual) {
		this.actual = actual;
	}

	

}
