package com.iavtar.web.controller;

import com.iavtar.domain.context.TransactionContext;
import com.iavtar.security.dto.AuthRequest;
import com.iavtar.security.dto.AuthResponse;
import com.iavtar.security.service.AuthenticationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthenticationService authenticationService;
    
    /**
     * User login endpoint
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Received login request for user: {} with transaction ID: {}", authRequest.getUsername(), transactionId);
        
        AuthResponse response = authenticationService.authenticate(authRequest);
        logger.info("Login successful for user: {} with transaction ID: {}", authRequest.getUsername(), transactionId);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * User registration endpoint
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRequest authRequest) {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Received registration request for user: {} with transaction ID: {}", authRequest.getUsername(), transactionId);
        
        AuthResponse response = authenticationService.register(authRequest);
        logger.info("Registration successful for user: {} with transaction ID: {}", authRequest.getUsername(), transactionId);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Token refresh endpoint
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody Map<String, String> request) {
        String transactionId = TransactionContext.getTransactionId();
        String refreshToken = request.get("refreshToken");
        
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            logger.warn("Refresh token is missing with transaction ID: {}", transactionId);
            return ResponseEntity.badRequest().build();
        }
        
        logger.info("Received token refresh request with transaction ID: {}", transactionId);
        
        AuthResponse response = authenticationService.refreshToken(refreshToken);
        logger.info("Token refresh successful with transaction ID: {}", transactionId);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Health check endpoint for authentication service
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        String transactionId = TransactionContext.getTransactionId();
        logger.info("Received auth health check request with transaction ID: {}", transactionId);
        
        Map<String, Object> response = Map.of(
            "status", "UP",
            "service", "authentication",
            "transactionId", transactionId,
            "timestamp", java.time.LocalDateTime.now()
        );
        
        return ResponseEntity.ok(response);
    }
} 