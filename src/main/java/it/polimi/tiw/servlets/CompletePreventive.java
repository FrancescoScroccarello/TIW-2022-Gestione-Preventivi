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
import it.polimi.tiw.dao.ConnectionHandler;
import it.polimi.tiw.dao.OptionDAO;
import it.polimi.tiw.dao.PreventiveDAO;

@WebServlet("/CompletePreventive")
public class CompletePreventive extends HttpServlet {
	private static final long serialVersionUID=1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	
	public CompletePreventive() {
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
		int preventiveid=Integer.parseInt(request.getParameter("preventiveid"));
		
		if(request.getParameter("price")=="") {
			OptionDAO optDao=new OptionDAO(connection);
			ArrayList<Option> options=new ArrayList<>();
			try{
				ArrayList<Integer> list=optDao.getOptionsOfPreventive(preventiveid);
				for(Integer i : list)
					options.add(optDao.getOptionByID(i.intValue()));
				ctx.setVariable("preventive", prevDao.getPreventiveByID(preventiveid));
				ctx.setVariable("preventiveid", preventiveid);
				ctx.setVariable("options", options);
				ctx.setVariable("errorMsg", "Inserire un prezzo adeguato");
				templateEngine.process("/pricePreventive", ctx, response.getWriter());
				return;
			}
			catch(SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Ops, something went wrong");
			}
		}
		
		int price=Integer.parseInt(request.getParameter("price"));
		if(price<=0) {
			OptionDAO optDao=new OptionDAO(connection);
			ArrayList<Option> options=new ArrayList<>();
			try{
				ArrayList<Integer> list=optDao.getOptionsOfPreventive(preventiveid);
				for(Integer i : list)
					options.add(optDao.getOptionByID(i.intValue()));
				ctx.setVariable("preventive", prevDao.getPreventiveByID(preventiveid));
				ctx.setVariable("preventiveid", preventiveid);
				ctx.setVariable("options", options);
				ctx.setVariable("errorMsg", "Prezzo non adeguato");
				templateEngine.process("/pricePreventive", ctx, response.getWriter());
				return;
			}
			catch(SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Ops, something went wrong");
			}
		}
		int employeeid=((Employee)request.getSession().getAttribute("user")).getID();
		try{
			prevDao.assignPrice(price, preventiveid, employeeid);
			ctx.setVariable("user", request.getSession().getAttribute("user"));
			ctx.setVariable("preventives", prevDao.findPreventives((Employee)request.getSession().getAttribute("user")));
			ctx.setVariable("todo", prevDao.getPreventivesUnassigned());
			templateEngine.process("/homeEmployee", ctx, response.getWriter());
		}
		catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Impossible to complete the operation");
		}
		
		
	}
}
