package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import it.polimi.tiw.beans.Option;

public class OptionDAO {
	private Connection connection;
	
	public OptionDAO(Connection con) {
		this.connection = con;
	}
	
	public ArrayList<Option> getOptions(int productid) throws SQLException{
		ArrayList<Option> list=new ArrayList<>();
		String query ="SELECT * FROM addings WHERE ProductCode = ?";
		try {
			PreparedStatement pStatement = connection.prepareStatement(query);
			pStatement.setInt(1, productid);
			
			ResultSet result=pStatement.executeQuery();
			if(!result.isBeforeFirst()) return null;
			else {
				result.next();
				while(!result.isAfterLast()) {
					list.add(new Option(
							result.getInt("Code"),
							result.getInt("ProductCode"),
							result.getString("Type"),
							result.getString("Name")));
					result.next();
				}
				return list;
			}
		}
		catch(SQLException e) {
			throw e;
		}
		
	}
	
	public Option getOptionByID(int id)throws SQLException {
		String query="SELECT * FROM addings WHERE Code = ?";
		try {
			PreparedStatement pStatement=connection.prepareStatement(query);
			pStatement.setInt(1, id);
			ResultSet result=pStatement.executeQuery();
			if(!result.isBeforeFirst()) return null;
			else {
				result.next();
				Option opt=new Option(
						result.getInt("Code"),
						result.getInt("ProductCode"),
						result.getString("Type"),
						result.getString("Name"));
				return opt;
			}
		}
		catch(SQLException e) {
			throw e;
		}
	}
	public ArrayList<Integer> getOptionsOfPreventive(int id) throws SQLException{
		ArrayList<Integer> list=new ArrayList<>();
		String query="SELECT * FROM optionselected WHERE PreventiveID=?";
		try {
			PreparedStatement pStatement=connection.prepareStatement(query);
			pStatement.setInt(1, id);
			ResultSet result=pStatement.executeQuery();
			if(!result.isBeforeFirst()) return null;
			else {
				result.next();
				while(!result.isAfterLast()) {
					list.add(result.getInt("OptionCode"));
					result.next();
				}
				return list;
			}
		}
		catch(SQLException e) {
			throw e;
		}
	}
}
