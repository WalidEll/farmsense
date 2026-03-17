package ma.farmsense.exception;

import org.springframework.http.HttpStatus;

public class AppException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public AppException(String message, HttpStatus status) {
        this(message, status, null);
    }

    public AppException(String message, HttpStatus status, String code) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() { return status; }
    public String getCode() { return code; }

    // ── Factories ──────────────────────────────────────────────

    public static AppException notFound(String msg)    { return new AppException(msg, HttpStatus.NOT_FOUND); }
    public static AppException forbidden(String msg)   { return new AppException(msg, HttpStatus.FORBIDDEN); }
    public static AppException conflict(String msg)    { return new AppException(msg, HttpStatus.CONFLICT); }
    public static AppException conflict(String code, String msg) { return new AppException(msg, HttpStatus.CONFLICT, code); }
    public static AppException badRequest(String msg)  { return new AppException(msg, HttpStatus.BAD_REQUEST); }
    public static AppException unprocessableEntity(String msg) { return new AppException(msg, HttpStatus.UNPROCESSABLE_ENTITY); }
    public static AppException unprocessableEntity(String code, String msg) { return new AppException(msg, HttpStatus.UNPROCESSABLE_ENTITY, code); }
    public static AppException unauthorized(String msg){ return new AppException(msg, HttpStatus.UNAUTHORIZED); }
    public static AppException internalError(String msg){ return new AppException(msg, HttpStatus.INTERNAL_SERVER_ERROR); }
}
