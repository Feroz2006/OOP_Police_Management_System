package test;

import database.DatabaseConnection;
import database.DatabaseInitializer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class DBTest {
	private static final String DATABASE_NAME = "PoliceDB";

	private static final List<String> EXPECTED_TABLES = Arrays.asList(
			"PERSON",
			"POLICE",
			"VICTIM",
			"ACCUSED",
			"FIR",
			"CASE",
			"CASE_ACCUSED",
			"CASE_OFFICER",
			"EVIDENCE"
	);

	public static void main(String[] args) {
		DatabaseConnection databaseConnection = DatabaseConnection.getInstance();

		try {
			new DatabaseInitializer().initializeDatabase();
			verifyDatabase(databaseConnection.getConnection());
			new DatabaseInitializer().dropTables();
			System.out.println("Database initialization test passed.");
		} catch (SQLException e) {
			throw new AssertionError("Database verification failed.", e);
		} finally {
			databaseConnection.closeConnection();
		}
	}

	private static void verifyDatabase(Connection connection) throws SQLException {
		try (PreparedStatement databaseStatement = connection.prepareStatement("SELECT DATABASE()");
			 ResultSet databaseResult = databaseStatement.executeQuery()) {
			if (!databaseResult.next() || !DATABASE_NAME.equals(databaseResult.getString(1))) {
				throw new AssertionError("Expected active database to be " + DATABASE_NAME);
			}
		}

		String tableQuery = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES "
				+ "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ?";
		try (PreparedStatement tableStatement = connection.prepareStatement(tableQuery)) {
			for (String tableName : EXPECTED_TABLES) {
				tableStatement.setString(1, tableName);
				try (ResultSet tableResult = tableStatement.executeQuery()) {
					tableResult.next();
					if (tableResult.getInt(1) != 1) {
						throw new AssertionError("Missing table: " + tableName);
					}
				}
			}
		}
	}
}
