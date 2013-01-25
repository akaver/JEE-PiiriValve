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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// Lets draw the main entry screen
		ShowMainScreen(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// admin unit possible post variants (edit, new, report)
		try {
			if (!request.getParameter("ViewAdminUnit").isEmpty()) {
				System.out.println("Redirecting ViewAdminUnit");
				response.sendRedirect("AdminUnitVC?AdminUnitID="
						+ request.getParameter("AdminUnitID"));
			}

		} catch (Exception e) {
		}
		try {
			if (!request.getParameter("AddAdminUnit").isEmpty()) {
				System.out.println("Redirecting AddAdminUnit");
				response.sendRedirect("AdminUnitVC?AdminUnitID=0");
			}

		} catch (Exception e) {
		}
		try {
			if (!request.getParameter("ReportAdminUnit").isEmpty()) {
				System.out.println("Redirecting AdminUnitReportVC");
				response.sendRedirect("AdminUnitReportVC?AdminUnitID="
						+ request.getParameter("AdminUnitID"));
			}

		} catch (Exception e) {
		}
		
		// admin unit type possible post variants (edit, new, report)
		try {
			if (!request.getParameter("ViewAdminUnitType").isEmpty()) {
				System.out.println("Redirecting ViewAdminUnitType");
				response.sendRedirect("AdminUnitTypeVC?AdminUnitTypeID="
						+ request.getParameter("AdminUnitTypeID"));
			}

		} catch (Exception e) {
		}
		try {
			if (!request.getParameter("AddAdminUnitType").isEmpty()) {
				System.out.println("Redirecting AddAdminUnitType");
				response.sendRedirect("AdminUnitTypeVC?AdminUnitTypeID=0");
			}

		} catch (Exception e) {
		}
		try {
			if (!request.getParameter("ReportAdminUnitType").isEmpty()) {
								
				// find the entry point to report: the unit type of the
				// unit that is currently selected at the units dropdown
				Integer adminUnitTypeID = findAdminUnitType(request);						
				
				System.out.println("Redirecting AdminUnitTypeReportVC");
				response.sendRedirect("AdminUnitTypeReportVC?AdminUnitTypeID="
						+ adminUnitTypeID);
			}

		} catch (Exception e) {
		}
	}

	// when entering units report, find the adminunittype we need to use as entry
	// point; this the type of the unit currently selected at dropdown
	private Integer findAdminUnitType(HttpServletRequest request) {
		String adminUnitIDAsString = request.getParameter("AdminUnitID");
		Integer adminUnitID = Integer.parseInt(adminUnitIDAsString);
		AdminUnit au = new AdminUnitDAO().getByID(adminUnitID);
		return au.getAdminUnitTypeID();
	}

	private void ShowMainScreen(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
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
		request.getRequestDispatcher("IndexScreen.jsp").forward(request,
				response);
	}
}
