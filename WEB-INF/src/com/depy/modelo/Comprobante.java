package com.depy.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.doxacore.modelo.Tipo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "comprobantes",
	indexes = {
        @Index(name = "idx_comprobante_empresa", columnList = "empresaid")
        })
public class Comprobante extends ModeloERP  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 538939804340864947L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long comprobanteid;
	
	@ManyToOne
    @JoinColumn(name = "comprobantetipoid")
	private Tipo comprobanteTipo;
	private String timbrado;
	private String Serie;
	private LocalDate fechaInicio = LocalDate.now();;
	private String establecimiento;
	private String puntoExpedicion;
	private long iniNro = 1;
	private long sigteNro = 1;
	private Boolean activo;
	
	private String logoPath;
	
	@Override
	public Object[] getArrayObjectDatos() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getStringDatos() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Long getComprobanteid() {
		return comprobanteid;
	}
	public void setComprobanteid(Long comprobanteid) {
		this.comprobanteid = comprobanteid;
	}

	public Tipo getComprobanteTipo() {
		return comprobanteTipo;
	}
	public void setComprobanteTipo(Tipo comprobanteTipo) {
		this.comprobanteTipo = comprobanteTipo;
	}
	
	public String getTimbrado() {
		return timbrado;
	}
	public void setTimbrado(String timbrado) {
		this.timbrado = timbrado;
	}
	public String getSerie() {
		return Serie;
	}
	public void setSerie(String serie) {
		Serie = serie;
	}

	public String getEstablecimiento() {
		return establecimiento;
	}
	public void setEstablecimiento(String establecimiento) {
		this.establecimiento = establecimiento;
	}
	public String getPuntoExpedicion() {
		return puntoExpedicion;
	}
	public void setPuntoExpedicion(String puntoExpedicion) {
		this.puntoExpedicion = puntoExpedicion;
	}
	public long getIniNro() {
		return iniNro;
	}
	public void setIniNro(long iniNro) {
		this.iniNro = iniNro;
	}
	public long getSigteNro() {
		return sigteNro;
	}
	public void setSigteNro(long sigteNro) {
		this.sigteNro = sigteNro;
	}
	public Boolean getActivo() {
		return activo;
	}
	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
	
	public LocalDate getFechaInicio() {
		return fechaInicio;
	}
	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}
	
	public String getLogoPath() {
		return logoPath;
	}
	public void setLogoPath(String logoPath) {
		this.logoPath = logoPath;
	}
	public Date getFechaInicioDate() {
		return fechaInicio != null
		        ? Date.from(fechaInicio.atStartOfDay(ZoneId.systemDefault()).toInstant())
		        : null;
	}

	public void setFechaInicioDate(Date d) {
		this.fechaInicio = d != null
		        ? d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
		        : null;
	}
	
	
	
	
	
	
}
