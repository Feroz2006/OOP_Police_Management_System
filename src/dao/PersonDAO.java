package dao;
import model.Person;
import java.util.HashMap;
import java.util.Map;
public class PersonDAO{
    private Map<String,Person> userDatabase;
    public PersonDAO(){
        userDatabase=new HashMap<>();
    }public void savePerson(Person person){
        userDatabase.put(person.getUsername(), person);
    }
    public Person getPersonByUsername(String username){
        return userDatabase.get(username);
    }
}