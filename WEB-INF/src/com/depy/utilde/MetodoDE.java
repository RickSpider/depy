package com.depy.utilde;

import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.depy.modelo.Documento;
import com.depy.modelo.DocumentoDetalle;
import com.depy.modelo.Factura;
import com.depy.modelo.NotaCredito;
import com.depy.modelo.NotaCreditoDoc;
import com.depy.modelo.Remision;
import com.depy.util.ParamsLocal;
import com.depy.utilde.conexion.HttpOrHttpsConexion;
import com.depy.utilde.conexion.ResultRest;
import com.depy.utilde.modelo.CondicionOperacion;
import com.depy.utilde.modelo.Contribuyente;
import com.depy.utilde.modelo.DE;
import com.depy.utilde.modelo.DEDetalle;
import com.depy.utilde.modelo.DocAsociado;
import com.depy.utilde.modelo.InfoComprasPublicas;
import com.depy.utilde.modelo.MercaderiaMov;
import com.depy.utilde.modelo.NotaCreditoDebito;
import com.depy.utilde.modelo.Receptor;
import com.depy.utilde.modelo.Timbrado;
import com.depy.utilde.modelo.TipoPago;
import com.depy.utilde.modelo.Transporte;
import com.depy.utilde.modelo.Transportista;
import com.depy.utilde.modelo.Vehiculo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MetodoDE{
	
	public DE getDe(String sucursal, Documento doc, Long contribuyenteid, String pass) {
		
		DE de = new DE();
		
		Contribuyente c = new Contribuyente();
		c.setContribuyenteid(contribuyenteid);
		c.setPass(pass);
		de.setContribuyente(c);
		
		de.setSucursal(sucursal);
		
		Timbrado t = new Timbrado();
		t.setTimbrado(doc.getTimbrado()+"");
		int primerGuion = doc.getTimbradoDocNro().indexOf('-');
	    int segundoGuion = doc.getTimbradoDocNro().indexOf('-', primerGuion + 1);
		t.setEstablecimiento(doc.getTimbradoDocNro().substring(0, primerGuion));
		t.setPuntoExpedicion(doc.getTimbradoDocNro().substring(primerGuion + 1, segundoGuion));
		t.setDocumentoNro(doc.getTimbradoDocNro().substring(segundoGuion + 1));
		t.setFecIni(Date.from(doc.getTimbradoFecha().atTime(5,0).atZone(ZoneId.systemDefault()).toInstant()));
		t.setSerieNum(t.getSerieNum());
		
		de.setTimbrado(t);
		
		de.setFecha(Date.from(doc.getFecha().atZone(ZoneId.systemDefault()).toInstant()));
		
		Receptor r = new Receptor();
		if (doc.getDocumentoTipo().getSigla().equals(ParamsLocal.SIGLA_TIPO_DOCUMENTO_RUC)) {
			
			int separador = doc.getDocumentoNro().indexOf('-');
			r.setDocNro(doc.getDocumentoNro().substring(0, separador).trim());
			r.setDv(doc.getDocumentoNro().substring(separador + 1).trim());
			
		}else {
			
			r.setTipoDocumento(doc.getDocumentoTipo().getCodeExtra());
			r.setDocNro(doc.getDocumentoNro().trim());
			
		}
		
		r.setRazonSocial(doc.getRazonSocial());
		
		/*System.out.println("Localidad: " + (doc.getLocalidad() != null));
		System.out.println("Direccion null: " + (doc.getDireccion() != null));
		System.out.println("Direccion blank: " + 
		    (doc.getDireccion() != null && !doc.getDireccion().isBlank()));
		System.out.println("CasaNro: " + (doc.getCasaNro() != null));*/
		
		if (doc.getLocalidad() != null && doc.getDireccion() != null
				&& !doc.getDireccion().isBlank() && doc.getCasaNro() != null) {
			
			r.setDireccion(doc.getDireccion());
			r.setDepartamento(doc.getLocalidad().getDistrito().getDepartamento().getDepartamentoid());
			r.setDistrito(doc.getLocalidad().getDistrito().getDistritoid());
			r.setCiudad(doc.getLocalidad().getLocalidadid());
			r.setCasaNro(doc.getCasaNro() != null ? doc.getCasaNro() : 0);
			
		}
		
		r.setEmail(doc.getEmail());
		
		de.setReceptor(r);
		
		if (doc instanceof Factura f) {
			
			if(!f.getMoneda().getTipo().equals("PYG")) {
				
				de.setOperacionMoneda(f.getMoneda().getTipo());
				de.setOperacionMonedaCambio(f.getMonedaCambio());
				
			}
		    
			CondicionOperacion co = new CondicionOperacion();
			co.setCondicion(f.getCondicion().getCodeExtra());
			
			if (co.getCondicion().longValue() == 1l) {
				//contado
				
				TipoPago tp = new TipoPago();
				tp.setTipoPagoCodigo(f.getPagos().get(0).getPagoTipo().getCodeExtra());
				tp.setMonto(f.getPagos().get(0).getMonto());
				
				if (de.getOperacionMoneda() != null) {
					
					tp.setMoneda(de.getOperacionMoneda());
					tp.setMonedaCambio(de.getOperacionMonedaCambio());
					
				}
				co.setTiposPagos(new ArrayList<>());
				co.getTiposPagos().add(tp);
				
			}else {
				//credito Plazo
				co.setOperacionTipo(1L);
				co.setPlazoCredito(f.getPlazoCredito());
				
			}
			
			de.setCondicionOperacion(co);
			
			if (f.getCpModalidad() != null &&
					!f.getCpModalidad().isBlank() 
					&& f.getCpEntidad() != null 
					&& f.getCpAno() != null
					&& f.getCpFechaEmision() != null
					&& f.getCpSecuencia() != null
					) {
				
				de.setInfComprasPublicas(new InfoComprasPublicas());
				de.getInfComprasPublicas().setModalidad(f.getCpModalidad());
				de.getInfComprasPublicas().setEntidad(f.getCpEntidad());
				de.getInfComprasPublicas().setAno(f.getCpAno());
				de.getInfComprasPublicas().setSecuencia(f.getCpSecuencia());;
				de.getInfComprasPublicas().setFechaEmision(Date.from(f.getCpFechaEmision().atStartOfDay(ZoneId.systemDefault()).toInstant()));
				
			}
			
		   
		} else if (doc instanceof NotaCredito n) {
			
			if(!n.getMoneda().getTipo().equals("PYG")) {
				
				de.setOperacionMoneda(n.getMoneda().getTipo());
				de.setOperacionMonedaCambio(n.getMonedaCambio());
				
			}
			
			NotaCreditoDebito ncd = new NotaCreditoDebito();
			
			ncd.setMotivoEmision(n.getMotivo().getCodeExtra());
			
			de.setNotaCreditoDebito(ncd);
			
			
			DocAsociado da = new DocAsociado();
			da.setTipo(1l);
			
			NotaCreditoDoc ncdoc = n.getDocumentosAsociados().get(0);
			
			da.setCdc(ncdoc.getFactura().getCdc());
			de.setDocAsociados(new ArrayList<>());
			de.getDocAsociados().add(da);

		} else if (doc instanceof Remision remi) {
			
			de.setRemision(new com.depy.utilde.modelo.Remision(remi.getMotivoEmision().getCodeExtra(),remi.getResponsableEmision().getCodeExtra()));
			de.getRemision().setKilometrosRecorrido(remi.getKilometrosRecorridos());
			de.getRemision().setFechaEmiFactura(Date.from(remi.getFacturaEmiFecha().atStartOfDay(ZoneId.systemDefault()).toInstant()));
			
			MercaderiaMov salida = new MercaderiaMov(remi.getSalidaDireccion(), remi.getSalidaCasaNro(), remi.getSalidaLocalidad().getDistrito().getDepartamento().getDepartamentoid(), remi.getSalidaLocalidad().getLocalidadid());
			MercaderiaMov entrega = new MercaderiaMov(remi.getEntregaDireccion(), remi.getEntregaCasaNro(), remi.getEntregaLocalidad().getDistrito().getDepartamento().getDepartamentoid(), remi.getEntregaLocalidad().getLocalidadid());
			
			List<MercaderiaMov> lEntrega = new ArrayList<MercaderiaMov>();
			lEntrega.add(entrega);
			
			Vehiculo v = new Vehiculo(remi.getVehiculoMarca(), remi.getVehiculoIdent().getCodeExtra(), remi.getVehiculoNro());
			List<Vehiculo> lVehiculo = new ArrayList<>();
			lVehiculo.add(v);
			
			Transportista tr = new Transportista();
			tr.setNombre(remi.getTransportistaNombre());
			tr.setDomicilio(remi.getTransportistaDireccion());
			
			if (remi.getTransportistaDocNum().contains("-")) {
				
				String[] num = remi.getTransportistaDocNum().split("-");
				
				tr.setDocNro(num[0]);
				tr.setDv(Integer.parseInt(num[1]));
				
			}else {
				
				tr.setTipoDoc(remi.getTransportistaDocTipo().getCodeExtra());
				tr.setDocNro(remi.getTransportistaDocNum());
				
			}

			tr.setChoferNombre(remi.getChoferNombre());
			tr.setChoferDireccion(remi.getChoferDireccion());
			tr.setChoferDocNum(remi.getChoferDocNum());
			
			
			Transporte transp = new Transporte(remi.getTransTipo().getCodeExtra(), remi.getTransModalidadtipo().getCodeExtra(), remi.getTransResponsableFlete().getCodeExtra(),
					Date.from(remi.getSalidaFecha().atStartOfDay(ZoneId.systemDefault()).toInstant()), 
					Date.from(remi.getSalidaFecha().atStartOfDay(ZoneId.systemDefault()).toInstant()), 
					salida, lEntrega,lVehiculo , tr);
			
			de.setTransporte(transp);
			de.setInfoFisco(remi.getInfoFisco());
			
			
		    
		} 
		
		
		de.setDetalles(this.procesarDetalle(doc.getDetalles()));
		
		return de;
		
	}
	
	
	
	private ArrayList<DEDetalle> procesarDetalle(List <? extends DocumentoDetalle> detalles){
		
		ArrayList<DEDetalle> lDet = new ArrayList<DEDetalle>();
		
		for (DocumentoDetalle x : detalles) {
			
			DEDetalle det = new DEDetalle();
			
			det.setItemCodigo(x.getItemCodigo());
			det.setItemDescripcion(x.getItemDescripcion());
			det.setCantidad(x.getCantidad());
			det.setPrecioUnitario(x.getPrecioUnitario() != null ? x.getPrecioUnitario() : null);
			det.setAfectacionTributaria(x.getAfectacionTributaria()!= null ? x.getAfectacionTributaria() : null);
			det.setProporcionIVA(x.getProporcionIva()!= null ? x.getProporcionIva() : null);
			det.setTasaIVA(x.getTasaIva()!= null ? x.getTasaIva() : null);
			
			if (x.getDncpE() != null && !x.getDncpE().isBlank() 
					&& x.getDncpG() != null && !x.getDncpG().isBlank() ) {
				
				det.setDncpG(x.getDncpG());
				det.setDncpE(x.getDncpE());
				
				
			}
			
			if (x.getUnidadMedida() != null) {
			    det.setItemUndMedida(x.getUnidadMedida().getUnidadmedidaid());
			}
			
			lDet.add(det);
			
		}
		
		return lDet;
	}
	
	public ResultRest enviarDE(String URL, DE de) {
		
		Gson gson = new GsonBuilder()
	                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss-03:00")
	                .create();
		
		String json = gson.toJson(de);
		
		System.out.println(json);
		
		return this.enviarJson(URL, json);
		
		
	}

	public ResultRest enviarJson(String URL, String json) {
		
		HttpOrHttpsConexion con = new HttpOrHttpsConexion();
	
		try {
			ResultRest rr = con.consumirREST(URL, "POST",json );
			return rr;
		} catch (IOException e) {
			
			e.printStackTrace();
			System.out.println("ERROR AL ENVIAR JSON...");
			return null;
		}	
		
	}
	
}
