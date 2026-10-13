package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseUtility;
import model.CaseRecord;

public class VictimController {
    private VictimController() {}
    public static List<CaseRecord> getCases(String username) {
        List<CaseRecord> cases = new ArrayList<>();
        DatabaseUtility dbUtility = new DatabaseUtility();
        try {
            ResultSet rs = dbUtility.executeQuery(
                "SELECT * FROM `CASE` WHERE victim_username = ?",
                username
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
}
