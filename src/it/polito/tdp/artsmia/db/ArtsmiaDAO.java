package it.polito.tdp.artsmia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.artsmia.model.ArtObject;
import it.polito.tdp.artsmia.model.ArtObjectAndCount;

public class ArtsmiaDAO {

	public List<ArtObject> listObjects() {
		
		String sql = "SELECT * from objects ";
		List<ArtObject> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				ArtObject artObj = new ArtObject(res.getInt("object_id"), res.getString("classification"), res.getString("continent"), 
						res.getString("country"), res.getInt("curator_approved"), res.getString("dated"), res.getString("department"), 
						res.getString("medium"), res.getString("nationality"), res.getString("object_name"), res.getInt("restricted"), 
						res.getString("rights_type"), res.getString("role"), res.getString("room"), res.getString("style"), res.getString("title"));
				
				result.add(artObj);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public int contaExhibitionComuni(ArtObject aoPartenza, ArtObject aoArrivo) {
		
		String sql = "SELECT count(*) AS exComuni  FROM EXHIBITION_OBJECTS AS a1, EXHIBITION_OBJECTS AS a2 " + 
					 "WHERE  a1.exhibition_id = a2.exhibition_id AND a1.object_id = ? AND a2.object_id = ? ";
		
		Connection conn = DBConnect.getConnection();

		try {
			
			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setInt(1,aoPartenza.getId());
			st.setInt(2,aoArrivo.getId());
			
			ResultSet res = st.executeQuery();
			
			res.next();
			
			int tot = res.getInt("exComuni");
			
			conn.close();
			
			return tot;
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			
			throw new RuntimeException();
		}
	}
	
	public List <ArtObjectAndCount> listArtObjectAndCount(ArtObject ao){
				
		List <ArtObjectAndCount> result = new  ArrayList<>();
		
		String sql = "SELECT count( a2.object_id) as peso, a2.object_id as id " + 
				     "FROM EXHIBITION_OBJECTS AS a1, EXHIBITION_OBJECTS AS a2 " + 
				     "WHERE a1.exhibition_id = a2.exhibition_id AND a1.object_id = ? AND a2.object_id > a1.object_id " + 
			         "GROUP BY a2.object_id ";
		
		Connection conn = DBConnect.getConnection();

		try {
			
			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setInt(1,ao.getId());
			
			ResultSet res = st.executeQuery();
			
			while (res.next()) {
				
				result.add(new ArtObjectAndCount(res.getInt("peso"),res.getInt("id")));
			}
			
			conn.close();
			
			return result;
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			
			throw new RuntimeException();
		}
	}

}
