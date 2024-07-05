package code.with.vanilson.studentmanagement.common.exception;

/**
 * EntityAlreadyExistsException
 *
 * @author vamuhong
 * @version 1.0
 * @since 2024-07-05
 */
public class EntityAlreadyExistsException extends RuntimeException {
    public EntityAlreadyExistsException(String message) {
        super(message);
    }
}