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
						
			st.setInt(1, obj.getColaborador().getColaborador());
			st.setInt(2, obj.getTipoDoc().getTipoDoc());
			st.setInt(3, obj.getZona().getZona());
			st.setString(4, obj.getRequerente());
			st.setString(5, Utils.parseToString(obj.getDataEmissao(), "dd/MM/yyyy"));
			
			
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

			st.setInt(1, obj.getColaborador().getColaborador());
			st.setInt(2, obj.getTipoDoc().getTipoDoc());
			st.setInt(3, obj.getZona().getZona());
			st.setString(4, obj.getRequerente());
			st.setString(5, Utils.parseToString(obj.getDataEmissao(), "dd/MM/yyyy"));
			st.setInt(6, obj.getCertidao());

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
			st = conn.prepareStatement(
					"SELECT TB_Certidao.*, TB_Colaborador.TXT_Nome as txtNome, TB_TipoDocumento.TXT_DescricaoTipoDoc as tipDoc, "
							+ " TB_Zona.INT_Zona as intZona, TB_Zona.TXT_UF as txtUF "
							+ "FROM TB_Certidao, TB_Colaborador, TB_TipoDocumento, TB_Zona "
							+ "WHERE (TB_Certidao.FK_Colaborador = TB_Colaborador.PK_Colaborador) "
							+ "AND (TB_Certidao.FK_TipDoc = TB_TipoDocumento.PK_TipDoc) "
							+"AND (TB_Certidao.FK_Zona = PK_Zona "
							+ "AND (TB_Certidao.PK_Certidao = ?);");
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
		obj.setColaborador(rs.getInt("idColab"));
		obj.setNome(rs.getString("nomeColab"));
		obj.setZona(zona);
	
		return obj;
	}
	
	private Zona instantiateZona(ResultSet rs) throws SQLException {
		Zona obj = new Zona();
		obj.setZona(rs.getInt("idZona"));
		obj.setZonaEleitoral(rs.getInt("intZona"));		
		obj.setSede(rs.getString("sedeZona"));
	
		return obj;
	}
	
	private Documento instantiateDocumento(ResultSet rs) throws SQLException {
		Documento obj = new Documento();
		obj.setTipoDoc(rs.getInt("idTipDoc"));
		obj.setDescricao(rs.getString("descricaoTipDoc"));
	
		return obj;
	}
	
	private Certidao instantiateCertidao(ResultSet rs, Colaborador colab, Zona zona, Documento tipoDoc) throws SQLException {
		Certidao obj = new Certidao();
		obj.setCertidao(rs.getInt("PK_Certidao"));
		obj.setColaborador(colab);
		obj.setZona(zona);
		obj.setDataEmissao( Utils.tryParseToDate(rs.getString("DAT_Emissao")));
		obj.setTipoDoc(tipoDoc);
		obj.setRequerente(rs.getString("TXT_Requerente"));
	
		return obj;
	}
	
	public List<Certidao>findAll(){
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
			"SELECT TB_Certidao.*, TB_Colaborador.PK_Colaborador as idColab, TB_Colaborador.TXT_Nome as nomeColab, "  
			+"TB_TipoDocumento.PK_TipDoc as idTipDoc, TB_TipoDocumento.TXT_DescricaoTipDoc as descricaoTipDoc, "
			+"TB_Zona.PK_Zona as idZona, TB_Zona.INT_Zona as intZona, TB_Zona.TXT_Sede as sedeZona "  
			+"FROM TB_CERTIDAO, TB_COLABORADOR, TB_TIPODOCUMENTO, TB_ZONA " 
			+"WHERE TB_Certidao.FK_Colaborador = TB_Colaborador.PK_Colaborador " 
			+"AND TB_Certidao.FK_TipDoc = TB_TipoDocumento.PK_TipDoc " 
			+"AND TB_Certidao.FK_Zona = TB_Zona.PK_Zona");
					
			rs = st.executeQuery();
			
			List<Certidao> list = new ArrayList<>();
			
			Map<Integer, Zona> map = new HashMap<>();
			Map<Integer, Documento> mapDoc = new HashMap<>();
			Map<Integer, Colaborador> mapColab = new HashMap<>();
			
			while (rs.next()) {
				
				Documento doc = mapDoc.get(rs.getInt("idTipDoc"));
				
				Colaborador colab = mapColab.get(rs.getInt("idColab"));
				
				Zona zona = map.get(rs.getInt("idZona"));
				
				if(zona == null) {
					zona = instantiateZona(rs);
					map.put(rs.getInt("idZona"), zona);
				}
				
				if(doc == null) {
					doc= instantiateDocumento(rs);
					mapDoc.put(rs.getInt("idTipDoc"), doc);
				}
				
				if(colab == null) {
					colab= instantiateColaborador(rs, zona);
					mapColab.put(rs.getInt("idColab"), colab);
				}
				
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
