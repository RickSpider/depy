package com.depy.sistema.empresa;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.image.AImage;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zul.Window;

import com.depy.modelo.Sucursal;
import com.depy.modelo.SucursalUsuario;
import com.depy.util.ParamsLocal;
import com.depy.util.TemplateViewModelLocal;
import com.doxacore.components.finder.FinderModel;
import com.doxacore.modelo.Usuario;
import com.doxacore.util.SystemInfo;



public class SucursalVM extends TemplateViewModelLocal{
	
	private List<Object[]> lSucursales;
	private List<Object[]> lSucursalesOri;
	private Sucursal sucursalSelected;
	private Usuario usuarioSelected;
	
	private boolean opCrearSucursal;
	private boolean opEditarSucursal;
	private boolean opBorrarSucursal;

	private boolean editar = false;

	@Init(superclass = true)
	public void initSucursalVM() {

		this.inicializarFiltros();
		this.cargarSucursales();
		
	}

	@AfterCompose(superclass = true)
	public void afterComposeSucursalVM() {

	}

	
	@Override
	protected void inicializarOperaciones() {
		this.opCrearSucursal = this.operacionHabilitada(ParamsLocal.OP_CREAR_SUCURSAL);
		this.opEditarSucursal = this.operacionHabilitada(ParamsLocal.OP_EDITAR_SUCURSAL);
		this.opBorrarSucursal = this.operacionHabilitada(ParamsLocal.OP_BORRAR_SUCURSAL);
		
	}
	
	private String filtroColumns[];

	private void inicializarFiltros() {

		this.filtroColumns = new String[4];

		for (int i = 0; i < this.filtroColumns.length; i++) {

			this.filtroColumns[i] = "";

		}

	}

	@Command
	@NotifyChange("lSucursales")
	public void filtrarSucursal() {

		this.lSucursales = this.filtrarListaObject(this.filtroColumns, this.lSucursalesOri);

	}

	private void cargarSucursales() {

		String sql = this.um.getSql("sucursal/listaSucursal.sql").replace("?1", this.getCurrentEmpresa().getEmpresaid()+"");
		this.lSucursales = this.reg.sqlNativo(sql);
		this.lSucursalesOri = this.lSucursales;
		
		this.filtrarSucursal();

	}
	
	//seccion modal
	
	private Window modal;
	
	@Command
	public void modalSucursalAgregar() {

		if (!this.opCrearSucursal)
			return;

		this.editar = false;
		this.modalSucursal(-1);

	}

	@Command
	public void modalSucursal(@BindingParam("sucursalid") long sucursalid) {
		
	
		this.usuarioSelected = null;
		
		this.logoFile = null;

		if (sucursalid != -1) {

			if (!this.opEditarSucursal)
				return;

			this.editar = true;

			this.sucursalSelected = this.reg.findObjectById(Sucursal.class, sucursalid);
			
			try {
				if (this.sucursalSelected.getLogoPath() != null) {
					this.logoFile = new AImage(new File(this.sucursalSelected.getLogoPath()));
				}				
				
			} catch (IOException e) {
				System.out.println("Error al abrir el logo...");
				e.printStackTrace();
			}


		} else {

			this.sucursalSelected = new Sucursal();
			//this.sucursalSelected.setEmpresa(getCurrentEmpresa());

		}

		modal = (Window) Executions.createComponents("/sistema/zul/empresa/sucursalModal.zul", this.mainComponent, null);
		Selectors.wireComponents(modal, this, false);
		modal.doModal();
		
		this.inicializarFinders();

	}
	
	@Command
	@NotifyChange("lSucursales")
	public void guardar() {

		this.sucursalSelected = this.save(this.sucursalSelected);
		
		this.guardarLogo();

		this.cargarSucursales();

		this.modal.detach();

		if (editar) {

			Notification.show("Sucursal Actualizada.");

			this.editar = false;

		} else {

			Notification.show("Los datos de la Nueva Sucursal fueron agragados.");
		}

	}
	
	
	
	
	private FinderModel empresaUsuarioFinder;
	//private FinderModel comprobanteTipoFinder;

	@NotifyChange("*")
	public void inicializarFinders() {
		
		String buscarTiposPorSiglaTipotipo = this.um.getCoreSql("buscarTiposPorSiglaTipotipo.sql");

		//String sqlComprobanteTipo = buscarTiposPorSiglaTipotipo.replace("?1", ParamsLocal.SIGLA_TIPOTIPO_COMPROBANTE );
		//comprobanteTipoFinder = new FinderModel("Comprobante", sqlComprobanteTipo);

		String sqlUsuario = this.um.getSql("empresaUsuario/buscarEmpresaUsuario.sql").replace("?1", this.getCurrentEmpresa().getEmpresaid()+"" );
		empresaUsuarioFinder = new FinderModel("Usuario", sqlUsuario);
		
	}

	public void generarFinders(@BindingParam("finder") String finder) {
		
		if (finder.compareTo(this.empresaUsuarioFinder.getNameFinder()) == 0) {

			this.empresaUsuarioFinder.generarListFinder();
			BindUtils.postNotifyChange(null, null, this.empresaUsuarioFinder, "listFinder");

			return;
		}
		
		
		

	}

	@Command
	public void finderFilter(@BindingParam("filter") String filter, @BindingParam("finder") String finder) {
		
		if (finder.compareTo(this.empresaUsuarioFinder.getNameFinder()) == 0) {

			this.empresaUsuarioFinder.setListFinder(this.filtrarListaObject(filter, this.empresaUsuarioFinder.getListFinderOri()));
			BindUtils.postNotifyChange(null, null, this.empresaUsuarioFinder, "listFinder");

			return;
		}
	
	}

	@Command
	@NotifyChange({"*"})
	public void onSelectetItemFinder(@BindingParam("id") Long id, @BindingParam("finder") String finder) {

		if (finder.compareTo(this.empresaUsuarioFinder.getNameFinder()) == 0) {

			this.usuarioSelected = this.reg.getObjectById(Usuario.class.getName(), id);
			return;
			
		}

	}
	
	@Command
	@NotifyChange({"sucursalSelected", "usuarioSelected"})
	public void agregarUsuario() {
		
		SucursalUsuario su = new SucursalUsuario();
		
		su.setEmpresa(this.sucursalSelected.getEmpresa());
		su.setSucursal(this.sucursalSelected);
		su.setUsuario(usuarioSelected);
		
		this.sucursalSelected.getUsuarios().add(su);
		
		this.usuarioSelected = null;
		
	}
	
	private Media logoFile;
	private boolean logoEditado = false;

	@Command
	@NotifyChange("*")
	public void uploadLogo(@BindingParam("file") Media file) {

		if (file == null) {
	        this.mensajeInfo("No se ha seleccionado ningún archivo.");
	        return;
	    }

	 String fileName = file.getName().toLowerCase();
	    if (!fileName.endsWith(".png") && !fileName.endsWith(".jpg") ) {
	        this.mensajeInfo("Archivo no válido, debe ser .png o .jpg");
	        return;
	    }
	    
	    this.logoFile = file;
	    
	    this.logoEditado = true;

	    this.mensajeInfo("Archivo subido correctamente.");

	}
	
	public void guardarLogo() {

		if (this.logoFile == null) {

			return;

		}
		
		if (!this.logoEditado) {
			
			return;
			
		}
		
		this.sucursalSelected.setLogoPath(SystemInfo.SISTEMA_PATH_ABSOLUTO+"/logos/"+this.sucursalSelected.getEmpresa().getEmpresaid()+"/"+this.logoFile.getName());
		
		File logo = new File(this.sucursalSelected.getLogoPath());

		Path directorioPath = Paths.get(logo.getParent());

		try {

			if (!Files.exists(directorioPath)) {

				Files.createDirectories(directorioPath);
			}

			try (InputStream inputStream = this.logoFile.getStreamData();

				OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(this.sucursalSelected.getLogoPath()))) {

				byte[] buffer = new byte[65536];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}

			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		}

	}
	

	public List<Object[]> getlSucursales() {
		return lSucursales;
	}

	public void setlSucursales(List<Object[]> lSucursales) {
		this.lSucursales = lSucursales;
	}

	public Sucursal getSucursalSelected() {
		return sucursalSelected;
	}

	public void setSucursalSelected(Sucursal sucursalSelected) {
		this.sucursalSelected = sucursalSelected;
	}

	public boolean isOpCrearSucursal() {
		return opCrearSucursal;
	}

	public void setOpCrearSucursal(boolean opCrearSucursal) {
		this.opCrearSucursal = opCrearSucursal;
	}

	public boolean isOpEditarSucursal() {
		return opEditarSucursal;
	}

	public void setOpEditarSucursal(boolean opEditarSucursal) {
		this.opEditarSucursal = opEditarSucursal;
	}

	public boolean isOpBorrarSucursal() {
		return opBorrarSucursal;
	}

	public void setOpBorrarSucursal(boolean opBorrarSucursal) {
		this.opBorrarSucursal = opBorrarSucursal;
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

	public FinderModel getEmpresaUsuarioFinderFinder() {
		return empresaUsuarioFinder;
	}

	public void setEmpresaUsuarioFinder(FinderModel empresaUsuarioFinder) {
		this.empresaUsuarioFinder = empresaUsuarioFinder;
	}

	public Usuario getUsuarioSelected() {
		return usuarioSelected;
	}

	public void setUsuarioSelected(Usuario usuarioSelected) {
		this.usuarioSelected = usuarioSelected;
	}

	public FinderModel getEmpresaUsuarioFinder() {
		return empresaUsuarioFinder;
	}

	public Media getLogoFile() {
		return logoFile;
	}

	public void setLogoFile(Media logoFile) {
		this.logoFile = logoFile;
	}
	
	

}
