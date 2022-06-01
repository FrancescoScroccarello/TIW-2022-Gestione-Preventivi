package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.Client;
import it.polimi.tiw.beans.Option;
import it.polimi.tiw.beans.Preventive;
import it.polimi.tiw.beans.Product;
import it.polimi.tiw.dao.ConnectionHandler;
import it.polimi.tiw.dao.OptionDAO;
import it.polimi.tiw.dao.PreventiveDAO;
import it.polimi.tiw.dao.ProductDAO;

@WebServlet("/GetPreventiveDetails")
public class GetPreventiveDetails extends HttpServlet {
	private static final long serialVersionUID=1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	
	public GetPreventiveDetails() {
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
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		int preventiveid=Integer.parseInt(request.getParameter("preventiveid"));
		
		Preventive prev=null;
		PreventiveDAO prevDao=new PreventiveDAO(connection);
		ProductDAO prodDao=new ProductDAO(connection);
		ArrayList<Option> opt=new ArrayList<>();
		OptionDAO optDao=new OptionDAO(connection);
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		try{
			ArrayList<Preventive> preventives=prevDao.findPreventives((Client)request.getSession().getAttribute("user"));
			ArrayList<Product> products=prodDao.getAvailableProducts();
			ctx.setVariable("preventives", preventives);
			ctx.setVariable("products", products);;
			
			prev=prevDao.getPreventiveByID(preventiveid);
			prev.setOptions(optDao.getOptionsOfPreventive(preventiveid));
			for(Integer i : prev.getOptions())
				opt.add(optDao.getOptionByID(i.intValue()));
			
		}
		catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Impossible to load details");
		}
		
		
		
		String path = "/homeClient.html";
		ctx.setVariable("user", request.getSession().getAttribute("user"));
		ctx.setVariable("detail", prev);
		ctx.setVariable("optionsDetail", opt);
		templateEngine.process(path, ctx, response.getWriter());
	}
}
