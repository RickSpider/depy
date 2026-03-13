package com.depy.sistema.gestion;

import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zul.ListModelArray;
import org.zkoss.zul.Window;

import com.depy.modelo.Cliente;
import com.depy.modelo.Localidad;
import com.depy.modelo.Ruc;
import com.depy.searchModel.LocalidadSM;
import com.depy.util.ParamsLocal;
import com.depy.util.TemplateViewModelLocal;
import com.doxacore.modelo.Tipo;
import com.doxacore.modelo.Tipotipo;

public class ClienteVM extends TemplateViewModelLocal {

	private List<Object[]> lClientes;
	private List<Object[]> lClientesOri;
	private Cliente clienteSelected;

	private boolean opCrearCliente;
	private boolean opEditarCliente;
	private boolean opBorrarCliente;

	private boolean editar = false;
	
	private List<Tipo> lDocumentoTipo;

	@Init(superclass = true)
	public void initClienteVM() {

		this.inicializarFiltros();
		this.cargarClientes();

	}

	@AfterCompose(superclass = true)
	public void afterComposeClienteVM() {

	}

	@Override
	protected void inicializarOperaciones() {
		this.opCrearCliente = this.operacionHabilitada(ParamsLocal.OP_CREAR_CLIENTE);
		this.opEditarCliente = this.operacionHabilitada(ParamsLocal.OP_EDITAR_CLIENTE);
		this.opBorrarCliente = this.operacionHabilitada(ParamsLocal.OP_BORRAR_CLIENTE);

	}

	private String filtroColumns[];

	private void inicializarFiltros() {

		this.filtroColumns = new String[3];

		for (int i = 0; i < this.filtroColumns.length; i++) {

			this.filtroColumns[i] = "";

		}

	}

	@Command
	@NotifyChange("lClientes")
	public void filtrarCliente() {

		this.lClientes = this.filtrarListaObject(this.filtroColumns, this.lClientesOri);

	}

	private void cargarClientes() {

		this.lClientes = this.reg.sqlNativo(
				this.um.getSql("cliente/listaCliente.sql").replace("?1", this.getCurrentEmpresa().getEmpresaid() + ""));
		this.lClientesOri = this.lClientes;

		this.filtrarCliente();

	}

	// seccion modal

	private Window modal;

	@Command
	public void modalClienteAgregar() {

		if (!this.opCrearCliente)
			return;

		this.editar = false;
		this.modalCliente(-1);
		
		

	}

	@Command
	public void modalCliente(@BindingParam("clienteid") long clienteid) {
		
		this.cargarDatosCb();
	
	//	this.documentoTipoSM = null;
		
		if (clienteid != -1) {

			if (!this.opEditarCliente)
				return;

			this.editar = true;
			this.clienteSelected = this.reg.findObjectById(Cliente.class, clienteid);
			this.localidadSMSelected = this.clienteSelected.getLocalidad() != null ? new LocalidadSM( this.clienteSelected.getLocalidad()): null;
			
			/*if (this.clienteSelected.getDocumentoTipo() != null) {
				this.documentoTipoSM = new TipoSM(this.clienteSelected.getDocumentoTipo());
			}*/
			
			
		} else {
			this.localidadSMSelected = null;
			this.clienteSelected = new Cliente();
			this.clienteSelected.setEmpresa(getCurrentEmpresa());
			this.clienteSelected.setDocumentoTipo(this.reg.getObjectBySigla(Tipo.class, ParamsLocal.SIGLA_TIPO_DOCUMENTO_RUC));
		//	this.documentoTipoSM = new TipoSM(this.clienteSelected.getDocumentoTipo());
		}

		modal = (Window) Executions.createComponents("/sistema/zul/gestion/clienteModal.zul", this.mainComponent, null);
		Selectors.wireComponents(modal, this, false);
		modal.doModal();
		
		//Clients.showBusy("Cargando datos...");

	    // Ejecutar generarSearchModels() después del render, inline
		 modal.addEventListener("onLater", event -> {
		        generarSearchModels();
		        BindUtils.postNotifyChange(null, null, this, "lLocalidadSearchModel");
		        //BindUtils.postNotifyChange(null, null, this, "lDocumentoTipoSM");
		       // Clients.clearBusy();
		    });

		 Events.echoEvent("onLater", modal, null);
	}

	@Command
	@NotifyChange("lClientes")
	public void guardar() {
		
	/*	if (this.documentoTipoSM != null) {
			this.clienteSelected.setDocumentoTipo(this.documentoTipoSM.getTipo());
		}*/
		
		if (!this.verficarCampos()) {
			return;
		}
		
		this.clienteSelected .setDocumentoNro(this.clienteSelected.getDocumentoNro().trim());
		this.clienteSelected .setRazonsocial(this.clienteSelected.getRazonsocial().trim());
		
		this.clienteSelected = this.save(this.clienteSelected);

		if (editar) {

			Notification.show("Empresa Actualizada.");

			this.editar = false;

		} else {

			Notification.show("Los datos de la Nueva Empresa fueron agragadas.");
		}

		this.cargarClientes();

		this.modal.detach();

	}
	
	public boolean verficarCampos() {
		
		if (this.clienteSelected.getDocumentoNro() == null 
				|| this.clienteSelected.getDocumentoNro().isBlank()) {
			
			this.mensajeInfo("Debes cargar un numero de documento.");
			
			return false;
			
		}
		
		if (this.clienteSelected.getRazonsocial() == null 
				|| this.clienteSelected.getRazonsocial().isBlank()) {
			
			this.mensajeInfo("Debes cargar la Razon social.");
			
			return false;
			
		}
		
		boolean alguno = this.localidadSMSelected != null || (this.clienteSelected.getDireccion() != null && !this.clienteSelected.getDireccion().isBlank()) || (this.clienteSelected.getCasaNro() != null && !this.clienteSelected.getCasaNro().isBlank());
		boolean todos  = this.localidadSMSelected != null && (this.clienteSelected.getDireccion() != null && !this.clienteSelected.getDireccion().isBlank()) && (this.clienteSelected.getCasaNro() != null && !this.clienteSelected.getCasaNro().isBlank());

		if (alguno && !todos) {
		    this.mensajeInfo("Localidad, dirección y número de casa deben informarse juntos");
		    return false;
		}
		
		
		return true;
		
	}
	
	private ListModelArray<LocalidadSM> lLocalidadSearchModel;
	private LocalidadSM localidadSMSelected;
	
//	private ListModelArray<TipoSM> lDocumentoTipoSM;
//	private TipoSM documentoTipoSM;
	
	private void generarSearchModels() {
		
		this.lLocalidadSearchModel = this.crearSearchModel(
				
	        this.um.getSql("localidad/buscarLocalidad.sql"),
	        o -> new LocalidadSM(
	                ((Number) o[0]).longValue(),
	                (String) o[1],
	                ((Number) o[2]).longValue(),
	                (String) o[3],
	                ((Number) o[4]).longValue(),
	                (String) o[5]
	            )
	    );
		
	/*	this.lDocumentoTipoSM = this.crearSearchModel(
			this.um.getCoreSql("buscarTiposPorSiglaTipotipo.sql").replace("?1", ParamsLocal.SIGLA_TIPOTIPO_DOUCMENTO),
	        o -> new TipoSM(
	        		((Number) o[0]).longValue(),
			        (String) o[1],
			        ((Number) o[4]).longValue()
	            )
	    );*/
	}
	
	public void cargarDatosCb() {
		
		Tipotipo tt = this.reg.getObjectBySigla(Tipotipo.class,ParamsLocal.SIGLA_TIPOTIPO_DOCUMENTO);
		String [] cols = {"tipotipo"};
		Object [] value = {tt}; 
		lDocumentoTipo = this.reg.getAllObjectsByColumns(Tipo.class, cols, value);
		
	}
	
	@NotifyChange("*")
	@Command
	public void onSelectedLocalidad() {
		
		this.clienteSelected.setLocalidad(this.localidadSMSelected != null ? this.reg.findObjectById(Localidad.class, this.localidadSMSelected.getLocalidadid()) : null );
	
	}
	
	@Command
	@NotifyChange("*")
	public void onChangeDoc() {
		
		String docNro = this.clienteSelected.getDocumentoNro();
		
		int idx = docNro.indexOf('-');
		if (idx > 0) {

			String [] columns = {"ruc","dv"};
		    Object[] valor = {docNro.substring(0, idx), docNro.substring(idx + 1)};
		   
		    Ruc ruc = this.reg.getObjectByColumns(Ruc.class, columns, valor);
		    
		    this.clienteSelected.setRazonsocial(ruc != null ? ruc.getRazonSocial(): null);
		    
		}
		
	}

	public List<Object[]> getlClientes() {
		return lClientes;
	}

	public void setlClientes(List<Object[]> lClientes) {
		this.lClientes = lClientes;
	}

	public Cliente getClienteSelected() {
		return clienteSelected;
	}

	public void setClienteSelected(Cliente clienteSelected) {
		this.clienteSelected = clienteSelected;
	}

	public boolean isOpCrearCliente() {
		return opCrearCliente;
	}

	public void setOpCrearCliente(boolean opCrearCliente) {
		this.opCrearCliente = opCrearCliente;
	}

	public boolean isOpEditarCliente() {
		return opEditarCliente;
	}

	public void setOpEditarCliente(boolean opEditarCliente) {
		this.opEditarCliente = opEditarCliente;
	}

	public boolean isOpBorrarCliente() {
		return opBorrarCliente;
	}

	public void setOpBorrarCliente(boolean opBorrarCliente) {
		this.opBorrarCliente = opBorrarCliente;
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

	public ListModelArray<LocalidadSM> getlLocalidadSearchModel() {
		return lLocalidadSearchModel;
	}

	public void setlLocalidadSearchModel(ListModelArray<LocalidadSM> lLocalidadSearchModel) {
		this.lLocalidadSearchModel = lLocalidadSearchModel;
	}

	public LocalidadSM getLocalidadSMSelected() {
		return localidadSMSelected;
	}

	public void setLocalidadSMSelected(LocalidadSM localidadSMSelected) {
		this.localidadSMSelected = localidadSMSelected;
	}

	public List<Tipo> getlDocumentoTipo() {
		return lDocumentoTipo;
	}

	public void setlDocumentoTipo(List<Tipo> lDocumentoTipo) {
		this.lDocumentoTipo = lDocumentoTipo;
	}
	
	
	
}
