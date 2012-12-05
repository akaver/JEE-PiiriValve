package border;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import viewModel.*;
import dao.*;

/**
 * Servlet implementation class AdminUnitType
 */
public class AdminUnitType extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdminUnitType() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ShowMainScreen(request,response);
	}

	
	protected void ShowMainScreen(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//create the view model
		AdminUnitTypeVM formData = new AdminUnitTypeVM();
		// populate it with some data, get it through dao
		formData.setAdminUnitType(new AdminUnitTypeDAO().getByID(1));
		// save the viewmodel for jsp dispatcher
		request.setAttribute("formData", formData);
		//call the dispatcher, pass along the view model
		request.getRequestDispatcher("AdminUnitTypeMainScreen.jsp").forward(request, response);
	}	
	
	
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	
	
}
