package model.dao;

import java.util.List;

import model.entities.Documento;

public interface DocumentoDao {

	void insert(Documento obj);
	void update (Documento obj);
	void deleteById (Integer id);
	Documento findById(Integer id);
	List<Documento> findAll();
}
