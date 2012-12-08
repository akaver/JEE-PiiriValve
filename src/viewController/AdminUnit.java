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
 * Servlet implementation class AdminUnit
 */
public class AdminUnit extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdminUnit() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// get the session
		HttpSession session = request.getSession();
		// and delete this viewmodel from session, so we can start fresh on each
		// get request
		// normally second requests should come through post
		session.removeAttribute("formData");

		showMainScreen(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		showMainScreen(request, response);		
	}
	
	protected void showMainScreen (HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// get the session
		HttpSession session = request.getSession();
		// get the viewmodel data from session
		AdminUnitVM formData = (AdminUnitVM) session
				.getAttribute("formData");
		// if no viewmodel in session, then normally this is first call through
		// get
		// so check get parameters and populate viewmodel with data from dao
		if (formData == null) {
			// null - hack, 0-add, >=1 - real id
			Integer adminUnitID = null;
			if (request.getParameter("AdminUnitID").equals("new")) {
				// create new instance of AdminUnitType
				adminUnitID = 0;
				System.out.println("Creating new entity.");
			} else {
				try {
					adminUnitID = Integer.parseInt(request
							.getParameter("AdminUnitID"));
				} catch (Exception e) {
					System.out.println("Exception in parseInt!");
				}
			}

			// check for valid ID
			if (adminUnitID == 0
					|| (adminUnitID >= 0 && new AdminUnitDAO()
							.isIDValid(adminUnitID))) {
				System.out.println("Starting view proccessing for AdminUnit ID:"
						+ adminUnitID);
			} else {
				// throw exception, this is hacking attempt
				throw new RuntimeException(
					"Hacking attempt, this is not valid ID for AdminUnit:"
							+ request.getParameter("AdminUnitID"));
			}

			// create the view model object and populate it with some data, get
			// it through dao
			formData = new AdminUnitVM();

			if (adminUnitID == 0) {
				// this is new entity
				formData.setAdminUnit(new dao.AdminUnit());
			} else {
				// get the entity from dao
				formData.setAdminUnit(new AdminUnitDAO()
					.getByID(adminUnitID));
			}

			// get the master for this AdminUnitType
			formData.setAdminUnitMaster(new AdminUnitDAO()
					.getMasterByIDWithZero(formData.getAdminUnit()
					.getAdminUnitID()));

			// load the full list of AdminUnit
			// TODO - remove all the subordinates of itself, otherwise user can
			// cause circular reference
			formData.setAdminUnitMasterListWithZero(new AdminUnitDAO()
				.getAllowedMastersByID(formData.getAdminUnit()
					.getAdminUnitTypeID()), formData.getAdminUnitMaster());

			// load the list of subordinates
			formData.setAdminUnitsSubordinateList(new AdminUnitDAO()
					.getSubordinates(formData.getAdminUnit()
							.getAdminUnitID()));

			// load the list of possible new subordinates
			formData.setAdminUnitsSubordinateListPossible(new AdminUnitDAO()
					.getAllowedSubordinatesByID(formData.getAdminUnit()
							.getAdminUnitTypeID(), formData.getAdminUnitsSubordinateList()));

		} else {
			// formData was there, so this is post. lets update the viewmodel
			// with changes the user wants to make
			// trivial stuff: name,code,comment
			formData.getAdminUnit().setCode(
				request.getParameter("AdminUnitCode"));
			formData.getAdminUnit().setName(
				request.getParameter("AdminUnitName"));
			formData.getAdminUnit().setComment(
				request.getParameter("AdminUnitComment"));

			// do some simple validation - so at least code and name are set
			// (and should be unique)
			List<String> errors = getValidationErrors(request);
			if (!errors.isEmpty()) {
				session.setAttribute("errors", errors);
			} else {
				session.removeAttribute("errors");
			}

			// AdminUnitMaster (0-"---": no master)
			System.out.println("AdminUnitMaster_adminUnitID:"
				+ request.getParameter("AdminUnitMaster_adminUnitID"));
			// was AdminUnitMaster dropdown changed?
			// do we have new adminunittype?
			try {
				if (!request
						.getParameter("AdminUnitMaster_adminUnitID")
						.equals(formData.getAdminUnitMaster()
								.getAdminUnitID().toString())) {
					System.out.println("Changing AdminUnitMaster_adminUnitID");
					// change viewmodels AdminUnitMaster, find new one from
					// dao
					// based on new id
					formData.setAdminUnitMaster(new AdminUnitDAO().getByID(Integer.parseInt(request
							.getParameter("AdminUnitMaster_adminUnitID"))));
				}
				if (!request.getParameter("AdminUnitType_adminUnitTypeID")
						.equals(formData.getAdminUnitType()
								.getAdminUnitTypeID().toString())) {
					System.out.println("Changing AdminUnitType_adminUnitTypeID");
					formData.setAdminUnitType(new AdminUnitTypeDAO().getByID(Integer.parseInt(request
							.getParameter("AdminUnitType_adminUnitTypeID"))));
				}
			} catch (Exception e) {
				System.out.println("Exception:" + e);
			}
			
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
					// found the button from the list, get the id
					Integer removeSubLineNo = Integer.parseInt(paramName
							.substring(13));
					System.out.println("removing from sub list item with id:"
							+ removeSubLineNo);
					// get the list
					List<dao.AdminUnit> adminUnitsSubordinateList = formData
							.getAdminUnitsSubordinateList();
					// get the item about to be removed, and insert it into
					// possible sublist
					formData.getAdminUnitsSubordinateListPossible().add(
							adminUnitsSubordinateList
									.get((int) removeSubLineNo.intValue()));
					// remove the item
					adminUnitsSubordinateList.remove((int) removeSubLineNo
							.intValue());
					// put the list back
					formData.setAdminUnitsSubordinateList(adminUnitsSubordinateList);
				}
				// add new subordinate
				if (paramName.equals("AddSubordinateButton")) {
					System.out.println("Adding new subordinate");
					// get the id from the post
					Integer listNo = Integer.parseInt(request
						.getParameter("AdminUnitType_NewSubordinateNo"));
					System.out.println("Adding new subordinate with list sequence no:"
						+ listNo);

					// get the list of exsisting subordinates
					List<dao.AdminUnit> adminUnitsSubordinateList = formData
						.getAdminUnitsSubordinateList();
					// add the item
					if (adminUnitsSubordinateList == null) {
						adminUnitsSubordinateList = new ArrayList<dao.AdminUnit>();
					}
					adminUnitsSubordinateList.add(formData
							.getAdminUnitsSubordinateListPossible().get(
									listNo));
					// put the list back
					formData.setAdminUnitsSubordinateList(adminUnitsSubordinateList);

					// now remove the item from list of possible candidates

					List<dao.AdminUnit> adminUnitsSubordinateListPossible = formData
							.getAdminUnitsSubordinateListPossible();
					adminUnitsSubordinateListPossible.remove(listNo
							.intValue());
					formData.setAdminUnitsSubordinateListPossible(adminUnitsSubordinateListPossible);
					}

				// global save and exit, no validation errors
				if (paramName.equals("SubmitButton") && errors.isEmpty()) {
					System.out.println("Submit, no errors, save and exit");
					// we have to update two tables - AdminUnitType and
					// AdminUnitTypeSubordination
					// save the primary AdminUnitType
					AdminUnitDAO adminUnitDAO = new AdminUnitDAO();
					Integer adminUnitID = adminUnitDAO.save(formData
							.getAdminUnit());
					System.out.println("Updated or new ID for AdminUnit is:"
							+ adminUnitID);

					// update this nits master
					adminUnitDAO.saveMaster(adminUnitID,
						formData.getAdminUnitMaster());
					// update this units subordinates
					// where shall i exit to?								
					request.getRequestDispatcher("mainScreen.jsp").forward(
							request, response);
				}

				// global cancel and exit
				if (paramName.equals("CancelButton")) {
					System.out.println("Cancel, nosave and exit");
					// where shall i exit to?								
					request.getRequestDispatcher("mainScreen.jsp").forward(
							request, response);
				}

			}

		}
		// save the viewmodel for jsp dispatcher into session
		session.setAttribute("formData", formData);

		// call the dispatcher
		request.getRequestDispatcher("AdminUnitMainScreen.jsp").forward(
				request, response);
		
	}
	
	private List<String> getValidationErrors(HttpServletRequest request) {
		List<String> res = new ArrayList<String>();

		// you should also check for unique value
		if ("".equals(request.getParameter("AdminUnitCode"))) {
			res.add("Enter code!");
		}
		if ("".equals(request.getParameter("AdminUnitName"))) {
			res.add("Enter name!");
		}

		return res;
	}

}
