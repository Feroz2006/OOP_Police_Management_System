package database;

import java.sql.*;
import exception.DatabaseException;

/**
 * Singleton class for managing the database connection.
 */
public class DatabaseConnection implements AutoCloseable {
    static private DatabaseConnection instance;
    private Connection connection;

    static final String DATABASE_URL = "jdbc:mysql://localhost:3306/";
    static final String DATABASE_USERNAME = "username"; // Replace with your database username
    static final String DATABASE_PASSWORD = "password"; // Replace with your database password

    private DatabaseConnection() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("Java SQL driver not found.", e);
        } catch (SQLException e) {
            throw new DatabaseException("Unable to connect to the database.", e);
        }
    }

    /**
     * Returns the singleton instance of DatabaseConnection.
     *
     * @return the singleton instance
     */
    public static DatabaseConnection getInstance() {

        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Returns the database connection.
     *
     * @return the database connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Closes the database connection.
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new DatabaseException("Unable to close the database connection.", e);
            }
        }
    }

    public void close() {
        closeConnection();
    }

    /**
     * Checks if the database connection is active.
     *
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            throw new DatabaseException("Unable to check the database connection.", e);
        }
    }
}
