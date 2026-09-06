package database;

import java.sql.*;
import exception.FatalDBException;

public class DatabaseInitializer {
    DatabaseConnection dbConnectionClass;
    Connection dbConnection;

    static final String DATABASE_NAME = "PoliceDB";

    public DatabaseInitializer() {
        dbConnectionClass = DatabaseConnection.getInstance();
        dbConnection = dbConnectionClass.getConnection();
    }

    /**
     * Initializes the database by creating the PoliceDB database and using it.
     */
    public void initializeDatabase() {
        if (!dbConnectionClass.isConnected())
            throw new FatalDBException(new SQLException("Database connection is not established."));

        try (Statement stmt = dbConnection.createStatement()) {
            // Create PoliceDB database if it doesn't exist
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME);
            stmt.executeUpdate("USE " + DATABASE_NAME);
        } catch (SQLException e) {
            throw new FatalDBException(e);
        }
    }

    /**
     * Creates the necessary tables in the database.
     */
    public void createTables() {
        if (!dbConnectionClass.isConnected())
            throw new FatalDBException(new SQLException("Database connection is not established."));

        try (Statement stmt = dbConnection.createStatement()) {

            /*
             * Create tables as necessary.
             */

        } catch (SQLException e) {
            throw new FatalDBException(e);
        }
    }

    /**
     * Drops the necessary tables in the database for resetting the database. This
     * method should be used with caution, as it will remove all data in the
     * specified tables.
     * Also closes the database connection after dropping the tables.
     */
    public void dropTables() {
        if (!dbConnectionClass.isConnected())
            throw new FatalDBException(new SQLException("Database connection is not established."));

        try (Statement stmt = dbConnection.createStatement()) {

            /*
             * Drop tables as necessary.
             */

            dbConnectionClass.closeConnection();
        } catch (SQLException e) {
            throw new FatalDBException(e);
        }
    }
}