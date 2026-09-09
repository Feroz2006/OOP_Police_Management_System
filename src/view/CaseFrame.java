package view;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.*;

import controller.*;
import model.*;

public class CaseFrame extends JFrame {
    Person currentUser;
    JPanel caseList;
    JScrollPane scrollPane;
    JButton createCaseButton;
    CasePanel newCasePanel;

    CaseFrame(Person currentUser) {
        super("Home");

        this.currentUser = currentUser;

        // X button makes the window close
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Prevents user from resizing the window
        setResizable(false);


        initializeComponents();
        initializeListeners();
        initializeLayout();
    }

    private void initializeComponents () {
        caseList = new JPanel();
        caseList.setLayout(new BoxLayout(caseList, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(caseList);
        createCaseButton = new JButton("Create Case");
        // Create case visible only if user is Police Officer
        createCaseButton.setVisible(currentUser instanceof Police);
    }

    private void initializeListeners() {
        if (currentUser instanceof Police) {
            createCaseButton.addActionListener(e -> {
                // if a new case is already being created, do nothing
                if (newCasePanel != null && newCasePanel.creatingCase)
                    return;

                newCasePanel = new CasePanel((Police) currentUser);
                caseList.add(newCasePanel);

                // Revalidate recalculates the Layout if components have changes size, position or layout
                caseList.revalidate();

                // Draws the components again on the screen
                caseList.repaint();
            });
        }
    }

    private void initializeLayout() {
        setLayout(new BorderLayout(5, 5));
        List<CaseRecord> cases = List.of();

        if (currentUser instanceof Police) {
            cases = PoliceController.getCases((Police) currentUser);
            add(createCaseButton, BorderLayout.NORTH);
        } else if (currentUser instanceof Victim) {
            cases = VictimController.getCases((Victim) currentUser);
        } else if (currentUser instanceof Accused) {
            cases = AccusedController.getCases((Accused) currentUser);
        }

        for (CaseRecord caseRecord : cases)
            caseList.add(new CasePanel(currentUser, caseRecord));

        add(scrollPane, BorderLayout.CENTER);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }
}
