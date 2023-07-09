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

import it.polimi.tiw.beans.Client;
import it.polimi.tiw.beans.Option;
import it.polimi.tiw.dao.ConnectionHandler;
import it.polimi.tiw.dao.OptionDAO;
import it.polimi.tiw.dao.PreventiveDAO;

@WebServlet("/CreatePrev")
@MultipartConfig
public class CreatePreventive extends HttpServlet{
	private static final long serialVersionUID=1L;
	private Connection connection;

	public CreatePreventive() {
		super();
		this.connection=null;
	}
	
	public void init() throws UnavailableException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int productid=Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("prod")));
		OptionDAO optDao=new OptionDAO(connection);
		try{
			ArrayList<Option>availableoptions=optDao.getOptions(productid);
			ArrayList<Integer> checkedOptions=new ArrayList<>();
			for(int i=0;i<availableoptions.size();i++) {
				String parameter=StringEscapeUtils.escapeJava(request.getParameter(String.valueOf(i)));
				if(parameter!=null)
					checkedOptions.add(Integer.parseInt(parameter));
				else
					break;
			}
			PreventiveDAO prevDao=new PreventiveDAO(connection);
			int[] opts=new int[checkedOptions.size()];
			for(int i=0;i<opts.length;i++) {
				opts[i]=checkedOptions.get(i);
			}
			prevDao.createPreventive(((Client)request.getSession().getAttribute("user")).getID(),productid, opts);
		}
		catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Something went wrong");
		}
	}
}
