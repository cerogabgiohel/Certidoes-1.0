package model.dao;

import db.DB;
import model.dao.impl.*;

public class DaoFactory {

	public static DocumentoDao createDocumentoDao() {
		return new DocumentoDaoJDBC(DB.getConnection());
	}
	
	public static ZonaDao createZonaDao() {
		return new ZonaDaoJDBC(DB.getConnection());
	}
	
	public static ColaboradorDao createColaboradorDao() {
		return new ColaboradorDaoJDBC(DB.getConnection());
	}
}
