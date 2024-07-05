package code.with.vanilson.studentmanagement.common.constants;

/**
 * ErrorCode
 *
 * @author vamuhong
 * @version 1.0
 * @since 2024-07-05
 */
public class ErrorCode {
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String BAD_REQUEST = "BAD_REQUEST";
    public static final String CONFLICT = "CONFLICT";
    public static final String UNPROCESSABLE_ENTITY = "UNPROCESSABLE_ENTITY";
    public static final String SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE";
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    public static final String UNEXPECTED_ERROR = "An unexpected error occurred";

    private ErrorCode() {
        // Private constructor to prevent instantiation
    }
}