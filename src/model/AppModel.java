package model;

import java.util.List;

import model.CaseRecord.Status;

public class AppModel {

    public void updateCase(Person currentUser, String caseId, String description, Status status) {
        
        throw new UnsupportedOperationException("Unimplemented method 'updateCase'");
    }

    public List<CaseRecord> getVisibleCases(Person currentUser) {
        
        throw new UnsupportedOperationException("Unimplemented method 'getVisibleCases'");
    }

    public void createCase(Person currentUser, String caseId, String description, String victimUsername,
            String accusedUsername) {
        
        throw new UnsupportedOperationException("Unimplemented method 'createCase'");
    }
    
}
