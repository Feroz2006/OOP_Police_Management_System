package controller;

import exception.AuthenticationException;
import model.Person;

public class LoginController {
    public static Person login(String username, String password) throws AuthenticationException {
        return new Person();
    }

    public static void logout() {
        
    }

    public static Person getCurrentUser() {
        return new Person();
    }
}
