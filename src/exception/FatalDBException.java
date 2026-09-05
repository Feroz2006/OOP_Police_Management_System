package exception;

import java.sql.SQLException;

public class FatalDBException extends RuntimeException {
    /**
     * Constructs a new FatalDBException with the specified cause.
     * Not meant to be handled by the user, as it indicates a fatal error in the database connection.
     * <br>
     * Check if {@code lib/} contains the required SQL driver({@code lib/mysql-connector-j-26.7.0.jar}), and the compilation uses {@code javac -cp "lib/mysql-connector-j-26.7.0.jar"}
     * <br>
     * Exit the program after this exception is thrown.
     *
     * @param e the cause of the exception
     */
    public FatalDBException(ClassNotFoundException e) { 
        super("Java SQL Driver not found", e);
        System.err.println("Java SQL Driver not found. Please ensure that the required SQL driver is present in the 'lib/' directory and that the compilation uses the correct classpath.");
    }

    /**
     * Constructs a new FatalDBException with the specified cause.
     * Not meant to be handled by the user, as it indicates a fatal error in the database connection.
     * <br>
     * Exit the program after this exception is thrown.
     *
     * @param e the cause of the exception
     */
    public FatalDBException(SQLException e) {
        super("SQL Exception occurred", e);
    }
}
