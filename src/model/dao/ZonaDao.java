package model.dao;

import java.util.List;

import model.entities.Zona;

public interface ZonaDao {
	
	void insert(Zona obj);
	void update (Zona obj);
	void deleteById (Integer id);
	Zona findById(Integer id);
	List<Zona> findAll();

}
