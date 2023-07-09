package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.Client;
import it.polimi.tiw.beans.Employee;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.ConnectionHandler;
import it.polimi.tiw.dao.UserDAO;

@WebServlet("/CheckLogin")
public class LoginManager extends HttpServlet{
	private static final long serialVersionUID=1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	
	public LoginManager() {
		super();
		this.connection=null;
		this.templateEngine = null;
	}
	
	public void init() throws UnavailableException {
		ServletContext servletcontext=getServletContext();
		connection = ConnectionHandler.getConnection(servletcontext);
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletcontext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username=null;
		String password=null;
		
		username = request.getParameter("username"); 
		password = request.getParameter("password");
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
			ctx.setVariable("errorMsg", "Credenziali mancanti");
			templateEngine.process("/login", ctx, response.getWriter());
		}
		
		UserDAO userDao=new UserDAO(connection);
		User user=null;
		try {
			user=userDao.findUser(username, password);
		}
		catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to check credentials");
			return;
		}
		
		String path=null;
		if (user == null) {
			ctx.setVariable("errorMsg", "Username o password non corretti");
			templateEngine.process("/login", ctx, response.getWriter());
		} else{
			
			request.getSession().setAttribute("user", user);
			
			if(user instanceof Client) {
				path = getServletContext().getContextPath() + "/Home";

			}
			else if(user instanceof Employee) {
				path = getServletContext().getContextPath() + "/home";
			}
			response.sendRedirect(path);
		}
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}