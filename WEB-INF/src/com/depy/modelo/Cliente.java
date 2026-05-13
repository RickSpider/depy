package com.depy.modelo;

import java.io.Serializable;

import org.hibernate.annotations.ColumnDefault;

import com.doxacore.modelo.Departamento;
import com.doxacore.modelo.Pais;
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
@Table(name = "clientes",indexes = {
		@Index(name = "idx_cliente_empresa", columnList = "empresaid"),
		@Index(name = "idx_cliente_empresa_documentonro", columnList = "empresaid, documentonro"),
        @Index(name = "idx_cliente_empresa_razonsocial", columnList = "empresaid, razonsocial")
    })
public class Cliente extends ModeloERP implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8546643412776940972L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long clienteid;
	
	private Tipo tipoOperacion;
	
	@ManyToOne
    @JoinColumn(name = "documentotipoid")
	private Tipo documentoTipo;
	private String documentoTipoOtro;
	
	private Integer casaNro;
	private String razonsocial;
	private String documentoNro;
	
	
	private String direccion;
	
	@ManyToOne
    @JoinColumn(name = "paisid")
	private Pais pais;
	
	@ManyToOne
	@JoinColumn(name = "departamentoid")
	private Departamento departamento;
	
	@ManyToOne
	@JoinColumn(name = "localidadid")
	private Localidad localidad;
	
	
	private String email;
	private String telefono;
	private String celular;
	
	@ColumnDefault("false")
	private Boolean gubernamental;
	
	
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
	public Long getClienteid() {
		return clienteid;
	}
	public void setClienteid(Long clienteid) {
		this.clienteid = clienteid;
	}
	public Tipo getTipoOperacion() {
		return tipoOperacion;
	}
	public void setTipoOperacion(Tipo tipoOperacion) {
		this.tipoOperacion = tipoOperacion;
	}
	
	public Integer getCasaNro() {
		return casaNro;
	}
	public void setCasaNro(Integer casaNro) {
		this.casaNro = casaNro;
	}
	public String getRazonsocial() {
		return razonsocial;
	}
	public void setRazonsocial(String razonsocial) {
		this.razonsocial = razonsocial;
	}
	public String getDocumentoNro() {
		return documentoNro;
	}
	public void setDocumentoNro(String documentoNro) {
		this.documentoNro = documentoNro;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public Pais getPais() {
		return pais;
	}
	public void setPais(Pais pais) {
		this.pais = pais;
	}
	public Departamento getDepartamento() {
		return departamento;
	}
	public void setDepartamento(Departamento departamento) {
		this.departamento = departamento;
	}
	public Localidad getLocalidad() {
		return localidad;
	}
	public void setLocalidad(Localidad localidad) {
		this.localidad = localidad;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getCelular() {
		return celular;
	}
	public void setCelular(String celular) {
		this.celular = celular;
	}
	public Tipo getDocumentoTipo() {
		return documentoTipo;
	}
	public void setDocumentoTipo(Tipo documentoTipo) {
		this.documentoTipo = documentoTipo;
	}
	public String getDocumentoTipoOtro() {
		return documentoTipoOtro;
	}
	public void setDocumentoTipoOtro(String documentoTipoOtro) {
		this.documentoTipoOtro = documentoTipoOtro;
	}
	public Boolean getGubernamental() {
		return gubernamental;
	}
	public void setGubernamental(Boolean gubernamental) {
		this.gubernamental = gubernamental;
	}
	
	
	
	
}
