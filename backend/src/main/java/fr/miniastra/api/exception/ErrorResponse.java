package fr.miniastra.api.exception;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        int status,
        String error,
        String message,
        List<FieldError> details,
        Instant timestamp
) {
    public record FieldError(String field, String code, String message) {}

    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(status, error, message, List.of(), Instant.now());
    }

    public static ErrorResponse of(int status, String error, String message, List<FieldError> details) {
        return new ErrorResponse(status, error, message, details, Instant.now());
    }
}
