package com.depy.utilde.modelo;

import java.util.Date;

import com.doxacore.modelo.Tipo;

public class Evento {
	
	private Long eventoid;

	private Contribuyente contribuyente;
	
	private Date fecha;
	private String cdc;
	
	private String timbrado;
    private String establecimiento;
    private String puntoExpedicion;
    private String numeroIni;
    private String numeroFin;
    
    private String estado;
    private String respuesta;
    private String mensaje;
    
    public String getRespuesta() {
		return respuesta;
	}
	public void setRespuesta(String respuesta) {
		this.respuesta = respuesta;
	}
	private String motivo;
    
    private Tipo eventoTipo;
    
	public Contribuyente getContribuyente() {
		return contribuyente;
	}
	public void setContribuyente(Contribuyente contribuyente) {
		this.contribuyente = contribuyente;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public String getCdc() {
		return cdc;
	}
	public void setCdc(String cdc) {
		this.cdc = cdc;
	}
	public String getTimbrado() {
		return timbrado;
	}
	public void setTimbrado(String timbrado) {
		this.timbrado = timbrado;
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
	public String getNumeroIni() {
		return numeroIni;
	}
	public void setNumeroIni(String numeroIni) {
		this.numeroIni = numeroIni;
	}
	public String getNumeroFin() {
		return numeroFin;
	}
	public void setNumeroFin(String numeroFin) {
		this.numeroFin = numeroFin;
	}
	public String getMotivo() {
		return motivo;
	}
	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}
	public Tipo getEventoTipo() {
		return eventoTipo;
	}
	public void setEventoTipo(Tipo eventoTipo) {
		this.eventoTipo = eventoTipo;
	}
	public Long getEventoid() {
		return eventoid;
	}
	public void setEventoid(Long eventoid) {
		this.eventoid = eventoid;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	
	
	
        
}
