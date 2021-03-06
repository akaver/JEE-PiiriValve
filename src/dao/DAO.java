/*
Eclipse 3.6 allows you to turn off formatting by placing a special comment, like
// @formatter:off
...
// @formatter:on
The on/off features have to be turned "on" in Eclipse preferences: Java > Code Style > Formatter. Click on "Edit" button, "Off/On Tags", check off "Enable Off/On tags".
 */
package dao;

import java.io.File;
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
			connection = DriverManager.getConnection("jdbc:hsqldb:file:" + db, "sa",
					"");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public DAO(boolean deleteLockFile) {
		// this delete lockfile is hack for ITK tomcat server problems
		// sometimes hsqldb will lock itself, only removal of *.lck helps
		if (deleteLockFile) {
			try {
				File lockfile = new File(
						"/usr/share/tomcat7/i377/Team02d/db.lck");
				if (lockfile.delete()) {
					System.out.println(lockfile.getName() + " is deleted!");
				} else {
					System.out.println("Lock file delete failed:"+lockfile.getAbsolutePath());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
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
	}

	private void dropTables() {
		executeSQL("DROP SCHEMA PUBLIC CASCADE");
	}

	public void insertDummyData() {
		insertDummyDataToAdminUnitTypeTable();
		insertDummyDataToAdminUnitTypeSubordinationTable();
		insertDummyDataToAdminUnitTable();
		insertDummyDataToAdminUnitSubordinationTable();
		insertDummyDataForTestingPastDates();
	}

	private void insertDummyDataForTestingPastDates() {
		// Kihelkond (id 9) - olemas apr-juuni 2012
		executeUpdateSQL("insert into AdminUnitType " +
				"(Code, Name, Comment, FromDate, ToDate, OpenedBy, OpenedDate, ChangedBy, ChangedDate, ClosedBy, ClosedDate) VALUES " +
				"('KH','Kihelkond','väga vana üksus','2012-04-01 00:00:00','2012-07-01 00:00:00'," +
				"'Admin', '2012-12-01 00:00:00', 'Admin', '2012-12-01 00:00:00', 'Admin', '2999-12-31 00:00:00')");
		// Talu (id 10) - olemas apr-juuni 2012
		executeUpdateSQL("insert into AdminUnitType " +
				"(Code, Name, Comment, FromDate, ToDate, OpenedBy, OpenedDate, ChangedBy, ChangedDate, ClosedBy, ClosedDate) VALUES " +
				"('T','Talu','talu, mis talu','2012-04-01 00:00:00','2012-07-01 00:00:00'," +
				"'Admin', '2012-12-01 00:00:00', 'Admin', '2012-12-01 00:00:00', 'Admin', '2999-12-31 00:00:00')");
		
		// Oli olemas kihelkond Alempois (id 12) - olemas apr-juuni 2012
		executeUpdateSQL("insert into AdminUnit " +
				"(Code, Name, Comment, AdminUnitTypeID, FromDate, ToDate, OpenedBy, OpenedDate, ChangedBy, ChangedDate, ClosedBy, ClosedDate) VALUES " +
				"('Alempois','Alempois','hirmus kõva kihelkond','9','2012-04-01','2012-07-01'," +
				"'Admin', '2012-12-01', 'Admin', '2012-12-01', 'Admin', '2999-12-31')");
		// Alempoisile allus Kurevere küla - allus apr-mai 2012
		executeUpdateSQL("insert into AdminUnitSubordination " +
				"(AdminUnitID, SubordinateAdminUnitID, Comment, FromDate, ToDate, OpenedBy, OpenedDate, ChangedBy, ChangedDate, ClosedBy, ClosedDate) VALUES " +
				"('12','9','','2012-04-01','2012-06-01'," +
				"'Admin', '2012-12-01', 'Admin', '2012-12-01', 'Admin', '2999-12-31')");
		// Alempoisile allus Karuvere küla - allus mai-juuni 2012
		executeUpdateSQL("insert into AdminUnitSubordination " +
				"(AdminUnitID, SubordinateAdminUnitID, Comment, FromDate, ToDate, OpenedBy, OpenedDate, ChangedBy, ChangedDate, ClosedBy, ClosedDate) VALUES " +
				"('12','10','','2012-05-01','2012-07-01'," +
				"'Admin', '2012-12-01', 'Admin', '2012-12-01', 'Admin', '2999-12-31')");
		
		// Oli olemas talu Karu talu (id 13) - olemas juuni 2012
		executeUpdateSQL("insert into AdminUnit " +
				"(Code, Name, Comment, AdminUnitTypeID, FromDate, ToDate, OpenedBy, OpenedDate, ChangedBy, ChangedDate, ClosedBy, ClosedDate) VALUES " +
				"('KaruTalu','Karu talu','siin elavad karud','10','2012-06-01','2012-07-01'," +
				"'Admin', '2012-12-01', 'Admin', '2012-12-01', 'Admin', '2999-12-31')");
		// ... mis allus Karuvere külale 5.-10. juuni 2012
		executeUpdateSQL("insert into AdminUnitSubordination " +
				"(AdminUnitID, SubordinateAdminUnitID, Comment, FromDate, ToDate, OpenedBy, OpenedDate, ChangedBy, ChangedDate, ClosedBy, ClosedDate) VALUES " +
				"('10','13','','2012-06-05','2012-06-10'," +
				"'Admin', '2012-12-01', 'Admin', '2012-12-01', 'Admin', '2999-12-31')");
	}

	private void createAdminUnitTypeTable() {
		executeSQL("create table AdminUnitType ( "
				+ "AdminUnitTypeID INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL,"
				+ "Code                     VARCHAR(10) NOT NULL,"
				+ "Name                     VARCHAR(100) NOT NULL,"
				+ "Comment                  LONGVARCHAR,"
				+ "FromDate                 TIMESTAMP NOT NULL,"
				+ "ToDate                   TIMESTAMP NOT NULL,"
				+ "OpenedBy                 VARCHAR(32) NOT NULL,"
				+ "OpenedDate               TIMESTAMP NOT NULL,"
				+ "ChangedBy                VARCHAR(32) NOT NULL,"
				+ "ChangedDate              TIMESTAMP NOT NULL,"
				+ "ClosedBy                 VARCHAR(32),"
				+ "ClosedDate               TIMESTAMP NOT NULL,"
				+ "PRIMARY KEY (AdminUnitTypeID)" + ")");
	}

	private void insertDummyDataToAdminUnitTypeTable() {
		String std = "'1900-01-01 00:00:00', '2999-12-31 00:00:00', 'Admin', '2012-12-01 00:00:00', 'Admin', '2012-12-01 00:00:00', 'Admin', '2999-12-31 00:00:00'";
		executeUpdateSQL("insert into AdminUnitType "
				+ "(Code, Name, Comment, FromDate, ToDate, OpenedBy, OpenedDate, ChangedBy, ChangedDate, ClosedBy, ClosedDate) VALUES "
				+ //
				"('R', 'Riik', '', "
				+ std + ")," + //
				"('M', 'Maakond', '', " + std + ")," + //
				"('ML', 'Maakonnalinn', '', " + std + ")," + //
				"('V', 'Vald', '', " + std + ")," + //
				"('VL', 'Vallalinn', '', " + std + ")," + //
				"('A1', 'Alev', '', " + std + ")," + //
				"('A2', 'Alevik', '', " + std + ")," + //
				"('K', 'Küla', '', " + std + ")" + //
				"");
	}

	private void createAdminUnitTypeSubordinationTable() {
		executeSQL("create table AdminUnitTypeSubordination ( "
				+ "AdminUnitTypeSubordinationID INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL,"
				+ "AdminUnitTypeID				INTEGER NOT NULL,"
				+ "SubordinateAdminUnitTypeID	INTEGER NOT NULL,"
				+ "Comment                 	 	LONGVARCHAR,"
				+ "OpenedBy                 	VARCHAR(32) NOT NULL,"
				+ "OpenedDate               	TIMESTAMP NOT NULL,"
				+ "ChangedBy                	VARCHAR(32) NOT NULL,"
				+ "ChangedDate              	TIMESTAMP NOT NULL,"
				+ "ClosedBy                 	VARCHAR(32),"
				+ "ClosedDate               	TIMESTAMP NOT NULL,"
				+ "PRIMARY KEY (AdminUnitTypeSubordinationID),"
				+ "FOREIGN KEY (AdminUnitTypeID) REFERENCES AdminUnitType ON DELETE RESTRICT,"
				+ "FOREIGN KEY (SubordinateAdminUnitTypeID) REFERENCES AdminUnitType ON DELETE RESTRICT"
				+ ")");
	}

	private void insertDummyDataToAdminUnitTypeSubordinationTable() {
		String std = "'Admin', '2012-12-01 00:00:00', 'Admin', '2012-12-01 00:00:00', 'Admin', '2999-12-31 00:00:00'";
		executeUpdateSQL("insert into AdminUnitTypeSubordination "
				+ "(AdminUnitTypeID, SubordinateAdminUnitTypeID, Comment, OpenedBy, OpenedDate, ChangedBy, ChangedDate, ClosedBy, ClosedDate) VALUES "+
// @formatter:off
				"('1', '2', 'riik->maakond', " + std + ")," +
				"('2', '3', 'maakond->maakonnalinn', " + std + ")," +
				"('2', '4', 'maakond->vald', " + std + ")," +
				"('4', '5', 'vald->vallalinn', " + std + ")," + 
//				"('4', '6', 'vald->alev', " + std + ")," + 
//				"('4', '7', 'vald->alevik', " + std + ")," +
				"('4', '8', 'vald->küla', " + std + ")" +
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
				"('Eesti', 'Eesti Vabariik', 'Kõrgeim haldusüksus, riik', '1'," + std + ")," + 
				"('Harjumaa', 'Harjumaa maakond', '', '2'," + std + ")," +
				"('Tallinn', 'Tallinn', 'pealinn', '3'," + std + ")," + 
				"('KiiliVald', 'Kiili vald', '', '4'," + std + ")," + 
				"('KiiliAlev', 'Kiili alev', '', '6'," + std + ")," + 
				"('Luige', 'Luige alevik', '', '7'," + std + ")," + 
				"('Kangru', 'Kangru alevik', '', '7'," + std + ")," + 
				"('Arusta', 'Arusta küla', '', '8'," + std + ")," + 
				"('Kurevere', 'Kurevere küla', '', '8'," + std + ")," + 
				"('Karuvere', 'Karuvere küla', '', '8'," + std + ")," +
				"('KureVald', 'Kure vald', '', '4'," + std + ")" + 
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
				"('4', '8', 'KiiliVald->Arusta'," + std + ")" + 
				//"('4', '9', 'KiiliVald->Kurevere'," + std + ")" + 
// @formatter:on
				"");
	}

}
