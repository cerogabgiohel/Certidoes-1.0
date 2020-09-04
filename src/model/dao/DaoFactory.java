package model.dao;

import db.DB;
import model.dao.impl.*;

public class DaoFactory {

	public static DocumentoDao createDocumentoDao() {
		return new DocumentoDaoJDBC(DB.getConnection());
	}
}
