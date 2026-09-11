package view;

import javax.swing.*;
import java.awt.*;

import model.Person;
import controller.LoginController;
import exceptions.AuthenticationException;

public class LoginFrame extends JFrame {

    private JTextField userField; // To accept username
    private JLabel userLabel;

    private JPasswordField passField; // To accept password
    private JLabel passLabel;

    private JButton loginButton;  // To submit form

    public LoginFrame() {
        super("Login");

        // X button makes the window close
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

        // Setting window size
        setSize(500, 400);

        // Centers window on screen
        setLocationRelativeTo(null); 

        // Prevents user from resizing the window
        setResizable(false);

        initializeComponents();
        initializeListeners();
        initializeLayout();
    }

    private void initializeComponents() {
        userField = new JTextField();
        userLabel = new JLabel("Username");
        passField = new JPasswordField();
        passLabel = new JLabel("Password");
        loginButton = new JButton("Login");
    }

    private void initializeLayout() {
        // Grid layout creates a grid containing cells of equal width and height
        setLayout(new GridLayout(9, 1));

        // JPanels are used to provide padding
        add(new JPanel());
        add(userLabel);
        add(userField);
        add(new JPanel());
        add(passLabel);
        add(passField);
        add(new JPanel());
        add(loginButton);
        add(new JPanel());
    }

    private void initializeListeners() {
        loginButton.addActionListener(e -> login());
    }

    private void login() {
        // disable login button to prevent it from being spammed
        loginButton.setEnabled(false);
        String username = userField.getText();
        // getPassword() returns character array
        String password = new String(passField.getPassword());

        try {
            // login should be a static method
            LoginController.login(username, password);
            
            // Switch to HomeFrame with user details
            CaseFrame caseFrame = new CaseFrame();
            caseFrame.setVisible(true);
            
            // dispose deletes the Frame
            dispose();
        } catch (AuthenticationException e) {
            JOptionPane.showMessageDialog(null, "Invalid username or password", "Invalid Credentials", JOptionPane.WARNING_MESSAGE);
        } catch(Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}
