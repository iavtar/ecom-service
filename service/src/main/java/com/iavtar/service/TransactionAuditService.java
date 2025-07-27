package com.iavtar.service;

import com.iavtar.domain.context.TransactionContext;
import com.iavtar.domain.entity.User;
import com.iavtar.infrastructure.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for transaction audit and business logic using transaction IDs
 */
@Service
public class TransactionAuditService {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionAuditService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Get audit trail for a specific transaction ID
     */
    public List<User> getAuditTrailByTransactionId(String transactionId) {
        logger.info("Retrieving audit trail for transaction ID: {}", transactionId);
        
        List<User> users = userRepository.findAll().stream()
                .filter(user -> transactionId.equals(user.getTransactionId()))
                .collect(Collectors.toList());
        
        logger.info("Found {} records for transaction ID: {}", users.size(), transactionId);
        return users;
    }
    
    /**
     * Get all transactions for a specific date range
     */
    public Map<String, List<User>> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        String currentTransactionId = TransactionContext.getTransactionId();
        logger.info("Retrieving transactions from {} to {} with current transaction ID: {}", 
                   startDate, endDate, currentTransactionId);
        
        Map<String, List<User>> transactionsByDate = userRepository.findAll().stream()
                .filter(user -> user.getCreatedAt() != null)
                .filter(user -> !user.getCreatedAt().isBefore(startDate) && !user.getCreatedAt().isAfter(endDate))
                .collect(Collectors.groupingBy(user -> user.getTransactionId()));
        
        logger.info("Found {} unique transactions in date range with current transaction ID: {}", 
                   transactionsByDate.size(), currentTransactionId);
        
        return transactionsByDate;
    }
    
    /**
     * Get transaction statistics
     */
    public Map<String, Object> getTransactionStatistics() {
        String currentTransactionId = TransactionContext.getTransactionId();
        logger.info("Calculating transaction statistics with current transaction ID: {}", currentTransactionId);
        
        List<User> allUsers = userRepository.findAll();
        
        Map<String, Object> statistics = Map.of(
            "totalUsers", allUsers.size(),
            "usersWithTransactionId", allUsers.stream().filter(u -> u.getTransactionId() != null).count(),
            "uniqueTransactionIds", allUsers.stream().map(User::getTransactionId).distinct().count(),
            "currentTransactionId", currentTransactionId,
            "calculationTimestamp", LocalDateTime.now()
        );
        
        logger.info("Transaction statistics calculated with current transaction ID: {}", currentTransactionId);
        return statistics;
    }
    
    /**
     * Validate transaction ID format
     */
    public boolean validateTransactionId(String transactionId) {
        logger.debug("Validating transaction ID: {} with current transaction ID: {}", 
                    transactionId, TransactionContext.getTransactionId());
        
        if (transactionId == null || transactionId.trim().isEmpty()) {
            return false;
        }
        
        // Basic format validation: PREFIX-YYYYMMDDHHMMSS-XXXXX
        boolean isValid = transactionId.matches("^[A-Z]+-\\d{14}-\\d{5}$");
        
        logger.debug("Transaction ID validation result: {} for transaction ID: {}", isValid, transactionId);
        return isValid;
    }
    
    /**
     * Get recent transactions (last N transactions)
     */
    public List<User> getRecentTransactions(int limit) {
        String currentTransactionId = TransactionContext.getTransactionId();
        logger.info("Retrieving last {} transactions with current transaction ID: {}", limit, currentTransactionId);
        
        List<User> recentUsers = userRepository.findAll().stream()
                .filter(user -> user.getTransactionId() != null)
                .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
                .limit(limit)
                .collect(Collectors.toList());
        
        logger.info("Retrieved {} recent transactions with current transaction ID: {}", 
                   recentUsers.size(), currentTransactionId);
        
        return recentUsers;
    }
} 