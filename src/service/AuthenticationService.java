package service;

import model.Person;
import exception.AuthenticationException;
import dao.PersonDAO;

public class AuthenticationService {

    private PersonDAO persondao;

    public AuthenticationService(PersonDAO persondao) {
        this.persondao = persondao;
    }

    public Person login(String username, String password) throws AuthenticationException {
        Person foundPerson = persondao.getPersonByUsername(username);

        if (foundPerson == null) {
            throw new AuthenticationException("User not found", username);
        }

        if (!foundPerson.validateCredentials(username, password)) {
            throw new AuthenticationException("Invalid password", username);
        }

        return foundPerson;
    }
}