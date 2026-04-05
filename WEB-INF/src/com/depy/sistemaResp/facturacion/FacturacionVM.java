package com.depy.sistemaResp.facturacion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;

import com.depy.modelo.Empresa;
import com.depy.modelo.Factura;
import com.depy.modelo.FacturaDetalle;
import com.doxacore.util.Register;
import com.doxacore.util.UtilMetodos;

public class FacturacionVM{
	
	private UtilMetodos um;
	private Register reg;
	
	private Factura facturaSelected;

	private List<Object[]> facturaciones;
	private List<Object[]> facturacionesOri;
	private Date desde;
	private Date hasta;
	
	private int rechazados;
	private int aprobados;
	
	
	
	private Boolean[] pantalla = {true, false};
	
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
	
	@Command
	@NotifyChange({"pantalla", "facturaSelected"})
	public void cambiarPantalla(@BindingParam("pantalla") int pantalla) {
		
		Arrays.fill(this.pantalla, false);
		
		this.pantalla[pantalla] = true;
		
		if (pantalla == 1) {
			
			this.facturaSelected = new Factura();
			this.agregarDetalle();
			
		}
		
	}
	
	//======Seccion nueva Factura =====
	
	@Command
	public void onChangeCondicion(@BindingParam("id") String id) {
		
			String condStr = id.replace("btn", "").toLowerCase();
			
			Clients.evalJavaScript(
					"document.getElementById('btnContado').classList.toggle('active', '"+condStr+"'=== 'contado');\n"
					+ "document.getElementById('btnCredito').classList.toggle('active', '"+condStr+"'=== 'credito');\n"
					+ "document.getElementById('creditoFields').classList.toggle('show', '"+condStr+"' === 'credito');");
		
	}
	
	@Command
	public void onChangeMoneda(@BindingParam("id") String id) {
		
		String moneda = id.replace("btn", "").toUpperCase();
		
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
	@NotifyChange({"iva5","iva10","exento","facturaSelected"})
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
	@NotifyChange({"iva5","iva10","exento","facturaSelected"})
	public void calcularTotales() {
		
		double totalDetalle = 0;
		
		for (FacturaDetalle d : this.facturaSelected.getDetalles()) {
			
			double totalLine = d.getCantidad()*d.getPrecioUnitario();
			
			 switch ((int) d.getTasaIva()) {
	         case 10 -> iva10 += totalLine / 11;
	         case 5  -> iva5  += totalLine / 21;
	         case 0  -> iva0  += totalLine;
		 }
		 
		 totalDetalle += totalLine;
			
		}
		
		this.facturaSelected.setTotalDetalle(totalDetalle);
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
	
	
	
}
