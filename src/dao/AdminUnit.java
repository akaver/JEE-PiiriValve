package dao;

import java.util.Date;
import java.util.List;

public class AdminUnit {
	private Integer adminUnitID;
	private String code;
	private String name;
	private String comment;
	private Integer adminUnitTypeID;
	private Date fromDate;
	private Date toDate;
	private String openedBy;
	private Date openedDate;
	private String changedBy;
	private Date changedDate;
	private String closedBy;
	private Date closedDate;
	
	//custom properties to be used in view controller -> jsp
	private List<AdminUnit> adminUnitSubordinatesList;
	private String adminUnitTypeString;
	
	public Integer getAdminUnitID() {
		return adminUnitID;
	}
	public void setAdminUnitID(Integer adminUnitID) {
		this.adminUnitID = adminUnitID;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Integer getAdminUnitTypeID() {
		return adminUnitTypeID;
	}
	public void setAdminUnitTypeID(Integer adminUnitTypeID) {
		this.adminUnitTypeID = adminUnitTypeID;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public String getOpenedBy() {
		return openedBy;
	}
	public void setOpenedBy(String openedBy) {
		this.openedBy = openedBy;
	}
	public Date getOpenedDate() {
		return openedDate;
	}
	public void setOpenedDate(Date openedDate) {
		this.openedDate = openedDate;
	}
	public String getChangedBy() {
		return changedBy;
	}
	public void setChangedBy(String changedBy) {
		this.changedBy = changedBy;
	}
	public Date getChangedDate() {
		return changedDate;
	}
	public void setChangedDate(Date changedDate) {
		this.changedDate = changedDate;
	}
	public String getClosedBy() {
		return closedBy;
	}
	public void setClosedBy(String closedBy) {
		this.closedBy = closedBy;
	}
	public Date getClosedDate() {
		return closedDate;
	}
	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
	}
	
	@Override
	public String toString() {
		return "ID: " + adminUnitID + "   " + "Code: " + code + "   "
				+ "Name: " + name;
	}
	public List<AdminUnit> getAdminUnitSubordinatesList() {
		return adminUnitSubordinatesList;
	}
	public void setAdminUnitSubordinatesList(
			List<AdminUnit> adminUnitSubordinatesList) {
		this.adminUnitSubordinatesList = adminUnitSubordinatesList;
	}
	public String getAdminUnitTypeString() {
		return adminUnitTypeString;
	}
	public void setAdminUnitTypeString(String adminUnitTypeString) {
		this.adminUnitTypeString = adminUnitTypeString;
	}
}
