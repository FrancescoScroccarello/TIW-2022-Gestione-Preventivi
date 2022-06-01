package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.beans.Client;
import it.polimi.tiw.beans.Employee;
import it.polimi.tiw.beans.User;

public class UserDAO {

	private Connection connection;
	
	public UserDAO(Connection con) {
		this.connection=con;
	}
	
	public User findUser(String usr, String pwd) throws SQLException {
		String query="SELECT * FROM client WHERE Username = ? AND Password = ?";
		try {
			PreparedStatement pStatement=connection.prepareStatement(query);
			pStatement.setString(1, usr);
			pStatement.setString(2,pwd);
			try {
				ResultSet result = pStatement.executeQuery();
				if(!result.isBeforeFirst()) {
					query="SELECT * FROM employee WHERE Username = ? AND Password = ?";
					pStatement = connection.prepareStatement(query);
					pStatement.setString(1, usr);
					pStatement.setString(2, pwd);
					result = pStatement.executeQuery();
					if(!result.isBeforeFirst()) return null;
					else {
						result.next();
						return new Employee(
								result.getInt("ID"),
								result.getString("Username"),
								result.getString("Name"),
								result.getString("Surname"));
					}
				}
				else {
					result.next();
					return new Client(
							result.getInt("ID"),
							result.getString("Username"),
							result.getString("Name"),
							result.getString("Surname"));
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
}
