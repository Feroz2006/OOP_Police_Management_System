package model;

public class Accused extends Person{
    private String Charges;
    private boolean BailStatus;
    public Accused(String personID,String name,int age,String phone,String username,String password,String Charges,boolean BailStatus){
        super(personID,name,age,phone,username,password);
        this.Charges=Charges;
        this.BailStatus=BailStatus;
    }   
    public String getCharges(){
        return Charges;
    }
    public boolean isBailStatus(){
        return BailStatus;
    }
    public void setCharges(String newCharges){
        this.Charges=newCharges;
    }
    public void setBailStatus(boolean newBailStatus){
        this.BailStatus=newBailStatus;
    }
    public void viewCase(String caseID){
        System.out.println("Accused viewing legal charges for case: "+caseID);
    }
    public String getRole(){
        return UserRole.ACCUSED.name();
    }
}
