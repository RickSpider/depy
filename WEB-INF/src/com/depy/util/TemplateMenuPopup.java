package com.depy.util;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Window;

import com.depy.modelo.Empresa;
import com.depy.modelo.EmpresaUsuario;
import com.depy.modelo.SucursalUsuario;
import com.doxacore.login.UsuarioCredencial;
import com.doxacore.modelo.Usuario;
import com.doxacore.util.Register;
import com.doxacore.util.UtilControlOperaciones;
import com.doxacore.util.UtilMetodos;

public class TemplateMenuPopup {

	protected Register reg;
	protected Component mainComponent;
	protected UtilControlOperaciones uco;
	protected UtilMetodos um;
	protected Window windowModal;

	@Init
	public void initTemplateMenuPopup( @ContextParam(ContextType.COMPONENT) Window window ) {

		this.reg = new Register();
		this.uco = new UtilControlOperaciones();
		this.um = new UtilMetodos();
		this.windowModal = window;

	}

	@AfterCompose
	public void afterComposeTemplateMenuPopup() {

	}
	
	protected Usuario getCurrentUser() {

		UsuarioCredencial usuarioCredencial = (UsuarioCredencial) Sessions.getCurrent().getAttribute("userCredential");

		Usuario currentUser = this.reg.getObjectByColumn(Usuario.class, "account",
				usuarioCredencial.getAccount());

		return currentUser;

	}
	
	protected EmpresaUsuario getCurrentEmpresaUsuario() {
		
		String campo[] = {"usuario", "actual"};
		Object[] value = {this.getCurrentUser(), true};
		
		return this.reg.getObjectByColumns(EmpresaUsuario.class, campo, value);
		
	}
	
	protected Empresa getCurrentEmpresa() {
		
		return this.getCurrentEmpresaUsuario().getEmpresa();
	}
	
	protected SucursalUsuario getCurrentSucursalUsuario() {
		
		EmpresaUsuario eu = this.getCurrentEmpresaUsuario();
		
		String campo[] = {"empresa","usuario", "actual"};
		Object[] value = {eu.getEmpresa(),eu.getUsuario(), true};
		
		return this.reg.getObjectByColumns(SucursalUsuario.class, campo, value);
		
	}
	
	protected List<Object[]> filtrarListaObject(String filtro, List<Object[]> listOri) {

		//System.out.println("VOY A FILTRAR " + listOri.size());

		List<Object[]> aux = new ArrayList<Object[]>();

		if (filtro.length() > 0) {

			for (Object[] x : listOri) {

				StringBuffer sbConcat = new StringBuffer();

				for (int i = 0; i < x.length; i++) {

					sbConcat.append(x[i]);
					sbConcat.append(" ");

				}

				if (sbConcat.toString().toUpperCase().contains(filtro.toUpperCase())) {

					aux.add(x);
					// System.out.println("FILTRANDO "+sbConcat.toString().toUpperCase());
					// System.out.println("FILTRO "+filtro.toUpperCase());

				}

			}

		} else {

			//System.out.println("NO FILTRE NADA");
			aux = listOri;

		}

		return aux;
	}

}
