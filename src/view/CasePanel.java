package view;

import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.*;

import controller.LoginController;
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
    JComboBox<String> status;
    JLabel officerName;
    JTextField victimName;
    JTextField accusedName;
    JButton saveButton;
    JButton deleteButton;

    CasePanel(CaseRecord caseRecord) {
        super();
        this.currentUser = LoginController.getCurrentUser();
        this.caseRecord = caseRecord;
        this.creatingCase = false;

        // Upcasting
        if (currentUser instanceof Police)
            this.readOnly = false;
        else
            this.readOnly = true;
        updateUIState();
    }

    // This constructor is called to Create new CaseRecords in the UI
    CasePanel() {
        super();
        this.currentUser = LoginController.getCurrentUser();
        this.caseRecord = null;
        this.creatingCase = true;
        this.readOnly = false;

        updateUIState();
    }

    private void updateUIState() {
        // Remove all components if any were added
        this.removeAll();

        // Create components, add them, set layout and listeners
        initializeComponents();
        initializeListeners();
        initializeLayout();

        // Revalidate recalculates the Layout if components have changes size, position
        // or layout
        this.revalidate();

        // Draws the components again on the screen
        this.repaint();
    }

    private void initializeComponents() {
        // Default values to be displayed when creating a new case
        String idText = "";
        String officerText = currentUser.getUsername();
        String victimText = "";
        String accusedText = "";
        String descriptionText = "";

        // ComboBox is an option selection menu, when creating a case it is set to OPEN
        String[] statusOptions = { "OPEN", "CLOSED" };
        status = new JComboBox<>(statusOptions);
        // Sets default selected option
        status.setSelectedItem("OPEN");
        // Use (String) status.getSelectedItem() to get the selected option

        // Changing default values if cases are to be viewed not created
        if (!creatingCase) {
            idText = caseRecord.getCaseId();
            officerText = caseRecord.getOfficerUsername();
            victimText = caseRecord.getVictimUsername();
            accusedText = caseRecord.getAccusedUsername();
            descriptionText = caseRecord.getDescription();

            // Sets selected option to current case status
            status.setSelectedItem(caseRecord.getStatus());
        }

        // Initializing Components
        saveButton = new JButton("Save");
        deleteButton = new JButton("Delete");
        id = new JTextField(idText);
        officerName = new JLabel(officerText, SwingConstants.LEFT);
        victimName = new JTextField(victimText);
        accusedName = new JTextField(accusedText);
        description = new JTextArea(descriptionText);

        // Moves overflowing text to the next line
        description.setLineWrap(true);

        // Setting Editable state
        id.setEditable(creatingCase);
        status.setEnabled(!(creatingCase || readOnly));
        victimName.setEditable(creatingCase);
        accusedName.setEditable(creatingCase);
    }

    private void initializeListeners() {
        if (readOnly) {
            saveButton.setEnabled(false);
            deleteButton.setEnabled(false);
            description.setEditable(false);
            return;
        }

        if (creatingCase) {
            saveButton.addActionListener(e -> createCase());
            deleteButton.setEnabled(false);
            return;
        }

        saveButton.addActionListener(e -> updateCase());
        deleteButton.addActionListener(e -> deleteCase());
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
        add(saveButton);
        add(deleteButton);
    }

    private void createCase() {
        if (victimName.getText().equals(currentUser.getUsername()) || accusedName.getText().equals(currentUser.getUsername())) {
            JOptionPane.showMessageDialog(null, "Officers cannot register themselves as victim/accused",
                    "Permission Error", JOptionPane.WARNING_MESSAGE);
            this.creatingCase = true;
        }
                
        else {
            this.caseRecord = PoliceController.createCase(id.getText(), description.getText(), victimName.getText(),
                    accusedName.getText(), officerName.getText());
            this.creatingCase = false;
        }
        updateUIState();
    }

    private void updateCase() {
        if (victimName.getText().equals(currentUser.getUsername()) || accusedName.getText().equals(currentUser.getUsername()))
            JOptionPane.showMessageDialog(null, "Officers cannot update cases where they are the victim/accused",
                    "Permission Error", JOptionPane.WARNING_MESSAGE);
        else
            this.caseRecord = PoliceController.updateCase(this.caseRecord, description.getText(),
                    (String) status.getSelectedItem());
        updateUIState();
    }

    private void deleteCase() {
        if (victimName.getText().equals(currentUser.getUsername()) || accusedName.getText().equals(currentUser.getUsername())) {
            JOptionPane.showMessageDialog(null, "Officers cannot delete cases where they are the victim/accused",
                    "Permission Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        PoliceController.deleteCase(this.caseRecord);

        // Removing this Component from its Container
        Container parent = getParent();
        parent.remove(this);
        parent.revalidate();
        parent.repaint();
    }
}
