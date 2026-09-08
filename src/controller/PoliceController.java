package controller;

import java.util.List;

import model.*;

public class PoliceController {
    public static List<CaseRecord> getCases(Police police) {
        return List.of();
    }

    public static void createCase(String caseId, String description, String victimUsername, String accusedUsername) {
        System.out.println("Case ID: " + caseId);
        System.out.println("Description: " + description);
        System.out.println("Victim Username: " + victimUsername);
        System.out.println("Accused Username: " + accusedUsername);
    }

    public static void updateCase(Person currentUser, String caseId, String description, String status) {
        System.out.println("Case ID: " + caseId);
        System.out.println("Description: " + description);
        System.out.println("Status: " + status);
    }
}
