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
 * Servlet implementation class AdminUnitTypeTreeViewVC
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
					AdminUnitType adminUnitType = adminUnitTypeDAO.getByID(1);
					AdminUnitTypeJSON t = new AdminUnitTypeJSON();
					
					if (adminUnitType != null) {
						t.setText(adminUnitType.getName());
						t.setExpanded(false);
						t.setId(adminUnitType.getAdminUnitTypeID().toString());
						t.setHasChildren(adminUnitTypeDAO.getSubordinateCount(adminUnitType)>=1);
						t.setHasChildren(true);
					}

					children.add(t);
					
				} else {
					//load the list of childrens
					for (AdminUnitType adminUnitType:adminUnitTypeDAO.getSubordinates(Integer.parseInt(request.getParameter("root")), "NOW()")){
						AdminUnitTypeJSON tempUnit = new AdminUnitTypeJSON();
						tempUnit.setText(adminUnitType.getName());
						tempUnit.setExpanded(false);
						tempUnit.setId(adminUnitType.getAdminUnitTypeID().toString());
						// TODO find out, does this unit has children. dont use fixed value
						tempUnit.setHasChildren(adminUnitTypeDAO.getSubordinateCount(adminUnitType)>=1);
						children.add(tempUnit);
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

}
