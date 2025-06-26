package Project_ITSS.common.validation;

/**
 * Common validation exception for the strategy pattern
 * Replaces scattered validation exceptions across modules
 */
public class ValidationException extends RuntimeException {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}