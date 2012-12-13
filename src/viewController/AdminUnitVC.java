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

public class AdminUnitVC extends HttpServlet {
	private static final long serialVersionUID = 1L;       
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// get the session
		HttpSession session = request.getSession();
		// and delete this viewmodel from session, so we can start fresh on each
		// get request
		// normally second requests should come through post
		session.removeAttribute("formData");

		showMainScreen(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		showMainScreen(request, response);		
	}	
	
	protected void showMainScreen (HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// set the encoding, otherwise database is encoded wrongly 
		request.setCharacterEncoding("UTF-8");
				
		HttpSession session = request.getSession();
		AdminUnitVM formData = (AdminUnitVM) session
				.getAttribute("formData");
		
		// if no viewmodel in session, then normally this is first call through
		// get
		// so check get parameters and populate viewmodel with data from dao
		if (formData == null) {
			session.removeAttribute("errors");			
			formData = processGET(request, response);			
		} else {
			// formData was there, so this is post!
			if (cancelWasPressed(request, response)) {
				return;
			}			
			formData = updateViewModelFieldsForDB(request, formData);
			
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
			
			formData = updateUnitsType(request, formData);			
			formData = updateUnitsMaster(request, formData);
			
			// now the tricky part - scan through several possible submit
			// buttons: which button was clicked?
			Enumeration<String> paramNames2 = request.getParameterNames();			
			while (paramNames2.hasMoreElements()) {
				String paramName = paramNames2.nextElement();
				
				if (paramName.startsWith("RemoveButton_")) {
					formData = removeSubordinate(formData, paramName);					
				}
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
		
		//At this point we stay on present screen
		
		// save the viewmodel for jsp dispatcher into session
		session.setAttribute("formData", formData);
		
		// call the dispatcher
		request.getRequestDispatcher("AdminUnitMainScreen.jsp").forward(
				request, response);
	}	
	
	private AdminUnitVM updateUnitsType(HttpServletRequest request,
			AdminUnitVM formData) {
		
		// do we have new (or first set?) adminunittype?
		if (formData.getAdminUnitType() == null ||
				!request.getParameter("AdminUnitType_adminUnitTypeID")
				.equals(formData.getAdminUnitType().getAdminUnitTypeID().toString())) {
			System.out.println("Changing AdminUnitType_adminUnitTypeID");
			
			// record new type to session
			formData.setAdminUnitType(new AdminUnitTypeDAO().getByID(Integer.parseInt(request
					.getParameter("AdminUnitType_adminUnitTypeID"))));
			
			// remove all current subordinates from session
			formData.getAdminUnitsSubordinateListRemoved().addAll(formData.getAdminUnitsSubordinateList());
			formData.getAdminUnitsSubordinateList().clear();
			
			// find new possible subordinates for new unit type
			formData.setAdminUnitsSubordinateListPossible(new AdminUnitDAO()
					.getAllowedSubordinatesByID(formData.getAdminUnitType().getAdminUnitTypeID(), 
							formData.getAdminUnitsSubordinateList()));
			
			// remove current master from session
			formData.getAdminUnitMaster().setAdminUnitID(0);
			
			// find new possible masters for new unit type
			formData.setAdminUnitMasterListWithZero(new AdminUnitDAO()
				.getAllowedMastersByID(formData.getAdminUnitType()
					.getAdminUnitTypeID()), formData.getAdminUnitMaster());
		}
		return formData;
	}

	private boolean cancelWasPressed(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// first find if we want to exit => no validation etc is necessary;
		// we will quit here
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String next = paramNames.nextElement();
			if (next.equals("CancelButton")) {
				System.out.println("Cancel, nosave and exit");
				response.sendRedirect("mainScreen.jsp");
				return true;
			}
		}	
		return false;
	}
	
	private AdminUnitVM updateViewModelFieldsForDB(HttpServletRequest request, AdminUnitVM formData) {
		// lets update the viewmodel with changes the user wants to make
		// trivial stuff: name,code,comment
		formData.getAdminUnit().setCode(
			request.getParameter("AdminUnitCode"));
		formData.getAdminUnit().setName(
			request.getParameter("AdminUnitName"));
		formData.getAdminUnit().setComment(
			request.getParameter("AdminUnitComment"));
		formData.getAdminUnit().setAdminUnitTypeID(
			Integer.parseInt(request.getParameter("AdminUnitType_adminUnitTypeID")));
		
		return formData;
	}
	
	private AdminUnitVM updateUnitsMaster(HttpServletRequest request, AdminUnitVM formData) {
		try {
			// was AdminUnitMaster dropdown changed?
			if (!request
					.getParameter("AdminUnitMaster_adminUnitID")
					.equals(formData.getAdminUnitMaster()
							.getAdminUnitID().toString())) {
				System.out.println("Changing AdminUnitMaster_adminUnitID");
				
				// change viewmodels AdminUnitMaster, find new one from
				// dao
				// based on new id
				dao.AdminUnit newMaster = new AdminUnitDAO().getByID(Integer.parseInt(request
						.getParameter("AdminUnitMaster_adminUnitID")));
				if (newMaster == null) {
					newMaster = new dao.AdminUnit();
					newMaster.setAdminUnitID(0);
				}
				formData.setAdminUnitMaster(newMaster);
				
			}
		} catch (Exception e) {
			System.out.println("Exception:" + e);
		}
		return formData;
	}
	
	private void submitToDataBase(AdminUnitVM formData,
			HttpServletResponse response) throws IOException {
		System.out.println("Submit, no errors, save and exit");
		
		// we have to update two tables - AdminUnit and
		// AdminUnitSubordination
		// save the primary AdminUnit
		AdminUnitDAO adminUnitDAO = new AdminUnitDAO();
		Integer adminUnitID = adminUnitDAO.save(formData
				.getAdminUnit());
		System.out.println("Updated or new ID for AdminUnit is:"
				+ adminUnitID);

		// update this units master
		//if (masterChanged) {
			adminUnitDAO.saveMaster(adminUnitID, formData.getAdminUnitMaster().getAdminUnitID());
		//}
		// update the master for all subordinates (missing jQuery right now)
		for (dao.AdminUnit sub : formData.getAdminUnitsSubordinateList()) {
			adminUnitDAO.saveMaster(sub.getAdminUnitID(), adminUnitID);
		}
		// remove subordination entries for abandoned subordinates
		for (dao.AdminUnit subEx : formData.getAdminUnitsSubordinateListRemoved()) {
			adminUnitDAO.saveMaster(subEx.getAdminUnitID(), 0);
		}
		
		// work is done, back to main screen								
		response.sendRedirect("mainScreen.jsp");
	}

	private AdminUnitVM addSubordinate(AdminUnitVM formData,
			HttpServletRequest request) {
		
		System.out.println("Adding new subordinate");
		// get the id from the post
		Integer listNo = Integer.parseInt(request
			.getParameter("AdminUnit_NewSubordinateNo"));
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
				
		return formData;
	}

	private AdminUnitVM removeSubordinate(AdminUnitVM formData, String paramName) {
		// found the button from the list, get the id
		Integer removeSubLineNo = Integer.parseInt(paramName
				.substring(13));
		System.out.println("Removing from sub list item with id:"
				+ removeSubLineNo);
		// get the list
		List<dao.AdminUnit> adminUnitsSubordinateList = formData
				.getAdminUnitsSubordinateList();
		
		// if we changed unit type then this list will be empty and
		// removed-sub-ord-list has been filled already
		if (adminUnitsSubordinateList.size() > 0) {
			// get the item about to be removed, and insert it into
			// possible sublist; also to removed list
			formData.getAdminUnitsSubordinateListPossible().add(
					adminUnitsSubordinateList
							.get((int) removeSubLineNo.intValue()));
								
			formData.getAdminUnitsSubordinateListRemoved().add(
					adminUnitsSubordinateList
							.get((int) removeSubLineNo.intValue()));
			// remove the item
			adminUnitsSubordinateList.remove((int) removeSubLineNo
					.intValue());
			// put the list back
			formData.setAdminUnitsSubordinateList(adminUnitsSubordinateList);
		}
		
		return formData;
	}

	private AdminUnitVM processGET(HttpServletRequest request,
			HttpServletResponse response) {
		
		Integer adminUnitID = processAndValidateID(request);
		AdminUnitVM formData = populateViewModelWithData(adminUnitID, request);		
		return formData;
	}
	
	private Integer processAndValidateID(HttpServletRequest request) {
		// ID from requestParameter: null - hack, 0-add, >=1 - real id
		
		Integer adminUnitID = null;
		if (request.getParameter("AdminUnitID").equals("new")) {
			// create new instance of AdminUnitType
			adminUnitID = 0;
			System.out.println("Creating new entity.");
		} else {
			try {
				adminUnitID = Integer.parseInt(request.getParameter("AdminUnitID"));
			} catch (Exception e) {
				System.out.println("Exception in parseInt!");
			}
		}

		// check for valid ID
		if (adminUnitID == 0
				|| (adminUnitID >= 0 && new AdminUnitDAO().isIDValid(adminUnitID))) {
			System.out.println("Starting view proccessing for AdminUnit ID:" + adminUnitID);
		} else {
			// throw exception, this is hacking attempt
			throw new RuntimeException(
				"Hacking attempt, this is not valid ID for AdminUnit:"
						+ request.getParameter("AdminUnitID"));
		}
		return adminUnitID;
	}
	
	private AdminUnitVM populateViewModelWithData(Integer adminUnitID, HttpServletRequest request) {
		AdminUnitVM formData = new AdminUnitVM();
		if (adminUnitID == 0) {
		// this is new entity
			formData.setAdminUnit(new dao.AdminUnit());
		} else {
			// get the entity from dao
			formData.setAdminUnit(new AdminUnitDAO()
					.getByID(adminUnitID));
			// get the type of this unit
			formData.setAdminUnitType(new AdminUnitTypeDAO()
					.getByID(formData.getAdminUnit().getAdminUnitTypeID()));
		}
		
		formData.setAdminUnitTypeList(new AdminUnitTypeDAO().getAll());

		// get the master for this AdminUnit
		formData.setAdminUnitMaster(new AdminUnitDAO()
				.getMasterByIDWithZero(formData.getAdminUnit()
				.getAdminUnitID()));
		
		// load the full list of possible AdminUnit masters
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
				
		// initiate the list of subordinates that can be removed
		formData.setAdminUnitsSubordinateListRemoved(new ArrayList<dao.AdminUnit>());
		
		return formData;
	}

	private List<String> getValidationErrors(HttpServletRequest request) {
		List<String> res = new ArrayList<String>();

		// you should also check for unique value
		if ("".equals(request.getParameter("AdminUnitCode"))) {
			res.add("Sisesta kood!");
		}
		if ("".equals(request.getParameter("AdminUnitName"))) {
			res.add("Sisesta nimi!");
		}

		return res;
	}

}
