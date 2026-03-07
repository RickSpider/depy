package com.depy.searchModel;

import com.doxacore.modelo.Tipo;

public class TipoSM {

	private long id;
	private String nombre;
	private Long codeExtra;
	
	public TipoSM(long id, String nombre) {
		super();
		this.id = id;
		this.nombre = nombre;
	}

	public TipoSM(long id, String nombre, Long codeExtra) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.codeExtra = codeExtra;
	}
	
	public TipoSM(Tipo t) {
		
		this.id = t.getTipoid();
		this.nombre = t.getTipo();
		
		if (t.getCodeExtra() != null) {
			this.codeExtra = this.getCodeExtra();
		}
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public Long getCodeExtra() {
		return codeExtra;
	}

	public void setCodeExtra(Long codeExtra) {
		this.codeExtra = codeExtra;
	}

	public Tipo getTipo() {
		
		Tipo out = new Tipo();
		out.setTipoid(this.id);
		
		return out;
		
	}

	@Override
	public String toString() {
		return this.nombre;
	}
	
}
