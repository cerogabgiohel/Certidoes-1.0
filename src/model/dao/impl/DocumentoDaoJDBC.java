package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import db.DbIntegrityException;
import model.dao.DocumentoDao;
import model.entities.Documento;

public class DocumentoDaoJDBC implements DocumentoDao{
	
private Connection conn;
	
	public DocumentoDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert (Documento obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO TB_TipoDocumento "
					+ "(TXT_DescricaoTipDoc) "
					+ "VALUES (?)");
						
			st.setString(1, obj.getDescricao());
			
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected == 0) {
				throw new DbException("Erro inesperado! Nenhuma linha foi inserida!");
			}
		}
		catch (SQLException e) {
			throw new DbException (e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
		
	}
	
	@Override
	public void update(Documento obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
				"UPDATE TB_TipoDocumento " +
				"SET TXT_DescricaoTipDoc = ? " +		
				"WHERE PK_TipDoc = ?");

			st.setString(1, obj.getDescricao());
			st.setInt(2, obj.getTipoDoc());
			

			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		} 
		finally {
			DB.closeStatement(st);
		}
	}
	
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
				"DELETE FROM TB_TipoDocumento WHERE PK_TipDoc = ?");
			st.setInt(1, id);

			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		} 
		finally {
			DB.closeStatement(st);
		}
	}
	
	public Documento findById(Integer id) {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT * FROM TB_TipoDocumento where PK_TipDoc = ?");
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Documento obj = instantiateDocumento(rs);
				return obj;
			} 
			return null;					
		}
		catch (SQLException e) {
			throw new DbException (e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
			
	}
	
	private Documento instantiateDocumento(ResultSet rs) throws SQLException {
		Documento obj = new Documento();
		obj.setTipoDoc(rs.getInt("PK_TipDoc"));
		obj.setDescricao(rs.getString("TXT_DescricaoTipDoc"));
	
		return obj;
	}
	
	public List<Documento>findAll(){
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT * FROM TB_TipoDocumento");
					
			rs = st.executeQuery();
			
			List<Documento> list = new ArrayList<>();
			
			while (rs.next()) {
				Documento obj = instantiateDocumento(rs);
				list.add(obj);
			}
			return list;
		}
		catch (SQLException e) {
				throw new DbException (e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

}
