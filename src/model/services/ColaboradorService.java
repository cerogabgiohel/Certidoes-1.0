package model.services;

import java.util.ArrayList;
import java.util.List;

import model.dao.DaoFactory;
import model.dao.ColaboradorDao;
import model.entities.Colaborador;
import model.entities.Zona;

public class ColaboradorService {
	
	private ColaboradorDao dao = DaoFactory.createColaboradorDao();
	
	public List<Colaborador> findAll() {		
		//MOCK
		List<Colaborador> list = new ArrayList<>();
		list.add(new Colaborador(11, "Gabriel", new Zona()));
		return list;
		//return dao.findAll();
		
	}
	
	public void saveOrUpdate(Colaborador obj) {		
		if (obj.getColaborador() == null) {
						dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void remove(Colaborador obj) {
		dao.deleteById(obj.getColaborador());
	}

}
