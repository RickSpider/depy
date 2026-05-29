package com.depy.sistemaResp.facturacion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zhtml.Filedownload;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zul.ListModelArray;

import com.depy.modelo.Cliente;
import com.depy.modelo.Comprobante;
import com.depy.modelo.Empresa;
import com.depy.modelo.Factura;
import com.depy.modelo.FacturaDetalle;
import com.depy.modelo.FacturaPago;
import com.depy.modelo.NotaCredito;
import com.depy.modelo.Remision;
import com.depy.modelo.Ruc;
import com.depy.searchModel.ClienteSM;
import com.depy.sistemaResp.TemplateViewModelLocalResp;
import com.depy.util.ParamsLocal;
import com.depy.util.UtilLocalMetodos;
import com.depy.utilde.MetodoDE;
import com.depy.utilde.conexion.HttpOrHttpsConexion;
import com.depy.utilde.conexion.ResultRest;
import com.depy.utilde.modelo.DE;
import com.depy.utilde.modelo.Kude;
import com.depy.utilde.response.ResponseComprobante;
import com.doxacore.modelo.Tipo;

import com.doxacore.util.SystemInfo;
import com.google.gson.Gson;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRXmlDataSource;

public class FacturacionRVM extends TemplateViewModelLocalResp{
	
	
	
	private Factura facturaSelected;

	private Cliente clienteSelected;
	
	private List<Object[]> facturaciones;
	private List<Object[]> facturacionesOri;
	
	
	private Date desde;
	private Date hasta;
	
	private int rechazados;
	private int aprobados;
	
	private Tipo facturaDefault;
	private Tipo efectivoDefault;
	
	private String cbPlazo = "dias";
	
	private Boolean[] pantalla = {true, false,false};
	
	@Init(superclass = true)
	public void initFacturacionVM() {
		
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
		
		this.cargarDatos();
		this.cargarDatosTipos();
	
	}

	@AfterCompose(superclass = true)
	public void afterComposeFacturacionVM() {

	}
	
	@NotifyChange({"facturaciones", "aprobados", "rechazados"})
	public void cargarDatos() {
		
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		List<Object[]> aux = new ArrayList<>(this.reg.sqlNativo(
				this.um.getSql("factura/listaFactura.sql")
				.replace("?1", this.su.getEmpresa().getEmpresaid()+"")
				.replace("?2", this.su.getSucursal().getSucursalid()+"")
				//.replace("?3", sdf.format(desde))
				//.replace("?4", sdf.format(hasta))
				//.replace("--1", "")
				//.replace("--2", "")
				));

		this.facturaciones = new ArrayList<>(aux);
		this.facturacionesOri = new ArrayList<>(aux);

		
		this.aprobados = 0;
		this.rechazados = 0;
		
		for (Object[] x :aux) {
			
			switch (x[7].toString()) {
            case "Aprobado" -> aprobados++;
            case "Rechazado" -> rechazados++;
            }
			
		}
			
	}
	
	private List<Integer> lIva;
	private List<Tipo> lDocumentoTipo;
	private Map<String, Tipo> mapTipos;
	
	
	public void cargarDatosTipos() {
		
		List <Tipo> lTipos = this.reg.getAllObjectsByColumnIn(Tipo.class,"tipotipo.sigla", List.of(
			    ParamsLocal.SIGLA_TIPOTIPO_CONDICIONPAGO,
			    ParamsLocal.SIGLA_TIPOTIPO_MONEDA,
			    ParamsLocal.SIGLA_TIPOTIPO_IVA,
			    ParamsLocal.SIGLA_TIPOTIPO_DOCUMENTO,
			    ParamsLocal.SIGLA_TIPOTIPO_COMPROBANTE,
			    ParamsLocal.SIGLA_TIPOTIPO_FORMAPAGO
			) );
		
		
		this.mapTipos = new HashMap<>();
		this.lIva = new ArrayList<>();
		this.lDocumentoTipo = new ArrayList<>();
		for (Tipo t : lTipos) {
			mapTipos.put(t.getSigla(), t);
			
			if (t.getTipotipo().getSigla().equals(ParamsLocal.SIGLA_TIPOTIPO_IVA)) {
				
				this.lIva.add(Integer.valueOf(t.getTipo()));
				
			}
			
			if (t.getSigla().equals(ParamsLocal.SIGLA_TIPO_COMPROBANTE_FACTURA)) {
				this.facturaDefault = t;
			}
			
			if (t.getSigla().equals(ParamsLocal.SIGLA_TIPO_FORMAPAGO_EFECTIVO)) {
				this.efectivoDefault = t;
			}
			
			if (t.getTipotipo().getSigla().equals(ParamsLocal.SIGLA_TIPOTIPO_DOCUMENTO)) {
				
				this.lDocumentoTipo.add(t);
				
			}
			
		}
		
	}
	
	@NotifyChange("facturaciones")
	public void filtrarFacturas(String q, String estado) {
	    // Si no hay palabra ni filtro de estado, restauramos la lista completa
	    if ((q == null || q.isBlank()) && ("Todos".equalsIgnoreCase(estado) || estado == null)) {
	        this.facturaciones = facturacionesOri;
	        return;
	    }

	    String busqueda = (q != null) ? q.toLowerCase() : null;
	    List<Object[]> filtradas = new ArrayList<>(facturacionesOri.size());

	    for (Object[] f : facturacionesOri) {
	        boolean coincideBusqueda = false;

	        // 1️⃣ Evaluar búsqueda por palabra
	        if (busqueda != null) {
	            for (Object campo : f) {
	                if (campo != null && campo.toString().toLowerCase().contains(busqueda.toLowerCase())) {
	                    coincideBusqueda = true;
	                    break; // rompo en cuanto hay coincidencia
	                }
	            }
	        } else {
	            coincideBusqueda = true; // no hay palabra => todos coinciden
	        }

	        // 2️⃣ Evaluar estado
	        boolean coincideEstado = "Todos".equalsIgnoreCase(estado) || estado.equalsIgnoreCase(f[7].toString());

	        // 3️⃣ Agregar solo si cumple ambos criterios
	        if (coincideBusqueda && coincideEstado) {
	            filtradas.add(f);
	        }
	    }

	    // Reemplazo la lista que usa el grid
	    this.facturaciones = filtradas;
	}
	
	private String btnfIdActual = "btnfTodos";
	private String filtro;
	
	@NotifyChange("*")
	public void filtroBtn(@BindingParam("id") String id) {
		
		this.btnfIdActual = id;
		
		String auxEstado = id.replace("btnf", "");
			
		this.filtrarFacturas(this.filtro, auxEstado);

		Clients.evalJavaScript(
				"  document.querySelectorAll('.filter-pill').forEach(b => b.classList.remove('active'));\r\n"
				+ "  document.getElementById('"+btnfIdActual+"').classList.add('active');"
				+ "");
		
		
	}
	
	@Command
	@NotifyChange("facturaciones")
	public void filtrarTexto() {
		
		this.filtrarFacturas(this.filtro, this.btnfIdActual.replace("btnf", ""));
		
	}
	
	@Command
	@NotifyChange({"pantalla", "facturaSelected", "lClienteSM","clienteSelected"})
	public void cambiarPantalla(@BindingParam("pantalla") int pantalla) {
		
		int anterior = 0; 
		
		for (int i  = 0 ; i<this.pantalla.length ; i++) {
			
			if (this.pantalla[i]) {
				anterior = i;
				break;
			}
			
		}
		
		
		
		Arrays.fill(this.pantalla, false);
		
		this.pantalla[pantalla] = true;
		
		if (pantalla == 1 && anterior == 0) {
			
			this.iva0 = 0;
			this.iva10 = 0;
			this.iva5 = 0;

			this.facturaSelected = new Factura();
			this.facturaSelected.setFecha(LocalDateTime.now());
			this.facturaSelected.setMoneda(this.mapTipos.get(ParamsLocal.SIGLA_TIPO_MONEDA_GUARANIES));
			this.facturaSelected.setMonedaCambio(1.0);
			this.facturaSelected.setSucursal(this.su.getSucursal());
			this.facturaSelected.setEmpresa(getCurrentEmpresa());

			FacturaPago fp = new FacturaPago();
			fp.setPagoTipo(this.mapTipos.get(ParamsLocal.SIGLA_TIPO_FORMAPAGO_EFECTIVO));
			fp.setFactura(facturaSelected);
			fp.setEmpresa(getCurrentEmpresa());
			
			this.facturaSelected.getPagos().add(fp);
			
			this.facturaSelected.setCondicion(this.mapTipos.get(ParamsLocal.SIGLA_TIPO_CONDICIONPAGO_CONTADO));			
						
			this.agregarDetalle();
			
			this.onChangeCondicion("btnContado");
			this.onChangeMoneda("btnPyg");
			
			this.clienteSMSelected = null;
			generarClienteSM();
			
		}else if (pantalla == 2) {
			
			this.clienteSelected = new Cliente();
			this.clienteSelected.setEmpresa(getCurrentEmpresa());
			
			for (Tipo t : this.lDocumentoTipo) {
				
				if (t.getSigla().equals(ParamsLocal.SIGLA_TIPO_DOCUMENTO_RUC)) {
					
					this.clienteSelected.setDocumentoTipo(t);
					break;
				}
				
			}
			
		}
		
	}
	
	//======Seccion nueva Factura =====
	
	private ListModelArray<ClienteSM> lClienteSM;
	private ClienteSM clienteSMSelected;
	
	public void generarClienteSM(){
		
		this.lClienteSM = this.crearSearchModel(				
	        this.um.getSql("cliente/buscarCliente.sql").replace("?1", this.getCurrentEmpresa().getEmpresaid()+""),
	        o -> new ClienteSM(
	                ((Number) o[0]).longValue(),
	                (String) o[1],
	                (String) o[2],
	                (String) o[3],
	                (String) o[4]
	        	)
	        );
		
	}
	
	protected <T> ListModelArray<T> crearSearchModel(String sql, java.util.function.Function<Object[], T> mapper) {
	    List<Object[]> resultados = this.reg.sqlNativo(sql);
	    List<T> lista = new ArrayList<>(resultados.size());

	    for (Object[] fila : resultados) {
	        lista.add(mapper.apply(fila));
	    }

	    ListModelArray<T> modelo = new ListModelArray<>(lista);
	    return modelo;
	}
	
	@Command
	public void onChangeCondicion(@BindingParam("id") String id) {
		
			String condStr = id.replace("btn", "").toLowerCase();
			
			if (condStr.equals("contado")) {
				
				this.facturaSelected.setCondicion(this.mapTipos.get(ParamsLocal.SIGLA_TIPO_CONDICIONPAGO_CONTADO));	
				this.facturaSelected.setPlazoCredito(null);
				this.facturaSelected.setPagos(new ArrayList<>());
				FacturaPago fp = new FacturaPago();
				fp.setFactura(this.facturaSelected);
				fp.setPagoTipo(this.mapTipos.get(ParamsLocal.SIGLA_TIPO_FORMAPAGO_EFECTIVO));
				fp.setEmpresa(getCurrentEmpresa());
				this.facturaSelected.getPagos().add(fp);
				
			}else {
				this.facturaSelected.setCondicion(this.mapTipos.get(ParamsLocal.SIGLA_TIPO_CONDICIONPAGO_CREDITO));
				this.cbPlazo ="Dias";
				this.facturaSelected.setPagos(new ArrayList<>());
			}
			
			Clients.evalJavaScript(
					"document.getElementById('btnContado').classList.toggle('active', '"+condStr+"'=== 'contado');\n"
					+ "document.getElementById('btnCredito').classList.toggle('active', '"+condStr+"'=== 'credito');\n"
					+ "document.getElementById('creditoFields').classList.toggle('show', '"+condStr+"' === 'credito');");
		
	}
	
	@Command
	public void onChangeDoc() {
		
		String docNro =this.clienteSelected.getDocumentoNro().trim();
	
		
			
		int idx = docNro.indexOf('-');
			
		if (idx > 0 && this.clienteSelected.getDocumentoTipo().getSigla().equals(ParamsLocal.SIGLA_TIPO_DOCUMENTO_RUC)) {

			String [] columns = {"ruc","dv"};
			Object[] valor = {docNro.substring(0, idx), docNro.substring(idx + 1)};
				   
			Ruc ruc = this.reg.getObjectByColumns(Ruc.class, columns, valor);
				    
			this.clienteSelected.setRazonsocial(ruc != null ? ruc.getRazonSocial(): null);
				    
			this.clienteSelected.setDocumentoTipo(ruc != null ?  this.mapTipos.get(ParamsLocal.SIGLA_TIPO_DOCUMENTO_RUC):null );
				
		}
		
		BindUtils.postNotifyChange(null, null, this.clienteSelected, "gocumentoNro");
		BindUtils.postNotifyChange(null, null, this.clienteSelected, "razonsocial");
		
	}
	
	@Command
	public void onChangeMoneda(@BindingParam("id") String id) {
		
		String moneda = id.replace("btn", "").toUpperCase();
		
		if(moneda.equals("PYG")) {
			this.facturaSelected.setMoneda(this.mapTipos.get(ParamsLocal.SIGLA_TIPO_MONEDA_GUARANIES));
			this.facturaSelected.setMonedaCambio(1.0);
		}else if (moneda.equals("USD")) {
			this.facturaSelected.setMoneda(this.mapTipos.get(ParamsLocal.SIGLA_TIPO_MONEDA_DOLARES));			
		}
		
		Clients.evalJavaScript(
				" document.getElementById('btnPyg').classList.toggle('active', '"+moneda+"' === 'PYG');\n"
				+ " document.getElementById('btnUsd').classList.toggle('active', '"+moneda+"' === 'USD');"
				+ " document.getElementById('tipoCambioGroup').classList.toggle('show', '"+moneda+"' === 'USD');");
		
	}
	
	
	@Command
	public void agregarDetalle() {
		
		this.facturaSelected.getDetalles().add(new FacturaDetalle(this.facturaSelected, 10, 1L,100, this.getCurrentEmpresa()));
		BindUtils.postNotifyChange(null, null, this.facturaSelected, "detalles");
	}
	
	@Command
	@NotifyChange({"iva5","iva10","iva0","facturaSelected"})
	public void borrarDetalle(@BindingParam("data") FacturaDetalle detalle) {
		
		this.facturaSelected.getDetalles().remove(detalle);

		this.calcularTotales();
		
	}
	
	protected Empresa getCurrentEmpresa() {
		
		Empresa out = new Empresa();
		out.setEmpresaid((Long) Sessions.getCurrent().getAttribute("empresaid"));
		
		return out;
		//return this.getCurrentEmpresaUsuario().getEmpresa();
	}
	
	private double iva5 = 0;
	private double iva10 = 0;
	private double iva0 = 0;
	
	@Command
	@NotifyChange({"iva5","iva10","iva0","facturaSelected.totalDetalle"})
	public void calcularTotales() {
		
		double totalDetalle = 0;
		
		this.iva0 =0;
		this.iva5 =0;
		this.iva10 =0;
		
		for (FacturaDetalle d : this.facturaSelected.getDetalles()) {
			
			double totalLine = d.getCantidad()*(d.getPrecioUnitario() != null ? d.getPrecioUnitario() : 0);
			
			 switch ((int) d.getTasaIva()) {
	         case 10 -> iva10 += totalLine / 11;
	         case 5  -> iva5  += totalLine / 21;
	         case 0  -> iva0  += totalLine;
		 }
		 
		 totalDetalle += totalLine;
			
		}
		
		this.facturaSelected.setTotalDetalle(totalDetalle);
		
		BindUtils.postNotifyChange(null, null, this.facturaSelected, "totalDetalle");
	}
	
	@Command
	@NotifyChange({"iva5","iva10","iva0"})
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
	
	public boolean verificarCampos() {
		
		if (this.clienteSMSelected == null) {
			
			Notification.show("Debes tener un cliente seleccionado.");
			return false;
		}
		
		if (!this.facturaSelected.getMoneda().getSigla().equals(ParamsLocal.SIGLA_TIPO_MONEDA_GUARANIES) && this.facturaSelected.getMonedaCambio() == 1 ) {
			Notification.show("Cuando la moneda no es Guaranies(PYG) el cambio debe ser mayor a 1 (uno).");
			return false;
		}
		
		if (this.facturaSelected.getCondicion().getSigla().equals(ParamsLocal.SIGLA_TIPO_CONDICIONPAGO_CREDITO)) {
			
			if (this.facturaSelected.getPlazoCredito() == null || this.facturaSelected.getPlazoCredito().isBlank()) {
				Notification.show("Debes cargar el plazo ya sea en dias o meses.");
				//this.mensajeInfo("Debes cargar el plazo ya sea en dias o meses.");
				return false;
			}
						
		}
		
		for (FacturaDetalle x : this.facturaSelected.getDetalles()) {
			
			/*if (x.getItemCodigo() == null || x.getItemCodigo().isBlank()) {
				this.mensajeInfo("Tienes items sin codigo.");
				return false;
				
			}*/
			
			if (x.getItemDescripcion() == null ||  x.getItemDescripcion().isBlank()) {
				Notification.show("Tienes items sin descripcion.");
				//this.mensajeInfo("Tienes items sin descripcion.");
				return false;
			}
			
			if (x.getPrecioUnitario() == null ||  x.getPrecioUnitario()<=0) {
				Notification.show("El precio debe ser mayor o igual a 1 (uno).");
				//this.mensajeInfo("El precio debe ser mayor o igual a 1 (uno).");
				return false;
			}

		}
		
		return true;
	}
	
	@Command
	public void procesarFactura() throws Exception {
		
		if (!this.verificarCampos()) {
			return;
		}
		
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
				
		String [] columns = {"empresa","comprobanteTipo","establecimiento","puntoExpedicion","activo"};
		Object [] values = {su.getEmpresa(), this.facturaDefault ,su.getSucursal().getEstablecimiento() , su.getPuntoExpedicion(),true};		
		
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
		
		this.enviarFactura(this.facturaSelected);
		
		this.consultarDe(this.facturaSelected.getFacturaid());
		
		this.createKude(this.facturaSelected.getFacturaid(), "factura");
		
		this.cambiarPantalla(0);
		
		cargarDatos();
		
		BindUtils.postNotifyChange(null, null, this, "*");

	}
	
	public void enviarFactura(Factura f) {
		
		MetodoDE mde = new MetodoDE();
		Empresa e = this.reg.findObjectById(Empresa.class, this.getCurrentEmpresa().getEmpresaid());
		DE de = mde.getDe(f.getSucursal().getNombre(), f,e.getFcwsId(), e.getFcwsPass());
		
		ResultRest rr = mde.enviarDE(this.getSistemaPropiedad("fcwsHOST").getValor()+"/factura", de);
		
		if (rr != null) {
			
			Gson gson = new Gson();
			Kude k = gson.fromJson(rr.getMensaje(), Kude.class);
			
			f.setCdc(k.getCdc());
			f.setQr(k.getQr());
			
			this.save(f);
		}
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
	
	//seccion kude
	
	private String prettyPrintXml(String xml) {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			StreamSource source = new StreamSource(new StringReader(xml));
			StringWriter writer = new StringWriter();
			transformer.transform(source, new StreamResult(writer));

			return writer.toString();
		} catch (Exception e) {
			return xml;
		}
	}

	@Command
	public void createKude(@BindingParam("ceid") long ceid, @BindingParam("tipode") String tipode) throws Exception {

		String xml = "";
		String logoPath = "";
		String fileName = "";

		Map<String, Object> parametros = new HashMap<>();
		String jasperPath;
		JRXmlDataSource dataSource = null;

		if (tipode.equals("factura")) {

			Factura factura = reg.findObjectById(Factura.class, ceid);
			if (factura.getSucursal().getLogoPath() != null) {
				logoPath = factura.getSucursal().getLogoPath();
			} else {
				logoPath = factura.getEmpresa().getLogoPath();
			}
			xml = this.extraerRDE(prettyPrintXml(factura.getXml())).trim();

			if (factura.getEventoTipo() != null
					&& factura.getEventoTipo().getSigla().equals(ParamsLocal.SIGLA_TIPO_EVENTO_CANCELACION)
					&& factura.getEventoEstado().equals("Aprobado")) {

				parametros.put("cancelado", true);

			}

			fileName = "Factura_" + factura.getTimbradoDocNro() + "_" + factura.getCdc();

		} else if (tipode.equals("notacredito")) {

			NotaCredito notacredito = reg.findObjectById(NotaCredito.class, ceid);
			if (notacredito.getSucursal().getLogoPath() != null) {
				logoPath = notacredito.getSucursal().getLogoPath();
			} else {
				logoPath = notacredito.getEmpresa().getLogoPath();
			}
			xml = this.extraerRDE(prettyPrintXml(notacredito.getXml())).trim();

			fileName = "NC_" + notacredito.getTimbradoDocNro() + "_" + notacredito.getCdc();

		}
		if (tipode.equals("remision")) {

			Remision remision = reg.findObjectById(Remision.class, ceid);
			if (remision.getSucursal().getLogoPath() != null) {
				logoPath = remision.getSucursal().getLogoPath();
			} else {
				logoPath = remision.getEmpresa().getLogoPath();
			}
			xml = this.extraerRDE(prettyPrintXml(remision.getXml())).trim();

			fileName = "Remision_" + remision.getTimbradoDocNro() + "_" + remision.getCdc();
		}

		int tipoDocumento = getTipoDocumentoFromXML(xml);
		jasperPath = SystemInfo.SISTEMA_PATH_ABSOLUTO + "/reportTemplate/";

		if (tipoDocumento == 1)
			jasperPath = String.valueOf(jasperPath) + "Factura.jasper";
		if (tipoDocumento == 2)
			jasperPath = String.valueOf(jasperPath) + "FacturaImportacion.jasper";
		if (tipoDocumento == 3)
			jasperPath = String.valueOf(jasperPath) + "FacturaExportacion.jasper";
		if (tipoDocumento == 4)
			jasperPath = String.valueOf(jasperPath) + "AutoFactura.jasper";
		if (tipoDocumento == 5)
			jasperPath = String.valueOf(jasperPath) + "NotaCredito.jasper";
		if (tipoDocumento == 6)
			jasperPath = String.valueOf(jasperPath) + "NotaDebito.jasper";
		if (tipoDocumento == 7)
			jasperPath = String.valueOf(jasperPath) + "NotaRemision.jasper";

		if (!(new File(jasperPath)).exists())
			throw new Exception("Archivo " + jasperPath + " no encontrado.!");
		try {
			InputStream inputStream = null;
			if (xml.startsWith("<?xml")) {
				inputStream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
			} else {
				inputStream = new FileInputStream(xml);
			}
			dataSource = new JRXmlDataSource(inputStream, "/rDE/DE/gDtipDE/gCamItem");
			Locale locale = new Locale("es", "PY");
			parametros.put("REPORT_LOCALE", locale);

			try {

				if (!logoPath.isBlank()) {

					parametros.put("LOGO_URL", ImageIO.read(new File(logoPath)));
				}

			} catch (IOException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		JasperPrint jprint = JasperFillManager.fillReport(jasperPath, parametros, dataSource);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JasperExportManager.exportReportToPdfStream(jprint, out);
		Filedownload.save(out.toByteArray(), "application/pdf", fileName + ".pdf");

	}

	public String extraerRDE(String xml) throws Exception {
		// Limpiar espacios
		xml = xml.trim();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
		DocumentBuilder builder = factory.newDocumentBuilder();

		// Parsear XML original
		Document doc = builder.parse(new InputSource(new StringReader(xml)));

		XPath xpath = XPathFactory.newInstance().newXPath();
		Node rdeNode = (Node) xpath.evaluate("//*[local-name()='rDE']", doc, XPathConstants.NODE);

		if (rdeNode == null) {
			throw new Exception("No se encontró rDE");
		}

		// Crear nuevo documento
		Document newDoc = builder.newDocument();
		Node imported = newDoc.importNode(rdeNode, true);
		newDoc.appendChild(imported);

		// Usar Transformer para controlar salida
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(newDoc), new StreamResult(writer));

		return writer.toString();
	}

	public int getTipoDocumentoFromXML(String archivoDEXML) throws Exception {

		InputStream inputStream = new ByteArrayInputStream(archivoDEXML.getBytes(StandardCharsets.UTF_8));
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
		
		Node student = document.getElementsByTagName("rDE").item(0);
		Element dataFileElement = (Element) student;
		Node dataFile = dataFileElement.getElementsByTagName("DE").item(0);
		Element iTipoDEElement = (Element) dataFile;
		Node iTiDE = iTipoDEElement.getElementsByTagName("iTiDE").item(0);
		Integer tipoDocumento = Integer.valueOf(iTiDE.getTextContent());
		
		return tipoDocumento;
	}
	
	//Seccion Cliente
	
	
	public boolean verficarCliente() {
		
		String [] columns = {"empresa","documentoNro"};
	    Object[] valor = {this.getCurrentEmpresa(), this.clienteSelected.getDocumentoNro()};
		Cliente cAux = this.reg.getObjectByColumns(Cliente.class, columns, valor);
		if (cAux != null) {

			Notification.show("Ya existe el contribuyente...");
			return false;
			
		}
		
		if(this.clienteSelected.getDocumentoTipo().getSigla().equals(ParamsLocal.SIGLA_TIPO_DOCUMENTO_RUC)) {
			
			if (!this.clienteSelected.getDocumentoNro().matches("^\\d+-\\d$")) {
				Notification.show("El Ruc no es de la forma 1234567-8.");
				return false;
			}
			
		}
		
		if (this.clienteSelected.getRazonsocial()==null 
				|| this.clienteSelected.getRazonsocial().isBlank()
				|| this.clienteSelected.getRazonsocial().isEmpty()) {
			
			Notification.show("Debes cargar una Razon Social");
			return false;
			
		}

		return true;
	}
	
	@NotifyChange({"pantalla", "facturaSelected", "lClienteSM","clienteSMSelected"})
	@Command
	public void guardarCliente() {
		
		if (!verficarCliente()) {
			return;
		}
		
		this.clienteSelected = this.save(this.clienteSelected);
		this.clienteSMSelected = new ClienteSM(this.clienteSelected);
		this.facturaSelected.setCliente(clienteSelected);
		this.clienteSelected =  null;
		this.generarClienteSM();
		
		this.cambiarPantalla(1);
		
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
	
	public int getRechazados() {
		return rechazados;
	}

	public void setRechazados(int rechazados) {
		this.rechazados = rechazados;
	}

	public int getAprobados() {
		return aprobados;
	}

	public void setAprobados(int aprobados) {
		this.aprobados = aprobados;
	}

	public String getFiltro() {
		return filtro;
	}

	public void setFiltro(String filtro) {
		this.filtro = filtro;
	}

	public Boolean[] getPantalla() {
		return pantalla;
	}

	public void setPantalla(Boolean[] pantalla) {
		this.pantalla = pantalla;
	}

	public Factura getFacturaSelected() {
		return facturaSelected;
	}

	public void setFacturaSelected(Factura facturaSelected) {
		this.facturaSelected = facturaSelected;
	}

	public double getIva5() {
		return iva5;
	}

	public void setIva5(double iva5) {
		this.iva5 = iva5;
	}

	public double getIva10() {
		return iva10;
	}

	public void setIva10(double iva10) {
		this.iva10 = iva10;
	}

	public double getIva0() {
		return iva0;
	}

	public void setIva0(double iva0) {
		this.iva0 = iva0;
	}

	public List<Integer> getlIva() {
		return lIva;
	}

	public Map<String, Tipo> getMapTipos() {
		return mapTipos;
	}

	public Tipo getEfectivoDefault() {
		return efectivoDefault;
	}

	public void setEfectivoDefault(Tipo efectivoDefault) {
		this.efectivoDefault = efectivoDefault;
	}

	public String getCbPlazo() {
		return cbPlazo;
	}

	public void setCbPlazo(String cbPlazo) {
		this.cbPlazo = cbPlazo;
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

	public List<Tipo> getlDocumentoTipo() {
		return lDocumentoTipo;
	}

	public void setlDocumentoTipo(List<Tipo> lDocumentoTipo) {
		this.lDocumentoTipo = lDocumentoTipo;
	}

	public Cliente getClienteSelected() {
		return clienteSelected;
	}

	public void setClienteSelected(Cliente clienteSelected) {
		this.clienteSelected = clienteSelected;
	}
	
	
	
	
	
}
