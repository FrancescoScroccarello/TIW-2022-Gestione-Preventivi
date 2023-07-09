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
import it.polimi.tiw.beans.Option;
import it.polimi.tiw.beans.Preventive;
import it.polimi.tiw.dao.ClientDAO;
import it.polimi.tiw.dao.ConnectionHandler;
import it.polimi.tiw.dao.OptionDAO;
import it.polimi.tiw.dao.PreventiveDAO;

@WebServlet("/AssignPrice")
public class PricePreventive extends HttpServlet {

	private static final long serialVersionUID=1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	
	public PricePreventive() {
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
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		PreventiveDAO prevDao=new PreventiveDAO(connection);
		ClientDAO cDao=new ClientDAO(connection);
		if(request.getParameter("preventiveid")==null) {
			try{
				ArrayList<Preventive> list=prevDao.findPreventives((Employee)request.getSession().getAttribute("user"));
				ctx.setVariable("user", request.getSession().getAttribute("user"));
				ctx.setVariable("preventives", list);
				list=prevDao.getPreventivesUnassigned();
				ctx.setVariable("todo",list);
				ctx.setVariable("errorMsg", "Selezionare un preventivo");
				templateEngine.process("/homeEmployee", ctx, response.getWriter());
				return;
			}
			catch(SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Ops somwthing went wrong");
			}
		}
		
		int preventiveid=Integer.parseInt(request.getParameter("preventiveid"));
		
		OptionDAO optDao=new OptionDAO(connection);
		
		try {
			Preventive prev=prevDao.getPreventiveByID(preventiveid);
			String usrn=cDao.getUsername(prev.getClient());
			ctx.setVariable("username", usrn);
			ctx.setVariable("preventive", prevDao.getPreventiveByID(preventiveid));
			ArrayList<Integer> list=optDao.getOptionsOfPreventive(preventiveid);
			ArrayList<Option> options=new ArrayList<>();
			for(Integer i : list)
				options.add(optDao.getOptionByID(i.intValue()));
			ctx.setVariable("options", options);
			ctx.setVariable("preventiveid", preventiveid);
			templateEngine.process("/pricePreventive", ctx, response.getWriter());
		}
		catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Impossible to handle this preventive");
		}
	}
}
