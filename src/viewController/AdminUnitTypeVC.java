package viewController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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
public class AdminUnitTypeVC extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Get request");
		// get the session
		HttpSession session = request.getSession();
		// and delete this viewmodel from session, so we can start fresh on each
		// get request
		// normally second requests should come through post
		session.removeAttribute("formData");

		ShowMainScreen(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		ShowMainScreen(request, response);
		System.out.println("Post request");
	}

	protected void ShowMainScreen(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// set the encoding, otherwise database is encoded wrongly
		request.setCharacterEncoding("UTF-8");

		// so, 2 ways to be here - there is session with our data (then it was
		// post),
		// or there isnt - then its get

		// there are two ways to arrive here through get method
		// either with id already set (validate it!) for editing or id=new or
		// id=0

		// get the session
		HttpSession session = request.getSession();
		// get the viewmodel data from session
		AdminUnitTypeVM formData = (AdminUnitTypeVM) session
				.getAttribute("formData");

		// if no viewmodel in session, then normally this is first call through
		// get
		// so check get parameters and populate viewmodel with data from dao
		if (formData == null) {
			session.removeAttribute("errors");
			formData = processGET(request, response);

			/*
			 * 
			 * 
			 * 
			 * // null - hack, 0-add, >=1 - real id Integer adminUnitTypeID =
			 * null; if (request.getParameter("AdminUnitTypeID").equals("new"))
			 * { // create new instance of AdminUnitType adminUnitTypeID = 0;
			 * System.out.println("Creating new entity."); } else { try {
			 * adminUnitTypeID = Integer.parseInt(request
			 * .getParameter("AdminUnitTypeID")); } catch (Exception e) {
			 * System.out.println("Exception in parseInt!"); } }
			 * 
			 * // check for valid ID if (adminUnitTypeID == 0 ||
			 * (adminUnitTypeID >= 0 && new AdminUnitTypeDAO()
			 * .isIDValid(adminUnitTypeID))) { System.out
			 * .println("Starting view proccessing for AdminUnitType ID:" +
			 * adminUnitTypeID); } else { // throw exception, this is hacking
			 * attempt throw new RuntimeException(
			 * "Hacking attempt, this is not valid ID for AdminUnitType:" +
			 * request.getParameter("AdminUnitTypeID")); }
			 * 
			 * // create the view model object and populate it with some data,
			 * get // it through dao formData = new AdminUnitTypeVM();
			 * 
			 * if (adminUnitTypeID == 0) { // this is new entity
			 * formData.setAdminUnitType(new dao.AdminUnitType()); } else { //
			 * get the entity from dao formData.setAdminUnitType(new
			 * AdminUnitTypeDAO() .getByID(adminUnitTypeID)); }
			 * 
			 * // get the master for this AdminUnitType
			 * formData.setAdminUnitTypeMaster(new AdminUnitTypeDAO()
			 * .getMasterByIDWithZero(formData.getAdminUnitType()
			 * .getAdminUnitTypeID()));
			 * 
			 * // load the full list of AdminUnitType // TODO - remove all the
			 * subordinates of itself, otherwise user can // cause circular
			 * reference formData.setAdminUnitTypeMasterListWithZero(new
			 * AdminUnitTypeDAO() .getAll());
			 * 
			 * // load the list of subordinates
			 * formData.setAdminUnitTypesSubordinateList(new AdminUnitTypeDAO()
			 * .getSubordinates(formData.getAdminUnitType()
			 * .getAdminUnitTypeID()));
			 * 
			 * // load the list of possible new subordinates
			 * formData.setAdminUnitTypesSubordinateListPossible(new
			 * AdminUnitTypeDAO()
			 * .getPossibleSubordinates(formData.getAdminUnitType()
			 * .getAdminUnitTypeID()));
			 */
		} else {
			// formData was there, so this is post. lets update the viewmodel
			// with changes the user wants to make

			// maybe it was cancel?
			if (cancelWasPressed(request, response)) {
				return;
			}

			// fill out viewmodel with first data
			formData = updateViewModelFieldsForDB(request, formData);

			// do some simple validation - so at least code and name are set
			// (and should be unique)
			List<String> errors = getValidationErrors(request);
			if (!errors.isEmpty()) {
				session.setAttribute("errors", errors);
			} else {
				session.removeAttribute("errors");
			}

			// AdminUnitTypeMaster (0-"---": no master)
			System.out
					.println("AdminUnitTypeMaster_adminUnitTypeID:"
							+ request
									.getParameter("AdminUnitTypeMaster_adminUnitTypeID"));

			// was AdminUnitTypeMaster dropdown changed?
			formData = updateUnitTypeMaster(request, formData);

			// now the tricky part - scan through several possible submit
			// buttons
			// which button was clicked?
			Enumeration<String> paramNames = request.getParameterNames();
			while (paramNames.hasMoreElements()) {
				String paramName = paramNames.nextElement();

				// table of subordinates for this adminUnitType
				// every line has separate submit button, with item sequence no
				// added to each button name.
				if (paramName.startsWith("RemoveButton_")) {
					formData = removeSubordinate(formData, paramName);
				}

				// add new subordinate
				if (paramName.equals("AddSubordinateButton")) {
					formData = addSubordinate(formData, request);
				}

				// global save and exit, no validation errors
				if (paramName.equals("SubmitButton") && errors.isEmpty()) {
					submitToDataBase(formData, response);
					return;
				}

			}

		}

		// save the viewmodel for jsp dispatcher into session
		session.setAttribute("formData", formData);

		// call the dispatcher
		request.getRequestDispatcher("AdminUnitTypeScreen.jsp").forward(
				request, response);
	}

	private void submitToDataBase(AdminUnitTypeVM formData,
			HttpServletResponse response) throws IOException {
		System.out.println("Submit, no errors, save and exit");

		// we have to update two tables - AdminUnitType and
		// AdminUnitTypeSubordination

		// save the primary AdminUnitType
		AdminUnitTypeDAO adminUnitTypeDAO = new AdminUnitTypeDAO();
		Integer adminUnitTypeID = adminUnitTypeDAO.save(formData
				.getAdminUnitType());
		System.out.println("Updated or new ID for AdminUnitType is:"
				+ adminUnitTypeID);

		// update this units master
		adminUnitTypeDAO.saveMaster(adminUnitTypeID,
				formData.getAdminUnitTypeMaster());

		// update this units subordinates

		// work is done, back to main screen
		response.sendRedirect("IndexVC");
	}

	private AdminUnitTypeVM addSubordinate(AdminUnitTypeVM formData,
			HttpServletRequest request) {
		System.out.println("Adding new subordinate");
		// get the id from the post
		Integer listNo = Integer.parseInt(request
				.getParameter("AdminUnitType_NewSubordinateNo"));
		System.out.println("Adding new subordinate with list sequence no:"
				+ listNo);

		// get the list of exsisting subordinates
		List<dao.AdminUnitType> adminUnitTypesSubordinateList = formData
				.getAdminUnitTypesSubordinateList();
		// add the item
		if (adminUnitTypesSubordinateList == null) {
			adminUnitTypesSubordinateList = new ArrayList<dao.AdminUnitType>();
		}
		adminUnitTypesSubordinateList.add(formData
				.getAdminUnitTypesSubordinateListPossible().get(listNo));
		// put the list back
		formData.setAdminUnitTypesSubordinateList(adminUnitTypesSubordinateList);

		// now remove the item from list of possible candidates

		List<dao.AdminUnitType> adminUnitTypesSubordinateListPossible = formData
				.getAdminUnitTypesSubordinateListPossible();
		adminUnitTypesSubordinateListPossible.remove(listNo.intValue());
		formData.setAdminUnitTypesSubordinateListPossible(adminUnitTypesSubordinateListPossible);

		return formData;
	}

	private AdminUnitTypeVM removeSubordinate(AdminUnitTypeVM formData,
			String paramName) {
		if (paramName.startsWith("RemoveButton_")) {
			// found the button from the list, get the id
			Integer removeSubLineNo = Integer.parseInt(paramName.substring(13));
			System.out.println("removing from sub list item with id:"
					+ removeSubLineNo);
			// get the list
			List<dao.AdminUnitType> adminUnitTypesSubordinateList = formData
					.getAdminUnitTypesSubordinateList();
			// get the item about to be removed, and insert it into
			// possible sublist
			formData.getAdminUnitTypesSubordinateListPossible().add(
					adminUnitTypesSubordinateList.get((int) removeSubLineNo
							.intValue()));

			// remove the item
			adminUnitTypesSubordinateList.remove((int) removeSubLineNo
					.intValue());
			// put the list back
			formData.setAdminUnitTypesSubordinateList(adminUnitTypesSubordinateList);
		}

		return formData;
	}

	private AdminUnitTypeVM updateUnitTypeMaster(HttpServletRequest request,
			AdminUnitTypeVM formData) {
		// was AdminUnitTypeMaster dropdown changed?
		try {
			if (!request.getParameter("AdminUnitTypeMaster_adminUnitTypeID")
					.equals(formData.getAdminUnitTypeMaster()
							.getAdminUnitTypeID().toString())) {
				System.out
						.println("Changing AdminUnitTypeMaster_adminUnitTypeID");
				// change viewmodels AdminUnitTypeMaster, find new one from
				// dao
				// based on new id
				formData.setAdminUnitTypeMaster(new AdminUnitTypeDAO().getByID(Integer.parseInt(request
						.getParameter("AdminUnitTypeMaster_adminUnitTypeID"))));
			}
		} catch (Exception e) {
			System.out.println("Exceptoin:" + e);

		}

		return formData;
	}

	private AdminUnitTypeVM updateViewModelFieldsForDB(
			HttpServletRequest request, AdminUnitTypeVM formData) {
		// lets update the viewmodel with changes the user wants to make
		// and that need to be saved to DB when changes are submitted

		// trivial stuff: name,code,comment
		formData.getAdminUnitType().setCode(
				request.getParameter("AdminUnitTypeCode"));
		formData.getAdminUnitType().setName(
				request.getParameter("AdminUnitTypeName"));
		formData.getAdminUnitType().setComment(
				request.getParameter("AdminUnitTypeComment"));

		return formData;
	}

	private boolean cancelWasPressed(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		// first find if we want to exit => no validation etc is necessary;
		// we will quit here
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String next = paramNames.nextElement();
			if (next.equals("CancelButton")) {
				System.out.println("Cancel, nosave and exit");
				response.sendRedirect("IndexVC");
				return true;
			}
		}
		return false;
	}

	private AdminUnitTypeVM processGET(HttpServletRequest request,
			HttpServletResponse response) {

		Integer adminUnitID = processAndValidateID(request);
		AdminUnitTypeVM formData = populateViewModelWithData(adminUnitID,
				request);
		return formData;
	}

	private Integer processAndValidateID(HttpServletRequest request) {
		// ID from requestParameter: null - hack, 0-add, >=1 - real id

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
		return adminUnitTypeID;
	}

	private AdminUnitTypeVM populateViewModelWithData(Integer adminUnitTypeID,
			HttpServletRequest request) {
		// create the view model object and populate it with some data, get
		// it through dao
		AdminUnitTypeVM formData = new AdminUnitTypeVM();

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
				.getMasterByIDWithZero(formData.getAdminUnitType()
						.getAdminUnitTypeID()));

		// load the full list of AdminUnitType
		// TODO - remove all the subordinates of itself, otherwise user can
		// cause circular reference
		formData.setAdminUnitTypeMasterListWithZero(new AdminUnitTypeDAO()
				.getAll());

		// load the list of subordinates
		formData.setAdminUnitTypesSubordinateList(new AdminUnitTypeDAO()
				.getSubordinates(formData.getAdminUnitType()
						.getAdminUnitTypeID()));

		// load the list of possible new subordinates
		formData.setAdminUnitTypesSubordinateListPossible(new AdminUnitTypeDAO()
				.getPossibleSubordinates(formData.getAdminUnitType()
						.getAdminUnitTypeID(),"NOW()"));

		return formData;
	}

	private List<String> getValidationErrors(HttpServletRequest request) {
		List<String> res = new ArrayList<String>();

		// you should also check for unique value
		if ("".equals(request.getParameter("AdminUnitTypeCode"))) {
			res.add("Enter code!");
		}
		if ("".equals(request.getParameter("AdminUnitTypeName"))) {
			res.add("Enter name!");
		}

		return res;
	}
}
