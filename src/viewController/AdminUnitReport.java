package viewController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

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
		
		// if no viewmodel in session, then normally this is first call through
		// get
		// so check get parameters and populate viewmodel with data from dao
		if (formData == null) {
			formData = populateViewModelWithData(request, response, 4);			
		} else {
			// TODO fantastic stuff with post request
		}
		// save the viewmodel for jsp dispatcher into session
		session.setAttribute("formData", formData);
				
		// call the dispatcher
		request.getRequestDispatcher("AdminUnitReportMainScreen.jsp").forward(
				request, response);
	}
	
	protected AdminUnitReportVM populateViewModelWithData(HttpServletRequest request, HttpServletResponse response,
			Integer id) {
		AdminUnitReportVM formData = new AdminUnitReportVM();
		formData.setSearchDate(new Date());
		formData.setAdminUnitTypeList(new AdminUnitTypeDAO().getAll());
		formData.setAdminUnitType(new AdminUnitTypeDAO().getByID(id));
		formData.setAdminUnitMasterList(new AdminUnitDAO().getByAdminUnitTypeID(id));
		for (dao.AdminUnit au : formData.getAdminUnitMasterList()) {
			au.setAdminUnitSubordinatesList(new AdminUnitDAO().getSubordinates(au.getAdminUnitID()));
		}
		
		return formData;
	}

}
