package model.services;

import java.util.List;

import model.dao.CertidaoDao;
import model.dao.DaoFactory;
import model.entities.Certidao;

public class CertidaoService {	
	
	private CertidaoDao dao = DaoFactory.createCertidaoDao();
	
	public List<Certidao> findAll() {		
		
		return dao.findAll();
		
	}
	
	public void saveOrUpdate(Certidao obj) {		
		if (obj.getCertidao() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void remove(Certidao obj) {
		dao.deleteById(obj.getCertidao());
	}
}
