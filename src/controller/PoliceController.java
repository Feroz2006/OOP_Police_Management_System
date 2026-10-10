package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseUtility;
import model.*;

public class PoliceController {
    private PoliceController() {}
    public static List<CaseRecord> getCases(Police police) {
        List<CaseRecord> cases = new ArrayList<>();
        DatabaseUtility dbUtility = new DatabaseUtility();
        try {
            ResultSet rs = dbUtility.executeQuery(
                "SELECT * FROM `CASE` WHERE officer_username = ?",
                police.getUsername()
            );
            while (rs.next()) {
                cases.add(new CaseRecord(
                    rs.getString("case_id"),
                    rs.getString("description"),
                    rs.getString("status"),
                    rs.getString("officer_username"),
                    rs.getString("victim_username"),
                    rs.getString("accused_username")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cases;
    }

    public static CaseRecord createCase(String caseId, String description, String victimUsername,
            String accusedUsername, String officerUsername) {
        DatabaseUtility dbUtility = new DatabaseUtility();
        try {
            dbUtility.executeUpdate(
                "INSERT INTO `CASE` (case_id, status, description, officer_username, victim_username, accused_username) VALUES (?, ?, ?, ?, ?, ?)",
                caseId, "OPEN", description, officerUsername, victimUsername, accusedUsername
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new CaseRecord(caseId, description, "OPEN", officerUsername, victimUsername, accusedUsername);
    }

    public static CaseRecord updateCase(CaseRecord oldRecord, String description, String status) {
        DatabaseUtility dbUtility = new DatabaseUtility();
        try {
            dbUtility.executeUpdate(
                "UPDATE `CASE` SET description = ?, status = ? WHERE case_id = ?",
                description, status, oldRecord.getCaseId()
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new CaseRecord(oldRecord.getCaseId(), description, status, oldRecord.getOfficerUsername(), oldRecord.getVictimUsername(), oldRecord.getAccusedUsername());
    }

    public static void deleteCase(CaseRecord record) {
        DatabaseUtility dbUtility = new DatabaseUtility();
        try {
            dbUtility.executeUpdate(
                "DELETE FROM `CASE` WHERE case_id = ?",
                record.getCaseId()
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
