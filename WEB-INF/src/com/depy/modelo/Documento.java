package com.depy.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;

import com.doxacore.modelo.Tipo;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Documento extends ModeloERP{

	private LocalDateTime fecha;
	
	private String timbrado;
	private LocalDate  timbradoFecha;
	private String timbradoDocNro;
	private String timbradoSerie;
	
	@ManyToOne
	@JoinColumn(name = "sucursalid")
	private Sucursal sucursal;
	
	@ManyToOne
	@JoinColumn(name = "clienteid")
	private Cliente cliente;
	
	@ManyToOne
	@JoinColumn(name = "documentoTipoid")
	private Tipo documentoTipo;
	
	private String documentoNro;
	private String razonSocial;
	private String email;
	private String Direccion;
	
	@ManyToOne
	@JoinColumn(name = "localidadid")
	private Localidad localidad;
	private Integer casaNro;
	
	private String cdc;

	@Column(columnDefinition = "text")
	private String xml;

	@Column(columnDefinition = "text")
	private String qr;

	@ColumnDefault("false")
	private boolean enviado = false;
	
	private String estado = "Pendiente";

	@Column(columnDefinition = "text")
	private String respuesta;

	private Long eventoid;
	
	@ManyToOne
	@JoinColumn(name = "eventotipoid")
	private Tipo eventoTipo;
	
	private LocalDateTime eventoFecha;
	
	@Column(columnDefinition = "text")
	private String eventoJson;
	
	private String eventoEstado;
	
	private String eventoRespuesta;
	
	@Column(columnDefinition = "text")
	private String infoFisco;

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

	public abstract List<? extends DocumentoDetalle> getDetalles();

	public LocalDateTime getFecha() {
		return fecha;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}

	public String getTimbrado() {
		return timbrado;
	}

	public void setTimbrado(String timbrado) {
		this.timbrado = timbrado;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Tipo getDocumentoTipo() {
		return documentoTipo;
	}

	public void setDocumentoTipo(Tipo documentoTipo) {
		this.documentoTipo = documentoTipo;
	}

	public String getDocumentoNro() {
		return documentoNro;
	}

	public void setDocumentoNro(String documentoNro) {
		this.documentoNro = documentoNro;
	}

	

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public String getDireccion() {
		return Direccion;
	}

	public void setDireccion(String direccion) {
		Direccion = direccion;
	}

	public Localidad getLocalidad() {
		return localidad;
	}

	public void setLocalidad(Localidad localidad) {
		this.localidad = localidad;
	}

	public String getCdc() {
		return cdc;
	}

	public void setCdc(String cdc) {
		this.cdc = cdc;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getQr() {
		return qr;
	}

	public void setQr(String qr) {
		this.qr = qr;
	}

	public boolean isEnviado() {
		return enviado;
	}

	public void setEnviado(boolean enviado) {
		this.enviado = enviado;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getRespuesta() {
		return respuesta;
	}

	public void setRespuesta(String respuesta) {
		this.respuesta = respuesta;
	}

	public Integer getCasaNro() {
		return casaNro;
	}

	public void setCasaNro(Integer casaNro) {
		this.casaNro = casaNro;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public LocalDate getTimbradoFecha() {
		return timbradoFecha;
	}

	public void setTimbradoFecha(LocalDate timbradoFecha) {
		this.timbradoFecha = timbradoFecha;
	}

	public String getTimbradoDocNro() {
		return timbradoDocNro;
	}

	public void setTimbradoDocNro(String timbradoDocNro) {
		this.timbradoDocNro = timbradoDocNro;
	}

	public String getTimbradoSerie() {
		return timbradoSerie;
	}

	public void setTimbradoSerie(String timbradoSerie) {
		this.timbradoSerie = timbradoSerie;
	}

	public Sucursal getSucursal() {
		return sucursal;
	}

	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}

	public Tipo getEventoTipo() {
		return eventoTipo;
	}

	public void setEventoTipo(Tipo eventoTipo) {
		this.eventoTipo = eventoTipo;
	}

	public String getEventoEstado() {
		return eventoEstado;
	}

	public void setEventoEstado(String eventoEstado) {
		this.eventoEstado = eventoEstado;
	}

	

	public String getEventoRespuesta() {
		return eventoRespuesta;
	}

	public void setEventoRespuesta(String eventoRespuesta) {
		this.eventoRespuesta = eventoRespuesta;
	}

	public LocalDateTime getEventoFecha() {
		return eventoFecha;
	}

	public void setEventoFecha(LocalDateTime eventoFecha) {
		this.eventoFecha = eventoFecha;
	}

	public String getEventoJson() {
		return eventoJson;
	}

	public void setEventoJson(String eventoJson) {
		this.eventoJson = eventoJson;
	}

	public Long getEventoid() {
		return eventoid;
	}

	public void setEventoid(Long eventoid) {
		this.eventoid = eventoid;
	}

	public String getInfoFisco() {
		return infoFisco;
	}

	public void setInfoFisco(String infoFisco) {
		this.infoFisco = infoFisco;
	}

	
	
}
