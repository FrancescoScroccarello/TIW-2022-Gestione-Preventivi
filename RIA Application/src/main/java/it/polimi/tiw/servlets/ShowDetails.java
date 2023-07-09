package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.Client;
import it.polimi.tiw.beans.Option;
import it.polimi.tiw.beans.Preventive;
import it.polimi.tiw.beans.Product;
import it.polimi.tiw.dao.ConnectionHandler;
import it.polimi.tiw.dao.OptionDAO;
import it.polimi.tiw.dao.PreventiveDAO;
import it.polimi.tiw.dao.ProductDAO;

@WebServlet("/ShowDetails")
@MultipartConfig
public class ShowDetails extends HttpServlet {
	private static final long serialVersionUID=1L;
	private Connection connection;
	
	public ShowDetails() {
		super();
		this.connection=null;
	}
	
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int previd=Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("preventiveid")));
		
		Preventive prev=null;
		PreventiveDAO prevDao=new PreventiveDAO(connection);
		ProductDAO prodDao=new ProductDAO(connection);
		ArrayList<Option> opt=new ArrayList<>();
		OptionDAO optDao=new OptionDAO(connection);
		
		try{
			ArrayList<Preventive> preventives=prevDao.findPreventives((Client)request.getSession().getAttribute("user"));
			ArrayList<Product> products=prodDao.getAvailableProducts();
			
			
			prev=prevDao.getPreventiveByID(previd);
			prev.setOptions(optDao.getOptionsOfPreventive(previd));
			for(Integer i : prev.getOptions())
				opt.add(optDao.getOptionByID(i.intValue()));
			ArrayList<Object>details=new ArrayList<>();
			details.add(opt);
			details.add(prev);
			Gson gson=new GsonBuilder().create();
			String json=gson.toJson(details);
			response.setStatus(HttpServletResponse.SC_OK);	
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
			return;
		}
		catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Impossible to load data");
			return;
		}
	}
}
