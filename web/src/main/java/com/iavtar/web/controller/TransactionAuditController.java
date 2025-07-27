package com.iavtar.web.controller;

import com.iavtar.domain.context.TransactionContext;
import com.iavtar.domain.entity.User;
import com.iavtar.service.TransactionAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controller for transaction audit and monitoring endpoints
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionAuditController {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionAuditController.class);
    
    @Autowired
    private TransactionAuditService transactionAuditService;
    
    /**
     * Get audit trail for a specific transaction ID
     */
    @GetMapping("/audit/{transactionId}")
    public ResponseEntity<List<User>> getAuditTrail(@PathVariable String transactionId) {
        String currentTransactionId = TransactionContext.getTransactionId();
        logger.info("Received audit trail request for transaction ID: {} with current transaction ID: {}", 
                   transactionId, currentTransactionId);
        
        List<User> auditTrail = transactionAuditService.getAuditTrailByTransactionId(transactionId);
        
        logger.info("Returning audit trail with {} records for transaction ID: {} with current transaction ID: {}", 
                   auditTrail.size(), transactionId, currentTransactionId);
        
        return ResponseEntity.ok(auditTrail);
    }
    
    /**
     * Get transactions by date range
     */
    @GetMapping("/by-date-range")
    public ResponseEntity<Map<String, List<User>>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        String currentTransactionId = TransactionContext.getTransactionId();
        logger.info("Received transactions by date range request from {} to {} with current transaction ID: {}", 
                   startDate, endDate, currentTransactionId);
        
        Map<String, List<User>> transactions = transactionAuditService.getTransactionsByDateRange(startDate, endDate);
        
        logger.info("Returning {} unique transactions for date range with current transaction ID: {}", 
                   transactions.size(), currentTransactionId);
        
        return ResponseEntity.ok(transactions);
    }
    
    /**
     * Get transaction statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getTransactionStatistics() {
        String currentTransactionId = TransactionContext.getTransactionId();
        logger.info("Received transaction statistics request with current transaction ID: {}", currentTransactionId);
        
        Map<String, Object> statistics = transactionAuditService.getTransactionStatistics();
        
        logger.info("Returning transaction statistics with current transaction ID: {}", currentTransactionId);
        
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * Validate transaction ID format
     */
    @GetMapping("/validate/{transactionId}")
    public ResponseEntity<Map<String, Object>> validateTransactionId(@PathVariable String transactionId) {
        String currentTransactionId = TransactionContext.getTransactionId();
        logger.info("Received transaction ID validation request for: {} with current transaction ID: {}", 
                   transactionId, currentTransactionId);
        
        boolean isValid = transactionAuditService.validateTransactionId(transactionId);
        
        Map<String, Object> response = Map.of(
            "transactionId", transactionId,
            "isValid", isValid,
            "currentTransactionId", currentTransactionId
        );
        
        logger.info("Transaction ID validation result: {} for transaction ID: {} with current transaction ID: {}", 
                   isValid, transactionId, currentTransactionId);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get recent transactions
     */
    @GetMapping("/recent")
    public ResponseEntity<List<User>> getRecentTransactions(
            @RequestParam(defaultValue = "10") int limit) {
        
        String currentTransactionId = TransactionContext.getTransactionId();
        logger.info("Received recent transactions request with limit: {} and current transaction ID: {}", 
                   limit, currentTransactionId);
        
        List<User> recentTransactions = transactionAuditService.getRecentTransactions(limit);
        
        logger.info("Returning {} recent transactions with current transaction ID: {}", 
                   recentTransactions.size(), currentTransactionId);
        
        return ResponseEntity.ok(recentTransactions);
    }
    
    /**
     * Get current transaction ID
     */
    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentTransactionId() {
        String currentTransactionId = TransactionContext.getTransactionId();
        logger.info("Received current transaction ID request: {}", currentTransactionId);
        
        Map<String, Object> response = Map.of(
            "currentTransactionId", currentTransactionId,
            "timestamp", LocalDateTime.now()
        );
        
        return ResponseEntity.ok(response);
    }
} 