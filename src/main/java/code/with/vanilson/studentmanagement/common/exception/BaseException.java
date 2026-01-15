package code.with.vanilson.studentmanagement.common.exception;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {
    private final Object[] args;

    public BaseException(String messageKey, Object... args) {
        super(messageKey);
        this.args = args;
    }
}
