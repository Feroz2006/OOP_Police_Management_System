import view.LoginFrame;
import javax.swing.JFrame;
import database.*;

public class Main {

    public static void main(String args[]) {
        new DatabaseInitializer().initializeDatabase(); // Initializes the database
        JFrame loginFrame = new LoginFrame();
        loginFrame.setVisible(true); // Makes window visible
        new DatabaseInitializer().dropTables(); // Resets the database by dropping existing tables
    }
    
}
