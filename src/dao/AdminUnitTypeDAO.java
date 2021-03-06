package dao;

import java.util.*;
import java.sql.*;
import org.apache.commons.dbutils.DbUtils;

public class AdminUnitTypeDAO extends DAO {

	public List<AdminUnitType> getAll() {
		return getAll(false);
	}

	public List<AdminUnitType> getAll(Boolean searchWithoutDateLimit) {
		List<AdminUnitType> res = null;
		String dateLimits = " where OpenedDate < NOW() and ClosedDate > NOW() and FromDate < NOW() and ToDate > NOW()";

		if (searchWithoutDateLimit) {
			dateLimits = "";
		}

		try {
			Statement statement = super.getConnection().createStatement();
			ResultSet resultSet = statement
					.executeQuery("select * from AdminUnitType" + dateLimits);

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
		return getByID(adminUnitTypeID, "NOW()");
	}

	// 3 options for dateString - validated date, NOW() or emptyString (no date
	// limits)
	public AdminUnitType getByID(Integer adminUnitTypeID, String dateString) {
		AdminUnitType res = null;
		String dateLimits = "";

		// if we search for NOW, entry must opened and valid
		if (dateString.equals("NOW()")) {
			dateLimits = " and OpenedDate < " + dateString
					+ " and ClosedDate > " + dateString + " and FromDate < "
					+ dateString + " and ToDate > " + dateString;
		} else if (dateString.equals("")) {
			dateLimits = "";
		}
		// if we search for custom date, entry must have been valid THEN
		else {
			dateLimits = " and FromDate < DATE '" + dateString
					+ "' and ToDate > DATE '" + dateString + "'";
		}

		System.out.println("adminUnitType getByID:" + adminUnitTypeID);

		String sql = "select * from AdminUnitType where AdminUnitTypeID=?"
				+ dateLimits;
		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			preparedStatement.setInt(1, adminUnitTypeID);
			System.out.println("SQL:" + preparedStatement);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				res = createAdminUnitTypeFromResultSet(resultSet);
			} else {
				System.out.println("SQL: no record found!");
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
		String sql = "select * from AdminUnitTypeSubordination where SubordinateAdminUnitTypeID=? and ClosedDate>NOW()";
		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			preparedStatement.setInt(1, adminUnitTypeID);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				masterID = resultSet.getInt("AdminUnitTypeID");
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

	public List<AdminUnitType> getSubordinates(Integer adminUnitTypeID,
			String dateTimeString) {
		System.out.println("Finding subordinates for adminUnitType with ID:"
				+ adminUnitTypeID);

		if (adminUnitTypeID == null) {
			return null;
		}
		List<AdminUnitType> res = new ArrayList<AdminUnitType>();

		String dateLimits = "";

		// if we search for NOW, entry must opened and valid
		if (dateTimeString.equals("NOW()")) {
			dateLimits = " and AdminUnitTypeSubordination.OpenedDate < "
					+ dateTimeString
					+ " and AdminUnitTypeSubordination.ClosedDate > "
					+ dateTimeString;
		} else if (dateTimeString.equals("")) {
			dateLimits = "";
		}

		// get the list of subordinate ID's
		String sql = "select * from AdminUnitTypeSubordination where AdminUnitTypeID=?"
				+ dateLimits;
		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			preparedStatement.setInt(1, adminUnitTypeID);
			ResultSet resultSet = preparedStatement.executeQuery();
			// find the record from AdminUnitType and insert into result
			while (resultSet.next()) {
				Integer subid = resultSet.getInt("SubordinateAdminUnitTypeID");
				System.out.println("Fetching subordinate with ID:" + subid);
				AdminUnitType tempAdminUnitType = getByID(subid);
				res.add(tempAdminUnitType);
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

	public List<AdminUnitType> getPossibleSubordinates(Integer adminUnitTypeID,
			String dateTimeString) {
		System.out.println("Possible subordinates for AdminUnitTypeID:"
				+ adminUnitTypeID);

		List<AdminUnitType> res = new ArrayList<AdminUnitType>();

		if (adminUnitTypeID == null) {
			adminUnitTypeID = 0;
		}
		// return the list of possible subordinates for this adminUnit
		// all units without any master set and excluding itself and itself's
		// master
		// pluss all the units which where removed from the list on the form
		// (but not yet saved to db as removed)
		// TODO fix datetime
		/*
		 * String dateLimits = "";
		 * 
		 * // if we search for NOW, entry must opened and valid if
		 * (dateTimeString.equals("NOW()")) { dateLimits =
		 * " and AdminUnitType.OpenedDate < " + dateTimeString +
		 * " and AdminUnitType.ClosedDate > " + dateTimeString +
		 * " and AdminUnitTypeSubordination.OpenedDate < " + dateTimeString +
		 * " and AdminUnitTypeSubordination.ClosedDate > " + dateTimeString +
		 * ""; } else if (dateTimeString.equals("")) { dateLimits = ""; }
		 */
		// list of all AdminUnitTypeID's
		// without current AdminUnitTypeID
		// without master currently set
		// and without record no 1 - the first semifixed unit - the state
		/*
		 * String sql = "select AdminUnitType.AdminUnitTypeID as ID1 " +
		 * "from  AdminUnitType LEFT OUTER JOIN AdminUnitTypeSubordination ON AdminUnitType.AdminUnitTypeID=AdminUnitTypeSubordination.SubordinateAdminUnitTypeID "
		 * + "where " + "AdminUnitType.AdminUnitTypeID<>1 and " + // id 1 is
		 * state - it cannot be subordinate "AdminUnitType.AdminUnitTypeID<>? "
		 * + "and " + "ISNULL(AdminUnitTypeSubordination.AdminUnitTypeID,0)=0 "+
		 * dateLimits+ "";
		 */
		// fuck the hsqldb, this isnt normal
		String sql = "select * from (select  AdminUnitTypeID, ISNULL(AdminUnitTypeSubordinationID,0) as IdNull "
				+ "from AdminUnitType LEFT JOIN "
				+ "(select * from AdminUnitTypeSubordination where ClosedDate>NOW()) AS  AdminUnitTypeSubordinationTemp "
				+ "ON AdminUnitType.AdminUnitTypeID=AdminUnitTypeSubordinationTemp.SubordinateAdminUnitTypeID "
				+ "where "
				+ "AdminUnitType.AdminUnitTypeID<>1 and "
				+ "AdminUnitType.AdminUnitTypeID<>? and "
				+ "AdminUnitType.ClosedDate>NOW() and "
				+ "AdminUnitType.ToDate>NOW() )";

		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			preparedStatement.setInt(1, adminUnitTypeID);
			System.out.println("SQL: " + preparedStatement);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				// lets skip over some records
				if (resultSet.getInt("IdNull") == 0) {
					System.out.println("sub: "
							+ resultSet.getInt("AdminUnitTypeID"));
					AdminUnitType tempAdminUnitType = getByID(resultSet
							.getInt("AdminUnitTypeID"));
					System.out.println("sub: " + tempAdminUnitType);
					res.add(tempAdminUnitType);
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

	public Integer save(dao.AdminUnitType adminUnitType) {
		System.out.println("Saving AdminUnitType:" + adminUnitType);
		Integer res = null;
		String sql = "";
		if (adminUnitType.getAdminUnitTypeID() == null) {
			// this is new record
			sql = "insert into AdminUnitType (Code, Name, Comment, FromDate, ToDate, OpenedBy, OpenedDate, ChangedBy, ChangedDate, ClosedBy, ClosedDate) "
					+ "VALUES (?,?,?,'1900-01-01', '2999-12-31', 'Admin', NOW(), 'Admin', NOW(), 'Admin', '2999-12-31')";
		} else {
			// update existing record
			sql = "update AdminUnitType set "
					+ "Code=?, Name=?, Comment=?, ChangedBy='Admin', ChangedDate=NOW() "
					+ "where AdminUnitTypeID=?";
		}

		System.out.println("Saving AdminUnitTyp sql:" + sql);

		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			preparedStatement.setString(1, adminUnitType.getCode());
			preparedStatement.setString(2, adminUnitType.getName());
			preparedStatement.setString(3, adminUnitType.getComment());
			if (adminUnitType.getAdminUnitTypeID() != null) {
				preparedStatement.setInt(4, adminUnitType.getAdminUnitTypeID());
			}
			preparedStatement.executeUpdate();

			if (adminUnitType.getAdminUnitTypeID() == null) {
				// this is the way to get the identity of last insert...
				PreparedStatement psIdentity = super.getConnection()
						.prepareStatement("CALL IDENTITY()");
				ResultSet result = psIdentity.executeQuery();
				result.next();
				res = result.getInt(1);
				DbUtils.closeQuietly(result);
				DbUtils.closeQuietly(psIdentity);

			} else {
				res = adminUnitType.getAdminUnitTypeID();
			}

			DbUtils.closeQuietly(preparedStatement);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
		}

		return res;
	}

	public void saveMaster(Integer adminUnitTypeID,
			dao.AdminUnitType adminUnitTypeMaster) {
		System.out.println("Saving master for adminUnitTypeID:"
				+ adminUnitTypeID + " Master is:" + adminUnitTypeMaster);
		// unit can only have one master!!
		// so either it already has a record of it in db, or we shall insert new

		// TODO: avoid recursion!!!!
		// major problem with recursion on subordinate list
		// it is possible to save new master, which is actually your
		// child-child-child somewhere
		// you should traverse a tree down from yourself and see
		// that new master is not among your sub branch

		String sql = "";
		if (adminUnitTypeID == 1) {
			System.out.println("Cannot change master on primary state!");
			return;
		}
		if (adminUnitTypeMaster.getAdminUnitTypeID() == 0) {
			// remove the master
			sql = "update AdminUnitTypeSubordination set "
					+ "ChangedBy='Admin', ChangedDate=NOW(), ClosedBy='Admin', ClosedDate=NOW() "
					+ "where SubordinateAdminUnitTypeID=?";

		} else {
			// change the master
			sql = "update AdminUnitTypeSubordination set "
					+ "AdminUnitTypeID=?, ChangedBy='Admin', ChangedDate=NOW() "
					+ "where SubordinateAdminUnitTypeID=? and ClosedDate>NOW()";
		}

		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			if (adminUnitTypeMaster.getAdminUnitTypeID() == 0) {
				preparedStatement.setInt(1, adminUnitTypeID);
			} else {
				preparedStatement.setInt(1,
						adminUnitTypeMaster.getAdminUnitTypeID());
				preparedStatement.setInt(2, adminUnitTypeID);
			}
			int rowsChanged = preparedStatement.executeUpdate();
			System.out.println("Rows changed:" + rowsChanged);
			if (rowsChanged == 0) {
				// fuckit, go for insert then
				sql = "insert into AdminUnitTypeSubordination "
						+ "(AdminUnitTypeID, SubordinateAdminUnitTypeID, Comment, OpenedBy, OpenedDate, ChangedBy, ChangedDate, ClosedBy, ClosedDate) values "
						+ "(?,?,'', 'Admin', NOW(), 'Admin', NOW(), 'Admin', '2999-12-31 00:00:00')";
				preparedStatement = super.getConnection().prepareStatement(sql);
				preparedStatement.setInt(1,
						adminUnitTypeMaster.getAdminUnitTypeID());
				preparedStatement.setInt(2, adminUnitTypeID);
				rowsChanged = preparedStatement.executeUpdate();
				System.out.println("Rows inserted:" + rowsChanged);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
		}

	}

	public void addSubordinate(Integer adminUnitTypeID,
			AdminUnitType subordinate) {
		System.out.println("Adding subordinate for adminUnitTypeID:"
				+ adminUnitTypeID + " Subordinate is:" + subordinate);

		String sql = "insert into AdminUnitTypeSubordination "
				+ "(AdminUnitTypeID, SubordinateAdminUnitTypeID, Comment, OpenedBy, OpenedDate, ChangedBy, ChangedDate, ClosedBy, ClosedDate) values "
				+ "(?,?,'', 'Admin', NOW(), 'Admin', NOW(), 'Admin', '2999-12-31 00:00:00')";

		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			preparedStatement.setInt(1, adminUnitTypeID);
			preparedStatement.setInt(2, subordinate.getAdminUnitTypeID());
			System.out.println("SQL:" + preparedStatement);
			int rowsChanged = preparedStatement.executeUpdate();
			System.out.println("Rows changed:" + rowsChanged);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
		}

	}

	public void removeSubordinate(Integer adminUnitTypeID,
			AdminUnitType subordinate) {
		System.out.println("Removing subordinate for adminUnitTypeID:"
				+ adminUnitTypeID + " Subordinate is:" + subordinate);

		String sql = "update AdminUnitTypeSubordination set "
				+ "ChangedBy='Admin', ChangedDate=NOW(), ClosedBy='Admin', ClosedDate=NOW() "
				+ "where AdminUnitTypeID=? and SubordinateAdminUnitTypeID=?";

		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			preparedStatement.setInt(1, adminUnitTypeID);
			preparedStatement.setInt(2, subordinate.getAdminUnitTypeID());
			System.out.println("SQL:" + preparedStatement);
			int rowsChanged = preparedStatement.executeUpdate();
			System.out.println("Rows changed:" + rowsChanged);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
		}

	}

	public Integer getSubordinateCount(AdminUnitType adminUnitType) {
		Integer res = 0;

		String sql = "select count(*) as Total  from AdminUnitTypeSubordination where "
				+ "AdminUnitTypeID=? and ClosedDate>NOW()";
		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			preparedStatement.setInt(1, adminUnitType.getAdminUnitTypeID());
			System.out.println("SQL: " + preparedStatement);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				res=resultSet.getInt("Total");
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
