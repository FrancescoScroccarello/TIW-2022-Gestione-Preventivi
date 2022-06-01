package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
import it.polimi.tiw.dao.OptionDAO;
import it.polimi.tiw.dao.PreventiveDAO;
import it.polimi.tiw.dao.ProductDAO;

@WebServlet("/CreatePreventive")
public class CreatePreventive extends HttpServlet {
	private static final long serialVersionUID=1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	
	public CreatePreventive() {
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
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String[] checkedOptions = request.getParameterValues("option");
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		PreventiveDAO prevDao=new PreventiveDAO(connection);
		ProductDAO prodDao=new ProductDAO(connection);
		try {
			ArrayList<Preventive> preventives=prevDao.findPreventives((Client)request.getSession().getAttribute("user"));
			ArrayList<Product> products=prodDao.getAvailableProducts();
			ctx.setVariable("preventives", preventives);
			ctx.setVariable("products", products);
		}
		catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Ops, something went wrong");
		}
		String path = "/homeClient.html";
		if (checkedOptions == null) {
			OptionDAO optDao=new OptionDAO(connection);
			int productid=Integer.parseInt(request.getParameter("prodChosen"));
			ctx.setVariable("prodChosen", productid);
			try {
				ctx.setVariable("options",optDao.getOptions(productid));
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Something went wrong");
			}
			ctx.setVariable("user", request.getSession().getAttribute("user"));
			ctx.setVariable("errorMsg", "Per ogni prodotto, selezionare almeno un'opzione");
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		
		int[] options=new int[checkedOptions.length];
		for(int i=0;i<options.length;i++) {
			options[i]=Integer.parseInt(checkedOptions[i]);
		}

		
		try {
			prevDao.createPreventive(((Client)request.getSession().getAttribute("user")).getID(), Integer.parseInt(request.getParameter("prodChosen")), options);
			request.getSession().setAttribute("preventives",prevDao.findPreventives((Client)request.getSession().getAttribute("user")));
		} 
		catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Impossible to create preventive");
		}
		
		ctx.setVariable("user", request.getSession().getAttribute("user"));
		ctx.setVariable("preventives", request.getSession().getAttribute("preventives"));
		ctx.setVariable("products", request.getSession().getAttribute("products"));
		templateEngine.process(path, ctx, response.getWriter());
	}
}
