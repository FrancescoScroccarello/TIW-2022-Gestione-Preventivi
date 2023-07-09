package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.UnavailableException;

import it.polimi.tiw.dao.ClientDAO;
import it.polimi.tiw.dao.ConnectionHandler;
import it.polimi.tiw.dao.EmployeeDAO;

@WebServlet("/RegistrationHandler")
@MultipartConfig
public class RegistrationManager extends HttpServlet{
	private static final long serialVersionUID=1L;
	private Connection connection;
	
	public RegistrationManager() {
		super();
		this.connection = null;
	}
	
	public void init() throws UnavailableException{
		connection = ConnectionHandler.getConnection(getServletContext());		
	}
	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String name = request.getParameter("name");
		String surname = request.getParameter("surname");
		String email = request.getParameter("email");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String confirm = request.getParameter("confirm");
		
		if(name==null || surname==null || email==null || username==null || password==null || confirm==null ||
			name.isEmpty() || surname.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || confirm.isEmpty()	) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Dati mancanti");
			return;
		}
		if(!email.contains("@")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Indirizzo email non valido");
			return;
		}
			
			
		if(password.equals(confirm)) {
			
			ClientDAO cliDao=new ClientDAO(connection);
			EmployeeDAO empDao=new EmployeeDAO(connection);
			if(email.contains("@dominioaziendale")) {
				try{
					if(empDao.insertEmployee(name, surname, email, username, password, confirm)) {
						response.setStatus(HttpServletResponse.SC_OK);
						response.setContentType("application/json");
						response.setCharacterEncoding("UTF-8");
						return;
					}	
					else {
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						response.getWriter().println("Username non disponibile");
						return;
					}
				}
				catch(SQLException e) {
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to register");
						return;
				}
			}
			try{
				if(cliDao.insertClient(name, surname, email, username, password, confirm)) {
					response.setStatus(HttpServletResponse.SC_OK);
					response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");
					return;
				}	
				else {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().println("Username non disponibile");
					return;
				}
			}
			catch(SQLException e) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to register");
					return;
			}
		}
		else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("I campi di password devono essere uguali");
			return;
		}
	}
	
}
