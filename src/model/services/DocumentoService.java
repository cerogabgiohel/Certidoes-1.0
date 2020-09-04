package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DocumentoDao;
import model.entities.Documento;

public class DocumentoService {
	
	private DocumentoDao dao = DaoFactory.createDocumentoDao();
	
	public List<Documento> findAll() {	
	
		return dao.findAll();
		
	}
	
	public void saveOrUpdate(Documento obj) {		
		if (obj.getTipoDoc() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void remove(Documento obj) {
		dao.deleteById(obj.getTipoDoc());
	}

}
