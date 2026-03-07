package com.depy.utilde.conexion;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
public class HttpOrHttpsConexion {

	public final static String POST ="POST";
	public final static String GET ="GET";
	public final static String PUT = "PUT";
	public final static String PATCH ="PATCH";
	public final static String DELETE ="DELETE";
	
	
	
	
	public ResultRest consumirREST(String link, String method, String json) throws IOException {
	    return link.startsWith("https://") ? this.consumirRESTHttps(link, method, json)
	                                       : this.consumirRESTHttp(link, method, json);
	}
	
	/**
	 * 
	 * Este metodo solo sirve para consumir con REST JSON
	 * 
	 * @param link url a se consumida
	 * @param method metodo POST, GET, PUT, PATCH, DELETE
	 * @param json el cuerpo en json para enviar
	 * @return
	 * @throws IOException
	 */
	
	public ResultRest consumirRESTHttp(String link, String method, String json) throws IOException {
		
		ResultRest respuesta = new ResultRest();
		
		URL url = new URL(link);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

		urlConnection.setRequestMethod(method);

		
		if (method.compareTo(GET) != 0) {
			
			urlConnection.addRequestProperty("Content-Type", "application/json; charset=UTF-8");
			urlConnection.setDoOutput(true);
			
		}else {
			
			urlConnection.setDoOutput(false);
			
		}
	   
		urlConnection.setConnectTimeout(10000);
		urlConnection.setReadTimeout(10000);

		urlConnection.addRequestProperty("Accept-Charset", "UTF-8");

		urlConnection.connect();

		if (method.compareTo(GET) != 0) {
			
			OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream(), StandardCharsets.UTF_8);
			out.write(json);
			out.close();
			
		}

		respuesta.setCode(urlConnection.getResponseCode());

		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;

		
		if (respuesta.getCode() < 300) {
			
			br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			
		} 
			
		if (respuesta.getCode() >= 400) {
				
			br = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
		
		}

		
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		br.close();

		respuesta.setMensaje(sb.toString());
		
		urlConnection.disconnect();
		
		return respuesta;
		
	}
	
	 public ResultRest consumirRESTHttps(String link, String method, String json) throws IOException {

	        ResultRest respuesta = new ResultRest();

	        try {
	            // ⚙️ Configurar SSL para aceptar cualquier certificado (autofirmado o no)
	            TrustManager[] trustAllCerts = new TrustManager[]{
	                new X509TrustManager() {
	                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                        return new X509Certificate[0];
	                    }

	                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
	                    }

	                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
	                    }
	                }
	            };

	            SSLContext sc = SSLContext.getInstance("TLS");
	            sc.init(null, trustAllCerts, new java.security.SecureRandom());
	            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

	            // 🔒 Verificador de nombre de host (ignora errores de CN)
	            HostnameVerifier allHostsValid = (hostname, session) -> true;
	            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

	        } catch (Exception e) {
	            throw new IOException("Error configurando SSL: " + e.getMessage(), e);
	        }

	        // 🌐 Establecer conexión HTTPS
	        URL url = new URL(link);
	        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
	        urlConnection.setRequestMethod(method);
	        
	        urlConnection.setRequestProperty("Accept", "application/json");
	        urlConnection.setRequestProperty("User-Agent", "Java-Client/1.0");

	        if (!method.equalsIgnoreCase(GET)) {
	            urlConnection.addRequestProperty("Content-Type", "application/json; charset=UTF-8");
	            urlConnection.setDoOutput(true);
	        } else {
	        	
	            urlConnection.setDoOutput(false);
	        }

	        urlConnection.setConnectTimeout(10000);
	        urlConnection.setReadTimeout(10000);
	        //urlConnection.addRequestProperty("Accept-Charset", "UTF-8");
	        urlConnection.connect();

	        // 📨 Si el método tiene cuerpo, enviar JSON
	        if (!method.equalsIgnoreCase(GET) && json != null && !json.isEmpty()) {
	            try (OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream(), StandardCharsets.UTF_8)) {
	                out.write(json);
	            }
	        }

	        // 📥 Leer respuesta
	        respuesta.setCode(urlConnection.getResponseCode());

	        try (BufferedReader br = new BufferedReader(new InputStreamReader(
	                respuesta.getCode() < 300
	                        ? urlConnection.getInputStream()
	                        : urlConnection.getErrorStream(),
	                StandardCharsets.UTF_8))) {

	            StringBuilder sb = new StringBuilder();
	            String line;
	            while ((line = br.readLine()) != null) {
	                sb.append(line);
	            }
	            respuesta.setMensaje(sb.toString());
	        }

	        urlConnection.disconnect();
	        return respuesta;
	    }
	
}
