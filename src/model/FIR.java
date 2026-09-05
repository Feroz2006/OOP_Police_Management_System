public class FIR{
private String firId;
private LocalDate date;
private String description;
private Victim victim;
private Police registeredBy;

public FIR(String firId, LocalDate date, String description, Victim victim, Police registeredBy) {
    this.firId = firId;
    this.date = date;
    this.description = description;
    this.victim = victim;
    this.registeredBy = registeredBy;
}

public String getFirId(){
    return firId;
}

public LocalDate  getDate(){
    return date;
}

public String getLocation(){
    return location;
}

public String getDescription(){
    return description;
}

public Victim getVictim(){
    return victim;
}

public Police getRegisteredBy(){
    return registeredBy;
}

public void setLocation(){
    this.location=location;
}

public void setDescription(String description){
    this.description=description;
}

public void setDescription(String description, String additionalDetails){
    this.description=description +" "+ additionalDetails;
}

@Override
public String toString(){
    return "FIR ID: " + firId + ", Date: " + date + ", Location: " + location + ", Description: " + description;
    }

}