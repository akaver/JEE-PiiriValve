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

	public List<AdminUnitType> getSubordinates(Integer adminUnitTypeID) {
		System.out.println("Finding subordinates for adminUnitType with ID:"
				+ adminUnitTypeID);
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

}
