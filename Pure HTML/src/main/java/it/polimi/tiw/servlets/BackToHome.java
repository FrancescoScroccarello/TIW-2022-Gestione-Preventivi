package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.Employee;
import it.polimi.tiw.beans.Preventive;
import it.polimi.tiw.dao.ConnectionHandler;
import it.polimi.tiw.dao.PreventiveDAO;

@WebServlet("/BackToHome")
public class BackToHome extends HttpServlet{
	
	private static final long serialVersionUID=1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	
	public BackToHome() {
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
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		try {
			PreventiveDAO prevDao=new PreventiveDAO(connection);	
			ArrayList<Preventive> list=prevDao.findPreventives((Employee)request.getSession().getAttribute("user"));
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("user", request.getSession().getAttribute("user"));
			ctx.setVariable("preventives", list);
			list=prevDao.getPreventivesUnassigned();
			ctx.setVariable("todo", list);
			templateEngine.process("/homeEmployee", ctx, response.getWriter());
		}
		catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Ops something went wrong");
		}
	}
}
