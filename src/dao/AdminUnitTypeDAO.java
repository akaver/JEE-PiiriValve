package dao;

import java.util.*;
import java.sql.*;
import org.apache.commons.dbutils.DbUtils;

public class AdminUnitTypeDAO extends DAO {
	public List<AdminUnitType> getAll() {
		List<AdminUnitType> res = null;

		try {
			Statement statement = super.getConnection().createStatement();
			ResultSet resultSet = statement
					.executeQuery("select * from AdminUnitType");

			res = new ArrayList<AdminUnitType>();

			while (resultSet.next()) {
				res.add(createAdminUnitTypeFromResultSet(resultSet));
			}

			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(statement);

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
		}
		return res;
	}

	public AdminUnitType getByID(Integer adminUnitTypeID) {
		AdminUnitType res = null;

		System.out.println("adminUnitType getByID:" + adminUnitTypeID);

		String sql = "select * from AdminUnitType where AdminUnitTypeID=?";
		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			preparedStatement.setInt(1, adminUnitTypeID);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				res = createAdminUnitTypeFromResultSet(resultSet);
			}
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(preparedStatement);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
		}
		return res;
	}

	// find and return AdminUnitType's master - if any
	public AdminUnitType getMasterByID(Integer adminUnitTypeID) {
		System.out.println("Finding master AdminUnitType for: "
				+ adminUnitTypeID);

		if (adminUnitTypeID == null) {
			return null;
		}

		Integer masterID = null;

		// find the subordinate record, which contains its masters id
		String sql = "select * from AdminUnitTypeSubordination where SubordinateAdminUnitTypeID=?";
		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			preparedStatement.setInt(1, adminUnitTypeID);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				masterID = resultSet.getInt("AdminUnitTypeID");
			}
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(preparedStatement);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
		}

		if (masterID != null) {
			// get the adminunittype record based on subtybe masters id
			return getByID(masterID);
		}

		return null;
	}

	public AdminUnitType getMasterByIDWithZero(Integer adminUnitTypeID) {
		System.out.println("Finding master AdminUnitType with zero for: "
				+ adminUnitTypeID);

		if (adminUnitTypeID == null) {
			return null;
		}

		AdminUnitType res = getMasterByID(adminUnitTypeID);

		if (res == null) {
			res = new AdminUnitType();
			res.setAdminUnitTypeID(0);
			res.setName("---");
		}

		return res;

	}

	public List<AdminUnitType> getSubordinates(Integer adminUnitTypeID) {
		System.out.println("Finding subordinates for adminUnitType with ID:"
				+ adminUnitTypeID);

		if (adminUnitTypeID == null) {
			return null;
		}
		List<AdminUnitType> res = new ArrayList<AdminUnitType>();

		// get the list of subordinate ID's
		String sql = "select * from AdminUnitTypeSubordination where AdminUnitTypeID=?";
		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			preparedStatement.setInt(1, adminUnitTypeID);
			ResultSet resultSet = preparedStatement.executeQuery();
			// find the record from AdminUnitType and insert into result
			while (resultSet.next()) {
				Integer subid = resultSet.getInt("SubordinateAdminUnitTypeID");
				System.out.println("Fetching subordinate with ID:" + subid);
				res.add(getByID(subid));
			}

			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(preparedStatement);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
		}

		return res;
	}

	private AdminUnitType createAdminUnitTypeFromResultSet(ResultSet rs)
			throws SQLException {
		AdminUnitType res = new AdminUnitType();
		res.setAdminUnitTypeID(rs.getInt("AdminUnitTypeID"));
		res.setCode(rs.getString("Code"));
		res.setName(rs.getString("Name"));
		res.setComment(rs.getString("Comment"));
		// standard fields
		res.setFromDate(rs.getDate("FromDate"));
		res.setToDate(rs.getDate("ToDate"));
		res.setOpenedBy(rs.getString("OpenedBy"));
		res.setOpenedDate(rs.getDate("OpenedDate"));
		res.setChangedBy(rs.getString("ChangedBy"));
		res.setChangedDate(rs.getDate("ChangedDate"));
		res.setClosedBy(rs.getString("ClosedBy"));
		res.setClosedDate(rs.getDate("ClosedDate"));

		return res;
	}

	public AdminUnitTypeDAO() {
		super();
	}

	public boolean isIDValid(Integer adminUnitTypeID) {
		System.out.println("adminUnitType isIDValidD:" + adminUnitTypeID);

		Boolean res = false;

		String sql = "select * from AdminUnitType where AdminUnitTypeID=?";
		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			preparedStatement.setInt(1, adminUnitTypeID);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				res = true;
			}
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(preparedStatement);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
		}

		return res;
	}

	public List<AdminUnitType> getPossibleSubordinates(Integer adminUnitTypeID) {
		System.out.println("Possible subordinates for AdminUnitTypeID:"+adminUnitTypeID);
		
		List<AdminUnitType> res = new ArrayList<AdminUnitType>();
		
		if (adminUnitTypeID==null){
			adminUnitTypeID = 0;
		}
		// return the list of possible subordinates for this adminUnit
		// all units without any master set and excluding itself and itself's master
		// pluss all the units which where removed from the list on the form
		// (but not yet saved to db as removed)

		// this is complicated......
		
		// list of all AdminUnitTypeID's
		// without current AdminUnitTypeID
		// without master currently set
		// and without record no 1 - the first semifixed unit - the state
		String sql = "select AdminUnitType.AdminUnitTypeID as ID1 "+
				"from AdminUnitType LEFT JOIN AdminUnitTypeSubordination ON AdminUnitType.AdminUnitTypeID=AdminUnitTypeSubordination.SubordinateAdminUnitTypeID "+
				"where "+
				"AdminUnitType.AdminUnitTypeID<>1 and "+ //id 1  is state - it cannot be subordinate
				"AdminUnitType.AdminUnitTypeID<>? "+
				"and "+ 
				"ISNULL(AdminUnitTypeSubordination.AdminUnitTypeID,0)=0 "+
				"";
		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			preparedStatement.setInt(1, adminUnitTypeID);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				System.out.println("sub: "+resultSet.getInt("ID1"));
				res.add(getByID(resultSet.getInt("ID1")));
			}
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(preparedStatement);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
		}
		
		return res;
	}

}
