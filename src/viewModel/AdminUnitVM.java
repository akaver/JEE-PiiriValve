package viewModel;

import java.util.*;

import dao.*;

public class AdminUnitVM {
	// adminunit we are editing
	private AdminUnit adminUnit;
	
	// adminunit wich is master for adminUnit
	private AdminUnit adminUnitMaster;
	// list of possible new masters for adminUnit
	private List<AdminUnit> adminUnitMastersListPossible;
	
	// list of adminUnitTypes, for choosing new type
	private  List<AdminUnitType> adminUnitTypeList;
	////trying to avoid that = maybe trying to avoid new dropdown...
	//private  List<AdminUnitType> adminUnitTypeMasterListWithZero;
	
	// list of adminunits which are subordinates to adminUnit
	private List<AdminUnit> adminUnitsSubordinateList;	
	// list of adminunis which are possible new subordinates to adminUnit
	private List<AdminUnit> adminUnitsSubordinateListPossible;	


	public AdminUnit getAdminUnit() {
		return adminUnit;
	}

	public void setAdminUnit(AdminUnit adminUnit) {
		this.adminUnit = adminUnit;
	}

	public AdminUnit getAdminUnitMaster() {
		return adminUnitMaster;
	}

	public void setAdminUnitMaster(AdminUnit adminUnitMaster) {
		this.adminUnitMaster = adminUnitMaster;
	}

	public List<AdminUnit> getAdminUnitMastersListPossible() {
		return adminUnitMastersListPossible;
	}

	public void setAdminUnitMastersListPossible(
			List<AdminUnit> adminUnitMastersListPossible) {
		this.adminUnitMastersListPossible = adminUnitMastersListPossible;
	}

	public List<AdminUnitType> getAdminUnitTypeList() {
		return adminUnitTypeList;
	}

	public void setAdminUnitTypeList(List<AdminUnitType> adminUnitTypeList) {
		this.adminUnitTypeList = adminUnitTypeList;
	}

	public List<AdminUnit> getAdminUnitsSubordinateList() {
		return adminUnitsSubordinateList;
	}

	public void setAdminUnitsSubordinateList(
			List<AdminUnit> adminUnitsSubordinateList) {
		this.adminUnitsSubordinateList = adminUnitsSubordinateList;
	}

	public List<AdminUnit> getAdminUnitsSubordinateListPossible() {
		return adminUnitsSubordinateListPossible;
	}

	public void setAdminUnitsSubordinateListPossible(
			List<AdminUnit> adminUnitsSubordinateListPossible) {
		this.adminUnitsSubordinateListPossible = adminUnitsSubordinateListPossible;
	}

	////trying to avoid that = maybe trying to avoid new dropdown...
	/*
	 * return list with one added record with ID=0 and name="----"
	 * its used in dropdwon to indicate "no selection"
	 
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
	}*/
}
