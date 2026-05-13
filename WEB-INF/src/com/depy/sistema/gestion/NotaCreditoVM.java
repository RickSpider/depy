package com.depy.sistema.gestion;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
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
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.depy.modelo.Empresa;
import com.depy.modelo.NotaCredito;

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

public class NotaCreditoVM extends TemplateViewModelLocal {

	private List<Object[]> lNotasCreditos;
	private List<Object[]> lNotasCreditosOri;
	private NotaCredito notacreditoSelected;

	private boolean opCrearNotaCredito;
	private boolean opEditarNotaCredito;
	private boolean opBorrarNotaCredito;

	private boolean editar = false;
	
	private Date desde;
	private Date hasta;

	@Init(superclass = true)
	public void initNotaCreditoVM() {
		
		this.desde = Date.from(
			    LocalDate.now()
		        .withDayOfMonth(1)
		        .atTime(LocalTime.MIN)
		        .atZone(ZoneId.systemDefault())
		        .toInstant()
		);
		
		this.hasta = Date.from(
			    LocalDate.now()
		        .with(TemporalAdjusters.lastDayOfMonth())
		        .atTime(LocalTime.MAX)
		        .atZone(ZoneId.systemDefault())
		        .toInstant()
		);

		this.inicializarFiltros();
		this.cargarNotasCreditos();

	}

	@AfterCompose(superclass = true)
	public void afterComposeNotaCreditoVM() {

	}

	@Override
	protected void inicializarOperaciones() {
	/*	this.opCrearNotaCredito = this.operacionHabilitada(ParamsLocal.OP_CREAR_NOTACREDITO);
		this.opEditarNotaCredito = this.operacionHabilitada(ParamsLocal.OP_EDITAR_NOTACREDITO);
		this.opBorrarNotaCredito = this.operacionHabilitada(ParamsLocal.OP_BORRAR_NOTACREDITO);*/

	}

	private String filtroColumns[];

	private void inicializarFiltros() {

		this.filtroColumns = new String[13];

		for (int i = 0; i < this.filtroColumns.length; i++) {

			this.filtroColumns[i] = "";

		}

	}

	@Command
	@NotifyChange("lNotasCreditos")
	public void filtrarNotasCreditos() {

		this.lNotasCreditos = this.filtrarListaObject(this.filtroColumns, this.lNotasCreditosOri);

	}
	
	@Command
	@NotifyChange("lNotasCreditos")
	public void onChangeFiltroFechas() {

		this.cargarNotasCreditos();

	}

	@Command
	@NotifyChange("*")
	public void cargarNotasCreditos() {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.lNotasCreditos = this.reg.sqlNativo(
				this.um.getSql("notacredito/listaNotaCredito.sql").replace("?1", this.getCurrentEmpresa().getEmpresaid() + "")
				.replace("?3", sdf.format(this.desde))
				.replace("?4", sdf.format(this.hasta))
				.replace("--2", ""));
		this.lNotasCreditosOri = this.lNotasCreditos;

		this.filtrarNotasCreditos();

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
		params.put("tipode", "notacredito");

		this.openInNewTabPost("sistema/zul/reporte/kudeViewer.zul", params);
		
	}
	
	private Window modal;
	private Evento eventoSelected;
	
	
	@Command()
	public void eventoModal(@BindingParam("id") Long id) {
		
		this.notacreditoSelected = this.reg.findObjectById(NotaCredito.class, id);
		this.eventoSelected = null;
		
		
		if (this.notacreditoSelected.getEventoTipo() != null) {
			
			this.mensajeInfo("El documanto ya posee un evento.");
			
			return ;
		}
		
		long horas = Duration.between(this.notacreditoSelected.getFecha(), LocalDateTime.now()).toHours();
		
		
		
		if (this.notacreditoSelected.getEstado().equals("Aprobado") && horas >= 41) {
			
			this.mensajeInfo("El tiempo de cancelacion del Documento ya expiro.");
			
			return;
		}
		
		if (this.notacreditoSelected.getEstado().contains("Pendiente")) {
			
			this.mensajeInfo("Solo puedes generar eventos a Documentos Aprobadas o Rechazadas");
			
			return;
			
		}
		
		Empresa e = this.reg.findObjectById(Empresa.class, this.getCurrentEmpresa().getEmpresaid());
		
		this.eventoSelected = new Evento();
		Contribuyente c = new Contribuyente();
		c.setContribuyenteid(e.getFcwsId());
		c.setPass(e.getFcwsPass());
		this.eventoSelected.setContribuyente(c);
		
		
		if (this.notacreditoSelected.getEstado().contains("Aprobado")) {
			
			Tipo t = this.reg.getObjectBySigla(Tipo.class, ParamsLocal.SIGLA_TIPO_EVENTO_CANCELACION);
			
			this.eventoSelected.setCdc(this.notacreditoSelected.getCdc());
			this.eventoSelected.setFecha(new Date());
			this.eventoSelected.setMotivo("Cancelacion de NotaCredito");
			this.eventoSelected.setEventoTipo(t);
			
			modal = (Window) Executions.createComponents("/sistema/zul/gestion/eventoCancelarModal.zul", this.mainComponent, null);
			
		}else if (this.notacreditoSelected.getEstado().equals("Rechazado")) {
			
			Tipo t = this.reg.getObjectBySigla(Tipo.class, ParamsLocal.SIGLA_TIPO_EVENTO_INUTILIZACION);
			
			this.eventoSelected.setFecha(new Date());
			
			this.eventoSelected.setTimbrado(this.notacreditoSelected.getTimbrado());
			String[] numeracion = this.notacreditoSelected.getTimbradoDocNro().split("-");
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
			
			rr = mde.enviarJson(this.getSistemaPropiedad("fcwsHOST").getValor()+"/evento/cancelarnotacredito", gson.toJson(this.eventoSelected));
			
		}else if (this.eventoSelected.getEventoTipo().getSigla().equals(ParamsLocal.SIGLA_TIPO_EVENTO_INUTILIZACION)) {
			
			rr = mde.enviarJson(this.getSistemaPropiedad("fcwsHOST").getValor()+"/evento/inutilizacionnumnotacredito", gson.toJson(this.eventoSelected));
			eventoResponse = gson.fromJson(rr.getMensaje(), Evento.class);
			
		}

		if (rr == null || rr.getCode() != 201) {
			
			this.mensajeError("Ocurrio al enviar el evento, reintenta en unos minutos.");
			
			return;
			
		}
		
		
		
		if (eventoResponse != null && eventoResponse.getEventoid() != null) {
			
			this.notacreditoSelected.setEventoid(eventoResponse.getEventoid());
		}
		
		this.notacreditoSelected.setEventoJson(gson.toJson(this.eventoSelected));
		this.notacreditoSelected.setEventoEstado("Pendiente");
		this.notacreditoSelected.setEventoFecha(this.eventoSelected.getFecha().toInstant()
		        .atZone(ZoneId.systemDefault())
		        .toLocalDateTime());
		this.notacreditoSelected.setEventoTipo(this.eventoSelected.getEventoTipo());
		
		this.save(this.notacreditoSelected);
		
		this.modal.detach();
		
		this.notacreditoSelected = null;
		this.eventoSelected = null;
		
		this.cargarNotasCreditos();
		
		BindUtils.postNotifyChange(null, null, this, "*");
		
	}
	
	@Command
	@NotifyChange("*")
	public void consultarEstados() {
		
		UtilLocalMetodos ulm = new UtilLocalMetodos();
		ulm.updateDE(new Register(), ulm.getSql("notacredito/listaNotaCreditoPendiente.sql").replace("?1", this.getCurrentEmpresa().getEmpresaid()+"").replace("--", ""), ulm.getSql("notacredito/updateNotaCredito.sql"));
	
		ulm.updateEvento(new Register(), ulm.getSql("notacredito/listaNotaCreditoEventoPendiente.sql").replace("?1", this.getCurrentEmpresa().getEmpresaid()+"").replace("--", ""), ulm.getSql("notacredito/updateNotaCreditoEvento.sql"));
		
		this.refrescarDatos();
	}
	
	@NotifyChange("*")
	@Command
	public void refrescarDatos(){
		
		this.cargarNotasCreditos();
		
		Notification.show("Refrescando Datos.");
	}
	

	public List<Object[]> getlNotasCreditos() {
		return lNotasCreditos;
	}

	public void setlNotasCreditos(List<Object[]> lNotasCreditos) {
		this.lNotasCreditos = lNotasCreditos;
	}

	public NotaCredito getNotaCreditoSelected() {
		return notacreditoSelected;
	}

	public void setNotaCreditoSelected(NotaCredito notacreditoSelected) {
		this.notacreditoSelected = notacreditoSelected;
	}

	public boolean isOpCrearNotaCredito() {
		return opCrearNotaCredito;
	}

	public void setOpCrearNotaCredito(boolean opCrearNotaCredito) {
		this.opCrearNotaCredito = opCrearNotaCredito;
	}

	public boolean isOpEditarNotaCredito() {
		return opEditarNotaCredito;
	}

	public void setOpEditarNotaCredito(boolean opEditarNotaCredito) {
		this.opEditarNotaCredito = opEditarNotaCredito;
	}

	public boolean isOpBorrarNotaCredito() {
		return opBorrarNotaCredito;
	}

	public void setOpBorrarNotaCredito(boolean opBorrarNotaCredito) {
		this.opBorrarNotaCredito = opBorrarNotaCredito;
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

	public Date getDesde() {
		return desde;
	}

	public void setDesde(Date desde) {
		this.desde = desde;
	}

	public Date getHasta() {
		return hasta;
	}

	public void setHasta(Date hasta) {
		this.hasta = hasta;
	}
	
	

}
