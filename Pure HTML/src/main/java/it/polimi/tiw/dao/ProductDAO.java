package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import it.polimi.tiw.beans.Product;

public class ProductDAO {
	private Connection connection;
	
	public ProductDAO(Connection con) {
		this.connection=con;
	}
	
	public Product getProductByID(int id) throws SQLException {
		String query="SELECT * FROM product WHERE Code = ?";
		try {
			PreparedStatement pStatement=connection.prepareStatement(query);
			pStatement.setInt(1, id);
			try {
				ResultSet result=pStatement.executeQuery();
				if(!result.isBeforeFirst()) return null;
				else {
					result.next();
					return new Product(
							result.getInt("Code"),
							result.getString("Name"),
							result.getString("Image")
							);
							
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
	public ArrayList<Product>getAvailableProducts() throws SQLException{
		ArrayList<Product> list = new ArrayList<>();
		String query = "SELECT * FROM product";
		try{
			PreparedStatement pStatement = connection.prepareStatement(query);
			try {
				ResultSet result = pStatement.executeQuery();
				if(!result.isBeforeFirst()) return null;
				else {
					result.next();
					while(!result.isAfterLast()) {
						list.add(new Product(
							result.getInt("Code"),
							result.getString("Name"),
							result.getString("Image")
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
	
	public String getProductName(int productID) throws SQLException {
		String query="SELECT Name FROM Product WHERE Code = ?";
		
		try {
			PreparedStatement pStatement=connection.prepareStatement(query);
			pStatement.setInt(1, productID);
			ResultSet result=pStatement.executeQuery();
			if(!result.isBeforeFirst()) return null;
			else {
				result.next();
				return result.getString("Name");
			}
		}
		catch(SQLException e) {
			throw e;
		}
	}
}
