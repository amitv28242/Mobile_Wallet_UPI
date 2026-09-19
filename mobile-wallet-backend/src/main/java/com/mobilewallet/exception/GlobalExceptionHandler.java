// FILE: src/main/java/com/mobilewallet/exception/GlobalExceptionHandler.java
package com.mobilewallet.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), "RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiError> handleInsufficientBalanceException(InsufficientBalanceException ex) {
        log.error("Insufficient balance: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), "INSUFFICIENT_BALANCE", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicateResourceException(DuplicateResourceException ex) {
        log.error("Duplicate resource: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), "DUPLICATE_RESOURCE", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        log.error("Invalid credentials: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), "INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidOTPException.class)
    public ResponseEntity<ApiError> handleInvalidOTPException(InvalidOTPException ex) {
        log.error("Invalid OTP: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), "INVALID_OTP", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidQRException.class)
    public ResponseEntity<ApiError> handleInvalidQRException(InvalidQRException ex) {
        log.error("Invalid QR: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), "INVALID_QR", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicatePaymentException.class)
    public ResponseEntity<ApiError> handleDuplicatePaymentException(DuplicatePaymentException ex) {
        log.error("Duplicate payment: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), "DUPLICATE_PAYMENT", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ApiError> handleAccountLockedException(AccountLockedException ex) {
        log.error("Account locked: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), "ACCOUNT_LOCKED", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiError> handleInvalidTokenException(InvalidTokenException ex) {
        log.error("Invalid token: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), "INVALID_TOKEN", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiError> handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
        log.error("Unauthorized access: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), "UNAUTHORIZED_ACCESS", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(WalletException.class)
    public ResponseEntity<ApiError> handleWalletException(WalletException ex) {
        log.error("Wallet error: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), "WALLET_ERROR", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<ApiError> handlePaymentProcessingException(PaymentProcessingException ex) {
        log.error("Payment processing error: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), "PAYMENT_FAILED", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(GuestLoginDisabledException.class)
    public ResponseEntity<ApiError> handleGuestLoginDisabledException(GuestLoginDisabledException ex) {
        log.error("Guest login disabled: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), "GUEST_LOGIN_DISABLED", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("Access denied: {}", ex.getMessage());
        return buildErrorResponse("Access denied", "ACCESS_DENIED", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException(BadCredentialsException ex) {
        log.error("Bad credentials: {}", ex.getMessage());
        return buildErrorResponse("Invalid username or password", "INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        log.error("User not found: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), "USER_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        String errorMessage = errors.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining(", "));
        
        log.error("Validation error: {}", errorMessage);
        return buildErrorResponseWithDetails(errorMessage, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        
        log.error("Constraint violation: {}", errorMessage);
        return buildErrorResponse(errorMessage, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.error("Type mismatch: {}", ex.getMessage());
        return buildErrorResponse("Invalid parameter type: " + ex.getName(), "INVALID_PARAMETER", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("Malformed JSON: {}", ex.getMessage());
        return buildErrorResponse("Malformed JSON request", "MALFORMED_REQUEST", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return buildErrorResponse("An unexpected error occurred", "INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiError> buildErrorResponse(String message, String code, HttpStatus status) {
        ApiError error = ApiError.builder()
                .success(false)
                .message(message)
                .code(code)
                .status(status.value())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, status);
    }

    private ResponseEntity<ApiError> buildErrorResponseWithDetails(String message, String code, 
                                                                   HttpStatus status, Map<String, String> errors) {
        ApiError error = ApiError.builder()
                .success(false)
                .message(message)
                .code(code)
                .status(status.value())
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .build();
        return new ResponseEntity<>(error, status);
    }
}