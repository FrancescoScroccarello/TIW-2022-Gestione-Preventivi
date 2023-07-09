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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.Product;
import it.polimi.tiw.dao.ConnectionHandler;
import it.polimi.tiw.dao.ProductDAO;
@WebServlet("/GetProducts")
@MultipartConfig
public class GetProducts extends HttpServlet{
	private static final long serialVersionUID=1L;
	private Connection connection;

	public GetProducts() {
		super();
		this.connection=null;
	}
	
	public void init() throws UnavailableException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ProductDAO prodDao=new ProductDAO(connection);
		ArrayList<Product> products=new ArrayList<>();
		try {
			products=prodDao.getAvailableProducts();
		}
		catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Impossible to load products");
			return;
		}
		Gson gson=new GsonBuilder().create();
		String json=gson.toJson(products);
		response.setStatus(HttpServletResponse.SC_OK);	
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}
}
