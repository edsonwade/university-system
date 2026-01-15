package code.with.vanilson.studentmanagement.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ResourceBadRequestException
 *
 * @author vamuhong
 * @version 1.0
 * @since 2024-07-05
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Bad request")
public class ResourceBadRequestException extends BaseException {
    public ResourceBadRequestException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}