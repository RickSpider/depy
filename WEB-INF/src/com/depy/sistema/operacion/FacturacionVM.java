package com.depy.sistema.operacion;

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
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelArray;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.depy.modelo.Cliente;
import com.depy.modelo.Comprobante;
import com.depy.modelo.Empresa;
import com.depy.modelo.Factura;
import com.depy.modelo.FacturaDetalle;
import com.depy.modelo.FacturaPago;
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

public class FacturacionVM extends TemplateViewModelLocal {
	
	private Factura facturaSelected;
	
	private List<Object[]> facturaciones;
	private Date desde;
	private Date hasta;
	
	private String sucursalStr;
	private String cbPlazo = "dias";
	
	private Tipo facturaDefault;
	private Tipo efectivoDefault;
	
	private boolean dncpOpen = false;
	
	@Init(superclass = true)
	public void initFacturacionVM() {
		
		this.desde = this.um.modificarHorasMinutosSegundos(new Date(), 0, 0, 0, 0);
		this.hasta = this.um.modificarHorasMinutosSegundos(this.desde, 23, 59, 59, 99);
		
		generarSearchModels();
		cargarDatosTipos();
		limpiarPantalla();
		
		this.sucursalStr = this.getCurrentSucursalUsuario().getSucursal().getNombre();
		
		//System.out.println("Iniciando Facturacion...");

	}

	@AfterCompose(superclass = true)
	public void afterComposeFacturacionVM() {

	}

	@Override
	protected void inicializarOperaciones() {

	}
	
	private ListModelArray<ClienteSM> lClienteSM;
	private ClienteSM clienteSMSelected;
	
	@Command
	@NotifyChange("*")
	public void limpiarPantalla() {
		
		this.iva0 = 0;
		this.iva10 = 0;
		this.iva5 = 0;
		
		this.cbPlazo ="dias";
		
		this.clienteSMSelected = null;
		
		this.facturaSelected = new Factura();
		this.facturaSelected.setFecha(LocalDateTime.now());
		this.facturaSelected.setMonedaCambio(1.0);
		this.facturaSelected.setSucursal(getCurrentSucursal());
		
		
		
		FacturaPago fp = new FacturaPago();
		//fp.setPagoTipo(this.reg.getObjectBySigla(Tipo.class, ParamsLocal.SIGLA_TIPO_FORMAPAGO_EFECTIVO));
		fp.setPagoTipo(this.efectivoDefault);
		fp.setFactura(facturaSelected);
		fp.setEmpresa(getCurrentEmpresa());
		
		this.facturaSelected.getPagos().add(fp);

		for (Tipo t : this.lMoneda) {
			
			if (t.getSigla().equals(ParamsLocal.SIGLA_TIPO_MONEDA_GUARANIES)) {
				
				this.facturaSelected.setMoneda(t);
				break;
			}
			
		}
		
		for (Tipo t : this.lCondicionPago) {
			
			if (t.getSigla().equals(ParamsLocal.SIGLA_TIPO_CONDICIONPAGO_CONTADO)) {
				
				this.facturaSelected.setCondicion(t);
				break;
			}
			
		}
		
		this.facturaSelected.getDetalles().add(new FacturaDetalle(this.facturaSelected, 10, 1L,100, this.getCurrentEmpresa()));
		
	}
	
	public void generarSearchModels() {

		generarClienteSM();

	}
	
	public void generarClienteSM(){
		
		//List<Object[]> lclientes = this.reg.sqlNativo(this.um.getSql("cliente/buscarCliente.sql").replace("?1", this.getCurrentEmpresa().getEmpresaid()+""));
		
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
	
	private List<Integer> lIva;
	private List<Tipo> lCondicionPago;
	private List<Tipo>lMoneda;
	
	
	public void cargarDatosTipos() {
		
		List <Tipo> lTipos = this.reg.getAllObjectsByColumnIn(Tipo.class,"tipotipo.sigla", List.of(
			    ParamsLocal.SIGLA_TIPOTIPO_CONDICIONPAGO,
			    ParamsLocal.SIGLA_TIPOTIPO_MONEDA,
			    ParamsLocal.SIGLA_TIPOTIPO_IVA,
			    ParamsLocal.SIGLA_TIPOTIPO_DOCUMENTO,
			    ParamsLocal.SIGLA_TIPOTIPO_COMPROBANTE,
			    ParamsLocal.SIGLA_TIPOTIPO_FORMAPAGO
			) );
		
		this.lCondicionPago = new ArrayList<Tipo>();
		this.lMoneda = new ArrayList<Tipo>();
		this.lIva = new ArrayList<>();
		this.lDocumentoTipo = new ArrayList<>();
		
		
		for (Tipo t : lTipos) {
			
			if (t.getTipotipo().getSigla().equals(ParamsLocal.SIGLA_TIPOTIPO_CONDICIONPAGO)) {
				
				this.lCondicionPago.add(t);
				
			}else if (t.getTipotipo().getSigla().equals(ParamsLocal.SIGLA_TIPOTIPO_MONEDA)) {
				
				this.lMoneda.add(t);
				
			}else if (t.getTipotipo().getSigla().equals(ParamsLocal.SIGLA_TIPOTIPO_IVA)) {
				
				this.lIva.add(Integer.valueOf(t.getTipo()));
				
			}else if (t.getTipotipo().getSigla().equals(ParamsLocal.SIGLA_TIPOTIPO_DOCUMENTO)) {
				
				this.lDocumentoTipo.add(t);
				
			}
			
			if (t.getSigla().equals(ParamsLocal.SIGLA_TIPO_COMPROBANTE_FACTURA)) {
				this.facturaDefault = t;
			}
			
			if (t.getSigla().equals(ParamsLocal.SIGLA_TIPO_FORMAPAGO_EFECTIVO)) {
				this.efectivoDefault = t;
			}
			
		}
				
		/*Tipotipo tt = this.reg.getObjectBySigla(Tipotipo.class,ParamsLocal.SIGLA_TIPOTIPO_CONDICIONPAGO);
		String [] cols = {"tipotipo"};
		Object [] value = {tt}; 
		lCondicionPago = this.reg.getAllObjectsByColumns(Tipo.class, cols, value);
		
		Tipotipo tt2 = this.reg.getObjectBySigla(Tipotipo.class,ParamsLocal.SIGLA_TIPOTIPO_MONEDA);
		String [] cols2 = {"tipotipo"};
		Object [] value2 = {tt2}; 
		lMoneda = this.reg.getAllObjectsByColumns(Tipo.class, cols2, value2);
		
		List<Object[]> ivaAux = this.reg.sqlNativo(this.um.getCoreSql("buscarTiposPorSiglaTipotipo.sql").replace("?1", ParamsLocal.SIGLA_TIPOTIPO_IVA));
		for (Object[] x : ivaAux) {
			
			this.lIva.add(Integer.valueOf(x[1].toString()));
			
		} */

	}
	
	private Window modal;
	//private Cliente clienteSelected;
	
	@Command
	public void crearCliente() {
		
		//this.cargarClienteDatosCb();
		
		this.facturaSelected.setCliente(new Cliente());
		
		for (Tipo t : this.lDocumentoTipo) {
			
			if (t.getSigla().equals(ParamsLocal.SIGLA_TIPO_DOCUMENTO_RUC)) {
				
				this.facturaSelected.getCliente().setDocumentoTipo(t);
				break;
			}
			
		}
		
		//this.facturaSelected.getCliente().setDocumentoTipo(this.reg.getObjectBySigla(Tipo.class, ParamsLocal.SIGLA_TIPO_DOCUMENTO_RUC));
		//this.documentoTipoSM = new TipoSM(this.facturaSelected.getCliente().getDocumentoTipo());
		
		modal = (Window) Executions.createComponents("/sistema/zul/operacion/crearClienteModal.zul", this.mainComponent, null);
		Selectors.wireComponents(modal, this, false);
		modal.doModal();
		
		modal.addEventListener("onLater", event -> {
		 	this.generarSearchModelCliente();
		 
		    BindUtils.postNotifyChange(null, null, this, "lLocalidadSearchModel");
		   // BindUtils.postNotifyChange(null, null, this, "lDocumentoTipo");
		    // Clients.clearBusy();
		});

		 Events.echoEvent("onLater", modal, null);
		 
		 //BindUtils.postNotifyChange(null, null, this.facturaSelected, "*");
		
	}
	
	private List<Tipo> lDocumentoTipo;
	
	/*public void cargarClienteDatosCb() {
		
		Tipotipo tt = this.reg.getObjectBySigla(Tipotipo.class,ParamsLocal.SIGLA_TIPOTIPO_DOCUMENTO);
		String [] cols = {"tipotipo"};
		Object [] value = {tt}; 
		lDocumentoTipo = this.reg.getAllObjectsByColumns(Tipo.class, cols, value);
		
	}*/
	
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
		
		this.facturaSelected.getCliente().setLocalidad(this.localidadSMSelected != null ? this.reg.findObjectById(Localidad.class, this.localidadSMSelected.getLocalidadid()) : null );
	
	}
	
	@Command
	@NotifyChange("*")
	public void onChangeDoc() {
		
		String docNro = this.facturaSelected.getCliente().getDocumentoNro();
		
		UtilLocalMetodos ulm = new UtilLocalMetodos();
		if (this.facturaSelected.getCliente().getDocumentoTipo().getSigla().equals(ParamsLocal.SIGLA_TIPO_DOCUMENTO_RUC) && !ulm.validarRuc(docNro)) {
			
			this.mensajeError("El documento de RUC proporcionado no es correcto, o no cumple con el formato, verifica el digito verificador o el ruc nuevamente");
			
			return;
		}
		
		int idx = docNro.indexOf('-');
		if (idx > 0) {

			String [] columns = {"ruc","dv"};
		    Object[] valor = {docNro.substring(0, idx), docNro.substring(idx + 1)};
		   
		    Ruc ruc = this.reg.getObjectByColumns(Ruc.class, columns, valor);
		    
		    this.facturaSelected.getCliente().setRazonsocial(ruc != null ? ruc.getRazonSocial(): null);
		    
		}
		
	}
	
	@Command
	@NotifyChange("*")
	public void onChangeCondicion() {
		
		if (this.facturaSelected.getCondicion().getSigla().equals(ParamsLocal.SIGLA_TIPO_CONDICIONPAGO_CREDITO)) {
			
			this.cbPlazo ="dias";
			this.facturaSelected.setPagos(new ArrayList<>());
			
		}else {
			
			this.facturaSelected.setPlazoCredito(null);
			this.facturaSelected.setPagos(new ArrayList<>());
			FacturaPago fp = new FacturaPago();
			fp.setFactura(this.facturaSelected);
			//fp.setPagoTipo(this.reg.getObjectBySigla(Tipo.class, ParamsLocal.SIGLA_TIPO_FORMAPAGO_EFECTIVO));
			fp.setPagoTipo(efectivoDefault);
			fp.setEmpresa(getCurrentEmpresa());
			this.facturaSelected.getPagos().add(fp);
		}
		
	}
	
	
	
	@Command
	public void agregarDetalle() {
		
		this.facturaSelected.getDetalles().add(new FacturaDetalle(this.facturaSelected, 10, 1L,100, this.getCurrentEmpresa()));
		BindUtils.postNotifyChange(null, null, this.facturaSelected, "detalles");
	}
	
	@Command
	@NotifyChange("*")
	public void guardarCliente() {
		

		if (!this.verficarCamposCliente()) {
			return;
		}

		String [] columns = {"empresa","documentoNro"};
	    Object[] valor = {this.getCurrentEmpresa(), this.facturaSelected.getCliente().getDocumentoNro()};
		Cliente cAux = this.reg.getObjectByColumns(Cliente.class, columns, valor);
		if (cAux != null) {
			
			this.mensajeInfo("Ya existe el contribuyente...");
			return;
			
		}		
		
		//this.facturaSelected.getCliente().setDocumentoTipo(this.documentoTipoSM.getTipo());
		
		this.facturaSelected.getCliente().setDocumentoNro(this.facturaSelected.getCliente().getDocumentoNro().trim());
		this.facturaSelected.getCliente().setRazonsocial(this.facturaSelected.getCliente().getRazonsocial().trim());
		
		this.facturaSelected.setCliente(this.save(this.facturaSelected.getCliente())); 
		
		this.clienteSMSelected = new ClienteSM(this.facturaSelected.getCliente());
		
		
		generarClienteSM();
		
		modal.detach();
		
		//BindUtils.postNotifyChange(null, null, this, "clienteSMSelected");
		//BindUtils.postNotifyChange(null, null, this, "clienteSelected");
	}
	
	public boolean verficarCamposCliente() {
		
		if (this.facturaSelected.getCliente().getDocumentoNro() == null 
				|| this.facturaSelected.getCliente().getDocumentoNro().isBlank()) {
			
			this.mensajeInfo("Debes cargar un numero de documento.");
			
			return false;
			
		}
		
		if (this.facturaSelected.getCliente().getRazonsocial() == null 
				|| this.facturaSelected.getCliente().getRazonsocial().isBlank()) {
			
			this.mensajeInfo("Debes cargar la Razon social.");
			
			return false;
			
		}
		
		boolean alguno = this.localidadSMSelected != null || (this.facturaSelected.getCliente().getDireccion() != null && !this.facturaSelected.getCliente().getDireccion().isBlank()) || (this.facturaSelected.getCliente().getCasaNro() != null );
		boolean todos  = this.localidadSMSelected != null && (this.facturaSelected.getCliente().getDireccion() != null && !this.facturaSelected.getCliente().getDireccion().isBlank()) && (this.facturaSelected.getCliente().getCasaNro() != null );

		if (alguno && !todos) {
		    this.mensajeInfo("Localidad, dirección y número de casa deben informarse juntos");
		    return false;
		}
		
		
		return true;
		
	}
	
	@Command
	@NotifyChange({"totalDetalle", "iva10","iva5","iva0"})
	public void onChangeIva(@BindingParam("detalle") FacturaDetalle det) {
		
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
		
		this.calcularTotales();
	}
	
	@Command
	@NotifyChange({"totalDetalle", "iva10","iva5","iva0"})
	public void calcularTotales() {
		
		this.iva10 = 0;
		this.iva5 = 0;
		this.iva0 = 0;
		
		double totalAux = 0;
		
		for (FacturaDetalle x : this.facturaSelected.getDetalles()) {
			
			double totalLinea = x.getCantidad() *
                    (x.getPrecioUnitario() != null ? x.getPrecioUnitario() : 0);
			
			totalAux += totalLinea;
			
			
			 switch ((int) x.getTasaIva()) {
	            case 10 -> iva10 += totalLinea / 11;
	            case 5  -> iva5  += totalLinea / 21;
	            case 0  -> iva0  += totalLinea;
	        }

		}
		
		this.facturaSelected.setTotalDetalle(totalAux);

	}
	
	@NotifyChange({"totalDetalle", "iva10","iva5","iva0"})
	public void borrarDetalle(@BindingParam("detalle") FacturaDetalle detalle) {
		
		this.facturaSelected.getDetalles().remove(detalle);
		
		BindUtils.postNotifyChange(null, null, this.facturaSelected, "detalles");
		
		this.calcularTotales();
		
	}
	
	
	private double iva10 = 0, iva5 = 0, iva0 = 0;
		
	public double getTotalDetalle() {

		return this.facturaSelected.getTotalDetalle();
		
	}
	
	public boolean verificarCampos() {
		
		if (this.clienteSMSelected == null) {
			
			this.mensajeInfo("Debes seleccionar un cliente.");
			return false;
			
		}
		
		if (!this.facturaSelected.getMoneda().getSigla().equals(ParamsLocal.SIGLA_TIPO_MONEDA_GUARANIES) && this.facturaSelected.getMonedaCambio() == 1 ) {
			this.mensajeInfo("Cuando la moneda no es Guaranies(PYG) el cambio debe ser mayor a 1 (uno).");
			return false;
		}
		
		if (this.facturaSelected.getCondicion().getSigla().equals(ParamsLocal.SIGLA_TIPO_CONDICIONPAGO_CREDITO)) {
			
			if (this.facturaSelected.getPlazoCredito() == null || this.facturaSelected.getPlazoCredito().isBlank()) {
				this.mensajeInfo("Debes cargar el plazo ya sea en dias o meses.");
				return false;
			}
						
		}
		
		for (FacturaDetalle x : this.facturaSelected.getDetalles()) {
			
			/*if (x.getItemCodigo() == null || x.getItemCodigo().isBlank()) {
				this.mensajeInfo("Tienes items sin codigo.");
				return false;
				
			}*/
			
			if (x.getItemDescripcion() == null ||  x.getItemDescripcion().isBlank()) {
				this.mensajeInfo("Tienes items sin descripcion.");
				return false;
			}
			
			if (x.getPrecioUnitario() == null ||  x.getPrecioUnitario()<=0) {
				this.mensajeInfo("El precio debe ser mayor o igual a 1 (uno).");
				return false;
			}
			
			
			
		}
		
		return true;
	}
	
	@Command
	public void generarFactura() {
		
		if (!verificarCampos()) {
			return;
		}
		
		EventListener event = new EventListener() {

			@Override
			public void onEvent(Event evt) throws Exception {

				if (evt.getName().equals(Messagebox.ON_YES)) {

					procesarFactura();

				}

			}

		};
		
		this.mensajeSiNo("Se Procedera a facturar.\n ¿Continuar?", "Facturacion", event);
		
	}
	
	public void procesarFactura() {
				
		Cliente c = this.reg.findObjectById(Cliente.class, this.clienteSMSelected.getId());
		
		this.facturaSelected.setCliente(c);
		this.facturaSelected.setDocumentoNro(c.getDocumentoNro());
		this.facturaSelected.setRazonSocial(c.getRazonsocial());
		this.facturaSelected.setDocumentoTipo(c.getDocumentoTipo());
		
		this.facturaSelected.setEmail(c.getEmail());
				
		if (c.getLocalidad() != null && c.getDireccion() != null) {
			this.facturaSelected.setLocalidad(c.getLocalidad());
			this.facturaSelected.setDireccion(c.getDireccion());
			this.facturaSelected.setCasaNro(c.getCasaNro());
		}
		
		
		SucursalUsuario su =  this.getCurrentSucursalUsuario();
			
		//Tipo t = this.reg.getObjectBySigla(Tipo.class, ParamsLocal.SIGLA_TIPO_COMPROBANTE_FACTURA);
		
		
		
		String [] columns = {"empresa","comprobanteTipo","establecimiento","puntoExpedicion","activo"};
		Object [] values = {su.getEmpresa(), this.facturaDefault ,su.getSucursal().getEstablecimiento() , su.getPuntoExpedicion(),true};
		
		
		//System.out.println("Factura Default: "+this.facturaDefault.getSigla());
		
		
		Comprobante comp = this.reg.getObjectByColumns(Comprobante.class, columns, values);
		this.facturaSelected.setTimbrado(comp.getTimbrado());
		this.facturaSelected.setTimbradoFecha(comp.getFechaInicio());
		this.facturaSelected.setTimbradoDocNro(comp.getEstablecimiento()+"-"+comp.getPuntoExpedicion()+"-"+String.format("%07d", comp.getSigteNro()));
		this.facturaSelected.setTimbradoSerie(comp.getSerie());
		
		comp.setSigteNro(comp.getSigteNro()+1);
		
		this.save(comp);
		
		if(this.facturaSelected.getCondicion().getSigla().equals(ParamsLocal.SIGLA_TIPO_CONDICIONPAGO_CONTADO)) {
			
			this.facturaSelected.getPagos().get(0).setMonto(this.facturaSelected.getTotalDetalle());
			
		}else if (this.facturaSelected.getCondicion().getSigla().equals(ParamsLocal.SIGLA_TIPO_CONDICIONPAGO_CREDITO)){
			
			this.facturaSelected.setPlazoCredito(this.facturaSelected.getPlazoCredito()+" "+this.cbPlazo);
			
		}
		
		int cont = 1;
		for(FacturaDetalle det : this.facturaSelected.getDetalles()) {
			
			if (det.getItemCodigo() == null) {
				det.setItemCodigo("SC"+String.format("%03d", cont));
				cont++;
			}
			
		}
		
		this.facturaSelected = this.save(this.facturaSelected);
		
		this.enviarFactura(this.facturaSelected, su.getSucursal());
		
		this.limpiarPantalla();
		
		BindUtils.postNotifyChange(null, null, this, "*");

	}
	
	public void enviarFactura(Factura f, Sucursal s) {
		
		MetodoDE mde = new MetodoDE();
		Empresa e = this.reg.findObjectById(Empresa.class, this.getCurrentEmpresa().getEmpresaid());
		DE de = mde.getDe(s.getNombre(), f,e.getFcwsId(), e.getFcwsPass());
		
		ResultRest rr = mde.enviarDE(this.getSistemaPropiedad("fcwsHOST").getValor()+"/factura", de);
		
		if (rr != null) {
			
			Gson gson = new Gson();
			Kude k = gson.fromJson(rr.getMensaje(), Kude.class);
			
			f.setCdc(k.getCdc());
			f.setQr(k.getQr());
			
			this.save(f);
			
			this.verKude(this.facturaSelected.getFacturaid());
		}else {
			this.save(f);
			this.mensajeError("Ocurrio un error al enviar la factura, se reintentara en unos minutos");
		}
	}
	
	
	
	@NotifyChange("facturaciones")
	public void cargarDatos() {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		this.facturaciones = new ArrayList<> (this.reg.sqlNativo(
				this.um.getSql("factura/listaFactura.sql")
				.replace("?1", this.getCurrentEmpresa().getEmpresaid()+"")
				.replace("?2", this.getCurrentSucursal().getSucursalid()+"")
				.replace("?3", sdf.format(desde))
				.replace("?4", sdf.format(hasta))
				.replace("--1", "")
				.replace("--2", "")));
		
		
	}
	
	@Command
	public void facturacionesModal() {
		
		this.cargarDatos();

		modal = (Window) Executions.createComponents("/sistema/zul/operacion/facturacionesModal.zul", this.mainComponent, null);
		Selectors.wireComponents(modal, this, false);
		modal.doModal();
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
		params.put("tipode", "factura");

		this.openInNewTabPost("sistema/zul/reporte/kudeViewer.zul", params);
		
	}
	
	@Command
	public void consultarDe(@BindingParam("id") long id) {
		
		Factura f = this.reg.findObjectById(Factura.class, id);
		
		if (f.getCdc() != null && f.getXml() == null) {
			
			UtilLocalMetodos ulm = new UtilLocalMetodos();
			
			
				ResponseComprobante rc = ulm.consultarDE(this.getSistemaPropiedad("fcwsHOST").getValor()+"/consultar/comprobantexml/"+f.getCdc(), new HttpOrHttpsConexion());
				
				f.setXml(rc.getXml());
				f.setEstado(rc.getEstado());
				f.setRespuesta(ulm.escapeSql(rc.getRespuesta()));
				this.save(f);
				
			
				//System.out.println("Error al consultar el DE");
				
			
		}

	}
	
	
	


	public Factura getFacturaSelected() {
		return facturaSelected;
	}

	public void setFacturaSelected(Factura facturaSelected) {
		this.facturaSelected = facturaSelected;
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
	
	public List<Tipo> getlMoneda() {
		return lMoneda;
	}

	public void setlMoneda(List<Tipo> lMoneda) {
		this.lMoneda = lMoneda;
	}

	public List<Integer> getlIva() {
		return lIva;
	}

	public void setlIva(List<Integer> lIva) {
		this.lIva = lIva;
	}

	public double getIva10() {
		return iva10;
	}

	public void setIva10(double iva10) {
		this.iva10 = iva10;
	}

	public double getIva5() {
		return iva5;
	}

	public void setIva5(double iva5) {
		this.iva5 = iva5;
	}

	public double getIva0() {
		return iva0;
	}

	public void setIva0(double iva0) {
		this.iva0 = iva0;
	}

	public List<Tipo> getlCondicionPago() {
		return lCondicionPago;
	}

	public void setlCondicionPago(List<Tipo> lCondicionPago) {
		this.lCondicionPago = lCondicionPago;
	}

	public List<Object[]> getFacturaciones() {
		return facturaciones;
	}

	public void setFacturaciones(List<Object[]> facturaciones) {
		this.facturaciones = facturaciones;
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

	public List<Tipo> getlDocumentoTipo() {
		return lDocumentoTipo;
	}

	public void setlDocumentoTipo(List<Tipo> lDocumentoTipo) {
		this.lDocumentoTipo = lDocumentoTipo;
	}

	public String getCbPlazo() {
		return cbPlazo;
	}

	public void setCbPlazo(String cbPlazo) {
		this.cbPlazo = cbPlazo;
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

	public boolean isDncpOpen() {
		return dncpOpen;
	}

	public void setDncpOpen(boolean dncpOpen) {
		this.dncpOpen = dncpOpen;
	}

	
	
}
