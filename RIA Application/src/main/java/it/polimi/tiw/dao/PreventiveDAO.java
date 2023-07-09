package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.beans.Client;
import it.polimi.tiw.beans.Employee;
import it.polimi.tiw.beans.Preventive;
import it.polimi.tiw.beans.User;

public class PreventiveDAO {
	private Connection connection;
	
	public PreventiveDAO(Connection cn) {
		this.connection=cn;
	}
	
	public Preventive getPreventiveByID(int id) throws SQLException {
		String query="SELECT * FROM preventive WHERE ID = ?";
		try {
			PreparedStatement pStatement=connection.prepareStatement(query);
			pStatement.setInt(1, id);
			try {
				ResultSet result=pStatement.executeQuery();
				if(!result.isBeforeFirst()) return null;
				else {
					result.next();
					Preventive prev=new Preventive(
							result.getInt("ID"),
							result.getInt("ClientID"),
							result.getInt("EmployeeID"),
							result.getInt("ProductCode"),
							result.getString("ProductName"),
							result.getInt("Price")
							);
					return prev;
				}
				
			}
			catch(SQLException e) {
				throw e;
			}
		}
		catch(SQLException e) {
			throw e;
		}
	}
	
	public ArrayList<Preventive> findPreventives(User user) throws SQLException {
		ArrayList<Preventive> list=new ArrayList<>();
		String query=null;
		if(user instanceof Client) {
			query="SELECT * FROM preventive WHERE ClientID = ?";
		}
		else if(user instanceof Employee) {
			query="SELECT * FROM preventive WHERE EmployeeID = ?";
		}
		try {
			PreparedStatement pStatement=connection.prepareStatement(query);
			pStatement.setInt(1, user.getID());
			try {
				ResultSet result=pStatement.executeQuery();
				if(!result.isBeforeFirst())return list;
				else {
					result.next();
					while(!result.isAfterLast()) {
						list.add(new Preventive(
								result.getInt("ID"),
								result.getInt("ClientID"),
								result.getInt("EmployeeID"),
								result.getInt("ProductCode"),
								result.getString("ProductName"),
								result.getInt("Price")
								));
						result.next();
					}
					return list;
				}
			}
			catch(SQLException e) {
				throw e;
			}
		}
		catch(SQLException e) {
			throw e;
		}
	}
	
	public void createPreventive(int clientid, int productid, int[] options) throws SQLException {
		String queryPrev="INSERT INTO preventive(ClientID, ProductCode, ProductName) VALUES(?,?,?);";
		String queryOpt="INSERT INTO optionselected(OptionCode, PreventiveID) VALUES(?,?)";
		String idquery= "SELECT last_insert_id()";
		
		ProductDAO prodDao=new ProductDAO(connection);
		String prodname=prodDao.getProductName(productid);
		
		int preventiveid=-1;
		
		try{
			PreparedStatement pStatement1=connection.prepareStatement(queryPrev);
			PreparedStatement pStatement2=connection.prepareStatement(queryOpt);
			PreparedStatement pStatement3=connection.prepareStatement(idquery);
			
			pStatement1.setInt(1, clientid);
			pStatement1.setInt(2, productid);
			pStatement1.setString(3, prodname);
			pStatement1.execute();
			ResultSet result=pStatement3.executeQuery();
			if(!result.isBeforeFirst()) throw new SQLException();
			result.next();
			preventiveid=result.getInt("last_insert_id()");
			
			for(int i : options) {
				pStatement2.setInt(1,i);
				pStatement2.setInt(2, preventiveid);
				pStatement2.execute();
			}
			
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public ArrayList<Preventive> getPreventivesUnassigned() throws SQLException{
		ArrayList<Preventive> list=new ArrayList<>();
		String query="SELECT * FROM preventive WHERE EmployeeID is null";
		PreparedStatement pStatement=connection.prepareStatement(query);
		ResultSet result=pStatement.executeQuery();
		if(!result.isBeforeFirst())return null;
		result.next();
		while(!result.isAfterLast()) {
			list.add(new Preventive(
					result.getInt("ID"),
					result.getInt("ClientID"),
					result.getInt("EmployeeID"),
					result.getInt("ProductCode"),
					result.getString("ProductName"),
					result.getInt("Price")));
			result.next();
		}
		return list;
	}
	
	public void assignPrice(int price, int preventiveid, int employeeid) throws SQLException {
		String query="UPDATE preventive SET EmployeeID = ?, Price = ? WHERE ID = ?";
		
		try{
			PreparedStatement pStatement=connection.prepareStatement(query);
			pStatement.setInt(1, employeeid);
			pStatement.setInt(2,price);
			pStatement.setInt(3, preventiveid);
			
			pStatement.execute(); 
			
		}
		catch(SQLException e) {
			throw e;
		}
	}
}
