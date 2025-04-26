package kdaniel.customers.controller;

import kdaniel.customers.util.FieldValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Kiszel Dániel
 * @Date 2025-04-26
 * @Description Global exception handler to manage custom and validation exceptions.
 * Handles FieldValidationException and MethodArgumentNotValidException across the application.
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @ExceptionHandler(FieldValidationException.class)
     * @Description Handles custom FieldValidationException, returning the validation errors in the response.
     */
    @ExceptionHandler(FieldValidationException.class)
    public ResponseEntity<Map<String, String>> handleFieldValidation(FieldValidationException ex) {
        return ResponseEntity.badRequest().body(ex.getErrors());
    }

    /**
     * @ExceptionHandler(MethodArgumentNotValidException.class)
     * @Description Handles validation exceptions for invalid method arguments. Returns detailed error messages.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }
}