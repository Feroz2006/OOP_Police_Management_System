package controller;

import java.util.List;

import model.*;
import database.DatabaseUtility;

public class PoliceController {
    public static List<CaseRecord> getCases(Police police) {
        return List.of();
    }

    public static CaseRecord createCase(String caseId, String description, String victimUsername,
            String accusedUsername, String officerUsername) {
        System.out.println("Case ID: " + caseId);
        System.out.println("Description: " + description);
        System.out.println("Victim Username: " + victimUsername);
        System.out.println("Accused Username: " + accusedUsername);

        // App Model Call here to create a new record in the database
        DatabaseUtility dbUtility = new DatabaseUtility();
        try {
            // Table schema needs updation.
            //dbUtility.executeUpdate("INSERT INTO `CASE` (case_id, description, status, officer_username, victim_username, accused_username) VALUES (?, ?, ?, ?, ?, ?)",
            //    caseId, description, "OPEN", officerUsername, victimUsername, accusedUsername);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new CaseRecord(caseId, description, "OPEN", officerUsername, victimUsername, accusedUsername);
    }

    public static CaseRecord updateCase(CaseRecord oldRecord, String description, String status) {
        System.out.println("Case ID: " + oldRecord.getCaseId());
        System.out.println("Description: " + description);
        System.out.println("Status: " + status);

        // App Model Call here to update the record in the database

        return new CaseRecord(oldRecord.getCaseId(), description, status, oldRecord.getOfficerUsername(), oldRecord.getVictimUsername(), oldRecord.getAccusedUsername());
    }

    public static void deleteCase(CaseRecord record) {
        // App Model Call here to delete the record from the database

    }
}
