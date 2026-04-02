package com.depy.modelo;

import java.io.Serializable;
import java.util.List;

import com.doxacore.modelo.Tipo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Sucursales",
	indexes = {
        @Index(name = "idx_sucursal_empresa", columnList = "empresaid")
        })
public class Sucursal extends ModeloERP implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3991360098257704777L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long sucursalid;
	
	private String nombre;
	private String establecimiento;
	private String direccion;
	
	@ManyToOne
	@JoinColumn(name = "comprobantetipoid")
	private Tipo comprobanteTipo;
	
	@ManyToOne
	@JoinColumn(name = "localidadid")
	private Localidad localidad;
	private Integer casaNro = 0;
		
	@OneToMany(mappedBy = "sucursal", cascade = {CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<SucursalUsuario> usuarios;
	
	private String logoPath;

	public Long getSucursalid() {
		return sucursalid;
	}

	public void setSucursalid(Long sucursalid) {
		this.sucursalid = sucursalid;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getEstablecimiento() {
		return establecimiento;
	}

	public void setEstablecimiento(String establecimiento) {
		this.establecimiento = establecimiento;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public List<SucursalUsuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<SucursalUsuario> usuarios) {
		this.usuarios = usuarios;
	}
	
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

	public Tipo getComprobanteTipo() {
		return comprobanteTipo;
	}

	public void setComprobanteTipo(Tipo comprobanteTipo) {
		this.comprobanteTipo = comprobanteTipo;
	}

	public String getLogoPath() {
		return logoPath;
	}

	public void setLogoPath(String logoPath) {
		this.logoPath = logoPath;
	}

	public Localidad getLocalidad() {
		return localidad;
	}

	public void setLocalidad(Localidad localidad) {
		this.localidad = localidad;
	}

	public Integer getCasaNro() {
		return casaNro;
	}

	public void setCasaNro(Integer casaNro) {
		this.casaNro = casaNro;
	}
	
	

}
