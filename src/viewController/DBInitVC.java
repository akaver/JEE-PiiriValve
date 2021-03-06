package viewController;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.*;

/**
 * Servlet implementation class DBInit
 */
public class DBInitVC extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DBInitVC() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().println("DB Init...");
		DAO dao = null;

		try {
			if (request.getParameter("deletelck").equals("yes")) {
				dao = new DAO(true);
			}
		} catch (Exception e) {
			dao = new DAO();
		}
		
		response.getWriter().println("Droping and creating tables!");
		dao.createTables();
		dao.insertDummyData();

		response.getWriter().println("DB Init done!!!");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
