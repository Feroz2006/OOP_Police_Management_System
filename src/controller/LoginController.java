package controller;

import exception.AuthenticationException;
import model.Person;
import service.AuthenticationService;
import dao.PersonDAO;

public class LoginController {
    private static Person loggedinUser;
    private static AuthenticationService authService = new AuthenticationService(new PersonDAO());

    public static Person login(String username, String password) throws AuthenticationException {
        loggedinUser = authService.login(username, password);
        return loggedinUser;
    }

    public static void logout() {
        loggedinUser = null;
    }

    public static Person getCurrentUser() {
        return loggedinUser;
    }
}
