package it.polito.tdp.newufosightings.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.tdp.newufosightings.model.Adiacenza;
import it.polito.tdp.newufosightings.model.Sighting;
import it.polito.tdp.newufosightings.model.State;

public class NewUfoSightingsDAO {

	public List<Sighting> loadAllSightings() {
		String sql = "SELECT * FROM sighting";
		List<Sighting> list = new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);	
			ResultSet res = st.executeQuery();

			while (res.next()) {
				list.add(new Sighting(res.getInt("id"), res.getTimestamp("datetime").toLocalDateTime(),
						res.getString("city"), res.getString("state"), res.getString("country"), res.getString("shape"),
						res.getInt("duration"), res.getString("duration_hm"), res.getString("comments"),
						res.getDate("date_posted").toLocalDate(), res.getDouble("latitude"),
						res.getDouble("longitude")));
			}

			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		return list;
	}

	public Map<String,State> loadAllStates() {
		String sql = "SELECT * FROM state";
		Map<String,State> result = new HashMap();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				State state = new State(rs.getString("id").toLowerCase(), rs.getString("Name"), rs.getString("Capital"),
						rs.getDouble("Lat"), rs.getDouble("Lng"), rs.getInt("Area"), rs.getInt("Population"),
						rs.getString("Neighbors"));
				
				result.put(state.getId(),state);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
/*
 	select distinct shape
	from sighting
	where year(datetime)=2000
	order by shape
 */
	public List<String> loadShape(int year){
		String sql = "select distinct shape " + 
				"	from sighting " + 
				"	where year(datetime)=? " + 
				"	order by shape";
		List<String> result = new ArrayList<>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, year);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				
				
				result.add(rs.getString("shape"));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	/*
	 select s1.state as stato1 ,s2.state as stato2, count(*) as num
from neighbor as n, sighting as s1, sighting as s2
where n.state1=s1.state and n.state2=s2.state and year(s1.datetime) = year(s2.datetime)
	and s1.shape='circle' and s2.shape='circle' and s2.state>s1.state
group by s1.state, s2.state
	 */
	public List<Adiacenza> loadAdiancenze(String forma, Map<String,State> stati){
		String sql = "select s1.state as stato1 ,s2.state as stato2, count(*) as num " + 
				"from neighbor as n, sighting as s1, sighting as s2 " + 
				"where n.state1=s1.state and n.state2=s2.state and year(s1.datetime) = year(s2.datetime) " + 
				"	and s1.shape='circle' and s2.shape=? and s2.state>s1.state " + 
				"group by s1.state, s2.state ";
		List<Adiacenza> result = new ArrayList<>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, forma);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				String s1=rs.getString("stato1");
				String s2=rs.getString("stato2");
				int peso=rs.getInt("num");
				
				if(stati.containsKey(s1)&&stati.containsKey(s2)) {
					result.add(new Adiacenza(stati.get(s1),stati.get(s2),peso));
				}
				
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	

}

