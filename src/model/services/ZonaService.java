package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.ZonaDao;
import model.entities.Zona;

public class ZonaService {
	
	private ZonaDao dao = DaoFactory.createZonaDao();
	
	public List<Zona> findAll() {	
	
		return dao.findAll();
		
	}
	
	public void saveOrUpdate(Zona obj) {		
		if (obj.getZona() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void remove(Zona obj) {
		dao.deleteById(obj.getZona());
	}

}
