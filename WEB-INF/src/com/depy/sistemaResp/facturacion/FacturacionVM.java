package com.depy.sistemaResp.facturacion;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.doxacore.util.Register;
import com.doxacore.util.UtilMetodos;

public class FacturacionVM{
	
	private UtilMetodos um;
	private Register reg;

	private List<Object[]> facturaciones;
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
	
	@NotifyChange("facturaciones")
	public void cargarDatos() {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		this.facturaciones = this.reg.sqlNativo(
				this.um.getSql("factura/listaFactura.sql")
				.replace("?1", 1+"")
				//.replace("?2", 1+"")
				//.replace("?3", sdf.format(desde))
				//.replace("?4", sdf.format(hasta))
				//.replace("--1", "")
				//.replace("--2", "")
				);

		this.aprobados = 0;
		this.rechazados = 0;
		
		for (Object[] x :facturaciones) {
			
			switch (x[7].toString()){
			
				case("Aprobado"):
					aprobados++;
					break;
					
				case("Rechazado"):
					rechazados++;
					break;
			
			}
			
		}
		
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
	
}
