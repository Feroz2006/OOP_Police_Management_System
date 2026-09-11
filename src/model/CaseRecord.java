package model;

public class CaseRecord {
    private String caseId;
    private String description;
    private String status;
    private String officerUsername;
    private String victimUsername;
    private String accusedUsername;

    public CaseRecord(String caseId, String description, String status, String officerUsername, String victimUsername,
            String accusedUsername) {
        if (caseId == null || caseId.isBlank()) {
            throw new IllegalArgumentException("Case ID cannot be null or blank");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or blank");
        }
        if (officerUsername == null || officerUsername.isBlank()) {
            throw new IllegalArgumentException("Officer username cannot be null or blank");
        }
        if (victimUsername == null || victimUsername.isBlank()) {
            throw new IllegalArgumentException("Victim username cannot be null or blank");
        }
        if (accusedUsername == null || accusedUsername.isBlank()) {
            throw new IllegalArgumentException("Accused username cannot be null or blank");
        }

        this.caseId = caseId;
        this.description = description;
        this.status = status;
        this.officerUsername = officerUsername;
        this.victimUsername = victimUsername;
        this.accusedUsername = accusedUsername;
    }

    public String getCaseId() {
        return caseId;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getOfficerUsername() {
        return officerUsername;
    }

    public String getVictimUsername() {
        return victimUsername;
    }

    public String getAccusedUsername() {
        return accusedUsername;
    }

    public String toString() {
        return "CaseRecord{" +
                "caseId=" + caseId +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", officerUsername='" + officerUsername + '\'' +
                ", victimUsername='" + victimUsername + '\'' +
                ", accusedUsername='" + accusedUsername + '\'' +
                '}';
    }

}
