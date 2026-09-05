package exception;

public class AuthenticationException extends Exception {
    private String attemptedUsername;
    public AuthenticationException(String message, String attemptedUsername){
        super(message);
        this.attemptedUsername=attemptedUsername;
    }
    public String getAttemptedUsername(){
        return attemptedUsername;
    }
}
