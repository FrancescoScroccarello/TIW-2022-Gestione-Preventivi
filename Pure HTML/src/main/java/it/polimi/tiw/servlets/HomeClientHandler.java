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

import it.polimi.tiw.beans.Client;
import it.polimi.tiw.beans.Preventive;
import it.polimi.tiw.beans.Product;
import it.polimi.tiw.dao.ConnectionHandler;
import it.polimi.tiw.dao.PreventiveDAO;
import it.polimi.tiw.dao.ProductDAO;

@WebServlet("/Home")
public class HomeClientHandler extends HttpServlet {
	private static final long serialVersionUID=1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	
	public HomeClientHandler() {
		super();
		this.connection=null;
		this.templateEngine=null;
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
		if(request.getSession().getAttribute("user")!=null) {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("user", request.getSession().getAttribute("user"));
			PreventiveDAO prevDao=new PreventiveDAO(connection);
			ProductDAO proDao=new ProductDAO(connection);
			
			try {
				ArrayList<Preventive> preventives=prevDao.findPreventives((Client)request.getSession().getAttribute("user"));
				ArrayList<Product> products=proDao.getAvailableProducts();
				ctx.setVariable("preventives",preventives);
				ctx.setVariable("products", products);
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossible to load preventives");
			}
			templateEngine.process("/homeClient", ctx, response.getWriter());
			
		}
		else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Login error");
		}
	}
	
	
}
