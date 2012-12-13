package dao;

import java.util.*;
import java.sql.*;

import org.apache.commons.dbutils.DbUtils;

public class AdminUnitDAO extends DAO {

	public AdminUnitDAO() {
		super();
	}

	public List<AdminUnit> getAll() {
		List<AdminUnit> res = null;

		try {
			Statement statement = super.getConnection().createStatement();
			ResultSet resultSet = statement
					.executeQuery("select * from AdminUnit where "
							+ "OpenedDate < NOW() and ClosedDate > NOW() and FromDate < NOW() and ToDate > NOW() ");

			res = new ArrayList<AdminUnit>();

			while (resultSet.next()) {
				res.add(createAdminUnitFromResultSet(resultSet));
			}

			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(statement);

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
		}
		return res;
	}

	public AdminUnit getByID(Integer adminUnitID) {
		return getByID(adminUnitID, "NOW()");
	}

	public AdminUnit getByID(Integer adminUnitID, String dateString) {
		AdminUnit res = null;
		System.out.println("adminUnit getByID:" + adminUnitID);

		String dateLimits = "";

		// if we search for NOW, entry must opened and valid
		if (dateString.equals("NOW()")) {
			dateLimits = " and OpenedDate < " + dateString
					+ " and ClosedDate > " + dateString + " and FromDate < "
					+ dateString + " and ToDate > " + dateString;
		}
		// if we search for custom date, entry must have been valid THEN
		else {
			dateLimits = " and FromDate < DATE '" + dateString
					+ "' and ToDate > DATE '" + dateString + "'";
		}

		String sql = "select * from AdminUnit where AdminUnitID=?" + dateLimits;
		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			preparedStatement.setInt(1, adminUnitID);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				res = createAdminUnitFromResultSet(resultSet);
			}
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(preparedStatement);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
		}
		return res;
	}

	// find and return AdminUnit's master - if any
	public AdminUnit getMasterByID(Integer adminUnitID) {
		System.out.println("Finding master AdminUnit for: " + adminUnitID);

		if (adminUnitID == null) {
			return null;
		}

		Integer masterID = null;

		// find the subordinate record, which contains its masters id
		String sql = "select * from AdminUnitSubordination where SubordinateAdminUnitID=?"
				+ "and OpenedDate < NOW() and ClosedDate > NOW() and FromDate < NOW() and ToDate > NOW() ";
		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			preparedStatement.setInt(1, adminUnitID);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				masterID = resultSet.getInt("AdminUnitID");
			} else {
				System.out.println("Master not found!");
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

	public AdminUnit getMasterByIDWithZero(Integer adminUnitID) {
		System.out.println("Finding master AdminUnit with zero for: "
				+ adminUnitID);

		if (adminUnitID == null) {
			AdminUnit emptyMaster = new AdminUnit();
			emptyMaster.setAdminUnitID(0);
			return emptyMaster;
		}

		AdminUnit res = getMasterByID(adminUnitID);

		if (res == null) {
			res = new AdminUnit();
			res.setAdminUnitID(0);
			res.setName("---");
		}

		return res;
	}

	// for populating list of possible masters to choose from; needs to know the
	// current
	// adminUnitType of adminUnit. must be read from session because it can
	// be changed during session
	public List<AdminUnit> getAllowedMastersByID(Integer adminUnitTypeID) {
		System.out.println("Finding allowed master AdminUnits for: "
				+ adminUnitTypeID);

		if (adminUnitTypeID == null) {
			return null;
		}

		List<AdminUnit> res = new ArrayList<AdminUnit>();

		// find all possible masters
		String sql = "select * from AdminUnit where AdminUnitTypeID in "
				+ "(select AdminUnitTypeID from AdminUnitTypeSubordination "
				+ "where SubordinateAdminUnitTypeID=? "
				+ "and OpenedDate < NOW() and ClosedDate > NOW() and FromDate < NOW() and ToDate > NOW() )";

		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			preparedStatement.setInt(1, adminUnitTypeID);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				res.add(createAdminUnitFromResultSet(resultSet));
			}
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(preparedStatement);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
		}

		return res;
	}

	public List<AdminUnit> getSubordinates(Integer adminUnitID) {
		return getSubordinates(adminUnitID, "NOW()");
	}

	public List<AdminUnit> getSubordinates(Integer adminUnitID,
			String dateString) {
		System.out.println("Finding subordinates for adminUnit with ID:"
				+ adminUnitID);

		if (adminUnitID == null) {
			return new ArrayList<AdminUnit>();
		}
		List<AdminUnit> res = new ArrayList<AdminUnit>();

		String dateLimits = "";

		// if we search for NOW, entry must opened and valid
		if (dateString.equals("NOW()")) {
			dateLimits = " and OpenedDate < " + dateString
					+ " and ClosedDate > " + dateString + " and FromDate < "
					+ dateString + " and ToDate > " + dateString;
		}
		// if we search for custom date, entry must have been valid THEN
		else {
			dateLimits = " and FromDate < DATE '" + dateString
					+ "' and ToDate > DATE '" + dateString + "'";
		}

		// get the list of subordinate ID's
		String sql = "select * from AdminUnitSubordination where AdminUnitID=? "
				+ dateLimits;
		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			preparedStatement.setInt(1, adminUnitID);
			ResultSet resultSet = preparedStatement.executeQuery();
			// find the record from AdminUnit and insert into result
			while (resultSet.next()) {
				Integer subid = resultSet.getInt("SubordinateAdminUnitID");
				if (subid != adminUnitID) {
					System.out.println("Fetching subordinate with ID:" + subid);
					res.add(getByID(subid, dateString));
				}
			}

			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(preparedStatement);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
		}

		return res;
	}

	// for populating list of possible subordinates; needs to know the current
	// adminUnitType of adminUnit
	public List<AdminUnit> getAllowedSubordinatesByID(Integer adminUnitTypeID,
			List<AdminUnit> foundSubordinates) {
		System.out.println("Finding allowed subordinate AdminUnits for: "
				+ adminUnitTypeID);

		if (adminUnitTypeID == null) {
			return new ArrayList<AdminUnit>();
		}

		List<AdminUnit> res = new ArrayList<AdminUnit>();

		// find all possible masters
		String sql = "select * from AdminUnit where AdminUnitTypeID in "
				+ "(select SubordinateAdminUnitTypeID from AdminUnitTypeSubordination "
				+ "where AdminUnitTypeID=? and "
				+ "OpenedDate < NOW() and ClosedDate > NOW() and FromDate < NOW() and ToDate > NOW() )";

		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			preparedStatement.setInt(1, adminUnitTypeID);
			ResultSet resultSet = preparedStatement.executeQuery();

			res = duplicatesRemoved(resultSet, foundSubordinates);

			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(preparedStatement);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
		}

		return res;
	}

	public List<AdminUnit> duplicatesRemoved(ResultSet resultSet,
			List<AdminUnit> foundSubordinates) throws SQLException {
		List<AdminUnit> res = new ArrayList<AdminUnit>();
		AdminUnit curr;
		boolean listed;

		// already appointed subordinates will not be
		// duplicated in the possible new ones list
		while (resultSet.next()) {
			curr = createAdminUnitFromResultSet(resultSet);
			listed = false;
			for (AdminUnit au : foundSubordinates) {
				if (au.getName().equals(curr.getName())) {
					listed = true;
					break;
				}
			}
			if (!listed) {
				res.add(curr);
			}
		}
		return res;
	}

	public boolean isIDValid(Integer adminUnitID) {
		System.out.println("adminUnit isIDValid:" + adminUnitID);

		Boolean res = false;

		String sql = "select * from AdminUnit where AdminUnitID=? and "
				+ "OpenedDate < NOW() and ClosedDate > NOW() and FromDate < NOW() and ToDate > NOW() ";
		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			preparedStatement.setInt(1, adminUnitID);
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

	public Integer save(dao.AdminUnit adminUnit) {
		System.out.println("Saving AdminUnit:" + adminUnit);
		Integer res = null;

		if (adminUnit.getAdminUnitID() == null) {
			res = insertNewAdminUnit(adminUnit);
		} else {
			res = updateAdminUnit(adminUnit);
		}
		return res;
	}

	private Integer insertNewAdminUnit(dao.AdminUnit adminUnit) {
		Integer res;
		String sql = "insert into AdminUnit (Code, Name, Comment, AdminUnitTypeID, FromDate, ToDate, OpenedBy, OpenedDate, "
				+ "ChangedBy, ChangedDate, ClosedBy, ClosedDate) "
				+ "VALUES (?,?,?,?,'1900-01-01', '2999-12-31', 'Admin', NOW(), 'Admin', NOW(), 'Admin', '2999-12-31')";

		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			preparedStatement.setString(1, adminUnit.getCode());
			preparedStatement.setString(2, adminUnit.getName());
			preparedStatement.setString(3, adminUnit.getComment());
			preparedStatement.setInt(4, adminUnit.getAdminUnitTypeID());
			preparedStatement.executeUpdate();
			// this is the way to get the identity of last insert...
			PreparedStatement psIdentity = super.getConnection()
					.prepareStatement("CALL IDENTITY()");
			ResultSet result = psIdentity.executeQuery();
			result.next();
			res = result.getInt(1);
			DbUtils.closeQuietly(result);
			DbUtils.closeQuietly(psIdentity);
			DbUtils.closeQuietly(preparedStatement);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
		}
		return res;
	}

	private Integer updateAdminUnit(dao.AdminUnit adminUnit) {
		Integer res;
		String sql = "update AdminUnit set "
				+ "Code=?, Name=?, Comment=?, AdminUnitTypeID=?, ChangedBy='Admin', ChangedDate=NOW() "
				+ "where AdminUnitID=?";
		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			preparedStatement.setString(1, adminUnit.getCode());
			preparedStatement.setString(2, adminUnit.getName());
			preparedStatement.setString(3, adminUnit.getComment());
			preparedStatement.setInt(4, adminUnit.getAdminUnitTypeID());
			preparedStatement.setInt(5, adminUnit.getAdminUnitID());
			preparedStatement.executeUpdate();
			res = adminUnit.getAdminUnitID();
			DbUtils.closeQuietly(preparedStatement);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
		}
		return res;
	}

	public void saveMaster(Integer adminUnitID, Integer adminUnitMasterID) {
		if (adminUnitID != adminUnitMasterID) {

			System.out.println("Saving master for adminUnitID:" + adminUnitID
					+ " Master is:" + adminUnitMasterID);

			// if master was removed (ID - 0), close the subordination entry
			if (adminUnitMasterID == 0) {
				closeSubordination(adminUnitID, adminUnitMasterID);
				return;
			}

			// else - try changing subordination (there can be only one master)
			Integer rowsChanged = updateSubordination(adminUnitID,
					adminUnitMasterID);

			// if this is a new subordination and there was nothing to update,
			// then insert
			if (rowsChanged == 0) {
				insertSubordination(adminUnitID, adminUnitMasterID);
			}
		}
	}

	private void closeSubordination(Integer adminUnitID,
			Integer adminUnitMasterID) {
		String sql = "update AdminUnitSubordination set ChangedBy='Admin', ChangedDate=NOW(), "
				+ "ClosedBy='Admin',ClosedDate=NOW(),ToDate=NOW() "
				+ "where SubordinateAdminUnitID=?";

		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			preparedStatement.setInt(1, adminUnitID);
			int rowsChanged = preparedStatement.executeUpdate();
			System.out.println("Rows changed:" + rowsChanged);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
		}
	}

	private int updateSubordination(Integer adminUnitID,
			Integer adminUnitMasterID) {
		int rowsChanged = 0;

		if (adminUnitID != adminUnitMasterID) {
			String sql = "update AdminUnitSubordination set "
					+ "AdminUnitID=?, SubordinateAdminUnitID=?, ChangedBy='Admin', ChangedDate=NOW(), "
					+ "ClosedDate='2999-12-31', ToDate='2999-12-31' "
					+ "where SubordinateAdminUnitID=?";

			try {
				PreparedStatement preparedStatement = super.getConnection()
						.prepareStatement(sql);
				preparedStatement.setInt(1, adminUnitMasterID);
				preparedStatement.setInt(2, adminUnitID);
				preparedStatement.setInt(3, adminUnitID);

				rowsChanged = preparedStatement.executeUpdate();
				System.out.println("Rows changed:" + rowsChanged);
				// checkUpdatePresent (adminUnitID, adminUnitMasterID);
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
			}
		}
		return rowsChanged;
	}

	/*
	 * //NB! Only for debugging private void checkUpdatePresent(Integer
	 * adminUnitID, Integer adminUnitMasterID) { String sql =
	 * "select * from AdminUnitSubOrdination"; try { PreparedStatement
	 * preparedStatement = super.getConnection() .prepareStatement(sql);
	 * //preparedStatement.setInt(1, adminUnitMasterID); ResultSet resultSet =
	 * preparedStatement.executeQuery(); while (resultSet.next()) {
	 * System.out.println("Servant: " +
	 * resultSet.getInt("SubordinateAdminUnitID") + " Master: " +
	 * resultSet.getInt("AdminUnitID")); } } catch (Exception e) { throw new
	 * RuntimeException(e); } finally { } }
	 */

	private void insertSubordination(Integer adminUnitID,
			Integer adminUnitMasterID) {
		if (adminUnitID != adminUnitMasterID) {
			String sql = "insert into AdminUnitSubordination "
					+ "(AdminUnitID, SubordinateAdminUnitID, Comment, FromDate, ToDate, OpenedBy, OpenedDate, ChangedBy, ChangedDate, ClosedBy, ClosedDate) values "
					+ "(?,?,'','1900-01-01','2999-12-31','Admin',NOW(),'Admin',NOW(),'Admin','2999-12-31')";

			try {
				PreparedStatement preparedStatement = super.getConnection()
						.prepareStatement(sql);
				preparedStatement.setInt(1, adminUnitMasterID);
				preparedStatement.setInt(2, adminUnitID);
				int rowsChanged = preparedStatement.executeUpdate();
				System.out.println("Rows inserted:" + rowsChanged);
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
			}
		}
	}

	private AdminUnit createAdminUnitFromResultSet(ResultSet rs)
			throws SQLException {

		AdminUnit res = new AdminUnit();
		res.setAdminUnitID(rs.getInt("AdminUnitID"));
		res.setCode(rs.getString("Code"));
		res.setName(rs.getString("Name"));
		res.setComment(rs.getString("Comment"));
		res.setAdminUnitTypeID(rs.getInt("AdminUnitTypeID"));
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

	public List<AdminUnit> getByAdminUnitTypeID(Integer adminUnitTypeID) {
		return getByAdminUnitTypeID(adminUnitTypeID, "NOW()");
	}

	public List<AdminUnit> getByAdminUnitTypeID(Integer adminUnitTypeID,
			String dateString) {
		List<AdminUnit> res = new ArrayList<AdminUnit>();

		String dateLimits = "";

		// if we search for NOW, entry must opened and valid
		if (dateString.equals("NOW()")) {
			dateLimits = " and OpenedDate < " + dateString
					+ " and ClosedDate > " + dateString + " and FromDate < "
					+ dateString + " and ToDate > " + dateString;
		}
		// if we search for custom date, entry must have been valid THEN
		else {
			dateLimits = " and FromDate < DATE '" + dateString
					+ "' and ToDate > DATE '" + dateString + "'";
		}

		String sql = "select * from AdminUnit where AdminUnitTypeID=?"
				+ dateLimits;

		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			preparedStatement.setInt(1, adminUnitTypeID);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				res.add(createAdminUnitFromResultSet(resultSet));
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
