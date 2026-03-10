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
import com.depy.util.ParamsLocal;
import com.depy.utilde.conexion.HttpOrHttpsConexion;
import com.depy.utilde.conexion.ResultRest;
import com.depy.utilde.modelo.CondicionOperacion;
import com.depy.utilde.modelo.Contribuyente;
import com.depy.utilde.modelo.DE;
import com.depy.utilde.modelo.DEDetalle;
import com.depy.utilde.modelo.DocAsociado;
import com.depy.utilde.modelo.NotaCreditoDebito;
import com.depy.utilde.modelo.Receptor;
import com.depy.utilde.modelo.Timbrado;
import com.depy.utilde.modelo.TipoPago;
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
		if (!doc.getDocumentoTipo().getSigla().equals(ParamsLocal.SIGLA_TIPO_DOCUMENTO_RUC)) {
			
		}else {
			
			int separador = doc.getDocumentoNro().indexOf('-');
			r.setDocNro(doc.getDocumentoNro().substring(0, separador));
			r.setDv(doc.getDocumentoNro().substring(separador + 1));
			
			
		}
		
		r.setRazonSocial(doc.getRazonSocial());
		
		if (doc.getLocalidad() != null && doc.getDireccion() != null
				&& doc.getDireccion() != null && !doc.getDireccion().isBlank()) {
			
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

		} 
		/*else if (doc instanceof Remision r) {
		    System.out.println(r.getVehiculo());
		    r.metodoRemisionPropio();
		} */
		
		
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
			det.setPrecioUnitario(x.getPrecioUnitario());
			det.setAfectacionTributaria(x.getAfectacionTributaria());
			det.setProporcionIVA(x.getProporcionIva());
			det.setTasaIVA(x.getTasaIva());
			
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
