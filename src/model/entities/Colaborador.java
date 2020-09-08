package model.entities;

import java.io.Serializable;

public class Colaborador implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer colaborador;
	private String nome;
	private Zona zona;
	
	public Colaborador() {
}

	public Colaborador(Integer colaborador, String nome, Zona zona) {
		super();
		this.colaborador = colaborador;
		this.nome = nome;
		this.zona = zona;
	}

	public Integer getColaborador() {
		return colaborador;
	}

	public void setColaborador(Integer colaborador) {
		this.colaborador = colaborador;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Zona getZona() {
		return zona;
	}

	public void setZona(Zona zona) {
		this.zona = zona;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((colaborador == null) ? 0 : colaborador.hashCode());
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
		Colaborador other = (Colaborador) obj;
		if (colaborador == null) {
			if (other.colaborador != null)
				return false;
		} else if (!colaborador.equals(other.colaborador))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Colaborador [colaborador=" + colaborador + ", nome=" + nome + ", zona=" + zona + "]";
	}
	
	

}
