package controller;

import exceptions.AuthenticationException;
import exceptions.DatabaseException;

import database.DatabaseUtility;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Accused;
import model.Person;
import model.Police;
import model.UserRole;
import model.Victim;

public class LoginController {
    private static Person loggedinUser;
    private static DatabaseUtility databaseutility;

    public static Person login(String username, String password) throws AuthenticationException, DatabaseException {
        loggedinUser = null;
        if (username == null || username.isBlank() ||
            password == null || password.isBlank()){
                throw new AuthenticationException("Invalid Username or password..", username);
            }

        if (databaseutility == null){
            throw new DatabaseException("Database Utility not initialized.");
        }

        String sql = "SELECT * FROM USERS WHERE username = ?";
    

        try (ResultSet rs = databaseutility.executeQuery(sql, username)){
            if (rs.next()) {
                String role = rs.getString("role");
                String storedPassword = rs.getString("password");

                UserRole userRole = UserRole.valueOf(role.toUpperCase());

                String personID = rs.getString("person_id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String phone = rs.getString("phone");


                if (!storedPassword.equals(password)){
                    throw new AuthenticationException("Invalid Username or Password.",username);  
                }

                if (userRole == UserRole.POLICE){
                    String rank = rs.getString("rank");
                    String department = rs.getString("department");
                    String BadgeNumber = rs.getString("badge_number");

                    loggedinUser = new Police(personID, name, age, phone, username, password, rank, department, BadgeNumber );
                
                }
                else if (userRole == UserRole.ACCUSED){
                    String Charges = rs.getString("charges");
                    boolean BailStatus = rs.getBoolean("bail_status");

                    loggedinUser = new Accused(personID, name, age, phone, username, password, Charges, BailStatus);
                }

                else if (userRole == UserRole.VICTIM){
                    String CrimeDetails = rs.getString("crime_details");
                    String EmergencyContact = rs.getString("emergency_contact");

                    loggedinUser = new Victim(personID, name, age, phone, username, password, CrimeDetails, EmergencyContact);

                }
                return loggedinUser;
            }
            else{
                throw new AuthenticationException("Invalid Username or Password.",username);
            }

        }
        catch (SQLException e){
            throw new DatabaseException("Database error during login.", e);
        }
    }
        
        

        
    

    public static void initialize(DatabaseUtility utility){
        databaseutility = utility;
    }

    public static void logout() {
        loggedinUser = null;
    }

    public static Person getCurrentUser() {
        return loggedinUser;
    }
}
