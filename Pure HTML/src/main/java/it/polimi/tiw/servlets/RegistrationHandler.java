package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.UnavailableException;

import it.polimi.tiw.dao.ClientDAO;
import it.polimi.tiw.dao.ConnectionHandler;
import it.polimi.tiw.dao.EmployeeDAO;

@WebServlet("/Registration")
public class RegistrationHandler extends HttpServlet{
	private static final long serialVersionUID=1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	
	public RegistrationHandler() {
		super();
		this.connection = null;
		this.templateEngine = null;
	}
	
	public void init() throws UnavailableException{
		ServletContext servletcontext=getServletContext();
		connection = ConnectionHandler.getConnection(servletcontext);
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletcontext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setSuffix(".html");
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);		
	}
	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String name = request.getParameter("name");
		String surname = request.getParameter("surname");
		String email = request.getParameter("email");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String confirm = request.getParameter("confirm");
		String path=null;
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		if(name==null || surname==null || email==null || username==null || password==null || confirm==null ||
			name.isEmpty() || surname.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || confirm.isEmpty()	) {
			ctx.setVariable("errorMsg", "Compilare tutti i campi");
			templateEngine.process("/register", ctx, response.getWriter());
			return;
		}
		
		if(!email.contains("@")) {
			ctx.setVariable("errorMsg", "Indirizzo email non valido");
			templateEngine.process("/register", ctx, response.getWriter());
			return;
		}
			
		if(password.equals(confirm)) {
			
			ClientDAO cliDao=new ClientDAO(connection);
			EmployeeDAO empDao=new EmployeeDAO(connection);
			
			if(email.contains("@dominioaziendale")) {
				try{
					if(empDao.insertEmployee(name, surname, email, username, password, confirm)) {
						path="login.html";
						response.sendRedirect(path);
						return;
					}	
					else {
						
						ctx.setVariable("errorMsg", "Username non disponibile");
						path = "/register.html";
						templateEngine.process(path, ctx, response.getWriter());
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
					path="login.html";
					response.sendRedirect(path);
					return;
				}	
				else {
					
					ctx.setVariable("errorMsg", "Username non disponibile");
					path = "/register.html";
					templateEngine.process(path, ctx, response.getWriter());
					return;
				}
			}
			catch(SQLException e) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to register");
					return;
			}
		}
		else {
				ctx.setVariable("errorMsg", "I campi di password devono essere uguali");
				path = "/register.html";
				templateEngine.process(path, ctx, response.getWriter());
				return;
		}
	}
	
}
