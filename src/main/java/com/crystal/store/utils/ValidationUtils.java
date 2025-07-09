package com.crystal.store.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.crystal.store.model.ResponseModel;

public class ValidationUtils {

    /**
     * Handles validation exceptions and returns a formatted error response
     * 
     * @param ex The validation exception
     * @return ResponseModel with validation error details
     */
    public static ResponseModel handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        String errorMessage = fieldErrors.stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return new ResponseModel(HttpStatus.BAD_REQUEST, "Validation failed: " + errorMessage, null,
                HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Validates email format using regex pattern
     * 
     * @param email The email string to validate
     * @return true if email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        // Basic email validation regex
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    public static ValidationResult validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return new ValidationResult(false, "Password is required");
        }
        if (password.length() < 6) {
            return new ValidationResult(false, "Password must be at least 6 characters long");
        }
        return new ValidationResult(true, "Password is valid");
    }

    public static class ValidationResult {
        private final boolean isValid;
        private final String errors;

        public ValidationResult(boolean isValid, String errors) {
            this.isValid = isValid;
            this.errors = errors;
        }

        public boolean isValid() {
            return isValid;
        }

        public String getErrors() {
            return errors;
        }
    }
}