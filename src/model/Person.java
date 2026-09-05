package model;

public abstract class Person {
    private String personID;
    private String name;
    private int age;
    private String phone;
    private String username;
    private String password;
    public Person(String personID,String name,int age,String phone,String username,String password){
        this.personID=personID;
        this.name=name;
        this.age=age;
        this.phone=phone;
        this.username=username;
        this.password=password;
    }
    public abstract void viewCase(String caseID);
    public String getPersonID(){
        return personID;
    }
    public String getName(){
        return name;
    }
    public int getAge(){
        return age;
    }
    public String getPhone(){
        return phone;
    }
    public String getUsername(){
        return username;
    }
    public String getPassword(){
        return password;
    }
    public void setName(String newname){
        this.name= newname;
    }
    public void setAge(int newage){
        this.age=newage;
    }
    public void setPhone(String newphone){
        this.phone=newphone;
    }
    public boolean validateCredentials(String inputusername,String inputPassword){
        return inputPassword.equals(password)&&inputusername.equals(username);
    }
    public abstract String getRole();
}