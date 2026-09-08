package view;

import java.awt.GridLayout;

import javax.swing.*;

import controller.PoliceController;
import model.CaseRecord;
import model.Person;
import model.Police;

public class CasePanel extends JPanel {
    Person currentUser;
    boolean readOnly;
    boolean creatingCase;
    CaseRecord caseRecord;
    JTextField id;
    JTextArea description;
    JTextField status;
    JLabel officerName;
    JTextField victimName;
    JTextField accusedName;
    JButton saveButton;

    CasePanel(Person currentUser, CaseRecord caseRecord) {
        super();
        this.currentUser = currentUser;
        this.caseRecord = caseRecord;
        this.creatingCase = false;

        // Upcasting
        if (currentUser instanceof Police)
            this.readOnly = false;
        else
            this.readOnly = true;

        initializeComponents();
        initializeListeners();
        initializeLayout();
    }

    CasePanel(Police currentUser) {
        super();
        this.currentUser = currentUser;
        this.caseRecord = null;
        this.creatingCase = true;
        this.readOnly = false;

        initializeComponents();
        initializeListeners();
        initializeLayout();
    }

    private void initializeComponents() {
        saveButton = new JButton("Save");

        if (creatingCase) {
            id = new JTextField();
            status = new JTextField("OPEN");
            officerName = new JLabel(currentUser.getUsername(), SwingConstants.LEFT);
            victimName = new JTextField();
            accusedName = new JTextField();
            description = new JTextArea();
            status.setEditable(false);
            return;
        }

        id = new JTextField(caseRecord.getCaseId());
        status = new JTextField(caseRecord.getStatus());
        officerName = new JLabel(caseRecord.getOfficerUsername(), SwingConstants.LEFT);
        victimName = new JTextField(caseRecord.getVictimUsername());
        accusedName = new JTextField(caseRecord.getAccusedUsername());
        description = new JTextArea(caseRecord.getDescription());
        id.setEditable(false);
        victimName.setEditable(false);
        accusedName.setEditable(false);

    }

    private void initializeListeners() {
        if (readOnly) {
            saveButton.setVisible(false);
            status.setEditable(false);
            description.setEditable(false);
            return;
        }

        if (creatingCase) {
            saveButton.addActionListener(e -> PoliceController.createCase(
                    id.getText(),
                    description.getText(),
                    victimName.getText(),
                    accusedName.getText()));
            return;
        }

        saveButton.addActionListener(e -> PoliceController.updateCase(
                currentUser,
                caseRecord.getCaseId(),
                description.getText(),
                status.getText()));
    }

    private void initializeLayout() {
        setLayout(new GridLayout(7, 2, 5, 5));

        add(new JLabel("Case ID:"));
        add(id);
        add(new JLabel("Status:"));
        add(status);
        add(new JLabel("Officer:"));
        add(officerName);
        add(new JLabel("Victim:"));
        add(victimName);
        add(new JLabel("Accused:"));
        add(accusedName);
        add(new JLabel("Description:"));
        add(description);
        add(new JLabel());
        add(saveButton);
    }
}
