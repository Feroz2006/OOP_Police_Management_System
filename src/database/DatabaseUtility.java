package database;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;

public class DatabaseUtility {
    private final Connection dbConnection;

    public DatabaseUtility() {
        dbConnection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Executes a parameterized SELECT query and returns its result set.
     * The caller must close the returned result set.
     */
    public ResultSet executeQuery(String sql, Object... parameters) throws SQLException {
        PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
        bindParameters(preparedStatement, parameters);
        return preparedStatement.executeQuery();
    }

    /**
     * Executes a parameterized INSERT, UPDATE, or DELETE statement.
     *
     * @return the number of affected rows
     */
    public int executeUpdate(String sql, Object... parameters) throws SQLException {
        PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
        bindParameters(preparedStatement, parameters);
        return preparedStatement.executeUpdate();
    }

    /**
     * Closes a JDBC or other closeable resource without failing for null.
     */
    public void close(AutoCloseable resource) throws SQLException {
        if (resource == null) {
            return;
        }

        try {
            resource.close();
        } catch (Exception e) {
            if (e instanceof SQLException) {
                throw (SQLException) e;
            }
            throw new SQLException("Unable to close database resource", e);
        }
    }

    private void bindParameters(PreparedStatement statement, Object... parameters)
            throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            statement.setObject(i + 1, parameters[i]);
        }
    }
}
