package controller;

import java.sql.ResultSet;
import java.sql.SQLException;

import database.DatabaseUtility;
import exceptions.AuthenticationException;
import model.*;

public class LoginController {
    private static Person loggedinUser;

    public static Person login(String username, String password) throws AuthenticationException {
        DatabaseUtility dbUtility = new DatabaseUtility();
        try {
            ResultSet rs = dbUtility.executeQuery(
                    "SELECT * FROM PERSON WHERE username = ? AND password = ?",
                    username, password);

            if (!rs.next()) {
                throw new AuthenticationException("Invalid username or password", username);
            }

            String personId = rs.getString("person_id");
            String name = rs.getString("name");
            int age = rs.getInt("age");
            String phone = rs.getString("phone");
            String role = rs.getString("role");

            switch (role) {
                case "POLICE":
                    ResultSet policeRs = dbUtility.executeQuery(
                            "SELECT * FROM POLICE WHERE person_id = ?",
                            personId);
                    if (policeRs.next()) {
                        loggedinUser = new Police(
                                personId, name, age, phone, username, password,
                                policeRs.getString("rank"),
                                policeRs.getString("department"),
                                policeRs.getString("badge_number"));
                    }
                    break;
                case "VICTIM":
                    ResultSet victimRs = dbUtility.executeQuery(
                            "SELECT * FROM VICTIM WHERE person_id = ?",
                            personId);
                    if (victimRs.next()) {
                        loggedinUser = new Victim(
                                personId, name, age, phone, username, password,
                                victimRs.getString("crime_details"),
                                victimRs.getString("emergency_contact"));
                    }
                    break;
                case "ACCUSED":
                    ResultSet accusedRs = dbUtility.executeQuery(
                            "SELECT * FROM ACCUSED WHERE person_id = ?",
                            personId);
                    if (accusedRs.next()) {
                        loggedinUser = new Accused(
                                personId, name, age, phone, username, password,
                                accusedRs.getString("charges"),
                                accusedRs.getBoolean("bail_status"));
                    }
                    break;
                default:
                    throw new AuthenticationException("Invalid role", username);
            }

            if (loggedinUser == null) {
                throw new AuthenticationException("User data not found", username);
            }

            return loggedinUser;
        } catch (SQLException e) {
            throw new AuthenticationException("Database error during authentication", username);
        }
    }

    public static void logout() {
        loggedinUser = null;
    }

    public static Person getCurrentUser() {
        return loggedinUser;
    }
}
