package code.with.vanilson.studentmanagement.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * EntityValidationException
 *
 * @author vamuhong
 * @version 1.0
 * @since 2024-07-05
 */
@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY, reason = "Validation failed")
public class EntityValidationException extends RuntimeException {
    public EntityValidationException(String message) {
        super(message);
    }
}