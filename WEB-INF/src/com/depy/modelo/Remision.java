package com.depy.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
@Table(name = "remisiones", indexes = { @Index(name = "idx_remision_empresa", columnList = "empresaid"),
		@Index(name = "idx_remision_empresa_cliente", columnList = "empresaid, clienteid"),
		@Index(name = "idx_remision_empresa_sucursal", columnList = "empresaid, sucursalid"),
		@Index(name = "idx_remision_cdc", columnList = "cdc"),
		@Index(name = "idx_remision_empresa_numeracion", columnList = "empresaid, numeracion"),
		@Index(name = "idx_remision_empresa_fecha", columnList = "empresaid, fecha"),
		@Index(name = "idx_remision_empresa_estado", columnList = "empresaid, estado") })
public class Remision extends Documento implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6202719246406948022L;

	@Id
	@Column(name = "remisionid")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long remisionid;
	
	@ManyToOne
	@JoinColumn(name = "motivoEmisionId")
	private Tipo motivoEmision;
	
	@ManyToOne
	@JoinColumn(name = "responsableemisionid")
	private Tipo responsableEmision;
	
	private int kilometrosRecorridos = 1; 
	private LocalDateTime facturaFecha; 
	
	@ManyToOne
	@JoinColumn(name = "transtipoid")
	private Tipo transTipo;
	
	@ManyToOne
	@JoinColumn(name = "transmodotipoid")
	private Tipo transModalidadtipo;
	
	@ManyToOne
	@JoinColumn(name = "transResponsableFlete")
	private Tipo transResponsableFlete;
	
	private LocalDate salidaFecha = LocalDate.now();
	private LocalDate entregaFecha = LocalDate.now();
	
	private LocalDate facturaEmiFecha = LocalDate.now();
	
	@Column(columnDefinition = "text")
	private String salidaDireccion;
	private Integer salidaCasaNro;
	
	@ManyToOne
	@JoinColumn(name = "salidalocalidadid")
	private Localidad salidaLocalidad;
	
	@Column(columnDefinition = "text")
	private String entregaDireccion;
	private Integer entregaCasaNro;
	
	@ManyToOne
	@JoinColumn(name = "entregalocalidadid")
	private Localidad entregaLocalidad;
	
	private String vehiculoMarca;
	
	@ManyToOne
	@JoinColumn(name = "vehiculoidentid")
	private Tipo vehiculoIdent;
	private String vehiculoNro;
	
	private String transportistaNombre;
	
	@Column(columnDefinition = "text")
	private String transportistaDireccion;
	
	@ManyToOne
	@JoinColumn(name = "transportistadoctipoid")
	private Tipo transportistaDocTipo;
	
	private String transportistaDocNum;
	
	private String choferDocNum;
	private String choferNombre;
	
	@Column(columnDefinition = "text")
	private String choferDireccion;

	@OneToMany(mappedBy = "remision", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<RemisionDetalle> detalles = new ArrayList<>();

	@Override
	public List<RemisionDetalle> getDetalles() {
		return detalles;
	}

	public Long getRemisionid() {
		return remisionid;
	}

	public void setRemisionid(Long remisionid) {
		this.remisionid = remisionid;
	}

	public Tipo getResponsableEmision() {
		return responsableEmision;
	}

	public void setResponsableEmision(Tipo responsableEmision) {
		this.responsableEmision = responsableEmision;
	}

	public Tipo getTransTipo() {
		return transTipo;
	}

	public void setTransTipo(Tipo transTipo) {
		this.transTipo = transTipo;
	}

	public Tipo getTransResponsableFlete() {
		return transResponsableFlete;
	}

	public void setTransResponsableFlete(Tipo transResponsableFlete) {
		this.transResponsableFlete = transResponsableFlete;
	}

	public String getSalidaDireccion() {
		return salidaDireccion;
	}

	public void setSalidaDireccion(String salidaDireccion) {
		this.salidaDireccion = salidaDireccion;
	}
	
	public Integer getSalidaCasaNro() {
		return salidaCasaNro;
	}

	public void setSalidaCasaNro(Integer salidaCasaNro) {
		this.salidaCasaNro = salidaCasaNro;
	}

	public Localidad getSalidaLocalidad() {
		return salidaLocalidad;
	}

	public void setSalidaLocalidad(Localidad salidaLocalidad) {
		this.salidaLocalidad = salidaLocalidad;
	}

	public String getEntregaDireccion() {
		return entregaDireccion;
	}

	public void setEntregaDireccion(String entregaDireccion) {
		this.entregaDireccion = entregaDireccion;
	}

	

	public Integer getEntregaCasaNro() {
		return entregaCasaNro;
	}

	public void setEntregaCasaNro(Integer entregaCasaNro) {
		this.entregaCasaNro = entregaCasaNro;
	}

	public Localidad getEntregaLocalidad() {
		return entregaLocalidad;
	}

	public void setEntregaLocalidad(Localidad entregaLocalidad) {
		this.entregaLocalidad = entregaLocalidad;
	}

	public String getVehiculoMarca() {
		return vehiculoMarca;
	}

	public void setVehiculoMarca(String vehiculoMarca) {
		this.vehiculoMarca = vehiculoMarca;
	}

	public Tipo getVehiculoIdent() {
		return vehiculoIdent;
	}

	public void setVehiculoIdent(Tipo vehiculoIdent) {
		this.vehiculoIdent = vehiculoIdent;
	}

	public String getVehiculoNro() {
		return vehiculoNro;
	}

	public void setVehiculoNro(String vehiculoNro) {
		this.vehiculoNro = vehiculoNro;
	}

	public String getTransportistaNombre() {
		return transportistaNombre;
	}

	public void setTransportistaNombre(String transportistaNombre) {
		this.transportistaNombre = transportistaNombre;
	}

	public String getTransportistaDireccion() {
		return transportistaDireccion;
	}

	public void setTransportistaDireccion(String transportistaDireccion) {
		this.transportistaDireccion = transportistaDireccion;
	}

	public String getChoferDocNum() {
		return choferDocNum;
	}

	public void setChoferDocNum(String choferDocNum) {
		this.choferDocNum = choferDocNum;
	}

	public String getChoferNombre() {
		return choferNombre;
	}

	public void setChoferNombre(String choferNombre) {
		this.choferNombre = choferNombre;
	}

	public void setDetalles(List<RemisionDetalle> detalles) {
		this.detalles = detalles;
	}

	public LocalDateTime getFacturaFecha() {
		return facturaFecha;
	}

	public void setFacturaFecha(LocalDateTime facturaFecha) {
		this.facturaFecha = facturaFecha;
	}

	public Tipo getTransportistaDocTipo() {
		return transportistaDocTipo;
	}

	public void setTransportistaDocTipo(Tipo transportistaDocTipo) {
		this.transportistaDocTipo = transportistaDocTipo;
	}

	public String getTransportistaDocNum() {
		return transportistaDocNum;
	}

	public void setTransportistaDocNum(String transportistaDocNum) {
		this.transportistaDocNum = transportistaDocNum;
	}

	public LocalDate getSalidaFecha() {
		return salidaFecha;
	}

	public void setSalidaFecha(LocalDate salidaFecha) {
		this.salidaFecha = salidaFecha;
	}

	public LocalDate getEntregaFecha() {
		return entregaFecha;
	}

	public void setEntregaFecha(LocalDate entregaFecha) {
		this.entregaFecha = entregaFecha;
	}

	public Tipo getMotivoEmision() {
		return motivoEmision;
	}

	public void setMotivoEmision(Tipo motivoEmision) {
		this.motivoEmision = motivoEmision;
	}

	public Tipo getTransModalidadtipo() {
		return transModalidadtipo;
	}

	public void setTransModalidadtipo(Tipo transModalidadtipo) {
		this.transModalidadtipo = transModalidadtipo;
	}

	public String getChoferDireccion() {
		return choferDireccion;
	}

	public void setChoferDireccion(String choferDireccion) {
		this.choferDireccion = choferDireccion;
	}

	public int getKilometrosRecorridos() {
		return kilometrosRecorridos;
	}

	public void setKilometrosRecorridos(int kilometrosRecorridos) {
		this.kilometrosRecorridos = kilometrosRecorridos;
	}

	public LocalDate getFacturaEmiFecha() {
		return facturaEmiFecha;
	}

	public void setFacturaEmiFecha(LocalDate facturaEmiFecha) {
		this.facturaEmiFecha = facturaEmiFecha;
	}


	
}
