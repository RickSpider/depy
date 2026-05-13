package com.depy.modelo;

import java.io.Serializable;
import java.time.LocalDate;
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
@Table(name = "facturas", indexes = { @Index(name = "idx_factura_empresa", columnList = "empresaid"),
		@Index(name = "idx_factura_empresa_cliente", columnList = "empresaid, clienteid"),
		@Index(name = "idx_factura_empresa_sucursal", columnList = "empresaid, sucursalid"),
		@Index(name = "idx_factura_cdc", columnList = "cdc"),
		@Index(name = "idx_factura_empresa_numeracion", columnList = "empresaid, numeracion"),
		@Index(name = "idx_factura_empresa_fecha", columnList = "empresaid, fecha"),
		@Index(name = "idx_factura_empresa_estado", columnList = "empresaid, estado") })
public class Factura extends Documento implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -371197973334956830L;

	@Id
	@Column(name = "facturaid")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long facturaid;

	// seccion Contrataciones publicas
	private String cpModalidad;
	private Long cpEntidad;
	private Long cpAno;
	private Long cpSecuencia;
	private LocalDate cpFechaEmision;

	@ManyToOne
	@JoinColumn(name = "monedaid")
	private Tipo moneda;
	private Double monedaCambio;

	@ManyToOne
	@JoinColumn(name = "condicionid")
	private Tipo condicion;

	@OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FacturaPago> pagos = new ArrayList<>();

	private Long operacionTipoCredito;
	private String plazoCredito;
	private Double montoEntregaIniCredito;

	private Integer cantidadCuotaCredito;

	@OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FacturaCuota> cuotas;

	@OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FacturaDetalle> detalles = new ArrayList<>();
	
	private Double totalDetalle = 0.0;

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

	public Long getFacturaid() {
		return facturaid;
	}

	public void setFacturaid(Long facturaid) {
		this.facturaid = facturaid;
	}

	public String getCpModalidad() {
		return cpModalidad;
	}

	public void setCpModalidad(String cpModalidad) {
		this.cpModalidad = cpModalidad;
	}

	public Long getCpEntidad() {
		return cpEntidad;
	}

	public void setCpEntidad(Long cpEntidad) {
		this.cpEntidad = cpEntidad;
	}

	public Long getCpAno() {
		return cpAno;
	}

	public void setCpAno(Long cpAno) {
		this.cpAno = cpAno;
	}

	public Long getCpSecuencia() {
		return cpSecuencia;
	}

	public void setCpSecuencia(Long cpSecuencia) {
		this.cpSecuencia = cpSecuencia;
	}

	public LocalDate getCpFechaEmision() {
		return cpFechaEmision;
	}

	public void setCpFechaEmision(LocalDate cpFechaEmision) {
		this.cpFechaEmision = cpFechaEmision;
	}

	public Tipo getCondicion() {
		return condicion;
	}

	public void setCondicion(Tipo condicion) {
		this.condicion = condicion;
	}

	public List<FacturaPago> getPagos() {
		return pagos;
	}

	public void setPagos(List<FacturaPago> pagos) {
		this.pagos = pagos;
	}

	public Long getOperacionTipoCredito() {
		return operacionTipoCredito;
	}

	public void setOperacionTipoCredito(Long operacionTipoCredito) {
		this.operacionTipoCredito = operacionTipoCredito;
	}

	public String getPlazoCredito() {
		return plazoCredito;
	}

	public void setPlazoCredito(String plazoCredito) {
		this.plazoCredito = plazoCredito;
	}

	public Double getMontoEntregaIniCredito() {
		return montoEntregaIniCredito;
	}

	public void setMontoEntregaIniCredito(Double montoEntregaIniCredito) {
		this.montoEntregaIniCredito = montoEntregaIniCredito;
	}

	public Integer getCantidadCuotaCredito() {
		return cantidadCuotaCredito;
	}

	public void setCantidadCuotaCredito(Integer cantidadCuotaCredito) {
		this.cantidadCuotaCredito = cantidadCuotaCredito;
	}

	public List<FacturaCuota> getCuotas() {
		return cuotas;
	}

	public void setCuotas(List<FacturaCuota> cuotas) {
		this.cuotas = cuotas;
	}

	public Tipo getMoneda() {
		return moneda;
	}

	public void setMoneda(Tipo moneda) {
		this.moneda = moneda;
	}
	
	@Override
	public List<FacturaDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<FacturaDetalle> detalles) {
		this.detalles = detalles;
	}

	public Double getMonedaCambio() {
		return monedaCambio;
	}

	public void setMonedaCambio(Double monedaCambio) {
		this.monedaCambio = monedaCambio;
	}

	public Double getTotalDetalle() {
		return totalDetalle;
	}

	public void setTotalDetalle(Double totalDetalle) {
		this.totalDetalle = totalDetalle;
	}

}
