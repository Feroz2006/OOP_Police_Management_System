package controller;

import exceptions.AuthenticationException;
import model.Person;
import model.Police;

public class LoginController {
    private static Person loggedinUser;

    public static Person login(String username, String password) throws AuthenticationException {
        loggedinUser = new Police("Placeholder ID", "Placeholder Name", 0, "Placeholder Phone No:", "", "", "Placeholder Rank", "Placeholder Dept.", "Placeholder Badge No:" );
        return  loggedinUser;
    }

    public static void logout() {
        loggedinUser = null;
    }

    public static Person getCurrentUser() {
        return loggedinUser;
    }
}
