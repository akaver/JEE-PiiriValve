/*
Eclipse 3.6 allows you to turn off formatting by placing a special comment, like
// @formatter:off
...
// @formatter:on
The on/off features have to be turned "on" in Eclipse preferences: Java > Code Style > Formatter. Click on "Edit" button, "Off/On Tags", check off "Enable Off/On tags".
 */
package dao;

import java.sql.*;

import org.apache.commons.dbutils.DbUtils;

public class DAO {
	private Connection connection;

	public DAO() {
		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		try {
			String db = "${user.home}/i377/Team02d/db;shutdown=true";
			connection = DriverManager.getConnection("jdbc:hsqldb:" + db, "sa",
					"");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Connection getConnection() {
		return connection;
	}

	public boolean executeSQL(String sql) {
		Statement st = null;
		try {
			st = connection.createStatement();
			return st.execute(sql);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			DbUtils.closeQuietly(st);
		}
	}

	public int executeUpdateSQL(String sql) {
		Statement st = null;
		try {
			st = connection.createStatement();
			return st.executeUpdate(sql);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			DbUtils.closeQuietly(st);
		}
	}

	public void createTables() {
		dropTables();
		createAdminUnitTypeTable();
		createAdminUnitTypeSubordinationTable();
		createAdminUnitTable();
		createAdminUnitSubordinationTable();
		createArmyUnitTable();
		createArmyUnitSubordinationTable();
	}

	private void dropTables() {
		executeSQL("DROP SCHEMA PUBLIC CASCADE");
		
//		executeSQL("DROP TABLE AdminUnitTypeSubordination IF EXISTS");
//		executeSQL("DROP TABLE AdminUnitType IF EXISTS");
//		executeSQL("DROP TABLE AdminUnitSubordination IF EXISTS");
//		executeSQL("DROP TABLE AdminUnit IF EXISTS");
//		executeSQL("DROP TABLE ArmyUnitSubordination IF EXISTS");
//		executeSQL("DROP TABLE ArmyUnit IF EXISTS");
	}

	public void insertDummyData() {
		insertDummyDataToAdminUnitTypeTable();
		insertDummyDataToAdminUnitTypeSubordinationTable();
		insertDummyDataToAdminUnitTable();
		insertDummyDataToAdminUnitSubordinationTable();
		insertDummyDataToArmyUnitTable();
		insertDummyDataToArmyUnitSubordinationTable();
	}

	private void createAdminUnitTypeTable() {
		executeSQL("create table AdminUnitType ( "
				+ "AdminUnitTypeID INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL,"
				+ "Code                     VARCHAR(10) NOT NULL,"
				+ "Name                     VARCHAR(100) NOT NULL,"
				+ "Comment                  LONGVARCHAR,"
				+ "FromDate                 DATE NOT NULL,"
				+ "ToDate                   DATE NOT NULL,"
				+ "OpenedBy                 VARCHAR(32) NOT NULL,"
				+ "OpenedDate               DATE NOT NULL,"
				+ "ChangedBy                VARCHAR(32) NOT NULL,"
				+ "ChangedDate              DATE NOT NULL,"
				+ "ClosedBy                 VARCHAR(32),"
				+ "ClosedDate               DATE NOT NULL,"
				+ "PRIMARY KEY (AdminUnitTypeID)" + ")");
	}

	private void insertDummyDataToAdminUnitTypeTable() {
		String std = "'1900-01-01', '2999-12-31', 'Admin', '2012-12-01', 'Admin', '2012-12-01', 'Admin', '2999-12-31'";
		executeUpdateSQL("insert into AdminUnitType "
				+ "(Code, Name, Comment, FromDate, ToDate, OpenedBy, OpenedDate, ChangedBy, ChangedDate, ClosedBy, ClosedDate) VALUES "
				+ //
				"('R', 'Riik', '', " + std + ")," + //
				"('M', 'Maakond', '', " + std + ")," + //
				"('V', 'Vald', '', " + std + ")," + //
				"('L', 'Linn', '', " + std + ")," + //
				"('A1', 'Alev', '', " + std + ")," + //
				"('A2', 'Alevik', '', " + std + ")," + //
				"('K', 'Küla', '', " + std + ")" + //
				"");
	}

	private void createAdminUnitTypeSubordinationTable() {
		executeSQL("create table AdminUnitTypeSubordination ( "
				+ "AdminUnitTypeSubordinationID INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL,"
				+ "AdminUnitTypeID		INTEGER NOT NULL,"
				+ "SubordinateAdminUnitTypeID	INTEGER NOT NULL,"
				+ "Comment                 	 	LONGVARCHAR,"
				+ "OpenedBy                 	VARCHAR(32) NOT NULL,"
				+ "OpenedDate               	DATE NOT NULL,"
				+ "ChangedBy                	VARCHAR(32) NOT NULL,"
				+ "ChangedDate              	DATE NOT NULL,"
				+ "ClosedBy                 	VARCHAR(32),"
				+ "ClosedDate               	DATE NOT NULL,"
				+ "PRIMARY KEY (AdminUnitTypeSubordinationID),"
				+ "FOREIGN KEY (AdminUnitTypeID) REFERENCES AdminUnitType ON DELETE RESTRICT,"
				+ "FOREIGN KEY (SubordinateAdminUnitTypeID) REFERENCES AdminUnitType ON DELETE RESTRICT"
				+ ")");
	}

	private void insertDummyDataToAdminUnitTypeSubordinationTable() {
		String std = "'Admin', '2012-12-01', 'Admin', '2012-12-01', 'Admin', '2999-12-31'";
		executeUpdateSQL("insert into AdminUnitTypeSubordination "
				+ "(AdminUnitTypeID, SubordinateAdminUnitTypeID, Comment, OpenedBy, OpenedDate, ChangedBy, ChangedDate, ClosedBy, ClosedDate) VALUES "+
// @formatter:off
				"('1', '2', 'riik->maakond', " + std + ")," +
				"('2', '3', 'maakond->vald', " + std + ")," +
				"('2', '4', 'maakond->linn', " + std + ")," +
				"('3', '4', 'vald->linn', " + std + ")," + 
				"('3', '5', 'vald->alev', " + std + ")," + 
				"('3', '6', 'vald->alevik', " + std + ")," +
				"('3', '7', 'vald->küla', " + std + ")" +
// @formatter:on
				"");

	}

	private void createAdminUnitTable() {
		executeSQL("create table AdminUnit ( "
				+ "AdminUnitID INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL,"
				+ "Code                     VARCHAR(10) NOT NULL,"
				+ "Name                     VARCHAR(100) NOT NULL,"
				+ "Comment                  LONGVARCHAR,"
				+ "AdminUnitTypeID			INTEGER NOT NULL,"
				+ "FromDate                 DATE NOT NULL,"
				+ "ToDate                   DATE NOT NULL,"
				+ "OpenedBy                 VARCHAR(32) NOT NULL,"
				+ "OpenedDate               DATE NOT NULL,"
				+ "ChangedBy                VARCHAR(32) NOT NULL,"
				+ "ChangedDate              DATE NOT NULL,"
				+ "ClosedBy                 VARCHAR(32),"
				+ "ClosedDate               DATE NOT NULL,"
				+ "PRIMARY KEY (AdminUnitID),"
				+ "FOREIGN KEY (AdminUnitTypeID) REFERENCES AdminUnitType ON DELETE RESTRICT"
				+ ")");
	}

	private void insertDummyDataToAdminUnitTable() {
		String std = "'1900-01-01', '2999-12-31', 'Admin', '2012-12-01', 'Admin', '2012-12-01', 'Admin', '2999-12-31'";
		executeUpdateSQL("insert into AdminUnit "
				+ "(Code, Name, Comment, AdminUnitTypeID, FromDate, ToDate, OpenedBy, OpenedDate, ChangedBy, ChangedDate, ClosedBy, ClosedDate) VALUES "
				+
// @formatter:off
				"('Eesti', 'Eesti Vabariik', 'globaalne AdminUnit, riik', '1'," + std + ")," + 
				"('Harjumaa', 'Harjumaa maakond', '', '2'," + std + ")," +
				"('Tallinn', 'Tallinn', 'pealinn', '4'," + std + ")," + 
				"('KiiliVald', 'Kiili vald', '', '3'," + std + ")," + 
				"('KiiliAlev', 'Kiili alev', '', '5'," + std + ")," + 
				"('Luige', 'Luige alevik', '', '6'," + std + ")," + 
				"('Kangru', 'Kangru alevik', '', '6'," + std + ")," + 
				"('Arusta', 'Arusta küla', '', '7'," + std + ")," + 
				"('Kurevere', ' Kurevere küla', '', '7'," + std + ")" + 

				
// @formatter:on
				"");

	}

	private void createAdminUnitSubordinationTable() {
		executeSQL("create table AdminUnitSubordination ( "
				+ "AdminUnitSubordinationID INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL,"
				+ "AdminUnitID		INTEGER,"
				+ "SubordinateAdminUnitID	INTEGER,"
				+ "Comment                  LONGVARCHAR,"
				+ "FromDate                 DATE NOT NULL,"
				+ "ToDate                   DATE NOT NULL,"
				+ "OpenedBy                 VARCHAR(32) NOT NULL,"
				+ "OpenedDate               DATE NOT NULL,"
				+ "ChangedBy                VARCHAR(32) NOT NULL,"
				+ "ChangedDate              DATE NOT NULL,"
				+ "ClosedBy                 VARCHAR(32),"
				+ "ClosedDate               DATE NOT NULL,"
				+ "PRIMARY KEY (AdminUnitSubordinationID),"
				+ "FOREIGN KEY (AdminUnitID) REFERENCES AdminUnit ON DELETE RESTRICT,"
				+ "FOREIGN KEY (SubordinateAdminUnitID) REFERENCES AdminUnit ON DELETE RESTRICT"

				+ ")");
	}

	private void insertDummyDataToAdminUnitSubordinationTable() {
		String std = "'1900-01-01', '2999-12-31', 'Admin', '2012-12-01', 'Admin', '2012-12-01', 'Admin', '2999-12-31'";
		executeUpdateSQL("insert into AdminUnitSubordination "
				+ "(AdminUnitID, SubordinateAdminUnitID, Comment, FromDate, ToDate, OpenedBy, OpenedDate, ChangedBy, ChangedDate, ClosedBy, ClosedDate) VALUES "
				+
// @formatter:off
				"('1', '2', 'Eesti->Harjumaa'," + std + ")," + 
				"('2', '3', 'Harjumaa->Tallinn'," + std + ")," + 
				"('2', '4', 'Harjumaa->KiiliVald'," + std + ")," + 
				"('4', '5', 'KiiliVald->KiiliAlev'," + std + ")," + 
				"('4', '6', 'KiiliVald->Luige'," + std + ")," + 
				"('4', '7', 'KiiliVald->Kangru'," + std + ")," + 
				"('4', '8', 'KiiliVald->Arusta'," + std + ")," + 
				"('4', '9', 'KiiliVald->Kurevere'," + std + ")" + 
// @formatter:on
				"");
	}

	private void createArmyUnitTable() {
		executeSQL("create table ArmyUnit ( "
				+ "ArmyUnitID INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL,"
				+ "Code                     VARCHAR(10) NOT NULL,"
				+ "AdminUnitID				INTEGER,"
				+ "Comment                  LONGVARCHAR,"
				+ "FromDate                 DATE NOT NULL,"
				+ "ToDate                   DATE NOT NULL,"
				+ "OpenedBy                 VARCHAR(32) NOT NULL,"
				+ "OpenedDate               DATE NOT NULL,"
				+ "ChangedBy                VARCHAR(32) NOT NULL,"
				+ "ChangedDate              DATE NOT NULL,"
				+ "ClosedBy                 VARCHAR(32),"
				+ "ClosedDate               DATE NOT NULL,"
				+ "PRIMARY KEY (ArmyUnitID)" + ")");
	}

	private void insertDummyDataToArmyUnitTable() {

	}

	private void createArmyUnitSubordinationTable() {
		executeSQL("create table ArmyUnitSubordination ( "
				+ "ArmyUnitSubordinationID INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL,"
				+ "ArmyUnitID			INTEGER NOT NULL,"
				+ "SubordinateArmyUnitID	INTEGER NOT NULL,"
				+ "Comment                  LONGVARCHAR,"
				+ "FromDate                 DATE NOT NULL,"
				+ "ToDate                   DATE NOT NULL,"
				+ "OpenedBy                 VARCHAR(32) NOT NULL,"
				+ "OpenedDate               DATE NOT NULL,"
				+ "ChangedBy                VARCHAR(32) NOT NULL,"
				+ "ChangedDate              DATE NOT NULL,"
				+ "ClosedBy                 VARCHAR(32),"
				+ "ClosedDate               DATE NOT NULL,"
				+ "PRIMARY KEY (ArmyUnitSubordinationID),"
				+ "FOREIGN KEY (ArmyUnitID) REFERENCES ArmyUnit ON DELETE RESTRICT,"
				+ "FOREIGN KEY (SubordinateArmyUnitID) REFERENCES ArmyUnit ON DELETE RESTRICT"

				+ ")");
	}

	private void insertDummyDataToArmyUnitSubordinationTable() {

	}

}
