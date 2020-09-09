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
import gui.util.Utils;
import model.dao.CertidaoDao;
import model.entities.*;

public class CertidaoDaoJDBC implements CertidaoDao {
	
private Connection conn;
	
	public CertidaoDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert (Certidao obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO TB_Certidao "
					+ "(FK_Colaborador, FK_TipDoc, FK_Zona, TXT_Requerente, DAT_Emissao) "
					+ "VALUES (?,?,?,?,?)");
						
			st.setString(1, obj.getColaborador().getNome());
			st.setString(2, obj.getTipoDoc().getDescricao());
			st.setString(3, obj.getZona().getZonaEleitoral() +" "+ obj.getZona().getSede());
			st.setString(4, obj.getRequerente());
			st.setString(5, Utils.parseToString(obj.getDataEmissao(), "dd//MM//yyyy"));
			
			
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
	public void update(Certidao obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
				"UPDATE TB_Certidao " +
				"SET FK_Colaborador = ?, FK_TipDoc = ?, FK_Zona = ?, TXT_Requerente = ?, DAT_Emissao = ? " +		
				"WHERE PK_Certidao = ?");

			st.setString(1, obj.getColaborador().getNome());
			st.setString(2, obj.getTipoDoc().getDescricao());
			st.setString(3, obj.getZona().getZonaEleitoral() + " " + obj.getZona().getSede());
			st.setString(4, obj.getRequerente());
			st.setString(5, Utils.parseToString(obj.getDataEmissao(), "dd/MM/yyyy"));

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
				"DELETE FROM TB_Certidao WHERE PK_Certidao = ?");
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
	
	public Certidao findById(Integer id) {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT TB_Certidao.*, TB_Zona.INT_Zona as intZona, TB_Zona.TXT_UF as uf, "
					+ "TB_Colaborador.TXT_Nome as nome, TB_TipoDocumento.TXT_DescricaoTipDoc as tipDoc "
					+ "From TB_Certidao, TB_Zona, TB_Colaborador, TB_TipoDocumento "
					+ "Where (TB_Certidao.FK_TipDoc = TB_Colaborador.TXT_Nome) "
					+ "AND (TB_Certidao.FK_TipDoc = TB_TipoDocumento.TXT_DescricaoTipDoc) "
					+ "AND (TB_Certidao.FK_Zona = TB_Zona.INT_Zona, TB_Zona.TXT_UF) "
					+ "AND (TB_Certidao.PK_Colaborador = ?);");
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				
				Documento doc = instantiateDocumento(rs);
				Zona zona = instantiateZona(rs);
				Colaborador colab = instantiateColaborador(rs, zona);
				Certidao obj = instantiateCertidao(rs, colab, zona, doc);
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
	
	private Documento instantiateDocumento(ResultSet rs) throws SQLException {
		Documento obj = new Documento();
		obj.setTipoDoc(rs.getInt("PK_TipDoc"));
		obj.setDescricao(rs.getString("TXT_DescricaoTipDoc"));
	
		return obj;
	}
	
	private Certidao instantiateCertidao(ResultSet rs, Colaborador colab, Zona zona, Documento tipoDoc) throws SQLException {
		Certidao obj = new Certidao();
		obj.setCertidao(rs.getInt("PK_Certidao"));
		obj.setColaborador(colab);
		obj.setZona(zona);
		obj.setDataEmissao(Utils.tryParseToDate(rs.getString("DAT_Emissao")));
		obj.setTipoDoc(tipoDoc);
		obj.setRequerente(rs.getString("TXT_Requerente"));
	
		return obj;
	}
	
	public List<Certidao>findAll(){
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT TB_Certidao.*, TB_Zona.INT_Zona as intZona, TB_Zona.TXT_UF as uf, "
					+ "TB_Colaborador.TXT_Nome as nome, TB_TipoDocumento.TXT_DescricaoTipDoc as tipDoc "
					+ "From TB_Certidao, TB_Zona, TB_Colaborador, TB_TipoDocumento "
					+ "Where (TB_Certidao.FK_TipDoc = TB_Colaborador.TXT_Nome) "
					+ "AND (TB_Certidao.FK_TipDoc = TB_TipoDocumento.TXT_DescricaoTipDoc) "
					+ "AND (TB_Certidao.FK_Zona = TB_Zona.INT_Zona, TB_Zona.TXT_UF) ");
					
			rs = st.executeQuery();
			
			List<Certidao> list = new ArrayList<>();
			Map<String, Zona> map = new HashMap<>();
			Map<String, Documento> mapDoc = new HashMap<>();
			Map<String, Colaborador> mapColab = new HashMap<>();
			while (rs.next()) {
				Documento doc = mapDoc.get(rs.getString("tipDoc"));
				Colaborador colab = mapColab.get(rs.getString("nome"));
				Zona zona = map.get(rs.getString("intZona" + " " + " uf"));
				Certidao obj = instantiateCertidao(rs,colab, zona, doc);
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
