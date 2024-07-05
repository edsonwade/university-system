package code.with.vanilson.studentmanagement.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * DatabaseException
 *
 * @author vamuhong
 * @version 1.0
 * @since 2024-07-05
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Database error")
public class DatabaseException extends RuntimeException {
    public DatabaseException(String message) {
        super(message);
    }
}