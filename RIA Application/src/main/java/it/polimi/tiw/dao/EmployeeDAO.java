package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.beans.Employee;

public class EmployeeDAO{
	private Connection connection;
	
	public EmployeeDAO(Connection cn) {this.connection=cn;}
	
	public Employee checkCredentials(String usrn, String pwd) throws SQLException{
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
					Employee cli=new Employee(
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
	
	public boolean insertEmployee(String name, String surname, String email, String username, String password, String confirm) throws SQLException {
		String query =  "INSERT INTO employee(Name, Surname, Email, Username, Password) VALUES(?,?,?,?,?)";


		String queryCheck = "SELECT * FROM employee WHERE Username = ?";
			
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


}
