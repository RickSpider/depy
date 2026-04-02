package com.depy.sistema.operacion;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelArray;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.depy.modelo.Cliente;
import com.depy.modelo.Comprobante;
import com.depy.modelo.Remision;
import com.depy.modelo.RemisionDetalle;
import com.depy.modelo.Localidad;
import com.depy.modelo.Ruc;
import com.depy.modelo.Sucursal;
import com.depy.modelo.SucursalUsuario;
import com.depy.searchModel.ClienteSM;
import com.depy.searchModel.LocalidadSM;
import com.depy.util.ParamsLocal;
import com.depy.util.TemplateViewModelLocal;
import com.depy.util.UtilLocalMetodos;
import com.depy.utilde.MetodoDE;
import com.depy.utilde.conexion.HttpOrHttpsConexion;
import com.depy.utilde.conexion.ResultRest;
import com.depy.utilde.modelo.DE;
import com.depy.utilde.modelo.Kude;
import com.depy.utilde.response.ResponseComprobante;
import com.doxacore.modelo.Tipo;
import com.google.gson.Gson;

import org.zkoss.bind.annotation.Command;

public class ERemisionVM extends TemplateViewModelLocal {
	
	private Remision remisionSelected;
	
	private List<Object[]> remisiones;
	private Date desde;
	private Date hasta;
	
	private String sucursalStr;
	
	@Init(superclass = true)
	public void initRemisionVM() {
		
		this.desde = this.um.modificarHorasMinutosSegundos(new Date(), 0, 0, 0, 0);
		this.hasta = this.um.modificarHorasMinutosSegundos(this.desde, 23, 59, 59, 99);
		
		generarSearchModels();
		cargarDatosCb();
		limpiarPantalla();
		
		this.sucursalStr = this.getCurrentSucursalUsuario().getSucursal().getNombre();
		
		//System.out.println("Iniciando Remision...");

	}

	@AfterCompose(superclass = true)
	public void afterComposeRemisionVM() {

	}

	@Override
	protected void inicializarOperaciones() {

	}
	
	private ListModelArray<ClienteSM> lClienteSM;
	private ClienteSM clienteSMSelected;
	
	@Command
	@NotifyChange("*")
	public void limpiarPantalla() {
		
		List <Tipo> lTipos = this.reg.getAllObjects(Tipo.class);
		
		Map<String, Tipo> mapa = new HashMap<>();
		for (Tipo t : lTipos) {
		    mapa.put(t.getSigla(), t);
		}
		
		Sucursal su = this.getCurrentSucursalUsuario().getSucursal();
		
		this.clienteSMSelected = null;
		
		this.remisionSelected = new Remision();
		this.remisionSelected.setSucursal(su);

		this.remisionSelected.setSalidaDireccion(su.getDireccion());
		this.remisionSelected.setSalidaCasaNro(su.getCasaNro());	
		this.remisionSelected.setSalidaLocalidad(su.getLocalidad());
		
		this.localidadSalidaSMSelected = su.getLocalidad() != null ? new LocalidadSM( su.getLocalidad()): null;
		
		
		
		this.remisionSelected.setMotivoEmision(mapa.get(ParamsLocal.SIGLA_TIPO_MOTIVOREMISION_TRASLADOVENTA));
		this.remisionSelected.setTransModalidadtipo(mapa.get( ParamsLocal.SIGLA_TIPO_TRANSPORTEMODALIDAD_TERRESTRE));
		this.remisionSelected.setTransTipo(mapa.get( ParamsLocal.SIGLA_TIPO_TRANSPORTE_PROPIO));
		this.remisionSelected.setResponsableEmision(mapa.get( ParamsLocal.SIGLA_TIPO_RESPONSABLEREMI_EMISORFE));
		this.remisionSelected.setTransResponsableFlete(mapa.get( ParamsLocal.SIGLA_TIPO_RESPONSABLEFLETE_EMISORFE));
		
		this.remisionSelected.setTransportistaNombre(su.getEmpresa().getRazonSocial());
		this.remisionSelected.setTransportistaDireccion(su.getDireccion());
		this.remisionSelected.setTransportistaDocNum(su.getEmpresa().getRuc());
		this.remisionSelected.setTransportistaDocTipo(mapa.get( ParamsLocal.SIGLA_TIPO_DOCUMENTO_RUC));
		
		this.remisionSelected.setVehiculoIdent(mapa.get(ParamsLocal.SIGLA_TIPO_VEHICULOIDENTIFICACION_NROIDENT));
		
		this.remisionSelected.setInfoFisco("Mercaderia Fragil");
		
		RemisionDetalle det = new RemisionDetalle(this.remisionSelected, su.getEmpresa());
		det.setEmpresa(su.getEmpresa());
		this.remisionSelected.getDetalles().add(det);
		
		
	}
	
	public void generarSearchModels() {

		generarClienteSM();
		generarSearchModelLocalidad();

	}

	private ListModelArray<LocalidadSM> lLocalidadEntregaSM;
	private LocalidadSM localidadEntregaSMSelected;
	
	private ListModelArray<LocalidadSM> lLocalidadSalidaSM;
	private LocalidadSM localidadSalidaSMSelected;

	
	private void generarSearchModelLocalidad() {
		
		List<Object[]> resultados = this.reg.sqlNativo(this.um.getSql("localidad/buscarLocalidad.sql"));
		
		this.lLocalidadEntregaSM = this.crearSearchModel(
				
	        resultados,
	        o -> new LocalidadSM(
	                ((Number) o[0]).longValue(),
	                (String) o[1],
	                ((Number) o[2]).longValue(),
	                (String) o[3],
	                ((Number) o[4]).longValue(),
	                (String) o[5]
	            )
	    );
		
		this.lLocalidadSalidaSM = this.crearSearchModel(
				
		        resultados,
		        o -> new LocalidadSM(
		                ((Number) o[0]).longValue(),
		                (String) o[1],
		                ((Number) o[2]).longValue(),
		                (String) o[3],
		                ((Number) o[4]).longValue(),
		                (String) o[5]
		            )
		    );
	}
	
	public void generarClienteSM(){
		
		this.lClienteSM = this.crearSearchModel(				
	        this.um.getSql("cliente/buscarCliente.sql").replace("?1", this.getCurrentEmpresa().getEmpresaid()+""),
	        o -> new ClienteSM(
	                ((Number) o[0]).longValue(),
	                (String) o[1],
	                (String) o[2],
	                (String) o[3]
	        	)
	        );
		
	}

	@Command
	@NotifyChange("*")
	public void onSelectedCliente() {
		
		this.remisionSelected.setCliente(null);
		this.remisionSelected.setDocumentoNro(null);
		this.remisionSelected.setRazonSocial(null);
		this.remisionSelected.setDocumentoTipo(null);
		this.remisionSelected.setDireccion(null);
		this.remisionSelected.setCasaNro(null);
		this.remisionSelected.setLocalidad(null);
		
		if (this.clienteSMSelected == null) {
			
			return;
			
		}

		Cliente c = this.reg.findObjectById(Cliente.class, this.clienteSMSelected.getId());

		boolean todos  = c.getLocalidad() != null && (c.getDireccion() != null && !c.getDireccion().isBlank()) && (c.getCasaNro() != null);

		if (!todos) {
	
			this.clienteSMSelected = null;
			
		    this.mensajeInfo("Verifique que el cliente tenga cargado Localidad, Direccion, y nro de casa");
		    
		    return;
		}
		
		this.remisionSelected.setCliente(c);
		this.remisionSelected.setLocalidad(c.getLocalidad());
		this.remisionSelected.setDocumentoNro(c.getDocumentoNro());
		this.remisionSelected.setRazonSocial(c.getRazonsocial());
		this.remisionSelected.setDocumentoTipo(c.getDocumentoTipo());
		this.remisionSelected.setDireccion(c.getDireccion());
		this.remisionSelected.setCasaNro(c.getCasaNro());
		
		this.localidadEntregaSMSelected = new LocalidadSM( c.getLocalidad());
		this.remisionSelected.setEntregaLocalidad(c.getLocalidad());
		this.remisionSelected.setEntregaDireccion(c.getDireccion());
		this.remisionSelected.setEntregaCasaNro(c.getCasaNro());
			
	}
	
	Map<String, List<Tipo>> agrupadoTipo = new HashMap<>();
	
	public void cargarDatosCb() {
		
			
		List <Tipo> lTipos = this.reg.getAllObjects(Tipo.class);
		
		for (Tipo t : lTipos) {
		    String key = t.getTipotipo().getSigla();

		    agrupadoTipo.computeIfAbsent(key, k -> new ArrayList<>())
		            .add(t);
		}

	}
	
	private ListModelArray<LocalidadSM> lLocalidadSearchModel;
	private LocalidadSM localidadSMSelected;

	
	private void generarSearchModelCliente() {
		
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
	}
	
	
	@Command
	@NotifyChange("*")
	public void onSelectedLocalidad() {
		
		this.remisionSelected.setEntregaLocalidad(this.localidadEntregaSMSelected != null ? this.reg.findObjectById(Localidad.class, this.localidadEntregaSMSelected.getLocalidadid()) : null );
		
		this.remisionSelected.setSalidaLocalidad(this.localidadSalidaSMSelected != null ? this.reg.findObjectById(Localidad.class, this.localidadSalidaSMSelected.getLocalidadid()) : null );
			
	}
	
	@Command
	@NotifyChange("*")
	public void onChangeDoc() {
		
		String docNro = this.remisionSelected.getCliente().getDocumentoNro();
		
		UtilLocalMetodos ulm = new UtilLocalMetodos();
		if (this.remisionSelected.getCliente().getDocumentoTipo().getSigla().equals(ParamsLocal.SIGLA_TIPO_DOCUMENTO_RUC) && !ulm.validarRuc(docNro)) {
			
			this.mensajeError("El documento de RUC proporcionado no es correcto, o no cumple con el formato, verifica el digito verificador o el ruc nuevamente");
			
			return;
		}
		
		int idx = docNro.indexOf('-');
		if (idx > 0) {

			String [] columns = {"ruc","dv"};
		    Object[] valor = {docNro.substring(0, idx), docNro.substring(idx + 1)};
		   
		    Ruc ruc = this.reg.getObjectByColumns(Ruc.class, columns, valor);
		    
		    this.remisionSelected.getCliente().setRazonsocial(ruc != null ? ruc.getRazonSocial(): null);
		    
		}
		
	}
	
	
	
	@Command
	public void agregarDetalle() {
		
		this.remisionSelected.getDetalles().add(new RemisionDetalle(this.remisionSelected, this.getCurrentEmpresa()));
		BindUtils.postNotifyChange(null, null, this.remisionSelected, "detalles");
	}
	
	public boolean verficarCamposCliente() {
		
		if (this.remisionSelected.getCliente().getDocumentoNro() == null 
				|| this.remisionSelected.getCliente().getDocumentoNro().isBlank()) {
			
			this.mensajeInfo("Debes cargar un numero de documento.");
			
			return false;
			
		}
		
		if (this.remisionSelected.getCliente().getRazonsocial() == null 
				|| this.remisionSelected.getCliente().getRazonsocial().isBlank()) {
			
			this.mensajeInfo("Debes cargar la Razon social.");
			
			return false;
			
		}
		
		boolean alguno = this.localidadSMSelected != null || (this.remisionSelected.getCliente().getDireccion() != null && !this.remisionSelected.getCliente().getDireccion().isBlank()) || (this.remisionSelected.getCliente().getCasaNro() != null );
		boolean todos  = this.localidadSMSelected != null && (this.remisionSelected.getCliente().getDireccion() != null && !this.remisionSelected.getCliente().getDireccion().isBlank()) && (this.remisionSelected.getCliente().getCasaNro() != null );

		if (alguno && !todos) {
		    this.mensajeInfo("Localidad, dirección y número de casa deben informarse juntos");
		    return false;
		}
		
		
		return true;
		
	}
	
	@Command
	@NotifyChange({"totalDetalle", "iva10","iva5","iva0"})
	public void onChangeIva(@BindingParam("detalle") RemisionDetalle det) {
		
		 switch ((int) det.getTasaIva()) {
	         case 10, 5 : {
	        	 det.setAfectacionTributaria(1l);
	        	 det.setProporcionIva(100);
	        	 break;
	         }
	         case 0 : {
	        	 det.setAfectacionTributaria(3l);
	        	 det.setProporcionIva(0);
	        	 break;
	         }
         
		 }
	
	}
	
	@NotifyChange({"totalDetalle", "iva10","iva5","iva0"})
	public void borrarDetalle(RemisionDetalle detalle) {
		
		this.remisionSelected.getDetalles().remove(detalle);
		
		BindUtils.postNotifyChange(null, null, this.remisionSelected, "detalles");
		
		
	}
	
	public boolean verificarCampos() {
		
		if (this.remisionSelected.getCliente() == null) {
			
			this.mensajeInfo("Tienes que agregar un cliente.");
			return false;
			
		}
		
		if (!(this.remisionSelected.getVehiculoIdent() != null 
				&& (this.remisionSelected.getVehiculoMarca() != null && !this.remisionSelected.getVehiculoMarca().isBlank()) 
				&& (this.remisionSelected.getVehiculoNro() != null && !this.remisionSelected.getVehiculoNro().isBlank()))) {
			
			this.mensajeInfo("Tienes que agregar los datos del Vehiculo.");
			return false;
			
		}
		
		if (!((this.remisionSelected.getChoferNombre() != null && !this.remisionSelected.getChoferNombre().isBlank() )
				&& (this.remisionSelected.getChoferDocNum() != null && !this.remisionSelected.getChoferDocNum().isBlank()) 
				&& (this.remisionSelected.getChoferDireccion() != null && !this.remisionSelected.getChoferDireccion().isBlank()))) {
			
			this.mensajeInfo("Tienes que agregar los datos del Chofer.");
			return false;
			
		}
		
		for (RemisionDetalle x : this.remisionSelected.getDetalles()) {
			
			/*if (x.getItemCodigo() == null || x.getItemCodigo().isBlank()) {
				this.mensajeInfo("Tienes items sin codigo.");
				return false;
				
			}*/
			
			if (x.getItemDescripcion() == null ||  x.getItemDescripcion().isBlank()) {
				this.mensajeInfo("Tienes items sin descripcion.");
				return false;
			}
					
		}
		
		return true;
	}
	
	@Command
	public void generarRemision() {
		
		if (!verificarCampos()) {
			return;
		}
		
		EventListener event = new EventListener() {

			@Override
			public void onEvent(Event evt) throws Exception {

				if (evt.getName().equals(Messagebox.ON_YES)) {

					procesarRemision();

				}

			}

		};
		
		this.mensajeSiNo("Se procedera a generar la remision.\n ¿Continuar?", "Remision", event);
		
	}
	
	public void procesarRemision() {
		
		this.remisionSelected.setFecha(LocalDateTime.now());
			
		SucursalUsuario su =  this.getCurrentSucursalUsuario();
			
		Tipo t = this.reg.getObjectBySigla(Tipo.class, ParamsLocal.SIGLA_TIPO_COMPROBANTE_REMISION);
		
		String [] columns = {"empresa","comprobanteTipo","establecimiento","puntoExpedicion","activo"};
		Object [] values = {su.getSucursal().getEmpresa(), t ,su.getSucursal().getEstablecimiento() , su.getPuntoExpedicion(),true};
		
		Comprobante comp = this.reg.getObjectByColumns(Comprobante.class, columns, values);
		this.remisionSelected.setTimbrado(comp.getTimbrado());
		this.remisionSelected.setTimbradoFecha(comp.getFechaInicio());
		this.remisionSelected.setTimbradoDocNro(comp.getEstablecimiento()+"-"+comp.getPuntoExpedicion()+"-"+String.format("%07d", comp.getSigteNro()));
		this.remisionSelected.setTimbradoSerie(comp.getSerie());
		
		comp.setSigteNro(comp.getSigteNro()+1);
		
		this.save(comp);
		
		int cont = 1;
		for(RemisionDetalle det : this.remisionSelected.getDetalles()) {
			
			if (det.getItemCodigo() == null) {
				det.setItemCodigo("SC"+String.format("%03d", cont));
				cont++;
			}
			
		}
		
		this.remisionSelected = this.save(this.remisionSelected);
		
		this.enviarRemision(this.remisionSelected, su);
		
		this.verKude(this.remisionSelected.getRemisionid());
		
		this.limpiarPantalla();
		
		BindUtils.postNotifyChange(null, null, this, "*");

	}
	
	public void enviarRemision(Remision remi, SucursalUsuario su) {
		
		MetodoDE mde = new MetodoDE();
		DE de = mde.getDe(su.getSucursal().getNombre(), remi, su.getSucursal().getEmpresa().getFcwsId(), su.getSucursal().getEmpresa().getFcwsPass());
		
		ResultRest rr = mde.enviarDE(this.getSistemaPropiedad("fcwsHOST").getValor()+"/remision", de);
		
		if (rr != null) {
			
			Gson gson = new Gson();
			Kude k = gson.fromJson(rr.getMensaje(), Kude.class);
			
			remi.setCdc(k.getCdc());
			remi.setQr(k.getQr());
			
			this.save(remi);
		}
	}
	
	
	
	@NotifyChange("remisiones")
	public void cargarDatos() {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		this.remisiones = this.reg.sqlNativo(
				this.um.getSql("remision/listaRemision.sql")
				.replace("?1", this.getCurrentEmpresa().getEmpresaid()+"")
				.replace("?2", this.getCurrentSucursal().getSucursalid()+"")
				.replace("?3", sdf.format(desde))
				.replace("?4", sdf.format(hasta))
				.replace("--1", "")
				.replace("--2", ""));
		
		
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
		
		this.consultarDe(id);
				 	
	    Map<String, String> params = new HashMap<>();
		params.put("id", String.valueOf(id));
		params.put("tipode", "remision");

		this.openInNewTabPost("sistema/zul/reporte/kudeViewer.zul", params);
		
	}
	
	@Command
	public void consultarDe(@BindingParam("id") long id) {
		
		Remision f = this.reg.findObjectById(Remision.class, id);
		
		if (f.getCdc() != null && f.getXml() == null) {
			
			UtilLocalMetodos ulm = new UtilLocalMetodos();
			
			
				ResponseComprobante rc = ulm.consultarDE(this.getSistemaPropiedad("fcwsHOST").getValor()+"/consultar/comprobantexml/"+f.getCdc(), new HttpOrHttpsConexion());
				
				f.setXml(rc.getXml());
				f.setEstado(rc.getEstado());
				f.setRespuesta(ulm.escapeSql(rc.getRespuesta()));
				this.save(f);
				
			
				System.out.println("Error al consultar el DE");
		
		}

	}
	
	private Window modal;
	
	@Command
	public void remisionesModal() {
		
		this.cargarDatos();

		modal = (Window) Executions.createComponents("/sistema/zul/operacion/remisionesModal.zul", this.mainComponent, null);
		Selectors.wireComponents(modal, this, false);
		modal.doModal();
	}


	public Remision getRemisionSelected() {
		return remisionSelected;
	}

	public void setRemisionSelected(Remision remisionSelected) {
		this.remisionSelected = remisionSelected;
	}

	public ListModelArray<ClienteSM> getlClienteSM() {
		return lClienteSM;
	}

	public void setlClienteSM(ListModelArray<ClienteSM> lClienteSM) {
		this.lClienteSM = lClienteSM;
	}

	public ClienteSM getClienteSMSelected() {
		return clienteSMSelected;
	}

	public void setClienteSMSelected(ClienteSM clienteSMSelected) {
		this.clienteSMSelected = clienteSMSelected;
	}

	public List<Object[]> getRemisiones() {
		return remisiones;
	}

	public void setRemisiones(List<Object[]> remisiones) {
		this.remisiones = remisiones;
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

	public String getSucursalStr() {
		return sucursalStr;
	}

	public void setSucursalStr(String sucursalStr) {
		this.sucursalStr = sucursalStr;
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

	public ListModelArray<LocalidadSM> getlLocalidadEntregaSM() {
		return lLocalidadEntregaSM;
	}

	public void setlLocalidadEntregaSM(ListModelArray<LocalidadSM> lLocalidadEntregaSM) {
		this.lLocalidadEntregaSM = lLocalidadEntregaSM;
	}

	public LocalidadSM getLocalidadEntregaSMSelected() {
		return localidadEntregaSMSelected;
	}

	public void setLocalidadEntregaSMSelected(LocalidadSM localidadEntregaSMSelected) {
		this.localidadEntregaSMSelected = localidadEntregaSMSelected;
	}

	public ListModelArray<LocalidadSM> getlLocalidadSalidaSM() {
		return lLocalidadSalidaSM;
	}

	public void setlLocalidadSalidaSM(ListModelArray<LocalidadSM> lLocalidadSalidaSM) {
		this.lLocalidadSalidaSM = lLocalidadSalidaSM;
	}

	public LocalidadSM getLocalidadSalidaSMSelected() {
		return localidadSalidaSMSelected;
	}

	public void setLocalidadSalidaSMSelected(LocalidadSM localidadSalidaSMSelected) {
		this.localidadSalidaSMSelected = localidadSalidaSMSelected;
	}

	public List<Tipo> getlDocumentoTipo() {
		return agrupadoTipo.get(ParamsLocal.SIGLA_TIPOTIPO_DOCUMENTO);
	}

	public List<Tipo> getlTransporteTipo() {
		return agrupadoTipo.get(ParamsLocal.SIGLA_TIPOTIPO_TRANSPORTE);
	}

	public List<Tipo> getlTransporteModalidad() {
		return agrupadoTipo.get(ParamsLocal.SIGLA_TIPOTIPO_TRANSPORTEMODALIDAD);
	}

	public List<Tipo> getlResponsableRemi() {
		return agrupadoTipo.get(ParamsLocal.SIGLA_TIPOTIPO_RESPONSABLEREMI);
	}

	public List<Tipo> getlResponsableFlete() {
		return agrupadoTipo.get(ParamsLocal.SIGLA_TIPOTIPO_RESPONSABLEFLETE);
	}

	public List<Tipo> getlMotivoEmision() {
		return agrupadoTipo.get(ParamsLocal.SIGLA_TIPOTIPO_MOTIVOREMISION);
	}

	public List<Tipo> getlVehiculoIdentificacion() {
		return agrupadoTipo.get(ParamsLocal.SIGLA_TIPOTIPO_VEHICULOIDENTIFICACION);
	}

	
	
}
