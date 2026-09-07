package view;

import java.awt.*;
import javax.swing.*;

import model.Police;

public class PolicePanel extends JPanel {
    JLabel title;

    // Cases
    JPanel casePanel;
    JLabel caseTitle;
    JButton createCase;
    JButton updateCase;
    JButton viewCase;

    PolicePanel(Police p) {
        super();

        initializeComponents();
        initializeListeners();
        initializeLayout();
    }

    private void initializeComponents() {
        title = new JLabel("Police Dashboard");
        title.setHorizontalAlignment(SwingConstants.CENTER);

        // Cases
        casePanel = new JPanel();
        caseTitle = new JLabel("Case Management");
        caseTitle.setHorizontalAlignment(SwingConstants.CENTER);
        createCase = new JButton("Create Case");
        updateCase = new JButton("Update Case");
        viewCase = new JButton("View Case");

    }

    private void initializeListeners() {
        createCase.addActionListener(e -> createCase());
        updateCase.addActionListener(e -> updateCase());
        viewCase.addActionListener(e -> viewCase());
    }

    private void initializeLayout() {
        // Overall panel uses GridLayout (For titles and subpanels)
        // Each subpanel uses FlowLayout to hold Buttons
        this.setLayout(new GridLayout(3, 1, 20, 20));
        casePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));

        this.add(title);

        // Cases
        this.add(caseTitle);
        casePanel.add(createCase);
        casePanel.add(viewCase);
        casePanel.add(updateCase);
        this.add(casePanel);
    }

    private void createCase() {

    }

    private void updateCase() {

    }

    private void viewCase() {

    }

}
