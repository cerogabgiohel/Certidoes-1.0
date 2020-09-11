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
import model.dao.ZonaDao;
import model.entities.Zona;

public class ZonaDaoJDBC implements ZonaDao{
	
private Connection conn;
	
	public ZonaDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert (Zona obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO TB_Zona "
					+ "(INT_Zona, TXT_UF, TXT_Sede) "
					+ "VALUES (?,?,?)");
						
			st.setInt(1, obj.getZonaEleitoral());
			st.setString(2, obj.getUf());
			st.setString(3, obj.getSede());
			
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
	public void update(Zona obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
				"UPDATE TB_Zona " +
				"SET INT_Zona = ?, TXT_UF = ?, TXT_Sede = ? " +		
				"WHERE PK_Zona = ?");

			st.setInt(1, obj.getZonaEleitoral());
			st.setString(2, obj.getUf());
			st.setString(3, obj.getSede());
			st.setInt(4, obj.getZona());			

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
				"DELETE FROM TB_Zona WHERE PK_Zona = ?");
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
	
	public Zona findById(Integer id) {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT * FROM TB_Zona where PK_Zona = ?");
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Zona obj = instantiateZona(rs);
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
	
	private Zona instantiateZona(ResultSet rs) throws SQLException {
		Zona obj = new Zona();
		obj.setZona(rs.getInt("PK_Zona"));
		obj.setZonaEleitoral(rs.getInt("INT_Zona"));
		obj.setUf(rs.getString("TXT_UF"));
		obj.setSede(rs.getString("TXT_Sede"));
	
		return obj;
	}
	
	public List<Zona>findAll(){
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT * FROM TB_Zona;");
					
			rs = st.executeQuery();
			
			List<Zona> list = new ArrayList<>();
			
			while (rs.next()) {
				Zona obj = instantiateZona(rs);
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
