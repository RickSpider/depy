package com.depy.sistemaResp.facturacion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;

import com.doxacore.util.Register;
import com.doxacore.util.UtilMetodos;

public class FacturacionVM{
	
	private UtilMetodos um;
	private Register reg;

	private List<Object[]> facturaciones;
	private List<Object[]> facturacionesOri;
	private Date desde;
	private Date hasta;
	
	private int rechazados;
	private int aprobados;
	
	@Init(superclass = true)
	public void initFacturacionVM() {
		
		this.reg = new Register();
		this.um = new UtilMetodos();
		
		this.desde = this.um.modificarHorasMinutosSegundos(new Date(), 0, 0, 0, 0);
		this.hasta = this.um.modificarHorasMinutosSegundos(this.desde, 23, 59, 59, 99);
		
		this.cargarDatos();
	
	}

	@AfterCompose(superclass = true)
	public void afterComposeFacturacionVM() {

	}
	
	@NotifyChange({"facturaciones", "aprobados", "rechazados"})
	public void cargarDatos() {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		List<Object[]> aux = new ArrayList<>(this.reg.sqlNativo(
				this.um.getSql("factura/listaFactura.sql")
				.replace("?1", 1+"")
				//.replace("?2", 1+"")
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
	
	
	
}
