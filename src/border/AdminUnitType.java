package border;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import viewModel.*;
import dao.*;

/**
 * Servlet implementation, for entity AdminUnitType
 * 
 * thera are several ways to get here:
 * 1 - through get with some ID for editing (check id for existance?)
 * 2 - through get for new type
 * 3 - post (after get), wiht some changed data (removed or added subordinates) - do not save to database yet - so after post everything should be saved in session
 * 4 - post (after get), final submit button - save everything to base and go back to originating page (which is saved where?)
 * 
 * so either there is session, or there isnt
 * cant i immediately, when i see nosession, create it, stuff everything into it and be done?
 * from that point forward, ony deal with session. me gusta.
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
		
		// this is temporary crap for testing, no real id can be passed like this
		Integer adminUnitTypeID = Integer.parseInt(request.getParameter("AdminUnitTypeID"));
		if (adminUnitTypeID == null){
			adminUnitTypeID  = 2; // 0 - add, 1-country (no subordinates) 
		}
		formData.setAdminUnitType(new AdminUnitTypeDAO().getByID(adminUnitTypeID));
		formData.setAdminUnitTypeMaster(new AdminUnitTypeDAO().getMasterByID(formData.getAdminUnitType().getAdminUnitTypeID()));
		formData.setAdminUnitTypeMasterListWithZero(new AdminUnitTypeDAO().getAll());
		formData.setAdminUnitTypesSubordinateList(new AdminUnitTypeDAO().getSubordinates(formData.getAdminUnitType().getAdminUnitTypeID()));
		
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
