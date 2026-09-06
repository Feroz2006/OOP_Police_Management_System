package database;

import java.sql.*;
import exception.FatalDBException;

/**
 * Singleton class for managing the database connection.
 */
public class DatabaseConnection {
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
            throw new FatalDBException(e);
        } catch (SQLException e) {
            throw new FatalDBException(e);
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
                throw new FatalDBException(e); // idk what could be done in this situation, so just throw a fatal
                                               // exception and exit the program
            }
        }
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
            throw new FatalDBException(e); // idk why isClosed() throws SQLException, but it does, so we need to handle
                                           // it
        }
    }
}
