package EffectiveMobile.Test.Exceptions;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCardDataException.class)
    public ResponseEntity<Map<String,String>> handleInvalidCard(InvalidCardDataException ex) {
        return ResponseEntity
                .badRequest()
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<Map<String,String>> handleDateParse(DateTimeParseException ex) {
        return ResponseEntity
                .badRequest()
                .body(Map.of("error", "Invalid date format, expected yyyy-MM-dd"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleDtoErrors(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleBadEnum(HttpMessageNotReadableException ex) {
        String raw = ex.getMostSpecificCause().getMessage();
        String message = "Invalid request body: " +
                (raw.contains("CardStatus")
                        ? "Wrong value for status, expected one of [ACTIVE, BLOCKED, EXPIRED]"
                        : "Malformed JSON");
        return ResponseEntity
                .badRequest()
                .body(Map.of("error", message));
    }
}
