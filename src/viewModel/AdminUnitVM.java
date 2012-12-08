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
	
	private  List<AdminUnit> adminUnitMasterListWithZero;
	
	private AdminUnitType adminUnitType;
	// list of adminUnitTypes, for choosing new type
	private  List<AdminUnitType> adminUnitTypeList;
			
	// list of adminunits which are subordinates to adminUnit
	private List<AdminUnit> adminUnitsSubordinateList;	
	// list of adminunits which are possible new subordinates to adminUnit
	private List<AdminUnit> adminUnitsSubordinateListPossible;	
	// list of removed subordinates
	private List<AdminUnit> adminUnitsSubordinateListRemoved;


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

	public List<AdminUnit> getAdminUnitMasterListWithZero() {
		return adminUnitMasterListWithZero;
	}
	
	/*
	 * return list with one added record with ID=0 and name="----"
	 * its used in dropdwon to indicate "no selection"
	 */
	public void setAdminUnitMasterListWithZero(
			List<AdminUnit> adminUnitMasterListWithZero, AdminUnit foundMaster) {
		// create new AdminUnitType
		AdminUnit withZero = new AdminUnit();
		// set id to 0
		withZero.setAdminUnitID(0);
		// and name to "---"
		withZero.setName("---");
		// append it to list
		// it goes to last place in list, should go into first!
		adminUnitMasterListWithZero.add(withZero);
		boolean masterPresent = false;
		for (AdminUnit au : adminUnitMasterListWithZero) {
			if (au.getName().equals(foundMaster.getName())) {
				masterPresent = true;
				break;
			}
		}
		
		if(!masterPresent)
			adminUnitMasterListWithZero.add(foundMaster);
		this.adminUnitMasterListWithZero = adminUnitMasterListWithZero;
	}

	public AdminUnitType getAdminUnitType() {
		return adminUnitType;
	}

	public void setAdminUnitType(AdminUnitType adminUnitType) {
		this.adminUnitType = adminUnitType;
	}

	public List<AdminUnit> getAdminUnitsSubordinateListRemoved() {
		return adminUnitsSubordinateListRemoved;
	}

	public void setAdminUnitsSubordinateListRemoved(
			List<AdminUnit> adminUnitsSubordinateListRemoved) {
		this.adminUnitsSubordinateListRemoved = adminUnitsSubordinateListRemoved;
	}
}
