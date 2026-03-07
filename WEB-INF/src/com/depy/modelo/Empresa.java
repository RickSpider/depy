package com.depy.modelo;

import java.io.Serializable;

import com.doxacore.modelo.Modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;


@Entity
@Table(name = "empresas", indexes = {
    @Index(name = "idx_empresa_api_key", columnList = "apiKey")
})
public class Empresa extends Modelo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3459371024065147147L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long empresaid;
	
	@Column(nullable = false)
	private String razonSocial;
	
	@Column(nullable = false, unique = true)
	private String ruc;
	
	@Column(unique = true)
	private String apiKey;
	
	//seccion fcws
	private Long fcwsId;
	private String fcwsPass;
	
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

	public Long getEmpresaid() {
		return empresaid;
	}

	public void setEmpresaid(Long empresaid) {
		this.empresaid = empresaid;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public String getRuc() {
		return ruc;
	}

	public void setRuc(String ruc) {
		this.ruc = ruc;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public Long getFcwsId() {
		return fcwsId;
	}

	public void setFcwsId(Long fcwsId) {
		this.fcwsId = fcwsId;
	}

	public String getFcwsPass() {
		return fcwsPass;
	}

	public void setFcwsPass(String fcwsPass) {
		this.fcwsPass = fcwsPass;
	}

	public String getLogoPath() {
		return logoPath;
	}

	public void setLogoPath(String logoPath) {
		this.logoPath = logoPath;
	}

	
	
}
