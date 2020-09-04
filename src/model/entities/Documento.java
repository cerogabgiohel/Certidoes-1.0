package model.entities;

import java.io.Serializable;

public class Documento implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private Integer tipoDoc;
	private String descricao;
	
	public Documento() {
	}

	public Documento(Integer tipoDoc, String descricao) {
		super();
		this.tipoDoc = tipoDoc;
		this.descricao = descricao;
	}

	public Integer getTipoDoc() {
		return tipoDoc;
	}

	public void setTipoDoc(Integer tipoDoc) {
		this.tipoDoc = tipoDoc;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tipoDoc == null) ? 0 : tipoDoc.hashCode());
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
		Documento other = (Documento) obj;
		if (tipoDoc == null) {
			if (other.tipoDoc != null)
				return false;
		} else if (!tipoDoc.equals(other.tipoDoc))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Documento [tipoDoc=" + tipoDoc + ", descricao=" + descricao + "]";
	}
	
	
}
