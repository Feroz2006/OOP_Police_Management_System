package view;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.*;

import controller.*;
import model.*;

public class CaseFrame extends JFrame {
    Person currentUser;
    JPanel caseList;
    JScrollPane scrollPane;
    JButton createCaseButton;
    JButton logoutButton;
    CasePanel newCasePanel;

    CaseFrame() {
        super("Home");

        this.currentUser = LoginController.getCurrentUser();

        // X button makes the window close
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        setSize(800, 600);
        setLocationRelativeTo(null);


        initializeComponents();
        initializeListeners();
        initializeLayout();
    }

    private void initializeComponents() {
        caseList = new JPanel();
        caseList.setLayout(new BoxLayout(caseList, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(caseList);
        createCaseButton = new JButton("Create Case");
        logoutButton = new JButton("Logout");
        
        // Create case visible only if user is Police Officer, if not the button is invisible and not added to the frame
        createCaseButton.setVisible(currentUser instanceof Police);
    }

    private void initializeListeners() {
        logoutButton.addActionListener(e -> logout());
        if (currentUser instanceof Police) {
            createCaseButton.addActionListener(e -> createCase());
        }
    }

    private void logout() {
        LoginController.logout();

        JFrame loginFrame = new LoginFrame();
        loginFrame.setVisible(true); // Makes window visible

        dispose();

    }

    private void createCase() {
        // if a new case is already being created, do nothing
        if (newCasePanel != null && newCasePanel.creatingCase)
            return;

        newCasePanel = new CasePanel();
        caseList.add(newCasePanel);

        // Revalidate recalculates the Layout if components have changes size, position
        // or layout
        caseList.revalidate();

        // Draws the components again on the screen
        caseList.repaint();

    }

    private void initializeLayout() {
        setLayout(new BorderLayout(5, 5));
        ArrayList<CaseRecord> cases = new ArrayList<>();
        add(logoutButton, BorderLayout.SOUTH);

        String username = currentUser.getUsername();
        cases.addAll(PoliceController.getCases(username));
        cases.addAll(VictimController.getCases(username));
        cases.addAll(AccusedController.getCases(username));

        if (currentUser instanceof Police) {
            add(createCaseButton, BorderLayout.NORTH);
        }

        for (CaseRecord caseRecord : cases)
            caseList.add(new CasePanel(caseRecord));

        add(scrollPane, BorderLayout.CENTER);
    }
}
