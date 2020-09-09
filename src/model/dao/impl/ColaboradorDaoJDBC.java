package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import db.DbIntegrityException;
import model.dao.ColaboradorDao;
import model.entities.Colaborador;
import model.entities.Zona;

public class ColaboradorDaoJDBC implements ColaboradorDao{

private Connection conn;
	
	public ColaboradorDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert (Colaborador obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO TB_Colaborador "
					+ "(TXT_Nome, FK_Zona) "
					+ "VALUES (?,?)");
						
			st.setString(1, obj.getNome());
			st.setString(2, obj.getZona().toString());
			
			
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
	public void update(Colaborador obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
				"UPDATE TB_Colaborador" +
				"SET TXT_Nome = ?, FK_Zona = ? " +		
				"WHERE PK_Colaborador = ?");

			st.setString(1, obj.getNome());
			st.setString(2, obj.getZona().toString());
			

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
				"DELETE FROM TB_Colaborador WHERE PK_Colaborador = ?");
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
	
	public Colaborador findById(Integer id) {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT TB_Colaborador.*, TB_Zona.INT_Zona as intZona, "
					+ "From TB_Colaborador, TB_Zona "
					+ "Where (TB_Colaborador.FK_Zona = TB_Zona.INT_Zona) "
					+"AND (TB_Colaborador.PK_Colaborador = ?);");
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Zona zona = instantiateZona(rs);
				Colaborador obj = instantiateColaborador(rs, zona);
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
	
	private Colaborador instantiateColaborador(ResultSet rs, Zona zona) throws SQLException {
		Colaborador obj = new Colaborador();
		obj.setColaborador(rs.getInt("PK_Colaborador"));
		obj.setNome(rs.getString("TXT_Nome"));
		obj.setZona(zona);
	
		return obj;
	}
	
	private Zona instantiateZona(ResultSet rs) throws SQLException {
		Zona obj = new Zona();
		obj.setZona(rs.getInt("PK_Zona"));
		obj.setZonaEleitoral(rs.getInt("INT_Zona"));
		obj.setUf(rs.getString("TXT_UF"));
		obj.setSede(rs.getString("TXT_Sede"));
	
		return obj;
	}
	
	public List<Colaborador>findAll(){
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT TB_Colaborador.*,TB_Zona.PK_Zona as zonaEleitoral "
					+ "FROM TB_Colaborador, TB_Zona "
					+ "WHERE TB_Colaborador.FK_Zona = TB_Zona.PK_Zona ");
					
			rs = st.executeQuery();
			
			List<Colaborador> list = new ArrayList<>();
			Map<Integer, Zona> map = new HashMap<>();
			
			while (rs.next()) {
				Zona zona = map.get(rs.getInt("zonaEleitoral"));
				Colaborador obj = instantiateColaborador(rs,zona);
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
