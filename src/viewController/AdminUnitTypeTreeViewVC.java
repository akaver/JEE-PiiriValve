package viewController;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import dao.*;

/**
 * json output for treeview
 */
public class AdminUnitTypeTreeViewVC extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("AdminUnitTypeTreeViewVC get");

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		AdminUnitTypeDAO adminUnitTypeDAO = new AdminUnitTypeDAO();
		Collection<AdminUnitTypeJSON> children = new ArrayList<AdminUnitTypeJSON>();

		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			System.out.println("get parameters:" + paramName + " = "
					+ request.getParameter(paramName));

			if (paramName.equals("root")) {
				if (request.getParameter("root").equals("source")) {

					children.add(saveToJSONType(adminUnitTypeDAO.getByID(1),
							adminUnitTypeDAO
									.getSubordinateCount(adminUnitTypeDAO
											.getByID(1)) >= 1));
				} else {
					// load the list of childrens
					for (AdminUnitType adminUnitType : adminUnitTypeDAO
							.getSubordinates(Integer.parseInt(request
									.getParameter("root")), "NOW()")) {

						children.add(saveToJSONType(
								adminUnitType,
								adminUnitTypeDAO
										.getSubordinateCount(adminUnitType) >= 1));
					}

				}
			}

		}

		out.println(gson.toJson(children));

		out.flush();

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("AdminUnitTypeTreeViewVC post");
	}

	private AdminUnitTypeJSON saveToJSONType(AdminUnitType adminUnitType,
			Boolean hasChildren) {
		AdminUnitTypeJSON res = new AdminUnitTypeJSON();

		res.setText(adminUnitType.getName());
		res.setExpanded(false);
		res.setId(adminUnitType.getAdminUnitTypeID().toString());
		res.setHasChildren(hasChildren);

		return res;
	}
}
