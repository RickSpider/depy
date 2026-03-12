package com.depy.sistema.gestion;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.depy.modelo.Empresa;
import com.depy.modelo.Factura;

import com.depy.util.ParamsLocal;
import com.depy.util.TemplateViewModelLocal;
import com.depy.util.UtilLocalMetodos;
import com.depy.utilde.MetodoDE;
import com.depy.utilde.conexion.ResultRest;
import com.depy.utilde.modelo.Contribuyente;
import com.depy.utilde.modelo.Evento;
import com.doxacore.modelo.Tipo;
import com.doxacore.util.Register;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FacturaVM extends TemplateViewModelLocal {

	private List<Object[]> lFacturas;
	private List<Object[]> lFacturasOri;
	private Factura facturaSelected;

	private boolean opCrearFactura;
	private boolean opEditarFactura;
	private boolean opBorrarFactura;

	private boolean editar = false;

	@Init(superclass = true)
	public void initFacturaVM() {

		this.inicializarFiltros();
		this.cargarFacturas();

	}

	@AfterCompose(superclass = true)
	public void afterComposeFacturaVM() {

	}

	@Override
	protected void inicializarOperaciones() {
		this.opCrearFactura = this.operacionHabilitada(ParamsLocal.OP_CREAR_FACTURA);
		this.opEditarFactura = this.operacionHabilitada(ParamsLocal.OP_EDITAR_FACTURA);
		this.opBorrarFactura = this.operacionHabilitada(ParamsLocal.OP_BORRAR_FACTURA);

	}

	private String filtroColumns[];

	private void inicializarFiltros() {

		this.filtroColumns = new String[13];

		for (int i = 0; i < this.filtroColumns.length; i++) {

			this.filtroColumns[i] = "";

		}

	}

	@Command
	@NotifyChange("lFacturas")
	public void filtrarFactura() {

		this.lFacturas = this.filtrarListaObject(this.filtroColumns, this.lFacturasOri);

	}

	@Command
	@NotifyChange("*")
	public void cargarFacturas() {

		this.lFacturas = this.reg.sqlNativo(
				this.um.getSql("factura/listaFactura.sql").replace("?1", this.getCurrentEmpresa().getEmpresaid() + ""));
		this.lFacturasOri = this.lFacturas;

		this.filtrarFactura();

	}
	
	
	@Command
	public void openQrSifen(@BindingParam("linkQr") String linkQr) {

		Executions.getCurrent().sendRedirect(linkQr, "_blank");

	}
	
	private void openInNewTabPost(String url, Map<String, String> params) {
	
		StringBuilder js = new StringBuilder();
	    js.append("var f=document.createElement('form');");
	    js.append("f.method='POST';");
	    js.append("f.action='").append(url).append("';");
	    js.append("f.target='_blank';");

	    for (Map.Entry<String, String> entry : params.entrySet()) {
	    
	    	js.append("var i=document.createElement('input');");
	        js.append("i.type='hidden';");
	        js.append("i.name='").append(entry.getKey()).append("';");
	        js.append("i.value='").append(entry.getValue()
	        		.replace("\\", "\\\\")
	                .replace("'", "\\'")
	                .replace("\r", "")
	                .replace("\n", "\\n"))
	                .append("';");
	            
	        js.append("f.appendChild(i);");
	        
	    }
	        
	    js.append("document.body.appendChild(f); f.submit(); document.body.removeChild(f);");
	    Clients.evalJavaScript(js.toString());
	}
	
	@Command
	public void verKude(@BindingParam("id") Long id) {
				 	
	    Map<String, String> params = new HashMap<>();
		params.put("id", String.valueOf(id));
		params.put("tipode", "factura");

		this.openInNewTabPost("sistema/zul/reporte/kudeViewer.zul", params);
		
	}
	
	private Window modal;
	private Evento eventoSelected;
	
	
	@Command()
	public void eventoModal(@BindingParam("id") Long id) {
		
		this.facturaSelected = this.reg.findObjectById(Factura.class, id);
		this.eventoSelected = null;
		
		
		if (this.facturaSelected.getEventoTipo() != null) {
			
			this.mensajeInfo("El documanto ya posee un evento.");
			
			return ;
		}
		
		long horas = Duration.between(this.facturaSelected.getFecha(), LocalDateTime.now()).toHours();
		
		
		
		if (this.facturaSelected.getEstado().equals("Aprobado") && horas >= 41) {
			
			this.mensajeInfo("El tiempo de cancelacion del Documento ya expiro.");
			
			return;
		}
		
		if (this.facturaSelected.getEstado().contains("Pendiente")) {
			
			this.mensajeInfo("Solo puedes generar eventos a Documentos Aprobadas o Rechazadas");
			
			return;
			
		}
		
		Empresa e = this.reg.findObjectById(Empresa.class, this.getCurrentEmpresa().getEmpresaid());
		
		this.eventoSelected = new Evento();
		Contribuyente c = new Contribuyente();
		c.setContribuyenteid(e.getFcwsId());
		c.setPass(e.getFcwsPass());
		this.eventoSelected.setContribuyente(c);
		
		
		if (this.facturaSelected.getEstado().contains("Aprobado")) {
			
			Tipo t = this.reg.getObjectBySigla(Tipo.class, ParamsLocal.SIGLA_TIPO_EVENTO_CANCELACION);
			
			this.eventoSelected.setCdc(this.facturaSelected.getCdc());
			this.eventoSelected.setFecha(new Date());
			this.eventoSelected.setMotivo("Cancelacion de Factura");
			this.eventoSelected.setEventoTipo(t);
			
			modal = (Window) Executions.createComponents("/sistema/zul/gestion/eventoCancelarModal.zul", this.mainComponent, null);
			
		}else if (this.facturaSelected.getEstado().equals("Rechazado")) {
			
			Tipo t = this.reg.getObjectBySigla(Tipo.class, ParamsLocal.SIGLA_TIPO_EVENTO_INUTILIZACION);
			
			this.eventoSelected.setFecha(new Date());
			
			this.eventoSelected.setTimbrado(this.facturaSelected.getTimbrado());
			String[] numeracion = this.facturaSelected.getTimbradoDocNro().split("-");
			this.eventoSelected.setEstablecimiento(numeracion[0]);
			this.eventoSelected.setPuntoExpedicion(numeracion[1]);
			this.eventoSelected.setNumeroIni(numeracion[2]);
			this.eventoSelected.setNumeroFin(numeracion[2]);
			this.eventoSelected.setEventoTipo(t);
			this.eventoSelected.setMotivo("Inutilizacion por Rechazo");
			
			modal = (Window) Executions.createComponents("/sistema/zul/gestion/eventoInutilizarModal.zul", this.mainComponent, null);
			
		}
		
		
		//modal = (Window) Executions.createComponents("/sistema/zul/operacion/eventoModal.zul", this.mainComponent, null);
		Selectors.wireComponents(modal, this, false);
		modal.doModal();
	}
	
	@Command
	public void generarEvento() {
		
		/*if (!verificarCampos()) {
			return;
		}*/
		
		EventListener event = new EventListener() {

			@Override
			public void onEvent(Event evt) throws Exception {

				if (evt.getName().equals(Messagebox.ON_YES)) {

					guardarEvento();

				}

			}

		};
		
		this.mensajeSiNo("Se Procedera a crear el evento.\n ¿Continuar?", "Generar Evento", event);
		
	}
	
	@Command()
	public void guardarEvento() {
		
		Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss-03:00")
                .create();
		
		MetodoDE mde = new MetodoDE();
		
		ResultRest rr = null;
		Evento eventoResponse = null;
		
		if (this.eventoSelected.getEventoTipo().getSigla().equals(ParamsLocal.SIGLA_TIPO_EVENTO_CANCELACION)) {
			
			rr = mde.enviarJson(this.getSistemaPropiedad("fcwsHOST").getValor()+"/evento/cancelarfactura", gson.toJson(this.eventoSelected));
			
		}else if (this.eventoSelected.getEventoTipo().getSigla().equals(ParamsLocal.SIGLA_TIPO_EVENTO_INUTILIZACION)) {
			
			rr = mde.enviarJson(this.getSistemaPropiedad("fcwsHOST").getValor()+"/evento/inutilizacionnumfactura", gson.toJson(this.eventoSelected));
			eventoResponse = gson.fromJson(rr.getMensaje(), Evento.class);
			
		}

		if (rr == null || rr.getCode() != 201) {
			
			this.mensajeError("Ocurrio al enviar el evento, reintenta en unos minutos.");
			
			return;
			
		}
		
		
		
		if (eventoResponse != null && eventoResponse.getEventoid() != null) {
			
			this.facturaSelected.setEventoid(eventoResponse.getEventoid());
		}
		
		this.facturaSelected.setEventoJson(gson.toJson(this.eventoSelected));
		this.facturaSelected.setEventoEstado("Pendiente");
		this.facturaSelected.setEventoFecha(this.eventoSelected.getFecha().toInstant()
		        .atZone(ZoneId.systemDefault())
		        .toLocalDateTime());
		this.facturaSelected.setEventoTipo(this.eventoSelected.getEventoTipo());
		
		this.save(this.facturaSelected);
		
		this.modal.detach();
		
		this.facturaSelected = null;
		this.eventoSelected = null;
		
		this.cargarFacturas();
		
		BindUtils.postNotifyChange(null, null, this, "*");
		
	}
	
	@Command
	@NotifyChange("*")
	public void consultarEstados() {
		
		UtilLocalMetodos ulm = new UtilLocalMetodos();
		ulm.updateDE(new Register(), ulm.getSql("factura/listaFacturaPendiente.sql").replace("?1", this.getCurrentEmpresa().getEmpresaid()+"").replace("--", ""), ulm.getSql("factura/updateFactura.sql"));
	
		ulm.updateEvento(new Register(), ulm.getSql("factura/listaFacturaEventoPendiente.sql").replace("?1", this.getCurrentEmpresa().getEmpresaid()+"").replace("--", ""), ulm.getSql("factura/updateFacturaEvento.sql"));
		
		this.cargarFacturas();
	}

	public List<Object[]> getlFacturas() {
		return lFacturas;
	}

	public void setlFacturas(List<Object[]> lFacturas) {
		this.lFacturas = lFacturas;
	}

	public Factura getFacturaSelected() {
		return facturaSelected;
	}

	public void setFacturaSelected(Factura facturaSelected) {
		this.facturaSelected = facturaSelected;
	}

	public boolean isOpCrearFactura() {
		return opCrearFactura;
	}

	public void setOpCrearFactura(boolean opCrearFactura) {
		this.opCrearFactura = opCrearFactura;
	}

	public boolean isOpEditarFactura() {
		return opEditarFactura;
	}

	public void setOpEditarFactura(boolean opEditarFactura) {
		this.opEditarFactura = opEditarFactura;
	}

	public boolean isOpBorrarFactura() {
		return opBorrarFactura;
	}

	public void setOpBorrarFactura(boolean opBorrarFactura) {
		this.opBorrarFactura = opBorrarFactura;
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

	public Evento getEventoSelected() {
		return eventoSelected;
	}

	public void setEventoSelected(Evento eventoSelected) {
		this.eventoSelected = eventoSelected;
	}

}
