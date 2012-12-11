package viewController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import viewModel.*;
import dao.*;

public class AdminUnitReport extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
	
	protected void showMainScreen(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException, UnsupportedEncodingException {
		// set the encoding, otherwise database is encoded wrongly 
		request.setCharacterEncoding("UTF-8");
				
		HttpSession session = request.getSession();
		AdminUnitReportVM formData = (AdminUnitReportVM) session
				.getAttribute("formData");
		session.removeAttribute("errors");		
		
		// if no viewmodel in session, then normally this is first call through
		// get
		// so check get parameters and populate viewmodel with data from dao
		if (formData == null) {			
			Integer adminUnitTypeID = processAndValidateID(request);
			if (adminUnitTypeID == null) {
				adminUnitTypeID = 1;
			}
			formData = populateViewModelWithData(request, response, adminUnitTypeID);			
		} else {
			if (BackButtonWasPressed(request, response)) {
				return;
			}
			
			if (RefreshButtonWasPressed(request)) {
				Boolean newDataNeeded = false;
				Integer newAdminUnitTypeID = formData.getAdminUnitType().getAdminUnitTypeID();
				
				if (adminUnitTypeHasChanged(formData, request)) {
					newAdminUnitTypeID = Integer.parseInt(request
							.getParameter("AdminUnitType_adminUnitTypeID"));
					formData.setAdminUnitType(new AdminUnitTypeDAO().getByID(newAdminUnitTypeID));
					newDataNeeded = true;					
				}
				
				if (searchDateHasChanged(formData, request)) {
					formData = setCustomDate(formData, request);
					newDataNeeded = true;
				}
				
				if (newDataNeeded) {
					formData = setUnitTypeSpecifics(formData, newAdminUnitTypeID);
				}
			}
		}
		
		// save the viewmodel for jsp dispatcher into session
		session.setAttribute("formData", formData);
				
		// call the dispatcher
		request.getRequestDispatcher("AdminUnitReportMainScreen.jsp").forward(
				request, response);
	}
	
	private boolean searchDateHasChanged(AdminUnitReportVM formData,
			HttpServletRequest request) {
		
		String newDateString = request.getParameter("SearchDate");
		if(!isValidDateString(newDateString)) {
			request.getSession().setAttribute("errors", "Sisesta kuupäev kujul pp.kk.aaaa");
			return false;
		}
		
		if (!newDateString.equals(formData.getAdminUnitType()
				.getAdminUnitTypeID())) {
			return true;
		}
		
		return false;
	}

	// source: http://www.javadb.com/check-if-a-string-is-a-valid-date
	private boolean isValidDateString(String dateString) {
		if (dateString == null)
		      return false;

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		    
		if (dateString.trim().length() != dateFormat.toPattern().length())
		      return false;
		dateFormat.setLenient(false);

		// need to format the datestring from dd.mm.yyyy -> yyyy-mm-dd 
		// for dateformat to work correctly
		String formattedDateString = reFormat(dateString);
				    
		try {
		    //parse the parameter to date
			dateFormat.parse(formattedDateString.trim());
		} catch (ParseException pe) {			
			return false;
		}
		return true;
	}

	private String reFormat(String dateString) {
		StringBuilder sb = new StringBuilder(dateString.substring(6));
		sb.append("-");
		sb.append(dateString.substring(3,5));
		sb.append("-");
		sb.append(dateString.substring(0,2));
		return sb.toString();
	}

	private Boolean adminUnitTypeHasChanged(AdminUnitReportVM formData, HttpServletRequest request) {
		
		try {
			// was AdminUnitType dropdown changed?
			if (!request
					.getParameter("AdminUnitType_adminUnitTypeID")
					.equals(formData.getAdminUnitType()
							.getAdminUnitTypeID().toString())) {
				System.out.println("Changing AdminUnitMaster_adminUnitID");
				return true;
			}
		} catch (Exception e) {
			System.out.println("Exception:" + e);
		}
		return false;
	}

	private AdminUnitReportVM setCustomDate(AdminUnitReportVM formData,
			HttpServletRequest request) {
		
		String newDateString = request.getParameter("SearchDate");
		formData.setSearchDate(newDateString);
		
		return formData;
	}

	private boolean RefreshButtonWasPressed(HttpServletRequest request) {
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String next = paramNames.nextElement();
			if (next.equals("RefreshButton")) {
				System.out.println("Going to refresh view");
				return true;
			}
		}	
		return false;
	}

	private Integer processAndValidateID(HttpServletRequest request) {
		Integer adminUnitTypeID = null;
		try {
			adminUnitTypeID = Integer.parseInt(request.getParameter("AdminUnitTypeID"));
		} catch (Exception e) {
			System.out.println("Trouble with parsing ID");
		}
		
		// if anybody enters with 0 we will not be angry but send him to 1		
		if (adminUnitTypeID == 0) {
			adminUnitTypeID = 1;
		}
		
		if (adminUnitTypeID > 0 && new AdminUnitTypeDAO().isIDValid(adminUnitTypeID)) {
			System.out.println("Starting view proccessing for AdminUnitType ID:" + adminUnitTypeID);
		} else {
			// throw exception, this is hacking attempt
			throw new RuntimeException(
				"Hacking attempt, this is not valid ID for AdminUnitType:"
						+ request.getParameter("AdminUnitTypeID"));
		}
		return adminUnitTypeID;
	}

	private boolean BackButtonWasPressed(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String next = paramNames.nextElement();
			if (next.equals("BackButton")) {
				System.out.println("Going back to main screen");
				response.sendRedirect("mainScreen.jsp");
				return true;
			}
		}	
		return false;
	}

	protected AdminUnitReportVM populateViewModelWithData(HttpServletRequest request, HttpServletResponse response,
			Integer adminUnitTypeID) {
		AdminUnitReportVM formData = new AdminUnitReportVM();	       
		formData.setAdminUnitTypeList(new AdminUnitTypeDAO().getAll());	
		formData = initializeDate(formData);
		formData = setUnitTypeSpecifics(formData, adminUnitTypeID); 	
		
		return formData;
	}

	private AdminUnitReportVM setUnitTypeSpecifics(AdminUnitReportVM formData, Integer adminUnitTypeID) {
		
		formData.setAdminUnitType(new AdminUnitTypeDAO().getByID(adminUnitTypeID));
		formData.setAdminUnitMasterList(new AdminUnitDAO().getByAdminUnitTypeID(adminUnitTypeID));
		for (dao.AdminUnit au : formData.getAdminUnitMasterList()) {
			au.setAdminUnitSubordinatesList(new AdminUnitDAO().getSubordinates(au.getAdminUnitID()));
			
			// for extra information to be displayed at Lookbutton click
			for (dao.AdminUnit sub : au.getAdminUnitSubordinatesList()) {
				sub.setAdminUnitTypeString(new AdminUnitTypeDAO().getByID(sub.getAdminUnitTypeID()).getName());
				sub.setAdminUnitSubordinatesList(new AdminUnitDAO().getSubordinates(sub.getAdminUnitID()));
			}
		}
		return formData;
	}

	private AdminUnitReportVM initializeDate(AdminUnitReportVM formData) {

		Calendar today = Calendar.getInstance();
		String dateString = today.get(Calendar.DATE) + "." + (today.get(Calendar.MONTH) + 1) + "." + today.get(Calendar.YEAR);		
        formData.setSearchDate(dateString);
        
        return formData;
	}

}
