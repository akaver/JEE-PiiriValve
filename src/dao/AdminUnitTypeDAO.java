package dao;

import java.util.*;
import java.sql.*;
import org.apache.commons.dbutils.DbUtils;

public class AdminUnitTypeDAO extends DAO {
	private Statement statement;
	private PreparedStatement preparedStatement;
	private ResultSet resultSet;

	public List<AdminUnitType> getAll() {
		List<AdminUnitType> res = null;

		try {
			statement = super.getConnection().createStatement();
			resultSet = statement.executeQuery("select * from AdminUnitType");

			res = new ArrayList<AdminUnitType>();

			while (resultSet.next()) {
				res.add(createAdminUnitTypeFromResultSet(resultSet));
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(statement);
		}
		return res;
	}

	public AdminUnitType getByID(Integer adminUnitTypeID) {
		AdminUnitType res = null;

		String sql = "select * from AdminUnitType where AdminUnitTypeID=?";
		try {
			preparedStatement = super.getConnection().prepareStatement(sql);
			preparedStatement.setInt(1, adminUnitTypeID);
			resultSet = preparedStatement.executeQuery();
			resultSet.next();
			res = createAdminUnitTypeFromResultSet(resultSet);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(statement);
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
