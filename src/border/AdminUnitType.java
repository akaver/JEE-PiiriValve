package border;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import viewModel.*;
import dao.*;

/**
 * Servlet implementation, for entity AdminUnitType
 * 
 * thera are several ways to get here: 1 - through get with some ID for editing
 * (check id for existence?) 2 - through get for new type 3 - post (after get),
 * wiht some changed data (removed or added subordinates) - do not save to
 * database yet - so after post everything should be saved in session 4 - post
 * (after get), final submit button - save everything to base and go back to
 * originating page (which is saved where?)
 * 
 * so either there is session, or there isnt cant i immediately, when i see
 * nosession, create it, stuff everything into it and be done? from that point
 * forward, ony deal with session. me gusta.
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// get the session
		HttpSession session = request.getSession();
		// and delete this viewmodel from session, so we can start fresh on each get request
		// normally second requests should come through post
		session.removeAttribute("formData");

		ShowMainScreen(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		ShowMainScreen(request, response);
	}

	protected void ShowMainScreen(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// so, 2 ways to be here - there is session with our data (then it was post), 
		// or there isnt - then its get

		// there are two ways to arrive here through get method
		// either with id already set (validate it!) for editing or id=new or
		// id=0 and

		// get the session
		HttpSession session = request.getSession();
		// get the viewmodel data from session
		AdminUnitTypeVM formData = (AdminUnitTypeVM) session
				.getAttribute("formData");

		// if no viewmodel in session, then normally this is first call through
		// get
		// so check get parameters and populate viewmodel with data from dao
		if (formData == null) {
			// null - hack, 0-add, >=1 - real id
			Integer adminUnitTypeID = null;
			if (request.getParameter("AdminUnitTypeID").equals("new")) {
				// create new instance of AdminUnitType
				adminUnitTypeID = 0;
				System.out.println("Creating new entity.");
			} else {
				try {
					adminUnitTypeID = Integer.parseInt(request
							.getParameter("AdminUnitTypeID"));
				} catch (Exception e) {
					System.out.println("Exception in parseInt!");
				}
			}

			// check for valid ID
			if (adminUnitTypeID == 0
					|| (adminUnitTypeID >= 0 && new AdminUnitTypeDAO()
							.isIDValid(adminUnitTypeID))) {
				System.out
						.println("Starting view proccessing for AdminUnitType ID:"
								+ adminUnitTypeID);
			} else {
				// throw exception, this is hacking attempt
				throw new RuntimeException(
						"Hacking attempt, this is not valid ID for AdminUnitType:"
								+ request.getParameter("AdminUnitTypeID"));
			}

			// create the view model object and populate it with some data, get
			// it through dao
			formData = new AdminUnitTypeVM();

			if (adminUnitTypeID == 0) {
				// this is new entity
				formData.setAdminUnitType(new dao.AdminUnitType());
			} else {
				// get the entity from dao
				formData.setAdminUnitType(new AdminUnitTypeDAO()
						.getByID(adminUnitTypeID));
			}

			// get the master for this AdminUnitType
			formData.setAdminUnitTypeMaster(new AdminUnitTypeDAO()
					.getMasterByID(formData.getAdminUnitType()
							.getAdminUnitTypeID()));

			// load the full list of AdminUnitType
			formData.setAdminUnitTypeMasterListWithZero(new AdminUnitTypeDAO()
					.getAll());

			// load the list of subordinates
			formData.setAdminUnitTypesSubordinateList(new AdminUnitTypeDAO()
					.getSubordinates(formData.getAdminUnitType()
							.getAdminUnitTypeID()));
		}

		
		// save the viewmodel for jsp dispatcher into session
		session.setAttribute("formData", formData);

		// call the dispatcher
		request.getRequestDispatcher("AdminUnitTypeMainScreen.jsp").forward(
				request, response);
	}

}
