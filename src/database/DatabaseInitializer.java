package database;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import exceptions.DatabaseException;

public class DatabaseInitializer {
    DatabaseConnection dbConnectionClass;
    Connection dbConnection;

    static final String DATABASE_NAME = "PoliceDB";

    public DatabaseInitializer() {
        dbConnectionClass = DatabaseConnection.getInstance();
        dbConnection = dbConnectionClass.getConnection();
    }

    public void initializeDatabase() {
        executeInitSqlScript();
    }

    private void executeInitSqlScript() {
        if (!dbConnectionClass.isConnected()) {
            throw new DatabaseException("Database connection is not established.",
                    new SQLException("Database connection is not established."));
        }

        executeSqlScript(Paths.get("sql", "database_init.sql"));
    }

    private void executeSqlScript(Path scriptPath) {
        try {
            String script = new String(Files.readAllBytes(scriptPath), StandardCharsets.UTF_8);
            try (Statement statement = dbConnection.createStatement()) {
                for (String command : script.split(";")) {
                    String trimmedCommand = command.trim();
                    if (!trimmedCommand.isEmpty()) {
                        statement.execute(trimmedCommand);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Unable to execute SQL script: " + scriptPath, e);
        } catch (java.io.IOException e) {
            throw new DatabaseException("Error reading SQL script file: " + scriptPath, e);
        }
    }

    public void dropTables() {
        if (!dbConnectionClass.isConnected()) {
            throw new DatabaseException("Database connection is not established.",
                    new SQLException("Database connection is not established."));
        }

        try (Statement statement = dbConnection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT DATABASE()")) {
            if (!resultSet.next() || !DATABASE_NAME.equals(resultSet.getString(1))) {
                return;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Unable to check the active database.", e);
        }

        executeSqlScript(Paths.get("sql", "database_drop.sql"));
        dbConnectionClass.closeConnection();
    }
}
