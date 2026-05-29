package com.depy.sistemaResp;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Sessions;

import com.depy.modelo.Empresa;
import com.depy.modelo.SucursalUsuario;
import com.doxacore.login.UsuarioCredencial;
import com.doxacore.modelo.Modelo;
import com.doxacore.modelo.SistemaPropiedad;
import com.doxacore.modelo.Usuario;
import com.doxacore.util.Register;
import com.doxacore.util.UtilMetodos;

public class TemplateViewModelLocalResp {

	protected UtilMetodos um;
	protected Register reg;
	protected SucursalUsuario su;
	
	
	@Init(superclass = true)
	public void initTemplateViewModelLocalResp() {
		
		this.reg = new Register();
		this.um = new UtilMetodos();
		this.su =  this.reg.getObjectByColumns(SucursalUsuario.class, new String[]{"empresa","usuario", "actual"}, new Object[]{this.getCurrentEmpresa(),this.getCurrentUser(), true});
		
	}
	
	protected Empresa getCurrentEmpresa() {
		
		Empresa out = new Empresa();
		out.setEmpresaid((Long) Sessions.getCurrent().getAttribute("empresaid"));
		
		return out;
		//return this.getCurrentEmpresaUsuario().getEmpresa();
	}
	
	protected Usuario getCurrentUser() {

		UsuarioCredencial usuarioCredencial = (UsuarioCredencial) Sessions.getCurrent().getAttribute("userCredential");

		Usuario currentUser = this.reg.getObjectByColumn(Usuario.class, "account",
				usuarioCredencial.getAccount());

		return currentUser;

	}
	
	protected <T extends Modelo> T save(T m) {

		return this.reg.saveObject(m, getCurrentUser().getAccount());

	}
	
	protected SistemaPropiedad getSistemaPropiedad(String clave) {

		return this.reg.getObjectByColumn(SistemaPropiedad.class, "clave", clave);
		
	}

	public SucursalUsuario getSu() {
		return su;
	}

	public void setSu(SucursalUsuario su) {
		this.su = su;
	}
	
	
}
