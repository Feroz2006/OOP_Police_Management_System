package ui;

import java.awt.*;

import javax.swing.*;

import model.*;

public class HomeFrame extends JFrame implements UI_Interface {

    Person currentUser;
    JPanel userInfo;
    JPanel personPanel;
    JButton logoutButton;
    JLabel username;
    JLabel role;
    JLabel id;

    HomeFrame(Person currentUser) {
        super("Home");
        this.currentUser = currentUser;

        // X button makes the window close
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Prevents user from resizing the window
        setResizable(false);

        // Interface methods
        initializeComponents();
        initializeListeners();
        initializeLayout();
    }

    private void initializeComponents() {
        // Upcasting
        if (currentUser instanceof Police)
            personPanel = new PolicePanel((Police) currentUser);
        else if (currentUser instanceof Victim)
            personPanel = new VictimPanel();
        else if (currentUser instanceof Accused)
            personPanel = new AccusedPanel();
        else
            personPanel = new JPanel();

        // username = new JLabel(currentUser.getName());
        // id = new JLabel(currentUser.getPersonId());
        // role = new JLabel(currentUser.getRole());
        userInfo = new JPanel();
        username = new JLabel("Placeholder Username");
        id = new JLabel("Placeholder ID");
        role = new JLabel("Placeholder Role");
        // TODO: Remove uncomment code and remove placeholders once Person is
        // implemented
        logoutButton = new JButton("Logout");
    }

    private void initializeLayout() {
        setLayout(new BorderLayout(0, 30));
        userInfo.add(username);
        userInfo.add(id);
        userInfo.add(role);

        add(userInfo, BorderLayout.NORTH);
        add(personPanel, BorderLayout.CENTER);
        add(logoutButton, BorderLayout.SOUTH);

        pack();

        // Centers window on screen
        setLocationRelativeTo(null);
    }

    private void initializeListeners() {
        logoutButton.addActionListener(e -> logout());
    }

    private void logout() {
        logoutButton.setEnabled(false);
        dispose();
        LoginFrame loginFrame = new LoginFrame();
        loginFrame.setVisible(true);
    }
}
