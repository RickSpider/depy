package com.depy.sistema.empresa;

import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.depy.modelo.Comprobante;
import com.depy.util.ParamsLocal;
import com.depy.util.TemplateViewModelLocal;
import com.doxacore.modelo.Tipo;
import com.doxacore.modelo.Tipotipo;



public class ComprobanteVM extends TemplateViewModelLocal {

	private List<Object[]> lComprobantes;
	private List<Object[]> lComprobantesOri;
	private Comprobante comprobanteSelected;

	private boolean opCrearComprobante;
	private boolean opEditarComprobante;
	private boolean opBorrarComprobante;
	
	private boolean campoTimbrado = true;

	@Init(superclass = true)
	public void initComprobanteVM() {

		cargarComprobantes();
		inicializarFiltros();

	}

	@AfterCompose(superclass = true)
	public void afterComposeComprobanteVM() {

	}

	@Override
	protected void inicializarOperaciones() {
		this.opCrearComprobante = this.operacionHabilitada(ParamsLocal.OP_CREAR_COMPROBANTE);
		this.opEditarComprobante = this.operacionHabilitada(ParamsLocal.OP_EDITAR_COMPROBANTE);
		this.opBorrarComprobante = this.operacionHabilitada(ParamsLocal.OP_BORRAR_COMPROBANTE);

	}

	private void cargarComprobantes() {
		
		this.lComprobantes = this.reg.sqlNativo(
				this.um.getSql("comprobante/listaComprobante.sql").replace("?1", this.getCurrentEmpresa().getEmpresaid()+"")
				);
		
	}

	// seccion filtro

	private String filtroColumns[];

	private void inicializarFiltros() {

		this.filtroColumns = new String[9]; // se debe de iniciar el filtro deacuerdo a la cantidad declarada en el
											// modelo sin id

		for (int i = 0; i < this.filtroColumns.length; i++) {

			this.filtroColumns[i] = "";

		}

	}

	@Command
	@NotifyChange("lComprobantes")
	public void filtrarComprobante() {

		

	}

	// fin seccion
	
	//seccion modal
	
	private Window modal;
	private boolean editar = false;

	@Command
	public void modalComprobanteAgregar() {

		if(!this.opCrearComprobante)
			return;

		this.editar = false;
		modalComprobante(-1);

	}

	@Command
	public void modalComprobante(@BindingParam("comprobanteid") long comprobanteid) {
		
		this.cargarCB();

		if (comprobanteid != -1) {

			if(!this.opEditarComprobante)
				return;
			
			this.comprobanteSelected = this.reg.findObjectById(Comprobante.class, comprobanteid);
			
			this.editar = true;

		} else {
			
			comprobanteSelected = new Comprobante();		

		}

		modal = (Window) Executions.createComponents("/sistema/zul/empresa/comprobanteModal.zul", this.mainComponent,
				null);
		Selectors.wireComponents(modal, this, false);
		modal.doModal();

	}
	
	private boolean verificarCampos() {
		
		return true;
	}	

	@Command
	@NotifyChange("lComprobantes")
	public void guardar() {
		
		if (!verificarCampos()) {
			return;
		}

		this.comprobanteSelected = this.save(comprobanteSelected);
		
		this.comprobanteSelected = null;

		this.cargarComprobantes();

		this.modal.detach();
		
		if (editar) {
			
			Notification.show("La Comprobante fue Actualizada.");
			this.editar = false;
		}else {
			
			Notification.show("La Comprobante fue agregada.");
		}
		
		

	}

	
	//fin modal
	
	@Command
	public void borrarComprobanteConfirmacion(@BindingParam("comprobante") final Comprobante comprobante) {
		
		if (!this.opBorrarComprobante)
			return;
		
		EventListener event = new EventListener () {

			@Override
			public void onEvent(Event evt) throws Exception {
				
				if (evt.getName().equals(Messagebox.ON_YES)) {
					
					borrarComprobante(comprobante);
					
				}
				
			}

		};
		
		this.mensajeEliminar("La Comprobante sera eliminada. \n Continuar?", event);
	}
	
	private void borrarComprobante (Comprobante comprobante) {
		
		this.reg.deleteObject(comprobante);
		
		this.cargarComprobantes();
		
		BindUtils.postNotifyChange(null,null,this,"lComprobantes");
		
	}
	
	private List<Tipo>lComprobanteTipo;
	
	private void cargarCB() {
		
		Tipotipo tt = this.reg.getObjectBySigla(Tipotipo.class,ParamsLocal.SIGLA_TIPOTIPO_COMPROBANTE);
		String [] cols = {"tipotipo"};
		Object [] value = {tt}; 
		this.lComprobanteTipo = this.reg.getAllObjectsByColumns(Tipo.class, cols, value);
		
	}
	
	public Comprobante getComprobanteSelected() {
		return comprobanteSelected;
	}

	public void setComprobanteSelected(Comprobante comprobanteSelected) {
		this.comprobanteSelected = comprobanteSelected;
	}

	public boolean isOpCrearComprobante() {
		return opCrearComprobante;
	}

	public void setOpCrearComprobante(boolean opCrearComprobante) {
		this.opCrearComprobante = opCrearComprobante;
	}

	public boolean isOpEditarComprobante() {
		return opEditarComprobante;
	}

	public void setOpEditarComprobante(boolean opEditarComprobante) {
		this.opEditarComprobante = opEditarComprobante;
	}

	public boolean isOpBorrarComprobante() {
		return opBorrarComprobante;
	}

	public void setOpBorrarComprobante(boolean opBorrarComprobante) {
		this.opBorrarComprobante = opBorrarComprobante;
	}

	public String[] getFiltroColumns() {
		return filtroColumns;
	}

	public void setFiltroColumns(String[] filtroColumns) {
		this.filtroColumns = filtroColumns;
	}

	public boolean isEditar() {
		return editar;
	}

	public void setEditar(boolean editar) {
		this.editar = editar;
	}

	public boolean isCampoTimbrado() {
		return campoTimbrado;
	}

	public void setCampoTimbrado(boolean campoTimbrado) {
		this.campoTimbrado = campoTimbrado;
	}

	public List<Object[]> getlComprobantes() {
		return lComprobantes;
	}

	public void setlComprobantes(List<Object[]> lComprobantes) {
		this.lComprobantes = lComprobantes;
	}

	public List<Tipo> getlComprobanteTipo() {
		return lComprobanteTipo;
	}

	public void setlComprobanteTipo(List<Tipo> lComprobanteTipo) {
		this.lComprobanteTipo = lComprobanteTipo;
	}
	
}
