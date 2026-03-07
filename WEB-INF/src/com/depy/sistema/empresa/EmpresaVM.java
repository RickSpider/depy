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

import com.depy.modelo.Empresa;
import com.depy.modelo.EmpresaUsuario;
import com.depy.modelo.Sucursal;
import com.depy.modelo.SucursalUsuario;
import com.depy.util.ParamsLocal;
import com.depy.util.TemplateViewModelLocal;
import com.doxacore.util.SystemInfo;






public class EmpresaVM extends TemplateViewModelLocal{
	
	private List<Object[]> lEmpresas;
	private List<Object[]> lEmpresasOri;
	private Empresa empresaSelected;
	
	private boolean opCrearEmpresa;
	private boolean opEditarEmpresa;
	private boolean opBorrarEmpresa;

	private boolean editar = false;

	@Init(superclass = true)
	public void initEmpresaVM() {

		this.inicializarFiltros();
		this.cargarEmpresas();
		

	}

	@AfterCompose(superclass = true)
	public void afterComposeEmpresaVM() {

	}

	
	@Override
	protected void inicializarOperaciones() {
		this.opCrearEmpresa = this.operacionHabilitada(ParamsLocal.OP_CREAR_EMPRESA);
		this.opEditarEmpresa = this.operacionHabilitada(ParamsLocal.OP_EDITAR_EMPRESA);
		this.opBorrarEmpresa = this.operacionHabilitada(ParamsLocal.OP_BORRAR_EMPRESA);
		
	}
	
	private String filtroColumns[];

	private void inicializarFiltros() {

		this.filtroColumns = new String[3];

		for (int i = 0; i < this.filtroColumns.length; i++) {

			this.filtroColumns[i] = "";

		}

	}

	@Command
	@NotifyChange("lEmpresas")
	public void filtrarEmpresa() {

		this.lEmpresas = this.filtrarListaObject(this.filtroColumns, this.lEmpresasOri);

	}

	private void cargarEmpresas() {

		this.lEmpresas = this.reg.sqlNativo(this.um.getSql("empresa/listaEmpresa.sql"));
		this.lEmpresasOri = this.lEmpresas;
		
		this.filtrarEmpresa();

	}
	
	//seccion modal
	
	private Window modal;
	
	@Command
	public void modalEmpresaAgregar() {

		if (!this.opCrearEmpresa)
			return;

		this.editar = false;
		this.modalEmpresa(-1);

	}

	@Command
	public void modalEmpresa(@BindingParam("empresaid") long empresaid) {

		this.logoFile = null;

		if (empresaid != -1) {

			if (!this.opEditarEmpresa)
				return;

			this.editar = true;

			this.empresaSelected = this.reg.findObjectById(Empresa.class, empresaid);
			try {
				if (this.empresaSelected.getLogoPath() != null) {
					this.logoFile = new AImage(new File(this.empresaSelected.getLogoPath()));
				}				
				
			} catch (IOException e) {
				System.out.println("Error al abrir el logo...");
				e.printStackTrace();
			}

		} else {

			this.empresaSelected = new Empresa();
		}

		modal = (Window) Executions.createComponents("/sistema/zul/empresa/empresaModal.zul", this.mainComponent, null);
		Selectors.wireComponents(modal, this, false);
		modal.doModal();

	}
	
	@Command
	@NotifyChange("lEmpresas")
	public void guardar() {

		this.empresaSelected = this.save(this.empresaSelected);
		
		this.guardarLogo();

		if (editar) {

			Notification.show("Empresa Actualizada.");

			this.editar = false;

		} else {
			
			EmpresaUsuario eu = new EmpresaUsuario();
			eu.setEmpresa(this.empresaSelected);
			eu.setUsuario(getCurrentUser());

			eu = this.save(eu);
			
			Sucursal s = new Sucursal();
			s.setEmpresa(this.empresaSelected);
			s.setNombre(this.empresaSelected.getRazonSocial()+" Matriz");
			s.setEstablecimiento("001");
			//s.setComprobanteTipo(this.reg.getObjectBySigla(Tipo.class, ParamsLocal.SIGLA_TIPO_COMPROBANTE_FACTURA));
		
			s = this.save(s);

			SucursalUsuario eus = new SucursalUsuario();
			eus.setEmpresa(eu.getEmpresa());
			eus.setUsuario(eu.getUsuario());
			eus.setActual(true);
			eus.setSucursal(s);
			
			eus = this.save(eus);

			Notification.show("Los datos de la Nueva Empresa fueron agragadas.");
		}
		
		this.cargarEmpresas();

		this.modal.detach();

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
		
		this.empresaSelected.setLogoPath(SystemInfo.SISTEMA_PATH_ABSOLUTO+"/logos/"+this.empresaSelected.getEmpresaid()+"/"+this.logoFile.getName());
		
		File logo = new File(this.empresaSelected.getLogoPath());

		Path directorioPath = Paths.get(logo.getParent());

		try {

			if (!Files.exists(directorioPath)) {

				Files.createDirectories(directorioPath);
			}

			try (InputStream inputStream = this.logoFile.getStreamData();

				OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(this.empresaSelected.getLogoPath()))) {

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

	public List<Object[]> getlEmpresas() {
		return lEmpresas;
	}

	public void setlEmpresas(List<Object[]> lEmpresas) {
		this.lEmpresas = lEmpresas;
	}

	public Empresa getEmpresaSelected() {
		return empresaSelected;
	}

	public void setEmpresaSelected(Empresa empresaSelected) {
		this.empresaSelected = empresaSelected;
	}

	public boolean isOpCrearEmpresa() {
		return opCrearEmpresa;
	}

	public void setOpCrearEmpresa(boolean opCrearEmpresa) {
		this.opCrearEmpresa = opCrearEmpresa;
	}

	public boolean isOpEditarEmpresa() {
		return opEditarEmpresa;
	}

	public void setOpEditarEmpresa(boolean opEditarEmpresa) {
		this.opEditarEmpresa = opEditarEmpresa;
	}

	public boolean isOpBorrarEmpresa() {
		return opBorrarEmpresa;
	}

	public void setOpBorrarEmpresa(boolean opBorrarEmpresa) {
		this.opBorrarEmpresa = opBorrarEmpresa;
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

	public Media getLogoFile() {
		return logoFile;
	}

	public void setLogoFile(Media logoFile) {
		this.logoFile = logoFile;
	}
	
	

}
