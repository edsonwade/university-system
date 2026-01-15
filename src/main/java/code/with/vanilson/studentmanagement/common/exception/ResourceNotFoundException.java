package code.with.vanilson.studentmanagement.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ResourceNotFoundException
 *
 * @author vamuhong
 * @version 1.0
 * @since 2024-07-05
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Entity with ID NOT FOUND")
public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}