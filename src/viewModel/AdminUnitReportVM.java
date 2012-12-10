package viewModel;

import java.util.List;
import dao.*;

public class AdminUnitReportVM {
	// the date on which subordinates had to be present
	private String searchDate;
	
	// the current unit type
	private AdminUnitType adminUnitType;
	
	// list of adminUnitTypes, for choosing new type
	private  List<AdminUnitType> adminUnitTypeList;
	
	// list of adminunits that we have chosen
	private List<AdminUnit> adminUnitMasterList;

	public AdminUnitType getAdminUnitType() {
		return adminUnitType;
	}

	public void setAdminUnitType(AdminUnitType adminUnitType) {
		this.adminUnitType = adminUnitType;
	}

	public List<AdminUnitType> getAdminUnitTypeList() {
		return adminUnitTypeList;
	}

	public void setAdminUnitTypeList(List<AdminUnitType> adminUnitTypeList) {
		this.adminUnitTypeList = adminUnitTypeList;
	}

	public List<AdminUnit> getAdminUnitMasterList() {
		return adminUnitMasterList;
	}

	public void setAdminUnitMasterList(List<AdminUnit> adminUnitMasterList) {
		this.adminUnitMasterList = adminUnitMasterList;
	}

	public String getSearchDate() {
		return searchDate;
	}

	public void setSearchDate(String searchDate) {
		this.searchDate = searchDate;
	}

}
