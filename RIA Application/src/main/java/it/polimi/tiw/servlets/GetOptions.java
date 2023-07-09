package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.Option;
import it.polimi.tiw.beans.Preventive;
import it.polimi.tiw.dao.ClientDAO;
import it.polimi.tiw.dao.ConnectionHandler;
import it.polimi.tiw.dao.OptionDAO;
import it.polimi.tiw.dao.PreventiveDAO;
@WebServlet("/GetOptions")
@MultipartConfig
public class GetOptions extends HttpServlet{
	private static final long serialVersionUID=1L;
	private Connection connection;

	public GetOptions() {
		super();
		this.connection=null;
	}
	
	public void init() throws UnavailableException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		OptionDAO optDao=new OptionDAO(connection);
		if(request.getParameter("product")!=null) {
			int productid=Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("product")));
			try{
				ArrayList<Option>options=optDao.getOptions(productid);
				Gson gson=new GsonBuilder().create();
				String json=gson.toJson(options);
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().println(json);
			}
			catch(SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Impossible to load options");
				return;
			}
		}
		else if(request.getParameter("prev")!=null) {
			int preventiveid=Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("prev")));
			try{
				PreventiveDAO prevDao=new PreventiveDAO(connection);
				Preventive prev=prevDao.getPreventiveByID(preventiveid);
				ArrayList<Integer>options=optDao.getOptionsOfPreventive(preventiveid);
				ArrayList<Object> realoptions=new ArrayList<>();
				ClientDAO cliDao=new ClientDAO(connection);
				String username=cliDao.getUsername(prev.getClient());
				realoptions.add(username);
				realoptions.add(prev);
				for(Integer i : options) {
					realoptions.add(optDao.getOptionByID(i.intValue()));
				}
				Gson gson=new GsonBuilder().create();
				String json=gson.toJson(realoptions);
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().println(json);
			}
			catch(SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Impossible to load options");
				return;
			}
		}
		else {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Something went wrong");
			return;
		}
		
		
		
	}
}
