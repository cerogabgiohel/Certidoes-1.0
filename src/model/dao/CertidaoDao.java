package model.dao;

import java.util.List;

import model.entities.Certidao;

public interface CertidaoDao {
	
	void insert(Certidao obj);
	void update (Certidao obj);
	void deleteById (Integer id);
	Certidao findById(Integer id);
	List<Certidao> findAll();

}
