package model.services;

//import java.util.ArrayList;
//import java.util.Date;
//import model.entities.Documento;
//import model.entities.Zona;
//import model.entities.Colaborador;
import java.util.List;

import model.dao.CertidaoDao;
import model.dao.DaoFactory;
import model.entities.Certidao;


public class CertidaoService {	
	
	private CertidaoDao dao = DaoFactory.createCertidaoDao();
	
	public List<Certidao> findAll() {		
		/*MOCK
		List<Certidao> list = new ArrayList<>();
		list.add(new Certidao(11, new Colaborador(), new Documento(), new Zona(), "Gabriel", new Date()));
		return list;
		*/
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
