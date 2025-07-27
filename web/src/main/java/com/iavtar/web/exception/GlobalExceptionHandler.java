package com.iavtar.web.exception;

import com.iavtar.domain.context.TransactionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler that includes transaction IDs in error responses
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex, WebRequest request) {
        String transactionId = TransactionContext.getTransactionId();
        
        logger.error("Runtime exception occurred with transaction ID: {} - Error: {}", transactionId, ex.getMessage(), ex);
        
        Map<String, Object> errorResponse = createErrorResponse(
            "Runtime Exception",
            ex.getMessage(),
            transactionId,
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, WebRequest request) {
        String transactionId = TransactionContext.getTransactionId();
        
        logger.error("Generic exception occurred with transaction ID: {} - Error: {}", transactionId, ex.getMessage(), ex);
        
        Map<String, Object> errorResponse = createErrorResponse(
            "Internal Server Error",
            "An unexpected error occurred",
            transactionId,
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        String transactionId = TransactionContext.getTransactionId();
        
        logger.warn("Illegal argument exception with transaction ID: {} - Error: {}", transactionId, ex.getMessage());
        
        Map<String, Object> errorResponse = createErrorResponse(
            "Bad Request",
            ex.getMessage(),
            transactionId,
            HttpStatus.BAD_REQUEST.value()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    private Map<String, Object> createErrorResponse(String error, String message, String transactionId, int status) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", status);
        response.put("error", error);
        response.put("message", message);
        response.put("transactionId", transactionId);
        response.put("path", "/api/users"); // You can make this dynamic based on the request
        
        return response;
    }
} 