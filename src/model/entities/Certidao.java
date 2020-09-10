package model.entities;

import java.io.Serializable;
import java.util.Date;

public class Certidao implements Serializable{
	
	private static final long serialVersionUID = 1L;	
	private Integer certidao;
	private Colaborador colaborador;
	private Documento tipoDoc;
	private Zona zona;
	private String requerente;
	private Date dataEmissao;
	
	public Certidao() {
	}

	public Certidao(Integer certidao, Colaborador colaborador, Documento tipoDoc, Zona zona, String requerente,
			Date dataEmissao) {
		super();
		this.certidao = certidao;
		this.colaborador = colaborador;
		this.tipoDoc = tipoDoc;
		this.zona = zona;
		this.requerente = requerente;
		this.dataEmissao = dataEmissao;
	}

	public Integer getCertidao() {
		return certidao;
	}

	public void setCertidao(Integer certidao) {
		this.certidao = certidao;
	}

	public Colaborador getColaborador() {
		return colaborador;
	}

	public void setColaborador(Colaborador colaborador) {
		this.colaborador = colaborador;
	}

	public Documento getTipoDoc() {
		return tipoDoc;
	}

	public void setTipoDoc(Documento tipoDoc) {
		this.tipoDoc = tipoDoc;
	}

	public Zona getZona() {
		return zona;
	}

	public void setZona(Zona zona) {
		this.zona = zona;
	}

	public String getRequerente() {
		return requerente;
	}

	public void setRequerente(String requerente) {
		this.requerente = requerente;
	}

	public Date getDataEmissao() {
		return dataEmissao;
	}

	public void setDataEmissao(Date dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((certidao == null) ? 0 : certidao.hashCode());
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
		Certidao other = (Certidao) obj;
		if (certidao == null) {
			if (other.certidao != null)
				return false;
		} else if (!certidao.equals(other.certidao))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Certidao [certidao=" + certidao + ", colaborador=" + colaborador + ", tipoDoc=" + tipoDoc + ", zona="
				+ zona + ", requerente=" + requerente + ", dataEmissao=" + dataEmissao + "]";
	}
	
	
}
