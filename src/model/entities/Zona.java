package model.entities;

import java.io.Serializable;

public class Zona implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Integer zona;
	private Integer zonaEleitoral;
	private String uf;
	private String sede;
	
	public Zona() {
	}
	
	public Zona(Integer zona, Integer zonaEleitoral, String uf, String sede) {
		super();
		this.zona = zona;
		this.zonaEleitoral = zonaEleitoral;
		this.uf = uf;
		this.sede = sede;
	}	

	public Integer getZona() {
		return zona;
	}

	public void setZona(Integer zona) {
		this.zona = zona;
	}

	public Integer getZonaEleitoral() {
		return zonaEleitoral;
	}

	public void setZonaEleitoral(Integer zonaEleitoral) {
		this.zonaEleitoral = zonaEleitoral;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public String getSede() {
		return sede;
	}

	public void setSede(String sede) {
		this.sede = sede;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((zona == null) ? 0 : zona.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Zona other = (Zona) obj;
		if (zona == null) {
			if (other.zona != null)
				return false;
		} else if (!zona.equals(other.zona))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Zona [zona=" + zona + ", zonaEleitoral=" + zonaEleitoral + ", uf=" + uf + ", sede=" + sede + "]";
	}
	

}
