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

public class CambiarSucursalModalVM extends TemplateMenuPopup {
	
	private SucursalUsuario sucursalUsuarioSelected;

	
	@Init(superclass = true)
	public void initTemplateMenuPopup() {
		
		this.inicializarFinders();
		this.sucursalUsuarioSelected = this.getCurrentSucursalUsuario();
	}
	
	@AfterCompose(superclass = true)
	public void afterComposeTemplateMenuPopup() {

	}

	private FinderModel sucursalFinder;

	@NotifyChange("*")
	public void inicializarFinders() {

		EmpresaUsuario eu = this.getCurrentEmpresaUsuario();
		
		String sqlSucursal = this.um.getSql("sucursal/buscarSucursalPorUsuario.sql")
				.replace("?1", eu.getEmpresa().getEmpresaid()+"")
				.replace("?2", eu.getUsuario().getUsuarioid()+"");
		sucursalFinder = new FinderModel("SucursalUsuario", sqlSucursal);
		
		
	}

	public void generarFinders(@BindingParam("finder") String finder) {

		if (finder.compareTo(this.sucursalFinder.getNameFinder()) == 0) {

			this.sucursalFinder.generarListFinder();
			BindUtils.postNotifyChange(null, null, this.sucursalFinder, "listFinder");

			return;
		}
		

	}

	@Command
	public void finderFilter(@BindingParam("filter") String filter, @BindingParam("finder") String finder) {

		if (finder.compareTo(this.sucursalFinder.getNameFinder()) == 0) {

			this.sucursalFinder.setListFinder(this.filtrarListaObject(filter, this.sucursalFinder.getListFinderOri()));
			BindUtils.postNotifyChange(null, null, this.sucursalFinder, "listFinder");

			return;
		}
		
	
	}

	@Command
	@NotifyChange({"sucursalUsuarioSelected"})
	public void onSelectetItemFinder(@BindingParam("id") Long id, @BindingParam("finder") String finder) {

		if (finder.compareTo(this.sucursalFinder.getNameFinder()) == 0) {

			this.sucursalUsuarioSelected = this.reg.getObjectById(SucursalUsuario.class.getName(), id);
			return;
			
		}


	}
	
	@Command
	public void guardar() {
		
		SucursalUsuario suOld = this.getCurrentSucursalUsuario();
		

		this.sucursalUsuarioSelected.setActual(true);
		this.reg.saveObject(sucursalUsuarioSelected, sucursalUsuarioSelected.getUsuario().getAccount());
		
		//de esta manera no da excepcion
		
		
		if(suOld != null) {
			
			suOld.setActual(false);
			this.reg.saveObject(suOld, suOld.getUsuario().getAccount());
			
		}
		
		Sessions.getCurrent().setAttribute("sucursalid", this.sucursalUsuarioSelected.getSucursal().getSucursalid());	
		
		this.windowModal.detach();
		Clients.evalJavaScript("window.location.reload();");
	
	}
	
	@Command
	 public void cerrarModal() {
		
        this.windowModal.detach();
        
    }

	public SucursalUsuario getSucursalUsuarioSelected() {
		return sucursalUsuarioSelected;
	}

	public void setSucursalUsuarioSelected(SucursalUsuario sucursalUsuarioSelected) {
		this.sucursalUsuarioSelected = sucursalUsuarioSelected;
	}

	public FinderModel getSucursalFinder() {
		return sucursalFinder;
	}

	public void setSucursalFinder(FinderModel sucursalFinder) {
		this.sucursalFinder = sucursalFinder;
	}


	
	
	

}
