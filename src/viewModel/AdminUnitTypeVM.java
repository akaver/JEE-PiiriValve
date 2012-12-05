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

	public void setAdminUnitTypeMasterListWithZero(
			List<AdminUnitType> adminUnitTypeMasterListWithZero) {
		AdminUnitType withZero = new AdminUnitType();
		withZero.setAdminUnitTypeID(0);
		withZero.setName("---");
		adminUnitTypeMasterListWithZero.add(withZero);
		this.adminUnitTypeMasterListWithZero = adminUnitTypeMasterListWithZero;
	}



}
