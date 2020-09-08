package model.dao;

import java.util.List;

import model.entities.Colaborador;

public interface ColaboradorDao {
	void insert(Colaborador obj);
	void update (Colaborador obj);
	void deleteById (Integer id);
	Colaborador findById(Integer id);
	List<Colaborador> findAll();
}
