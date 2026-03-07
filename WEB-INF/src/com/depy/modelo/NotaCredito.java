package com.depy.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.doxacore.modelo.Tipo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table(name = "notascreditos", indexes = { @Index(name = "idx_notacredito_empresa", columnList = "empresaid"),
		@Index(name = "idx_notacredito_empresa_cliente", columnList = "empresaid, clienteid"),
		@Index(name = "idx_notacredito_empresa_sucursal", columnList = "empresaid, sucursalid"),
		@Index(name = "idx_notacredito_cdc", columnList = "cdc"),
		@Index(name = "idx_notacredito_empresa_numeracion", columnList = "empresaid, numeracion"),
		@Index(name = "idx_notacredito_empresa_fecha", columnList = "empresaid, fecha"),
		@Index(name = "idx_notacredito_empresa_estado", columnList = "empresaid, estado") })
public class NotaCredito extends Documento implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2220421520975454931L;

	@Id
	@Column(name = "notacreditoid")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long notacreditoid;
	
	@ManyToOne
	@JoinColumn(name = "monedaid")
	private Tipo moneda;
	private Double monedaCambio;
	
	@ManyToOne
	@JoinColumn(name = "motivotipoid")
	private Tipo motivo;

	@OneToMany(mappedBy = "notacredito", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<NotaCreditoDetalle> detalles =  new ArrayList<>();
	
	@OneToMany(mappedBy = "notacredito", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<NotaCreditoDoc> documentosAsociados = new ArrayList<>();
	
	private Double totalDetalle = 0.0;
	
	public Double getTotalDetalle() {
		return totalDetalle;
	}
	public void setTotalDetalle(Double totalDetalle) {
		this.totalDetalle = totalDetalle;
	}
	@Override
	public List<NotaCreditoDetalle> getDetalles() {
		return detalles;
	}
	public void setDetalles(List<NotaCreditoDetalle> detalles) {
		this.detalles = detalles;
	}
	public Long getNotacreditoid() {
		return notacreditoid;
	}
	public void setNotacreditoid(Long notacreditoid) {
		this.notacreditoid = notacreditoid;
	}
	public Tipo getMoneda() {
		return moneda;
	}
	public void setMoneda(Tipo moneda) {
		this.moneda = moneda;
	}
	public Double getMonedaCambio() {
		return monedaCambio;
	}
	public void setMonedaCambio(Double monedaCambio) {
		this.monedaCambio = monedaCambio;
	}
	public List<NotaCreditoDoc> getDocumentosAsociados() {
		return documentosAsociados;
	}
	public void setDocumentosAsociados(List<NotaCreditoDoc> documentosAsociados) {
		this.documentosAsociados = documentosAsociados;
	}
	public Tipo getMotivo() {
		return motivo;
	}
	public void setMotivo(Tipo motivo) {
		this.motivo = motivo;
	}
	
	
	
	
}
