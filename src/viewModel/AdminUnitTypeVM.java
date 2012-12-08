package viewModel;

import java.util.*;
import dao.*;

public class AdminUnitTypeVM {
	// adminunittype we are editing
	private AdminUnitType adminUnitType;
	
	// adminunittype wich is master for adminUnitType
	private  AdminUnitType adminUnitTypeMaster;
	// list of adminUnitTypes, for dropdown
	private  List<AdminUnitType> adminUnitTypeMasterList;
	private  List<AdminUnitType> adminUnitTypeMasterListWithZero;
	
	// list of adminunitypes which are subordinates to adminUnitType
	private List<AdminUnitType> adminUnitTypesSubordinateList;
	
	// list of adminunitypes which are possible new subordinates to adminUnitType
	private List<AdminUnitType> adminUnitTypesSubordinateListPossible;
	
	

	public AdminUnitType getAdminUnitType() {
		return adminUnitType;
	}

	public void setAdminUnitType(AdminUnitType adminUnitType) {
		this.adminUnitType = adminUnitType;
	}

	public AdminUnitType getAdminUnitTypeMaster() {
		return adminUnitTypeMaster;
	}

	public void setAdminUnitTypeMaster(AdminUnitType adminUnitTypeMaster) {
		this.adminUnitTypeMaster = adminUnitTypeMaster;
	}

	public List<AdminUnitType> getAdminUnitTypeMasterList() {
		return adminUnitTypeMasterList;
	}

	public void setAdminUnitTypeMasterList(
			List<AdminUnitType> adminUnitTypeMasterList) {
		this.adminUnitTypeMasterList = adminUnitTypeMasterList;
	}

	public List<AdminUnitType> getAdminUnitTypesSubordinateList() {
		return adminUnitTypesSubordinateList;
	}

	public void setAdminUnitTypesSubordinateList(
			List<AdminUnitType> adminUnitTypesSubordinateList) {
		this.adminUnitTypesSubordinateList = adminUnitTypesSubordinateList;
	}

	public List<AdminUnitType> getAdminUnitTypeMasterListWithZero() {
		return adminUnitTypeMasterListWithZero;
	}

	/*
	 * return list with one added record with ID=0 and name="----"
	 * its used in dropdwon to indicate "no selection"
	 */
	public void setAdminUnitTypeMasterListWithZero(
			List<AdminUnitType> adminUnitTypeMasterListWithZero) {
		// create new AdminUnitType
		AdminUnitType withZero = new AdminUnitType();
		// set id to 0
		withZero.setAdminUnitTypeID(0);
		// and name to "---"
		withZero.setName("---");
		// append it to list
		// it goes to last place in list, should go into first!
		adminUnitTypeMasterListWithZero.add(withZero);
		this.adminUnitTypeMasterListWithZero = adminUnitTypeMasterListWithZero;
	}

	public List<AdminUnitType> getAdminUnitTypesSubordinateListPossible() {
		return adminUnitTypesSubordinateListPossible;
	}

	public void setAdminUnitTypesSubordinateListPossible(
			List<AdminUnitType> adminUnitTypesSubordinateListPossible) {
		this.adminUnitTypesSubordinateListPossible = adminUnitTypesSubordinateListPossible;
	}

}
