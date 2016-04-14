package kr.pe.maun.csdgenerator.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.datatools.connectivity.IConnection;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.drivers.jdbc.JDBCConnectionFactory;

import kr.pe.maun.csdgenerator.model.ColumnItem;

public class DatabaseResource {

	private final String ORACLE_TABLE_QUERY = "SELECT TABLE_NAME FROM TABS";
	private final String ORACLE_COLUMN_QUERY = "SELECT C.COLUMN_NAME, C.DATA_TYPE, NVL(UCC.COMMENTS, ' ') AS COMMENTS FROM COLS C LEFT OUTER JOIN USER_COL_COMMENTS UCC ON C.COLUMN_NAME = UCC.COLUMN_NAME AND C.TABLE_NAME = UCC.TABLE_NAME WHERE C.TABLE_NAME = ?";
	private final String ORACLE_INDEX_QUERY = "SELECT INDEX_NAME, TABLE_NAME, COLUMN_POSITION, COLUMN_LENGTH, COLUMN_NAME FROM USER_IND_COLUMNS WHERE TABLE_NAME = ?";

	private final String MYSQL_TABLE_QUERY = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = ?";
	private final String MYSQL_COLUMN_QUERY = "SELECT COLUMN_NAME, COLUMN_TYPE, REPLACE(COLUMN_COMMENT, '\n', '') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?";
	private final String MYSQL_INDEX_QUERY = "SHOW INDEX FROM ? WHERE KEY_NAME = 'PRIMARY'";

	private final String POSTGRESQL_TABLE_QUERY = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'public' AND TABLE_CATALOG = ? ORDER BY TABLE_NAME";
	private final String POSTGRESQL_COLUMN_QUERY = "SELECT ISC.COLUMN_NAME ,ISC.DATA_TYPE ,(SELECT PG_CATALOG.COL_DESCRIPTION(PA.ATTRELID, PA.ATTNUM) FROM PG_CATALOG.PG_CLASS PC INNER JOIN PG_CATALOG.PG_ATTRIBUTE PA ON PA.ATTRELID = PC.OID WHERE PC.RELNAME = ISC.TABLE_NAME AND PA.ATTNAME = ISC.COLUMN_NAME ) AS COMMENT FROM INFORMATION_SCHEMA.COLUMNS ISC WHERE ISC.TABLE_CATALOG = ? AND ISC.TABLE_NAME = ?";
	private final String POSTGRESQL_INDEX_QUERY = "SELECT CC.TABLE_CATALOG, CC.TABLE_SCHEMA, CC.TABLE_NAME, CC.CONSTRAINT_NAME, CC.COLUMN_NAME FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS TC ,INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE CC WHERE TC.TABLE_CATALOG = ? AND TC.TABLE_NAME = ? AND TC.CONSTRAINT_TYPE = 'PRIMARY KEY' AND TC.TABLE_CATALOG = CC.TABLE_CATALOG AND TC.TABLE_SCHEMA = CC.TABLE_SCHEMA AND TC.TABLE_NAME = CC.TABLE_NAME AND TC.CONSTRAINT_NAME = CC.CONSTRAINT_NAME";

	private IConnection connection;
	IConnectionProfile profile;

	public DatabaseResource() {
	}

	public DatabaseResource(IConnectionProfile profile) {
		this.profile = profile;
		connection = new JDBCConnectionFactory().createConnection(profile);
	}

	public List<String> getDatabaseTables(IConnectionProfile profile) {

		ArrayList<String> tables = new ArrayList<String>();

		try {
			if(connection != null) {
				connection.close();
			}
			connection = new JDBCConnectionFactory().createConnection(profile);
			Connection rawConnection = (Connection) connection.getRawConnection();
			Properties baseProperties = profile.getBaseProperties();
			String vendor = baseProperties.getProperty("org.eclipse.datatools.connectivity.db.vendor").toLowerCase();

			PreparedStatement stmt = null;

			String url = baseProperties.getProperty("org.eclipse.datatools.connectivity.db.URL");
			String[] urlArray = url.split("/");

			switch (vendor) {
			case "mysql":
				stmt = rawConnection.prepareStatement(MYSQL_TABLE_QUERY);
				stmt.setString(1, urlArray[urlArray.length - 1]);
				break;
			case "oracle":
				stmt = rawConnection.prepareStatement(ORACLE_TABLE_QUERY);
				break;
			case "postgres":
				stmt = rawConnection.prepareStatement(POSTGRESQL_TABLE_QUERY);
				stmt.setString(1, urlArray[urlArray.length - 1]);
				break;
			}
			if(stmt != null) {
				ResultSet executeQuery = stmt.executeQuery();
				while(executeQuery.next()) {
					tables.add(executeQuery.getString(1));
				}
				stmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(connection != null) {
				connection.close();
			}
		}

		return tables;
	}

	public List<ColumnItem> getColumns(String tableName) {

		ArrayList<ColumnItem> columnItems = new ArrayList<ColumnItem>();

		try {
			Connection rawConnection = (Connection) connection.getRawConnection();
			Properties baseProperties = profile.getBaseProperties();
			String vendor = baseProperties.getProperty("org.eclipse.datatools.connectivity.db.vendor").toLowerCase();

			PreparedStatement stmt = null;

			String url = baseProperties.getProperty("org.eclipse.datatools.connectivity.db.URL");
			String[] urlArray = url.split("/");

			switch (vendor) {
			case "mysql":
				stmt = rawConnection.prepareStatement(MYSQL_COLUMN_QUERY);
				stmt.setString(1, urlArray[urlArray.length - 1]);
				stmt.setString(2, tableName);
				break;
			case "oracle":
				stmt = rawConnection.prepareStatement(ORACLE_COLUMN_QUERY);
				stmt.setString(1, tableName);
				break;
			case "postgres":
				stmt = rawConnection.prepareStatement(POSTGRESQL_COLUMN_QUERY);
				stmt.setString(1, urlArray[urlArray.length - 1]);
				stmt.setString(2, tableName);
				break;
			}

			if(stmt != null) {
				ResultSet executeQuery = stmt.executeQuery();
				while(executeQuery.next()) {
					ColumnItem columnItem = new ColumnItem();
					columnItem.setColumnName(executeQuery.getString(1));
					columnItem.setDataType(executeQuery.getString(2));
					columnItem.setComments(executeQuery.getString(3));
					columnItems.add(columnItem);
				}
				stmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return columnItems;
	}

	public List<String> getIndexColumns(String tableName) {

		ArrayList<String> columnItems = new ArrayList<String>();

		try {
			Connection rawConnection = (Connection) connection.getRawConnection();
			Properties baseProperties = profile.getBaseProperties();
			String vendor = baseProperties.getProperty("org.eclipse.datatools.connectivity.db.vendor").toLowerCase();

			PreparedStatement stmt = null;

			String url = baseProperties.getProperty("org.eclipse.datatools.connectivity.db.URL");
			String[] urlArray = url.split("/");

			switch (vendor) {
			case "mysql":
				stmt = rawConnection.prepareStatement(MYSQL_INDEX_QUERY.replace("?", tableName));
				break;
			case "oracle":
				stmt = rawConnection.prepareStatement(ORACLE_INDEX_QUERY);
				stmt.setString(1, tableName);
				break;
			case "postgres":
				stmt = rawConnection.prepareStatement(POSTGRESQL_INDEX_QUERY);
				stmt.setString(1, urlArray[urlArray.length - 1]);
				stmt.setString(2, tableName);
				break;
			}

			if(stmt != null) {
				ResultSet executeQuery = stmt.executeQuery();
				while(executeQuery.next()) {
					columnItems.add(executeQuery.getString(5));
				}
				stmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return columnItems;
	}

	@Override
	protected void finalize() throws Throwable {
		if(connection != null) {
			connection.close();
		}
		super.finalize();
	}
}
