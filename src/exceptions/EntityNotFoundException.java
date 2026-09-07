package exceptions;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, String id) {
        super(entityName + " with ID " + id + " was not found");
    }
}
