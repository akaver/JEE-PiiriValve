package viewController;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.*;
import viewModel.*;


/**
 * Servlet implementation class BorderGuard
 */
public class IndexVC extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public IndexVC() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Lets draw the main entry screen
		ShowMainScreen(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		ShowMainScreen(request,response);
	}

	private void ShowMainScreen(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// get the session
		HttpSession session = request.getSession();
		// and delete this viewmodel from session, so we can start fresh on each
		// get request
		// normally second requests should come through post
		session.removeAttribute("formData");
		
		// set the encoding, otherwise database is encoded wrongly 
		request.setCharacterEncoding("UTF-8");
		
		// create the view model object and populate it with some data, get
					// it through dao
		IndexVM formData = new IndexVM();
		
		// load data
		formData.setAdminUnitTypeList(new AdminUnitTypeDAO().getAll());
		formData.setAdminUnitList(new AdminUnitDAO().getAll());
		
		// save the viewmodel for jsp dispatcher into session
		session.setAttribute("formData", formData);
		
		// call the dispatcher
		request.getRequestDispatcher("IndexScreen.jsp").forward(request, response);
	}
}