package model;

public class Police extends Person {
    private String rank;
    private String department;
    private String BadgeNumber;
    public Police(String personID,String name,int age,String phone,String username,String password,String rank,String department,String BadgeNumber){
        super(personID,name,age,phone,username,password);
        this.rank=rank;
        this.department=department;
        this.BadgeNumber=BadgeNumber;
    }
    public String getRank(){
        return rank;
    }
    public String getDepartment(){
        return department;
    }
    public String getBadgeNumber(){
        return BadgeNumber;
    }
    public void setRank(String newrank){
        this.rank=newrank;
    }
    public void setDepartment(String newdepartment){
        this.department=newdepartment;
    }
    public void setBadgeNumber(String newBadgeNumber){
        this.BadgeNumber=newBadgeNumber;
    }
    public void viewCase(String caseID){
        System.out.println("Police viewing full details of case: "+caseID);
    }
    public String getRole(){
        return UserRole.POLICE.name();
    }
}
