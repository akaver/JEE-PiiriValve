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
					.executeQuery("select * from AdminUnit");

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
		AdminUnit res = null;

		System.out.println("adminUnit getByID:" + adminUnitID);

		String sql = "select * from AdminUnit where AdminUnitID=?";
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
		System.out.println("Finding master AdminUnit for: "
				+ adminUnitID);

		if (adminUnitID == null) {
			return null;
		}

		Integer masterID = null;

		// find the subordinate record, which contains its masters id
		String sql = "select * from AdminUnitSubordination where SubordinateAdminUnitID=?";
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
			return null;
		}

		AdminUnit res = getMasterByID(adminUnitID);

		if (res == null) {
			res = new AdminUnit();
			res.setAdminUnitTypeID(0);
			res.setName("---");
		}

		return res;
	}
	
	//for populating list of possible masters to choose from; needs to know the current 
	//adminUnitType of adminUnit. must be read from session because it can
	//be changed during session
	public List<AdminUnit> getAllowedMastersByID(Integer adminUnitTypeID) {
		System.out.println("Finding allowed master AdminUnits for: "
				+ adminUnitTypeID);

		if (adminUnitTypeID == null) {
			return null;
		}

		List<AdminUnit> res = null;
		
		//find all possible masters
		String sql = "select * from AdminUnit where AdminUnitTypeID in " +
				"(select AdminUnitTypeID from AdminUnitTypeSubordination " +
				"where SubordinateAdminUnitTypeID=?)";		

		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			preparedStatement.setInt(1, adminUnitTypeID);
			ResultSet resultSet = preparedStatement.executeQuery();
			
			res = new ArrayList<AdminUnit>();

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
		System.out.println("Finding subordinates for adminUnit with ID:"
				+ adminUnitID);

		if (adminUnitID == null) {
			return null;
		}
		List<AdminUnit> res = new ArrayList<AdminUnit>();

		// get the list of subordinate ID's
		String sql = "select * from AdminUnitSubordination where AdminUnitID=?";
		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			preparedStatement.setInt(1, adminUnitID);
			ResultSet resultSet = preparedStatement.executeQuery();
			// find the record from AdminUnit and insert into result
			while (resultSet.next()) {
				Integer subid = resultSet.getInt("SubordinateAdminUnitID");
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
	
	//for populating list of possible subordinates; needs to know the current 
	//adminUnitType of adminUnit. must be read from session because it can
	//be changed during session
	public List<AdminUnit> getAllowedSuborindatesByID(Integer adminUnitTypeID) {
		System.out.println("Finding allowed subordinate AdminUnits for: "
				+ adminUnitTypeID);

		if (adminUnitTypeID == null) {
			return null;
		}

		List<AdminUnit> res = null;
		
		//find all possible masters
		String sql = "select * from AdminUnit where AdminUnitTypeID in " +
				"(select SubordinateAdminUnitTypeID from AdminUnitTypeSubordination " +
				"where AdminUnitTypeID=?)";		

		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			preparedStatement.setInt(1, adminUnitTypeID);
			ResultSet resultSet = preparedStatement.executeQuery();
				
			res = new ArrayList<AdminUnit>();

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
	
	public boolean isIDValid(Integer adminUnitID) {
		System.out.println("adminUnite isIDValid:" + adminUnitID);

		Boolean res = false;

		String sql = "select * from AdminUnit where AdminUnitID=?";
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
		String sql = "";
		if (adminUnit.getAdminUnitID() == null) {
			// this is new record
			sql = "insert into AdminUnit (Code, Name, Comment, AdminUnitTypeID, FromDate, ToDate, OpenedBy, OpenedDate, " +
					"ChangedBy, ChangedDate, ClosedBy, ClosedDate) " +
					"VALUES (?,?,?,?,'1900-01-01', '2999-12-31', 'Admin', NOW(), 'Admin', NOW(), 'Admin', '2999-12-31')";
		} else {
			// update existing record
			sql = "update AdminUnit set "
					+ "Code=?, Name=?, Comment=?, AdminUnitTypeID=?, ChangedBy='Admin', ChangedDate=NOW() "
					+ "where AdminUnitID=?";
		}

		System.out.println("Saving AdminUnitTyp sql:" + sql);

		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			preparedStatement.setString(1, adminUnit.getCode());
			preparedStatement.setString(2, adminUnit.getName());
			preparedStatement.setString(3, adminUnit.getComment());
			preparedStatement.setInt(4, adminUnit.getAdminUnitTypeID());
			if (adminUnit.getAdminUnitID() != null) {
				preparedStatement.setInt(5, adminUnit.getAdminUnitID());
			}
			preparedStatement.executeUpdate();

			if (adminUnit.getAdminUnitID() == null) {
				// this is the way to get the identity of last insert...
				PreparedStatement psIdentity = super.getConnection()
						.prepareStatement("CALL IDENTITY()");
				ResultSet result = psIdentity.executeQuery();
				result.next();
				res = result.getInt(1);
				DbUtils.closeQuietly(result);
				DbUtils.closeQuietly(psIdentity);

			} else {
				res = adminUnit.getAdminUnitID();
			}

			DbUtils.closeQuietly(preparedStatement);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
		}

		return res;
	}
	
	//called for every adminunit involved?
	public void saveMaster(Integer adminUnitID, dao.AdminUnit adminUnitMaster) {
		System.out.println("Saving master for adminUnitID:"
				+ adminUnitID + " Master is:" + adminUnitMaster);
		
		// unit can only have one master!!
		// so either it already has a record of it in db, or we shall insert new
		// one
		// try to change the existing record
		String sql = "";
		sql = "update AdminUnitSubordination set "
				+ "AdminUnitID=?, SubordinateAdminUnitID=?, ChangedBy='Admin', ChangedDate=NOW() "
				+ "where SubordinateAdminUnitID=?";
		try {
			PreparedStatement preparedStatement = super.getConnection()
					.prepareStatement(sql);
			preparedStatement.setInt(1, adminUnitMaster.getAdminUnitID());
			preparedStatement.setInt(2, adminUnitID);
			preparedStatement.setInt(3, adminUnitID);
			int rowsChanged = preparedStatement.executeUpdate();
			System.out.println("Rows changed:" + rowsChanged);
			if (rowsChanged == 0) {
				// go for insert then
				sql = "insert into AdminUnitSubordination "
						+ "(AdminUnitID, SubordinateAdminUnitID, Comment, OpenedBy, OpenedDate, ChangedBy, ChangedDate, ClosedBy, ClosedDate) values "
						+ "(?,?,'', 'Admin', NOW(), 'Admin', NOW(), 'Admin', '2999-12-31')";
				preparedStatement = super.getConnection().prepareStatement(sql);
				preparedStatement.setInt(1, adminUnitMaster.getAdminUnitID());
				preparedStatement.setInt(2, adminUnitID);
				rowsChanged = preparedStatement.executeUpdate();
				System.out.println("Rows inserted:" + rowsChanged);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
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

}
