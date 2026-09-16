package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseUtility;
import model.Accused;
import model.CaseRecord;

public class AccusedController {
    public static List<CaseRecord> getCases(Accused accused) {
        List<CaseRecord> cases = new ArrayList<>();
        DatabaseUtility dbUtility = new DatabaseUtility();
        try {
            ResultSet rs = dbUtility.executeQuery(
                "SELECT * FROM `CASE` WHERE accused_username = ?",
                accused.getUsername()
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
