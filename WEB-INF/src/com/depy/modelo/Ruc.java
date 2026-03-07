package com.depy.modelo;

import java.io.Serializable;

import com.doxacore.modelo.Modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name="rucs"
        ,indexes = {
            @Index(name="idx_ruc_ruc", columnList="ruc"),
            @Index(name="idx_ruc_ruc_dv", columnList="ruc, dv")
        }
)
public class Ruc extends Modelo implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -3202849566372602190L;

	@Id
    private Long rucid;
    
    @Column(nullable=false)
    private String ruc;
    private String dv;
    @Column(name="razon_social",columnDefinition="varchar(500)")
    private String razonSocial;
    
    @Column(columnDefinition = "boolean default false")
    private boolean gubernamental = false;

	public Long getRucid() {
		return rucid;
	}

	public void setRucid(Long rucid) {
		this.rucid = rucid;
	}

	public String getRuc() {
		return ruc;
	}

	public void setRuc(String ruc) {
		this.ruc = ruc;
	}

	public String getDv() {
		return dv;
	}

	public void setDv(String dv) {
		this.dv = dv;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public boolean isGubernamental() {
		return gubernamental;
	}

	public void setGubernamental(boolean gubernamental) {
		this.gubernamental = gubernamental;
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
    
    
    
}