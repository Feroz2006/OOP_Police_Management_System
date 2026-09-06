package ui;

import java.awt.*;
import javax.swing.*;

import model.Police;

public class PolicePanel extends JPanel implements UI_Interface {
    JLabel title;

    // Evidence
    JPanel evidencePanel;
    JLabel evidenceTitle;
    JButton addEvidence;
    JButton updateEvidence;
    JButton viewEvidence;

    // Cases
    JPanel casePanel;
    JLabel caseTitle;
    JButton createCase;
    JButton updateCase;
    JButton viewCase;
    JButton assignOfficer;

    // FIR
    JPanel firPanel;
    JLabel firTitle;
    JButton registerFir;

    PolicePanel(Police p) {
        super();

        initializeComponents();
        initializeListeners();
        initializeLayout();
    }

    private void initializeComponents() {
        title = new JLabel("Police Dashboard");
        title.setHorizontalAlignment(SwingConstants.CENTER);

        // Evidence
        evidencePanel = new JPanel();
        evidenceTitle = new JLabel("Evidence Management");
        evidenceTitle.setHorizontalAlignment(SwingConstants.CENTER);
        addEvidence = new JButton("Add Evidence");
        updateEvidence = new JButton("Update Evidence");
        viewEvidence = new JButton("View Evidence");

        // Cases
        casePanel = new JPanel();
        caseTitle = new JLabel("Case Management");
        caseTitle.setHorizontalAlignment(SwingConstants.CENTER);
        createCase = new JButton("Create Case");
        updateCase = new JButton("Update Case");
        viewCase = new JButton("View Case");
        assignOfficer = new JButton("Assign Officer");

        // FIR
        firPanel = new JPanel();
        firTitle = new JLabel("FIR Management");
        firTitle.setHorizontalAlignment(SwingConstants.CENTER);
        registerFir = new JButton("Register FIR");

    }

    private void initializeListeners() {
        registerFir.addActionListener(e -> registerFir());
        createCase.addActionListener(e -> createCase());
        updateCase.addActionListener(e -> updateCase());
        addEvidence.addActionListener(e -> addEvidence());
        updateEvidence.addActionListener(e -> updateEvidence());
        viewCase.addActionListener(e -> viewCase());
        assignOfficer.addActionListener(e -> assignOfficer());
    }

    private void initializeLayout() {
        // Overall panel uses GridLayout (For titles and subpanels)
        // Each subpanel uses FlowLayout to hold Buttons
        this.setLayout(new GridLayout(7, 1, 20, 20));
        evidencePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        casePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        firPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));

        this.add(title);
        
        // Evidence
        this.add(evidenceTitle);
        evidencePanel.add(addEvidence);
        evidencePanel.add(updateEvidence);
        evidencePanel.add(viewEvidence);
        this.add(evidencePanel);

        // Cases
        this.add(caseTitle);
        casePanel.add(createCase);
        casePanel.add(viewCase);
        casePanel.add(updateCase);
        casePanel.add(assignOfficer);
        this.add(casePanel);


        // FIR
        this.add(firTitle);
        firPanel.add(registerFir);
        this.add(firPanel);
    }

    private void registerFir() {

    }

    private void createCase() {

    }

    private void updateCase() {

    }

    private void addEvidence() {

    }

    private void updateEvidence() {

    }

    private void assignOfficer() {

    }

    private void viewCase() {

    }

}
