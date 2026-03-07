package com.depy.sistema.empresa;

import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zul.Window;

import com.depy.modelo.EmpresaUsuario;
import com.depy.util.ParamsLocal;
import com.depy.util.TemplateViewModelLocal;
import com.doxacore.modelo.Rol;
import com.doxacore.modelo.Usuario;
import com.doxacore.modelo.UsuarioRol;
import com.doxacore.util.UtilStaticMetodos;

public class EmpresaUsuarioVM extends TemplateViewModelLocal{
	
	private List<Object[]> lEmpresasUsuarios;
	private List<Object[]> lEmpresasUsuariosOri;
	private EmpresaUsuario empresaUsuarioSelected;
	
	private boolean opCrearEmpresaUsuario;
	private boolean opEditarEmpresaUsuario;
	private boolean opBorrarEmpresaUsuario;

	private boolean editar = false;

	@Init(superclass = true)
	public void initEmpresaUsuarioVM() {

		this.cargarEmpresasUsuarios();
		this.inicializarFiltros();
	}

	@AfterCompose(superclass = true)
	public void afterComposeEmpresaUsuarioVM() {

	}

	
	@Override
	protected void inicializarOperaciones() {
		this.opCrearEmpresaUsuario = this.operacionHabilitada(ParamsLocal.OP_CREAR_EMPRESAUSUARIO);
		this.opEditarEmpresaUsuario = this.operacionHabilitada(ParamsLocal.OP_EDITAR_EMPRESAUSUARIO);
		this.opBorrarEmpresaUsuario = this.operacionHabilitada(ParamsLocal.OP_BORRAR_EMPRESAUSUARIO);
		
	}
	
	private String filtroColumns[];

	private void inicializarFiltros() {

		this.filtroColumns = new String[3];

		for (int i = 0; i < this.filtroColumns.length; i++) {

			this.filtroColumns[i] = "";

		}

	}

	@Command
	@NotifyChange("lEmpresasUsuarios")
	public void filtrarEmpresaUsuario() {

		this.lEmpresasUsuarios = this.filtrarListaObject(this.filtroColumns, this.lEmpresasUsuariosOri);

	}

	private void cargarEmpresasUsuarios() {

		String sql = this.um.getSql("empresaUsuario/listaEmpresaUsuario.sql").replace("?1", this.getCurrentEmpresa().getEmpresaid()+"");
		this.lEmpresasUsuarios = this.reg.sqlNativo(sql);
		this.lEmpresasUsuariosOri = this.lEmpresasUsuarios;

	}
	
	//seccion modal
	
	private Window modal;
	
	@Command
	public void modalEmpresaUsuarioAgregar() {

		if (!this.opCrearEmpresaUsuario)
			return;

		this.editar = false;
		this.modalEmpresaUsuario(-1);

	}

	@Command
	public void modalEmpresaUsuario(@BindingParam("empresaUsuarioid") long empresaUsuarioid) {

		if (empresaUsuarioid != -1) {

			if (!this.opEditarEmpresaUsuario)
				return;

			this.editar = true;

			this.empresaUsuarioSelected = this.reg.findObjectById(EmpresaUsuario.class, empresaUsuarioid);

		} else {

			this.empresaUsuarioSelected = new EmpresaUsuario();
			this.empresaUsuarioSelected.setEmpresa(getCurrentEmpresa());
			this.empresaUsuarioSelected.setUsuario(new Usuario());
		
			
		}

		modal = (Window) Executions.createComponents("/sistema/zul/empresa/empresaUsuarioModal.zul", this.mainComponent, null);
		Selectors.wireComponents(modal, this, false);
		modal.doModal();

	}
	
	@Command
	@NotifyChange("lEmpresasUsuarios")
	public void guardar() {
		
		if (this.empresaUsuarioSelected.getUsuario().getUsuarioid() == null ) {
			
			this.empresaUsuarioSelected.getUsuario().setPassword(UtilStaticMetodos.getSHA256(this.empresaUsuarioSelected.getUsuario().getAccount()));
			
		}
		this.empresaUsuarioSelected.setActual(true);
		this.empresaUsuarioSelected = this.save(this.empresaUsuarioSelected);

		this.cargarEmpresasUsuarios();

		this.modal.detach();

		if (editar) {

			Notification.show("EmpresaUsuario Actualizada.");

			this.editar = false;

		} else {
			
			Rol operador = this.reg.getObjectByColumn(Rol.class, "rol", ParamsLocal.ROL_OPERADOR );
			UsuarioRol ur = new UsuarioRol();
			ur.setUsuario(this.empresaUsuarioSelected.getUsuario());
			ur.setRol(operador);
			this.save(ur);
			

			Notification.show("Los datos de la Nueva EmpresaUsuario fueron agragados.");
		}

	}
	
	@Command
	@NotifyChange("empresaUsuarioSelected")
	public void onChangeUsuario() {
		
		Usuario u = this.reg.getObjectByColumn(Usuario.class, "account", this.empresaUsuarioSelected.getUsuario().getAccount());
		
		if (u != null) {
			
			this.empresaUsuarioSelected.setUsuario(u);
			
		}
	}

	public List<Object[]> getlEmpresasUsuarios() {
		return lEmpresasUsuarios;
	}

	public void setlEmpresasUsuarios(List<Object[]> lEmpresasUsuarios) {
		this.lEmpresasUsuarios = lEmpresasUsuarios;
	}

	public EmpresaUsuario getEmpresaUsuarioSelected() {
		return empresaUsuarioSelected;
	}

	public void setEmpresaUsuarioSelected(EmpresaUsuario empresaUsuarioSelected) {
		this.empresaUsuarioSelected = empresaUsuarioSelected;
	}

	public boolean isOpCrearEmpresaUsuario() {
		return opCrearEmpresaUsuario;
	}

	public void setOpCrearEmpresaUsuario(boolean opCrearEmpresaUsuario) {
		this.opCrearEmpresaUsuario = opCrearEmpresaUsuario;
	}

	public boolean isOpEditarEmpresaUsuario() {
		return opEditarEmpresaUsuario;
	}

	public void setOpEditarEmpresaUsuario(boolean opEditarEmpresaUsuario) {
		this.opEditarEmpresaUsuario = opEditarEmpresaUsuario;
	}

	public boolean isOpBorrarEmpresaUsuario() {
		return opBorrarEmpresaUsuario;
	}

	public void setOpBorrarEmpresaUsuario(boolean opBorrarEmpresaUsuario) {
		this.opBorrarEmpresaUsuario = opBorrarEmpresaUsuario;
	}

	public boolean isEditar() {
		return editar;
	}

	public void setEditar(boolean editar) {
		this.editar = editar;
	}

	public String[] getFiltroColumns() {
		return filtroColumns;
	}

	public void setFiltroColumns(String[] filtroColumns) {
		this.filtroColumns = filtroColumns;
	}

}
