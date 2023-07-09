package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.beans.Client;

public class ClientDAO{
	private Connection connection;
	
	public ClientDAO(Connection cn) {this.connection=cn;}
	
	public Client checkCredentials(String usrn, String pwd) throws SQLException{
		String query = "SELECT  ID, Username, Name, Surname, Email FROM client  WHERE Username = ? AND Password = ?";
		try{
			PreparedStatement pStatement = connection.prepareStatement(query);
			pStatement.setString(1,usrn);
			pStatement.setString(2, pwd);
			try{
				ResultSet result=pStatement.executeQuery();
				if(!result.isBeforeFirst()) return null;
				else {
					result.next();
					Client cli=new Client(
							result.getInt("ID"),
							result.getString("Username"),
							result.getString("Name"),
							result.getString("Surname")
							);
					return cli;
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
	
	public boolean insertClient(String name, String surname, String email, String username, String password, String confirm) throws SQLException {
		String query =  "INSERT INTO client(Name, Surname, Email, Username, Password) VALUES(?,?,?,?,?)";


		String queryCheck = "SELECT * FROM client WHERE Username = ?";
			
		try {
			PreparedStatement pStatement = connection.prepareStatement(query);
			PreparedStatement pCheck = connection.prepareStatement(queryCheck);
			pStatement.setString(1,name);
			pStatement.setString(2,surname);
			pStatement.setString(3,email);
			pStatement.setString(4,username);
			pCheck.setString(1, username);
			pStatement.setString(5,password);
			
			ResultSet result = pCheck.executeQuery();
			if(!result.isBeforeFirst()) {
				pStatement.execute();
				return true;
			}
			else {
				return false;
			}
			
		}
		catch(SQLException e) {
			throw e;
		}
	}
	
	public String getUsername(int clientid) throws SQLException {
		String query="SELECT Username FROM Client WHERE ID=?";
		try {
			PreparedStatement pStatement=connection.prepareStatement(query);
			pStatement.setInt(1, clientid);
			ResultSet result=pStatement.executeQuery();
			if(!result.isBeforeFirst()) {
				return null;
			}
			else {
				result.next();
				return result.getString("Username");
			}
		}
		catch(SQLException e) {
			throw e;
		}
	}
}


