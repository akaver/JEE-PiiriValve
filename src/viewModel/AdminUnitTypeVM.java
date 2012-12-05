package viewModel;

import java.util.*;
import dao.*;

public class AdminUnitTypeVM {
	private AdminUnitType adminUnitType;

	private Integer adminUnitTypeMasterID;
	private List<AdminUnitType> adminUnitTypeMaster;
	
	private List<AdminUnitType> adminUnitTypeSubordinates;

	public AdminUnitType getAdminUnitType() {
		return adminUnitType;
	}

	public void setAdminUnitType(AdminUnitType adminUnitType) {
		this.adminUnitType = adminUnitType;
	}

	public Integer getAdminUnitTypeMasterID() {
		return adminUnitTypeMasterID;
	}

	public void setAdminUnitTypeMasterID(Integer adminUnitTypeMasterID) {
		this.adminUnitTypeMasterID = adminUnitTypeMasterID;
	}

	public List<AdminUnitType> getAdminUnitTypeMaster() {
		return adminUnitTypeMaster;
	}

	public void setAdminUnitTypeMaster(List<AdminUnitType> adminUnitTypeMaster) {
		this.adminUnitTypeMaster = adminUnitTypeMaster;
	}

	public List<AdminUnitType> getAdminUnitTypeSubordinates() {
		return adminUnitTypeSubordinates;
	}

	public void setAdminUnitTypeSubordinates(
			List<AdminUnitType> adminUnitTypeSubordinates) {
		this.adminUnitTypeSubordinates = adminUnitTypeSubordinates;
	}
}
