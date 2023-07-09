package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.beans.Employee;
import it.polimi.tiw.dao.ConnectionHandler;
import it.polimi.tiw.dao.PreventiveDAO;

@WebServlet("/CompletePrev")
@MultipartConfig
public class CompletePreventive extends HttpServlet{
	private static final long serialVersionUID=1L;
	private Connection connection;

	public CompletePreventive() {
		super();
		this.connection=null;
	}
	
	public void init() throws UnavailableException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PreventiveDAO prevDao=new PreventiveDAO(connection);
		int preventive=Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("preventive")));
		int price=-1;
		try{
			price=Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("price")));
		}
		catch(NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Inserire un prezzo adeguato");
			return;
		}
		if(price<=0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Inserire un prezzo adeguato");
			return;
		}
		try {
			prevDao.assignPrice(price, preventive, ((Employee)request.getSession().getAttribute("user")).getID());
			response.setStatus(HttpServletResponse.SC_OK);
			return;
		}
		catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Impossible to complete preventive");
		}
	}
}
