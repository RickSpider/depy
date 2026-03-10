package com.depy.util;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import com.depy.utilde.conexion.HttpOrHttpsConexion;
import com.depy.utilde.conexion.ResultRest;
import com.depy.utilde.modelo.Evento;
import com.depy.utilde.response.ResponseComprobante;
import com.doxacore.modelo.SistemaPropiedad;
import com.doxacore.util.Register;
import com.doxacore.util.UtilMetodos;
import com.google.gson.Gson;

public class UtilLocalMetodos extends UtilMetodos {
	
	
	private String generarBloque(int longitud) {

		String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		SecureRandom random = new SecureRandom();

		StringBuilder sb = new StringBuilder(longitud);
		for (int i = 0; i < longitud; i++) {
			sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
		}
		return sb.toString();
	}

	public String generarApiKey() {
		return generarBloque(12) + "-" + generarBloque(4) + "-" + generarBloque(4) + "-" + generarBloque(12);
	}
	
	public void updateDE(Register reg, String sqlPendientes, String sqlUpdate) {

		String urlFinal = reg.getObjectByColumn(SistemaPropiedad.class, "clave", "fcwsHOST").getValor()+"/consultar/comprobantexml/";
		HttpOrHttpsConexion con = new HttpOrHttpsConexion();
		//Gson gson = new Gson();
		//ResultRest rr = null;
		
		List<Object[]> lPendientes = reg.sqlNativo(sqlPendientes);
		List<String> lUpdates = new ArrayList<>();
		
		for (Object[] x : lPendientes) {
			
			System.out.println(urlFinal+x[1].toString());
			
			try {
				
				/*rr = con.consumirREST(urlFinal+x[1].toString(), HttpOrHttpsConexion.GET, null);
				ResponseComprobante rc = gson.fromJson(rr.getMensaje(), ResponseComprobante.class);*/
				
				ResponseComprobante rc = consultarDE(urlFinal+x[1].toString(), con); //nuevo
				
				String sql = sqlUpdate.replace("?1", rc.getCdc())
						.replace("?2", rc.getXml())
						.replace("?3", rc.getEstado())
						.replace("?4", this.escapeSql(rc.getRespuesta()));
				
				//System.out.println("========================= sql =============");
				//System.out.println(sql);
				
				//int af = reg.sqlNativoIUD(sql);
				
				lUpdates.add(sql);
				
				//System.out.println("afectado "+af);
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		if (lUpdates.size() > 0) {
			
			reg.sqlNativoIUDBatch(lUpdates);
			
		}
		
	}
	
	public void procesarDatosFactura(String sqlUpdate) {
		
		//hacer depues para poder meter en uno solo factura remision nota de credito
		
	}
	
	public void updateEvento(Register reg, String sqlPendientes, String sqlUpdate){
		
		String URL = reg.getObjectByColumn(SistemaPropiedad.class, "clave", "fcwsHOST").getValor()+"/consultar/evento/";
		HttpOrHttpsConexion con = new HttpOrHttpsConexion();
		ResultRest rr = null;
		
		List<String> lUpdates = new ArrayList<>();
		
		List<Object[]> lFacturasEventoPendientes = reg.sqlNativo(sqlPendientes);
		
		for (Object[] x : lFacturasEventoPendientes) {
			
			if (x[4].toString().equals("Cancelacion")) {
				//evento/inutilizacion
				
				try {
					rr = con.consumirREST(URL+"cancelacion/"+x[1].toString(), HttpOrHttpsConexion.GET, null);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				
				if(rr != null && rr.getCode() == 200) {
					
					Evento e = new Gson().fromJson(rr.getMensaje(), Evento.class);
					
					lUpdates.add(sqlUpdate.replace("?1", x[0].toString())
							.replace("?2", e.getEstado())
							.replace("?3", e.getMensaje()));
					
				}else {
					
					System.out.println("Error al consultar el Evento de Cancelacion.");
				}
				
				
			}else if (x[4].toString().equals("Inutilizacion")) {
			
				try {
					System.out.println(URL+"inutilizacion/"+x[2].toString());
					rr = con.consumirREST(URL+"inutilizacion/"+x[2].toString(), HttpOrHttpsConexion.GET, null);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	

				if(rr != null && rr.getCode() == 200) {
					
					Evento e = new Gson().fromJson(rr.getMensaje(), Evento.class);
					
					//System.out.println(e.getEventoid());
					
					String update = sqlUpdate.replace("?1", x[0].toString())
							.replace("?2", e.getEstado())
							.replace("?3", e.getMensaje());
					
					//System.out.println(update);
					
					lUpdates.add(update);
					
				}else {
					
					System.out.println("Error al consultar el Evento de Inutilizacion.");
				}
			
			}

		}
		
		if (lUpdates.size() > 0) {
			
			reg.sqlNativoIUDBatch(lUpdates);
			
		}

	}
	
	
	
	public String escapeSql(String s) {
	    if (s == null) return "";
	    return s.replace("'", "''");
	}
	
	public ResponseComprobante consultarDE(String url, HttpOrHttpsConexion con) throws IOException {
		
		ResultRest rr = con.consumirREST(url, HttpOrHttpsConexion.GET, null);
		return new Gson().fromJson(rr.getMensaje(), ResponseComprobante.class);
		
		
	}

}
