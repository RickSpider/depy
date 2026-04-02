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
import com.depy.modelo.Empresa;
import com.depy.modelo.Factura;
import com.depy.modelo.NotaCredito;
import com.depy.modelo.NotaCreditoDetalle;
import com.depy.modelo.NotaCreditoDoc;
import com.depy.modelo.Sucursal;
import com.depy.modelo.SucursalUsuario;
import com.depy.searchModel.FacturaSM;
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
import com.doxacore.modelo.Tipotipo;
import com.google.gson.Gson;

import org.zkoss.bind.annotation.Command;

public class ENotaCreditoVM extends TemplateViewModelLocal {
	
	private NotaCredito notacreditoSelected;
	
	private List<Object[]> notascreditos;
	private Date desde;
	private Date hasta;
	
	private FacturaSM facturaSMSelected;
	
	private String sucursalStr;
	
	@Init(superclass = true)
	public void initNotaCreditocionVM() {
		
		this.desde = this.um.modificarHorasMinutosSegundos(new Date(), 0, 0, 0, 0);
		this.hasta = this.um.modificarHorasMinutosSegundos(this.desde, 23, 59, 59, 99);
		
		this.cargarFacturaSM();
		this.cargarDatosCb();
		limpiarPantalla();
		
		this.sucursalStr = this.getCurrentSucursalUsuario().getSucursal().getNombre();
		
		//System.out.println("Iniciando NotaCreditocion...");

	}

	@AfterCompose(superclass = true)
	public void afterComposeNotaCreditocionVM() {

	}

	@Override
	protected void inicializarOperaciones() {

	}
	
	@Command
	@NotifyChange("*")
	public void limpiarPantalla() {
		
		this.iva0 = 0;
		this.iva10 = 0;
		this.iva5 = 0;
	
		this.facturaSMSelected = null;
		
		this.notacreditoSelected = new NotaCredito();
		this.notacreditoSelected.setSucursal(getCurrentSucursal());
		
		Tipo t = this.reg.getObjectBySigla(Tipo.class, ParamsLocal.SIGLA_TIPO_MOTIVONCD_DEVOLUCIONAJUSTEPRECIO);
		this.notacreditoSelected.setMotivo(t);
	
	}
	
	private Window modal;	

	
	@Command
	public void agregarDetalle() {
		
		this.notacreditoSelected.getDetalles().add(new NotaCreditoDetalle(this.notacreditoSelected, 10, 1L,100, this.getCurrentEmpresa()));
		BindUtils.postNotifyChange(null, null, this.notacreditoSelected, "detalles");
	}
	
	@Command
	@NotifyChange({"totalDetalle", "iva10","iva5","iva0"})
	public void onChangeIva(@BindingParam("detalle") NotaCreditoDetalle det) {
		
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
		
		for (NotaCreditoDetalle x : this.notacreditoSelected.getDetalles()) {
			
			double totalLinea = x.getCantidad() *
                    (x.getPrecioUnitario() != null ? x.getPrecioUnitario() : 0);
			
			totalAux += totalLinea;
			
			
			 switch ((int) x.getTasaIva()) {
	            case 10 -> iva10 += totalLinea / 11;
	            case 5  -> iva5  += totalLinea / 21;
	            case 0  -> iva0  += totalLinea;
	        }

		}
		
		this.notacreditoSelected.setTotalDetalle(totalAux);

	}
	
	@NotifyChange({"totalDetalle", "iva10","iva5","iva0"})
	public void borrarDetalle(NotaCreditoDetalle detalle) {
		
		this.notacreditoSelected.getDetalles().remove(detalle);
		
		BindUtils.postNotifyChange(null, null, this.notacreditoSelected, "detalles");
		
		this.calcularTotales();
		
	}
	
	
	private double iva10, iva5, iva0 = 0;
		
	public double getTotalDetalle() {

		return this.notacreditoSelected.getTotalDetalle();
		
	}
	
	public boolean verificarCampos() {
	
		
		for (NotaCreditoDetalle x : this.notacreditoSelected.getDetalles()) {
			
			if (x.getItemCodigo() == null || x.getItemCodigo().isBlank()) {
				this.mensajeInfo("Tienes items sin codigo.");
				return false;
				
			}
			
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
	public void generarNotaCredito() {
		
		if (!verificarCampos()) {
			return;
		}
		
		EventListener event = new EventListener() {

			@Override
			public void onEvent(Event evt) throws Exception {

				if (evt.getName().equals(Messagebox.ON_YES)) {

					procesarNotaCredito();

				}

			}

		};
		
		this.mensajeSiNo("Se procedera a generar la notacreditor.\n ¿Continuar?", "NotaCredito", event);
		
	}
	
	public void procesarNotaCredito() {
		
		this.notacreditoSelected.setFecha(LocalDateTime.now());
		
				
		//Cliente c = this.reg.findObjectById(Cliente.class, this.clienteSMSelected.getId());
		
		Factura f = this.reg.findObjectById(Factura.class, this.facturaSMSelected.getFacturaid() );
		
		if (f.getTotalDetalle() < this.notacreditoSelected.getTotalDetalle()) {
			
			this.mensajeInfo("El monto de la Nota de Credito no debe superar a la Factura");
			
			return;
			
		}
		
		Cliente c = f.getCliente();
		
		this.notacreditoSelected.setCliente(c);
		this.notacreditoSelected.setDocumentoNro(c.getDocumentoNro());
		this.notacreditoSelected.setRazonSocial(c.getRazonsocial());
		this.notacreditoSelected.setDocumentoTipo(c.getDocumentoTipo());
		
		this.notacreditoSelected.setEmail(c.getEmail());
				
		if (c.getLocalidad() != null && c.getDireccion() != null) {
			this.notacreditoSelected.setLocalidad(c.getLocalidad());
			this.notacreditoSelected.setDireccion(c.getDireccion());
		}
		
		this.notacreditoSelected.getDocumentosAsociados().add(new NotaCreditoDoc(this.notacreditoSelected, f, this.getCurrentEmpresa()));
		this.notacreditoSelected.setMoneda(f.getMoneda());
		this.notacreditoSelected.setMonedaCambio(f.getMonedaCambio());
		
		SucursalUsuario su =  this.getCurrentSucursalUsuario();
			
		Tipo t = this.reg.getObjectBySigla(Tipo.class, ParamsLocal.SIGLA_TIPO_COMPROBANTE_NOTACREDITO);
		
		String [] columns = {"empresa","comprobanteTipo","establecimiento","puntoExpedicion","activo"};
		Object [] values = {this.getCurrentEmpresa(), t ,su.getSucursal().getEstablecimiento() , su.getPuntoExpedicion(),true};
		
		Comprobante comp = this.reg.getObjectByColumns(Comprobante.class, columns, values);
		this.notacreditoSelected.setTimbrado(comp.getTimbrado());
		this.notacreditoSelected.setTimbradoFecha(comp.getFechaInicio());
		this.notacreditoSelected.setTimbradoDocNro(comp.getEstablecimiento()+"-"+comp.getPuntoExpedicion()+"-"+String.format("%07d", comp.getSigteNro()));
		this.notacreditoSelected.setTimbradoSerie(comp.getSerie());
		
		comp.setSigteNro(comp.getSigteNro()+1);
		
		this.save(comp);
		
		this.notacreditoSelected = this.save(this.notacreditoSelected);
		
		this.enviarNotaCredito(this.notacreditoSelected, su.getSucursal());
		
		this.verKude(this.notacreditoSelected.getNotacreditoid());
		
		this.limpiarPantalla();
		
		BindUtils.postNotifyChange(null, null, this, "*");

	}
	
	public void enviarNotaCredito(NotaCredito nc, Sucursal s) {
		
		MetodoDE mde = new MetodoDE();
		Empresa e = this.reg.findObjectById(Empresa.class, this.getCurrentEmpresa().getEmpresaid());
		DE de = mde.getDe(s.getNombre(), nc,e.getFcwsId(), e.getFcwsPass());
		
		ResultRest rr = mde.enviarDE(this.getSistemaPropiedad("fcwsHOST").getValor()+"/notacredito", de);
		
		if (rr != null) {
			
			Gson gson = new Gson();
			Kude k = gson.fromJson(rr.getMensaje(), Kude.class);
			
			nc.setCdc(k.getCdc());
			nc.setQr(k.getQr());
			
			this.save(nc);
		}
	}
	
	
	
	@NotifyChange("notascreditos")
	public void cargarDatos() {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		this.notascreditos = this.reg.sqlNativo(
				this.um.getSql("notacredito/listaNotaCredito.sql")
				.replace("?1", this.getCurrentEmpresa().getEmpresaid()+"")
				.replace("?2", this.getCurrentSucursal().getSucursalid()+"")
				.replace("?3", sdf.format(desde))
				.replace("?4", sdf.format(hasta))
				.replace("--1", "")
				.replace("--2", ""));
		
	}
	
	/*@NotifyChange("*")
	public void cargarFacturas() {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		this.lFacturas = this.reg.sqlNativo(
				this.um.getSql("factura/listaFactura.sql")
				.replace("?1", this.getCurrentEmpresa().getEmpresaid()+"")
				.replace("?2", this.getCurrentSucursal().getSucursalid()+"")
				.replace("?3", sdf.format(desde))
				.replace("?4", sdf.format(hasta))
				.replace("--1", "")
				.replace("--2", "")
				.replace("--3",""));
		
	}*/
	
	private ListModelArray<FacturaSM> lFacturaSM;
	
	public void cargarFacturaSM(){
		
		this.lFacturaSM = this.crearSearchModel(				
	        this.um.getSql("factura/listaFactura.sql")
	        .replace("?1", this.getCurrentEmpresa().getEmpresaid()+"")
	        .replace("?2", this.getCurrentSucursal().getSucursalid()+"")
	        .replace("--1", "")
			.replace("--3",""),
	        o -> new FacturaSM(
	                ((Number) o[0]).longValue(),
	                (String) o[2],
	                (String) o[5],
	                (String) o[6],
	                (String) o[1],
	                ((Number) o[8]).doubleValue(),
	                (String) o[13],
	                ((Number) o[14]).doubleValue()
	                
	        	)
	        );
		
	}
	
	private List<Tipo> lMotivo;
	private List<Integer> lIva = new ArrayList<>();
	
	public void cargarDatosCb() {
		
		Tipotipo tt = this.reg.getObjectBySigla(Tipotipo.class,ParamsLocal.SIGLA_TIPOTIPO_MOTIVONCD);
		String [] cols = {"tipotipo"};
		Object [] value = {tt}; 
		lMotivo = this.reg.getAllObjectsByColumns(Tipo.class, cols, value);
		
		List<Object[]> ivaAux = this.reg.sqlNativo(this.um.getCoreSql("buscarTiposPorSiglaTipotipo.sql").replace("?1", ParamsLocal.SIGLA_TIPOTIPO_IVA));
		for (Object[] x : ivaAux) {
			
			this.lIva.add(Integer.valueOf(x[1].toString()));
			
		} 


	}
	
	@Command
	public void notascreditosModal() {
		
		this.cargarDatos();

		modal = (Window) Executions.createComponents("/sistema/zul/operacion/notascreditosModal.zul", this.mainComponent, null);
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
		params.put("tipode", "notacredito");

		this.openInNewTabPost("sistema/zul/reporte/kudeViewer.zul", params);
		
	}
	
	@Command
	public void consultarDe(@BindingParam("id") long id) {
		
		NotaCredito f = this.reg.findObjectById(NotaCredito.class, id);
		
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
	
	
/*	@Command
	public void notaCreditoModal() {
		
		//this.cargarFacturas();

		modal = (Window) Executions.createComponents("/sistema/zul/operacion/facturasModal.zul", this.mainComponent, null);
		Selectors.wireComponents(modal, this, false);
		modal.doModal();
	}*/
	
	

	public NotaCredito getNotaCreditoSelected() {
		return notacreditoSelected;
	}

	public void setNotaCreditoSelected(NotaCredito notacreditoSelected) {
		this.notacreditoSelected = notacreditoSelected;
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

	public List<Object[]> getNotaCreditociones() {
		return notascreditos;
	}

	public void setNotaCreditociones(List<Object[]> notascreditos) {
		this.notascreditos = notascreditos;
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

	public NotaCredito getNotacreditoSelected() {
		return notacreditoSelected;
	}

	public void setNotacreditoSelected(NotaCredito notacreditoSelected) {
		this.notacreditoSelected = notacreditoSelected;
	}

	public List<Object[]> getNotascreditos() {
		return notascreditos;
	}

	public void setNotascreditos(List<Object[]> notascreditos) {
		this.notascreditos = notascreditos;
	}

	public FacturaSM getFacturaSMSelected() {
		return facturaSMSelected;
	}

	public void setFacturaSMSelected(FacturaSM facturaSMSelected) {
		this.facturaSMSelected = facturaSMSelected;
	}

	public ListModelArray<FacturaSM> getlFacturaSM() {
		return lFacturaSM;
	}

	public void setlFacturaSM(ListModelArray<FacturaSM> lFacturaSM) {
		this.lFacturaSM = lFacturaSM;
	}

	public List<Tipo> getlMotivo() {
		return lMotivo;
	}

	public void setlMotivo(List<Tipo> lMotivo) {
		this.lMotivo = lMotivo;
	}

	public List<Integer> getlIva() {
		return lIva;
	}

	public void setlIva(List<Integer> lIva) {
		this.lIva = lIva;
	}

	

}
