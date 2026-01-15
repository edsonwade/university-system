package code.with.vanilson.studentmanagement.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ResourceAlreadyExistsException
 *
 * @author vamuhong
 * @version 1.0
 * @since 2024-07-05
 */
@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Resource already exists")
public class ResourceAlreadyExistsException extends BaseException {
    public ResourceAlreadyExistsException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}