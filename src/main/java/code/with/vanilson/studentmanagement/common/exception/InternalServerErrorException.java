package code.with.vanilson.studentmanagement.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Internal Server Error")
public class InternalServerErrorException extends BaseException {
    public InternalServerErrorException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
