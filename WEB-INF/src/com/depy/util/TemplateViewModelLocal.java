package com.depy.util;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.ListModelArray;

import com.depy.modelo.Empresa;
import com.depy.modelo.ModeloERP;
import com.depy.modelo.Sucursal;
import com.depy.modelo.SucursalUsuario;
import com.doxacore.TemplateViewModel;
import com.doxacore.report.ReportBigExcel;

public abstract class TemplateViewModelLocal extends TemplateViewModel {
	@Init(superclass = true)
	public void initTemplateViewModelLocal() {
		
	}

	@AfterCompose(superclass = true)
	public void afterComposeTemplateViewModelLocal() {
		
	}
	
	
	
	protected Empresa getCurrentEmpresa() {
		
		Empresa out = new Empresa();
		out.setEmpresaid((Long) Sessions.getCurrent().getAttribute("empresaid"));
		
		return out;
		//return this.getCurrentEmpresaUsuario().getEmpresa();
	}
	
	protected SucursalUsuario getCurrentSucursalUsuario() {
		
		SucursalUsuario su = this.reg.getObjectByColumns(SucursalUsuario.class, new String[]{"empresa","usuario", "actual"}, new Object[]{this.getCurrentEmpresa(),this.getCurrentUser(), true});
		
		return su;
		
	}
	
	protected Sucursal getCurrentSucursal() {
		
		Sucursal out = new Sucursal();
		out.setSucursalid((Long) Sessions.getCurrent().getAttribute("sucursalid"));
		
		return out;
		
		
	}
	
	protected <T extends ModeloERP> T save(T m) {
		
		if (m.getEmpresa() == null) {
			
			m.setEmpresa(getCurrentEmpresa());
			
		}

		return this.reg.saveObject(m, getCurrentUser().getAccount());

	}
	
	protected void exportarExcelGenerico(String nombreArchivo, String tituloPrincipal, String[] columnas,List<Object[]> datos) {


	    List<String[]> titulos = new ArrayList<>();
	    titulos.add(new String[]{tituloPrincipal});
	    titulos.add(new String[]{""}); // Espacio en blanco

	    // --- 2️⃣ Cabeceras dinámicas (opcional, puede venir vacía) ---
	    List<String[]> headersDatos = new ArrayList<>();
	    headersDatos.add(columnas);
	    
	    // --- 3️⃣ Procesar los datos ---
	    List<Object[]> detalles = new ArrayList<>();

	    for (Object[] fila : datos) {
	        Object[] o = new Object[fila.length-1];

	        for (int i = 0; i < fila.length-1; i++) {
	            o[i] = (fila[i+1] != null) ? fila[i+1].toString() : "";
	        }

	        detalles.add(o);
	    }

	    // --- 4️⃣ Exportar usando tu clase existente ---
	    ReportBigExcel re = new ReportBigExcel(nombreArchivo);
	    re.descargar(titulos, headersDatos, detalles);
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
}
