package com.depy.sistema.menuPopup;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;

import com.depy.modelo.EmpresaUsuario;
import com.depy.modelo.SucursalUsuario;
import com.depy.util.TemplateMenuPopup;
import com.doxacore.components.finder.FinderModel;
import com.doxacore.login.UsuarioCredencial;


public class CambiarEmpresaModalVM extends TemplateMenuPopup {
	
	private EmpresaUsuario empresaUsuarioSelected;
	
	
	
	@Init(superclass = true)
	public void initTemplateMenuPopup() {
		
		this.inicializarFinders();
		this.empresaUsuarioSelected = this.getCurrentEmpresaUsuario();
	}
	
	@AfterCompose(superclass = true)
	public void afterComposeTemplateMenuPopup() {

	}
	
	
	
	private FinderModel empresaFinder;

	@NotifyChange("*")
	public void inicializarFinders() {

		String sqlUsuario = this.um.getSql("empresaUsuario/buscarEmpresaPorUsuario.sql").replace("?1", this.getCurrentUser().getUsuarioid()+"" );
		empresaFinder = new FinderModel("EmpresaUsuario", sqlUsuario);
		
		
	}

	public void generarFinders(@BindingParam("finder") String finder) {

		if (finder.compareTo(this.empresaFinder.getNameFinder()) == 0) {

			this.empresaFinder.generarListFinder();
			BindUtils.postNotifyChange(null, null, this.empresaFinder, "listFinder");

			return;
		}
		

	}

	@Command
	public void finderFilter(@BindingParam("filter") String filter, @BindingParam("finder") String finder) {

		if (finder.compareTo(this.empresaFinder.getNameFinder()) == 0) {

			this.empresaFinder.setListFinder(this.filtrarListaObject(filter, this.empresaFinder.getListFinderOri()));
			BindUtils.postNotifyChange(null, null, this.empresaFinder, "listFinder");

			return;
		}
		
	
	}

	@Command
	@NotifyChange({"empresaUsuarioSelected"})
	public void onSelectetItemFinder(@BindingParam("id") Long id, @BindingParam("finder") String finder) {

		if (finder.compareTo(this.empresaFinder.getNameFinder()) == 0) {

			this.empresaUsuarioSelected = this.reg.findObjectById(EmpresaUsuario.class, id);
			return;
			
		}


	}
	
	@Command
	public void guardar() {
		
		
		EmpresaUsuario euOld = getCurrentEmpresaUsuario();
		if (euOld != null) {
			
			euOld.setActual(false);
			this.reg.saveObject(euOld, euOld.getUsuario().getAccount());
			
		}		
		
		this.empresaUsuarioSelected.setActual(true);
		this.reg.saveObject(empresaUsuarioSelected, empresaUsuarioSelected.getUsuario().getAccount());
		
		UsuarioCredencial usuarioCredencial = (UsuarioCredencial) Sessions.getCurrent().getAttribute("userCredential");
		
		
		usuarioCredencial.setExtra2(this.empresaUsuarioSelected.getEmpresa().getRazonSocial());
		
		Sessions.getCurrent().setAttribute("empresaid", this.empresaUsuarioSelected.getEmpresa().getEmpresaid());	
		
		SucursalUsuario su = this.getCurrentSucursalUsuario();
		
		
		if (su != null) {
			
			Sessions.getCurrent().setAttribute("sucursalid", su.getSucursal().getSucursalid());	
			usuarioCredencial.setExtra(su.getSucursal().getNombre());
		}
		
		Sessions.getCurrent().setAttribute("userCredential", usuarioCredencial);
		this.windowModal.detach();
		Clients.evalJavaScript("window.location.reload();");
	
	}
	
	@Command
	 public void cerrarModal() {
		
        this.windowModal.detach();
        
    }

	public EmpresaUsuario getEmpresaUsuarioSelected() {
		return empresaUsuarioSelected;
	}

	public void setEmpresaUsuarioSelected(EmpresaUsuario empresaUsuarioSelected) {
		this.empresaUsuarioSelected = empresaUsuarioSelected;
	}

	public FinderModel getEmpresaFinder() {
		return empresaFinder;
	}

	public void setEmpresaFinder(FinderModel empresaFinder) {
		this.empresaFinder = empresaFinder;
	}

	
	
	

}
