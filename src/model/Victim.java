package model;

public class Victim extends Person{
    private String CrimeDetails;
    private String EmergencyContact;
    public Victim(String personID,String name,int age,String phone,String username,String password,String CrimeDetails,String EmergencyContact){
        super(personID,name,age,phone,username,password);
        this.CrimeDetails=CrimeDetails;
        this.EmergencyContact=EmergencyContact;
    }
    public String getCrimeDetails(){
        return CrimeDetails;
    }
    public String getEmergencyContact(){
        return EmergencyContact;
    }
    public void setCrimeDetails(String newCrimeDetails){
        this.CrimeDetails=newCrimeDetails;
    }
    public void setEmergencyContact(String newEmergencyContact){
        this.EmergencyContact=newEmergencyContact;
    }
    public void viewCase(String caseID){
        System.out.println("Victim viewing public details and status of case: "+caseID);
    }
    public String getRole(){
        return UserRole.VICTIM.name();
    }
}
