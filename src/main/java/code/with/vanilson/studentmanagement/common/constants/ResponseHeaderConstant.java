package code.with.vanilson.studentmanagement.common.constants;

/**
 * ResponseHeaderConstant
 *
 * @author vamuhong
 * @version 1.0
 * @since 2024-07-05
 */
public class ResponseHeaderConstant {
    // Common response header field names
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String ACCEPT = "Accept";
    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String LOCATION = "Location";
    public static final String ETAG = "ETag";
    public static final String SERVER = "Server";
    public static final String DATE = "Date";
    public static final String EXPIRES = "Expires";
    public static final String CONTENT_DISPOSITION = "Content-Disposition";

    // Content type values
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_XML = "application/xml";
    public static final String CONTENT_TYPE_PDF = "application/pdf";
    public static final String CONTENT_TYPE_CSV = "text/csv";
    public static final String CONTENT_TYPE_HTML = "text/html";

    private ResponseHeaderConstant() {
        // Private constructor to prevent instantiation

    }
}